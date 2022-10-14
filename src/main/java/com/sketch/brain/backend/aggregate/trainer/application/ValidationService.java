package com.sketch.brain.backend.aggregate.trainer.application;

import com.sketch.brain.backend.aggregate.trainer.domain.ValidatorImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidationService {

    private final ValidatorImpl validator;

    public boolean isValidLayers(ConcurrentHashMap<String, Object> body){
        //Key( Layer 의 종류가 된다. )
        Enumeration<String> keys = body.keys();
        return this.validator.checkValidLayer(keys,body);
    }
}
