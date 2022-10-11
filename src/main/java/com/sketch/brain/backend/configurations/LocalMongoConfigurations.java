package com.sketch.brain.backend.configurations;


import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile("local")
@EnableAutoConfiguration(exclude = {EmbeddedMongoAutoConfiguration.class})
@PropertySource("classpath:application.yaml")
public class LocalMongoConfigurations {

    private final Environment environment;

    /**
     * Local 로 실행시킬 경우는, Embedded Mongo 를 사용한다.
     * 서버 자원을 사용하지 않게끔 변경.
     * @throws IOException
     */
    @Bean
    public void connectEmbeddedMongodb() throws IOException {
        MongodExecutable mongodExecutable = null;
        log.info("Local - Embedded MongoDB Enable.");

        String host = environment.getProperty("spring.data.mongodb.host");
        String database = environment.getProperty("spring.data.mongodb.database");
        assert database != null;
        int port = Integer.parseInt(Objects.requireNonNull(environment.getProperty("spring.data.mongodb.port")));

        MongodConfig mongodConfig = MongodConfig
                .builder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(host,port, Network.localhostIsIPv6()))
                .build();
        MongodStarter starter = MongodStarter.getDefaultInstance();
        mongodExecutable = starter.prepare(mongodConfig);
        mongodExecutable.start();
    }

}
