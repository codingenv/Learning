# Java Design Patterns

### A Beginner-Friendly Guide to the 10 Most Important Patterns

---

> **Who this book is for:** Software engineers who know basic Java (classes, objects, interfaces, inheritance) but have never formally studied design patterns.
>
> **What you will be able to do after reading:** Recognise common design problems, choose the right pattern, implement it cleanly in Java, explain it in an interview, and avoid the classic mistakes.
>
> **How to read this book:** Chapter 1 builds the foundation. Then each chapter covers one pattern using the same repeatable structure, so you always know where to find what you need. Every chapter ends with exercises, interview questions, and a pitfalls section.

---

## Table of Contents

1. [Introduction to Design Patterns](#chapter-1-introduction-to-design-patterns)
2. [Singleton Pattern](#chapter-2-singleton-pattern)
3. [Factory Method Pattern](#chapter-3-factory-method-pattern)
4. [Abstract Factory Pattern](#chapter-4-abstract-factory-pattern)
5. [Builder Pattern](#chapter-5-builder-pattern)
6. [Prototype Pattern](#chapter-6-prototype-pattern)
7. [Adapter Pattern](#chapter-7-adapter-pattern)
8. [Decorator Pattern](#chapter-8-decorator-pattern)
9. [Observer Pattern](#chapter-9-observer-pattern)
10. [Strategy Pattern](#chapter-10-strategy-pattern)
11. [Command Pattern](#chapter-11-command-pattern)
12. [Bringing It All Together](#chapter-12-bringing-it-all-together)

---

## Chapter 1: Introduction to Design Patterns

Welcome. Before we dive into individual patterns, let us answer the most important questions: what *are* design patterns, why should you care, and how are they organised? This chapter is the foundation for everything that follows.

### 1.1 What Are Design Patterns?

A **design pattern** is a proven, reusable solution to a problem that occurs over and over again in software design. It is **not** a finished piece of code you copy and paste. It is more like a *recipe* or a *blueprint* -- a general approach you adapt to your specific situation.

**Analogy: house blueprints.** An architect does not reinvent the idea of "a staircase" for every house. There are well-known, proven ways to design a staircase that are safe and efficient. The architect picks the right one and adapts it to the house. Design patterns are the same: proven blueprints for common coding problems, refined by thousands of developers over decades.

**A crucial point:** A pattern is a *description of how to solve a problem*, not the solution itself. Two developers using the same pattern may write slightly different code -- and both can be correct.

The famous catalogue of patterns comes from a 1994 book by four authors (often called the "Gang of Four" or GoF), which described 23 classic patterns. In this book we focus on the **10 most important and widely used** ones -- the patterns you will actually meet in real Java projects and in job interviews.

### 1.2 Why Do Design Patterns Matter?

You might wonder: "Why not just write code that works?" You can -- but design patterns give you four big advantages:

**1. A shared vocabulary.** When you tell a teammate "let us use a Factory here," they instantly understand the whole approach without a long explanation. Patterns are a common language among developers worldwide.

**2. Proven solutions.** These approaches have been battle-tested for decades. Using them means you avoid reinventing the wheel and avoid common traps.

**3. Better, more maintainable code.** Patterns lead to code that is easier to change, extend, and test -- because they are built around solid principles like "separate the things that change from the things that stay the same."

**4. Interview readiness.** Design patterns are one of the most common topics in software engineering interviews. Knowing them well is a real career advantage.

**A word of caution (important!):** Patterns are tools, not goals. Do **not** force a pattern into your code just because you know it. The best engineers use a pattern only when it genuinely simplifies a real problem. We will repeat this warning throughout the book in each "When NOT to use it" section.

### 1.3 The Three Categories of Patterns

The 23 classic patterns are grouped into three families based on what they do. Understanding these categories helps you mentally organise every pattern you learn.

```
THE THREE FAMILIES OF DESIGN PATTERNS

  +-------------------+-------------------------+----------------------------+
  | CREATIONAL        | STRUCTURAL              | BEHAVIORAL                 |
  | "How objects are  | "How objects are        | "How objects talk and      |
  |  created"         |  composed/connected"    |  share responsibility"     |
  +-------------------+-------------------------+----------------------------+
  | - Singleton       | - Adapter               | - Observer                 |
  | - Factory Method  | - Decorator             | - Strategy                 |
  | - Abstract Factory|   (and others)          | - Command                  |
  | - Builder         |                         |   (and others)             |
  | - Prototype       |                         |                            |
  +-------------------+-------------------------+----------------------------+
```

**1. Creational patterns** -- concerned with **how objects are created.** They make object creation flexible, controlled, and decoupled from the rest of your code. (Singleton, Factory Method, Abstract Factory, Builder, Prototype.)

**2. Structural patterns** -- concerned with **how objects and classes are composed** into larger structures while keeping them flexible. (Adapter, Decorator, and others.)

**3. Behavioral patterns** -- concerned with **how objects communicate** and how responsibilities are assigned between them. (Observer, Strategy, Command, and others.)

The 10 patterns in this book are spread across these three families, as shown above. As you read each chapter, remind yourself which family it belongs to -- it tells you a lot about what the pattern is for.

### 1.4 A Few Principles Behind Good Patterns

Almost every pattern is built on a handful of timeless design principles. You do not need to memorise these now, but keep them in the back of your mind -- you will see them appear again and again:

- **Program to an interface, not an implementation.** Depend on *what* something does (an interface), not *how* it does it (a concrete class). This makes swapping implementations easy.
- **Favour composition over inheritance.** Building behaviour by *combining* small objects is often more flexible than deep inheritance hierarchies.
- **Encapsulate what varies.** Find the parts of your code most likely to change, and isolate them so changes do not ripple everywhere.
- **Loose coupling.** Keep objects as independent of each other as possible, so a change in one does not break many others.

**Analogy for "program to an interface":** A wall socket is an interface. Any device with the right plug works, regardless of what is inside it. You do not rewire your house for each new appliance. Good code is the same -- it relies on standard "sockets" (interfaces) so parts can be swapped freely.

### 1.5 How Each Pattern Chapter Is Structured

To make learning predictable, every pattern chapter follows the **same 10-part structure:**

1. **Introduction** -- the problem it solves and when to use it.
2. **Real-world analogy** -- an intuitive everyday example.
3. **Concept explanation** -- plain-English description.
4. **Java implementation** -- clean, commented, beginner-friendly code with a step-by-step walkthrough.
5. **Class diagram & block diagram** -- an ASCII block diagram plus a description of how the classes relate.
6. **Advantages** -- what you gain.
7. **Disadvantages** -- the costs and trade-offs.
8. **Common mistakes** -- traps beginners fall into.
9. **When NOT to use it** -- knowing the limits is as important as knowing the pattern.
10. **Real-world use cases** -- where it appears in real systems and frameworks.

Plus, each chapter ends with **exercises**, **interview-style questions**, and a **common pitfalls** recap.

### Chapter 1 Summary

- A **design pattern** is a proven, reusable *blueprint* for a common design problem -- not copy-paste code.
- Patterns give you a **shared vocabulary, proven solutions, maintainable code,** and **interview readiness.**
- They fall into three families: **Creational** (object creation), **Structural** (object composition), and **Behavioral** (object communication).
- Underlying principles: program to interfaces, favour composition, encapsulate what varies, keep coupling loose.
- Use a pattern only when it genuinely helps -- never force one.

### Exercises

1. In your own words, explain the difference between a design pattern and a finished piece of code.
2. List the three pattern categories and write one sentence describing what each is concerned with.
3. Think of a real-world non-software "pattern" (like a recipe or a building technique) and explain why it counts as a reusable solution.

### Interview Questions

1. What is a design pattern, and why are they useful?
2. Name the three categories of design patterns and give an example of each.
3. What does "program to an interface, not an implementation" mean, and why does it matter?

### Common Pitfalls

- **Pattern obsession:** forcing patterns where simple code would do. This adds complexity, not value.
- **Memorising code instead of intent:** patterns are about *why* and *when*, not just *how*. Understand the problem each one solves.
- **Ignoring trade-offs:** every pattern has disadvantages. Always weigh them.

---

## Chapter 2: Singleton Pattern

**Category: Creational**

### 2.1 Introduction

**The problem it solves:** Sometimes you need *exactly one* instance of a class in your entire application -- and you need a single, global point to access it. Examples: a single configuration manager, one logging service, one database connection pool, one cache. If different parts of your program created their own copies, you would get inconsistent state, wasted resources, or conflicts.

The **Singleton pattern** ensures a class has **only one instance** and provides a **global access point** to it.

**When should you use it?**
- When creating more than one instance would cause problems (e.g., two configuration objects disagreeing).
- When exactly one object must coordinate actions across the whole system.
- When you want a single shared resource (a connection pool, a thread pool, a cache).

### 2.2 Real-World Analogy

Think of the **President (or Prime Minister) of a country.** There is only one at a time. Everyone refers to "the President" and there is a well-known way to reach that single office. You cannot accidentally create a second President. The office *guarantees* there is exactly one, and provides a single point of access.

Another analogy: the **printer spooler** on your computer. There is one spooler managing the print queue. If every app created its own spooler, print jobs would collide.

### 2.3 Concept Explanation

The idea is simple to state:
1. **Prevent others from creating instances** of the class directly (make the constructor private).
2. **Create the single instance inside the class itself.**
3. **Offer a public method** (commonly `getInstance()`) that always returns that same single instance.

Because the constructor is private, no outside code can write `new MyClass()`. The only way to get the object is through `getInstance()`, which hands back the one and only instance every time.

### 2.4 Java Implementation

Let us build a `ConfigurationManager` that loads app settings once and shares them everywhere. We will show the most common **thread-safe** approach using a "holder" idiom, which is both simple and efficient.

```java
// A Singleton that holds application configuration.
public class ConfigurationManager {

    // Step 1: A private static field will hold the single instance.
    //         We use a private static inner "holder" class (explained below).

    // Step 2: Make the constructor PRIVATE so no other class can do "new".
    private ConfigurationManager() {
        // Imagine loading settings from a file here.
        System.out.println("Configuration loaded (this should print only once).");
    }

    // Step 3: The "holder" idiom. This inner class is not loaded into memory
    //         until getInstance() is called for the first time. The JVM
    //         guarantees class loading is thread-safe, so this is safe AND lazy.
    private static class Holder {
        private static final ConfigurationManager INSTANCE = new ConfigurationManager();
    }

    // Step 4: The global access point. Always returns the same instance.
    public static ConfigurationManager getInstance() {
        return Holder.INSTANCE;
    }

    // A sample method the single instance provides.
    public String getSetting(String key) {
        return "value-for-" + key;
    }
}
```

Using it anywhere in the application:

```java
public class App {
    public static void main(String[] args) {
        ConfigurationManager c1 = ConfigurationManager.getInstance();
        ConfigurationManager c2 = ConfigurationManager.getInstance();

        // Both variables point to the SAME object.
        System.out.println(c1 == c2);          // prints: true
        System.out.println(c1.getSetting("timeout")); // value-for-timeout
    }
}
```

**Step-by-step explanation:**
- **Private constructor:** stops anyone from creating new instances with `new`.
- **The `Holder` inner class:** holds the single instance in a `static final` field. Java only loads this inner class the first time it is referenced (inside `getInstance()`), so the instance is created **lazily** (only when first needed) and the JVM makes this **thread-safe automatically** -- no manual locking needed.
- **`getInstance()`:** the one public door to the object. `c1 == c2` is `true`, proving both references point to the exact same object.

**An even simpler version (the enum Singleton).** Many experts consider this the best Singleton in Java because it is concise and handles tricky edge cases (serialization, reflection) for free:

```java
// The enum Singleton -- short, safe, and recommended by many experts.
public enum Configuration {
    INSTANCE;   // this single constant IS the one instance

    public String getSetting(String key) {
        return "value-for-" + key;
    }
}

// Usage:
// Configuration.INSTANCE.getSetting("timeout");
```

### 2.5 Class Diagram & Block Diagram

**Block diagram:**

```
        +------------------------------+
        |      ConfigurationManager    |
        +------------------------------+
        | - INSTANCE  (the ONE object) |
        | - ConfigurationManager()     |  <- private constructor (no "new")
        +------------------------------+
        | + getInstance() : Manager    |  <- the single global access point
        | + getSetting(key) : String   |
        +------------------------------+
                      ^
                      | "always returns the SAME instance"
               +--------------+
               |    Client    |   calls ConfigurationManager.getInstance()
               +--------------+
```

**In words:** Picture a single box labelled `ConfigurationManager`. Inside it:
- A **private constructor** (marked with a minus sign, meaning private).
- A **private static field** holding the one instance.
- A **public static method `getInstance()`** (marked with a plus sign) that returns that instance.

There are no subclasses and no complex relationships -- the whole pattern lives inside one self-contained class that points to a single instance of itself.

### 2.6 Advantages

- **Controlled access:** guarantees exactly one instance.
- **Saves resources:** avoids creating expensive objects repeatedly.
- **Global access point:** easy to reach from anywhere.
- **Lazy initialisation possible:** the instance can be created only when first needed.

### 2.7 Disadvantages

- **Hidden dependencies:** code that calls `getInstance()` secretly depends on the Singleton, which is not visible in method signatures. This makes the design harder to understand.
- **Hard to unit test:** Singletons carry global state that persists between tests, causing tests to interfere with each other. They are also hard to replace with fakes/mocks.
- **Can become a "god object":** people tend to dump too many responsibilities into the one global object.
- **Concurrency care needed:** naive implementations are not thread-safe.

### 2.8 Common Mistakes

- **Forgetting thread safety.** A simple `if (instance == null) instance = new ...()` can create *two* instances if two threads run it at once. Use the holder idiom or enum.
- **Making the constructor public by accident**, which lets others create extra instances.
- **Overusing Singletons** as a convenient global variable dumping ground.
- **Ignoring serialization/reflection attacks** that can create a second instance (the enum approach avoids these).

### 2.9 When NOT to Use It

- When you actually might need more than one instance later (Singletons are hard to "un-singleton").
- When it just hides a global variable -- global mutable state makes code fragile.
- When testability matters a lot; prefer **dependency injection** (passing the single instance in) so you can swap it in tests. In modern Spring applications, beans are singletons *managed by the framework* and injected -- which gives you the "one instance" benefit without the testing pain.

### 2.10 Real-World Use Cases

- **`java.lang.Runtime`** -- the JDK's own Singleton (`Runtime.getRuntime()`).
- **Logging frameworks** -- a single shared logger configuration.
- **Spring beans** -- by default, Spring creates one shared instance per bean (singleton scope).
- **Database connection pools and caches** -- one shared pool for the whole app.
- **Configuration/settings managers** -- one source of truth for settings.

### Exercises

1. Implement a thread-safe `Logger` Singleton with a `log(String message)` method. Prove that two calls to `getInstance()` return the same object.
2. Convert your `Logger` into the enum Singleton form.
3. Explain in writing why the "holder idiom" is both lazy and thread-safe.

### Interview Questions

1. What problem does the Singleton pattern solve?
2. How do you make a Singleton thread-safe in Java? Name at least two approaches.
3. Why do many experts recommend the enum Singleton?
4. Why are Singletons considered hard to unit test, and how does dependency injection help?

### Common Pitfalls

- A non-thread-safe `getInstance()` can create multiple instances under concurrency.
- Treating Singletons as global variables leads to tangled, fragile code.
- Forgetting that serialization and reflection can break the "one instance" guarantee (enum fixes this).

---

## Chapter 3: Factory Method Pattern

**Category: Creational**

### 3.1 Introduction

**The problem it solves:** Sometimes you need to create objects, but you do not want your code to be tied to the *exact* class being created. If you scatter `new ConcreteThing()` calls all over your code, then adding a new type or changing the creation logic means editing many places. The **Factory Method** moves object creation into a dedicated method, so the rest of your code asks for an object *by what it does* (an interface) rather than *what exactly it is*.

In short: **Factory Method defines a method for creating objects, but lets subclasses (or the method's logic) decide which concrete class to actually create.**

**When should you use it?**
- When you do not know in advance the exact type of object you need.
- When you want to centralise and isolate object-creation logic.
- When you want to make it easy to add new types without changing existing code.

### 3.2 Real-World Analogy

Think of a **pizza restaurant.** You (the customer) just say, "I want a Margherita pizza." You do not care *how* the kitchen makes it or which chef handles the dough. The kitchen (the factory) takes your order and returns the right pizza. If the restaurant adds a new "Veggie Supreme" to the menu, you order it the same way -- nothing changes for you as a customer.

The kitchen is the **factory**; "pizza" is the **interface**; Margherita and Veggie Supreme are the **concrete products.**

### 3.3 Concept Explanation

The pattern has three pieces:
1. A **product interface** (e.g., `Pizza`) -- the common type all created objects share.
2. **Concrete products** (e.g., `MargheritaPizza`, `VeggiePizza`) -- the actual classes.
3. A **factory** with a creation method that returns the product interface type, deciding internally which concrete product to build.

The calling code only ever talks to the **interface** and the **factory**. It never writes `new MargheritaPizza()` directly. This means new product types can be added in one place without touching the calling code.

### 3.4 Java Implementation

Let us build a pizza factory.

```java
// Step 1: The product interface -- what every pizza can do.
public interface Pizza {
    void prepare();
    void bake();
}
```

```java
// Step 2: Concrete products implementing the interface.
public class MargheritaPizza implements Pizza {
    public void prepare() { System.out.println("Preparing Margherita: tomato + mozzarella."); }
    public void bake()    { System.out.println("Baking Margherita for 10 minutes."); }
}

public class VeggiePizza implements Pizza {
    public void prepare() { System.out.println("Preparing Veggie: peppers + onions + olives."); }
    public void bake()    { System.out.println("Baking Veggie for 12 minutes."); }
}
```

```java
// Step 3: The factory. Its method decides which concrete pizza to create.
public class PizzaFactory {

    // The "factory method". Callers pass a type and get back a Pizza,
    // without knowing the concrete class.
    public Pizza createPizza(String type) {
        switch (type.toLowerCase()) {
            case "margherita": return new MargheritaPizza();
            case "veggie":     return new VeggiePizza();
            default:
                throw new IllegalArgumentException("Unknown pizza type: " + type);
        }
    }
}
```

```java
// Step 4: The client code -- notice it never says "new MargheritaPizza()".
public class PizzaShop {
    public static void main(String[] args) {
        PizzaFactory factory = new PizzaFactory();

        Pizza pizza = factory.createPizza("margherita"); // ask the factory
        pizza.prepare();
        pizza.bake();
    }
}
```

**Step-by-step explanation:**
- **`Pizza` interface:** the client depends only on this, not on concrete classes.
- **Concrete pizzas:** each implements `Pizza`. To add a new pizza, you create a new class and add one line to the factory -- the client never changes.
- **`PizzaFactory.createPizza(...)`:** the single place where the decision "which class to instantiate" lives. This is the factory method.
- **`PizzaShop` (client):** asks the factory for a `Pizza` and uses it through the interface. It is fully decoupled from concrete classes.

**A note on the "true" GoF Factory Method.** The classic version uses *subclasses* to decide the product: an abstract `creator` class declares an abstract `createPizza()` method, and each subclass overrides it to return a specific product. The simpler "switch-based" version above is often called a *Simple Factory* and is the most common starting point in real code. Both share the same core idea: **centralise creation behind a method.**

### 3.5 Class Diagram & Block Diagram

**Block diagram:**

```
     +---------------+  creates  +-----------------------+
     |  PizzaFactory |---------->|     <<interface>>     |
     |  createPizza()|           |        Pizza          |
     +---------------+           |  prepare() / bake()   |
            ^                    +-----------------------+
            | uses                  ^                ^
     +---------------+   implements |                | implements
     |    Client     |     +------------------+  +---------------+
     | (PizzaShop)   |     | MargheritaPizza  |  |  VeggiePizza  |
     +---------------+     +------------------+  +---------------+

  The Client talks ONLY to PizzaFactory + the Pizza interface --
  never directly to the concrete pizza classes.
```

**In words:**
- An interface `Pizza` at the top.
- Two boxes `MargheritaPizza` and `VeggiePizza`, each with a dashed arrow pointing up to `Pizza` (meaning "implements").
- A `PizzaFactory` box with a `createPizza()` method. An arrow points from `PizzaFactory` to `Pizza` (it produces Pizzas).
- A `PizzaShop` (client) box with an arrow to `PizzaFactory` (it uses the factory) and to `Pizza` (it uses the product through the interface).

The client connects to the *interface* and the *factory*, never directly to the concrete products.

### 3.6 Advantages

- **Decoupling:** client code does not depend on concrete classes.
- **Single place for creation logic:** easier to maintain and change.
- **Easy to extend:** add new product types with minimal changes (Open/Closed Principle).
- **Cleaner code:** no scattered `new` calls.

### 3.7 Disadvantages

- **More classes/indirection:** you add a factory and an interface, which can feel heavy for very simple cases.
- **Can grow complex:** a switch-based factory can become large if there are many types (the Abstract Factory or registration maps can help).

### 3.8 Common Mistakes

- **Returning concrete types** from the factory method instead of the interface -- this defeats the decoupling.
- **Putting business logic inside the factory** -- the factory's job is *creation*, not behaviour.
- **Confusing Simple Factory, Factory Method, and Abstract Factory** -- keep their intents distinct (the next chapter clarifies Abstract Factory).

### 3.9 When NOT to Use It

- When object creation is trivial and unlikely to change -- a plain `new` is clearer.
- When you only ever have one product type and no plans to add more.
- When the extra indirection would confuse readers more than it helps.

### 3.10 Real-World Use Cases

- **`java.util.Calendar.getInstance()`** -- returns a concrete calendar based on locale/time zone, hidden behind the method.
- **`NumberFormat.getInstance()`**, **`DateFormat.getInstance()`** in the JDK.
- **JDBC `DriverManager.getConnection(...)`** -- returns a `Connection` whose concrete class depends on the database.
- **Logging frameworks** -- `LoggerFactory.getLogger(...)`.
- **Spring's `BeanFactory`** -- the entire Spring container is, in essence, a sophisticated factory.

### Exercises

1. Build a `ShapeFactory` that creates `Circle`, `Square`, and `Rectangle` objects implementing a `Shape` interface with a `draw()` method.
2. Add a new `Triangle` shape and observe that the client code does not change.
3. Refactor your switch-based factory to use a `Map<String, Supplier<Shape>>` so adding a shape needs no switch edit.

### Interview Questions

1. What problem does the Factory Method solve?
2. What is the difference between Simple Factory, Factory Method, and Abstract Factory?
3. How does the Factory Method support the Open/Closed Principle?
4. Give an example of a factory method in the Java standard library.

### Common Pitfalls

- Leaking concrete types by returning them instead of the interface.
- Letting the factory accumulate unrelated logic.
- Overusing factories for objects that are trivial to create.

---

## Chapter 4: Abstract Factory Pattern

**Category: Creational**

### 4.1 Introduction

**The problem it solves:** Sometimes you need to create **families of related objects** that must be used together and must stay consistent. For example, a UI toolkit must produce buttons, checkboxes, and scrollbars that all match the *same* operating-system look (all Windows-style, or all macOS-style). You must never accidentally mix a Windows button with a macOS checkbox.

The **Abstract Factory** provides an interface for creating *whole families* of related products, without specifying their concrete classes. Where a Factory Method makes *one* product, an Abstract Factory makes a *coordinated set* of products.

**When should you use it?**
- When your system needs to work with multiple families of related products.
- When products in a family must be used together and kept consistent.
- When you want to switch the entire family at once (e.g., change the whole UI theme).

### 4.2 Real-World Analogy

Think of **furniture sets.** A "Modern" set includes a modern chair, modern sofa, and modern table -- all matching. A "Victorian" set includes a Victorian chair, sofa, and table. You choose a *style* (the factory), and it gives you a whole matching set. You would never put a Victorian chair next to a sleek modern glass table -- they would clash. The Abstract Factory guarantees everything you get belongs to the same style family.

### 4.3 Concept Explanation

The structure has four parts:
1. **Abstract products** -- interfaces for each kind of product (e.g., `Button`, `Checkbox`).
2. **Concrete products** -- specific implementations per family (e.g., `WindowsButton`, `MacButton`).
3. **Abstract factory** -- an interface with a creation method for each product (e.g., `createButton()`, `createCheckbox()`).
4. **Concrete factories** -- one per family (e.g., `WindowsFactory`, `MacFactory`), each producing matching products.

The client picks one concrete factory and uses it to build all its products. Because everything comes from the *same* factory, the products are guaranteed to match.

### 4.4 Java Implementation

Let us build a cross-platform UI toolkit.

```java
// Step 1: Abstract products -- one interface per kind of widget.
public interface Button {
    void render();
}
public interface Checkbox {
    void render();
}
```

```java
// Step 2: Concrete products for the WINDOWS family.
public class WindowsButton implements Button {
    public void render() { System.out.println("Rendering a Windows-style button."); }
}
public class WindowsCheckbox implements Checkbox {
    public void render() { System.out.println("Rendering a Windows-style checkbox."); }
}

// Concrete products for the MAC family.
public class MacButton implements Button {
    public void render() { System.out.println("Rendering a macOS-style button."); }
}
public class MacCheckbox implements Checkbox {
    public void render() { System.out.println("Rendering a macOS-style checkbox."); }
}
```

```java
// Step 3: The abstract factory -- can create a whole family of widgets.
public interface GUIFactory {
    Button createButton();
    Checkbox createCheckbox();
}
```

```java
// Step 4: Concrete factories -- each builds ONE consistent family.
public class WindowsFactory implements GUIFactory {
    public Button createButton()     { return new WindowsButton(); }
    public Checkbox createCheckbox() { return new WindowsCheckbox(); }
}
public class MacFactory implements GUIFactory {
    public Button createButton()     { return new MacButton(); }
    public Checkbox createCheckbox() { return new MacCheckbox(); }
}
```

```java
// Step 5: The client uses ONLY the abstract types. It does not know
//         whether it is dealing with Windows or Mac widgets.
public class Application {
    private final Button button;
    private final Checkbox checkbox;

    // The client is given a factory; it builds a matching family from it.
    public Application(GUIFactory factory) {
        this.button = factory.createButton();
        this.checkbox = factory.createCheckbox();
    }

    public void renderUI() {
        button.render();
        checkbox.render();
    }

    public static void main(String[] args) {
        // Decide the family ONCE (e.g., based on the OS).
        String os = "windows";
        GUIFactory factory = os.equals("windows") ? new WindowsFactory() : new MacFactory();

        Application app = new Application(factory);
        app.renderUI();   // all widgets match the chosen family
    }
}
```

**Step-by-step explanation:**
- **Abstract products (`Button`, `Checkbox`):** the client depends only on these.
- **Concrete products:** grouped into families (Windows vs Mac).
- **`GUIFactory` interface:** declares one creation method per product, so a factory makes a *complete set*.
- **Concrete factories:** each one produces only its own family's products, guaranteeing consistency.
- **`Application` (client):** receives a `GUIFactory` and builds its UI. Switching the entire theme is as easy as passing a different factory -- no other code changes.

### 4.5 Class Diagram & Block Diagram

**Block diagram:**

```
              +----------------------+
              |    <<interface>>     |
              |      GUIFactory      |
              |   createButton()     |
              |   createCheckbox()   |
              +----------------------+
                 ^                ^
       implements |                | implements
   +-----------------+        +----------------+
   | WindowsFactory  |        |   MacFactory   |
   +-----------------+        +----------------+
        | creates                    | creates
        v                            v
   WINDOWS family               MAC family
   WindowsButton +              MacButton +
   WindowsCheckbox              MacCheckbox

   (Button   <- WindowsButton, MacButton      implement)
   (Checkbox <- WindowsCheckbox, MacCheckbox  implement)

  One factory  =>  one MATCHING family of products.
```

**In words:**
- Two product interfaces at the top: `Button` and `Checkbox`.
- Four concrete product boxes: `WindowsButton`, `MacButton` (implement `Button`); `WindowsCheckbox`, `MacCheckbox` (implement `Checkbox`).
- A `GUIFactory` interface with `createButton()` and `createCheckbox()`.
- Two concrete factory boxes `WindowsFactory` and `MacFactory`, each implementing `GUIFactory`.
- The `Application` client points to `GUIFactory`, `Button`, and `Checkbox` -- never to any concrete class.

Visually: two parallel "columns" of products (Button family, Checkbox family) and two "rows" of styles (Windows, Mac), with factories tying each row together.

### 4.6 Advantages

- **Guaranteed consistency:** products from one factory always match.
- **Easy family switching:** change one line (the factory) to swap the entire set.
- **Strong decoupling:** the client never touches concrete product classes.
- **Open/Closed friendly:** add a new family by adding a new factory + products, without touching the client.

### 4.7 Disadvantages

- **Lots of classes/interfaces:** can feel heavy, especially for small projects.
- **Hard to add a new *product type*:** adding, say, a `Slider` means changing the factory interface and *every* concrete factory.
- **Higher complexity:** more moving parts to understand.

### 4.8 Common Mistakes

- **Mixing families** by bypassing the factory and instantiating concrete products directly.
- **Confusing it with Factory Method** -- Abstract Factory creates *families* (multiple related products), Factory Method creates *one* product.
- **Over-engineering** -- using it when you only have one family.

### 4.9 When NOT to Use It

- When you have only one family of products (no need for the abstraction).
- When product types change frequently (adding product kinds is painful here).
- When the added complexity outweighs the benefit in a small application.

### 4.10 Real-World Use Cases

- **Cross-platform UI toolkits** (the classic example): Swing's look-and-feel, AWT toolkits.
- **`javax.xml.parsers.DocumentBuilderFactory`** and similar JDK factories that produce families of related parser objects.
- **JDBC drivers** producing families of related objects (`Connection`, `Statement`, `ResultSet`) consistent for one database.
- **Theming systems** where switching a theme swaps a whole set of styled components.

### Exercises

1. Build an Abstract Factory for a game with two families: `OrcFactory` and `ElfFactory`, each producing a `Soldier` and an `Archer` with a `describe()` method.
2. Add a third family (`HumanFactory`) and confirm the client code does not change.
3. Now try adding a new product type (`Mage`) to all families, and notice how many files you must touch -- this reveals the pattern's main weakness.

### Interview Questions

1. How does Abstract Factory differ from Factory Method?
2. What guarantee does Abstract Factory provide about the objects it creates?
3. Why is adding a new *product type* (not a new family) difficult with Abstract Factory?
4. Give a real-world Java API that uses this pattern.

### Common Pitfalls

- Accidentally mixing products from different families by skipping the factory.
- Choosing Abstract Factory when a simpler Factory Method (or no factory) would do.
- Underestimating how painful it is to add new product *types* later.

---

## Chapter 5: Builder Pattern

**Category: Creational**

### 5.1 Introduction

**The problem it solves:** Some objects have many fields -- some required, many optional. Creating them with a giant constructor becomes a nightmare:

```java
// The "telescoping constructor" problem -- which argument is which?!
new Pizza(12, true, false, true, false, true, "thin");
```

What does `true, false, true` mean? You cannot tell without checking the constructor. And if half the parameters are optional, you end up writing many overloaded constructors. The **Builder pattern** solves this by constructing the object **step by step** with clearly named methods, then producing the final object at the end.

**When should you use it?**
- When an object has many parameters, especially optional ones.
- When you want readable, self-documenting object creation.
- When you want to create **immutable** objects (set everything at build time, then never change it).

### 5.2 Real-World Analogy

Think of ordering a **custom burger** at a build-your-own counter. You say: "Start with a bun, add a beef patty, add cheese, add lettuce, no onions, extra sauce." Each instruction is a clear step. At the end, the staff hands you the finished burger. You did not have to specify everything in one confusing sentence -- you built it up, one named choice at a time.

### 5.3 Concept Explanation

The Builder pattern separates the **construction** of a complex object from its final **representation.** You create a small helper object (the "builder") that:
1. Offers a method for each field (often returning the builder itself, so calls can be chained).
2. Has a final `build()` method that validates and returns the finished object.

Because each method is named (`.cheese(true)`, `.size(12)`), the code reads like plain English, and you only set the fields you care about.

### 5.4 Java Implementation

We will build an immutable `Pizza` using a static nested `Builder`.

```java
public class Pizza {
    // All fields are final -> the Pizza is immutable once built.
    private final int size;          // required
    private final boolean cheese;    // optional
    private final boolean pepperoni; // optional
    private final boolean mushrooms; // optional

    // Step 4: Private constructor takes the builder and copies values in.
    private Pizza(Builder builder) {
        this.size = builder.size;
        this.cheese = builder.cheese;
        this.pepperoni = builder.pepperoni;
        this.mushrooms = builder.mushrooms;
    }

    @Override
    public String toString() {
        return "Pizza{size=" + size + ", cheese=" + cheese
             + ", pepperoni=" + pepperoni + ", mushrooms=" + mushrooms + "}";
    }

    // Step 1: A static nested Builder class.
    public static class Builder {
        private final int size;            // required -> in builder's constructor
        private boolean cheese = false;    // optional -> sensible defaults
        private boolean pepperoni = false;
        private boolean mushrooms = false;

        // Required field goes in the builder's constructor.
        public Builder(int size) {
            this.size = size;
        }

        // Step 2: One method per optional field. Each returns "this"
        //         so calls can be chained fluently.
        public Builder cheese(boolean value)    { this.cheese = value; return this; }
        public Builder pepperoni(boolean value) { this.pepperoni = value; return this; }
        public Builder mushrooms(boolean value) { this.mushrooms = value; return this; }

        // Step 3: build() validates and returns the finished Pizza.
        public Pizza build() {
            if (size <= 0) {
                throw new IllegalStateException("Size must be positive.");
            }
            return new Pizza(this);
        }
    }
}
```

Using the builder:

```java
public class Restaurant {
    public static void main(String[] args) {
        // Reads almost like a sentence. Only set what you need.
        Pizza pizza = new Pizza.Builder(12)
                .cheese(true)
                .mushrooms(true)
                .build();

        System.out.println(pizza);
        // Pizza{size=12, cheese=true, pepperoni=false, mushrooms=true}
    }
}
```

**Step-by-step explanation:**
- **Required vs optional:** the required `size` goes into the `Builder` constructor; optional fields have defaults and their own setter-like methods.
- **Fluent chaining:** each optional method returns `this`, so you can chain `.cheese(true).mushrooms(true)`.
- **`build()`:** the single place to validate (e.g., size must be positive) and create the immutable `Pizza`.
- **Immutability:** `Pizza`'s fields are `final` and there are no setters, so once built it can never change -- which makes it safe to share across threads.

### 5.5 Class Diagram & Block Diagram

**Block diagram:**

```
  +-------------------+  build()   +-------------------+
  |  Pizza.Builder    |----------->|       Pizza       |
  +-------------------+            +-------------------+
  | size (required)   |            | - size      final |
  | cheese()          |            | - cheese    final |
  | pepperoni()       |            | - pepperoni final |
  | mushrooms()       |            | - mushrooms final |
  | build() : Pizza   |            | (no setters =     |
  +-------------------+            |    immutable)     |
        ^   each method            +-------------------+
        |   returns "this"
        |   (fluent chaining)
   +-----------+
   |  Client   |  new Pizza.Builder(12).cheese(true).build()
   +-----------+
```

**In words:**
- A `Pizza` class with `final` fields and a `private` constructor.
- Inside (or alongside) it, a `Builder` class with the same fields, one method per field returning `Builder`, and a `build()` method returning a `Pizza`.
- An arrow from `Builder` to `Pizza` labelled "creates". The client talks only to the `Builder`.

### 5.6 Advantages

- **Readable creation:** named methods make intent obvious.
- **Handles optional fields gracefully:** no constructor explosion.
- **Supports immutability:** great for safe, thread-friendly objects.
- **Validation in one place:** `build()` checks invariants before returning.

### 5.7 Disadvantages

- **More code:** you write a builder in addition to the class.
- **Overkill for simple objects:** a 2-field object does not need a builder.
- **Slight verbosity:** doubles the field declarations (class + builder).

### 5.8 Common Mistakes

- **Forgetting to validate** in `build()`, allowing invalid objects.
- **Letting the builder be reused incorrectly**, accidentally sharing state between objects.
- **Adding setters to the final object**, which breaks the immutability benefit.
- **Using a builder when a simple constructor would be clearer.**

### 5.9 When NOT to Use It

- When the object has only a few fields and a constructor reads fine.
- When all fields are required and order is obvious.
- When immutability and optional fields are not concerns.

### 5.10 Real-World Use Cases

- **`StringBuilder`** -- builds a string step by step (a classic builder).
- **`java.util.stream.Stream.Builder`** and **`Calendar.Builder`** in the JDK.
- **Lombok's `@Builder`** -- generates a builder automatically.
- **HTTP clients:** `HttpRequest.newBuilder().uri(...).header(...).build()` in `java.net.http`.
- **Configuration objects** across countless libraries (e.g., OkHttp, gRPC).

### Exercises

1. Build an immutable `User` class with required `username` and optional `email`, `age`, and `address`, using a Builder.
2. Add validation: `age` must not be negative, and `username` must not be blank.
3. Compare your builder version to a constructor-only version. Which reads better with 5 optional fields?

### Interview Questions

1. What problem does the Builder pattern solve compared to telescoping constructors?
2. How does Builder help create immutable objects?
3. Why does each builder method return `this`?
4. Give an example of the Builder pattern in the Java standard library.

### Common Pitfalls

- Skipping validation in `build()`.
- Exposing setters on the final object, defeating immutability.
- Using Builder for trivially simple objects.

---

## Chapter 6: Prototype Pattern

**Category: Creational**

### 6.1 Introduction

**The problem it solves:** Sometimes creating a new object from scratch is **expensive** (it requires heavy computation, database calls, or complex setup). If you already have an object configured the way you want, it can be far cheaper to **copy (clone)** it than to build a fresh one. The **Prototype pattern** creates new objects by **cloning an existing instance** (the "prototype") rather than calling `new` and reconfiguring everything.

**When should you use it?**
- When creating an object is costly and you already have a similar one.
- When you want to produce many objects that are variations of a base configuration.
- When you want to avoid a complex hierarchy of factory classes.

### 6.2 Real-World Analogy

Think of a **photocopier.** Instead of writing a document by hand every time, you create one master copy and then photocopy it as many times as you need. Each copy starts identical to the master, and you can then make small edits on individual copies. The master is the **prototype**; the photocopies are the **clones.**

Another analogy: a cell dividing -- it copies itself rather than building a new cell atom by atom.

### 6.3 Concept Explanation

The pattern relies on a **clone operation.** A prototype object knows how to make a copy of itself. Clients ask the prototype to `clone()` instead of constructing a new object directly.

There is an important subtlety: **shallow vs deep copy.**
- A **shallow copy** copies the object's fields as-is. If a field is a reference to another object, both the original and the copy point to the *same* nested object (shared).
- A **deep copy** also copies the nested objects, so the clone is fully independent.

Choosing correctly matters: shallow copies can cause bugs where editing the clone accidentally changes the original.

### 6.4 Java Implementation

Java has built-in support via the `Cloneable` interface and `Object.clone()`, but a clean, explicit approach is often preferred. We will show both ideas.

```java
// A prototype interface declaring a copy method.
public interface Prototype {
    Prototype copy();
}
```

```java
import java.util.ArrayList;
import java.util.List;

public class Document implements Prototype {
    private String title;
    private List<String> sections;   // a reference type -> watch out for deep copy!

    public Document(String title, List<String> sections) {
        this.title = title;
        this.sections = sections;
    }

    // A copy constructor makes deep copying explicit and safe.
    private Document(Document source) {
        this.title = source.title;
        // DEEP copy: build a NEW list so the clone is independent.
        this.sections = new ArrayList<>(source.sections);
    }

    @Override
    public Document copy() {
        return new Document(this);   // delegate to the copy constructor
    }

    public void setTitle(String title) { this.title = title; }
    public void addSection(String s)   { this.sections.add(s); }

    @Override
    public String toString() {
        return "Document{title='" + title + "', sections=" + sections + "}";
    }
}
```

Using the prototype:

```java
import java.util.ArrayList;
import java.util.List;

public class Editor {
    public static void main(String[] args) {
        List<String> baseSections = new ArrayList<>(List.of("Intro", "Body"));
        Document master = new Document("Template", baseSections);

        // Clone the master instead of building from scratch.
        Document copy = master.copy();
        copy.setTitle("My Report");
        copy.addSection("Conclusion");   // only affects the copy

        System.out.println(master); // Document{title='Template', sections=[Intro, Body]}
        System.out.println(copy);   // Document{title='My Report', sections=[Intro, Body, Conclusion]}
    }
}
```

**Step-by-step explanation:**
- **`copy()` method:** the prototype knows how to copy itself.
- **Copy constructor:** a private constructor that takes an existing `Document` and copies its data. This is a clean Java idiom for cloning.
- **Deep copy of `sections`:** we create a `new ArrayList<>(...)`. Without this, the clone and master would share the same list, and adding "Conclusion" to the copy would also change the master -- a classic bug.
- **Result:** the master is untouched when we edit the copy, proving the clone is independent.

**A note on `Cloneable`.** Java's built-in `Cloneable` interface and `Object.clone()` are widely considered awkward and error-prone (they default to shallow copies and have confusing rules). Most experienced developers prefer **copy constructors** or **copy factory methods**, as shown above.

### 6.5 Class Diagram & Block Diagram

**Block diagram:**

```
        +---------------------+
        |    <<interface>>    |
        |      Prototype      |
        |      copy()         |
        +---------------------+
                  ^ implements
        +---------------------+   copy()    +---------------------+
        |      Document       |------------>|  Document (clone)   |
        | title, sections     | deep copy   | independent copy    |
        | copy()              |             +---------------------+
        +---------------------+
                  ^
                  | calls copy() instead of "new"
            +-----------+
            |  Client   |
            +-----------+
```

**In words:**
- A `Prototype` interface with a `copy()` method.
- A `Document` class implementing `Prototype`, containing fields and a private copy constructor.
- The client holds a reference to a `Prototype`/`Document` and calls `copy()` to get new instances, rather than calling `new` directly.

### 6.6 Advantages

- **Cheaper creation:** cloning can be far faster than building from scratch.
- **Avoids complex creation code:** no big factory hierarchies needed.
- **Easy variations:** clone a base configuration, then tweak.
- **Runtime flexibility:** you can add and remove prototypes dynamically.

### 6.7 Disadvantages

- **Deep vs shallow copy complexity:** copying objects with many nested references (or circular references) is tricky.
- **Cloning can be error-prone:** especially with Java's `Cloneable`.
- **Hidden coupling:** clones may unexpectedly share mutable state if copied shallowly.

### 6.8 Common Mistakes

- **Shallow copying when you needed deep copying**, causing the clone to share state with the original.
- **Relying on `Object.clone()`** without understanding its shallow-copy default.
- **Forgetting to copy nested mutable objects** (lists, maps, custom objects).

### 6.9 When NOT to Use It

- When objects are cheap and simple to create with `new`.
- When objects have no meaningful "copy" semantics.
- When deep copying would be more complex than just constructing fresh objects.

### 6.10 Real-World Use Cases

- **`Object.clone()`** and `Cloneable` in the JDK (the built-in mechanism).
- **Prototype-scoped beans in Spring** (a new instance per request, sometimes via copying a template).
- **Game development:** cloning enemy/template objects instead of rebuilding them.
- **Document/graphics editors:** duplicating shapes or document templates.
- **Caching:** keeping a pre-configured master object and handing out copies.

### Exercises

1. Create a `Shape` prototype with `copy()`, then clone a configured `Circle` and change only the clone's colour.
2. Add a nested mutable field (e.g., a list of points) and demonstrate the difference between shallow and deep copy.
3. Rewrite a clone using a copy constructor instead of `Cloneable`.

### Interview Questions

1. What problem does the Prototype pattern solve?
2. Explain the difference between shallow and deep copy with an example.
3. Why do many developers avoid Java's `Cloneable` interface?
4. When would cloning be preferable to constructing a new object?

### Common Pitfalls

- Shallow copies that secretly share mutable state.
- Misusing `Object.clone()` without handling deep copy.
- Forgetting nested objects when copying.

---

## Chapter 7: Adapter Pattern

**Category: Structural**

### 7.1 Introduction

**The problem it solves:** You have two pieces of code that *should* work together, but their interfaces do not match. Maybe you are using a third-party library, or some legacy code, whose methods do not fit what your application expects. You cannot (or do not want to) change either side. The **Adapter pattern** creates a "translator" object that converts one interface into another, so the two can collaborate.

**When should you use it?**
- When you want to use an existing class but its interface does not match what you need.
- When integrating third-party or legacy code you cannot modify.
- When you want to reuse a class that is otherwise incompatible.

### 7.2 Real-World Analogy

Think of a **power plug adapter** you take when travelling. Your laptop charger has a US plug, but the wall socket in Europe is shaped differently. You cannot change your charger or the wall. So you use a small adapter that sits between them, translating one shape into the other. The Adapter pattern is exactly this "in-between translator."

### 7.3 Concept Explanation

There are three players:
1. **Target** -- the interface your application expects to use.
2. **Adaptee** -- the existing class with an incompatible interface.
3. **Adapter** -- a class that implements the Target interface and internally calls the Adaptee, translating between them.

Your client code talks only to the **Target** interface. The **Adapter** quietly forwards those calls to the **Adaptee** in whatever form it understands.

### 7.4 Java Implementation

Imagine your app expects a `MediaPlayer` that can `play(...)`, but you have a third-party `AdvancedAudioLibrary` with differently named methods.

```java
// Step 1: TARGET -- the interface your application expects.
public interface MediaPlayer {
    void play(String fileName);
}
```

```java
// Step 2: ADAPTEE -- an existing (e.g., third-party) class we cannot change.
public class AdvancedAudioLibrary {
    public void playMp3File(String name) {
        System.out.println("Playing MP3: " + name);
    }
}
```

```java
// Step 3: ADAPTER -- implements the Target and delegates to the Adaptee.
public class AudioAdapter implements MediaPlayer {

    private final AdvancedAudioLibrary library; // wrap the incompatible class

    public AudioAdapter(AdvancedAudioLibrary library) {
        this.library = library;
    }

    @Override
    public void play(String fileName) {
        // Translate the "play" call into the adaptee's method.
        library.playMp3File(fileName);
    }
}
```

```java
// Step 4: The client uses ONLY the MediaPlayer interface.
public class MusicApp {
    public static void main(String[] args) {
        AdvancedAudioLibrary thirdParty = new AdvancedAudioLibrary();
        MediaPlayer player = new AudioAdapter(thirdParty);  // wrap it

        player.play("song.mp3");   // client speaks MediaPlayer; adapter translates
    }
}
```

**Step-by-step explanation:**
- **`MediaPlayer` (Target):** what the client wants to use.
- **`AdvancedAudioLibrary` (Adaptee):** useful, but its method is `playMp3File`, not `play`. We cannot edit it.
- **`AudioAdapter`:** implements `MediaPlayer`, holds an `AdvancedAudioLibrary` inside, and forwards `play(...)` to `playMp3File(...)`. This is the translation.
- **`MusicApp` (client):** only knows `MediaPlayer`. It never sees the mismatch -- the adapter hides it.

This is called **object adapter** (it uses composition -- holding the adaptee as a field). There is also a *class adapter* using inheritance, but composition is preferred in Java since Java has single inheritance.

### 7.5 Class Diagram & Block Diagram

**Block diagram:**

```
  +----------+ uses +-------------------+        +-----------------------+
  |  Client  |----->|   <<interface>>   |        | AdvancedAudioLibrary  |
  +----------+      |    MediaPlayer    |        |      (Adaptee)        |
                    |    play(file)     |        |   playMp3File(name)   |
                    +-------------------+        +-----------------------+
                             ^                              ^
                  implements |                              | calls (has-a)
                      +-------------------+                 |
                      |   AudioAdapter    |-----------------+
                      |   play(file) {    |
                      |     lib.playMp3.. |
                      |   }               |
                      +-------------------+

  The Adapter TRANSLATES play()  ->  playMp3File().
```

**In words:**
- A `MediaPlayer` interface (Target).
- An `AudioAdapter` class implementing `MediaPlayer`, with an arrow ("has-a") pointing to `AdvancedAudioLibrary` (Adaptee).
- The client points to `MediaPlayer`. Inside, the adapter forwards calls to the adaptee.

Picture the adapter as a bridge: client -> Target interface -> Adapter -> Adaptee.

### 7.6 Advantages

- **Reuse incompatible code:** integrate libraries/legacy code without modifying them.
- **Separation of concerns:** translation logic lives in one place.
- **Open/Closed friendly:** add new adapters without changing existing code.
- **Client stays clean:** it only knows the Target interface.

### 7.7 Disadvantages

- **Extra layer:** adds a class and a small amount of indirection.
- **Can hide complexity:** too many adapters can make the system harder to follow.
- **Not a fix for bad design:** sometimes the real answer is to refactor, not adapt.

### 7.8 Common Mistakes

- **Putting business logic in the adapter** -- it should only translate, not add features.
- **Creating overly broad adapters** that try to translate too much at once.
- **Confusing Adapter with Decorator** -- Adapter *changes* an interface; Decorator *adds behaviour* while keeping the same interface.

### 7.9 When NOT to Use It

- When you can simply change one of the interfaces to match (then no adapter is needed).
- When the two interfaces are fundamentally incompatible in meaning (an adapter would be a lie).
- When it adds indirection without real benefit.

### 7.10 Real-World Use Cases

- **`java.util.Arrays.asList(...)`** -- adapts an array to a `List` interface.
- **`InputStreamReader`** -- adapts a byte stream (`InputStream`) to a character stream (`Reader`).
- **`java.io.OutputStreamWriter`** -- similar byte-to-character adapter.
- **Spring MVC `HandlerAdapter`** -- adapts various handler types to a common interface.
- **Wrapping third-party SDKs** behind your own clean interface.

### Exercises

1. You have a `LegacyRectangle` class with `drawShape(x1,y1,x2,y2)`. Write an adapter so it works with a `Shape` interface that has `draw()`.
2. Wrap a third-party JSON library behind your own `JsonSerializer` interface using an adapter.
3. Explain how `InputStreamReader` is an example of this pattern.

### Interview Questions

1. What problem does the Adapter pattern solve?
2. What is the difference between an object adapter and a class adapter?
3. How does Adapter differ from Decorator?
4. Give two examples of adapters in the Java standard library.

### Common Pitfalls

- Adding behaviour instead of pure translation.
- Confusing it with Decorator or Facade.
- Using an adapter to paper over a design that should be refactored.

---

## Chapter 8: Decorator Pattern

**Category: Structural**

### 8.1 Introduction

**The problem it solves:** You want to add new behaviour or responsibilities to an object **dynamically**, without changing its class and without creating an explosion of subclasses. Imagine a coffee shop: a coffee can have milk, sugar, whipped cream, caramel -- in any combination. Creating a subclass for every combination (`CoffeeWithMilkAndSugar`, `CoffeeWithMilkAndCaramel`, ...) would be insane. The **Decorator pattern** lets you "wrap" an object in layers, each layer adding a feature, while everyone still treats it as the same type.

**When should you use it?**
- When you want to add responsibilities to individual objects, not the whole class.
- When extension by subclassing would cause a combinatorial explosion of classes.
- When you want to add/remove features at runtime by stacking wrappers.

### 8.2 Real-World Analogy

Think of **getting dressed.** You start with your body, then add a shirt, then a sweater, then a jacket. Each layer adds warmth (behaviour) but you are still "you" underneath. You can add or remove layers freely depending on the weather. Each clothing layer is a **decorator** wrapping what is underneath.

Another perfect analogy: ordering coffee and adding toppings one by one, each adding to the cost and description.

### 8.3 Concept Explanation

The key trick: a decorator **implements the same interface** as the object it wraps, and **holds a reference** to that wrapped object. When a method is called, the decorator does its own little bit of extra work and then delegates to the wrapped object. Because every layer shares the same interface, you can stack them in any order, and the client cannot tell how many layers there are.

This is a textbook example of **"favour composition over inheritance"** -- you compose behaviour by wrapping, instead of locking it in via subclassing.

### 8.4 Java Implementation

Let us build a coffee ordering system where you can add condiments dynamically.

```java
// Step 1: The common interface (the "component").
public interface Coffee {
    String getDescription();
    double getCost();
}
```

```java
// Step 2: A concrete base component -- plain coffee.
public class SimpleCoffee implements Coffee {
    public String getDescription() { return "Coffee"; }
    public double getCost()        { return 2.00; }
}
```

```java
// Step 3: The abstract decorator. It implements Coffee AND wraps a Coffee.
public abstract class CoffeeDecorator implements Coffee {
    protected final Coffee inner;   // the wrapped object

    public CoffeeDecorator(Coffee inner) {
        this.inner = inner;
    }
    // By default, delegate to the wrapped object.
    public String getDescription() { return inner.getDescription(); }
    public double getCost()        { return inner.getCost(); }
}
```

```java
// Step 4: Concrete decorators -- each adds one feature.
public class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee inner) { super(inner); }
    @Override
    public String getDescription() { return inner.getDescription() + " + Milk"; }
    @Override
    public double getCost()        { return inner.getCost() + 0.50; }
}

public class SugarDecorator extends CoffeeDecorator {
    public SugarDecorator(Coffee inner) { super(inner); }
    @Override
    public String getDescription() { return inner.getDescription() + " + Sugar"; }
    @Override
    public double getCost()        { return inner.getCost() + 0.25; }
}
```

```java
// Step 5: The client stacks decorators in any combination.
public class CoffeeShop {
    public static void main(String[] args) {
        Coffee order = new SimpleCoffee();        // start plain
        order = new MilkDecorator(order);         // wrap with milk
        order = new SugarDecorator(order);        // wrap with sugar

        System.out.println(order.getDescription()); // Coffee + Milk + Sugar
        System.out.println("$" + order.getCost());  // $2.75
    }
}
```

**Step-by-step explanation:**
- **`Coffee` interface:** every component and decorator shares it, so they are interchangeable.
- **`SimpleCoffee`:** the plain object we will decorate.
- **`CoffeeDecorator`:** the abstract base that *is* a `Coffee` and *holds* a `Coffee`. By default it just forwards calls to the wrapped object.
- **`MilkDecorator` / `SugarDecorator`:** each overrides the methods to add its bit (extra text, extra cost) and then calls the inner object.
- **Stacking:** `new SugarDecorator(new MilkDecorator(new SimpleCoffee()))` builds layers. Calling `getCost()` ripples inward: sugar adds 0.25 to (milk adds 0.50 to (coffee's 2.00)) = 2.75.

### 8.5 Class Diagram & Block Diagram

**Block diagram:**

```
            +----------------------+
            |    <<interface>>     |
            |        Coffee        |
            | getCost()/getDesc()  |
            +----------------------+
              ^                  ^
   implements |                  | implements + HAS-A (wraps a Coffee)
   +---------------+    +--------------------------+
   | SimpleCoffee  |    |  CoffeeDecorator (abs.)  |---+
   +---------------+    +--------------------------+   | wraps a Coffee
                              ^             ^          |
                    +----------------+ +-----------------+
                    | MilkDecorator  | | SugarDecorator  |
                    +----------------+ +-----------------+

  Stacking (like nested Russian dolls):
    Sugar( Milk( SimpleCoffee ) )  -> getCost() ripples inward.
```

**In words:**
- A `Coffee` interface at the top.
- `SimpleCoffee` implements `Coffee`.
- An abstract `CoffeeDecorator` that *both* implements `Coffee` *and* has a reference to a `Coffee` (this dual relationship is the heart of the pattern).
- `MilkDecorator` and `SugarDecorator` extend `CoffeeDecorator`.

Picture nested boxes (like Russian dolls): Sugar wraps Milk wraps SimpleCoffee, and all of them are `Coffee`.

### 8.6 Advantages

- **Flexible, runtime composition:** add/remove features by stacking wrappers.
- **Avoids subclass explosion:** no class per combination.
- **Single Responsibility:** each decorator does one small thing.
- **Open/Closed:** add new decorators without touching existing classes.

### 8.7 Disadvantages

- **Many small classes:** lots of tiny decorator classes can be hard to navigate.
- **Order can matter:** stacking in different orders may produce different results.
- **Harder debugging:** a deep stack of wrappers can be tricky to trace.
- **Identity issues:** the decorated object is not the same instance as the original.

### 8.8 Common Mistakes

- **Forgetting to delegate** to the wrapped object, breaking the chain.
- **Making decorators depend on a concrete class** instead of the shared interface.
- **Confusing Decorator with Inheritance** -- decorators add behaviour at runtime; subclassing fixes it at compile time.
- **Overusing it** when a couple of subclasses would be simpler.

### 8.9 When NOT to Use It

- When the set of behaviours is small and fixed -- a subclass or a simple flag may be clearer.
- When the order/stacking complexity outweighs the flexibility.
- When you actually need to *change* the interface (that is Adapter, not Decorator).

### 8.10 Real-World Use Cases

- **Java I/O streams** -- the classic example: `new BufferedReader(new InputStreamReader(new FileInputStream(file)))` stacks decorators that each add a capability (buffering, char decoding).
- **`java.util.Collections.unmodifiableList(...)` / `synchronizedList(...)`** -- wrap a list to add behaviour.
- **Servlet request/response wrappers** (`HttpServletRequestWrapper`).
- **GUI frameworks** -- adding borders, scrollbars to components.

### Exercises

1. Build a `Pizza` component and decorators for `ExtraCheese` and `Olives`, computing total cost.
2. Demonstrate that wrapping in different orders still works and produces a sensible description.
3. Explain how `BufferedReader` and `InputStreamReader` together illustrate the Decorator pattern.

### Interview Questions

1. What problem does the Decorator pattern solve, and how does it avoid subclass explosion?
2. Why must a decorator implement the same interface as the object it wraps?
3. How does Decorator differ from Adapter?
4. Give a real example of the Decorator pattern in the Java I/O library.

### Common Pitfalls

- Forgetting to call the wrapped object inside a decorator.
- Depending on concrete classes rather than the shared interface.
- Stacking so many decorators that the code becomes hard to follow.

---

## Chapter 9: Observer Pattern

**Category: Behavioral**

### 9.1 Introduction

**The problem it solves:** You have one object whose state changes, and several other objects that need to react whenever that change happens. You do not want the changing object to be tightly tied to all those dependents (it should not need to know their exact classes). The **Observer pattern** defines a one-to-many relationship: when one object (the **subject**) changes, all its registered dependents (the **observers**) are notified automatically.

**When should you use it?**
- When a change in one object must trigger updates in many others.
- When you do not want the subject to know the concrete types of its dependents.
- When you need a publish/subscribe ("pub/sub") style of communication.

### 9.2 Real-World Analogy

Think of a **YouTube channel and its subscribers.** When a creator (the subject) uploads a new video, every subscriber (observer) gets a notification automatically. The creator does not call each fan personally; they just publish, and everyone subscribed is informed. Subscribers can join or leave anytime, and the creator does not need to know who they are individually.

Another analogy: a **newspaper subscription** -- the publisher prints once, and all subscribers receive a copy.

### 9.3 Concept Explanation

There are two roles:
1. **Subject (Observable):** holds the state and a list of observers. It offers methods to `subscribe`, `unsubscribe`, and `notify`.
2. **Observers:** register with the subject and implement an `update(...)` method that the subject calls when something changes.

When the subject's state changes, it loops through its observers and calls each one's `update(...)`. The subject only knows observers through a common interface, so it stays loosely coupled.

### 9.4 Java Implementation

Let us build a weather station that notifies multiple display panels when the temperature changes.

```java
// Step 1: The observer interface -- what every observer must implement.
public interface Observer {
    void update(float temperature);
}
```

```java
// Step 2: The subject interface -- manage and notify observers.
public interface Subject {
    void subscribe(Observer o);
    void unsubscribe(Observer o);
    void notifyObservers();
}
```

```java
import java.util.ArrayList;
import java.util.List;

// Step 3: The concrete subject holds state and a list of observers.
public class WeatherStation implements Subject {
    private final List<Observer> observers = new ArrayList<>();
    private float temperature;

    public void subscribe(Observer o)   { observers.add(o); }
    public void unsubscribe(Observer o) { observers.remove(o); }

    public void notifyObservers() {
        // Tell every observer about the new state.
        for (Observer o : observers) {
            o.update(temperature);
        }
    }

    // When state changes, notify everyone automatically.
    public void setTemperature(float temperature) {
        this.temperature = temperature;
        notifyObservers();
    }
}
```

```java
// Step 4: Concrete observers react to updates.
public class PhoneDisplay implements Observer {
    private final String name;
    public PhoneDisplay(String name) { this.name = name; }
    public void update(float temperature) {
        System.out.println(name + " shows temperature: " + temperature + "C");
    }
}
```

```java
// Step 5: Wiring it together.
public class WeatherApp {
    public static void main(String[] args) {
        WeatherStation station = new WeatherStation();

        Observer alice = new PhoneDisplay("Alice's phone");
        Observer bob   = new PhoneDisplay("Bob's phone");

        station.subscribe(alice);
        station.subscribe(bob);

        station.setTemperature(25.0f);  // both get notified
        // Alice's phone shows temperature: 25.0C
        // Bob's phone shows temperature: 25.0C

        station.unsubscribe(bob);
        station.setTemperature(30.0f);  // only Alice now
        // Alice's phone shows temperature: 30.0C
    }
}
```

**Step-by-step explanation:**
- **`Observer` interface:** any class that wants updates implements `update(...)`.
- **`Subject` interface:** defines subscribe/unsubscribe/notify so subjects are interchangeable.
- **`WeatherStation`:** keeps a list of observers. When `setTemperature` is called, it updates state and calls `notifyObservers()`, which loops and calls `update` on each.
- **`PhoneDisplay`:** reacts to updates. Multiple displays can subscribe.
- **Loose coupling:** the station only knows the `Observer` interface -- it has no idea about phones, laptops, or anything specific. New observer types can be added without changing the station.

### 9.5 Class Diagram & Block Diagram

**Block diagram:**

```
   +------------------------+         +---------------------+
   |    <<interface>>       |         |    <<interface>>    |
   |       Subject          |         |      Observer       |
   | subscribe/unsubscribe  |         |     update(temp)    |
   | notifyObservers()      |         +---------------------+
   +------------------------+              ^    ^    ^
            ^ implements                   |    |    |  (many observers)
   +------------------------+ notifies +---------+ +---------+
   |    WeatherStation      |--------->| Phone A | | Phone B |  ...
   | holds List<Observer>   |   ALL    +---------+ +---------+
   +------------------------+

  A state change in the Subject => every Observer's update() is called.
```

**In words:**
- A `Subject` interface (subscribe/unsubscribe/notify) and an `Observer` interface (update).
- `WeatherStation` implements `Subject` and holds a list of `Observer` (one-to-many association).
- `PhoneDisplay` implements `Observer`.
- An arrow from `WeatherStation` to `Observer` labelled "notifies many".

Picture one subject in the centre with arrows fanning out to many observers.

### 9.6 Advantages

- **Loose coupling:** subject and observers depend only on interfaces.
- **Dynamic relationships:** observers can subscribe/unsubscribe at runtime.
- **Broadcast communication:** one change updates many objects automatically.
- **Open/Closed:** add new observer types without changing the subject.

### 9.7 Disadvantages

- **Unexpected update cascades:** one change can trigger many updates, sometimes chaining unpredictably.
- **Memory leaks:** forgetting to unsubscribe keeps observers alive (the "lapsed listener" problem).
- **Ordering not guaranteed:** observers may be notified in an unpredictable order.
- **Debugging difficulty:** the flow of notifications can be hard to follow.

### 9.8 Common Mistakes

- **Forgetting to unsubscribe**, causing memory leaks.
- **Doing heavy work inside `update()`**, slowing down notifications for everyone.
- **Assuming a notification order** that is not guaranteed.
- **Modifying the observer list while notifying** (can cause concurrency errors).

### 9.9 When NOT to Use It

- When there is only one observer and a direct method call is simpler.
- When the update cascades become too complex to reason about.
- When real-time ordering and delivery guarantees are critical (consider a proper messaging system instead).

### 9.10 Real-World Use Cases

- **GUI event listeners** -- button click listeners are observers (`addActionListener`).
- **`java.util.Observer`/`Observable`** (now deprecated, but historically the built-in version).
- **`PropertyChangeListener`** in JavaBeans.
- **Reactive programming** (RxJava, Project Reactor) is built on this idea at scale.
- **Spring's `ApplicationListener`** and event publishing.
- **Message brokers / pub-sub systems** conceptually follow this pattern.

### Exercises

1. Build a `NewsAgency` subject that notifies `NewsChannel` observers when breaking news arrives.
2. Add the ability to unsubscribe and prove a removed observer no longer receives updates.
3. Add a second observer type (e.g., `EmailSubscriber`) without modifying the subject.

### Interview Questions

1. What problem does the Observer pattern solve?
2. How does the Observer pattern achieve loose coupling?
3. What is the "lapsed listener" problem and how do you avoid it?
4. How does Observer relate to modern reactive programming?

### Common Pitfalls

- Memory leaks from never unsubscribing.
- Heavy or blocking work inside `update()`.
- Relying on a specific notification order.

---

## Chapter 10: Strategy Pattern

**Category: Behavioral**

### 10.1 Introduction

**The problem it solves:** You have multiple ways to do the same task (different algorithms), and you want to choose between them -- possibly at runtime -- without filling your code with `if/else` or `switch` statements. For example, sorting with different comparison rules, calculating shipping cost by different carriers, or paying by different methods. The **Strategy pattern** puts each algorithm in its own class behind a common interface, so they can be swapped freely.

**When should you use it?**
- When you have several interchangeable ways to perform a task.
- When you want to choose the algorithm at runtime.
- When you want to eliminate big conditional blocks that select behaviour.

### 10.2 Real-World Analogy

Think of **navigation apps.** When you want directions, you can choose a strategy: "fastest route by car," "route by walking," "route by public transport," or "avoid tolls." The goal (get from A to B) is the same, but the *strategy* for computing the route differs. You pick one, and you can switch anytime. Each routing method is an interchangeable strategy.

Another analogy: choosing a **payment method** at checkout -- credit card, PayPal, or cash. Same goal (pay), different strategies.

### 10.3 Concept Explanation

The pattern has three parts:
1. A **strategy interface** declaring the method that all algorithms share (e.g., `pay(amount)`).
2. **Concrete strategies** -- each a separate class implementing one algorithm.
3. A **context** -- the object that uses a strategy. It holds a reference to a strategy and delegates the work to it, without knowing which concrete one it is.

Switching behaviour is as easy as handing the context a different strategy object. This embodies "encapsulate what varies" -- the varying algorithm is isolated into its own class.

### 10.4 Java Implementation

Let us build a checkout that supports multiple payment strategies.

```java
// Step 1: The strategy interface -- the common contract.
public interface PaymentStrategy {
    void pay(double amount);
}
```

```java
// Step 2: Concrete strategies -- each is one payment algorithm.
public class CreditCardPayment implements PaymentStrategy {
    private final String cardNumber;
    public CreditCardPayment(String cardNumber) { this.cardNumber = cardNumber; }
    public void pay(double amount) {
        System.out.println("Paid $" + amount + " using credit card " + cardNumber);
    }
}

public class PayPalPayment implements PaymentStrategy {
    private final String email;
    public PayPalPayment(String email) { this.email = email; }
    public void pay(double amount) {
        System.out.println("Paid $" + amount + " using PayPal account " + email);
    }
}
```

```java
// Step 3: The context holds a strategy and delegates to it.
public class ShoppingCart {
    private PaymentStrategy paymentStrategy;   // the current strategy

    // The strategy can be set or changed at runtime.
    public void setPaymentStrategy(PaymentStrategy strategy) {
        this.paymentStrategy = strategy;
    }

    public void checkout(double amount) {
        if (paymentStrategy == null) {
            throw new IllegalStateException("Choose a payment method first.");
        }
        paymentStrategy.pay(amount);   // delegate -- no if/else by type!
    }
}
```

```java
// Step 4: The client picks a strategy, and can switch it freely.
public class Store {
    public static void main(String[] args) {
        ShoppingCart cart = new ShoppingCart();

        cart.setPaymentStrategy(new CreditCardPayment("1234-5678"));
        cart.checkout(100.0);   // Paid $100.0 using credit card 1234-5678

        // Switch strategy at runtime -- no code change in the cart.
        cart.setPaymentStrategy(new PayPalPayment("me@example.com"));
        cart.checkout(50.0);    // Paid $50.0 using PayPal account me@example.com
    }
}
```

**Step-by-step explanation:**
- **`PaymentStrategy` interface:** all payment methods share `pay(amount)`.
- **Concrete strategies:** each payment type is its own class. Adding a new method (e.g., crypto) means writing one new class -- no existing code changes.
- **`ShoppingCart` (context):** holds a `PaymentStrategy` and calls `pay(...)` on it. Notice there is **no `if (type == creditCard)...`** -- the conditional logic disappears.
- **Runtime switching:** the client can swap strategies whenever it likes.

**Modern Java tip:** Because `PaymentStrategy` is a functional interface (one method), you can often use a lambda: `cart.setPaymentStrategy(amount -> System.out.println("Paid " + amount))`. Strategy and lambdas pair beautifully.

### 10.5 Class Diagram & Block Diagram

**Block diagram:**

```
   +-------------------+ has-a  +---------------------+
   |   ShoppingCart    |------->|    <<interface>>    |
   |    (Context)      |        |   PaymentStrategy   |
   | setStrategy(s)    |        |     pay(amount)     |
   | checkout()        |        +---------------------+
   +-------------------+            ^               ^
                         implements |               | implements
                    +-------------------+   +----------------+
                    | CreditCardPayment |   | PayPalPayment  |
                    +-------------------+   +----------------+

  Swap the plugged-in strategy at runtime; NO if/else-by-type needed.
```

**In words:**
- A `PaymentStrategy` interface with `pay(amount)`.
- `CreditCardPayment` and `PayPalPayment` implement it.
- A `ShoppingCart` context with a `PaymentStrategy` field (a "has-a" arrow to the interface) and a `checkout()` method that delegates.

Picture the context pointing to the *interface*, with several concrete strategies plugged in behind it like interchangeable cartridges.

### 10.6 Advantages

- **Eliminates conditionals:** no big `switch`/`if-else` selecting behaviour by type.
- **Open/Closed:** add new strategies without touching the context.
- **Runtime flexibility:** swap algorithms on the fly.
- **Testable:** each strategy can be tested in isolation.

### 10.7 Disadvantages

- **More classes:** each algorithm is its own class.
- **Client must know the strategies:** someone has to choose which one to use.
- **Overkill for one or two simple cases:** a basic `if` may be clearer.

### 10.8 Common Mistakes

- **Keeping conditionals anyway** -- defeating the purpose by selecting strategies with a giant switch in one place (sometimes acceptable, but watch it).
- **Storing state in strategies** that should be stateless, causing bugs when shared.
- **Confusing Strategy with State pattern** -- they look similar structurally but have different intents (Strategy = pick an algorithm; State = behaviour changes as internal state changes).

### 10.9 When NOT to Use It

- When there is only one algorithm and no plan for more.
- When the variations are tiny and a simple conditional is clearer.
- When the overhead of many classes is not justified.

### 10.10 Real-World Use Cases

- **`Comparator` in Java** -- `Collections.sort(list, comparator)` passes a sorting *strategy*. This is the canonical example.
- **`java.util.function` interfaces** (e.g., `Function`, `Predicate`) used as strategies via lambdas.
- **Payment processing, shipping cost, tax calculation** -- swapping algorithms.
- **Compression libraries** -- choosing zip vs gzip vs lz4 at runtime.
- **Spring's `Resource` loading and many pluggable behaviours.**

### Exercises

1. Build a `TextFormatter` context with strategies `UpperCaseStrategy`, `LowerCaseStrategy`, and `TitleCaseStrategy`.
2. Replace one strategy with a lambda to see how functional interfaces simplify Strategy.
3. Implement shipping-cost strategies (`Standard`, `Express`) and switch them at runtime.

### Interview Questions

1. What problem does the Strategy pattern solve, and how does it remove conditionals?
2. How does Strategy support the Open/Closed Principle?
3. What is the difference between the Strategy and State patterns?
4. How do Java's `Comparator` and lambdas relate to Strategy?

### Common Pitfalls

- Reintroducing big conditionals to select strategies.
- Making strategies stateful when they should be stateless.
- Using Strategy where a single simple method would do.

---

## Chapter 11: Command Pattern

**Category: Behavioral**

### 11.1 Introduction

**The problem it solves:** You want to **turn a request (an action) into a standalone object.** Why? So you can store it, pass it around, queue it, log it, schedule it, or undo it. Instead of calling a method directly, you wrap "what to do" into a command object. This decouples the thing that *triggers* an action (e.g., a button) from the thing that *performs* it (e.g., a light). The **Command pattern** encapsulates a request as an object.

**When should you use it?**
- When you want to parameterise objects with actions (e.g., a button that can be configured to do anything).
- When you need to queue, schedule, or log operations.
- When you need **undo/redo** functionality.
- When you want to decouple the invoker of an action from its receiver.

### 11.2 Real-World Analogy

Think of a **restaurant order ticket.** When you order, the waiter writes your request on a ticket and places it in the kitchen queue. The ticket is a *command object*: it captures "what to make" independently of who cooks it. The waiter (invoker) does not cook; the chef (receiver) does. Tickets can be queued, reordered, and tracked. The order is separated from its execution.

Another perfect analogy: a **TV remote control.** Each button is configured with a command (turn on, volume up). Pressing the button executes its command, but the button does not know how the TV works internally.

### 11.3 Concept Explanation

There are four roles:
1. **Command interface:** declares an `execute()` method (and often `undo()`).
2. **Concrete commands:** each wraps a specific action and the object it acts on (the receiver). `execute()` calls the right method on the receiver.
3. **Receiver:** the object that actually does the work (e.g., a `Light`).
4. **Invoker:** triggers commands (e.g., a `RemoteControl` button). It holds commands and calls `execute()` without knowing the details.

Because actions are objects, you can store them in lists (queues), keep a history (for undo), or pass them as parameters.

### 11.4 Java Implementation

Let us build a remote control that can operate devices and support undo.

```java
// Step 1: The command interface.
public interface Command {
    void execute();
    void undo();
}
```

```java
// Step 2: The receiver -- the object that does the real work.
public class Light {
    private final String location;
    public Light(String location) { this.location = location; }
    public void on()  { System.out.println(location + " light is ON"); }
    public void off() { System.out.println(location + " light is OFF"); }
}
```

```java
// Step 3: Concrete commands wrap a receiver and an action.
public class LightOnCommand implements Command {
    private final Light light;
    public LightOnCommand(Light light) { this.light = light; }
    public void execute() { light.on(); }   // do the action
    public void undo()    { light.off(); }  // reverse the action
}

public class LightOffCommand implements Command {
    private final Light light;
    public LightOffCommand(Light light) { this.light = light; }
    public void execute() { light.off(); }
    public void undo()    { light.on(); }
}
```

```java
// Step 4: The invoker triggers commands and remembers the last one for undo.
public class RemoteControl {
    private Command lastCommand;

    public void pressButton(Command command) {
        command.execute();        // run it
        lastCommand = command;    // remember it for undo
    }

    public void pressUndo() {
        if (lastCommand != null) {
            lastCommand.undo();
        }
    }
}
```

```java
// Step 5: Wiring everything together.
public class SmartHome {
    public static void main(String[] args) {
        Light livingRoom = new Light("Living Room");      // receiver

        Command lightOn  = new LightOnCommand(livingRoom);
        Command lightOff = new LightOffCommand(livingRoom);

        RemoteControl remote = new RemoteControl();        // invoker

        remote.pressButton(lightOn);   // Living Room light is ON
        remote.pressButton(lightOff);  // Living Room light is OFF
        remote.pressUndo();            // Living Room light is ON  (undo!)
    }
}
```

**Step-by-step explanation:**
- **`Command` interface:** every action shares `execute()` and `undo()`.
- **`Light` (receiver):** knows how to turn itself on/off. It does the real work.
- **Concrete commands:** each holds a `Light` and translates `execute()`/`undo()` into the right calls. The action is now an *object* you can store.
- **`RemoteControl` (invoker):** runs whatever command it is given and keeps the last one so it can undo. It has no idea what the command actually does -- total decoupling.
- **Undo:** because each command knows how to reverse itself, undo is straightforward. Storing commands in a stack would give you multi-level undo.

### 11.5 Class Diagram & Block Diagram

**Block diagram:**

```
  +----------------+ holds +-------------------+ acts on +-------------+
  | RemoteControl  |------>|   <<interface>>   |-------->|    Light    |
  |   (Invoker)    |       |      Command      | (has-a) | (Receiver)  |
  | pressButton()  |       | execute()/undo()  |         | on()/off()  |
  | pressUndo()    |       +-------------------+         +-------------+
  +----------------+           ^             ^
                    implements |             | implements
                  +----------------+  +------------------+
                  | LightOnCommand |  | LightOffCommand  |
                  +----------------+  +------------------+

  Invoker triggers execute(); the Receiver does the real work.
  Each command knows its own undo() -> easy undo/redo.
```

**In words:**
- A `Command` interface with `execute()` and `undo()`.
- Concrete commands `LightOnCommand`, `LightOffCommand` implement it, each holding a reference to a `Light` (receiver).
- A `RemoteControl` (invoker) holds `Command` references and calls `execute()`/`undo()`.
- `Light` is the receiver doing the real work.

Picture: Invoker -> Command (interface) -> Receiver. The invoker and receiver never touch directly.

### 11.6 Advantages

- **Decoupling:** the invoker is fully separated from the receiver.
- **Undo/redo:** commands can reverse themselves and be stored in history.
- **Queuing/scheduling/logging:** since actions are objects, you can store and replay them.
- **Composability:** combine commands into "macro" commands.
- **Open/Closed:** add new commands without changing existing code.

### 11.7 Disadvantages

- **Many small classes:** one class per action can be a lot.
- **Indirection:** more layers to follow when reading the code.
- **Overkill for simple direct calls.**

### 11.8 Common Mistakes

- **Putting business logic inside the command** instead of delegating to the receiver -- the command should orchestrate, the receiver should do the work.
- **Forgetting to implement `undo()` correctly**, breaking the history.
- **Making commands carry too much state**, complicating reuse.

### 11.9 When NOT to Use It

- When a simple direct method call is enough and you do not need undo/queue/log.
- When the extra classes add complexity without real benefit.
- When actions never need to be stored, reversed, or parameterised.

### 11.10 Real-World Use Cases

- **`java.lang.Runnable`** -- a command object you hand to a thread or executor.
- **`java.util.concurrent` executors** -- you submit tasks (commands) to a queue.
- **GUI buttons/menu items** -- each configured with an action (Swing `Action`).
- **Undo/redo in editors** (text editors, image editors, IDEs).
- **Job/task scheduling systems** and **transaction logs** that replay commands.
- **Spring's `@Async` tasks** and message handlers.

### Exercises

1. Add a `Fan` receiver with `high()`, `off()`, and create `FanHighCommand` and `FanOffCommand`.
2. Extend `RemoteControl` to keep a *stack* of commands for multi-level undo.
3. Create a `MacroCommand` that executes a list of commands in order (e.g., "movie mode": dim lights + turn on TV).

### Interview Questions

1. What problem does the Command pattern solve?
2. How does the Command pattern enable undo/redo functionality?
3. What are the four roles in the Command pattern?
4. How does `Runnable` relate to the Command pattern?

### Common Pitfalls

- Putting real work in the command instead of the receiver.
- Implementing `undo()` incorrectly or not at all.
- Overusing it for simple, direct operations.

---

## Chapter 12: Bringing It All Together

You have now met all 10 patterns. This final chapter ties everything together: a side-by-side comparison, a practical guide to choosing the right pattern, and focused tips for interviews.

### 12.1 Summary Comparison of All 10 Patterns

```
+--------------------+-------------+--------------------------------------------------+
| Pattern            | Category    | One-line purpose                                 |
+--------------------+-------------+--------------------------------------------------+
| Singleton          | Creational  | Guarantee exactly one instance + global access   |
| Factory Method     | Creational  | Create objects via a method, hide concrete class |
| Abstract Factory   | Creational  | Create FAMILIES of related, matching objects     |
| Builder            | Creational  | Build complex objects step by step (readable)    |
| Prototype          | Creational  | Create new objects by cloning an existing one    |
| Adapter            | Structural  | Translate one interface into another             |
| Decorator          | Structural  | Add behaviour by wrapping, no subclass explosion |
| Observer           | Behavioral  | Notify many dependents when one object changes   |
| Strategy           | Behavioral  | Swap interchangeable algorithms at runtime       |
| Command            | Behavioral  | Turn a request into an object (queue/undo/log)   |
+--------------------+-------------+--------------------------------------------------+
```

**Quick analogy recap (great for memory):**
```
  Singleton        -> The one and only President
  Factory Method   -> Ordering a pizza (kitchen decides how)
  Abstract Factory -> A matching furniture set (all same style)
  Builder          -> Build-your-own burger, step by step
  Prototype        -> A photocopier making copies of a master
  Adapter          -> A travel power-plug adapter
  Decorator        -> Adding layers of clothing
  Observer         -> YouTube channel notifying subscribers
  Strategy         -> Navigation app picking a route type
  Command          -> A restaurant order ticket / TV remote button
```

### 12.2 How to Choose the Right Pattern

The biggest skill is recognising *which problem you have*, because the problem points to the pattern. Use this decision guide:

**If your problem is about CREATING objects:**
```
  Need exactly one shared instance?              -> Singleton
  Need to hide which concrete class is created?  -> Factory Method
  Need families of matching objects together?    -> Abstract Factory
  Object has many optional fields / want it
    immutable and readable to construct?         -> Builder
  Creating from scratch is expensive and you
    already have a similar object?               -> Prototype
```

**If your problem is about STRUCTURING / connecting objects:**
```
  Two incompatible interfaces must work together? -> Adapter
  Want to add features dynamically by wrapping?    -> Decorator
```

**If your problem is about BEHAVIOUR / communication:**
```
  One change must update many objects?            -> Observer
  Multiple interchangeable algorithms?            -> Strategy
  Want to store/queue/undo/log actions?           -> Command
```

**A simple mental process:**
1. **Name the problem** in plain words ("I have many ways to calculate shipping").
2. **Match it to a category** (creation? structure? behaviour?).
3. **Pick the pattern** whose purpose matches.
4. **Sanity check:** would simpler code work just as well? If yes, prefer simple code.

**Patterns that are often confused -- know the difference:**
- **Factory Method vs Abstract Factory:** one product vs a *family* of products.
- **Adapter vs Decorator:** Adapter *changes* an interface; Decorator *keeps* the interface but adds behaviour.
- **Adapter vs Facade:** Adapter makes one thing fit an expected interface; Facade simplifies a whole subsystem behind one easy interface.
- **Strategy vs State:** Strategy picks an algorithm you choose; State changes behaviour automatically as internal state changes.
- **Strategy vs Command:** Strategy is about *how* to do something (algorithm); Command is about *what* to do, packaged as a storable object.

### 12.3 Tips for Interviews

Design patterns come up constantly in interviews. Here is how to shine:

**1. Lead with the problem, not the code.** When asked about a pattern, first state *what problem it solves*. For example: "Strategy solves the problem of having multiple interchangeable algorithms without cluttering code with conditionals." This shows real understanding.

**2. Use a crisp real-world analogy.** Interviewers love clear analogies (the YouTube/subscriber analogy for Observer, the travel adapter for Adapter). They prove you truly grasp the concept.

**3. Know the trade-offs.** Always be ready to mention *disadvantages* and *when NOT to use* a pattern. Saying "Singletons are convenient but hurt testability and hide dependencies" demonstrates maturity.

**4. Cite a real Java example.** Connecting a pattern to the JDK earns big points:
```
  Singleton    -> Runtime.getRuntime()
  Factory      -> Calendar.getInstance(), DriverManager.getConnection()
  Builder      -> StringBuilder, HttpRequest.newBuilder()
  Adapter      -> InputStreamReader, Arrays.asList()
  Decorator    -> BufferedReader wrapping InputStreamReader (java.io)
  Observer     -> ActionListener, reactive streams
  Strategy     -> Comparator, java.util.function interfaces
  Command      -> Runnable, executor tasks
  Prototype    -> Object.clone() / Cloneable
```

**5. Recognise the category.** If you forget the details of a pattern, narrowing it to creational/structural/behavioral still shows structured thinking.

**6. Avoid the "pattern zealot" trap.** If asked to design something, do not over-engineer with five patterns. Interviewers value engineers who keep things simple and add patterns only when justified.

**7. Be ready to write one from memory.** Practise writing **Singleton, Strategy, Factory, Observer, and Builder** by hand -- these are the most commonly requested in coding interviews.

**Common interview questions across all patterns:**
1. "Which design patterns have you used in real projects, and why?"
2. "What is the difference between Factory Method and Abstract Factory?"
3. "Why are Singletons considered an anti-pattern by some developers?"
4. "How would you implement undo/redo?" (Command)
5. "How does the Decorator pattern differ from inheritance?"
6. "Give an example of a design pattern in the Java standard library."

### 12.4 Tricky Interview Questions (With Answers)

The questions in this section are the *gotchas* -- the ones interviewers use to separate people who memorised definitions from those who truly understand patterns. Each comes with a concise, correct answer you can adapt.

**Q1. Is Singleton an anti-pattern? Defend your answer.**
It is not inherently an anti-pattern, but it is *often misused*. It is legitimate when you genuinely need exactly one instance (e.g., a connection pool). It becomes an anti-pattern when used as a glorified global variable: it hides dependencies, introduces global mutable state, and makes unit testing hard. The modern fix is **dependency injection** (let a framework like Spring manage the single instance and inject it), which keeps the "one instance" benefit while staying testable.

**Q2. How can a Singleton break, and how do you make it bulletproof?**
Four ways it can break: (1) **multithreading** -- two threads create two instances; fix with the holder idiom or `synchronized`/double-checked locking with `volatile`. (2) **Reflection** -- code can call the private constructor via reflection; an enum prevents this. (3) **Serialization** -- deserializing creates a new object; implement `readResolve()` or use an enum. (4) **Multiple classloaders** -- each classloader gets its own instance. The **enum Singleton** handles reflection and serialization for free, which is why many experts prefer it.

**Q3. Why is double-checked locking buggy without `volatile`?**
Without `volatile`, the JVM may *reorder* instructions: the instance reference can be assigned *before* the constructor finishes. Another thread could then see a non-null but partially constructed object. Marking the field `volatile` prevents this reordering and guarantees visibility across threads.

**Q4. What is the real difference between Factory Method and Abstract Factory?**
Factory Method creates **one** product and uses **inheritance** (subclasses decide the concrete product). Abstract Factory creates **families of related products** and uses **composition** (you pick a factory object). Rule of thumb: one product -> Factory Method; a matching *set* of products -> Abstract Factory.

**Q5. Is `Simple Factory` a real GoF pattern?**
No. The "Simple Factory" (a single class with a `switch` that returns products) is a common *idiom*, not one of the 23 GoF patterns. The true **Factory Method** uses subclassing/overriding to decide the product. Saying this shows you know the distinction precisely.

**Q6. Adapter vs Decorator vs Proxy vs Facade -- they all "wrap" something. How do they differ?**
All wrap an object, but the *intent* differs: **Adapter** changes the interface to a different one the client expects. **Decorator** keeps the *same* interface but adds behaviour. **Proxy** keeps the same interface but controls access (lazy loading, security, remoting) without adding features. **Facade** provides a *new simplified* interface over a complex subsystem. Intent, not structure, is what the interviewer wants.

**Q7. Strategy and State look structurally identical. What is the difference?**
The structure (a context delegating to interchangeable objects) is the same, but the *intent* differs. In **Strategy**, the client chooses the algorithm and it usually does not change on its own. In **State**, the object changes its own behaviour as its internal state changes, and states often trigger transitions to other states. Strategy is about *how* to do something; State is about *what* the object currently is.

**Q8. How are lambdas related to Strategy and Command?**
A functional interface with a single method is essentially a Strategy or Command. A lambda is a concise, anonymous implementation of it. `Collections.sort(list, (a, b) -> a - b)` passes a sorting *strategy* as a lambda. `Runnable r = () -> doWork()` is a *command* as a lambda. Java 8 made these patterns far less verbose.

**Q9. Decorator vs Inheritance -- why not just subclass?**
Subclassing fixes behaviour at *compile time* and causes a combinatorial explosion of classes for every feature combination (`CoffeeWithMilkAndSugarAndCaramel`...). Decorator adds behaviour at *runtime* by composition, letting you mix and match features in any combination with far fewer classes. This is "favour composition over inheritance" in action.

**Q10. Give a real Java example where Decorator and Adapter appear together.**
`new BufferedReader(new InputStreamReader(inputStream))`. `InputStreamReader` is an **Adapter** (it adapts a byte `InputStream` to a character `Reader`). `BufferedReader` is a **Decorator** (it keeps the `Reader` interface but adds buffering). One line, two patterns.

**Q11. What is the "lapsed listener" problem in Observer?**
If observers register but never unregister, the subject keeps strong references to them, preventing garbage collection -- a **memory leak**. Fixes: always unsubscribe (e.g., in cleanup/`dispose`), or use weak references so observers can be collected when no longer used elsewhere.

**Q12. Can the Observer pattern cause infinite loops?**
Yes. If observer A's `update()` changes the subject, which notifies A again, you can get a notification storm or infinite recursion. Avoid by not mutating the subject inside `update()`, guarding against re-entrant notifications, or using asynchronous event queues.

**Q13. Builder vs Factory -- when do you use which?**
Use a **Factory** when you care about *which type* to create (the decision is "what object"). Use a **Builder** when you care about *how to construct* a single complex object step by step (many optional parameters, immutability). Factory answers "what"; Builder answers "how to assemble".

**Q14. Does the Prototype pattern do a deep or shallow copy by default?**
`Object.clone()` does a **shallow** copy by default -- nested object references are shared, not copied. This is a classic source of bugs (editing the clone mutates the original). For independent clones you must implement a **deep copy** (e.g., via a copy constructor that copies nested collections/objects).

**Q15. Are Spring beans Singletons in the GoF sense?**
Not exactly. Spring's default singleton scope means **one instance per Spring container**, managed and injected by the framework -- not enforced by a private constructor in your class. It gives the "one shared instance" benefit without the testability problems of the classic GoF Singleton, because you can inject a different/mock bean in tests.

**Q16. Which design principle underlies most of these patterns?**
Two stand out: **"program to an interface, not an implementation"** and **"favour composition over inheritance."** Strategy, Decorator, Adapter, Command, Observer, and the factories all lean on these. Mentioning SOLID -- especially the **Open/Closed Principle** (open for extension, closed for modification) -- ties many patterns together.

**Q17. Can you over-use design patterns? Give an example.**
Yes -- this is "pattern obsession." Wrapping a two-field class in a Builder, adding a Factory for an object that never varies, or layering five patterns where a plain method would do, all add complexity without value. Mature engineers reach for a pattern only when it removes real pain.

**Q18. How would you implement multi-level undo/redo?**
Use the **Command pattern** with two stacks. Each command implements `execute()` and `undo()`. On execute, push the command onto an *undo stack*. On undo, pop it, call `undo()`, and push it onto a *redo stack*. On redo, pop from the redo stack and `execute()` again. This is exactly how editors and IDEs implement it.

**Q19. Why prefer composition over inheritance, in pattern terms?**
Inheritance is rigid (fixed at compile time, tight coupling to the parent, fragile base-class problem) and allows only single inheritance in Java. Composition lets you assemble and swap behaviour at runtime, keeps classes focused, and is the backbone of Strategy, Decorator, and Command.

**Q20. An interviewer asks you to design a notification system (email, SMS, push). Which patterns apply?**
Multiple: **Strategy** (interchangeable send algorithms per channel), **Factory** (create the right sender), **Observer** (notify subscribers when an event occurs), and possibly **Command** (queue/retry notifications). The strong answer names the patterns *and* justifies each -- and avoids cramming in patterns that add no value.

### 12.5 Final Words

Congratulations -- you have journeyed through the 10 most important Java design patterns, from creation (Singleton, Factory Method, Abstract Factory, Builder, Prototype), through structure (Adapter, Decorator), to behaviour (Observer, Strategy, Command).

Remember the core lessons that run through every chapter:
- **Patterns are blueprints, not copy-paste code.** Adapt them to your situation.
- **Each pattern solves a specific problem.** Learn the *problem*, and the pattern becomes obvious.
- **Every pattern has trade-offs.** Knowing when *not* to use one is as valuable as knowing how.
- **Favour simplicity.** Reach for a pattern only when it genuinely makes your code clearer, more flexible, or more maintainable.
- **The underlying principles matter most:** program to interfaces, favour composition over inheritance, encapsulate what varies, and keep coupling loose.

The best way to cement this knowledge is to **use it.** Pick a small project, notice where these problems appear, and apply the matching pattern. Over time, recognising and applying patterns will become second nature -- and you will write code that other engineers find clean, flexible, and a pleasure to work with.

You are no longer a beginner with design patterns. Go build something great.

### Final Exercises

1. **Identify the pattern:** For each scenario, name the best-fit pattern and justify it in one sentence:
   (a) A logging service that must be shared and unique across the app.
   (b) A drawing app that lets you stack effects (shadow, border, glow) on shapes.
   (c) A checkout supporting credit card, PayPal, and crypto payments.
   (d) An e-commerce system where many services must react when an order is placed.
   (e) A text editor that needs unlimited undo.

2. **Combine patterns:** Many real systems combine patterns. Sketch how a "smart home app" might use Command (for actions), Observer (for sensor updates), and Singleton (for a config manager) together.

3. **Refactor with a pattern:** Take a piece of code you have written that uses a big `switch` to select behaviour, and refactor it using the Strategy pattern.

### Final Interview Questions

1. Walk through all three pattern categories and give one example from each.
2. How do you decide whether to use Factory Method, Abstract Factory, or Builder for object creation?
3. Describe a real project problem and explain which pattern you would apply and why.
4. What does "favour composition over inheritance" mean, and which patterns in this book demonstrate it?
5. When is it a mistake to use a design pattern at all?
