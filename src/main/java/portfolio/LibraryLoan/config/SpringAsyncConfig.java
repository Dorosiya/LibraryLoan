package portfolio.LibraryLoan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class SpringAsyncConfig {

    private int CORE_POOL_SIZE = 2;
    private int MAX_POOL_SIZE = 5;
    private int QUEUE_CAPACITY = 100;

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);    // 핵심 스레드 풀 크기
        executor.setMaxPoolSize(MAX_POOL_SIZE);     // 최대 스레드 풀 크기
        executor.setQueueCapacity(QUEUE_CAPACITY); // 큐 용량
        executor.setThreadNamePrefix("AsyncExecutorThread-"); // 스레드 이름 접두어
        executor.initialize();
        return executor;
    }

}
