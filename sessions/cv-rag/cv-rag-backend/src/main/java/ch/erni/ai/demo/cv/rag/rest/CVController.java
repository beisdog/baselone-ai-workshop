package ch.erni.ai.demo.cv.rag.rest;

import ch.erni.ai.demo.cv.model.Profile;
import ch.erni.ai.demo.cv.rag.config.VectorStoreFactory;
import ch.erni.ai.demo.cv.rag.rest.CVIngestorController.Namespace;
import ch.erni.ai.demo.cv.rag.rest.model.AskSimple;
import ch.erni.ai.demo.cv.rag.rest.model.Message;
import ch.erni.ai.demo.cv.rag.rest.model.SearchInput;
import ch.erni.ai.demo.cv.rag.rest.model.TextSegmentResult;
import ch.erni.ai.demo.cv.service.CVService;
import ch.erni.ai.llm.service.ModelRegistry;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/cv")

@RequiredArgsConstructor
public class CVController {

    @Value("${application.prompt-dir}")
    private String promptDir;
    private final ModelRegistry modelRegistry;
    private final VectorStoreFactory vectorStoreFactory;
    private final VectorSearchController vectorSearchController;
    private final CVService cvService;


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
        return vectorSearchController.vectorSearch(namespace, searchInput);
    }

    @PostMapping("/ask/cv/{id}")
    public Message askAboutCV(@PathVariable("id") String id, @RequestBody AskSimple input) throws URISyntaxException, IOException {
        throw new UnsupportedOperationException();
    }

    private static void logPrompt(String userMessageText) {
        log.info("User Prompt: \n{}", userMessageText);
    }

    @PostMapping("/ask/cv-list/{namespace}")
    public Message askAboutCVSearchResult(@PathVariable("namespace") Namespace namespace,
                                          @RequestBody SearchInput input) throws URISyntaxException, IOException {
        throw new UnsupportedOperationException();
    }


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

    @PostMapping("/agent/{question}")
    public Message agentAssistForCVs(@PathVariable String question) {
        throw new UnsupportedOperationException();
    }
}
