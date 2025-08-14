package com.tjxjnoobie.api.internal.utils.reflection;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;

public class ReflectUtil {



    public static Class<?> getClass(String name) {
        try {
            return Class.forName(name);
        } catch (Exception e) {
            return null;
        }
    }

    public static MethodHandles.Lookup getSuperLookup() throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchFieldException, NoSuchMethodException {
        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
        Field theUnsafeField = getField(unsafeClass, unsafeClass, true);
        Method theUnsafeGetObjectMethod = getMethod(unsafeClass, "getObject", false, new Class[] { Object.class, long.class });
        Method theUnsafeStaticFieldOffsetMethod = getMethod(unsafeClass, "staticFieldOffset", false, new Class[] { Field.class });
        Object theUnsafe = theUnsafeField.get(null);
        Field implLookup = getField(MethodHandles.Lookup.class, "IMPL_LOOKUP", false);
        return (MethodHandles.Lookup)theUnsafeGetObjectMethod.invoke(theUnsafe, new Object[] { MethodHandles.Lookup.class, theUnsafeStaticFieldOffsetMethod.invoke(theUnsafe, new Object[] { implLookup }) });
    }

    public static void addFileLibrary(File file) throws Throwable {
        ClassLoader classLoader = ReflectUtil.class.getClassLoader();
        MethodHandle handle = getSuperLookup().unreflect(getMethodWithParent(classLoader.getClass(), "addURL", false, new Class[] { URL.class }));
        handle.invoke(classLoader, file.toURI().toURL());
    }

    public static Field getField(Class<?> clazz, String target, boolean handleAccessible) throws NoSuchFieldException {
        try {
            Field field = clazz.getDeclaredField(target);
            if (handleAccessible)
                field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            throw new NoSuchFieldException(target + " field in " + target);
        }
    }

    public static Field getField(Class<?> clazz, Class<?> target, boolean handleAccessible) throws NoSuchFieldException {
        return getField0(clazz, clazz, target, handleAccessible);
    }

    private static Field getField0(Class<?> source, Class<?> clazz, Class<?> target, boolean handleAccessible) throws NoSuchFieldException {
        Field[] arrayOfField;
        int i;
        byte b;
        for (arrayOfField = clazz.getDeclaredFields(), i = arrayOfField.length, b = 0; b < i; ) {
            Field field = arrayOfField[b];
            if (field.getType() != target) {
                b++;
                continue;
            }
            if (handleAccessible)
                field.setAccessible(true);
            return field;
        }
        clazz = clazz.getSuperclass();
        if (clazz != null)
            return getField(clazz, target, handleAccessible);
        throw new NoSuchFieldException(target.getName() + " type in " + target.getName());
    }

    public static Method getMethod(Class<?> clazz, String name, boolean handleAccessible, Class<?>... args) throws NoSuchMethodException {
        Method[] arrayOfMethod;
        int i;
        byte b;
        for (arrayOfMethod = clazz.getDeclaredMethods(), i = arrayOfMethod.length, b = 0; b < i; ) {
            Method method = arrayOfMethod[b];
            if (!method.getName().equalsIgnoreCase(name) ||
                    !Arrays.equals((Object[])method.getParameterTypes(), (Object[])args)) {
                b++;
                continue;
            }
            if (handleAccessible)
                method.setAccessible(true);
            return method;
        }
        throw new NoSuchMethodException(name + " method in " + name);
    }

    public static Method getMethodWithParent(Class<?> clazz, String name, boolean handleAccessible, Class<?>... args) throws NoSuchMethodException {
        Method[] arrayOfMethod;
        int i;
        byte b;
        for (arrayOfMethod = clazz.getDeclaredMethods(), i = arrayOfMethod.length, b = 0; b < i; ) {
            Method method = arrayOfMethod[b];
            if (!method.getName().equalsIgnoreCase(name) ||
                    !Arrays.equals((Object[])method.getParameterTypes(), (Object[])args)) {
                b++;
                continue;
            }
            if (handleAccessible)
                method.setAccessible(true);
            return method;
        }
        if (clazz != Object.class)
            return getMethodWithParent(clazz.getSuperclass(), name, handleAccessible, args);
        throw new NoSuchMethodException(name + " method in " + name);
    }
    public static void loadLibs(){
        try {
            File libsFolder = new File("libs");
            File[] jarFiles = libsFolder.listFiles((file) -> file.getName().endsWith(".jar") || file.isDirectory());
            if (jarFiles == null || jarFiles.length == 0) {
                System.out.println("No JAR libraries found in " + libsFolder.getAbsolutePath());
                return;
            }
            for (File libraryFile : jarFiles) {
                System.out.println("Loading: " + libraryFile.getName());

                ReflectUtil.addFileLibrary(libraryFile);
            }
            System.out.println("Successfully loaded. ");
        } catch (Exception e) {
            System.err.println("Failed to load external library: " + e.getMessage());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}

