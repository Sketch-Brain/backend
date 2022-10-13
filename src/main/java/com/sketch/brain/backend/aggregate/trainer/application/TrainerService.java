package com.sketch.brain.backend.aggregate.trainer.application;

import com.sketch.brain.backend.aggregate.trainer.domain.TrainerImpl;
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

    public void saveRunnableSource(ConcurrentHashMap<String, Object> body){
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
        String runnable = trainer.convertSource(keys, body);
        log.info("Runnable Outputs : {}",runnable);
        //실제로 DB 에 쓴다.
        trainer.writeSource();
    }
}
