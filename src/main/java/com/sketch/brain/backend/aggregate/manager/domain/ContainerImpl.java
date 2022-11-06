package com.sketch.brain.backend.aggregate.manager.domain;

import com.sketch.brain.backend.aggregate.manager.dto.TokenDto;
import com.sketch.brain.backend.aggregate.manager.entity.ContainerEntity;
import com.sketch.brain.backend.aggregate.manager.infrastructure.ContainerInfraStructure;
import io.fabric8.kubernetes.api.model.Service;
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
    public void run(Deployment deployment, Service service) {
        String namespace = environment.getProperty("sketch.brain.worker.NAME_SPACE");

        this.infraStructure.runDeployment(deployment,namespace);
        this.infraStructure.runService(service, namespace);
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
    public Deployment constructK8sContainer(String namespace, String tag, String imageName, String X_TOKEN, String TOKEN) {
        Deployment deployment = this.infraStructure.constructDeploy(namespace, imageName, tag, X_TOKEN, TOKEN);
        return deployment;
    }

    @Override
    public Service constructK8sService(String namespace, String TOKEN) {
        Service service = this.infraStructure.constructService(namespace, TOKEN);
        return service;
    }
}
