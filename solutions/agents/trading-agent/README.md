# Trading Agent System with Finnhub.io

An educational stock trading agent system built with LangChain4j and real market data from Finnhub.io.

## ⚠️ CRITICAL DISCLAIMERS

**THIS IS FOR EDUCATIONAL PURPOSES ONLY**
- NOT financial advice - consult qualified professionals
- Trading involves substantial risk of financial loss
- Only trade with money you can afford to lose
- Always use paper trading before real money
- Past performance doesn't guarantee future results

## 🏗️ Architecture

### Multi-Agent System

This project demonstrates a **supervisor agent pattern** where specialized AI agents coordinate to provide comprehensive trading analysis:

```
TradingSupervisor (Coordinator)
├── MarketResearchAgent (Analysis)
│   └── Uses: Real-time quotes, financials, news
├── RiskManagementAgent (Position Sizing)
│   └── Uses: 2% rule, volatility, stop-losses
└── MarketDataTool (Real Data)
    └── Finnhub Kotlin Client
```

### Key Components

1. **TradingSupervisor**: Orchestrates agents and coordinates analysis
2. **MarketResearchAgent**: Analyzes stocks using real market data
3. **RiskManagementAgent**: Calculates safe position sizes
4. **MarketDataTool**: Integrates with Finnhub API
5. **FinnhubService**: Wraps Finnhub Kotlin client

## 🚀 Quick Start

### Prerequisites

1. **Java 17+**
   ```bash
   java -version
   ```

2. **Maven 3.6+**
   ```bash
   mvn -version
   ```

3. **Ollama with llama3.2**
   ```bash
   # Install Ollama from https://ollama.ai
   ollama pull llama3.2
   ollama serve
   ```

4. **Finnhub API Key**
   - Get free key at: https://finnhub.io/
   - Free tier: 60 API calls/minute

### Setup

1. **Clone and navigate to project**
   ```bash
   cd /Users/david.beisert/deverni/workspace/baselone-ai-workshop/solutions/agents/trading-agent
   ```

2. **Set Finnhub API key**
   ```bash
   export FINNHUB_API_KEY='your_api_key_here'
   ```

3. **Build project**
   ```bash
   mvn clean install
   ```

4. **Run application**
   ```bash
   mvn exec:java
   ```

## 📊 Usage Examples

### Basic Stock Analysis
```
Analyze AAPL for a swing trade
```

### Position Sizing
```
Calculate position size for MSFT with $10,000 account and 2% risk
```

### Comprehensive Analysis
```
I have $5,000 to invest. Analyze NVDA for a 2-week hold with moderate risk tolerance.
```

### Risk Assessment
```
What stop loss should I use for TSLA at $250 entry?
```

### Market Research
```
What's the latest news and sentiment on GOOGL?
```

## 🛠️ Project Structure

```
trading-agent/
├── src/main/java/com/baselone/trading/
│   ├── TradingAgentApplication.java      # Main entry point
│   ├── agent/
│   │   ├── TradingSupervisor.java        # Coordinates all agents
│   │   ├── MarketResearchAgent.java      # Stock analysis agent
│   │   └── RiskManagementAgent.java      # Position sizing agent
│   ├── service/
│   │   └── FinnhubService.java           # Finnhub client wrapper
│   └── tool/
│       └── MarketDataTool.java           # Market data tools
├── pom.xml                                # Maven dependencies
└── README.md                              # This file
```

## 🔧 Configuration

### Ollama Model
Default model: `llama3.2`

To change model, edit `TradingSupervisor.java`:
```java
.modelName("your-preferred-model")
```

### API Rate Limiting
Free Finnhub tier: 60 calls/minute

The application includes automatic rate limiting to respect API quotas.

## 📚 Key Concepts Demonstrated

### 1. Supervisor Agent Pattern
The supervisor analyzes requests and automatically determines which specialized agents to invoke:
- Single agent for simple queries
- Multiple agents for comprehensive analysis
- Dynamic planning based on request complexity

### 2. Real Market Data Integration
Uses Finnhub Kotlin client for:
- Real-time stock quotes
- Financial metrics (P/E, ROE, 52-week ranges)
- Company news and sentiment
- Analyst recommendations

### 3. Risk Management
Built-in safety rules:
- 2% risk rule (never risk more than 2% per trade)
- Position size limits (max 20% of account)
- Stop-loss requirements
- Risk/reward calculations

### 4. Tool Integration
Agents use specialized tools to access:
- Market data queries
- Position size calculations
- Financial metrics analysis
- News and sentiment data

## 🎓 Educational Value

This project teaches:
1. **Multi-agent coordination** with LangChain4j
2. **Real API integration** with Finnhub
3. **Financial risk management** principles
4. **Java/Kotlin interoperability**
5. **Agentic AI patterns** and workflows

## 🐛 Troubleshooting

### "Ollama connection refused"
```bash
# Start Ollama
ollama serve

# Verify model is available
ollama list
```

### "Finnhub API key not found"
```bash
# Set environment variable
export FINNHUB_API_KEY='your_key_here'

# Verify it's set
echo $FINNHUB_API_KEY
```

### "Rate limit exceeded"
- Free tier: 60 calls/minute
- Wait 60 seconds between intensive queries
- Consider upgrading Finnhub plan

## 📖 Learning Resources

- **LangChain4j Docs**: https://docs.langchain4j.dev/
- **Finnhub API**: https://finnhub.io/docs/api
- **Agentic Patterns**: https://docs.langchain4j.dev/tutorials/agents

## 🤝 Contributing

This is an educational project. To improve:
1. Add more specialized agents
2. Implement backtesting
3. Add portfolio tracking
4. Include technical indicators
5. Enhance risk management

## 📄 License

Educational project - use at your own risk with proper disclaimers.

## ⚖️ Legal

**This software is provided "as is" without warranty of any kind.**
- Not financial advice
- No liability for trading losses
- Users responsible for their own trading decisions
- Must comply with local trading regulations

---

**Remember: Paper trade first, risk management always, educational purposes only!**
