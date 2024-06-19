package portfolio.LibraryLoan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import portfolio.LibraryLoan.util.SpringSecurityAuditorAware;

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
public class LibraryLoanApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryLoanApplication.class, args);
	}

	@Bean
	public AuditorAware<String> auditorProvider() {
		return new SpringSecurityAuditorAware();
	}

}
