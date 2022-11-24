package com.sketch.brain.backend.aggregate.manager.dao;

import com.sketch.brain.backend.aggregate.manager.entity.ContainerEntity;
import org.bson.types.ObjectId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface ContainerRepository extends JpaRepository<ContainerEntity, ObjectId> {

    @Query(value = "SELECT * FROM containers.container WHERE experiment_id = ?1 AND user_id = ?2",nativeQuery = true)
    ContainerEntity findByExperimentIdAndUserId(byte[] experimentId, String userId);

    @Query(value = "SELECT * FROM containers.container WHERE experiment_id = ?1",nativeQuery = true)
    ContainerEntity findByExperimentId(byte[] experimentId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE container SET status = ?2 WHERE experiment_id = ?1", nativeQuery = true)
    void updateStatusByExperimentId(byte[] experimentId, String status);

    @Modifying
    @Transactional
    @Query(value = "UPDATE container SET python_source = ?2 WHERE experiment_id = ?1", nativeQuery = true)
    void updatePythonSource(byte[] experimentId,String pythonSource);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM container WHERE experiment_id = ?1 ",nativeQuery = true)
    void deleteEntityByExperimentId(byte[] experimentId);
}
