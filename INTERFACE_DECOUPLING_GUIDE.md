# MCR Speedrun Project - Interface Decoupling Implementation Guide

## Overview
This document provides a comprehensive guide for implementing interface-based decoupling throughout the MCR Speedrun project. All dependencies in both `GlobalContext.java` and `SpeedRunContext.java` now have corresponding interfaces to enable loose coupling and better testability.

## Created Interfaces

### Core Game Interfaces
1. **`IGameMode`** - Game mode management
2. **`IGameState`** - Game state management  
3. **`IGameType`** - Game type management
4. **`IGameManager`** - Game flow and player management

### Utility Interfaces
5. **`IUtils`** - General utility functions
6. **`IMCUtils`** - Minecraft-specific utilities
7. **`IProxyUtils`** - Proxy/network utilities

### Manager Interfaces
8. **`IWorldManager`** - World creation and management
9. **`IPlayerManager`** - Player state management
10. **`ISoundManager`** - Sound and audio management
11. **`IStatsManager`** - Statistics management
12. **`IBossBarManager`** - Boss bar management
13. **`IPunishManager`** - Punishment system management

### Cache Interfaces
14. **`ISpeedrunStatsCache`** - Speedrun statistics caching
15. **`IRatingCache`** - Rating system caching
16. **`IRankCache`** - Rank data caching (extends existing)
17. **`ILobbyStatsCache`** - Lobby statistics caching
18. **`ILocationCache`** - Location data caching

### Rating System Interfaces
19. **`IRating`** - Individual rating calculations
20. **`IRatingAPI`** - Rating system API

### Rank System Interfaces
21. **`IRankMC`** - Minecraft rank management
22. **`IRank`** - General rank operations

### Player Data Interfaces
23. **`IPlayerProfile`** - Player profile management

### Infrastructure Interfaces
24. **`IRedis`** - Redis operations
25. **`IDebugger`** - Debug system management
26. **`IRetentionManager`** - Player retention management (extends existing)

### Event Handler Interfaces
27. **`IJoinEvent`** - Player join event handling
28. **`IQuitEvent`** - Player quit event handling
29. **`IFairFight`** - Fair fight system

### Command Interfaces
30. **`IDebug`** - Debug command operations

### System Interfaces
31. **`IVoting`** - Voting system management
32. **`IPunishLog`** - Punishment logging

## Implementation Strategy

### Phase 1: Interface Implementation (Current)
✅ **Completed**: All interfaces have been created with comprehensive method signatures.

### Phase 2: Update Context Classes
🔄 **In Progress**: 
- ✅ Updated `GlobalContext` field declarations to use interfaces
- ✅ Updated `GlobalContext` constructor parameters to use interfaces
- 🔄 Need to update method return types in `GlobalContext`
- ⏳ Update `SpeedRunContext` similarly

### Phase 3: Update Concrete Classes
⏳ **Next Steps**: Make all concrete classes implement their corresponding interfaces:

```java
// Example implementation
public class GameManager implements IGameManager {
    // Existing implementation
}

public class PlayerManager extends AbstractManager<SpeedRunContext> implements IPlayerManager {
    // Existing implementation with interface methods
}
```

### Phase 4: Update Dependencies Throughout Project
⏳ **Final Phase**: Update all classes that depend on these services to use interfaces instead of concrete classes.

## Interface Implementation Examples

### 1. Core Game Interface Implementation
```java
public class GameState implements IGameState {
    private GameStateEnum currentState;
    private GameStateEnum previousState;
    
    @Override
    public GameStateEnum getCurrentState() {
        return currentState;
    }
    
    @Override
    public void setGameState(GameStateEnum gameState, String serverId) {
        this.previousState = this.currentState;
        this.currentState = gameState;
        // Additional logic...
    }
    
    // Implement all other interface methods...
}
```

### 2. Manager Interface Implementation
```java
public class WorldManager extends AbstractManager<GlobalContext> implements IWorldManager {
    
    @Override
    public World createWorld(String worldName, World.Environment environment) {
        ensureInitialized();
        // Existing implementation...
    }
    
    @Override
    public boolean deleteWorld(String worldName) {
        ensureInitialized();
        // Existing implementation...
    }
    
    // Implement all other interface methods...
}
```

### 3. Cache Interface Implementation
```java
public class SpeedrunStatsCache extends AbstractCache<UUID, Object> implements ISpeedrunStatsCache {
    
    public SpeedrunStatsCache() {
        super(30, TimeUnit.MINUTES); // 30-minute TTL
    }
    
    @Override
    public Object getPlayerStats(UUID playerId) {
        return get(playerId, this::loadPlayerStatsFromDatabase);
    }
    
    @Override
    public void updatePlayerStats(UUID playerId, Object stats) {
        put(playerId, stats);
        saveToDatabase(playerId, stats);
    }
    
    // Implement all other interface methods...
}
```

## Benefits of Interface Decoupling

### 1. **Loose Coupling**
- Classes depend on abstractions, not concrete implementations
- Easier to swap implementations without changing dependent code
- Better separation of concerns

### 2. **Testability**
- Easy to create mock implementations for unit testing
- Can test components in isolation
- Faster test execution with lightweight mocks

### 3. **Flexibility**
- Multiple implementations of the same interface
- Runtime implementation switching
- Plugin-based architecture support

### 4. **Maintainability**
- Clear contracts defined by interfaces
- Easier to understand component responsibilities
- Reduced impact of changes

## Migration Steps for Existing Classes

### Step 1: Implement Interface
```java
// Before
public class SomeManager {
    public void doSomething() { ... }
}

// After
public class SomeManager implements ISomeManager {
    @Override
    public void doSomething() { ... }
}
```

### Step 2: Update Dependencies
```java
// Before
private SomeManager someManager;

// After
private ISomeManager someManager;
```

### Step 3: Update Constructor/Injection
```java
// Before
public MyClass(SomeManager someManager) {
    this.someManager = someManager;
}

// After
public MyClass(ISomeManager someManager) {
    this.someManager = someManager;
}
```

## Testing Strategy

### 1. **Mock Implementations**
Create lightweight mock implementations for testing:

```java
public class MockGameManager implements IGameManager {
    private final Map<UUID, String> players = new HashMap<>();
    
    @Override
    public void addPlayer(UUID uuid, String name) {
        players.put(uuid, name);
    }
    
    @Override
    public int getCurrentPlayers() {
        return players.size();
    }
    
    // Minimal implementations for testing...
}
```

### 2. **Integration Tests**
Test with real implementations to ensure interface contracts are correct.

### 3. **Contract Tests**
Verify that all implementations correctly fulfill interface contracts.

## Configuration and Dependency Injection

### Option 1: Factory Pattern
```java
public class ManagerFactory {
    public static IGameManager createGameManager(SpeedRunContext context) {
        return new GameManager(context);
    }
    
    public static IPlayerManager createPlayerManager(SpeedRunContext context) {
        return new PlayerManager(context);
    }
}
```

### Option 2: Builder Pattern
```java
public class ContextBuilder {
    public GlobalContext build() {
        return new GlobalContext(
            createGameMode(),
            createUtils(),
            createStatsCache(),
            // ... other dependencies
        );
    }
    
    private IGameMode createGameMode() {
        return new GameMode();
    }
}
```

## Error Handling and Validation

### Interface Method Validation
```java
public interface IGameManager {
    /**
     * Adds a player to the game
     * @param uuid The player's UUID (must not be null)
     * @param name The player's name (must not be null or empty)
     * @throws IllegalArgumentException if parameters are invalid
     */
    void addPlayer(UUID uuid, String name);
}
```

### Implementation Validation
```java
@Override
public void addPlayer(UUID uuid, String name) {
    if (uuid == null) {
        throw new IllegalArgumentException("UUID cannot be null");
    }
    if (name == null || name.trim().isEmpty()) {
        throw new IllegalArgumentException("Name cannot be null or empty");
    }
    // Implementation...
}
```

## Performance Considerations

### 1. **Interface Overhead**
- Minimal performance impact from interface calls
- JVM optimizations handle virtual method calls efficiently

### 2. **Memory Usage**
- Interfaces themselves have no memory overhead
- Additional abstraction layers are negligible

### 3. **Startup Time**
- Dependency injection may add minimal startup time
- Benefits far outweigh costs

## Next Steps

1. **Complete GlobalContext Updates**
   - Update all method return types to use interfaces
   - Update IGlobalContext interface to match

2. **Update SpeedRunContext**
   - Apply same interface-based approach
   - Create ISpeedRunContext interface

3. **Implement Interfaces in Concrete Classes**
   - Start with core classes (GameManager, PlayerManager, etc.)
   - Add interface implementations gradually

4. **Update Dependent Classes**
   - Replace concrete type references with interface references
   - Update method parameters and return types

5. **Create Mock Implementations**
   - Build test infrastructure with mock implementations
   - Enable comprehensive unit testing

6. **Documentation and Examples**
   - Create implementation guides for each interface
   - Provide examples of proper usage

## Conclusion

The interface decoupling implementation provides a solid foundation for a more maintainable, testable, and flexible codebase. The comprehensive set of interfaces covers all major dependencies and enables true loose coupling throughout the MCR Speedrun project.

This approach will significantly improve code quality, reduce maintenance burden, and enable easier testing and future enhancements.