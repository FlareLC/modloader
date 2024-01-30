package dev.flarelc.modloader;

import dev.flarelc.api.Module;
import dev.flarelc.api.ModuleInfo;
import dev.flarelc.modloader.bridge.MinecraftVersion;
import dev.flarelc.modloader.bridge.hooks.AbstractHook;
import dev.flarelc.modloader.bridge.hooks.RemakeTransformer;
import dev.flarelc.modloader.bridge.hooks.impl.GuiIngameHook;
import dev.flarelc.modloader.bridge.hooks.impl.MinecraftHook;
import fr.bodyalhoha.remake.Remake;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import javax.swing.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FlareLoader {
    private static FlareLoader instance = null;
    private MinecraftVersion version;
    private ModuleLoader moduleLoader;
    private List<AbstractHook> hooks = Arrays.asList(
            new MinecraftHook(),
            new GuiIngameHook()
    );

    public static void run(){

        instance = new FlareLoader();
        if(!instance.init()){
            JOptionPane.showMessageDialog(null, "Failed to initialize FlareLoader!", "FlareLoader", JOptionPane.ERROR_MESSAGE);
            return;
        }

        instance.moduleLoader = new ModuleLoader();

        try{
            Remake.init();
            Remake.add(new RemakeTransformer());
            instance.hooks.forEach(hook -> {
                String className = hook.getHookInfo().className();
                className = instance.moduleLoader.mappings.getOrDefault(className, className).replace("/", ".");
                System.out.println("[modloader] hooking class " + className + "...");
                try{
                    Remake.remake(Class.forName(className));
                }catch (Exception e) {
                    System.out.println("[modloader] Failed to hook class " + className + "!");
                    e.printStackTrace();
                }

            });
        }catch (Exception e) {
            e.printStackTrace();
        }
        /* load modules */

        String path = System.getenv("APPDATA") + "/flarelc/";
        // list files in path + mods

        for (File file : Objects.requireNonNull(new File(path + "mods").listFiles())) {
            if (file.isFile()) {
                instance.moduleLoader.loadModule(file.getName());
            }
        }

    }

    public static void end(){
        System.out.println("[modloader] loading modules...");
        FlareLoader.instance.moduleLoader.waiting.forEach(loader -> {
            String mainClass = "";
            for(ClassNode cn : loader.classes){
                if(cn.visibleAnnotations == null)
                    continue;
                for(AnnotationNode ann : cn.visibleAnnotations){
                    if (ann.desc.equals("L" + ModuleInfo.class.getName().replace(".", "/") + ";")) {
                        mainClass = cn.name;
                        break;
                    }
                }
            }

            System.out.println("[modloader] loading module class " + mainClass + "...");
            try{
                Class<?> clazz = Class.forName(mainClass.replace("/", "."));
                Module module = (Module) clazz.newInstance();
                module.onLoad();
                System.out.println("[modloader Loaded module " + module.getModuleInfo().name());
            }catch (Exception e) {
                System.out.println("[modloader] Failed to load module class " + mainClass + "!");
                e.printStackTrace();
            }

        });
    }

    public static FlareLoader getInstance(){
        return instance;
    }

    public boolean init(){
        version = MinecraftVersion.VANILLA_1_8_9;

        try{
            this.getClass().getClassLoader().loadClass("net.minecraft.client.Minecraft");
            version = MinecraftVersion.LUNAR_1_8_9;
        }catch (Exception ignored){
        }
        return true;
    }

    public MinecraftVersion getVersion() {
        return version;
    }

    public ModuleLoader getModuleLoader() {
        return moduleLoader;
    }
    public List<AbstractHook> getHooks(){
        return hooks;
    }
}
