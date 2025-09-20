package com.chouket370.realtimefrauddetectionsystem.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import lombok.AllArgsConstructor;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.bson.Document;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@AllArgsConstructor
public class TransactionChangeStreamListener {

    private final TransactionVectorSearchService vectorSearchService;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final MongoCollection<Document> transactionsCollection;
    private static final Logger logger = LoggerFactory.getLogger(TransactionChangeStreamListener.class);

    public void startListening() {
        logger.info("CHANGE STREAM: Starting MongoDB Change Stream Listener...");

        executorService.submit(() -> {
            logger.info("CHANGE STREAM: Change stream thread started, waiting for new transactions...");

            List<Bson> pipeline = List.of(Aggregates.match(Filters.eq("operationType", "insert")));

            try (MongoCursor<ChangeStreamDocument<Document>> cursor = transactionsCollection.watch(pipeline).iterator()) {
                logger.info("CHANGE STREAM: Successfully connected to MongoDB change stream");

                while (cursor.hasNext()) {
                    ChangeStreamDocument<Document> change = cursor.next();
                    Document transactionDoc = change.getFullDocument();

                    if (transactionDoc != null) {
                        logger.info("CHANGE STREAM: New transaction detected: {}", transactionDoc.getString("transactionId"));

                        List<Double> embedding = transactionDoc.getList("embedding", Double.class);
                        if (embedding != null) {
                            logger.info("CHANGE STREAM: Performing vector search for fraud detection");
                            vectorSearchService.evaluateTransactionFraud(transactionDoc);
                        } else {
                            logger.error("CHANGE STREAM: Warning - Transaction does not contain an embedding field.");
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("CHANGE STREAM: Error in change stream: {}", e.getMessage(), e);
            }
        });
    }
}