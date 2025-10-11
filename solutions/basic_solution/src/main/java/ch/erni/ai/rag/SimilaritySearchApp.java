package ch.erni.ai.rag;

import ch.erni.ai.basic.AbstractChat;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;

import java.util.stream.Collectors;

import static ch.erni.ai.basic.ChatStarter.start;

public class SimilaritySearchApp extends AbstractChat {

    EmbeddingModel embeddingModel = null;
    PgVectorEmbeddingStore pg = null;

    public SimilaritySearchApp() {
        this.embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        this.pg = PgVectorEmbeddingStore.builder()
                .createTable(true)
                .table("manual_embeddings")
                .dimension(embeddingModel.dimension())
                .host("localhost")
                .port(8432)
                .user("baselone")
                .password("baselone")
                .database("baselone")
                //.dropTableFirst(true)
                .build();
    }

    @Override
    public String chat(String userInput) {
        var queryEmbedding = embeddingModel.embed(userInput).content();

        var query = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(10)
                .build();
        var result = pg.search(query);
        return "Search results: \n"
        + result.matches().stream().map(
                it -> {
                    StringBuilder r =  new StringBuilder(
                            it.embedded().metadata().getString("file")
                    );
                    r.append(":").append(it.score()).append("\n")
                            .append(it.embedded().text().substring(0, Math.min(it.embedded().text().length(), 150)));
                    return r.toString();
                }
        ).collect(Collectors.joining("\n-----------------------\n"));
    }

    public static void main(String[] args) {
        start(new SimilaritySearchApp());
    }
}
