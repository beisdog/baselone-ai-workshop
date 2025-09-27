package ch.erni.ai.demo.cv.rag.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VectorStoreFactory {

    @Value("${application.vector_store.pgvector.host}")
    private String pghost;
    @Value("${application.vector_store.pgvector.port}")
    private int pgport;
    @Value("${application.vector_store.pgvector.user}")
    private String pguser;
    @Value("${application.vector_store.pgvector.password}")
    private String pgpassword;
    @Value("${application.vector_store.pgvector.database}")
    private String pgdatabase;



    public EmbeddingStore<TextSegment> createEmbeddingStore(String table, int dimensions) {
        EmbeddingStore<TextSegment> embeddingStore = PgVectorEmbeddingStore.builder()
                //.createTable(true)
                .host(this.pghost)
                .port(this.pgport)
                .database(this.pgdatabase)
                .user(this.pguser)
                .password(this.pgpassword)
                .dimension(dimensions)//768, 1024
                .table(table)
                .build();
        return embeddingStore;
    }

}
