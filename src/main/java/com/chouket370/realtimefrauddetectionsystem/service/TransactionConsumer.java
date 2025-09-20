package com.chouket370.realtimefrauddetectionsystem.service;

import com.chouket370.realtimefrauddetectionsystem.Repository.TransactionRepository;
import com.chouket370.realtimefrauddetectionsystem.model.Transaction;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TransactionConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TransactionConsumer.class);
    private final TransactionRepository transactionRepository;

    @KafkaListener(topics = "transactions", groupId = "fraud-detection", containerFactory = "kafkaListenerContainerFactory")
    public void consumeTransaction(Transaction transaction) {
        try {
            logger.info(" KAFKA CONSUMER: Received transaction: {}", transaction.getTransactionId());
            logger.info("KAFKA CONSUMER: Transaction details - User: {}, Amount: {}, Currency: {}",
                    transaction.getUserId(), transaction.getAmount(), transaction.getCurrency());

            Transaction savedTransaction = transactionRepository.save(transaction);
            logger.info("KAFKA CONSUMER: Transaction saved to MongoDB: {}", savedTransaction.getId());

        } catch (Exception e) {
            logger.error(" KAFKA CONSUMER: Failed to save transaction {}: {}",
                    transaction.getTransactionId(), e.getMessage(), e);
        }
    }
}