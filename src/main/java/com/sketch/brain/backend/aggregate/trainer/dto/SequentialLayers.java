package com.sketch.brain.backend.aggregate.trainer.dto;

import com.sketch.brain.backend.global.error.ArgumentError;
import com.sketch.brain.backend.global.error.exceptions.ValidationExceptions;

import java.util.List;

public interface SequentialLayers {
    /**
     * Layer DTO 클래스들이 공통적으로 가져야 할 특징들을 나열.
     * 기본적으로, Python Source 로 Convert 가능해야 함.
     */
    String toRunnableSource();

    /**
     * 실행할 수 없는 형태가 들어오는 경우를 검사하는 로직을 Layer 마다 설정한다.
     * Validator 할 때 사용된다.
     * Return List 가 Empty 가 아닌 경우는 전부 에러로 인식한다.
     */
    List<ArgumentError> mustNeedMetrics();
}
