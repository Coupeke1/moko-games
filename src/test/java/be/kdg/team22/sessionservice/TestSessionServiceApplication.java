package be.kdg.team22.sessionservice;

import org.springframework.boot.SpringApplication;

public class TestSessionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(SessionServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
