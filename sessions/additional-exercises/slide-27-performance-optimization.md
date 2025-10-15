# Slide 27: Performance Optimization und Monitoring

**Performance Optimization Techniques:**
```java
@Service
public class PerformanceOptimizedRAGService {
    
    private final Cache<String, List<Content>> retrievalCache = 
        Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .recordStats()
            .build();
    
    private final Cache<String, String> responseCache = 
        Caffeine.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();
    
    @Async("ragExecutor")
    public CompletableFuture<String> processQueryAsync(String query) {
        return CompletableFuture.supplyAsync(() -> {
            // 1. Check response cache first
            String cachedResponse = responseCache.getIfPresent(query);
            if (cachedResponse != null) {
                return cachedResponse;
            }
            
            // 2. Retrieve with caching
            List<Content> context = retrievalCache.get(query, 
                k -> contentRetriever.retrieve(Query.from(k))
            );
            
            // 3. Generate response
            String response = ragAssistant.answerWithContext(query, context);
            
            // 4. Cache result
            responseCache.put(query, response);
            
            return response;
        });
    }
    
    @EventListener
    public void preloadFrequentQueries(FrequentQueriesEvent event) {
        // Preload cache mit häufigen Queries
        event.getFrequentQueries().parallelStream()
            .forEach(query -> {
                try {
                    retrievalCache.get(query, 
                        k -> contentRetriever.retrieve(Query.from(k)));
                } catch (Exception e) {
                    log.warn("Failed to preload query: " + query, e);
                }
            });
    }
}
```

**Monitoring und Observability:**
```java
@Component
public class RAGMonitoringService {
    
    private final MeterRegistry meterRegistry;
    private final Timer retrievalTimer;
    private final Timer generationTimer;
    private final Counter errorCounter;
    
    public RAGMonitoringService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.retrievalTimer = Timer.builder("rag.retrieval.duration")
            .description("Time spent on document retrieval")
            .register(meterRegistry);
        this.generationTimer = Timer.builder("rag.generation.duration")
            .description("Time spent on response generation")
            .register(meterRegistry);
        this.errorCounter = Counter.builder("rag.errors")
            .description("Number of RAG processing errors")
            .register(meterRegistry);
    }
    
    public String monitoredQuery(String query) {
        try {
            // Monitor retrieval
            List<Content> context = retrievalTimer.recordCallable(() -> 
                contentRetriever.retrieve(Query.from(query))
            );
            
            // Monitor generation
            String response = generationTimer.recordCallable(() -> 
                ragAssistant.answerWithContext(query, context)
            );
            
            // Record success metrics
            meterRegistry.counter("rag.successful_queries").increment();
            meterRegistry.gauge("rag.last_context_size", context.size());
            
            return response;
            
        } catch (Exception e) {
            errorCounter.increment();
            log.error("RAG query failed", e);
            throw new RAGProcessingException("Query processing failed", e);
        }
    }
}
```

**Resource Management:**
```java
@Configuration
public class RAGResourceConfig {
    
    @Bean
    @ConfigurationProperties("rag.thread-pool")
    public ThreadPoolTaskExecutor ragExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("rag-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
    
    @Bean
    public HikariDataSource vectorStoreDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/vectordb");
        config.setUsername("vectoruser");
        config.setPassword("vectorpass");
        config.setMaximumPoolSize(20);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setLeakDetectionThreshold(60000);
        return new HikariDataSource(config);
    }
}
```
