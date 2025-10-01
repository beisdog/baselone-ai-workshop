package ch.erni.ai.demo.agent.trading.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * Risk Management Agent - Calculates position sizes and risk parameters
 */
public interface RiskManagementAgent {
    
    @SystemMessage("""
        You are a professional risk management specialist with access to real market data.
        
        Your PRIMARY responsibility is protecting capital through proper position sizing.
        
        MANDATORY RULES (NEVER VIOLATE):
        1. Never risk more than 2% of account on a single trade
        2. Position size must not exceed 20% of total account value
        3. Stop losses are REQUIRED - typically 5-8% from entry
        4. Always calculate risk/reward ratio (minimum 2:1 preferred)
        5. Consider stock's actual volatility when setting stops
        
        Your calculations must include:
        - Maximum position size based on 2% rule
        - Specific stop-loss price level
        - Take-profit target (risk/reward)
        - Dollar amount at risk
        - Number of shares to buy
        
        Use real market data (current price, 52-week range, volatility) for calculations.
        CRITICAL: Emphasize this is educational - users must paper trade first.
        """)
    @UserMessage("""
        Calculate risk management parameters for {{symbol}}:
        - Account Balance: ${{accountBalance}}
        - Risk per trade: {{riskPercent}}%  
        - Entry Strategy: {{entryStrategy}}
        
        Use real market data to determine:
        1. Safe position size (shares and dollar amount)
        2. Stop-loss price based on volatility
        3. Take-profit target (minimum 2:1 reward/risk)
        4. Total capital at risk
        5. Position as % of account
        
        Show all calculations step-by-step.
        """)
    @dev.langchain4j.agentic.Agent("Calculates position sizing and risk parameters using real market volatility")
    String calculateRiskParameters(@V("symbol") String symbol,
                                   @V("accountBalance") double accountBalance,
                                   @V("riskPercent") double riskPercent,
                                   @V("entryStrategy") String entryStrategy);
}
