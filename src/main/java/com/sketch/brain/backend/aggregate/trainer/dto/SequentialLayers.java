package com.sketch.brain.backend.aggregate.trainer.dto;

public interface SequentialLayers {
    /**
     * Layer DTO 클래스들이 공통적으로 가져야 할 특징들을 나열.
     * 기본적으로, Python Source 로 Convert 가능해야 함.
     */
    String toRunnableSource();
}
