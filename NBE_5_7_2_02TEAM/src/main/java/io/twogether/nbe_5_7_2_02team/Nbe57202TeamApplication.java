package io.twogether.nbe_5_7_2_02team;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@ConfigurationPropertiesScan
public class Nbe57202TeamApplication {

    public static void main(String[] args) {
        SpringApplication.run(Nbe57202TeamApplication.class, args);
    }
}
