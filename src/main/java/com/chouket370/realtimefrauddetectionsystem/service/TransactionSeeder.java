package com.chouket370.realtimefrauddetectionsystem.service;


import com.chouket370.realtimefrauddetectionsystem.Repository.CustomerRepository;
import com.chouket370.realtimefrauddetectionsystem.Repository.TransactionRepository;
import com.chouket370.realtimefrauddetectionsystem.enums.Category;
import com.chouket370.realtimefrauddetectionsystem.enums.Currency;
import com.chouket370.realtimefrauddetectionsystem.enums.Merchant;
import com.chouket370.realtimefrauddetectionsystem.model.Customer;
import com.chouket370.realtimefrauddetectionsystem.model.Transaction;
import com.mongodb.client.MongoCollection;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.bson.Document;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.chouket370.realtimefrauddetectionsystem.enums.Merchant.getRandomMerchant;

@Service
@AllArgsConstructor
public class TransactionSeeder {
    private final EmbeddingGenerator embeddingGenerator;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;
    private final MongoCollection<Document> transactionsCollection;
    private final TransactionChangeStreamListener transactionChangeStreamListener;


    @PostConstruct
    public void seedTransactions() {

        Logger logger = LoggerFactory.getLogger(TransactionSeeder.class);

        if (transactionsCollection.countDocuments() > 0) {
            logger.info("Transactions already seeded.");
            return;
        }

        List<Customer> customers = customerRepository.findAll();
        if (customers.isEmpty()) {
            System.err.println("No customers in DB");
            return;
        }
        Random random = new Random();
        List<Transaction> transactions = new ArrayList<>();


        for (Customer customer : customers) {
            for(int i=0;i<=10;i++){
                boolean isSuspicious = random.nextDouble() < 0.1;
                double amount = isSuspicious ? customer.getMeanSpending() * (2 + random.nextDouble())
                        : customer.getMeanSpending() * (0.5 + random.nextDouble());
                Category category = isSuspicious ? customer.getUnfrequentCategory() : customer.getFrequentCategory();
                Merchant merchant = getRandomMerchant(category);
                Currency currency = isSuspicious ? customer.getRandomSuspiciousCurrency()
                        : customer.getPreferredCurrency();
                Transaction transaction= new Transaction(
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
                String embeddingText=transaction.generateEmbeddingText();
                float[] embedding= embeddingGenerator.getEmbedding(embeddingText);
                transaction.setEmbedding(embedding);
                transactions.add(transaction);


            }
        }
        transactionRepository.saveAll(transactions);
        logger.info("Seeded transactions.");

        transactionChangeStreamListener.startListening();
        logger.info("Change Stream Listener Started.");

    }

}
