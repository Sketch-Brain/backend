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
public class ActivationDto implements SequentialLayers{

    @JsonProperty("activation")
    @NotNull
    private String activation;//String

    @Override
    public String toRunnableSource() {
        return "layers.Activation('"+this.activation+"'),\n";
    }

    @Override
    public List<ArgumentError> mustNeedMetrics() {
        List<ArgumentError> errors = new ArrayList<>();
        if(this.activation == null){
            errors.add(new ArgumentError("Activation","type : null","Activation type must required."));
        }
        if(!this.activation.equals("relu") && !this.activation.equals("selu")){
            errors.add(new ArgumentError("Activation","type :"+this.activation,"Support Activation type is 'relu' or 'selu'"));
        }
        return errors;
    }
}
