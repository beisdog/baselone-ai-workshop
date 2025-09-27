package ch.erni.ai.llm.service;

import ai.djl.huggingface.tokenizers.Encoding;
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import dev.langchain4j.data.message.*;
import dev.langchain4j.model.TokenCountEstimator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Alternative implementation that can load the tokenizer file from huggingface directly.
 */
public class LoadingFromHuggingFaceTokenEstimator implements TokenCountEstimator {

    private static Map<String, LoadingFromHuggingFaceTokenEstimator> CACHE = new ConcurrentHashMap<>();
    private final HuggingFaceTokenizer tokenizer;

    public static LoadingFromHuggingFaceTokenEstimator get(String model) {
        if (!CACHE.containsKey(model) ){

            HuggingFaceTokenizer tokenizer = HuggingFaceTokenizer.newInstance(model);
            CACHE.put(model, new LoadingFromHuggingFaceTokenEstimator(tokenizer));
        }
        return CACHE.get(model);
    }

    public LoadingFromHuggingFaceTokenEstimator(HuggingFaceTokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public int estimateTokenCountInText(String text) {
        Encoding encoding = this.tokenizer.encode(text, false, true);
        return encoding.getTokens().length;
    }

    public int estimateTokenCountInMessage(ChatMessage message) {
        if (message instanceof SystemMessage systemMessage) {
            return this.estimateTokenCountInText(systemMessage.text());
        } else if (message instanceof UserMessage userMessage) {
            return this.estimateTokenCountInText(userMessage.singleText());
        } else if (message instanceof AiMessage aiMessage) {
            return aiMessage.text() == null ? 0 : this.estimateTokenCountInText(aiMessage.text());
        } else if (message instanceof ToolExecutionResultMessage toolExecutionResultMessage) {
            return this.estimateTokenCountInText(toolExecutionResultMessage.text());
        } else {
            throw new IllegalArgumentException("Unknown message type: " + message);
        }
    }

    public int estimateTokenCountInMessages(Iterable<ChatMessage> messages) {
        int tokens = 0;

        for(ChatMessage message : messages) {
            tokens += this.estimateTokenCountInMessage(message);
        }

        return tokens;
    }
}
