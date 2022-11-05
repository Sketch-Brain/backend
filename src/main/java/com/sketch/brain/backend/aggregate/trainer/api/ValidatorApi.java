package com.sketch.brain.backend.aggregate.trainer.api;

import com.sketch.brain.backend.aggregate.trainer.application.ValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
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
        log.info("body : {}",body);
        String userId =(String) body.remove("userId");

        ArrayList<String> keyValues = new ArrayList<>();
        Queue<LinkedHashMap<String,Object>> values = new LinkedList<>();
        //Key 값과 body value 를 추출하기 위해서 변경.
        ArrayList<LinkedHashMap<String,Object>> layers = (ArrayList<LinkedHashMap<String,Object>>) body.remove("layers");
        layers.forEach(layer->{// Key 값들 추출하면서, Object 에서 Name 제거.
            keyValues.add((String) layer.remove("name"));
            values.add(layer);
        });
        Enumeration<String> keys = Collections.enumeration(keyValues);
        //True 라면 Validation Success.
        Map<String, String> result = new ConcurrentHashMap<>();
        if(this.service.isValidLayers(keys,values)){
            result.put("valid","success");
        }else result.put("valid","failed");
        Links allLinks;
        Link selfLink = linkTo(methodOn(ValidatorApi.class).isValidLayers(body)).withSelfRel();
        Link saveLink = linkTo(methodOn(TrainerApi.class).saveRunnable(body)).withRel("save");

        allLinks = Links.of(selfLink, saveLink);
        return EntityModel.of(result, allLinks);
    }
}
