package dev.flarelc.modloader.bridge.hooks.impl;

import dev.flarelc.api.events.EventBus;
import dev.flarelc.api.events.impl.EventKeyType;
import dev.flarelc.api.events.impl.EventTick;
import dev.flarelc.modloader.FlareLoader;
import dev.flarelc.modloader.bridge.hooks.AbstractHook;
import dev.flarelc.modloader.bridge.hooks.HookInfo;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

@HookInfo(
        className = "net/minecraft/client/Minecraft"
)
public class MinecraftHook extends AbstractHook {

    @Override
    public void transform(ClassNode cn) {
        cn.methods.forEach(mn -> {
            if(mn.name.equals(FlareLoader.getInstance().getModuleLoader().mappings.getOrDefault(this.getHookInfo().className() + ".runTick()V", "runTick")) && mn.desc.equals("()V")){
                InsnList list = new InsnList();
                list.add(new TypeInsnNode(Opcodes.NEW, EventTick.class.getName().replace(".", "/")));
                list.add(new InsnNode(Opcodes.DUP));
                list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, EventTick.class.getName().replace(".", "/"), "<init>", "()V", false));
                list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, EventBus.class.getName().replace(".", "/"), "post", "(Ldev/flarelc/api/events/Event;)V", false));
                mn.instructions.insertBefore(mn.instructions.getFirst(), list);

                AbstractInsnNode target = null;
                int count = 0;
                for (AbstractInsnNode in : mn.instructions.toArray()) {
                    if (in instanceof MethodInsnNode && ((MethodInsnNode)in).name.equals("getEventKeyState") && ++count == 3) {
                        target = in.getNext().getNext().getNext().getNext().getNext();
                    }
                }
                InsnList list2 = new InsnList();
                list2.add(new TypeInsnNode(Opcodes.NEW, EventKeyType.class.getName().replace(".", "/")));
                list2.add(new InsnNode(Opcodes.DUP));
                list2.add(new VarInsnNode(Opcodes.ILOAD, 1));
                list2.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, EventKeyType.class.getName().replace(".", "/"), "<init>", "(I)V", false));
                list2.add(new MethodInsnNode(Opcodes.INVOKESTATIC, EventBus.class.getName().replace(".", "/"), "post", "(Ldev/flarelc/api/events/Event;)V", false));
                mn.instructions.insert(target, list2);
            }
        });
    }

}
