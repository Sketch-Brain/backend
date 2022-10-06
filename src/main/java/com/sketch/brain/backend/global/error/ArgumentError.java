package com.sketch.brain.backend.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ArgumentError {
    private final String fieldName;
    private final String value;
    private final String reason;
}
