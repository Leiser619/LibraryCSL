package pl.csl.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient googleBooksWebClient() {
        return WebClient.builder()
                .baseUrl("https://www.googleapis.com/books/v1")
                .defaultHeader("User-Agent", "LibraryApp/1.0")
                .build();
    }

}
