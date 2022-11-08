package com.sketch.brain.backend.aggregate.manager.dao;

import com.sketch.brain.backend.aggregate.manager.entity.ContainerEntity;
import org.bson.types.ObjectId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ContainerRepository extends JpaRepository<ContainerEntity, ObjectId> {

    @Query(value = "SELECT * FROM containers.container WHERE experiment_id = ?1 AND user_id = ?2",nativeQuery = true)
    ContainerEntity findByExperimentId(byte[] experimentId, String userId);

}
