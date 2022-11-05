package com.sketch.brain.backend.aggregate.trainer.domain;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public interface Validator {

    public boolean isValidParameters(ConcurrentHashMap<String, Object> body);


    public boolean checkValidLayer(Enumeration<String> layers, Queue<LinkedHashMap<String, Object>> queue);
}
