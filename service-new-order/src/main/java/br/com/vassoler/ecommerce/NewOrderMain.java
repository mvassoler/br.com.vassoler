package br.com.vassoler.ecommerce;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {
    
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try(var orderDispatcher = new KafkaDispatcher<Order>()) {
            try(var emailDispatchr = new KafkaDispatcher<Email>()) {
                int i = 1;
                while (i < 5) {
                    var key = UUID.randomUUID().toString();
                    var order = new Order(UUID.randomUUID().toString(), UUID.randomUUID().toString(), new BigDecimal(5 + i));
                    //send(tópico, key, valor)
                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", key, order);
                    var email = new Email("Teste", "Teste do envio do e-mail");
                    emailDispatchr.send("ECOMMERCE_SEND_EMAI", key, email);
                    i++;
                }
            }
        }
    }

}
