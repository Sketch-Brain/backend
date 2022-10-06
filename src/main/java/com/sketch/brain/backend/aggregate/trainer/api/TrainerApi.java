package com.sketch.brain.backend.aggregate.trainer.api;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("/api/trainer")
@RequiredArgsConstructor
public class TrainerApi {
    /**
     * 학습이 Container 로 실행되기 이전에, 입력된 layer 를 실제로 작동할 수 있는
     * Python Layer source 로 변경시킨다.
     * 해당 내용을 Database 에 저장한다.
     */




    /**
     * Training 을 진행중인 Project의 학습 정보를 Get 한다.
     * @param projectId : String
     * @return EntityModel<Project Informations>
     */
    @GetMapping("/getInfo/{projectId}")
    public EntityModel<?> getProjectInfo(
            @PathVariable String projectId
    ){
        Link selfLink = linkTo(methodOn(TrainerApi.class).getProjectInfo(projectId))
                .withSelfRel();

        return EntityModel.of(selfLink);
    }
}
