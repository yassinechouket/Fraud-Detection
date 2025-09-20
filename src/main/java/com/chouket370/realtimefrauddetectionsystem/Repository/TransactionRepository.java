package com.chouket370.realtimefrauddetectionsystem.Repository;

import com.chouket370.realtimefrauddetectionsystem.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
}
