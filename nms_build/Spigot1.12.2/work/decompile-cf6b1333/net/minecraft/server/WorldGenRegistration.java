package net.minecraft.server;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

public class WorldGenRegistration {

    public static void a() {
        WorldGenFactory.a(WorldGenRegistration.WorldGenPyramidPiece.class, "TeDP");
        WorldGenFactory.a(WorldGenRegistration.WorldGenJungleTemple.class, "TeJP");
        WorldGenFactory.a(WorldGenRegistration.WorldGenWitchHut.class, "TeSH");
        WorldGenFactory.a(WorldGenRegistration.b.class, "Iglu");
    }

    public static class b extends WorldGenRegistration.WorldGenScatteredPiece {

        private static final MinecraftKey e = new MinecraftKey("igloo/igloo_top");
        private static final MinecraftKey f = new MinecraftKey("igloo/igloo_middle");
        private static final MinecraftKey g = new MinecraftKey("igloo/igloo_bottom");

        public b() {}

        public b(Random random, int i, int j) {
            super(random, i, 64, j, 7, 5, 8);
        }

        public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
            if (!this.a(world, structureboundingbox, -1)) {
                return false;
            } else {
                StructureBoundingBox structureboundingbox1 = this.d();
                BlockPosition blockposition = new BlockPosition(structureboundingbox1.a, structureboundingbox1.b, structureboundingbox1.c);
                EnumBlockRotation[] aenumblockrotation = EnumBlockRotation.values();
                MinecraftServer minecraftserver = world.getMinecraftServer();
                DefinedStructureManager definedstructuremanager = world.getDataManager().h();
                DefinedStructureInfo definedstructureinfo = (new DefinedStructureInfo()).a(aenumblockrotation[random.nextInt(aenumblockrotation.length)]).a(Blocks.dj).a(structureboundingbox1);
                DefinedStructure definedstructure = definedstructuremanager.a(minecraftserver, WorldGenRegistration.b.e);

                definedstructure.a(world, blockposition, definedstructureinfo);
                if (random.nextDouble() < 0.5D) {
                    DefinedStructure definedstructure1 = definedstructuremanager.a(minecraftserver, WorldGenRegistration.b.f);
                    DefinedStructure definedstructure2 = definedstructuremanager.a(minecraftserver, WorldGenRegistration.b.g);
                    int i = random.nextInt(8) + 4;

                    for (int j = 0; j < i; ++j) {
                        BlockPosition blockposition1 = definedstructure.a(definedstructureinfo, new BlockPosition(3, -1 - j * 3, 5), definedstructureinfo, new BlockPosition(1, 2, 1));

                        definedstructure1.a(world, blockposition.a((BaseBlockPosition) blockposition1), definedstructureinfo);
                    }

                    BlockPosition blockposition2 = blockposition.a((BaseBlockPosition) definedstructure.a(definedstructureinfo, new BlockPosition(3, -1 - i * 3, 5), definedstructureinfo, new BlockPosition(3, 5, 7)));

                    definedstructure2.a(world, blockposition2, definedstructureinfo);
                    Map map = definedstructure2.a(blockposition2, definedstructureinfo);
                    Iterator iterator = map.entrySet().iterator();

                    while (iterator.hasNext()) {
                        Entry entry = (Entry) iterator.next();

                        if ("chest".equals(entry.getValue())) {
                            BlockPosition blockposition3 = (BlockPosition) entry.getKey();

                            world.setTypeAndData(blockposition3, Blocks.AIR.getBlockData(), 3);
                            TileEntity tileentity = world.getTileEntity(blockposition3.down());

                            if (tileentity instanceof TileEntityChest) {
                                ((TileEntityChest) tileentity).a(LootTables.n, random.nextLong());
                            }
                        }
                    }
                } else {
                    BlockPosition blockposition4 = DefinedStructure.a(definedstructureinfo, new BlockPosition(3, 0, 5));

                    world.setTypeAndData(blockposition.a((BaseBlockPosition) blockposition4), Blocks.SNOW.getBlockData(), 3);
                }

                return true;
            }
        }
    }

    public static class WorldGenWitchHut extends WorldGenRegistration.WorldGenScatteredPiece {

        private boolean e;

        public WorldGenWitchHut() {}

        public WorldGenWitchHut(Random random, int i, int j) {
            super(random, i, 64, j, 7, 7, 9);
        }

        protected void a(NBTTagCompound nbttagcompound) {
            super.a(nbttagcompound);
            nbttagcompound.setBoolean("Witch", this.e);
        }

        protected void a(NBTTagCompound nbttagcompound, DefinedStructureManager definedstructuremanager) {
            super.a(nbttagcompound, definedstructuremanager);
            this.e = nbttagcompound.getBoolean("Witch");
        }

        public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
            if (!this.a(world, structureboundingbox, 0)) {
                return false;
            } else {
                this.a(world, structureboundingbox, 1, 1, 1, 5, 1, 7, Blocks.PLANKS.fromLegacyData(BlockWood.EnumLogVariant.SPRUCE.a()), Blocks.PLANKS.fromLegacyData(BlockWood.EnumLogVariant.SPRUCE.a()), false);
                this.a(world, structureboundingbox, 1, 4, 2, 5, 4, 7, Blocks.PLANKS.fromLegacyData(BlockWood.EnumLogVariant.SPRUCE.a()), Blocks.PLANKS.fromLegacyData(BlockWood.EnumLogVariant.SPRUCE.a()), false);
                this.a(world, structureboundingbox, 2, 1, 0, 4, 1, 0, Blocks.PLANKS.fromLegacyData(BlockWood.EnumLogVariant.SPRUCE.a()), Blocks.PLANKS.fromLegacyData(BlockWood.EnumLogVariant.SPRUCE.a()), false);
                this.a(world, structureboundingbox, 2, 2, 2, 3, 3, 2, Blocks.PLANKS.fromLegacyData(BlockWood.EnumLogVariant.SPRUCE.a()), Blocks.PLANKS.fromLegacyData(BlockWood.EnumLogVariant.SPRUCE.a()), false);
                this.a(world, structureboundingbox, 1, 2, 3, 1, 3, 6, Blocks.PLANKS.fromLegacyData(BlockWood.EnumLogVariant.SPRUCE.a()), Blocks.PLANKS.fromLegacyData(BlockWood.EnumLogVariant.SPRUCE.a()), false);
                this.a(world, structureboundingbox, 5, 2, 3, 5, 3, 6, Blocks.PLANKS.fromLegacyData(BlockWood.EnumLogVariant.SPRUCE.a()), Blocks.PLANKS.fromLegacyData(BlockWood.EnumLogVariant.SPRUCE.a()), false);
                this.a(world, structureboundingbox, 2, 2, 7, 4, 3, 7, Blocks.PLANKS.fromLegacyData(BlockWood.EnumLogVariant.SPRUCE.a()), Blocks.PLANKS.fromLegacyData(BlockWood.EnumLogVariant.SPRUCE.a()), false);
                this.a(world, structureboundingbox, 1, 0, 2, 1, 3, 2, Blocks.LOG.getBlockData(), Blocks.LOG.getBlockData(), false);
                this.a(world, structureboundingbox, 5, 0, 2, 5, 3, 2, Blocks.LOG.getBlockData(), Blocks.LOG.getBlockData(), false);
                this.a(world, structureboundingbox, 1, 0, 7, 1, 3, 7, Blocks.LOG.getBlockData(), Blocks.LOG.getBlockData(), false);
                this.a(world, structureboundingbox, 5, 0, 7, 5, 3, 7, Blocks.LOG.getBlockData(), Blocks.LOG.getBlockData(), false);
                this.a(world, Blocks.FENCE.getBlockData(), 2, 3, 2, structureboundingbox);
                this.a(world, Blocks.FENCE.getBlockData(), 3, 3, 7, structureboundingbox);
                this.a(world, Blocks.AIR.getBlockData(), 1, 3, 4, structureboundingbox);
                this.a(world, Blocks.AIR.getBlockData(), 5, 3, 4, structureboundingbox);
                this.a(world, Blocks.AIR.getBlockData(), 5, 3, 5, structureboundingbox);
                this.a(world, Blocks.FLOWER_POT.getBlockData().set(BlockFlowerPot.CONTENTS, BlockFlowerPot.EnumFlowerPotContents.MUSHROOM_RED), 1, 3, 5, structureboundingbox);
                this.a(world, Blocks.CRAFTING_TABLE.getBlockData(), 3, 2, 6, structureboundingbox);
                this.a(world, Blocks.cauldron.getBlockData(), 4, 2, 6, structureboundingbox);
                this.a(world, Blocks.FENCE.getBlockData(), 1, 2, 1, structureboundingbox);
                this.a(world, Blocks.FENCE.getBlockData(), 5, 2, 1, structureboundingbox);
                IBlockData iblockdata = Blocks.SPRUCE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.NORTH);
                IBlockData iblockdata1 = Blocks.SPRUCE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.EAST);
                IBlockData iblockdata2 = Blocks.SPRUCE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.WEST);
                IBlockData iblockdata3 = Blocks.SPRUCE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.SOUTH);

                this.a(world, structureboundingbox, 0, 4, 1, 6, 4, 1, iblockdata, iblockdata, false);
                this.a(world, structureboundingbox, 0, 4, 2, 0, 4, 7, iblockdata1, iblockdata1, false);
                this.a(world, structureboundingbox, 6, 4, 2, 6, 4, 7, iblockdata2, iblockdata2, false);
                this.a(world, structureboundingbox, 0, 4, 8, 6, 4, 8, iblockdata3, iblockdata3, false);

                int i;
                int j;

                for (i = 2; i <= 7; i += 5) {
                    for (j = 1; j <= 5; j += 4) {
                        this.b(world, Blocks.LOG.getBlockData(), j, -1, i, structureboundingbox);
                    }
                }

                if (!this.e) {
                    i = this.a(2, 5);
                    j = this.d(2);
                    int k = this.b(2, 5);

                    if (structureboundingbox.b((BaseBlockPosition) (new BlockPosition(i, j, k)))) {
                        this.e = true;
                        EntityWitch entitywitch = new EntityWitch(world);

                        entitywitch.cW();
                        entitywitch.setPositionRotation((double) i + 0.5D, (double) j, (double) k + 0.5D, 0.0F, 0.0F);
                        entitywitch.prepare(world.D(new BlockPosition(i, j, k)), (GroupDataEntity) null);
                        world.addEntity(entitywitch);
                    }
                }

                return true;
            }
        }
    }

    public static class WorldGenJungleTemple extends WorldGenRegistration.WorldGenScatteredPiece {

        private boolean e;
        private boolean f;
        private boolean g;
        private boolean h;
        private static final WorldGenRegistration.WorldGenJungleTemple.WorldGenJungleTemplePiece i = new WorldGenRegistration.WorldGenJungleTemple.WorldGenJungleTemplePiece(null);

        public WorldGenJungleTemple() {}

        public WorldGenJungleTemple(Random random, int i, int j) {
            super(random, i, 64, j, 12, 10, 15);
        }

        protected void a(NBTTagCompound nbttagcompound) {
            super.a(nbttagcompound);
            nbttagcompound.setBoolean("placedMainChest", this.e);
            nbttagcompound.setBoolean("placedHiddenChest", this.f);
            nbttagcompound.setBoolean("placedTrap1", this.g);
            nbttagcompound.setBoolean("placedTrap2", this.h);
        }

        protected void a(NBTTagCompound nbttagcompound, DefinedStructureManager definedstructuremanager) {
            super.a(nbttagcompound, definedstructuremanager);
            this.e = nbttagcompound.getBoolean("placedMainChest");
            this.f = nbttagcompound.getBoolean("placedHiddenChest");
            this.g = nbttagcompound.getBoolean("placedTrap1");
            this.h = nbttagcompound.getBoolean("placedTrap2");
        }

        public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
            if (!this.a(world, structureboundingbox, 0)) {
                return false;
            } else {
                this.a(world, structureboundingbox, 0, -4, 0, this.a - 1, 0, this.c - 1, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, structureboundingbox, 2, 1, 2, 9, 2, 2, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, structureboundingbox, 2, 1, 12, 9, 2, 12, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, structureboundingbox, 2, 1, 3, 2, 2, 11, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, structureboundingbox, 9, 1, 3, 9, 2, 11, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, structureboundingbox, 1, 3, 1, 10, 6, 1, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, structureboundingbox, 1, 3, 13, 10, 6, 13, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, structureboundingbox, 1, 3, 2, 1, 6, 12, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, structureboundingbox, 10, 3, 2, 10, 6, 12, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, structureboundingbox, 2, 3, 2, 9, 3, 12, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, structureboundingbox, 2, 6, 2, 9, 6, 12, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, structureboundingbox, 3, 7, 3, 8, 7, 11, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, structureboundingbox, 4, 8, 4, 7, 8, 10, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, structureboundingbox, 3, 1, 3, 8, 2, 11);
                this.a(world, structureboundingbox, 4, 3, 6, 7, 3, 9);
                this.a(world, structureboundingbox, 2, 4, 2, 9, 5, 12);
                this.a(world, structureboundingbox, 4, 6, 5, 7, 6, 9);
                this.a(world, structureboundingbox, 5, 7, 6, 6, 7, 8);
                this.a(world, structureboundingbox, 5, 1, 2, 6, 2, 2);
                this.a(world, structureboundingbox, 5, 2, 12, 6, 2, 12);
                this.a(world, structureboundingbox, 5, 5, 1, 6, 5, 1);
                this.a(world, structureboundingbox, 5, 5, 13, 6, 5, 13);
                this.a(world, Blocks.AIR.getBlockData(), 1, 5, 5, structureboundingbox);
                this.a(world, Blocks.AIR.getBlockData(), 10, 5, 5, structureboundingbox);
                this.a(world, Blocks.AIR.getBlockData(), 1, 5, 9, structureboundingbox);
                this.a(world, Blocks.AIR.getBlockData(), 10, 5, 9, structureboundingbox);

                int i;

                for (i = 0; i <= 14; i += 14) {
                    this.a(world, structureboundingbox, 2, 4, i, 2, 5, i, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                    this.a(world, structureboundingbox, 4, 4, i, 4, 5, i, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                    this.a(world, structureboundingbox, 7, 4, i, 7, 5, i, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                    this.a(world, structureboundingbox, 9, 4, i, 9, 5, i, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                }

                this.a(world, structureboundingbox, 5, 6, 0, 6, 6, 0, false, random, WorldGenRegistration.WorldGenJungleTemple.i);

                for (i = 0; i <= 11; i += 11) {
                    for (int j = 2; j <= 12; j += 2) {
                        this.a(world, structureboundingbox, i, 4, j, i, 5, j, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                    }

                    this.a(world, structureboundingbox, i, 6, 5, i, 6, 5, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                    this.a(world, structureboundingbox, i, 6, 9, i, 6, 9, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                }

                this.a(world, structureboundingbox, 2, 7, 2, 2, 9, 2, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, structureboundingbox, 9, 7, 2, 9, 9, 2, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, structureboundingbox, 2, 7, 12, 2, 9, 12, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, structureboundingbox, 9, 7, 12, 9, 9, 12, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, structureboundingbox, 4, 9, 4, 4, 9, 4, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, structureboundingbox, 7, 9, 4, 7, 9, 4, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, structureboundingbox, 4, 9, 10, 4, 9, 10, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, structureboundingbox, 7, 9, 10, 7, 9, 10, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, structureboundingbox, 5, 9, 7, 6, 9, 7, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                IBlockData iblockdata = Blocks.STONE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.EAST);
                IBlockData iblockdata1 = Blocks.STONE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.WEST);
                IBlockData iblockdata2 = Blocks.STONE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.SOUTH);
                IBlockData iblockdata3 = Blocks.STONE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.NORTH);

                this.a(world, iblockdata3, 5, 9, 6, structureboundingbox);
                this.a(world, iblockdata3, 6, 9, 6, structureboundingbox);
                this.a(world, iblockdata2, 5, 9, 8, structureboundingbox);
                this.a(world, iblockdata2, 6, 9, 8, structureboundingbox);
                this.a(world, iblockdata3, 4, 0, 0, structureboundingbox);
                this.a(world, iblockdata3, 5, 0, 0, structureboundingbox);
                this.a(world, iblockdata3, 6, 0, 0, structureboundingbox);
                this.a(world, iblockdata3, 7, 0, 0, structureboundingbox);
                this.a(world, iblockdata3, 4, 1, 8, structureboundingbox);
                this.a(world, iblockdata3, 4, 2, 9, structureboundingbox);
                this.a(world, iblockdata3, 4, 3, 10, structureboundingbox);
                this.a(world, iblockdata3, 7, 1, 8, structureboundingbox);
                this.a(world, iblockdata3, 7, 2, 9, structureboundingbox);
                this.a(world, iblockdata3, 7, 3, 10, structureboundingbox);
                this.a(world, structureboundingbox, 4, 1, 9, 4, 1, 9, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, structureboundingbox, 7, 1, 9, 7, 1, 9, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, structureboundingbox, 4, 1, 10, 7, 2, 10, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, structureboundingbox, 5, 4, 5, 6, 4, 5, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, iblockdata, 4, 4, 5, structureboundingbox);
                this.a(world, iblockdata1, 7, 4, 5, structureboundingbox);

                int k;

                for (k = 0; k < 4; ++k) {
                    this.a(world, iblockdata2, 5, 0 - k, 6 + k, structureboundingbox);
                    this.a(world, iblockdata2, 6, 0 - k, 6 + k, structureboundingbox);
                    this.a(world, structureboundingbox, 5, 0 - k, 7 + k, 6, 0 - k, 9 + k);
                }

                this.a(world, structureboundingbox, 1, -3, 12, 10, -1, 13);
                this.a(world, structureboundingbox, 1, -3, 1, 3, -1, 13);
                this.a(world, structureboundingbox, 1, -3, 1, 9, -1, 5);

                for (k = 1; k <= 13; k += 2) {
                    this.a(world, structureboundingbox, 1, -3, k, 1, -2, k, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                }

                for (k = 2; k <= 12; k += 2) {
                    this.a(world, structureboundingbox, 1, -1, k, 3, -1, k, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                }

                this.a(world, structureboundingbox, 2, -2, 1, 5, -2, 1, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, structureboundingbox, 7, -2, 1, 9, -2, 1, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, structureboundingbox, 6, -3, 1, 6, -3, 1, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, structureboundingbox, 6, -1, 1, 6, -1, 1, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, Blocks.TRIPWIRE_HOOK.getBlockData().set(BlockTripwireHook.FACING, EnumDirection.EAST).set(BlockTripwireHook.ATTACHED, Boolean.valueOf(true)), 1, -3, 8, structureboundingbox);
                this.a(world, Blocks.TRIPWIRE_HOOK.getBlockData().set(BlockTripwireHook.FACING, EnumDirection.WEST).set(BlockTripwireHook.ATTACHED, Boolean.valueOf(true)), 4, -3, 8, structureboundingbox);
                this.a(world, Blocks.TRIPWIRE.getBlockData().set(BlockTripwire.ATTACHED, Boolean.valueOf(true)), 2, -3, 8, structureboundingbox);
                this.a(world, Blocks.TRIPWIRE.getBlockData().set(BlockTripwire.ATTACHED, Boolean.valueOf(true)), 3, -3, 8, structureboundingbox);
                this.a(world, Blocks.REDSTONE_WIRE.getBlockData(), 5, -3, 7, structureboundingbox);
                this.a(world, Blocks.REDSTONE_WIRE.getBlockData(), 5, -3, 6, structureboundingbox);
                this.a(world, Blocks.REDSTONE_WIRE.getBlockData(), 5, -3, 5, structureboundingbox);
                this.a(world, Blocks.REDSTONE_WIRE.getBlockData(), 5, -3, 4, structureboundingbox);
                this.a(world, Blocks.REDSTONE_WIRE.getBlockData(), 5, -3, 3, structureboundingbox);
                this.a(world, Blocks.REDSTONE_WIRE.getBlockData(), 5, -3, 2, structureboundingbox);
                this.a(world, Blocks.REDSTONE_WIRE.getBlockData(), 5, -3, 1, structureboundingbox);
                this.a(world, Blocks.REDSTONE_WIRE.getBlockData(), 4, -3, 1, structureboundingbox);
                this.a(world, Blocks.MOSSY_COBBLESTONE.getBlockData(), 3, -3, 1, structureboundingbox);
                if (!this.g) {
                    this.g = this.a(world, structureboundingbox, random, 3, -2, 1, EnumDirection.NORTH, LootTables.m);
                }

                this.a(world, Blocks.VINE.getBlockData().set(BlockVine.SOUTH, Boolean.valueOf(true)), 3, -2, 2, structureboundingbox);
                this.a(world, Blocks.TRIPWIRE_HOOK.getBlockData().set(BlockTripwireHook.FACING, EnumDirection.NORTH).set(BlockTripwireHook.ATTACHED, Boolean.valueOf(true)), 7, -3, 1, structureboundingbox);
                this.a(world, Blocks.TRIPWIRE_HOOK.getBlockData().set(BlockTripwireHook.FACING, EnumDirection.SOUTH).set(BlockTripwireHook.ATTACHED, Boolean.valueOf(true)), 7, -3, 5, structureboundingbox);
                this.a(world, Blocks.TRIPWIRE.getBlockData().set(BlockTripwire.ATTACHED, Boolean.valueOf(true)), 7, -3, 2, structureboundingbox);
                this.a(world, Blocks.TRIPWIRE.getBlockData().set(BlockTripwire.ATTACHED, Boolean.valueOf(true)), 7, -3, 3, structureboundingbox);
                this.a(world, Blocks.TRIPWIRE.getBlockData().set(BlockTripwire.ATTACHED, Boolean.valueOf(true)), 7, -3, 4, structureboundingbox);
                this.a(world, Blocks.REDSTONE_WIRE.getBlockData(), 8, -3, 6, structureboundingbox);
                this.a(world, Blocks.REDSTONE_WIRE.getBlockData(), 9, -3, 6, structureboundingbox);
                this.a(world, Blocks.REDSTONE_WIRE.getBlockData(), 9, -3, 5, structureboundingbox);
                this.a(world, Blocks.MOSSY_COBBLESTONE.getBlockData(), 9, -3, 4, structureboundingbox);
                this.a(world, Blocks.REDSTONE_WIRE.getBlockData(), 9, -2, 4, structureboundingbox);
                if (!this.h) {
                    this.h = this.a(world, structureboundingbox, random, 9, -2, 3, EnumDirection.WEST, LootTables.m);
                }

                this.a(world, Blocks.VINE.getBlockData().set(BlockVine.EAST, Boolean.valueOf(true)), 8, -1, 3, structureboundingbox);
                this.a(world, Blocks.VINE.getBlockData().set(BlockVine.EAST, Boolean.valueOf(true)), 8, -2, 3, structureboundingbox);
                if (!this.e) {
                    this.e = this.a(world, structureboundingbox, random, 8, -3, 3, LootTables.l);
                }

                this.a(world, Blocks.MOSSY_COBBLESTONE.getBlockData(), 9, -3, 2, structureboundingbox);
                this.a(world, Blocks.MOSSY_COBBLESTONE.getBlockData(), 8, -3, 1, structureboundingbox);
                this.a(world, Blocks.MOSSY_COBBLESTONE.getBlockData(), 4, -3, 5, structureboundingbox);
                this.a(world, Blocks.MOSSY_COBBLESTONE.getBlockData(), 5, -2, 5, structureboundingbox);
                this.a(world, Blocks.MOSSY_COBBLESTONE.getBlockData(), 5, -1, 5, structureboundingbox);
                this.a(world, Blocks.MOSSY_COBBLESTONE.getBlockData(), 6, -3, 5, structureboundingbox);
                this.a(world, Blocks.MOSSY_COBBLESTONE.getBlockData(), 7, -2, 5, structureboundingbox);
                this.a(world, Blocks.MOSSY_COBBLESTONE.getBlockData(), 7, -1, 5, structureboundingbox);
                this.a(world, Blocks.MOSSY_COBBLESTONE.getBlockData(), 8, -3, 5, structureboundingbox);
                this.a(world, structureboundingbox, 9, -1, 1, 9, -1, 5, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, structureboundingbox, 8, -3, 8, 10, -1, 10);
                this.a(world, Blocks.STONEBRICK.fromLegacyData(BlockSmoothBrick.e), 8, -2, 11, structureboundingbox);
                this.a(world, Blocks.STONEBRICK.fromLegacyData(BlockSmoothBrick.e), 9, -2, 11, structureboundingbox);
                this.a(world, Blocks.STONEBRICK.fromLegacyData(BlockSmoothBrick.e), 10, -2, 11, structureboundingbox);
                IBlockData iblockdata4 = Blocks.LEVER.getBlockData().set(BlockLever.FACING, BlockLever.EnumLeverPosition.NORTH);

                this.a(world, iblockdata4, 8, -2, 12, structureboundingbox);
                this.a(world, iblockdata4, 9, -2, 12, structureboundingbox);
                this.a(world, iblockdata4, 10, -2, 12, structureboundingbox);
                this.a(world, structureboundingbox, 8, -3, 8, 8, -3, 10, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, structureboundingbox, 10, -3, 8, 10, -3, 10, false, random, WorldGenRegistration.WorldGenJungleTemple.i);
                this.a(world, Blocks.MOSSY_COBBLESTONE.getBlockData(), 10, -2, 9, structureboundingbox);
                this.a(world, Blocks.REDSTONE_WIRE.getBlockData(), 8, -2, 9, structureboundingbox);
                this.a(world, Blocks.REDSTONE_WIRE.getBlockData(), 8, -2, 10, structureboundingbox);
                this.a(world, Blocks.REDSTONE_WIRE.getBlockData(), 10, -1, 9, structureboundingbox);
                this.a(world, Blocks.STICKY_PISTON.getBlockData().set(BlockPiston.FACING, EnumDirection.UP), 9, -2, 8, structureboundingbox);
                this.a(world, Blocks.STICKY_PISTON.getBlockData().set(BlockPiston.FACING, EnumDirection.WEST), 10, -2, 8, structureboundingbox);
                this.a(world, Blocks.STICKY_PISTON.getBlockData().set(BlockPiston.FACING, EnumDirection.WEST), 10, -1, 8, structureboundingbox);
                this.a(world, Blocks.UNPOWERED_REPEATER.getBlockData().set(BlockRepeater.FACING, EnumDirection.NORTH), 10, -2, 10, structureboundingbox);
                if (!this.f) {
                    this.f = this.a(world, structureboundingbox, random, 9, -3, 10, LootTables.l);
                }

                return true;
            }
        }

        static class WorldGenJungleTemplePiece extends StructurePiece.StructurePieceBlockSelector {

            private WorldGenJungleTemplePiece() {}

            public void a(Random random, int i, int j, int k, boolean flag) {
                if (random.nextFloat() < 0.4F) {
                    this.a = Blocks.COBBLESTONE.getBlockData();
                } else {
                    this.a = Blocks.MOSSY_COBBLESTONE.getBlockData();
                }

            }

            WorldGenJungleTemplePiece(Object object) {
                this();
            }
        }
    }

    public static class WorldGenPyramidPiece extends WorldGenRegistration.WorldGenScatteredPiece {

        private final boolean[] e = new boolean[4];

        public WorldGenPyramidPiece() {}

        public WorldGenPyramidPiece(Random random, int i, int j) {
            super(random, i, 64, j, 21, 15, 21);
        }

        protected void a(NBTTagCompound nbttagcompound) {
            super.a(nbttagcompound);
            nbttagcompound.setBoolean("hasPlacedChest0", this.e[0]);
            nbttagcompound.setBoolean("hasPlacedChest1", this.e[1]);
            nbttagcompound.setBoolean("hasPlacedChest2", this.e[2]);
            nbttagcompound.setBoolean("hasPlacedChest3", this.e[3]);
        }

        protected void a(NBTTagCompound nbttagcompound, DefinedStructureManager definedstructuremanager) {
            super.a(nbttagcompound, definedstructuremanager);
            this.e[0] = nbttagcompound.getBoolean("hasPlacedChest0");
            this.e[1] = nbttagcompound.getBoolean("hasPlacedChest1");
            this.e[2] = nbttagcompound.getBoolean("hasPlacedChest2");
            this.e[3] = nbttagcompound.getBoolean("hasPlacedChest3");
        }

        public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
            this.a(world, structureboundingbox, 0, -4, 0, this.a - 1, 0, this.c - 1, Blocks.SANDSTONE.getBlockData(), Blocks.SANDSTONE.getBlockData(), false);

            int i;

            for (i = 1; i <= 9; ++i) {
                this.a(world, structureboundingbox, i, i, i, this.a - 1 - i, i, this.c - 1 - i, Blocks.SANDSTONE.getBlockData(), Blocks.SANDSTONE.getBlockData(), false);
                this.a(world, structureboundingbox, i + 1, i, i + 1, this.a - 2 - i, i, this.c - 2 - i, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            }

            for (i = 0; i < this.a; ++i) {
                for (int j = 0; j < this.c; ++j) {
                    boolean flag = true;

                    this.b(world, Blocks.SANDSTONE.getBlockData(), i, -5, j, structureboundingbox);
                }
            }

            IBlockData iblockdata = Blocks.SANDSTONE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.NORTH);
            IBlockData iblockdata1 = Blocks.SANDSTONE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.SOUTH);
            IBlockData iblockdata2 = Blocks.SANDSTONE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.EAST);
            IBlockData iblockdata3 = Blocks.SANDSTONE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.WEST);
            int k = ~EnumColor.ORANGE.getInvColorIndex() & 15;
            int l = ~EnumColor.BLUE.getInvColorIndex() & 15;

            this.a(world, structureboundingbox, 0, 0, 0, 4, 9, 4, Blocks.SANDSTONE.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, 1, 10, 1, 3, 10, 3, Blocks.SANDSTONE.getBlockData(), Blocks.SANDSTONE.getBlockData(), false);
            this.a(world, iblockdata, 2, 10, 0, structureboundingbox);
            this.a(world, iblockdata1, 2, 10, 4, structureboundingbox);
            this.a(world, iblockdata2, 0, 10, 2, structureboundingbox);
            this.a(world, iblockdata3, 4, 10, 2, structureboundingbox);
            this.a(world, structureboundingbox, this.a - 5, 0, 0, this.a - 1, 9, 4, Blocks.SANDSTONE.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, this.a - 4, 10, 1, this.a - 2, 10, 3, Blocks.SANDSTONE.getBlockData(), Blocks.SANDSTONE.getBlockData(), false);
            this.a(world, iblockdata, this.a - 3, 10, 0, structureboundingbox);
            this.a(world, iblockdata1, this.a - 3, 10, 4, structureboundingbox);
            this.a(world, iblockdata2, this.a - 5, 10, 2, structureboundingbox);
            this.a(world, iblockdata3, this.a - 1, 10, 2, structureboundingbox);
            this.a(world, structureboundingbox, 8, 0, 0, 12, 4, 4, Blocks.SANDSTONE.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, 9, 1, 0, 11, 3, 4, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), 9, 1, 1, structureboundingbox);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), 9, 2, 1, structureboundingbox);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), 9, 3, 1, structureboundingbox);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), 10, 3, 1, structureboundingbox);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), 11, 3, 1, structureboundingbox);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), 11, 2, 1, structureboundingbox);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), 11, 1, 1, structureboundingbox);
            this.a(world, structureboundingbox, 4, 1, 1, 8, 3, 3, Blocks.SANDSTONE.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, 4, 1, 2, 8, 2, 2, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, 12, 1, 1, 16, 3, 3, Blocks.SANDSTONE.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, 12, 1, 2, 16, 2, 2, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, 5, 4, 5, this.a - 6, 4, this.c - 6, Blocks.SANDSTONE.getBlockData(), Blocks.SANDSTONE.getBlockData(), false);
            this.a(world, structureboundingbox, 9, 4, 9, 11, 4, 11, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, 8, 1, 8, 8, 3, 8, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), false);
            this.a(world, structureboundingbox, 12, 1, 8, 12, 3, 8, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), false);
            this.a(world, structureboundingbox, 8, 1, 12, 8, 3, 12, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), false);
            this.a(world, structureboundingbox, 12, 1, 12, 12, 3, 12, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), false);
            this.a(world, structureboundingbox, 1, 1, 5, 4, 4, 11, Blocks.SANDSTONE.getBlockData(), Blocks.SANDSTONE.getBlockData(), false);
            this.a(world, structureboundingbox, this.a - 5, 1, 5, this.a - 2, 4, 11, Blocks.SANDSTONE.getBlockData(), Blocks.SANDSTONE.getBlockData(), false);
            this.a(world, structureboundingbox, 6, 7, 9, 6, 7, 11, Blocks.SANDSTONE.getBlockData(), Blocks.SANDSTONE.getBlockData(), false);
            this.a(world, structureboundingbox, this.a - 7, 7, 9, this.a - 7, 7, 11, Blocks.SANDSTONE.getBlockData(), Blocks.SANDSTONE.getBlockData(), false);
            this.a(world, structureboundingbox, 5, 5, 9, 5, 7, 11, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), false);
            this.a(world, structureboundingbox, this.a - 6, 5, 9, this.a - 6, 7, 11, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), false);
            this.a(world, Blocks.AIR.getBlockData(), 5, 5, 10, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 5, 6, 10, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 6, 6, 10, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), this.a - 6, 5, 10, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), this.a - 6, 6, 10, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), this.a - 7, 6, 10, structureboundingbox);
            this.a(world, structureboundingbox, 2, 4, 4, 2, 6, 4, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, this.a - 3, 4, 4, this.a - 3, 6, 4, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, iblockdata, 2, 4, 5, structureboundingbox);
            this.a(world, iblockdata, 2, 3, 4, structureboundingbox);
            this.a(world, iblockdata, this.a - 3, 4, 5, structureboundingbox);
            this.a(world, iblockdata, this.a - 3, 3, 4, structureboundingbox);
            this.a(world, structureboundingbox, 1, 1, 3, 2, 2, 3, Blocks.SANDSTONE.getBlockData(), Blocks.SANDSTONE.getBlockData(), false);
            this.a(world, structureboundingbox, this.a - 3, 1, 3, this.a - 2, 2, 3, Blocks.SANDSTONE.getBlockData(), Blocks.SANDSTONE.getBlockData(), false);
            this.a(world, Blocks.SANDSTONE.getBlockData(), 1, 1, 2, structureboundingbox);
            this.a(world, Blocks.SANDSTONE.getBlockData(), this.a - 2, 1, 2, structureboundingbox);
            this.a(world, Blocks.STONE_SLAB.fromLegacyData(BlockDoubleStepAbstract.EnumStoneSlabVariant.SAND.a()), 1, 2, 2, structureboundingbox);
            this.a(world, Blocks.STONE_SLAB.fromLegacyData(BlockDoubleStepAbstract.EnumStoneSlabVariant.SAND.a()), this.a - 2, 2, 2, structureboundingbox);
            this.a(world, iblockdata3, 2, 1, 2, structureboundingbox);
            this.a(world, iblockdata2, this.a - 3, 1, 2, structureboundingbox);
            this.a(world, structureboundingbox, 4, 3, 5, 4, 3, 18, Blocks.SANDSTONE.getBlockData(), Blocks.SANDSTONE.getBlockData(), false);
            this.a(world, structureboundingbox, this.a - 5, 3, 5, this.a - 5, 3, 17, Blocks.SANDSTONE.getBlockData(), Blocks.SANDSTONE.getBlockData(), false);
            this.a(world, structureboundingbox, 3, 1, 5, 4, 2, 16, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, this.a - 6, 1, 5, this.a - 5, 2, 16, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);

            int i1;

            for (i1 = 5; i1 <= 17; i1 += 2) {
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), 4, 1, i1, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.CHISELED.a()), 4, 2, i1, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), this.a - 5, 1, i1, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.CHISELED.a()), this.a - 5, 2, i1, structureboundingbox);
            }

            this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), 10, 0, 7, structureboundingbox);
            this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), 10, 0, 8, structureboundingbox);
            this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), 9, 0, 9, structureboundingbox);
            this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), 11, 0, 9, structureboundingbox);
            this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), 8, 0, 10, structureboundingbox);
            this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), 12, 0, 10, structureboundingbox);
            this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), 7, 0, 10, structureboundingbox);
            this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), 13, 0, 10, structureboundingbox);
            this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), 9, 0, 11, structureboundingbox);
            this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), 11, 0, 11, structureboundingbox);
            this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), 10, 0, 12, structureboundingbox);
            this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), 10, 0, 13, structureboundingbox);
            this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(l), 10, 0, 10, structureboundingbox);

            for (i1 = 0; i1 <= this.a - 1; i1 += this.a - 1) {
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), i1, 2, 1, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), i1, 2, 2, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), i1, 2, 3, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), i1, 3, 1, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), i1, 3, 2, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), i1, 3, 3, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), i1, 4, 1, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.CHISELED.a()), i1, 4, 2, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), i1, 4, 3, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), i1, 5, 1, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), i1, 5, 2, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), i1, 5, 3, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), i1, 6, 1, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.CHISELED.a()), i1, 6, 2, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), i1, 6, 3, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), i1, 7, 1, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), i1, 7, 2, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), i1, 7, 3, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), i1, 8, 1, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), i1, 8, 2, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), i1, 8, 3, structureboundingbox);
            }

            for (i1 = 2; i1 <= this.a - 3; i1 += this.a - 3 - 2) {
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), i1 - 1, 2, 0, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), i1, 2, 0, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), i1 + 1, 2, 0, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), i1 - 1, 3, 0, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), i1, 3, 0, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), i1 + 1, 3, 0, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), i1 - 1, 4, 0, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.CHISELED.a()), i1, 4, 0, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), i1 + 1, 4, 0, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), i1 - 1, 5, 0, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), i1, 5, 0, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), i1 + 1, 5, 0, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), i1 - 1, 6, 0, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.CHISELED.a()), i1, 6, 0, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), i1 + 1, 6, 0, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), i1 - 1, 7, 0, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), i1, 7, 0, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), i1 + 1, 7, 0, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), i1 - 1, 8, 0, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), i1, 8, 0, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), i1 + 1, 8, 0, structureboundingbox);
            }

            this.a(world, structureboundingbox, 8, 4, 0, 12, 6, 0, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), false);
            this.a(world, Blocks.AIR.getBlockData(), 8, 6, 0, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 12, 6, 0, structureboundingbox);
            this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), 9, 5, 0, structureboundingbox);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.CHISELED.a()), 10, 5, 0, structureboundingbox);
            this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(k), 11, 5, 0, structureboundingbox);
            this.a(world, structureboundingbox, 8, -14, 8, 12, -11, 12, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), false);
            this.a(world, structureboundingbox, 8, -10, 8, 12, -10, 12, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.CHISELED.a()), Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.CHISELED.a()), false);
            this.a(world, structureboundingbox, 8, -9, 8, 12, -9, 12, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), false);
            this.a(world, structureboundingbox, 8, -8, 8, 12, -1, 12, Blocks.SANDSTONE.getBlockData(), Blocks.SANDSTONE.getBlockData(), false);
            this.a(world, structureboundingbox, 9, -11, 9, 11, -1, 11, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, Blocks.STONE_PRESSURE_PLATE.getBlockData(), 10, -11, 10, structureboundingbox);
            this.a(world, structureboundingbox, 9, -13, 9, 11, -13, 11, Blocks.TNT.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, Blocks.AIR.getBlockData(), 8, -11, 10, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 8, -10, 10, structureboundingbox);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.CHISELED.a()), 7, -10, 10, structureboundingbox);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), 7, -11, 10, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 12, -11, 10, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 12, -10, 10, structureboundingbox);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.CHISELED.a()), 13, -10, 10, structureboundingbox);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), 13, -11, 10, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 10, -11, 8, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 10, -10, 8, structureboundingbox);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.CHISELED.a()), 10, -10, 7, structureboundingbox);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), 10, -11, 7, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 10, -11, 12, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 10, -10, 12, structureboundingbox);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.CHISELED.a()), 10, -10, 13, structureboundingbox);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), 10, -11, 13, structureboundingbox);
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                EnumDirection enumdirection = (EnumDirection) iterator.next();

                if (!this.e[enumdirection.get2DRotationValue()]) {
                    int j1 = enumdirection.getAdjacentX() * 2;
                    int k1 = enumdirection.getAdjacentZ() * 2;

                    this.e[enumdirection.get2DRotationValue()] = this.a(world, structureboundingbox, random, 10 + j1, -11, 10 + k1, LootTables.k);
                }
            }

            return true;
        }
    }

    abstract static class WorldGenScatteredPiece extends StructurePiece {

        protected int a;
        protected int b;
        protected int c;
        protected int d = -1;

        public WorldGenScatteredPiece() {}

        protected WorldGenScatteredPiece(Random random, int i, int j, int k, int l, int i1, int j1) {
            super(0);
            this.a = l;
            this.b = i1;
            this.c = j1;
            this.a(EnumDirection.EnumDirectionLimit.HORIZONTAL.a(random));
            if (this.f().k() == EnumDirection.EnumAxis.Z) {
                this.l = new StructureBoundingBox(i, j, k, i + l - 1, j + i1 - 1, k + j1 - 1);
            } else {
                this.l = new StructureBoundingBox(i, j, k, i + j1 - 1, j + i1 - 1, k + l - 1);
            }

        }

        protected void a(NBTTagCompound nbttagcompound) {
            nbttagcompound.setInt("Width", this.a);
            nbttagcompound.setInt("Height", this.b);
            nbttagcompound.setInt("Depth", this.c);
            nbttagcompound.setInt("HPos", this.d);
        }

        protected void a(NBTTagCompound nbttagcompound, DefinedStructureManager definedstructuremanager) {
            this.a = nbttagcompound.getInt("Width");
            this.b = nbttagcompound.getInt("Height");
            this.c = nbttagcompound.getInt("Depth");
            this.d = nbttagcompound.getInt("HPos");
        }

        protected boolean a(World world, StructureBoundingBox structureboundingbox, int i) {
            if (this.d >= 0) {
                return true;
            } else {
                int j = 0;
                int k = 0;
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

                for (int l = this.l.c; l <= this.l.f; ++l) {
                    for (int i1 = this.l.a; i1 <= this.l.d; ++i1) {
                        blockposition_mutableblockposition.c(i1, 64, l);
                        if (structureboundingbox.b((BaseBlockPosition) blockposition_mutableblockposition)) {
                            j += Math.max(world.q(blockposition_mutableblockposition).getY(), world.worldProvider.getSeaLevel());
                            ++k;
                        }
                    }
                }

                if (k == 0) {
                    return false;
                } else {
                    this.d = j / k;
                    this.l.a(0, this.d - this.l.b + i, 0);
                    return true;
                }
            }
        }
    }
}
