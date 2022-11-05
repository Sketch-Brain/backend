package com.sketch.brain.backend.aggregate.trainer.api;


import com.sketch.brain.backend.aggregate.trainer.application.TrainerService;
import com.sketch.brain.backend.aggregate.trainer.application.ValidationService;
import com.sketch.brain.backend.aggregate.trainer.dto.Runnable;
import com.sketch.brain.backend.aggregate.trainer.model.PythonDocumentModel;
import com.sketch.brain.backend.global.error.ArgumentError;
import com.sketch.brain.backend.global.error.exceptions.ValidationErrorCodeImpl;
import com.sketch.brain.backend.global.error.exceptions.ValidationExceptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("/api/trainer")
@RequiredArgsConstructor
public class TrainerApi {//Aggregate Root - Trainer

    private final TrainerService trainerService;
    private final ValidationService validationService;

    /**
     * 학습이 Container 로 실행되기 이전에, 입력된 layer 를 실제로 작동할 수 있는
     * Python Layer source 로 변경시킨다.
     * 해당 내용을 Database 에 저장한다.
     */
    @PostMapping(value = "/save/runnable", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<Runnable> saveRunnable(
            @RequestBody ConcurrentHashMap<String, Object> body//HashMap 은 Concurrent Issue 가 있음.
    ){
        log.info("save New Runnable");
        //[FIXME] 이곳에서 User 의 정합성 검사.
        //Layer로 쓰기 전, userId 와 같이 필요하지 않은 정보를 제거한다.
        String userId =(String) body.remove("userId");
        log.info("userId : {}",userId);
        //Post 에서 Mapping 된 Value 가 없으면 에러 발생.
        List<ArgumentError> errors = new ArrayList<>();
        if(userId == null) {
            errors.add(new ArgumentError("userId","Validation Failed.","Value userId required but accept null"));
            throw new ValidationExceptions(ValidationErrorCodeImpl.REQUIRED_PARAM_NOT_FOUND,errors);
        }else if(!Pattern.matches("^[a-z|A-Z|0-9|_-]*$",userId)){
            errors.add(new ArgumentError("userId","Forbidden Special characters in userId","userId only allowed numbers ( 0-9 ) & Alphabets( a-z, A-Z ) & Dash( - ), Under bar( _ )"));
            throw new ValidationExceptions(ValidationErrorCodeImpl.SPECIAL_CHARACTER_FORBIDDEN,errors);
        }

        ArrayList<String> keyValues = new ArrayList<>();
        Queue<LinkedHashMap<String,Object>> values = new LinkedList<>();
        //Key 값과 body value 를 추출하기 위해서 변경.
        ArrayList<LinkedHashMap<String,Object>> layers = (ArrayList<LinkedHashMap<String,Object>>) body.remove("layers");
        layers.forEach(layer->{// Key 값들 추출하면서, Object 에서 Name 제거.
            keyValues.add((String) layer.remove("name"));
            values.add(layer);
        });
        Enumeration<String> keys = Collections.enumeration(keyValues);
        //Validation 은 분리하였음. 연속으로 호출하게끔 변경. 따라서 제거.
//        this.validationService.isValidLayers(keys, values);

        Runnable runnableSource = this.trainerService.saveRunnableSource(userId, keys, values);
        Link selfLink = linkTo(methodOn(TrainerApi.class).saveRunnable(body)).withSelfRel();
        return EntityModel.of(runnableSource, selfLink);
    }


    /**
     * Training 을 진행중인 Project의 학습 정보를 Get 한다.
     * @param projectId : String
     * @return EntityModel<Project Informations>
     */
    @GetMapping("/getInfo/{experimentId}")
    public EntityModel<?> getExperimentInfo(
            @PathVariable ObjectId projectId
    ){
        Link selfLink = linkTo(methodOn(TrainerApi.class).getExperimentInfo(projectId))
                .withSelfRel();

        return EntityModel.of(selfLink);
    }
}
