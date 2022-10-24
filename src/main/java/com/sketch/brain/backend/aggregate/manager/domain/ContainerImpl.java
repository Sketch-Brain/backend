package com.sketch.brain.backend.aggregate.manager.domain;

import com.sketch.brain.backend.aggregate.manager.entity.ContainerEntity;
import com.sketch.brain.backend.aggregate.manager.infrastructure.ContainerInfraStructure;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContainerImpl implements Container{

    private final ContainerInfraStructure infraStructure;

    @Override
    public void run() {

    }

    @Override
    public void getContainerInfo() {

    }

    @Override
    public ContainerEntity writeDB(byte[] experimentId, String userId, String dataName, String modelName) {
        return this.infraStructure.writeSource(experimentId, userId, dataName, modelName);
    }
}
