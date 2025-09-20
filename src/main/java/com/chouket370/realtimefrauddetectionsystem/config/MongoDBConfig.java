package com.chouket370.realtimefrauddetectionsystem.config;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoDBConfig {
    private static final String TRANSACTIONS_COLLECTION = "transactions";

    @Bean
    public MongoCollection<Document> transactionsCollection(MongoClient mongoClient,
                                                            @Value("${spring.data.mongodb.database}") String dbName) {
        return mongoClient.getDatabase(dbName).getCollection(TRANSACTIONS_COLLECTION);
    }
}
