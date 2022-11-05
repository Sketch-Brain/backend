package com.sketch.brain.backend.aggregate.trainer.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sketch.brain.backend.aggregate.trainer.application.TrainerService;
import com.sketch.brain.backend.aggregate.trainer.dto.Runnable;
import com.sketch.brain.backend.aggregate.trainer.model.PythonDocumentModel;
import com.sketch.brain.backend.global.error.exceptions.ValidationErrorCodeImpl;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TrainerApiTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TrainerService trainerService;

    PythonDocumentModel documentModel;
    PythonDocumentModel documentModel2;
    PythonDocumentModel documentModel3;
    Runnable runnable;
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
        this.runnable = new Runnable(this.documentModel.getExperimentId().toString(),this.documentModel.getRunnable());
    }

    @Test
    @DisplayName("HATEOAS와 Controller 는 올바르게 작동한다.")
    void successIfProperParm() throws Exception {
        //given
        String userId = this.documentModel.getUserId();
        ConcurrentHashMap<String, Object> body = new ConcurrentHashMap<>();
        Map<String, Object> body2 = new HashMap<>();
        body2.put("kernelSize","(3,3)");
        body2.put("filters",16);
        body.put("Conv2D",body2);
        body.put("userId",userId);
        //when
        //Service Logic 은 여기서 검증하지 않는다.
        when(this.trainerService.saveRunnableSource(anyString(),any(Enumeration.class),any(Queue.class))).thenReturn(this.runnable);

        //then
        this.mockMvc.perform(post("/api/trainer/save/runnable").accept(MediaTypes.HAL_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links").isNotEmpty())
                .andExpect(jsonPath("runnable").value(this.documentModel.getRunnable()));
    }

    @Test
    @DisplayName("필요한 파라미터가 없거나, 잘못되면 Exception이 발생한다.")
    void failWhenParamIsEmpty() throws Exception {
        //given
        ConcurrentHashMap<String, Object> body = new ConcurrentHashMap<>();
        Map<String, Object> body2 = new HashMap<>();
        body2.put("kernelSize","(3,3)");
        body2.put("filters",16);
        body.put("Conv2D",body2);
        //when
        when(this.trainerService.saveRunnableSource(anyString(),any(Enumeration.class),any(Queue.class))).thenReturn(this.runnable);
        //then
        //UserID 가 없으면 Not Found.
        this.mockMvc.perform(post("/api/trainer/save/runnable")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value(ValidationErrorCodeImpl.REQUIRED_PARAM_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("errors").isNotEmpty());

        //특수문자가 들어가면 Forbidden
        body.put("userId","123#a -\n");
        this.mockMvc.perform(post("/api/trainer/save/runnable")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("message").value(ValidationErrorCodeImpl.SPECIAL_CHARACTER_FORBIDDEN.getMessage()))
                .andExpect(jsonPath("errors").isNotEmpty());
    }
}
