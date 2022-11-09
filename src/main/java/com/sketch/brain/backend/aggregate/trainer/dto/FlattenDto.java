package com.sketch.brain.backend.aggregate.trainer.dto;

import com.sketch.brain.backend.global.error.ArgumentError;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class FlattenDto implements SequentialLayers{
    //NO FIELDS
    @Override
    public String toRunnableSource() {
        return "layers.Flatten(),\n";
    }

    @Override
    public List<ArgumentError> mustNeedMetrics() {
        List<ArgumentError> errors = new ArrayList<>();

        return errors;
    }
}
