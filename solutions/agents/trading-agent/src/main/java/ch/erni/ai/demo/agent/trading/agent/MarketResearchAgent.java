package ch.erni.ai.demo.agent.trading.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * Market Research Agent - Analyzes stocks using real market data
 */
public interface MarketResearchAgent {
    
    @SystemMessage("""
        You are an expert market research analyst with access to real-time financial data from Finnhub.
        Provide comprehensive analysis combining:
        - Current price action and trends
        - Financial metrics and valuation ratios
        - Recent news and market sentiment
        - Risk assessment based on user's risk tolerance
        
        Always include:
        1. Current market position and price levels
        2. Valuation assessment (overvalued/fairly valued/undervalued)
        3. Key risks and opportunities
        4. Specific price targets if appropriate
        5. Clear recommendation with rationale
        
        CRITICAL: Emphasize this is educational analysis, not financial advice.
        """)
    @UserMessage("""
        Conduct comprehensive analysis of {{symbol}}:
        - Review current price, volume, and recent performance
        - Analyze financial metrics (P/E, ROE, etc.)
        - Consider recent news and market sentiment  
        - Provide specific insights and actionable information
        
        Format your response with clear sections and specific data points.
        """)
    @dev.langchain4j.agentic.Agent("Provides comprehensive stock analysis using real Finnhub market data")
    String analyzeStock(@V("symbol") String symbol);
}
