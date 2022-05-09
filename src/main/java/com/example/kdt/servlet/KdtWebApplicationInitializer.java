package com.example.kdt.servlet;

import com.example.kdt.customer.CustomerController;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.BeansException;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class KdtWebApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        AnnotationConfigWebApplicationContext rootApplicationContext = new AnnotationConfigWebApplicationContext();
        rootApplicationContext.register(RootConfig.class);
        ContextLoaderListener loaderListener = new ContextLoaderListener(rootApplicationContext);
        servletContext.addListener(loaderListener);


        AnnotationConfigWebApplicationContext webApplicationContext = new AnnotationConfigWebApplicationContext();
        webApplicationContext.register(ServletConfig.class);

        DispatcherServlet dispatcherServlet = new DispatcherServlet(webApplicationContext);

        ServletRegistration.Dynamic servletRegistration =
                servletContext.addServlet("dispatcherServlet", dispatcherServlet);
        servletRegistration.addMapping("/");
        servletRegistration.setLoadOnStartup(-1);
    }

    @EnableWebMvc
    @Configuration
    @ComponentScan(basePackages = {"com.example.kdt.customer"},
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = CustomerController.class),
    useDefaultFilters = false)
    static class ServletConfig implements WebMvcConfigurer, ApplicationContextAware {

        private ApplicationContext applicationContext;

        @Override
        public void configureViewResolvers(ViewResolverRegistry registry) {
            registry.jsp().viewNames("jsp/*");

            SpringResourceTemplateResolver springResourceTemplateResolver = new SpringResourceTemplateResolver();
            springResourceTemplateResolver.setApplicationContext(applicationContext);
            springResourceTemplateResolver.setPrefix("/WEB-INF/");
            springResourceTemplateResolver.setSuffix(".html");

            SpringTemplateEngine springTemplateEngine = new SpringTemplateEngine();
            springTemplateEngine.setTemplateResolver(springResourceTemplateResolver);

            ThymeleafViewResolver thymeleafViewResolver = new ThymeleafViewResolver();
            thymeleafViewResolver.setTemplateEngine(springTemplateEngine);
            thymeleafViewResolver.setOrder(1);
            thymeleafViewResolver.setViewNames(new String[]{"views/*"});

            registry.viewResolver(thymeleafViewResolver);
        }

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/resources/**")
                    .addResourceLocations("/resources/")
                    .setCachePeriod(60);
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }

        @Override
        public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
            MarshallingHttpMessageConverter marshallingHttpMessageConverter = new MarshallingHttpMessageConverter();
            XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
            marshallingHttpMessageConverter.setMarshaller(xStreamMarshaller);
            marshallingHttpMessageConverter.setUnmarshaller(xStreamMarshaller);
            converters.add(0, marshallingHttpMessageConverter);

            JavaTimeModule javaTimeModule = new JavaTimeModule();
            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME));
            Jackson2ObjectMapperBuilder modules = Jackson2ObjectMapperBuilder.json().modules(javaTimeModule);
            converters.add(1, new MappingJackson2HttpMessageConverter(modules.build()));
        }

    }

    @Configuration
    @ComponentScan(basePackages = "com.example.kdt.customer",
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = CustomerController.class))
    @EnableTransactionManagement
    static class RootConfig {

        @Bean
        public DataSource dataSource() {
            HikariDataSource dataSource = DataSourceBuilder.create()
                    .url("jdbc:mysql://localhost/order_mgmt?useUnicode=true&serverTimezone=UTC")
                    .username("test")
                    .password("test")
                    .type(HikariDataSource.class)
                    .build();

            return dataSource;
        }

        @Bean
        public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }

    }

}
