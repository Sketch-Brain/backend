package com.sketch.brain.backend.aggregate.manager.infrastructure;

import com.sketch.brain.backend.aggregate.manager.dao.ContainerRepository;
import com.sketch.brain.backend.aggregate.manager.entity.ContainerEntity;
import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;

import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@RequiredArgsConstructor
@Component
@PropertySource("classpath:application.yaml")
public class ContainerInfraStructure {

    private final ContainerRepository containerRepository;
    //Kubernetes, dev, local 에서는 Mocking kubernetes 를 사용한다.
    private final KubernetesClient kubernetesClient;
    private final Environment environment;

    /**
     * SQL Database 에 Entity 작성.
     * @param experimentId
     * @param userId
     * @param dataName
     * @param modelName
     * @return
     */
    public ContainerEntity writeSource(byte[] experimentId, String userId, String dataName, String modelName){
        ContainerEntity entity = new ContainerEntity();
        entity.setExperiment_id(experimentId);
        entity.setUser_id(userId);
        entity.setData_name(dataName);
        entity.setModel_name(modelName);
        entity.setCreated_at(LocalDateTime.now());
        // TOKEN 값이 없으면, 통신 불가능.
        entity.setX_TOKEN(RandomStringUtils.random(10,true,true));
        entity.setTOKEN(RandomStringUtils.random(10,true,true));
        // 11/8 각종 value 추가.
        entity.setStatus("Created");
        entity.setPython_source(null);
        entity.setAccuracy(null);
        return this.containerRepository.save(entity);
    }

    public ContainerEntity updateStatus(byte[] experimentId, String status){
        this.containerRepository.updateStatusByExperimentId(experimentId, status);
        return this.containerRepository.findByExperimentId(experimentId);
    }

    public ContainerEntity updatePythonSource(byte[] experimentId, String pythonSource){
        this.containerRepository.updatePythonSource(experimentId, pythonSource);
        return this.containerRepository.findByExperimentId(experimentId);
    }

    public ContainerEntity getEntityByExperimentId(byte[] experimentId, String userId){
        return this.containerRepository.findByExperimentIdAndUserId(experimentId,userId);
    }

    public void getInfo(String namespace){
        kubernetesClient.pods()
                .inNamespace(namespace)
                .list()
                .getItems()
                .forEach(pod -> {
//                    pod.getStatus().getContainerStatuses().forEach(containerStatus -> log.info("status: {}, {}",containerStatus.getReady(),containerStatus.getStarted()));
                    log.info(pod.getMetadata().getName());
                });
    }
    /**
     * HealthCheck. for Training Containers.
     */
    public Boolean isReadyRestServer(UriComponents urls, HttpHeaders headers){
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(5000);//Set timeouts
        factory.setReadTimeout(4000);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body,headers);
        try {
            RestTemplate restTemplate = new RestTemplate(factory);
            ResponseEntity<Object> results =
                    restTemplate.exchange(
                            urls.toString(),
                            HttpMethod.GET,
                            entity,
                            Object.class
                    );
            return results.getStatusCode() == HttpStatus.OK;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ClusterIP 로 되어있는 KubernetesPod 에게 Request 를 요청하고, 결과값 Return.
     * @param urls service Urls
     * @param headers Header
     * @param body Request Body
     * @param method HttpMethod
     * @return ResponseEntity<Object>
     */
    public ResponseEntity<Object> sendRequest(UriComponents urls, HttpHeaders headers, ConcurrentHashMap<String, Object> body, HttpMethod method){
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(5000);//Set timeouts
        factory.setReadTimeout(4000);

        HttpEntity<Object> entity = new HttpEntity<>(body,headers);
        try {
            RestTemplate restTemplate = new RestTemplate(factory);
            return restTemplate.exchange(
                    urls.toString(),
                    method,
                    entity,
                    Object.class
            );
        //FIXME Exception Handling required
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

//    public Boolean postRunnableSource(HttpHeaders headers, MultiValueMap<String, String> body, String svcName){
//        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body,headers);
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<Object> results =
//                restTemplate.exchange(
//                        svcName,
//                        HttpMethod.POST,
//                        entity,
//                        Object.class
//                );
//        return results.getBody() == HttpStatus.OK;
//    }//공용 함수로 인해서 삭제.

    public List<ContainerStatus> getPodStatus(String namespace, String TOKEN){
        Pod pods = this.kubernetesClient.pods()
                .inNamespace(namespace)
                .list().getItems()
                .stream().filter(pod-> pod.getMetadata().getName().contains(TOKEN.toLowerCase()))
                .findAny().get();
        return pods.getStatus().getContainerStatuses();
    }

    public Service constructService(String namespace, String TOKEN){
        //Token Value 를 갖고, Service 이름을 저장.
        String svcName = "training-container-svc-"+TOKEN.toLowerCase();
        Service service = new ServiceBuilder()
                .withNewMetadata()
                    .withName(svcName)
                    .withNamespace(namespace)
                    .addToLabels("app","tw-"+TOKEN.toLowerCase())
                .endMetadata()
                .withNewSpec()
                    .withType("ClusterIP")
                .addToSelector("app","tw-"+TOKEN.toLowerCase())
                    .withPorts()
                        .withExternalName("worker-port")
                        .addNewPort()
                            .withPort(8888)
                            .withNewTargetPort(8888)
                        .endPort()
                .endSpec().build();

        log.info("Serivce : {}",service);
        return service;
    }

    public void runService(@NotNull Service service, String namespace){
        log.info("Apply Service :{}",service.getMetadata().getName());
        Service apply = this.kubernetesClient.services().inNamespace(namespace).createOrReplace(service);
    }

    public Deployment constructDeploy(String userId, String datasetName, String namespace, String imageName, String tag, String X_TOKEN, String TOKEN) {
        String workerName = "tw-"+TOKEN.toLowerCase();
        String imagePullSecret = "docker-pull-secret";
        String imagePullPolicy = "IfNotPresent";
        String databaseUrls = this.environment.getProperty("spring.datasource.url");
        // IP:PORT/TABLE_NAME 정규식.
        String regex = "(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])([:][0-9][0-9][0-9][0-9][0-9]?)\\/([A-Z]|[a-z])+";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(databaseUrls);
        String urls = null;
        while(matcher.find()){
            urls = matcher.group();
        }
        String DatabaseURL = String.format(
                "mysql+mysqldb://%s:%s@%s",
                this.environment.getProperty("spring.datasource.username"),
                this.environment.getProperty("spring.datasource.password"),
                urls
        );
        log.info("Urls : {}, {}",urls, DatabaseURL);

        Deployment deployment = new DeploymentBuilder()
                .withNewMetadata()
                    .withName(workerName+"-deploy")
                    .withNamespace(namespace)
                    .addToLabels("app",workerName)
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
                                .addNewEnv().withName("USER_ID").withValue(userId).endEnv()
                                .addNewEnv().withName("DATASET_NAME").withValue(datasetName).endEnv()
                                .addNewEnv().withName("X_TOKEN").withValue(X_TOKEN).endEnv()
                                .addNewEnv().withName("TOKEN").withValue(TOKEN).endEnv()
                            .endContainer()
                        .endSpec()
                    .endTemplate()
                .endSpec()
        .build();
//        log.info("deployment : {}",deployment.toString());
        return deployment;
    }

    public void runDeployment(@NotNull Deployment deployment, String namespace){
        log.info("Apply Deployment :{}",deployment.getMetadata().getName());
        Deployment apply = this.kubernetesClient.apps().deployments().inNamespace(namespace).createOrReplace(deployment);
    }
}
