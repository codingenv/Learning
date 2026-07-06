# Model Context Protocol (MCP) Servers in AI Agents
### A Comprehensive Technical Reference Book for AI Engineers and Systems Architects

---

> **Purpose:** Production engineering reference, internal handbook, and interview preparation guide.
> **Audience:** Intermediate to advanced software engineers, AI developers, and systems architects.
> **Last Updated:** May 2026

---

## Table of Contents

1. [Introduction to MCP](#1-introduction-to-mcp)
2. [Core Concepts of MCP](#2-core-concepts-of-mcp)
3. [MCP Protocol Deep Dive](#3-mcp-protocol-deep-dive)
4. [MCP Server Architecture](#4-mcp-server-architecture)
5. [Building an MCP Server — Step-by-Step Tutorial](#5-building-an-mcp-server--step-by-step-tutorial)
6. [Advanced MCP Server Development](#6-advanced-mcp-server-development)
7. [Integrating MCP with AI Agents](#7-integrating-mcp-with-ai-agents)
8. [Real-World Use Cases](#8-real-world-use-cases)
9. [Security & Governance](#9-security--governance)
10. [Performance Optimization](#10-performance-optimization)
11. [Common Pitfalls](#11-common-pitfalls)
12. [MCP vs Other Frameworks](#12-mcp-vs-other-frameworks)
13. [Future of MCP](#13-future-of-mcp)
14. [Interview Preparation](#14-interview-preparation)
15. [Appendix](#15-appendix)

---

## 1. Introduction to MCP

### 1.1 What is Model Context Protocol (MCP)?

**Model Context Protocol (MCP)** is an open standard protocol introduced by Anthropic in November 2024 that defines a universal, structured communication interface between AI language models (clients) and external tools, data sources, and services (servers). It standardizes how AI agents discover, invoke, and consume external capabilities — replacing ad-hoc integrations with a coherent, interoperable protocol layer.

In essence, MCP does for AI tools what **HTTP did for the web** and what **LSP (Language Server Protocol) did for code editors** — it provides a common language so that any compliant AI client can work with any compliant MCP server without custom glue code.

```
┌──────────────────────────────────────────────────────────────────────┐
│                        THE MCP ECOSYSTEM                             │
│                                                                      │
│  ┌─────────────────┐   MCP Protocol    ┌───────────────────────────┐ │
│  │   AI CLIENT     │ ◄────────────────► │      MCP SERVER           │ │
│  │                 │                    │                           │ │
│  │  Claude         │   JSON-RPC 2.0     │  File System Tools        │ │
│  │  GPT-4o         │   over:            │  Database Connectors      │ │
│  │  Gemini         │   • stdio          │  Web Search               │ │
│  │  Custom LLM     │   • HTTP/SSE       │  Code Execution           │ │
│  │  Agent          │   • WebSocket      │  Calendar / Email         │ │
│  └─────────────────┘                    └───────────────────────────┘ │
└──────────────────────────────────────────────────────────────────────┘
```

**Key characteristics:**
- **Open standard:** Specification is public; anyone can implement a client or server
- **Transport-agnostic:** Works over stdio, HTTP+SSE, WebSocket
- **Capability discovery:** Servers advertise what they can do; clients discover dynamically
- **Bidirectional:** Both client→server requests and server→client notifications
- **Stateful sessions:** Supports persistent connections and stateful interactions

### 1.2 Why MCP Matters in AI Agent Architectures

Before MCP, integrating an AI model with external tools required:
- Custom function schemas per model provider (OpenAI format ≠ Anthropic format)
- Hand-written glue code for every new tool
- No standardized discovery mechanism
- No portable tool servers — tied to a specific SDK or framework

MCP addresses this by acting as a **universal adapter layer**:

| Problem (Pre-MCP) | Solution (With MCP) |
|---|---|
| Every LLM has a different tool-calling format | One MCP server works with all MCP-compatible clients |
| Tools are tightly coupled to a specific framework | MCP servers are framework-independent services |
| No standard discovery mechanism | `tools/list`, `resources/list` for dynamic discovery |
| No standard context injection | Resources and prompts are first-class protocol primitives |
| N×M integration problem | N clients + M servers = N+M implementations |

**The N×M → N+M reduction** is the core value proposition:

```
WITHOUT MCP (N×M):           WITH MCP (N+M):
─────────────────────        ──────────────────────
Claude ──► FileSystem        Claude ──► MCP Protocol ──► FileSystem Server
Claude ──► Database          GPT-4o ──► MCP Protocol ──► Database Server
GPT-4o ──► FileSystem        Gemini ──► MCP Protocol ──► Web Search Server
GPT-4o ──► Database
Gemini ──► FileSystem        Any client works with any server.
Gemini ──► Database          N clients + M servers = N+M adapters.
= N×M adapters needed
```

### 1.3 Evolution of Tool-Use in LLMs

Understanding MCP requires tracing the evolution of how language models interact with external systems:

#### Phase 1: Prompt Engineering (2020–2022)
Tools were described in plain text in the prompt. The model parsed output as text and a wrapper script executed commands.
```
Prompt: "You have access to a calculator. To use it, output: CALC: <expression>"
Output: "CALC: 45 * 12"
Script: Parses output, executes calculation, appends result to prompt
```
**Problem:** Fragile, no structure, model must learn a custom format per tool.

#### Phase 2: Function Calling (2023)
OpenAI introduced structured function calling. Tools are defined in JSON schema; the model returns structured JSON specifying which function to call with which arguments.
```json
{
  "name": "get_weather",
  "arguments": "{\"location\": \"New York\", \"unit\": \"celsius\"}"
}
```
**Problem:** Schema format is provider-specific. Tools are defined per-request, not reusable servers. No standard discovery. State management is manual.

#### Phase 3: Plugins (2023)
OpenAI's ChatGPT Plugins allowed third-party services to register themselves with a manifest. The model could invoke plugin APIs.
**Problem:** Centralized registry (OpenAI controlled), HTTP-only, limited to ChatGPT, no standard protocol.

#### Phase 4: MCP (2024–Present)
A fully open, transport-agnostic, stateful protocol with dynamic capability discovery, bidirectional communication, and a rich primitive set (Tools, Resources, Prompts, Sampling).

```
Phase 1: Plain text  →  Phase 2: JSON function calls  →  Phase 3: Plugins  →  Phase 4: MCP
(fragile)               (provider-specific)               (centralized)         (open standard)
```

### 1.4 MCP vs APIs vs Plugins vs Tool Calling

| Dimension | REST API | Function Calling | Plugins | MCP |
|---|---|---|---|---|
| **Standard** | HTTP/REST | Provider-specific JSON | Provider-controlled | Open specification |
| **Discovery** | Manual / OpenAPI | Per-request definition | Plugin manifest | `tools/list` dynamic |
| **Transport** | HTTP | N/A (in-prompt) | HTTP | stdio, HTTP+SSE, WS |
| **Stateful** | Optional | No | No | Yes (sessions) |
| **Bidirectional** | No (webhooks hack) | No | No | Yes |
| **Context injection** | No | No | Limited | First-class (Resources) |
| **Portability** | High (HTTP) | Low (tied to LLM) | None | High (protocol-level) |
| **Tool server reuse** | No | No | No | Yes |
| **Who controls** | Developer | LLM provider | LLM provider | Open community |

> **Key insight:** REST APIs are for machine-to-machine communication designed by humans. MCP is for AI-agent-to-service communication designed for autonomous agent use — with dynamic discovery, structured schema, and rich context primitives.

---

## 2. Core Concepts of MCP

### 2.1 MCP Architecture Overview

MCP follows a **client-server architecture** with three participating roles:

```
┌───────────────────────────────────────────────────────────────────┐
│                      MCP ARCHITECTURE                             │
│                                                                   │
│  ┌──────────────────────────────────────────────────────────┐     │
│  │                    HOST APPLICATION                       │     │
│  │  (e.g., Claude Desktop, IDE plugin, custom AI app)       │     │
│  │                                                           │     │
│  │   ┌──────────────────────────────────────────────────┐   │     │
│  │   │              MCP CLIENT                          │   │     │
│  │   │  (embedded in host; manages protocol sessions)   │   │     │
│  │   └──────────┬───────────────────────────────────────┘   │     │
│  └──────────────│──────────────────────────────────────────-┘     │
│                 │ MCP Protocol (JSON-RPC 2.0)                      │
│       ┌─────────┼──────────────────────────────┐                  │
│       │         │                              │                  │
│       ▼         ▼                              ▼                  │
│  ┌─────────┐ ┌─────────┐                 ┌─────────┐             │
│  │  MCP    │ │  MCP    │       ...       │  MCP    │             │
│  │ Server  │ │ Server  │                 │ Server  │             │
│  │(files)  │ │(web DB) │                 │(email)  │             │
│  └─────────┘ └─────────┘                 └─────────┘             │
└───────────────────────────────────────────────────────────────────┘
```

**Three roles:**
- **Host:** The application that embeds an LLM and the MCP client (e.g., Claude Desktop, a custom AI agent, an IDE)
- **Client:** The MCP protocol implementation within the host; manages one or more server connections
- **Server:** A lightweight service that exposes Tools, Resources, and Prompts via the MCP protocol

### 2.2 Client-Server Model

The MCP client-server relationship has important properties:

**One client, many servers:** A single MCP client can maintain simultaneous connections to multiple MCP servers. The client aggregates all tools from all connected servers and presents them to the LLM.

**Isolated sessions:** Each client-server pair has an independent session with its own initialization, capabilities negotiation, and lifecycle.

**Server independence:** MCP servers have no knowledge of other MCP servers. They don't communicate with each other — only with the client.

```
MCP Client ────────────────────────────────────────────────────
     │                                                          │
     ├── Session A ──► Server 1 (filesystem tools)             │
     ├── Session B ──► Server 2 (database tools)               │
     ├── Session C ──► Server 3 (web search tools)             │
     └── Session D ──► Server 4 (code execution)               │
                                                                │
  LLM sees: unified tool catalog from all 4 servers combined   │
─────────────────────────────────────────────────────────────--
```

### 2.3 Context Exchange Lifecycle

A complete MCP session follows this lifecycle:

```
Phase 1: INITIALIZATION
  Client → Server: initialize (clientInfo, protocolVersion, capabilities)
  Server → Client: initialize result (serverInfo, protocolVersion, capabilities)
  Client → Server: initialized (notification — session is ready)

Phase 2: CAPABILITY DISCOVERY
  Client → Server: tools/list
  Server → Client: { tools: [{ name, description, inputSchema }, ...] }
  Client → Server: resources/list
  Server → Client: { resources: [{ uri, name, mimeType }, ...] }
  Client → Server: prompts/list
  Server → Client: { prompts: [{ name, description, arguments }, ...] }

Phase 3: OPERATION (repeated)
  Client → Server: tools/call { name: "...", arguments: { ... } }
  Server → Client: { content: [{ type: "text", text: "..." }] }
  Client → Server: resources/read { uri: "file:///path" }
  Server → Client: { contents: [{ uri, mimeType, text }] }

Phase 4: TERMINATION
  Either party closes the connection / transport
```

### 2.4 The Four MCP Primitives

MCP defines four first-class primitives that servers can expose:

#### Tools
**Executable functions** that the AI model can invoke. Tools represent actions — things that *do* something and return a result.

```json
{
  "name": "execute_sql",
  "description": "Execute a read-only SQL query against the analytics database",
  "inputSchema": {
    "type": "object",
    "properties": {
      "query": { "type": "string", "description": "SQL SELECT statement" },
      "limit": { "type": "integer", "default": 100 }
    },
    "required": ["query"]
  }
}
```

#### Resources
**Contextual data** that the server exposes for the client to read. Resources represent information — things to *read* and inject into context.

```json
{
  "uri": "file:///workspace/README.md",
  "name": "Project README",
  "description": "Main documentation file for this project",
  "mimeType": "text/markdown"
}
```

#### Prompts
**Parameterized prompt templates** that servers expose. Prompts allow servers to define reusable, context-aware prompt fragments.

```json
{
  "name": "code_review",
  "description": "Perform a structured code review",
  "arguments": [
    { "name": "language", "description": "Programming language", "required": true },
    { "name": "focus", "description": "Review focus area (security/performance/style)" }
  ]
}
```

#### Sampling
An **LLM invocation primitive** — the server can ask the client to generate a completion. This enables servers to use the LLM's capabilities as part of their own logic (meta-generation).

```json
{
  "method": "sampling/createMessage",
  "params": {
    "messages": [{ "role": "user", "content": { "type": "text", "text": "Summarize this log: ..." } }],
    "maxTokens": 256
  }
}
```

### 2.5 Stateless vs Stateful MCP Servers

| Aspect | Stateless Server | Stateful Server |
|---|---|---|
| **Session persistence** | No state between calls | Maintains session context |
| **Scalability** | Easier to scale horizontally | Requires sticky sessions or shared state store |
| **Use cases** | Simple tools (calculator, search) | Multi-step workflows, authenticated sessions, streaming jobs |
| **Complexity** | Low | Higher (session management) |
| **Example** | Unit converter, date formatter | Database session with open transaction, long-running code execution |

**Stateful example — browser automation:**
```python
# Stateful: maintains browser session across tool calls
class BrowserMCPServer:
    def __init__(self):
        self.sessions = {}  # session_id → browser instance

    def navigate(self, session_id, url):
        browser = self.sessions.get(session_id) or self._create_browser()
        self.sessions[session_id] = browser
        browser.goto(url)
        return browser.content()

    def click(self, session_id, selector):
        browser = self.sessions[session_id]  # reuse existing session
        browser.click(selector)
```

---

## 3. MCP Protocol Deep Dive

### 3.1 Communication Model

MCP uses **JSON-RPC 2.0** as its messaging format over pluggable transport layers.

#### JSON-RPC 2.0 Message Types

**Request (expects a response):**
```json
{
  "jsonrpc": "2.0",
  "id": "req-001",
  "method": "tools/call",
  "params": {
    "name": "get_weather",
    "arguments": { "city": "London" }
  }
}
```

**Response (reply to a request):**
```json
{
  "jsonrpc": "2.0",
  "id": "req-001",
  "result": {
    "content": [
      { "type": "text", "text": "London: 18°C, Partly cloudy" }
    ]
  }
}
```

**Error Response:**
```json
{
  "jsonrpc": "2.0",
  "id": "req-001",
  "error": {
    "code": -32602,
    "message": "Invalid params",
    "data": { "field": "city", "reason": "must be a string" }
  }
}
```

**Notification (no response expected):**
```json
{
  "jsonrpc": "2.0",
  "method": "notifications/tools/list_changed",
  "params": {}
}
```

#### Transport Layers

| Transport | Description | Use Case |
|---|---|---|
| **stdio** | Messages over stdin/stdout | Local subprocess servers; Claude Desktop integrations |
| **HTTP + SSE** | HTTP requests + Server-Sent Events for streaming | Remote servers; web-based integrations |
| **WebSocket** | Full-duplex persistent connection | Low-latency, high-frequency tool interactions |

**stdio transport flow:**
```
Host Process                       MCP Server Process
─────────────                      ─────────────────
Spawns server as subprocess
        │
        ├──── stdin ──────────────► server reads JSON-RPC messages
        │
        ◄──── stdout ──────────────  server writes JSON-RPC messages
        │
        └──── stderr ──────────────  server logs (not parsed by client)
```

**HTTP + SSE transport flow:**
```
MCP Client                         MCP Server (HTTP)
──────────                         ─────────────────
POST /message ──────────────────►  Handle request
◄─── 202 Accepted ─────────────── 

GET  /sse ──────────────────────►  Open SSE stream
◄─── event: message ─────────────  Push response via SSE
◄─── event: message ─────────────  Push streaming updates
◄─── event: close ───────────────  Signal completion
```

### 3.2 Initialization and Capability Negotiation

The initialization handshake establishes what both sides can do:

**Client → Server (initialize request):**
```json
{
  "jsonrpc": "2.0",
  "id": 1,
  "method": "initialize",
  "params": {
    "protocolVersion": "2024-11-05",
    "clientInfo": {
      "name": "claude-desktop",
      "version": "0.7.0"
    },
    "capabilities": {
      "roots": { "listChanged": true },
      "sampling": {}
    }
  }
}
```

**Server → Client (initialize response):**
```json
{
  "jsonrpc": "2.0",
  "id": 1,
  "result": {
    "protocolVersion": "2024-11-05",
    "serverInfo": {
      "name": "filesystem-server",
      "version": "1.2.0"
    },
    "capabilities": {
      "tools": { "listChanged": true },
      "resources": { "subscribe": true, "listChanged": true },
      "prompts": { "listChanged": false },
      "logging": {}
    }
  }
}
```

### 3.3 Tool Registration and Invocation

#### Tool List Response
```json
{
  "jsonrpc": "2.0",
  "id": 2,
  "result": {
    "tools": [
      {
        "name": "read_file",
        "description": "Read the contents of a file from the filesystem",
        "inputSchema": {
          "type": "object",
          "properties": {
            "path": {
              "type": "string",
              "description": "Absolute or relative path to the file"
            },
            "encoding": {
              "type": "string",
              "enum": ["utf-8", "base64"],
              "default": "utf-8"
            }
          },
          "required": ["path"]
        }
      },
      {
        "name": "write_file",
        "description": "Write content to a file. Creates file if it doesn't exist.",
        "inputSchema": {
          "type": "object",
          "properties": {
            "path": { "type": "string" },
            "content": { "type": "string" },
            "createDirectories": { "type": "boolean", "default": false }
          },
          "required": ["path", "content"]
        }
      }
    ]
  }
}
```

#### Tool Call Request and Response
```json
// REQUEST
{
  "jsonrpc": "2.0",
  "id": 5,
  "method": "tools/call",
  "params": {
    "name": "read_file",
    "arguments": { "path": "/workspace/config.yaml" }
  }
}

// SUCCESS RESPONSE
{
  "jsonrpc": "2.0",
  "id": 5,
  "result": {
    "content": [
      {
        "type": "text",
        "text": "database:\n  host: localhost\n  port: 5432\n  name: myapp\n"
      }
    ],
    "isError": false
  }
}

// ERROR RESPONSE (tool executed but returned an error — not a protocol error)
{
  "jsonrpc": "2.0",
  "id": 5,
  "result": {
    "content": [
      { "type": "text", "text": "Error: File not found: /workspace/config.yaml" }
    ],
    "isError": true
  }
}
```

> **Important distinction:** A tool execution failure (file not found, query error) returns a **result** with `isError: true`, not a JSON-RPC error. JSON-RPC errors are reserved for protocol-level failures (invalid request format, unknown method, etc.).

### 3.4 Resources — Reading Contextual Data

```json
// LIST RESOURCES
{
  "jsonrpc": "2.0", "id": 3,
  "method": "resources/list"
}

// RESPONSE
{
  "jsonrpc": "2.0", "id": 3,
  "result": {
    "resources": [
      {
        "uri": "db://customers/schema",
        "name": "Customer DB Schema",
        "description": "PostgreSQL schema for the customers database",
        "mimeType": "application/json"
      },
      {
        "uri": "config://app/settings",
        "name": "Application Configuration",
        "mimeType": "application/yaml"
      }
    ]
  }
}

// READ A RESOURCE
{
  "jsonrpc": "2.0", "id": 4,
  "method": "resources/read",
  "params": { "uri": "db://customers/schema" }
}

// RESPONSE
{
  "jsonrpc": "2.0", "id": 4,
  "result": {
    "contents": [
      {
        "uri": "db://customers/schema",
        "mimeType": "application/json",
        "text": "{ \"tables\": [{ \"name\": \"customers\", \"columns\": [...] }] }"
      }
    ]
  }
}
```

### 3.5 Streaming Responses

For long-running tools (e.g., file search, code execution), MCP supports streaming progress via notifications:

```json
// Server pushes progress notifications while executing
{ "jsonrpc": "2.0", "method": "notifications/progress",
  "params": { "progressToken": "exec-001", "progress": 25, "total": 100 }}

{ "jsonrpc": "2.0", "method": "notifications/progress",
  "params": { "progressToken": "exec-001", "progress": 75, "total": 100 }}

// Final result arrives as normal response
{ "jsonrpc": "2.0", "id": 7, "result": { "content": [...] }}
```

### 3.6 Error Handling

MCP uses standard JSON-RPC error codes plus custom MCP error codes:

| Code | Name | Description |
|---|---|---|
| `-32700` | Parse error | Invalid JSON received |
| `-32600` | Invalid request | JSON-RPC structure invalid |
| `-32601` | Method not found | Unknown method name |
| `-32602` | Invalid params | Parameters don't match schema |
| `-32603` | Internal error | Server-side unexpected error |
| `-32000` | Server error | Generic MCP server error |
| `-32001` | Resource not found | Requested URI doesn't exist |
| `-32002` | Unauthorized | Authentication required or failed |
| `-32003` | Rate limited | Too many requests |

**Error handling best practice in a client:**
```python
async def safe_tool_call(client, tool_name, arguments):
    try:
        result = await client.call_tool(tool_name, arguments)
        if result.isError:
            # Tool-level error — inform the LLM, let it decide next steps
            return f"Tool error: {result.content[0].text}"
        return result.content[0].text
    except MCPError as e:
        if e.code == -32002:
            raise AuthenticationError("MCP server requires authentication")
        elif e.code == -32003:
            await asyncio.sleep(1)  # Backoff, retry
            return await safe_tool_call(client, tool_name, arguments)
        else:
            raise  # Propagate unexpected errors
```

---

## 4. MCP Server Architecture

### 4.1 Internal Architecture

A production MCP server is composed of several well-defined layers:

```
┌─────────────────────────────────────────────────────────────────────┐
│                        MCP SERVER INTERNALS                         │
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │                   TRANSPORT LAYER                           │    │
│  │  stdin/stdout │ HTTP+SSE handler │ WebSocket handler        │    │
│  └─────────────────────┬───────────────────────────────────────┘    │
│                        │ raw bytes/JSON                             │
│  ┌─────────────────────▼───────────────────────────────────────┐    │
│  │               JSON-RPC PROTOCOL LAYER                       │    │
│  │  Parse │ Validate │ Route │ Serialize responses             │    │
│  └─────────────────────┬───────────────────────────────────────┘    │
│                        │ typed MCP messages                        │
│  ┌─────────────────────▼───────────────────────────────────────┐    │
│  │                 REQUEST ROUTER                              │    │
│  │  initialize │ tools/* │ resources/* │ prompts/* │ ping     │    │
│  └──────┬──────────────┬───────────────┬──────────────────────┘    │
│         │              │               │                            │
│  ┌──────▼──────┐ ┌──────▼──────┐ ┌──────▼────────┐                 │
│  │    TOOL     │ │  RESOURCE   │ │    PROMPT     │                 │
│  │  REGISTRY   │ │  REGISTRY   │ │   REGISTRY    │                 │
│  └──────┬──────┘ └──────┬──────┘ └──────┬────────┘                 │
│         │              │               │                            │
│  ┌──────▼──────────────▼───────────────▼────────────────────────┐  │
│  │                   MIDDLEWARE PIPELINE                        │  │
│  │  Auth  │  Rate Limit  │  Input Validation  │  Audit Log     │  │
│  └──────────────────────────┬─────────────────────────────────-─┘  │
│                             │                                       │
│  ┌──────────────────────────▼──────────────────────────────────┐    │
│  │                   TOOL IMPLEMENTATIONS                      │    │
│  │  Business logic, external API calls, DB queries, etc.       │    │
│  └─────────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────┘
```

### 4.2 Request Routing Pipeline

Every incoming MCP request passes through this pipeline:

```
Incoming Request
      │
      ▼
[1] PARSE         — Deserialize JSON, validate JSON-RPC structure
      │
      ▼
[2] AUTHENTICATE  — Verify token/API key/mTLS certificate
      │
      ▼
[3] AUTHORIZE     — Check caller has permission for this method/tool
      │
      ▼
[4] RATE LIMIT    — Token bucket / sliding window check
      │
      ▼
[5] ROUTE         — Dispatch to correct handler (tools, resources, prompts)
      │
      ▼
[6] VALIDATE      — Validate tool arguments against JSON Schema
      │
      ▼
[7] EXECUTE       — Run tool logic (may be async, may call external systems)
      │
      ▼
[8] TRANSFORM     — Normalize output to MCP content format
      │
      ▼
[9] AUDIT LOG     — Record: who called what, when, with what args, what result
      │
      ▼
[10] RESPOND      — Serialize and send JSON-RPC response
```

### 4.3 Tool Registry Design

The tool registry is the central catalog of all tools the server exposes. A well-designed registry supports:

```python
from dataclasses import dataclass, field
from typing import Any, Callable, Awaitable

@dataclass
class ToolDefinition:
    name: str
    description: str
    input_schema: dict
    handler: Callable[..., Awaitable[Any]]
    requires_auth: bool = True
    rate_limit_per_minute: int = 60
    timeout_seconds: int = 30
    tags: list[str] = field(default_factory=list)

class ToolRegistry:
    def __init__(self):
        self._tools: dict[str, ToolDefinition] = {}

    def register(self, tool: ToolDefinition):
        self._validate_schema(tool.input_schema)
        self._tools[tool.name] = tool

    def get(self, name: str) -> ToolDefinition:
        if name not in self._tools:
            raise MCPError(-32601, f"Tool not found: {name}")
        return self._tools[name]

    def list_all(self) -> list[dict]:
        return [
            {
                "name": t.name,
                "description": t.description,
                "inputSchema": t.input_schema
            }
            for t in self._tools.values()
        ]

    def _validate_schema(self, schema: dict):
        import jsonschema
        jsonschema.Draft7Validator.check_schema(schema)
```

### 4.4 Input Validation and Schema Enforcement

Every tool call must be validated against its declared JSON Schema before execution:

```python
import jsonschema
from jsonschema import ValidationError

class InputValidator:
    def validate(self, tool: ToolDefinition, arguments: dict) -> None:
        try:
            jsonschema.validate(instance=arguments, schema=tool.input_schema)
        except ValidationError as e:
            raise MCPError(
                code=-32602,
                message="Invalid params",
                data={
                    "field": list(e.absolute_path),
                    "message": e.message,
                    "schema_path": list(e.absolute_schema_path)
                }
            )
```

### 4.5 Security Layers

A production MCP server must implement multiple security layers:

```
┌─────────────────────────────────────────────────────┐
│                  SECURITY LAYERS                    │
│                                                     │
│  Layer 1: TRANSPORT SECURITY                        │
│  ├── TLS for HTTP transport                         │
│  ├── Unix socket permissions for stdio              │
│  └── Origin validation for WebSocket               │
│                                                     │
│  Layer 2: AUTHENTICATION                            │
│  ├── API key (header or param)                      │
│  ├── OAuth 2.0 / JWT bearer token                  │
│  └── mTLS client certificates                      │
│                                                     │
│  Layer 3: AUTHORIZATION                             │
│  ├── Role-based tool access                         │
│  ├── Scoped permissions per tool                    │
│  └── Resource-level read/write control             │
│                                                     │
│  Layer 4: INPUT SANITIZATION                        │
│  ├── JSON Schema validation                         │
│  ├── Path traversal prevention                      │
│  └── SQL injection prevention                      │
│                                                     │
│  Layer 5: SANDBOXING                                │
│  ├── OS-level process isolation                     │
│  ├── Network egress restrictions                    │
│  └── Filesystem access controls                    │
└─────────────────────────────────────────────────────┘
```

---

## 5. Building an MCP Server — Step-by-Step Tutorial

### 5.1 Step 1: Environment Setup

#### Python Setup
```bash
# Create virtual environment
python -m venv mcp-env
source mcp-env/bin/activate  # Windows: mcp-env\Scripts\activate

# Install MCP SDK
pip install mcp

# Verify
python -c "import mcp; print(mcp.__version__)"
```

#### Node.js Setup
```bash
# Initialize project
mkdir my-mcp-server && cd my-mcp-server
npm init -y

# Install MCP SDK
npm install @modelcontextprotocol/sdk

# TypeScript (recommended)
npm install -D typescript @types/node tsx
npx tsc --init
```

**`tsconfig.json` for Node.js MCP server:**
```json
{
  "compilerOptions": {
    "target": "ES2022",
    "module": "Node16",
    "moduleResolution": "Node16",
    "outDir": "./dist",
    "rootDir": "./src",
    "strict": true,
    "esModuleInterop": true
  }
}
```

### 5.2 Step 2: Create a Minimal MCP Server

#### Python — Minimal Server
```python
# server.py
from mcp.server import Server
from mcp.server.stdio import stdio_server
from mcp import types

# Create server instance
app = Server("my-first-mcp-server")

@app.list_tools()
async def list_tools() -> list[types.Tool]:
    return [
        types.Tool(
            name="ping",
            description="Returns 'pong' — health check tool",
            inputSchema={
                "type": "object",
                "properties": {},
                "required": []
            }
        )
    ]

@app.call_tool()
async def call_tool(name: str, arguments: dict) -> list[types.TextContent]:
    if name == "ping":
        return [types.TextContent(type="text", text="pong")]
    raise ValueError(f"Unknown tool: {name}")

async def main():
    async with stdio_server() as (read_stream, write_stream):
        await app.run(read_stream, write_stream, app.create_initialization_options())

if __name__ == "__main__":
    import asyncio
    asyncio.run(main())
```

#### Node.js (TypeScript) — Minimal Server
```typescript
// src/server.ts
import { Server } from "@modelcontextprotocol/sdk/server/index.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";
import {
  CallToolRequestSchema,
  ListToolsRequestSchema,
} from "@modelcontextprotocol/sdk/types.js";

const server = new Server(
  { name: "my-first-mcp-server", version: "1.0.0" },
  { capabilities: { tools: {} } }
);

server.setRequestHandler(ListToolsRequestSchema, async () => ({
  tools: [
    {
      name: "ping",
      description: "Returns pong — health check",
      inputSchema: { type: "object", properties: {}, required: [] },
    },
  ],
}));

server.setRequestHandler(CallToolRequestSchema, async (request) => {
  if (request.params.name === "ping") {
    return { content: [{ type: "text", text: "pong" }] };
  }
  throw new Error(`Unknown tool: ${request.params.name}`);
});

const transport = new StdioServerTransport();
await server.connect(transport);
console.error("MCP server running on stdio");
```

### 5.3 Step 3: Define Tools — Calculator Example

#### Python — Calculator MCP Server with Full Schema
```python
# calculator_server.py
from mcp.server import Server
from mcp.server.stdio import stdio_server
from mcp import types
import asyncio
import math

app = Server("calculator-mcp-server")

TOOLS = [
    types.Tool(
        name="calculate",
        description="Evaluate a mathematical expression safely",
        inputSchema={
            "type": "object",
            "properties": {
                "expression": {
                    "type": "string",
                    "description": "Math expression e.g. '2 + 3 * 4', 'sqrt(16)', 'pi * r**2'"
                }
            },
            "required": ["expression"]
        }
    ),
    types.Tool(
        name="unit_convert",
        description="Convert between units of measurement",
        inputSchema={
            "type": "object",
            "properties": {
                "value": { "type": "number" },
                "from_unit": { "type": "string", "enum": ["km", "miles", "kg", "lbs", "celsius", "fahrenheit"] },
                "to_unit": { "type": "string", "enum": ["km", "miles", "kg", "lbs", "celsius", "fahrenheit"] }
            },
            "required": ["value", "from_unit", "to_unit"]
        }
    )
]

SAFE_MATH_GLOBALS = {
    "sqrt": math.sqrt, "sin": math.sin, "cos": math.cos,
    "tan": math.tan, "log": math.log, "log10": math.log10,
    "pi": math.pi, "e": math.e, "abs": abs, "round": round,
    "__builtins__": {}
}

CONVERSIONS = {
    ("km", "miles"): lambda x: x * 0.621371,
    ("miles", "km"): lambda x: x * 1.60934,
    ("kg", "lbs"): lambda x: x * 2.20462,
    ("lbs", "kg"): lambda x: x * 0.453592,
    ("celsius", "fahrenheit"): lambda x: x * 9/5 + 32,
    ("fahrenheit", "celsius"): lambda x: (x - 32) * 5/9,
}

@app.list_tools()
async def list_tools() -> list[types.Tool]:
    return TOOLS

@app.call_tool()
async def call_tool(name: str, arguments: dict) -> list[types.TextContent]:
    if name == "calculate":
        expr = arguments["expression"]
        try:
            result = eval(expr, SAFE_MATH_GLOBALS)
            return [types.TextContent(type="text", text=f"{expr} = {result}")]
        except Exception as ex:
            return [types.TextContent(type="text", text=f"Error: {ex}"), ]

    elif name == "unit_convert":
        v, f, t = arguments["value"], arguments["from_unit"], arguments["to_unit"]
        key = (f, t)
        if key not in CONVERSIONS:
            return [types.TextContent(type="text", text=f"No conversion available from {f} to {t}")]
        result = CONVERSIONS[key](v)
        return [types.TextContent(type="text", text=f"{v} {f} = {result:.4f} {t}")]

    raise ValueError(f"Unknown tool: {name}")

async def main():
    async with stdio_server() as (read_stream, write_stream):
        await app.run(read_stream, write_stream, app.create_initialization_options())

if __name__ == "__main__":
    asyncio.run(main())
```

### 5.4 Step 4: Implement Tool Logic — Database Query Tool

#### Python — Database MCP Server
```python
# database_server.py
import asyncio
import asyncpg
from mcp.server import Server
from mcp.server.stdio import stdio_server
from mcp import types
import os

app = Server("database-mcp-server")

DB_CONFIG = {
    "host": os.getenv("DB_HOST", "localhost"),
    "port": int(os.getenv("DB_PORT", "5432")),
    "database": os.getenv("DB_NAME", "myapp"),
    "user": os.getenv("DB_USER", "readonly_user"),
    "password": os.getenv("DB_PASSWORD", ""),
}

_pool: asyncpg.Pool = None

async def get_pool():
    global _pool
    if _pool is None:
        _pool = await asyncpg.create_pool(**DB_CONFIG, min_size=2, max_size=10)
    return _pool

@app.list_tools()
async def list_tools() -> list[types.Tool]:
    return [
        types.Tool(
            name="query_database",
            description="Execute a READ-ONLY SQL query. Only SELECT statements are allowed.",
            inputSchema={
                "type": "object",
                "properties": {
                    "sql": {
                        "type": "string",
                        "description": "SQL SELECT statement to execute"
                    },
                    "limit": {
                        "type": "integer",
                        "description": "Maximum rows to return (default 50, max 500)",
                        "minimum": 1,
                        "maximum": 500,
                        "default": 50
                    }
                },
                "required": ["sql"]
            }
        ),
        types.Tool(
            name="list_tables",
            description="List all available tables in the database",
            inputSchema={"type": "object", "properties": {}, "required": []}
        )
    ]

@app.call_tool()
async def call_tool(name: str, arguments: dict) -> list[types.TextContent]:
    pool = await get_pool()

    if name == "query_database":
        sql = arguments["sql"].strip()
        limit = arguments.get("limit", 50)

        # Security: only allow SELECT
        if not sql.upper().startswith("SELECT"):
            return [types.TextContent(type="text",
                text="Error: Only SELECT statements are permitted.")]

        # Enforce limit
        if "LIMIT" not in sql.upper():
            sql = f"{sql} LIMIT {limit}"

        async with pool.acquire() as conn:
            rows = await conn.fetch(sql)
            if not rows:
                return [types.TextContent(type="text", text="Query returned 0 rows.")]

            # Format as markdown table
            columns = list(rows[0].keys())
            header = "| " + " | ".join(columns) + " |"
            separator = "| " + " | ".join(["---"] * len(columns)) + " |"
            data_rows = "\n".join(
                "| " + " | ".join(str(row[c]) for c in columns) + " |"
                for row in rows
            )
            table = f"{header}\n{separator}\n{data_rows}"
            return [types.TextContent(type="text",
                text=f"**{len(rows)} rows returned:**\n\n{table}")]

    elif name == "list_tables":
        async with pool.acquire() as conn:
            rows = await conn.fetch(
                "SELECT table_name FROM information_schema.tables "
                "WHERE table_schema = 'public' ORDER BY table_name"
            )
            tables = [row["table_name"] for row in rows]
            return [types.TextContent(type="text",
                text="Available tables:\n" + "\n".join(f"- {t}" for t in tables))]

async def main():
    async with stdio_server() as (read_stream, write_stream):
        await app.run(read_stream, write_stream, app.create_initialization_options())

if __name__ == "__main__":
    asyncio.run(main())
```

### 5.5 Step 5: Run and Test the Server

#### Running via stdio (direct)
```bash
python calculator_server.py
```
The server waits on stdin. You can test by sending JSON-RPC manually:
```bash
echo '{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"protocolVersion":"2024-11-05","clientInfo":{"name":"test","version":"1.0"},"capabilities":{}}}' | python calculator_server.py
```

#### Using MCP Inspector (recommended testing tool)
```bash
npx @modelcontextprotocol/inspector python calculator_server.py
```
Opens a web UI at `http://localhost:5173` where you can:
- Browse registered tools, resources, prompts
- Execute tool calls interactively
- View raw JSON-RPC messages

#### Writing Automated Tests (Python)
```python
# test_calculator_server.py
import pytest
import asyncio
from mcp import ClientSession, StdioServerParameters
from mcp.client.stdio import stdio_client

@pytest.mark.asyncio
async def test_calculator_ping():
    server_params = StdioServerParameters(
        command="python", args=["calculator_server.py"]
    )
    async with stdio_client(server_params) as (read, write):
        async with ClientSession(read, write) as session:
            await session.initialize()

            # List tools
            tools = await session.list_tools()
            tool_names = [t.name for t in tools.tools]
            assert "calculate" in tool_names
            assert "unit_convert" in tool_names

            # Call calculator
            result = await session.call_tool("calculate", {"expression": "2 + 3 * 4"})
            assert "14" in result.content[0].text

            # Unit conversion
            result = await session.call_tool(
                "unit_convert",
                {"value": 100, "from_unit": "km", "to_unit": "miles"}
            )
            assert "62.1371" in result.content[0].text

@pytest.mark.asyncio
async def test_unknown_tool():
    server_params = StdioServerParameters(command="python", args=["calculator_server.py"])
    async with stdio_client(server_params) as (read, write):
        async with ClientSession(read, write) as session:
            await session.initialize()
            with pytest.raises(Exception):
                await session.call_tool("nonexistent_tool", {})
```

#### Claude Desktop Integration (claude_desktop_config.json)
```json
{
  "mcpServers": {
    "calculator": {
      "command": "python",
      "args": ["/path/to/calculator_server.py"],
      "env": {}
    },
    "database": {
      "command": "python",
      "args": ["/path/to/database_server.py"],
      "env": {
        "DB_HOST": "localhost",
        "DB_NAME": "myapp",
        "DB_USER": "readonly_user",
        "DB_PASSWORD": "secret"
      }
    }
  }
}
```

---

## 6. Advanced MCP Server Development

### 6.1 Multi-Tool Server Architecture

Production MCP servers typically expose tens to hundreds of tools organized by domain. The key is a modular, plugin-based architecture:

```python
# multi_tool_server.py
from mcp.server import Server
from mcp.server.stdio import stdio_server
from mcp import types
import asyncio

# Import tool modules
from tools.filesystem_tools import FilesystemTools
from tools.web_tools import WebTools
from tools.data_tools import DataTools

app = Server("enterprise-mcp-server")

# Initialize tool modules
fs_tools = FilesystemTools(root_path="/workspace")
web_tools = WebTools(allowed_domains=["api.company.com", "docs.company.com"])
data_tools = DataTools(db_url=os.getenv("DATABASE_URL"))

ALL_TOOLS = [*fs_tools.definitions, *web_tools.definitions, *data_tools.definitions]

HANDLERS = {
    **fs_tools.handlers,
    **web_tools.handlers,
    **data_tools.handlers
}

@app.list_tools()
async def list_tools() -> list[types.Tool]:
    return ALL_TOOLS

@app.call_tool()
async def call_tool(name: str, arguments: dict) -> list[types.TextContent]:
    handler = HANDLERS.get(name)
    if not handler:
        raise ValueError(f"Unknown tool: {name}")
    return await handler(arguments)
```

**Tool module pattern:**
```python
# tools/filesystem_tools.py
from mcp import types
import aiofiles, aiofiles.os

class FilesystemTools:
    def __init__(self, root_path: str):
        self.root = root_path
        self.definitions = [
            types.Tool(name="read_file", description="...", inputSchema={...}),
            types.Tool(name="write_file", description="...", inputSchema={...}),
            types.Tool(name="list_directory", description="...", inputSchema={...}),
        ]
        self.handlers = {
            "read_file": self.read_file,
            "write_file": self.write_file,
            "list_directory": self.list_directory,
        }

    def _safe_path(self, path: str) -> str:
        """Prevent path traversal — all paths must be within root."""
        import os
        full = os.path.normpath(os.path.join(self.root, path))
        if not full.startswith(self.root):
            raise PermissionError(f"Path outside root: {path}")
        return full

    async def read_file(self, arguments: dict) -> list[types.TextContent]:
        safe = self._safe_path(arguments["path"])
        async with aiofiles.open(safe, "r") as f:
            content = await f.read()
        return [types.TextContent(type="text", text=content)]
```

### 6.2 Tool Chaining

Tool chaining allows one tool's output to feed into another automatically. Implement via a workflow engine on the server:

```python
@app.call_tool()
async def call_tool(name: str, arguments: dict) -> list[types.TextContent]:
    if name == "analyze_and_summarize":
        # Chain: fetch_url → extract_text → summarize
        url = arguments["url"]

        # Step 1: Fetch
        raw_html = await fetch_url(url)

        # Step 2: Extract text
        text = await extract_text_from_html(raw_html)

        # Step 3: Summarize using sampling (ask the LLM client)
        # This inverts the usual flow — server asks client to use LLM
        summary_result = await app.request_context.session.create_message(
            messages=[{
                "role": "user",
                "content": {"type": "text", "text": f"Summarize in 3 bullets:\n\n{text[:3000]}"}
            }],
            max_tokens=300
        )
        return [types.TextContent(type="text", text=summary_result.content.text)]
```

### 6.3 Async Execution and Long-Running Tasks

For tasks that take more than a few seconds, use background execution with progress notifications:

```python
import asyncio
from uuid import uuid4

_running_jobs: dict[str, asyncio.Task] = {}

@app.call_tool()
async def call_tool(name: str, arguments: dict) -> list[types.TextContent]:
    if name == "run_analysis":
        job_id = str(uuid4())
        task = asyncio.create_task(
            run_analysis_job(job_id, arguments)
        )
        _running_jobs[job_id] = task
        return [types.TextContent(type="text",
            text=f"Analysis started. Job ID: {job_id}. Use check_job_status to poll.")]

    elif name == "check_job_status":
        job_id = arguments["job_id"]
        task = _running_jobs.get(job_id)
        if not task:
            return [types.TextContent(type="text", text=f"No job found: {job_id}")]
        if task.done():
            result = task.result()
            del _running_jobs[job_id]
            return [types.TextContent(type="text", text=f"Complete:\n{result}")]
        return [types.TextContent(type="text", text=f"Job {job_id} is still running...")]

async def run_analysis_job(job_id: str, arguments: dict) -> str:
    # Simulate long-running work
    for step in range(5):
        await asyncio.sleep(2)
        # Send progress notification to client
        await app.request_context.session.send_progress_notification(
            progress_token=job_id,
            progress=(step + 1) * 20,
            total=100
        )
    return "Analysis complete: findings here..."
```

### 6.4 Caching Strategies

```python
import functools, time
from typing import Any

class TTLCache:
    def __init__(self, ttl_seconds: int = 300):
        self._cache: dict[str, tuple[Any, float]] = {}
        self._ttl = ttl_seconds

    def get(self, key: str) -> Any | None:
        if key in self._cache:
            value, ts = self._cache[key]
            if time.time() - ts < self._ttl:
                return value
            del self._cache[key]
        return None

    def set(self, key: str, value: Any):
        self._cache[key] = (value, time.time())

_cache = TTLCache(ttl_seconds=300)

async def cached_web_search(query: str) -> str:
    cache_key = f"search:{query}"
    if (hit := _cache.get(cache_key)) is not None:
        return hit
    result = await do_actual_search(query)
    _cache.set(cache_key, result)
    return result
```

**For distributed deployments, use Redis:**
```python
import redis.asyncio as aioredis
import json, hashlib

redis_client = aioredis.from_url(os.getenv("REDIS_URL", "redis://localhost"))

async def cached_tool_call(tool_name: str, arguments: dict, ttl: int = 300) -> str:
    key = f"mcp:{tool_name}:{hashlib.md5(json.dumps(arguments, sort_keys=True).encode()).hexdigest()}"
    cached = await redis_client.get(key)
    if cached:
        return cached.decode()
    result = await execute_tool(tool_name, arguments)
    await redis_client.setex(key, ttl, result)
    return result
```

### 6.5 Observability — Logging, Tracing, and Metrics

```python
import structlog
from opentelemetry import trace
from opentelemetry.sdk.trace import TracerProvider
from opentelemetry.sdk.trace.export import BatchSpanProcessor
from opentelemetry.exporter.otlp.proto.grpc.trace_exporter import OTLPSpanExporter
from prometheus_client import Counter, Histogram, start_http_server
import time

# Structured logging
log = structlog.get_logger()

# OpenTelemetry tracing
provider = TracerProvider()
provider.add_span_processor(BatchSpanProcessor(OTLPSpanExporter()))
trace.set_tracer_provider(provider)
tracer = trace.get_tracer("mcp-server")

# Prometheus metrics
TOOL_CALLS = Counter("mcp_tool_calls_total", "Total tool calls", ["tool_name", "status"])
TOOL_LATENCY = Histogram("mcp_tool_duration_seconds", "Tool execution latency", ["tool_name"])

@app.call_tool()
async def call_tool(name: str, arguments: dict) -> list[types.TextContent]:
    start = time.time()
    with tracer.start_as_current_span(f"tool/{name}") as span:
        span.set_attribute("tool.name", name)
        span.set_attribute("tool.arguments", str(arguments))
        try:
            result = await _execute_tool(name, arguments)
            TOOL_CALLS.labels(tool_name=name, status="success").inc()
            log.info("tool_called", tool=name, duration=time.time()-start, status="success")
            return result
        except Exception as e:
            TOOL_CALLS.labels(tool_name=name, status="error").inc()
            span.record_exception(e)
            log.error("tool_failed", tool=name, error=str(e))
            raise
        finally:
            TOOL_LATENCY.labels(tool_name=name).observe(time.time() - start)

# Start metrics HTTP server
start_http_server(8000)  # Prometheus scrapes at :8000/metrics
```

---

## 7. Integrating MCP with AI Agents

### 7.1 Integration Architecture Overview

```
┌──────────────────────────────────────────────────────────────────┐
│                    AGENT INTEGRATION FLOW                        │
│                                                                  │
│  User Input                                                      │
│      │                                                           │
│      ▼                                                           │
│  ┌────────────────────────────────────────────────────────┐      │
│  │                   AI AGENT HOST                        │      │
│  │                                                        │      │
│  │  ┌──────────────┐    ┌──────────────────────────────┐  │      │
│  │  │     LLM      │    │       MCP CLIENT             │  │      │
│  │  │  (GPT/Claude │◄──►│  - tools/list on startup     │  │      │
│  │  │   /Gemini)   │    │  - tools/call on agent req   │  │      │
│  │  └──────────────┘    │  - manages N server sessions │  │      │
│  │                      └──────────────┬───────────────┘  │      │
│  └─────────────────────────────────────│────────────────-─┘      │
│                                        │                         │
│              ┌─────────────────────────┼────────────────────┐    │
│              ▼                         ▼                    ▼    │
│        ┌──────────┐            ┌──────────────┐      ┌──────────┐ │
│        │MCP Server│            │  MCP Server  │      │MCP Server│ │
│        │(filesystem│           │ (database)   │      │(web APIs)│ │
│        └──────────┘            └──────────────┘      └──────────┘ │
└──────────────────────────────────────────────────────────────────┘
```

### 7.2 Integration with OpenAI API

The OpenAI API uses its own function-calling format. To bridge MCP tools with OpenAI, translate MCP tool definitions to OpenAI's function format at runtime.

```python
# openai_mcp_agent.py
import asyncio
import json
from openai import AsyncOpenAI
from mcp import ClientSession, StdioServerParameters
from mcp.client.stdio import stdio_client

openai_client = AsyncOpenAI()

def mcp_tool_to_openai_function(tool) -> dict:
    """Convert MCP Tool definition to OpenAI function calling format."""
    return {
        "type": "function",
        "function": {
            "name": tool.name,
            "description": tool.description,
            "parameters": tool.inputSchema,
        }
    }

async def run_agent_with_mcp(user_message: str):
    server_params = StdioServerParameters(
        command="python",
        args=["calculator_server.py"]
    )

    async with stdio_client(server_params) as (read, write):
        async with ClientSession(read, write) as mcp_session:
            await mcp_session.initialize()

            # Discover all tools from MCP server
            mcp_tools_result = await mcp_session.list_tools()
            openai_tools = [
                mcp_tool_to_openai_function(t)
                for t in mcp_tools_result.tools
            ]

            messages = [{"role": "user", "content": user_message}]

            # Agentic loop
            while True:
                response = await openai_client.chat.completions.create(
                    model="gpt-4o",
                    messages=messages,
                    tools=openai_tools,
                    tool_choice="auto"
                )

                choice = response.choices[0]
                messages.append(choice.message.model_dump())

                # No tool calls — agent is done
                if choice.finish_reason == "stop":
                    return choice.message.content

                # Execute each tool call via MCP
                if choice.finish_reason == "tool_calls":
                    for tool_call in choice.message.tool_calls:
                        tool_name = tool_call.function.name
                        tool_args = json.loads(tool_call.function.arguments)

                        print(f"[Agent] Calling MCP tool: {tool_name}({tool_args})")

                        # Route through MCP
                        mcp_result = await mcp_session.call_tool(tool_name, tool_args)
                        tool_output = mcp_result.content[0].text

                        messages.append({
                            "role": "tool",
                            "tool_call_id": tool_call.id,
                            "content": tool_output
                        })

# Run
result = asyncio.run(run_agent_with_mcp(
    "What is 15% tip on a $47.50 bill? Also convert the total to GBP if 1 USD = 0.79 GBP"
))
print(result)
```

### 7.3 Integration with LangChain

LangChain provides a native `MCP` integration. Alternatively, you can wrap MCP tools as LangChain `BaseTool` instances.

#### Using LangChain's MCP Adapter
```python
# langchain_mcp_agent.py
from langchain_mcp_adapters.client import MultiServerMCPClient
from langchain_openai import ChatOpenAI
from langgraph.prebuilt import create_react_agent

async def run_langchain_mcp_agent():
    # Configure multiple MCP servers
    async with MultiServerMCPClient({
        "calculator": {
            "command": "python",
            "args": ["calculator_server.py"],
            "transport": "stdio",
        },
        "filesystem": {
            "command": "npx",
            "args": ["-y", "@modelcontextprotocol/server-filesystem", "/workspace"],
            "transport": "stdio",
        },
        "web_search": {
            "url": "http://localhost:8080/sse",
            "transport": "sse",
        }
    }) as client:
        # Get all tools from all connected MCP servers
        tools = await client.get_tools()
        print(f"Discovered {len(tools)} tools across all MCP servers")

        llm = ChatOpenAI(model="gpt-4o", temperature=0)
        agent = create_react_agent(llm, tools)

        result = await agent.ainvoke({
            "messages": [("user", "Search for the latest Python release and save a summary to /workspace/python_news.txt")]
        })
        return result["messages"][-1].content

import asyncio
print(asyncio.run(run_langchain_mcp_agent()))
```

#### Manual Wrapper (without adapter library)
```python
from langchain_core.tools import BaseTool
from pydantic import BaseModel
from typing import Type, Any

class MCPToolWrapper(BaseTool):
    """Wraps an MCP tool as a LangChain-compatible tool."""
    name: str
    description: str
    args_schema: Type[BaseModel]
    _mcp_session: Any = None
    _tool_name: str = ""

    def __init__(self, mcp_session, mcp_tool):
        # Dynamically create Pydantic model from JSON Schema
        schema_model = jsonschema_to_pydantic(mcp_tool.inputSchema)
        super().__init__(
            name=mcp_tool.name,
            description=mcp_tool.description,
            args_schema=schema_model
        )
        self._mcp_session = mcp_session
        self._tool_name = mcp_tool.name

    async def _arun(self, **kwargs) -> str:
        result = await self._mcp_session.call_tool(self._tool_name, kwargs)
        return result.content[0].text

    def _run(self, **kwargs) -> str:
        import asyncio
        return asyncio.run(self._arun(**kwargs))
```

### 7.4 Integration with LlamaIndex

```python
# llamaindex_mcp_agent.py
from llama_index.core.agent import ReActAgent
from llama_index.core.tools import FunctionTool
from llama_index.llms.openai import OpenAI
from mcp import ClientSession, StdioServerParameters
from mcp.client.stdio import stdio_client
import asyncio, json

async def create_llamaindex_tools_from_mcp(server_params):
    tools = []
    async with stdio_client(server_params) as (read, write):
        async with ClientSession(read, write) as session:
            await session.initialize()
            mcp_tools = await session.list_tools()

            for mcp_tool in mcp_tools.tools:
                # Capture tool in closure
                def make_tool_fn(t_name, t_session_params):
                    def tool_fn(**kwargs):
                        async def _call():
                            async with stdio_client(t_session_params) as (r, w):
                                async with ClientSession(r, w) as s:
                                    await s.initialize()
                                    result = await s.call_tool(t_name, kwargs)
                                    return result.content[0].text
                        return asyncio.run(_call())
                    return tool_fn

                fn = make_tool_fn(mcp_tool.name, server_params)
                fn.__doc__ = mcp_tool.description
                fn.__name__ = mcp_tool.name

                tools.append(FunctionTool.from_defaults(fn=fn, name=mcp_tool.name))
    return tools

async def main():
    server_params = StdioServerParameters(command="python", args=["calculator_server.py"])
    tools = await create_llamaindex_tools_from_mcp(server_params)
    llm = OpenAI(model="gpt-4o")
    agent = ReActAgent.from_tools(tools, llm=llm, verbose=True)
    response = agent.chat("What is the square root of 144 plus 20% of 500?")
    print(response)

asyncio.run(main())
```

### 7.5 Custom Agent Framework Integration

For teams building their own agent frameworks, here is a reference integration:

```python
# custom_agent.py
import asyncio, json
from dataclasses import dataclass
from openai import AsyncOpenAI
from mcp import ClientSession
from mcp.client.stdio import stdio_client, StdioServerParameters

@dataclass
class AgentConfig:
    model: str = "gpt-4o"
    max_iterations: int = 10
    system_prompt: str = "You are a helpful AI assistant with access to tools."

class MCPAgent:
    def __init__(self, config: AgentConfig, server_configs: list[dict]):
        self.config = config
        self.server_configs = server_configs
        self.llm = AsyncOpenAI()
        self._sessions: list[ClientSession] = []
        self._all_tools: list = []

    async def __aenter__(self):
        for sc in self.server_configs:
            params = StdioServerParameters(command=sc["command"], args=sc.get("args", []))
            ctx = stdio_client(params)
            read, write = await ctx.__aenter__()
            session = ClientSession(read, write)
            await session.__aenter__()
            await session.initialize()
            self._sessions.append(session)

            tools = await session.list_tools()
            for t in tools.tools:
                self._all_tools.append({
                    "mcp_tool": t,
                    "session": session,
                    "openai_format": {
                        "type": "function",
                        "function": {
                            "name": t.name,
                            "description": t.description,
                            "parameters": t.inputSchema
                        }
                    }
                })
        return self

    async def __aexit__(self, *args):
        for s in self._sessions:
            await s.__aexit__(*args)

    async def _find_session_for_tool(self, tool_name: str) -> ClientSession:
        for t in self._all_tools:
            if t["mcp_tool"].name == tool_name:
                return t["session"]
        raise ValueError(f"No session found for tool: {tool_name}")

    async def run(self, user_input: str) -> str:
        messages = [
            {"role": "system", "content": self.config.system_prompt},
            {"role": "user", "content": user_input}
        ]
        openai_tools = [t["openai_format"] for t in self._all_tools]

        for iteration in range(self.config.max_iterations):
            response = await self.llm.chat.completions.create(
                model=self.config.model,
                messages=messages,
                tools=openai_tools if openai_tools else None,
                tool_choice="auto" if openai_tools else None
            )
            choice = response.choices[0]
            messages.append(choice.message.model_dump(exclude_none=True))

            if choice.finish_reason == "stop":
                return choice.message.content

            if choice.finish_reason == "tool_calls":
                for tc in choice.message.tool_calls:
                    session = await self._find_session_for_tool(tc.function.name)
                    args = json.loads(tc.function.arguments)
                    mcp_result = await session.call_tool(tc.function.name, args)
                    messages.append({
                        "role": "tool",
                        "tool_call_id": tc.id,
                        "content": mcp_result.content[0].text
                    })
        return "Max iterations reached."

# Usage
async def main():
    config = AgentConfig(model="gpt-4o", max_iterations=8)
    servers = [
        {"command": "python", "args": ["calculator_server.py"]},
        {"command": "python", "args": ["database_server.py"]},
    ]
    async with MCPAgent(config, servers) as agent:
        answer = await agent.run("How many customers do we have, and what is 5% of that number?")
        print(answer)

asyncio.run(main())
```

### 7.6 How Agents Discover and Invoke MCP Tools

**Discovery flow:**
```
1. Agent host spawns/connects to configured MCP servers
2. Client sends tools/list to each server
3. Client aggregates tool catalog (all servers combined)
4. Client formats tools for the LLM (OpenAI, Anthropic, etc. format)
5. LLM receives unified tool list in its system context

On tool call:
6. LLM outputs structured tool invocation
7. Client parses tool name, looks up which server owns it
8. Client routes tools/call to the correct server session
9. Server executes and returns result
10. Client returns result to LLM as tool output
```

**Name collision handling:** When two servers expose a tool with the same name, the client must namespace them:
```python
# Prefix tool names with server name to avoid collisions
def namespace_tool(server_name: str, tool: MCPTool) -> MCPTool:
    return MCPTool(
        name=f"{server_name}__{tool.name}",  # e.g., "filesystem__read_file"
        description=f"[{server_name}] {tool.description}",
        inputSchema=tool.inputSchema
    )
```

---

## 8. Real-World Use Cases

### 8.1 Enterprise Automation

**Scenario:** An AI assistant that can autonomously execute multi-step business workflows across enterprise systems.

```
MCP Server Architecture for Enterprise Copilot:

┌─────────────────────────────────────────────────────────────────┐
│                     ENTERPRISE AI COPILOT                       │
│                                                                 │
│  User: "Onboard new employee John Smith starting Monday"        │
│                  │                                              │
│                  ▼                                              │
│            AI AGENT (LLM)                                       │
│                  │                                              │
│    ┌─────────────┼──────────────┬─────────────────────────┐     │
│    ▼             ▼              ▼                         ▼     │
│ ┌──────┐   ┌──────────┐  ┌──────────┐              ┌──────────┐ │
│ │ HR   │   │ IT/AD    │  │ Slack    │              │ Payroll  │ │
│ │ MCP  │   │ MCP      │  │ MCP      │              │ MCP      │ │
│ │Server│   │ Server   │  │ Server   │              │ Server   │ │
│ └──────┘   └──────────┘  └──────────┘              └──────────┘ │
│                                                                 │
│ Agent actions:                                                  │
│  1. hr_server.create_employee_record(name, start_date, dept)   │
│  2. it_server.create_ad_account(email, groups, permissions)    │
│  3. it_server.provision_laptop(employee_id, device_type)       │
│  4. slack_server.invite_to_channels(email, [#general, #eng])   │
│  5. payroll_server.setup_direct_deposit(employee_id, bank_info)│
└─────────────────────────────────────────────────────────────────┘
```

**Sample HR MCP Server tool (Python):**
```python
types.Tool(
    name="create_employee_record",
    description="Create a new employee record in the HR system. Returns employee ID.",
    inputSchema={
        "type": "object",
        "properties": {
            "full_name": {"type": "string"},
            "email": {"type": "string", "format": "email"},
            "department": {"type": "string"},
            "job_title": {"type": "string"},
            "start_date": {"type": "string", "format": "date"},
            "manager_email": {"type": "string", "format": "email"}
        },
        "required": ["full_name", "email", "department", "job_title", "start_date"]
    }
)
```

### 8.2 Data Retrieval and Analytics Systems

**Scenario:** A natural language analytics copilot that lets non-technical users query complex datasets.

```python
# Analytics MCP Server — key tools exposed:
ANALYTICS_TOOLS = [
    "query_sales_database",      # SQL against sales data warehouse
    "get_kpi_dashboard",         # Pre-computed KPI snapshot
    "generate_chart",            # Matplotlib chart → base64 image
    "export_to_csv",             # Export query results
    "schedule_report",           # Schedule recurring reports
    "compare_periods",           # YoY / QoQ comparison helper
]
```

**User interaction:**
```
User: "Show me top 10 products by revenue last quarter vs same quarter last year"

Agent:
  Action 1: query_sales_database(sql="SELECT ... WHERE quarter = 'Q1 2025'")
  Action 2: query_sales_database(sql="SELECT ... WHERE quarter = 'Q1 2024'")
  Action 3: compare_periods(current=result1, previous=result2)
  Action 4: generate_chart(data=comparison, chart_type="bar", title="Top 10 Products Q1 YoY")
  Response: Displays table + chart with narrative analysis
```

### 8.3 DevOps Copilot

**Scenario:** An AI agent with read/write access to infrastructure and CI/CD systems.

```
DevOps MCP Servers:
├── kubernetes_server      → get_pods, scale_deployment, get_logs, rollback
├── github_server          → list_prs, get_diff, run_workflow, create_issue
├── datadog_server         → query_metrics, get_alerts, create_dashboard
├── terraform_server       → plan, apply (with human approval gate), show_state
└── pagerduty_server       → list_incidents, acknowledge, escalate
```

**Incident response workflow:**
```
Alert: "Production API latency > 2000ms"

Agent:
  1. datadog_server.query_metrics("avg:api.latency{env:prod}", last=30m)
     → Latency spike started 12 mins ago on pods api-7b4d9-* 
  2. kubernetes_server.get_pods(namespace="prod", label="app=api")
     → 3 of 5 pods are in CrashLoopBackOff
  3. kubernetes_server.get_logs(pod="api-7b4d9-xk2p1", tail=50)
     → OutOfMemoryError in ReportService.generatePDF()
  4. github_server.get_recent_commits(repo="api-service", count=5)
     → Last deploy 15 mins ago: "Optimize PDF generation"
  5. kubernetes_server.rollback(deployment="api", revision=2)
     → Deployment rolled back. All pods healthy.
  6. github_server.create_issue(
       title="PDF optimization OOM - rolled back",
       body="Deploy commit abc123 caused OOM. Rolled back to rev 2.",
       labels=["bug", "production-incident"]
     )
  7. pagerduty_server.acknowledge(incident_id="INC-4521")
```

### 8.4 Personal AI Assistant

**Scenario:** A personal productivity assistant with access to calendar, email, notes, and web.

```
Personal Assistant MCP Servers:
├── google_calendar_server  → list_events, create_event, find_free_time
├── gmail_server            → search_emails, read_email, compose_draft, send
├── notion_server           → search_pages, create_page, update_page
├── web_search_server       → search, fetch_url, summarize_page
└── weather_server          → get_forecast, get_current
```

**Sample interaction:**
```
User: "I need to schedule a 1-hour product review with Sarah next week. 
       Draft an agenda based on our last meeting notes and email it to her."

Agent:
  1. google_calendar_server.find_free_time(
       attendees=["sarah@company.com", "me@company.com"],
       duration=60, week="next"
     ) → Tuesday 2pm or Thursday 10am available
  2. notion_server.search_pages(query="product review meeting notes")
     → Found: "Product Review - March 15" with 5 action items
  3. google_calendar_server.create_event(
       title="Product Review", time="Tuesday 2pm", duration=60,
       attendees=["sarah@company.com"]
     )
  4. gmail_server.compose_draft(
       to="sarah@company.com",
       subject="Product Review — Tuesday 2pm + Agenda",
       body="[Draft with agenda based on previous notes and new action items]"
     )
  Response: "Scheduled for Tuesday 2pm. Draft email ready — want me to send it?"
```

---

## 9. Security & Governance

### 9.1 Authentication Strategies

MCP servers must authenticate callers before executing any tool. Three primary approaches:

#### API Key Authentication
```python
# middleware/auth.py
import os
from functools import wraps

VALID_API_KEYS = set(os.getenv("MCP_API_KEYS", "").split(","))

class APIKeyAuthMiddleware:
    def __init__(self, server):
        self.server = server
        self._original_call_tool = server.call_tool

    def enforce(self):
        @wraps(self._original_call_tool)
        async def authenticated_call(name, arguments, headers=None):
            api_key = (headers or {}).get("X-API-Key", "")
            if api_key not in VALID_API_KEYS:
                raise MCPError(-32002, "Unauthorized: Invalid API key")
            return await self._original_call_tool(name, arguments)
        self.server.call_tool = authenticated_call
```

#### JWT Bearer Token Authentication
```python
import jwt
from datetime import datetime

JWT_SECRET = os.getenv("JWT_SECRET")
JWT_ALGORITHM = "HS256"

def verify_jwt(token: str) -> dict:
    try:
        payload = jwt.decode(token, JWT_SECRET, algorithms=[JWT_ALGORITHM])
        if payload["exp"] < datetime.utcnow().timestamp():
            raise MCPError(-32002, "Token expired")
        return payload
    except jwt.InvalidTokenError as e:
        raise MCPError(-32002, f"Invalid token: {e}")

def require_scopes(*required_scopes):
    """Decorator to enforce JWT scope-based authorization."""
    def decorator(handler):
        async def wrapper(arguments, context):
            payload = verify_jwt(context.auth_token)
            user_scopes = set(payload.get("scopes", []))
            if not all(s in user_scopes for s in required_scopes):
                raise MCPError(-32002, f"Missing required scopes: {required_scopes}")
            return await handler(arguments, context)
        return wrapper
    return decorator

# Usage
@require_scopes("database:read")
async def query_database_handler(arguments, context):
    ...
```

#### OAuth 2.0 with PKCE (for browser-based MCP clients)
```python
# OAuth flow for HTTP+SSE transport
from authlib.integrations.httpx_client import AsyncOAuth2Client

async def create_oauth_mcp_session(client_id: str, auth_url: str, token_url: str):
    client = AsyncOAuth2Client(client_id=client_id)
    uri, state, code_verifier = client.create_authorization_url(
        auth_url,
        code_challenge_method="S256"
    )
    # Redirect user to `uri`, capture authorization code
    # Then exchange for token:
    token = await client.fetch_token(
        token_url,
        authorization_response=callback_url,
        code_verifier=code_verifier
    )
    return token["access_token"]
```

### 9.2 Role-Based Access Control (RBAC)

```python
from enum import Enum
from dataclasses import dataclass

class Permission(Enum):
    READ = "read"
    WRITE = "write"
    EXECUTE = "execute"
    ADMIN = "admin"

ROLE_PERMISSIONS = {
    "viewer":    {Permission.READ},
    "analyst":   {Permission.READ, Permission.EXECUTE},
    "developer": {Permission.READ, Permission.WRITE, Permission.EXECUTE},
    "admin":     {Permission.READ, Permission.WRITE, Permission.EXECUTE, Permission.ADMIN},
}

TOOL_REQUIRED_PERMISSIONS = {
    "read_file":          Permission.READ,
    "query_database":     Permission.READ,
    "write_file":         Permission.WRITE,
    "execute_code":       Permission.EXECUTE,
    "delete_resource":    Permission.ADMIN,
    "modify_config":      Permission.ADMIN,
}

def check_permission(user_role: str, tool_name: str) -> bool:
    required = TOOL_REQUIRED_PERMISSIONS.get(tool_name, Permission.READ)
    granted = ROLE_PERMISSIONS.get(user_role, set())
    return required in granted

@app.call_tool()
async def call_tool(name: str, arguments: dict) -> list[types.TextContent]:
    user_role = get_current_user_role()  # from session/token
    if not check_permission(user_role, name):
        raise MCPError(-32002, f"Role '{user_role}' not permitted to call '{name}'")
    ...
```

### 9.3 Rate Limiting and Abuse Prevention

```python
import asyncio
from collections import defaultdict
import time

class TokenBucketRateLimiter:
    """Per-client token bucket rate limiter."""

    def __init__(self, capacity: int = 60, refill_rate: float = 1.0):
        self.capacity = capacity
        self.refill_rate = refill_rate  # tokens per second
        self._buckets: dict[str, dict] = defaultdict(
            lambda: {"tokens": capacity, "last_refill": time.time()}
        )
        self._lock = asyncio.Lock()

    async def check(self, client_id: str) -> bool:
        async with self._lock:
            bucket = self._buckets[client_id]
            now = time.time()
            elapsed = now - bucket["last_refill"]
            bucket["tokens"] = min(
                self.capacity,
                bucket["tokens"] + elapsed * self.refill_rate
            )
            bucket["last_refill"] = now

            if bucket["tokens"] >= 1:
                bucket["tokens"] -= 1
                return True
            return False

rate_limiter = TokenBucketRateLimiter(capacity=60, refill_rate=1.0)

@app.call_tool()
async def call_tool(name: str, arguments: dict) -> list[types.TextContent]:
    client_id = get_current_client_id()
    if not await rate_limiter.check(client_id):
        raise MCPError(-32003, "Rate limit exceeded. Please retry after 1 second.")
    ...
```

**Per-tool rate limits:**
```python
TOOL_RATE_LIMITS = {
    "web_search":      TokenBucketRateLimiter(capacity=10, refill_rate=0.2),  # 10/min
    "execute_code":    TokenBucketRateLimiter(capacity=5, refill_rate=0.08),  # 5/min
    "query_database":  TokenBucketRateLimiter(capacity=30, refill_rate=0.5),  # 30/min
}

async def check_tool_rate_limit(client_id: str, tool_name: str):
    limiter = TOOL_RATE_LIMITS.get(tool_name)
    if limiter and not await limiter.check(client_id):
        raise MCPError(-32003, f"Rate limit for '{tool_name}' exceeded.")
```

### 9.4 Input Sanitization and Injection Prevention

```python
import re, os

class SecuritySanitizer:

    @staticmethod
    def sanitize_file_path(path: str, allowed_root: str) -> str:
        """Prevent path traversal attacks."""
        normalized = os.path.normpath(os.path.join(allowed_root, path))
        if not normalized.startswith(os.path.abspath(allowed_root)):
            raise MCPError(-32602, f"Path traversal attempt detected: {path}")
        return normalized

    @staticmethod
    def sanitize_sql(sql: str) -> str:
        """Rudimentary SQL injection guard — use parameterized queries instead."""
        dangerous_keywords = ["DROP", "DELETE", "TRUNCATE", "ALTER", "INSERT", "UPDATE", "EXEC"]
        sql_upper = sql.upper()
        for keyword in dangerous_keywords:
            if re.search(r'\b' + keyword + r'\b', sql_upper):
                raise MCPError(-32602, f"Dangerous SQL keyword detected: {keyword}")
        return sql

    @staticmethod
    def sanitize_shell_command(cmd: str) -> str:
        """Prevent shell injection in code execution tools."""
        dangerous_patterns = [";", "&&", "||", "`", "$(",  ">", "<", "|"]
        for pattern in dangerous_patterns:
            if pattern in cmd:
                raise MCPError(-32602, f"Shell injection attempt: '{pattern}' is not allowed")
        return cmd
```

### 9.5 Sandboxing Tool Execution

```python
# Sandboxed code execution using Docker
import asyncio, docker

async def execute_code_sandboxed(code: str, language: str = "python") -> str:
    client = docker.from_env()

    LANGUAGE_IMAGES = {
        "python": "python:3.11-slim",
        "node":   "node:20-alpine",
        "bash":   "bash:5",
    }

    image = LANGUAGE_IMAGES.get(language, "python:3.11-slim")

    try:
        container = client.containers.run(
            image=image,
            command=["python", "-c", code] if language == "python" else ["node", "-e", code],
            mem_limit="128m",          # Memory cap
            cpu_period=100000,
            cpu_quota=50000,           # 50% CPU cap
            network_mode="none",       # No network access
            read_only=True,            # Immutable filesystem
            remove=True,
            timeout=10,                # 10-second hard timeout
            user="nobody",             # Non-root user
        )
        return container.decode("utf-8")
    except docker.errors.ContainerError as e:
        return f"Execution error:\n{e.stderr.decode()}"
    except Exception as e:
        return f"Sandbox error: {e}"
```

### 9.6 Audit Logging

```python
import json
from datetime import datetime, timezone

class AuditLogger:
    def __init__(self, log_file: str = "mcp_audit.jsonl"):
        self.log_file = log_file

    async def log_tool_call(
        self,
        client_id: str,
        tool_name: str,
        arguments: dict,
        result_summary: str,
        success: bool,
        duration_ms: float
    ):
        entry = {
            "timestamp": datetime.now(timezone.utc).isoformat(),
            "event": "tool_call",
            "client_id": client_id,
            "tool": tool_name,
            "arguments": self._redact_sensitive(arguments),
            "success": success,
            "result_summary": result_summary[:200],
            "duration_ms": round(duration_ms, 2)
        }
        with open(self.log_file, "a") as f:
            f.write(json.dumps(entry) + "\n")

    def _redact_sensitive(self, data: dict) -> dict:
        SENSITIVE_FIELDS = {"password", "token", "secret", "api_key", "credit_card"}
        return {
            k: "***REDACTED***" if k.lower() in SENSITIVE_FIELDS else v
            for k, v in data.items()
        }
```

---

## 10. Performance Optimization

### 10.1 Scaling MCP Servers

#### Horizontal Scaling with Load Balancer

For HTTP+SSE transport, MCP servers can be horizontally scaled behind a load balancer:

```
                    ┌──────────────────────────────────────────┐
                    │           LOAD BALANCER (nginx/HAProxy)   │
                    │   Sticky sessions for stateful servers    │
                    └───────┬──────────────┬───────────────────┘
                            │              │
              ┌─────────────▼──┐    ┌──────▼──────────┐
              │  MCP Server    │    │   MCP Server    │
              │  Instance 1    │    │   Instance 2    │
              │  (port 8081)   │    │   (port 8082)   │
              └────────────────┘    └─────────────────┘
                       │                    │
              ┌────────▼────────────────────▼────────┐
              │         Shared State Store            │
              │   (Redis — sessions, cache, locks)    │
              └───────────────────────────────────────┘
```

**nginx configuration for MCP HTTP+SSE:**
```nginx
upstream mcp_backend {
    ip_hash;  # Sticky sessions for stateful servers
    server mcp1:8081;
    server mcp2:8082;
    server mcp3:8083;
    keepalive 32;
}

server {
    listen 443 ssl;
    server_name mcp.company.com;

    location / {
        proxy_pass http://mcp_backend;
        proxy_http_version 1.1;
        proxy_set_header Connection "";

        # SSE-specific settings
        proxy_read_timeout 3600s;      # Keep SSE connections alive
        proxy_buffering off;           # Disable buffering for SSE
        proxy_cache off;
        chunked_transfer_encoding on;
    }
}
```

#### Stateless Scaling (Preferred)
For most tool servers, make them stateless and rely on Redis for any shared state:

```python
import redis.asyncio as aioredis

redis = aioredis.from_url(os.getenv("REDIS_URL"))

@app.call_tool()
async def call_tool(name: str, arguments: dict) -> list[types.TextContent]:
    if name == "get_user_context":
        user_id = arguments["user_id"]
        # Read shared state from Redis instead of in-process memory
        context = await redis.get(f"user_context:{user_id}")
        return [types.TextContent(type="text", text=context or "No context found")]
```

### 10.2 Connection Pooling and Resource Management

```python
# database_pool.py
import asyncpg
from contextlib import asynccontextmanager

class DatabasePool:
    _instance = None

    @classmethod
    async def get(cls) -> asyncpg.Pool:
        if cls._instance is None:
            cls._instance = await asyncpg.create_pool(
                dsn=os.getenv("DATABASE_URL"),
                min_size=5,
                max_size=20,
                max_inactive_connection_lifetime=300.0,
                command_timeout=30.0,
            )
        return cls._instance

    @classmethod
    @asynccontextmanager
    async def acquire(cls):
        pool = await cls.get()
        async with pool.acquire() as conn:
            yield conn

# In tool handlers — reuse pooled connections
async def query_database_handler(arguments: dict):
    async with DatabasePool.acquire() as conn:
        rows = await conn.fetch(arguments["sql"])
        ...
```

### 10.3 Parallel Tool Execution

When an agent calls multiple independent tools, execute them in parallel:

```python
import asyncio

async def execute_tools_parallel(
    session: ClientSession,
    tool_calls: list[dict]
) -> list[str]:
    """Execute multiple tool calls concurrently."""
    tasks = [
        session.call_tool(tc["name"], tc["arguments"])
        for tc in tool_calls
    ]
    results = await asyncio.gather(*tasks, return_exceptions=True)

    outputs = []
    for result, tc in zip(results, tool_calls):
        if isinstance(result, Exception):
            outputs.append(f"Tool '{tc['name']}' failed: {result}")
        else:
            outputs.append(result.content[0].text)
    return outputs

# Example: Fetch weather for 3 cities simultaneously
results = await execute_tools_parallel(session, [
    {"name": "get_weather", "arguments": {"city": "London"}},
    {"name": "get_weather", "arguments": {"city": "Paris"}},
    {"name": "get_weather", "arguments": {"city": "Tokyo"}},
])
```

### 10.4 Latency Control and Timeouts

```python
import asyncio

async def call_tool_with_timeout(
    session: ClientSession,
    tool_name: str,
    arguments: dict,
    timeout_seconds: float = 30.0
) -> str:
    try:
        result = await asyncio.wait_for(
            session.call_tool(tool_name, arguments),
            timeout=timeout_seconds
        )
        return result.content[0].text
    except asyncio.TimeoutError:
        return f"Error: Tool '{tool_name}' timed out after {timeout_seconds}s"
```

**Tiered timeout strategy by tool type:**
```python
TOOL_TIMEOUTS = {
    "ping":            2.0,
    "get_weather":     5.0,
    "query_database":  30.0,
    "execute_code":    60.0,
    "run_analysis":    300.0,  # Long-running
}
```

### 10.5 Response Compression and Payload Optimization

```python
import gzip, json

def compress_large_response(data: str, threshold_bytes: int = 10_000) -> str:
    """Compress text responses over threshold before sending."""
    if len(data.encode()) > threshold_bytes:
        compressed = gzip.compress(data.encode())
        b64 = __import__("base64").b64encode(compressed).decode()
        return f"[GZIP_BASE64] {b64}"
    return data

def truncate_response(data: str, max_chars: int = 50_000) -> str:
    """Truncate extremely large responses to avoid context overflow."""
    if len(data) > max_chars:
        return data[:max_chars] + f"\n\n[... TRUNCATED — {len(data) - max_chars} chars omitted]"
    return data
```

### 10.6 Benchmarking and Performance Profiling

```python
# benchmark_mcp.py
import asyncio, time, statistics
from mcp import ClientSession, StdioServerParameters
from mcp.client.stdio import stdio_client

async def benchmark_tool(server_params, tool_name, arguments, iterations=100):
    latencies = []
    async with stdio_client(server_params) as (read, write):
        async with ClientSession(read, write) as session:
            await session.initialize()
            for _ in range(iterations):
                start = time.perf_counter()
                await session.call_tool(tool_name, arguments)
                latencies.append((time.perf_counter() - start) * 1000)

    print(f"\nTool: {tool_name} | Iterations: {iterations}")
    print(f"  Mean:   {statistics.mean(latencies):.2f}ms")
    print(f"  Median: {statistics.median(latencies):.2f}ms")
    print(f"  P95:    {sorted(latencies)[int(0.95*len(latencies))]:.2f}ms")
    print(f"  P99:    {sorted(latencies)[int(0.99*len(latencies))]:.2f}ms")
    print(f"  Min:    {min(latencies):.2f}ms | Max: {max(latencies):.2f}ms")

asyncio.run(benchmark_tool(
    StdioServerParameters(command="python", args=["calculator_server.py"]),
    "calculate", {"expression": "sqrt(144) + 2**8"},
    iterations=200
))
```

---

## 11. Common Pitfalls

### 11.1 Poor Schema Design

**Anti-pattern:** Vague, unstructured tool definitions.
```json
// BAD — too vague, no schema constraints
{
  "name": "do_stuff",
  "description": "Does stuff with data",
  "inputSchema": {
    "type": "object",
    "properties": {
      "data": { "type": "string" }
    }
  }
}
```

**Best practice:** Precise names, rich descriptions, strict schemas with enums and constraints.
```json
// GOOD — precise, constrained, self-documenting
{
  "name": "resize_image",
  "description": "Resize an image to specified dimensions. Returns the path of the resized image.",
  "inputSchema": {
    "type": "object",
    "properties": {
      "source_path": {
        "type": "string",
        "description": "Absolute path to the source image file"
      },
      "width": {
        "type": "integer",
        "minimum": 1, "maximum": 8192,
        "description": "Target width in pixels"
      },
      "height": {
        "type": "integer",
        "minimum": 1, "maximum": 8192,
        "description": "Target height in pixels"
      },
      "format": {
        "type": "string",
        "enum": ["jpeg", "png", "webp"],
        "default": "jpeg"
      },
      "quality": {
        "type": "integer",
        "minimum": 1, "maximum": 100,
        "default": 85,
        "description": "Compression quality (1=worst, 100=best). Only for jpeg/webp."
      }
    },
    "required": ["source_path", "width", "height"]
  }
}
```

**Why it matters:** The LLM uses the schema and description to decide how and when to call a tool. Poor descriptions lead to wrong tool selection. Missing constraints lead to invalid inputs reaching your business logic.

### 11.2 Overloading MCP with Business Logic

**Anti-pattern:** Putting complex orchestration, data transformation, and business rules inside the MCP server.

```python
# BAD — MCP server as a monolith
@app.call_tool()
async def call_tool(name, arguments):
    if name == "process_order":
        # MCP server doing too much:
        customer = await fetch_customer(arguments["customer_id"])
        inventory = await check_inventory(arguments["items"])
        discount = calculate_loyalty_discount(customer)
        fraud_score = await run_fraud_check(customer, arguments)
        if fraud_score > 0.8:
            await notify_fraud_team(...)
        order = await create_order(...)
        await send_confirmation_email(...)
        await update_inventory(...)
        return format_response(order)
```

**Best practice:** MCP tools should be **thin, focused wrappers** around underlying services. Keep business logic in your domain services; MCP is just the interface.

```python
# GOOD — MCP as thin interface, business logic in service layer
@app.call_tool()
async def call_tool(name, arguments):
    if name == "create_order":
        # Single, focused action
        order_id = await order_service.create(
            customer_id=arguments["customer_id"],
            items=arguments["items"]
        )
        return [types.TextContent(type="text", text=f"Order created: {order_id}")]
    elif name == "check_order_status":
        status = await order_service.get_status(arguments["order_id"])
        return [types.TextContent(type="text", text=str(status))]
```

### 11.3 Improper Tool Granularity

**Too coarse** (one tool does everything → LLM can't control sub-steps):
```python
# BAD
{"name": "manage_files", "description": "Read, write, delete, or list files"}
```

**Too fine** (hundreds of trivial tools → overwhelming the LLM):
```python
# BAD
{"name": "read_first_line_of_file"}
{"name": "read_second_line_of_file"}
{"name": "count_characters_in_file"}
# ... 50 more micro-tools
```

**Just right** (meaningful, composable actions):
```python
# GOOD
{"name": "read_file",       "description": "Read entire file content"}
{"name": "write_file",      "description": "Write/overwrite file content"}
{"name": "list_directory",  "description": "List files in a directory"}
{"name": "search_in_files", "description": "Search for text patterns across files"}
```

**Rule of thumb:** Each tool should map to one coherent user-level action that can be described in a single sentence.

### 11.4 Ignoring the `isError` Flag

A common pitfall is not distinguishing between protocol errors and tool execution errors:

```python
# BAD — treats all results the same
result = await session.call_tool("query_database", {"sql": bad_sql})
print(result.content[0].text)  # Silently passes error text to LLM as if it were data

# GOOD — check isError and handle appropriately
result = await session.call_tool("query_database", {"sql": query})
if result.isError:
    # Return error context to LLM so it can retry or report
    return f"The tool returned an error: {result.content[0].text}"
else:
    return result.content[0].text
```

### 11.5 Not Handling Connection Failures

```python
# BAD — no reconnect logic
session = await create_session()
result = await session.call_tool(...)  # Fails silently if server died

# GOOD — reconnect with exponential backoff
import asyncio

async def call_tool_with_retry(session_factory, tool_name, arguments, max_retries=3):
    for attempt in range(max_retries):
        try:
            async with session_factory() as session:
                return await session.call_tool(tool_name, arguments)
        except ConnectionError as e:
            if attempt == max_retries - 1:
                raise
            wait = 2 ** attempt  # Exponential backoff: 1s, 2s, 4s
            await asyncio.sleep(wait)
```

### 11.6 Debugging Failures

**Systematic debugging checklist:**

```
MCP DEBUGGING CHECKLIST
═══════════════════════════════════════════════════
□ 1. Enable debug transport logging:
      MCP_LOG_LEVEL=debug python server.py

□ 2. Use MCP Inspector for interactive testing:
      npx @modelcontextprotocol/inspector python server.py

□ 3. Check initialization succeeds:
      Verify protocolVersion matches between client and server

□ 4. Verify tools/list returns expected tools:
      Tool names must be unique; schema must be valid JSON Schema

□ 5. Validate input schema against test arguments:
      python -c "import jsonschema; jsonschema.validate(args, schema)"

□ 6. Check stderr for server-side errors:
      MCP servers should log errors to stderr, not stdout

□ 7. Confirm isError flag handling:
      Tool errors use isError:true in result, not JSON-RPC error codes

□ 8. Test transport separately:
      echo raw JSON-RPC via stdin to verify transport works

□ 9. Check environment variables:
      Server often depends on env vars for DB URLs, API keys

□ 10. Verify timeouts:
       Long-running tools need client timeout config > tool execution time
═══════════════════════════════════════════════════
```

---

## 12. MCP vs Other Frameworks

### 12.1 MCP vs REST APIs

| Dimension | REST API | MCP |
|---|---|---|
| **Purpose** | General machine-to-machine | AI agent ↔ tool communication |
| **Discovery** | Manual / OpenAPI spec | Dynamic `tools/list` at runtime |
| **Schema** | Optional (OpenAPI) | Mandatory JSON Schema per tool |
| **Transport** | HTTP only | stdio, HTTP+SSE, WebSocket |
| **Statefulness** | Stateless by default | First-class stateful sessions |
| **Bidirectional** | No (webhooks workaround) | Yes (notifications, sampling) |
| **Context injection** | Not supported | Resources primitive |
| **Versioning** | URL or header (`v1/`, `Accept-version`) | Protocol version negotiation |
| **AI-native** | No — designed for humans | Yes — designed for LLM consumption |
| **Learning curve** | Low | Medium |

**When to use REST over MCP:**
- Public-facing APIs consumed by other services or humans
- API already exists and serves multiple consumers
- No AI agent integration needed
- Team lacks MCP expertise

**When to use MCP over REST:**
- Building tools specifically for AI agent consumption
- Need dynamic capability discovery
- Building a library of reusable tools across multiple AI clients
- Need stateful multi-step workflows with an AI

### 12.2 MCP vs GraphQL

| Dimension | GraphQL | MCP |
|---|---|---|
| **Data model** | Graph/tree-based queries | Action-oriented tool calls |
| **Flexibility** | Client specifies exact fields needed | Schema is server-defined |
| **Real-time** | Subscriptions | SSE + notifications |
| **Transport** | HTTP (POST) | stdio, HTTP+SSE, WebSocket |
| **Schema** | SDL (Schema Definition Language) | JSON Schema |
| **Use case** | Complex data fetching, front-ends | AI agent tool execution |
| **AI-native** | No | Yes |

**Key distinction:** GraphQL is about **querying data shapes**. MCP is about **invoking capabilities**. They serve different purposes and are not direct competitors — in fact, a GraphQL MCP server is a valid architecture (MCP tools that internally query a GraphQL API).

### 12.3 MCP vs Plugin-Based Systems

| Dimension | OpenAI Plugins | LangChain Tools | MCP |
|---|---|---|---|
| **Standard** | OpenAI-specific | LangChain-specific | Open standard |
| **Portability** | ChatGPT only | LangChain agents only | Any MCP client |
| **Discovery** | Plugin store | Code registration | Dynamic `tools/list` |
| **Transport** | HTTP only | In-process | stdio, HTTP, WebSocket |
| **Ecosystem** | Closed (deprecated) | Open but framework-tied | Fully open |
| **Tool reuse** | Across ChatGPT users | Within LangChain | Across any LLM/framework |
| **Status** | Deprecated (2024) | Active | Growing rapidly |

**The plugin ecosystem problem MCP solves:**
```
Pre-MCP world:
  Tool X built for LangChain → unusable in LlamaIndex without rewrites
  Tool Y built for OpenAI → unusable in Claude without rewrites
  Tool Z built for custom agent → unusable everywhere else

Post-MCP world:
  Tool X as MCP server → works with LangChain, LlamaIndex, Claude, custom agents, all at once
```

### 12.4 When NOT to Use MCP

MCP adds overhead and complexity. Avoid it when:

| Scenario | Better Alternative |
|---|---|
| Simple, one-off tool needed by a single LLM | Direct function calling in that LLM's native format |
| Tool will never be reused across frameworks | Inline tool function, no server needed |
| Ultra-low latency is required (<10ms) | In-process function call — MCP adds network/serialization overhead |
| You control both client and server code tightly | Direct library call is simpler |
| Non-AI consumers (mobile apps, other services) | REST API — more widely understood |
| Experimenting/prototyping quickly | LangChain tools or direct function calling are faster to set up |

**Rule:** Use MCP when you are building **reusable, AI-facing capabilities** that need to work across multiple clients, frameworks, or LLM providers.

---

## 13. Future of MCP

### 13.1 Standardization Trends

MCP is gaining rapid adoption across the AI ecosystem:

- **Anthropic** created and maintains the open specification
- **Microsoft** added MCP support to Azure AI Foundry and Copilot Studio
- **Google** announced MCP support in Gemini and Google Workspace AI
- **Block, Replit, Sourcegraph** are among early major adopters
- The **MCP specification** is hosted at `modelcontextprotocol.io` as a public, community-governed standard

The trajectory mirrors how the **Language Server Protocol (LSP)** became the universal standard for code intelligence in editors. MCP is poised to become the equivalent universal standard for AI-to-tool communication.

**Emerging specification areas (as of 2025–2026):**
- MCP over gRPC transport (for high-performance scenarios)
- Standardized authentication flows (OAuth 2.0 profiles for MCP)
- Tool versioning and deprecation protocols
- Multi-modal content types (images, audio in tool responses)
- Batch tool calls (invoke multiple tools in a single round trip)

### 13.2 Multi-Agent Ecosystems

MCP is increasingly used in **multi-agent systems** where agents communicate with each other through MCP:

```
┌────────────────────────────────────────────────────────────────────┐
│                    MULTI-AGENT MCP ARCHITECTURE                    │
│                                                                    │
│  User Request                                                      │
│       │                                                            │
│       ▼                                                            │
│  ┌─────────────────┐                                               │
│  │ ORCHESTRATOR    │  (Planner Agent — decomposes tasks)           │
│  │ AGENT           │                                               │
│  └────┬────────────┘                                               │
│       │ MCP calls to sub-agents                                    │
│  ┌────▼────────┐  ┌──────────────┐  ┌──────────────────────────┐  │
│  │ RESEARCH    │  │  CODING      │  │  COMMUNICATION           │  │
│  │ AGENT       │  │  AGENT       │  │  AGENT                   │  │
│  │ (MCP Server)│  │  (MCP Server)│  │  (MCP Server)            │  │
│  │             │  │              │  │                          │  │
│  │ web_search  │  │ write_code   │  │ send_email               │  │
│  │ summarize   │  │ run_tests    │  │ post_to_slack            │  │
│  │ fact_check  │  │ review_pr    │  │ schedule_meeting         │  │
│  └─────────────┘  └──────────────┘  └──────────────────────────┘  │
└────────────────────────────────────────────────────────────────────┘
```

In this pattern:
- Each specialized agent exposes itself as an **MCP server**
- The orchestrator agent treats sub-agents as **MCP tools**
- Sub-agents can themselves be MCP clients connected to tool servers
- The entire hierarchy is composed through the MCP protocol

### 13.3 Tool Marketplaces

The standardization of MCP enables a **tool marketplace ecosystem**:

```
MCP Tool Marketplace (Emerging):
├── Official Anthropic servers (filesystem, GitHub, Slack, Postgres...)
├── Community-contributed servers (Jira, Salesforce, custom DBs...)
├── Commercial servers (enterprise connectors from vendors)
└── Certified/verified servers with security audits
```

Developers will:
1. Browse a marketplace for needed capabilities
2. Add the MCP server config to their agent host
3. The agent immediately gains those capabilities — no code changes

**Package distribution:**
```bash
# Already emerging patterns:
npx @modelcontextprotocol/server-github
npx @modelcontextprotocol/server-postgres
pip install mcp-server-filesystem
docker pull mcp/server-web-search:latest
```

### 13.4 Continuous and Adaptive MCP

Future MCP servers will be **adaptive**:
- **Dynamic tool registration:** Servers can add/remove tools at runtime without restarting (via `notifications/tools/list_changed`)
- **Capability negotiation per session:** Different clients may be granted different tool subsets based on their authentication level
- **Self-describing tools with examples:** Tool schemas will include usage examples, enabling better LLM understanding with zero-shot prompting
- **Semantic tool search:** Instead of listing all tools, agents will query "find tools for sending messages" and get relevant results

---

## 14. Interview Preparation

### A. Beginner Questions

---

**Q1: What is MCP and what problem does it solve?**

**A:** Model Context Protocol is an open standard for communication between AI models (clients) and external tools/services (servers). It solves the **N×M integration problem**: before MCP, every AI framework needed custom integration code for every tool. With MCP, any compliant client works with any compliant server — reducing N×M integrations to N+M implementations. It uses JSON-RPC 2.0 for structured messaging and supports dynamic capability discovery so agents can find available tools at runtime.

---

**Q2: What are the four MCP primitives?**

**A:**
- **Tools:** Executable functions that perform actions and return results (e.g., `query_database`, `send_email`). The model can invoke these.
- **Resources:** Read-only data the server exposes for context injection (e.g., file contents, DB schema, config).
- **Prompts:** Parameterized prompt templates the server exposes for reuse across interactions.
- **Sampling:** The server requests the LLM client to generate a completion — an inverted control flow enabling server-side LLM use.

---

**Q3: What transport mechanisms does MCP support?**

**A:** Three transports:
1. **stdio:** Messages over stdin/stdout — used for local subprocess servers (Claude Desktop integrations)
2. **HTTP + SSE (Server-Sent Events):** HTTP POST for requests, SSE for streaming responses — used for remote servers
3. **WebSocket:** Full-duplex persistent connection — used for low-latency, high-frequency interactions

---

**Q4: How does MCP differ from OpenAI function calling?**

**A:**

| Aspect | Function Calling | MCP |
|---|---|---|
| Standard | OpenAI-specific | Open, any provider |
| Tool servers | No — functions defined per request | Yes — reusable server processes |
| Discovery | Static (defined in code) | Dynamic (tools/list at runtime) |
| State | Stateless | Stateful sessions |
| Transport | N/A (in-prompt) | stdio, HTTP+SSE, WebSocket |
| Portability | Tied to OpenAI | Works with any MCP client |

Function calling is a **model capability**; MCP is a **protocol**. MCP builds on function calling by adding a standard server-side architecture and dynamic discovery.

---

**Q5: What is the MCP initialization handshake?**

**A:** Three-step process:
1. Client sends `initialize` with `protocolVersion`, `clientInfo`, and `capabilities`
2. Server responds with `initialize result` including its `protocolVersion`, `serverInfo`, and `capabilities`
3. Client sends `initialized` notification — confirming both sides have agreed on capabilities and the session is active

This negotiation determines which protocol features both sides support before any tool calls happen.

---

### B. Intermediate Questions

---

**Q6: How does an agent discover and route tool calls across multiple MCP servers?**

**A:**
1. On startup, the agent host connects to all configured MCP servers
2. It calls `tools/list` on each server and aggregates the results
3. All tools are presented to the LLM in its tool catalog (with server-name prefixes to avoid collisions)
4. When the LLM outputs a tool call, the client looks up which server registered that tool name
5. The client routes `tools/call` to the correct server session
6. The result is returned to the LLM as a tool output

Key implementation: the client maintains a **tool → session mapping** built during the discovery phase.

---

**Q7: Explain the difference between a JSON-RPC error and a tool execution error in MCP.**

**A:** This is a critical distinction:
- **JSON-RPC error** (`error` field in response): Protocol-level failure — invalid method, parse error, auth failure, rate limit. The request itself could not be processed.
- **Tool execution error** (`result` with `isError: true`): The tool was invoked correctly but the execution failed (e.g., file not found, SQL syntax error, API timeout). The request was processed successfully — the tool just returned an error state.

Why it matters: Tool errors should be **returned to the LLM** as context so it can retry or adjust. JSON-RPC errors indicate a problem the LLM cannot recover from by retrying.

---

**Q8: How would you implement a stateful browser automation MCP server?**

**A:** Maintain a session map keyed by session ID:
```python
class BrowserMCPServer:
    def __init__(self):
        self.sessions = {}  # session_id → Playwright browser context

    async def navigate(self, session_id, url):
        ctx = await self._get_or_create_session(session_id)
        await ctx.page.goto(url)
        return await ctx.page.content()

    async def click(self, session_id, selector):
        ctx = self.sessions[session_id]
        await ctx.page.click(selector)

    async def _get_or_create_session(self, session_id):
        if session_id not in self.sessions:
            browser = await playwright.chromium.launch(headless=True)
            self.sessions[session_id] = await browser.new_context()
        return self.sessions[session_id]
```
The session ID is passed in tool arguments and maintained by the agent across multiple tool calls. Sessions are cleaned up after inactivity or when the agent signals completion.

---

**Q9: What are the security considerations when building a code execution MCP server?**

**A:**
1. **Sandboxing:** Run code in Docker containers with `network_mode=none`, `read_only=True`, `user=nobody`, memory and CPU limits
2. **Timeout enforcement:** Hard kill containers after N seconds (never trust `asyncio.wait_for` alone — use Docker `timeout` parameter)
3. **Language allowlist:** Only permit specific languages/runtimes
4. **Output sanitization:** Strip ANSI escape codes, limit output size
5. **No persistent state between executions:** Each call gets a fresh container
6. **Resource quotas:** Prevent fork bombs, memory exhaustion
7. **Audit logging:** Record every execution attempt including the code submitted
8. **Rate limiting:** Code execution is expensive — strict per-client limits

---

**Q10: Describe how MCP's `sampling` primitive inverts the control flow.**

**A:** Normally the flow is: LLM → MCP client → MCP server → tool execution. With `sampling`, the MCP server can call back to the LLM client and request a completion:

```
Normal: Host(LLM) → Client → Server → Tool
Sampling: Server → Client → Host(LLM) → [generates text] → Server
```

This enables servers to use the LLM as a sub-routine. Example: an MCP server that fetches a web page and then asks the LLM to summarize it before returning the summary. The tool itself becomes LLM-powered. This is powerful for server-side prompt chains, dynamic content generation within tool responses, and AI-assisted data transformation.

---

### C. Advanced / Tricky Questions

---

**Q11: How would you handle tool name collisions when connecting to multiple MCP servers that expose tools with the same name?**

**A:** Client-side namespacing is the standard solution:
```python
# Prefix all tool names with the server name
def namespace_tool(server_name: str, tool) -> dict:
    return {
        "name": f"{server_name}__{tool.name}",
        "description": f"[{server_name}] {tool.description}",
        "inputSchema": tool.inputSchema
    }

# Build reverse mapping for routing
tool_to_session = {
    f"{server_name}__{t.name}": session
    for server_name, session, tools in server_sessions
    for t in tools
}
```

Additional strategies:
- Enforce unique tool names at server registry level (fail fast at startup if collision detected)
- Allow explicit server targeting in tool arguments: `{"server": "filesystem", "tool": "read_file", ...}`
- Use semantic tool selection where the LLM chooses the best match when multiple similar tools exist

---

**Q12: How does MCP handle protocol version incompatibilities between client and server?**

**A:** During initialization, both parties exchange `protocolVersion`. The negotiation works as follows:
- If the server supports the client's requested version → proceed
- If the server supports a lower version → it responds with the highest version it supports; the client must decide whether to proceed or abort
- If versions are incompatible → the server returns a JSON-RPC error and the session fails

Best practice: MCP clients should support a range of protocol versions and gracefully degrade features unavailable in older server versions. The `capabilities` object further refines what features are mutually available within a given version.

---

**Q13: Explain how you would design an MCP server for a multi-tenant SaaS product where each tenant has isolated data.**

**A:**

**Data isolation strategy:**
```python
@app.call_tool()
async def call_tool(name: str, arguments: dict) -> list[types.TextContent]:
    # Extract tenant from authenticated session token
    tenant_id = get_tenant_from_context(app.request_context)

    if name == "query_data":
        # All queries are tenant-scoped at the DB level
        async with TenantDB(tenant_id) as db:
            result = await db.query(arguments["sql"])
        return [types.TextContent(type="text", text=result)]
```

**Key design decisions:**
- Each tenant gets a separate DB schema or database instance (not just a WHERE clause)
- Auth tokens carry tenant claims — validated on every request, never trusted from client arguments
- Vector stores (if used for RAG) are namespace-scoped per tenant with RBAC enforcement
- Audit logs include tenant_id on every entry
- Resource limits (rate limiting, storage quotas) enforced per tenant
- No cross-tenant data leakage even if an attacker controls the LLM being used

---

**Q14: A client reports that tool calls randomly fail with timeout errors in production. How would you diagnose and fix this?**

**A:**

**Diagnosis steps:**
1. Check p99 latency metrics — are timeouts happening for specific tools or all?
2. Correlate with time-of-day — is it load-related?
3. Check database connection pool exhaustion (connections waiting → long queues → timeouts)
4. Check if timeouts occur on first call after idle period (cold start issue — DB reconnect latency)
5. Check if the LLM client's timeout is shorter than the actual tool execution time

**Common root causes and fixes:**
- **Connection pool exhausted:** Increase pool size, add wait timeout, log pool status
- **Cold start:** Implement a `ping` tool and warm up connections on server start
- **Slow queries:** Add query timeouts, optimize indexes, add EXPLAIN ANALYZE
- **Client timeout misconfiguration:** Align client `timeout_seconds` with realistic tool execution time
- **SSE keepalive missing:** For HTTP transport, add SSE heartbeat every 15s to prevent proxy timeouts

---

### D. System Design Questions

---

**Q15: Design an MCP system that can serve 1 million users.**

**A:**

**Architecture:**

```
CDN / Edge Layer
       │
   Load Balancer (L7, SSL termination)
       │
   ┌───┴──────────────────────────────────┐
   │        MCP Gateway Cluster           │
   │  (Auth, rate limiting, routing)      │
   │  10-20 stateless instances           │
   └───┬──────────────────────────────────┘
       │ Routes to appropriate shard
   ┌───┴────────────────────────────┐
   │    MCP Server Clusters          │
   │                                │
   │  Stateless Servers (100s):      │
   │  - Calculator, search, etc.    │
   │  (horizontal scale freely)     │
   │                                │
   │  Stateful Servers (sharded):    │
   │  - DB sessions, browser        │
   │  (sticky sessions per user)    │
   └───────────────────────────────-┘
       │
   ┌───┴──────────────────────────────────┐
   │     Shared Infrastructure            │
   │  Redis Cluster (sessions, cache)     │
   │  PostgreSQL (primary + replicas)     │
   │  Vector DB (Milvus/Weaviate cluster) │
   └──────────────────────────────────────┘
```

**Key design decisions:**
- **Stateless-first:** Design 90% of tools to be stateless → trivially horizontally scalable
- **Gateway layer:** Authentication, rate limiting, and routing at the gateway before reaching tool servers
- **Redis for shared state:** Session state, caches, distributed locks — never in-process memory
- **Tiered rate limiting:** Global (per IP), per-user, per-tenant, per-tool
- **Async everywhere:** All I/O non-blocking; no synchronous DB calls in request handlers
- **Observability:** Distributed tracing (Jaeger/Tempo) + metrics (Prometheus) + structured logging
- **Circuit breakers:** If a downstream service (DB, external API) is degraded, fail fast rather than queuing requests

**Capacity estimation:**
- 1M users × 10 tool calls/day = 10M calls/day = ~115 calls/second average
- With 10× peak factor: design for ~1,150 calls/second peak
- At 50ms average latency per call: ~58 concurrent connections needed
- With safety margin: 20 server instances × 100 concurrent connections each = 2,000 concurrent cap

---

**Q16: Design an MCP-based enterprise automation platform for a Fortune 500 company.**

**A:**

**Requirements:**
- 50,000 employees using AI copilots across departments
- 100+ enterprise systems (SAP, Salesforce, ServiceNow, Workday, Jira)
- Strict data governance and compliance (SOC2, ISO27001)
- 99.9% availability SLA

**Architecture:**

```
Employee Copilot (Chat Interface)
          │
    AI Agent Host (per department)
          │
    Internal MCP Gateway
    ├── Auth: SSO/SAML + JWT
    ├── RBAC: Department-based tool permissions
    ├── Rate limits: Per-user, per-department
    ├── Audit: Every call logged to SIEM
    └── DLP: Output scanning for PII
          │
    MCP Server Registry (100+ servers)
    ├── SAP ERP MCP Server
    ├── Salesforce CRM MCP Server
    ├── ServiceNow ITSM MCP Server
    ├── Workday HR MCP Server
    ├── Jira Project MCP Server
    ├── Internal Data Warehouse MCP Server
    └── [More specialized servers...]
```

**Governance framework:**
- Each tool classified by data sensitivity (public / internal / confidential / restricted)
- Tool approval workflow: security team reviews new tools before production deployment
- Per-employee tool access based on job role from Active Directory
- All tool arguments and responses scanned for PII before logging (auto-redaction)
- Monthly access reviews — unused tool permissions automatically revoked

---

### E. Scenario-Based Questions

---

**Q17: An MCP server's tool is producing hallucinated results — it returns success but the data is wrong. How do you detect and prevent this?**

**A:** This is a **data validation problem**, not a protocol problem. Solutions:
1. **Schema validation on output:** If tool output has a known structure (JSON), validate it against an expected schema before returning
2. **Consistency checks:** For deterministic operations (e.g., math), verify result correctness
3. **Checksums/hashes:** For file operations, return checksums the client can verify
4. **Idempotency testing:** Call the tool twice with the same input; results must match
5. **LLM-based verification:** Use the `sampling` primitive to have the LLM verify its own tool output before returning (self-consistency check)
6. **Monitoring:** Log and sample tool outputs; use automated evaluation to detect quality degradation

---

**Q18: You need to give an AI agent access to a production database but must prevent data exfiltration. How do you design the MCP server?**

**A:**

**Defense-in-depth approach:**
```python
class SafeDatabaseMCPServer:
    ALLOWED_TABLES = {"products", "orders", "categories"}  # Allowlist
    MAX_ROWS = 100  # Never return more than 100 rows
    FORBIDDEN_COLUMNS = {"user_passwords", "credit_card_numbers", "ssn"}

    async def query(self, sql: str, user_context: UserContext) -> str:
        # 1. Only SELECT
        if not sql.strip().upper().startswith("SELECT"):
            raise MCPError(-32602, "Only SELECT queries permitted")

        # 2. Parse and validate table names
        tables_in_query = extract_table_names(sql)
        if not tables_in_query.issubset(self.ALLOWED_TABLES):
            raise MCPError(-32602, f"Unauthorized tables: {tables_in_query - self.ALLOWED_TABLES}")

        # 3. Forbid sensitive columns
        if any(col in sql.lower() for col in self.FORBIDDEN_COLUMNS):
            raise MCPError(-32602, "Query references forbidden columns")

        # 4. Enforce row limit
        sql = f"SELECT * FROM ({sql}) AS q LIMIT {self.MAX_ROWS}"

        # 5. Use read-only DB user at connection level
        async with readonly_db_pool.acquire() as conn:
            rows = await conn.fetch(sql)

        # 6. PII scan on output before returning
        result = format_results(rows)
        result = pii_scanner.redact(result)  # Redact any leaked PII
        return result
```

---

**Q19: How would you implement human-in-the-loop approval for high-risk MCP tool calls?**

**A:**

Define a risk classification for tools and pause execution for high-risk calls pending human approval:

```python
HIGH_RISK_TOOLS = {
    "deploy_to_production",
    "delete_database",
    "send_mass_email",
    "transfer_funds",
    "modify_user_permissions"
}

@app.call_tool()
async def call_tool(name: str, arguments: dict) -> list[types.TextContent]:
    if name in HIGH_RISK_TOOLS:
        # Create approval request
        approval_id = await approval_service.create_request(
            tool=name,
            arguments=arguments,
            requested_by=get_current_user(),
            expires_at=datetime.utcnow() + timedelta(minutes=30)
        )

        # Return pending status — agent must poll or wait
        return [types.TextContent(type="text",
            text=f"HIGH-RISK ACTION REQUIRES APPROVAL.\n"
                 f"Approval ID: {approval_id}\n"
                 f"A human must approve this action at: https://approvals.company.com/{approval_id}\n"
                 f"Use check_approval_status(approval_id='{approval_id}') to check if approved."
        )]

    return await execute_tool(name, arguments)

# Companion tool for polling approval status
async def check_approval_status(arguments: dict) -> list[types.TextContent]:
    status = await approval_service.get_status(arguments["approval_id"])
    if status == "approved":
        # Execute the originally requested action
        original = await approval_service.get_original_request(arguments["approval_id"])
        return await execute_tool(original.tool, original.arguments)
    elif status == "rejected":
        return [types.TextContent(type="text", text="Action was rejected by the approver.")]
    else:
        return [types.TextContent(type="text", text="Approval still pending. Please wait.")]
```

---

**Q20: How does MCP enable AI agent interoperability in a multi-agent system?**

**A:** In multi-agent systems, each specialized agent exposes an **MCP server interface**, and the orchestrating agent treats sub-agents as tools via the MCP client:

```
Orchestrator Agent → MCP Client
  calls tools/list on:
    - ResearchAgent MCP Server (tools: search, summarize, fact_check)
    - CodingAgent MCP Server (tools: write_code, run_tests, review)
    - WriterAgent MCP Server (tools: draft_doc, format, proofread)

Orchestrator calls: ResearchAgent.search("topic")
                    CodingAgent.write_code(spec)
                    WriterAgent.draft_doc(research + code)
```

Benefits of this pattern:
- **Standardized interface:** Every agent is interchangeable if it speaks MCP
- **Independent scaling:** Each agent can run on different hardware (GPU agents vs CPU agents)
- **Composition:** New capabilities emerge from combining agents without modifying existing ones
- **Observability:** All inter-agent communication is logged as MCP tool calls
- **Authorization:** Orchestrator's permissions are enforced; it cannot grant sub-agents more access than it has

---

## 15. Appendix

### A. Complete MCP Server — Python Reference Implementation

```python
#!/usr/bin/env python3
"""
Complete MCP Server Reference Implementation
Demonstrates: tools, resources, prompts, error handling, logging
"""
import asyncio
import json
import logging
import os
from datetime import datetime

from mcp.server import Server
from mcp.server.stdio import stdio_server
from mcp import types

logging.basicConfig(level=logging.INFO, stream=__import__("sys").stderr)
log = logging.getLogger("mcp-reference-server")

app = Server("reference-mcp-server")

# ── TOOLS ─────────────────────────────────────────────────────────────────────

@app.list_tools()
async def list_tools() -> list[types.Tool]:
    return [
        types.Tool(
            name="echo",
            description="Echo back the provided message. Useful for testing.",
            inputSchema={
                "type": "object",
                "properties": {
                    "message": {"type": "string", "description": "Message to echo"}
                },
                "required": ["message"]
            }
        ),
        types.Tool(
            name="get_timestamp",
            description="Get the current UTC timestamp in ISO 8601 format.",
            inputSchema={"type": "object", "properties": {}, "required": []}
        ),
        types.Tool(
            name="json_format",
            description="Parse and pretty-print a JSON string.",
            inputSchema={
                "type": "object",
                "properties": {
                    "json_string": {"type": "string", "description": "JSON string to format"},
                    "indent": {"type": "integer", "default": 2, "minimum": 0, "maximum": 8}
                },
                "required": ["json_string"]
            }
        ),
    ]

@app.call_tool()
async def call_tool(name: str, arguments: dict) -> list[types.TextContent]:
    log.info(f"Tool called: {name} | Args: {arguments}")

    if name == "echo":
        return [types.TextContent(type="text", text=arguments["message"])]

    elif name == "get_timestamp":
        return [types.TextContent(type="text", text=datetime.utcnow().isoformat() + "Z")]

    elif name == "json_format":
        try:
            parsed = json.loads(arguments["json_string"])
            formatted = json.dumps(parsed, indent=arguments.get("indent", 2))
            return [types.TextContent(type="text", text=formatted)]
        except json.JSONDecodeError as e:
            return [types.TextContent(type="text", text=f"Invalid JSON: {e}")]

    raise ValueError(f"Unknown tool: {name}")

# ── RESOURCES ─────────────────────────────────────────────────────────────────

@app.list_resources()
async def list_resources() -> list[types.Resource]:
    return [
        types.Resource(
            uri="config://server/info",
            name="Server Information",
            description="Basic information about this MCP server",
            mimeType="application/json"
        )
    ]

@app.read_resource()
async def read_resource(uri: str) -> str:
    if uri == "config://server/info":
        return json.dumps({
            "name": "reference-mcp-server",
            "version": "1.0.0",
            "tools": ["echo", "get_timestamp", "json_format"],
            "uptime_seconds": (datetime.utcnow() - SERVER_START_TIME).seconds
        }, indent=2)
    raise ValueError(f"Unknown resource: {uri}")

# ── PROMPTS ───────────────────────────────────────────────────────────────────

@app.list_prompts()
async def list_prompts() -> list[types.Prompt]:
    return [
        types.Prompt(
            name="debug_tool_call",
            description="Template for debugging a failed tool call",
            arguments=[
                types.PromptArgument(name="tool_name", description="Name of the tool that failed", required=True),
                types.PromptArgument(name="error_message", description="Error message received", required=True),
            ]
        )
    ]

@app.get_prompt()
async def get_prompt(name: str, arguments: dict) -> types.GetPromptResult:
    if name == "debug_tool_call":
        return types.GetPromptResult(
            description="Debug prompt for tool call failure",
            messages=[
                types.PromptMessage(
                    role="user",
                    content=types.TextContent(
                        type="text",
                        text=f"The MCP tool '{arguments['tool_name']}' failed with: {arguments['error_message']}\n"
                             f"Please diagnose the issue and suggest a fix."
                    )
                )
            ]
        )
    raise ValueError(f"Unknown prompt: {name}")

# ── MAIN ──────────────────────────────────────────────────────────────────────

SERVER_START_TIME = datetime.utcnow()

async def main():
    log.info("Starting reference MCP server")
    async with stdio_server() as (read_stream, write_stream):
        await app.run(
            read_stream,
            write_stream,
            app.create_initialization_options()
        )

if __name__ == "__main__":
    asyncio.run(main())
```

### B. Complete MCP Server — Node.js/TypeScript Reference Implementation

```typescript
#!/usr/bin/env node
/**
 * Complete MCP Server Reference — Node.js/TypeScript
 * Demonstrates: tools, resources, error handling
 */
import { Server } from "@modelcontextprotocol/sdk/server/index.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";
import {
  CallToolRequestSchema,
  GetPromptRequestSchema,
  ListPromptsRequestSchema,
  ListResourcesRequestSchema,
  ListToolsRequestSchema,
  ReadResourceRequestSchema,
} from "@modelcontextprotocol/sdk/types.js";

const server = new Server(
  { name: "reference-mcp-server-ts", version: "1.0.0" },
  {
    capabilities: {
      tools: {},
      resources: {},
      prompts: {},
    },
  }
);

const SERVER_START = Date.now();

// ── TOOLS ────────────────────────────────────────────────────────────────────

server.setRequestHandler(ListToolsRequestSchema, async () => ({
  tools: [
    {
      name: "echo",
      description: "Echo back the provided message",
      inputSchema: {
        type: "object",
        properties: {
          message: { type: "string", description: "Message to echo" },
        },
        required: ["message"],
      },
    },
    {
      name: "get_timestamp",
      description: "Get the current UTC timestamp",
      inputSchema: { type: "object", properties: {}, required: [] },
    },
    {
      name: "random_number",
      description: "Generate a random integer between min and max (inclusive)",
      inputSchema: {
        type: "object",
        properties: {
          min: { type: "integer", default: 0 },
          max: { type: "integer", default: 100 },
        },
        required: [],
      },
    },
  ],
}));

server.setRequestHandler(CallToolRequestSchema, async (request) => {
  const { name, arguments: args = {} } = request.params;

  switch (name) {
    case "echo":
      return { content: [{ type: "text", text: String(args.message) }] };

    case "get_timestamp":
      return { content: [{ type: "text", text: new Date().toISOString() }] };

    case "random_number": {
      const min = Number(args.min ?? 0);
      const max = Number(args.max ?? 100);
      const result = Math.floor(Math.random() * (max - min + 1)) + min;
      return { content: [{ type: "text", text: String(result) }] };
    }

    default:
      throw new Error(`Unknown tool: ${name}`);
  }
});

// ── RESOURCES ─────────────────────────────────────────────────────────────────

server.setRequestHandler(ListResourcesRequestSchema, async () => ({
  resources: [
    {
      uri: "config://server/info",
      name: "Server Information",
      mimeType: "application/json",
    },
  ],
}));

server.setRequestHandler(ReadResourceRequestSchema, async (request) => {
  if (request.params.uri === "config://server/info") {
    return {
      contents: [
        {
          uri: "config://server/info",
          mimeType: "application/json",
          text: JSON.stringify({
            name: "reference-mcp-server-ts",
            version: "1.0.0",
            uptime_ms: Date.now() - SERVER_START,
          }, null, 2),
        },
      ],
    };
  }
  throw new Error(`Unknown resource: ${request.params.uri}`);
});

// ── PROMPTS ───────────────────────────────────────────────────────────────────

server.setRequestHandler(ListPromptsRequestSchema, async () => ({
  prompts: [
    {
      name: "explain_tool",
      description: "Generate an explanation of what a tool does",
      arguments: [
        { name: "tool_name", description: "Name of the tool", required: true },
      ],
    },
  ],
}));

server.setRequestHandler(GetPromptRequestSchema, async (request) => {
  if (request.params.name === "explain_tool") {
    const toolName = request.params.arguments?.tool_name ?? "unknown";
    return {
      messages: [
        {
          role: "user",
          content: {
            type: "text",
            text: `Explain what the '${toolName}' tool does and provide an example of when to use it.`,
          },
        },
      ],
    };
  }
  throw new Error(`Unknown prompt: ${request.params.name}`);
});

// ── MAIN ──────────────────────────────────────────────────────────────────────

const transport = new StdioServerTransport();
await server.connect(transport);
console.error("Reference MCP server (TypeScript) running on stdio");
```

### C. JSON Schema Reference for MCP Tools

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "MCP Tool Input Schema Examples",
  "definitions": {

    "string_only": {
      "type": "object",
      "properties": {
        "text": { "type": "string" }
      },
      "required": ["text"]
    },

    "with_enum": {
      "type": "object",
      "properties": {
        "format": {
          "type": "string",
          "enum": ["json", "csv", "markdown", "plain"],
          "default": "plain"
        }
      },
      "required": []
    },

    "with_array": {
      "type": "object",
      "properties": {
        "tags": {
          "type": "array",
          "items": { "type": "string" },
          "minItems": 1,
          "maxItems": 10
        }
      },
      "required": ["tags"]
    },

    "with_nested_object": {
      "type": "object",
      "properties": {
        "pagination": {
          "type": "object",
          "properties": {
            "page":     { "type": "integer", "minimum": 1, "default": 1 },
            "per_page": { "type": "integer", "minimum": 1, "maximum": 100, "default": 20 }
          },
          "required": []
        }
      }
    },

    "with_date_and_constraints": {
      "type": "object",
      "properties": {
        "start_date": { "type": "string", "format": "date" },
        "end_date":   { "type": "string", "format": "date" },
        "limit":      { "type": "integer", "minimum": 1, "maximum": 1000, "default": 50 },
        "query":      { "type": "string", "minLength": 3, "maxLength": 500 }
      },
      "required": ["start_date", "end_date", "query"]
    }
  }
}
```

### D. Debugging Checklist

```
MCP SERVER DEBUGGING CHECKLIST
═══════════════════════════════════════════════════════════════════════

SETUP & CONFIGURATION
□ Correct Python/Node version installed (Python ≥ 3.10, Node ≥ 18)
□ MCP SDK installed (pip install mcp / npm install @modelcontextprotocol/sdk)
□ All environment variables set (DB URLs, API keys, etc.)
□ Server script is executable and path is correct in client config

PROTOCOL & TRANSPORT
□ Server starts without errors (check stderr)
□ Initialization succeeds (protocolVersion negotiated)
□ tools/list returns expected tools (use MCP Inspector to verify)
□ No tool name collisions within the server
□ JSON Schema for each tool is valid (run jsonschema.check_schema())

TOOL EXECUTION
□ Tool handler is registered for every tool in tools/list
□ Input validation happens before business logic
□ isError flag set correctly (true for tool failures, not protocol errors)
□ Async handlers use `async def` and `await` correctly
□ No synchronous blocking calls inside async handlers (use asyncio.to_thread for blocking I/O)

SECURITY
□ Path traversal prevention for file tools
□ SQL injection prevention for DB tools
□ Read-only DB user for query tools
□ Rate limiting active
□ Auth enforced on all tool calls
□ Sensitive fields not logged (passwords, tokens, API keys)

PERFORMANCE
□ Database connection pooling configured
□ Timeouts configured per tool
□ Caching implemented for expensive repeated calls
□ No memory leaks in long-running stateful servers (session cleanup)

PRODUCTION READINESS
□ Structured logging to stderr (not stdout — stdout is the MCP transport)
□ Health check tool or endpoint implemented
□ Metrics instrumentation (Prometheus / OpenTelemetry)
□ Graceful shutdown handler
□ Retry logic with backoff on client side
□ Deployment tested end-to-end with MCP Inspector before going live

═══════════════════════════════════════════════════════════════════════
```

### E. Claude Desktop Configuration Reference

```json
{
  "mcpServers": {
    "filesystem": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-filesystem", "/Users/username/Documents"],
      "env": {}
    },
    "github": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-github"],
      "env": {
        "GITHUB_PERSONAL_ACCESS_TOKEN": "ghp_xxxxxxxxxxxxxxxxxxxx"
      }
    },
    "postgres": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-postgres"],
      "env": {
        "POSTGRES_CONNECTION_STRING": "postgresql://user:password@localhost/mydb"
      }
    },
    "custom_python_server": {
      "command": "python",
      "args": ["/path/to/my_mcp_server.py"],
      "env": {
        "API_KEY": "your_api_key_here",
        "LOG_LEVEL": "INFO"
      }
    },
    "remote_http_server": {
      "url": "https://my-mcp-server.company.com/sse",
      "headers": {
        "Authorization": "Bearer your_token_here"
      }
    }
  }
}
```

### F. Reference Resources

| Resource | URL | Description |
|---|---|---|
| **MCP Specification** | `modelcontextprotocol.io` | Official protocol specification |
| **Python SDK** | `github.com/modelcontextprotocol/python-sdk` | Official Python implementation |
| **TypeScript SDK** | `github.com/modelcontextprotocol/typescript-sdk` | Official TypeScript implementation |
| **MCP Inspector** | `github.com/modelcontextprotocol/inspector` | Interactive testing and debugging tool |
| **Official Servers** | `github.com/modelcontextprotocol/servers` | Reference server implementations |
| **Awesome MCP** | `github.com/punkpeye/awesome-mcp-servers` | Community curated server list |
| **LangChain MCP Adapters** | `github.com/langchain-ai/langchain-mcp-adapters` | LangChain integration library |

---

## Quick Reference Card

```
MCP ESSENTIALS
═══════════════════════════════════════════════════════════════

PROTOCOL STACK
  JSON-RPC 2.0 messages
  over stdio │ HTTP+SSE │ WebSocket

FOUR PRIMITIVES
  Tools     → Things the LLM can DO (actions)
  Resources → Things the LLM can READ (context)
  Prompts   → Reusable prompt templates
  Sampling  → Server requests LLM completion

SESSION LIFECYCLE
  initialize → initialized → [operations] → close

KEY METHOD NAMES
  tools/list          resources/list        prompts/list
  tools/call          resources/read        prompts/get
  sampling/createMessage
  notifications/tools/list_changed

ERROR CODES
  -32700 Parse error     -32601 Method not found
  -32600 Invalid request -32602 Invalid params
  -32002 Unauthorized    -32003 Rate limited

TOOL CALL RESULT
  { content: [{type: "text", text: "..."}], isError: false }
  isError: true  → tool failed (return to LLM for retry)
  JSON-RPC error → protocol failed (client must handle)

SECURITY CHECKLIST
  ✓ Authenticate every call
  ✓ RBAC per tool
  ✓ Rate limit per client
  ✓ Validate all inputs (JSON Schema)
  ✓ Sanitize paths, SQL, shell commands
  ✓ Sandbox code execution
  ✓ Audit log everything

PERFORMANCE
  ✓ Stateless servers → free horizontal scale
  ✓ Redis for shared state
  ✓ Connection pooling for DB/HTTP
  ✓ Async throughout
  ✓ Semantic caching for idempotent tools
  ✓ Parallel execution for independent tools

TOOL DESIGN RULES
  ✓ One tool = one coherent action
  ✓ Rich descriptions (LLM reads these)
  ✓ Strict schemas with enums/constraints
  ✓ Explicit error messages (use isError)
  ✓ Idempotent where possible
  ✗ No business logic orchestration in MCP
  ✗ No cross-cutting concerns in tool logic
  ✗ No >50 tools on one server (LLM overwhelm)

═══════════════════════════════════════════════════════════════
```

---

*End of Document — Model Context Protocol (MCP) Servers in AI Agents*
