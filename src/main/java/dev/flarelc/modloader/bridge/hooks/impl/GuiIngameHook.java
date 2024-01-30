package dev.flarelc.modloader.bridge.hooks.impl;

import dev.flarelc.api.events.EventBus;
import dev.flarelc.api.events.impl.EventRenderGameOverlay;
import dev.flarelc.modloader.FlareLoader;
import dev.flarelc.modloader.bridge.hooks.AbstractHook;
import dev.flarelc.modloader.bridge.hooks.HookInfo;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

@HookInfo(
        className = "net/minecraft/client/gui/GuiIngame"
)
public class GuiIngameHook extends AbstractHook {

    @Override
    public void transform(ClassNode cn) {
        cn.methods.forEach(mn -> {
            if(mn.name.equals(FlareLoader.getInstance().getModuleLoader().mappings.getOrDefault(this.getHookInfo().className() + ".renderGameOverlay(F)V", "renderGameOverlay")) && mn.desc.equals("(F)V")){

                InsnList list = new InsnList();
                list.add(new TypeInsnNode(Opcodes.NEW, EventRenderGameOverlay.class.getName().replace(".", "/")));
                list.add(new InsnNode(Opcodes.DUP));
                list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, EventRenderGameOverlay.class.getName().replace(".", "/"), "<init>", "()V", false));
                list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, EventBus.class.getName().replace(".", "/"), "post", "(Ldev/flarelc/api/events/Event;)V", false));
                AbstractInsnNode in = mn.instructions.getLast();
                for(int i = 0; i < 12; i++)
                    in = in.getPrevious();
                mn.instructions.insertBefore(in, list);

            }
        });
    }

}
