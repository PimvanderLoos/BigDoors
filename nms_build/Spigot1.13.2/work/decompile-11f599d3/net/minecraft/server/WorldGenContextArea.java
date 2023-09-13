package net.minecraft.server;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;

public class WorldGenContextArea extends WorldGenContextLayer<AreaLazy> {

    private final Long2IntLinkedOpenHashMap c = new Long2IntLinkedOpenHashMap(16, 0.25F);
    private final int d;
    private final int e;

    public WorldGenContextArea(int i, int j, long k, long l) {
        super(l);
        this.c.defaultReturnValue(Integer.MIN_VALUE);
        this.d = i;
        this.e = j;
        this.a(k);
    }

    public AreaLazy a(AreaDimension areadimension, AreaTransformer8 areatransformer8) {
        return new AreaLazy(this.c, this.d, areadimension, areatransformer8);
    }

    public AreaLazy a(AreaDimension areadimension, AreaTransformer8 areatransformer8, AreaLazy arealazy) {
        return new AreaLazy(this.c, Math.min(256, arealazy.a() * 4), areadimension, areatransformer8);
    }

    public AreaLazy a(AreaDimension areadimension, AreaTransformer8 areatransformer8, AreaLazy arealazy, AreaLazy arealazy1) {
        return new AreaLazy(this.c, Math.min(256, Math.max(arealazy.a(), arealazy1.a()) * 4), areadimension, areatransformer8);
    }
}
