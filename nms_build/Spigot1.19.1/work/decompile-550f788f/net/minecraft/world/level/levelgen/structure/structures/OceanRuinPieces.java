package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.tags.TagsBlock;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.monster.EntityDrowned;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.BlockChest;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnumBlockMirror;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityChest;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.structure.DefinedStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.WorldGenFeatureStructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessorBlockIgnore;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessorRotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.LootTables;

public class OceanRuinPieces {

    private static final MinecraftKey[] WARM_RUINS = new MinecraftKey[]{new MinecraftKey("underwater_ruin/warm_1"), new MinecraftKey("underwater_ruin/warm_2"), new MinecraftKey("underwater_ruin/warm_3"), new MinecraftKey("underwater_ruin/warm_4"), new MinecraftKey("underwater_ruin/warm_5"), new MinecraftKey("underwater_ruin/warm_6"), new MinecraftKey("underwater_ruin/warm_7"), new MinecraftKey("underwater_ruin/warm_8")};
    private static final MinecraftKey[] RUINS_BRICK = new MinecraftKey[]{new MinecraftKey("underwater_ruin/brick_1"), new MinecraftKey("underwater_ruin/brick_2"), new MinecraftKey("underwater_ruin/brick_3"), new MinecraftKey("underwater_ruin/brick_4"), new MinecraftKey("underwater_ruin/brick_5"), new MinecraftKey("underwater_ruin/brick_6"), new MinecraftKey("underwater_ruin/brick_7"), new MinecraftKey("underwater_ruin/brick_8")};
    private static final MinecraftKey[] RUINS_CRACKED = new MinecraftKey[]{new MinecraftKey("underwater_ruin/cracked_1"), new MinecraftKey("underwater_ruin/cracked_2"), new MinecraftKey("underwater_ruin/cracked_3"), new MinecraftKey("underwater_ruin/cracked_4"), new MinecraftKey("underwater_ruin/cracked_5"), new MinecraftKey("underwater_ruin/cracked_6"), new MinecraftKey("underwater_ruin/cracked_7"), new MinecraftKey("underwater_ruin/cracked_8")};
    private static final MinecraftKey[] RUINS_MOSSY = new MinecraftKey[]{new MinecraftKey("underwater_ruin/mossy_1"), new MinecraftKey("underwater_ruin/mossy_2"), new MinecraftKey("underwater_ruin/mossy_3"), new MinecraftKey("underwater_ruin/mossy_4"), new MinecraftKey("underwater_ruin/mossy_5"), new MinecraftKey("underwater_ruin/mossy_6"), new MinecraftKey("underwater_ruin/mossy_7"), new MinecraftKey("underwater_ruin/mossy_8")};
    private static final MinecraftKey[] BIG_RUINS_BRICK = new MinecraftKey[]{new MinecraftKey("underwater_ruin/big_brick_1"), new MinecraftKey("underwater_ruin/big_brick_2"), new MinecraftKey("underwater_ruin/big_brick_3"), new MinecraftKey("underwater_ruin/big_brick_8")};
    private static final MinecraftKey[] BIG_RUINS_MOSSY = new MinecraftKey[]{new MinecraftKey("underwater_ruin/big_mossy_1"), new MinecraftKey("underwater_ruin/big_mossy_2"), new MinecraftKey("underwater_ruin/big_mossy_3"), new MinecraftKey("underwater_ruin/big_mossy_8")};
    private static final MinecraftKey[] BIG_RUINS_CRACKED = new MinecraftKey[]{new MinecraftKey("underwater_ruin/big_cracked_1"), new MinecraftKey("underwater_ruin/big_cracked_2"), new MinecraftKey("underwater_ruin/big_cracked_3"), new MinecraftKey("underwater_ruin/big_cracked_8")};
    private static final MinecraftKey[] BIG_WARM_RUINS = new MinecraftKey[]{new MinecraftKey("underwater_ruin/big_warm_4"), new MinecraftKey("underwater_ruin/big_warm_5"), new MinecraftKey("underwater_ruin/big_warm_6"), new MinecraftKey("underwater_ruin/big_warm_7")};

    public OceanRuinPieces() {}

    private static MinecraftKey getSmallWarmRuin(RandomSource randomsource) {
        return (MinecraftKey) SystemUtils.getRandom((Object[]) OceanRuinPieces.WARM_RUINS, randomsource);
    }

    private static MinecraftKey getBigWarmRuin(RandomSource randomsource) {
        return (MinecraftKey) SystemUtils.getRandom((Object[]) OceanRuinPieces.BIG_WARM_RUINS, randomsource);
    }

    public static void addPieces(StructureTemplateManager structuretemplatemanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation, StructurePieceAccessor structurepieceaccessor, RandomSource randomsource, OceanRuinStructure oceanruinstructure) {
        boolean flag = randomsource.nextFloat() <= oceanruinstructure.largeProbability;
        float f = flag ? 0.9F : 0.8F;

        addPiece(structuretemplatemanager, blockposition, enumblockrotation, structurepieceaccessor, randomsource, oceanruinstructure, flag, f);
        if (flag && randomsource.nextFloat() <= oceanruinstructure.clusterProbability) {
            addClusterRuins(structuretemplatemanager, randomsource, enumblockrotation, blockposition, oceanruinstructure, structurepieceaccessor);
        }

    }

    private static void addClusterRuins(StructureTemplateManager structuretemplatemanager, RandomSource randomsource, EnumBlockRotation enumblockrotation, BlockPosition blockposition, OceanRuinStructure oceanruinstructure, StructurePieceAccessor structurepieceaccessor) {
        BlockPosition blockposition1 = new BlockPosition(blockposition.getX(), 90, blockposition.getZ());
        BlockPosition blockposition2 = DefinedStructure.transform(new BlockPosition(15, 0, 15), EnumBlockMirror.NONE, enumblockrotation, BlockPosition.ZERO).offset(blockposition1);
        StructureBoundingBox structureboundingbox = StructureBoundingBox.fromCorners(blockposition1, blockposition2);
        BlockPosition blockposition3 = new BlockPosition(Math.min(blockposition1.getX(), blockposition2.getX()), blockposition1.getY(), Math.min(blockposition1.getZ(), blockposition2.getZ()));
        List<BlockPosition> list = allPositions(randomsource, blockposition3);
        int i = MathHelper.nextInt(randomsource, 4, 8);

        for (int j = 0; j < i; ++j) {
            if (!list.isEmpty()) {
                int k = randomsource.nextInt(list.size());
                BlockPosition blockposition4 = (BlockPosition) list.remove(k);
                EnumBlockRotation enumblockrotation1 = EnumBlockRotation.getRandom(randomsource);
                BlockPosition blockposition5 = DefinedStructure.transform(new BlockPosition(5, 0, 6), EnumBlockMirror.NONE, enumblockrotation1, BlockPosition.ZERO).offset(blockposition4);
                StructureBoundingBox structureboundingbox1 = StructureBoundingBox.fromCorners(blockposition4, blockposition5);

                if (!structureboundingbox1.intersects(structureboundingbox)) {
                    addPiece(structuretemplatemanager, blockposition4, enumblockrotation1, structurepieceaccessor, randomsource, oceanruinstructure, false, 0.8F);
                }
            }
        }

    }

    private static List<BlockPosition> allPositions(RandomSource randomsource, BlockPosition blockposition) {
        List<BlockPosition> list = Lists.newArrayList();

        list.add(blockposition.offset(-16 + MathHelper.nextInt(randomsource, 1, 8), 0, 16 + MathHelper.nextInt(randomsource, 1, 7)));
        list.add(blockposition.offset(-16 + MathHelper.nextInt(randomsource, 1, 8), 0, MathHelper.nextInt(randomsource, 1, 7)));
        list.add(blockposition.offset(-16 + MathHelper.nextInt(randomsource, 1, 8), 0, -16 + MathHelper.nextInt(randomsource, 4, 8)));
        list.add(blockposition.offset(MathHelper.nextInt(randomsource, 1, 7), 0, 16 + MathHelper.nextInt(randomsource, 1, 7)));
        list.add(blockposition.offset(MathHelper.nextInt(randomsource, 1, 7), 0, -16 + MathHelper.nextInt(randomsource, 4, 6)));
        list.add(blockposition.offset(16 + MathHelper.nextInt(randomsource, 1, 7), 0, 16 + MathHelper.nextInt(randomsource, 3, 8)));
        list.add(blockposition.offset(16 + MathHelper.nextInt(randomsource, 1, 7), 0, MathHelper.nextInt(randomsource, 1, 7)));
        list.add(blockposition.offset(16 + MathHelper.nextInt(randomsource, 1, 7), 0, -16 + MathHelper.nextInt(randomsource, 4, 8)));
        return list;
    }

    private static void addPiece(StructureTemplateManager structuretemplatemanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation, StructurePieceAccessor structurepieceaccessor, RandomSource randomsource, OceanRuinStructure oceanruinstructure, boolean flag, float f) {
        switch (oceanruinstructure.biomeTemp) {
            case WARM:
            default:
                MinecraftKey minecraftkey = flag ? getBigWarmRuin(randomsource) : getSmallWarmRuin(randomsource);

                structurepieceaccessor.addPiece(new OceanRuinPieces.a(structuretemplatemanager, minecraftkey, blockposition, enumblockrotation, f, oceanruinstructure.biomeTemp, flag));
                break;
            case COLD:
                MinecraftKey[] aminecraftkey = flag ? OceanRuinPieces.BIG_RUINS_BRICK : OceanRuinPieces.RUINS_BRICK;
                MinecraftKey[] aminecraftkey1 = flag ? OceanRuinPieces.BIG_RUINS_CRACKED : OceanRuinPieces.RUINS_CRACKED;
                MinecraftKey[] aminecraftkey2 = flag ? OceanRuinPieces.BIG_RUINS_MOSSY : OceanRuinPieces.RUINS_MOSSY;
                int i = randomsource.nextInt(aminecraftkey.length);

                structurepieceaccessor.addPiece(new OceanRuinPieces.a(structuretemplatemanager, aminecraftkey[i], blockposition, enumblockrotation, f, oceanruinstructure.biomeTemp, flag));
                structurepieceaccessor.addPiece(new OceanRuinPieces.a(structuretemplatemanager, aminecraftkey1[i], blockposition, enumblockrotation, 0.7F, oceanruinstructure.biomeTemp, flag));
                structurepieceaccessor.addPiece(new OceanRuinPieces.a(structuretemplatemanager, aminecraftkey2[i], blockposition, enumblockrotation, 0.5F, oceanruinstructure.biomeTemp, flag));
        }

    }

    public static class a extends DefinedStructurePiece {

        private final OceanRuinStructure.a biomeType;
        private final float integrity;
        private final boolean isLarge;

        public a(StructureTemplateManager structuretemplatemanager, MinecraftKey minecraftkey, BlockPosition blockposition, EnumBlockRotation enumblockrotation, float f, OceanRuinStructure.a oceanruinstructure_a, boolean flag) {
            super(WorldGenFeatureStructurePieceType.OCEAN_RUIN, 0, structuretemplatemanager, minecraftkey, minecraftkey.toString(), makeSettings(enumblockrotation), blockposition);
            this.integrity = f;
            this.biomeType = oceanruinstructure_a;
            this.isLarge = flag;
        }

        public a(StructureTemplateManager structuretemplatemanager, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.OCEAN_RUIN, nbttagcompound, structuretemplatemanager, (minecraftkey) -> {
                return makeSettings(EnumBlockRotation.valueOf(nbttagcompound.getString("Rot")));
            });
            this.integrity = nbttagcompound.getFloat("Integrity");
            this.biomeType = OceanRuinStructure.a.valueOf(nbttagcompound.getString("BiomeType"));
            this.isLarge = nbttagcompound.getBoolean("IsLarge");
        }

        private static DefinedStructureInfo makeSettings(EnumBlockRotation enumblockrotation) {
            return (new DefinedStructureInfo()).setRotation(enumblockrotation).setMirror(EnumBlockMirror.NONE).addProcessor(DefinedStructureProcessorBlockIgnore.STRUCTURE_AND_AIR);
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext structurepieceserializationcontext, NBTTagCompound nbttagcompound) {
            super.addAdditionalSaveData(structurepieceserializationcontext, nbttagcompound);
            nbttagcompound.putString("Rot", this.placeSettings.getRotation().name());
            nbttagcompound.putFloat("Integrity", this.integrity);
            nbttagcompound.putString("BiomeType", this.biomeType.toString());
            nbttagcompound.putBoolean("IsLarge", this.isLarge);
        }

        @Override
        protected void handleDataMarker(String s, BlockPosition blockposition, WorldAccess worldaccess, RandomSource randomsource, StructureBoundingBox structureboundingbox) {
            if ("chest".equals(s)) {
                worldaccess.setBlock(blockposition, (IBlockData) Blocks.CHEST.defaultBlockState().setValue(BlockChest.WATERLOGGED, worldaccess.getFluidState(blockposition).is(TagsFluid.WATER)), 2);
                TileEntity tileentity = worldaccess.getBlockEntity(blockposition);

                if (tileentity instanceof TileEntityChest) {
                    ((TileEntityChest) tileentity).setLootTable(this.isLarge ? LootTables.UNDERWATER_RUIN_BIG : LootTables.UNDERWATER_RUIN_SMALL, randomsource.nextLong());
                }
            } else if ("drowned".equals(s)) {
                EntityDrowned entitydrowned = (EntityDrowned) EntityTypes.DROWNED.create(worldaccess.getLevel());

                entitydrowned.setPersistenceRequired();
                entitydrowned.moveTo(blockposition, 0.0F, 0.0F);
                entitydrowned.finalizeSpawn(worldaccess, worldaccess.getCurrentDifficultyAt(blockposition), EnumMobSpawn.STRUCTURE, (GroupDataEntity) null, (NBTTagCompound) null);
                worldaccess.addFreshEntityWithPassengers(entitydrowned);
                if (blockposition.getY() > worldaccess.getSeaLevel()) {
                    worldaccess.setBlock(blockposition, Blocks.AIR.defaultBlockState(), 2);
                } else {
                    worldaccess.setBlock(blockposition, Blocks.WATER.defaultBlockState(), 2);
                }
            }

        }

        @Override
        public void postProcess(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, RandomSource randomsource, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            this.placeSettings.clearProcessors().addProcessor(new DefinedStructureProcessorRotation(this.integrity)).addProcessor(DefinedStructureProcessorBlockIgnore.STRUCTURE_AND_AIR);
            int i = generatoraccessseed.getHeight(HeightMap.Type.OCEAN_FLOOR_WG, this.templatePosition.getX(), this.templatePosition.getZ());

            this.templatePosition = new BlockPosition(this.templatePosition.getX(), i, this.templatePosition.getZ());
            BlockPosition blockposition1 = DefinedStructure.transform(new BlockPosition(this.template.getSize().getX() - 1, 0, this.template.getSize().getZ() - 1), EnumBlockMirror.NONE, this.placeSettings.getRotation(), BlockPosition.ZERO).offset(this.templatePosition);

            this.templatePosition = new BlockPosition(this.templatePosition.getX(), this.getHeight(this.templatePosition, generatoraccessseed, blockposition1), this.templatePosition.getZ());
            super.postProcess(generatoraccessseed, structuremanager, chunkgenerator, randomsource, structureboundingbox, chunkcoordintpair, blockposition);
        }

        private int getHeight(BlockPosition blockposition, IBlockAccess iblockaccess, BlockPosition blockposition1) {
            int i = blockposition.getY();
            int j = 512;
            int k = i - 1;
            int l = 0;
            Iterator iterator = BlockPosition.betweenClosed(blockposition, blockposition1).iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition2 = (BlockPosition) iterator.next();
                int i1 = blockposition2.getX();
                int j1 = blockposition2.getZ();
                int k1 = blockposition.getY() - 1;
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(i1, k1, j1);
                IBlockData iblockdata = iblockaccess.getBlockState(blockposition_mutableblockposition);

                for (Fluid fluid = iblockaccess.getFluidState(blockposition_mutableblockposition); (iblockdata.isAir() || fluid.is(TagsFluid.WATER) || iblockdata.is(TagsBlock.ICE)) && k1 > iblockaccess.getMinBuildHeight() + 1; fluid = iblockaccess.getFluidState(blockposition_mutableblockposition)) {
                    --k1;
                    blockposition_mutableblockposition.set(i1, k1, j1);
                    iblockdata = iblockaccess.getBlockState(blockposition_mutableblockposition);
                }

                j = Math.min(j, k1);
                if (k1 < k - 2) {
                    ++l;
                }
            }

            int l1 = Math.abs(blockposition.getX() - blockposition1.getX());

            if (k - j > 2 && l > l1 - 2) {
                i = j + 1;
            }

            return i;
        }
    }
}
