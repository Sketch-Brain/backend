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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
@PropertySource("classpath:bootstrap.yaml")
public class ContainerImpl implements Container{

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
    public TokenDto writeDB(byte[] experimentId, String userId, String dataName, String modelName) {
        ContainerEntity entity = this.infraStructure.writeSource(experimentId, userId, dataName, modelName);
        return new TokenDto(entity.getX_TOKEN(),entity.getTOKEN());
    }

    @Override
    public Deployment constructK8sContainer(String userId, String datasetName, String namespace, String tag, String imageName, String X_TOKEN, String TOKEN) {
        Deployment deployment = this.infraStructure.constructDeploy(userId, datasetName, namespace, imageName, tag, X_TOKEN, TOKEN);
        return deployment;
    }

    @Override
    public Service constructK8sService(String namespace, String TOKEN) {
        Service service = this.infraStructure.constructService(namespace, TOKEN);
        return service;
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
            }
        }
        //여기까지 오면 Exception 발생.
        throw new ContainerExceptions(ContainerErrorCodeImpl.CONTAINER_SERVICE_ERROR);
    }

    @Override
    public Boolean injectRunnableSource(String runnable, String svcName, String X_TOKEN, String TOKEN) {
        //Token 이 없으면 통신 불가능.
        String serviceName = svcName +"?token="+TOKEN;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type","application/json");
        headers.add("x-token",X_TOKEN);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("runnable",runnable); //Body 값으로, POST 요청으로 전달.
        return this.infraStructure.postRunnableSource(headers, params, serviceName);

    }
}
