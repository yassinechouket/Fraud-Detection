package com.chouket370.realtimefrauddetectionsystem.service;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.huggingface.HuggingFaceEmbeddingModel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmbeddingGenerator {

    private final EmbeddingModel embeddingModel;

    public EmbeddingGenerator(
            @Value("${huggingface.api.token}") String token,
            @Value("${huggingface.model.id}") String modelId) {

        this.embeddingModel = HuggingFaceEmbeddingModel.builder()
                .modelId(modelId)
                .accessToken(token)
                .build();
    }

    public float[] getEmbedding(String transaction) {
        return embeddingModel.embed(transaction).content().vector();
    }
}

