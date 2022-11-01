package com.sketch.brain.backend.aggregate.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenDto {

    @NotNull
    private String X_TOKEN;
    @NotNull
    private String TOKEN;
}
