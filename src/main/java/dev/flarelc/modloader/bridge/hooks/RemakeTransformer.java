package dev.flarelc.modloader.bridge.hooks;

import dev.flarelc.modloader.FlareLoader;
import fr.bodyalhoha.remake.transformers.TransformClass;
import fr.bodyalhoha.remake.transformers.Transformer;
import org.objectweb.asm.tree.ClassNode;

@TransformClass(
        klass = "Crazy? I was crazy once. They locked me in a room. A rubber room. A rubber room with rats. And rats make me crazy. Crazy?"
)
public class RemakeTransformer extends Transformer {

    public RemakeTransformer(){

    }

    @Override
    public void run(ClassNode classNode) {
        System.out.println("[modloader] transforming class " + classNode.name + "...");
        FlareLoader.getInstance().getHooks().forEach(hook -> {
            String className = hook.getHookInfo().className();
            className = FlareLoader.getInstance().getModuleLoader().mappings.getOrDefault(className, className);
            if(className.equals(classNode.name)){
                hook.transform(classNode);
            }
        });
    }
}
