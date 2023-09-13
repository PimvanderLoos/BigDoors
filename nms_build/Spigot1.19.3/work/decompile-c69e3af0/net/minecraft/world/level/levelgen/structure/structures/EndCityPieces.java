package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.decoration.EntityItemFrame;
import net.minecraft.world.entity.monster.EntityShulker;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.entity.TileEntityLootable;
import net.minecraft.world.level.levelgen.structure.DefinedStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.WorldGenFeatureStructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessorBlockIgnore;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.loot.LootTables;

public class EndCityPieces {

    private static final int MAX_GEN_DEPTH = 8;
    static final EndCityPieces.b HOUSE_TOWER_GENERATOR = new EndCityPieces.b() {
        @Override
        public void init() {}

        @Override
        public boolean generate(StructureTemplateManager structuretemplatemanager, int i, EndCityPieces.a endcitypieces_a, BlockPosition blockposition, List<StructurePiece> list, RandomSource randomsource) {
            if (i > 8) {
                return false;
            } else {
                EnumBlockRotation enumblockrotation = endcitypieces_a.placeSettings().getRotation();
                EndCityPieces.a endcitypieces_a1 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structuretemplatemanager, endcitypieces_a, blockposition, "base_floor", enumblockrotation, true));
                int j = randomsource.nextInt(3);

                if (j == 0) {
                    EndCityPieces.addHelper(list, EndCityPieces.addPiece(structuretemplatemanager, endcitypieces_a1, new BlockPosition(-1, 4, -1), "base_roof", enumblockrotation, true));
                } else if (j == 1) {
                    endcitypieces_a1 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structuretemplatemanager, endcitypieces_a1, new BlockPosition(-1, 0, -1), "second_floor_2", enumblockrotation, false));
                    endcitypieces_a1 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structuretemplatemanager, endcitypieces_a1, new BlockPosition(-1, 8, -1), "second_roof", enumblockrotation, false));
                    EndCityPieces.recursiveChildren(structuretemplatemanager, EndCityPieces.TOWER_GENERATOR, i + 1, endcitypieces_a1, (BlockPosition) null, list, randomsource);
                } else if (j == 2) {
                    endcitypieces_a1 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structuretemplatemanager, endcitypieces_a1, new BlockPosition(-1, 0, -1), "second_floor_2", enumblockrotation, false));
                    endcitypieces_a1 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structuretemplatemanager, endcitypieces_a1, new BlockPosition(-1, 4, -1), "third_floor_2", enumblockrotation, false));
                    endcitypieces_a1 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structuretemplatemanager, endcitypieces_a1, new BlockPosition(-1, 8, -1), "third_roof", enumblockrotation, true));
                    EndCityPieces.recursiveChildren(structuretemplatemanager, EndCityPieces.TOWER_GENERATOR, i + 1, endcitypieces_a1, (BlockPosition) null, list, randomsource);
                }

                return true;
            }
        }
    };
    static final List<Tuple<EnumBlockRotation, BlockPosition>> TOWER_BRIDGES = Lists.newArrayList(new Tuple[]{new Tuple<>(EnumBlockRotation.NONE, new BlockPosition(1, -1, 0)), new Tuple<>(EnumBlockRotation.CLOCKWISE_90, new BlockPosition(6, -1, 1)), new Tuple<>(EnumBlockRotation.COUNTERCLOCKWISE_90, new BlockPosition(0, -1, 5)), new Tuple<>(EnumBlockRotation.CLOCKWISE_180, new BlockPosition(5, -1, 6))});
    static final EndCityPieces.b TOWER_GENERATOR = new EndCityPieces.b() {
        @Override
        public void init() {}

        @Override
        public boolean generate(StructureTemplateManager structuretemplatemanager, int i, EndCityPieces.a endcitypieces_a, BlockPosition blockposition, List<StructurePiece> list, RandomSource randomsource) {
            EnumBlockRotation enumblockrotation = endcitypieces_a.placeSettings().getRotation();
            EndCityPieces.a endcitypieces_a1 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structuretemplatemanager, endcitypieces_a, new BlockPosition(3 + randomsource.nextInt(2), -3, 3 + randomsource.nextInt(2)), "tower_base", enumblockrotation, true));

            endcitypieces_a1 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structuretemplatemanager, endcitypieces_a1, new BlockPosition(0, 7, 0), "tower_piece", enumblockrotation, true));
            EndCityPieces.a endcitypieces_a2 = randomsource.nextInt(3) == 0 ? endcitypieces_a1 : null;
            int j = 1 + randomsource.nextInt(3);

            for (int k = 0; k < j; ++k) {
                endcitypieces_a1 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structuretemplatemanager, endcitypieces_a1, new BlockPosition(0, 4, 0), "tower_piece", enumblockrotation, true));
                if (k < j - 1 && randomsource.nextBoolean()) {
                    endcitypieces_a2 = endcitypieces_a1;
                }
            }

            if (endcitypieces_a2 != null) {
                Iterator iterator = EndCityPieces.TOWER_BRIDGES.iterator();

                while (iterator.hasNext()) {
                    Tuple<EnumBlockRotation, BlockPosition> tuple = (Tuple) iterator.next();

                    if (randomsource.nextBoolean()) {
                        EndCityPieces.a endcitypieces_a3 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structuretemplatemanager, endcitypieces_a2, (BlockPosition) tuple.getB(), "bridge_end", enumblockrotation.getRotated((EnumBlockRotation) tuple.getA()), true));

                        EndCityPieces.recursiveChildren(structuretemplatemanager, EndCityPieces.TOWER_BRIDGE_GENERATOR, i + 1, endcitypieces_a3, (BlockPosition) null, list, randomsource);
                    }
                }

                EndCityPieces.addHelper(list, EndCityPieces.addPiece(structuretemplatemanager, endcitypieces_a1, new BlockPosition(-1, 4, -1), "tower_top", enumblockrotation, true));
            } else {
                if (i != 7) {
                    return EndCityPieces.recursiveChildren(structuretemplatemanager, EndCityPieces.FAT_TOWER_GENERATOR, i + 1, endcitypieces_a1, (BlockPosition) null, list, randomsource);
                }

                EndCityPieces.addHelper(list, EndCityPieces.addPiece(structuretemplatemanager, endcitypieces_a1, new BlockPosition(-1, 4, -1), "tower_top", enumblockrotation, true));
            }

            return true;
        }
    };
    static final EndCityPieces.b TOWER_BRIDGE_GENERATOR = new EndCityPieces.b() {
        public boolean shipCreated;

        @Override
        public void init() {
            this.shipCreated = false;
        }

        @Override
        public boolean generate(StructureTemplateManager structuretemplatemanager, int i, EndCityPieces.a endcitypieces_a, BlockPosition blockposition, List<StructurePiece> list, RandomSource randomsource) {
            EnumBlockRotation enumblockrotation = endcitypieces_a.placeSettings().getRotation();
            int j = randomsource.nextInt(4) + 1;
            EndCityPieces.a endcitypieces_a1 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structuretemplatemanager, endcitypieces_a, new BlockPosition(0, 0, -4), "bridge_piece", enumblockrotation, true));

            endcitypieces_a1.setGenDepth(-1);
            byte b0 = 0;

            for (int k = 0; k < j; ++k) {
                if (randomsource.nextBoolean()) {
                    endcitypieces_a1 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structuretemplatemanager, endcitypieces_a1, new BlockPosition(0, b0, -4), "bridge_piece", enumblockrotation, true));
                    b0 = 0;
                } else {
                    if (randomsource.nextBoolean()) {
                        endcitypieces_a1 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structuretemplatemanager, endcitypieces_a1, new BlockPosition(0, b0, -4), "bridge_steep_stairs", enumblockrotation, true));
                    } else {
                        endcitypieces_a1 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structuretemplatemanager, endcitypieces_a1, new BlockPosition(0, b0, -8), "bridge_gentle_stairs", enumblockrotation, true));
                    }

                    b0 = 4;
                }
            }

            if (!this.shipCreated && randomsource.nextInt(10 - i) == 0) {
                EndCityPieces.addHelper(list, EndCityPieces.addPiece(structuretemplatemanager, endcitypieces_a1, new BlockPosition(-8 + randomsource.nextInt(8), b0, -70 + randomsource.nextInt(10)), "ship", enumblockrotation, true));
                this.shipCreated = true;
            } else if (!EndCityPieces.recursiveChildren(structuretemplatemanager, EndCityPieces.HOUSE_TOWER_GENERATOR, i + 1, endcitypieces_a1, new BlockPosition(-3, b0 + 1, -11), list, randomsource)) {
                return false;
            }

            endcitypieces_a1 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structuretemplatemanager, endcitypieces_a1, new BlockPosition(4, b0, 0), "bridge_end", enumblockrotation.getRotated(EnumBlockRotation.CLOCKWISE_180), true));
            endcitypieces_a1.setGenDepth(-1);
            return true;
        }
    };
    static final List<Tuple<EnumBlockRotation, BlockPosition>> FAT_TOWER_BRIDGES = Lists.newArrayList(new Tuple[]{new Tuple<>(EnumBlockRotation.NONE, new BlockPosition(4, -1, 0)), new Tuple<>(EnumBlockRotation.CLOCKWISE_90, new BlockPosition(12, -1, 4)), new Tuple<>(EnumBlockRotation.COUNTERCLOCKWISE_90, new BlockPosition(0, -1, 8)), new Tuple<>(EnumBlockRotation.CLOCKWISE_180, new BlockPosition(8, -1, 12))});
    static final EndCityPieces.b FAT_TOWER_GENERATOR = new EndCityPieces.b() {
        @Override
        public void init() {}

        @Override
        public boolean generate(StructureTemplateManager structuretemplatemanager, int i, EndCityPieces.a endcitypieces_a, BlockPosition blockposition, List<StructurePiece> list, RandomSource randomsource) {
            EnumBlockRotation enumblockrotation = endcitypieces_a.placeSettings().getRotation();
            EndCityPieces.a endcitypieces_a1 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structuretemplatemanager, endcitypieces_a, new BlockPosition(-3, 4, -3), "fat_tower_base", enumblockrotation, true));

            endcitypieces_a1 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structuretemplatemanager, endcitypieces_a1, new BlockPosition(0, 4, 0), "fat_tower_middle", enumblockrotation, true));

            for (int j = 0; j < 2 && randomsource.nextInt(3) != 0; ++j) {
                endcitypieces_a1 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structuretemplatemanager, endcitypieces_a1, new BlockPosition(0, 8, 0), "fat_tower_middle", enumblockrotation, true));
                Iterator iterator = EndCityPieces.FAT_TOWER_BRIDGES.iterator();

                while (iterator.hasNext()) {
                    Tuple<EnumBlockRotation, BlockPosition> tuple = (Tuple) iterator.next();

                    if (randomsource.nextBoolean()) {
                        EndCityPieces.a endcitypieces_a2 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structuretemplatemanager, endcitypieces_a1, (BlockPosition) tuple.getB(), "bridge_end", enumblockrotation.getRotated((EnumBlockRotation) tuple.getA()), true));

                        EndCityPieces.recursiveChildren(structuretemplatemanager, EndCityPieces.TOWER_BRIDGE_GENERATOR, i + 1, endcitypieces_a2, (BlockPosition) null, list, randomsource);
                    }
                }
            }

            EndCityPieces.addHelper(list, EndCityPieces.addPiece(structuretemplatemanager, endcitypieces_a1, new BlockPosition(-2, 8, -2), "fat_tower_top", enumblockrotation, true));
            return true;
        }
    };

    public EndCityPieces() {}

    static EndCityPieces.a addPiece(StructureTemplateManager structuretemplatemanager, EndCityPieces.a endcitypieces_a, BlockPosition blockposition, String s, EnumBlockRotation enumblockrotation, boolean flag) {
        EndCityPieces.a endcitypieces_a1 = new EndCityPieces.a(structuretemplatemanager, s, endcitypieces_a.templatePosition(), enumblockrotation, flag);
        BlockPosition blockposition1 = endcitypieces_a.template().calculateConnectedPosition(endcitypieces_a.placeSettings(), blockposition, endcitypieces_a1.placeSettings(), BlockPosition.ZERO);

        endcitypieces_a1.move(blockposition1.getX(), blockposition1.getY(), blockposition1.getZ());
        return endcitypieces_a1;
    }

    public static void startHouseTower(StructureTemplateManager structuretemplatemanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation, List<StructurePiece> list, RandomSource randomsource) {
        EndCityPieces.FAT_TOWER_GENERATOR.init();
        EndCityPieces.HOUSE_TOWER_GENERATOR.init();
        EndCityPieces.TOWER_BRIDGE_GENERATOR.init();
        EndCityPieces.TOWER_GENERATOR.init();
        EndCityPieces.a endcitypieces_a = addHelper(list, new EndCityPieces.a(structuretemplatemanager, "base_floor", blockposition, enumblockrotation, true));

        endcitypieces_a = addHelper(list, addPiece(structuretemplatemanager, endcitypieces_a, new BlockPosition(-1, 0, -1), "second_floor_1", enumblockrotation, false));
        endcitypieces_a = addHelper(list, addPiece(structuretemplatemanager, endcitypieces_a, new BlockPosition(-1, 4, -1), "third_floor_1", enumblockrotation, false));
        endcitypieces_a = addHelper(list, addPiece(structuretemplatemanager, endcitypieces_a, new BlockPosition(-1, 8, -1), "third_roof", enumblockrotation, true));
        recursiveChildren(structuretemplatemanager, EndCityPieces.TOWER_GENERATOR, 1, endcitypieces_a, (BlockPosition) null, list, randomsource);
    }

    static EndCityPieces.a addHelper(List<StructurePiece> list, EndCityPieces.a endcitypieces_a) {
        list.add(endcitypieces_a);
        return endcitypieces_a;
    }

    static boolean recursiveChildren(StructureTemplateManager structuretemplatemanager, EndCityPieces.b endcitypieces_b, int i, EndCityPieces.a endcitypieces_a, BlockPosition blockposition, List<StructurePiece> list, RandomSource randomsource) {
        if (i > 8) {
            return false;
        } else {
            List<StructurePiece> list1 = Lists.newArrayList();

            if (endcitypieces_b.generate(structuretemplatemanager, i, endcitypieces_a, blockposition, list1, randomsource)) {
                boolean flag = false;
                int j = randomsource.nextInt();
                Iterator iterator = list1.iterator();

                while (iterator.hasNext()) {
                    StructurePiece structurepiece = (StructurePiece) iterator.next();

                    structurepiece.setGenDepth(j);
                    StructurePiece structurepiece1 = StructurePiece.findCollisionPiece(list, structurepiece.getBoundingBox());

                    if (structurepiece1 != null && structurepiece1.getGenDepth() != endcitypieces_a.getGenDepth()) {
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

    public static class a extends DefinedStructurePiece {

        public a(StructureTemplateManager structuretemplatemanager, String s, BlockPosition blockposition, EnumBlockRotation enumblockrotation, boolean flag) {
            super(WorldGenFeatureStructurePieceType.END_CITY_PIECE, 0, structuretemplatemanager, makeResourceLocation(s), s, makeSettings(flag, enumblockrotation), blockposition);
        }

        public a(StructureTemplateManager structuretemplatemanager, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.END_CITY_PIECE, nbttagcompound, structuretemplatemanager, (minecraftkey) -> {
                return makeSettings(nbttagcompound.getBoolean("OW"), EnumBlockRotation.valueOf(nbttagcompound.getString("Rot")));
            });
        }

        private static DefinedStructureInfo makeSettings(boolean flag, EnumBlockRotation enumblockrotation) {
            DefinedStructureProcessorBlockIgnore definedstructureprocessorblockignore = flag ? DefinedStructureProcessorBlockIgnore.STRUCTURE_BLOCK : DefinedStructureProcessorBlockIgnore.STRUCTURE_AND_AIR;

            return (new DefinedStructureInfo()).setIgnoreEntities(true).addProcessor(definedstructureprocessorblockignore).setRotation(enumblockrotation);
        }

        @Override
        protected MinecraftKey makeTemplateLocation() {
            return makeResourceLocation(this.templateName);
        }

        private static MinecraftKey makeResourceLocation(String s) {
            return new MinecraftKey("end_city/" + s);
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound) {
            super.addAdditionalSaveData(structurepieceserializationcontext, nbttagcompound);
            nbttagcompound.putString("Rot", this.placeSettings.getRotation().name());
            nbttagcompound.putBoolean("OW", this.placeSettings.getProcessors().get(0) == DefinedStructureProcessorBlockIgnore.STRUCTURE_BLOCK);
        }

        @Override
        protected void handleDataMarker(String s, BlockPosition blockposition, WorldAccess worldaccess, RandomSource randomsource, StructureBoundingBox structureboundingbox) {
            if (s.startsWith("Chest")) {
                BlockPosition blockposition1 = blockposition.below();

                if (structureboundingbox.isInside(blockposition1)) {
                    TileEntityLootable.setLootTable(worldaccess, randomsource, blockposition1, LootTables.END_CITY_TREASURE);
                }
            } else if (structureboundingbox.isInside(blockposition) && World.isInSpawnableBounds(blockposition)) {
                if (s.startsWith("Sentry")) {
                    EntityShulker entityshulker = (EntityShulker) EntityTypes.SHULKER.create(worldaccess.getLevel());

                    if (entityshulker != null) {
                        entityshulker.setPos((double) blockposition.getX() + 0.5D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.5D);
                        worldaccess.addFreshEntity(entityshulker);
                    }
                } else if (s.startsWith("Elytra")) {
                    EntityItemFrame entityitemframe = new EntityItemFrame(worldaccess.getLevel(), blockposition, this.placeSettings.getRotation().rotate(EnumDirection.SOUTH));

                    entityitemframe.setItem(new ItemStack(Items.ELYTRA), false);
                    worldaccess.addFreshEntity(entityitemframe);
                }
            }

        }
    }

    private interface b {

        void init();

        boolean generate(StructureTemplateManager structuretemplatemanager, int i, EndCityPieces.a endcitypieces_a, BlockPosition blockposition, List<StructurePiece> list, RandomSource randomsource);
    }
}
