package com.sketch.brain.backend.aggregate.trainer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Runnable {

    private String experimentId;
    private String runnable;
}
