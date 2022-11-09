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
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.MediaTypes;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("/api/trainer/container")
@RequiredArgsConstructor
public class ContainerApi {

    private final ContainerService containerService;

    /**
     * 실제 학습 Pod 를 실행시킨다.
     * @param body RequestBody
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

        MultiValueMap<String, Object> results = new LinkedMultiValueMap<>();

        //Pod 가 준비되었다면, runnable 을 Insert 하기.
        while(true){// 이 반복문은 isContainerReady 함수 반복실행을 위한 것.
            if(this.containerService.isContainerReady(experimentId, tokens.getX_TOKEN(),tokens.getTOKEN())){
                //FIXME - 이후 Return, value 체크, Hateoas chnage 해야함.
                log.info("Inject Runnable sources");
                this.containerService.injectRunnable(experimentId,runnable,tokens.getX_TOKEN(),tokens.getTOKEN());
                results.add("experimentId",new ObjectId(experimentId).toString());
                log.info("Results Add :{}", new ObjectId(experimentId).toString());
                break;
            }
        }

        //FIXME 실패하는 경우, delete 만 남기고, Start, getInfo 와 같은 정보는 모두 삭제.
        Links allLinks;
        Link selfLink = linkTo(methodOn(ContainerApi.class).createExperiment(body)).withSelfRel();
        Link startLink = linkTo(methodOn(ContainerApi.class).startExperiment(body)).withRel("start");
        Link deleteLink = linkTo(methodOn(ContainerApi.class).deleteExperiment(body)).withRel("delete");
        allLinks = Links.of(selfLink, startLink, deleteLink);
        return EntityModel.of(results,allLinks);
    }

    //학습을 시작한다.
    @PatchMapping(value="/start/experiment",produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<?> startExperiment(
            @RequestBody ConcurrentHashMap<String, Object> body
    ){
        //get Required Arguments
        //필수 정보 userId 를 읽는다.
        String userId =(String) body.remove("userId");
        log.info("userId : {}",userId);
        byte[] experimentId = new ObjectId((String) body.remove("experimentId")).toByteArray();

        //Patch 에서 Mapping 된 Value 가 없으면 에러 발생.
        List<ArgumentError> errors = new ArrayList<>();
        if(userId == null) {
            errors.add(new ArgumentError("userId","Validation Failed.","Value userId required but accept null"));
            throw new ValidationExceptions(ValidationErrorCodeImpl.REQUIRED_PARAM_NOT_FOUND,errors);
        }else if(!Pattern.matches("^[a-z|A-Z|0-9|_-]*$",userId)){
            errors.add(new ArgumentError("userId","Forbidden Special characters in userId","userId only allowed numbers ( 0-9 ) & Alphabets( a-z, A-Z ) & Dash( - ), Under bar( _ )"));
            throw new ValidationExceptions(ValidationErrorCodeImpl.SPECIAL_CHARACTER_FORBIDDEN,errors);
        }
        LinkedHashMap<String, Object> result = this.containerService.startExperiment(experimentId,userId);

        Links allLinks;
        Link selfLink = linkTo(methodOn(ContainerApi.class).deleteExperiment(body)).withSelfRel();
//        allLinks = Links.of(selfLink, startLink, deleteLink);
        return EntityModel.of(result,selfLink);
    }

    @DeleteMapping(value = "/delete/experiment",produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<?> deleteExperiment(
            @RequestBody ConcurrentHashMap<String, Object> body
    ){
        //get Required Arguments
        //필수 정보 userId 를 읽는다.
        String userId =(String) body.remove("userId");
        log.info("userId : {}",userId);
        byte[] experimentId = new ObjectId((String) body.remove("experimentId")).toByteArray();

        //Patch 에서 Mapping 된 Value 가 없으면 에러 발생.
        List<ArgumentError> errors = new ArrayList<>();
        if(userId == null) {
            errors.add(new ArgumentError("userId","Validation Failed.","Value userId required but accept null"));
            throw new ValidationExceptions(ValidationErrorCodeImpl.REQUIRED_PARAM_NOT_FOUND,errors);
        }else if(!Pattern.matches("^[a-z|A-Z|0-9|_-]*$",userId)){
            errors.add(new ArgumentError("userId","Forbidden Special characters in userId","userId only allowed numbers ( 0-9 ) & Alphabets( a-z, A-Z ) & Dash( - ), Under bar( _ )"));
            throw new ValidationExceptions(ValidationErrorCodeImpl.SPECIAL_CHARACTER_FORBIDDEN,errors);
        }

        return null;
    }

    /**
     * Get Experiment 의 Status 정보를 Read.
     * @param experimentId UUID
     * @param userId userId
     * @return
     */
    @GetMapping(value = "/get/experimentInfo/{experimentId}/{userId}",produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<?> getExperimentInfo(
            @PathVariable String experimentId, @PathVariable String userId
    ){
        byte[] convertExpId = new ObjectId(experimentId).toByteArray();

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
