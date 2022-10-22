package com.sketch.brain.backend.aggregate.trainer.api;

import com.sketch.brain.backend.aggregate.trainer.application.ValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("/api/validator")
@RequiredArgsConstructor
public class ValidatorApi {
    /**
     * 입력받은 Layer 가 올바르게 작성되었는지 확인한다.
     * 절대 실행될 수 없거나, 불가능한 경우를 여기서 제외한다.
     */

    private final ValidationService service;

    @PostMapping(value = "/isValidLayers", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<?> isValidLayers(
            @RequestBody ConcurrentHashMap<String, Object> body//HashMap 은 Concurrent Issue 가 있음.
    ){
        log.info("check isValidParameters");

        //Layer로 쓰기 전, userId 와 같이 필요하지 않은 정보를 제거한다.
        String userId =(String) body.remove("userId");

        //True 라면 Validation Success.
        Map<String, String> result = new ConcurrentHashMap<>();
        if(this.service.isValidLayers(body)){
            result.put("valid","success");
        }else result.put("valid","failed");
        Link selfLink = linkTo(methodOn(ValidatorApi.class).isValidLayers(body)).withSelfRel();
        return EntityModel.of(result, selfLink);
    }
}
