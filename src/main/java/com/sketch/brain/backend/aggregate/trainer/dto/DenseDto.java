package com.sketch.brain.backend.aggregate.trainer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sketch.brain.backend.global.error.ArgumentError;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DenseDto implements SequentialLayers{

    @JsonProperty("units")
    @NotNull
    private int units;

    @JsonProperty("activation")
    @NotNull
    private String activation;

    @JsonProperty("use_bias")
    @NotNull
    private Boolean use_bias;

    @JsonProperty("bias_initializer")
    @NotNull
    private String bias_initializer;

    @Override
    public String toRunnableSource() {
        String runnable = "layers.Dense(units="+this.units+",";
        if(this.activation != null) runnable+="activation='"+this.activation+"',";//String value
        if(this.use_bias != null){
            String values = StringUtils.capitalize(this.use_bias.toString());
            runnable += "use_bias="+ values +",";// Python 에서 Boolean 은 첫 글자가 대문자임.
        }
        runnable +="bias_initializer='"+this.bias_initializer+"'),\n";
        return runnable;
    }

    @Override
    public List<ArgumentError> mustNeedMetrics() {
        List<ArgumentError> errors = new ArrayList<>();

        if( this.units <= 0) {
            errors.add(new ArgumentError("Dense","units : "+ this.units,"units value is must bigger then 0"));
        }
        if(!this.activation.equals("relu") && !this.activation.equals("selu") && !this.activation.equals("softmax")){
            errors.add(new ArgumentError("Dense","activation :"+this.activation,"Support Activation type is 'relu' or 'selu'"));
        }
        return errors;
    }
}
