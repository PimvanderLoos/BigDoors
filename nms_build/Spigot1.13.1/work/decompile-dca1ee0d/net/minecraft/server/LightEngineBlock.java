package net.minecraft.server;

import java.util.Iterator;

public class LightEngineBlock extends LightEngine {

    public LightEngineBlock() {}

    public EnumSkyBlock a() {
        return EnumSkyBlock.BLOCK;
    }

    public void a(RegionLimitedWorldAccess regionlimitedworldaccess, IChunkAccess ichunkaccess) {
        Iterator iterator = ichunkaccess.j().iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition = (BlockPosition) iterator.next();

            this.a((IWorldWriter) regionlimitedworldaccess, blockposition, this.b(regionlimitedworldaccess, blockposition));
            this.a(ichunkaccess.getPos(), blockposition, this.a((IWorldReader) regionlimitedworldaccess, blockposition));
        }

        this.a((GeneratorAccess) regionlimitedworldaccess, ichunkaccess.getPos());
    }
}
