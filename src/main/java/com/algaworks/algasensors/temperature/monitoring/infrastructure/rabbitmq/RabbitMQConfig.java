package com.algaworks.algasensors.temperature.monitoring.infrastructure.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
public class RabbitMQConfig {

    private  static final String PROCESS_TEMPERATURE = "temperature-monitoring.process-temperature.v1.";

    public static final String QUEUE_PROCESS_TEMPERATURE = PROCESS_TEMPERATURE +"q";

    public static final String DEAD_LETTER_QUEUE_PROCESS_TEMPERATURE = PROCESS_TEMPERATURE +"dlq";

    public static final String QUEUE_ALERTING = "temperature-monitoring.alerting.v1.q";

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper){
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory){
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public Queue queueProcessTemperature(){
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange","");
        args.put("x-dead-letter-routing-key",DEAD_LETTER_QUEUE_PROCESS_TEMPERATURE);
        return QueueBuilder.durable(QUEUE_PROCESS_TEMPERATURE).withArguments(args).build();
    }

    @Bean
    public Queue dealLetterQueueProcessTemperature(){
        return QueueBuilder.durable(DEAD_LETTER_QUEUE_PROCESS_TEMPERATURE).build();
    }

    @Bean
    public Queue queueAlertint(){
        return QueueBuilder.durable(QUEUE_ALERTING).build();
    }

    @Bean
    public Binding bindingAlerting(){
        return BindingBuilder.bind(queueAlertint()).to(exchange());
    }

    @Bean
    public Binding bindingProcessTemperaturing(){
        return BindingBuilder.bind(queueProcessTemperature()).to(exchange());
    }

    public FanoutExchange exchange(){
       return ExchangeBuilder.fanoutExchange( "temperature-processing.temperature-received.v1.e").build();
    }


}
