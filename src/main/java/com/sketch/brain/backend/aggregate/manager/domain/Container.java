package com.sketch.brain.backend.aggregate.manager.domain;

import com.sketch.brain.backend.aggregate.manager.dto.TokenDto;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import org.bson.types.ObjectId;

public interface Container {

    /**
     * 학습을 실행시킨다. 실제 Container 를 Running 한다.
     */
    void run(Deployment deployment,Service service);

    /**
     *
     */
    void getContainerInfo(String namespace);

    TokenDto writeDB(byte[] experimentId, String userId, String dataName, String modelName);

    Deployment constructK8sContainer(String namespace, String tag, String imageName, String X_TOKEN, String TOKEN);

    Service constructK8sService(String namespace, String TOKEN);
}
