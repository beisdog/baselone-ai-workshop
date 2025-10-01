# Trading Agent System - Complete Setup Guide

## 📋 What You'll Build

A multi-agent trading analysis system that:
- Analyzes stocks using **real market data** from Finnhub.io
- Calculates position sizes with proper **risk management**
- Uses **supervisor agent pattern** to coordinate specialized agents
- Provides educational trading analysis (NOT financial advice)

## 🎯 Prerequisites

### 1. Install Java 17+
```bash
# Check Java version
java -version

# If needed, install from: https://adoptium.net/
```

### 2. Install Maven
```bash
# Check Maven version
mvn -version

# If needed, install from: https://maven.apache.org/
```

### 3. Install and Setup LMStudio
# Download from: https://lmstudio.ai/
Load a model like llama3.2

### 4. Get Finnhub API Key
1. Go to: https://finnhub.io/
2. Sign up for free account
3. Copy your API key
4. Free tier includes 60 API calls/minute

## 🚀 Setup Steps

### Step 1: Navigate to Project
```bash
cd ./solutions/agents/trading-agent
```

### Step 2: Set API Key
```bash
# Set environment variable (required!)
export FINNHUB_API_KEY='paste_your_api_key_here'

# Verify it's set
echo $FINNHUB_API_KEY
```

### Step 3: Build Project
```bash
# Clean build
mvn clean install
```

This will:
- Download all dependencies (LangChain4j, Finnhub client, etc.)
- Compile Java source files
- Run tests
- Create executable JAR

### Step 4: Run Application
```bash
# Option 1: Using run script
chmod +x run.sh
./run.sh

# Option 2: Using Maven directly
mvn exec:java

# Option 3: Using full class name
mvn exec:java -Dexec.mainClass="ch.erni.ai.demo.agent.trading.TradingAgentApplication"
```

## 🎓 How to Use

### Example Queries

1. **Basic Stock Analysis**
   ```
   Analyze AAPL for a potential swing trade
   ```

2. **Position Sizing**
   ```
   Calculate position size for MSFT with $10,000 account and 2% risk
   ```

3. **Comprehensive Analysis**
   ```
   I have $5,000. Analyze NVDA with moderate risk tolerance for 2-week hold.
   ```

4. **Risk Assessment**
   ```
   What stop loss should I use for TSLA at $250?
   ```

5. **News and Sentiment**
   ```
   What's the latest news on GOOGL?
   ```

### Understanding the Output

The system will:
1. 🤖 Supervisor analyzes your request
2. 📊 Invokes appropriate agents (Research, Risk Management)
3. 🔍 Agents use real Finnhub data via tools
4. 📋 Provides comprehensive analysis with specific numbers
5. ⚠️ Includes educational disclaimers

## 🏗️ Architecture Overview

```
User Query
    ↓
TradingSupervisor (Decides which agents to use)
    ↓
    ├─→ MarketResearchAgent
    │   └─→ MarketDataTool → Finnhub API
    │       ├─→ Stock quotes
    │       ├─→ Financial metrics
    │       ├─→ Company news
    │       └─→ Analyst ratings
    │
    └─→ RiskManagementAgent  
        └─→ MarketDataTool → Finnhub API
            ├─→ Position size calculation
            ├─→ Stop-loss levels
            ├─→ Risk/reward ratios
            └─→ Account risk validation
```

## 🔧 Customization

### Change LLM Model
Edit `TradingSupervisor.java`:
```java
.modelName("llama3.2")  // Change to your preferred model
```

### Adjust Risk Parameters
Edit `RiskManagementAgent.java`:
```java
// Default: 2% risk per trade
// Default: 20% max position size
```

### Add More Agents
1. Create new agent interface in `agent/` package
2. Add `@Agent` annotation
3. Define tools in `tool/` package
4. Register with supervisor in `TradingSupervisor.java`

## 🐛 Troubleshooting

### Problem: "Ollama connection refused"
**Solution:**
```bash
# Start Ollama in separate terminal
ollama serve

# Verify it's running
curl http://localhost:11434/api/tags
```

### Problem: "FINNHUB_API_KEY not found"
**Solution:**
```bash
# Set the environment variable
export FINNHUB_API_KEY='your_key_here'

# Verify
echo $FINNHUB_API_KEY

# For permanent setup, add to ~/.bashrc or ~/.zshrc:
echo 'export FINNHUB_API_KEY="your_key_here"' >> ~/.bashrc
source ~/.bashrc
```

### Problem: "Rate limit exceeded"
**Solution:**
- Free tier: 60 calls/minute
- Wait 60 seconds between intensive queries
- Consider Finnhub paid plan for higher limits

### Problem: "Maven build fails"
**Solution:**
```bash
# Clear Maven cache
rm -rf ~/.m2/repository

# Rebuild
mvn clean install -U
```

### Problem: "Symbol not found"
**Solution:**
- Use correct ticker symbols (e.g., AAPL not Apple)
- Check if stock trades on US exchanges
- Verify symbol exists on Finnhub

## 📊 What Makes This Project Educational

### 1. Real Market Data
- Uses actual stock prices, not mock data
- Financial metrics from Finnhub API
- Real news and analyst ratings

### 2. Proper Risk Management
- 2% rule enforcement
- Position size calculations
- Stop-loss requirements
- Risk/reward analysis

### 3. Multi-Agent Architecture
- Supervisor coordinates specialized agents
- Each agent has specific expertise
- Tools provide real data access
- Dynamic planning based on queries

### 4. Production Patterns
- Error handling
- API rate limiting
- Logging and monitoring
- Configuration management

## ⚠️ Important Reminders

1. **Educational Only**: This is NOT financial advice
2. **Paper Trade First**: Never risk real money without practice
3. **Consult Professionals**: Get advice from qualified financial advisors
4. **Risk Management**: Follow the 2% rule and position limits
5. **Market Risk**: Trading involves substantial risk of loss

## 📚 Next Steps

### For Students:
1. Run example queries to understand the system
2. Try different stocks and scenarios
3. Modify agent prompts to change behavior
4. Add new tools or agents
5. Experiment with different risk parameters

### For Instructors:
1. Demonstrate supervisor agent coordination
2. Show real API integration patterns
3. Explain risk management principles
4. Discuss agentic AI architectures
5. Review code structure and design patterns

## 🤝 Support

If you encounter issues:
1. Check troubleshooting section
2. Verify all prerequisites installed
3. Check Ollama and Finnhub status
4. Review error messages carefully
5. Ensure API key is set correctly

## 📖 Additional Resources

- **LangChain4j**: https://docs.langchain4j.dev/
- **Finnhub API**: https://finnhub.io/docs/api
- **LMStdui**: https://lmstudio.ai/
- **Agentic Patterns**: https://docs.langchain4j.dev/tutorials/agents

---

**Ready to start? Run:**
```bash
chmod +x run.sh && ./run.sh
```

**Remember: This is educational. Paper trade first. Risk management always!**
