package com.m2u.eyelink.agent.profiler.instrument;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.exception.ELAgentException;

public class LegacyProfilerPluginClassInjector implements ClassInjector {
    private static final Logger logger = LoggerFactory.getLogger(LegacyProfilerPluginClassInjector.class);

    private static final Method DEFINE_CLASS;
    
    static {
        try {
            DEFINE_CLASS = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
            DEFINE_CLASS.setAccessible(true);
        } catch (Exception e) {
            throw new ELAgentException("Cannot access ClassLoader.defineClass(String, byte[], int, int)", e);
        }
    }
    
    private final ClassLoader sourceClassLoader;
    
    public LegacyProfilerPluginClassInjector(ClassLoader sourceClassLoader) {
        if (sourceClassLoader == null) {
            throw new NullPointerException("sourceClassLoader must not be null");
        }
        this.sourceClassLoader = sourceClassLoader;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Class<? extends T> injectClass(ClassLoader classLoader, String className) {
        ClassLoader targetClassLoader = classLoader == null ? ClassLoader.getSystemClassLoader() : classLoader;

        try {
            return (Class<T>)loadFromOtherClassLoader(targetClassLoader, className);
        } catch (Exception e) {
            logger.warn("Failed to load plugin class {} with classLoader {}", className, targetClassLoader, e);
            throw new ELAgentException("Failed to load plugin class " + className + " with classLoader " + targetClassLoader, e);
        }
    }
    
    private Class<?> loadFromOtherClassLoader(ClassLoader classLoader, String className) throws NotFoundException, IllegalArgumentException, IOException, CannotCompileException, IllegalAccessException, InvocationTargetException {
        ClassPool pool = new ClassPool();
        
        pool.appendClassPath(new LoaderClassPath(classLoader));
        pool.appendClassPath(new LoaderClassPath(sourceClassLoader));
        
        return loadFromOtherClassLoader(pool, classLoader, className);
    }
    
    private Class<?> loadFromOtherClassLoader(ClassPool pool, ClassLoader classLoader, String className) throws NotFoundException, IOException, CannotCompileException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Class<?> c = null;
        
        try {
            c = classLoader.loadClass(className);
        } catch (ClassNotFoundException ignore) {
            
        }
        
        if (c != null) {
            return c;
        }
        
        CtClass ct = pool.get(className);
        
        if (ct == null) {
            throw new NotFoundException(className);
        }
        
        
        CtClass superClass = ct.getSuperclass();
        
        if (superClass != null) {
            loadFromOtherClassLoader(pool, classLoader, superClass.getName());
        }
        
        CtClass[] interfaces = ct.getInterfaces();
        
        for (CtClass i : interfaces) {
            loadFromOtherClassLoader(pool, classLoader, i.getName());
        }
        
        Collection<String> refs = ct.getRefClasses();
        
        for (String ref : refs) {
            try {
                loadFromOtherClassLoader(pool, classLoader, ref);
            } catch (NotFoundException e) {
                logger.warn("Skip a referenced class because of NotFoundException : ", e);
            }
        }
        
        byte[] bytes = ct.toBytecode();
        return (Class<?>)DEFINE_CLASS.invoke(classLoader, ct.getName(), bytes, 0, bytes.length);
    }

    @Override
    public InputStream getResourceAsStream(ClassLoader targetClassLoader, String classPath) {
        ClassLoader classLoader = targetClassLoader;
        if(classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }

        return classLoader.getResourceAsStream(classPath);
    }
}
