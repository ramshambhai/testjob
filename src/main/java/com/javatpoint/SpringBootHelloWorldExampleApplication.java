package com.javatpoint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.javatpoint.scheduler.schedulerConfig;

@SpringBootApplication
@Configuration
@PropertySource("classpath:application.properties")
@EnableScheduling
public class SpringBootHelloWorldExampleApplication
	{
	public static void main(String[] args) 
		{
			SpringApplication.run(SpringBootHelloWorldExampleApplication.class, args);
		}
	@Bean
	public static PropertySourcesPlaceholderConfigurer property()
		{
			return new  PropertySourcesPlaceholderConfigurer();
		}
	@Bean
    public schedulerConfig bean() {
        return new schedulerConfig();
    }
	}