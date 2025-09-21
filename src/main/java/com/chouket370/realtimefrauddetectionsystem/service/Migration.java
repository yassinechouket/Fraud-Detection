package com.chouket370.realtimefrauddetectionsystem.service;

import com.chouket370.realtimefrauddetectionsystem.model.Transaction;
import jakarta.annotation.PostConstruct;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.bson.Document;

import java.util.List;

@Component
public class Migration {

    private final MongoTemplate mongoTemplate;

    public Migration(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void runMigration() {
        // 1. Create indexes
        mongoTemplate.indexOps("transactions")
                .createIndex(new Index().on("userId", Sort.Direction.ASC));



        // Vector index for embeddings (MongoDB 7+ supports vector search)
        mongoTemplate.executeCommand(new Document(
                "createIndexes",
                "transactions"
        ).append("indexes", List.of(new Document()
                .append("name", "vector_index")
                .append("key", new Document("embedding", "vector"))
                .append("vectorOptions", new Document()
                        .append("dimensions", 5) // adjust to your embedding length
                        .append("similarity", "cosine")
                )
        )));

        // 2. Update old documents if needed
        mongoTemplate.updateMulti(
                new Query(),
                new Update().set("isFraud", false),
                "transactions"
        );


    }
}

