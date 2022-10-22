package com.sketch.brain.backend.global.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sketch.brain.backend.global.error.exceptions.CommonErrorCodeImpl;
import com.sketch.brain.backend.global.error.exceptions.TrainingErrorCodeImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("만약 Method Type 이 다르면, 실패한다.")
    public void failIfUnSupportMethodCall() throws Exception {
        //given

        //when
        /**
         * Http Request Method 가 다른 경우 ErrorResponse 의 errors 가 emtpy Array 인지도 검증해야 함.
         */
        this.mockMvc.perform(post("/api/trainer/getInfo/2"))
                .andExpect(status().isMethodNotAllowed())
//                .andExpect(jsonPath("code").value(CommonErrorCodeImpl.METHOD_NOT_ALLOWED.getHttpStatus()))
                .andExpect(jsonPath("message").value(CommonErrorCodeImpl.METHOD_NOT_ALLOWED.getMessage()))
                .andExpect(jsonPath("errors").isEmpty())
                .andDo(print());
        //then
    }

    @Test
    @DisplayName("만약 Method Type 이 같으면, 성공한다.")
    public void passIfCorrectMethodCall() throws Exception {
        //given
        //when
        this.mockMvc.perform(get("/api/trainer/getInfo/1"))
                .andExpect(status().isOk());
        //then
    }

    @Test
    @DisplayName("만약 Training Layer 로 잘못된 Layer 를 보내면, 실패한다.")
    public void failIfGetUnknownTrainingLayer() throws Exception {
        //given
        //when
        /**
         * 학습할 수 없는 Layer 를 보내게 되면, BAD_REQUEST 를 호출.
         */
        Map<String, Object> body = new HashMap<>();
        Map<String, Object> body2 = new HashMap<>();
        body2.put("kernelSize","(3,3)");
        body2.put("filters",16);
        //Conv23D 는 학습할 수 없는 Layer 의 명칭임.
        body.put("Conv23D",body2);
        this.mockMvc.perform(post("/api/trainer/save/runnable")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value(TrainingErrorCodeImpl.UNKNOWN_LAYER_DETECTED.getMessage()));
        //then
    }
}
