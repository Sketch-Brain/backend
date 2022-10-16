package com.sketch.brain.backend.aggregate.trainer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sketch.brain.backend.global.error.ArgumentError;
import com.sketch.brain.backend.global.error.exceptions.TrainingExceptions;
import com.sketch.brain.backend.global.error.exceptions.ValidationErrorCodeImpl;
import com.sketch.brain.backend.global.error.exceptions.ValidationExceptions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConvolutionDto implements SequentialLayers{

    /**
     * Options 를 나열.
     */
    @JsonProperty("filters")
    @NotNull
    private int filters;

    @JsonProperty("kernelSize")
    @NotNull
    private String kernelSize;

    @Nullable
    @JsonProperty("strides")
    private int strides;

    @Nullable
    @JsonProperty("inputShape")
    private String inputShape;

    /**
     * Convert 되는 Python Source 를 각 Layer 별로 정의한다.
     * @return Runnable Python Source String
     */
    @Override
    public String toRunnableSource() {
        String runnable = "layers.Conv2D("+
            "filters="+this.filters+",";
        if(this.strides > 0) runnable += "strides="+this.strides+",";
        if(this.inputShape != null) runnable += "input_shape="+this.inputShape+",";
        runnable += "kernel_size="+this.kernelSize+"),\n";
        return runnable;
    }

    @Override
    public List<ArgumentError> mustNeedMetrics() {
        //Return Errors.
        List<ArgumentError> errors = new ArrayList<>();
        //Filter 와 kernelSize 값은 null 이거나, 0과 같은 값이 들어와서는 안된다.
        if( this.filters <= 0) {
            errors.add(new ArgumentError("Conv2D","filters : "+String.valueOf(this.filters),"filter Value is must bigger then 0"));
        }
        if(this.kernelSize == null){
            errors.add(new ArgumentError("Conv2D","kernelSize : null","Param kernelSize required."));
        }//또한, kernelSize 변수 값은 (int,int) 형태여야 한다.
        else if(!Pattern.matches("^\\([0-9]+\\,[0-9]+\\)$",this.kernelSize)){
            errors.add(new ArgumentError("Conv2D","kernelSize : "+this.kernelSize,"Param kernelSize value struct is (int,int)."));
        }
        return errors;
    }

}
