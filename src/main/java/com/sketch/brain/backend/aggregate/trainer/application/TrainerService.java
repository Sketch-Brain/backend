package com.sketch.brain.backend.aggregate.trainer.application;

import com.sketch.brain.backend.aggregate.trainer.domain.TrainerImpl;
import com.sketch.brain.backend.aggregate.trainer.dto.Runnable;
import com.sketch.brain.backend.aggregate.trainer.model.PythonDocumentModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Service
public class TrainerService {

    private final TrainerImpl trainer;

    public Runnable saveRunnableSource(String userId, ConcurrentHashMap<String, Object> body){
        /**
         * Here is some examples of Json objects.
         * {
         *     "Conv2D":{
         *         "filters":16,
         *         "kernelSize":"(3,3)"
         *     }
         * }
         */
        //Key( Layer 의 종류가 된다. )
        Enumeration<String> keys = body.keys();

        //Convert 를 먼저 진행하고,
        String runnable = this.trainer.convertSource(keys, body);
        log.info("Runnable Outputs : {}",runnable);
        //실제로 DB 에 쓴다.
        return this.trainer.writeSource(userId, runnable);
    }
}
