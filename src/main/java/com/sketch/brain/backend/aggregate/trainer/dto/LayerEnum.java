package com.sketch.brain.backend.aggregate.trainer.dto;

import com.sketch.brain.backend.global.error.exceptions.TrainingErrorCodeImpl;
import com.sketch.brain.backend.global.error.exceptions.TrainingExceptions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum LayerEnum {

    /**
     * Request 를 받을 때, 이 Layer 를 특정할 이름이 필요하다.
     * 해당 이름을 정의한다.
     * Request body's Key name , Class Name
     */
    CONVOLUTION("Conv2D",ConvolutionDto.class),
    ACTIVATION("Activation",ActivationDto.class),
    BATCH_NORMALIZATION("BatchNormalization",BatchNormalizationDto.class),
    DENSE("Dense",DenseDto.class),
    DROP_OUT("Dropout",DropoutDto.class),
    FLATTEN("Flatten",FlattenDto.class),
    GLOBAL_AVERAGE_POOLING("GlobalAveragePooling2D",GlobalAveragePoolingDto.class),
    MAX_POOLING("MaxPooling2D",MaxPoolingDto.class),
    ZERO_PADDING("ZeroPadding2D",ZeroPaddingDto.class),
    UNKNOWN(null,null);

    private final String key;
    private final Class<?> layersClass;

    public static LayerEnum find(String keyValue){
        return Stream.of(values())
                .filter(layerEnum -> {
                    try{
                        return layerEnum.key.equals(keyValue);
                    }catch (NullPointerException e) {// 없는 Layer로 학습을 돌리려 한다면.
                        throw new TrainingExceptions(TrainingErrorCodeImpl.UNKNOWN_LAYER_DETECTED);
                    }
                })
                .findAny()
                .orElse(UNKNOWN);
    }
}
