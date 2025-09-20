package com.chouket370.realtimefrauddetectionsystem.service;


import com.chouket370.realtimefrauddetectionsystem.model.Transaction;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class TransactionProducer {

    private final KafkaTemplate<String, Transaction> kafkaTemplate;

    public static final Logger log = LoggerFactory.getLogger(TransactionProducer.class);
    private static final String TOPIC = "transactions";

    
    public void sendTransaction(Transaction transaction) {

        try{
            kafkaTemplate.send(TOPIC,transaction.getId(), transaction);
            log.info("Transaction sent successfully");
        }catch (Exception e){
            log.error("Failed to send transaction: {}", e.getMessage());
        }
    }



}
