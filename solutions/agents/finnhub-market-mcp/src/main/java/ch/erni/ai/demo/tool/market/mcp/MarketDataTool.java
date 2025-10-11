package ch.erni.ai.demo.tool.market.mcp;


import ch.erni.ai.demo.market.MarketDataService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * Market Data Tool - Provides real market data from Finnhub
 */
public class MarketDataTool {

    private final MarketDataService marketDataService;

    public MarketDataTool(String finnhubApiKey) {
        this.marketDataService = new MarketDataService(finnhubApiKey);
    }

    @Tool(description = "Get real-time stock quote with current price, change, and trading range")
    public String getStockQuote(@ToolParam(description = "stock ticker symbol", required = true) String symbol) {
        return marketDataService.getStockQuote(symbol);
    }

    @Tool(description = "Get financial metrics including P/E ratio, ROE, 52-week high/low, and valuation assessment")
    public String getFinancialMetrics(@ToolParam(description = "stock ticker symbol") String symbol) {
        return marketDataService.getFinancialMetrics(symbol);
    }

    @Tool(description = "Get recent company news and market sentiment for the past 7 days")
    public String getRecentNews(@ToolParam(description = "stock ticker symbol") String symbol) {
        return this.marketDataService.getRecentNews(symbol);
    }

    @Tool(description = "Get analyst recommendations and trends for a stock")
    public String getAnalystRecommendations(@ToolParam(description = "stock ticker symbol") String symbol) {
        return this.marketDataService.getAnalystRecommendations(symbol);
    }

    @Tool(description = "Calculate position size based on account balance, risk percentage, entry price, and stop loss")
    public String calculatePositionSize(@ToolParam(description = "stock symbol") String symbol,
                                        @ToolParam(description = "account balance in dollars") double accountBalance,
                                        @ToolParam(description = "risk percentage (e.g., 2 for 2%)") double riskPercent,
                                        @ToolParam(description = "entry price") double entryPrice,
                                        @ToolParam(description = "stop loss price") double stopLoss) {
        return this.marketDataService.calculatePositionSize(symbol, accountBalance, riskPercent, entryPrice, stopLoss);
    }
}
