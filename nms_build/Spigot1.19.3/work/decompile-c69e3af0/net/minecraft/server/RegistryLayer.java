package net.minecraft.server;

import java.util.List;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;

public enum RegistryLayer {

    STATIC, WORLDGEN, DIMENSIONS, RELOADABLE;

    private static final List<RegistryLayer> VALUES = List.of(values());
    private static final IRegistryCustom.Dimension STATIC_ACCESS = IRegistryCustom.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);

    private RegistryLayer() {}

    public static LayeredRegistryAccess<RegistryLayer> createRegistryAccess() {
        return (new LayeredRegistryAccess<>(RegistryLayer.VALUES)).replaceFrom(RegistryLayer.STATIC, RegistryLayer.STATIC_ACCESS);
    }
}
