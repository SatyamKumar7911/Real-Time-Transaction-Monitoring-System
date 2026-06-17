package com.example.notification_service.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
/**
 * Configuration for running without Kafka (fallback mode)
 */
@Configuration
@Profile("no-kafka")
public class NoKafkaConfig {
    // This profile can be used to run the service without Kafka
    // Use: java -jar app.jar --spring.profiles.active=no-kafka
}