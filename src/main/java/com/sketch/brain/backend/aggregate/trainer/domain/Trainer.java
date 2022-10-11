package com.sketch.brain.backend.aggregate.trainer.domain;

public interface Trainer {
    /**
     * 입력받은 Layer 의 모양대로, 실행할 수 있는 Python Source 를 구성한다.
     */
    public void convertSource();
    public void writeSource();
}
