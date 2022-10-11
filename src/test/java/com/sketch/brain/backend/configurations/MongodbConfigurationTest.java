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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {LocalMongoConfigurations.class, MongodbConfigurations.class, PythonDocumentModel.class, PythonDocumentRepository.class})
@EnableConfigurationProperties
@ActiveProfiles("test")
//@DataMongoTest
public class MongodbConfigurationTest {
    @Autowired
    PythonDocumentRepository repository;

    PythonDocumentModel documentModel;
    LocalDateTime time;
    @BeforeEach
    void setUp(){
        //given
        this.time = LocalDateTime.now();
        this.documentModel = new PythonDocumentModel("TestUser","print(\"hello world!\")",
                this.time, this.time);
    }

    //[FIXME] TestSource 수정해야 함!
    @Test
    @DisplayName("Document 가 정상적으로 저장된다.")
    void insertDocumentTest(){
        //when
        this.repository.save(this.documentModel);

        //then
        PythonDocumentModel testModel = this.repository.findById(this.documentModel.getUserId()).get();
        assertThat(testModel.getUserId()).isEqualTo("TestUser");
        assertThat(testModel.getRunnable()).isEqualTo("print(\"hello world!\")");
        assertThat(testModel.getStartedAt()).isEqualTo(this.time);
    }
}
