package ch.erni.ai.demo.agent.trading.tool;


import io.finnhub.api.apis.DefaultApi;
import io.finnhub.api.infrastructure.ApiClient;

public class MarketToolTest {

    public static void main(String[] args) {
        String apikey = "d3bm1q9r01qqg7bv6acgd3bm1q9r01qqg7bv6ad0";
        MarketDataTool tool = new MarketDataTool(apikey);

        System.out.println(tool.getRecentNews("GOOGL"));
    }
}
