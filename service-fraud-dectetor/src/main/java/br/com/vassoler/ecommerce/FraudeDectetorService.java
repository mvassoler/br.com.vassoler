package br.com.vassoler.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.HashMap;

public class FraudeDectetorService {

    public static void main(String[] args){
        var fraudeDectetorService = new FraudeDectetorService();
        try (var service = new KafkaService(FraudeDectetorService.class.getSimpleName(), "ECOMMERCE_NEW_ORDER",
                fraudeDectetorService::parse, Order.class, new HashMap<String,String>())){
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Order> record){
        System.out.println("Lendo Mensagens de Fraude:");
        System.out.println("Chave: " + record.key());
        System.out.println("Tópico: " + record.topic());
        System.out.println("Valor: " + record.value());
        System.out.println("Partição: " + record.partition());
        System.out.println("Offset" + record.offset());
        try {
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        var order = record.value();
        System.out.println("Registro.: " + order.toString());
        System.out.println("Ordem processada");

    }

}
