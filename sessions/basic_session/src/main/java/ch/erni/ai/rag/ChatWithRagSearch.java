package ch.erni.ai.rag;

import ch.erni.ai.basic.AbstractChat;
import ch.erni.ai.basic.ChatStarter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;

import java.util.Map;

public class ChatWithRagSearch extends AbstractChat {
    private ChatModel chatModel;
    private EmbeddingModel embeddingModel;

    private PgVectorEmbeddingStore pg;

    public ChatWithRagSearch() {
        this.chatModel = createChatModel();

        this.embeddingModel = OpenAiEmbeddingModel
                .builder()
                .modelName("text-embedding-nomic-embed-text-v2")
                .baseUrl("http://localhost:1234/v1")
                .apiKey("dummy")
                .httpClientBuilder(getHttp1ClientBuilder())
                .build();

        this.pg = PgVectorEmbeddingStore.builder()
                .createTable(true)
                .table("profile_full2")
                .dimension(embeddingModel.dimension())
                .host("localhost")
                .port(8432)
                .user("baselone")
                .password("baselone")
                .database("baselone")
                .build();

    }

    public String chat(String userInput) {
        //Query vectorstore
        EmbeddingSearchResult<TextSegment> searchResult = null;
        // assemble the prompt
        var template = PromptTemplate.from("""
                Beantworte die Benutzerfrage anhand einer Liste von CVs.
                
                Hier ist die Liste der CV:
                {{cv_list}}
                
                Hier die Frage: {{question}}
                """);
        Prompt prompt = template.apply(
                Map.of("cv_list", asString(searchResult), "question",userInput)
        );

        String promptText = prompt.text();
        return chatModel.chat(promptText);
    }

    private String asString(EmbeddingSearchResult<TextSegment> searchResult) {
        StringBuilder builder = new StringBuilder();
        for(var match: searchResult.matches()) {
            String text = match.embedded().text();
            Metadata meta = match.embedded().metadata();
            builder.append("<CV id=").append(meta.getString("id"))
                    .append("name=").append(meta.getString("name"))
                    .append(">\n")
                    .append(text)
                    .append("\n</CV>\n");
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        ChatStarter.start(new ChatWithRagSearch());
    }
}
