package dev.flarelc.modloader.bridge.hooks;

import org.objectweb.asm.tree.ClassNode;

public abstract class AbstractHook {

    public abstract void transform(ClassNode cn);
    public HookInfo getHookInfo(){
        return this.getClass().getAnnotation(HookInfo.class);
    }

}
