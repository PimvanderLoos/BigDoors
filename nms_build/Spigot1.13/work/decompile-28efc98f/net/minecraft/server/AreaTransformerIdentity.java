package net.minecraft.server;

public interface AreaTransformerIdentity extends AreaTransformer {

    default AreaDimension a(AreaDimension areadimension) {
        return areadimension;
    }
}
