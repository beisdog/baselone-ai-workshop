package ch.erni.ai.basic;

import ch.erni.ai.demo.market.MarketDataService;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.AiServices;

/**
 * Finish the tool call.
 */
public class ChatWithMarketTool extends AbstractChat{

    private final ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

    @Override
    public String chat(String userInput) {
        String finnhubApiKey = "d3bm1q9r01qqg7bv6acgd3bm1q9r01qqg7bv6ad0";
        var tool = new GetStockQuoteTool(finnhubApiKey);//TODO replace with real tool for reading marked data (see
        return AiServices.builder(BasicAssistant.class)
                .chatModel(createChatModel())
                .chatMemory(this.memory)
                .tools(tool)
                .build().chat(userInput);
    }

    public static class GetStockQuoteTool {
        private MarketDataService service;

        public GetStockQuoteTool(String apiKey) {
            this.service = new MarketDataService(apiKey);
        }

        /**
         * Get real-time stock quote with current price, change, and trading range
         * @param symbol - stock ticker symbol
         */
        public String getStockQuote(/* stock ticker symbol*/ String symbol) {
            return service.getStockQuote(symbol);
        }
    }
}
