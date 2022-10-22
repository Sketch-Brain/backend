package com.sketch.brain.backend.aggregate.trainer.domain;

import com.sketch.brain.backend.aggregate.trainer.infrastructure.ValidationInfraStructure;
import com.sketch.brain.backend.global.error.exceptions.ValidationErrorCodeImpl;
import com.sketch.brain.backend.global.error.exceptions.ValidationExceptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidatorImpl implements Validator{

    private final ValidationInfraStructure infraStructure;

    @Override
    public boolean isValidParameters(ConcurrentHashMap<String, Object> body) {
        return false;
    }

    @Override
    public boolean checkValidLayer(Enumeration<String> layers, ConcurrentHashMap<String, Object> body) {
        //NotNull 인 field 의 값들이 제대로 잘 들어왔는지 여부를 먼저 검사한다.
        while(layers.hasMoreElements()){
            String layerKey = layers.nextElement();
            if(!this.infraStructure.isConvertable(layerKey,body)){
                throw new ValidationExceptions(ValidationErrorCodeImpl.REQUIRED_LAYER_ATTRIBUTE_IS_EMPTY);
            }
        }
        return true;
    }
}
