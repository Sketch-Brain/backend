package com.sketch.brain.backend.aggregate.trainer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sketch.brain.backend.global.error.ArgumentError;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchNormalizationDto implements SequentialLayers{

    @JsonProperty("axis")
    @NotNull
    private int axis;

    @Override
    public String toRunnableSource() {
        return "layers.BatchNormalization(axis="+this.axis+"),\n";
    }

    @Override
    public List<ArgumentError> mustNeedMetrics() {
        List<ArgumentError> errors = new ArrayList<>();

        if( this.axis <= 0) {
            errors.add(new ArgumentError("BatchNormalization","axis : "+ this.axis,"axis value is must bigger then 0"));
        }
        return errors;
    }
}
