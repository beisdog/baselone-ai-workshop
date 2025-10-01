package ch.erni.ai.demo.agent.trading.agent;

import ch.erni.ai.demo.agent.trading.tool.MarketDataTool;
import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.supervisor.SupervisorAgent;
import dev.langchain4j.agentic.supervisor.SupervisorResponseStrategy;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import io.finnhub.api.apis.DefaultApi;

/**
 * Trading Supervisor - Coordinates all trading agents and tools
 */
public class TradingSupervisor {
    private final SupervisorAgent supervisor;
    private final ChatModel model;

    public TradingSupervisor(ChatModel model, DefaultApi finnhubService) {
        // Initialize language model
        this.model = model;

        // Create supervisor with all agents and tools
        this.supervisor = createSupervisor(finnhubService);
    }

    private SupervisorAgent createSupervisor(DefaultApi finnhubService) {
        // Create market data tool
        MarketDataTool marketDataTool = new MarketDataTool(finnhubService);

        // Build Market Research Agent
        MarketResearchAgent researchAgent = AgenticServices
                .agentBuilder(MarketResearchAgent.class)
                .chatModel(model)
                .tools(marketDataTool)
                .outputName("marketAnalysis")
                .build();

        // Build Risk Management Agent
        RiskManagementAgent riskAgent = AgenticServices
                .agentBuilder(RiskManagementAgent.class)
                .chatModel(model)
                .tools(marketDataTool)
                .outputName("riskAssessment")
                .build();

        // Create Supervisor that coordinates agents
        return AgenticServices
                .supervisorBuilder()
                .chatModel(model)
                .subAgents(researchAgent, riskAgent)
                .responseStrategy(SupervisorResponseStrategy.SUMMARY)

                .supervisorContext("""
                        TRADING ANALYSIS PROTOCOL WITH REAL MARKET DATA:
                        
                        You are a trading analysis coordinator using REAL-TIME data from Finnhub.io
                        
                        CRITICAL RULES (NEVER VIOLATE):
                        1. This is EDUCATIONAL ANALYSIS ONLY - emphasize in every response
                        2. Users must PAPER TRADE before considering real money
                        3. This is NOT financial advice - users must consult professionals
                        4. Always include risk warnings and disclaimers
                        
                        ANALYSIS WORKFLOW:
                        1. Market Research Agent analyzes:
                           - Real-time price and trends
                           - Financial metrics (P/E, ROE, 52-week range)
                           - Recent news and sentiment
                           - Valuation assessment
                        
                        2. Risk Management Agent calculates:
                           - Position size (NEVER exceed 2% risk rule)
                           - Stop loss levels (typically 5-8% from entry)
                           - Risk/reward ratio (minimum 2:1)
                           - Position as % of account (max 20%)
                        
                        DECISION MAKING:
                        - For "analyze" or "research" queries: Use Market Research Agent
                        - For "position size" or "risk" queries: Use Risk Management Agent
                        - For comprehensive analysis: Use BOTH agents in sequence
                        - Always validate with real market data from tools
                        
                        RESPONSE FORMAT:
                        1. Start with brief summary of request
                        2. Present analysis from relevant agents
                        3. Synthesize into clear recommendation
                        4. Include specific numbers and price levels
                        5. End with prominent educational disclaimer
                        
                        Remember: Real data, educational purpose, risk management first.
                        """)

                .build();
    }

    /**
     * Analyze a trading request using real market data
     */
    public String analyze(String request) {
        System.out.println("🤖 Supervisor analyzing with real market data...");

        try {
            String result = supervisor.invoke(request);
            return result;
        } catch (Exception e) {
            return "❌ Error during analysis: " + e.getMessage() +
                    "\n\nPlease check that the LLM is running and the model is available.";
        }
    }
}
