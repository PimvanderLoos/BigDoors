package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public class WorldGenVillagePieces {

    public static void a() {
        WorldGenFactory.a(WorldGenVillagePieces.WorldGenVillageLibrary.class, "ViBH");
        WorldGenFactory.a(WorldGenVillagePieces.WorldGenVillageFarm2.class, "ViDF");
        WorldGenFactory.a(WorldGenVillagePieces.WorldGenVillageFarm.class, "ViF");
        WorldGenFactory.a(WorldGenVillagePieces.WorldGenVillageLight.class, "ViL");
        WorldGenFactory.a(WorldGenVillagePieces.WorldGenVillageButcher.class, "ViPH");
        WorldGenFactory.a(WorldGenVillagePieces.WorldGenVillageHouse.class, "ViSH");
        WorldGenFactory.a(WorldGenVillagePieces.WorldGenVillageHut.class, "ViSmH");
        WorldGenFactory.a(WorldGenVillagePieces.WorldGenVillageTemple.class, "ViST");
        WorldGenFactory.a(WorldGenVillagePieces.WorldGenVillageBlacksmith.class, "ViS");
        WorldGenFactory.a(WorldGenVillagePieces.WorldGenVillageStartPiece.class, "ViStart");
        WorldGenFactory.a(WorldGenVillagePieces.WorldGenVillageRoad.class, "ViSR");
        WorldGenFactory.a(WorldGenVillagePieces.WorldGenVillageHouse2.class, "ViTRH");
        WorldGenFactory.a(WorldGenVillagePieces.WorldGenVillageWell.class, "ViW");
    }

    public static List<WorldGenVillagePieces.WorldGenVillagePieceWeight> a(Random random, int i) {
        ArrayList arraylist = Lists.newArrayList();

        arraylist.add(new WorldGenVillagePieces.WorldGenVillagePieceWeight(WorldGenVillagePieces.WorldGenVillageHouse.class, 4, MathHelper.nextInt(random, 2 + i, 4 + i * 2)));
        arraylist.add(new WorldGenVillagePieces.WorldGenVillagePieceWeight(WorldGenVillagePieces.WorldGenVillageTemple.class, 20, MathHelper.nextInt(random, 0 + i, 1 + i)));
        arraylist.add(new WorldGenVillagePieces.WorldGenVillagePieceWeight(WorldGenVillagePieces.WorldGenVillageLibrary.class, 20, MathHelper.nextInt(random, 0 + i, 2 + i)));
        arraylist.add(new WorldGenVillagePieces.WorldGenVillagePieceWeight(WorldGenVillagePieces.WorldGenVillageHut.class, 3, MathHelper.nextInt(random, 2 + i, 5 + i * 3)));
        arraylist.add(new WorldGenVillagePieces.WorldGenVillagePieceWeight(WorldGenVillagePieces.WorldGenVillageButcher.class, 15, MathHelper.nextInt(random, 0 + i, 2 + i)));
        arraylist.add(new WorldGenVillagePieces.WorldGenVillagePieceWeight(WorldGenVillagePieces.WorldGenVillageFarm2.class, 3, MathHelper.nextInt(random, 1 + i, 4 + i)));
        arraylist.add(new WorldGenVillagePieces.WorldGenVillagePieceWeight(WorldGenVillagePieces.WorldGenVillageFarm.class, 3, MathHelper.nextInt(random, 2 + i, 4 + i * 2)));
        arraylist.add(new WorldGenVillagePieces.WorldGenVillagePieceWeight(WorldGenVillagePieces.WorldGenVillageBlacksmith.class, 15, MathHelper.nextInt(random, 0, 1 + i)));
        arraylist.add(new WorldGenVillagePieces.WorldGenVillagePieceWeight(WorldGenVillagePieces.WorldGenVillageHouse2.class, 8, MathHelper.nextInt(random, 0 + i, 3 + i * 2)));
        Iterator iterator = arraylist.iterator();

        while (iterator.hasNext()) {
            if (((WorldGenVillagePieces.WorldGenVillagePieceWeight) iterator.next()).d == 0) {
                iterator.remove();
            }
        }

        return arraylist;
    }

    private static int a(List<WorldGenVillagePieces.WorldGenVillagePieceWeight> list) {
        boolean flag = false;
        int i = 0;

        WorldGenVillagePieces.WorldGenVillagePieceWeight worldgenvillagepieces_worldgenvillagepieceweight;

        for (Iterator iterator = list.iterator(); iterator.hasNext(); i += worldgenvillagepieces_worldgenvillagepieceweight.b) {
            worldgenvillagepieces_worldgenvillagepieceweight = (WorldGenVillagePieces.WorldGenVillagePieceWeight) iterator.next();
            if (worldgenvillagepieces_worldgenvillagepieceweight.d > 0 && worldgenvillagepieces_worldgenvillagepieceweight.c < worldgenvillagepieces_worldgenvillagepieceweight.d) {
                flag = true;
            }
        }

        return flag ? i : -1;
    }

    private static WorldGenVillagePieces.WorldGenVillagePiece a(WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece, WorldGenVillagePieces.WorldGenVillagePieceWeight worldgenvillagepieces_worldgenvillagepieceweight, List<StructurePiece> list, Random random, int i, int j, int k, EnumDirection enumdirection, int l) {
        Class oclass = worldgenvillagepieces_worldgenvillagepieceweight.a;
        Object object = null;

        if (oclass == WorldGenVillagePieces.WorldGenVillageHouse.class) {
            object = WorldGenVillagePieces.WorldGenVillageHouse.a(worldgenvillagepieces_worldgenvillagestartpiece, list, random, i, j, k, enumdirection, l);
        } else if (oclass == WorldGenVillagePieces.WorldGenVillageTemple.class) {
            object = WorldGenVillagePieces.WorldGenVillageTemple.a(worldgenvillagepieces_worldgenvillagestartpiece, list, random, i, j, k, enumdirection, l);
        } else if (oclass == WorldGenVillagePieces.WorldGenVillageLibrary.class) {
            object = WorldGenVillagePieces.WorldGenVillageLibrary.a(worldgenvillagepieces_worldgenvillagestartpiece, list, random, i, j, k, enumdirection, l);
        } else if (oclass == WorldGenVillagePieces.WorldGenVillageHut.class) {
            object = WorldGenVillagePieces.WorldGenVillageHut.a(worldgenvillagepieces_worldgenvillagestartpiece, list, random, i, j, k, enumdirection, l);
        } else if (oclass == WorldGenVillagePieces.WorldGenVillageButcher.class) {
            object = WorldGenVillagePieces.WorldGenVillageButcher.a(worldgenvillagepieces_worldgenvillagestartpiece, list, random, i, j, k, enumdirection, l);
        } else if (oclass == WorldGenVillagePieces.WorldGenVillageFarm2.class) {
            object = WorldGenVillagePieces.WorldGenVillageFarm2.a(worldgenvillagepieces_worldgenvillagestartpiece, list, random, i, j, k, enumdirection, l);
        } else if (oclass == WorldGenVillagePieces.WorldGenVillageFarm.class) {
            object = WorldGenVillagePieces.WorldGenVillageFarm.a(worldgenvillagepieces_worldgenvillagestartpiece, list, random, i, j, k, enumdirection, l);
        } else if (oclass == WorldGenVillagePieces.WorldGenVillageBlacksmith.class) {
            object = WorldGenVillagePieces.WorldGenVillageBlacksmith.a(worldgenvillagepieces_worldgenvillagestartpiece, list, random, i, j, k, enumdirection, l);
        } else if (oclass == WorldGenVillagePieces.WorldGenVillageHouse2.class) {
            object = WorldGenVillagePieces.WorldGenVillageHouse2.a(worldgenvillagepieces_worldgenvillagestartpiece, list, random, i, j, k, enumdirection, l);
        }

        return (WorldGenVillagePieces.WorldGenVillagePiece) object;
    }

    private static WorldGenVillagePieces.WorldGenVillagePiece c(WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece, List<StructurePiece> list, Random random, int i, int j, int k, EnumDirection enumdirection, int l) {
        int i1 = a(worldgenvillagepieces_worldgenvillagestartpiece.d);

        if (i1 <= 0) {
            return null;
        } else {
            int j1 = 0;

            while (j1 < 5) {
                ++j1;
                int k1 = random.nextInt(i1);
                Iterator iterator = worldgenvillagepieces_worldgenvillagestartpiece.d.iterator();

                while (iterator.hasNext()) {
                    WorldGenVillagePieces.WorldGenVillagePieceWeight worldgenvillagepieces_worldgenvillagepieceweight = (WorldGenVillagePieces.WorldGenVillagePieceWeight) iterator.next();

                    k1 -= worldgenvillagepieces_worldgenvillagepieceweight.b;
                    if (k1 < 0) {
                        if (!worldgenvillagepieces_worldgenvillagepieceweight.a(l) || worldgenvillagepieces_worldgenvillagepieceweight == worldgenvillagepieces_worldgenvillagestartpiece.c && worldgenvillagepieces_worldgenvillagestartpiece.d.size() > 1) {
                            break;
                        }

                        WorldGenVillagePieces.WorldGenVillagePiece worldgenvillagepieces_worldgenvillagepiece = a(worldgenvillagepieces_worldgenvillagestartpiece, worldgenvillagepieces_worldgenvillagepieceweight, list, random, i, j, k, enumdirection, l);

                        if (worldgenvillagepieces_worldgenvillagepiece != null) {
                            ++worldgenvillagepieces_worldgenvillagepieceweight.c;
                            worldgenvillagepieces_worldgenvillagestartpiece.c = worldgenvillagepieces_worldgenvillagepieceweight;
                            if (!worldgenvillagepieces_worldgenvillagepieceweight.a()) {
                                worldgenvillagepieces_worldgenvillagestartpiece.d.remove(worldgenvillagepieces_worldgenvillagepieceweight);
                            }

                            return worldgenvillagepieces_worldgenvillagepiece;
                        }
                    }
                }
            }

            StructureBoundingBox structureboundingbox = WorldGenVillagePieces.WorldGenVillageLight.a(worldgenvillagepieces_worldgenvillagestartpiece, list, random, i, j, k, enumdirection);

            if (structureboundingbox != null) {
                return new WorldGenVillagePieces.WorldGenVillageLight(worldgenvillagepieces_worldgenvillagestartpiece, l, random, structureboundingbox, enumdirection);
            } else {
                return null;
            }
        }
    }

    private static StructurePiece d(WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece, List<StructurePiece> list, Random random, int i, int j, int k, EnumDirection enumdirection, int l) {
        if (l > 50) {
            return null;
        } else if (Math.abs(i - worldgenvillagepieces_worldgenvillagestartpiece.d().a) <= 112 && Math.abs(k - worldgenvillagepieces_worldgenvillagestartpiece.d().c) <= 112) {
            WorldGenVillagePieces.WorldGenVillagePiece worldgenvillagepieces_worldgenvillagepiece = c(worldgenvillagepieces_worldgenvillagestartpiece, list, random, i, j, k, enumdirection, l + 1);

            if (worldgenvillagepieces_worldgenvillagepiece != null) {
                list.add(worldgenvillagepieces_worldgenvillagepiece);
                worldgenvillagepieces_worldgenvillagestartpiece.e.add(worldgenvillagepieces_worldgenvillagepiece);
                return worldgenvillagepieces_worldgenvillagepiece;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private static StructurePiece e(WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece, List<StructurePiece> list, Random random, int i, int j, int k, EnumDirection enumdirection, int l) {
        if (l > 3 + worldgenvillagepieces_worldgenvillagestartpiece.b) {
            return null;
        } else if (Math.abs(i - worldgenvillagepieces_worldgenvillagestartpiece.d().a) <= 112 && Math.abs(k - worldgenvillagepieces_worldgenvillagestartpiece.d().c) <= 112) {
            StructureBoundingBox structureboundingbox = WorldGenVillagePieces.WorldGenVillageRoad.a(worldgenvillagepieces_worldgenvillagestartpiece, list, random, i, j, k, enumdirection);

            if (structureboundingbox != null && structureboundingbox.b > 10) {
                WorldGenVillagePieces.WorldGenVillageRoad worldgenvillagepieces_worldgenvillageroad = new WorldGenVillagePieces.WorldGenVillageRoad(worldgenvillagepieces_worldgenvillagestartpiece, l, random, structureboundingbox, enumdirection);

                list.add(worldgenvillagepieces_worldgenvillageroad);
                worldgenvillagepieces_worldgenvillagestartpiece.f.add(worldgenvillagepieces_worldgenvillageroad);
                return worldgenvillagepieces_worldgenvillageroad;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static class WorldGenVillageLight extends WorldGenVillagePieces.WorldGenVillagePiece {

        public WorldGenVillageLight() {}

        public WorldGenVillageLight(WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece, int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(worldgenvillagepieces_worldgenvillagestartpiece, i);
            this.a(enumdirection);
            this.l = structureboundingbox;
        }

        public static StructureBoundingBox a(WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece, List<StructurePiece> list, Random random, int i, int j, int k, EnumDirection enumdirection) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, 0, 0, 0, 3, 4, 2, enumdirection);

            return StructurePiece.a(list, structureboundingbox) != null ? null : structureboundingbox;
        }

        public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
            if (this.g < 0) {
                this.g = this.b(world, structureboundingbox);
                if (this.g < 0) {
                    return true;
                }

                this.l.a(0, this.g - this.l.e + 4 - 1, 0);
            }

            IBlockData iblockdata = this.a(Blocks.FENCE.getBlockData());

            this.a(world, structureboundingbox, 0, 0, 0, 2, 3, 1, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, iblockdata, 1, 0, 0, structureboundingbox);
            this.a(world, iblockdata, 1, 1, 0, structureboundingbox);
            this.a(world, iblockdata, 1, 2, 0, structureboundingbox);
            this.a(world, Blocks.WOOL.fromLegacyData(EnumColor.WHITE.getInvColorIndex()), 1, 3, 0, structureboundingbox);
            this.a(world, EnumDirection.EAST, 2, 3, 0, structureboundingbox);
            this.a(world, EnumDirection.NORTH, 1, 3, 1, structureboundingbox);
            this.a(world, EnumDirection.WEST, 0, 3, 0, structureboundingbox);
            this.a(world, EnumDirection.SOUTH, 1, 3, -1, structureboundingbox);
            return true;
        }
    }

    public static class WorldGenVillageFarm2 extends WorldGenVillagePieces.WorldGenVillagePiece {

        private Block a;
        private Block b;
        private Block c;
        private Block d;

        public WorldGenVillageFarm2() {}

        public WorldGenVillageFarm2(WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece, int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(worldgenvillagepieces_worldgenvillagestartpiece, i);
            this.a(enumdirection);
            this.l = structureboundingbox;
            this.a = this.a(random);
            this.b = this.a(random);
            this.c = this.a(random);
            this.d = this.a(random);
        }

        protected void a(NBTTagCompound nbttagcompound) {
            super.a(nbttagcompound);
            nbttagcompound.setInt("CA", Block.REGISTRY.a((Object) this.a));
            nbttagcompound.setInt("CB", Block.REGISTRY.a((Object) this.b));
            nbttagcompound.setInt("CC", Block.REGISTRY.a((Object) this.c));
            nbttagcompound.setInt("CD", Block.REGISTRY.a((Object) this.d));
        }

        protected void a(NBTTagCompound nbttagcompound, DefinedStructureManager definedstructuremanager) {
            super.a(nbttagcompound, definedstructuremanager);
            this.a = Block.getById(nbttagcompound.getInt("CA"));
            this.b = Block.getById(nbttagcompound.getInt("CB"));
            this.c = Block.getById(nbttagcompound.getInt("CC"));
            this.d = Block.getById(nbttagcompound.getInt("CD"));
            if (!(this.a instanceof BlockCrops)) {
                this.a = Blocks.WHEAT;
            }

            if (!(this.b instanceof BlockCrops)) {
                this.b = Blocks.CARROTS;
            }

            if (!(this.c instanceof BlockCrops)) {
                this.c = Blocks.POTATOES;
            }

            if (!(this.d instanceof BlockCrops)) {
                this.d = Blocks.BEETROOT;
            }

        }

        private Block a(Random random) {
            switch (random.nextInt(10)) {
            case 0:
            case 1:
                return Blocks.CARROTS;

            case 2:
            case 3:
                return Blocks.POTATOES;

            case 4:
                return Blocks.BEETROOT;

            default:
                return Blocks.WHEAT;
            }
        }

        public static WorldGenVillagePieces.WorldGenVillageFarm2 a(WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece, List<StructurePiece> list, Random random, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, 0, 0, 0, 13, 4, 9, enumdirection);

            return a(structureboundingbox) && StructurePiece.a(list, structureboundingbox) == null ? new WorldGenVillagePieces.WorldGenVillageFarm2(worldgenvillagepieces_worldgenvillagestartpiece, l, random, structureboundingbox, enumdirection) : null;
        }

        public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
            if (this.g < 0) {
                this.g = this.b(world, structureboundingbox);
                if (this.g < 0) {
                    return true;
                }

                this.l.a(0, this.g - this.l.e + 4 - 1, 0);
            }

            IBlockData iblockdata = this.a(Blocks.LOG.getBlockData());

            this.a(world, structureboundingbox, 0, 1, 0, 12, 4, 8, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, 1, 0, 1, 2, 0, 7, Blocks.FARMLAND.getBlockData(), Blocks.FARMLAND.getBlockData(), false);
            this.a(world, structureboundingbox, 4, 0, 1, 5, 0, 7, Blocks.FARMLAND.getBlockData(), Blocks.FARMLAND.getBlockData(), false);
            this.a(world, structureboundingbox, 7, 0, 1, 8, 0, 7, Blocks.FARMLAND.getBlockData(), Blocks.FARMLAND.getBlockData(), false);
            this.a(world, structureboundingbox, 10, 0, 1, 11, 0, 7, Blocks.FARMLAND.getBlockData(), Blocks.FARMLAND.getBlockData(), false);
            this.a(world, structureboundingbox, 0, 0, 0, 0, 0, 8, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 6, 0, 0, 6, 0, 8, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 12, 0, 0, 12, 0, 8, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 1, 0, 0, 11, 0, 0, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 1, 0, 8, 11, 0, 8, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 3, 0, 1, 3, 0, 7, Blocks.WATER.getBlockData(), Blocks.WATER.getBlockData(), false);
            this.a(world, structureboundingbox, 9, 0, 1, 9, 0, 7, Blocks.WATER.getBlockData(), Blocks.WATER.getBlockData(), false);

            int i;
            int j;

            for (i = 1; i <= 7; ++i) {
                j = ((BlockCrops) this.a).g();
                int k = j / 3;

                this.a(world, this.a.fromLegacyData(MathHelper.nextInt(random, k, j)), 1, 1, i, structureboundingbox);
                this.a(world, this.a.fromLegacyData(MathHelper.nextInt(random, k, j)), 2, 1, i, structureboundingbox);
                int l = ((BlockCrops) this.b).g();
                int i1 = l / 3;

                this.a(world, this.b.fromLegacyData(MathHelper.nextInt(random, i1, l)), 4, 1, i, structureboundingbox);
                this.a(world, this.b.fromLegacyData(MathHelper.nextInt(random, i1, l)), 5, 1, i, structureboundingbox);
                int j1 = ((BlockCrops) this.c).g();
                int k1 = j1 / 3;

                this.a(world, this.c.fromLegacyData(MathHelper.nextInt(random, k1, j1)), 7, 1, i, structureboundingbox);
                this.a(world, this.c.fromLegacyData(MathHelper.nextInt(random, k1, j1)), 8, 1, i, structureboundingbox);
                int l1 = ((BlockCrops) this.d).g();
                int i2 = l1 / 3;

                this.a(world, this.d.fromLegacyData(MathHelper.nextInt(random, i2, l1)), 10, 1, i, structureboundingbox);
                this.a(world, this.d.fromLegacyData(MathHelper.nextInt(random, i2, l1)), 11, 1, i, structureboundingbox);
            }

            for (i = 0; i < 9; ++i) {
                for (j = 0; j < 13; ++j) {
                    this.c(world, j, 4, i, structureboundingbox);
                    this.b(world, Blocks.DIRT.getBlockData(), j, -1, i, structureboundingbox);
                }
            }

            return true;
        }
    }

    public static class WorldGenVillageFarm extends WorldGenVillagePieces.WorldGenVillagePiece {

        private Block a;
        private Block b;

        public WorldGenVillageFarm() {}

        public WorldGenVillageFarm(WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece, int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(worldgenvillagepieces_worldgenvillagestartpiece, i);
            this.a(enumdirection);
            this.l = structureboundingbox;
            this.a = this.a(random);
            this.b = this.a(random);
        }

        protected void a(NBTTagCompound nbttagcompound) {
            super.a(nbttagcompound);
            nbttagcompound.setInt("CA", Block.REGISTRY.a((Object) this.a));
            nbttagcompound.setInt("CB", Block.REGISTRY.a((Object) this.b));
        }

        protected void a(NBTTagCompound nbttagcompound, DefinedStructureManager definedstructuremanager) {
            super.a(nbttagcompound, definedstructuremanager);
            this.a = Block.getById(nbttagcompound.getInt("CA"));
            this.b = Block.getById(nbttagcompound.getInt("CB"));
        }

        private Block a(Random random) {
            switch (random.nextInt(10)) {
            case 0:
            case 1:
                return Blocks.CARROTS;

            case 2:
            case 3:
                return Blocks.POTATOES;

            case 4:
                return Blocks.BEETROOT;

            default:
                return Blocks.WHEAT;
            }
        }

        public static WorldGenVillagePieces.WorldGenVillageFarm a(WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece, List<StructurePiece> list, Random random, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, 0, 0, 0, 7, 4, 9, enumdirection);

            return a(structureboundingbox) && StructurePiece.a(list, structureboundingbox) == null ? new WorldGenVillagePieces.WorldGenVillageFarm(worldgenvillagepieces_worldgenvillagestartpiece, l, random, structureboundingbox, enumdirection) : null;
        }

        public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
            if (this.g < 0) {
                this.g = this.b(world, structureboundingbox);
                if (this.g < 0) {
                    return true;
                }

                this.l.a(0, this.g - this.l.e + 4 - 1, 0);
            }

            IBlockData iblockdata = this.a(Blocks.LOG.getBlockData());

            this.a(world, structureboundingbox, 0, 1, 0, 6, 4, 8, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, 1, 0, 1, 2, 0, 7, Blocks.FARMLAND.getBlockData(), Blocks.FARMLAND.getBlockData(), false);
            this.a(world, structureboundingbox, 4, 0, 1, 5, 0, 7, Blocks.FARMLAND.getBlockData(), Blocks.FARMLAND.getBlockData(), false);
            this.a(world, structureboundingbox, 0, 0, 0, 0, 0, 8, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 6, 0, 0, 6, 0, 8, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 1, 0, 0, 5, 0, 0, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 1, 0, 8, 5, 0, 8, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 3, 0, 1, 3, 0, 7, Blocks.WATER.getBlockData(), Blocks.WATER.getBlockData(), false);

            int i;
            int j;

            for (i = 1; i <= 7; ++i) {
                j = ((BlockCrops) this.a).g();
                int k = j / 3;

                this.a(world, this.a.fromLegacyData(MathHelper.nextInt(random, k, j)), 1, 1, i, structureboundingbox);
                this.a(world, this.a.fromLegacyData(MathHelper.nextInt(random, k, j)), 2, 1, i, structureboundingbox);
                int l = ((BlockCrops) this.b).g();
                int i1 = l / 3;

                this.a(world, this.b.fromLegacyData(MathHelper.nextInt(random, i1, l)), 4, 1, i, structureboundingbox);
                this.a(world, this.b.fromLegacyData(MathHelper.nextInt(random, i1, l)), 5, 1, i, structureboundingbox);
            }

            for (i = 0; i < 9; ++i) {
                for (j = 0; j < 7; ++j) {
                    this.c(world, j, 4, i, structureboundingbox);
                    this.b(world, Blocks.DIRT.getBlockData(), j, -1, i, structureboundingbox);
                }
            }

            return true;
        }
    }

    public static class WorldGenVillageBlacksmith extends WorldGenVillagePieces.WorldGenVillagePiece {

        private boolean a;

        public WorldGenVillageBlacksmith() {}

        public WorldGenVillageBlacksmith(WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece, int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(worldgenvillagepieces_worldgenvillagestartpiece, i);
            this.a(enumdirection);
            this.l = structureboundingbox;
        }

        public static WorldGenVillagePieces.WorldGenVillageBlacksmith a(WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece, List<StructurePiece> list, Random random, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, 0, 0, 0, 10, 6, 7, enumdirection);

            return a(structureboundingbox) && StructurePiece.a(list, structureboundingbox) == null ? new WorldGenVillagePieces.WorldGenVillageBlacksmith(worldgenvillagepieces_worldgenvillagestartpiece, l, random, structureboundingbox, enumdirection) : null;
        }

        protected void a(NBTTagCompound nbttagcompound) {
            super.a(nbttagcompound);
            nbttagcompound.setBoolean("Chest", this.a);
        }

        protected void a(NBTTagCompound nbttagcompound, DefinedStructureManager definedstructuremanager) {
            super.a(nbttagcompound, definedstructuremanager);
            this.a = nbttagcompound.getBoolean("Chest");
        }

        public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
            if (this.g < 0) {
                this.g = this.b(world, structureboundingbox);
                if (this.g < 0) {
                    return true;
                }

                this.l.a(0, this.g - this.l.e + 6 - 1, 0);
            }

            IBlockData iblockdata = Blocks.COBBLESTONE.getBlockData();
            IBlockData iblockdata1 = this.a(Blocks.OAK_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.NORTH));
            IBlockData iblockdata2 = this.a(Blocks.OAK_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.WEST));
            IBlockData iblockdata3 = this.a(Blocks.PLANKS.getBlockData());
            IBlockData iblockdata4 = this.a(Blocks.STONE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.NORTH));
            IBlockData iblockdata5 = this.a(Blocks.LOG.getBlockData());
            IBlockData iblockdata6 = this.a(Blocks.FENCE.getBlockData());

            this.a(world, structureboundingbox, 0, 1, 0, 9, 4, 6, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, 0, 0, 0, 9, 0, 6, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 0, 4, 0, 9, 4, 6, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 0, 5, 0, 9, 5, 6, Blocks.STONE_SLAB.getBlockData(), Blocks.STONE_SLAB.getBlockData(), false);
            this.a(world, structureboundingbox, 1, 5, 1, 8, 5, 5, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, 1, 1, 0, 2, 3, 0, iblockdata3, iblockdata3, false);
            this.a(world, structureboundingbox, 0, 1, 0, 0, 4, 0, iblockdata5, iblockdata5, false);
            this.a(world, structureboundingbox, 3, 1, 0, 3, 4, 0, iblockdata5, iblockdata5, false);
            this.a(world, structureboundingbox, 0, 1, 6, 0, 4, 6, iblockdata5, iblockdata5, false);
            this.a(world, iblockdata3, 3, 3, 1, structureboundingbox);
            this.a(world, structureboundingbox, 3, 1, 2, 3, 3, 2, iblockdata3, iblockdata3, false);
            this.a(world, structureboundingbox, 4, 1, 3, 5, 3, 3, iblockdata3, iblockdata3, false);
            this.a(world, structureboundingbox, 0, 1, 1, 0, 3, 5, iblockdata3, iblockdata3, false);
            this.a(world, structureboundingbox, 1, 1, 6, 5, 3, 6, iblockdata3, iblockdata3, false);
            this.a(world, structureboundingbox, 5, 1, 0, 5, 3, 0, iblockdata6, iblockdata6, false);
            this.a(world, structureboundingbox, 9, 1, 0, 9, 3, 0, iblockdata6, iblockdata6, false);
            this.a(world, structureboundingbox, 6, 1, 4, 9, 4, 6, iblockdata, iblockdata, false);
            this.a(world, Blocks.FLOWING_LAVA.getBlockData(), 7, 1, 5, structureboundingbox);
            this.a(world, Blocks.FLOWING_LAVA.getBlockData(), 8, 1, 5, structureboundingbox);
            this.a(world, Blocks.IRON_BARS.getBlockData(), 9, 2, 5, structureboundingbox);
            this.a(world, Blocks.IRON_BARS.getBlockData(), 9, 2, 4, structureboundingbox);
            this.a(world, structureboundingbox, 7, 2, 4, 8, 2, 5, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, iblockdata, 6, 1, 3, structureboundingbox);
            this.a(world, Blocks.FURNACE.getBlockData(), 6, 2, 3, structureboundingbox);
            this.a(world, Blocks.FURNACE.getBlockData(), 6, 3, 3, structureboundingbox);
            this.a(world, Blocks.DOUBLE_STONE_SLAB.getBlockData(), 8, 1, 1, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 0, 2, 2, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 0, 2, 4, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 2, 2, 6, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 4, 2, 6, structureboundingbox);
            this.a(world, iblockdata6, 2, 1, 4, structureboundingbox);
            this.a(world, Blocks.WOODEN_PRESSURE_PLATE.getBlockData(), 2, 2, 4, structureboundingbox);
            this.a(world, iblockdata3, 1, 1, 5, structureboundingbox);
            this.a(world, iblockdata1, 2, 1, 5, structureboundingbox);
            this.a(world, iblockdata2, 1, 1, 4, structureboundingbox);
            if (!this.a && structureboundingbox.b((BaseBlockPosition) (new BlockPosition(this.a(5, 5), this.d(1), this.b(5, 5))))) {
                this.a = true;
                this.a(world, structureboundingbox, random, 5, 1, 5, LootTables.e);
            }

            int i;

            for (i = 6; i <= 8; ++i) {
                if (this.a(world, i, 0, -1, structureboundingbox).getMaterial() == Material.AIR && this.a(world, i, -1, -1, structureboundingbox).getMaterial() != Material.AIR) {
                    this.a(world, iblockdata4, i, 0, -1, structureboundingbox);
                    if (this.a(world, i, -1, -1, structureboundingbox).getBlock() == Blocks.GRASS_PATH) {
                        this.a(world, Blocks.GRASS.getBlockData(), i, -1, -1, structureboundingbox);
                    }
                }
            }

            for (i = 0; i < 7; ++i) {
                for (int j = 0; j < 10; ++j) {
                    this.c(world, j, 6, i, structureboundingbox);
                    this.b(world, iblockdata, j, -1, i, structureboundingbox);
                }
            }

            this.a(world, structureboundingbox, 7, 1, 1, 1);
            return true;
        }

        protected int c(int i, int j) {
            return 3;
        }
    }

    public static class WorldGenVillageHouse2 extends WorldGenVillagePieces.WorldGenVillagePiece {

        public WorldGenVillageHouse2() {}

        public WorldGenVillageHouse2(WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece, int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(worldgenvillagepieces_worldgenvillagestartpiece, i);
            this.a(enumdirection);
            this.l = structureboundingbox;
        }

        public static WorldGenVillagePieces.WorldGenVillageHouse2 a(WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece, List<StructurePiece> list, Random random, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, 0, 0, 0, 9, 7, 12, enumdirection);

            return a(structureboundingbox) && StructurePiece.a(list, structureboundingbox) == null ? new WorldGenVillagePieces.WorldGenVillageHouse2(worldgenvillagepieces_worldgenvillagestartpiece, l, random, structureboundingbox, enumdirection) : null;
        }

        public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
            if (this.g < 0) {
                this.g = this.b(world, structureboundingbox);
                if (this.g < 0) {
                    return true;
                }

                this.l.a(0, this.g - this.l.e + 7 - 1, 0);
            }

            IBlockData iblockdata = this.a(Blocks.COBBLESTONE.getBlockData());
            IBlockData iblockdata1 = this.a(Blocks.OAK_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.NORTH));
            IBlockData iblockdata2 = this.a(Blocks.OAK_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.SOUTH));
            IBlockData iblockdata3 = this.a(Blocks.OAK_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.EAST));
            IBlockData iblockdata4 = this.a(Blocks.OAK_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.WEST));
            IBlockData iblockdata5 = this.a(Blocks.PLANKS.getBlockData());
            IBlockData iblockdata6 = this.a(Blocks.LOG.getBlockData());

            this.a(world, structureboundingbox, 1, 1, 1, 7, 4, 4, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, 2, 1, 6, 8, 4, 10, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, 2, 0, 5, 8, 0, 10, iblockdata5, iblockdata5, false);
            this.a(world, structureboundingbox, 1, 0, 1, 7, 0, 4, iblockdata5, iblockdata5, false);
            this.a(world, structureboundingbox, 0, 0, 0, 0, 3, 5, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 8, 0, 0, 8, 3, 10, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 1, 0, 0, 7, 2, 0, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 1, 0, 5, 2, 1, 5, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 2, 0, 6, 2, 3, 10, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 3, 0, 10, 7, 3, 10, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 1, 2, 0, 7, 3, 0, iblockdata5, iblockdata5, false);
            this.a(world, structureboundingbox, 1, 2, 5, 2, 3, 5, iblockdata5, iblockdata5, false);
            this.a(world, structureboundingbox, 0, 4, 1, 8, 4, 1, iblockdata5, iblockdata5, false);
            this.a(world, structureboundingbox, 0, 4, 4, 3, 4, 4, iblockdata5, iblockdata5, false);
            this.a(world, structureboundingbox, 0, 5, 2, 8, 5, 3, iblockdata5, iblockdata5, false);
            this.a(world, iblockdata5, 0, 4, 2, structureboundingbox);
            this.a(world, iblockdata5, 0, 4, 3, structureboundingbox);
            this.a(world, iblockdata5, 8, 4, 2, structureboundingbox);
            this.a(world, iblockdata5, 8, 4, 3, structureboundingbox);
            this.a(world, iblockdata5, 8, 4, 4, structureboundingbox);
            IBlockData iblockdata7 = iblockdata1;
            IBlockData iblockdata8 = iblockdata2;
            IBlockData iblockdata9 = iblockdata4;
            IBlockData iblockdata10 = iblockdata3;

            int i;
            int j;

            for (i = -1; i <= 2; ++i) {
                for (j = 0; j <= 8; ++j) {
                    this.a(world, iblockdata7, j, 4 + i, i, structureboundingbox);
                    if ((i > -1 || j <= 1) && (i > 0 || j <= 3) && (i > 1 || j <= 4 || j >= 6)) {
                        this.a(world, iblockdata8, j, 4 + i, 5 - i, structureboundingbox);
                    }
                }
            }

            this.a(world, structureboundingbox, 3, 4, 5, 3, 4, 10, iblockdata5, iblockdata5, false);
            this.a(world, structureboundingbox, 7, 4, 2, 7, 4, 10, iblockdata5, iblockdata5, false);
            this.a(world, structureboundingbox, 4, 5, 4, 4, 5, 10, iblockdata5, iblockdata5, false);
            this.a(world, structureboundingbox, 6, 5, 4, 6, 5, 10, iblockdata5, iblockdata5, false);
            this.a(world, structureboundingbox, 5, 6, 3, 5, 6, 10, iblockdata5, iblockdata5, false);

            for (i = 4; i >= 1; --i) {
                this.a(world, iblockdata5, i, 2 + i, 7 - i, structureboundingbox);

                for (j = 8 - i; j <= 10; ++j) {
                    this.a(world, iblockdata10, i, 2 + i, j, structureboundingbox);
                }
            }

            this.a(world, iblockdata5, 6, 6, 3, structureboundingbox);
            this.a(world, iblockdata5, 7, 5, 4, structureboundingbox);
            this.a(world, iblockdata4, 6, 6, 4, structureboundingbox);

            for (i = 6; i <= 8; ++i) {
                for (j = 5; j <= 10; ++j) {
                    this.a(world, iblockdata9, i, 12 - i, j, structureboundingbox);
                }
            }

            this.a(world, iblockdata6, 0, 2, 1, structureboundingbox);
            this.a(world, iblockdata6, 0, 2, 4, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 0, 2, 2, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 0, 2, 3, structureboundingbox);
            this.a(world, iblockdata6, 4, 2, 0, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 5, 2, 0, structureboundingbox);
            this.a(world, iblockdata6, 6, 2, 0, structureboundingbox);
            this.a(world, iblockdata6, 8, 2, 1, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 8, 2, 2, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 8, 2, 3, structureboundingbox);
            this.a(world, iblockdata6, 8, 2, 4, structureboundingbox);
            this.a(world, iblockdata5, 8, 2, 5, structureboundingbox);
            this.a(world, iblockdata6, 8, 2, 6, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 8, 2, 7, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 8, 2, 8, structureboundingbox);
            this.a(world, iblockdata6, 8, 2, 9, structureboundingbox);
            this.a(world, iblockdata6, 2, 2, 6, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 2, 2, 7, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 2, 2, 8, structureboundingbox);
            this.a(world, iblockdata6, 2, 2, 9, structureboundingbox);
            this.a(world, iblockdata6, 4, 4, 10, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 5, 4, 10, structureboundingbox);
            this.a(world, iblockdata6, 6, 4, 10, structureboundingbox);
            this.a(world, iblockdata5, 5, 5, 10, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 2, 1, 0, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 2, 2, 0, structureboundingbox);
            this.a(world, EnumDirection.NORTH, 2, 3, 1, structureboundingbox);
            this.a(world, structureboundingbox, random, 2, 1, 0, EnumDirection.NORTH);
            this.a(world, structureboundingbox, 1, 0, -1, 3, 2, -1, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            if (this.a(world, 2, 0, -1, structureboundingbox).getMaterial() == Material.AIR && this.a(world, 2, -1, -1, structureboundingbox).getMaterial() != Material.AIR) {
                this.a(world, iblockdata7, 2, 0, -1, structureboundingbox);
                if (this.a(world, 2, -1, -1, structureboundingbox).getBlock() == Blocks.GRASS_PATH) {
                    this.a(world, Blocks.GRASS.getBlockData(), 2, -1, -1, structureboundingbox);
                }
            }

            for (i = 0; i < 5; ++i) {
                for (j = 0; j < 9; ++j) {
                    this.c(world, j, 7, i, structureboundingbox);
                    this.b(world, iblockdata, j, -1, i, structureboundingbox);
                }
            }

            for (i = 5; i < 11; ++i) {
                for (j = 2; j < 9; ++j) {
                    this.c(world, j, 7, i, structureboundingbox);
                    this.b(world, iblockdata, j, -1, i, structureboundingbox);
                }
            }

            this.a(world, structureboundingbox, 4, 1, 2, 2);
            return true;
        }
    }

    public static class WorldGenVillageButcher extends WorldGenVillagePieces.WorldGenVillagePiece {

        public WorldGenVillageButcher() {}

        public WorldGenVillageButcher(WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece, int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(worldgenvillagepieces_worldgenvillagestartpiece, i);
            this.a(enumdirection);
            this.l = structureboundingbox;
        }

        public static WorldGenVillagePieces.WorldGenVillageButcher a(WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece, List<StructurePiece> list, Random random, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, 0, 0, 0, 9, 7, 11, enumdirection);

            return a(structureboundingbox) && StructurePiece.a(list, structureboundingbox) == null ? new WorldGenVillagePieces.WorldGenVillageButcher(worldgenvillagepieces_worldgenvillagestartpiece, l, random, structureboundingbox, enumdirection) : null;
        }

        public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
            if (this.g < 0) {
                this.g = this.b(world, structureboundingbox);
                if (this.g < 0) {
                    return true;
                }

                this.l.a(0, this.g - this.l.e + 7 - 1, 0);
            }

            IBlockData iblockdata = this.a(Blocks.COBBLESTONE.getBlockData());
            IBlockData iblockdata1 = this.a(Blocks.OAK_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.NORTH));
            IBlockData iblockdata2 = this.a(Blocks.OAK_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.SOUTH));
            IBlockData iblockdata3 = this.a(Blocks.OAK_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.WEST));
            IBlockData iblockdata4 = this.a(Blocks.PLANKS.getBlockData());
            IBlockData iblockdata5 = this.a(Blocks.LOG.getBlockData());
            IBlockData iblockdata6 = this.a(Blocks.FENCE.getBlockData());

            this.a(world, structureboundingbox, 1, 1, 1, 7, 4, 4, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, 2, 1, 6, 8, 4, 10, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, 2, 0, 6, 8, 0, 10, Blocks.DIRT.getBlockData(), Blocks.DIRT.getBlockData(), false);
            this.a(world, iblockdata, 6, 0, 6, structureboundingbox);
            this.a(world, structureboundingbox, 2, 1, 6, 2, 1, 10, iblockdata6, iblockdata6, false);
            this.a(world, structureboundingbox, 8, 1, 6, 8, 1, 10, iblockdata6, iblockdata6, false);
            this.a(world, structureboundingbox, 3, 1, 10, 7, 1, 10, iblockdata6, iblockdata6, false);
            this.a(world, structureboundingbox, 1, 0, 1, 7, 0, 4, iblockdata4, iblockdata4, false);
            this.a(world, structureboundingbox, 0, 0, 0, 0, 3, 5, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 8, 0, 0, 8, 3, 5, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 1, 0, 0, 7, 1, 0, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 1, 0, 5, 7, 1, 5, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 1, 2, 0, 7, 3, 0, iblockdata4, iblockdata4, false);
            this.a(world, structureboundingbox, 1, 2, 5, 7, 3, 5, iblockdata4, iblockdata4, false);
            this.a(world, structureboundingbox, 0, 4, 1, 8, 4, 1, iblockdata4, iblockdata4, false);
            this.a(world, structureboundingbox, 0, 4, 4, 8, 4, 4, iblockdata4, iblockdata4, false);
            this.a(world, structureboundingbox, 0, 5, 2, 8, 5, 3, iblockdata4, iblockdata4, false);
            this.a(world, iblockdata4, 0, 4, 2, structureboundingbox);
            this.a(world, iblockdata4, 0, 4, 3, structureboundingbox);
            this.a(world, iblockdata4, 8, 4, 2, structureboundingbox);
            this.a(world, iblockdata4, 8, 4, 3, structureboundingbox);
            IBlockData iblockdata7 = iblockdata1;
            IBlockData iblockdata8 = iblockdata2;

            int i;
            int j;

            for (i = -1; i <= 2; ++i) {
                for (j = 0; j <= 8; ++j) {
                    this.a(world, iblockdata7, j, 4 + i, i, structureboundingbox);
                    this.a(world, iblockdata8, j, 4 + i, 5 - i, structureboundingbox);
                }
            }

            this.a(world, iblockdata5, 0, 2, 1, structureboundingbox);
            this.a(world, iblockdata5, 0, 2, 4, structureboundingbox);
            this.a(world, iblockdata5, 8, 2, 1, structureboundingbox);
            this.a(world, iblockdata5, 8, 2, 4, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 0, 2, 2, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 0, 2, 3, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 8, 2, 2, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 8, 2, 3, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 2, 2, 5, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 3, 2, 5, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 5, 2, 0, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 6, 2, 5, structureboundingbox);
            this.a(world, iblockdata6, 2, 1, 3, structureboundingbox);
            this.a(world, Blocks.WOODEN_PRESSURE_PLATE.getBlockData(), 2, 2, 3, structureboundingbox);
            this.a(world, iblockdata4, 1, 1, 4, structureboundingbox);
            this.a(world, iblockdata7, 2, 1, 4, structureboundingbox);
            this.a(world, iblockdata3, 1, 1, 3, structureboundingbox);
            this.a(world, structureboundingbox, 5, 0, 1, 7, 0, 3, Blocks.DOUBLE_STONE_SLAB.getBlockData(), Blocks.DOUBLE_STONE_SLAB.getBlockData(), false);
            this.a(world, Blocks.DOUBLE_STONE_SLAB.getBlockData(), 6, 1, 1, structureboundingbox);
            this.a(world, Blocks.DOUBLE_STONE_SLAB.getBlockData(), 6, 1, 2, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 2, 1, 0, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 2, 2, 0, structureboundingbox);
            this.a(world, EnumDirection.NORTH, 2, 3, 1, structureboundingbox);
            this.a(world, structureboundingbox, random, 2, 1, 0, EnumDirection.NORTH);
            if (this.a(world, 2, 0, -1, structureboundingbox).getMaterial() == Material.AIR && this.a(world, 2, -1, -1, structureboundingbox).getMaterial() != Material.AIR) {
                this.a(world, iblockdata7, 2, 0, -1, structureboundingbox);
                if (this.a(world, 2, -1, -1, structureboundingbox).getBlock() == Blocks.GRASS_PATH) {
                    this.a(world, Blocks.GRASS.getBlockData(), 2, -1, -1, structureboundingbox);
                }
            }

            this.a(world, Blocks.AIR.getBlockData(), 6, 1, 5, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 6, 2, 5, structureboundingbox);
            this.a(world, EnumDirection.SOUTH, 6, 3, 4, structureboundingbox);
            this.a(world, structureboundingbox, random, 6, 1, 5, EnumDirection.SOUTH);

            for (i = 0; i < 5; ++i) {
                for (j = 0; j < 9; ++j) {
                    this.c(world, j, 7, i, structureboundingbox);
                    this.b(world, iblockdata, j, -1, i, structureboundingbox);
                }
            }

            this.a(world, structureboundingbox, 4, 1, 2, 2);
            return true;
        }

        protected int c(int i, int j) {
            return i == 0 ? 4 : super.c(i, j);
        }
    }

    public static class WorldGenVillageHut extends WorldGenVillagePieces.WorldGenVillagePiece {

        private boolean a;
        private int b;

        public WorldGenVillageHut() {}

        public WorldGenVillageHut(WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece, int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(worldgenvillagepieces_worldgenvillagestartpiece, i);
            this.a(enumdirection);
            this.l = structureboundingbox;
            this.a = random.nextBoolean();
            this.b = random.nextInt(3);
        }

        protected void a(NBTTagCompound nbttagcompound) {
            super.a(nbttagcompound);
            nbttagcompound.setInt("T", this.b);
            nbttagcompound.setBoolean("C", this.a);
        }

        protected void a(NBTTagCompound nbttagcompound, DefinedStructureManager definedstructuremanager) {
            super.a(nbttagcompound, definedstructuremanager);
            this.b = nbttagcompound.getInt("T");
            this.a = nbttagcompound.getBoolean("C");
        }

        public static WorldGenVillagePieces.WorldGenVillageHut a(WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece, List<StructurePiece> list, Random random, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, 0, 0, 0, 4, 6, 5, enumdirection);

            return a(structureboundingbox) && StructurePiece.a(list, structureboundingbox) == null ? new WorldGenVillagePieces.WorldGenVillageHut(worldgenvillagepieces_worldgenvillagestartpiece, l, random, structureboundingbox, enumdirection) : null;
        }

        public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
            if (this.g < 0) {
                this.g = this.b(world, structureboundingbox);
                if (this.g < 0) {
                    return true;
                }

                this.l.a(0, this.g - this.l.e + 6 - 1, 0);
            }

            IBlockData iblockdata = this.a(Blocks.COBBLESTONE.getBlockData());
            IBlockData iblockdata1 = this.a(Blocks.PLANKS.getBlockData());
            IBlockData iblockdata2 = this.a(Blocks.STONE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.NORTH));
            IBlockData iblockdata3 = this.a(Blocks.LOG.getBlockData());
            IBlockData iblockdata4 = this.a(Blocks.FENCE.getBlockData());

            this.a(world, structureboundingbox, 1, 1, 1, 3, 5, 4, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, 0, 0, 0, 3, 0, 4, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 1, 0, 1, 2, 0, 3, Blocks.DIRT.getBlockData(), Blocks.DIRT.getBlockData(), false);
            if (this.a) {
                this.a(world, structureboundingbox, 1, 4, 1, 2, 4, 3, iblockdata3, iblockdata3, false);
            } else {
                this.a(world, structureboundingbox, 1, 5, 1, 2, 5, 3, iblockdata3, iblockdata3, false);
            }

            this.a(world, iblockdata3, 1, 4, 0, structureboundingbox);
            this.a(world, iblockdata3, 2, 4, 0, structureboundingbox);
            this.a(world, iblockdata3, 1, 4, 4, structureboundingbox);
            this.a(world, iblockdata3, 2, 4, 4, structureboundingbox);
            this.a(world, iblockdata3, 0, 4, 1, structureboundingbox);
            this.a(world, iblockdata3, 0, 4, 2, structureboundingbox);
            this.a(world, iblockdata3, 0, 4, 3, structureboundingbox);
            this.a(world, iblockdata3, 3, 4, 1, structureboundingbox);
            this.a(world, iblockdata3, 3, 4, 2, structureboundingbox);
            this.a(world, iblockdata3, 3, 4, 3, structureboundingbox);
            this.a(world, structureboundingbox, 0, 1, 0, 0, 3, 0, iblockdata3, iblockdata3, false);
            this.a(world, structureboundingbox, 3, 1, 0, 3, 3, 0, iblockdata3, iblockdata3, false);
            this.a(world, structureboundingbox, 0, 1, 4, 0, 3, 4, iblockdata3, iblockdata3, false);
            this.a(world, structureboundingbox, 3, 1, 4, 3, 3, 4, iblockdata3, iblockdata3, false);
            this.a(world, structureboundingbox, 0, 1, 1, 0, 3, 3, iblockdata1, iblockdata1, false);
            this.a(world, structureboundingbox, 3, 1, 1, 3, 3, 3, iblockdata1, iblockdata1, false);
            this.a(world, structureboundingbox, 1, 1, 0, 2, 3, 0, iblockdata1, iblockdata1, false);
            this.a(world, structureboundingbox, 1, 1, 4, 2, 3, 4, iblockdata1, iblockdata1, false);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 0, 2, 2, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 3, 2, 2, structureboundingbox);
            if (this.b > 0) {
                this.a(world, iblockdata4, this.b, 1, 3, structureboundingbox);
                this.a(world, Blocks.WOODEN_PRESSURE_PLATE.getBlockData(), this.b, 2, 3, structureboundingbox);
            }

            this.a(world, Blocks.AIR.getBlockData(), 1, 1, 0, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 1, 2, 0, structureboundingbox);
            this.a(world, structureboundingbox, random, 1, 1, 0, EnumDirection.NORTH);
            if (this.a(world, 1, 0, -1, structureboundingbox).getMaterial() == Material.AIR && this.a(world, 1, -1, -1, structureboundingbox).getMaterial() != Material.AIR) {
                this.a(world, iblockdata2, 1, 0, -1, structureboundingbox);
                if (this.a(world, 1, -1, -1, structureboundingbox).getBlock() == Blocks.GRASS_PATH) {
                    this.a(world, Blocks.GRASS.getBlockData(), 1, -1, -1, structureboundingbox);
                }
            }

            for (int i = 0; i < 5; ++i) {
                for (int j = 0; j < 4; ++j) {
                    this.c(world, j, 6, i, structureboundingbox);
                    this.b(world, iblockdata, j, -1, i, structureboundingbox);
                }
            }

            this.a(world, structureboundingbox, 1, 1, 2, 1);
            return true;
        }
    }

    public static class WorldGenVillageLibrary extends WorldGenVillagePieces.WorldGenVillagePiece {

        public WorldGenVillageLibrary() {}

        public WorldGenVillageLibrary(WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece, int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(worldgenvillagepieces_worldgenvillagestartpiece, i);
            this.a(enumdirection);
            this.l = structureboundingbox;
        }

        public static WorldGenVillagePieces.WorldGenVillageLibrary a(WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece, List<StructurePiece> list, Random random, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, 0, 0, 0, 9, 9, 6, enumdirection);

            return a(structureboundingbox) && StructurePiece.a(list, structureboundingbox) == null ? new WorldGenVillagePieces.WorldGenVillageLibrary(worldgenvillagepieces_worldgenvillagestartpiece, l, random, structureboundingbox, enumdirection) : null;
        }

        public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
            if (this.g < 0) {
                this.g = this.b(world, structureboundingbox);
                if (this.g < 0) {
                    return true;
                }

                this.l.a(0, this.g - this.l.e + 9 - 1, 0);
            }

            IBlockData iblockdata = this.a(Blocks.COBBLESTONE.getBlockData());
            IBlockData iblockdata1 = this.a(Blocks.OAK_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.NORTH));
            IBlockData iblockdata2 = this.a(Blocks.OAK_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.SOUTH));
            IBlockData iblockdata3 = this.a(Blocks.OAK_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.EAST));
            IBlockData iblockdata4 = this.a(Blocks.PLANKS.getBlockData());
            IBlockData iblockdata5 = this.a(Blocks.STONE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.NORTH));
            IBlockData iblockdata6 = this.a(Blocks.FENCE.getBlockData());

            this.a(world, structureboundingbox, 1, 1, 1, 7, 5, 4, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, 0, 0, 0, 8, 0, 5, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 0, 5, 0, 8, 5, 5, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 0, 6, 1, 8, 6, 4, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 0, 7, 2, 8, 7, 3, iblockdata, iblockdata, false);

            int i;

            for (int j = -1; j <= 2; ++j) {
                for (i = 0; i <= 8; ++i) {
                    this.a(world, iblockdata1, i, 6 + j, j, structureboundingbox);
                    this.a(world, iblockdata2, i, 6 + j, 5 - j, structureboundingbox);
                }
            }

            this.a(world, structureboundingbox, 0, 1, 0, 0, 1, 5, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 1, 1, 5, 8, 1, 5, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 8, 1, 0, 8, 1, 4, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 2, 1, 0, 7, 1, 0, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 0, 2, 0, 0, 4, 0, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 0, 2, 5, 0, 4, 5, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 8, 2, 5, 8, 4, 5, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 8, 2, 0, 8, 4, 0, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 0, 2, 1, 0, 4, 4, iblockdata4, iblockdata4, false);
            this.a(world, structureboundingbox, 1, 2, 5, 7, 4, 5, iblockdata4, iblockdata4, false);
            this.a(world, structureboundingbox, 8, 2, 1, 8, 4, 4, iblockdata4, iblockdata4, false);
            this.a(world, structureboundingbox, 1, 2, 0, 7, 4, 0, iblockdata4, iblockdata4, false);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 4, 2, 0, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 5, 2, 0, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 6, 2, 0, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 4, 3, 0, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 5, 3, 0, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 6, 3, 0, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 0, 2, 2, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 0, 2, 3, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 0, 3, 2, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 0, 3, 3, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 8, 2, 2, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 8, 2, 3, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 8, 3, 2, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 8, 3, 3, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 2, 2, 5, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 3, 2, 5, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 5, 2, 5, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 6, 2, 5, structureboundingbox);
            this.a(world, structureboundingbox, 1, 4, 1, 7, 4, 1, iblockdata4, iblockdata4, false);
            this.a(world, structureboundingbox, 1, 4, 4, 7, 4, 4, iblockdata4, iblockdata4, false);
            this.a(world, structureboundingbox, 1, 3, 4, 7, 3, 4, Blocks.BOOKSHELF.getBlockData(), Blocks.BOOKSHELF.getBlockData(), false);
            this.a(world, iblockdata4, 7, 1, 4, structureboundingbox);
            this.a(world, iblockdata3, 7, 1, 3, structureboundingbox);
            this.a(world, iblockdata1, 6, 1, 4, structureboundingbox);
            this.a(world, iblockdata1, 5, 1, 4, structureboundingbox);
            this.a(world, iblockdata1, 4, 1, 4, structureboundingbox);
            this.a(world, iblockdata1, 3, 1, 4, structureboundingbox);
            this.a(world, iblockdata6, 6, 1, 3, structureboundingbox);
            this.a(world, Blocks.WOODEN_PRESSURE_PLATE.getBlockData(), 6, 2, 3, structureboundingbox);
            this.a(world, iblockdata6, 4, 1, 3, structureboundingbox);
            this.a(world, Blocks.WOODEN_PRESSURE_PLATE.getBlockData(), 4, 2, 3, structureboundingbox);
            this.a(world, Blocks.CRAFTING_TABLE.getBlockData(), 7, 1, 1, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 1, 1, 0, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 1, 2, 0, structureboundingbox);
            this.a(world, structureboundingbox, random, 1, 1, 0, EnumDirection.NORTH);
            if (this.a(world, 1, 0, -1, structureboundingbox).getMaterial() == Material.AIR && this.a(world, 1, -1, -1, structureboundingbox).getMaterial() != Material.AIR) {
                this.a(world, iblockdata5, 1, 0, -1, structureboundingbox);
                if (this.a(world, 1, -1, -1, structureboundingbox).getBlock() == Blocks.GRASS_PATH) {
                    this.a(world, Blocks.GRASS.getBlockData(), 1, -1, -1, structureboundingbox);
                }
            }

            for (i = 0; i < 6; ++i) {
                for (int k = 0; k < 9; ++k) {
                    this.c(world, k, 9, i, structureboundingbox);
                    this.b(world, iblockdata, k, -1, i, structureboundingbox);
                }
            }

            this.a(world, structureboundingbox, 2, 1, 2, 1);
            return true;
        }

        protected int c(int i, int j) {
            return 1;
        }
    }

    public static class WorldGenVillageTemple extends WorldGenVillagePieces.WorldGenVillagePiece {

        public WorldGenVillageTemple() {}

        public WorldGenVillageTemple(WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece, int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(worldgenvillagepieces_worldgenvillagestartpiece, i);
            this.a(enumdirection);
            this.l = structureboundingbox;
        }

        public static WorldGenVillagePieces.WorldGenVillageTemple a(WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece, List<StructurePiece> list, Random random, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, 0, 0, 0, 5, 12, 9, enumdirection);

            return a(structureboundingbox) && StructurePiece.a(list, structureboundingbox) == null ? new WorldGenVillagePieces.WorldGenVillageTemple(worldgenvillagepieces_worldgenvillagestartpiece, l, random, structureboundingbox, enumdirection) : null;
        }

        public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
            if (this.g < 0) {
                this.g = this.b(world, structureboundingbox);
                if (this.g < 0) {
                    return true;
                }

                this.l.a(0, this.g - this.l.e + 12 - 1, 0);
            }

            IBlockData iblockdata = Blocks.COBBLESTONE.getBlockData();
            IBlockData iblockdata1 = this.a(Blocks.STONE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.NORTH));
            IBlockData iblockdata2 = this.a(Blocks.STONE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.WEST));
            IBlockData iblockdata3 = this.a(Blocks.STONE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.EAST));

            this.a(world, structureboundingbox, 1, 1, 1, 3, 3, 7, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, 1, 5, 1, 3, 9, 3, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, 1, 0, 0, 3, 0, 8, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 1, 1, 0, 3, 10, 0, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 0, 1, 1, 0, 10, 3, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 4, 1, 1, 4, 10, 3, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 0, 0, 4, 0, 4, 7, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 4, 0, 4, 4, 4, 7, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 1, 1, 8, 3, 4, 8, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 1, 5, 4, 3, 10, 4, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 1, 5, 5, 3, 5, 7, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 0, 9, 0, 4, 9, 4, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 0, 4, 0, 4, 4, 4, iblockdata, iblockdata, false);
            this.a(world, iblockdata, 0, 11, 2, structureboundingbox);
            this.a(world, iblockdata, 4, 11, 2, structureboundingbox);
            this.a(world, iblockdata, 2, 11, 0, structureboundingbox);
            this.a(world, iblockdata, 2, 11, 4, structureboundingbox);
            this.a(world, iblockdata, 1, 1, 6, structureboundingbox);
            this.a(world, iblockdata, 1, 1, 7, structureboundingbox);
            this.a(world, iblockdata, 2, 1, 7, structureboundingbox);
            this.a(world, iblockdata, 3, 1, 6, structureboundingbox);
            this.a(world, iblockdata, 3, 1, 7, structureboundingbox);
            this.a(world, iblockdata1, 1, 1, 5, structureboundingbox);
            this.a(world, iblockdata1, 2, 1, 6, structureboundingbox);
            this.a(world, iblockdata1, 3, 1, 5, structureboundingbox);
            this.a(world, iblockdata2, 1, 2, 7, structureboundingbox);
            this.a(world, iblockdata3, 3, 2, 7, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 0, 2, 2, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 0, 3, 2, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 4, 2, 2, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 4, 3, 2, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 0, 6, 2, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 0, 7, 2, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 4, 6, 2, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 4, 7, 2, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 2, 6, 0, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 2, 7, 0, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 2, 6, 4, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 2, 7, 4, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 0, 3, 6, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 4, 3, 6, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 2, 3, 8, structureboundingbox);
            this.a(world, EnumDirection.SOUTH, 2, 4, 7, structureboundingbox);
            this.a(world, EnumDirection.EAST, 1, 4, 6, structureboundingbox);
            this.a(world, EnumDirection.WEST, 3, 4, 6, structureboundingbox);
            this.a(world, EnumDirection.NORTH, 2, 4, 5, structureboundingbox);
            IBlockData iblockdata4 = Blocks.LADDER.getBlockData().set(BlockLadder.FACING, EnumDirection.WEST);

            int i;

            for (i = 1; i <= 9; ++i) {
                this.a(world, iblockdata4, 3, i, 3, structureboundingbox);
            }

            this.a(world, Blocks.AIR.getBlockData(), 2, 1, 0, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 2, 2, 0, structureboundingbox);
            this.a(world, structureboundingbox, random, 2, 1, 0, EnumDirection.NORTH);
            if (this.a(world, 2, 0, -1, structureboundingbox).getMaterial() == Material.AIR && this.a(world, 2, -1, -1, structureboundingbox).getMaterial() != Material.AIR) {
                this.a(world, iblockdata1, 2, 0, -1, structureboundingbox);
                if (this.a(world, 2, -1, -1, structureboundingbox).getBlock() == Blocks.GRASS_PATH) {
                    this.a(world, Blocks.GRASS.getBlockData(), 2, -1, -1, structureboundingbox);
                }
            }

            for (i = 0; i < 9; ++i) {
                for (int j = 0; j < 5; ++j) {
                    this.c(world, j, 12, i, structureboundingbox);
                    this.b(world, iblockdata, j, -1, i, structureboundingbox);
                }
            }

            this.a(world, structureboundingbox, 2, 1, 2, 1);
            return true;
        }

        protected int c(int i, int j) {
            return 2;
        }
    }

    public static class WorldGenVillageHouse extends WorldGenVillagePieces.WorldGenVillagePiece {

        private boolean a;

        public WorldGenVillageHouse() {}

        public WorldGenVillageHouse(WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece, int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(worldgenvillagepieces_worldgenvillagestartpiece, i);
            this.a(enumdirection);
            this.l = structureboundingbox;
            this.a = random.nextBoolean();
        }

        protected void a(NBTTagCompound nbttagcompound) {
            super.a(nbttagcompound);
            nbttagcompound.setBoolean("Terrace", this.a);
        }

        protected void a(NBTTagCompound nbttagcompound, DefinedStructureManager definedstructuremanager) {
            super.a(nbttagcompound, definedstructuremanager);
            this.a = nbttagcompound.getBoolean("Terrace");
        }

        public static WorldGenVillagePieces.WorldGenVillageHouse a(WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece, List<StructurePiece> list, Random random, int i, int j, int k, EnumDirection enumdirection, int l) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, 0, 0, 0, 5, 6, 5, enumdirection);

            return StructurePiece.a(list, structureboundingbox) != null ? null : new WorldGenVillagePieces.WorldGenVillageHouse(worldgenvillagepieces_worldgenvillagestartpiece, l, random, structureboundingbox, enumdirection);
        }

        public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
            if (this.g < 0) {
                this.g = this.b(world, structureboundingbox);
                if (this.g < 0) {
                    return true;
                }

                this.l.a(0, this.g - this.l.e + 6 - 1, 0);
            }

            IBlockData iblockdata = this.a(Blocks.COBBLESTONE.getBlockData());
            IBlockData iblockdata1 = this.a(Blocks.PLANKS.getBlockData());
            IBlockData iblockdata2 = this.a(Blocks.STONE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.NORTH));
            IBlockData iblockdata3 = this.a(Blocks.LOG.getBlockData());
            IBlockData iblockdata4 = this.a(Blocks.FENCE.getBlockData());

            this.a(world, structureboundingbox, 0, 0, 0, 4, 0, 4, iblockdata, iblockdata, false);
            this.a(world, structureboundingbox, 0, 4, 0, 4, 4, 4, iblockdata3, iblockdata3, false);
            this.a(world, structureboundingbox, 1, 4, 1, 3, 4, 3, iblockdata1, iblockdata1, false);
            this.a(world, iblockdata, 0, 1, 0, structureboundingbox);
            this.a(world, iblockdata, 0, 2, 0, structureboundingbox);
            this.a(world, iblockdata, 0, 3, 0, structureboundingbox);
            this.a(world, iblockdata, 4, 1, 0, structureboundingbox);
            this.a(world, iblockdata, 4, 2, 0, structureboundingbox);
            this.a(world, iblockdata, 4, 3, 0, structureboundingbox);
            this.a(world, iblockdata, 0, 1, 4, structureboundingbox);
            this.a(world, iblockdata, 0, 2, 4, structureboundingbox);
            this.a(world, iblockdata, 0, 3, 4, structureboundingbox);
            this.a(world, iblockdata, 4, 1, 4, structureboundingbox);
            this.a(world, iblockdata, 4, 2, 4, structureboundingbox);
            this.a(world, iblockdata, 4, 3, 4, structureboundingbox);
            this.a(world, structureboundingbox, 0, 1, 1, 0, 3, 3, iblockdata1, iblockdata1, false);
            this.a(world, structureboundingbox, 4, 1, 1, 4, 3, 3, iblockdata1, iblockdata1, false);
            this.a(world, structureboundingbox, 1, 1, 4, 3, 3, 4, iblockdata1, iblockdata1, false);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 0, 2, 2, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 2, 2, 4, structureboundingbox);
            this.a(world, Blocks.GLASS_PANE.getBlockData(), 4, 2, 2, structureboundingbox);
            this.a(world, iblockdata1, 1, 1, 0, structureboundingbox);
            this.a(world, iblockdata1, 1, 2, 0, structureboundingbox);
            this.a(world, iblockdata1, 1, 3, 0, structureboundingbox);
            this.a(world, iblockdata1, 2, 3, 0, structureboundingbox);
            this.a(world, iblockdata1, 3, 3, 0, structureboundingbox);
            this.a(world, iblockdata1, 3, 2, 0, structureboundingbox);
            this.a(world, iblockdata1, 3, 1, 0, structureboundingbox);
            if (this.a(world, 2, 0, -1, structureboundingbox).getMaterial() == Material.AIR && this.a(world, 2, -1, -1, structureboundingbox).getMaterial() != Material.AIR) {
                this.a(world, iblockdata2, 2, 0, -1, structureboundingbox);
                if (this.a(world, 2, -1, -1, structureboundingbox).getBlock() == Blocks.GRASS_PATH) {
                    this.a(world, Blocks.GRASS.getBlockData(), 2, -1, -1, structureboundingbox);
                }
            }

            this.a(world, structureboundingbox, 1, 1, 1, 3, 3, 3, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            if (this.a) {
                this.a(world, iblockdata4, 0, 5, 0, structureboundingbox);
                this.a(world, iblockdata4, 1, 5, 0, structureboundingbox);
                this.a(world, iblockdata4, 2, 5, 0, structureboundingbox);
                this.a(world, iblockdata4, 3, 5, 0, structureboundingbox);
                this.a(world, iblockdata4, 4, 5, 0, structureboundingbox);
                this.a(world, iblockdata4, 0, 5, 4, structureboundingbox);
                this.a(world, iblockdata4, 1, 5, 4, structureboundingbox);
                this.a(world, iblockdata4, 2, 5, 4, structureboundingbox);
                this.a(world, iblockdata4, 3, 5, 4, structureboundingbox);
                this.a(world, iblockdata4, 4, 5, 4, structureboundingbox);
                this.a(world, iblockdata4, 4, 5, 1, structureboundingbox);
                this.a(world, iblockdata4, 4, 5, 2, structureboundingbox);
                this.a(world, iblockdata4, 4, 5, 3, structureboundingbox);
                this.a(world, iblockdata4, 0, 5, 1, structureboundingbox);
                this.a(world, iblockdata4, 0, 5, 2, structureboundingbox);
                this.a(world, iblockdata4, 0, 5, 3, structureboundingbox);
            }

            if (this.a) {
                IBlockData iblockdata5 = Blocks.LADDER.getBlockData().set(BlockLadder.FACING, EnumDirection.SOUTH);

                this.a(world, iblockdata5, 3, 1, 3, structureboundingbox);
                this.a(world, iblockdata5, 3, 2, 3, structureboundingbox);
                this.a(world, iblockdata5, 3, 3, 3, structureboundingbox);
                this.a(world, iblockdata5, 3, 4, 3, structureboundingbox);
            }

            this.a(world, EnumDirection.NORTH, 2, 3, 1, structureboundingbox);

            for (int i = 0; i < 5; ++i) {
                for (int j = 0; j < 5; ++j) {
                    this.c(world, j, 6, i, structureboundingbox);
                    this.b(world, iblockdata, j, -1, i, structureboundingbox);
                }
            }

            this.a(world, structureboundingbox, 1, 1, 2, 1);
            return true;
        }
    }

    public static class WorldGenVillageRoad extends WorldGenVillagePieces.WorldGenVillageRoadPiece {

        private int a;

        public WorldGenVillageRoad() {}

        public WorldGenVillageRoad(WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece, int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
            super(worldgenvillagepieces_worldgenvillagestartpiece, i);
            this.a(enumdirection);
            this.l = structureboundingbox;
            this.a = Math.max(structureboundingbox.c(), structureboundingbox.e());
        }

        protected void a(NBTTagCompound nbttagcompound) {
            super.a(nbttagcompound);
            nbttagcompound.setInt("Length", this.a);
        }

        protected void a(NBTTagCompound nbttagcompound, DefinedStructureManager definedstructuremanager) {
            super.a(nbttagcompound, definedstructuremanager);
            this.a = nbttagcompound.getInt("Length");
        }

        public void a(StructurePiece structurepiece, List<StructurePiece> list, Random random) {
            boolean flag = false;

            int i;
            StructurePiece structurepiece1;

            for (i = random.nextInt(5); i < this.a - 8; i += 2 + random.nextInt(5)) {
                structurepiece1 = this.a((WorldGenVillagePieces.WorldGenVillageStartPiece) structurepiece, list, random, 0, i);
                if (structurepiece1 != null) {
                    i += Math.max(structurepiece1.l.c(), structurepiece1.l.e());
                    flag = true;
                }
            }

            for (i = random.nextInt(5); i < this.a - 8; i += 2 + random.nextInt(5)) {
                structurepiece1 = this.b((WorldGenVillagePieces.WorldGenVillageStartPiece) structurepiece, list, random, 0, i);
                if (structurepiece1 != null) {
                    i += Math.max(structurepiece1.l.c(), structurepiece1.l.e());
                    flag = true;
                }
            }

            EnumDirection enumdirection = this.f();

            if (flag && random.nextInt(3) > 0 && enumdirection != null) {
                switch (enumdirection) {
                case NORTH:
                default:
                    WorldGenVillagePieces.e((WorldGenVillagePieces.WorldGenVillageStartPiece) structurepiece, list, random, this.l.a - 1, this.l.b, this.l.c, EnumDirection.WEST, this.e());
                    break;

                case SOUTH:
                    WorldGenVillagePieces.e((WorldGenVillagePieces.WorldGenVillageStartPiece) structurepiece, list, random, this.l.a - 1, this.l.b, this.l.f - 2, EnumDirection.WEST, this.e());
                    break;

                case WEST:
                    WorldGenVillagePieces.e((WorldGenVillagePieces.WorldGenVillageStartPiece) structurepiece, list, random, this.l.a, this.l.b, this.l.c - 1, EnumDirection.NORTH, this.e());
                    break;

                case EAST:
                    WorldGenVillagePieces.e((WorldGenVillagePieces.WorldGenVillageStartPiece) structurepiece, list, random, this.l.d - 2, this.l.b, this.l.c - 1, EnumDirection.NORTH, this.e());
                }
            }

            if (flag && random.nextInt(3) > 0 && enumdirection != null) {
                switch (enumdirection) {
                case NORTH:
                default:
                    WorldGenVillagePieces.e((WorldGenVillagePieces.WorldGenVillageStartPiece) structurepiece, list, random, this.l.d + 1, this.l.b, this.l.c, EnumDirection.EAST, this.e());
                    break;

                case SOUTH:
                    WorldGenVillagePieces.e((WorldGenVillagePieces.WorldGenVillageStartPiece) structurepiece, list, random, this.l.d + 1, this.l.b, this.l.f - 2, EnumDirection.EAST, this.e());
                    break;

                case WEST:
                    WorldGenVillagePieces.e((WorldGenVillagePieces.WorldGenVillageStartPiece) structurepiece, list, random, this.l.a, this.l.b, this.l.f + 1, EnumDirection.SOUTH, this.e());
                    break;

                case EAST:
                    WorldGenVillagePieces.e((WorldGenVillagePieces.WorldGenVillageStartPiece) structurepiece, list, random, this.l.d - 2, this.l.b, this.l.f + 1, EnumDirection.SOUTH, this.e());
                }
            }

        }

        public static StructureBoundingBox a(WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece, List<StructurePiece> list, Random random, int i, int j, int k, EnumDirection enumdirection) {
            for (int l = 7 * MathHelper.nextInt(random, 3, 5); l >= 7; l -= 7) {
                StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, 0, 0, 0, 3, 3, l, enumdirection);

                if (StructurePiece.a(list, structureboundingbox) == null) {
                    return structureboundingbox;
                }
            }

            return null;
        }

        public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
            IBlockData iblockdata = this.a(Blocks.GRASS_PATH.getBlockData());
            IBlockData iblockdata1 = this.a(Blocks.PLANKS.getBlockData());
            IBlockData iblockdata2 = this.a(Blocks.GRAVEL.getBlockData());
            IBlockData iblockdata3 = this.a(Blocks.COBBLESTONE.getBlockData());

            for (int i = this.l.a; i <= this.l.d; ++i) {
                for (int j = this.l.c; j <= this.l.f; ++j) {
                    BlockPosition blockposition = new BlockPosition(i, 64, j);

                    if (structureboundingbox.b((BaseBlockPosition) blockposition)) {
                        blockposition = world.q(blockposition).down();
                        if (blockposition.getY() < world.getSeaLevel()) {
                            blockposition = new BlockPosition(blockposition.getX(), world.getSeaLevel() - 1, blockposition.getZ());
                        }

                        while (blockposition.getY() >= world.getSeaLevel() - 1) {
                            IBlockData iblockdata4 = world.getType(blockposition);

                            if (iblockdata4.getBlock() == Blocks.GRASS && world.isEmpty(blockposition.up())) {
                                world.setTypeAndData(blockposition, iblockdata, 2);
                                break;
                            }

                            if (iblockdata4.getMaterial().isLiquid()) {
                                world.setTypeAndData(blockposition, iblockdata1, 2);
                                break;
                            }

                            if (iblockdata4.getBlock() == Blocks.SAND || iblockdata4.getBlock() == Blocks.SANDSTONE || iblockdata4.getBlock() == Blocks.RED_SANDSTONE) {
                                world.setTypeAndData(blockposition, iblockdata2, 2);
                                world.setTypeAndData(blockposition.down(), iblockdata3, 2);
                                break;
                            }

                            blockposition = blockposition.down();
                        }
                    }
                }
            }

            return true;
        }
    }

    public abstract static class WorldGenVillageRoadPiece extends WorldGenVillagePieces.WorldGenVillagePiece {

        public WorldGenVillageRoadPiece() {}

        protected WorldGenVillageRoadPiece(WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece, int i) {
            super(worldgenvillagepieces_worldgenvillagestartpiece, i);
        }
    }

    public static class WorldGenVillageStartPiece extends WorldGenVillagePieces.WorldGenVillageWell {

        public WorldChunkManager a;
        public int b;
        public WorldGenVillagePieces.WorldGenVillagePieceWeight c;
        public List<WorldGenVillagePieces.WorldGenVillagePieceWeight> d;
        public List<StructurePiece> e = Lists.newArrayList();
        public List<StructurePiece> f = Lists.newArrayList();

        public WorldGenVillageStartPiece() {}

        public WorldGenVillageStartPiece(WorldChunkManager worldchunkmanager, int i, Random random, int j, int k, List<WorldGenVillagePieces.WorldGenVillagePieceWeight> list, int l) {
            super((WorldGenVillagePieces.WorldGenVillageStartPiece) null, 0, random, j, k);
            this.a = worldchunkmanager;
            this.d = list;
            this.b = l;
            BiomeBase biomebase = worldchunkmanager.getBiome(new BlockPosition(j, 0, k), Biomes.b);

            if (biomebase instanceof BiomeDesert) {
                this.h = 1;
            } else if (biomebase instanceof BiomeSavanna) {
                this.h = 2;
            } else if (biomebase instanceof BiomeTaiga) {
                this.h = 3;
            }

            this.a(this.h);
            this.i = random.nextInt(50) == 0;
        }
    }

    public static class WorldGenVillageWell extends WorldGenVillagePieces.WorldGenVillagePiece {

        public WorldGenVillageWell() {}

        public WorldGenVillageWell(WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece, int i, Random random, int j, int k) {
            super(worldgenvillagepieces_worldgenvillagestartpiece, i);
            this.a(EnumDirection.EnumDirectionLimit.HORIZONTAL.a(random));
            if (this.f().k() == EnumDirection.EnumAxis.Z) {
                this.l = new StructureBoundingBox(j, 64, k, j + 6 - 1, 78, k + 6 - 1);
            } else {
                this.l = new StructureBoundingBox(j, 64, k, j + 6 - 1, 78, k + 6 - 1);
            }

        }

        public void a(StructurePiece structurepiece, List<StructurePiece> list, Random random) {
            WorldGenVillagePieces.e((WorldGenVillagePieces.WorldGenVillageStartPiece) structurepiece, list, random, this.l.a - 1, this.l.e - 4, this.l.c + 1, EnumDirection.WEST, this.e());
            WorldGenVillagePieces.e((WorldGenVillagePieces.WorldGenVillageStartPiece) structurepiece, list, random, this.l.d + 1, this.l.e - 4, this.l.c + 1, EnumDirection.EAST, this.e());
            WorldGenVillagePieces.e((WorldGenVillagePieces.WorldGenVillageStartPiece) structurepiece, list, random, this.l.a + 1, this.l.e - 4, this.l.c - 1, EnumDirection.NORTH, this.e());
            WorldGenVillagePieces.e((WorldGenVillagePieces.WorldGenVillageStartPiece) structurepiece, list, random, this.l.a + 1, this.l.e - 4, this.l.f + 1, EnumDirection.SOUTH, this.e());
        }

        public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
            if (this.g < 0) {
                this.g = this.b(world, structureboundingbox);
                if (this.g < 0) {
                    return true;
                }

                this.l.a(0, this.g - this.l.e + 3, 0);
            }

            IBlockData iblockdata = this.a(Blocks.COBBLESTONE.getBlockData());
            IBlockData iblockdata1 = this.a(Blocks.FENCE.getBlockData());

            this.a(world, structureboundingbox, 1, 0, 1, 4, 12, 4, iblockdata, Blocks.FLOWING_WATER.getBlockData(), false);
            this.a(world, Blocks.AIR.getBlockData(), 2, 12, 2, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 3, 12, 2, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 2, 12, 3, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 3, 12, 3, structureboundingbox);
            this.a(world, iblockdata1, 1, 13, 1, structureboundingbox);
            this.a(world, iblockdata1, 1, 14, 1, structureboundingbox);
            this.a(world, iblockdata1, 4, 13, 1, structureboundingbox);
            this.a(world, iblockdata1, 4, 14, 1, structureboundingbox);
            this.a(world, iblockdata1, 1, 13, 4, structureboundingbox);
            this.a(world, iblockdata1, 1, 14, 4, structureboundingbox);
            this.a(world, iblockdata1, 4, 13, 4, structureboundingbox);
            this.a(world, iblockdata1, 4, 14, 4, structureboundingbox);
            this.a(world, structureboundingbox, 1, 15, 1, 4, 15, 4, iblockdata, iblockdata, false);

            for (int i = 0; i <= 5; ++i) {
                for (int j = 0; j <= 5; ++j) {
                    if (j == 0 || j == 5 || i == 0 || i == 5) {
                        this.a(world, iblockdata, j, 11, i, structureboundingbox);
                        this.c(world, j, 12, i, structureboundingbox);
                    }
                }
            }

            return true;
        }
    }

    abstract static class WorldGenVillagePiece extends StructurePiece {

        protected int g = -1;
        private int a;
        protected int h;
        protected boolean i;

        public WorldGenVillagePiece() {}

        protected WorldGenVillagePiece(WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece, int i) {
            super(i);
            if (worldgenvillagepieces_worldgenvillagestartpiece != null) {
                this.h = worldgenvillagepieces_worldgenvillagestartpiece.h;
                this.i = worldgenvillagepieces_worldgenvillagestartpiece.i;
            }

        }

        protected void a(NBTTagCompound nbttagcompound) {
            nbttagcompound.setInt("HPos", this.g);
            nbttagcompound.setInt("VCount", this.a);
            nbttagcompound.setByte("Type", (byte) this.h);
            nbttagcompound.setBoolean("Zombie", this.i);
        }

        protected void a(NBTTagCompound nbttagcompound, DefinedStructureManager definedstructuremanager) {
            this.g = nbttagcompound.getInt("HPos");
            this.a = nbttagcompound.getInt("VCount");
            this.h = nbttagcompound.getByte("Type");
            if (nbttagcompound.getBoolean("Desert")) {
                this.h = 1;
            }

            this.i = nbttagcompound.getBoolean("Zombie");
        }

        @Nullable
        protected StructurePiece a(WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece, List<StructurePiece> list, Random random, int i, int j) {
            EnumDirection enumdirection = this.f();

            if (enumdirection != null) {
                switch (enumdirection) {
                case NORTH:
                default:
                    return WorldGenVillagePieces.d(worldgenvillagepieces_worldgenvillagestartpiece, list, random, this.l.a - 1, this.l.b + i, this.l.c + j, EnumDirection.WEST, this.e());

                case SOUTH:
                    return WorldGenVillagePieces.d(worldgenvillagepieces_worldgenvillagestartpiece, list, random, this.l.a - 1, this.l.b + i, this.l.c + j, EnumDirection.WEST, this.e());

                case WEST:
                    return WorldGenVillagePieces.d(worldgenvillagepieces_worldgenvillagestartpiece, list, random, this.l.a + j, this.l.b + i, this.l.c - 1, EnumDirection.NORTH, this.e());

                case EAST:
                    return WorldGenVillagePieces.d(worldgenvillagepieces_worldgenvillagestartpiece, list, random, this.l.a + j, this.l.b + i, this.l.c - 1, EnumDirection.NORTH, this.e());
                }
            } else {
                return null;
            }
        }

        @Nullable
        protected StructurePiece b(WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece, List<StructurePiece> list, Random random, int i, int j) {
            EnumDirection enumdirection = this.f();

            if (enumdirection != null) {
                switch (enumdirection) {
                case NORTH:
                default:
                    return WorldGenVillagePieces.d(worldgenvillagepieces_worldgenvillagestartpiece, list, random, this.l.d + 1, this.l.b + i, this.l.c + j, EnumDirection.EAST, this.e());

                case SOUTH:
                    return WorldGenVillagePieces.d(worldgenvillagepieces_worldgenvillagestartpiece, list, random, this.l.d + 1, this.l.b + i, this.l.c + j, EnumDirection.EAST, this.e());

                case WEST:
                    return WorldGenVillagePieces.d(worldgenvillagepieces_worldgenvillagestartpiece, list, random, this.l.a + j, this.l.b + i, this.l.f + 1, EnumDirection.SOUTH, this.e());

                case EAST:
                    return WorldGenVillagePieces.d(worldgenvillagepieces_worldgenvillagestartpiece, list, random, this.l.a + j, this.l.b + i, this.l.f + 1, EnumDirection.SOUTH, this.e());
                }
            } else {
                return null;
            }
        }

        protected int b(World world, StructureBoundingBox structureboundingbox) {
            int i = 0;
            int j = 0;
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            for (int k = this.l.c; k <= this.l.f; ++k) {
                for (int l = this.l.a; l <= this.l.d; ++l) {
                    blockposition_mutableblockposition.c(l, 64, k);
                    if (structureboundingbox.b((BaseBlockPosition) blockposition_mutableblockposition)) {
                        i += Math.max(world.q(blockposition_mutableblockposition).getY(), world.worldProvider.getSeaLevel() - 1);
                        ++j;
                    }
                }
            }

            if (j == 0) {
                return -1;
            } else {
                return i / j;
            }
        }

        protected static boolean a(StructureBoundingBox structureboundingbox) {
            return structureboundingbox != null && structureboundingbox.b > 10;
        }

        protected void a(World world, StructureBoundingBox structureboundingbox, int i, int j, int k, int l) {
            if (this.a < l) {
                for (int i1 = this.a; i1 < l; ++i1) {
                    int j1 = this.a(i + i1, k);
                    int k1 = this.d(j);
                    int l1 = this.b(i + i1, k);

                    if (!structureboundingbox.b((BaseBlockPosition) (new BlockPosition(j1, k1, l1)))) {
                        break;
                    }

                    ++this.a;
                    if (this.i) {
                        EntityZombieVillager entityzombievillager = new EntityZombieVillager(world);

                        entityzombievillager.setPositionRotation((double) j1 + 0.5D, (double) k1, (double) l1 + 0.5D, 0.0F, 0.0F);
                        entityzombievillager.prepare(world.D(new BlockPosition(entityzombievillager)), (GroupDataEntity) null);
                        entityzombievillager.setProfession(this.c(i1, 0));
                        entityzombievillager.cW();
                        world.addEntity(entityzombievillager);
                    } else {
                        EntityVillager entityvillager = new EntityVillager(world);

                        entityvillager.setPositionRotation((double) j1 + 0.5D, (double) k1, (double) l1 + 0.5D, 0.0F, 0.0F);
                        entityvillager.setProfession(this.c(i1, world.random.nextInt(6)));
                        entityvillager.a(world.D(new BlockPosition(entityvillager)), (GroupDataEntity) null, false);
                        world.addEntity(entityvillager);
                    }
                }

            }
        }

        protected int c(int i, int j) {
            return j;
        }

        protected IBlockData a(IBlockData iblockdata) {
            if (this.h == 1) {
                if (iblockdata.getBlock() == Blocks.LOG || iblockdata.getBlock() == Blocks.LOG2) {
                    return Blocks.SANDSTONE.getBlockData();
                }

                if (iblockdata.getBlock() == Blocks.COBBLESTONE) {
                    return Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.DEFAULT.a());
                }

                if (iblockdata.getBlock() == Blocks.PLANKS) {
                    return Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a());
                }

                if (iblockdata.getBlock() == Blocks.OAK_STAIRS) {
                    return Blocks.SANDSTONE_STAIRS.getBlockData().set(BlockStairs.FACING, iblockdata.get(BlockStairs.FACING));
                }

                if (iblockdata.getBlock() == Blocks.STONE_STAIRS) {
                    return Blocks.SANDSTONE_STAIRS.getBlockData().set(BlockStairs.FACING, iblockdata.get(BlockStairs.FACING));
                }

                if (iblockdata.getBlock() == Blocks.GRAVEL) {
                    return Blocks.SANDSTONE.getBlockData();
                }
            } else if (this.h == 3) {
                if (iblockdata.getBlock() == Blocks.LOG || iblockdata.getBlock() == Blocks.LOG2) {
                    return Blocks.LOG.getBlockData().set(BlockLog1.VARIANT, BlockWood.EnumLogVariant.SPRUCE).set(BlockLogAbstract.AXIS, iblockdata.get(BlockLogAbstract.AXIS));
                }

                if (iblockdata.getBlock() == Blocks.PLANKS) {
                    return Blocks.PLANKS.getBlockData().set(BlockWood.VARIANT, BlockWood.EnumLogVariant.SPRUCE);
                }

                if (iblockdata.getBlock() == Blocks.OAK_STAIRS) {
                    return Blocks.SPRUCE_STAIRS.getBlockData().set(BlockStairs.FACING, iblockdata.get(BlockStairs.FACING));
                }

                if (iblockdata.getBlock() == Blocks.FENCE) {
                    return Blocks.SPRUCE_FENCE.getBlockData();
                }
            } else if (this.h == 2) {
                if (iblockdata.getBlock() == Blocks.LOG || iblockdata.getBlock() == Blocks.LOG2) {
                    return Blocks.LOG2.getBlockData().set(BlockLog2.VARIANT, BlockWood.EnumLogVariant.ACACIA).set(BlockLogAbstract.AXIS, iblockdata.get(BlockLogAbstract.AXIS));
                }

                if (iblockdata.getBlock() == Blocks.PLANKS) {
                    return Blocks.PLANKS.getBlockData().set(BlockWood.VARIANT, BlockWood.EnumLogVariant.ACACIA);
                }

                if (iblockdata.getBlock() == Blocks.OAK_STAIRS) {
                    return Blocks.ACACIA_STAIRS.getBlockData().set(BlockStairs.FACING, iblockdata.get(BlockStairs.FACING));
                }

                if (iblockdata.getBlock() == Blocks.COBBLESTONE) {
                    return Blocks.LOG2.getBlockData().set(BlockLog2.VARIANT, BlockWood.EnumLogVariant.ACACIA).set(BlockLogAbstract.AXIS, BlockLogAbstract.EnumLogRotation.Y);
                }

                if (iblockdata.getBlock() == Blocks.FENCE) {
                    return Blocks.ACACIA_FENCE.getBlockData();
                }
            }

            return iblockdata;
        }

        protected BlockDoor i() {
            switch (this.h) {
            case 2:
                return Blocks.ACACIA_DOOR;

            case 3:
                return Blocks.SPRUCE_DOOR;

            default:
                return Blocks.WOODEN_DOOR;
            }
        }

        protected void a(World world, StructureBoundingBox structureboundingbox, Random random, int i, int j, int k, EnumDirection enumdirection) {
            if (!this.i) {
                this.a(world, structureboundingbox, random, i, j, k, EnumDirection.NORTH, this.i());
            }

        }

        protected void a(World world, EnumDirection enumdirection, int i, int j, int k, StructureBoundingBox structureboundingbox) {
            if (!this.i) {
                this.a(world, Blocks.TORCH.getBlockData().set(BlockTorch.FACING, enumdirection), i, j, k, structureboundingbox);
            }

        }

        protected void b(World world, IBlockData iblockdata, int i, int j, int k, StructureBoundingBox structureboundingbox) {
            IBlockData iblockdata1 = this.a(iblockdata);

            super.b(world, iblockdata1, i, j, k, structureboundingbox);
        }

        protected void a(int i) {
            this.h = i;
        }
    }

    public static class WorldGenVillagePieceWeight {

        public Class<? extends WorldGenVillagePieces.WorldGenVillagePiece> a;
        public final int b;
        public int c;
        public int d;

        public WorldGenVillagePieceWeight(Class<? extends WorldGenVillagePieces.WorldGenVillagePiece> oclass, int i, int j) {
            this.a = oclass;
            this.b = i;
            this.d = j;
        }

        public boolean a(int i) {
            return this.d == 0 || this.c < this.d;
        }

        public boolean a() {
            return this.d == 0 || this.c < this.d;
        }
    }
}
