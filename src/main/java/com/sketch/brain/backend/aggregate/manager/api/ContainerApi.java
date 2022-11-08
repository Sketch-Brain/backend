package com.sketch.brain.backend.aggregate.manager.api;

import com.sketch.brain.backend.aggregate.manager.application.ContainerService;
import com.sketch.brain.backend.aggregate.manager.dto.TokenDto;
import com.sketch.brain.backend.global.error.ArgumentError;
import com.sketch.brain.backend.global.error.exceptions.ValidationErrorCodeImpl;
import com.sketch.brain.backend.global.error.exceptions.ValidationExceptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/api/trainer/container")
@RequiredArgsConstructor
public class ContainerApi {

    private final ContainerService containerService;

    /**
     * 실제 학습 Pod 를 실행시킨다.
     * @param body
     * @return
     */
    @PostMapping(value = "/create/experiment", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<?> createExperiment(
            @RequestBody ConcurrentHashMap<String, Object> body//HashMap 은 Concurrent Issue 가 있음.
    ){
        //필수 정보 userId 를 읽는다.
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

        //get Required Arguments
        byte[] experimentId = new ObjectId((String) body.remove("experimentId")).toByteArray();
        String runnable = (String) body.remove("runnable");
        String datasetName = (String) body.remove("dataName");
        String modelName = (String) body.remove("modelName");
        if(experimentId == null || runnable == null) {
            errors.add(new ArgumentError("userId","Validation Failed.","Value runnable & experiment Id required but accept null"));
            throw new ValidationExceptions(ValidationErrorCodeImpl.REQUIRED_PARAM_NOT_FOUND,errors);
        }
        //Token 발행,
        TokenDto tokens = this.containerService.writeSource(experimentId,userId,datasetName,modelName);
        this.containerService.runContainer(userId, datasetName, tokens);

        //Pod 가 준비되었다면, runnable 을 Insert 하기.
        while(true){
            if(this.containerService.isContainerReady(tokens.getX_TOKEN(),tokens.getTOKEN())){
                //FIXME - 이후 Return, value 체크.
                this.containerService.injectRunnable(runnable,tokens.getX_TOKEN(),tokens.getTOKEN());
                break;
            }
            //준비되지 않았다면, 계속해서 다시 Call.
        }
        return null;
    }

    @GetMapping(value = "/get/podList/{namespace}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<?> getKubernetesPodList(
            @PathVariable String namespace
    ){
        log.info("Kubernetes Namespace get : {}",namespace);
        this.containerService.getPodLists(namespace);
        return null;
    }


}
