package com.sketch.brain.backend.configurations;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Slf4j
@RequiredArgsConstructor
@Profile({"sketch-dev","local"})
@PropertySource("classpath:bootstrap.yaml")
@Configuration
public class KubernetesConfigurations {

    @Bean
    public KubernetesClient mockLocalKubernetes(){
        // Enable CRUD Modes.
        log.info("Local Kubernetes, Mocking Kubernetes Server enable");
        KubernetesServer server = new KubernetesServer(false, true);
        // Run Kubernetes mock server.
        server.before();
        return server.getClient();
    }

}
