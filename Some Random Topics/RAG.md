# Retrieval-Augmented Generation (RAG) in Agentic AI Systems
### A Comprehensive Technical Reference for Engineers and AI Practitioners

---

> **Document Purpose:** Deep-dive technical guide on RAG — from fundamentals to advanced agentic architectures — suitable for learning, building, and interview preparation.
> **Audience:** Software engineers, data scientists, ML engineers, and AI practitioners (Beginner → Expert)
> **Last Updated:** May 2026

---

## Table of Contents

1. [Introduction](#1-introduction)
2. [Fundamentals of RAG](#2-fundamentals-of-rag)
3. [Core Concepts](#3-core-concepts)
4. [Agentic AI Overview](#4-agentic-ai-overview)
5. [RAG in Agentic AI](#5-rag-in-agentic-ai)
6. [Advanced RAG Architectures](#6-advanced-rag-architectures)
7. [Optimization Techniques](#7-optimization-techniques)
8. [Memory & Context Management](#8-memory--context-management)
9. [Real-World Use Cases](#9-real-world-use-cases)
10. [Tools & Frameworks](#10-tools--frameworks)
11. [Challenges & Limitations](#11-challenges--limitations)
12. [Future Trends](#12-future-trends)
13. [Interview Preparation](#13-interview-preparation)
14. [Practical Implementation](#14-practical-implementation)
15. [Best Practices & Anti-Patterns](#15-best-practices--anti-patterns)
16. [Key Takeaways](#16-key-takeaways)

---

## 1. Introduction

### 1.1 What is RAG?

**Retrieval-Augmented Generation (RAG)** is an AI architecture that enhances the output quality of Large Language Models (LLMs) by dynamically fetching relevant external knowledge at inference time and injecting it into the model's prompt before generating a response.

```
RAG = Retrieval (Find relevant knowledge) + Augmentation (Add it to prompt) + Generation (LLM answers)
```

Rather than relying solely on static knowledge baked into model parameters during training, RAG allows a model to *look things up* — much like a human researcher consulting reference materials before writing an answer.

**Analogy:**
> Imagine giving a student an open-book exam. The student (LLM) is not expected to memorize everything; instead they consult relevant chapters (retrieved documents) and synthesize an accurate answer from them.

### 1.2 Why RAG is Important in Modern AI Systems

| Benefit | Description |
|---|---|
| **Up-to-date knowledge** | Retrieves current documents beyond the model's training cutoff |
| **Domain specificity** | Grounds responses in proprietary or domain-specific corpora |
| **Source attribution** | Enables citations, improving trust and auditability |
| **Reduced hallucinations** | Anchors generation in factual retrieved context |
| **Cost efficiency** | Cheaper than fine-tuning large models repeatedly |
| **Scalability** | Knowledge base can grow without retraining the model |

### 1.3 Limitations of Standalone LLMs

- **Training cutoff:** Cannot access events published after training
- **Hallucinations:** Confidently generate plausible-but-false information
- **Context window limits:** Storing entire knowledge bases in-context is impractical
- **Static knowledge:** Cannot be updated without expensive retraining
- **No source tracing:** Cannot reliably cite where information came from
- **Domain blindness:** Lack depth in highly specialized fields

> **RAG directly addresses every one of these limitations** by decoupling the knowledge store from the model and making retrieval a dynamic, runtime operation.

---

## 2. Fundamentals of RAG

### 2.1 Basic Architecture: Retriever + Generator

```
┌─────────────────────────────────────────────────────────┐
│                     RAG PIPELINE                        │
│  User Query                                             │
│      │                                                  │
│      ▼                                                  │
│  ┌──────────────┐   Relevant Chunks  ┌───────────────┐  │
│  │  RETRIEVER   │ ─────────────────► │  GENERATOR    │  │
│  │  Vector DB   │                    │    (LLM)      │  │
│  │  + Embedder  │                    │  Prompt =     │  │
│  └──────────────┘                    │  [Context] +  │  │
│                                      │  [Query]      │  │
│                                      └───────┬───────┘  │
│                                              ▼           │
│                                        Final Answer      │
└─────────────────────────────────────────────────────────┘
```

**Retriever:** Finds the most relevant pieces of information from a knowledge corpus given a user query.

**Generator:** An LLM that receives retrieved context alongside the query and produces a grounded, coherent response.

### 2.2 Key Components

#### Embeddings
A dense numerical vector representation of text in high-dimensional space. Semantically similar texts have vectors that are close together.

```
"machine learning" → [0.23, -0.11, 0.87, 0.04, ...]  (768-dim)
"deep learning"    → [0.25, -0.09, 0.85, 0.06, ...]  (similar → close)
"cooking recipes"  → [-0.72, 0.44, -0.31, 0.61, ...] (different → far)
```

Popular embedding models: `text-embedding-ada-002` (OpenAI), `all-MiniLM-L6-v2` (Sentence Transformers), `bge-large-en-v1.5` (BAAI), `Cohere Embed v3`

#### Vector Databases

| Database | Type | Highlights |
|---|---|---|
| **FAISS** | Library (Meta) | In-memory, blazing fast |
| **Pinecone** | Managed cloud | Serverless, production-ready |
| **Weaviate** | Open-source | GraphQL API, multi-modal |
| **Chroma** | Open-source | Simple, developer-friendly |
| **Qdrant** | Open-source | Rust-based, fast, rich filtering |
| **Milvus** | Open-source | Enterprise scale, cloud-native |
| **pgvector** | Postgres ext | Keeps vectors with relational data |

#### Retrievers
- **Dense retriever:** Embedding similarity (semantic search)
- **Sparse retriever:** BM25/TF-IDF keyword matching
- **Hybrid retriever:** Combines both dense and sparse signals

### 2.3 How RAG Works Step-by-Step

**Phase 1 — Indexing (Offline)**
```
Raw Documents → Text Extraction → Chunking → Embedding Model → Vector Database
```

**Phase 2 — Retrieval & Generation (Online)**
```
User Query → Embed Query → Similarity Search (Top-K Chunks)
          → Prompt Construction → LLM Generation → Grounded Response
```

**Prompt template:**
```
System: Answer ONLY based on the provided context. If not found, say "I don't know."

Context:
--- [Chunk 1] ---
--- [Chunk 2] ---
--- [Chunk 3] ---

Question: {user_query}
Answer:
```

### 2.4 Types of RAG

| Type | Description | Pros | Cons |
|---|---|---|---|
| **Naive RAG** | Index → retrieve top-k → generate | Simple, fast prototyping | Limited quality |
| **Advanced RAG** | Adds query rewriting, re-ranking | Better recall/precision | More complex |
| **Modular RAG** | Interchangeable pipeline modules | Fully customizable | Highest complexity |
| **Hybrid RAG** | Dense + sparse retrieval combined | Best recall | Score fusion needed |

---

## 3. Core Concepts

### 3.1 Embeddings and Similarity Search

**Similarity Metrics:**

| Metric | Formula | When to Use |
|---|---|---|
| **Cosine Similarity** | cos(θ) = (A·B)/(‖A‖‖B‖) | Most common; direction matters |
| **Dot Product** | A·B | Fast; works with normalized vectors |
| **Euclidean Distance** | √Σ(aᵢ-bᵢ)² | Less common; magnitude-sensitive |

```python
from sklearn.metrics.pairwise import cosine_similarity
import numpy as np

vec_a = np.array([[0.1, 0.9, 0.3]])
vec_b = np.array([[0.15, 0.85, 0.28]])
score = cosine_similarity(vec_a, vec_b)
print(f"Similarity: {score[0][0]:.4f}")  # ~0.9987
```

### 3.2 FAISS Deep Dive

| Index Type | Description | Use Case |
|---|---|---|
| `IndexFlatL2` | Exact brute-force | Small (<100K docs) |
| `IndexIVFFlat` | Inverted file, approximate | Medium datasets |
| `IndexHNSWFlat` | Graph-based ANN | High recall + speed |
| `IndexPQ` | Product Quantization | Large, memory constrained |

```python
import faiss, numpy as np

dimension = 768
index = faiss.IndexHNSWFlat(dimension, 32)  # 32 neighbors per node
vectors = np.random.rand(10000, dimension).astype('float32')
faiss.normalize_L2(vectors)
index.add(vectors)

query = np.random.rand(1, dimension).astype('float32')
faiss.normalize_L2(query)
distances, indices = index.search(query, k=5)
```

### 3.3 Chunking Strategies

| Strategy | Description | Pros | Cons |
|---|---|---|---|
| **Fixed-size** | Split every N tokens | Simple | Cuts mid-sentence |
| **Sentence** | Split at sentence boundaries | Natural | Variable size |
| **Recursive** | Paragraph → sentence → word | Semantic-aware | More complex |
| **Semantic** | Split where embedding similarity drops | High quality | Expensive |
| **Structure-based** | Use headers/sections | Best for structured docs | Format-dependent |

**Key parameters:**
- `chunk_size`: 256–1024 tokens (commonly)
- `chunk_overlap`: 10–20% of chunk_size to preserve boundary context

```python
from langchain.text_splitter import RecursiveCharacterTextSplitter

splitter = RecursiveCharacterTextSplitter(
    chunk_size=512,
    chunk_overlap=64,
    separators=["\n\n", "\n", ". ", " ", ""]
)
chunks = splitter.split_text(document_text)
```

### 3.4 Retrieval Strategies

#### Top-K Retrieval
Retrieve K most similar chunks. Common K: 3–10. Higher K = more coverage but more noise.

#### Maximum Marginal Relevance (MMR)
Balances relevance and diversity — avoids near-duplicate chunks.
```
MMR = λ × Relevance(doc, query) - (1-λ) × max(Similarity(doc, already_selected))
```

#### Two-Stage Re-ranking
```
Stage 1: Embedding similarity → top-50 candidates  (fast bi-encoder)
Stage 2: Cross-encoder re-ranking → top-5 final    (slow, accurate)
```

Popular re-rankers: `cross-encoder/ms-marco-MiniLM-L-6-v2`, Cohere Rerank API, `bge-reranker-large`

```python
from sentence_transformers import CrossEncoder

reranker = CrossEncoder('cross-encoder/ms-marco-MiniLM-L-6-v2')
pairs = [(query, doc.text) for doc in candidates]
scores = reranker.predict(pairs)
ranked = sorted(zip(scores, candidates), reverse=True)[:5]
```

---

## 4. Agentic AI Overview

### 4.1 What is Agentic AI?

**Agentic AI** refers to AI systems that operate autonomously to accomplish complex, multi-step goals by using tools, maintaining memory, making plans, and adapting to feedback — without requiring explicit human instruction at each step.

An agent is not just a chatbot. It is an **autonomous actor** that can:
- **Perceive** its environment (inputs, tool outputs, user feedback)
- **Reason and plan** (decompose goals into sub-tasks)
- **Act** (execute tools, write code, browse web, call APIs)
- **Reflect** (evaluate results and adjust strategy)
- **Remember** (persist state across interactions)

### 4.2 LLMs vs Autonomous Agents

| Dimension | Standalone LLM | Autonomous Agent |
|---|---|---|
| **Interaction** | Single turn Q&A | Multi-turn, goal-directed loops |
| **Tools** | None | Web search, code execution, RAG, APIs |
| **Memory** | Context window only | Short + long-term memory stores |
| **Planning** | Implicit | Explicit: decompose, prioritize, sequence |
| **Adaptation** | None | Self-correction, reflection, retry |
| **Autonomy** | Reactive | Proactive, goal-seeking |

### 4.3 Core Agent Components

```
┌──────────────────────────────────────────────────────┐
│                  AGENTIC AI CORE                     │
│                                                      │
│  ┌───────────┐  ┌────────────┐  ┌────────────────┐   │
│  │ PLANNING  │  │  MEMORY    │  │   REASONING    │   │
│  │Task decomp│  │Short-term  │  │Chain-of-thought│   │
│  │Goal mgmt  │  │Long-term   │  │ReAct loop      │   │
│  │Replanning │  │Episodic    │  │Self-reflection │   │
│  └─────┬─────┘  └─────┬──────┘  └───────┬────────┘   │
│        └──────────────┴─────────────────┘             │
│                        │                              │
│                 ┌──────▼──────┐                       │
│                 │    LLM CORE │                       │
│                 └──────┬──────┘                       │
│                        │                              │
│              ┌──────────┴──────────┐                  │
│              │       TOOLS         │                  │
│              │ RAG │ Code │ Web    │                  │
│              └─────────────────────┘                  │
└──────────────────────────────────────────────────────┘
```

**Memory types:**
- **Working memory:** Current conversation context (prompt window)
- **Episodic memory:** Past interactions (stored and retrievable via RAG)
- **Semantic memory:** Factual knowledge base (RAG)
- **Procedural memory:** How to perform tasks (few-shot examples)

**Planning approaches:**
- **ReAct (Reason + Act):** Alternate reasoning traces and tool calls
- **Plan-and-Execute:** Create full plan upfront, then execute steps
- **Tree of Thoughts:** Explore multiple reasoning paths in parallel

---

## 5. RAG in Agentic AI

### 5.1 Why Agents Need RAG

Agents without RAG are knowledge-blind beyond their training data. RAG transforms agents into **knowledge-aware actors**:

- **Dynamic knowledge access:** Retrieve up-to-date information at runtime
- **Task-specific grounding:** Fetch only what's needed for the current sub-task
- **Factual accuracy:** Prevent hallucinations on specialized knowledge
- **Audit trail:** Retrieved sources provide verifiable evidence
- **Scalability:** Handle unlimited knowledge without expanding context windows

### 5.2 RAG as an Agent Tool (ReAct Pattern)

In agentic systems, RAG is a **callable tool** — the agent decides *when* and *what* to retrieve:

```
Thought: "I need to find the company's refund policy."
Action:  rag_search(query="refund policy for premium subscription")
Obs:     [Chunk: "Premium subscribers may request a full refund within 30 days..."]
Thought: "I have the policy. I can now answer."
Action:  respond("Based on our policy, you can get a full refund within 30 days.")
```

This **deliberate retrieval** is far more powerful than automatic retrieval because the agent can:
- Reformulate queries for better retrieval
- Retrieve from multiple knowledge bases
- Decide not to retrieve if it has sufficient context
- Chain multiple retrievals for complex multi-hop questions

### 5.3 Tool-Augmented Retrieval

```python
tools = [
    Tool(name="search_product_docs",    fn=product_rag.search),
    Tool(name="search_support_tickets", fn=ticket_rag.search),
    Tool(name="search_legal_docs",      fn=legal_rag.search),
    Tool(name="web_search",             fn=tavily_search),
    Tool(name="sql_query",              fn=db.execute),
]
```

The agent selects the right tool for each sub-query, enabling **heterogeneous knowledge fusion** — combining structured databases, unstructured documents, and real-time web sources.

### 5.4 Dynamic Knowledge Access Patterns

| Pattern | Description |
|---|---|
| **Iterative retrieval** | Retrieve → read → identify gaps → retrieve again |
| **HyDE** | Generate a hypothetical answer, use it as retrieval query |
| **Step-back prompting** | Abstract the question to retrieve higher-level context first |
| **Query routing** | Route to the most appropriate knowledge base/tool |
| **Self-ask decomposition** | Break complex question into simpler sub-questions |

---

## 6. Advanced RAG Architectures

### 6.1 Multi-Step RAG

Performs iterative retrievals for complex, multi-hop questions.

```
Question: "What are the side effects of the drug used to treat the disease 
           discovered by the 2023 Nobel Prize winner in Medicine?"

Step 1: retrieve("Nobel Prize Medicine 2023 winner")
        → "Katalin Karikó and Drew Weissman won for mRNA vaccine development"
Step 2: retrieve("diseases treated by mRNA vaccines")
        → "COVID-19, certain cancers..."
Step 3: retrieve("mRNA vaccine side effects")
        → "Fatigue, fever, injection site pain..."
Final:  Synthesize across all retrieved context
```

### 6.2 Retrieval Pipelines (Production)

```
Query
 │
[1. Query Analysis]        ← Intent detection, entity extraction
[2. Query Transformation]  ← Rewriting, expansion, HyDE
[3. Routing]               ← Select appropriate retriever/knowledge base
[4. Retrieval]             ← Dense + sparse hybrid search
[5. Metadata Filtering]    ← Date ranges, categories, access control
[6. Re-ranking]            ← Cross-encoder re-scoring
[7. Context Compression]   ← Extract most relevant sentences
[8. Prompt Construction]   ← Assemble final prompt
[9. Generation]            ← LLM response
[10. Output Validation]    ← Faithfulness check, citation extraction
```

### 6.3 Self-Reflective RAG (ReAct and Self-RAG)

#### ReAct (Reasoning + Acting)
Interleaves reasoning traces with tool calls, creating a transparent and steerable loop.

#### Self-RAG
A model trained to control its own retrieval with special tokens:
- `[Retrieve]`: Should I retrieve? (yes/no)
- `[IsREL]`: Is this retrieved document relevant?
- `[IsSUP]`: Is my generation supported by the document?
- `[IsUSE]`: Is the response useful?

Benefits: Avoids unnecessary retrievals, filters irrelevant docs, self-assesses faithfulness.

#### Reflexion
After completing a task, the agent reflects on what went wrong and generates a verbal self-critique:
```
Trial 1 → Fail
Reflection: "I retrieved general docs instead of the specific version docs.
             Next time I'll include the version number in my query."
Trial 2 (with refined strategy) → Success
```

### 6.4 Graph-Based RAG (GraphRAG)

Structures knowledge as a graph where entities are nodes and relationships are edges.

```
Traditional RAG:  Query → "Company X CEO" → relevant paragraph chunks
GraphRAG:         Query → "Company X CEO" → Node(CEO) → Board, Founded, Acquisitions...
                                          → Richer, connected answer
```

- Enables community detection for thematic summarization
- Best for stable corpora with rich entity relationships
- Trade-off: expensive graph construction during indexing

### 6.5 Hierarchical RAG

```
Level 0: Document summaries  (coarse retrieval — find relevant docs)
  ↓
Level 1: Section summaries   (mid retrieval — narrow to section)
  ↓
Level 2: Paragraph chunks    (fine retrieval — extract exact passage)
```

Reduces noise for large corpora by drilling down from general to specific.

### 6.6 Multi-Modal RAG

| Modality | Embedding Approach | Use Case |
|---|---|---|
| **Images** | CLIP, BLIP-2 | Visual Q&A, product search |
| **Tables** | Structure-aware encoders | Financial analysis |
| **Audio** | Whisper → text → embed | Meeting notes retrieval |
| **Code** | CodeBERT, UniXcoder | Code search, documentation |
| **PDFs** | Nougat, Unstructured | Research papers, contracts |

---

## 7. Optimization Techniques

### 7.1 Query Transformation

#### Query Rewriting
```
Original: "why is my thing not working"
Rewritten: "troubleshooting steps for [product] connection failure"
```

#### HyDE (Hypothetical Document Embeddings)
Generate a hypothetical answer, embed it as the retrieval query:
```python
hypothetical = llm.generate(f"Write a paragraph answering: {user_query}")
results = vector_db.search(embed(hypothetical), k=5)
```

#### Multi-Query Retrieval
```python
queries = llm.generate(f"Create 3 different phrasings of: {user_query}")
all_results = [vector_db.search(embed(q), k=5) for q in queries]
final = deduplicate(flatten(all_results))
```

#### Step-Back Prompting
```
Specific:  "What was Einstein's contribution to the Manhattan Project?"
Step-back: "What was Einstein's role in 20th century physics?" → retrieve first
```

### 7.2 Re-ranking

Cross-encoders process query and document **jointly** for accurate relevance scoring:

```python
from sentence_transformers import CrossEncoder

reranker = CrossEncoder('cross-encoder/ms-marco-MiniLM-L-6-v2')
candidates = vector_db.search(query_vector, k=50)
scores = reranker.predict([(query, doc.text) for doc in candidates])
top5 = sorted(zip(scores, candidates), reverse=True)[:5]
```

### 7.3 Latency Optimization

| Technique | Description | Benefit |
|---|---|---|
| **Query caching** | Cache results for identical queries | Eliminates redundant DB calls |
| **Semantic caching** | Cache by semantic similarity | Handles paraphrases |
| **ANN indexes** | HNSW/IVF instead of exact search | Sub-linear query time |
| **Async retrieval** | Parallelize multiple retrievals | Reduces end-to-end latency |
| **Batch embedding** | Embed multiple chunks in one call | GPU utilization |

### 7.4 Hallucination Reduction

- **Strict grounding instructions:** "Only use provided context"
- **Faithfulness checks:** Post-generation NLI model verifies answer is entailed by context
- **Citation forcing:** Require model to cite specific chunks
- **Temperature tuning:** 0.0–0.3 for factual, grounded tasks
- **Negative prompting:** "If the context doesn't contain the answer, say 'I don't know'"

### 7.5 RAG Evaluation (RAGAS Framework)

| Metric | Description |
|---|---|
| **Faithfulness** | Is the answer factually supported by retrieved context? |
| **Answer Relevancy** | Does the answer address the question? |
| **Context Recall** | Does retrieved context contain enough to answer? |
| **Context Precision** | Are retrieved chunks relevant (not noisy)? |

```python
from ragas import evaluate
from ragas.metrics import faithfulness, answer_relevancy, context_recall, context_precision

results = evaluate(
    dataset=eval_dataset,
    metrics=[faithfulness, answer_relevancy, context_recall, context_precision]
)
print(results)
```

**Retrieval metrics:**

| Metric | Description |
|---|---|
| **Precision@K** | Fraction of top-K retrieved docs that are relevant |
| **Recall@K** | Fraction of all relevant docs retrieved in top-K |
| **MRR** | Mean Reciprocal Rank of first relevant result |
| **NDCG** | Normalized Discounted Cumulative Gain — accounts for ranking order |

---

## 8. Memory & Context Management

### 8.1 Short-Term vs Long-Term Memory

```
SHORT-TERM (Working Memory)
├── Current conversation turns
├── Immediate task context
└── Recent tool outputs

LONG-TERM (Persistent Memory)
├── Episodic: Past conversation summaries (vector DB)
├── Semantic: Knowledge base (RAG)
├── Procedural: Learned strategies/preferences
└── User profile: Personalization data
```

| Memory Type | Storage | Retrieval | RAG Role |
|---|---|---|---|
| **Working** | In-context (prompt) | Direct | Not applicable |
| **Episodic** | Vector DB of past conversations | Semantic search | Core use case |
| **Semantic** | Domain knowledge vector DB | Semantic search | Core use case |
| **Procedural** | Prompt templates, few-shot examples | Selection | Supporting role |

### 8.2 Knowledge Grounding Levels

1. **No grounding:** Pure LLM parametric generation (highest hallucination risk)
2. **Prompt grounding:** Static documents in system prompt (limited, stale)
3. **RAG grounding:** Dynamic retrieval at inference time (recommended)
4. **Hybrid grounding:** RAG + web search + structured DB queries (most comprehensive)

### 8.3 Stateful vs Stateless RAG

**Stateless RAG:** Each query is independent. Simpler. Best for: FAQ bots, search engines.

**Stateful RAG:** Maintains conversation history and user context. Best for: personal assistants, enterprise copilots.

```python
class StatefulRAGAgent:
    def __init__(self):
        self.history = []
        self.memory_db = VectorDB()

    def chat(self, user_message: str) -> str:
        past_context = self.memory_db.search(user_message, k=3)
        knowledge = knowledge_rag.search(user_message, k=5)
        prompt = build_prompt(self.history, past_context, knowledge, user_message)
        response = llm.generate(prompt)
        self.memory_db.add(f"Q: {user_message}\nA: {response}")
        self.history.append({"role": "user", "content": user_message})
        self.history.append({"role": "assistant", "content": response})
        return response
```

---

## 9. Real-World Use Cases

### 9.1 Enterprise Search
**Problem:** Employees waste hours searching SharePoint, Confluence, Jira, email.

**RAG Solution:**
- Index all internal documents into a unified vector store
- Natural language search across all sources
- Automatic citations to source documents
- **Metrics:** First Contact Resolution +30–40%, search time -60%

### 9.2 Customer Support Chatbots
- Index product documentation, FAQs, support tickets
- Bot retrieves relevant docs before responding
- Escalation if retrieval confidence is low → human handoff
- Personalization via user purchase history as additional context

### 9.3 Coding Assistants
RAG in tools like GitHub Copilot, Cursor, Windsurf:
- Retrieve relevant code from current repository
- Fetch documentation for libraries being used
- Search StackOverflow / GitHub Issues for error solutions
- Pull related test files when generating tests

### 9.4 Healthcare & Legal Systems

**Healthcare RAG:**
- Retrieve from PubMed, clinical guidelines, drug databases
- Support clinical decision-making with evidence-based answers
- Drug interaction checking with citation to regulatory docs

**Legal RAG:**
- Index case law, statutes, regulations, contracts
- Retrieve relevant precedents for legal research
- Contract clause comparison and compliance checking

**Critical requirements:**
- High recall (missing relevant docs = risk)
- Source attribution mandatory
- Access control on sensitive documents
- Data freshness is critical

---

## 10. Tools & Frameworks

### 10.1 LangChain

```python
from langchain_community.document_loaders import PyPDFLoader
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain_openai import OpenAIEmbeddings, ChatOpenAI
from langchain_community.vectorstores import FAISS
from langchain.chains import RetrievalQA

loader = PyPDFLoader("document.pdf")
docs = loader.load()
chunks = RecursiveCharacterTextSplitter(chunk_size=512, chunk_overlap=64).split_documents(docs)
vectorstore = FAISS.from_documents(chunks, OpenAIEmbeddings())
retriever = vectorstore.as_retriever(search_kwargs={"k": 5})

qa = RetrievalQA.from_chain_type(
    llm=ChatOpenAI(model="gpt-4o"),
    retriever=retriever,
    return_source_documents=True
)
result = qa.invoke({"query": "What is the main topic?"})
```

**LangChain Agent with RAG tool:**
```python
from langchain.agents import create_react_agent, AgentExecutor
from langchain.tools.retriever import create_retriever_tool

rag_tool = create_retriever_tool(retriever, "search_kb", "Search the knowledge base")
agent = create_react_agent(llm=ChatOpenAI(), tools=[rag_tool], prompt=react_prompt)
AgentExecutor(agent=agent, tools=[rag_tool]).invoke({"input": "What is our return policy?"})
```

### 10.2 LlamaIndex

```python
from llama_index.core import VectorStoreIndex, SimpleDirectoryReader

documents = SimpleDirectoryReader("data/").load_data()
index = VectorStoreIndex.from_documents(documents)
query_engine = index.as_query_engine(similarity_top_k=5)
response = query_engine.query("Explain the refund process")
print(response.source_nodes)  # Retrieved chunks with scores
```

**LlamaIndex unique features:**
- `SubQuestionQueryEngine`: Decomposes complex questions
- `RouterQueryEngine`: Routes to different indexes by query type
- `KnowledgeGraphIndex`: Graph-based retrieval
- `RecursiveRetriever`: Hierarchical document retrieval

### 10.3 Semantic Kernel (Microsoft)

```csharp
var memory = new SemanticTextMemory(vectorStore, embeddingGeneration);
await memory.SaveInformationAsync("products", "prod-001",
    "Product X is a premium widget with 2-year warranty...");

var results = memory.SearchAsync("warranty information", limit: 3);
await foreach (var result in results)
    Console.WriteLine($"{result.Metadata.Text} (Score: {result.Relevance})");
```

### 10.4 Vector Databases Comparison

| Feature | Pinecone | Weaviate | Qdrant | Chroma | pgvector |
|---|---|---|---|---|---|
| **Hosting** | Managed cloud | Self/Cloud | Self/Cloud | Self | Self |
| **Scalability** | Very high | High | High | Low-Med | PG limits |
| **Filtering** | Metadata | GraphQL | Rich payload | Basic | SQL WHERE |
| **Multi-modal** | No | Yes | Yes | No | No |
| **Best for** | Production SaaS | Complex queries | Performance | Local dev | Existing PG |
| **Cost** | Paid (free tier) | Open source | Open source | Open source | Open source |

---

## 11. Challenges & Limitations

### 11.1 Data Freshness
RAG is only as current as the last ingestion run. **Solutions:**
- Incremental ingestion pipelines triggered on document change
- TTL for chunks in vector store
- Hybrid RAG + live web search for real-time queries
- Metadata date filters: `{"filter": {"date": {"$gte": "2024-01-01"}}}`

### 11.2 Retrieval Quality Issues

| Problem | Cause | Solution |
|---|---|---|
| **Low recall** | Bad chunking, embedding mismatch | Domain-tuned embeddings, better chunking |
| **Low precision** | Off-topic results | Re-ranking, metadata filtering |
| **Semantic gap** | Query phrasing ≠ doc phrasing | HyDE, multi-query, query expansion |
| **Lost in middle** | LLM ignores middle chunks | Context compression, reverse ordering |
| **Boundary splits** | Important context split across chunks | Larger overlap, semantic chunking |

### 11.3 Cost and Scalability
- Embedding millions of documents is expensive with API-based models → use open-source (BAAI/bge)
- LLM calls with large context increase token cost → context compression
- Vector DB at scale requires significant infrastructure → tiered retrieval

### 11.4 Security Risks

| Risk | Mitigation |
|---|---|
| **Prompt injection** | Input sanitization, sandboxed prompts |
| **Data exfiltration** | Per-user access control on vector store |
| **PII leakage** | PII redaction before indexing |
| **Unauthorized access** | RBAC metadata filtering on every query |
| **Poisoning attacks** | Source allowlisting, input validation |

---

## 12. Future Trends

### 12.1 Agentic Workflows + RAG
The convergence of agents and RAG is creating **RAG-native agentic systems**:
- Retrieval is a first-class capability decided dynamically by the agent
- Multi-agent systems share and update a common knowledge store
- Long-horizon tasks maintain persistent RAG-backed memory across sessions

### 12.2 Hybrid Reasoning Systems
Future systems combining:
- RAG (external knowledge retrieval)
- Symbolic reasoning (rule engines, knowledge graphs)
- Chain-of-thought (in-context reasoning)
- World models (internal simulations)

### 12.3 Continuous Learning Systems
Moving from static RAG to **living knowledge bases**:
- Automatic ingestion from monitored data streams
- Online learning from user feedback (flagging bad retrievals)
- Model fine-tuning triggered by retrieval failure patterns
- Near-real-time updates from news feeds, wikis, product catalogs

### 12.4 RAG + Reasoning Models
New reasoning models (o1, o3, DeepSeek R1) combined with RAG:
- Model reasons about *whether* retrieved information is sufficient
- Multi-step internal reasoning interleaved with retrieval calls
- Better disambiguation of conflicting retrieved facts

---

## 13. Interview Preparation

### A. Basic Questions

---

**Q1: What is RAG and how does it work?**

**A:** RAG (Retrieval-Augmented Generation) improves LLM responses by retrieving relevant external documents at inference time and injecting them into the prompt before generating an answer. Two phases: (1) **Offline indexing** — chunk, embed, store documents in vector DB; (2) **Online retrieval** — embed query, similarity-search vector DB, retrieve top-K chunks, augment prompt, generate grounded response.

---

**Q2: Why prefer RAG over fine-tuning for domain knowledge?**

**A:**
- **Cost:** Fine-tuning requires expensive GPU compute; RAG only needs embedding computation
- **Updatability:** RAG updates by swapping documents; fine-tuning requires full retraining
- **Attribution:** RAG provides source references; fine-tuning bakes knowledge into opaque weights
- **No catastrophic forgetting:** RAG leaves the base model untouched
- **Auditability:** You can inspect exactly which documents influenced the answer

---

**Q3: What is a vector database and why is it used in RAG?**

**A:** A vector database is storage optimized for high-dimensional embedding vectors. It enables **semantic search** — finding documents *meaningfully similar* to a query (not just keyword matches) using ANN algorithms (HNSW, IVF) that perform fast similarity search over millions of vectors in milliseconds.

---

**Q4: Why does chunking matter? What parameters control it?**

**A:** Chunking splits documents before embedding. It matters because: LLMs have token limits; embedding quality degrades on very long texts; smaller chunks enable more precise retrieval. Key parameters: `chunk_size` (256–1024 tokens), `chunk_overlap` (10–20% of chunk size to preserve boundary context).

---

**Q5: Difference between dense and sparse retrieval?**

**A:**
- **Dense:** Neural embeddings capture semantic similarity (synonyms, paraphrases). Slower to set up, no exact keyword guarantee.
- **Sparse:** BM25/TF-IDF exact keyword matching. Fast, interpretable, no model needed. Misses semantic variants.
- **Hybrid:** Combines both, merged via Reciprocal Rank Fusion. Generally outperforms either alone.

---

### B. Intermediate Questions

---

**Q6: Explain Naive vs Advanced vs Modular RAG.**

**A:**
- **Naive:** Chunk → embed → store → retrieve top-k → generate. Simple but limited retrieval quality.
- **Advanced:** Adds pre-retrieval (query rewriting, HyDE) and post-retrieval (re-ranking, context compression) steps.
- **Modular:** Each pipeline stage is a swappable module. Supports routing, parallel retrieval, dynamic orchestration. Most flexible; used in production systems.

---

**Q7: What is HyDE and when would you use it?**

**A:** HyDE generates a *hypothetical answer* using an LLM, then embeds that answer as the retrieval query instead of the raw question. Documents answering a question are semantically similar to a hypothetical answer, not necessarily to the question itself. Use when queries are short/vague or there is a style gap between query and document vocabulary. Caveat: hallucinated hypothetical answers may retrieve wrong docs.

---

**Q8: Cross-encoders vs bi-encoders — when to use each?**

**A:**
- **Bi-encoder:** Embeds query and document separately. Pre-computable doc embeddings → O(1) retrieval. Fast but less accurate.
- **Cross-encoder:** Concatenates query + doc into single transformer. Highly accurate but O(N) — can't pre-compute.
- **Best practice:** Bi-encoder for fast first-stage retrieval (top-50), cross-encoder for accurate re-ranking (top-5).

---

**Q9: How do you evaluate a RAG system with RAGAS?**

**A:** RAGAS measures 4 metrics using LLMs as judges (no human labels needed):
1. **Faithfulness:** Answer factually supported by context (anti-hallucination measure)
2. **Answer Relevancy:** Answer addresses the question
3. **Context Recall:** Retrieved context contains enough to answer
4. **Context Precision:** Retrieved chunks are relevant, not noisy

```python
from ragas import evaluate
from ragas.metrics import faithfulness, answer_relevancy, context_recall
results = evaluate(dataset, metrics=[faithfulness, answer_relevancy, context_recall])
```

---

**Q10: How does RAG fit into an agentic architecture?**

**A:** In agentic systems, RAG is a **callable tool** — not automatic. The agent decides when to retrieve, what to query, which knowledge base to use, and whether retrieved context is sufficient. This deliberate, agent-controlled retrieval is more powerful because the retrieval strategy is subject to the agent's reasoning capability — enabling iterative retrieval, query reformulation, and multi-source fusion.

---

### C. Advanced Questions

---

**Q11: Explain Self-RAG and its advantages.**

**A:** Self-RAG trains the model to use special reflection tokens to control retrieval:
- `[Retrieve]`: Should I retrieve? (yes/no) — avoids unnecessary calls
- `[IsREL]`: Is this document relevant? — filters noise
- `[IsSUP]`: Is my generation supported? — self-faithfulness check
- `[IsUSE]`: Is the response useful? — quality gating

Advantages: Adaptive retrieval (skips when not needed), self-filtering of irrelevant docs, better calibration of when the model knows vs doesn't know.

---

**Q12: What is GraphRAG and what problems does it solve?**

**A:** GraphRAG structures knowledge as a graph (entities=nodes, relationships=edges) with community detection for hierarchical summaries.

Problems solved:
1. **Global queries:** "What are the main themes?" — flat RAG only retrieves local chunks. GraphRAG uses community summaries to answer holistic questions.
2. **Multi-hop reasoning:** "Who did X work with, and what did that person publish?" — requires traversing entity relationships, not text similarity.
3. **Context coherence:** Related entities are linked, providing richer connected context.

Trade-off: Expensive graph construction during indexing; best for stable, important corpora.

---

**Q13: Describe the "lost in the middle" problem and mitigations.**

**A:** LLMs perform best on context at the beginning or end of the prompt window, and significantly worse on information in the middle. If the most relevant chunk is placed 3rd out of 5, the model may underuse it.

Mitigations:
1. **Reorder by importance:** Most relevant chunk first or last
2. **Context compression:** Extract only the most relevant sentences from each chunk
3. **Fewer, better chunks:** Use re-ranking to select 2–3 highly relevant chunks instead of 10
4. **Long-context fine-tuned models:** Llama 3.1, Gemini 1.5 Pro trained to handle long contexts

---

**Q14: Design a production RAG system for 100M documents.**

**A:**

**Ingestion:** Distributed processing (Spark), batch GPU embedding (open-source BGE models), incremental updates via Kafka CDC.

**Storage:** Milvus/Weaviate for vector storage (horizontal sharding), PostgreSQL for metadata/filtering, tiered storage (hot in memory, cold on disk).

**Retrieval:** HNSW ANN index → cross-encoder reranking. Redis semantic cache. Per-query async parallel retrieval.

**Monitoring:** p99 retrieval latency, faithfulness score (sampled), cache hit rate, recall@5 on golden dataset.

**Cost optimization:** Open-source embeddings, quantized HNSW indexes, semantic caching, tiered retrieval (cheap ANN → expensive reranker only when needed).

---

### D. Scenario-Based Questions

---

**Q15: RAG chatbot returns correct but outdated information. How do you fix it?**

**A:**
1. Add `created_date` metadata field to all chunks — identify stale retrievals
2. Implement incremental ingestion (webhook/CDC triggers on document change)
3. Add TTL to chunks for time-sensitive content
4. Supplement with real-time web search for current-events queries
5. Date-filter retrieval: `search_kwargs={"filter": {"date": {"$gte": "2024-01-01"}}}`

---

**Q16: Bot says "I don't know" even when the answer is clearly in the documents. Debug it.**

**A:**

**Debugging steps:**
1. Log retrieved chunks for the failing query — what is actually being returned?
2. Check similarity scores — is the relevant chunk scoring too low?
3. Inspect the relevant document section — was the key information split at a chunk boundary?

**Root causes and fixes:**
- **Semantic mismatch:** Query vocabulary ≠ document vocabulary → add HyDE or multi-query
- **Chunking issue:** Key sentence split across chunks → increase overlap or use semantic chunking
- **k too small:** Relevant chunk outside top-3 → increase k to 5–10
- **Over-restrictive prompt:** Loosen grounding instruction or lower similarity threshold
- **Bad embedding model:** Use a domain-specific or fine-tuned embedding model

---

**Q17: Build a RAG system for a law firm with confidential client documents.**

**A:**

**Isolation:** Per-client vector store namespaces, RBAC metadata filtering on every query, attorney-client privilege classification before indexing.

**Security:** End-to-end encryption, no external API calls (use local LLMs: Llama 3, Mistral), PII redaction layer before embedding, full audit log of all retrievals.

**Quality:** Hybrid retrieval (BM25 for statute/case-number exact match + dense for semantic), human review step for high-stakes answers, high-k retrieval with strict re-ranking.

**Compliance:** Document retention policies at vector DB level, right-to-deletion (purge all chunks for a client), immutable audit trail.

---

### E. Coding/Design Questions

---

**Q18: Implement hybrid retrieval with Reciprocal Rank Fusion.**

```python
from rank_bm25 import BM25Okapi
from typing import List, Tuple

def reciprocal_rank_fusion(
    dense_results: List[Tuple[str, float]],
    sparse_results: List[Tuple[str, float]],
    k: int = 60
) -> List[Tuple[str, float]]:
    rrf_scores = {}
    for rank, (doc_id, _) in enumerate(dense_results):
        rrf_scores[doc_id] = rrf_scores.get(doc_id, 0) + 1 / (k + rank + 1)
    for rank, (doc_id, _) in enumerate(sparse_results):
        rrf_scores[doc_id] = rrf_scores.get(doc_id, 0) + 1 / (k + rank + 1)
    return sorted(rrf_scores.items(), key=lambda x: x[1], reverse=True)


def hybrid_retrieve(query, corpus_texts, embedder, vector_index, top_k=5):
    # Dense retrieval
    qvec = embedder.encode([query])
    d_scores, d_idx = vector_index.search(qvec, top_k * 5)
    dense = [(str(i), float(s)) for i, s in zip(d_idx[0], d_scores[0])]

    # Sparse (BM25)
    bm25 = BM25Okapi([t.split() for t in corpus_texts])
    sp_scores = bm25.get_scores(query.split())
    sparse = sorted(enumerate(sp_scores), key=lambda x: x[1], reverse=True)[:top_k * 5]
    sparse = [(str(i), float(s)) for i, s in sparse]

    # Merge
    fused = reciprocal_rank_fusion(dense, sparse)
    return [corpus_texts[int(doc_id)] for doc_id, _ in fused[:top_k]]
```

---

**Q19: Multi-tenant RAG API data model.**

```python
# Data Model
class DocumentChunk:
    id: str            # UUID
    tenant_id: str     # Tenant isolation key
    content: str       # Chunk text
    embedding: list    # Dense vector
    metadata: dict     # {source, date, author, access_level, category}

class QueryRequest:
    tenant_id: str
    user_id: str       # Per-user access control
    query: str
    top_k: int = 5
    filters: dict = {} # {"category": "legal", "date_after": "2024"}

class QueryResponse:
    answer: str
    sources: list      # [{doc_id, text, score, metadata}]
    faithfulness: float

# REST API
# POST /api/v1/ingest          → chunk, embed, store with tenant namespace
# POST /api/v1/query           → retrieve with tenant+user filters, generate
# DELETE /api/v1/documents/:id → remove all chunks for document
# GET  /api/v1/stats/:tenant   → doc count, storage, query volume
```

---

## 14. Practical Implementation

### 14.1 Complete RAG Pipeline — Python

**Install dependencies:**
```bash
pip install langchain langchain-openai langchain-community faiss-cpu \
            sentence-transformers pypdf python-dotenv ragas rank_bm25
```

**Full implementation:**

```python
import os
from dotenv import load_dotenv
from langchain_community.document_loaders import PyPDFDirectoryLoader
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain_openai import OpenAIEmbeddings, ChatOpenAI
from langchain_community.vectorstores import FAISS
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser
from langchain_core.runnables import RunnablePassthrough

load_dotenv()

# ── 1. LOAD ──────────────────────────────────────────────────────────────────
loader = PyPDFDirectoryLoader("./documents/")
raw_docs = loader.load()
print(f"Loaded {len(raw_docs)} pages")

# ── 2. CHUNK ─────────────────────────────────────────────────────────────────
splitter = RecursiveCharacterTextSplitter(
    chunk_size=512, chunk_overlap=64,
    separators=["\n\n", "\n", ". ", " ", ""]
)
chunks = splitter.split_documents(raw_docs)
print(f"Created {len(chunks)} chunks")

# ── 3. EMBED + STORE ─────────────────────────────────────────────────────────
embeddings = OpenAIEmbeddings(model="text-embedding-3-small")
# For free local embeddings:
# from langchain_community.embeddings import HuggingFaceEmbeddings
# embeddings = HuggingFaceEmbeddings(model_name="BAAI/bge-small-en-v1.5")

vectorstore = FAISS.from_documents(chunks, embeddings)
vectorstore.save_local("./faiss_index")
print("Index saved")

# ── 4. RETRIEVE ──────────────────────────────────────────────────────────────
vectorstore = FAISS.load_local(
    "./faiss_index", embeddings, allow_dangerous_deserialization=True
)
retriever = vectorstore.as_retriever(search_kwargs={"k": 5})

# ── 5. PROMPT ────────────────────────────────────────────────────────────────
prompt = ChatPromptTemplate.from_template("""
You are a helpful assistant. Answer ONLY based on the provided context.
If the answer is not in the context, respond with "I don't know."

Context:
{context}

Question: {question}

Answer:""")

# ── 6. CHAIN (LCEL) ──────────────────────────────────────────────────────────
def format_docs(docs):
    return "\n\n---\n\n".join(
        f"Source: {d.metadata.get('source','?')} | Page: {d.metadata.get('page','?')}\n{d.page_content}"
        for d in docs
    )

llm = ChatOpenAI(model="gpt-4o-mini", temperature=0)

rag_chain = (
    {"context": retriever | format_docs, "question": RunnablePassthrough()}
    | prompt
    | llm
    | StrOutputParser()
)

# ── 7. QUERY ─────────────────────────────────────────────────────────────────
answer = rag_chain.invoke("What are the main topics covered in the documents?")
print(answer)
```

### 14.2 Advanced RAG with Re-ranking

```python
from sentence_transformers import CrossEncoder

reranker = CrossEncoder("cross-encoder/ms-marco-MiniLM-L-6-v2")

def advanced_retrieve(query: str, top_k: int = 5) -> list:
    # Stage 1: Broad retrieval
    candidates = vectorstore.similarity_search(query, k=20)
    
    # Stage 2: Cross-encoder re-ranking
    pairs = [(query, doc.page_content) for doc in candidates]
    scores = reranker.predict(pairs)
    
    # Sort and return top-k
    ranked = sorted(zip(scores, candidates), reverse=True)[:top_k]
    return [doc for _, doc in ranked]

# Custom retriever wrapper
from langchain_core.retrievers import BaseRetriever
from langchain_core.documents import Document

class RerankedRetriever(BaseRetriever):
    def _get_relevant_documents(self, query: str) -> list[Document]:
        return advanced_retrieve(query)

rag_chain_reranked = (
    {"context": RerankedRetriever() | format_docs, "question": RunnablePassthrough()}
    | prompt | llm | StrOutputParser()
)
```

### 14.3 Agentic RAG with LangChain

```python
from langchain.agents import AgentExecutor, create_react_agent
from langchain.tools.retriever import create_retriever_tool
from langchain import hub

rag_tool = create_retriever_tool(
    retriever,
    name="search_knowledge_base",
    description=(
        "Search the company knowledge base for product information, "
        "policies, and FAQs. Use this for any domain-specific questions."
    )
)

# Add other tools
from langchain_community.tools import TavilySearchResults
web_tool = TavilySearchResults(max_results=3)

tools = [rag_tool, web_tool]

react_prompt = hub.pull("hwchase17/react")
agent = create_react_agent(llm=ChatOpenAI(model="gpt-4o"), tools=tools, prompt=react_prompt)
executor = AgentExecutor(agent=agent, tools=tools, verbose=True, max_iterations=5)

result = executor.invoke({
    "input": "What is our refund policy and does it align with current consumer protection laws?"
})
print(result["output"])
```

### 14.4 Evaluation with RAGAS

```python
from datasets import Dataset
from ragas import evaluate
from ragas.metrics import faithfulness, answer_relevancy, context_recall, context_precision

# Build eval dataset: questions, ground truths, answers, contexts
test_cases = [
    {
        "question": "What is the return policy?",
        "ground_truth": "Items can be returned within 30 days with receipt.",
        "answer": rag_chain.invoke("What is the return policy?"),
        "contexts": [doc.page_content for doc in retriever.invoke("return policy")],
    }
]

dataset = Dataset.from_list(test_cases)
scores = evaluate(dataset, metrics=[faithfulness, answer_relevancy, context_recall, context_precision])
print(scores)
# Expected output: {'faithfulness': 0.95, 'answer_relevancy': 0.92, ...}
```

---

## 15. Best Practices & Anti-Patterns

### Best Practices

| Area | Best Practice |
|---|---|
| **Chunking** | Use recursive splitting with 10–20% overlap; test chunk sizes empirically |
| **Embeddings** | Choose domain-appropriate embedding model; normalize vectors |
| **Retrieval** | Use hybrid (dense + sparse) for production; start with k=5 |
| **Re-ranking** | Always re-rank for high-stakes queries; use cross-encoders |
| **Prompting** | Include explicit grounding instructions and "I don't know" fallback |
| **Evaluation** | Measure faithfulness, recall, precision with RAGAS before shipping |
| **Freshness** | Implement incremental ingestion pipelines with change detection |
| **Security** | Apply per-user RBAC metadata filters on every vector DB query |
| **Caching** | Implement semantic caching for frequently repeated queries |
| **Monitoring** | Track retrieval latency p99, faithfulness score, cache hit rate |

### Anti-Patterns to Avoid

| Anti-Pattern | Problem | Fix |
|---|---|---|
| **Fixed chunk size regardless of content** | Cuts sentences mid-thought | Use recursive or semantic chunking |
| **No chunk overlap** | Loses context at boundaries | Add 10–20% overlap |
| **Single-stage exact search on large corpus** | Slow, poor recall | Use HNSW ANN + re-ranking |
| **Stuffing too many chunks into prompt** | "Lost in the middle", high token cost | Re-rank + compress to top 3–5 |
| **No grounding instruction** | LLM ignores context, hallucinates | Always add "answer only from context" |
| **Ignoring metadata** | No filtering on date/category/access | Add rich metadata at indexing time |
| **One embedding model for all domains** | Semantic mismatch | Fine-tune or use domain-specific models |
| **No evaluation pipeline** | Unknown retrieval/generation quality | Implement RAGAS eval from day one |
| **Synchronous retrieval in production** | High latency | Use async parallel retrieval |
| **No error handling on retrieval miss** | Crashes or hallucinated fallback | Define explicit "not found" behavior |

---

## 16. Key Takeaways

### Conceptual Pillars
- RAG decouples **knowledge** from **reasoning** — the LLM reasons, the vector DB knows
- Chunking, embedding quality, and retrieval strategy are the three biggest levers on RAG performance
- Two-stage retrieval (broad → re-rank) provides both speed and accuracy
- In agentic systems, RAG is a **deliberate tool call**, not an automatic step

### Architecture Choices
- **Naive RAG** → Prototyping and simple use cases
- **Advanced RAG** → Production with quality requirements
- **Modular RAG** → Enterprise systems requiring flexibility
- **GraphRAG** → Complex relationship queries over stable corpora
- **Multi-step RAG** → Multi-hop reasoning tasks

### Evaluation is Non-Negotiable
Always evaluate both retrieval quality (Recall@K, Precision@K) and generation quality (faithfulness, relevance) using frameworks like RAGAS before deploying.

### Security First
In any multi-user or enterprise deployment, **per-user RBAC metadata filtering** on the vector store is mandatory — not optional.

### The Future is Agentic
The most powerful AI systems combine agentic reasoning with RAG — letting the agent decide **when, what, and how** to retrieve. Pure RAG is a building block; agentic RAG is the destination.

---

## Quick Reference Card

```
RAG PIPELINE CHECKLIST
═══════════════════════════════════════════════════
INDEXING
 ✓ Extract clean text (handle PDFs, HTML, tables)
 ✓ Choose chunk_size (256–1024) and overlap (10–20%)
 ✓ Use appropriate embedding model for your domain
 ✓ Store rich metadata (date, source, category, access_level)
 ✓ Normalize embeddings before indexing

RETRIEVAL
 ✓ Use hybrid (dense + sparse) with RRF fusion
 ✓ Apply metadata filters for access control
 ✓ Re-rank with cross-encoder for high-stakes queries
 ✓ Apply context compression to reduce noise
 ✓ Implement semantic caching for repeated queries

GENERATION
 ✓ Include grounding instruction in system prompt
 ✓ Add explicit "I don't know" fallback
 ✓ Order chunks by relevance (most relevant first/last)
 ✓ Include source attribution in output

EVALUATION
 ✓ Measure Faithfulness, Answer Relevancy (RAGAS)
 ✓ Measure Context Recall and Precision
 ✓ Monitor p99 retrieval latency in production
 ✓ Sample-evaluate faithfulness continuously

SECURITY
 ✓ RBAC metadata filter on every query
 ✓ PII redaction before indexing
 ✓ Audit log of all retrievals
 ✓ Input sanitization to prevent prompt injection
═══════════════════════════════════════════════════
```

---

*End of Document — RAG in Agentic AI Systems*
