package com.urlshortener.config;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import jakarta.annotation.PostConstruct;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void initIndexes() {
        MongoCollection<Document> collection = mongoTemplate.getCollection("urls");

        // Crear índice en longUrl para optimizar búsquedas de reutilización
        collection.createIndex(
            Indexes.ascending("longUrl"),
            new IndexOptions().name("idx_long_url")
        );

        System.out.println("✓ Índice creado en campo 'longUrl' para optimización de búsquedas");
    }
}

