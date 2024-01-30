package dev.flarelc.api;

public class Module {

    private final ModuleInfo info;

    public Module(){
        if(!this.getClass().isAnnotationPresent(ModuleInfo.class))
            throw new RuntimeException("Module class " + this.getClass().getName() + " does not have a ModuleInfo annotation!");
        this.info = this.getClass().getAnnotation(ModuleInfo.class);
    }

    public ModuleInfo getModuleInfo(){
        return this.info;
    }

    public void onLoad(){

    }

}
