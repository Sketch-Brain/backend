package com.sketch.brain.backend.aggregate.manager.domain;

import com.sketch.brain.backend.aggregate.manager.dto.TokenDto;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import org.springframework.util.MultiValueMap;

import java.util.LinkedHashMap;

public interface Container {

    /**
     * 학습을 실행시킨다. 실제 Container 를 Running 한다.
     */
    void run(Deployment deployment,Service service);

    /**
     *
     */
    void getContainerInfo(String namespace);

    /**
     * Experiment 의 학습을 시작시킨다.
     * @param experimentId experimentId ( UUID )
     * @param namespace kubernetes worker's Namespace
     * @param X_TOKEN Header Tokens
     * @param TOKEN Tokens
     */
    LinkedHashMap<String, Object> startExperiment(byte[] experimentId, String namespace, String X_TOKEN, String TOKEN,String userId);

    TokenDto writeDB(byte[] experimentId, String userId, String dataName, String modelName);

    /**
     * Token 값을 조회. experimentId ( UUID ) 로 조회.
     * @param experimentId UUID
     * @param userId userId
     * @return TokenDto Objects
     */
    TokenDto getExperimentTokens(byte[] experimentId, String userId);

    String updateStatus(byte[] experimentId, String status);

    String updatePythonSource(byte[] experimentId, String status);

    Deployment constructK8sContainer(String userId, String datasetName, String namespace, String tag, String imageName, String X_TOKEN, String TOKEN);

    Service constructK8sService(String namespace, String TOKEN);

    /**
     * Container 가 준비상태에 도달했는지 검사한다.
     * @param namespace
     * @param TOKEN
     * @return True( isReady ), False ( notReady )
     */
    Boolean isContainerReady(String namespace, String TOKEN);

    /**
     * FastAPI Rest server 의 HealthCheck 를 통과하는지 검사한다.
     * @param experimentId : UUID
     * @param svcName : Service Name
     * @param X_TOKEN
     * @param TOKEN
     * @return True( isReady ), False ( notReady )
     */
    Boolean isRestServerReady(byte[] experimentId, String svcName,String X_TOKEN, String TOKEN);

    /**
     * Training Container 에 Python Source 를 inject한다.
     * @param runnable : Python Source
     * @param svcName : Service Name
     * @param X_TOKEN
     * @param TOKEN
     * @return
     */
    Boolean injectRunnableSource(byte[] experimentId, String runnable,String svcName, String X_TOKEN, String TOKEN);
}
