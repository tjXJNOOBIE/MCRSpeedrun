package com.tjxjnoobie.api.interfaces;

import com.tjxjnoobie.api.contexts.GlobalContext;
import com.tjxjnoobie.api.internal.utils.Utils;
import com.tjxjnoobie.api.managers.Debugger;
import com.tjxjnoobie.api.platform.cache.RankCache;
import com.tjxjnoobie.api.platform.minecraft.RankMC;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class InterfaceManager {

    private static MainInterFace mainInterFace;
    private static BlockPlaceHandler blockPlaceHandler;
    private static CoreJoinHandler coreJoinHandler;
    private static CoreQuitHandler coreQuitHandler;
    private static ChatHandler chatHandler;
    private static GlobalContext globalContext;
    private static final Map<String, Object> handlerMap = new HashMap<>();

    public static void registerHandlers(String name, Object handler) {
        handlerMap.put(name, handler);
    }

    public static Object getHandler(String name) {
        return handlerMap.get(name);
    }
    public static void setMainInterFace(MainInterFace sender) {
        if (mainInterFace == null) {
            mainInterFace = sender;
        }
    }

    public static MainInterFace getMainInterFace() {
        if (mainInterFace == null) {
            throw new IllegalStateException("MessageSender has not been set!");
        }
        return mainInterFace;
    }


    public static void setBlockPlaceHandler() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        System.out.println("Trying to BlockPlace setup handler");
        Class<?> blockPlaceClass = Class.forName("com.tjxjnoobie.kingdomFactions.Events.BlockPlace");
        Object blockPlaceInstance = blockPlaceClass.getDeclaredConstructor().newInstance();

        if(blockPlaceHandler == null){
            blockPlaceHandler = (BlockPlaceHandler) blockPlaceInstance;
            System.out.println("Handler: " +blockPlaceHandler.toString());
        }else {
            System.out.println("Is handler already setup? Handler: "+blockPlaceHandler.toString());
        }
    }
    public static BlockPlaceHandler getBlockPlaceHandler(){
        if(blockPlaceHandler == null){
            throw new IllegalStateException("Block Place handler not set!");
        }
        return blockPlaceHandler;
    }
    public static ChatHandler getChatHandler(){
        if(chatHandler == null){
            throw new IllegalStateException("Block Place handler not set!");
        }
        return chatHandler;
    }

    public static void setCoreJoinHandler(RankMC rankMC, RankCache rankCache, Debugger debugger, Utils utils) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        System.out.println("Trying to setup CoreJoin handler");

        // Load the CoreJoin class
        Class<?> coreJoinClass = Class.forName("com.tjxjnoobie.core.Events.CoreJoin");

        // Get the constructor with RankMC as a parameter
        Constructor<?> constructor = coreJoinClass.getDeclaredConstructor(RankMC.class,RankCache.class,Debugger.class,Utils.class);
        System.out.println("Constructor found: " + constructor);
        // Instantiate the CoreJoin class with the RankMC parameter
        Object coreJoinInstance = constructor.newInstance(rankMC,rankCache,debugger,utils);
        System.out.println("CoreJoin instance created: " + coreJoinInstance);
        System.out.println("ClassLoader of CoreJoin: " + coreJoinClass.getClassLoader());
        System.out.println("ClassLoader of RankMC: " + rankMC.getClass().getClassLoader());
        System.out.println("ClassLoader of RankCache: " + rankCache.getClass().getClassLoader());
        if (coreJoinClass.getClassLoader() != rankCache.getClass().getClassLoader()) {
            System.out.println("CoreJoin and RankCache are loaded by different ClassLoaders.");
        } else {
            System.out.println("CoreJoin and RankCache are loaded by the same ClassLoader.");
        }
        if(rankCache == null){
            System.out.println("RankCahce is null on Handler init");
        }
        // Set the handler
        if (coreJoinHandler == null) {
            coreJoinHandler = (CoreJoinHandler) coreJoinInstance;
            System.out.println("Handler initialized: " + coreJoinHandler);
        } else {
            System.out.println("Handler already set: " + coreJoinHandler);
        }
    }
    public static CoreJoinHandler getCoreJoinHandler(){
        if(coreJoinHandler == null){
            throw new IllegalStateException("CoreJoin handler not set!");

        }

        return coreJoinHandler;
    }
    public static void setCoreQuitHandler(GlobalContext context)
            throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        System.out.println("Trying to setup CoreQuit handler");

        // Load the CoreQuit class.
        Class<?> coreQuitClass = Class.forName("com.tjxjnoobie.core.Events.CoreQuit");

        // Assume you want to use a specific constructor – for instance, the first one.
        Constructor<?> constructor = coreQuitClass.getDeclaredConstructors()[0];

        // Retrieve the parameter types for the constructor.
        Class<?>[] parameterTypes = constructor.getParameterTypes();

        // Create an array to hold constructor arguments.
        Object[] args = new Object[parameterTypes.length];

        // Automatically get each dependency from the context.
        for (int i = 0; i < parameterTypes.length; i++) {
            args[i] = context.get(parameterTypes[i]);
        }

        // Create an instance of CoreQuit using the resolved dependencies.
        Object coreQuitInstance = constructor.newInstance(args);

        if (coreQuitHandler == null) {
            coreQuitHandler = (CoreQuitHandler) coreQuitInstance;
            System.out.println("Handler: " + coreQuitHandler.toString());
        } else {
            System.out.println("Handler already setup: " + coreQuitHandler.toString());
        }
    }
    public static void setGlobalHandler(IGlobalContext context,String className, String handlerName)
            throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        System.out.println("Trying to setup "+className+" via Reflection");

        // Load the CoreQuit class.
        Class<?> HandlerClass = Class.forName(className);

        // Assume you want to use a specific constructor – for instance, the first one.
        Constructor<?> constructor = HandlerClass.getDeclaredConstructors()[0];

        // Retrieve the parameter types for the constructor.
        Class<?>[] parameterTypes = constructor.getParameterTypes();

        // Create an array to hold constructor arguments.
        Object[] args = new Object[parameterTypes.length];

        // Automatically get each dependency from the context.
        for (int i = 0; i < parameterTypes.length; i++) {
            args[i] = context.get(parameterTypes[i]);
        }

        // Create an instance of CoreQuit using the resolved dependencies.
        Object HandlerInstance = constructor.newInstance(args);
        System.out.println("Class: " + HandlerInstance.toString() + " has been setup");
        registerHandlers(handlerName,HandlerInstance);
        coreJoinHandler = (CoreJoinHandler) handlerMap.get("CoreJoinHandler");
        chatHandler = (ChatHandler) handlerMap.get("ChatHandler");

    }
    public static void setupGlobalHandlers(GlobalContext context, Map<String, String> handlerConfig)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        // Loop through each entry in your configuration map:
        for (Map.Entry<String, String> entry : handlerConfig.entrySet()) {
            String handlerName = entry.getKey();         // Unique name for the handler
            String handlerClassName = entry.getValue();    // Fully qualified class name

            System.out.println("Setting up handler " + handlerName + " (" + handlerClassName + ")");

            // Load the handler class via reflection.
            Class<?> handlerClass = Class.forName(handlerClassName);

            // Use a constructor (here, the first one is assumed).
            Constructor<?> constructor = handlerClass.getDeclaredConstructors()[0];
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            Object[] args = new Object[parameterTypes.length];

            // Retrieve each dependency from your GlobalContext.
            for (int i = 0; i < parameterTypes.length; i++) {
                args[i] = context.get(parameterTypes[i]);
            }

            // Create an instance of the handler.
            Object handlerInstance = constructor.newInstance(args);
            System.out.println("Handler " + handlerName + " instantiated: " + handlerInstance);

            // Register the handler instance in a registry (a simple map, for example).
            registerHandlers(handlerName, handlerInstance);
        }
    }
    public static CoreQuitHandler getCoreQuitHandler(){
        if(coreQuitHandler == null){
            throw new IllegalStateException("Core quit handler not set!");
        }
        return coreQuitHandler;
    }

}
