package ch.erni.ai.rag;

import ch.erni.ai.basic.AbstractChat;
import ch.erni.ai.demo.cv.config.CVConfigProps;
import ch.erni.ai.demo.cv.service.CVService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;

import java.util.Map;

public class IngestCVApp {

    public static void main(String[] args) {


        EmbeddingModel embeddingModel = OpenAiEmbeddingModel
                .builder()
                .modelName("text-embedding-nomic-embed-text-v2")
                .baseUrl("http://localhost:1234/v1")
                .httpClientBuilder(AbstractChat.getHttp1ClientBuilder())
                .build();

        PgVectorEmbeddingStore pg = PgVectorEmbeddingStore.builder()
                .createTable(true)
                .table("profile_full2")
                .dimension(embeddingModel.dimension())
                .host("localhost")
                .port(8432)
                .user("baselone")
                .password("baselone")
                .database("baselone")
                .dropTableFirst(true)
                .build();

        var props = new CVConfigProps();
        props.setSourceDir("./data/cv_data");
        var objectMapper = new ObjectMapper()
                .enable(
                        SerializationFeature.INDENT_OUTPUT
                )
                .disable(
                        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
                )
                ;

        CVService cvService = new CVService(props, objectMapper);
        var profiles = cvService.getProfiles();

        for (var profile : profiles) {
            String md = cvService.getProfileAsMarkdown(profile.id);
            System.out.println("ingesting " + profile.name + " ...");
            ingest(
                    embeddingModel,
                    pg,
                    md,
                    Metadata.from(Map.of("id", profile.id, "name", profile.name))
            );
        }
    }

    public static String ingest(EmbeddingModel embeddingModel,
                                EmbeddingStore<TextSegment> embeddingStore,
                                String md, Metadata metadata) {
        // create embedding
        // add to vectorstore
        // add and return the id
        return "the id";
    }
}
