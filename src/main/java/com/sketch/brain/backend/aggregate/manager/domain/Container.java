package com.sketch.brain.backend.aggregate.manager.domain;

import com.sketch.brain.backend.aggregate.manager.entity.ContainerEntity;
import org.bson.types.ObjectId;

public interface Container {

    /**
     * 학습을 실행시킨다. 실제 Container 를 Running 한다.
     */
    void run();

    /**
     *
     */
    void getContainerInfo();

    ContainerEntity writeDB(byte[] experimentId, String userId, String dataName, String modelName);
}
