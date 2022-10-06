package com.sketch.brain.backend.global.error;

import com.sketch.brain.backend.global.error.exceptions.CommonErrorCodeImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

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
                .andExpect(jsonPath("code").value(CommonErrorCodeImpl.METHOD_NOT_ALLOWED.getHttpStatus()))
                .andExpect(jsonPath("message").value(CommonErrorCodeImpl.METHOD_NOT_ALLOWED.getMessage()))
                .andExpect(jsonPath("errors").isEmpty())
                .andDo(print());
        //then
    }
}
