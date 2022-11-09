package com.sketch.brain.backend.aggregate.trainer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sketch.brain.backend.global.error.ArgumentError;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
    @JsonProperty("filter")
    @NotNull
    private int filters;

    @JsonProperty("kernel_size")//Tuple
    @NotNull
    private String kernel_size;

    @JsonProperty("padding")
    @NotNull
    private String padding;

    @JsonProperty("use_bias")
    @NotNull
    private Boolean use_bias;

    @JsonProperty("kernel_initializer")
    @NotNull
    private String kernel_initializer;

    @JsonProperty("bias_initializer")
    @NotNull
    private String bias_initializer;

    @JsonProperty("strides")//Tuple
    @NotNull
    private String strides;

    @JsonProperty("data_format")
    @Nullable
    private String data_format;

    @Nullable
    @JsonProperty("input_shape")
    private String input_shape;

    /**
     * Convert 되는 Python Source 를 각 Layer 별로 정의한다.
     * @return Runnable Python Source String
     */
    @Override
    public String toRunnableSource() {
        String runnable = "layers.Conv2D("+
            "filters="+this.filters+",";
        if(this.strides != null) runnable += "strides="+this.strides+",";
        if(this.input_shape != null) runnable += "input_shape="+this.input_shape+",";
        if(this.padding != null) runnable += "padding=\""+this.padding+"\",";//String Value!
        if(this.use_bias != null){
            String values = StringUtils.capitalize(this.use_bias.toString());
            runnable += "use_bias="+ values +",";// Python 에서 Boolean 은 첫 글자가 대문자임.
        }
        if(this.kernel_initializer != null) runnable += "kernel_initializer=\""+this.kernel_initializer+"\",";//String Value!
        if(this.bias_initializer != null) runnable += "bias_initializer=\""+this.bias_initializer+"\",";//String Value!
        if(this.data_format != null) runnable += "data_format="+this.data_format+",";
        runnable += "kernel_size="+this.kernel_size+"),\n";
        return runnable;
    }

    /**
     * 검증할, 반드시 꼭 필요한 로직들이 이곳에 작성되어야 함.
     * errors 가 empty 라면 Exception 없음.
     * @return List<ArgumentError>
     */
    @Override
    public List<ArgumentError> mustNeedMetrics() {
        //Return Errors.
        List<ArgumentError> errors = new ArrayList<>();
        //Filter 와 kernelSize 값은 null 이거나, 0과 같은 값이 들어와서는 안된다.
        if( this.filters <= 0) {
            errors.add(new ArgumentError("Conv2D","filters : "+ this.filters,"filter Value is must bigger then 0"));
        }
        if(this.kernel_size == null){
            errors.add(new ArgumentError("Conv2D","kernelSize : null","Param kernelSize required."));
        }//또한, kernelSize 변수 값은 (int,int) 형태여야 한다.
        else if(!Pattern.matches("^\\([0-9]+\\,[0-9]+\\)$",this.kernel_size)){
            errors.add(new ArgumentError("Conv2D","kernelSize : "+this.kernel_size,"Param kernelSize value struct is (int,int)."));
        }
        if(this.strides == null){
            errors.add(new ArgumentError("Conv2D","strides : null","Param strides required."));
        }//또한, strides 변수 값은 (int,int) 형태여야 한다.
        else if(!Pattern.matches("^\\([0-9]+\\,[0-9]+\\)$",this.strides)){
            errors.add(new ArgumentError("Conv2D","strides : "+this.strides,"Param strides value struct is (int,int)."));
        }
        return errors;
    }

}
