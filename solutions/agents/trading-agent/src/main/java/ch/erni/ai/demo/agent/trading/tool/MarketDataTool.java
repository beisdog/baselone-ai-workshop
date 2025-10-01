package ch.erni.ai.demo.agent.trading.tool;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.P;
import io.finnhub.api.apis.DefaultApi;
import io.finnhub.api.models.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Market Data Tool - Provides real market data from Finnhub
 */
public class MarketDataTool {
    private final DefaultApi finnhubService;
    
    public MarketDataTool(DefaultApi finnhubService) {
        this.finnhubService = finnhubService;
    }
    
    @Tool("Get real-time stock quote with current price, change, and trading range")
    public String getStockQuote(@P("stock ticker symbol") String symbol) {
        try {
            Quote quote = finnhubService.quote(symbol);
            CompanyProfile2 profile = finnhubService.companyProfile2(symbol,null,null);
            return this.formatQuote(symbol, quote, profile);
        } catch (Exception e) {
            return "Error retrieving quote for " + symbol + ": " + e.getMessage();
        }
    }
    
    @Tool("Get financial metrics including P/E ratio, ROE, 52-week high/low, and valuation assessment")
    public String getFinancialMetrics(@P("stock ticker symbol") String symbol) {
        try {
            BasicFinancials financials = finnhubService.companyBasicFinancials(symbol,"all");
            return this.formatFinancials(symbol, financials);
        } catch (Exception e) {
            return "Error retrieving financial metrics for " + symbol + ": " + e.getMessage();
        }
    }
    
    @Tool("Get recent company news and market sentiment for the past 7 days")
    public String getRecentNews(@P("stock ticker symbol") String symbol) {
        try {
            LocalDate today = LocalDate.now();
            LocalDate weekAgo = today.minusDays(7);
            
            String from = weekAgo.format(DateTimeFormatter.ISO_DATE);
            String to = today.format(DateTimeFormatter.ISO_DATE);
            
            List<CompanyNews> news = finnhubService.companyNews(symbol, from, to);
            return this.formatNews(symbol, news);
        } catch (Exception e) {
            return "Error retrieving news for " + symbol + ": " + e.getMessage();
        }
    }
    
    @Tool("Get analyst recommendations and trends for a stock")
    public String getAnalystRecommendations(@P("stock ticker symbol") String symbol) {
        try {
            List<RecommendationTrend> recommendations = finnhubService.recommendationTrends(symbol);
            return formatRecommendations(symbol, recommendations);
        } catch (Exception e) {
            return "Error retrieving recommendations for " + symbol + ": " + e.getMessage();
        }
    }
    
    @Tool("Calculate position size based on account balance, risk percentage, entry price, and stop loss")
    public String calculatePositionSize(@P("stock symbol") String symbol,
                                       @P("account balance in dollars") double accountBalance,
                                       @P("risk percentage (e.g., 2 for 2%)") double riskPercent,
                                       @P("entry price") double entryPrice,
                                       @P("stop loss price") double stopLoss) {
        
        double riskAmount = accountBalance * (riskPercent / 100.0);
        double riskPerShare = Math.abs(entryPrice - stopLoss);
        int maxShares = (int) (riskAmount / riskPerShare);
        double positionValue = maxShares * entryPrice;
        double positionPercent = (positionValue / accountBalance) * 100;
        
        return String.format("""
            Position Size Calculation for %s:
            
            Account Balance: $%.2f
            Risk Per Trade: %.1f%% ($%.2f)
            
            Entry Price: $%.2f
            Stop Loss: $%.2f
            Risk Per Share: $%.2f
            
            RECOMMENDED POSITION:
            Max Shares: %d shares
            Position Value: $%.2f (%.1f%% of account)
            Capital at Risk: $%.2f
            
            Risk/Reward Setup:
            - If stopped out: -$%.2f (%.1f%%)
            - Position size follows 2%% rule: %s
            - Position under 20%% of account: %s
            
            ⚠️ REMINDER: This is educational calculation only!
            """,
            symbol.toUpperCase(),
            accountBalance,
            riskPercent,
            riskAmount,
            entryPrice,
            stopLoss,
            riskPerShare,
            maxShares,
            positionValue,
            positionPercent,
            riskAmount,
            riskAmount,
            riskPercent,
            riskPercent <= 2.0 ? "✓ YES" : "✗ NO - REDUCE RISK",
            positionPercent <= 20.0 ? "✓ YES" : "✗ NO - REDUCE SIZE"
        );
    }
    
    private String formatRecommendations(String symbol, List<RecommendationTrend> recommendations) {
        if (recommendations == null || recommendations.isEmpty()) {
            return "No analyst recommendations available for " + symbol.toUpperCase();
        }
        
        StringBuilder result = new StringBuilder();
        result.append(String.format("Analyst Recommendations for %s:\n\n", symbol.toUpperCase()));
        
        // Get most recent recommendation
        RecommendationTrend latest = recommendations.get(0);
        
        long strongBuy = latest.getStrongBuy() != null ? latest.getStrongBuy() : 0;
        long buy = latest.getBuy() != null ? latest.getBuy() : 0;
        long hold = latest.getHold() != null ? latest.getHold() : 0;
        long sell = latest.getSell() != null ? latest.getSell() : 0;
        long strongSell = latest.getStrongSell() != null ? latest.getStrongSell() : 0;
        
        long total = strongBuy + buy + hold + sell + strongSell;
        
        result.append(String.format("Period: %s\n", latest.getPeriod()));
        result.append(String.format("Strong Buy: %d\n", strongBuy));
        result.append(String.format("Buy: %d\n", buy));
        result.append(String.format("Hold: %d\n", hold));
        result.append(String.format("Sell: %d\n", sell));
        result.append(String.format("Strong Sell: %d\n", strongSell));
        result.append(String.format("\nTotal Analysts: %d\n", total));
        
        // Calculate consensus
        if (total > 0) {
            double bullishPercent = ((strongBuy + buy) * 100.0) / total;
            result.append(String.format("Bullish Sentiment: %.1f%%\n", bullishPercent));
            
            if (bullishPercent > 60) {
                result.append("Consensus: POSITIVE");
            } else if (bullishPercent < 40) {
                result.append("Consensus: NEGATIVE");
            } else {
                result.append("Consensus: NEUTRAL");
            }
        }
        
        return result.toString();
    }

    /**
     * Format financial metrics as human-readable string
     */
    public String formatFinancials(String symbol, BasicFinancials financials) {
        var metrics = (Map<String,Object>) financials.getMetric();
        if (metrics == null) {
            return "Financial metrics not available for " + symbol;
        }



        Double pe = getMetricValue(metrics.get("peBasicExclExtraTTM"));
        Double pb = getMetricValue(metrics.get("pbAnnual"));
        Double roe = getMetricValue(metrics.get("roeRfy"));
        Double roa = getMetricValue(metrics.get("roaRfy"));
        Double high52 = getMetricValue(metrics.get("52WeekHigh"));
        Double low52 = getMetricValue(metrics.get("52WeekLow"));

        return String.format("""
                        Financial Metrics for %s:
                        P/E Ratio: %.2f
                        P/B Ratio: %.2f
                        ROE: %.2f%%
                        ROA: %.2f%%
                        52-Week High: $%.2f
                        52-Week Low: $%.2f
                        
                        Valuation Assessment:
                        %s
                        """,
                symbol.toUpperCase(),
                pe != null ? pe : 0.0,
                pb != null ? pb : 0.0,
                roe != null ? roe * 100 : 0.0,
                roa != null ? roa * 100 : 0.0,
                high52 != null ? high52 : 0.0,
                low52 != null ? low52 : 0.0,
                assessValuation(pe, roe)
        );
    }

    /**
     * Format news as human-readable string
     */
    public String formatNews(String symbol, List<CompanyNews> newsList) {
        if (newsList == null || newsList.isEmpty()) {
            return "No recent news found for " + symbol.toUpperCase();
        }

        StringBuilder result = new StringBuilder();
        result.append(String.format("Recent News for %s:\n\n", symbol.toUpperCase()));

        int count = 0;
        for (CompanyNews news : newsList) {
            if (count >= 3) break;  // Limit to 3 most recent

            result.append(String.format("%d. %s\n", count + 1, news.getHeadline()));
            result.append(String.format("   Source: %s\n", news.getSource()));
            String summary = news.getSummary();
            if (summary != null && !summary.isEmpty()) {
                String shortSummary = summary.length() > 100 ?
                        summary.substring(0, 100) + "..." : summary;
                result.append(String.format("   Summary: %s\n", shortSummary));
            }
            result.append(String.format("   URL: %s\n\n", news.getUrl()));
            count++;
        }

        return result.toString();
    }

    private Double getMetricValue(Object value) {
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return null;
    }

    private String assessValuation(Double pe, Double roe) {
        StringBuilder assessment = new StringBuilder();

        if (pe != null && pe > 0) {
            if (pe < 15) {
                assessment.append("P/E suggests potential undervaluation; ");
            } else if (pe > 25) {
                assessment.append("P/E suggests potential overvaluation; ");
            } else {
                assessment.append("P/E in reasonable range; ");
            }
        }

        if (roe != null && roe > 0) {
            if (roe > 0.15) {
                assessment.append("Strong ROE indicates good profitability");
            } else if (roe < 0.05) {
                assessment.append("Low ROE may indicate profitability concerns");
            }
        }

        return assessment.length() > 0 ? assessment.toString() : "Insufficient data for assessment";
    }

    /**
     * Format quote as human-readable string
     */
    public String formatQuote(String symbol, Quote quote, CompanyProfile2 profile) {
        double current = quote.getC() != null ? quote.getC().doubleValue() : 0.0;
        double previousClose = quote.getPc() != null ? quote.getPc().doubleValue() : 0.0;
        double change = current - previousClose;
        double changePercent = previousClose != 0 ? (change / previousClose) * 100 : 0.0;

        return String.format("""
                        Real-Time Quote for %s (%s):
                        Current Price: $%.2f
                        Change: $%.2f (%.2f%%)
                        Today's High: $%.2f
                        Today's Low: $%.2f
                        Previous Close: $%.2f
                        
                        Company Info:
                        Name: %s
                        Exchange: %s
                        Market Cap: $%.1fB
                        Country: %s
                        Industry: %s
                        """,
                symbol.toUpperCase(),
                profile.getName() != null ? profile.getName() : "N/A",
                current,
                change,
                changePercent,
                quote.getH() != null ? quote.getH().doubleValue() : 0.0,
                quote.getL() != null ? quote.getL().doubleValue() : 0.0,
                previousClose,
                profile.getName() != null ? profile.getName() : "N/A",
                profile.getExchange() != null ? profile.getExchange() : "N/A",
                profile.getMarketCapitalization() != null ? profile.getMarketCapitalization().doubleValue() / 1000 : 0.0,
                profile.getCountry() != null ? profile.getCountry() : "N/A",
                profile.getFinnhubIndustry() != null ? profile.getFinnhubIndustry() : "N/A"
        );
    }

}
