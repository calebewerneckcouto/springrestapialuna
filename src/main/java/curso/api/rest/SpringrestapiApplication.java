package curso.api.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EntityScan(basePackages="curso.api.rest.model") /*Faz o scaneamento das Entidades*/
@ComponentScan(basePackages= {"curso.*"}) /*Ler os pacotes do projeto. Faz o mapeamento de todas as pastas*/
@EnableJpaRepositories(basePackages= {"curso.api.rest.repository"})
@EnableTransactionManagement /*Gerencia de transações no Banco de Dados*/
@EnableWebMvc /*Ativa recursos de MVC*/
@RestController
@EnableAutoConfiguration
@EnableCaching
public class SpringrestapiApplication implements WebMvcConfigurer{

	public static void main(String[] args) {
		SpringApplication.run(SpringrestapiApplication.class, args);
		
	}
	
	/* Mapeamento Global que refletem em todo o sistema */
	@Override
		public void addCorsMappings(CorsRegistry registry) {
			registry.addMapping("/usuario/**")
			.allowedMethods("*")
			.allowedOrigins("*"); /*Liberando o mapeamento de usuario para todas as origens*/
		}

}