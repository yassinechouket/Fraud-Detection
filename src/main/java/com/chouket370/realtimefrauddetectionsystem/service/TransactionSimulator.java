package com.chouket370.realtimefrauddetectionsystem.service;

import com.chouket370.realtimefrauddetectionsystem.enums.Category;
import com.chouket370.realtimefrauddetectionsystem.enums.Currency;
import com.chouket370.realtimefrauddetectionsystem.enums.Merchant;
import com.chouket370.realtimefrauddetectionsystem.model.Customer;
import com.chouket370.realtimefrauddetectionsystem.model.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.chouket370.realtimefrauddetectionsystem.enums.Merchant.getRandomMerchant;

@Component
@AllArgsConstructor
public class TransactionSimulator {
    @Autowired
    private TransactionProducer producer;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private final EmbeddingGenerator embeddingGenerator;


    private final Random random = new Random();


    @Scheduled(fixedRate = 7000)
    public void generateTransaction() throws JsonProcessingException {

        List<Customer> customers = mongoTemplate.findAll(Customer.class);

        if (customers.isEmpty()) {
            System.err.println("No customers in DB. Did you run CustomerSeeder?");
            return;
        }
        Customer customer = customers.get(random.nextInt(customers.size()));

        boolean isSuspicious=random.nextDouble()<0.1;
        double amount = isSuspicious ? customer.getMeanSpending() * (2 + random.nextDouble())
                : customer.getMeanSpending() * (0.5 + random.nextDouble());
                    /*If fraud → spend 2–3× more than usual.
                    If normal → spend 0.5–1.5× of usual.*/
        Category category=isSuspicious?customer.getUnfrequentCategory():customer.getFrequentCategory();
        Currency currency = isSuspicious ? customer.getRandomSuspiciousCurrency()
                : customer.getPreferredCurrency();
        Merchant merchant = getRandomMerchant(category);


        Transaction tx = new Transaction(
                null,
                UUID.randomUUID().toString(),
                customer.getUserId(),
                amount,
                currency,
                Instant.now(),
                merchant,
                category,
                false
        );
        String embeddingText = tx.generateEmbeddingText();
        tx.setEmbedding(embeddingGenerator.getEmbedding(embeddingText));

        producer.sendTransaction(tx);
    }
}
