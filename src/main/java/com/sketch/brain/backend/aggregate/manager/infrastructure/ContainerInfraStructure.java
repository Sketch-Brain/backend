package com.sketch.brain.backend.aggregate.manager.infrastructure;

import com.sketch.brain.backend.aggregate.manager.dao.ContainerRepository;
import com.sketch.brain.backend.aggregate.manager.entity.ContainerEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ContainerInfraStructure {

    private final ContainerRepository containerRepository;


    public ContainerEntity writeSource(byte[] experimentId, String userId, String dataName, String modelName){
        ContainerEntity entity = new ContainerEntity();
        entity.setExperiment_id(experimentId);
        entity.setUser_id(userId);
        entity.setData_name(dataName);
        entity.setModel_name(modelName);
        return this.containerRepository.save(entity);
    }
}
