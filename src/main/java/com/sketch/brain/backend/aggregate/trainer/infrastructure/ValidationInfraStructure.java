package com.sketch.brain.backend.aggregate.trainer.infrastructure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sketch.brain.backend.aggregate.trainer.dto.LayerEnum;
import com.sketch.brain.backend.global.error.ArgumentError;
import com.sketch.brain.backend.global.error.exceptions.ValidationErrorCodeImpl;
import com.sketch.brain.backend.global.error.exceptions.ValidationExceptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class ValidationInfraStructure {
    private final ObjectMapper objectMapper;

    public boolean isConvertable(String layerKey, ConcurrentHashMap<String, Object> body){
        try {
            LayerEnum layerEnum = LayerEnum.find(layerKey);
            log.info("key : {}",layerEnum.getKey());
            //WildCard 를 사용해서 Class Convert 진행.
            Class<?> layerClass = layerEnum.getLayersClass();

            this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            //알 수 없는 Properties 에 대해서 Failed 한다.
            this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,true);
            this.objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES,true);
            this.objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES,true);
            this.objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,true);

            String values = this.objectMapper.writeValueAsString(body.get(layerKey));
            //여기에서 Exception 이 발생하지 않으면, 넘어가면 된다.
            Object obj = this.objectMapper.readValue(values, layerClass);
            Method checkIsValid = layerClass.getDeclaredMethod("mustNeedMetrics");
            //Layer 마다 가지고 있는 mustNeedMetrics 함수를 통해서, Jackson 으로만 잡을 수 없는 에러들을 잡는다.
            List<ArgumentError> errors = (List<ArgumentError>) checkIsValid.invoke(obj);
            //예외 발생하면, Exception 처리.
            //FIXME Unit Test Source 작성하기.
            if(!errors.isEmpty()) throw new ValidationExceptions(ValidationErrorCodeImpl.REQUIRED_LAYER_ATTRIBUTE_IS_EMPTY,errors);
            return true;
        }catch (JsonProcessingException |
                NoSuchMethodException | IllegalAccessException | InvocationTargetException e){// Field 가 똑바로 채워지지 않았다.
            List<ArgumentError> errors = new ArrayList<>();
            errors.add(new ArgumentError(e.getClass().getName(),"Validation Failed.",e.getMessage()));
            throw new ValidationExceptions(ValidationErrorCodeImpl.REQUIRED_LAYER_ATTRIBUTE_IS_EMPTY,errors);
//            e.printStackTrace();
//            return false;
        }
    }
}
