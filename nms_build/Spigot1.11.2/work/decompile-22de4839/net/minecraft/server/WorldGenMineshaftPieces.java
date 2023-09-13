package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public class WorldGenMineshaftPieces {

    public static void a() {
        WorldGenFactory.a(WorldGenMineshaftPieces.WorldGenMineshaftCorridor.class, "MSCorridor");
        WorldGenFactory.a(WorldGenMineshaftPieces.WorldGenMineshaftCross.class, "MSCrossing");
        WorldGenFactory.a(WorldGenMineshaftPieces.WorldGenMineshaftRoom.class, "MSRoom");
        WorldGenFactory.a(WorldGenMineshaftPieces.WorldGenMineshaftStairs.class, "MSStairs");
    }

    private static WorldGenMineshaftPieces.c a(List<StructurePiece> list, Random random, int i, int j, int k, @Nullable EnumDirection enumdirection, int l, WorldGenMineshaft.Type worldgenmineshaft_type) {
        int i1 = random.nextInt(100);
        StructureBoundingBox structureboundingbox;

        if (i1 >= 80) {
            structureboundingbox = WorldGenMineshaftPieces.WorldGenMineshaftCross.a(list, random, i, j, k, enumdirection);
            if (structureboundingbox != null) {
                return new WorldGenMineshaftPieces.WorldGenMineshaftCross(l, random, structureboundingbox, enumdirection, worldgenmineshaft_type);
            }
        } else if (i1 >= 70) {
            structureboundingbox = WorldGenMineshaftPieces.WorldGenMineshaftStairs.a(list, random, i, j, k, enumdirection);
            if (structureboundingbox != null) {
                return new WorldGenMineshaftPieces.WorldGenMineshaftStairs(l, random, structureboundingbox, enumdirection, worldgenmineshaft_type);
            }
        } else {
            structureboundingbox = WorldGenMineshaftPieces.WorldGenMineshaftCorridor.a(list, random, i, j, k, enumdirection);
            if (structureboundingbox != null) {
                return new WorldGenMineshaftPieces.WorldGenMineshaftCorridor(l, random, structureboundingbox, enumdirection, worldgenmineshaft_type);
            }
        }

        return null;
    }

    private static WorldGenMineshaftPieces.c b(StructurePiece structurepiece, List<StructurePiece> list, Random random, int i, int j, int k, EnumDirection enumdirection, int l) {
        if (l > 8) {
            return null;
        } else if (Math.abs(i - structurepiece.d().a) <= 80 && Math.abs(k - structurepiece.d().c) <= 80) {
            WorldGenMineshaft.Type worldgenmineshaft_type = ((WorldGenMineshaftPieces.c) structurepiece).a;
            WorldGenMineshaftPieces.c worldgenmineshaftpieces_c = a(list, random, i, j, k, enumdirection, l + 1, worldgenmineshaft_type);

            if (worldgenmineshaftpieces_c != null) {
                list.add(worldgenmineshaftpieces_c);
                worldgenmineshaftpieces_c.a(structurepiece, list, random);
            }

            return worldgenmineshaftpieces_c;
        } else {
            return null;
        }
    }

    public static class WorldGenMineshaftStairs extends WorldGenMineshaftPieces.c {

        public WorldGenMineshaftStairs() {}

        public WorldGenMineshaftStairs(int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection, WorldGenMineshaft.Type worldgenmineshaft_type) {
            super(i, worldgenmineshaft_type);
            this.a(enumdirection);
            this.l = structureboundingbox;
        }

        public static StructureBoundingBox a(List<StructurePiece> list, Random random, int i, int j, int k, EnumDirection enumdirection) {
            StructureBoundingBox structureboundingbox = new StructureBoundingBox(i, j - 5, k, i, j + 2, k);

            switch (enumdirection) {
            case NORTH:
            default:
                structureboundingbox.d = i + 2;
                structureboundingbox.c = k - 8;
                break;

            case SOUTH:
                structureboundingbox.d = i + 2;
                structureboundingbox.f = k + 8;
                break;

            case WEST:
                structureboundingbox.a = i - 8;
                structureboundingbox.f = k + 2;
                break;

            case EAST:
                structureboundingbox.d = i + 8;
                structureboundingbox.f = k + 2;
            }

            return StructurePiece.a(list, structureboundingbox) != null ? null : structureboundingbox;
        }

        public void a(StructurePiece structurepiece, List<StructurePiece> list, Random random) {
            int i = this.e();
            EnumDirection enumdirection = this.f();

            if (enumdirection != null) {
                switch (enumdirection) {
                case NORTH:
                default:
                    WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.a, this.l.b, this.l.c - 1, EnumDirection.NORTH, i);
                    break;

                case SOUTH:
                    WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.a, this.l.b, this.l.f + 1, EnumDirection.SOUTH, i);
                    break;

                case WEST:
                    WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.a - 1, this.l.b, this.l.c, EnumDirection.WEST, i);
                    break;

                case EAST:
                    WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.d + 1, this.l.b, this.l.c, EnumDirection.EAST, i);
                }
            }

        }

        public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
            if (this.a(world, structureboundingbox)) {
                return false;
            } else {
                this.a(world, structureboundingbox, 0, 5, 0, 2, 7, 1, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
                this.a(world, structureboundingbox, 0, 0, 7, 2, 2, 8, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);

                for (int i = 0; i < 5; ++i) {
                    this.a(world, structureboundingbox, 0, 5 - i - (i < 4 ? 1 : 0), 2 + i, 2, 7 - i, 2 + i, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
                }

                return true;
            }
        }
    }

    public static class WorldGenMineshaftCross extends WorldGenMineshaftPieces.c {

        private EnumDirection b;
        private boolean c;

        public WorldGenMineshaftCross() {}

        protected void a(NBTTagCompound nbttagcompound) {
            super.a(nbttagcompound);
            nbttagcompound.setBoolean("tf", this.c);
            nbttagcompound.setInt("D", this.b.get2DRotationValue());
        }

        protected void a(NBTTagCompound nbttagcompound, DefinedStructureManager definedstructuremanager) {
            super.a(nbttagcompound, definedstructuremanager);
            this.c = nbttagcompound.getBoolean("tf");
            this.b = EnumDirection.fromType2(nbttagcompound.getInt("D"));
        }

        public WorldGenMineshaftCross(int i, Random random, StructureBoundingBox structureboundingbox, @Nullable EnumDirection enumdirection, WorldGenMineshaft.Type worldgenmineshaft_type) {
            super(i, worldgenmineshaft_type);
            this.b = enumdirection;
            this.l = structureboundingbox;
            this.c = structureboundingbox.d() > 3;
        }

        public static StructureBoundingBox a(List<StructurePiece> list, Random random, int i, int j, int k, EnumDirection enumdirection) {
            StructureBoundingBox structureboundingbox = new StructureBoundingBox(i, j, k, i, j + 2, k);

            if (random.nextInt(4) == 0) {
                structureboundingbox.e += 4;
            }

            switch (enumdirection) {
            case NORTH:
            default:
                structureboundingbox.a = i - 1;
                structureboundingbox.d = i + 3;
                structureboundingbox.c = k - 4;
                break;

            case SOUTH:
                structureboundingbox.a = i - 1;
                structureboundingbox.d = i + 3;
                structureboundingbox.f = k + 3 + 1;
                break;

            case WEST:
                structureboundingbox.a = i - 4;
                structureboundingbox.c = k - 1;
                structureboundingbox.f = k + 3;
                break;

            case EAST:
                structureboundingbox.d = i + 3 + 1;
                structureboundingbox.c = k - 1;
                structureboundingbox.f = k + 3;
            }

            return StructurePiece.a(list, structureboundingbox) != null ? null : structureboundingbox;
        }

        public void a(StructurePiece structurepiece, List<StructurePiece> list, Random random) {
            int i = this.e();

            switch (this.b) {
            case NORTH:
            default:
                WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.a + 1, this.l.b, this.l.c - 1, EnumDirection.NORTH, i);
                WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.a - 1, this.l.b, this.l.c + 1, EnumDirection.WEST, i);
                WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.d + 1, this.l.b, this.l.c + 1, EnumDirection.EAST, i);
                break;

            case SOUTH:
                WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.a + 1, this.l.b, this.l.f + 1, EnumDirection.SOUTH, i);
                WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.a - 1, this.l.b, this.l.c + 1, EnumDirection.WEST, i);
                WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.d + 1, this.l.b, this.l.c + 1, EnumDirection.EAST, i);
                break;

            case WEST:
                WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.a + 1, this.l.b, this.l.c - 1, EnumDirection.NORTH, i);
                WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.a + 1, this.l.b, this.l.f + 1, EnumDirection.SOUTH, i);
                WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.a - 1, this.l.b, this.l.c + 1, EnumDirection.WEST, i);
                break;

            case EAST:
                WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.a + 1, this.l.b, this.l.c - 1, EnumDirection.NORTH, i);
                WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.a + 1, this.l.b, this.l.f + 1, EnumDirection.SOUTH, i);
                WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.d + 1, this.l.b, this.l.c + 1, EnumDirection.EAST, i);
            }

            if (this.c) {
                if (random.nextBoolean()) {
                    WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.a + 1, this.l.b + 3 + 1, this.l.c - 1, EnumDirection.NORTH, i);
                }

                if (random.nextBoolean()) {
                    WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.a - 1, this.l.b + 3 + 1, this.l.c + 1, EnumDirection.WEST, i);
                }

                if (random.nextBoolean()) {
                    WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.d + 1, this.l.b + 3 + 1, this.l.c + 1, EnumDirection.EAST, i);
                }

                if (random.nextBoolean()) {
                    WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.a + 1, this.l.b + 3 + 1, this.l.f + 1, EnumDirection.SOUTH, i);
                }
            }

        }

        public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
            if (this.a(world, structureboundingbox)) {
                return false;
            } else {
                IBlockData iblockdata = this.G_();

                if (this.c) {
                    this.a(world, structureboundingbox, this.l.a + 1, this.l.b, this.l.c, this.l.d - 1, this.l.b + 3 - 1, this.l.f, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
                    this.a(world, structureboundingbox, this.l.a, this.l.b, this.l.c + 1, this.l.d, this.l.b + 3 - 1, this.l.f - 1, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
                    this.a(world, structureboundingbox, this.l.a + 1, this.l.e - 2, this.l.c, this.l.d - 1, this.l.e, this.l.f, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
                    this.a(world, structureboundingbox, this.l.a, this.l.e - 2, this.l.c + 1, this.l.d, this.l.e, this.l.f - 1, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
                    this.a(world, structureboundingbox, this.l.a + 1, this.l.b + 3, this.l.c + 1, this.l.d - 1, this.l.b + 3, this.l.f - 1, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
                } else {
                    this.a(world, structureboundingbox, this.l.a + 1, this.l.b, this.l.c, this.l.d - 1, this.l.e, this.l.f, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
                    this.a(world, structureboundingbox, this.l.a, this.l.b, this.l.c + 1, this.l.d, this.l.e, this.l.f - 1, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
                }

                this.b(world, structureboundingbox, this.l.a + 1, this.l.b, this.l.c + 1, this.l.e);
                this.b(world, structureboundingbox, this.l.a + 1, this.l.b, this.l.f - 1, this.l.e);
                this.b(world, structureboundingbox, this.l.d - 1, this.l.b, this.l.c + 1, this.l.e);
                this.b(world, structureboundingbox, this.l.d - 1, this.l.b, this.l.f - 1, this.l.e);

                for (int i = this.l.a; i <= this.l.d; ++i) {
                    for (int j = this.l.c; j <= this.l.f; ++j) {
                        if (this.a(world, i, this.l.b - 1, j, structureboundingbox).getMaterial() == Material.AIR && this.b(world, i, this.l.b - 1, j, structureboundingbox) < 8) {
                            this.a(world, iblockdata, i, this.l.b - 1, j, structureboundingbox);
                        }
                    }
                }

                return true;
            }
        }

        private void b(World world, StructureBoundingBox structureboundingbox, int i, int j, int k, int l) {
            if (this.a(world, i, l + 1, k, structureboundingbox).getMaterial() != Material.AIR) {
                this.a(world, structureboundingbox, i, j, k, i, l, k, this.G_(), Blocks.AIR.getBlockData(), false);
            }

        }
    }

    public static class WorldGenMineshaftCorridor extends WorldGenMineshaftPieces.c {

        private boolean b;
        private boolean c;
        private boolean d;
        private int e;

        public WorldGenMineshaftCorridor() {}

        protected void a(NBTTagCompound nbttagcompound) {
            super.a(nbttagcompound);
            nbttagcompound.setBoolean("hr", this.b);
            nbttagcompound.setBoolean("sc", this.c);
            nbttagcompound.setBoolean("hps", this.d);
            nbttagcompound.setInt("Num", this.e);
        }

        protected void a(NBTTagCompound nbttagcompound, DefinedStructureManager definedstructuremanager) {
            super.a(nbttagcompound, definedstructuremanager);
            this.b = nbttagcompound.getBoolean("hr");
            this.c = nbttagcompound.getBoolean("sc");
            this.d = nbttagcompound.getBoolean("hps");
            this.e = nbttagcompound.getInt("Num");
        }

        public WorldGenMineshaftCorridor(int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection, WorldGenMineshaft.Type worldgenmineshaft_type) {
            super(i, worldgenmineshaft_type);
            this.a(enumdirection);
            this.l = structureboundingbox;
            this.b = random.nextInt(3) == 0;
            this.c = !this.b && random.nextInt(23) == 0;
            if (this.f().k() == EnumDirection.EnumAxis.Z) {
                this.e = structureboundingbox.e() / 5;
            } else {
                this.e = structureboundingbox.c() / 5;
            }

        }

        public static StructureBoundingBox a(List<StructurePiece> list, Random random, int i, int j, int k, EnumDirection enumdirection) {
            StructureBoundingBox structureboundingbox = new StructureBoundingBox(i, j, k, i, j + 2, k);

            int l;

            for (l = random.nextInt(3) + 2; l > 0; --l) {
                int i1 = l * 5;

                switch (enumdirection) {
                case NORTH:
                default:
                    structureboundingbox.d = i + 2;
                    structureboundingbox.c = k - (i1 - 1);
                    break;

                case SOUTH:
                    structureboundingbox.d = i + 2;
                    structureboundingbox.f = k + (i1 - 1);
                    break;

                case WEST:
                    structureboundingbox.a = i - (i1 - 1);
                    structureboundingbox.f = k + 2;
                    break;

                case EAST:
                    structureboundingbox.d = i + (i1 - 1);
                    structureboundingbox.f = k + 2;
                }

                if (StructurePiece.a(list, structureboundingbox) == null) {
                    break;
                }
            }

            return l > 0 ? structureboundingbox : null;
        }

        public void a(StructurePiece structurepiece, List<StructurePiece> list, Random random) {
            int i = this.e();
            int j = random.nextInt(4);
            EnumDirection enumdirection = this.f();

            if (enumdirection != null) {
                switch (enumdirection) {
                case NORTH:
                default:
                    if (j <= 1) {
                        WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.a, this.l.b - 1 + random.nextInt(3), this.l.c - 1, enumdirection, i);
                    } else if (j == 2) {
                        WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.a - 1, this.l.b - 1 + random.nextInt(3), this.l.c, EnumDirection.WEST, i);
                    } else {
                        WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.d + 1, this.l.b - 1 + random.nextInt(3), this.l.c, EnumDirection.EAST, i);
                    }
                    break;

                case SOUTH:
                    if (j <= 1) {
                        WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.a, this.l.b - 1 + random.nextInt(3), this.l.f + 1, enumdirection, i);
                    } else if (j == 2) {
                        WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.a - 1, this.l.b - 1 + random.nextInt(3), this.l.f - 3, EnumDirection.WEST, i);
                    } else {
                        WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.d + 1, this.l.b - 1 + random.nextInt(3), this.l.f - 3, EnumDirection.EAST, i);
                    }
                    break;

                case WEST:
                    if (j <= 1) {
                        WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.a - 1, this.l.b - 1 + random.nextInt(3), this.l.c, enumdirection, i);
                    } else if (j == 2) {
                        WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.a, this.l.b - 1 + random.nextInt(3), this.l.c - 1, EnumDirection.NORTH, i);
                    } else {
                        WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.a, this.l.b - 1 + random.nextInt(3), this.l.f + 1, EnumDirection.SOUTH, i);
                    }
                    break;

                case EAST:
                    if (j <= 1) {
                        WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.d + 1, this.l.b - 1 + random.nextInt(3), this.l.c, enumdirection, i);
                    } else if (j == 2) {
                        WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.d - 3, this.l.b - 1 + random.nextInt(3), this.l.c - 1, EnumDirection.NORTH, i);
                    } else {
                        WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.d - 3, this.l.b - 1 + random.nextInt(3), this.l.f + 1, EnumDirection.SOUTH, i);
                    }
                }
            }

            if (i < 8) {
                int k;
                int l;

                if (enumdirection != EnumDirection.NORTH && enumdirection != EnumDirection.SOUTH) {
                    for (k = this.l.a + 3; k + 3 <= this.l.d; k += 5) {
                        l = random.nextInt(5);
                        if (l == 0) {
                            WorldGenMineshaftPieces.b(structurepiece, list, random, k, this.l.b, this.l.c - 1, EnumDirection.NORTH, i + 1);
                        } else if (l == 1) {
                            WorldGenMineshaftPieces.b(structurepiece, list, random, k, this.l.b, this.l.f + 1, EnumDirection.SOUTH, i + 1);
                        }
                    }
                } else {
                    for (k = this.l.c + 3; k + 3 <= this.l.f; k += 5) {
                        l = random.nextInt(5);
                        if (l == 0) {
                            WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.a - 1, this.l.b, k, EnumDirection.WEST, i + 1);
                        } else if (l == 1) {
                            WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.d + 1, this.l.b, k, EnumDirection.EAST, i + 1);
                        }
                    }
                }
            }

        }

        protected boolean a(World world, StructureBoundingBox structureboundingbox, Random random, int i, int j, int k, MinecraftKey minecraftkey) {
            BlockPosition blockposition = new BlockPosition(this.a(i, k), this.d(j), this.b(i, k));

            if (structureboundingbox.b((BaseBlockPosition) blockposition) && world.getType(blockposition).getMaterial() == Material.AIR && world.getType(blockposition.down()).getMaterial() != Material.AIR) {
                IBlockData iblockdata = Blocks.RAIL.getBlockData().set(BlockMinecartTrack.SHAPE, random.nextBoolean() ? BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH : BlockMinecartTrackAbstract.EnumTrackPosition.EAST_WEST);

                this.a(world, iblockdata, i, j, k, structureboundingbox);
                EntityMinecartChest entityminecartchest = new EntityMinecartChest(world, (double) ((float) blockposition.getX() + 0.5F), (double) ((float) blockposition.getY() + 0.5F), (double) ((float) blockposition.getZ() + 0.5F));

                entityminecartchest.a(minecraftkey, random.nextLong());
                world.addEntity(entityminecartchest);
                return true;
            } else {
                return false;
            }
        }

        public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
            if (this.a(world, structureboundingbox)) {
                return false;
            } else {
                boolean flag = false;
                boolean flag1 = true;
                boolean flag2 = false;
                boolean flag3 = true;
                int i = this.e * 5 - 1;
                IBlockData iblockdata = this.G_();

                this.a(world, structureboundingbox, 0, 0, 0, 2, 1, i, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
                this.a(world, structureboundingbox, random, 0.8F, 0, 2, 0, 2, 2, i, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false, 0);
                if (this.c) {
                    this.a(world, structureboundingbox, random, 0.6F, 0, 0, 0, 2, 1, i, Blocks.WEB.getBlockData(), Blocks.AIR.getBlockData(), false, 8);
                }

                int j;
                int k;

                for (j = 0; j < this.e; ++j) {
                    k = 2 + j * 5;
                    this.a(world, structureboundingbox, 0, 0, k, 2, 2, random);
                    this.a(world, structureboundingbox, random, 0.1F, 0, 2, k - 1);
                    this.a(world, structureboundingbox, random, 0.1F, 2, 2, k - 1);
                    this.a(world, structureboundingbox, random, 0.1F, 0, 2, k + 1);
                    this.a(world, structureboundingbox, random, 0.1F, 2, 2, k + 1);
                    this.a(world, structureboundingbox, random, 0.05F, 0, 2, k - 2);
                    this.a(world, structureboundingbox, random, 0.05F, 2, 2, k - 2);
                    this.a(world, structureboundingbox, random, 0.05F, 0, 2, k + 2);
                    this.a(world, structureboundingbox, random, 0.05F, 2, 2, k + 2);
                    if (random.nextInt(100) == 0) {
                        this.a(world, structureboundingbox, random, 2, 0, k - 1, LootTables.f);
                    }

                    if (random.nextInt(100) == 0) {
                        this.a(world, structureboundingbox, random, 0, 0, k + 1, LootTables.f);
                    }

                    if (this.c && !this.d) {
                        int l = this.d(0);
                        int i1 = k - 1 + random.nextInt(3);
                        int j1 = this.a(1, i1);
                        int k1 = this.b(1, i1);
                        BlockPosition blockposition = new BlockPosition(j1, l, k1);

                        if (structureboundingbox.b((BaseBlockPosition) blockposition) && this.b(world, 1, 0, i1, structureboundingbox) < 8) {
                            this.d = true;
                            world.setTypeAndData(blockposition, Blocks.MOB_SPAWNER.getBlockData(), 2);
                            TileEntity tileentity = world.getTileEntity(blockposition);

                            if (tileentity instanceof TileEntityMobSpawner) {
                                ((TileEntityMobSpawner) tileentity).getSpawner().setMobName(EntityTypes.getName(EntityCaveSpider.class));
                            }
                        }
                    }
                }

                for (j = 0; j <= 2; ++j) {
                    for (k = 0; k <= i; ++k) {
                        boolean flag4 = true;
                        IBlockData iblockdata1 = this.a(world, j, -1, k, structureboundingbox);

                        if (iblockdata1.getMaterial() == Material.AIR && this.b(world, j, -1, k, structureboundingbox) < 8) {
                            boolean flag5 = true;

                            this.a(world, iblockdata, j, -1, k, structureboundingbox);
                        }
                    }
                }

                if (this.b) {
                    IBlockData iblockdata2 = Blocks.RAIL.getBlockData().set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH);

                    for (k = 0; k <= i; ++k) {
                        IBlockData iblockdata3 = this.a(world, 1, -1, k, structureboundingbox);

                        if (iblockdata3.getMaterial() != Material.AIR && iblockdata3.b()) {
                            float f = this.b(world, 1, 0, k, structureboundingbox) > 8 ? 0.9F : 0.7F;

                            this.a(world, structureboundingbox, random, f, 1, 0, k, iblockdata2);
                        }
                    }
                }

                return true;
            }
        }

        private void a(World world, StructureBoundingBox structureboundingbox, int i, int j, int k, int l, int i1, Random random) {
            if (this.a(world, structureboundingbox, i, i1, l, k)) {
                IBlockData iblockdata = this.G_();
                IBlockData iblockdata1 = this.b();
                IBlockData iblockdata2 = Blocks.AIR.getBlockData();

                this.a(world, structureboundingbox, i, j, k, i, l - 1, k, iblockdata1, iblockdata2, false);
                this.a(world, structureboundingbox, i1, j, k, i1, l - 1, k, iblockdata1, iblockdata2, false);
                if (random.nextInt(4) == 0) {
                    this.a(world, structureboundingbox, i, l, k, i, l, k, iblockdata, iblockdata2, false);
                    this.a(world, structureboundingbox, i1, l, k, i1, l, k, iblockdata, iblockdata2, false);
                } else {
                    this.a(world, structureboundingbox, i, l, k, i1, l, k, iblockdata, iblockdata2, false);
                    this.a(world, structureboundingbox, random, 0.05F, i + 1, l, k - 1, Blocks.TORCH.getBlockData().set(BlockTorch.FACING, EnumDirection.NORTH));
                    this.a(world, structureboundingbox, random, 0.05F, i + 1, l, k + 1, Blocks.TORCH.getBlockData().set(BlockTorch.FACING, EnumDirection.SOUTH));
                }

            }
        }

        private void a(World world, StructureBoundingBox structureboundingbox, Random random, float f, int i, int j, int k) {
            if (this.b(world, i, j, k, structureboundingbox) < 8) {
                this.a(world, structureboundingbox, random, f, i, j, k, Blocks.WEB.getBlockData());
            }

        }
    }

    public static class WorldGenMineshaftRoom extends WorldGenMineshaftPieces.c {

        private final List<StructureBoundingBox> b = Lists.newLinkedList();

        public WorldGenMineshaftRoom() {}

        public WorldGenMineshaftRoom(int i, Random random, int j, int k, WorldGenMineshaft.Type worldgenmineshaft_type) {
            super(i, worldgenmineshaft_type);
            this.a = worldgenmineshaft_type;
            this.l = new StructureBoundingBox(j, 50, k, j + 7 + random.nextInt(6), 54 + random.nextInt(6), k + 7 + random.nextInt(6));
        }

        public void a(StructurePiece structurepiece, List<StructurePiece> list, Random random) {
            int i = this.e();
            int j = this.l.d() - 3 - 1;

            if (j <= 0) {
                j = 1;
            }

            int k;
            WorldGenMineshaftPieces.c worldgenmineshaftpieces_c;
            StructureBoundingBox structureboundingbox;

            for (k = 0; k < this.l.c(); k += 4) {
                k += random.nextInt(this.l.c());
                if (k + 3 > this.l.c()) {
                    break;
                }

                worldgenmineshaftpieces_c = WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.a + k, this.l.b + random.nextInt(j) + 1, this.l.c - 1, EnumDirection.NORTH, i);
                if (worldgenmineshaftpieces_c != null) {
                    structureboundingbox = worldgenmineshaftpieces_c.d();
                    this.b.add(new StructureBoundingBox(structureboundingbox.a, structureboundingbox.b, this.l.c, structureboundingbox.d, structureboundingbox.e, this.l.c + 1));
                }
            }

            for (k = 0; k < this.l.c(); k += 4) {
                k += random.nextInt(this.l.c());
                if (k + 3 > this.l.c()) {
                    break;
                }

                worldgenmineshaftpieces_c = WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.a + k, this.l.b + random.nextInt(j) + 1, this.l.f + 1, EnumDirection.SOUTH, i);
                if (worldgenmineshaftpieces_c != null) {
                    structureboundingbox = worldgenmineshaftpieces_c.d();
                    this.b.add(new StructureBoundingBox(structureboundingbox.a, structureboundingbox.b, this.l.f - 1, structureboundingbox.d, structureboundingbox.e, this.l.f));
                }
            }

            for (k = 0; k < this.l.e(); k += 4) {
                k += random.nextInt(this.l.e());
                if (k + 3 > this.l.e()) {
                    break;
                }

                worldgenmineshaftpieces_c = WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.a - 1, this.l.b + random.nextInt(j) + 1, this.l.c + k, EnumDirection.WEST, i);
                if (worldgenmineshaftpieces_c != null) {
                    structureboundingbox = worldgenmineshaftpieces_c.d();
                    this.b.add(new StructureBoundingBox(this.l.a, structureboundingbox.b, structureboundingbox.c, this.l.a + 1, structureboundingbox.e, structureboundingbox.f));
                }
            }

            for (k = 0; k < this.l.e(); k += 4) {
                k += random.nextInt(this.l.e());
                if (k + 3 > this.l.e()) {
                    break;
                }

                worldgenmineshaftpieces_c = WorldGenMineshaftPieces.b(structurepiece, list, random, this.l.d + 1, this.l.b + random.nextInt(j) + 1, this.l.c + k, EnumDirection.EAST, i);
                if (worldgenmineshaftpieces_c != null) {
                    structureboundingbox = worldgenmineshaftpieces_c.d();
                    this.b.add(new StructureBoundingBox(this.l.d - 1, structureboundingbox.b, structureboundingbox.c, this.l.d, structureboundingbox.e, structureboundingbox.f));
                }
            }

        }

        public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
            if (this.a(world, structureboundingbox)) {
                return false;
            } else {
                this.a(world, structureboundingbox, this.l.a, this.l.b, this.l.c, this.l.d, this.l.b, this.l.f, Blocks.DIRT.getBlockData(), Blocks.AIR.getBlockData(), true);
                this.a(world, structureboundingbox, this.l.a, this.l.b + 1, this.l.c, this.l.d, Math.min(this.l.b + 3, this.l.e), this.l.f, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
                Iterator iterator = this.b.iterator();

                while (iterator.hasNext()) {
                    StructureBoundingBox structureboundingbox1 = (StructureBoundingBox) iterator.next();

                    this.a(world, structureboundingbox, structureboundingbox1.a, structureboundingbox1.e - 2, structureboundingbox1.c, structureboundingbox1.d, structureboundingbox1.e, structureboundingbox1.f, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
                }

                this.a(world, structureboundingbox, this.l.a, this.l.b + 4, this.l.c, this.l.d, this.l.e, this.l.f, Blocks.AIR.getBlockData(), false);
                return true;
            }
        }

        public void a(int i, int j, int k) {
            super.a(i, j, k);
            Iterator iterator = this.b.iterator();

            while (iterator.hasNext()) {
                StructureBoundingBox structureboundingbox = (StructureBoundingBox) iterator.next();

                structureboundingbox.a(i, j, k);
            }

        }

        protected void a(NBTTagCompound nbttagcompound) {
            super.a(nbttagcompound);
            NBTTagList nbttaglist = new NBTTagList();
            Iterator iterator = this.b.iterator();

            while (iterator.hasNext()) {
                StructureBoundingBox structureboundingbox = (StructureBoundingBox) iterator.next();

                nbttaglist.add(structureboundingbox.g());
            }

            nbttagcompound.set("Entrances", nbttaglist);
        }

        protected void a(NBTTagCompound nbttagcompound, DefinedStructureManager definedstructuremanager) {
            super.a(nbttagcompound, definedstructuremanager);
            NBTTagList nbttaglist = nbttagcompound.getList("Entrances", 11);

            for (int i = 0; i < nbttaglist.size(); ++i) {
                this.b.add(new StructureBoundingBox(nbttaglist.d(i)));
            }

        }
    }

    abstract static class c extends StructurePiece {

        protected WorldGenMineshaft.Type a;

        public c() {}

        public c(int i, WorldGenMineshaft.Type worldgenmineshaft_type) {
            super(i);
            this.a = worldgenmineshaft_type;
        }

        protected void a(NBTTagCompound nbttagcompound) {
            nbttagcompound.setInt("MST", this.a.ordinal());
        }

        protected void a(NBTTagCompound nbttagcompound, DefinedStructureManager definedstructuremanager) {
            this.a = WorldGenMineshaft.Type.a(nbttagcompound.getInt("MST"));
        }

        protected IBlockData G_() {
            switch (this.a) {
            case NORMAL:
            default:
                return Blocks.PLANKS.getBlockData();

            case MESA:
                return Blocks.PLANKS.getBlockData().set(BlockWood.VARIANT, BlockWood.EnumLogVariant.DARK_OAK);
            }
        }

        protected IBlockData b() {
            switch (this.a) {
            case NORMAL:
            default:
                return Blocks.FENCE.getBlockData();

            case MESA:
                return Blocks.DARK_OAK_FENCE.getBlockData();
            }
        }

        protected boolean a(World world, StructureBoundingBox structureboundingbox, int i, int j, int k, int l) {
            for (int i1 = i; i1 <= j; ++i1) {
                if (this.a(world, i1, k + 1, l, structureboundingbox).getMaterial() == Material.AIR) {
                    return false;
                }
            }

            return true;
        }
    }
}
