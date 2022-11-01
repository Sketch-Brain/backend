package com.sketch.brain.backend.aggregate.manager.infrastructure;

import com.sketch.brain.backend.aggregate.manager.dao.ContainerRepository;
import com.sketch.brain.backend.aggregate.manager.entity.ContainerEntity;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
@PropertySource("classpath:application.yaml")
public class ContainerInfraStructure {

    private final ContainerRepository containerRepository;
    //Kubernetes, dev, local 에서는 Mocking kubernetes 를 사용한다.
    private final KubernetesClient kubernetesClient;
    private final Environment environment;

    public ContainerEntity writeSource(byte[] experimentId, String userId, String dataName, String modelName){
        ContainerEntity entity = new ContainerEntity();
        entity.setExperiment_id(experimentId);
        entity.setUser_id(userId);
        entity.setData_name(dataName);
        entity.setModel_name(modelName);
        // TOKEN 값이 없으면, 통신 불가능.
        entity.setX_TOKEN(RandomStringUtils.random(10,true,true));
        entity.setTOKEN(RandomStringUtils.random(10,true,true));
        return this.containerRepository.save(entity);
    }

    public void getInfo(String namespace){
        kubernetesClient.pods()
                .inNamespace(namespace)
                .list()
                .getItems()
                .forEach(pod -> {
                    log.info(pod.getMetadata().getName());
                });
    }

    public Deployment constructDeploy(String namespace, String imageName, String tag, String X_TOKEN, String TOKEN) {
        String appName = "training-container";
        String workerName = "training-worker";
        String imagePullSecret = "docker-pull-secret";
        String imagePullPolicy = "IfNotPresent";

        String DatabaseURL = "Mock";
        //FIXME DatabaseURL Construct 필요!
//        String DatabaseURL = String.format(
//                "mysql+mysqldb://%s:%s@%s:%s/%s",
//                this.environment.getProperty("spring.datasource.username"),
//                this.environment.getProperty("spring.datasource.password"),
//
//        )

        Deployment deployment = new DeploymentBuilder()
                .withNewMetadata()
                    .withName(appName+"-deploy")
                    .withNamespace(namespace)
                    .addToLabels("app",appName)
                .endMetadata()
                .withNewSpec()
                    .withReplicas(1)//Default replica = 1
                    .withNewSelector()
                        .addToMatchLabels("app",workerName)
                    .endSelector()
                    .withNewTemplate()
                        .withNewMetadata()
                            .addToLabels("app",workerName)
                        .endMetadata()
                        .withNewSpec()
                            .addNewImagePullSecret(imagePullSecret)
                            .addNewContainer()
                                .withName(workerName)
                                .withImage(imageName+":"+tag)
                                .withImagePullPolicy(imagePullPolicy)
                                .addNewPort()
                                    .withName(workerName)
                                    .withContainerPort(1234)
                                .endPort()
                                .addNewEnv().withName("SQL_DATABASE_URLS").withValue(DatabaseURL).endEnv()
                                .addNewEnv().withName("X_TOKEN").withValue(X_TOKEN).endEnv()
                                .addNewEnv().withName("TOKEN").withValue(TOKEN).endEnv()
                            .endContainer()
                        .endSpec()
                    .endTemplate()
                .endSpec()
        .build();
        log.info("deployment : {}",deployment.toString());
        return deployment;
    }

    public void runDeployment(@NotNull Deployment deployment, String namespace){
        log.info("Apply Deployment :{}",deployment.getMetadata().getName());
        Deployment apply = this.kubernetesClient.apps().deployments().inNamespace(namespace).createOrReplace(deployment);
    }
}
