package com.sketch.brain.backend.aggregate.manager.domain;

import com.sketch.brain.backend.aggregate.manager.dto.TokenDto;
import com.sketch.brain.backend.aggregate.manager.entity.ContainerEntity;
import com.sketch.brain.backend.aggregate.manager.infrastructure.ContainerInfraStructure;
import com.sketch.brain.backend.global.error.exceptions.ContainerErrorCodeImpl;
import com.sketch.brain.backend.global.error.exceptions.ContainerExceptions;
import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
@PropertySource("classpath:application.yaml")
public class ContainerImpl implements Container{
    //FIXME 중복된 함수들 제거하고, 함수로 따로 교체.
    private final ContainerInfraStructure infraStructure;
    private final Environment environment;

    @Override
    public void run(String namespace,Deployment deployment, Service service) {

        this.infraStructure.runDeployment(deployment,namespace);
        this.infraStructure.runService(service, namespace);
    }

    @Override
    public void getContainerInfo(String namespace) {
        this.infraStructure.getInfo(namespace);
    }

    @Override
    public LinkedHashMap<String, Object> startExperiment(byte[] experimentId, String namespace, String X_TOKEN, String TOKEN, String userId) {

        //Status 가 Running 상태가 아니라면, 실행할 수 없는 상태이므로 중단.
        ContainerEntity entity = this.infraStructure.getEntityByExperimentId(experimentId, userId);
        if (!Objects.equals(entity.getStatus(), "Ready")) throw new ContainerExceptions(ContainerErrorCodeImpl.EXPERIMENT_IS_NOT_READY);

        String svcName = "http://training-container-svc-"+TOKEN.toLowerCase()+"."+namespace+".svc.cluster.local"+
                ":8888/trainer/worker/run";
        //Token을 바탕으로 Header, 추가.
        UriComponents urls = UriComponentsBuilder.fromHttpUrl(svcName+"?token="+TOKEN).build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type","application/json");
        headers.add("x-token",X_TOKEN);

        ConcurrentHashMap<String, Object> body = new ConcurrentHashMap<>();
        body.put("experimentId", new ObjectId(experimentId).toString());
        ResponseEntity<Object> result = this.infraStructure.sendRequest(urls,headers,body, HttpMethod.PATCH);

        //Exception Handling
        if (result == null) {
            updateStatus(experimentId, "Failed");
            throw new ContainerExceptions(ContainerErrorCodeImpl.CONTAINER_SERVICE_ERROR);
        }
        if (result.getStatusCode() != HttpStatus.OK){
            updateStatus(experimentId, "Failed");
            throw new ContainerExceptions(ContainerErrorCodeImpl.EXPERIMENT_START_FAILED);
        }else{
            updateStatus(experimentId, "Running");
            return (LinkedHashMap<String, Object>) result.getBody();
        }
    }

    @Override
    public TokenDto writeDB(byte[] experimentId, String userId, String dataName, String modelName) {
        ContainerEntity entity = this.infraStructure.writeSource(experimentId, userId, dataName, modelName);
        //11/13 추가 Result Table 에 Create 전달.
        String host = environment.getProperty("spring.data.mongodb.host");
        UriComponents urls = UriComponentsBuilder.fromHttpUrl("http://"+host+":32700/api/server/result").build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type","application/json");

        ConcurrentHashMap<String, Object> body = new ConcurrentHashMap<>();
        body.put("uuid", new ObjectId(experimentId).toString());
        body.put("user",userId);
        body.put("data_name",dataName);
        body.put("model_name", modelName);
        body.put("result","Experiments Not Finished");

        ResponseEntity<Object> response = this.infraStructure.sendRequest(urls, headers, body, HttpMethod.PUT);
        if (response.getStatusCode().equals(HttpStatus.OK)) return new TokenDto(entity.getX_TOKEN(),entity.getTOKEN());
        else throw new ContainerExceptions(ContainerErrorCodeImpl.EXPERIMENT_IS_NOT_READY);
    }

    /**
     * Experiment Id, And user ID 둘다 만족하는 Experiment 의 Token 을 Get.
     * @param experimentId UUID
     * @param userId userId
     * @return
     */
    @Override
    public TokenDto getExperimentTokens(byte[] experimentId, String userId) {
        ContainerEntity entity = this.infraStructure.getEntityByExperimentId(experimentId, userId);
        return new TokenDto(entity.getX_TOKEN(), entity.getTOKEN());
    }

    @Override
    public void deleteEntityByExperimentId(byte[] experimentId){//FIXME Logic 분리 필요.
        this.infraStructure.deleteContainerById(experimentId);
        String host = environment.getProperty("spring.data.mongodb.host");
        UriComponents urls = UriComponentsBuilder.fromHttpUrl("http://"+host+":32700/api/server/result").build();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type","application/json");

        ConcurrentHashMap<String, Object> body = new ConcurrentHashMap<>();
        body.put("uuid", new ObjectId(experimentId).toString());

        this.infraStructure.sendRequest(urls, headers, body, HttpMethod.DELETE);
    }

    @Override
    public String updateStatus(byte[] experimentId, String status) {//Update Status 함수를 실행시킬 때, Result 도 교체.
        ContainerEntity entity = this.infraStructure.updateStatus(experimentId, status);

        String host = environment.getProperty("spring.data.mongodb.host");
        UriComponents urls = UriComponentsBuilder.fromHttpUrl("http://"+host+":32700/api/server/result").build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type","application/json");

        ConcurrentHashMap<String, Object> body = new ConcurrentHashMap<>();
        body.put("uuid", new ObjectId(experimentId).toString());
        body.put("result",status);
        this.infraStructure.sendRequest(urls, headers, body, HttpMethod.PATCH);

        return entity.getStatus();
    }

    @Override
    public String updatePythonSource(byte[] experimentId, String status) {
        ContainerEntity entity = this.infraStructure.updatePythonSource(experimentId, status);
        return entity.getPython_source();
    }


    @Override
    public Deployment constructK8sContainer(String userId, String datasetName, String namespace, String tag, String imageName, String X_TOKEN, String TOKEN) {
        return this.infraStructure.constructDeploy(userId, datasetName, namespace, imageName, tag, X_TOKEN, TOKEN);
    }

    @Override
    public void deleteDeploymentsAndService(String namespace, String TOKEN) {
        //Kuberntes Resource 먼저 삭제.
        this.infraStructure.deleteDeployment(namespace, TOKEN);
        this.infraStructure.deleteService(namespace, TOKEN);
    }

    @Override
    public Service constructK8sService(String namespace, String TOKEN) {
        return this.infraStructure.constructService(namespace, TOKEN);
    }

    @Override
    public Boolean isContainerReady(String namespace, String TOKEN) {
        List<ContainerStatus> statusList = this.infraStructure.getPodStatus(namespace, TOKEN);
        for (ContainerStatus containerStatus : statusList) {
//            log.info(String.valueOf(containerStatus.getReady() && containerStatus.getStarted()));
            return containerStatus.getReady() && containerStatus.getStarted();
        }
        //If List isEmpty
        log.info("LIST IS EMPTY");
        //FIXME container Exception handling 추가!
        return false;
    }

    @Override
    public Boolean isRestServerReady(byte[] experimentId, String svcName,String X_TOKEN, String TOKEN) {
        //Token을 바탕으로 Header, 추가.
        UriComponents urls = UriComponentsBuilder.fromHttpUrl(svcName+"?token="+TOKEN).build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type","application/json");
        headers.add("x-token",X_TOKEN);
        //요청 Send 후, 결과 return.
        int retrys = 1;
        while(retrys <= 10){//10번까지 재시도.
            Boolean isReady = this.infraStructure.isReadyRestServer(urls, headers);
            if(isReady) return isReady;
            else{
                log.info("Health Check retry {} to {}",retrys,urls.toString());
                retrys++;
                try {
                    Thread.sleep(1000);//FOR TEST
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        //여기까지 오면 Exception 발생.
        this.infraStructure.updateStatus(experimentId,"Failed");
        throw new ContainerExceptions(ContainerErrorCodeImpl.CONTAINER_SERVICE_ERROR);

    }

    @Override
    public Boolean injectRunnableSource(byte[] experimentId, String runnable, String svcName, String X_TOKEN, String TOKEN) {
        //Token 이 없으면 통신 불가능.
        UriComponents urls = UriComponentsBuilder.fromHttpUrl(svcName+"?token="+TOKEN).build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type","application/json");
        headers.add("x-token",X_TOKEN);

        ConcurrentHashMap<String, Object> body = new ConcurrentHashMap<>();
        body.put("runnable",runnable); //Body 값으로, POST 요청으로 전달.
        body.put("experimentId", new ObjectId(experimentId).toString()); //String Value 로 Return.
        log.info(body.toString());
        //11/8 공용 Request Function sendRequest 로 함수 대체.
        ResponseEntity<Object> result = this.infraStructure.sendRequest(urls,headers,body,HttpMethod.POST);

        if (result == null) {
            log.error("Result is NULL");
            throw new ContainerExceptions(ContainerErrorCodeImpl.CONTAINER_SERVICE_ERROR);
        }
        log.info("Results in inject Runnable : {}, {}",result.getStatusCode(),result.getBody().toString());
        return result.getStatusCode() == HttpStatus.OK;
    }
}
