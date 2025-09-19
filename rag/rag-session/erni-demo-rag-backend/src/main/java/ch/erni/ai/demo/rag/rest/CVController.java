package ch.erni.ai.demo.rag.rest;

import ch.erni.ai.demo.rag.config.PineconeConfig;
import ch.erni.ai.demo.rag.model.Profile;
import ch.erni.ai.demo.rag.rest.CVIngestorController.Namespace;
import ch.erni.ai.demo.rag.service.CVService;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/cv")

@RequiredArgsConstructor
public class CVController {

    @Value("${erni.resources.dir}")
    private String resourcesDir;
    private final EmbeddingModel embeddingModel;
    private final PineconeConfig pineconeConfig;
    private final ChatLanguageModel chatLanguageModel;
    private final CVService cvService;

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class TextSegmentResult {
        public String text;
        public Map<String, Object> metadata;
        public String namespace;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchInput {
        public String question;
        public int maxResults;
    }


    @GetMapping("/profiles")
    public List<CVService.ProfileShort> getProfiles() {
        return cvService.getProfiles();
    }

    @SneakyThrows
    @GetMapping("/profiles/{id}")
    public Profile getProfile(@PathVariable("id") String id) {
        return cvService.getProfile(id);
    }

    @SneakyThrows
    @GetMapping("/profiles/{id}/md")
    public String getProfileAsMarkdown(@PathVariable("id") String id) {
        return cvService.getProfileAsMarkdown(id);
    }

    @PostMapping("/profiles/vs/search/{namespace}")
    public List<TextSegmentResult> vectorSearch(@PathVariable("namespace") Namespace namespace, @RequestBody SearchInput searchInput) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @PostMapping("/ask/cv/{id}")
    public ChatLanguageModelController.Message askAboutCV(@PathVariable("id") String id, @RequestBody ChatLanguageModelController.AskInput input) throws URISyntaxException, IOException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private static void logPrompt(String userMessageText) {
        log.info("User Prompt: \n{}", userMessageText);
    }

    @PostMapping("/ask/cv-list/{namespace}")
    public ChatLanguageModelController.Message askAboutCVSearchResult(@PathVariable("namespace") Namespace namespace,
                                                                      @RequestBody SearchInput input) throws URISyntaxException, IOException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @NotNull
    private static String convertTextSegmentsToString(List<TextSegmentResult> textSegments) {
        return textSegments
                .stream()
                .map(textSegment ->
                        new StringBuilder("Profile ID:")
                                .append(textSegment.metadata.get("id")).append("\n")
                                .append("Name:").append(textSegment.metadata.get("name")).append("\n")
                                .append("CV:\n")
                                .append(textSegment.text).append("\n")
                                .append("---\n")
                                .toString()
                )
                .collect(Collectors.joining("\n"));
    }

    @PostMapping("/agent")
    public ChatLanguageModelController.Message agentAssistForCVs(@RequestBody ChatLanguageModelController.AskInput input) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
