package br.com.vassoler.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;

class KafkaService<T> implements Closeable {

    private final KafkaConsumer<String, T> consumer;
    private final ConsumerFunction parse;

    KafkaService(String group, String topic, ConsumerFunction parse, Class<T> type, Map<String, String> properties) {
        this(group, parse, type, properties);
        consumer.subscribe(Collections.singletonList(topic));
    }

    KafkaService(String group, Pattern topics, ConsumerFunction parse, Class<T> type, Map<String, String> properties) {
        this(group, parse, type, properties);
        consumer.subscribe(topics);
    }

    private KafkaService(String group, ConsumerFunction parse, Class<T> type, Map<String, String> properties) {
        this.parse = parse;
        this.consumer = new KafkaConsumer<>(this.getProperties(type, group, properties));
    }

    void run(){
        while (true) {
            var records = consumer.poll(Duration.ofMillis(100));
            if (!records.isEmpty()) {
                System.out.println("Encontrado " + records.count() + " registros");
                for(var record : records){
                    parse.consume(record);
                }
            }
        }
    }

    private Properties getProperties(Class<T> type, String group, Map<String, String> overrideProperties) {
        var properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
        //o nome do grupo é livre, mas não deve ser repetido entre os serviços que consomem as mensagens
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG,group);
        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        //Define o número de mensagens consumidas por período - Visa evitar o problema de rebalanceamento do Kafka
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        properties.setProperty(GsonDeserializer.TYPE_CONFIG, type.getName());
        properties.putAll(overrideProperties);
        return  properties;
    }

    @Override
    public void close(){
        this.consumer.close();
    }
}
