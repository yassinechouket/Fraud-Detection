package com.chouket370.realtimefrauddetectionsystem.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.search.VectorSearchOptions;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.search.SearchPath.fieldPath;

@Service
public class TransactionVectorSearchService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionVectorSearchService.class);

    private final MongoCollection<Document> transactionCollection;
    private static final String VECTOR_INDEX_NAME = "vector_index";
    private static final int SEARCH_LIMIT = 5;
    private static final int NUM_CANDIDATES = 50;

    public TransactionVectorSearchService(MongoCollection<Document> transactionCollection) {
        this.transactionCollection = transactionCollection;
    }

    public void evaluateTransactionFraud(Document transactionDoc){
        String transactionId = transactionDoc.getString("transactionId");
        String userId = transactionDoc.getString("userId");
        List<Double> embedding = transactionDoc.getList("embedding", Double.class);

        logger.info("VECTOR SEARCH: Evaluating transaction {} for user {}", transactionId, userId);

        try {
            List<Document> similarTransactions = findSimilarTransactions(embedding, userId);
            logger.info("VECTOR SEARCH: Found {} similar transactions", similarTransactions.size());

            boolean isFraud = similarTransactions.isEmpty() ||
                    similarTransactions.stream().anyMatch(doc -> doc.getBoolean("isFraud", false));

            if (isFraud) {
                markTransactionAsFraud(transactionId);
                logger.info("FRAUD DETECTED: Transaction {} marked as fraudulent!", transactionId);
            } else {
                logger.info("VECTOR SEARCH: Transaction {} appears legitimate", transactionId);
            }

        } catch (Exception e) {
            logger.error("VECTOR SEARCH: Error during fraud evaluation for {}: {}", transactionId, e.getMessage(), e);
        }
    }

    List<Document> findSimilarTransactions(List<Double> embedding, String userId) {
        try {
            Bson vectorSearch = Aggregates.vectorSearch(
                    fieldPath("embedding"),
                    embedding,
                    VECTOR_INDEX_NAME,
                    SEARCH_LIMIT,
                    VectorSearchOptions.approximateVectorSearchOptions(NUM_CANDIDATES)
            );

            Bson matchUser = Aggregates.match(Filters.eq("userId", userId));

            return transactionCollection.aggregate(Arrays.asList(vectorSearch, matchUser))
                    .into(new ArrayList<>());
        } catch (Exception e) {
            logger.error("VECTOR SEARCH: Error finding similar transactions: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    private void markTransactionAsFraud(String transactionId) {
        try {
            transactionCollection.updateOne(
                    Filters.eq("transactionId", transactionId),
                    Updates.set("isFraud", true)
            );
            logger.info("FRAUD MARKED: Transaction {} updated as fraud in database", transactionId);
        } catch (Exception e) {
            logger.error("FRAUD MARKING: Failed to mark transaction {} as fraud: {}", transactionId, e.getMessage(), e);
        }
    }
}