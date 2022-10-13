package com.sketch.brain.backend.aggregate.trainer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConvolutionDto implements SequentialLayers{

    /**
     * Options 를 나열.
     */
    @NotNull
    @JsonProperty("filters")
    private int filters;

    @NotNull
    @JsonProperty("kernelSize")
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

}
