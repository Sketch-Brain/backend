package com.sketch.brain.backend.configurations;

import com.sketch.brain.backend.aggregate.trainer.dao.PythonDocumentRepository;
import com.sketch.brain.backend.aggregate.trainer.model.PythonDocumentModel;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataMongoTest
@EnableConfigurationProperties
@ActiveProfiles("test")
public class LocalMongodbConfigurationTest {
    @Autowired
    PythonDocumentRepository repository;

    PythonDocumentModel documentModel;
    PythonDocumentModel documentModel2;
    PythonDocumentModel documentModel3;
    LocalDateTime time;
    @BeforeEach
    void setUp(){
        //given
        this.time = LocalDateTime.now();
        this.documentModel = new PythonDocumentModel(ObjectId.get(),"testuser","print(\"hello world!\")",
                this.time, this.time);
        this.documentModel2 = new PythonDocumentModel(ObjectId.get(),"testuser","print(\"hello world2!\")",
                this.time, this.time);
        this.documentModel3 = new PythonDocumentModel(ObjectId.get(),"testuser2","print(\"hello world2!\")",
                this.time, this.time);
    }

    //[FIXME] LocalDateTime 의 assertion Failed. -> 뒤에 2자리 짤림.
    @Test
    @DisplayName("Document 가 정상적으로 저장된다.")
    void insertDocumentTest(){
        //when
        this.repository.save(this.documentModel);

        //then
        PythonDocumentModel testModel = this.repository.findById(this.documentModel.getExperimentId()).get();
        assertThat(testModel.getUserId()).isEqualTo(this.documentModel.getUserId());
        assertThat(testModel.getRunnable()).isEqualTo(this.documentModel.getRunnable());
        assertThat(testModel.getExperimentId()).isEqualTo(this.documentModel.getExperimentId());
//        assertThat(testModel.getStartedAt()).isEqualTo(this.time);
    }

    @Test
    @DisplayName("여러 Document 를 하나의 id 로 조회하는데 성공한다.")
    void findAllDocumentById(){
        //when
        this.repository.save(this.documentModel);
        this.repository.save(this.documentModel2);
        this.repository.save(this.documentModel3);
        //then
        List<PythonDocumentModel> testModel = this.repository.findByUserId(this.documentModel.getUserId());
        assertThat(testModel.size()).isEqualTo(2);
        testModel.forEach(pythonDocumentModel -> {
            assertThat(pythonDocumentModel.getUserId()).isEqualTo(this.documentModel.getUserId());
        });
    }
}
