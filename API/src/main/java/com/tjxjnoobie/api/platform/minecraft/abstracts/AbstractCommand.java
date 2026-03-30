package com.tjxjnoobie.api.platform.minecraft.abstracts;

import com.tjxjnoobie.api.platform.minecraft.utils.interfaces.IMCUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for commands providing common functionality
 * @param <T> The context type this command operates with
 */
public abstract class AbstractCommand<T> implements CommandExecutor, TabCompleter, IMCUtils {


    protected final T context;
    protected final String commandName;
    protected final String permission;
    protected final boolean requiresPlayer;
    
    protected AbstractCommand(T context, String commandName, String permission, boolean requiresPlayer) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        if (commandName == null || commandName.trim().isEmpty()) {
            throw new IllegalArgumentException("Command name cannot be null or empty");
        }
        
        this.context = context;
        this.commandName = commandName.toLowerCase();
        this.permission = permission;
        this.requiresPlayer = requiresPlayer;
    }
    
    protected AbstractCommand(T context, String commandName, String permission) {
        this(context, commandName, permission, true);
    }
    
    protected AbstractCommand(T context, String commandName) {
        this(context, commandName, null, true);
    }
    
    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            // Check if player is required
            if (requiresPlayer && !(sender instanceof Player)) {
                sender.sendMessage("§cThis command can only be executed by players.");
                return true;
            }
            
            // Check permissions
            if (permission != null && !sender.hasPermission(permission)) {
                sender.sendMessage("§cYou don't have permission to use this command.");
                return true;
            }
            
            // Validate arguments
            if (!validateArguments(sender, args)) {
                sendUsage(sender);
                return true;
            }
            
            // Execute the command
            return executeCommand(sender, command, label, args);
            
        } catch (Exception e) {
            sender.sendMessage("§cAn error occurred while executing the command.");
            if (sender instanceof Player) {
                sendDebugMessage((Player) sender, "[COMMAND] Error in " + commandName + ": " + e.getMessage());
            }
            e.printStackTrace();
            return true;
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        try {

            return getTabCompletions(sender, command, alias, args);
            
        } catch (Exception e) {
            if (sender instanceof Player) {
                sendDebugMessage((Player) sender, "[COMMAND] Tab completion error in " + commandName + ": " + e.getMessage());
            }
            return new ArrayList<>();
        }
    }
    
    /**
     * Gets the context this command operates with
     * @return The context instance
     */
    public T getContext() {
        return context;
    }
    
    /**
     * Gets the command name
     * @return The command name
     */
    public String getCommandName() {
        return commandName;
    }
    
    /**
     * Gets the required permission
     * @return The permission string or null if no permission required
     */
    public String getPermission() {
        return permission;
    }
    
    /**
     * Checks if this command requires a player sender
     * @return true if player required, false otherwise
     */
    public boolean requiresPlayer() {
        return requiresPlayer;
    }
    
    /**
     * Validates command arguments
     * @param sender The command sender
     * @param args The command arguments
     * @return true if arguments are valid, false otherwise
     */
    protected boolean validateArguments(CommandSender sender, String[] args) {
        // Default implementation accepts any arguments
        return true;
    }
    
    /**
     * Sends usage information to the sender
     * @param sender The command sender
     */
    protected void sendUsage(CommandSender sender) {
        String usage = getUsage();
        if (usage != null && !usage.trim().isEmpty()) {
            sender.sendMessage("§cUsage: " + usage);
        }
    }
    
    /**
     * Gets the usage string for this command
     * @return The usage string
     */
    protected abstract String getUsage();
    
    /**
     * Executes the command logic
     * @param sender The command sender
     * @param command The command object
     * @param label The command label used
     * @param args The command arguments
     * @return true if command was handled successfully
     */
    protected abstract boolean executeCommand(CommandSender sender, Command command, String label, String[] args);
    
    /**
     * Provides tab completions for the command
     * @param sender The command sender
     * @param command The command object
     * @param alias The command alias used
     * @param args The current arguments
     * @return List of possible completions
     */
    protected List<String> getTabCompletions(CommandSender sender, Command command, String alias, String[] args) {
        // Default implementation returns empty list
        return new ArrayList<>();
    }
    
    /**
     * Helper method to get player from sender if it's a player
     * @param sender The command sender
     * @return Player instance or null if sender is not a player
     */
    protected Player getPlayer(CommandSender sender) {
        return sender instanceof Player ? (Player) sender : null;
    }
    
    /**
     * Helper method to check if sender is a player
     * @param sender The command sender
     * @return true if sender is a player
     */
    protected boolean isPlayer(CommandSender sender) {
        return sender instanceof Player;
    }
    
    /**
     * Helper method to filter tab completions based on partial input
     * @param completions The list of all possible completions
     * @param partial The partial input to match
     * @return Filtered list of completions
     */
    protected List<String> filterCompletions(List<String> completions, String partial) {
        if (partial == null || partial.isEmpty()) {
            return completions;
        }
        
        List<String> filtered = new ArrayList<>();
        String lowerPartial = partial.toLowerCase();
        
        for (String completion : completions) {
            if (completion.toLowerCase().startsWith(lowerPartial)) {
                filtered.add(completion);
            }
        }
        
        return filtered;
    }
}