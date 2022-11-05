package com.sketch.brain.backend.aggregate.trainer.application;

import com.sketch.brain.backend.aggregate.trainer.domain.TrainerImpl;
import com.sketch.brain.backend.aggregate.trainer.dto.Runnable;
import com.sketch.brain.backend.aggregate.trainer.model.PythonDocumentModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Service
public class TrainerService {

    private final TrainerImpl trainer;

    public Runnable saveRunnableSource(String userId, Enumeration<String> keys, Queue<LinkedHashMap<String, Object>> values){
        /**
         * Here is some examples of Json objects.
         * {
         *     "userId":"hellouser",
         *     "layers":[
         *         {
         *             "name":"Conv2D",
         *             "filters":3,
         *             "kernelSize":"(3,3)"
         *         },
         *         {
         *             "name":"Conv2D",
         *             "filters":4,
         *             "kernelSize":"(4,4)"
         *         }
         *     ]
         * }
         */
        //Convert 를 먼저 진행하고,
        String runnable = this.trainer.convertSource(keys, values);
        log.info("Runnable Outputs : {}",runnable);
        //실제로 DB 에 쓴다.
        return this.trainer.writeSource(userId, runnable);
    }
}
