# Slide 25: Handling Sensitive Data

**Data Classification und Anonymization:**
```java
@Component
public class DataProtectionService {
    
    private final PIIDetector piiDetector;
    private final DataMasker dataMasker;
    
    public ProcessingResult processSafeDocument(Document document) {
        // 1. Detect PII
        PIIAnalysisResult analysis = piiDetector.analyze(document.text());
        
        if (analysis.containsPII()) {
            if (analysis.isHighRisk()) {
                // Reject processing completely
                return ProcessingResult.rejected("Contains high-risk PII");
            } else {
                // Anonymize vor processing
                String anonymized = dataMasker.anonymize(document.text(), analysis);
                document = Document.from(anonymized, document.metadata());
            }
        }
        
        return ProcessingResult.approved(document);
    }
}

// PII Detection Implementation  
@Service
public class PIIDetector {
    
    // Regex patterns für deutsche Identifikatoren
    private static final Pattern GERMAN_ID_PATTERN = 
        Pattern.compile("\\\\b\\\\d{2}\\\\s?\\\\d{2}\\\\s?\\\\d{2}\\\\s?\\\\d{2}\\\\s?\\\\d{3}\\\\b\");
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\\\.[a-zA-Z]{2,}");
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("\\\\+49\\\\s?\\\\d+|0\\\\d{3,4}\\\\s?\\\\d+");
        
    public PIIAnalysisResult analyze(String text) {
        List<PIIMatch> matches = new ArrayList<>();
        
        // Check für verschiedene PII-Typen
        matches.addAll(findMatches(text, GERMAN_ID_PATTERN, PIIType.SOCIAL_SECURITY));
        matches.addAll(findMatches(text, EMAIL_PATTERN, PIIType.EMAIL));
        matches.addAll(findMatches(text, PHONE_PATTERN, PIIType.PHONE));
        
        return new PIIAnalysisResult(matches);
    }
}
```

**Enterprise Security Patterns:**
```java
// Input Validation und Sanitization
@Component
public class InputValidator {
    
    private static final List<String> DANGEROUS_PATTERNS = Arrays.asList(
        "ignore previous instructions",
        "act as",
        "forget everything",
        "system:",
        "assistant:"
    );
    
    public ValidationResult validateInput(String input) {
        // 1. Check für Prompt Injection
        for (String pattern : DANGEROUS_PATTERNS) {
            if (input.toLowerCase().contains(pattern)) {
                return ValidationResult.rejected("Potential prompt injection detected");
            }
        }
        
        // 2. Length validation
        if (input.length() > MAX_INPUT_LENGTH) {
            return ValidationResult.rejected("Input too long");
        }
        
        // 3. Content filtering
        if (containsInappropriateContent(input)) {
            return ValidationResult.rejected("Inappropriate content detected");
        }
        
        return ValidationResult.approved();
    }
}

// Output Filtering
@Component  
public class OutputFilter {
    
    public String filterResponse(String response) {
        // 1. Remove any leaked system information
        response = removeSystemInfo(response);
        
        // 2. Mask any accidentally exposed PII
        response = maskPII(response);
        
        // 3. Apply content policy
        response = applyContentPolicy(response);
        
        return response;
    }
}
```

**GDPR-Compliant Deployment:**
```java
@Configuration
public class GDPRComplianceConfig {
    
    @Bean
    public AuditLogger auditLogger() {
        return AuditLogger.builder()
            .logPersonalDataAccess(true)
            .logDataProcessingPurpose(true)
            .retentionPeriod(Duration.ofDays(1095)) // 3 Jahre
            .anonymizeAfterRetention(true)
            .build();
    }
    
    @Bean
    public DataSubjectRightsHandler rightsHandler() {
        return new DataSubjectRightsHandler() {
            @Override
            public void handleAccessRequest(String userId) {
                // Alle gespeicherten Daten für User exportieren
            }
            
            @Override
            public void handleDeletionRequest(String userId) {
                // Alle personenbezogenen Daten löschen
                embeddingStore.removeAll(metadataKey("userId").isEqualTo(userId));
                auditService.logDeletion(userId);
            }
        };
    }
}
```
