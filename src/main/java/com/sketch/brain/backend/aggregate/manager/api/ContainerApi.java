package com.sketch.brain.backend.aggregate.manager.api;

import com.sketch.brain.backend.aggregate.manager.application.ContainerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/api/container")
@RequiredArgsConstructor
public class ContainerApi {

    private final ContainerService containerService;

    /**
     * 실제 학습 Pod 를 실행시킨다.
     * @param body
     * @return
     */
    @PostMapping(value = "/create/Experiment", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<?> createExperiment(
            @RequestBody ConcurrentHashMap<String, Object> body//HashMap 은 Concurrent Issue 가 있음.
    ){
        return null;
    }

}
