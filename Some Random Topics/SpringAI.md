# Spring AI: A Senior Developer's Production Reference

> *If you've shipped distributed systems with Spring Boot, this guide skips the basics. It goes straight to how Spring AI works under the hood, the patterns that hold at scale, and the failure modes you will hit in production.*

---

## Table of Contents

**Part 1: Foundations**
1. [What is Spring AI -- Positioning and Architecture](#1-what-is-spring-ai)
2. [Legacy Spring vs Spring AI -- The Paradigm Shift](#2-legacy-spring-vs-spring-ai)
3. [Core Abstractions -- Deep Dive](#3-core-abstractions)
4. [Setup, Auto-Configuration and Multi-Provider](#4-setup-and-auto-configuration)
5. [ChatClient Builder and Fluent API Internals](#5-chatclient-builder)
6. [Prompt Engineering and Template Patterns](#6-prompt-engineering)

**Part 2: Production Patterns**
7. [RAG -- Production-Grade Implementation](#7-rag)
8. [Function Calling and Tool Use -- Internals](#8-function-calling)
9. [Advisors API -- Cross-Cutting Concerns for AI](#9-advisors-api)
10. [Structured Output -- Type-Safe LLM Responses](#10-structured-output)
11. [Observability, Cost Management and Monitoring](#11-observability)
12. [Testing AI Applications -- Strategies and Patterns](#12-testing)
13. [Error Handling, Resilience and Fallback Patterns](#13-error-handling)
14. [Real-World Architecture Patterns](#14-architecture-patterns)
15. [Anti-Patterns, Pitfalls and Production War Stories](#15-anti-patterns)
16. [Roadmap and Future-Proofing](#16-roadmap)

---

## 1. What is Spring AI

### The Problem Before Spring AI

Pre-Spring AI, integrating LLMs in Java meant writing provider-specific HTTP clients and managing divergent error models for every provider separately:

```
Your Application
    |
    +-> OpenAI SDK    -> HttpClient, specific request/response schema
    +-> Anthropic SDK -> Different HttpClient, different error codes
    +-> Ollama REST   -> Another client, different streaming protocol
    +-> Azure OpenAI  -> OpenAI wrapper but different endpoints and auth
```

Every provider switch required code changes. Streaming was incompatible across providers. Token counting differed. There was no standard abstraction.

### What Spring AI Actually Is

Spring AI is a **Spring-first AI integration framework** that:
- Provides a unified abstraction over LLM providers, vector stores, and embedding models
- Follows Spring conventions (auto-configuration, DI, `@Bean` lifecycle)
- Handles provider-specific HTTP plumbing, retry, streaming serialization, and error translation internally
- Is NOT a general AI toolkit, not a training framework, not a vector database

**The Spring Data analogy is exact:**

| Spring Data | Spring AI |
|---|---|
| `JpaRepository` abstraction | `ChatClient` abstraction |
| `DataSource` (configured once) | `ChatModel` (auto-configured) |
| `@Query` | `PromptTemplate` |
| `Pageable` result metadata | `ChatResponse` with `Usage` metadata |
| Provider-specific dialect | Provider-specific `ChatOptions` |
| `VectorStore` interface | `VectorStore` interface |

### Architecture Layers

```
+-----------------------------------------------------------------------+
|  Your Spring Boot Application                                         |
|  @RestController -> @Service -> @Repository                           |
+--------------------------------+--------------------------------------+
                                 | Constructor injection
+--------------------------------v--------------------------------------+
|  Spring AI Abstractions (spring-ai-core)                             |
|  +---------------+  +------------------+  +-----------------------+  |
|  |  ChatClient   |  | EmbeddingClient  |  |     VectorStore       |  |
|  |  (fluent API) |  | (text -> vector) |  |  (semantic search)    |  |
|  +------+--------+  +-------+----------+  +----------+------------+  |
|         |                   |                        |               |
|  +------v--------+  +-------v----------+  +----------v------------+  |
|  |   ChatModel   |  |  EmbeddingModel  |  |  VectorStore impl     |  |
|  |   (SPI)       |  |  (SPI)           |  |  (SPI)                |  |
+-------+------------------+---------------------------+--------------+
        |                  |                           |
+-------v------------------v---------------------------v--------------+
|  Provider Auto-Configuration (spring-ai-*-spring-boot-starter)      |
|  +-----------+  +------------+  +----------+  +------------------+  |
|  | OpenAI    |  | Anthropic  |  | Ollama   |  | pgvector         |  |
|  | ChatModel |  | ChatModel  |  | ChatModel|  | VectorStore      |  |
+-----------------------------------------------------------------------+
```

### What Spring AI Is NOT

| Misconception | Reality |
|---|---|
| Spring AI trains models | No. It integrates with pretrained models via API |
| Spring AI replaces LangChain4j | LangChain4j is more feature-rich for agents. Spring AI is Spring-ecosystem-first |
| Spring AI is a vector database | It provides `VectorStore` abstraction; underlying DB is yours |
| Spring AI requires OpenAI | Works with Anthropic, Ollama (local), Azure, Google, Mistral |
| Production-ready only on cloud | Ollama provider runs entirely locally -- no external API calls |

---

## 2. Legacy Spring vs Spring AI

A 15-year Spring veteran will recognize all Spring AI patterns -- they are Spring idioms applied to a new domain. But the *execution model* is fundamentally different.

### 2.1 Request-Response vs Streaming Token Pipeline

**Legacy Spring -- synchronous, atomic:**
```java
@GetMapping("/api/data")
public ResponseEntity<DataDto> getData() {
    DataDto data = externalService.fetchData();  // blocks 10ms, returns complete
    return ResponseEntity.ok(data);              // entire body sent at once
}
```

**Spring AI -- streaming, incremental:**
```java
@GetMapping(value = "/api/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<String> chatStream(@RequestParam String question) {
    return chatClient.prompt()
        .user(question)
        .stream()      // Flux<ChatResponse>, not a blocking call
        .content();    // Flux<String> of individual tokens
}
```

**Why streaming is the default mental model:**
- GPT-4 takes 3-15 seconds for a full response
- Without streaming: blank screen then sudden full text (terrible UX)
- With streaming: tokens appear progressively (ChatGPT UX)
- First token typically arrives in 200-500ms even if full response takes 8 seconds

### 2.2 Stateless Services vs Context-Window-Aware Conversation State

**The core LLM constraint:** LLMs are stateless at the API level. You must send the full conversation history on every call. The model has no memory of previous requests.

```java
// What actually happens on every LLM call:
Prompt prompt = new Prompt(List.of(
    new SystemMessage("You are a helpful assistant"),
    new UserMessage("My name is Alice"),           // Turn 1
    new AssistantMessage("Nice to meet you!"),     // Turn 1 response
    new UserMessage("What is my name?")            // Turn 2 -- needs history above
    // Without history: LLM says "I don't know your name"
    // With history:    LLM says "Your name is Alice"
));
```

**Context window constraints:**

| Model | Max Tokens | ~Words |
|---|---|---|
| GPT-3.5-turbo | 16K | 12,000 |
| GPT-4 Turbo | 128K | 96,000 |
| GPT-4o | 128K | 96,000 |
| Claude 3.5 Sonnet | 200K | 150,000 |
| Gemini 1.5 Pro | 1M | 750,000 |

**Production implication:** Long conversations hit limits. You must prune history, summarize, or use sliding-window strategies.

### 2.3 Deterministic vs Probabilistic Execution

This is the biggest shift for backend developers trained on determinism.

| Property | Legacy Spring | Spring AI |
|---|---|---|
| Same input -> same output | Always | Temperature=0: usually; Temperature>0: varies |
| Unit test with exact assertions | Standard | Anti-pattern |
| Retry is safe | Yes | Maybe (same error can repeat) |
| Debug by reproducing | Trivial | Hard (non-deterministic by design) |
| P99 latency predictable | Yes | Varies with model load and token count |

**Temperature controls randomness:**
```java
// Deterministic (use for extraction, classification)
ChatOptions deterministic = OpenAiChatOptions.builder()
    .withTemperature(0.0f)   // greedy decoding
    .withSeed(42)            // reproducible (OpenAI supports seed)
    .build();

// Creative (use for content generation)
ChatOptions creative = OpenAiChatOptions.builder()
    .withTemperature(0.9f)
    .withTopP(0.95f)
    .build();
```

### 2.4 Strongly Typed DTOs vs Structured Extraction

**Legacy Spring:** compile-time safe, runtime validated DTOs.

**Spring AI:** LLMs output text. You need infrastructure to reliably extract typed data.

```java
// Naive approach (fragile -- format unpredictable)
String raw = chatClient.prompt()
    .user("Extract name and age from: 'I am Bob, 25'")
    .call().content();
// Response might be: "Name: Bob, Age: 25"  OR "Bob is 25 years old" -- unpredictable

// Spring AI approach (reliable)
public record PersonExtract(String name, int age) {}

PersonExtract person = chatClient.prompt()
    .user("Extract from: 'I am Bob, 25'")
    .call()
    .entity(PersonExtract.class);  // generates schema, forces JSON, parses to type
// person.name() == "Bob", person.age() == 25 -- always typed
```

### 2.5 Cost Model -- Server Resources vs Token Usage

```
Legacy cost model:
  Cost proportional to (RPS * server_time * instance_cost)
  Optimization: reduce server time, use smaller instances, CDN caching

Spring AI cost model:
  Cost proportional to (requests * tokens_per_request * price_per_token)
  Optimization: reduce token count, choose cheaper models, cache LLM responses

Real numbers (OpenAI, approximate):
  GPT-3.5-turbo: $0.001/1K input,  $0.002/1K output
  GPT-4-turbo:   $0.010/1K input,  $0.030/1K output
  GPT-4o:        $0.005/1K input,  $0.015/1K output

Example RAG query with 3K context + 500 output tokens on GPT-4-turbo:
  Input:  3000 * $0.010/1000 = $0.030
  Output:  500 * $0.030/1000 = $0.015
  Per request: $0.045
  At 10K requests/day: $450/day = $13,500/month
```

### Paradigm Comparison Table

| Dimension | Legacy Spring | Spring AI |
|---|---|---|
| Response model | Synchronous, complete | Streaming, incremental |
| State management | Stateless (explicit session) | Context-window management |
| Output predictability | Deterministic | Probabilistic (temperature-controlled) |
| Data typing | Compile-time safe | Runtime extraction + validation |
| Latency SLA | <100ms P99 | <5s P99 typical; streaming hides tail |
| Cost driver | Infrastructure | Token usage (API calls) |
| Error handling | Exception, retry | Rate limit, backoff, re-prompt, fallback |
| Testing approach | Exact assertions | Semantic assertions + contract tests |
| Debugging | Stack trace | Prompt + response logging |

---

## 3. Core Abstractions

### 3.1 ChatClient vs ChatModel -- Know the Difference

**`ChatModel`** is the low-level SPI that maps directly to a provider's API:

```java
// ChatModel: direct, low-level SPI (you rarely use this)
public interface ChatModel {
    ChatResponse call(Prompt prompt);
    Flux<ChatResponse> stream(Prompt prompt);
}
```

**`ChatClient`** is the high-level fluent API -- the one you always use:

```java
ChatClient client = ChatClient.builder(chatModel)
    .defaultSystem("You are an expert Java backend developer assistant")
    .defaultOptions(OpenAiChatOptions.builder()
        .withModel("gpt-4o")
        .withTemperature(0.3f)
        .build())
    .build();

// Usage: clean, no boilerplate
String response = client.prompt()
    .user("Explain MVCC in PostgreSQL")
    .call()
    .content();
```

**Auto-configuration wires them together:**
```java
// What Spring Boot auto-config does under the hood:
@Bean
@ConditionalOnMissingBean
public ChatClient.Builder chatClientBuilder(OpenAiChatModel model) {
    return ChatClient.builder(model);
}

@Bean
@ConditionalOnMissingBean
public ChatClient chatClient(ChatClient.Builder builder) {
    return builder.build();
}
```

Override the `ChatClient.Builder` bean to set application-wide defaults:
```java
@Configuration
public class AiConfig {
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
            .defaultSystem("""
                You are an expert Java backend developer assistant.
                Always provide code examples in Java 17+ syntax.
                Be concise but thorough.
                """)
            .defaultOptions(OpenAiChatOptions.builder()
                .withModel("gpt-4o")
                .withTemperature(0.3f)
                .withMaxTokens(2000)
                .build())
            .build();
    }
}
```

### 3.2 Prompt and Message Hierarchy

```
Prompt
  |
  +-- List<Message>
  |     +-- SystemMessage       "You are a helpful assistant"
  |     +-- UserMessage         "What is 2+2?"
  |     +-- AssistantMessage    "2+2 is 4"  (history)
  |     +-- ToolResponseMessage  result of @Tool function call
  |
  +-- ChatOptions               model params (temperature, max_tokens, model name)
```

**Full Prompt construction:**
```java
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;

Prompt prompt = new Prompt(
    List.of(
        new SystemMessage("""
            You are a helpful assistant for Java backend developers.
            Respond with technical detail appropriate for a senior developer.
            """),
        new UserMessage("How does Spring AI handle streaming under the hood?"),
        new AssistantMessage("Spring AI uses Project Reactor's Flux..."),  // history
        new UserMessage("Show me a production-ready example")               // current turn
    ),
    OpenAiChatOptions.builder()
        .withModel("gpt-4o")
        .withTemperature(0.2f)
        .withMaxTokens(1500)
        .build()
);

ChatResponse response = chatClient.call(prompt);
```

**Provider-specific options** for model-specific features:
```java
// OpenAI-specific: JSON mode, seed, logprobs
OpenAiChatOptions openAi = OpenAiChatOptions.builder()
    .withModel("gpt-4o")
    .withResponseFormat(new ResponseFormat(ResponseFormat.Type.JSON_OBJECT))
    .withSeed(42)         // reproducible output
    .withTemperature(0.0f)
    .build();

// Anthropic-specific: extended thinking
AnthropicChatOptions anthropic = AnthropicChatOptions.builder()
    .withModel("claude-3-5-sonnet-20241022")
    .withMaxTokens(8096)
    .build();

// Ollama-specific: local model tuning
OllamaChatOptions ollama = OllamaChatOptions.builder()
    .withModel("llama3.2")
    .withNumCtx(8192)    // context window size
    .withTemperature(0.7f)
    .build();
```

### 3.3 ChatResponse -- Extracting Everything

```java
ChatResponse response = chatClient.prompt()
    .user("Explain Spring AI")
    .call();

// Content
String content = response.getResult().getOutput().getContent();

// Token usage -- critical for cost tracking
Usage usage = response.getMetadata().getUsage();
long promptTokens     = usage.getPromptTokens();
long completionTokens = usage.getGenerationTokens();
double estimatedCost  = (promptTokens * 0.005 + completionTokens * 0.015) / 1000.0;

// Finish reason -- why the LLM stopped
String finishReason = response.getResult().getMetadata().getFinishReason();
// "stop"         -> normal completion
// "length"       -> hit max_tokens limit (response may be cut off!)
// "tool_calls"   -> LLM wants to call a function
// "content_filter" -> content policy triggered

if ("length".equals(finishReason)) {
    logger.warn("Response truncated -- increase max_tokens or reduce prompt");
}

// Multiple candidates (when n > 1 in options)
List<Generation> generations = response.getResults();
```

### 3.4 Streaming -- Flux Integration

```java
// Token-by-token Flux<String>
Flux<String> tokens = chatClient.prompt()
    .user("Write a guide to Spring AI")
    .stream()
    .content();

// Full ChatResponse per chunk (includes metadata)
Flux<ChatResponse> chunks = chatClient.prompt()
    .user("Write a guide")
    .stream()
    .chatResponse();

// Production SSE endpoint
@GetMapping(value = "/api/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<ServerSentEvent<String>> chatStream(@RequestParam String message) {
    return chatClient.prompt()
        .user(message)
        .stream()
        .content()
        .map(token -> ServerSentEvent.<String>builder()
            .data(token)
            .build())
        .concatWith(Flux.just(
            ServerSentEvent.<String>builder()
                .event("done")
                .data("[DONE]")
                .build()))
        .doOnError(e -> logger.error("Stream error: {}", e.getMessage()));
}
```

### 3.5 ChatMemory -- Conversation Persistence

```java
// In-memory (default, not persistent across restarts)
@Bean
public ChatMemory inMemoryChatMemory() {
    return new InMemoryChatMemory();
}

// Redis-backed implementation for production
@Component
public class RedisChatMemory implements ChatMemory {
    private final StringRedisTemplate redis;
    private final ObjectMapper mapper;
    private static final String PREFIX = "chat:memory:";
    private static final Duration TTL  = Duration.ofHours(24);

    @Override
    public void add(String conversationId, List<Message> messages) {
        String key = PREFIX + conversationId;
        messages.forEach(msg -> {
            try {
                String json = mapper.writeValueAsString(
                    Map.of("type", msg.getMessageType().getValue(),
                           "content", msg.getContent()));
                redis.opsForList().rightPush(key, json);
                redis.expire(key, TTL);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        String key  = PREFIX + conversationId;
        long   size = redis.opsForList().size(key);
        long   from = Math.max(0, size - lastN);
        List<String> raw = redis.opsForList().range(key, from, -1);
        if (raw == null) return List.of();

        return raw.stream().map(json -> {
            try {
                Map<?, ?> m = mapper.readValue(json, Map.class);
                String type    = (String) m.get("type");
                String content = (String) m.get("content");
                return switch (type) {
                    case "user"      -> (Message) new UserMessage(content);
                    case "assistant" -> new AssistantMessage(content);
                    default          -> new SystemMessage(content);
                };
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    @Override
    public void clear(String conversationId) {
        redis.delete(PREFIX + conversationId);
    }
}
```

### 3.6 EmbeddingClient and VectorStore

```java
@Service
public class EmbeddingService {
    private final EmbeddingClient embeddingClient;

    // Single text embedding
    public float[] embed(String text) {
        return embeddingClient.embed(text);
        // text-embedding-3-small: 1536-dimensional vector
        // text-embedding-3-large: 3072-dimensional vector
    }

    // Batch embedding (cheaper per-token than individual calls)
    public List<float[]> embedBatch(List<String> texts) {
        EmbeddingRequest request = new EmbeddingRequest(texts, EmbeddingOptions.EMPTY);
        return embeddingClient.embedForResponse(request)
            .getResults().stream()
            .map(Embedding::getOutput)
            .toList();
    }
}
```

**pgvector setup (production vector store):**
```yaml
spring:
  ai:
    vectorstore:
      pgvector:
        index-type: HNSW
        distance-type: COSINE_DISTANCE
        dimensions: 1536        # must match embedding model
        initialize-schema: false  # NEVER true in production
```

```sql
-- What Spring AI creates
CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE vector_store (
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content   TEXT,
    metadata  JSONB,
    embedding vector(1536)
);

-- HNSW index for approximate nearest-neighbor search
CREATE INDEX ON vector_store USING hnsw (embedding vector_cosine_ops)
    WITH (m = 16, ef_construction = 64);
```

---

## 4. Setup and Auto-Configuration

### 4.1 BOM-Managed Dependencies

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-bom</artifactId>
            <version>1.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <!-- LLM providers -- include what you need -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-anthropic-spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-ollama-spring-boot-starter</artifactId>
    </dependency>

    <!-- Vector stores -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-pgvector-store-spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-redis-store-spring-boot-starter</artifactId>
    </dependency>

    <!-- Document loaders -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-pdf-document-reader</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-tika-document-reader</artifactId>
    </dependency>
</dependencies>
```

### 4.2 Auto-Configuration Internals

Knowing how auto-config works helps debug "why is the wrong model being used?" problems:

```java
// Simplified view of OpenAiAutoConfiguration
@Configuration
@ConditionalOnClass(OpenAiApi.class)
@EnableConfigurationProperties(OpenAiConnectionProperties.class)
public class OpenAiAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.ai.openai", name = "api-key")
    public OpenAiApi openAiApi(OpenAiConnectionProperties props) {
        return new OpenAiApi(props.getBaseUrl(), props.getApiKey());
    }

    @Bean
    @ConditionalOnMissingBean
    public OpenAiChatModel openAiChatModel(OpenAiApi api,
                                           OpenAiChatProperties props) {
        return new OpenAiChatModel(api, props.getOptions());
    }

    @Bean
    @ConditionalOnMissingBean
    public ChatClient.Builder chatClientBuilder(OpenAiChatModel model) {
        return ChatClient.builder(model);
    }
}
```

**Key annotations:**
- `@ConditionalOnMissingBean` -- your `@Bean` overrides auto-config
- `@ConditionalOnProperty` -- auto-config only activates if `spring.ai.openai.api-key` is set
- `@ConditionalOnClass` -- auto-config only activates if the provider JAR is on classpath

### 4.3 Multi-Provider Configuration

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4o
          temperature: 0.7
    anthropic:
      api-key: ${ANTHROPIC_API_KEY}
      chat:
        options:
          model: claude-3-5-sonnet-20241022
          max-tokens: 4096
    ollama:
      base-url: http://localhost:11434
      chat:
        options:
          model: llama3.2

    retry:
      max-attempts: 3
      initial-interval: 2s
      multiplier: 2.0
      max-interval: 30s
      on-http-codes: 429,500,503
      exclude-on-http-codes: 400,401,403
```

**Routing between providers:**
```java
@Configuration
public class MultiProviderConfig {

    @Bean("openAiChatClient")
    public ChatClient openAiChatClient(OpenAiChatModel model) {
        return ChatClient.builder(model)
            .defaultOptions(OpenAiChatOptions.builder()
                .withModel("gpt-4o").withTemperature(0.3f).build())
            .build();
    }

    @Bean("anthropicChatClient")
    public ChatClient anthropicChatClient(AnthropicChatModel model) {
        return ChatClient.builder(model)
            .defaultOptions(AnthropicChatOptions.builder()
                .withModel("claude-3-5-sonnet-20241022").withMaxTokens(4096).build())
            .build();
    }

    @Bean("ollamaChatClient")
    public ChatClient ollamaChatClient(OllamaChatModel model) {
        return ChatClient.builder(model)
            .defaultOptions(OllamaChatOptions.builder().withModel("llama3.2").build())
            .build();
    }
}

@Service
public class ModelRouter {
    @Qualifier("openAiChatClient")    private final ChatClient openAi;
    @Qualifier("anthropicChatClient") private final ChatClient anthropic;
    @Qualifier("ollamaChatClient")    private final ChatClient ollama;

    public ChatClient select(TaskType task) {
        return switch (task) {
            case COMPLEX_REASONING   -> openAi;       // GPT-4o
            case LONG_DOCUMENT       -> anthropic;    // Claude 200K context
            case SENSITIVE_INTERNAL  -> ollama;       // no data leaves infra
        };
    }
}
```

---

## 5. ChatClient Builder

### 5.1 What the Builder Creates

`ChatClient` is immutable. The builder sets application-level defaults applied to every call:

```java
ChatClient client = ChatClient.builder(chatModel)
    // Default system prompt (applied to every .prompt() call)
    .defaultSystem("You are an expert Java developer. Use Java 17+ syntax.")

    // Default options (overridable per call)
    .defaultOptions(OpenAiChatOptions.builder()
        .withModel("gpt-4o")
        .withTemperature(0.3f)
        .withMaxTokens(2000)
        .build())

    // Default advisors (cross-cutting concerns -- see Section 9)
    .defaultAdvisors(
        new MessageChatMemoryAdvisor(chatMemory),
        new SimpleLoggerAdvisor())

    .build();
```

### 5.2 The Full Call Chain

```java
String result = client.prompt()
    // Message construction
    .system("Override the default system prompt for this call")
    .user("Explain the Builder pattern in Java")
    .messages(conversationHistory)  // inject history

    // Per-call option overrides (higher priority than defaults)
    .options(OpenAiChatOptions.builder()
        .withModel("gpt-3.5-turbo")  // override model for this call
        .withTemperature(0.9f)
        .build())

    // Tool registration for this call
    .tools(new WeatherTools(), new CalendarTools())

    // Blocking call variants
    .call()
    .content();                        // String

// Other terminal operations:
ChatResponse full = client.prompt().user("Q").call().chatResponse();
PersonInfo   typed = client.prompt().user("Q").call().entity(PersonInfo.class);
Flux<String> stream = client.prompt().user("Q").stream().content();
```

### 5.3 System Message Templating

```java
// Parameterized system prompt
return client.prompt()
    .system(s -> s.text("""
        You are a support agent for {company}.
        Customer tier: {tier}
        Member since: {since}
        """)
        .param("company", "TechCorp")
        .param("tier", user.getTier())
        .param("since", user.getCreatedAt().getYear()))
    .user(userMessage)
    .call()
    .content();
```

**PromptTemplate for reusable templates:**
```java
@Configuration
public class PromptConfig {

    @Bean
    public PromptTemplate codeReviewTemplate() {
        return new PromptTemplate("""
            Review the following {language} code for:
            1. Security vulnerabilities
            2. Performance issues
            3. SOLID principle violations
            4. Missing error handling

            Code:
            ```{language}
            {code}
            ```
            Provide actionable feedback for a senior developer.
            """);
    }
}

@Service
public class CodeReviewService {
    private final ChatClient chatClient;
    private final PromptTemplate codeReviewTemplate;

    public String review(String language, String code) {
        Prompt prompt = codeReviewTemplate.create(
            Map.of("language", language, "code", code));
        return chatClient.call(prompt)
            .getResult().getOutput().getContent();
    }
}
```

### 5.4 Per-Call Options Override Pattern

```java
@Service
public class AdaptiveAiService {
    // Default: GPT-3.5, temp=0.7
    private final ChatClient chatClient;

    // Cheap default for simple tasks
    public String quickSummary(String text) {
        return chatClient.prompt()
            .user("Summarize in 2 sentences: " + text)
            .call().content();
    }

    // Expensive override for complex reasoning
    public String deepAnalysis(String text) {
        return chatClient.prompt()
            .user("Analyze in depth: " + text)
            .options(OpenAiChatOptions.builder()
                .withModel("gpt-4o")
                .withTemperature(0.1f)
                .withMaxTokens(4000)
                .build())
            .call().content();
    }
}
```

### 5.5 Prompt Templates in External Files

Store prompts as resources for large-scale applications:

```
src/main/resources/prompts/
    rag-answer.st
    code-review.st
    sentiment-classify.st
```

```java
// src/main/resources/prompts/rag-answer.st
Answer the user's question based ONLY on the provided context.
If the answer is not in the context, respond with exactly: INSUFFICIENT_CONTEXT
Do not use general knowledge.

Context:
{context}

Question: {question}
```

```java
@Service
public class RagAnswerService {
    private final ChatClient chatClient;

    @Value("classpath:prompts/rag-answer.st")
    private Resource ragPromptResource;

    public String answer(String question, List<Document> docs) {
        PromptTemplate template = new PromptTemplate(ragPromptResource);
        String contextText = docs.stream()
            .map(d -> "[Source: " + d.getMetadata().get("source") + "]\n" + d.getContent())
            .collect(Collectors.joining("\n\n---\n\n"));

        Prompt prompt = template.create(
            Map.of("context", contextText, "question", question));

        return chatClient.call(prompt).getResult().getOutput().getContent();
    }
}
```

---

## 6. Prompt Engineering

Prompt engineering is a first-class production concern. The prompt is your program; the LLM is the interpreter.

### 6.1 System Prompt Architecture

The system prompt defines persona, constraints, output format, and context. Treat it as a configuration file:

```java
// Anti-pattern: vague
String bad = "You are a helpful assistant";

// Production: specific, constrained, format-enforced
String good = """
    ## Role
    You are a backend API documentation generator for a Java Spring Boot application.

    ## Responsibilities
    - Generate OpenAPI-compatible endpoint documentation
    - Infer request/response schemas from provided code
    - Flag security issues in API design

    ## Constraints
    - Only document public @RestController methods
    - Never invent endpoints not present in the code
    - If code is ambiguous, say "AMBIGUOUS: [reason]" rather than guessing

    ## Output Format
    Always respond in this exact JSON structure:
    {
      "endpoint": "POST /api/v1/...",
      "description": "...",
      "requestBody": {...},
      "responses": {...},
      "securityNotes": [...]
    }

    ## Context
    Application: {appName}
    Java Version: {javaVersion}
    Spring Boot: {springBootVersion}
    """;
```

### 6.2 Few-Shot Prompting

Examples in the prompt dramatically improve consistency for classification and extraction:

```java
@Service
public class SentimentClassifier {
    private final ChatClient chatClient;

    private static final String SYSTEM = """
        Classify customer feedback sentiment as POSITIVE, NEGATIVE, or NEUTRAL.
        Output only the single classification word, nothing else.

        Examples:
        Input: "This product saved me hours of work!"
        Output: POSITIVE

        Input: "The service was slow and support unhelpful."
        Output: NEGATIVE

        Input: "Product arrived on time."
        Output: NEUTRAL
        """;

    public Sentiment classify(String feedback) {
        String result = chatClient.prompt()
            .system(SYSTEM)
            .user(feedback)
            .options(OpenAiChatOptions.builder()
                .withTemperature(0.0f)  // deterministic for classification
                .withMaxTokens(10)       // only need one word
                .build())
            .call().content()
            .trim().toUpperCase();

        return Sentiment.valueOf(result);
    }
}
```

### 6.3 Chain-of-Thought Prompting

Force the model to reason step-by-step before producing output:

```java
@Service
public class SecurityAnalysisService {
    private final ChatClient chatClient;

    public SecurityAnalysis analyzeCode(String code) {
        String raw = chatClient.prompt()
            .system("""
                When analyzing code for security issues:
                1. First identify all external inputs
                2. Trace data flow from inputs to sinks (DB, HTTP, file)
                3. Check each sink for injection risks
                4. Evaluate authentication and authorization paths
                5. Only after steps 1-4, produce your assessment

                Format:
                <thinking>
                [Step-by-step analysis]
                </thinking>
                <assessment>
                {"riskLevel":"HIGH|MEDIUM|LOW","vulnerabilities":[...],"recommendations":[...]}
                </assessment>
                """)
            .user("Analyze:\n```java\n" + code + "\n```")
            .options(OpenAiChatOptions.builder()
                .withModel("gpt-4o").withTemperature(0.1f).build())
            .call().content();

        // Extract JSON from <assessment> block
        int start = raw.indexOf("<assessment>") + 12;
        int end   = raw.indexOf("</assessment>");
        return objectMapper.readValue(raw.substring(start, end).trim(),
            SecurityAnalysis.class);
    }
}
```

### 6.4 Output Format Enforcement and Retry

For non-.entity() scenarios where you need controlled output:

```java
private static final String JSON_FORMAT = """
    Respond ONLY with valid JSON. No markdown, no code blocks, no explanations.
    JSON must match:
    {
      "issues": [{"severity":"HIGH|MEDIUM|LOW","line":<number>,
                  "description":"<string>","fix":"<string>"}],
      "summary": "<string>",
      "passesReview": <boolean>
    }
    """;

public ReviewResult reviewWithRetry(String code, int maxAttempts) {
    for (int attempt = 1; attempt <= maxAttempts; attempt++) {
        String userContent = attempt == 1
            ? "Review:\n" + code
            : "Previous attempt failed parsing. Fix the JSON and retry.\n"
              + "Error: " + lastError + "\nOriginal code:\n" + code;
        try {
            String response = chatClient.prompt()
                .system(JSON_FORMAT)
                .user(userContent)
                .call().content();
            return objectMapper.readValue(response, ReviewResult.class);
        } catch (JsonProcessingException e) {
            lastError = e.getMessage();
            logger.warn("Attempt {}/{} parse failed: {}", attempt, maxAttempts, lastError);
        }
    }
    throw new ReviewParseException("Exhausted " + maxAttempts + " attempts");
}
```

---

## 7. RAG

RAG (Retrieval-Augmented Generation) is the most important pattern for enterprise AI. Naive RAG works in demos; production RAG requires significantly more engineering.

### 7.1 The Complete Pipeline

```
INGESTION (offline):
  Raw Docs -> Document Loader -> TokenTextSplitter -> EmbeddingClient -> VectorStore
  (PDF/Word)                   (512-token chunks)   (text -> float[])  (pgvector)

QUERY (online, per request):
  User Question -> Embed Query -> Similarity Search -> Top-K Docs
                                                            |
                                               Build Augmented Prompt
                                                            |
                                                      LLM Response
```

### 7.2 Document Loading and Chunking

```java
@Service
public class DocumentIngestionService {
    private final VectorStore vectorStore;
    private final TokenTextSplitter splitter;

    public DocumentIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        // 512-token chunks with 128-token overlap is the standard starting point
        // Too small = lost context; too large = diluted relevance
        this.splitter = new TokenTextSplitter(512, 128, 5, 10000, true);
    }

    public void ingestPdf(Resource pdf, Map<String, Object> metadata) {
        PagePdfDocumentReader reader = new PagePdfDocumentReader(
            pdf,
            PdfDocumentReaderConfig.builder()
                .withPageExtractedTextFormatter(
                    new ExtractedTextFormatter.Builder()
                        .withNumberOfBottomTextLinesToDelete(3)  // remove page footers
                        .build())
                .withPagesPerDocument(1)
                .build()
        );

        List<Document> chunks = splitter.apply(reader.get());
        chunks.forEach(c -> {
            c.getMetadata().putAll(metadata);
            c.getMetadata().put("source",     pdf.getFilename());
            c.getMetadata().put("ingestedAt", Instant.now().toString());
        });

        vectorStore.add(chunks);
        logger.info("Ingested {} chunks from {}", chunks.size(), pdf.getFilename());
    }

    public void ingestUrl(String url, String category) {
        List<Document> chunks = splitter.apply(new TikaDocumentReader(url).get());
        chunks.forEach(c -> {
            c.getMetadata().put("source_url", url);
            c.getMetadata().put("category",   category);
        });
        vectorStore.add(chunks);
    }
}
```

### 7.3 Production RAG Service

```java
@Service
public class ProductionRagService {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    private static final String RAG_SYSTEM = """
        Answer questions based ONLY on the provided context.
        Rules:
        1. If the answer is in the context, answer accurately and cite [Source: filename].
        2. If the answer is NOT in the context, say "I don't have that information."
        3. Never use general training knowledge -- only the provided context.

        Context:
        {context}
        """;

    public RagResponse answer(RagRequest request) {
        // Optional metadata filter (tenant isolation, category scoping)
        FilterExpressionBuilder b = new FilterExpressionBuilder();
        Filter.Expression filter  = request.category() != null
            ? b.eq("category", request.category()).build() : null;

        List<Document> docs = vectorStore.similaritySearch(
            SearchRequest.query(request.question())
                .withTopK(5)
                .withSimilarityThreshold(0.70)
                .withFilterExpression(filter));

        if (docs.isEmpty()) return RagResponse.noContext();

        String context = docs.stream()
            .map(d -> "[Source: " + d.getMetadata().getOrDefault("source","?") + "]\n"
                      + d.getContent())
            .collect(Collectors.joining("\n\n---\n\n"));

        String answer = chatClient.prompt()
            .system(s -> s.text(RAG_SYSTEM).param("context", context))
            .user(request.question())
            .options(OpenAiChatOptions.builder()
                .withTemperature(0.1f).withMaxTokens(1000).build())
            .call().content();

        List<String> sources = docs.stream()
            .map(d -> (String) d.getMetadata().getOrDefault("source","unknown"))
            .distinct().toList();

        return new RagResponse(answer, sources, docs.size());
    }
}

public record RagRequest(String question, String category) {}
public record RagResponse(String answer, List<String> sources, int documentsUsed) {
    public static RagResponse noContext() {
        return new RagResponse("No relevant information found.", List.of(), 0);
    }
}
```

### 7.4 Hybrid Search -- Keyword + Semantic

Pure vector search misses exact matches (product codes, IDs, proper nouns). Production RAG combines both:

```java
@Service
public class HybridSearchService {
    private final VectorStore vectorStore;
    private final JdbcTemplate jdbc;

    public List<Document> hybridSearch(String query, int topK) {
        // 1. Semantic (vector cosine similarity)
        List<Document> semantic = vectorStore.similaritySearch(
            SearchRequest.query(query).withTopK(topK));

        // 2. Keyword (PostgreSQL full-text search, BM25-style ranking)
        List<Document> keyword = jdbc.query("""
            SELECT id, content, metadata::text
            FROM vector_store
            WHERE to_tsvector('english', content) @@ plainto_tsquery('english', ?)
            ORDER BY ts_rank(to_tsvector('english', content),
                             plainto_tsquery('english', ?)) DESC
            LIMIT ?
            """,
            (rs, row) -> new Document(rs.getString("id"),
                                      rs.getString("content"), Map.of()),
            query, query, topK);

        // 3. Reciprocal Rank Fusion (RRF) merge
        return rrfMerge(semantic, keyword, topK);
    }

    private List<Document> rrfMerge(List<Document> a, List<Document> b, int k) {
        int rrf = 60;
        Map<String, Double> scores = new HashMap<>();
        for (int i = 0; i < a.size(); i++)
            scores.merge(a.get(i).getId(), 1.0 / (rrf + i + 1), Double::sum);
        for (int i = 0; i < b.size(); i++)
            scores.merge(b.get(i).getId(), 1.0 / (rrf + i + 1), Double::sum);

        Map<String, Document> all = Stream.concat(a.stream(), b.stream())
            .collect(Collectors.toMap(Document::getId, d -> d, (x, y) -> x));

        return scores.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(k)
            .map(e -> all.get(e.getKey()))
            .filter(Objects::nonNull)
            .toList();
    }
}
```

### 7.5 RAG Anti-Patterns

| Anti-pattern | Problem | Fix |
|---|---|---|
| Chunk = entire document | Too much irrelevant text dilutes relevance | 512-token chunks, 128-token overlap |
| No similarity threshold | Returns unrelated documents | `.withSimilarityThreshold(0.70)` |
| Pure semantic search | Misses exact-match queries | Hybrid search (semantic + BM25) |
| No metadata filter | Cross-tenant data leakage | Always filter by `tenantId` or `category` |
| Not logging context used | Impossible to debug wrong answers | Log chunk IDs and sources per query |
| Re-embedding on every read | Slow, expensive | Embed once at ingestion; re-embed on document update |

---

## 8. Function Calling

### 8.1 The Multi-Turn Protocol

Spring AI automates this two-round protocol transparently:

```
Round 1: App -> LLM
  messages:  [system, user: "What is the weather in Tokyo?"]
  tools:     [getWeather(city: string) -> string]

Round 1: LLM -> App
  finish_reason: "tool_calls"
  tool_calls:    [{id:"call_1", name:"getWeather", args:{"city":"Tokyo"}}]

Spring AI internally calls: weatherTools.getWeather("Tokyo") -> "Sunny, 28C"

Round 2: App -> LLM
  messages: [system, user, assistant(tool_calls), tool_result: "Sunny, 28C"]

Round 2: LLM -> App
  finish_reason: "stop"
  content: "The weather in Tokyo is currently sunny at 28 degrees Celsius."
```

### 8.2 @Tool Annotation

```java
@Component  // must be a Spring bean
public class EnterpriseTools {

    private final CustomerRepository customerRepo;
    private final OrderRepository     orderRepo;
    private final AuditLogger         audit;

    @Tool(description = "Look up a customer by UUID or email address")
    public CustomerInfo getCustomer(
            @ToolParam(description = "Customer UUID or email") String identifier) {
        if (identifier.contains("@")) {
            return customerRepo.findByEmail(identifier)
                .map(CustomerInfo::from).orElse(CustomerInfo.notFound());
        }
        return customerRepo.findById(UUID.fromString(identifier))
            .map(CustomerInfo::from).orElse(CustomerInfo.notFound());
    }

    @Tool(description = "Search orders by customer and date range. Returns up to 10.")
    public List<OrderSummary> searchOrders(
            @ToolParam(description = "Customer UUID")          String customerId,
            @ToolParam(description = "Start date (ISO-8601)") String startDate,
            @ToolParam(description = "End date (ISO-8601)")   String endDate) {
        return orderRepo.findByCustomerAndDateRange(
                UUID.fromString(customerId),
                LocalDate.parse(startDate),
                LocalDate.parse(endDate))
            .stream().limit(10).map(OrderSummary::from).toList();
    }

    // Write operations -- always audit LLM-triggered mutations
    @Tool(description = "Cancel an order. Call only after explicit customer confirmation.")
    public String cancelOrder(
            @ToolParam(description = "Order UUID to cancel") String orderId,
            @ToolParam(description = "Cancellation reason")  String reason) {
        audit.log("TOOL_CANCEL_ORDER", orderId, "AI_AGENT", reason);
        orderRepo.cancel(UUID.fromString(orderId), reason);
        return "Order " + orderId + " cancelled successfully.";
    }
}
```

### 8.3 Tool Security -- Prompt Injection Defence

**Critical:** LLMs can be prompt-injected to call tools with malicious arguments. Always authorise tool inputs:

```java
@Component
public class SecureOrderTools {

    @Tool(description = "Get order details for the current authenticated user")
    public OrderDetails getMyOrder(
            @ToolParam(description = "Order UUID") String orderId) {

        String currentUserId = SecurityContextHolder.getContext()
            .getAuthentication().getName();

        Order order = orderRepo.findById(UUID.fromString(orderId))
            .orElseThrow(() -> new OrderNotFoundException(orderId));

        // ALWAYS check ownership -- LLM could be injected to access other users' orders
        if (!order.getCustomerId().toString().equals(currentUserId)) {
            audit.logSuspicious("TOOL_UNAUTHORIZED", orderId, currentUserId);
            throw new AccessDeniedException("Order not accessible");
        }
        return OrderDetails.from(order);
    }
}
```

### 8.4 Tool Registration Strategies

```java
// Global tools -- always available
@Bean
public ChatClient chatClient(ChatClient.Builder builder, EnterpriseTools tools) {
    return builder.defaultTools(tools).build();
}

// Conditional tools -- register based on user role
public String handleQuery(String query, UserContext ctx) {
    var req = chatClient.prompt().system("You are a support agent.").user(query);
    if (ctx.hasRole("ORDER_VIEW"))     req = req.tools(orderTools);
    if (ctx.hasRole("INVENTORY_VIEW")) req = req.tools(inventoryTools);
    return req.call().content();
}
```

### 8.5 Agentic Loop -- Multi-Step Tool Iteration

```java
@Service
public class ResearchAgent {
    private final ChatClient chatClient;

    public String research(String topic) {
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage("""
            You are a research agent. Use available tools to gather information.
            Continue calling tools until you have enough to answer, then stop.
            """));
        messages.add(new UserMessage("Research: " + topic));

        // Safety limit: max 10 tool-call rounds
        for (int round = 0; round < 10; round++) {
            ChatResponse response = chatClient.call(
                new Prompt(messages, OpenAiChatOptions.builder()
                    .withModel("gpt-4o").build()));

            messages.add(response.getResult().getOutput());

            if ("stop".equals(response.getResult().getMetadata().getFinishReason())) {
                return response.getResult().getOutput().getContent();
            }
            // "tool_calls" -> Spring AI called tools, results added to messages
        }
        throw new AgentException("Agent exceeded max tool-call rounds for: " + topic);
    }
}
```

---

## 9. Advisors API

Advisors are Spring AI's AOP equivalent -- they intercept every LLM call to inject cross-cutting behaviour without touching business logic.

### 9.1 How the Chain Works

```
chatClient.prompt().user("...").call()
    |
    v  Pre-processing (ascending order)
    +-> PiiMaskingAdvisor     (order=HIGHEST_PRECEDENCE) -> masks PII in prompt
    +-> TokenBudgetAdvisor    (order=100)                -> enforces token limit
    +-> MessageChatMemoryAdvisor (order=0)               -> injects history
    +-> QuestionAnswerAdvisor    (order=0)               -> injects RAG context
    |
    v  LLM API call
    |
    v  Post-processing (descending order)
    +-> QuestionAnswerAdvisor    -> records which docs were used
    +-> MessageChatMemoryAdvisor -> saves response to memory
    +-> TokenBudgetAdvisor       -> records token metrics
    +-> PiiMaskingAdvisor        -> logs masking events
```

### 9.2 Built-In Advisors

```java
@Configuration
public class AdvisorConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder,
                                 VectorStore vectorStore, ChatMemory memory) {
        return builder
            // RAG: auto-retrieve and inject relevant context
            .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.defaults()
                    .withTopK(4)
                    .withSimilarityThreshold(0.70))
                .build())
            // Memory: auto-manage conversation history
            .defaultAdvisors(MessageChatMemoryAdvisor.builder(memory).build())
            // Logging: log prompts and responses
            .defaultAdvisors(new SimpleLoggerAdvisor())
            .build();
    }
}

// Usage -- all advisor behaviour is transparent
@Service
public class ConversationalRagService {
    private final ChatClient chatClient;

    public String chat(String conversationId, String question) {
        // Automatically: RAG context injected + conversation history + logged
        return chatClient.prompt()
            .advisors(a -> a.param(
                AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY,
                conversationId))
            .user(question)
            .call().content();
    }
}
```

### 9.3 Custom Advisor -- PII Masking

```java
@Component
public class PiiMaskingAdvisor implements CallAroundAdvisor {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}");
    private static final Pattern CC_PATTERN =
        Pattern.compile("\\b(?:\\d{4}[\\s\\-]?){3}\\d{4}\\b");
    private static final Pattern SSN_PATTERN =
        Pattern.compile("\\b\\d{3}-\\d{2}-\\d{4}\\b");

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest req, CallAroundAdvisorChain chain) {
        AdvisedRequest masked = maskPii(req);

        if (!Objects.equals(masked.userText(), req.userText())) {
            logger.info("PII masked before LLM call, conversation={}",
                req.adviseContext().get("conversationId"));
        }

        return chain.nextAroundCall(masked);
    }

    private AdvisedRequest maskPii(AdvisedRequest req) {
        String text = req.userText();
        if (text == null) return req;
        text = EMAIL_PATTERN.matcher(text).replaceAll("[EMAIL]");
        text = CC_PATTERN   .matcher(text).replaceAll("[CC_NUMBER]");
        text = SSN_PATTERN  .matcher(text).replaceAll("[SSN]");
        return AdvisedRequest.from(req).withUserText(text).build();
    }

    @Override public String getName()  { return "PiiMaskingAdvisor"; }
    @Override public int    getOrder() { return Ordered.HIGHEST_PRECEDENCE; }
}
```

### 9.4 Custom Advisor -- Token Budget Guard

```java
@Component
public class TokenBudgetAdvisor implements CallAroundAdvisor {
    private static final int MAX_ESTIMATED_TOKENS = 3000;
    private final MeterRegistry metrics;

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest req, CallAroundAdvisorChain chain) {
        // Pre-call: rough estimate (1 token ~= 4 chars)
        int estimated = req.userText() != null ? req.userText().length() / 4 : 0;
        if (estimated > MAX_ESTIMATED_TOKENS) {
            metrics.counter("ai.budget.exceeded", "type", "prompt").increment();
            throw new TokenBudgetExceededException(
                "Prompt too long (~" + estimated + " estimated tokens)");
        }

        AdvisedResponse response = chain.nextAroundCall(req);

        // Post-call: record actual usage
        Usage usage = response.response().getMetadata().getUsage();
        metrics.counter("ai.tokens.prompt")    .increment(usage.getPromptTokens());
        metrics.counter("ai.tokens.completion").increment(usage.getGenerationTokens());
        return response;
    }

    @Override public String getName()  { return "TokenBudgetAdvisor"; }
    @Override public int    getOrder() { return 100; }
}
```

### 9.5 Advisor Ordering Rule of Thumb

| Order | Advisor | Reason |
|---|---|---|
| `HIGHEST_PRECEDENCE` | PII masking | Must run first -- before ANY data leaves your infra |
| `100` | Token budget | Catch oversize prompts before calling LLM |
| `0` (default) | MessageChatMemoryAdvisor | Injects history after budget check |
| `0` (default) | QuestionAnswerAdvisor | Injects RAG context after budget check |
| `LOWEST_PRECEDENCE` | SimpleLoggerAdvisor | Logs final request and response |

---

## 10. Structured Output

### 10.1 The .entity() Method

`entity()` is the cleanest extraction path -- Spring AI generates JSON schema, forces structured output, parses and validates automatically:

```java
// Simple record
public record BookInfo(
    String title, String author, int publicationYear,
    String genre, double averageRating) {}

BookInfo book = chatClient.prompt()
    .user("Extract: 'Effective Java by Joshua Bloch, 2018, Java, rated 4.8'")
    .call()
    .entity(BookInfo.class);
// book.title() == "Effective Java", book.publicationYear() == 2018

// Nested complex structure
public record ResumeExtract(
    String name, String email,
    List<String> skills,
    List<WorkExperience> experience) {}

public record WorkExperience(
    String company, String role,
    String startDate, String endDate,
    List<String> responsibilities) {}

ResumeExtract resume = chatClient.prompt()
    .user("Extract from this resume:\n" + resumeText)
    .call()
    .entity(ResumeExtract.class);
```

### 10.2 List Extraction

```java
// List of primitives
List<String> keywords = chatClient.prompt()
    .user("Extract 10 technical terms from: " + document)
    .call()
    .entity(new ParameterizedTypeReference<List<String>>() {});

// List of objects
public record ApiEndpoint(String httpMethod, String path,
                           String description, List<String> parameters) {}

List<ApiEndpoint> endpoints = chatClient.prompt()
    .user("List all REST endpoints in:\n" + controllerCode)
    .call()
    .entity(new ParameterizedTypeReference<List<ApiEndpoint>>() {});
```

### 10.3 How Structured Output Works Internally

```
Does model support native JSON mode?
    YES (OpenAI, some Anthropic) -> sets response_format: {type: "json_object"}
                                    + appends JSON schema to system prompt
    NO  -> appends format instructions: "Respond with JSON matching schema: {...}"

Parsing:
    Spring AI parses response -> Jackson ObjectMapper -> target type
    On failure -> retries with error feedback (up to 3 times by default)
```

```java
// Force JSON mode explicitly
String json = chatClient.prompt()
    .user("Extract all methods as JSON array")
    .options(OpenAiChatOptions.builder()
        .withResponseFormat(new ResponseFormat(ResponseFormat.Type.JSON_OBJECT))
        .withTemperature(0.0f)
        .build())
    .call().content();
```

### 10.4 Robust Extraction with Retry

```java
@Service
public class RobustExtractorService {
    private final ChatClient chatClient;

    public <T> T extract(String text, Class<T> targetType) {
        String lastError = null;
        for (int attempt = 1; attempt <= 3; attempt++) {
            String userText = attempt == 1 ? text
                : "Previous attempt failed: " + lastError
                  + "\nPlease fix the JSON. Original text:\n" + text;
            try {
                return chatClient.prompt()
                    .user(userText)
                    .options(OpenAiChatOptions.builder()
                        .withResponseFormat(new ResponseFormat(
                            ResponseFormat.Type.JSON_OBJECT))
                        .withTemperature(0.0f)
                        .build())
                    .call()
                    .entity(targetType);
            } catch (Exception e) {
                lastError = e.getMessage();
                logger.warn("Extraction attempt {}/3 failed: {}", attempt, lastError);
            }
        }
        throw new ExtractionException("Failed after 3 attempts: " + targetType.getSimpleName());
    }
}
```

### 10.5 API Selection Guide

| Scenario | API | Reason |
|---|---|---|
| Simple record from text | `.entity(MyRecord.class)` | Handles schema + parse automatically |
| List of objects | `.entity(new ParameterizedTypeReference<List<T>>(){})` | Generic type support |
| Custom validation | `BeanOutputConverter` | Access raw schema and response |
| Need raw JSON string | `.call().content()` + Jackson | Direct control over parsing |

---

## 11. Observability

### 11.1 Auto-Published Micrometer Metrics

With `spring-boot-actuator` on classpath, Spring AI auto-publishes:

```yaml
# Expose metrics endpoint
management:
  endpoints:
    web:
      exposure:
        include: health, metrics, prometheus
  metrics:
    tags:
      application: ${spring.application.name}
      env: ${spring.profiles.active:local}
```

Auto-published metrics:
```
gen_ai_client_operation_duration_seconds{
    gen_ai_operation_name="chat",
    gen_ai_system="openai",
    gen_ai_request_model="gpt-4o",
    status="success"}

gen_ai_client_token_usage_total{gen_ai_token_type="input"}
gen_ai_client_token_usage_total{gen_ai_token_type="output"}
```

### 11.2 Cost Tracking Advisor

Centralise cost recording so every call is tracked automatically:

```java
@Component
public class CostTrackingAdvisor implements CallAroundAdvisor {

    private static final Map<String, double[]> COST_PER_1K = Map.of(
        "gpt-4o",            new double[]{0.005, 0.015},
        "gpt-4-turbo",       new double[]{0.010, 0.030},
        "gpt-3.5-turbo",     new double[]{0.001, 0.002},
        "claude-3-5-sonnet", new double[]{0.003, 0.015},
        "claude-3-haiku",    new double[]{0.00025, 0.00125}
    );

    private final MeterRegistry metrics;

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest req, CallAroundAdvisorChain chain) {
        long start    = System.currentTimeMillis();
        AdvisedResponse response = chain.nextAroundCall(req);
        long latencyMs = System.currentTimeMillis() - start;

        Usage  usage  = response.response().getMetadata().getUsage();
        String model  = resolveModel(response);
        double[] cost = COST_PER_1K.getOrDefault(model, new double[]{0, 0});
        double total  = (usage.getPromptTokens()     * cost[0]
                        + usage.getGenerationTokens() * cost[1]) / 1000.0;

        Tags tags = Tags.of("model", model);
        metrics.counter("ai.cost.usd",           tags).increment(total);
        metrics.counter("ai.tokens.prompt",      tags).increment(usage.getPromptTokens());
        metrics.counter("ai.tokens.completion",  tags).increment(usage.getGenerationTokens());
        metrics.timer  ("ai.latency",            tags).record(latencyMs, TimeUnit.MILLISECONDS);

        if (total > 0.10) {
            logger.warn("HIGH COST call: ${} model={} in={}tok out={}tok",
                String.format("%.4f", total), model,
                usage.getPromptTokens(), usage.getGenerationTokens());
        }
        return response;
    }

    private String resolveModel(AdvisedResponse r) {
        try { return (String) r.response().getMetadata().getModel(); }
        catch (Exception e) { return "unknown"; }
    }

    @Override public String getName()  { return "CostTrackingAdvisor"; }
    @Override public int    getOrder() { return Ordered.LOWEST_PRECEDENCE; }
}
```

### 11.3 Prometheus Alerting Rules

```yaml
groups:
  - name: spring_ai
    rules:
      - alert: AiHighErrorRate
        expr: |
          rate(gen_ai_client_operation_duration_seconds_count{status="error"}[5m])
          / rate(gen_ai_client_operation_duration_seconds_count[5m]) > 0.05
        for: 2m
        labels: { severity: warning }
        annotations: { summary: "AI error rate > 5%" }

      - alert: AiHighP95Latency
        expr: |
          histogram_quantile(0.95,
            rate(gen_ai_client_operation_duration_seconds_bucket[5m])) > 15
        for: 5m
        labels: { severity: warning }
        annotations: { summary: "AI P95 latency > 15s" }

      - alert: AiCostSpike
        expr: increase(ai_cost_usd_total[1h]) > 50
        for: 0m
        labels: { severity: critical }
        annotations: { summary: "AI spend > $50 in last hour" }

      - alert: AiHighOutputTokenRate
        expr: rate(ai_tokens_completion_total[5m]) > 10000
        for: 5m
        labels: { severity: warning }
        annotations: { summary: "High output token rate -- check for prompt loops" }
```

### 11.4 Logging Strategy

```java
@Component
@Slf4j
public class AiAuditLogger {

    // LOG THESE:
    public void logRequest(String convId, String model, int promptLen, String endpoint) {
        log.info("AI_REQ conv={} model={} chars={} endpoint={}",
            convId, model, promptLen, endpoint);
    }

    public void logResponse(String convId, Usage usage, String finishReason, long ms) {
        log.info("AI_RESP conv={} in={}tok out={}tok finish={} latencyMs={}",
            convId, usage.getPromptTokens(), usage.getGenerationTokens(),
            finishReason, ms);
        if ("length".equals(finishReason)) {
            log.warn("Response truncated -- increase max_tokens. conv={}", convId);
        }
    }

    // DO NOT LOG:
    // - Full prompt text in production (may contain PII from user input)
    // - Raw API keys (appear in stack traces if not masked)
    // - RAG context content if source docs are confidential
    // - Full LLM responses (may contain extracted PII)
}
```

---

## 12. Testing

Testing LLM applications requires a layered strategy. Standard unit testing assumptions (determinism, exact assertions) do not hold.

### 12.1 The Testing Pyramid

```
         /\
        /  \  E2E AI Tests (few -- expensive, slow)
       /    \ Real model + real data + semantic assertions
      /------\
     /        \  Integration Tests (moderate)
    /  Testcontainers + Ollama (no API key needed)
   /----------\
  /            \  Unit Tests (majority)
 / Mock ChatClient -- test business logic only
/--------------\
```

### 12.2 Unit Testing -- Extracting Testable Logic

`ChatClient` uses a fluent chain that is hard to mock. The recommended pattern extracts a testable functional interface:

```java
@Service
public class CustomerClassificationService {
    private final ChatClient chatClient;

    public CustomerTier classifyCustomer(CustomerProfile profile) {
        String result = chatClient.prompt()
            .system("Classify tier as BRONZE, SILVER, GOLD, or PLATINUM. One word only.")
            .user(buildPrompt(profile))
            .options(OpenAiChatOptions.builder().withTemperature(0.0f).build())
            .call().content().trim().toUpperCase();
        return CustomerTier.valueOf(result);
    }

    // Package-private for direct unit testing
    String buildPrompt(CustomerProfile p) {
        return String.format("Annual spend: $%d, Account age: %d months, Return rate: %.1f%%",
            p.annualSpend(), p.accountAgeMonths(), p.returnRate() * 100);
    }
}

// Test the prompt-building logic directly -- no LLM needed
class CustomerClassificationServiceTest {

    private final ChatClient mockClient = mock(ChatClient.class);
    private final CustomerClassificationService svc =
        new CustomerClassificationService(mockClient);

    @Test
    void buildPrompt_shouldFormatAllFields() {
        String prompt = svc.buildPrompt(new CustomerProfile(5000, 24, 0.05));
        assertThat(prompt)
            .contains("$5000")
            .contains("24 months")
            .contains("5.0%");
    }
}
```

### 12.3 Integration Testing with Testcontainers + Ollama

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-testcontainers</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>ollama</artifactId>
    <scope>test</scope>
</dependency>
```

```java
@SpringBootTest
@Testcontainers
class RagServiceIntegrationTest {

    @Container
    static OllamaContainer ollama =
        new OllamaContainer("ollama/ollama:latest").withModel("llama3.2");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry reg) {
        reg.add("spring.ai.ollama.base-url", ollama::getEndpoint);
        reg.add("spring.ai.ollama.chat.options.model", () -> "llama3.2");
    }

    @Autowired private RagService ragService;
    @Autowired private VectorStore vectorStore;

    @BeforeEach
    void loadTestDocs() {
        vectorStore.add(List.of(
            new Document("Spring Boot 3.3 requires Java 17 minimum.",
                Map.of("source", "spring-docs.txt")),
            new Document("Java 21 introduces virtual threads via Project Loom.",
                Map.of("source", "java-release-notes.txt"))
        ));
    }

    @Test
    void shouldAnswerFromContext() {
        RagResponse resp = ragService.answer(
            new RagRequest("What Java version does Spring Boot 3.3 require?", null));

        // Semantic assertion -- never assert exact string
        assertThat(resp.answer().toLowerCase()).contains("java 17");
        assertThat(resp.sources()).anyMatch(s -> s.contains("spring-docs"));
    }

    @Test
    void shouldReturnNoContextWhenNotFound() {
        RagResponse resp = ragService.answer(
            new RagRequest("What is the population of Mars?", null));
        assertThat(resp.documentsUsed()).isZero();
    }
}
```

### 12.4 Prompt Contract Testing

Validate classification accuracy across a suite of known examples:

```java
@SpringBootTest
@Tag("prompt-contract")
class SentimentClassifierContractTest {

    @Autowired private SentimentClassifier classifier;

    static Stream<Arguments> positiveExamples() {
        return Stream.of(
            Arguments.of("This product is absolutely amazing!"),
            Arguments.of("Incredible service, exceeded all expectations."),
            Arguments.of("Best purchase I have made this year!")
        );
    }

    @ParameterizedTest
    @MethodSource("positiveExamples")
    void positiveTextShouldClassifyAsPositive(String text) {
        assertThat(classifier.classify(text))
            .withFailMessage("Got wrong sentiment for: '%s'", text)
            .isEqualTo(Sentiment.POSITIVE);
    }

    @RepeatedTest(3)
    void classificationShouldBeConsistentForClearInput() {
        String clear = "This is terrible, worst product ever.";
        // Temperature=0 should give same result 3 times
        Set<Sentiment> results = Set.of(
            classifier.classify(clear),
            classifier.classify(clear),
            classifier.classify(clear));
        assertThat(results).hasSize(1);  // all three agree
    }
}
```

### 12.5 WireMock for LLM API Stubbing

Fast, deterministic tests without any LLM:

```java
@SpringBootTest
@AutoConfigureWireMock(port = 0)
class ChatServiceWireMockTest {

    @Autowired private ChatService chatService;

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry reg) {
        reg.add("spring.ai.openai.base-url",
            () -> "http://localhost:${wiremock.server.port}");
        reg.add("spring.ai.openai.api-key", () -> "test-key");
    }

    @Test
    void shouldRetryOnRateLimit() {
        stubFor(post(urlEqualTo("/v1/chat/completions"))
            .inScenario("retry").whenScenarioStateIs(STARTED)
            .willReturn(aResponse().withStatus(429)
                .withHeader("Retry-After", "1")
                .withBody("{\"error\":{\"type\":\"rate_limit_error\"}}"))
            .willSetStateTo("retried"));

        stubFor(post(urlEqualTo("/v1/chat/completions"))
            .inScenario("retry").whenScenarioStateIs("retried")
            .willReturn(aResponse().withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(successBody("Hello! How can I help?"))));

        assertThat(chatService.chat("Hi")).isEqualTo("Hello! How can I help?");
        verify(2, postRequestedFor(urlEqualTo("/v1/chat/completions")));
    }

    private String successBody(String content) {
        return """
            {"id":"test","choices":[{"index":0,
            "message":{"role":"assistant","content":"%s"},"finish_reason":"stop"}],
            "usage":{"prompt_tokens":10,"completion_tokens":8,"total_tokens":18}}
            """.formatted(content);
    }
}
```

---

## 13. Error Handling

### 13.1 Spring AI Exception Hierarchy

```
RuntimeException
  +-- SpringAIException
        +-- ChatClientException
        |     +-- RateLimitException      (429 -- too many requests)
        |     +-- TokenLimitException     (prompt exceeds context window)
        |     +-- ContentFilterException  (content policy violation)
        |     +-- ChatClientTimeoutException
        +-- EmbeddingException
        +-- VectorStoreException
```

Built-in retry config:
```yaml
spring:
  ai:
    retry:
      max-attempts: 3
      initial-interval: 2s
      multiplier: 2.0         # 2s, 4s, 8s
      max-interval: 30s
      on-http-codes: 429,500,503
      exclude-on-http-codes: 400,401,403  # never retry auth or client errors
```

### 13.2 Resilience4j -- Circuit Breaker Integration

```xml
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
</dependency>
```

```yaml
resilience4j:
  circuitbreaker:
    instances:
      aiPrimary:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 30s
        permitted-calls-in-half-open-state: 3
  timelimiter:
    instances:
      aiPrimary:
        timeout-duration: 30s
```

```java
@Service
public class ResilientAiService {
    private final ChatClient primary;   // GPT-4o
    private final ChatClient fallback;  // GPT-3.5-turbo (cheaper)
    private final ChatClient local;     // Ollama (always available)

    @CircuitBreaker(name = "aiPrimary", fallbackMethod = "fallbackChat")
    @TimeLimiter(name = "aiPrimary")
    public CompletableFuture<String> chat(String message) {
        return CompletableFuture.supplyAsync(() ->
            primary.prompt().user(message).call().content());
    }

    public CompletableFuture<String> fallbackChat(String message, Exception ex) {
        logger.warn("Primary AI failed ({}), trying fallback", ex.getClass().getSimpleName());
        return CompletableFuture.supplyAsync(() ->
            fallback.prompt().user(message).call().content());
    }
}
```

### 13.3 Comprehensive Error Handling

```java
@Service
public class SafeAiService {
    private final ChatClient chatClient;

    public String chat(String message) {
        try {
            return chatClient.prompt()
                .user(message)
                .options(OpenAiChatOptions.builder().withMaxTokens(500).build())
                .call().content();

        } catch (RateLimitException e) {
            throw new ServiceUnavailableException(
                "AI service busy. Retry after " + e.getRetryAfter() + "s");

        } catch (TokenLimitException e) {
            logger.error("Token limit exceeded: {}", e.getMessage());
            throw new BadRequestException(
                "Your message is too long. Please shorten it.");

        } catch (ContentFilterException e) {
            return "I cannot respond to that type of request.";

        } catch (ChatClientTimeoutException e) {
            logger.warn("LLM timeout for message length: {}", message.length());
            return "I am taking longer than expected. Please try a shorter question.";

        } catch (Exception e) {
            logger.error("Unexpected AI error", e);
            return "I am temporarily unavailable. Please try again shortly.";
        }
    }
}
```

### 13.4 Context Window Overflow Management

```java
@Service
public class ContextWindowManager {
    private final ChatClient chatClient;
    private final ChatMemory memory;
    private static final int SUMMARIZE_THRESHOLD = 6000;  // estimated tokens

    public String chat(String conversationId, String message) {
        List<Message> history = memory.get(conversationId, Integer.MAX_VALUE);
        int estimated = history.stream()
            .mapToInt(m -> m.getContent().length() / 4).sum();

        if (estimated > SUMMARIZE_THRESHOLD) {
            List<Message> toSummarise = history.subList(0, history.size() / 2);
            String summary = summarise(toSummarise);

            memory.clear(conversationId);
            memory.add(conversationId,
                List.of(new SystemMessage("[Summary of earlier conversation]: " + summary)));
            history = memory.get(conversationId, Integer.MAX_VALUE);
        }

        history.add(new UserMessage(message));
        ChatResponse resp = chatClient.call(new Prompt(history));
        String reply = resp.getResult().getOutput().getContent();

        memory.add(conversationId, List.of(
            new UserMessage(message), new AssistantMessage(reply)));

        return reply;
    }

    private String summarise(List<Message> messages) {
        String conv = messages.stream()
            .map(m -> m.getMessageType() + ": " + m.getContent())
            .collect(Collectors.joining("\n"));
        return chatClient.prompt()
            .system("Summarise this conversation in 3-5 sentences.")
            .user(conv)
            .call().content();
    }
}
```

### 13.5 Error Handling Decision Matrix

| Exception | Root Cause | Action |
|---|---|---|
| `RateLimitException` | 429 -- too many requests | Retry with exponential backoff, fallback model |
| `TokenLimitException` | Prompt exceeds context window | Prune history, summarise, use larger-context model |
| `ContentFilterException` | Content policy violation | User-friendly message, log for review |
| `ChatClientTimeoutException` | Model too slow | Cheaper/faster model, cached response |
| `ApiException` (5xx) | Provider outage | Circuit breaker, Ollama fallback |
| `JsonProcessingException` | Bad structured output | Retry with error feedback in prompt |
| `AccessDeniedException` (tool) | Prompt injection attempt | Deny silently, audit log immediately |

---

## 14. Real-World Architecture Patterns

### 14.1 Conversational Support Agent with Streaming

```java
@RestController
@RequestMapping("/api/support")
public class SupportController {
    private final SupportAgentService agentService;

    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chat(
            @RequestParam String sessionId,
            @RequestParam String message,
            @AuthenticationPrincipal UserDetails user) {

        return agentService.chatStream(user.getUsername(), sessionId, message)
            .map(tok -> ServerSentEvent.<String>builder().data(tok).build())
            .concatWith(Flux.just(
                ServerSentEvent.<String>builder().event("done").data("[DONE]").build()));
    }
}

@Service
public class SupportAgentService {
    private final ChatClient   chatClient;
    private final UserService  userService;

    public Flux<String> chatStream(String userId, String sessionId, String message) {
        User user = userService.findById(userId);
        return chatClient.prompt()
            .system(s -> s.text("""
                You are a support agent for TechCorp.
                Customer: {name} | Tier: {tier} | Since: {since}
                Be helpful, professional, and concise. Max 200 words per response.
                Escalate complex billing issues with "ESCALATE: [reason]".
                """)
                .param("name",  user.getName())
                .param("tier",  user.getTier())
                .param("since", user.getCreatedAt().getYear()))
            .advisors(a -> a.param(
                AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, sessionId))
            .tools(orderTools, billingTools, knowledgeBaseTools)
            .user(message)
            .stream().content();
    }
}
```

### 14.2 Multi-Model Content Generation Pipeline

Cost-optimise by tiering cheap and expensive models:

```java
@Service
public class ContentPipeline {
    private final ChatClient draftClient;      // GPT-3.5 -- fast, cheap
    private final ChatClient refinementClient; // GPT-4o  -- quality
    private final ChatClient classifierClient; // GPT-3.5 -- cheap extraction

    public GeneratedContent generate(String topic, String tone) {
        // Stage 1: Draft (cheap)
        String draft = draftClient.prompt()
            .system("Write a first draft. Focus on content, not polish.")
            .user("Topic: " + topic + "\nTone: " + tone)
            .call().content();

        // Stage 2: Quality check (cheap + structured output)
        DraftQuality quality = classifierClient.prompt()
            .user("Evaluate this draft:\n" + draft)
            .call()
            .entity(DraftQuality.class);

        // Stage 3: Refine only if quality is low (expensive model only when needed)
        String finalContent = quality.score() < 7
            ? refinementClient.prompt()
                .system("You are an expert editor. Improve grammar, flow, and depth.")
                .user(draft).call().content()
            : draft;

        return new GeneratedContent(finalContent, quality.score(), draft);
    }
}

public record DraftQuality(int score, String feedback, List<String> improvements) {}
```

### 14.3 Multi-Agent Orchestration

```java
@Service
public class ResearchOrchestrator {
    private final ChatClient chatClient;

    public ResearchReport research(String topic) {
        // Agent 1: Decompose into sub-questions
        List<String> subQuestions = chatClient.prompt()
            .system("Generate 3-5 specific sub-questions to research this topic thoroughly.")
            .user(topic)
            .call()
            .entity(new ParameterizedTypeReference<List<String>>() {});

        // Agent 2: Research each sub-question in parallel
        List<CompletableFuture<String>> futures = subQuestions.stream()
            .map(q -> CompletableFuture.supplyAsync(() ->
                chatClient.prompt()
                    .system("You are a research specialist. Provide detailed findings.")
                    .user("Research question: " + q)
                    .options(OpenAiChatOptions.builder()
                        .withModel("gpt-4o").build())
                    .call().content()))
            .toList();

        List<String> findings = futures.stream()
            .map(CompletableFuture::join).toList();

        // Agent 3: Synthesise into a structured report
        String combined = IntStream.range(0, subQuestions.size())
            .mapToObj(i -> "Q: " + subQuestions.get(i) + "\nA: " + findings.get(i))
            .collect(Collectors.joining("\n\n"));

        String synthesis = chatClient.prompt()
            .system("""
                You are a senior analyst. Synthesise research findings into a structured report
                with: Executive Summary, Key Findings, Recommendations.
                """)
            .user("Topic: " + topic + "\n\nFindings:\n" + combined)
            .options(OpenAiChatOptions.builder().withModel("gpt-4o").build())
            .call().content();

        return new ResearchReport(topic, subQuestions, findings, synthesis);
    }
}
```

### 14.4 Batch Document Intelligence

```java
@Service
public class DocumentIntelligencePipeline {
    private final DocumentIngestionService ingestion;

    @Scheduled(cron = "0 2 * * * *")  // 2 AM daily re-index
    public void reindexUpdatedDocuments() {
        List<Resource> updated =
            documentRepository.findUpdatedSince(LocalDate.now().minusDays(1));

        updated.parallelStream().forEach(doc -> {
            try {
                ingestion.ingestPdf(doc, Map.of(
                    "category", "technical",
                    "indexedAt", Instant.now().toString()));
            } catch (Exception e) {
                logger.error("Failed to ingest: {}", doc.getFilename(), e);
            }
        });

        logger.info("Re-indexed {} documents", updated.size());
    }
}
```

---

## 15. Anti-Patterns

### 15.1 Blocking on Streaming

```java
// WRONG: blocks the thread, no streaming benefit
@GetMapping("/chat")
public String chat(String message) {
    return chatClient.prompt().user(message).stream().content()
        .collectList().map(t -> String.join("", t)).block();  // anti-pattern
}

// RIGHT (blocking call): use .call() directly
@GetMapping("/chat")
public String chat(String message) {
    return chatClient.prompt().user(message).call().content();
}

// RIGHT (streaming): return Flux -- WebFlux handles it
@GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<String> chatStream(String message) {
    return chatClient.prompt().user(message).stream().content();
}
```

### 15.2 Ignoring finish_reason

```java
// WRONG: assumes response is always complete
String answer = response.getResult().getOutput().getContent();

// RIGHT: check why the model stopped
String reason = response.getResult().getMetadata().getFinishReason();
String content = response.getResult().getOutput().getContent();
if ("length".equals(reason)) {
    logger.warn("Response truncated -- increase max_tokens");
    content = content + " [truncated]";
} else if ("content_filter".equals(reason)) {
    content = "I cannot respond to that request.";
}
```

### 15.3 No Token Budgeting

```java
// WRONG: no guard on large inputs
@PostMapping("/summarize")
public String summarize(@RequestBody String doc) {
    return chatClient.prompt().user("Summarize: " + doc).call().content();
    // doc could be 50MB -- costs $500+ per call
}

// RIGHT: enforce limits
@PostMapping("/summarize")
public String summarize(@RequestBody String doc) {
    if (doc.length() > 100_000) {
        throw new BadRequestException("Document too large (max 100K characters)");
    }
    return chatClient.prompt()
        .user("Summarize: " + doc)
        .options(OpenAiChatOptions.builder().withMaxTokens(1000).build())
        .call().content();
}
```

### 15.4 No Tenant Isolation in RAG

```java
// WRONG: returns any tenant's documents
List<Document> docs = vectorStore.similaritySearch(
    SearchRequest.query(question).withTopK(5));

// RIGHT: always filter by tenantId
List<Document> docs = vectorStore.similaritySearch(
    SearchRequest.query(question)
        .withTopK(5)
        .withFilterExpression(
            new FilterExpressionBuilder()
                .eq("tenantId", currentTenantId).build()));

// During ingestion: always tag with tenantId
chunks.forEach(c -> c.getMetadata().put("tenantId", tenantId));
vectorStore.add(chunks);
```

### 15.5 No Prompt Versioning

```java
// WRONG: inline prompt, no version tracking
chatClient.prompt()
    .system("You are helpful")  // someone edits this -- everything breaks silently
    .user(message).call().content();

// RIGHT: treat prompts as versioned code
public final class PromptLibrary {
    // Track in git, reference in logs, A/B test between versions
    public static final String CLASSIFIER_V2_1 =
        "[sentiment-classifier v2.1 2024-12-01] Classify as POSITIVE/NEGATIVE/NEUTRAL...";

    public static final String RAG_ANSWER_V1_3 =
        "[rag-answer v1.3 2024-11-15] Answer from context only...";
}
```

### 15.6 Production Incident: Multi-Tenant RAG Data Leakage

**Scenario:** Multi-tenant SaaS. Company A's chatbot returns Company B's internal pricing documents.

**Root cause:** Vector store queries had no `tenantId` filter. All tenants' documents shared the same index.

**Impact:** PII and confidential business data exposed across tenant boundaries. GDPR violation.

**Fix:**
```java
// Mandatory: filter on every query
SearchRequest.query(question)
    .withFilterExpression(
        filterBuilder.eq("tenantId", SecurityUtils.getCurrentTenantId()).build());

// Mandatory: tag every document at ingestion time
chunks.forEach(c -> c.getMetadata().put("tenantId", tenantId));
```

**Lesson:** Treat `tenantId` filtering like authentication -- it is not optional.

### 15.7 Production Incident: Thundering Herd on Cold Start

**Scenario:** New service pod starts. 500 users hit simultaneously. Each request triggers RAG + LLM. No concurrency limit. 500 parallel API calls hit OpenAI rate limit. All 500 fail with 429.

**Fix:**
```java
// Bounded concurrency -- max N simultaneous LLM calls
@Service
public class BoundedAiService {
    private final Semaphore semaphore = new Semaphore(20);
    private final ChatClient chatClient;

    public String chat(String message) throws InterruptedException {
        if (!semaphore.tryAcquire(5, TimeUnit.SECONDS)) {
            throw new ServiceUnavailableException("AI service at capacity. Retry shortly.");
        }
        try {
            return chatClient.prompt().user(message).call().content();
        } finally {
            semaphore.release();
        }
    }
}
```

### 15.8 Common Pitfalls Summary

| Pitfall | Impact | Prevention |
|---|---|---|
| Blocking on Flux | Thread starvation in WebFlux | Use `.call()` for blocking; return `Flux` for streaming |
| Ignoring finish_reason | Silently truncated responses | Always check; warn on "length" |
| No input size limits | Surprise $100+ bills per request | Validate character count before calling |
| No tenant filter on RAG | Data leakage between tenants | Mandatory `withFilterExpression(tenantFilter)` |
| Inline prompts, no versioning | Silent regressions on edit | Prompts as named constants tracked in git |
| Mocking ChatClient fluent chain | Fragile, hard-to-maintain tests | Extract testable interface; test logic separately |
| No circuit breaker | Cascading failures during outage | Resilience4j + Ollama fallback |
| Parallel calls without limit | Rate limit storms | Semaphore or rate limiter on concurrent calls |

---

## 16. Roadmap

### 16.1 Spring AI 1.0 Feature Set

| Feature | Status | Notes |
|---|---|---|
| ChatClient fluent API | Stable | Primary interaction model |
| Streaming (Flux/SSE) | Stable | Full WebFlux integration |
| Function calling / @Tool | Stable | Parallel tool calls supported |
| Structured output / .entity() | Stable | Records, generics, nested types |
| Advisors API | Stable | Custom advisors supported |
| RAG / VectorStore | Stable | pgvector, Redis, Milvus, Pinecone, Chroma |
| Document loaders | Stable | PDF, Tika (Word, HTML, Markdown) |
| ChatMemory | Stable | InMemory; custom backends |
| Micrometer metrics | Stable | Auto-published |
| Multi-modal (images) | Experimental | OpenAI Vision, Anthropic Claude |
| Agent framework | In progress | Autonomous multi-step agents |

### 16.2 Upcoming Directions

- **Agentic workflows** -- first-class planning agents, sub-agent delegation, tool-calling loops
- **Multi-modal** -- stable image and audio input across providers
- **Structured streaming** -- stream directly into typed objects token by token
- **Prompt management** -- versioning, A/B testing, rollback for prompts
- **Cost governance** -- per-user token budgets and quota enforcement built in
- **Improved local model support** -- better Ollama tooling, quantised model selection

### 16.3 Future-Proofing Rules

```java
// Rule 1: Always depend on the abstraction
@Service
public class MyService {
    private final ChatClient chatClient;  // NOT OpenAiChatModel
    // Swap provider in config -- zero code change
}

// Rule 2: Config-driven model selection
@Value("${ai.model.default:gpt-4o}")
private String defaultModel;

// Rule 3: Version your prompts
// Treat prompts as code -- commit to git, review in PRs, test against expected output
public final class PromptLibrary {
    public static final String SENTIMENT_CLASSIFIER_V2 = "...";
    public static final String RAG_ANSWER_V3           = "...";
}

// Rule 4: Abstract vector store access
// Use VectorStore interface -- never PgVectorStore directly
// Store migration (pgvector -> Milvus) becomes a config change

// Rule 5: Track cost from day one
// Add CostTrackingAdvisor to defaultAdvisors before first production deploy
// You will not regret it
```

### 16.4 Production Readiness Checklist

**Architecture**
- [ ] Using `ChatClient` abstraction, not provider-specific models
- [ ] Multi-provider fallback configured (primary + fallback + local Ollama)
- [ ] Context window management: sliding window or summarisation strategy
- [ ] RAG tenant isolation: `tenantId` filter on every query

**Cost and Performance**
- [ ] Input size validation per endpoint
- [ ] `CostTrackingAdvisor` recording metrics
- [ ] Model tiering: cheap for simple tasks, expensive for complex
- [ ] Semantic cache for frequent questions

**Reliability**
- [ ] Spring AI retry configured (backoff on 429, 5xx)
- [ ] Resilience4j circuit breaker on primary model
- [ ] 30s timeout on all LLM calls
- [ ] Semaphore / rate limiter on concurrent LLM calls

**Security**
- [ ] `PiiMaskingAdvisor` running at `HIGHEST_PRECEDENCE`
- [ ] Every `@Tool` write operation validates SecurityContext
- [ ] API keys from environment / secrets manager, never in code
- [ ] System prompt instructs model to ignore injected instructions

**Observability**
- [ ] Micrometer metrics: token usage, latency, cost per endpoint
- [ ] Structured logging: conversationId, model, tokens, finish_reason per call
- [ ] Alerting rules: error rate, P95 latency, hourly cost spike
- [ ] Audit log: all `@Tool` mutations recorded with actor "AI_AGENT"

**Testing**
- [ ] Unit tests cover business logic with mocked LLM
- [ ] Integration tests use Testcontainers + Ollama (no API key in CI)
- [ ] Prompt contract tests validate classification accuracy
- [ ] Fallback verified: circuit breaker triggers on primary outage

---

## Summary -- The Mental Model

```
+-------------------------------------------------------+
|  Spring AI = Spring idioms for LLMs                   |
|                                                       |
|  ChatClient      = RestTemplate (for LLMs)            |
|  Prompt          = HttpEntity (request wrapper)       |
|  ChatResponse    = ResponseEntity (+ token metadata)  |
|  ChatMemory      = HttpSession (conversation state)   |
|  EmbeddingClient = RestTemplate (for embeddings)      |
|  VectorStore     = JdbcTemplate (for semantic search) |
|  @Tool           = @RestController (LLM calls you)    |
|  Advisors        = HandlerInterceptor (AOP for AI)    |
|  PromptTemplate  = @Query (parameterised prompts)     |
+-------------------------------------------------------+
|  The 5 Non-Negotiables:                               |
|  1. Use ChatClient -- never ChatModel directly        |
|  2. Every RAG query MUST filter by tenantId           |
|  3. Check finish_reason -- "length" = truncated       |
|  4. Prompts are code -- version, review, test them    |
|  5. Token cost = money -- track from day one          |
+-------------------------------------------------------+
```

---

*Spring AI succeeds for the same reason Spring Data did: it takes complex infrastructure and makes it feel like familiar Java. You already know 80% of it. The remaining 20% is learning to think in tokens, context windows, and probabilistic outputs -- and that mental shift is exactly what this guide was written to provide.*
