package net.minecraft.server;

public class BiomeVoid extends BiomeBase {

    public BiomeVoid(BiomeBase.a biomebase_a) {
        super(biomebase_a);
        this.u.clear();
        this.v.clear();
        this.w.clear();
        this.x.clear();
        this.t = new BiomeVoidDecorator();
    }

    public boolean i() {
        return true;
    }
}
