package com.sketch.brain.backend.aggregate.trainer.domain;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

public interface Validator {

    public boolean isValidParameters(ConcurrentHashMap<String, Object> body);


    public boolean checkValidLayer(Enumeration<String> layers, ConcurrentHashMap<String, Object> body);
}
