package dev.flarelc.modloader;

import dev.flarelc.api.Module;
import dev.flarelc.api.ModuleInfo;
import dev.flarelc.modloader.bridge.MinecraftVersion;
import dev.flarelc.modloader.remapper.JarLoader;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.SimpleRemapper;
import org.objectweb.asm.tree.ClassNode;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModuleLoader {

    public HashMap<String, String> mappings = new HashMap<>();
    public List<Module> modules = new ArrayList<>();
    public List<JarLoader> waiting = new ArrayList<>();

    public ModuleLoader(){
        if(FlareLoader.getInstance().getVersion() == MinecraftVersion.VANILLA_1_8_9){
            loadMappingsFile(System.getenv("APPDATA") + "/flarelc/mappings/vanilla_1.8.9.txt");
        }
    }

    public void loadMappingsFile(String path){
        File file = new File(path);
        try{
            BufferedReader reader = new BufferedReader(new java.io.FileReader(file));
            String line;
            while((line = reader.readLine()) != null){
                String[] split = line.split(",");
                mappings.put(split[0], split[1]);
            }
            reader.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JarLoader remapModule(String input, String output) {
        try {
            JarLoader loader = new JarLoader(input, output);
            loader.loadJar();
            SimpleRemapper remapper = new SimpleRemapper(mappings);
            for (int i = 0; i < loader.classes.size(); i++) {
                ClassNode cn = loader.classes.get(i);
                ClassNode remapped = new ClassNode();
                cn.accept(new ClassRemapper(remapped, remapper));
                loader.classes.set(i, remapped);
            }
            loader.saveJar();


            return loader;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void loadModule(String path){

        String flare = System.getenv("APPDATA") + "/flarelc/";

        if(!(new File(flare + "remapped")).exists())
            new File(flare + "remapped").mkdirs();

        // clear content of remapped folder
        File[] files = new File(flare + "remapped").listFiles();
        if(files != null){
            for(File file : files){
                file.delete();
            }
        }


        JarLoader loader = remapModule(flare + "mods/" + path, flare + "remapped/" + path);
        if(loader == null){
            System.out.println("Failed to remap module!");
            return;
        }

        waiting.add(loader);



    }

}
