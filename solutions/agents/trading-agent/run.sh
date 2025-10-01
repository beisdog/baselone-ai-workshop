#!/bin/bash

# Trading Agent System - Run Script
# Educational stock trading analysis using LangChain4j and Finnhub

echo "=================================================="
echo "🤖 Trading Agent System Startup"
echo "=================================================="
echo ""

# Check if Finnhub API key is set
if [ -z "$FINNHUB_API_KEY" ]; then
    echo "❌ ERROR: FINNHUB_API_KEY environment variable not set"
    echo ""
    echo "Setup Instructions:"
    echo "1. Get free API key from https://finnhub.io/"
    echo "2. Set environment variable:"
    echo "   export FINNHUB_API_KEY='your_api_key_here'"
    echo "3. Run this script again"
    echo ""
    exit 1
fi

# Check if Ollama is running
echo "🔍 Checking if Ollama is running..."
if ! curl -s http://localhost:11434/api/tags > /dev/null 2>&1; then
    echo "❌ ERROR: Ollama is not running"
    echo ""
    echo "Please start Ollama:"
    echo "   ollama serve"
    echo ""
    echo "And ensure llama3.2 model is available:"
    echo "   ollama pull llama3.2"
    echo ""
    exit 1
fi

echo "✅ Ollama is running"
echo "✅ Finnhub API key is configured"
echo ""

# Build and run
echo "🔨 Building project..."
mvn clean package -q

if [ $? -ne 0 ]; then
    echo "❌ Build failed"
    exit 1
fi

echo "✅ Build successful"
echo ""
echo "🚀 Starting Trading Agent System..."
echo "=================================================="
echo ""

# Run the application
mvn exec:java -Dexec.mainClass="com.baselone.trading.TradingAgentApplication"
