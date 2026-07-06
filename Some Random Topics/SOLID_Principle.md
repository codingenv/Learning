# SOLID Principles (with Examples + Pictorial Representations)

## Table of Contents

1. [Overview](#overview)
2. [S — Single Responsibility Principle (SRP)](#s--single-responsibility-principle-srp)
   - [Definition](#definition)
   - [Why it matters](#why-it-matters)
   - [Pictorial representation](#pictorial-representation)
   - [Code example (Bad vs Good)](#code-example-bad-vs-good)
   - [Interview notes](#interview-notes)
3. [O — Open/Closed Principle (OCP)](#o--openclosed-principle-ocp)
   - [Definition](#definition-1)
   - [Why it matters](#why-it-matters-1)
   - [Pictorial representation](#pictorial-representation-1)
   - [Code example (Bad vs Good)](#code-example-bad-vs-good-1)
   - [Interview notes](#interview-notes-1)
4. [L — Liskov Substitution Principle (LSP)](#l--liskov-substitution-principle-lsp)
   - [Definition](#definition-2)
   - [Why it matters](#why-it-matters-2)
   - [Pictorial representation](#pictorial-representation-2)
   - [Code example (Bad vs Good)](#code-example-bad-vs-good-2)
   - [Interview notes](#interview-notes-2)
5. [I — Interface Segregation Principle (ISP)](#i--interface-segregation-principle-isp)
   - [Definition](#definition-3)
   - [Why it matters](#why-it-matters-3)
   - [Pictorial representation](#pictorial-representation-3)
   - [Code example (Bad vs Good)](#code-example-bad-vs-good-3)
   - [Interview notes](#interview-notes-3)
6. [D — Dependency Inversion Principle (DIP)](#d--dependency-inversion-principle-dip)
   - [Definition](#definition-4)
   - [Why it matters](#why-it-matters-4)
   - [Pictorial representation](#pictorial-representation-4)
   - [Code example (Bad vs Good)](#code-example-bad-vs-good-4)
   - [Interview notes](#interview-notes-4)
7. [How SOLID works together (a practical mental model)](#how-solid-works-together-a-practical-mental-model)
8. [Interview Questions (SOLID)](#interview-questions-solid)

---

## Overview

**SOLID** is a set of 5 object-oriented design principles that help you build systems that are:

- easier to **change** without breaking other parts,
- easier to **test**,
- easier to **extend**, and
- easier to **maintain** over time.

The 5 principles are:

- **S** — Single Responsibility Principle (SRP)
- **O** — Open/Closed Principle (OCP)
- **L** — Liskov Substitution Principle (LSP)
- **I** — Interface Segregation Principle (ISP)
- **D** — Dependency Inversion Principle (DIP)

> Important: SOLID is not about “using more interfaces” or “using inheritance everywhere”. It’s about controlling coupling and making change safer.

---

## S — Single Responsibility Principle (SRP)

### Definition
A class/module should have **one reason to change**.

That means a class should focus on **one responsibility** (one business capability). If it changes for unrelated reasons (UI + DB + logging + formatting), it violates SRP.

### Why it matters
- Changes become safer (less blast radius).
- Code becomes easier to understand.
- Testing becomes simpler.
- Teams can work independently on separate responsibilities.

### Pictorial representation

```
Before (SRP violated):

+---------------------------+
| OrderService              |
| - validate order          |
| - calculate totals        |
| - save to DB              |
| - send email              |
| - generate invoice PDF    |
+---------------------------+
         ^  ^  ^  ^
         |  |  |  |
   many reasons to change

After (SRP applied):

+------------------+   +------------------+   +------------------+
| OrderValidator   |   | OrderRepository  |   | EmailNotifier    |
+------------------+   +------------------+   +------------------+
         \                 |                      /
          \                |                     /
           \               v                    /
            +----------------------------------+
            | OrderService (orchestrates only) |
            +----------------------------------+
```

### Code example (Bad vs Good)

#### Bad (multiple responsibilities)
```java
class ReportService {
    public String buildReport(List<Sale> sales) {
        // 1) Business logic (aggregation)
        double total = sales.stream().mapToDouble(Sale::amount).sum();

        // 2) Formatting logic
        String report = "TOTAL=" + total;

        // 3) Persistence logic
        Database.save("report", report);

        // 4) Side effect (email)
        Email.send("finance@company.com", report);

        return report;
    }
}
```

#### Good (split responsibilities)
```java
class ReportCalculator {
    public double totalSales(List<Sale> sales) {
        return sales.stream().mapToDouble(Sale::amount).sum();
    }
}

class ReportFormatter {
    public String format(double total) {
        return "TOTAL=" + total;
    }
}

class ReportRepository {
    public void save(String report) {
        Database.save("report", report);
    }
}

class ReportNotifier {
    public void notifyFinance(String report) {
        Email.send("finance@company.com", report);
    }
}

class ReportService {
    private final ReportCalculator calculator = new ReportCalculator();
    private final ReportFormatter formatter = new ReportFormatter();
    private final ReportRepository repository = new ReportRepository();
    private final ReportNotifier notifier = new ReportNotifier();

    public String buildReport(List<Sale> sales) {
        double total = calculator.totalSales(sales);
        String report = formatter.format(total);
        repository.save(report);
        notifier.notifyFinance(report);
        return report;
    }
}
```

### Interview notes
- SRP is about **reasons to change**, not “one method per class”.
- A common SRP smell: a class that imports many unrelated packages (db + ui + http + formatting).

---

## O — Open/Closed Principle (OCP)

### Definition
Software entities (classes, modules, functions) should be **open for extension** but **closed for modification**.

Meaning: when requirements change, you should be able to add behavior mainly by **adding new code**, not by editing stable code that risks regression.

### Why it matters
- Reduces risk of breaking existing behavior.
- Makes features easier to add.
- Enables plug-in architectures.

### Pictorial representation

```
Before (OCP violated):

+-------------------------------+
| DiscountService               |
| if (type == SEASONAL) ...     |
| else if (type == VIP) ...     |
| else if (type == NEW_USER)... |
+-------------------------------+
          ^
          |
   Must modify this file
   for every new discount

After (OCP applied):

+-------------------+    implements    +-------------------+
| DiscountPolicy    |<-----------------| SeasonalDiscount   |
| + apply(order)    |<-----------------| VipDiscount        |
+-------------------+<-----------------| NewUserDiscount    |
           ^
           |
+-------------------+
| DiscountService   |
| uses list<Policy> |
+-------------------+

Add new discount by adding a new class.
```

### Code example (Bad vs Good)

#### Bad (modifying core logic for every new type)
```java
class ShippingCost {
    public double cost(String shippingType, double weightKg) {
        if ("STANDARD".equals(shippingType)) return 5 + weightKg * 0.5;
        if ("EXPRESS".equals(shippingType)) return 15 + weightKg * 0.8;
        if ("INTERNATIONAL".equals(shippingType)) return 25 + weightKg * 1.2;
        throw new IllegalArgumentException("Unknown type");
    }
}
```

#### Good (extend by adding strategies)
```java
interface ShippingStrategy {
    double cost(double weightKg);
}

class StandardShipping implements ShippingStrategy {
    public double cost(double weightKg) { return 5 + weightKg * 0.5; }
}

class ExpressShipping implements ShippingStrategy {
    public double cost(double weightKg) { return 15 + weightKg * 0.8; }
}

class ShippingCost {
    private final Map<String, ShippingStrategy> strategies;

    ShippingCost(Map<String, ShippingStrategy> strategies) {
        this.strategies = strategies;
    }

    public double cost(String type, double weightKg) {
        ShippingStrategy s = strategies.get(type);
        if (s == null) throw new IllegalArgumentException("Unknown type");
        return s.cost(weightKg);
    }
}

// Usage:
Map<String, ShippingStrategy> map = Map.of(
    "STANDARD", new StandardShipping(),
    "EXPRESS", new ExpressShipping()
);
var calc = new ShippingCost(map);
```

### Interview notes
- OCP is commonly achieved through **polymorphism**, **composition**, and **dependency injection**.
- Overdoing OCP can lead to too many tiny classes; apply it to areas that change often.

---

## L — Liskov Substitution Principle (LSP)

### Definition
If **S is a subtype of T**, then objects of type T should be replaceable with objects of type S **without breaking correctness**.

In practical terms: subclasses should honor the **contract** of the base type.

### Why it matters
- Prevents surprising runtime behavior.
- Keeps inheritance hierarchies safe.
- Reduces need for `instanceof` checks and special-case handling.

### Pictorial representation

```
If code expects a Bird that can fly:

Client -> Bird.fly()

Then substituting Penguin (can't fly) breaks the contract.

Fix: model abilities separately.

+------------+       +----------------+
| Bird       |       | Flyable        |
| eat()      |       | fly()          |
+------------+       +----------------+
      ^                      ^
      |                      |
+-----------+         +---------------+
| Penguin   |         | Sparrow       |
| eat()     |         | eat(), fly()  |
+-----------+         +---------------+
```

### Code example (Bad vs Good)

#### Bad (subclass breaks expectations)
```java
class Rectangle {
    protected int width, height;
    public void setWidth(int w) { width = w; }
    public void setHeight(int h) { height = h; }
    public int area() { return width * height; }
}

class Square extends Rectangle {
    @Override
    public void setWidth(int w) {
        width = w;
        height = w; // forces square
    }

    @Override
    public void setHeight(int h) {
        width = h;
        height = h; // forces square
    }
}

// Client expects independent width/height:
static void demo(Rectangle r) {
    r.setWidth(5);
    r.setHeight(10);
    // Expect 50
    System.out.println(r.area());
}
// With Square, prints 100 -> surprise => LSP violation
```

#### Good (model with interfaces / composition)
```java
interface Shape {
    int area();
}

final class Rectangle implements Shape {
    private final int width, height;
    Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }
    public int area() { return width * height; }
}

final class Square implements Shape {
    private final int side;
    Square(int side) { this.side = side; }
    public int area() { return side * side; }
}
```

### Interview notes
- LSP is about **behavioral substitutability**, not “inherits compiles”.
- Smells: overridden methods that throw `UnsupportedOperationException`, or require extra preconditions.

---

## I — Interface Segregation Principle (ISP)

### Definition
Clients should not be forced to depend on methods they do not use.

Prefer **small, focused interfaces** over large “fat” interfaces.

### Why it matters
- Reduces unnecessary coupling.
- Makes implementations simpler.
- Prevents “dummy” methods or `UnsupportedOperationException`.

### Pictorial representation

```
Before (fat interface):

+---------------------+
| IMachine            |
| print()             |
| scan()              |
| fax()               |
+---------------------+
         ^
         |
  +-------------+
  | SimplePrinter |  (forced to implement scan/fax)
  +-------------+

After (segregated):

+-----------+   +----------+   +---------+
| IPrinter  |   | IScanner |   | IFax    |
| print()   |   | scan()   |   | fax()   |
+-----------+   +----------+   +---------+
     ^              ^              ^
     |              |              |
+--------------+  +-----------+  +----------------+
| SimplePrinter|  | Scanner   |  | AllInOneDevice |
+--------------+  +-----------+  +----------------+
```

### Code example (Bad vs Good)

#### Bad
```java
interface Worker {
    void code();
    void test();
    void deploy();
}

class Intern implements Worker {
    public void code() { /* ok */ }
    public void test() { /* ok */ }
    public void deploy() {
        throw new UnsupportedOperationException("No prod access");
    }
}
```

#### Good
```java
interface Coder { void code(); }
interface Tester { void test(); }
interface Deployer { void deploy(); }

class Intern implements Coder, Tester {
    public void code() { /* ok */ }
    public void test() { /* ok */ }
}

class DevOpsEngineer implements Deployer {
    public void deploy() { /* ok */ }
}
```

### Interview notes
- ISP is often applied at the **API boundary** (service interfaces, SDKs, microservices contracts).
- Fat interfaces cause ripple effects when you add one method.

---

## D — Dependency Inversion Principle (DIP)

### Definition
1. High-level modules should not depend on low-level modules. Both should depend on **abstractions**.
2. Abstractions should not depend on details. Details should depend on abstractions.

Put differently: business logic should not hard-code concrete dependencies like `new MySqlRepo()`; it should depend on an interface/contract.

### Why it matters
- Makes code testable (mock/stub dependencies).
- Makes swapping implementations easier (MySQL → Postgres, SMTP → SendGrid).
- Reduces coupling to frameworks and infrastructure.

### Pictorial representation

```
Before (DIP violated):

+-------------------+     depends on      +------------------+
| OrderProcessor    |-------------------->| MySqlOrderRepo   |
| (business rules)  |                     | (details)        |
+-------------------+                     +------------------+

After (DIP applied):

+-------------------+     depends on      +------------------+
| OrderProcessor    |-------------------->| OrderRepository  |
| (business rules)  |                     | (abstraction)    |
+-------------------+                     +------------------+
                                                ^
                                                |
                                       +------------------+
                                       | MySqlOrderRepo   |
                                       | InMemoryRepo     |
                                       +------------------+
```

### Code example (Bad vs Good)

#### Bad (hard dependency on a detail)
```java
class EmailService {
    public void send(String to, String msg) { /* smtp */ }
}

class UserRegistration {
    private final EmailService email = new EmailService();

    public void register(String userEmail) {
        // business logic...
        email.send(userEmail, "Welcome!");
    }
}
```

#### Good (depend on an abstraction)
```java
interface Notifier {
    void notify(String to, String msg);
}

class EmailNotifier implements Notifier {
    public void notify(String to, String msg) {
        // smtp/sendgrid/etc.
    }
}

class UserRegistration {
    private final Notifier notifier;

    UserRegistration(Notifier notifier) {
        this.notifier = notifier;
    }

    public void register(String userEmail) {
        // business logic...
        notifier.notify(userEmail, "Welcome!");
    }
}

// Test example:
class FakeNotifier implements Notifier {
    public final List<String> sent = new ArrayList<>();
    public void notify(String to, String msg) { sent.add(to + ":" + msg); }
}
```

### Interview notes
- DIP is the foundation behind **Dependency Injection** (DI), but DIP ≠ “use a DI framework”.
- Apply DIP at boundaries where you expect change: database, network, filesystem, third-party APIs.

---

## How SOLID works together (a practical mental model)

Think of SOLID as controlling coupling along two dimensions:

1. **Change isolation** (SRP + OCP)
2. **Safe substitutability and contracts** (LSP + ISP)
3. **Stable boundaries** between business logic and infrastructure (DIP)

### Pictorial summary

```
          (SRP) split responsibilities
                 |
                 v
 (OCP) add new behavior without editing core
                 |
                 v
 (LSP) inheritance/substitution stays correct
                 |
                 v
 (ISP) keep contracts small and focused
                 |
                 v
 (DIP) depend on abstractions, not concrete details

Result: code that changes with less risk.
```

---

## Interview Questions (SOLID) — with Answers

### 1) Explain SOLID in your own words. Why do these principles matter in large systems?
**Answer:** SOLID is a set of design principles that reduce coupling and isolate change. In large systems, requirements change frequently and are implemented by multiple teams; SOLID helps keep changes localized, reduces regression risk, improves testability, and prevents the system from becoming a tangled dependency graph.

Key points to mention:
- SOLID improves **maintainability** and **evolvability**.
- It encourages **composition**, **clear contracts**, and **stable boundaries**.
- It reduces “change amplification” (small change → many files).

### 2) Give a real example from your project where SRP was violated. What refactor did you do?
**Answer:** Typical example: a `UserService` that validates input, talks to DB, calls external email/SMS providers, and constructs HTTP responses. I refactor by extracting responsibilities into focused components:
- `UserValidator` (validation)
- `UserRepository` (persistence)
- `Notifier` (side effects)
- `UserService` becomes an orchestrator of these dependencies.

How to do it safely:
- Add tests around existing behavior.
- Extract class-by-class (or function-by-function) behind existing public API.
- Keep contracts stable and move details out.

### 3) How do you detect a “God class”? What steps do you take to break it down safely?
**Answer:** Signals:
- Very large file, many imports, many unrelated methods.
- High churn (changed often), high fan-in/fan-out.
- Multiple reasons to change (business rules + IO + formatting + auth).

Refactor steps:
1. Identify cohesive clusters (validation, persistence, formatting, orchestration).
2. Introduce seams: interfaces or helper classes.
3. Extract one responsibility at a time, ensuring tests pass.
4. Replace internal calls with new components.
5. Re-run tests + add new tests for extracted modules.

### 4) OCP: When is it worth introducing Strategy/Polymorphism? When is it over-engineering?
**Answer:** Worth it when:
- Behavior changes frequently (pricing rules, workflows, integrations).
- You have stable core logic and variable policies.
- You need plugin-like extensibility.

Over-engineering when:
- The “variants” are unlikely to change.
- Only 1–2 branches exist and will remain so.
- Introducing abstraction adds indirection without reducing change risk.

Rule of thumb: apply OCP to **volatile axes** of change, not everything.

### 5) Give an example of OCP using composition instead of inheritance.
**Answer:** A classic example is pricing or shipping rules:
- `OrderPricer` composes a list of `PricingRule` strategies.
- Add new behavior by adding a new `PricingRule` implementation and wiring it.

Composition advantage:
- You can combine rules, order them, enable/disable them at runtime.
- Avoids brittle inheritance hierarchies.

### 6) LSP: What does “behavioral contract” mean? Give an example of an LSP violation you’ve seen.
**Answer:** Behavioral contract means the guarantees a type provides:
- Preconditions should not get stronger in subclasses.
- Postconditions should not get weaker.
- Invariants must be preserved.

Example violation:
- Base class `FileStore.save()` promises it always persists data.
- Subclass `ReadOnlyFileStore.save()` throws `UnsupportedOperationException`.

Fix:
- Don’t model “read-only” as a subtype of a read-write contract.
- Split interfaces (ISP) into `Reader` and `Writer`, or use composition.

### 7) Classic: Why is `Square extends Rectangle` problematic? How would you redesign it?
**Answer:** Because `Rectangle` implies width and height can vary independently; `Square` enforces width==height. That breaks expectations of clients that rely on independent setters, producing incorrect results.

Redesign options:
- Prefer immutability and model both as separate `Shape` implementations.
- Or eliminate setters and use constructors/factories that preserve invariants.

### 8) What’s the relationship between LSP and `instanceof` checks in code?
**Answer:** Frequent `instanceof` checks often indicate violated substitutability:
- If clients must check subtype to behave correctly, the base type contract is insufficient or violated.

Fix approach:
- Strengthen/clarify abstraction (better interface design).
- Use polymorphism to move behavior into the type.
- Or split interfaces so clients depend only on what they need (ISP).

### 9) ISP: What problems do fat interfaces cause in microservice-to-microservice APIs?
**Answer:** Fat interfaces in service contracts cause:
- Breaking changes and forced client upgrades when adding/removing fields/methods.
- Clients depending on endpoints they never use.
- Security risk: exposing data/actions broadly.
- More coupling → slower independent deployments.

Better approach:
- Provide smaller, purpose-driven APIs/endpoints.
- Version APIs thoughtfully.
- Avoid “mega DTOs”; use separate request/response models per use case.

### 10) How do you apply ISP in REST/gRPC APIs?
**Answer:**
- REST: design endpoints around use cases (resource + action), avoid one endpoint returning everything.
- gRPC: split large `.proto` services into multiple services; use focused RPC methods.
- Use separate DTOs/messages for different operations.
- Keep read vs write operations separate (often aligns with CQRS).

### 11) DIP: Explain the difference between Dependency Injection and Dependency Inversion.
**Answer:**
- **DIP** is a design principle: high-level policy depends on abstractions, not details.
- **DI** is a technique to supply dependencies from the outside (constructor injection, etc.).

DI frameworks (Spring, Guice) help apply DIP, but DIP can be done without a framework.

### 12) How do you unit test business logic that depends on external systems (DB, HTTP, queues) using DIP?
**Answer:**
1. Define an abstraction (e.g., `PaymentGateway`, `OrderRepository`).
2. In production, inject real implementations.
3. In unit tests, inject fakes/stubs/mocks.

This allows:
- deterministic tests
- no network/DB
- precise edge case simulation (timeouts, retries, failures)

### 13) Where should interfaces live (domain vs infrastructure)? What are trade-offs?
**Answer:** Common guidance:
- Put interfaces in the **domain/application** layer if they represent what the domain needs (e.g., `OrderRepository`). Then infrastructure “implements” them.
- Put interfaces in infrastructure if they are purely technical and not part of domain language.

Trade-offs:
- Domain-owned interfaces keep dependencies pointing inward (Clean Architecture).
- Too many domain interfaces can add indirection if the system is small.

### 14) How does SOLID relate to Clean Architecture / Hexagonal Architecture?
**Answer:** SOLID supports the same goals:
- SRP/OCP help keep use-cases and policies stable.
- ISP helps keep ports small.
- DIP is central: the domain depends on abstractions (ports), adapters implement details.

Hexagonal/ports-and-adapters is essentially DIP at the architecture level.

### 15) In your experience, which SOLID principle gives the most value and why?
**Answer:** Often **SRP** and **DIP** deliver the biggest ROI:
- SRP reduces complexity and change impact.
- DIP makes testing and swapping infrastructure dramatically easier.

But the “most value” depends on context:
- Plugin-like systems benefit heavily from OCP.
- Inheritance-heavy codebases benefit heavily from LSP.

### 16) How would you teach SOLID to a junior developer using a simple example?
**Answer:** Use a small feature (e.g., notifications) and evolve requirements:
1. Start with email-only.
2. Add SMS.
3. Add push notifications.

Show:
- OCP/DIP: introduce `Notifier` interface and inject implementations.
- SRP: keep formatting vs sending separate.
- ISP: avoid forcing SMS notifier to implement email-specific methods.

### 17) What’s a scenario where violating SRP is acceptable temporarily?
**Answer:** Early prototyping / spike solutions where speed is key and requirements are unstable. It’s acceptable if you:
- keep the scope small,
- timebox the prototype,
- and plan a refactor once requirements stabilize.

### 18) How do SOLID principles show up in refactoring legacy code?
**Answer:** You usually apply SOLID incrementally:
- Add tests around critical paths.
- Introduce seams (DIP) to break direct dependencies.
- Extract responsibilities (SRP) to reduce “God objects”.
- Replace conditionals with polymorphism where change is frequent (OCP).
- Split fat interfaces and remove `UnsupportedOperationException` patterns (ISP/LSP).

### 19) Explain how LSP affects exception handling and method preconditions/postconditions.
**Answer:** In LSP:
- A subtype should not require **stricter preconditions** than the base type.
- It should not deliver **weaker postconditions**.
- It should not throw **new, unexpected exceptions** for valid base-type inputs.

Example:
- Base: `parse(String)` accepts any string and returns optional result.
- Subtype: throws on empty string (stronger precondition) → LSP risk.

### 20) Provide a design for a notification system (Email/SMS/Push) demonstrating OCP and DIP.
**Answer:**
- Define `Notifier` abstraction: `send(to, message)`.
- Implement `EmailNotifier`, `SmsNotifier`, `PushNotifier`.
- High-level use case depends on `Notifier` (or a `NotificationService` that composes many notifiers).
- Add new channel by adding a new implementation and wiring configuration (OCP).

Pictorial:
```
+----------------------+      depends on      +-------------------+
| UserRegistration     |--------------------->| Notifier          |
+----------------------+                      +-------------------+
                                                     ^
                                                     |
                                 +-------------------+-------------------+
                                 |                   |                   |
                          +-------------+      +-------------+     +-------------+
                          | Email       |      | SMS         |     | Push        |
                          | Notifier    |      | Notifier    |     | Notifier    |
                          +-------------+      +-------------+     +-------------+
```

---

### Quick one-liners (useful in interviews)

- **SRP**: “One reason to change.”
- **OCP**: “Add new behavior without editing stable code.”
- **LSP**: “Subtypes must be substitutable without surprises.”
- **ISP**: “No client should depend on methods it doesn’t use.”
- **DIP**: “High-level policy depends on abstractions, not details.”
