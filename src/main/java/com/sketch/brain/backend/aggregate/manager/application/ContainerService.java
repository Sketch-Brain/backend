package com.sketch.brain.backend.aggregate.manager.application;

import com.sketch.brain.backend.aggregate.manager.domain.ContainerImpl;
import com.sketch.brain.backend.aggregate.manager.dto.TokenDto;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Slf4j
@Service
@RequiredArgsConstructor
@PropertySource("classpath:bootstrap.yaml")
public class ContainerService {

    private final ContainerImpl container;
    private final Environment environment;

    /**
     * MySQL DB 에 실행시킬 Container 의 정보를 Write 한다.
     * @param experimentId 실험 ID ( ObjectId )
     * @param userId 실험 user ID ( String )
     * @param dataName 실험 dataset Name ( String )
     * @param modelName 실험 model ( String )
     * @return ContainerEntity
     */
    public TokenDto writeSource(byte[] experimentId, String userId, String dataName, String modelName){
        return this.container.writeDB(experimentId, userId, dataName, modelName);
    }

    public void getPodLists(String namespace){
        this.container.getContainerInfo(namespace);
    }

    /**
     * 학습을 시작한다. Create 할 때, Source 가 이미 저장되어 있으므로, 그 다음부터 생각하면 된다.
     * @param experimentId ObjectId
     * @param userId userId
     */
    public MultiValueMap<String, Object> startExperiment(byte[] experimentId, String userId){
        String namespace = this.environment.getProperty("sketch.brain.worker.NAME_SPACE");
        TokenDto token = this.container.getExperimentTokens(experimentId, userId);
        log.info(token.getTOKEN());
        return this.container.startExperiment(namespace,token.getX_TOKEN(),token.getTOKEN());
    }

    /**
     * Container 가 준비되었는지 확인한다. FastAPI 가 잘 작동했고, HealthCheck 도 Pass 한 경우.
     * @param X_TOKEN : X_TOKEN( FastAPI에 필요한 토큰값. )
     * @param TOKEN : TOKEN VALUE ( FastAPI + SVC, Deploy 이름 규칙 )
     */
    public Boolean isContainerReady(String X_TOKEN, String TOKEN){
        String namespace = this.environment.getProperty("sketch.brain.worker.NAME_SPACE");
        //우선 Kubernetes Pod 상태를 먼저 점검한다.
        Boolean isReady = this.container.isContainerReady(namespace, TOKEN);
        // Pod 가 준비되지 않았다면, False 를 return.
        if (!isReady) return false;
        //HealthCheck URL을 확인하기.
        //FIXME URL Construct maybe domain logic?
        String svcName = "http://training-container-svc-"+TOKEN.toLowerCase()+"."+namespace+".svc.cluster.local"+
                ":8888/trainer/worker/health";
        //결과 Return
        return this.container.isRestServerReady(svcName, X_TOKEN, TOKEN);
    }

    public void runContainer(String userId, String datasetName, TokenDto token){
        // 값들을 파싱해서 전달.
        String namespace = environment.getProperty("sketch.brain.worker.NAME_SPACE");
        String tag = environment.getProperty("sketch.brain.worker.IMAGE_TAG");
        String imageName = environment.getProperty("sketch.brain.worker.IMAGE_NAME");
        String X_TOKEN = token.getX_TOKEN();
        String TOKEN = token.getTOKEN();

        //우선 Deployment 먼저 구성해야 함.
        Deployment deployment = this.container.constructK8sContainer(userId, datasetName, namespace, tag, imageName, X_TOKEN, TOKEN);
        //Service 구성.
        io.fabric8.kubernetes.api.model.Service service = this.container.constructK8sService(namespace, TOKEN);
        // 만들어진 k8s resource 들을 실제로 실행.
        this.container.run(deployment, service);
    }

    /**
     * Training Container 로 runnable Source 를 주입.
     * @param runnable : Python Runnable Source
     * @param X_TOKEN
     * @param TOKEN
     * @return Boolean Success 여부.
     */
    public Boolean injectRunnable(String runnable,String X_TOKEN, String TOKEN){
        String namespace = environment.getProperty("sketch.brain.worker.NAME_SPACE");
        String svcName = "http://training-container-svc-"+TOKEN.toLowerCase()+"."+namespace+".svc.cluster.local"+
                ":8888/trainer/worker/insertRunnable";
        return this.container.injectRunnableSource(runnable,svcName,X_TOKEN, TOKEN);
    }
}
