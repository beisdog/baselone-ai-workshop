package ch.erni.ai.demo.tool.market;

import ch.erni.ai.demo.market.MarketDataService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

/**
 * Market Data Tool - Provides real market data from Finnhub
 */
public class MarketDataTool {
    private final MarketDataService marketDataService;

    public MarketDataTool(String finnhubApiKey) {
        this.marketDataService = new MarketDataService(finnhubApiKey);
    }

    @Tool("Get real-time stock quote with current price, change, and trading range")
    public String getStockQuote(@P("stock ticker symbol") String symbol) {
        return marketDataService.getStockQuote(symbol);
    }

    @Tool("Get financial metrics including P/E ratio, ROE, 52-week high/low, and valuation assessment")
    public String getFinancialMetrics(@P("stock ticker symbol") String symbol) {
        return marketDataService.getFinancialMetrics(symbol);
    }

    @Tool("Get recent company news and market sentiment for the past 7 days")
    public String getRecentNews(@P("stock ticker symbol") String symbol) {
        return this.marketDataService.getRecentNews(symbol);
    }

    @Tool("Get analyst recommendations and trends for a stock")
    public String getAnalystRecommendations(@P("stock ticker symbol") String symbol) {
        return this.marketDataService.getAnalystRecommendations(symbol);
    }

    @Tool("Calculate position size based on account balance, risk percentage, entry price, and stop loss")
    public String calculatePositionSize(@P("stock symbol") String symbol,
                                        @P("account balance in dollars") double accountBalance,
                                        @P("risk percentage (e.g., 2 for 2%)") double riskPercent,
                                        @P("entry price") double entryPrice,
                                        @P("stop loss price") double stopLoss) {
        return this.marketDataService.calculatePositionSize(symbol, accountBalance, riskPercent, entryPrice, stopLoss);
    }
}
