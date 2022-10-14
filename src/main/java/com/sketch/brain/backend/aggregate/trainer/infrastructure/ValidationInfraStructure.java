package com.sketch.brain.backend.aggregate.trainer.infrastructure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sketch.brain.backend.aggregate.trainer.dto.LayerEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class ValidationInfraStructure {
    private final ObjectMapper objectMapper;

    public boolean isConvertable(String layerKey, ConcurrentHashMap<String, Object> body){
        try{
            this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            //알 수 없는 Properties 에 대해서 Failed 한다.
            this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,true);

            //FIXME THIS LOGIC NOT WORKING. NEED TO FIX
            this.objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES,true);
            String values = this.objectMapper.writeValueAsString(body.get(layerKey));
            //여기에서 Exception 이 발생하지 않으면, 넘어가면 된다.
            this.objectMapper.readValue(values, LayerEnum.find(layerKey).getLayersClass());

            return true;
        }catch (JsonProcessingException nullPointerException){// Field 가 똑바로 채워지지 않았다.
            return false;
        }
    }
}
