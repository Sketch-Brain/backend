package com.sketch.brain.backend.infrastructure.configurations;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@RequiredArgsConstructor
@PropertySource("classpath:application.yaml")
public class DatabaseConfigurations {

    private final Environment environment;

}
