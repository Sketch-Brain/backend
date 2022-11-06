package com.sketch.brain.backend.aggregate.manager.application;

import com.sketch.brain.backend.aggregate.manager.domain.ContainerImpl;
import com.sketch.brain.backend.aggregate.manager.dto.TokenDto;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

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

    public void runContainer(TokenDto token){
        // 값들을 파싱해서 전달.
        String namespace = environment.getProperty("sketch.brain.worker.NAME_SPACE");
        String tag = environment.getProperty("sketch.brain.worker.IMAGE_TAG");
        String imageName = environment.getProperty("sketch.brain.worker.IMAGE_NAME");
        String X_TOKEN = token.getX_TOKEN();
        String TOKEN = token.getTOKEN();

        //우선 Deployment 먼저 구성해야 함.
        Deployment deployment = this.container.constructK8sContainer(namespace, tag, imageName, X_TOKEN, TOKEN);
        //Service 구성.
        io.fabric8.kubernetes.api.model.Service service = this.container.constructK8sService(namespace, TOKEN);
        // 만들어진 k8s resource 들을 실제로 실행.
        this.container.run(deployment, service);
    }
}
