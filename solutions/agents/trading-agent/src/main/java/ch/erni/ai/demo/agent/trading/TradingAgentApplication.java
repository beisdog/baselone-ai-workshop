package ch.erni.ai.demo.agent.trading;

import ch.erni.ai.demo.agent.trading.agent.TradingSupervisor;
import ch.erni.ai.demo.agent.trading.util.TextUtils;
import dev.langchain4j.http.client.jdk.JdkHttpClient;
import dev.langchain4j.http.client.jdk.JdkHttpClientBuilder;
import dev.langchain4j.model.openai.OpenAiChatModel;
import io.finnhub.api.apis.DefaultApi;
import io.finnhub.api.infrastructure.ApiClient;

import java.net.http.HttpClient;
import java.util.Scanner;

/**
 * Trading Agent Application with Real Market Data from Finnhub.io
 * <p>
 * CRITICAL DISCLAIMERS:
 * - This is for EDUCATIONAL PURPOSES ONLY - not financial advice
 * - Trading involves substantial risk of financial loss
 * - Always use paper trading before real money
 * - Consult qualified financial professionals
 * - Never trade with money you cannot afford to lose
 */
public class TradingAgentApplication {

    // Console formatting settings
    private static final int CONSOLE_WIDTH = 100;
    private static final int TEXT_WRAP_WIDTH = 80;

    public static void main(String[] args) {
        printDisclaimers();

        // Get Finnhub API key from environment variable
        String finnhubApiKey = System.getenv("FINNHUB_API_KEY");
        if (finnhubApiKey == null) {
            throw new IllegalArgumentException("Finnhub API KEY environment variable is not set");
        }

        if (finnhubApiKey == null || finnhubApiKey.isEmpty()) {
            System.out.println("\n🚨 SETUP REQUIRED:");
            System.out.println("1. Get free API key from https://finnhub.io/");
            System.out.println("2. Set environment variable: export FINNHUB_API_KEY='your_key_here'");
            System.out.println("3. Run the program again");
            return;
        }

        try {
            // Initialize services
            System.out.println("\n📊 Initializing Trading Agent System...");
            ApiClient.Companion.getApiKey().put("token", finnhubApiKey);
            DefaultApi finnhubService = new DefaultApi();

            // Test connection
            System.out.println("✅ Connected to Finnhub.io");

            // Initialize trading supervisor
            HttpClient.Builder httpClientBuilder = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1);

            JdkHttpClientBuilder jdkHttpClientBuilder = JdkHttpClient.builder()
                    .httpClientBuilder(httpClientBuilder);
            var model = OpenAiChatModel.builder()
                    .baseUrl("http://localhost:1234/v1")
                    //.apiKey("openai api key")
                    //.modelName(OpenAiChatModelName.GPT_4_1)
                    .modelName("openai/gpt-oss-120b")
                    //.temperature(0.1)  // Low temperature for consistent financial analysis
                    //.timeout(java.time.Duration.ofMinutes(3))
                    .logRequests(true)
                    .logResponses(true)
                    .httpClientBuilder(jdkHttpClientBuilder)
                    .build();
            TradingSupervisor supervisor = new TradingSupervisor(model, finnhubApiKey);
            System.out.println("✅ Trading Supervisor initialized");
            System.out.println("✅ Agents ready: Market Research, Risk Management, Technical Analysis");

            // Run interactive mode
            runInteractiveMode(supervisor);
        } catch (Exception e) {
            System.err.println("❌ Error initializing application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runInteractiveMode(TradingSupervisor supervisor) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n" + TextUtils.createSeparator(CONSOLE_WIDTH));
        System.out.println(TextUtils.centerText("🎓 Interactive Trading Analysis Mode", CONSOLE_WIDTH));
        System.out.println(TextUtils.createSeparator(CONSOLE_WIDTH));
        System.out.println("💡 Example queries:");
        System.out.println("   - 'Analyze AAPL for a swing trade'");
        System.out.println("   - 'Calculate position size for MSFT with $10,000 account'");
        System.out.println("   - 'Compare TSLA vs NVDA for risk/reward'");
        System.out.println("   - 'What's the latest news on GOOGL?'");
        System.out.println("\nType 'exit' to quit, 'help' for more examples");
        System.out.println(TextUtils.createSeparator(CONSOLE_WIDTH));

        while (true) {
            System.out.print("\n📈 Your analysis request: ");
            String userRequest = scanner.nextLine().trim();

            if (userRequest.equalsIgnoreCase("exit")) {
                System.out.println("\n👋 Trading analysis session ended.");
                System.out.println("⚠️ Remember: Always practice with paper trading first!");
                break;
            }

            if (userRequest.equalsIgnoreCase("help")) {
                printHelpExamples();
                continue;
            }

            if (userRequest.isEmpty()) {
                continue;
            }

            try {
                System.out.println("\n" + TextUtils.createSeparator(CONSOLE_WIDTH));
                
                // Show processing message
                System.out.println("🤖 Analyzing request: " + TextUtils.truncateText(userRequest, 60));
                System.out.println("📊 Gathering real market data...");
                
                String analysis = supervisor.analyze(userRequest);

                // Format and display the analysis with proper text wrapping
                System.out.println("\n📋 ANALYSIS RESULTS:");
                System.out.println(TextUtils.createSeparator(CONSOLE_WIDTH, '-'));
                
                String wrappedAnalysis = TextUtils.wrapText(analysis, TEXT_WRAP_WIDTH);
                System.out.println(wrappedAnalysis);
                
                System.out.println(TextUtils.createSeparator(CONSOLE_WIDTH, '-'));
                System.out.println("⚠️ REMINDER: Educational analysis only - not financial advice!");
                System.out.println(TextUtils.createSeparator(CONSOLE_WIDTH));
                
            } catch (Exception e) {
                System.out.println(TextUtils.createSeparator(CONSOLE_WIDTH, '-'));
                System.err.println("❌ Error: " + e.getMessage());
                System.out.println(TextUtils.createSeparator(CONSOLE_WIDTH, '-'));
            }
        }

        scanner.close();
    }

    private static void printDisclaimers() {
        System.out.println(TextUtils.createSeparator(CONSOLE_WIDTH));
        System.out.println(TextUtils.centerText("🚨 IMPORTANT DISCLAIMERS", CONSOLE_WIDTH));
        System.out.println(TextUtils.createSeparator(CONSOLE_WIDTH));
        
        String[] disclaimers = {
            "- This uses REAL market data but is for EDUCATIONAL PURPOSES ONLY",
            "- NOT financial advice - consult qualified professionals",
            "- Trading involves substantial risk of financial loss",
            "- Only trade with money you can afford to lose",
            "- Always use paper trading before real money",
            "- Data provided by Finnhub.io"
        };
        
        for (String disclaimer : disclaimers) {
            System.out.println(TextUtils.wrapText(disclaimer, TEXT_WRAP_WIDTH));
        }
        
        System.out.println(TextUtils.createSeparator(CONSOLE_WIDTH));
    }

    private static void printHelpExamples() {
        System.out.println("\n" + TextUtils.createSeparator(CONSOLE_WIDTH, '-'));
        System.out.println(TextUtils.centerText("💡 Example Analysis Requests", CONSOLE_WIDTH));
        System.out.println(TextUtils.createSeparator(CONSOLE_WIDTH, '-'));
        
        System.out.println("\n📊 Stock Analysis:");
        System.out.println("   - 'Analyze AAPL for a potential swing trade'");
        System.out.println("   - 'What's the technical outlook for TSLA?'");
        System.out.println("   - 'Should I buy MSFT at current levels?'");
        
        System.out.println("\n💰 Risk Management:");
        System.out.println("   - 'Calculate position size for GOOGL with $10,000 account and 2% risk'");
        System.out.println("   - 'What stop loss should I use for NVDA at $500?'");
        System.out.println("   - 'How many shares of AMZN can I buy with $5,000?'");
        
        System.out.println("\n📰 News & Fundamentals:");
        System.out.println("   - 'What's the latest news on META?'");
        System.out.println("   - 'Show me the financial metrics for NFLX'");
        System.out.println("   - 'Is AAPL overvalued based on P/E ratio?'");
        
        System.out.println("\n⚖️ Comparisons:");
        System.out.println("   - 'Compare TSLA vs RIVN for a tech play'");
        System.out.println("   - 'Which is better: SPY or QQQ for long-term hold?'");
        
        System.out.println(TextUtils.createSeparator(CONSOLE_WIDTH, '-'));
    }
}
