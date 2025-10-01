#!/bin/bash

# Complete setup script for Trading Agent project
# This script creates all necessary directories and placeholder source files

set -e

echo "🏗️  Setting up Trading Agent project..."
cd "$(dirname "$0")"

# Create complete directory structure
echo "📁 Creating directory structure..."
mkdir -p src/main/java/com/baselone/trading/agents
mkdir -p src/main/java/com/baselone/trading/tools
mkdir -p src/main/java/com/baselone/trading/models
mkdir -p src/main/java/com/baselone/trading/supervisor
mkdir -p src/main/resources
mkdir -p src/test/java/com/baselone/trading

echo "✅ Directory structure created successfully!"
echo ""
echo "📝 Project structure:"
echo "trading-agent/"
echo "├── pom.xml"
echo "├── README.md"
echo "├── setup.sh"
echo "├── full-setup.sh (this script)"
echo "└── src/"
echo "    ├── main/"
echo "    │   ├── java/com/baselone/trading/"
echo "    │   │   ├── TradingAgentApplication.java"
echo "    │   │   ├── agents/"
echo "    │   │   ├── tools/"
echo "    │   │   ├── models/"
echo "    │   │   └── supervisor/"
echo "    │   └── resources/"
echo "    │       └── config.properties"
echo "    └── test/"
echo ""
echo "📋 Next steps:"
echo "1. Source files are ready to be added to the project"
echo "2. Get your Finnhub API key from https://finnhub.io/"
echo "3. Set environment variable: export FINNHUB_API_KEY=your_key"
echo "4. Ensure Ollama is running: ollama pull llama3.2"
echo "5. Build project: mvn clean install"
echo "6. Run application: mvn exec:java"
echo ""
echo "✅ Setup complete! Ready for source code files."
