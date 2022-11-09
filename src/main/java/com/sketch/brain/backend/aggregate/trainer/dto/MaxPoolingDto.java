package com.sketch.brain.backend.aggregate.trainer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sketch.brain.backend.global.error.ArgumentError;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaxPoolingDto implements SequentialLayers{

    @JsonProperty("pool_size")
    @NotNull
    private String pool_size;//Tuple

    @JsonProperty("strides")
    @NotNull
    private String strides;//String 인데, 실제 사용 예제는 Integer 처럼 씀.

    @JsonProperty("padding")
    @NotNull
    private String padding;

    @Override
    public String toRunnableSource() {
        String runnable = "layers.MaxPooling2D(pool_size="+this.pool_size+",";
        
        return runnable;
    }

    @Override
    public List<ArgumentError> mustNeedMetrics() {
        List<ArgumentError> errors = new ArrayList<>();
        if(this.pool_size == null){
            errors.add(new ArgumentError("MaxPooling2D","pool_size : null","Param pool_size required."));
        }//또한, pool_size 변수 값은 (int,int) 형태여야 한다.
        else if(!Pattern.matches("^\\([0-9]+\\,[0-9]+\\)$",this.pool_size)){
            errors.add(new ArgumentError("MaxPooling2D","pool_size : "+this.pool_size,"Param pool_size value struct is (int,int)."));
        }
        return errors;
    }
}
