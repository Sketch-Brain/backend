package com.sketch.brain.backend.aggregate.trainer.dao;

import com.sketch.brain.backend.aggregate.trainer.model.PythonDocumentModel;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PythonDocumentRepository extends MongoRepository<PythonDocumentModel, ObjectId> {

    List<PythonDocumentModel> findByUserId(String userId);
}
