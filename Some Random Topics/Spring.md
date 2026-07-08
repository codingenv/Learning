# Spring Framework & Hibernate: A Complete Interview Guide
## Expert-Level Questions & Answers (For 16+ Years Java Developers)

> A comprehensive reference covering Spring Core, Spring MVC, Spring Data, Hibernate, JPA, and advanced patterns. Designed for senior engineers, architects, and interview preparation.

---

## Table of Contents

### Part 1: Spring Core & Dependency Injection
1. [Q1: What is IoC and how does Spring implement it?](#q1-what-is-ioc-and-how-does-spring-implement-it)
2. [Q2: Explain the difference between constructor injection and setter injection](#q2-explain-the-difference-between-constructor-injection-and-setter-injection)
3. [Q3: What are Spring Beans? Explain bean lifecycle](#q3-what-are-spring-beans-explain-bean-lifecycle)
4. [Q4: Explain bean scopes in Spring](#q4-explain-bean-scopes-in-spring)
5. [Q5: How does Spring resolve circular dependencies?](#q5-how-does-spring-resolve-circular-dependencies)
6. [Q6: What is autowiring and its modes](#q6-what-is-autowiring-and-its-modes)
7. [Q7: How does Spring handle property injection?](#q7-how-does-spring-handle-property-injection)

### Part 2: Spring MVC & Web Layer
8. [Q8: Explain the DispatcherServlet and its role](#q8-explain-the-dispatcherservlet-and-its-role)
9. [Q9: What is the Spring MVC request processing flow?](#q9-what-is-the-spring-mvc-request-processing-flow)
10. [Q10: Explain HandlerMapping, HandlerAdapter, and ViewResolver](#q10-explain-handlermapping-handleradapter-and-viewresolver)
11. [Q11: How does Spring validate request data?](#q11-how-does-spring-validate-request-data)
12. [Q12: Explain interceptors vs filters in Spring](#q12-explain-interceptors-vs-filters-in-spring)
13. [Q13: How does exception handling work in Spring MVC?](#q13-how-does-exception-handling-work-in-spring-mvc)
14. [Q14: What is REST and how to build RESTful services in Spring?](#q14-what-is-rest-and-how-to-build-restful-services-in-spring)

### Part 3: Spring Data & Database
15. [Q15: What is Spring Data JPA and how it works](#q15-what-is-spring-data-jpa-and-how-it-works)
16. [Q16: Explain JPA relationships (One-to-One, One-to-Many, Many-to-Many)](#q16-explain-jpa-relationships-one-to-one-one-to-many-many-to-many)
17. [Q17: What is lazy loading vs eager loading?](#q17-what-is-lazy-loading-vs-eager-loading)
18. [Q18: Explain the N+1 query problem and solutions](#q18-explain-the-n1-query-problem-and-solutions)
19. [Q19: How to use custom queries with @Query annotation](#q19-how-to-use-custom-queries-with-query-annotation)
20. [Q20: What is Specification and Criteria API?](#q20-what-is-specification-and-criteria-api)

### Part 4: Hibernate Deep Dive
21. [Q21: What is Hibernate and how does it work?](#q21-what-is-hibernate-and-how-does-it-work)
22. [Q22: Explain Hibernate session lifecycle](#q22-explain-hibernate-session-lifecycle)
23. [Q23: What is the difference between get() and load()?](#q23-what-is-the-difference-between-get-and-load)
24. [Q24: Explain Hibernate caching strategy (L1 and L2)](#q24-explain-hibernate-caching-strategy-l1-and-l2)
25. [Q25: What are Hibernate states (Transient, Persistent, Detached)?](#q25-what-are-hibernate-states-transient-persistent-detached)
26. [Q26: How does Hibernate handle inheritance mapping?](#q26-how-does-hibernate-handle-inheritance-mapping)
27. [Q27: Explain lazy loading and proxy pattern in Hibernate](#q27-explain-lazy-loading-and-proxy-pattern-in-hibernate)

### Part 5: Transactions & Data Consistency
28. [Q28: Explain @Transactional and how it works](#q28-explain-transactional-and-how-it-works)
29. [Q29: What are transaction propagation types?](#q29-what-are-transaction-propagation-types)
30. [Q30: Explain isolation levels and dirty read, phantom read, non-repeatable read](#q30-explain-isolation-levels-and-dirty-read-phantom-read-non-repeatable-read)
31. [Q31: How does Spring handle transaction rollback?](#q31-how-does-spring-handle-transaction-rollback)
32. [Q32: What is optimistic locking vs pessimistic locking?](#q32-what-is-optimistic-locking-vs-pessimistic-locking)

### Part 6: Spring AOP & Proxy
33. [Q33: Explain AOP concepts and uses](#q33-explain-aop-concepts-and-uses)
34. [Q34: How does Spring create proxies (JDK Dynamic vs CGLIB)?](#q34-how-does-spring-create-proxies-jdk-dynamic-vs-cglib)
35. [Q35: Explain advice types in Spring AOP](#q35-explain-advice-types-in-spring-aop)
36. [Q36: What are pointcuts and join points?](#q36-what-are-pointcuts-and-join-points)

### Part 7: Spring Boot
37. [Q37: What is Spring Boot and how is it different from Spring?](#q37-what-is-spring-boot-and-how-is-it-different-from-spring)
38. [Q38: How does Spring Boot auto-configuration work?](#q38-how-does-spring-boot-auto-configuration-work)
39. [Q39: Explain Spring Boot starters and dependencies](#q39-explain-spring-boot-starters-and-dependencies)
40. [Q40: How to externalize configuration in Spring Boot?](#q40-how-to-externalize-configuration-in-spring-boot)

### Part 8: Spring Security
41. [Q41: How does Spring Security work (architecture overview)?](#q41-how-does-spring-security-work-architecture-overview)
42. [Q42: Explain authentication vs authorization](#q42-explain-authentication-vs-authorization)
43. [Q43: How to implement OAuth2 / JWT in Spring?](#q43-how-to-implement-oauth2--jwt-in-spring)
44. [Q44: What is CSRF and how to prevent it?](#q44-what-is-csrf-and-how-to-prevent-it)

### Part 9: Advanced Patterns & Production
45. [Q45: Explain event-driven architecture in Spring](#q45-explain-event-driven-architecture-in-spring)
46. [Q46: How to implement caching in Spring (Redis, Caffeine)?](#q46-how-to-implement-caching-in-spring-redis-caffeine)
47. [Q47: How to handle distributed transactions in microservices?](#q47-how-to-handle-distributed-transactions-in-microservices)
48. [Q48: Explain Circuit Breaker pattern and Resilience4j](#q48-explain-circuit-breaker-pattern-and-resilience4j)
49. [Q49: How to monitor and profile Spring applications?](#q49-how-to-monitor-and-profile-spring-applications)
50. [Q50: What are best practices for Spring application architecture?](#q50-what-are-best-practices-for-spring-application-architecture)

---

# Part 1: Spring Core & Dependency Injection

---

## Q1: What is IoC and how does Spring implement it?

### Answer

**IoC (Inversion of Control)** is a design principle where the control flow is inverted. Instead of the application creating and managing objects, a container (Spring Container) manages the lifecycle and dependencies.

#### Traditional Approach (Without IoC):

```java
// Application controls object creation
public class UserService {
    private UserRepository userRepository;
    
    public UserService() {
        this.userRepository = new UserRepositoryImpl();  // Tightly coupled
    }
}
```

Problems:
- Tight coupling
- Hard to test (can't mock dependencies)
- Difficult to change implementations

#### IoC Approach (With Spring):

```java
// Spring container controls object creation
@Service
public class UserService {
    private final UserRepository userRepository;
    
    // Spring injects the dependency
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}

@Repository
public class UserRepositoryImpl implements UserRepository {
    // Implementation
}

// Spring application context
@Configuration
public class AppConfig {
    @Bean
    public UserRepository userRepository() {
        return new UserRepositoryImpl();
    }
    
    @Bean
    public UserService userService(UserRepository userRepository) {
        return new UserService(userRepository);
    }
}
```

#### How Spring Implements IoC:

```
APPLICATION START
    ↓
Spring Container Initialized
    ↓
Scans Classpath/Classes for @Component, @Service, @Repository
    ↓
Creates Bean Definitions
    ↓
Instantiates Beans (Dependency Injection)
    ↓
Application uses beans from container
    ↓
Container manages lifecycle (init, destroy)
```

#### Benefits:

```
Benefits of IoC:
├─ Loose Coupling: Implementations can change without affecting code
├─ Testability: Easy to inject mocks for testing
├─ Configuration: Centralized bean management
├─ Flexibility: Switch implementations easily
└─ Code Reuse: Same bean can be used in multiple places
```

---

## Q2: Explain the difference between constructor injection and setter injection

### Answer

#### Constructor Injection

```java
@Service
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    
    // Constructor injection
    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }
}
```

**Advantages:**
- Immutable dependencies (final keyword)
- Required dependencies explicit in constructor
- Thread-safe (no partial initialization)
- Good for testing (dependencies required at creation)

**Disadvantages:**
- More verbose code
- Circular dependency issues

#### Setter Injection

```java
@Service
public class UserService {
    private UserRepository userRepository;
    private EmailService emailService;
    
    // Setter injection
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
}
```

**Advantages:**
- Optional dependencies possible
- Handles circular dependencies
- Can change dependencies after initialization
- Less boilerplate

**Disadvantages:**
- Mutable objects (thread-safety concerns)
- Hidden dependencies (hard to see what's needed)
- Object can be in incomplete state
- Not ideal for testing

#### Comparison Table:

```
Aspect              Constructor Injection      Setter Injection
────────────────────────────────────────────────────────────────
Immutability        ✓ Immutable (final)       ✗ Mutable
Circular Deps       ✗ Problem                 ✓ Handles easily
Required Deps       ✓ Explicit                ✗ Implicit
Optional Deps       ✗ Hard                    ✓ Easy (@Autowired required=false)
Thread Safety       ✓ Safe                    ✗ Unsafe
Testing             ✓ Easy (explicit)         ✗ Hard (hidden)
Boilerplate         ✗ More code               ✓ Less code
Dependency Visibility ✓ Clear                  ✗ Hidden
```

#### Best Practice:

```java
// PREFERRED: Constructor injection for required dependencies
@Service
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final Logger logger;  // Optional
    
    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.logger = LoggerFactory.getLogger(UserService.class);
    }
}

// Use setter only for optional dependencies
@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private AnalyticsService analyticsService;  // Optional
    
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }
    
    @Autowired(required = false)
    public void setAnalyticsService(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }
}
```

---

## Q3: What are Spring Beans? Explain bean lifecycle

### Answer

**Spring Beans** are objects managed by the Spring Container (ApplicationContext). They are instantiated, assembled, and managed by Spring.

#### Bean Lifecycle Sequence:

```
1. INSTANTIATION
   └─ Spring creates bean instance

2. POPULATE PROPERTIES
   └─ Inject dependencies (setter/field)

3. SET BEAN NAME (if implements BeanNameAware)
   └─ setBeanName() called

4. SET BEAN FACTORY (if implements BeanFactoryAware)
   └─ setBeanFactory() called

5. SET APPLICATION CONTEXT (if implements ApplicationContextAware)
   └─ setApplicationContext() called

6. PRE-INITIALIZATION (BeanPostProcessor.postProcessBeforeInitialization)
   └─ Custom processing before initialization

7. INITIALIZATION CALLBACKS
   ├─ @PostConstruct method called
   ├─ afterPropertiesSet() (InitializingBean interface)
   └─ init-method specified in configuration

8. POST-INITIALIZATION (BeanPostProcessor.postProcessAfterInitialization)
   └─ Custom processing after initialization

9. BEAN READY FOR USE
   └─ Available in application context

10. DESTRUCTION CALLBACKS
    ├─ @PreDestroy method called
    ├─ destroy() (DisposableBean interface)
    └─ destroy-method specified in configuration

11. BEAN DESTROYED
    └─ Bean lifecycle ends
```

#### Visual Representation:

```
┌─────────────────────────────────────────────────────────────┐
│                    SPRING BEAN LIFECYCLE                    │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Instantiation                                              │
│      ↓                                                       │
│  Dependency Injection                                       │
│      ↓                                                       │
│  Aware Interfaces (BeanNameAware, ApplicationContextAware) │
│      ↓                                                       │
│  BeanPostProcessor.postProcessBeforeInitialization()       │
│      ↓                                                       │
│  Initialization Methods (@PostConstruct, afterPropertiesSet)
│      ↓                                                       │
│  BeanPostProcessor.postProcessAfterInitialization()        │
│      ↓                                                       │
│  ✓ BEAN READY TO USE                                        │
│      ↓                                                       │
│  Destruction Methods (@PreDestroy, destroy)                │
│      ↓                                                       │
│  X BEAN DESTROYED                                           │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

#### Code Example:

```java
@Component
public class UserServiceBean implements InitializingBean, DisposableBean, 
                                       BeanNameAware, ApplicationContextAware {
    
    private String beanName;
    private ApplicationContext applicationContext;
    private UserRepository userRepository;
    
    // 1. Constructor (Instantiation)
    public UserServiceBean() {
        System.out.println("1. UserServiceBean instantiated");
    }
    
    // 2. Setter (Dependency Injection)
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        System.out.println("2. Dependency injected");
        this.userRepository = userRepository;
    }
    
    // 3. BeanNameAware interface
    @Override
    public void setBeanName(String name) {
        System.out.println("3. Bean name set: " + name);
        this.beanName = name;
    }
    
    // 4. ApplicationContextAware interface
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        System.out.println("4. Application context set");
        this.applicationContext = applicationContext;
    }
    
    // 5. @PostConstruct (Initialization)
    @PostConstruct
    public void init() {
        System.out.println("5. @PostConstruct - Initialization");
        // Setup resources, validate configuration
    }
    
    // 6. InitializingBean interface
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("6. afterPropertiesSet - InitializingBean");
    }
    
    // 7. @PreDestroy (Destruction)
    @PreDestroy
    public void preDestroy() {
        System.out.println("7. @PreDestroy - Clean up");
    }
    
    // 8. DisposableBean interface
    @Override
    public void destroy() throws Exception {
        System.out.println("8. destroy - DisposableBean");
    }
}

/* OUTPUT:
1. UserServiceBean instantiated
2. Dependency injected
3. Bean name set: userServiceBean
4. Application context set
5. @PostConstruct - Initialization
6. afterPropertiesSet - InitializingBean
(Bean ready for use)
7. @PreDestroy - Clean up
8. destroy - DisposableBean
*/
```

#### BeanPostProcessor Example:

```java
@Component
public class CustomBeanPostProcessor implements BeanPostProcessor {
    
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("Before init: " + beanName);
        return bean;
    }
    
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("After init: " + beanName);
        // Can wrap bean with proxy here
        return bean;
    }
}
```

---

## Q4: Explain bean scopes in Spring

### Answer

**Bean Scope** defines the lifecycle and visibility of a bean instance in the Spring container.

#### Types of Bean Scopes:

```
┌──────────────┬──────────────────────────────┬──────────────────┐
│ Scope        │ Description                  │ Use Case         │
├──────────────┼──────────────────────────────┼──────────────────┤
│ singleton    │ One instance per container   │ Stateless beans  │
│ prototype    │ New instance per request     │ Stateful beans   │
│ request      │ One per HTTP request         │ Web context      │
│ session      │ One per HTTP session         │ Web context      │
│ application  │ One per ServletContext       │ Web context      │
│ websocket    │ One per WebSocket session    │ WebSocket        │
└──────────────┴──────────────────────────────┴──────────────────┘
```

#### 1. Singleton Scope (Default)

```java
@Component
// OR
@Scope("singleton")
public class SingletonBean {
    private int counter = 0;
    
    public void increment() {
        counter++;
    }
    
    public int getCounter() {
        return counter;
    }
}

@SpringBootTest
public class SingletonTest {
    @Test
    public void testSingleton() {
        ApplicationContext context = new AnnotationConfigApplicationContext(SingletonBean.class);
        
        SingletonBean bean1 = context.getBean(SingletonBean.class);
        SingletonBean bean2 = context.getBean(SingletonBean.class);
        
        bean1.increment();
        bean1.increment();
        
        // Same instance, counter is 2
        assertEquals(2, bean2.getCounter());  // ✓ PASS
        assertTrue(bean1 == bean2);           // ✓ PASS (same object)
    }
}
```

**Characteristics:**
- One instance for entire application
- Shared across all requests
- Thread-safe access required
- Best for stateless services

**Singleton Gotcha (with stateful dependencies):**

```java
@Component
@Scope("singleton")
public class StatefulService {
    private String user;  // DANGER: Shared across requests!
    
    public void process(String user) {
        this.user = user;
        // ...
    }
    
    public String getUser() {
        return user;
    }
}

// PROBLEM: If two threads call process(), user field gets overwritten
// Solution: Use ThreadLocal or make it stateless
```

#### 2. Prototype Scope

```java
@Component
@Scope("prototype")
public class PrototypeBean {
    private int counter = 0;
    
    public void increment() {
        counter++;
    }
}

@SpringBootTest
public class PrototypeTest {
    @Test
    public void testPrototype() {
        ApplicationContext context = new AnnotationConfigApplicationContext(PrototypeBean.class);
        
        PrototypeBean bean1 = context.getBean(PrototypeBean.class);
        PrototypeBean bean2 = context.getBean(PrototypeBean.class);
        
        bean1.increment();
        bean1.increment();
        
        bean2.increment();
        
        assertEquals(2, bean1.getCounter());  // ✓ PASS
        assertEquals(1, bean2.getCounter());  // ✓ PASS (different instances)
        assertTrue(bean1 != bean2);           // ✓ PASS (different objects)
    }
}
```

**Characteristics:**
- New instance per request
- No shared state
- More memory usage
- Good for stateful beans

#### 3. Request Scope (Web Only)

```java
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestScopedBean {
    private String requestId;
    
    @PostConstruct
    public void init() {
        this.requestId = UUID.randomUUID().toString();
    }
    
    public String getRequestId() {
        return requestId;
    }
}

@RestController
public class RequestController {
    @Autowired
    private RequestScopedBean requestBean1;
    
    @Autowired
    private RequestScopedBean requestBean2;
    
    @GetMapping("/test")
    public String test() {
        // requestBean1 and requestBean2 are different instances within same request
        // but same instance across all controllers in this request
        return "ID: " + requestBean1.getRequestId();
    }
}

// Each HTTP request gets new RequestScopedBean instance
// Request 1: new instance A
// Request 2: new instance B
// Request 1 again: new instance C (not A)
```

**Important:** Need `proxyMode` to inject into singleton beans!

#### 4. Session Scope (Web Only)

```java
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionScopedBean {
    private Map<String, Object> userSession = new HashMap<>();
    
    public void setAttribute(String key, Object value) {
        userSession.put(key, value);
    }
    
    public Object getAttribute(String key) {
        return userSession.get(key);
    }
}

@RestController
public class SessionController {
    @Autowired
    private SessionScopedBean sessionBean;
    
    @GetMapping("/login")
    public String login() {
        sessionBean.setAttribute("userId", 123);
        return "Logged in";
    }
    
    @GetMapping("/profile")
    public String getProfile() {
        Integer userId = (Integer) sessionBean.getAttribute("userId");
        return "User: " + userId;
    }
}

// Same user across multiple requests has same SessionScopedBean instance
// Different users have different instances
```

#### Scope Lifetime Comparison:

```
Scope       │ Instance Lifetime
────────────┼──────────────────────────────────────
singleton   │ Application startup → Application shutdown
prototype   │ Each getBean() call
request     │ HTTP request received → Response sent
session     │ Session created → Session invalidated
application │ Application startup → Application shutdown
websocket   │ WebSocket connected → WebSocket closed
```

#### Production Best Practice:

```java
// ✓ GOOD: Singleton with immutable dependencies
@Service
public class UserService {
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public User getUser(Long id) {
        return userRepository.findById(id);
    }
}

// ✗ BAD: Singleton with mutable state
@Service  // Singleton by default!
public class BadService {
    private String currentUser;  // DANGER!
    
    public void processUser(String username) {
        this.currentUser = username;  // Race condition in multi-threaded!
    }
}

// ✓ GOOD: Prototype with mutable state
@Service
@Scope("prototype")
public class GoodService {
    private String currentUser;  // Safe now
    
    public void processUser(String username) {
        this.currentUser = username;
    }
}
```

---

## Q5: How does Spring resolve circular dependencies?

### Answer

**Circular Dependency** occurs when Bean A depends on Bean B, and Bean B depends on Bean A.

```
Classic Circular Dependency:
    A ──depends on──> B
    ↑                │
    └────depends on──┘
```

#### Example of Circular Dependency:

```java
@Component
public class ServiceA {
    private ServiceB serviceB;
    
    @Autowired
    public ServiceA(ServiceB serviceB) {  // Needs B
        this.serviceB = serviceB;
    }
}

@Component
public class ServiceB {
    private ServiceA serviceA;
    
    @Autowired
    public ServiceB(ServiceA serviceA) {  // Needs A
        this.serviceA = serviceA;
    }
}

// ERROR: BeanCurrentlyInCreationException!
// Can't create ServiceA without ServiceB
// Can't create ServiceB without ServiceA
```

#### How Spring Resolves Circular Dependencies:

Spring uses **Three Levels of Caches**:

```
Spring Bean Creation Process with Caches:

┌────────────────────────────────────────────────────────────┐
│  SPRING SINGLETON CACHE STRATEGY                           │
├────────────────────────────────────────────────────────────┤
│                                                            │
│  singletonObjects (L1 Cache)                              │
│  └─ Fully initialized beans ready to use                  │
│                                                            │
│  earlySingletonObjects (L2 Cache)                         │
│  └─ Partially initialized beans (after instantiation)     │
│     └─ Used to resolve circular dependencies              │
│                                                            │
│  singletonFactories (L3 Cache)                            │
│  └─ ObjectFactory that can create bean                    │
│     └─ Called to create early singleton if needed         │
│                                                            │
└────────────────────────────────────────────────────────────┘
```

#### Step-by-Step Resolution:

```
Scenario: ServiceA depends on ServiceB, ServiceB depends on ServiceA

STEP 1: Create ServiceA
    ├─ ServiceA instance created (not initialized)
    ├─ Add ServiceA to earlySingletonObjects (L2 cache)
    └─ (Now other beans can reference this partial ServiceA)

STEP 2: Inject ServiceB into ServiceA
    ├─ ServiceB not found in any cache
    ├─ Start creating ServiceB
    ├─ ServiceB instance created (not initialized)
    └─ Add ServiceB to earlySingletonObjects

STEP 3: Inject ServiceA into ServiceB
    ├─ ServiceA found in earlySingletonObjects (L2 cache!)
    ├─ Inject partial ServiceA into ServiceB
    └─ (This is the key - use incomplete ServiceA)

STEP 4: ServiceB initialization complete
    ├─ ServiceB moved to singletonObjects (L1 cache)
    └─ Initialization methods called

STEP 5: ServiceA initialization complete
    ├─ Now ServiceA has fully initialized ServiceB
    ├─ Initialization methods called
    └─ Move to singletonObjects

RESULT: ✓ Both beans properly initialized!
```

#### Setter Injection (Works with Circular Dependencies):

```java
@Component
public class ServiceA {
    private ServiceB serviceB;
    
    @Autowired
    public void setServiceB(ServiceB serviceB) {  // Setter injection
        this.serviceB = serviceB;
    }
}

@Component
public class ServiceB {
    private ServiceA serviceA;
    
    @Autowired
    public void setServiceA(ServiceA serviceA) {  // Setter injection
        this.serviceA = serviceA;
    }
}

// ✓ WORKS! Because:
// 1. ServiceA created without needing ServiceB
// 2. ServiceB created without needing ServiceA
// 3. ServiceA.setServiceB(serviceB) called
// 4. ServiceB.setServiceA(serviceA) called
```

#### Constructor Injection (FAILS with Circular Dependencies):

```java
@Component
public class ServiceA {
    private ServiceB serviceB;
    
    @Autowired
    public ServiceA(ServiceB serviceB) {  // Constructor injection
        this.serviceB = serviceB;
    }
}

@Component
public class ServiceB {
    private ServiceA serviceA;
    
    @Autowired
    public ServiceB(ServiceA serviceA) {  // Constructor injection
        this.serviceA = serviceA;
    }
}

// ✗ FAILS! BeanCurrentlyInCreationException
// Because both need full initialization before use
// Spring cannot use early singleton reference for constructor arg
```

#### Solutions for Circular Dependencies:

```
1. SETTER INJECTION (Preferred)
   ├─ Use @Autowired setter instead of constructor
   └─ Spring can handle circular dependencies

2. LAZY INITIALIZATION
   ├─ Use @Lazy annotation
   └─ Delay bean creation until first use
   
   @Component
   public class ServiceA {
       @Autowired
       private @Lazy ServiceB serviceB;
   }

3. BREAK THE CYCLE
   ├─ Refactor to eliminate circular dependency
   ├─ Introduce intermediate service
   └─ Best long-term solution

4. OBJECT PROVIDER
   ├─ Use ObjectProvider instead of direct injection
   └─ Defer resolution
   
   @Component
   public class ServiceA {
       private ObjectProvider<ServiceB> serviceBProvider;
       
       @Autowired
       public ServiceA(ObjectProvider<ServiceB> serviceBProvider) {
           this.serviceBProvider = serviceBProvider;
       }
       
       public void process() {
           ServiceB serviceB = serviceBProvider.getIfAvailable();
           // Use when needed
       }
   }

5. APPLICATION CONTEXT LOOKUP
   ├─ Look up bean from ApplicationContext at runtime
   └─ Last resort
   
   @Component
   public class ServiceA implements ApplicationContextAware {
       private ApplicationContext context;
       
       @Override
       public void setApplicationContext(ApplicationContext context) {
           this.context = context;
       }
       
       public void process() {
           ServiceB serviceB = context.getBean(ServiceB.class);
       }
   }
```

#### Best Practice:

```java
// ✓ BEST: Refactor to eliminate circular dependency
// Introduce a third component to break the cycle

@Component
public class UserService {
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}

@Component
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserFacade userFacade;
    
    public OrderService(OrderRepository orderRepository, UserFacade userFacade) {
        this.orderRepository = orderRepository;
        this.userFacade = userFacade;
    }
}

// New facade breaks the cycle
@Component
public class UserFacade {
    private final UserService userService;
    
    public UserFacade(UserService userService) {
        this.userService = userService;
    }
}

// No circular dependency!
```

---

## Q6: What is autowiring and its modes

### Answer

**Autowiring** is the mechanism for Spring to automatically resolve and inject dependencies without explicit XML configuration.

#### Autowiring Modes:

```
┌──────────────┬───────────────────────────────┬──────────────┐
│ Mode         │ Description                   │ How it Works │
├──────────────┼───────────────────────────────┼──────────────┤
│ no           │ No autowiring (default XML)   │ Manual       │
│ byName       │ Match by bean name            │ Property     │
│ byType       │ Match by type                 │ Property     │
│ constructor  │ Match by constructor param    │ Constructor  │
│ autodetect   │ Auto choose byType/byName     │ XML only     │
└──────────────┴───────────────────────────────┴──────────────┘
```

#### 1. No Autowiring (Default)

```java
<!-- XML Configuration -->
<bean id="userRepository" class="com.example.UserRepositoryImpl"/>
<bean id="userService" class="com.example.UserService">
    <constructor-arg ref="userRepository"/>
</bean>
```

#### 2. Byname Autowiring

```java
// Annotation-based (Equivalent to byName)
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;  // Matches bean name "userRepository"
}

@Repository
public class UserRepositoryImpl implements UserRepository {
    // Matches by name
}

// OR using @Qualifier
@Service
public class UserService {
    private UserRepository userRepository;
    
    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository repo) {
        this.userRepository = repo;
    }
}
```

#### 3. Bytype Autowiring

```java
// Most common annotation-based approach
@Service
public class UserService {
    private final UserRepository userRepository;
    
    @Autowired
    public UserService(UserRepository userRepository) {  // Matches by type
        this.userRepository = userRepository;
    }
}

@Repository
public class UserRepositoryImpl implements UserRepository {
    // Matches by type (UserRepository)
}

// Problem: Multiple implementations of same interface
@Repository
public class UserRepositoryImpl implements UserRepository { }

@Repository
public class MockUserRepositoryImpl implements UserRepository { }

// ERROR: No unique bean of type UserRepository is defined
// Solution: Use @Qualifier or @Primary
```

#### 4. Constructor Autowiring

```java
// Implicit constructor autowiring
@Service
public class UserService {
    private final UserRepository userRepository;
    
    // Spring automatically autowires via constructor
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}

// Can also be explicit with @Autowired
@Service
public class OrderService {
    private final UserService userService;
    
    @Autowired
    public OrderService(UserService userService) {
        this.userService = userService;
    }
}
```

#### Resolving Multiple Implementations:

```java
// Interface with multiple implementations
public interface PaymentProcessor {
    void process(Payment payment);
}

@Component
public class CreditCardProcessor implements PaymentProcessor {
    @Override
    public void process(Payment payment) { }
}

@Component
public class PayPalProcessor implements PaymentProcessor {
    @Override
    public void process(Payment payment) { }
}

// Solution 1: Using @Primary
@Component
@Primary
public class CreditCardProcessor implements PaymentProcessor {
    @Override
    public void process(Payment payment) { }
}

@Service
public class PaymentService {
    private final PaymentProcessor processor;
    
    public PaymentService(PaymentProcessor processor) {
        this.processor = processor;  // Gets CreditCardProcessor (primary)
    }
}

// Solution 2: Using @Qualifier
@Service
public class PaymentService {
    private final PaymentProcessor creditCardProcessor;
    private final PaymentProcessor paypalProcessor;
    
    public PaymentService(@Qualifier("creditCardProcessor") PaymentProcessor cc,
                         @Qualifier("paypalProcessor") PaymentProcessor pp) {
        this.creditCardProcessor = cc;
        this.paypalProcessor = pp;
    }
}

// Solution 3: Inject all implementations
@Service
public class PaymentService {
    private final List<PaymentProcessor> processors;
    
    public PaymentService(List<PaymentProcessor> processors) {
        this.processors = processors;  // Contains all implementations
    }
    
    public void processAllMethods(Payment payment) {
        processors.forEach(p -> p.process(payment));
    }
}

// Solution 4: Using Map with named beans
@Service
public class PaymentService {
    private final Map<String, PaymentProcessor> processors;
    
    public PaymentService(Map<String, PaymentProcessor> processors) {
        this.processors = processors;
    }
    
    public void process(String type, Payment payment) {
        PaymentProcessor processor = processors.get(type);
        processor.process(payment);
    }
}

// Usage:
// paymentService.process("creditCardProcessor", payment);
// paymentService.process("paypalProcessor", payment);
```

#### @Autowired Annotation Attributes:

```java
// required attribute
@Service
public class UserService {
    
    // required=true (default): Exception if not found
    @Autowired(required = true)
    private UserRepository userRepository;
    
    // required=false: Optional dependency, null if not found
    @Autowired(required = false)
    private EmailService emailService;
    
    public void sendEmail(String email) {
        if (emailService != null) {
            emailService.send(email);
        }
    }
}

// Using Optional instead
@Service
public class AnotherService {
    private final Optional<EmailService> emailService;
    
    @Autowired
    public AnotherService(Optional<EmailService> emailService) {
        this.emailService = emailService;
    }
    
    public void sendIfAvailable(String email) {
        emailService.ifPresent(es -> es.send(email));
    }
}
```

#### Collection Autowiring:

```java
@Service
public class EventService {
    
    // Autowires all EventListener beans in order
    private final List<EventListener> listeners;
    
    @Autowired
    public EventService(List<EventListener> listeners) {
        this.listeners = listeners;
    }
    
    public void publish(Event event) {
        listeners.forEach(l -> l.onEvent(event));
    }
}

// All EventListener implementations are injected
@Component
public class LoggingListener implements EventListener {
    @Override
    public void onEvent(Event event) { }
}

@Component
public class MetricsListener implements EventListener {
    @Override
    public void onEvent(Event event) { }
}
```

#### Best Practices:

```
1. PREFER CONSTRUCTOR INJECTION
   ├─ Makes dependencies explicit
   ├─ Enables immutability
   └─ Easier to test

2. USE @PRIMARY FOR DEFAULT IMPLEMENTATIONS
   ├─ Clear which bean is used by default
   └─ Reduces boilerplate

3. USE @QUALIFIER FOR SPECIFIC REQUIREMENTS
   ├─ Explicit about which implementation needed
   └─ Self-documenting code

4. INJECT COLLECTIONS FOR MULTIPLE IMPLEMENTATIONS
   ├─ Extensible design
   ├─ Strategies pattern
   └─ Plugin architecture

5. AVOID @Autowired ON FIELDS
   ├─ Harder to test
   ├─ Hidden dependencies
   └─ Mutable state concerns

6. USE Optional FOR OPTIONAL DEPENDENCIES
   ├─ Explicit about optionality
   └─ Safer than required=false
```

---

## Q7: How does Spring handle property injection?

### Answer

**Property Injection** allows externalizing configuration values from code to properties files, environment variables, or configuration servers.

#### Property Sources (Priority Order):

```
1. Command-line arguments (highest priority)
   └─ java -jar app.jar --server.port=8080

2. System properties (-D)
   └─ java -Dserver.port=8080 -jar app.jar

3. Environment variables
   └─ export SERVER_PORT=8080

4. application.properties or application.yml

5. application-{profile}.properties

6. Default values (lowest priority)
   └─ @Value("${property:defaultValue}")
```

#### 1. Using @Value Annotation:

```java
@Configuration
public class AppConfig {
    
    // Simple property
    @Value("${app.name}")
    private String appName;
    
    // With default value
    @Value("${app.version:1.0.0}")
    private String appVersion;
    
    // Environment variable
    @Value("${DATABASE_URL}")
    private String databaseUrl;
    
    // System property
    @Value("${java.version}")
    private String javaVersion;
    
    // Expression
    @Value("#{systemProperties['user.home']}")
    private String userHome;
    
    // List property
    @Value("${app.allowed-hosts}")
    private List<String> allowedHosts;
    
    // Map property
    @Value("#{ T(java.util.Arrays).asList('${app.servers}'.split(',')) }")
    private List<String> servers;
}

// application.properties
/*
app.name=MyApplication
app.version=2.0.0
DATABASE_URL=jdbc:mysql://localhost:3306/mydb
app.allowed-hosts=localhost,example.com
app.servers=server1,server2,server3
*/
```

#### 2. Using @ConfigurationProperties:

```java
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String name;
    private String version;
    private int port;
    private Database database = new Database();
    private List<String> allowedHosts = new ArrayList<>();
    
    // Getters and setters
    public static class Database {
        private String url;
        private String username;
        private String password;
        
        // Getters and setters
    }
}

// application.yml
/*
app:
  name: MyApplication
  version: 2.0.0
  port: 8080
  database:
    url: jdbc:mysql://localhost:3306/mydb
    username: root
    password: secret
  allowedHosts:
    - localhost
    - example.com
*/

@Service
public class UserService {
    private final AppProperties appProperties;
    
    public UserService(AppProperties appProperties) {
        this.appProperties = appProperties;
    }
    
    public void printConfig() {
        System.out.println("App: " + appProperties.getName());
        System.out.println("DB URL: " + appProperties.getDatabase().getUrl());
    }
}
```

#### 3. Using Environment:

```java
@Component
public class ConfigReader {
    private final Environment env;
    
    @Autowired
    public ConfigReader(Environment env) {
        this.env = env;
    }
    
    public void readConfig() {
        String appName = env.getProperty("app.name");
        int port = env.getProperty("app.port", Integer.class);
        String dbUrl = env.getProperty("DATABASE_URL");
        
        // Check if property exists
        if (env.containsProperty("feature.enabled")) {
            boolean enabled = env.getProperty("feature.enabled", Boolean.class);
        }
        
        // Get active profiles
        String[] profiles = env.getActiveProfiles();
    }
}
```

#### 4. Property Profiles:

```java
// application.properties (default)
app.name=MyApp
spring.datasource.url=jdbc:mysql://localhost:3306/mydb

// application-dev.properties
spring.datasource.url=jdbc:mysql://localhost:3306/mydb_dev
logging.level.root=DEBUG

// application-prod.properties
spring.datasource.url=jdbc:mysql://prod-db:3306/mydb
logging.level.root=INFO

@Configuration
@Profile("dev")
public class DevConfig {
    @Bean
    public DataSource dataSource() {
        // Dev datasource
    }
}

@Configuration
@Profile("prod")
public class ProdConfig {
    @Bean
    public DataSource dataSource() {
        // Prod datasource
    }
}

// Activate profile:
// java -jar app.jar --spring.profiles.active=prod
// OR in application.properties:
// spring.profiles.active=prod
```

#### 5. Spring Cloud Config:

```java
// application.yml
spring:
  cloud:
    config:
      uri: http://config-server:8888
      name: myapp
      
spring:
  config:
    import: "configserver:"

@Component
@RefreshScope  // Allows updating properties without restart
public class DynamicConfig {
    @Value("${feature.flag}")
    private boolean featureFlag;
    
    public void handleRequest() {
        if (featureFlag) {
            // Feature enabled
        }
    }
}

// Update config on server:
// POST /actuator/refresh  → Reloads @RefreshScope beans
```

#### 6. Type-Safe Configuration:

```java
@ConfigurationProperties(prefix = "database")
@Validated
public class DatabaseProperties {
    
    @NotBlank
    private String url;
    
    @NotBlank
    private String username;
    
    @Min(0)
    @Max(100)
    private int poolSize = 10;
    
    @Email
    private String adminEmail;
    
    // Nested objects
    @Valid
    private Connection connection = new Connection();
    
    public static class Connection {
        @Positive
        private int timeout = 30;
        
        @NotNull
        private ConnectionType type;
    }
    
    // Getters and setters
}

// application.yml
database:
  url: jdbc:mysql://localhost:3306/mydb
  username: root
  poolSize: 20
  adminEmail: admin@example.com
  connection:
    timeout: 60
    type: TCP
```

#### Property Placeholder Resolution:

```java
// Referencing other properties
app.name=MyApp
app.version=1.0.0
app.fullName=${app.name}-${app.version}  // MyApp-1.0.0

@Value("${app.fullName}")
private String fullName;

// Conditional defaults
@Value("${app.profile:default}")
private String profile;

// Expression with SpEL
@Value("#{T(java.lang.Math).random() * 100}")
private double randomValue;

@Value("#{systemProperties['java.home']}")
private String javaHome;

// Environment variable access
@Value("${HOME:defaultHome}")
private String homeDir;
```

#### Best Practices:

```java
// ✓ GOOD: Centralized configuration
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private String appName;
    private Database database = new Database();
    
    // Type-safe configuration
}

// ✗ BAD: Scattered @Value annotations
@Component
public class ScatteredConfig {
    @Value("${app.name}")
    private String name;
    
    @Value("${db.url}")
    private String dbUrl;
    
    // Hard to maintain, no validation
}

// ✓ GOOD: Profile-specific configuration
// - application.properties (common)
// - application-dev.properties (dev overrides)
// - application-prod.properties (prod overrides)

// ✓ GOOD: Environment for dynamic lookup
@Component
public class DynamicLookup {
    @Autowired
    private Environment env;
    
    public String getConfig(String key) {
        return env.getProperty(key);
    }
}

// ✓ GOOD: Type validation
@ConfigurationProperties(prefix = "app")
@Validated
public class ValidatedConfig {
    @NotBlank
    private String name;
    
    @Positive
    private int port;
}
```

---

# Part 2: Spring MVC & Web Layer

---

## Q8: Explain the DispatcherServlet and its role

### Answer

**DispatcherServlet** is the front controller (entry point) for all HTTP requests in a Spring MVC application. It's a servlet that dispatches requests to appropriate handlers.

#### Request Processing Flow:

```
HTTP Request
    ↓
DispatcherServlet.doService()
    ↓
DispatcherServlet.doDispatch()
    ├─ HandlerMapping (find handler)
    ├─ HandlerAdapter (execute handler)
    ├─ ViewResolver (resolve view)
    └─ Response rendered
    ↓
HTTP Response
```

#### Detailed Sequence Diagram:

```
CLIENT REQUEST
    │
    ↓
┌─────────────────────────────────────────────┐
│         DispatcherServlet                   │
│  (Receives ALL requests for *.htm, /, etc)  │
└─────────────────────────────────────────────┘
    │
    ├─ Pre-process request
    │  (Attribute resolution, locale, theme)
    │
    ├─ Find Handler
    │  ↓
    │  ┌──────────────────────────┐
    │  │   HandlerMapping         │
    │  │ (Finds matching handler) │
    │  └──────────────────────────┘
    │  └─→ @RequestMapping("/users")
    │
    ├─ Execute Handler
    │  ↓
    │  ┌──────────────────────────┐
    │  │   HandlerAdapter         │
    │  │ (Calls handler method)   │
    │  └──────────────────────────┘
    │  └─→ userController.getUsers()
    │
    ├─ Post-process Response
    │  (Handler Interceptor.postHandle)
    │
    ├─ Resolve View
    │  ↓
    │  ┌──────────────────────────┐
    │  │   ViewResolver           │
    │  │ (String → View object)   │
    │  └──────────────────────────┘
    │  └─→ "users" → UserView
    │
    ├─ Render View
    │  (View generates response)
    │
    └─ Return Response
       │
       ↓
    RESPONSE TO CLIENT
```

#### DispatcherServlet Configuration:

```java
// Spring Boot: Auto-configured
// Can be customized if needed

@Configuration
public class DispatcherServletConfig {
    
    @Bean
    public DispatcherServlet dispatcherServlet(ApplicationContext applicationContext) {
        DispatcherServlet servlet = new DispatcherServlet(applicationContext);
        servlet.setThrowExceptionIfNoHandlerFound(true);
        servlet.setDispatchOptionsRequest(true);
        return servlet;
    }
}

// web.xml (Traditional approach)
/*
<servlet>
    <servlet-name>dispatcher</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>/WEB-INF/spring-mvc.xml</param-value>
    </init-param>
</servlet>

<servlet-mapping>
    <servlet-name>dispatcher</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>
*/
```

#### Key Components Used by DispatcherServlet:

```java
// 1. HandlerMapping: Maps URLs to handlers
@Configuration
public class MappingConfig {
    @Bean
    public HandlerMapping handlerMapping() {
        // Default: RequestMappingHandlerMapping (@RequestMapping)
        return new RequestMappingHandlerMapping();
    }
}

// 2. HandlerAdapter: Executes the handler
@Configuration
public class AdapterConfig {
    @Bean
    public HandlerAdapter handlerAdapter() {
        // Default: RequestMappingHandlerAdapter
        return new RequestMappingHandlerAdapter();
    }
}

// 3. ViewResolver: Resolves view names to View objects
@Configuration
public class ViewConfig {
    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/jsp/");
        resolver.setSuffix(".jsp");
        return resolver;
    }
}

// 4. Interceptors: Pre and post-request handling
@Component
public class LoggingInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
                            Object handler) throws Exception {
        System.out.println("Before handler: " + request.getRequestURI());
        return true;  // Continue to next interceptor
    }
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                          Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("After handler, before view render");
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                               Object handler, Exception ex) throws Exception {
        System.out.println("After view render");
    }
}

// Register interceptor
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggingInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/static/**");
    }
}
```

#### Complete Request-Response Cycle:

```java
// 1. Request arrives at DispatcherServlet
@RestController
@RequestMapping("/users")
public class UserController {
    
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return new User(id, "John");
    }
}

// 2. DispatcherServlet.doDispatch() execution:
public class DispatcherServlet extends FrameworkServlet {
    
    protected void doDispatch(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
        
        // 1. Get HandlerExecutionChain (handler + interceptors)
        HandlerExecutionChain mappedHandler = 
            getHandler(request);  // RequestMappingHandlerMapping
        
        // 2. Get HandlerAdapter
        HandlerAdapter ha = 
            getHandlerAdapter(mappedHandler.getHandler());
        
        // 3. Execute interceptor preHandle
        if (!mappedHandler.applyPreHandle(request, response)) {
            return;
        }
        
        // 4. Handle the request
        ModelAndView mv = ha.handle(request, response, 
                                   mappedHandler.getHandler());
        
        // 5. Execute interceptor postHandle
        mappedHandler.applyPostHandle(request, response, mv);
        
        // 6. Render view
        processDispatchResult(request, response, mappedHandler, mv, null);
    }
    
    private void processDispatchResult(HttpServletRequest request,
                                      HttpServletResponse response,
                                      HandlerExecutionChain mappedHandler,
                                      ModelAndView mv,
                                      Exception exception) throws Exception {
        
        // Get ViewResolver and resolve view name to View object
        View view;
        if (mv != null) {
            view = resolveViewName(mv.getViewName(), mv.getModelMap(), 
                                  request, response);
        }
        
        // Render the view
        view.render(mv.getModelMap(), request, response);
        
        // Trigger afterCompletion callbacks
        mappedHandler.triggerAfterCompletion(request, response, null);
    }
}
```

#### DispatcherServlet Attributes in Request:

```java
@RestController
public class RequestAttributesController {
    
    @GetMapping("/attributes")
    public void printAttributes(HttpServletRequest request) {
        // DispatcherServlet.HANDLER_MAPPING_ATTRIBUTE
        Object handler = request.getAttribute(
            DispatcherServlet.HANDLER_MAPPING_ATTRIBUTE);
        
        // DispatcherServlet.REQUEST_CONTEXT_ATTRIBUTE
        RequestContext context = (RequestContext) request.getAttribute(
            DispatcherServlet.REQUEST_CONTEXT_ATTRIBUTE);
        
        // DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE
        LocaleResolver localeResolver = (LocaleResolver) request.getAttribute(
            DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE);
        
        // DispatcherServlet.THEME_RESOLVER_ATTRIBUTE
        ThemeResolver themeResolver = (ThemeResolver) request.getAttribute(
            DispatcherServlet.THEME_RESOLVER_ATTRIBUTE);
    }
}
```

#### Exception Handling in DispatcherServlet:

```java
@Configuration
public class ExceptionHandlingConfig {
    
    @Bean
    public HandlerExceptionResolver handlerExceptionResolver() {
        return new ExceptionHandlerExceptionResolver() {
            @Override
            protected ModelAndView doResolveException(HttpServletRequest request,
                                                     HttpServletResponse response,
                                                     Object handler,
                                                     Exception ex) {
                if (ex instanceof EntityNotFoundException) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    return new ModelAndView("error/404");
                }
                return super.doResolveException(request, response, handler, ex);
            }
        };
    }
}

// Or using @ControllerAdvice
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }
}
```

#### Best Practices:

```java
// ✓ GOOD: Centralized request logging
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(
        RequestLoggingInterceptor.class);
    
    @Override
    public boolean preHandle(HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);
        
        logger.info("Request: {} {}", request.getMethod(), 
                   request.getRequestURI());
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request,
                               HttpServletResponse response,
                               Object handler,
                               Exception ex) throws Exception {
        long startTime = (Long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - startTime;
        
        logger.info("Response: {} {} ({}ms)", 
                   response.getStatus(), request.getRequestURI(), duration);
    }
}

// ✓ GOOD: Handler not found handling
@Configuration
public class DispatcherServletConfiguration {
    
    @Bean
    public DispatcherServlet dispatcherServlet(ApplicationContext applicationContext) {
        DispatcherServlet servlet = new DispatcherServlet(applicationContext);
        servlet.setThrowExceptionIfNoHandlerFound(true);  // Throw exception instead of sending 404
        servlet.setDispatchOptionsRequest(true);          // Handle OPTIONS requests
        servlet.setDispatchTraceRequest(true);            // Handle TRACE requests
        return servlet;
    }
}

// ✓ GOOD: Custom HandlerExceptionResolver
@Component
public class ApiExceptionResolver implements HandlerExceptionResolver {
    
    private static final Logger logger = LoggerFactory.getLogger(
        ApiExceptionResolver.class);
    
    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Object handler,
                                        Exception ex) {
        
        ApiErrorResponse errorResponse = buildErrorResponse(ex);
        
        response.setStatus(errorResponse.getStatus());
        response.setContentType("application/json;charset=UTF-8");
        
        try {
            response.getWriter().write(jsonSerialize(errorResponse));
        } catch (IOException e) {
            logger.error("Error writing error response", e);
        }
        
        return new ModelAndView();  // Return empty to indicate handled
    }
}
```

---

## Q9: What is the Spring MVC request processing flow?

### Answer

The **Request Processing Flow** in Spring MVC follows a specific sequence from request arrival to response transmission.

#### Complete Flow Sequence:

```
1. HTTP Request arrives at server
    ↓
2. Servlet container routes to DispatcherServlet
    ↓
3. DispatcherServlet receives request
    ↓
4. MultipartResolver checks for file upload
    ↓
5. LocaleResolver determines request locale
    ↓
6. ThemeResolver determines theme
    ↓
7. HandlerMapping finds handler
    ↓
8. HandlerInterceptor.preHandle()
    ↓
9. HandlerAdapter executes handler
    ├─ Controller method execution
    ├─ Method parameter binding
    └─ Return value processing
    ↓
10. HandlerInterceptor.postHandle()
    ↓
11. ViewResolver resolves view name
    ↓
12. View.render() (if not REST)
    ↓
13. HandlerInterceptor.afterCompletion()
    ↓
14. HTTP Response sent
```

#### Visual Diagram:

```
REQUEST CYCLE
═════════════════════════════════════════════════════════════

CLIENT
  │
  │ GET /users/1
  │
  ↓
┌─────────────────────────────┐
│  DispatcherServlet          │
│  (Front Controller)         │
└─────────────────────────────┘
  │
  ├─→ HandlerMapping
  │   └─→ RequestMappingHandlerMapping
  │       └─→ Matches @RequestMapping("/users/{id}")
  │           └─→ Returns HandlerExecutionChain
  │
  ├─→ Interceptor Chain (Pre-handle)
  │   ├─→ LoggingInterceptor.preHandle()
  │   ├─→ SecurityInterceptor.preHandle()
  │   └─→ TimingInterceptor.preHandle()
  │
  ├─→ HandlerAdapter
  │   └─→ RequestMappingHandlerAdapter
  │       └─→ Invokes Controller Method
  │           └─→ userController.getUser(1)
  │               └─→ Returns response data or ModelAndView
  │
  ├─→ Interceptor Chain (Post-handle)
  │   ├─→ LoggingInterceptor.postHandle()
  │   ├─→ SecurityInterceptor.postHandle()
  │   └─→ TimingInterceptor.postHandle()
  │
  ├─→ ViewResolver (if ModelAndView returned)
  │   └─→ InternalResourceViewResolver
  │       └─→ "users" → UserView
  │
  ├─→ View.render()
  │   └─→ Generates response body
  │
  ├─→ Interceptor Chain (After-completion)
  │   ├─→ LoggingInterceptor.afterCompletion()
  │   ├─→ SecurityInterceptor.afterCompletion()
  │   └─→ TimingInterceptor.afterCompletion()
  │
  ↓
RESPONSE
```

#### Step-by-Step Code Example:

```java
// ============ STEP 1-3: Request Routing ============
// HTTP Request: GET /users/1
// DispatcherServlet is configured in web.xml or Spring Boot

// ============ STEP 4: MultipartResolver ============
@Configuration
public class MultipartConfig {
    
    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setMaxUploadSize(10485760);  // 10MB
        return resolver;
    }
}

// ============ STEP 5: LocaleResolver ============
@Configuration
public class LocaleConfig implements WebMvcConfigurer {
    
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH);
        return resolver;
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LocaleChangeInterceptor())
                .addPathPatterns("/**");
    }
}

// ============ STEP 6: ThemeResolver ============
@Configuration
public class ThemeConfig {
    
    @Bean
    public ThemeResolver themeResolver() {
        CookieThemeResolver resolver = new CookieThemeResolver();
        resolver.setDefaultThemeName("light");
        return resolver;
    }
}

// ============ STEP 7: HandlerMapping ============
@RestController
@RequestMapping("/users")
public class UserController {
    
    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    // This mapping is discovered by RequestMappingHandlerMapping
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.findById(id);
    }
}

// ============ STEP 8: HandlerInterceptor.preHandle ============
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(
        AuthenticationInterceptor.class);
    
    @Override
    public boolean preHandle(HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler) throws Exception {
        logger.info("Pre-handle: {} {}", request.getMethod(), 
                   request.getRequestURI());
        
        // Check authentication
        String authToken = request.getHeader("Authorization");
        if (authToken == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, 
                             "Missing authorization");
            return false;  // Stop further processing
        }
        
        // Store user info in request for later use
        request.setAttribute("user", extractUser(authToken));
        return true;  // Continue to next interceptor
    }
}

// ============ STEP 9: HandlerAdapter executes handler ============
public class RequestMappingHandlerAdapter implements HandlerAdapter {
    
    @Override
    public ModelAndView handle(HttpServletRequest request,
                              HttpServletResponse response,
                              Object handler) throws Exception {
        
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        
        // 1. Resolve method arguments
        // - Extract @PathVariable("id") = 1
        // - Extract @RequestParam, @RequestBody
        // - Inject dependencies (Service, etc.)
        Object[] args = resolveMethodArguments(request, handlerMethod);
        
        // 2. Invoke controller method
        Object returnValue = handlerMethod.getMethod()
                                         .invoke(handlerMethod.getBean(), args);
        
        // 3. Process return value
        ModelAndView modelAndView = processReturnValue(returnValue, handlerMethod);
        
        return modelAndView;
    }
}

// ============ STEP 10: HandlerInterceptor.postHandle ============
@Component
public class ResponseInterceptor implements HandlerInterceptor {
    
    @Override
    public void postHandle(HttpServletRequest request,
                          HttpServletResponse response,
                          Object handler,
                          ModelAndView modelAndView) throws Exception {
        
        // Can modify response before view rendering
        if (modelAndView != null) {
            modelAndView.addObject("timestamp", System.currentTimeMillis());
            modelAndView.addObject("requestId", generateRequestId());
        }
    }
}

// ============ STEP 11: ViewResolver ============
@Configuration
public class ViewResolverConfig implements WebMvcConfigurer {
    
    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/jsp/");
        resolver.setSuffix(".jsp");
        return resolver;
    }
}

// ============ STEP 12: View.render() ============
// If not REST controller (@RestController = @Controller + @ResponseBody)
// Spring renders the view and generates HTML response

public class UserView implements View {
    
    @Override
    public void render(Map<String, ?> model, HttpServletRequest request,
                      HttpServletResponse response) throws Exception {
        User user = (User) model.get("user");
        
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write("<h1>" + user.getName() + "</h1>");
    }
}

// ============ STEP 13: Interceptor.afterCompletion ============
@Component
public class LoggingInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(
        LoggingInterceptor.class);
    
    @Override
    public void afterCompletion(HttpServletRequest request,
                               HttpServletResponse response,
                               Object handler,
                               Exception ex) throws Exception {
        
        if (ex != null) {
            logger.error("Request failed: {}", request.getRequestURI(), ex);
        } else {
            logger.info("Request completed: {} {}", request.getMethod(),
                       request.getRequestURI());
        }
    }
}

// ============ STEP 14: Response sent ============
// HTTP Response: 200 OK with JSON/HTML body
```

#### Request Processing Flow Diagram (UML Sequence):

```
Client                    DispatcherServlet        HandlerMapping
  │                             │                      │
  │──── HTTP GET /users/1 ────→ │                      │
  │                             │                      │
  │                             │─── getHandler() ──→ │
  │                             │                      │
  │                             │ ← HandlerExecutionChain
  │                             │
  │                             │─── Interceptor.preHandle()
  │                             │
  │                             │─── HandlerAdapter.handle()
  │                             │      ├─ Invoke controller
  │                             │      └─ Return ModelAndView
  │                             │
  │                             │─── Interceptor.postHandle()
  │                             │
  │                             │─── ViewResolver.resolveViewName()
  │                             │
  │                             │─── View.render()
  │                             │
  │                             │─── Interceptor.afterCompletion()
  │                             │
  │ ← HTTP 200 OK with body ─── │
  │
```

#### Complete Practical Example:

```java
// Scenario: GET /users/123 → returns JSON user object

// 1. Request arrives
// GET /users/123 HTTP/1.1
// Host: localhost:8080

// 2. DispatcherServlet routes to controller
@RestController
@RequestMapping("/users")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();  // 404
        }
        return ResponseEntity.ok(user);  // 200
    }
}

// 3. Request interceptors pre-process
@Component
public class AuthInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler) throws Exception {
        // Validate auth token
        String token = request.getHeader("Authorization");
        if (!isValidToken(token)) {
            response.sendError(401, "Unauthorized");
            return false;
        }
        return true;
    }
}

// 4. Method arguments are resolved
// @PathVariable Long id = 123

// 5. Controller method executes
// userService.findById(123L)

// 6. Response object is processed
// For @RestController: automatic JSON serialization
// ResponseEntity.ok(user) → HTTP 200 + JSON body

// 7. Interceptors post-process response

// 8. Response sent to client:
// HTTP/1.1 200 OK
// Content-Type: application/json;charset=UTF-8
// Content-Length: 156
//
// {"id":123,"name":"John Doe","email":"john@example.com"}
```

---

# Part 3: Spring Data & Database

---

## Q15: What is Spring Data JPA and how it works

### Answer

**Spring Data JPA** is a Spring Framework module that simplifies working with JPA (Java Persistence API) by providing a repository abstraction layer. It reduces boilerplate code significantly.

#### How Spring Data JPA Works:

```
1. Define Repository Interface (extends JpaRepository)
   ↓
2. Spring creates dynamic proxy implementation
   ↓
3. Query methods are analyzed
   ├─ Derived queries (method names)
   ├─ @Query custom queries
   └─ CRUD operations
   ↓
4. Queries are executed against database
   ↓
5. Results are mapped back to entities
```

#### Basic Repository Setup:

```java
// Entity
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(unique = true)
    private String email;
    
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
    
    // Getters and setters
}

// Repository Interface
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Spring Data generates implementations for these methods
    
    // Derived query methods
    User findByEmail(String email);
    List<User> findByName(String name);
    List<User> findByDepartmentId(Long departmentId);
    List<User> findByNameContainingIgnoreCase(String namePattern);
    
    // Pagination
    Page<User> findByDepartmentId(Long departmentId, Pageable pageable);
    
    // Sorting
    List<User> findByDepartmentIdOrderByNameAsc(Long departmentId);
    
    // Custom query
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    User findUserByEmail(String email);
    
    // Native SQL query
    @Query(value = "SELECT * FROM users WHERE name LIKE %?1%", 
           nativeQuery = true)
    List<User> searchUsersByName(String name);
}

// Usage in service
@Service
public class UserService {
    
    private final UserRepository userRepository;
    
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    // CRUD operations automatically available
    public User createUser(User user) {
        return userRepository.save(user);
    }
    
    public Optional<User> getUser(Long id) {
        return userRepository.findById(id);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    // Custom queries
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public List<User> searchUsers(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }
}
```

#### Query Derivation (Method Names):

```
Keyword           │ Sample Syntax                │ SQL Equivalent
──────────────────┼──────────────────────────────┼─────────────────────
And               │ findByNameAndEmail()         │ WHERE name=? AND email=?
Or                │ findByNameOrEmail()          │ WHERE name=? OR email=?
Is, Equals        │ findByName(), findById()     │ WHERE name=? 
Between           │ findByAgeBetween(min, max)   │ WHERE age BETWEEN ? AND ?
LessThan          │ findByAgeLessThan(age)       │ WHERE age < ?
LessThanEqual     │ findByAgeLessThanEqual(age)  │ WHERE age <= ?
GreaterThan       │ findByAgeGreaterThan(age)    │ WHERE age > ?
GreaterThanEqual  │ findByAgeGreaterThanEqual()  │ WHERE age >= ?
After             │ findByCreatedDateAfter()     │ WHERE created_date > ?
Before            │ findByCreatedDateBefore()    │ WHERE created_date < ?
IsNull            │ findByDeletedAtIsNull()      │ WHERE deleted_at IS NULL
IsNotNull         │ findByDeletedAtIsNotNull()   │ WHERE deleted_at IS NOT NULL
Like              │ findByNameLike()             │ WHERE name LIKE ?
NotLike           │ findByNameNotLike()          │ WHERE name NOT LIKE ?
StartingWith      │ findByNameStartingWith()     │ WHERE name LIKE ?%
EndingWith        │ findByNameEndingWith()       │ WHERE name LIKE %?
Containing        │ findByNameContaining()       │ WHERE name LIKE %?%
In                │ findByIdIn(List ids)         │ WHERE id IN (?, ?, ?)
NotIn             │ findByIdNotIn(List ids)      │ WHERE id NOT IN (?, ?, ?)
IgnoreCase        │ findByNameIgnoreCase()       │ WHERE LOWER(name)=LOWER(?)
Not               │ findByNameNot(name)          │ WHERE name != ?
Distinct          │ findDistinctByName()         │ SELECT DISTINCT
OrderBy           │ findByNameOrderByIdDesc()    │ ORDER BY id DESC
True/False        │ findByActiveTrue()           │ WHERE active = true
```

#### Advanced Query Examples:

```java
public interface UserRepository extends JpaRepository<User, Long>, 
                                       JpaSpecificationExecutor<User> {
    
    // ============ Paging and Sorting ============
    
    // Page vs List
    Page<User> findByDepartmentId(Long deptId, Pageable pageable);
    // Returns: page number, total count, has next/previous
    
    Slice<User> findByNameStartingWith(String prefix, Pageable pageable);
    // Returns: has next/previous but no total count (faster)
    
    List<User> findByDepartmentId(Long deptId, Sort sort);
    
    
    // ============ Batch Operations ============
    
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.name = ?1 WHERE u.department.id = ?2")
    int updateUsersByDepartment(String newName, Long departmentId);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM User u WHERE u.department.id = ?1")
    void deleteByDepartmentId(Long departmentId);
    
    @Transactional
    long deleteByEmailContaining(String emailPattern);  // Returns deleted count
    
    
    // ============ Projection (Fetch specific columns) ============
    
    // DTO interface projection
    public interface UserDTO {
        Long getId();
        String getName();
        String getEmail();
    }
    
    List<UserDTO> findByDepartmentId(Long deptId);
    // Queries only: id, name, email (not entire entity)
    
    
    // ============ Custom Implementation ============
    
    // Implement custom logic for repository
}

public interface UserRepositoryCustom {
    List<User> findUsersWithComplexCriteria(UserSearchCriteria criteria);
}

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public List<User> findUsersWithComplexCriteria(UserSearchCriteria criteria) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);
        
        List<Predicate> predicates = new ArrayList<>();
        
        if (criteria.getName() != null) {
            predicates.add(cb.like(root.get("name"), 
                          "%" + criteria.getName() + "%"));
        }
        
        if (criteria.getDepartmentId() != null) {
            predicates.add(cb.equal(root.get("department").get("id"), 
                          criteria.getDepartmentId()));
        }
        
        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }
        
        return entityManager.createQuery(query).getResultList();
    }
}

// Combine standard and custom repository
public interface UserRepository extends JpaRepository<User, Long>,
                                       UserRepositoryCustom {
    // Has all Spring Data methods + custom methods
}
```

#### Spring Data JPA Lifecycle:

```
┌─────────────────────────────────────────────────────────┐
│  Spring Data JPA Repository Creation                    │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  1. ApplicationContext starts                           │
│     └─ Scans for @Repository interfaces                 │
│                                                         │
│  2. RepositoryFactoryBean processes interfaces          │
│     └─ Detects if extends JpaRepository                  │
│                                                         │
│  3. Query method inspection                             │
│     ├─ Analyzes method names (derived queries)          │
│     ├─ Parses @Query annotations                        │
│     └─ Creates QueryMethod objects                      │
│                                                         │
│  4. Query provider selection                            │
│     ├─ JPA Criteria Query for derived queries           │
│     ├─ JPQL for @Query annotation                       │
│     └─ Native SQL for nativeQuery=true                  │
│                                                         │
│  5. Dynamic proxy generation                            │
│     └─ Creates SimpleJpaRepository proxy instance       │
│                                                         │
│  6. Proxy registered in container                       │
│     └─ Available for dependency injection               │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

#### Internal Architecture:

```java
// What Spring Data does behind the scenes

@Repository
public class SimpleJpaRepository<T, ID> implements JpaRepository<T, ID> {
    
    @PersistenceContext
    private EntityManager em;
    
    private final JpaEntityInformation<T, ID> entityInformation;
    
    @Override
    public S save(S entity) {
        if (entityInformation.isNew(entity)) {
            em.persist(entity);  // INSERT
            return entity;
        } else {
            return em.merge(entity);  // UPDATE
        }
    }
    
    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(
            em.find(entityInformation.getJavaType(), id));
    }
    
    @Override
    public List<T> findAll() {
        return getQuery(null, Sort.unsorted()).getResultList();
    }
    
    @Override
    public Page<T> findAll(Pageable pageable) {
        long total = count();
        List<T> content = getQuery(null, pageable.getSort())
                            .setFirstResult((int) pageable.getOffset())
                            .setMaxResults(pageable.getPageSize())
                            .getResultList();
        return new PageImpl<>(content, pageable, total);
    }
    
    @Override
    public void deleteById(ID id) {
        T entity = em.find(entityInformation.getJavaType(), id);
        if (entity != null) {
            em.remove(entity);
        }
    }
}
```

#### Best Practices:

```java
// ✓ GOOD: Use repository interfaces for data access
@Service
public class UserService {
    private final UserRepository userRepository;
    
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }
}

// ✓ GOOD: Use pagination for large result sets
@GetMapping("/users")
public Page<UserDTO> listUsers(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
    return userRepository.findAll(pageable).map(UserDTO::from);
}

// ✗ BAD: Fetching all data at once
@GetMapping("/users")
public List<User> getAllUsers() {
    return userRepository.findAll();  // Can cause OutOfMemory on large dataset
}

// ✓ GOOD: Use custom queries for complex scenarios
@Query("SELECT NEW map(u.id as id, u.name as name, COUNT(o) as orderCount) " +
       "FROM User u LEFT JOIN u.orders o " +
       "GROUP BY u.id, u.name")
List<Map<String, Object>> getUserOrderStats();

// ✓ GOOD: Use @Modifying for bulk operations
@Transactional
@Modifying(clearAutomatically = true)
@Query("UPDATE User u SET u.lastLogin = now() WHERE u.id = ?1")
void updateLastLogin(Long userId);

// ✓ GOOD: Use Specifications for dynamic queries
Specification<User> spec = (root, query, cb) -> {
    List<Predicate> predicates = new ArrayList<>();
    if (name != null) {
        predicates.add(cb.like(root.get("name"), "%" + name + "%"));
    }
    if (departmentId != null) {
        predicates.add(cb.equal(root.get("department").get("id"), departmentId));
    }
    return cb.and(predicates.toArray(new Predicate[0]));
};
List<User> users = userRepository.findAll(spec);
```

---

# Part 4: Hibernate Deep Dive

Due to space constraints, I'll continue with key Hibernate questions. Let me provide comprehensive answers for the remaining sections:

---

## Q21: What is Hibernate and how does it work?

### Answer

**Hibernate** is an Object-Relational Mapping (ORM) framework that maps Java objects to database tables, allowing you to work with objects instead of SQL.

#### Hibernate Architecture:

```
┌─────────────────────────────────────────────────────────────┐
│  APPLICATION LAYER                                          │
│  (Your Java code using objects)                            │
└─────────────────┬───────────────────────────────────────────┘
                  │
┌─────────────────┴───────────────────────────────────────────┐
│  HIBERNATE ORM LAYER                                        │
├─────────────────────────────────────────────────────────────┤
│  ┌──────────────────┐  ┌────────────────────┐             │
│  │ Session/Entity   │  │ Query Language     │             │
│  │ Manager          │  │ (HQL, Criteria)    │             │
│  └──────────────────┘  └────────────────────┘             │
│        │                       │                           │
│  ┌─────┴──────────────────────┴──────────────────┐        │
│  │  MAPPING LAYER                                │        │
│  │  (Maps entities to tables)                    │        │
│  └─────┬──────────────────────────────────────────┘       │
│        │                                                   │
│  ┌─────┴──────────────────────────────────────────┐       │
│  │  PERSISTENCE LAYER                            │       │
│  │  (JDBC, Caching, Transactions)                │       │
│  └─────┬──────────────────────────────────────────┘       │
└────────┼──────────────────────────────────────────────────┘
         │
┌────────┴──────────────────────────────────────────────────┐
│  DATABASE LAYER                                            │
│  (MySQL, PostgreSQL, Oracle, etc)                          │
└────────────────────────────────────────────────────────────┘
```

#### How Hibernate Works (Step-by-Step):

```
1. CONFIGURATION
   ├─ Load hibernate.properties/cfg.xml
   ├─ Register database driver
   └─ Initialize SessionFactory

2. ENTITY MAPPING
   ├─ Read @Entity annotations
   ├─ Map entity to table
   └─ Map properties to columns

3. SESSION CREATION
   ├─ Create Session from SessionFactory
   └─ Get database connection

4. ENTITY OPERATIONS
   ├─ Persist (INSERT): session.save()
   ├─ Retrieve (SELECT): session.get()
   ├─ Update (UPDATE): session.update()
   └─ Delete (DELETE): session.delete()

5. LAZY LOADING
   ├─ Don't load related entities immediately
   └─ Load when accessed

6. DIRTY CHECKING
   ├─ Track changes to entities
   └─ Auto-generate UPDATE queries

7. FLUSH & COMMIT
   ├─ Flush: Send pending changes to DB
   └─ Commit: Make changes permanent

8. CLOSE SESSION
   └─ Release resources, close connection
```

#### Complete Hibernate Example:

```java
// 1. Entity class
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_name", nullable = false, length = 100)
    private String name;
    
    @Column(unique = true)
    private String email;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Task> tasks = new HashSet<>();
    
    // Getters and setters
}

// 2. Configuration
@Configuration
public class HibernateConfig {
    
    @Bean
    public SessionFactory sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBuilder builder = new LocalSessionFactoryBuilder(dataSource);
        
        builder.scanPackages("com.example.entity")
               .addProperties(hibernateProperties());
        
        return builder.buildSessionFactory();
    }
    
    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.use_sql_comments", "true");
        properties.put("hibernate.jdbc.batch_size", "20");
        properties.put("hibernate.order_inserts", "true");
        properties.put("hibernate.order_updates", "true");
        return properties;
    }
}

// 3. Usage: Hibernate operations
@Repository
public class UserRepository {
    
    private final SessionFactory sessionFactory;
    
    @Autowired
    public UserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    // ============ CREATE ============
    public User save(User user) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(user);  // or session.persist()
            tx.commit();
            return user;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
    
    // ============ READ ============
    public User findById(Long id) {
        Session session = sessionFactory.openSession();
        try {
            // Session.get(): Loads immediately, returns null if not found
            return session.get(User.class, id);
        } finally {
            session.close();
        }
    }
    
    public User findByIdLazy(Long id) {
        Session session = sessionFactory.openSession();
        try {
            // Session.load(): Returns proxy, throws exception if not found
            return session.load(User.class, id);
        } finally {
            session.close();
        }
    }
    
    // ============ UPDATE ============
    public User update(User user) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.update(user);  // Explicit update
            tx.commit();
            return user;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
    
    public User updateDirtyCheck(User user) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            
            // Load user and modify properties
            User loadedUser = session.get(User.class, user.getId());
            loadedUser.setName(user.getName());
            loadedUser.setEmail(user.getEmail());
            // No explicit update needed! Dirty checking handles it.
            
            tx.commit();
            return loadedUser;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
    
    // ============ DELETE ============
    public void delete(Long id) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.delete(user);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
    
    // ============ QUERY ============
    public List<User> findAll() {
        Session session = sessionFactory.openSession();
        try {
            Query<User> query = session.createQuery("FROM User", User.class);
            return query.list();
        } finally {
            session.close();
        }
    }
    
    public List<User> findByName(String name) {
        Session session = sessionFactory.openSession();
        try {
            Query<User> query = session.createQuery(
                "FROM User WHERE name LIKE :name", User.class);
            query.setParameter("name", "%" + name + "%");
            return query.list();
        } finally {
            session.close();
        }
    }
    
    // ============ CRITERIA ============
    public List<User> findByCriteria(String name, Long departmentId) {
        Session session = sessionFactory.openSession();
        try {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<User> criteria = cb.createQuery(User.class);
            Root<User> root = criteria.from(User.class);
            
            List<Predicate> predicates = new ArrayList<>();
            if (name != null) {
                predicates.add(cb.like(root.get("name"), "%" + name + "%"));
            }
            if (departmentId != null) {
                predicates.add(cb.equal(root.get("department").get("id"), departmentId));
            }
            
            criteria.where(cb.and(predicates.toArray(new Predicate[0])));
            
            return session.createQuery(criteria).list();
        } finally {
            session.close();
        }
    }
}
```

#### Hibernate Workflow Diagram:

```
USER CODE                    HIBERNATE                      DATABASE
   │                            │                             │
   │  user = new User()         │                             │
   ├─────────────────────→      │                             │
   │  session.save(user)        │                             │
   ├─────────────────────→      │                             │
   │                            │  Dirty checking             │
   │                            ├─ Track changes             │
   │                            │                             │
   │  user.setName("New")       │                             │
   ├─────────────────────→      │                             │
   │  session.flush()           │  Generate SQL               │
   ├─────────────────────→      ├─ INSERT/UPDATE/DELETE      │
   │                            │  statements                 │
   │                            │                  SQL ─→     │
   │                            │                             │
   │  tx.commit()               │  Commit transaction         │
   ├─────────────────────→      ├─────────────────────────→  │
   │                            │                             │
   │  ← user with ID            │  ← Result set              │
```

---

I'll now create the complete file with all questions. Due to character limits, let me add the remaining essential sections:
<function_calls>
<invoke name="Bash">
<parameter name="command">wc -l "E:\Prakash\Github\Learning\Some Random Topics\Spring.md"