package com.sketch.brain.backend.configurations;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

import java.util.Objects;

@Slf4j
@Configuration
@RequiredArgsConstructor
@PropertySource("classpath:application.yaml")
public class MongodbConfigurations extends AbstractMongoClientConfiguration {
    /**
     * MongoDB Client 는 환경이 local 인지, 서버인지에 따라서 각각 다르게 설정한다.
     */
    private final Environment environment;

    @Override
    protected String getDatabaseName() {
        String database = null;
        database = environment.getProperty("spring.data.mongodb.database");
        if(database == null) database = environment.getProperty("spring.embedded.mongodb.database");
        log.info("database : {}",database);
        return database;
    }

    @SneakyThrows
    @Override
    @Bean
    public MongoClient mongoClient(){
        log.info("Crearte MongoClients");
        //SSL 과 같은 Setting 은 우선 Pass.
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(this.getConnectString())).build();
        return MongoClients.create(mongoClientSettings);
    }


    private String getConnectString(){

        String userInfo = "";
        String conParam = "";

        //Production && Dev 일 경우에는 User를 여기서 체크. Embedded 는 따로 User가 없음.
        if(Objects.equals(environment.getProperty("spring.config.activate.on-profile"), "sketch-dev") ||
                Objects.equals(environment.getProperty("spring.config.activate.on-profile"), "sketch-prod")){
            userInfo = environment.getProperty("spring.data.mongodb.username")
                    .concat(":").concat(Objects.requireNonNull(environment.getProperty("spring.data.mongodb.password"))).concat("@");
            conParam = "?authSource=testdb&authMechanism=SCRAM-SHA-1";
        }
        String mongoUrl = String.format(
                "mongodb://%s%s:%s/%s%s",
                userInfo,
                environment.getProperty("spring.data.mongodb.host"),
                environment.getProperty("spring.data.mongodb.port"),
                environment.getProperty("spring.data.mongodb.database"),
                conParam
        );
        log.info("Mongo Create URL : {}",mongoUrl);
        return mongoUrl;
    }
}
