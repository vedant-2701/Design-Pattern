# Factory Pattern
> **Category:** Creational Design Pattern  
> **Difficulty:** ⭐⭐☆☆☆  
> **Last Reviewed:** 2026-02-18

---

## ⚡ Quick Summary
A pattern that delegates object creation to a dedicated factory class instead of using `new` directly.  
Use it when the exact type of object to create is determined at runtime, and you want the client completely decoupled from construction details.

---

## 🏠 Real-World Analogy
Think of a **vehicle manufacturing plant**. When you walk into a showroom and say
*"I want an SUV"*, you don't build the SUV yourself — you don't worry about the engine
assembly, wiring, or painting process. You just tell the showroom what you want, and
the **factory behind the scenes** figures out which machinery, workers, and parts to use,
then hands you the finished vehicle.

The Factory Pattern works exactly the same way:
**you ask for an object by type, and the factory decides how to create and return
the right one — you never deal with the construction details.**

---

## 📖 Theory — What & Why

**What it is:**  
A creational design pattern that defines an interface for creating objects, but lets
subclasses or a dedicated factory class decide which class to instantiate. The client
only talks to the factory, never to the concrete classes directly.

**Problem it solves:**  
In large systems, object creation logic becomes complex — different environments,
configurations, or user types need different implementations of the same interface.
Scattering `new ConcreteClass()` calls everywhere makes the code tightly coupled and
a nightmare to change. The Factory centralizes and hides that decision.

**Real-world usage (Big Tech):**  
- **Amazon Payment Gateway** — a factory decides whether to create a `CreditCardProcessor`,
  `UPIProcessor`, or `WalletProcessor` based on the payment method — the checkout flow never knows the difference.
- **Java's `Calendar.getInstance()`** — returns different `Calendar` subclass implementations
  depending on the locale and timezone.
- **SLF4J (Java Logging)** — `LoggerFactory.getLogger()` returns the right logger
  implementation without the caller knowing which logging backend is configured.

---

## 🗺️ Architecture Diagram
```mermaid
graph TD
    Client -->|"create(type)"| Factory[Factory Class]

    Factory -->|type = EMAIL| A[EmailNotifier]
    Factory -->|type = SMS  | B[SMSNotifier]
    Factory -->|type = PUSH | C[PushNotifier]

    A -->|implements| I[NotificationChannel Interface]
    B -->|implements| I
    C -->|implements| I

    I --> Result[".send() called uniformly by Client"]
```

---

## 💻 Implementations

| Language   | Scenario                              | File |
|------------|---------------------------------------|------|
| TypeScript | Notification Service — SaaS App       | [typescript/NotificationFactory.ts](./typescript/NotificationFactory.ts) |
| Java       | Vehicle Dispatcher — Ride-Sharing App | [java/VehicleFactory.java](./java/VehicleFactory.java) |

---

## ⚖️ Trade-offs & Bottlenecks

| Dimension | Problem | Fix |
|-----------|---------|-----|
| Open/Closed Violation | Adding a new type means editing the factory's `switch`/`registry` — modifying existing code | Use a registry-based factory — new types self-register, factory never changes |
| Single Responsibility | Factory grows large when it handles complex construction logic for many types | Delegate heavy construction to Builder Pattern inside the factory method |
| Testing | Concrete classes created inside factory can't be easily mocked | Inject a factory interface instead of calling the static method directly; mock the factory in tests |
| Type Explosion | Too many subtypes makes the factory a long decision tree | Group related types and use Abstract Factory — a factory of factories |
| Stateless Assumption | Simple Factory assumes all products of a type are identical | If products carry unique state, pass parameters into the factory method |

---

## 🚨 Common Mistakes to Avoid
- Calling `new ConcreteClass()` directly in client code — defeats the entire purpose of the pattern
- Putting business logic inside the factory — factory must only handle creation, nothing else
- Using a giant `if-else` chain instead of a registry map — hard to extend and read
- Forgetting to throw a clear error for unknown types — silent failures are worse than loud ones
- Confusing Simple Factory (one class, one method) with Factory Method Pattern (subclasses override creation)

---

## 🔗 Related Concepts
- `01_Singleton` — Factory itself is often implemented as a Singleton so only one factory instance exists
- `03_Abstract_Factory` — A factory of factories; used when you have families of related objects
- `04_Builder` — Use Builder inside Factory when object construction requires many steps or parameters
- `Dependency Injection` — Modern frameworks use DI containers that act as smart, configurable factories

---

## ❓ Knowledge Check (Answer from memory before reading answer)

> You are extending the Ride-Sharing app. A new requirement arrives: **Premium Cab**
> must behave exactly like `Cab` but with a surge pricing multiplier that changes
> every 15 minutes based on demand.
>
> **If you add `PREMIUM_CAB` directly into `VehicleFactory`, which design principle
> are you violating and why? What is the exact change you would make to the factory
> to fix this — without touching the existing `Cab` class or the `VehicleFactory`'s
> core logic?**

**Your Answer (fill this after attempting):**  
...

**Hint (reveal only if stuck):**  
Think about the Open/Closed Principle — open for extension, closed for modification.
Can a new type register itself into the factory instead of the factory knowing about it?