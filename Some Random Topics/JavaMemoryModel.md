# Java Memory Management Model - Complete Guide

## Table of Contents
1. [Sample Java Program](#sample-java-program)
2. [Memory Allocation Flow](#memory-allocation-flow)
3. [Detailed Memory Breakdown](#detailed-memory-breakdown)
4. [Step-by-Step Execution](#step-by-step-execution)
5. [Garbage Collection](#garbage-collection)
6. [Visual Diagrams](#visual-diagrams)

---

## Sample Java Program with Memory Area Annotations

```java
// ┌─────────────────────────────────────────────────────────────┐
// │ CLASS DEFINITION → Stored in METHOD AREA when loaded       │
// └─────────────────────────────────────────────────────────────┘
public class MemoryDemo {
    
    // ┌───────────────────────────────────────────────────────────┐
    // │ STATIC VARIABLE → Stored in METHOD AREA (initialized)     │
    // │ This memory is allocated when class is loaded             │
    // │ Exists for lifetime of JVM                                │
    // └───────────────────────────────────────────────────────────┘
    static String staticVar = "I am static";
    
    // ┌───────────────────────────────────────────────────────────┐
    // │ MAIN METHOD CODE → Stored in METHOD AREA (as bytecode)    │
    // │ When main() is called:                                    │
    // │  - New STACK FRAME created in STACK (Thread)              │
    // │  - All local variables will be stored on this frame       │
    // └───────────────────────────────────────────────────────────┘
    public static void main(String[] args) {
        
        // ┌─────────────────────────────────────────────────────┐
        // │ LINE 1: Person person1 = new Person("Alice", 25);  │
        // │                                                     │
        // │ STACK[main]:                                        │
        // │   person1 → [reference variable]                   │
        // │                                                     │
        // │ HEAP:                                              │
        // │   @0x1000 → Person { name: @0x3000, age: 25 }     │
        // │   @0x3000 → String "Alice"                        │
        // │                                                     │
        // │ METHOD AREA:                                       │
        // │   Person.class (loaded if not already)             │
        // └─────────────────────────────────────────────────────┘
        Person person1 = new Person("Alice", 25);
        
        // ┌─────────────────────────────────────────────────────┐
        // │ LINE 2: Person person2 = new Person("Bob", 30);    │
        // │                                                     │
        // │ STACK[main] (adds new variable):                   │
        // │   person1 → @0x1000                               │
        // │   person2 → [reference variable]                  │
        // │                                                     │
        // │ HEAP (adds new objects):                           │
        // │   @0x2000 → Person { name: @0x4000, age: 30 }    │
        // │   @0x4000 → String "Bob"                         │
        // │                                                     │
        // │ REFERENCE COUNT:                                   │
        // │   @0x1000: 1 (person1)                            │
        // │   @0x2000: 1 (person2)                            │
        // └─────────────────────────────────────────────────────┘
        Person person2 = new Person("Bob", 30);
        
        // ┌─────────────────────────────────────────────────────┐
        // │ LINE 3: Person person3 = person1;                   │
        // │                                                     │
        // │ STACK[main] (adds new variable):                   │
        // │   person1 → @0x1000                               │
        // │   person2 → @0x2000                               │
        // │   person3 → @0x1000 (SAME as person1!)            │
        // │                                                     │
        // │ HEAP: (No new objects created, just another ref)   │
        // │   @0x1000: Person (still same object)             │
        // │   @0x2000: Person (still same object)             │
        // │                                                     │
        // │ REFERENCE COUNT:                                   │
        // │   @0x1000: 2 (person1 AND person3!)              │
        // │   @0x2000: 1 (person2)                            │
        // │                                                     │
        // │ IMPORTANT: Same object, multiple references!      │
        // └─────────────────────────────────────────────────────┘
        Person person3 = person1;
        
        // ┌─────────────────────────────────────────────────────┐
        // │ LINE 4: person1 = null;                             │
        // │                                                     │
        // │ STACK[main] (modifies variable):                   │
        // │   person1 → null (reference cleared)              │
        // │   person2 → @0x2000                               │
        // │   person3 → @0x1000 (still points!)               │
        // │                                                     │
        // │ HEAP: (Objects NOT deleted yet!)                   │
        // │   @0x1000: Person (NOT garbage collected!)        │
        // │           → Still referenced by person3           │
        // │   @0x2000: Person (still referenced)              │
        // │                                                     │
        // │ GARBAGE COLLECTION STATUS:                         │
        // │   @0x1000: NOT eligible (person3 refs it)         │
        // │   @0x2000: NOT eligible (person2 refs it)         │
        // │                                                     │
        // │ CRITICAL: Setting to null doesn't delete object!  │
        // │ Other references must also be cleared!             │
        // └─────────────────────────────────────────────────────┘
        person1 = null;
        
        // ┌─────────────────────────────────────────────────────┐
        // │ LINE 5: printInfo(person2);                         │
        // │                                                     │
        // │ STACK[main] (before call):                         │
        // │   person1 → null                                  │
        // │   person2 → @0x2000                               │
        // │   person3 → @0x1000                               │
        // │                                                     │
        // │ NEW STACK FRAME CREATED: printInfo()              │
        // │   STACK[printInfo]:                                │
        // │     p → @0x2000 (parameter, same ref as person2)  │
        // │                                                     │
        // │ HEAP: (No changes)                                 │
        // │   Person objects still there, still referenced     │
        // │                                                     │
        // │ METHOD AREA:                                       │
        // │   printInfo() method bytecode executed             │
        // └─────────────────────────────────────────────────────┘
        printInfo(person2);
        
        // ┌─────────────────────────────────────────────────────┐
        // │ END OF main() METHOD:                               │
        // │                                                     │
        // │ STACK[main] is FREED                               │
        // │   person1, person2, person3 variables → GONE       │
        // │                                                     │
        // │ HEAP ANALYSIS - Garbage Collection:                │
        // │   @0x1000: Person                                  │
        // │     - person3 was on stack (now freed)            │
        // │     - NO REFERENCES → GARBAGE COLLECTED            │
        // │                                                     │
        // │   @0x2000: Person                                  │
        // │     - person2 was on stack (now freed)            │
        // │     - NO REFERENCES → GARBAGE COLLECTED            │
        // │                                                     │
        // │   @0x3000, @0x4000: String objects                │
        // │     - NO REFERENCES → GARBAGE COLLECTED            │
        // │                                                     │
        // │ RESULT: All heap objects freed, memory reclaimed   │
        // └─────────────────────────────────────────────────────┘
    }
    
    // ┌───────────────────────────────────────────────────────────┐
    // │ METHOD CODE → Stored in METHOD AREA (as bytecode)         │
    // │ Parameter 'p' will be on STACK when method is called      │
    // │ Local execution happens in STACK FRAME                    │
    // └───────────────────────────────────────────────────────────┘
    static void printInfo(Person p) {
        // ┌─────────────────────────────────────────────────────┐
        // │ STACK[printInfo]:                                   │
        // │   p → @0x2000 (reference to Person object)        │
        // │                                                     │
        // │ HEAP:                                              │
        // │   @0x2000 → Person { name: "Bob", age: 30 }      │
        // │          (accessed through 'p' reference)          │
        // │                                                     │
        // │ When println executes, it follows:                 │
        // │   p (STACK) → @0x2000 (HEAP) → access name, age   │
        // └─────────────────────────────────────────────────────┘
        System.out.println("Name: " + p.name + ", Age: " + p.age);
        
        // ┌─────────────────────────────────────────────────────┐
        // │ RETURN from printInfo:                              │
        // │   STACK[printInfo] is FREED                        │
        // │   Parameter 'p' is REMOVED from stack              │
        // │   Control returns to main()                         │
        // │   main() stack frame is still active                │
        // └─────────────────────────────────────────────────────┘
    }
}

// ┌─────────────────────────────────────────────────────────────┐
// │ CLASS DEFINITION → Stored in METHOD AREA when loaded       │
// │ Instance variables part of class structure (METHOD AREA)   │
// │ But each INSTANCE of Person has its own values on HEAP     │
// └─────────────────────────────────────────────────────────────┘
class Person {
    
    // ┌─────────────────────────────────────────────────────────┐
    // │ INSTANCE VARIABLES (Field Declarations)               │
    // │ Blueprint stored in METHOD AREA                       │
    // │ Actual values stored in HEAP (with each object)       │
    // │                                                       │
    // │ Every Person object on HEAP contains:                │
    // │   - name: String reference (4-8 bytes for ref)      │
    // │   - age: int primitive value (4 bytes)              │
    // └─────────────────────────────────────────────────────────┘
    String name;     // Instance variable
    int age;         // Instance variable
    
    // ┌─────────────────────────────────────────────────────────┐
    // │ CONSTRUCTOR CODE → Stored in METHOD AREA (bytecode)    │
    // │ When Person() is called:                              │
    // │   1. Object allocated on HEAP                         │
    // │   2. Constructor code (from METHOD AREA) executed    │
    // │   3. STACK has 'this' reference + parameters        │
    // │   4. Instance variables initialized                  │
    // └─────────────────────────────────────────────────────────┘
    Person(String name, int age) {
        // ┌──────────────────────────────────────────────────┐
        // │ STACK[Person constructor]:                       │
        // │   this → @0x1000 (new Person object)            │
        // │   name → reference to String (from caller)      │
        // │   age → 25 or 30 (primitive value)              │
        // │                                                  │
        // │ HEAP[@0x1000]:                                  │
        // │   name field ← string reference (assigned)      │
        // │   age field ← int value (assigned)              │
        // │                                                  │
        // │ METHOD AREA:                                    │
        // │   this.name assignment (bytecode)              │
        // │   this.age assignment (bytecode)               │
        // └──────────────────────────────────────────────────┘
        this.name = name;  // Assign String ref from parameter to heap object
        this.age = age;    // Assign int value from parameter to heap object
    }
}
```

### Memory Location Summary Table

| Code Element | Stored In | When Allocated | When Freed | Thread-Safe |
|--------------|-----------|----------------|-----------|------------|
| `static String staticVar` | METHOD AREA | Class load | JVM exit | Yes (shared) |
| `class Person` bytecode | METHOD AREA | Class load | JVM exit | Yes (shared) |
| `main()` bytecode | METHOD AREA | Class load | JVM exit | Yes (shared) |
| `printInfo()` bytecode | METHOD AREA | Class load | JVM exit | Yes (shared) |
| Local variable `person1` | STACK[main] | main() call | main() return | Yes (per-thread) |
| Local variable `person2` | STACK[main] | main() call | main() return | Yes (per-thread) |
| Local variable `person3` | STACK[main] | main() call | main() return | Yes (per-thread) |
| `Person("Alice", 25)` object | HEAP | `new` keyword | GC (when unreferenced) | No (needs sync) |
| `Person("Bob", 30)` object | HEAP | `new` keyword | GC (when unreferenced) | No (needs sync) |
| String `"Alice"` | HEAP | `new` keyword | GC (when unreferenced) | No (needs sync) |
| String `"Bob"` | HEAP | `new` keyword | GC (when unreferenced) | No (needs sync) |
| Parameter `p` in printInfo | STACK[printInfo] | printInfo() call | printInfo() return | Yes (per-thread) |

---

## Memory Allocation Flow

### Overall JVM Memory Structure

```
┌─────────────────────────────────────────────────────────────────┐
│                       JVM Runtime Memory                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────────────┐                                       │
│  │   METHOD AREA        │  (Shared across all threads)          │
│  │  ┌────────────────┐  │                                       │
│  │  │ Class Structs  │  │  - Person.class structure             │
│  │  │ Method Code    │  │  - MemoryDemo.class structure         │
│  │  │ Constant Pool  │  │  - "I am static" string literal      │
│  │  │ Static Vars    │  │  - staticVar = "I am static"          │
│  │  └────────────────┘  │                                       │
│  └──────────────────────┘                                       │
│                                                                 │
│  ┌──────────────────────┐                                       │
│  │      HEAP            │  (Shared across all threads, GC here) │
│  │  ┌────────────────┐  │                                       │
│  │  │  Person Obj 1  │  │  @0x1000  [name="Alice", age=25]      │
│  │  │  Person Obj 2  │  │  @0x2000  [name="Bob", age=30]        │
│  │  │  String "Alice"│  │  @0x3000                              │
│  │  │  String "Bob"  │  │  @0x4000                              │
│  │  └────────────────┘  │                                       │
│  └──────────────────────┘                                       │
│                                                                 │
│  ┌──────────────────────┐                                       │
│  │   STACK (main)       │  (Per thread)                         │
│  │  ┌────────────────┐  │                                       │
│  │  │ args: null     │  │  Reference to String[] array          │
│  │  │ person1: ref   │  │  → points to @0x1000 (or null)        │
│  │  │ person2: ref   │  │  → points to @0x2000                  │
│  │  │ person3: ref   │  │  → points to @0x1000                  │
│  │  └────────────────┘  │                                       │
│  └──────────────────────┘                                       │
│                                                                 │
│  ┌──────────────────────┐                                       │
│  │   STACK (printInfo)  │  (Per thread)                         │
│  │  ┌────────────────┐  │                                       │
│  │  │ p: ref         │  │  → points to @0x2000                  │
│  │  └────────────────┘  │                                       │
│  └──────────────────────┘                                       │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## Detailed Memory Breakdown

### 1. METHOD AREA (Class Data)
**Location**: Shared across all threads  
**When Allocated**: At class loading time  
**Freed**: When JVM shuts down or class is unloaded

```
┌────────────────────────────────────────┐
│         METHOD AREA (Metaspace)        │
├────────────────────────────────────────┤
│ Class: Person                          │
│   - Fields: String name, int age       │
│   - Constructor: Person(String, int)   │
│   - Code stored as bytecode            │
├────────────────────────────────────────┤
│ Class: MemoryDemo                      │
│   - Static var: staticVar              │
│   - Methods: main(), printInfo()       │
├────────────────────────────────────────┤
│ Static Variables (initialized)         │
│   staticVar → "I am static" (@heap)    │
├────────────────────────────────────────┤
│ String Literal Pool                    │
│   "Alice" (if used multiple times)     │
│   "Bob"                                │
│   "I am static"                        │
└────────────────────────────────────────┘
```

### 2. HEAP (Objects)
**Location**: Shared across all threads  
**When Allocated**: When `new` keyword is used  
**Freed**: By Garbage Collector when no references exist

```
┌────────────────────────────────────────┐
│            HEAP (Garbage Collected)    │
├────────────────────────────────────────┤
│                                        │
│  @0x1000: Person Object               │
│  ┌──────────────────┐                 │
│  │ name → @0x3000  │ (String ref)     │
│  │ age  → 25       │ (primitive)      │
│  └──────────────────┘                 │
│                                        │
│  @0x2000: Person Object               │
│  ┌──────────────────┐                 │
│  │ name → @0x4000  │ (String ref)     │
│  │ age  → 30       │ (primitive)      │
│  └──────────────────┘                 │
│                                        │
│  @0x3000: String Object               │
│  ┌──────────────────┐                 │
│  │ value: "Alice"  │                 │
│  │ length: 5       │                  │
│  └──────────────────┘                 │
│                                        │
│  @0x4000: String Object               │
│  ┌──────────────────┐                 │
│  │ value: "Bob"    │                 │
│  │ length: 3       │                  │
│  └──────────────────┘                 │
│                                        │
└────────────────────────────────────────┘
```

### 3. STACK (Method Execution)
**Location**: Per thread (separate for each thread)  
**When Allocated**: When method is called  
**Freed**: When method returns

```
STACK FRAME: main() method
┌──────────────────────────────────┐
│ Local Variables:                 │
│                                  │
│ args ──→ @0x5000 (String[])     │
│ person1 ──→ @0x1000 (Person)    │
│ person2 ──→ @0x2000 (Person)    │
│ person3 ──→ @0x1000 (Person)    │
│                                  │
│ Method Info:                     │
│ - Return Address: Line after     │
│   main() call                    │
│ - Local Variables Count: 4       │
└──────────────────────────────────┘
         ↓ (when printInfo called)
STACK FRAME: printInfo() method
┌──────────────────────────────────┐
│ Local Variables:                 │
│                                  │
│ p ──→ @0x2000 (Person)          │
│                                  │
│ Method Info:                     │
│ - Return Address: Line 5         │
└──────────────────────────────────┘
```

---

## Step-by-Step Execution

### Line-by-Line Memory Changes

#### **Line 1: `Person person1 = new Person("Alice", 25);`**

```
STEP 1: Class Loader loads Person.class (if not already loaded)
        → METHOD AREA now has Person class structure

STEP 2: Create String "Alice"
        HEAP: @0x3000 → String("Alice")

STEP 3: Create Person object
        HEAP: @0x1000 → Person {name: @0x3000, age: 25}

STEP 4: Store reference on STACK
        STACK[main]: person1 = @0x1000

MEMORY STATE:
┌─ METHOD AREA ─────────┐
│ Person.class loaded   │
│ staticVar = @heap     │
└───────────────────────┘
          
┌─ HEAP ────────────────┐
│ @0x1000: Person       │
│   name → @0x3000      │
│   age → 25            │
│ @0x3000: "Alice"      │
└───────────────────────┘

┌─ STACK (main) ────────┐
│ person1 → @0x1000     │
└───────────────────────┘
```

#### **Line 2: `Person person2 = new Person("Bob", 30);`**

```
STEP 1: Create String "Bob"
        HEAP: @0x4000 → String("Bob")

STEP 2: Create Person object
        HEAP: @0x2000 → Person {name: @0x4000, age: 30}

STEP 3: Store reference on STACK
        STACK[main]: person2 = @0x2000

MEMORY STATE:
┌─ HEAP ────────────────┐
│ @0x1000: Person       │  ← person1
│   name → @0x3000      │
│   age → 25            │
│ @0x2000: Person       │  ← person2
│   name → @0x4000      │
│   age → 30            │
│ @0x3000: "Alice"      │
│ @0x4000: "Bob"        │
└───────────────────────┘

┌─ STACK (main) ────────┐
│ person1 → @0x1000     │
│ person2 → @0x2000     │
└───────────────────────┘
```

#### **Line 3: `Person person3 = person1;`**

```
STEP 1: Read value of person1 reference (@0x1000)

STEP 2: Assign same reference to person3
        STACK[main]: person3 = @0x1000

MEMORY STATE:
┌─ HEAP ────────────────┐
│ @0x1000: Person       │  ← person1 & person3
│   name → @0x3000      │    (both point here)
│   age → 25            │
│ @0x2000: Person       │  ← person2
│   name → @0x4000      │
│   age → 30            │
│ @0x3000: "Alice"      │
│ @0x4000: "Bob"        │
└───────────────────────┘

┌─ STACK (main) ────────┐
│ person1 → @0x1000     │
│ person2 → @0x2000     │
│ person3 → @0x1000     │
└───────────────────────┘
    REFERENCE COUNT:
    @0x1000: 2 references (person1, person3)
    @0x2000: 1 reference (person2)
```

#### **Line 4: `person1 = null;`**

```
STEP 1: Null out person1 reference
        STACK[main]: person1 = null

MEMORY STATE:
┌─ STACK (main) ────────┐
│ person1 → null        │
│ person2 → @0x2000     │
│ person3 → @0x1000     │
└───────────────────────┘

REFERENCE COUNT UPDATE:
@0x1000: 1 reference (person3) ← Still alive!
@0x2000: 1 reference (person2)

IMPORTANT: Object @0x1000 is NOT garbage collected yet
because person3 still references it!
```

#### **Line 5: `printInfo(person2);`**

```
STEP 1: Call printInfo(person2)
        Create new STACK FRAME for printInfo()

STEP 2: Pass reference to person2
        STACK[printInfo]: p = @0x2000

STEP 3: Inside printInfo, access p.name and p.age
        Follow reference from stack → heap → object fields

MEMORY STATE (during printInfo):
┌─ STACK (main) ────────┐
│ person1 → null        │
│ person2 → @0x2000     │
│ person3 → @0x1000     │
└───────────────────────┘
         ↓ (call stack)
┌─ STACK (printInfo) ───┐
│ p → @0x2000           │
└───────────────────────┘

HEAP: (unchanged)
┌─ HEAP ────────────────┐
│ @0x1000: Person("Alice")
│ @0x2000: Person("Bob") ← accessed via p
│ @0x3000: "Alice"
│ @0x4000: "Bob"
└───────────────────────┘

OUTPUT: Name: Bob, Age: 30
```

#### **After main() Returns (Garbage Collection)**

```
STEP 1: printInfo() returns
        STACK[printInfo] is freed

STEP 2: main() returns
        STACK[main] is freed (all local variables gone)

STEP 3: Garbage Collector runs (automatic)
        Check for unreferenced objects:
        
        REFERENCE ANALYSIS:
        @0x1000 → 0 references (no variable points to it)
                  MARKED FOR DELETION
        @0x2000 → 0 references (no variable points to it)
                  MARKED FOR DELETION
        @0x3000 → 0 references (no variable points to it)
                  MARKED FOR DELETION
        @0x4000 → 0 references (no variable points to it)
                  MARKED FOR DELETION

STEP 4: Sweep Phase
        All unreferenced objects deleted from HEAP
        Memory reclaimed and available for new objects

FINAL MEMORY STATE:
┌─ HEAP ────────────────┐
│ (Empty - all freed)   │
└───────────────────────┘

┌─ METHOD AREA ─────────┐
│ Person.class (still)  │  ← Remains loaded
│ MemoryDemo.class      │
│ staticVar (still)     │
└───────────────────────┘
```

---

## Garbage Collection Process

### Mark & Sweep Algorithm

```
BEFORE GC:
┌─────────────────────────────────────────┐
│ Reachable Objects   │ Unreachable       │
│                     │ Objects (TRASH)   │
│ ┌─────────────────┐ │ ┌──────────────┐ │
│ │ person3 @0x1000│ │ │ person1:null │ │
│ └─────────────────┘ │ │ person2:gone │ │
│ staticVar @0x7000   │ │ Strings      │ │
│                     │ └──────────────┘ │
└─────────────────────────────────────────┘

GARBAGE COLLECTION:

Phase 1: MARK (Identify reachable objects)
┌────────────────────────────────────────┐
│ Start from ROOT REFERENCES:            │
│ - Stack variables                      │
│ - Static variables                     │
│ - Global references                    │
│                                        │
│ Traverse and mark all reachable:       │
│ ✓ @0x1000 (reachable via person3)     │
│ ✓ @0x3000 (reachable via @0x1000)    │
│ ✓ @0x7000 (reachable via static var) │
│ ✗ @0x2000 (unreachable)               │
│ ✗ @0x4000 (unreachable)               │
└────────────────────────────────────────┘

Phase 2: SWEEP (Delete unmarked objects)
┌────────────────────────────────────────┐
│ For each heap object:                  │
│ - If marked: keep in memory            │
│ - If not marked: deallocate            │
│                                        │
│ @0x1000: KEEP (marked)                │
│ @0x2000: DELETE (not marked)          │
│ @0x3000: KEEP (marked)                │
│ @0x4000: DELETE (not marked)          │
└────────────────────────────────────────┘

Phase 3: COMPACT (Optional, moves objects)
┌────────────────────────────────────────┐
│ Relocate objects to reduce             │
│ fragmentation:                         │
│                                        │
│ @0x1000 → @0x1000 (stays)             │
│ @0x3000 → @0x2000 (compacted)         │
│                                        │
│ Update all references accordingly      │
└────────────────────────────────────────┘

AFTER GC:
┌─────────────────────┐
│ @0x1000: Person     │  (Reachable)
│ @0x2000: "Alice"    │  (Compacted)
│ Free Memory (block) │  (Available)
└─────────────────────┘
```

---

## Memory Regions Quick Reference

| Region | Stores | Thread-Safe | Size | Life Cycle |
|--------|--------|-------------|------|-----------|
| **Method Area** | Classes, methods, static vars | Yes (shared) | Fixed | Class load → JVM exit |
| **Heap** | Objects, arrays | No (needs sync) | Dynamic | Object creation → GC |
| **Stack** | Primitives, references | Yes (per thread) | Limited | Method call → return |
| **PC Register** | Bytecode address | Yes (per thread) | Small | Method call → return |
| **Native Stack** | Native method calls | Per thread | System | Call → return |

---

## Key Concepts Illustrated

### 1. Reference vs Object
```
Stack contains REFERENCE (pointer/address):
    person1 → @0x1000

Heap contains ACTUAL OBJECT:
    @0x1000 → Person {name="Alice", age=25}

Multiple references can point to same object:
    person1 → @0x1000
    person3 → @0x1000 (same object!)
```

### 2. Primitives vs Objects
```
Primitives (stored on STACK):
    int age = 25;           // age is on stack
    int[] arr = new int[3]; // arr ref on stack, array on heap

Objects (reference on STACK, object on HEAP):
    Person p = new Person();  // p ref on stack
                              // Person obj on heap
```

### 3. String Literal Pool
```
String s1 = "Hello";  // Created in pool, @0x5000
String s2 = "Hello";  // Reuses same object, @0x5000
String s3 = new String("Hello");  // New object, @0x6000

s1 == s2  // true (same reference)
s1.equals(s3)  // true (same content)
s1 == s3  // false (different objects)
```

---

## Common Memory Issues

### 1. Stack Overflow
```
❌ Infinite Recursion:
void recursiveMethod() {
    recursiveMethod();  // Stack grows infinitely!
}

RESULT: StackOverflowError
    Every method call adds frame to stack
    Stack has limited size
    Eventually exceeds capacity
```

### 2. Memory Leak
```
❌ Static Collection that grows unbounded:
static List<String> list = new ArrayList<>();

public void addToList(String item) {
    list.add(item);  // Keeps growing
    // Items never removed - never garbage collected!
}

RESULT: OutOfMemoryError (Heap exhausted)
```

### 3. Heap Exhaustion
```
❌ Creating too many objects:
List<byte[]> list = new ArrayList<>();
while(true) {
    list.add(new byte[1024*1024]);  // 1MB objects
}

RESULT: OutOfMemoryError: Java heap space
    Heap fills up faster than GC can free
    Or objects have references preventing GC
```

---

## Memory Configuration (JVM Flags)

```bash
# Set heap size
-Xms512m          # Initial heap size: 512MB
-Xmx1024m         # Maximum heap size: 1GB

# Example:
java -Xms512m -Xmx1024m -cp . MemoryDemo

# Stack size
-Xss1m            # Thread stack size: 1MB

# Metaspace (Method Area)
-XX:MetaspaceSize=64m      # Initial metaspace
-XX:MaxMetaspaceSize=256m   # Maximum metaspace

# Garbage Collector selection
-XX:+UseG1GC              # G1 Garbage Collector
-XX:+UseSerialGC          # Serial GC
-XX:+UseParallelGC        # Parallel GC
```

---

## Best Practices

### ✅ DO:
1. **Nullify references** when object is no longer needed
   ```java
   Person p = new Person();
   // ... use p ...
   p = null;  // Help GC identify as eligible
   ```

2. **Use try-with-resources** for automatic cleanup
   ```java
   try (FileReader fr = new FileReader("file.txt")) {
       // Use fr
   }  // Automatically closed
   ```

3. **Keep scope tight** for automatic stack cleanup
   ```java
   if (condition) {
       int temp = 100;  // Freed when block ends
   }
   ```

4. **Avoid static collections** without bounds
   ```java
   static List<String> list = new ArrayList<>();
   // Add logic to remove old items!
   ```

### ❌ DON'T:
1. Don't rely on `System.gc()` - it's only a hint
2. Don't create objects in tight loops unnecessarily
3. Don't hold references to large objects longer than needed
4. Don't ignore memory warning signs
5. Don't assume `new` keyword is free

---

## Monitoring & Tools

### View Memory Usage in Code:
```java
Runtime runtime = Runtime.getRuntime();

long totalMemory = runtime.totalMemory();      // Total allocated
long freeMemory = runtime.freeMemory();        // Currently free
long maxMemory = runtime.maxMemory();          // Maximum available
long usedMemory = totalMemory - freeMemory;   // Currently used

System.out.println("Used: " + usedMemory / 1024 / 1024 + " MB");
System.out.println("Free: " + freeMemory / 1024 / 1024 + " MB");
System.out.println("Max: " + maxMemory / 1024 / 1024 + " MB");
```

### External Tools:
- **JProfiler**: Visual memory profiling
- **YourKit**: Real-time monitoring
- **Eclipse MAT**: Memory leak detection
- **JConsole**: Built-in JVM monitoring
- **VisualVM**: Visual tool for JVM

---

## Summary Diagram

```
┌──────────────────────────────────────────────────────────────┐
│                    COMPLETE JAVA MEMORY MODEL                │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌─────────────────┐          ┌──────────────────────┐      │
│  │  Source Code    │          │  Class Loader        │      │
│  │  MemoryDemo.java│──────→   │  Loads .class files  │      │
│  │  Person.java    │          └──────────────────────┘      │
│  └─────────────────┘                    ↓                   │
│                                         │                   │
│                                         ↓                   │
│                        ┌────────────────────────┐            │
│                        │   METHOD AREA          │            │
│                        │ (Class Structures)     │            │
│                        │ (Static Variables)     │            │
│                        │ (Method Code)          │            │
│                        └────────────────────────┘            │
│                                         ↑↓                   │
│                        ┌────────────────────────┐            │
│                        │  EXECUTION ENGINE      │            │
│                        │ (Interprets bytecode)  │            │
│                        │ (JIT Compilation)      │            │
│                        └────────────────────────┘            │
│                         ↑                    ↓               │
│              ┌──────────────────┐   ┌──────────────────┐    │
│              │  STACK           │   │   HEAP           │    │
│              │ (Variables)      │   │ (Objects)        │    │
│              │ (References)     │   │ (GC Managed)     │    │
│              │ (Method Frames)  │   │ (Shared)         │    │
│              └──────────────────┘   └──────────────────┘    │
│                                                              │
│  ┌─────────────────────────────────────────────────────────┐│
│  │ GARBAGE COLLECTION (Automatic Memory Management)       ││
│  │ • Mark Phase: Identify reachable objects              ││
│  │ • Sweep Phase: Delete unreachable objects            ││
│  │ • Compact Phase: Relocate to reduce fragmentation    ││
│  └─────────────────────────────────────────────────────────┘│
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

---

## Conclusion

Java's memory management consists of:

1. **METHOD AREA**: Static, shared, class-level data
2. **HEAP**: Dynamic, shared, garbage-collected objects
3. **STACK**: Fast, thread-local, method variables
4. **EXECUTION ENGINE**: JVM that runs your code
5. **GARBAGE COLLECTOR**: Automatic cleanup of unused objects

Understanding this model helps you write efficient Java programs and debug memory issues effectively.

---

**Document Version**: 1.0  
**Last Updated**: 2026-07-30  
**References**: JVM Specification, Java Memory Model Documentation
