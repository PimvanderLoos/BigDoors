package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.decoration.EntityItemFrame;
import net.minecraft.world.entity.monster.EntityShulker;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.entity.TileEntityLootable;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureStructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessorBlockIgnore;
import net.minecraft.world.level.storage.loot.LootTables;

public class WorldGenEndCityPieces {

    private static final int MAX_GEN_DEPTH = 8;
    static final WorldGenEndCityPieces.PieceGenerator HOUSE_TOWER_GENERATOR = new WorldGenEndCityPieces.PieceGenerator() {
        @Override
        public void a() {}

        @Override
        public boolean a(DefinedStructureManager definedstructuremanager, int i, WorldGenEndCityPieces.Piece worldgenendcitypieces_piece, BlockPosition blockposition, List<StructurePiece> list, Random random) {
            if (i > 8) {
                return false;
            } else {
                EnumBlockRotation enumblockrotation = worldgenendcitypieces_piece.placeSettings.d();
                WorldGenEndCityPieces.Piece worldgenendcitypieces_piece1 = WorldGenEndCityPieces.a(list, WorldGenEndCityPieces.a(definedstructuremanager, worldgenendcitypieces_piece, blockposition, "base_floor", enumblockrotation, true));
                int j = random.nextInt(3);

                if (j == 0) {
                    WorldGenEndCityPieces.a(list, WorldGenEndCityPieces.a(definedstructuremanager, worldgenendcitypieces_piece1, new BlockPosition(-1, 4, -1), "base_roof", enumblockrotation, true));
                } else if (j == 1) {
                    worldgenendcitypieces_piece1 = WorldGenEndCityPieces.a(list, WorldGenEndCityPieces.a(definedstructuremanager, worldgenendcitypieces_piece1, new BlockPosition(-1, 0, -1), "second_floor_2", enumblockrotation, false));
                    worldgenendcitypieces_piece1 = WorldGenEndCityPieces.a(list, WorldGenEndCityPieces.a(definedstructuremanager, worldgenendcitypieces_piece1, new BlockPosition(-1, 8, -1), "second_roof", enumblockrotation, false));
                    WorldGenEndCityPieces.a(definedstructuremanager, WorldGenEndCityPieces.TOWER_GENERATOR, i + 1, worldgenendcitypieces_piece1, (BlockPosition) null, list, random);
                } else if (j == 2) {
                    worldgenendcitypieces_piece1 = WorldGenEndCityPieces.a(list, WorldGenEndCityPieces.a(definedstructuremanager, worldgenendcitypieces_piece1, new BlockPosition(-1, 0, -1), "second_floor_2", enumblockrotation, false));
                    worldgenendcitypieces_piece1 = WorldGenEndCityPieces.a(list, WorldGenEndCityPieces.a(definedstructuremanager, worldgenendcitypieces_piece1, new BlockPosition(-1, 4, -1), "third_floor_2", enumblockrotation, false));
                    worldgenendcitypieces_piece1 = WorldGenEndCityPieces.a(list, WorldGenEndCityPieces.a(definedstructuremanager, worldgenendcitypieces_piece1, new BlockPosition(-1, 8, -1), "third_roof", enumblockrotation, true));
                    WorldGenEndCityPieces.a(definedstructuremanager, WorldGenEndCityPieces.TOWER_GENERATOR, i + 1, worldgenendcitypieces_piece1, (BlockPosition) null, list, random);
                }

                return true;
            }
        }
    };
    static final List<Tuple<EnumBlockRotation, BlockPosition>> TOWER_BRIDGES = Lists.newArrayList(new Tuple[]{new Tuple<>(EnumBlockRotation.NONE, new BlockPosition(1, -1, 0)), new Tuple<>(EnumBlockRotation.CLOCKWISE_90, new BlockPosition(6, -1, 1)), new Tuple<>(EnumBlockRotation.COUNTERCLOCKWISE_90, new BlockPosition(0, -1, 5)), new Tuple<>(EnumBlockRotation.CLOCKWISE_180, new BlockPosition(5, -1, 6))});
    static final WorldGenEndCityPieces.PieceGenerator TOWER_GENERATOR = new WorldGenEndCityPieces.PieceGenerator() {
        @Override
        public void a() {}

        @Override
        public boolean a(DefinedStructureManager definedstructuremanager, int i, WorldGenEndCityPieces.Piece worldgenendcitypieces_piece, BlockPosition blockposition, List<StructurePiece> list, Random random) {
            EnumBlockRotation enumblockrotation = worldgenendcitypieces_piece.placeSettings.d();
            WorldGenEndCityPieces.Piece worldgenendcitypieces_piece1 = WorldGenEndCityPieces.a(list, WorldGenEndCityPieces.a(definedstructuremanager, worldgenendcitypieces_piece, new BlockPosition(3 + random.nextInt(2), -3, 3 + random.nextInt(2)), "tower_base", enumblockrotation, true));

            worldgenendcitypieces_piece1 = WorldGenEndCityPieces.a(list, WorldGenEndCityPieces.a(definedstructuremanager, worldgenendcitypieces_piece1, new BlockPosition(0, 7, 0), "tower_piece", enumblockrotation, true));
            WorldGenEndCityPieces.Piece worldgenendcitypieces_piece2 = random.nextInt(3) == 0 ? worldgenendcitypieces_piece1 : null;
            int j = 1 + random.nextInt(3);

            for (int k = 0; k < j; ++k) {
                worldgenendcitypieces_piece1 = WorldGenEndCityPieces.a(list, WorldGenEndCityPieces.a(definedstructuremanager, worldgenendcitypieces_piece1, new BlockPosition(0, 4, 0), "tower_piece", enumblockrotation, true));
                if (k < j - 1 && random.nextBoolean()) {
                    worldgenendcitypieces_piece2 = worldgenendcitypieces_piece1;
                }
            }

            if (worldgenendcitypieces_piece2 != null) {
                Iterator iterator = WorldGenEndCityPieces.TOWER_BRIDGES.iterator();

                while (iterator.hasNext()) {
                    Tuple<EnumBlockRotation, BlockPosition> tuple = (Tuple) iterator.next();

                    if (random.nextBoolean()) {
                        WorldGenEndCityPieces.Piece worldgenendcitypieces_piece3 = WorldGenEndCityPieces.a(list, WorldGenEndCityPieces.a(definedstructuremanager, worldgenendcitypieces_piece2, (BlockPosition) tuple.b(), "bridge_end", enumblockrotation.a((EnumBlockRotation) tuple.a()), true));

                        WorldGenEndCityPieces.a(definedstructuremanager, WorldGenEndCityPieces.TOWER_BRIDGE_GENERATOR, i + 1, worldgenendcitypieces_piece3, (BlockPosition) null, list, random);
                    }
                }

                WorldGenEndCityPieces.a(list, WorldGenEndCityPieces.a(definedstructuremanager, worldgenendcitypieces_piece1, new BlockPosition(-1, 4, -1), "tower_top", enumblockrotation, true));
            } else {
                if (i != 7) {
                    return WorldGenEndCityPieces.a(definedstructuremanager, WorldGenEndCityPieces.FAT_TOWER_GENERATOR, i + 1, worldgenendcitypieces_piece1, (BlockPosition) null, list, random);
                }

                WorldGenEndCityPieces.a(list, WorldGenEndCityPieces.a(definedstructuremanager, worldgenendcitypieces_piece1, new BlockPosition(-1, 4, -1), "tower_top", enumblockrotation, true));
            }

            return true;
        }
    };
    static final WorldGenEndCityPieces.PieceGenerator TOWER_BRIDGE_GENERATOR = new WorldGenEndCityPieces.PieceGenerator() {
        public boolean shipCreated;

        @Override
        public void a() {
            this.shipCreated = false;
        }

        @Override
        public boolean a(DefinedStructureManager definedstructuremanager, int i, WorldGenEndCityPieces.Piece worldgenendcitypieces_piece, BlockPosition blockposition, List<StructurePiece> list, Random random) {
            EnumBlockRotation enumblockrotation = worldgenendcitypieces_piece.placeSettings.d();
            int j = random.nextInt(4) + 1;
            WorldGenEndCityPieces.Piece worldgenendcitypieces_piece1 = WorldGenEndCityPieces.a(list, WorldGenEndCityPieces.a(definedstructuremanager, worldgenendcitypieces_piece, new BlockPosition(0, 0, -4), "bridge_piece", enumblockrotation, true));

            worldgenendcitypieces_piece1.genDepth = -1;
            byte b0 = 0;

            for (int k = 0; k < j; ++k) {
                if (random.nextBoolean()) {
                    worldgenendcitypieces_piece1 = WorldGenEndCityPieces.a(list, WorldGenEndCityPieces.a(definedstructuremanager, worldgenendcitypieces_piece1, new BlockPosition(0, b0, -4), "bridge_piece", enumblockrotation, true));
                    b0 = 0;
                } else {
                    if (random.nextBoolean()) {
                        worldgenendcitypieces_piece1 = WorldGenEndCityPieces.a(list, WorldGenEndCityPieces.a(definedstructuremanager, worldgenendcitypieces_piece1, new BlockPosition(0, b0, -4), "bridge_steep_stairs", enumblockrotation, true));
                    } else {
                        worldgenendcitypieces_piece1 = WorldGenEndCityPieces.a(list, WorldGenEndCityPieces.a(definedstructuremanager, worldgenendcitypieces_piece1, new BlockPosition(0, b0, -8), "bridge_gentle_stairs", enumblockrotation, true));
                    }

                    b0 = 4;
                }
            }

            if (!this.shipCreated && random.nextInt(10 - i) == 0) {
                WorldGenEndCityPieces.a(list, WorldGenEndCityPieces.a(definedstructuremanager, worldgenendcitypieces_piece1, new BlockPosition(-8 + random.nextInt(8), b0, -70 + random.nextInt(10)), "ship", enumblockrotation, true));
                this.shipCreated = true;
            } else if (!WorldGenEndCityPieces.a(definedstructuremanager, WorldGenEndCityPieces.HOUSE_TOWER_GENERATOR, i + 1, worldgenendcitypieces_piece1, new BlockPosition(-3, b0 + 1, -11), list, random)) {
                return false;
            }

            worldgenendcitypieces_piece1 = WorldGenEndCityPieces.a(list, WorldGenEndCityPieces.a(definedstructuremanager, worldgenendcitypieces_piece1, new BlockPosition(4, b0, 0), "bridge_end", enumblockrotation.a(EnumBlockRotation.CLOCKWISE_180), true));
            worldgenendcitypieces_piece1.genDepth = -1;
            return true;
        }
    };
    static final List<Tuple<EnumBlockRotation, BlockPosition>> FAT_TOWER_BRIDGES = Lists.newArrayList(new Tuple[]{new Tuple<>(EnumBlockRotation.NONE, new BlockPosition(4, -1, 0)), new Tuple<>(EnumBlockRotation.CLOCKWISE_90, new BlockPosition(12, -1, 4)), new Tuple<>(EnumBlockRotation.COUNTERCLOCKWISE_90, new BlockPosition(0, -1, 8)), new Tuple<>(EnumBlockRotation.CLOCKWISE_180, new BlockPosition(8, -1, 12))});
    static final WorldGenEndCityPieces.PieceGenerator FAT_TOWER_GENERATOR = new WorldGenEndCityPieces.PieceGenerator() {
        @Override
        public void a() {}

        @Override
        public boolean a(DefinedStructureManager definedstructuremanager, int i, WorldGenEndCityPieces.Piece worldgenendcitypieces_piece, BlockPosition blockposition, List<StructurePiece> list, Random random) {
            EnumBlockRotation enumblockrotation = worldgenendcitypieces_piece.placeSettings.d();
            WorldGenEndCityPieces.Piece worldgenendcitypieces_piece1 = WorldGenEndCityPieces.a(list, WorldGenEndCityPieces.a(definedstructuremanager, worldgenendcitypieces_piece, new BlockPosition(-3, 4, -3), "fat_tower_base", enumblockrotation, true));

            worldgenendcitypieces_piece1 = WorldGenEndCityPieces.a(list, WorldGenEndCityPieces.a(definedstructuremanager, worldgenendcitypieces_piece1, new BlockPosition(0, 4, 0), "fat_tower_middle", enumblockrotation, true));

            for (int j = 0; j < 2 && random.nextInt(3) != 0; ++j) {
                worldgenendcitypieces_piece1 = WorldGenEndCityPieces.a(list, WorldGenEndCityPieces.a(definedstructuremanager, worldgenendcitypieces_piece1, new BlockPosition(0, 8, 0), "fat_tower_middle", enumblockrotation, true));
                Iterator iterator = WorldGenEndCityPieces.FAT_TOWER_BRIDGES.iterator();

                while (iterator.hasNext()) {
                    Tuple<EnumBlockRotation, BlockPosition> tuple = (Tuple) iterator.next();

                    if (random.nextBoolean()) {
                        WorldGenEndCityPieces.Piece worldgenendcitypieces_piece2 = WorldGenEndCityPieces.a(list, WorldGenEndCityPieces.a(definedstructuremanager, worldgenendcitypieces_piece1, (BlockPosition) tuple.b(), "bridge_end", enumblockrotation.a((EnumBlockRotation) tuple.a()), true));

                        WorldGenEndCityPieces.a(definedstructuremanager, WorldGenEndCityPieces.TOWER_BRIDGE_GENERATOR, i + 1, worldgenendcitypieces_piece2, (BlockPosition) null, list, random);
                    }
                }
            }

            WorldGenEndCityPieces.a(list, WorldGenEndCityPieces.a(definedstructuremanager, worldgenendcitypieces_piece1, new BlockPosition(-2, 8, -2), "fat_tower_top", enumblockrotation, true));
            return true;
        }
    };

    public WorldGenEndCityPieces() {}

    static WorldGenEndCityPieces.Piece a(DefinedStructureManager definedstructuremanager, WorldGenEndCityPieces.Piece worldgenendcitypieces_piece, BlockPosition blockposition, String s, EnumBlockRotation enumblockrotation, boolean flag) {
        WorldGenEndCityPieces.Piece worldgenendcitypieces_piece1 = new WorldGenEndCityPieces.Piece(definedstructuremanager, s, worldgenendcitypieces_piece.templatePosition, enumblockrotation, flag);
        BlockPosition blockposition1 = worldgenendcitypieces_piece.template.a(worldgenendcitypieces_piece.placeSettings, blockposition, worldgenendcitypieces_piece1.placeSettings, BlockPosition.ZERO);

        worldgenendcitypieces_piece1.a(blockposition1.getX(), blockposition1.getY(), blockposition1.getZ());
        return worldgenendcitypieces_piece1;
    }

    public static void a(DefinedStructureManager definedstructuremanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation, List<StructurePiece> list, Random random) {
        WorldGenEndCityPieces.FAT_TOWER_GENERATOR.a();
        WorldGenEndCityPieces.HOUSE_TOWER_GENERATOR.a();
        WorldGenEndCityPieces.TOWER_BRIDGE_GENERATOR.a();
        WorldGenEndCityPieces.TOWER_GENERATOR.a();
        WorldGenEndCityPieces.Piece worldgenendcitypieces_piece = a(list, new WorldGenEndCityPieces.Piece(definedstructuremanager, "base_floor", blockposition, enumblockrotation, true));

        worldgenendcitypieces_piece = a(list, a(definedstructuremanager, worldgenendcitypieces_piece, new BlockPosition(-1, 0, -1), "second_floor_1", enumblockrotation, false));
        worldgenendcitypieces_piece = a(list, a(definedstructuremanager, worldgenendcitypieces_piece, new BlockPosition(-1, 4, -1), "third_floor_1", enumblockrotation, false));
        worldgenendcitypieces_piece = a(list, a(definedstructuremanager, worldgenendcitypieces_piece, new BlockPosition(-1, 8, -1), "third_roof", enumblockrotation, true));
        a(definedstructuremanager, WorldGenEndCityPieces.TOWER_GENERATOR, 1, worldgenendcitypieces_piece, (BlockPosition) null, list, random);
    }

    static WorldGenEndCityPieces.Piece a(List<StructurePiece> list, WorldGenEndCityPieces.Piece worldgenendcitypieces_piece) {
        list.add(worldgenendcitypieces_piece);
        return worldgenendcitypieces_piece;
    }

    static boolean a(DefinedStructureManager definedstructuremanager, WorldGenEndCityPieces.PieceGenerator worldgenendcitypieces_piecegenerator, int i, WorldGenEndCityPieces.Piece worldgenendcitypieces_piece, BlockPosition blockposition, List<StructurePiece> list, Random random) {
        if (i > 8) {
            return false;
        } else {
            List<StructurePiece> list1 = Lists.newArrayList();

            if (worldgenendcitypieces_piecegenerator.a(definedstructuremanager, i, worldgenendcitypieces_piece, blockposition, list1, random)) {
                boolean flag = false;
                int j = random.nextInt();
                Iterator iterator = list1.iterator();

                while (iterator.hasNext()) {
                    StructurePiece structurepiece = (StructurePiece) iterator.next();

                    structurepiece.genDepth = j;
                    StructurePiece structurepiece1 = StructureStart.a(list, structurepiece.f());

                    if (structurepiece1 != null && structurepiece1.genDepth != worldgenendcitypieces_piece.genDepth) {
                        flag = true;
                        break;
                    }
                }

                if (!flag) {
                    list.addAll(list1);
                    return true;
                }
            }

            return false;
        }
    }

    public static class Piece extends DefinedStructurePiece {

        public Piece(DefinedStructureManager definedstructuremanager, String s, BlockPosition blockposition, EnumBlockRotation enumblockrotation, boolean flag) {
            super(WorldGenFeatureStructurePieceType.END_CITY_PIECE, 0, definedstructuremanager, a(s), s, a(flag, enumblockrotation), blockposition);
        }

        public Piece(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.END_CITY_PIECE, nbttagcompound, worldserver, (minecraftkey) -> {
                return a(nbttagcompound.getBoolean("OW"), EnumBlockRotation.valueOf(nbttagcompound.getString("Rot")));
            });
        }

        private static DefinedStructureInfo a(boolean flag, EnumBlockRotation enumblockrotation) {
            DefinedStructureProcessorBlockIgnore definedstructureprocessorblockignore = flag ? DefinedStructureProcessorBlockIgnore.STRUCTURE_BLOCK : DefinedStructureProcessorBlockIgnore.STRUCTURE_AND_AIR;

            return (new DefinedStructureInfo()).a(true).a((DefinedStructureProcessor) definedstructureprocessorblockignore).a(enumblockrotation);
        }

        @Override
        protected MinecraftKey a() {
            return a(this.templateName);
        }

        private static MinecraftKey a(String s) {
            return new MinecraftKey("end_city/" + s);
        }

        @Override
        protected void a(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super.a(worldserver, nbttagcompound);
            nbttagcompound.setString("Rot", this.placeSettings.d().name());
            nbttagcompound.setBoolean("OW", this.placeSettings.i().get(0) == DefinedStructureProcessorBlockIgnore.STRUCTURE_BLOCK);
        }

        @Override
        protected void a(String s, BlockPosition blockposition, WorldAccess worldaccess, Random random, StructureBoundingBox structureboundingbox) {
            if (s.startsWith("Chest")) {
                BlockPosition blockposition1 = blockposition.down();

                if (structureboundingbox.b((BaseBlockPosition) blockposition1)) {
                    TileEntityLootable.a((IBlockAccess) worldaccess, random, blockposition1, LootTables.END_CITY_TREASURE);
                }
            } else if (structureboundingbox.b((BaseBlockPosition) blockposition) && World.l(blockposition)) {
                if (s.startsWith("Sentry")) {
                    EntityShulker entityshulker = (EntityShulker) EntityTypes.SHULKER.a((World) worldaccess.getLevel());

                    entityshulker.setPosition((double) blockposition.getX() + 0.5D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.5D);
                    worldaccess.addEntity(entityshulker);
                } else if (s.startsWith("Elytra")) {
                    EntityItemFrame entityitemframe = new EntityItemFrame(worldaccess.getLevel(), blockposition, this.placeSettings.d().a(EnumDirection.SOUTH));

                    entityitemframe.setItem(new ItemStack(Items.ELYTRA), false);
                    worldaccess.addEntity(entityitemframe);
                }
            }

        }
    }

    private interface PieceGenerator {

        void a();

        boolean a(DefinedStructureManager definedstructuremanager, int i, WorldGenEndCityPieces.Piece worldgenendcitypieces_piece, BlockPosition blockposition, List<StructurePiece> list, Random random);
    }
}
