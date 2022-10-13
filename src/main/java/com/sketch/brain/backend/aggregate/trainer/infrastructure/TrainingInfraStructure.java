package com.sketch.brain.backend.aggregate.trainer.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sketch.brain.backend.aggregate.trainer.dto.LayerEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class TrainingInfraStructure {

    private final ObjectMapper objectMapper;

    public String retConstructedString(String layerKey, ConcurrentHashMap<String, Object> body){
        LayerEnum layerEnum = LayerEnum.find(layerKey);
        try{
            //Body Data 를 String 으로 convert 한 것.
            String values = this.objectMapper.writeValueAsString(body.get(layerKey));
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
}
