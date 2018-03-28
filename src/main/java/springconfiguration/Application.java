package springconfiguration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import service.StorageService;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
@EnableJpaRepositories({"biojobs", "converters"})
@EntityScan("biojobs")
@ComponentScan({"service*", "controller", "springconfiguration"})
@EnableAsync
public class Application extends SpringBootServletInitializer {

	private static Class applicationClass = Application.class;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(applicationClass);
    }

	@Bean
	public Executor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(2);
		executor.setQueueCapacity(5);
		executor.setThreadNamePrefix("LongRunnigJob-");
		executor.initialize();
		return executor;
	}
    
	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
            storageService.deleteAll();
            storageService.init();
		};
	}
}