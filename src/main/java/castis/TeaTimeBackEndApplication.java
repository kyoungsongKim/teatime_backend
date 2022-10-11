package castis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TeaTimeBackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeaTimeBackEndApplication.class, args);
    }

}
