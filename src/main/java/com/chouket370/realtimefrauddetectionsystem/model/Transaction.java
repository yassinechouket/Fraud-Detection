package com.chouket370.realtimefrauddetectionsystem.model;


import com.chouket370.realtimefrauddetectionsystem.enums.Category;
import com.chouket370.realtimefrauddetectionsystem.enums.Currency;
import com.chouket370.realtimefrauddetectionsystem.enums.Merchant;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;



@NoArgsConstructor
@Data
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;
    @JsonProperty("transaction_id")
    private String transactionId;
    @Indexed
    private String userId;
    private double amount;
    private Currency currency;
    private Instant timestamp;
    private Merchant merchant;
    private Category category;
    private boolean isFraud;
    private float[] embedding = {};

    public Transaction(String id, String transactionId, String userId,
                       double amount, Currency currency, Instant timestamp,
                       Merchant merchant, Category category, boolean isFraud) {
        this.id = id;
        this.transactionId = transactionId;
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.timestamp = timestamp;
        this.merchant = merchant;
        this.category = category;
        this.isFraud = isFraud;

    }

    public String generateEmbeddingText() {
        return userId + " " + amount + " " + currency + " " + merchant + " " + category;
    }

}
