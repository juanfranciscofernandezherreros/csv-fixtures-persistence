package com.fernandez.fixtures.consumer;

import com.fernandez.fixtures.avro.FixtureKey;
import com.fernandez.fixtures.avro.FixtureValue;
import com.fernandez.fixtures.service.FixturesPersistenceService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ParsedFixturesConsumer {
    private final FixturesPersistenceService service;
    public ParsedFixturesConsumer(FixturesPersistenceService service){this.service=service;}

    @KafkaListener(topics="${app.kafka.topics.parsed-fixtures}",groupId="${spring.kafka.consumer.group-id}")
    public void listen(ConsumerRecord<FixtureKey,FixtureValue> record){
        service.persist(record.value());
    }
}
