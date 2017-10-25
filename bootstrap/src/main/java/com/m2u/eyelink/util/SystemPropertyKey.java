package com.m2u.eyelink.util;

public enum SystemPropertyKey {

    JAVA_VERSION("java.version"),
    JAVA_RUNTIME_VERSION("java.runtime.version"),
    JAVA_RUNTIME_NAME("java.runtime.name"),
    JAVA_SPECIFICATION_VERSION("java.specification.version"),
    JAVA_CLASS_VERSION("java.class.version"),
    JAVA_VM_NAME("java.vm.name"),
    JAVA_VM_VERSION("java.vm.version"),
    JAVA_VM_INFO("java.vm.info"),
    JAVA_VM_SPECIFICATION_VERSION("java.vm.specification.version"),
    SUN_JAVA_COMMAND("sun.java.command"); // May be unsupported depending on the JVM.

    private final String key;

    SystemPropertyKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}

