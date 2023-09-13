package net.minecraft.server;

public interface AreaTransformerOffset1 extends AreaTransformer {

    default AreaDimension a(AreaDimension areadimension) {
        return new AreaDimension(areadimension.a() - 1, areadimension.b() - 1, areadimension.c() + 2, areadimension.d() + 2);
    }
}
