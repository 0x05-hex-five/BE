package hexfive.ismedi.global.config;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class JacksonConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer(){
        return builder -> {
            builder.timeZone(TimeZone.getTimeZone("Asia/Seoul")); // KST
            builder.simpleDateFormat("yyyy-MM-dd");                   // 날짜 포맷
        };
    }
}
