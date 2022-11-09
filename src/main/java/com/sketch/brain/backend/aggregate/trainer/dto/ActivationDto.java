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

    @JsonProperty("type")
    @NotNull
    private String type;//String

    @Override
    public String toRunnableSource() {
        return "layers.Activation('"+this.type+"'),\n";
    }

    @Override
    public List<ArgumentError> mustNeedMetrics() {
        List<ArgumentError> errors = new ArrayList<>();
        if(this.type == null){
            errors.add(new ArgumentError("Activation","type : null","Activation type must required."));
        }
        if(!this.type.equals("relu") && !this.type.equals("selu")){
            errors.add(new ArgumentError("Activation","type :"+this.type,"Support Activation type is 'relu' or 'selu'"));
        }
        return errors;
    }
}
