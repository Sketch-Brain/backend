package com.sketch.brain.backend.aggregate.manager.domain;

import com.sketch.brain.backend.aggregate.manager.dto.TokenDto;
import com.sketch.brain.backend.aggregate.manager.entity.ContainerEntity;
import com.sketch.brain.backend.aggregate.manager.infrastructure.ContainerInfraStructure;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@PropertySource("classpath:bootstrap.yaml")
public class ContainerImpl implements Container{

    private final ContainerInfraStructure infraStructure;
    private final Environment environment;

    @Override
    public void run(Deployment deployment) {
        String namespace = environment.getProperty("sketch.brain.worker.NAME_SPACE");

        this.infraStructure.runDeployment(deployment,namespace);
    }

    @Override
    public void getContainerInfo(String namespace) {
        this.infraStructure.getInfo(namespace);
    }

    @Override
    public TokenDto writeDB(byte[] experimentId, String userId, String dataName, String modelName) {
        ContainerEntity entity = this.infraStructure.writeSource(experimentId, userId, dataName, modelName);
        return new TokenDto(entity.getX_TOKEN(),entity.getTOKEN());
    }

    @Override
    public Deployment constructK8sContainer(TokenDto token) {
        // 값들을 파싱해서 전달.
        String namespace = environment.getProperty("sketch.brain.worker.NAME_SPACE");
        String tag = environment.getProperty("sketch.brain.worker.IMAGE_TAG");
        String imageName = environment.getProperty("sketch.brain.worker.IMAGE_NAME");

        Deployment deployment = this.infraStructure.constructDeploy(namespace, imageName, tag, token.getX_TOKEN(), token.getTOKEN());
        return deployment;
    }
}
