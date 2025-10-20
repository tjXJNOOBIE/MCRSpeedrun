package com.tjxjnoobie.api.abstracts;

import com.tjxjnoobie.api.enums.GameStateEnum;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Abstract base class for game state management providing common state transition functionality
 * @param <T> The context type this state manager operates with
 */
public abstract class AbstractGameStateManager<T> {
    // TODO Change abstract context to interface?
    protected T context;
    protected volatile GameStateEnum currentState;
    protected volatile GameStateEnum previousState;

    Predicate<GameStateEnum> gameStateEnums;


    // State change listeners
    private final CopyOnWriteArrayList<Consumer<StateChangeEvent>> stateChangeListeners = new CopyOnWriteArrayList<>();
    
    // State-specific data storage
    private final ConcurrentHashMap<GameStateEnum, Object> stateData = new ConcurrentHashMap<>();
    
    protected AbstractGameStateManager( GameStateEnum initialState) {

        if (initialState == null) {
            throw new IllegalArgumentException("Initial state cannot be null");
        }

        this.currentState = initialState;
        this.previousState = null;
    }

    // Game loop
    protected abstract void onLobbyStart();
    protected abstract void onPreGameStart();
    protected abstract void onGameStart();
    protected abstract void onGameEnd();
    /**
     * Gets the current game state
     * @return The current state
     */
    public GameStateEnum getCurrentState() {
        return currentState;
    }
    
    /**
     * Gets the previous game state
     * @return The previous state or null if no previous state
     */
    public GameStateEnum getPreviousState() {
        return previousState;
    }
    
    /**
     * Transitions to a new game state
     * @param newState The new state to transition to
     * @param serverId Optional server ID for distributed systems
     * @return true if transition was successful, false otherwise
     */
    public synchronized boolean transitionTo(GameStateEnum newState, String serverId) {
        GameStateEnum[] stats = GameStateEnum.values();
        if (newState == null || Arrays.stream(stats).noneMatch(gameStateEnums)) {
            throw new IllegalArgumentException("New state cannot be null");
        }
        if (currentState == newState) {
            return true; // Already in the desired state
        }
        
        // Validate transition
        if (!isValidTransition(currentState, newState)) {
            return false;
        }
        
        GameStateEnum oldState = currentState;
        
        try {
            // Execute pre-transition logic
            onPreStateChange(oldState, newState);
            
            // Update state
            this.previousState = oldState;
            this.currentState = newState;
            
            // Execute post-transition logic
            onPostStateChange(oldState, newState);
            
            // Notify listeners
            StateChangeEvent event = new StateChangeEvent(oldState, newState, serverId);
            notifyStateChangeListeners(event);
            
            return true;
            
        } catch (Exception e) {
            // Rollback on failure
            this.currentState = oldState;
            this.previousState = null;
            
            onStateChangeError(oldState, newState, e);
            throw new RuntimeException("Failed to transition from " + oldState + " to " + newState, e);
        }
    }
    
    /**
     * Transitions to a new game state without server ID
     * @param newState The new state to transition to
     * @return true if transition was successful, false otherwise
     */
    public boolean transitionTo(GameStateEnum newState) {
        return transitionTo(newState, null);
    }
    
    /**
     * Checks if the current state matches the given state
     * @param state The state to check
     * @return true if current state matches
     */
    public boolean isInState(GameStateEnum state) {
        return currentState == state;
    }
    
    /**
     * Checks if the current state is one of the given states
     * @param states The states to check
     * @return true if current state is one of the given states
     */
    public boolean isInAnyState(GameStateEnum... states) {
        if (states == null || states.length == 0) {
            return false;
        }
        
        for (GameStateEnum state : states) {
            if (currentState == state) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Adds a state change listener
     * @param listener The listener to add
     */
    public void addStateChangeListener(Consumer<StateChangeEvent> listener) {
        if (listener != null) {
            stateChangeListeners.add(listener);
        }
    }
    
    /**
     * Removes a state change listener
     * @param listener The listener to remove
     */
    public void removeStateChangeListener(Consumer<StateChangeEvent> listener) {
        stateChangeListeners.remove(listener);
    }
    
    /**
     * Stores data associated with a specific state
     * @param state The state to associate data with
     * @param data The data to store
     */
    public void setStateData(GameStateEnum state, Object data) {
        if (state != null) {
            stateData.put(state, data);
        }
    }
    
    /**
     * Gets data associated with a specific state
     * @param state The state to get data for
     * @param clazz The expected data type
     * @return The data or null if not found
     */
    @SuppressWarnings("unchecked")
    public <U> U getStateData(GameStateEnum state, Class<U> clazz) {
        if (state == null || clazz == null) {
            return null;
        }
        
        Object data = stateData.get(state);
        return clazz.isInstance(data) ? (U) data : null;
    }
    
    /**
     * Clears data for a specific state
     * @param state The state to clear data for
     */
    public void clearStateData(GameStateEnum state) {
        if (state != null) {
            stateData.remove(state);
        }
    }
    

    /**
     * Validates if a state transition is allowed
     * Subclasses should override this to implement specific transition rules
     * @param from The current state
     * @param to The target state
     * @return true if transition is valid
     */
    protected boolean isValidTransition(GameStateEnum from, GameStateEnum to) {
        // Default implementation allows all transitions
        return true;
    }
    
    /**
     * Called before a state change occurs
     * Subclasses can override this for custom pre-transition logic
     * @param from The current state
     * @param to The target state
     */
    protected void onPreStateChange(GameStateEnum from, GameStateEnum to) {
        // Default implementation does nothing
    }
    
    /**
     * Called after a state change occurs
     * Subclasses can override this for custom post-transition logic
     * @param from The previous state
     * @param to The new current state
     */
    protected void onPostStateChange(GameStateEnum from, GameStateEnum to) {
        // Default implementation does nothing
    }
    
    /**
     * Called when a state change fails
     * Subclasses can override this for custom error handling
     * @param from The current state
     * @param to The target state that failed
     * @param error The error that occurred
     */
    protected void onStateChangeError(GameStateEnum from, GameStateEnum to, Exception error) {
        // Default implementation does nothing
    }
    
    /**
     * Notifies all state change listeners
     * @param event The state change event
     */
    private void notifyStateChangeListeners(StateChangeEvent event) {
        for (Consumer<StateChangeEvent> listener : stateChangeListeners) {
            try {
                listener.accept(event);
            } catch (Exception e) {
                // Log error but don't let it affect other listeners
                System.err.println("Error in state change listener: " + e.getMessage());
            }
        }
    }
    
    /**
     * State change event class
     */
    public static class StateChangeEvent {
        private final GameStateEnum fromState;
        private final GameStateEnum toState;
        private final String serverId;
        private final long timestamp;
        
        public StateChangeEvent(GameStateEnum fromState, GameStateEnum toState, String serverId) {
            this.fromState = fromState;
            this.toState = toState;
            this.serverId = serverId;
            this.timestamp = System.currentTimeMillis();
        }
        
        public GameStateEnum getFromState() { return fromState; }
        public GameStateEnum getToState() { return toState; }
        public String getServerId() { return serverId; }
        public long getTimestamp() { return timestamp; }
        
        @Override
        public String toString() {
            return String.format("StateChangeEvent{%s -> %s, serverId='%s', timestamp=%d}", 
                               fromState, toState, serverId, timestamp);
        }
    }
}