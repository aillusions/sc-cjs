package agd.ign.ignition;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @author aillusions
 */
@SpringBootApplication
@ComponentScan("agd.ign.ignition")
@Configuration
@EnableAsync
//@EntityScan("com.delivery.domain")
//@EnableJpaRepositories("com.delivery.repository")

public class WebServiceApplication extends SpringBootServletInitializer {

    @Bean(name = "threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        return new ThreadPoolTaskExecutor();
    }

}
