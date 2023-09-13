package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;

public class WorldGenMineshaftStart extends StructureStart {

    private WorldGenMineshaft.Type c;

    public WorldGenMineshaftStart() {}

    public WorldGenMineshaftStart(World world, Random random, int i, int j, WorldGenMineshaft.Type worldgenmineshaft_type) {
        super(i, j);
        this.c = worldgenmineshaft_type;
        WorldGenMineshaftPieces.WorldGenMineshaftRoom worldgenmineshaftpieces_worldgenmineshaftroom = new WorldGenMineshaftPieces.WorldGenMineshaftRoom(0, random, (i << 4) + 2, (j << 4) + 2, this.c);

        this.a.add(worldgenmineshaftpieces_worldgenmineshaftroom);
        worldgenmineshaftpieces_worldgenmineshaftroom.a((StructurePiece) worldgenmineshaftpieces_worldgenmineshaftroom, this.a, random);
        this.d();
        if (worldgenmineshaft_type == WorldGenMineshaft.Type.MESA) {
            boolean flag = true;
            int k = world.getSeaLevel() - this.b.e + this.b.d() / 2 - -5;

            this.b.a(0, k, 0);
            Iterator iterator = this.a.iterator();

            while (iterator.hasNext()) {
                StructurePiece structurepiece = (StructurePiece) iterator.next();

                structurepiece.a(0, k, 0);
            }
        } else {
            this.a(world, random, 10);
        }

    }
}
