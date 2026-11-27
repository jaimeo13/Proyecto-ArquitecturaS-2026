package com.vitallogix.backend.config;

import java.util.Locale;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * Configura la internacionalización (i18n) y resolución de idioma mediante encabezados HTTP.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
@Configuration
public class LocaleConfig implements WebMvcConfigurer {

    /**
     * Ejecuta la operación {@code localeResolver} del componente.
     * @return resultado de la operación.
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        // Use Locale.forLanguageTag("es") to avoid the deprecated warning.
        resolver.setDefaultLocale(Locale.forLanguageTag("es"));
        return resolver;
    }

    /**
     * Ejecuta la operación {@code addInterceptors} del componente.
     * @param registry parámetro de entrada de la operación.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang"); // Example: http://localhost:8080/api/products?lang=es
        registry.addInterceptor(interceptor);
    }
}
