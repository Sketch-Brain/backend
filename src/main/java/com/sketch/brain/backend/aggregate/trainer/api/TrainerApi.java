package com.sketch.brain.backend.aggregate.trainer.api;


import com.sketch.brain.backend.aggregate.trainer.application.TrainerService;
import com.sketch.brain.backend.aggregate.trainer.application.ValidationService;
import com.sketch.brain.backend.aggregate.trainer.model.PythonDocumentModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.*;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

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
    public EntityModel<PythonDocumentModel> saveRunnable(
            @RequestBody ConcurrentHashMap<String, Object> body//HashMap 은 Concurrent Issue 가 있음.
    ){
        log.info("save New Runnable");

        //Layer로 쓰기 전, userId 와 같이 필요하지 않은 정보를 제거한다.
        String userId =(String) body.remove("userId");

        //[FIXME] 이곳에서 User 의 정합성 검사.
        //[FIXME] 이곳에서 Validation 체크 먼저 하고, saveRunnable 로 들어가야 한다. 아직 미구현.

        PythonDocumentModel model = this.trainerService.saveRunnableSource(userId, body);
        Link selfLink = linkTo(methodOn(TrainerApi.class).saveRunnable(body)).withSelfRel();
        return EntityModel.of(model, selfLink);
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
