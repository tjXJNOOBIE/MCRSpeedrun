# Concrete Dependency Injection Guide

This guide explains how to use the new concrete dependency injection system that has been implemented to decouple interfaces from concrete classes while maintaining the existing dependency injection pattern.

## Overview

The system now provides both interface-based and concrete class-based dependency injection:

- **Interface-based**: For compatibility and abstraction
- **Concrete class-based**: For direct access to concrete implementations
- **Dual registration**: Both interfaces and concrete classes are registered in the dependency map

## Key Components

### 1. Concrete Context Classes

#### ConcreteGlobalContext
- Located: `com.tjxjnoobie.api.managers.ConcreteGlobalContext`
- Purpose: Global dependency injection for API-level components
- Features:
  - Implements `IGlobalContext` for interface compatibility
  - Stores concrete class instances internally
  - Provides both interface and concrete getters
  - Dual registration of dependencies

#### ConcreteSpeedRunContext
- Located: `com.tjxjnoobie.speed.managers.ConcreteSpeedRunContext`
- Purpose: SpeedRun-specific dependency injection
- Features:
  - Implements `ISpeedRunContext` for interface compatibility
  - Includes SpeedRun-specific managers and components
  - Plugin-aware dependency injection

### 2. Factory Classes

#### DependencyFactory
- Located: `com.tjxjnoobie.api.factories.DependencyFactory`
- Purpose: Creates and configures ConcreteGlobalContext instances
- Methods:
  - `createGlobalContext()`: Full context with all dependencies
  - `createMinimalGlobalContext()`: Essential dependencies only
  - `createCustomGlobalContext()`: Configurable dependency inclusion

#### SpeedRunDependencyFactory
- Located: `com.tjxjnoobie.speed.factories.SpeedRunDependencyFactory`
- Purpose: Creates and configures ConcreteSpeedRunContext instances
- Methods:
  - `createSpeedRunContext(Plugin)`: Full SpeedRun context
  - `createMinimalSpeedRunContext(Plugin)`: Essential dependencies only
  - `createCustomSpeedRunContext()`: Configurable dependency inclusion

## Usage Examples

### Basic Usage - Global Context

```java
// Create a full global context
ConcreteGlobalContext context = DependencyFactory.createGlobalContext();

// Access via interfaces (for compatibility)
IGameMode gameMode = context.getGameMode();
IUtils utils = context.getUtils();

// Access via concrete classes (for direct access)
GameMode concreteGameMode = context.getConcreteGameMode();
Utils concreteUtils = context.getConcreteUtils();

// Validate the context
if (DependencyFactory.validateContext(context)) {
    System.out.println("Context is valid!");
}
```

### Basic Usage - SpeedRun Context

```java
// In your main plugin class
public class SpeedRunPlugin extends JavaPlugin {
    private ConcreteSpeedRunContext context;
    
    @Override
    public void onEnable() {
        // Create a full SpeedRun context
        context = SpeedRunDependencyFactory.createSpeedRunContext(this);
        
        // Access managers
        GameManager gameManager = context.getConcreteGameManager();
        PlayerManager playerManager = context.getConcretePlayerManager();
        
        // Validate and print summary
        if (SpeedRunDependencyFactory.validateContext(context)) {
            SpeedRunDependencyFactory.printContextSummary(context);
        }
    }
}
```

### Custom Configuration

```java
// Create a minimal context for testing
ConcreteGlobalContext minimalContext = DependencyFactory.createMinimalGlobalContext();

// Create a custom context with specific features
ConcreteGlobalContext customContext = DependencyFactory.createCustomGlobalContext(
    true,  // Include database components
    false, // Exclude Redis
    true   // Include rating system
);

// Create a custom SpeedRun context
ConcreteSpeedRunContext speedRunContext = SpeedRunDependencyFactory.createCustomSpeedRunContext(
    plugin,
    true,  // Include database
    true,  // Include Redis
    false, // Exclude rating system
    true   // Include inventory management
);
```

### Builder Pattern (Alternative)

```java
// Using the builder pattern for fine-grained control
ConcreteGlobalContext context = ConcreteGlobalContext.builder()
    .setConcreteGameMode(new GameMode())
    .setConcreteUtils(new Utils())
    .setConcreteDebugger(new Debugger());

// Add context-dependent components
MCUtils mcUtils = new MCUtils(context);
context.setMcUtils(mcUtils);
```

## Migration from Interface-Only System

### Before (Interface-Only)
```java
// Old way - interface-only dependency injection
GlobalContext context = new GlobalContext()
    .setGameMode(gameMode)
    .setUtils(utils);

IGameMode gameMode = context.getGameMode();
```

### After (Concrete + Interface)
```java
// New way - concrete dependency injection with interface compatibility
ConcreteGlobalContext context = DependencyFactory.createGlobalContext();

// Still works - interface access
IGameMode gameMode = context.getGameMode();

// New capability - concrete access
GameMode concreteGameMode = context.getConcreteGameMode();
```

## Benefits

### 1. **Interface Compatibility**
- Existing code using interfaces continues to work
- No breaking changes to existing API

### 2. **Concrete Access**
- Direct access to concrete implementations when needed
- Better performance (no casting required)
- Access to concrete-specific methods

### 3. **Dual Registration**
- Dependencies are registered both as interfaces and concrete classes
- `context.get(IGameMode.class)` and `context.get(GameMode.class)` both work

### 4. **Factory-Based Creation**
- Centralized dependency creation and configuration
- Consistent wiring of dependencies
- Easy to create different configurations for different environments

### 5. **Error Handling**
- Graceful degradation when optional dependencies are unavailable
- Validation methods to ensure required dependencies are present
- Detailed error messages for troubleshooting

## Best Practices

### 1. **Use Factories for Creation**
```java
// Good - use factory
ConcreteGlobalContext context = DependencyFactory.createGlobalContext();

// Avoid - manual creation (unless you need fine control)
ConcreteGlobalContext context = new ConcreteGlobalContext();
```

### 2. **Validate Contexts**
```java
ConcreteGlobalContext context = DependencyFactory.createGlobalContext();
if (!DependencyFactory.validateContext(context)) {
    throw new IllegalStateException("Context validation failed");
}
```

### 3. **Use Appropriate Access Methods**
```java
// Use interface access for general compatibility
IGameMode gameMode = context.getGameMode();

// Use concrete access when you need specific functionality
GameMode concreteGameMode = context.getConcreteGameMode();
if (concreteGameMode.isGameMode(GameModeEnum.SPEEDRUN)) {
    // Concrete-specific logic
}
```

### 4. **Choose the Right Factory Method**
```java
// For production - full context
ConcreteGlobalContext prodContext = DependencyFactory.createGlobalContext();

// For testing - minimal context
ConcreteGlobalContext testContext = DependencyFactory.createMinimalGlobalContext();

// For specific needs - custom context
ConcreteGlobalContext customContext = DependencyFactory.createCustomGlobalContext(
    includeDatabase, includeRedis, includeRating
);
```

## Troubleshooting

### Common Issues

1. **ClassCastException when using setters**
   - Ensure you're passing the correct concrete implementation
   - The system expects concrete classes, not just interfaces

2. **Missing Dependencies**
   - Use `validateContext()` to check for missing required dependencies
   - Use `printContextSummary()` to see what's registered

3. **Null Pointer Exceptions**
   - Check that optional dependencies are available before using them
   - Use the factory methods which handle optional dependencies gracefully

### Debug Information

```java
// Print detailed context information
DependencyFactory.printContextSummary(context);

// Check if specific dependencies are registered
boolean hasGameMode = context.isRegistered(GameMode.class);
boolean hasIGameMode = context.isRegistered(IGameMode.class);
```

## Conclusion

The concrete dependency injection system provides a powerful and flexible way to manage dependencies while maintaining backward compatibility. Use the factory classes for easy setup, and choose between interface and concrete access based on your specific needs.