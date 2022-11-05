package com.sketch.brain.backend.aggregate.trainer.domain;

import com.sketch.brain.backend.aggregate.trainer.dto.Runnable;
import com.sketch.brain.backend.aggregate.trainer.infrastructure.TrainingInfraStructure;
import com.sketch.brain.backend.aggregate.trainer.model.PythonDocumentModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class TrainerImpl implements Trainer{

    private final TrainingInfraStructure infraStructure;

    @Override
    public String convertSource(Enumeration<String> layers, ConcurrentHashMap<String, Object> body) {
        log.info("Impl convertSource");
        StringBuilder runnable = new StringBuilder();
        // Element 가 있을 때 까지, Convert를 진행한다.
        while(layers.hasMoreElements()){
            String layerKey = layers.nextElement();
            runnable.append(this.infraStructure.retConstructedString(layerKey,body));
        }
        return runnable.toString();
    }

    @Override
    public Runnable writeSource(String userId, String runnable) {
        log.info("writeSource to Database");
        PythonDocumentModel model = this.infraStructure.savePythonDocumentModel(userId, runnable);
        return new Runnable(model.getExperimentId().toString(),model.getRunnable());
    }
}
