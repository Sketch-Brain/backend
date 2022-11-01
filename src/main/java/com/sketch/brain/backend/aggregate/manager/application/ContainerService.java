package com.sketch.brain.backend.aggregate.manager.application;

import com.sketch.brain.backend.aggregate.manager.domain.ContainerImpl;
import com.sketch.brain.backend.aggregate.manager.dto.TokenDto;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContainerService {

    private final ContainerImpl container;

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
        //우선 Deployment 먼저 구성해야 함.
        Deployment deployment = this.container.constructK8sContainer(token);
        // 만들어진 deployment 를 바탕으로 Run.
        this.container.run(deployment);
    }
}
