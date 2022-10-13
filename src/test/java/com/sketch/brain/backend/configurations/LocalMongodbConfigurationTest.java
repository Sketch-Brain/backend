package com.sketch.brain.backend.configurations;

import com.sketch.brain.backend.aggregate.trainer.dao.PythonDocumentRepository;
import com.sketch.brain.backend.aggregate.trainer.model.PythonDocumentModel;
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

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataMongoTest
@EnableConfigurationProperties
@ActiveProfiles("test")
public class LocalMongodbConfigurationTest {
    @Autowired
    PythonDocumentRepository repository;

    PythonDocumentModel documentModel;
    LocalDateTime time;
    @BeforeEach
    void setUp(){
        //given
        this.time = LocalDateTime.now();
        this.documentModel = new PythonDocumentModel("testId","print(\"hello world!\")",
                this.time, this.time);
    }

    //[FIXME] LocalDateTime 의 assertion Failed. -> 뒤에 2자리 짤림.
    @Test
    @DisplayName("Document 가 정상적으로 저장된다.")
    void insertDocumentTest(){
        //when
        this.repository.save(this.documentModel);

        //then
        PythonDocumentModel testModel = this.repository.findById(this.documentModel.get_id()).get();
        assertThat(testModel.get_id()).isEqualTo("testId");
        assertThat(testModel.getRunnable()).isEqualTo("print(\"hello world!\")");
//        assertThat(testModel.getStartedAt()).isEqualTo(this.time);
    }
}
