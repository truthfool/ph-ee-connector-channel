package org.mifos.connector;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.mifos.connector.gsmastub.api.ApiOriginFilter;
import org.mifos.connector.gsmastub.configuration.CustomInstantDeserializer;
import org.mifos.connector.gsmastub.configuration.LocalDateConverter;
import org.mifos.connector.gsmastub.configuration.LocalDateTimeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZonedDateTime;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Channel connector APIs"))
public class ChannelConnectorApplication {

    @Bean
    public ObjectMapper objectMapper() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(Instant.class, CustomInstantDeserializer.INSTANT);
        javaTimeModule.addDeserializer(OffsetDateTime.class, CustomInstantDeserializer.OFFSET_DATE_TIME);
        javaTimeModule.addDeserializer(ZonedDateTime.class, CustomInstantDeserializer.ZONED_DATE_TIME);
        /*javaTimeModule.addSerializer(LocalDate.class, new CustomInstantDeserializer.LocalDateSerializer());*/
        javaTimeModule.addDeserializer(LocalDate.class, new CustomInstantDeserializer.LocalDateDeserializer());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(javaTimeModule);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Configuration
    static class CustomDateConfig implements WebMvcConfigurer {
        @Override
        public void addFormatters(FormatterRegistry registry) {
            registry.addConverter(new LocalDateConverter("yyyy-MM-dd"));
            registry.addConverter(new LocalDateTimeConverter("yyyy-MM-dd'T'HH:mm:ss.SSS"));
            //registry.addConverter(new LocalDateTimeConverter("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
        }
    }
    @Bean
    public FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {
        FilterRegistrationBean<ForwardedHeaderFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ForwardedHeaderFilter());
        registrationBean.setOrder(0); // Set the order to ensure this filter is executed first
        return registrationBean;
    }
    @Configuration
    public class FilterConfig {
        @Bean
        public FilterRegistrationBean<ApiOriginFilter> loggingFilter() {
            FilterRegistrationBean<ApiOriginFilter> registrationBean = new FilterRegistrationBean<>();
            registrationBean.setFilter(new ApiOriginFilter());
            registrationBean.addUrlPatterns("/*");
            registrationBean.setOrder(1);
            return registrationBean;
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(ChannelConnectorApplication.class, args);
    }
}
