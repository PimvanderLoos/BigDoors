package net.minecraft.world.level.newbiome.layer.traits;

public interface AreaTransformerIdentity extends AreaTransformer {

    @Override
    default int a(int i) {
        return i;
    }

    @Override
    default int b(int i) {
        return i;
    }
}
