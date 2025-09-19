package ch.erni.ai.demo.rag.config;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pinecone.PineconeEmbeddingStore;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "erni.vector-store.pinecone")
public class PineconeConfig {
    private String apiKey;
    private String index;
    private String namespace;

    public EmbeddingStore<TextSegment> createEmbeddingStore(String table) {
        EmbeddingStore<TextSegment> embeddingStore = PineconeEmbeddingStore.builder()
                .apiKey(this.getApiKey())
                .index(this.getIndex())
                .nameSpace(this.getNamespace() + "_" + table)
                .build();
        return embeddingStore;
    };
}
