package dev.flarelc.modloader.bridge;

public enum MinecraftVersion {

    VANILLA_1_8_9("Vanilla 1.8.9"),
    LUNAR_1_8_9("Lunar 1.8.9");

    public final String name;
    MinecraftVersion(String name) {
        this.name = name;
    }
}
