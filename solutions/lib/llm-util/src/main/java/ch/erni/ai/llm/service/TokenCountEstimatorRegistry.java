package ch.erni.ai.llm.service;
import ch.erni.ai.llm.config.TokenizerConfig;
import dev.langchain4j.model.TokenCountEstimator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenCountEstimatorRegistry {
    private final TokenizerConfig tokenizerConfig;

    public TokenCountEstimator getTokenCountEstimator(String model) {
        var tokenizer = tokenizerConfig.getTokenizer().stream().filter(t -> t.getName().equals(model)).findFirst();
        if (tokenizer.isPresent()) {
            return LoadingFromHuggingFaceTokenEstimator.get(tokenizer.get().getPath());
        } else {
            throw new IllegalArgumentException("no tokenizer for model configured. Found tokenizers: " + tokenizerConfig.getTokenizer());
        }
    }
}
