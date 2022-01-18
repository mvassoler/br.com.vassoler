package br.com.vassoler.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Map;
import java.util.regex.Pattern;

public class LogService {

    public static void main(String[] args){
        var logService = new LogService();
        try(var service = new KafkaService(LogService.class.getSimpleName(), Pattern.compile("ECOMMERCE.*"), logService::parse, String.class,
                Map.of(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()))){
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, String> record){
        System.out.println("Logs");
        System.out.println("Log: " +  record.topic());
        System.out.println("Chave: " + record.key());
        System.out.println("Valor: " + record.value());
        System.out.println("Partição: " + record.partition());
        System.out.println("Offset" + record.offset());
    }
}
