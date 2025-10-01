#!/bin/bash

# Setup script for Trading Agent project
echo "Setting up Trading Agent project structure..."

cd "$(dirname "$0")"

# Create directory structure
mkdir -p src/main/java/com/baselone/trading/agents
mkdir -p src/main/java/com/baselone/trading/tools
mkdir -p src/main/java/com/baselone/trading/models
mkdir -p src/main/java/com/baselone/trading/supervisor
mkdir -p src/main/resources

echo "✅ Directory structure created"
echo ""
echo "Next steps:"
echo "1. Get your Finnhub API key from https://finnhub.io/"
echo "2. Set environment variable: export FINNHUB_API_KEY=your_key"
echo "3. Ensure Ollama is running with llama3.2 model"
echo "4. Run: mvn clean install"
echo "5. Run: mvn exec:java"
echo ""
echo "Project structure ready!"
