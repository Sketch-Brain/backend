package com.sketch.brain.backend.aggregate.trainer.dto;

import com.sketch.brain.backend.global.error.ArgumentError;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DropoutDto implements SequentialLayers{

    private float rate;

    @Override
    public String toRunnableSource() {
        return "layers.Dropout(rate="+this.rate+"),\n";
    }

    @Override
    public List<ArgumentError> mustNeedMetrics() {
        List<ArgumentError> errors = new ArrayList<>();
        if( this.rate <= 0) {
            errors.add(new ArgumentError("Dropout","rate : "+ this.rate,"rate value is must bigger then 0"));
        }
        return errors;
    }
}
