package com.sketch.brain.backend.aggregate.manager.dao;

import com.sketch.brain.backend.aggregate.manager.entity.ContainerEntity;
import org.bson.types.ObjectId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContainerRepository extends JpaRepository<ContainerEntity, ObjectId> {
}
