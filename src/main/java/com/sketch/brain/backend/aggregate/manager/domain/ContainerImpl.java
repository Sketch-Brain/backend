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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
@PropertySource("classpath:bootstrap.yaml")
public class ContainerImpl implements Container{
    //FIXME 중복된 함수들 제거하고, 함수로 따로 교체.
    private final ContainerInfraStructure infraStructure;
    private final Environment environment;

    @Override
    public void run(Deployment deployment, Service service) {
        String namespace = environment.getProperty("sketch.brain.worker.NAME_SPACE");

        this.infraStructure.runDeployment(deployment,namespace);
        this.infraStructure.runService(service, namespace);
    }

    @Override
    public void getContainerInfo(String namespace) {
        this.infraStructure.getInfo(namespace);
    }

    @Override
    public LinkedHashMap<String, Object> startExperiment(String namespace, String X_TOKEN, String TOKEN) {
        String svcName = "http://training-container-svc-"+TOKEN.toLowerCase()+"."+namespace+".svc.cluster.local"+
                ":8888/trainer/worker/run";
        //Token을 바탕으로 Header, 추가.
        UriComponents urls = UriComponentsBuilder.fromHttpUrl(svcName+"?token="+TOKEN).build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type","application/json");
        headers.add("x-token",X_TOKEN);

        ConcurrentHashMap<String, Object> body = new ConcurrentHashMap<>();
        ResponseEntity<Object> result = this.infraStructure.sendRequest(urls,headers,body, HttpMethod.PATCH);

        //Exception Handling
        if (result == null) throw new ContainerExceptions(ContainerErrorCodeImpl.CONTAINER_SERVICE_ERROR);
        if (result.getStatusCode() != HttpStatus.OK){
            throw new ContainerExceptions(ContainerErrorCodeImpl.EXPERIMENT_START_FAILED);
        }else return (LinkedHashMap<String, Object>) result.getBody();
    }

    @Override
    public TokenDto writeDB(byte[] experimentId, String userId, String dataName, String modelName) {
        ContainerEntity entity = this.infraStructure.writeSource(experimentId, userId, dataName, modelName);
        return new TokenDto(entity.getX_TOKEN(),entity.getTOKEN());
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
    public Deployment constructK8sContainer(String userId, String datasetName, String namespace, String tag, String imageName, String X_TOKEN, String TOKEN) {
        return this.infraStructure.constructDeploy(userId, datasetName, namespace, imageName, tag, X_TOKEN, TOKEN);
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
    public Boolean isRestServerReady(String svcName,String X_TOKEN, String TOKEN) {
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
        throw new ContainerExceptions(ContainerErrorCodeImpl.CONTAINER_SERVICE_ERROR);

    }

    @Override
    public Boolean injectRunnableSource(String runnable, String svcName, String X_TOKEN, String TOKEN) {
        //Token 이 없으면 통신 불가능.
        UriComponents urls = UriComponentsBuilder.fromHttpUrl(svcName+"?token="+TOKEN).build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type","application/json");
        headers.add("x-token",X_TOKEN);

        ConcurrentHashMap<String, Object> body = new ConcurrentHashMap<>();
        body.put("runnable",runnable); //Body 값으로, POST 요청으로 전달.
        log.info(body.toString());
        //11/8 공용 Request Function sendRequest 로 함수 대체.
        ResponseEntity<Object> result = this.infraStructure.sendRequest(urls,headers,body,HttpMethod.POST);

        if (result == null) throw new ContainerExceptions(ContainerErrorCodeImpl.CONTAINER_SERVICE_ERROR);

        return result.getStatusCode() == HttpStatus.OK;
    }
}
