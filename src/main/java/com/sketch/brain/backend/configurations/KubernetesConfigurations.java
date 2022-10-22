package com.sketch.brain.backend.configurations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile("sketch-prod")
@PropertySource("classpath:bootstrap.yaml")
public class KubernetesConfigurations {

}
