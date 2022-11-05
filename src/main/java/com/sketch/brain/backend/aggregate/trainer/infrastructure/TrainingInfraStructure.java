package com.sketch.brain.backend.aggregate.trainer.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sketch.brain.backend.aggregate.trainer.dao.PythonDocumentRepository;
import com.sketch.brain.backend.aggregate.trainer.dto.LayerEnum;
import com.sketch.brain.backend.aggregate.trainer.model.PythonDocumentModel;
import com.sketch.brain.backend.global.error.exceptions.TrainingErrorCodeImpl;
import com.sketch.brain.backend.global.error.exceptions.TrainingExceptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class TrainingInfraStructure {

    private final ObjectMapper objectMapper;
    private final PythonDocumentRepository pythonDocumentRepository;

    /**
     * body 의 정보들을 Python Runnable Source 로 Convert 변경한다.
     * @param layerKey : Key value(String)
     * @param queue : Object Queue
     * @return
     */
    public String retConstructedString(String layerKey, Queue<LinkedHashMap<String, Object>> queue){
        LayerEnum layerEnum = LayerEnum.find(layerKey);
        log.info("retConstructedString");
        try{
            //Body Data 를 String 으로 convert 한 것.
            String values = this.objectMapper.writeValueAsString(queue.remove());
            log.info("values : {}",values);
            //WildCard 를 사용해서 Class Convert 진행.
            Class<?> layerClass = layerEnum.getLayersClass();
            //Jackson Object Mapper 이용해서, Object 로 convert.
            Object classObject = this.objectMapper.readValue(values,layerClass);
            //SequentialLayers 를 impl, 공통으로 갖는 toRunnableSource 를 실행.
            Method getRunnableMethods = layerClass.getDeclaredMethod("toRunnableSource");
            return (String) getRunnableMethods.invoke(classObject);
        }catch (Exception e){
            //[FIXME] Exception 처리 필요.
            e.printStackTrace();
            return null;
        }
    }

    /**
     * NoSQL DB 에 Document 를 실제로 쓴다.
     * @param userId : userId
     * @param runnable : runnable Python Source.
     */
    public PythonDocumentModel savePythonDocumentModel(String userId, String runnable){
        PythonDocumentModel documentModel = new PythonDocumentModel(ObjectId.get(), userId, runnable, LocalDateTime.now(),null);
        return this.pythonDocumentRepository.save(documentModel);
    }

    /**
     * Document 를 userId값을 바탕으로 찾는다. 복수의 여러 개가 존재할 수 있다.
     * @param userId
     * @return
     */
    public List<PythonDocumentModel> findAllPythonDocumentModelById(String userId){
        List<PythonDocumentModel> models = this.pythonDocumentRepository.findByUserId(userId);
        if( models == null ){
            throw new TrainingExceptions(TrainingErrorCodeImpl.NO_EXPERIMENT_DETECTED);
        }else return models;
    }
}
