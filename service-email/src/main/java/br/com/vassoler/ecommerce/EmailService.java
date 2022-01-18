package br.com.vassoler.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.HashMap;

public class EmailService {

    public static void main(String[] args){
        var emailService = new EmailService();
        try(var service = new KafkaService(EmailService.class.getSimpleName(), "ECOMMERCE_SEND_EMAI", emailService::parse,
                Email.class, new HashMap<String,String>())) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Email> record){
        System.out.println("Recebendo mensagens do E-mail");
        System.out.println("Tópico: " + record.topic());
        System.out.println("Chave: " + record.key());
        System.out.println("Valor: " + record.value());
        System.out.println("Partição: " + record.partition());
        System.out.println("Offset" + record.offset());
    }

}
