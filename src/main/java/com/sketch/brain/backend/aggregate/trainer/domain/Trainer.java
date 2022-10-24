package com.sketch.brain.backend.aggregate.trainer.domain;

import com.sketch.brain.backend.aggregate.trainer.dto.Runnable;
import com.sketch.brain.backend.aggregate.trainer.model.PythonDocumentModel;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

public interface Trainer {
    /**
     * 입력받은 Layer 의 모양대로, 실행할 수 있는 Python Source 를 구성한다.
     */
    String convertSource(Enumeration<String> layers, ConcurrentHashMap<String, Object> body);

    /**
     * 입력받은 Source 를 실제로 DB 에 작성한다.
     */
    Runnable writeSource(String userId, String runnable);
}
