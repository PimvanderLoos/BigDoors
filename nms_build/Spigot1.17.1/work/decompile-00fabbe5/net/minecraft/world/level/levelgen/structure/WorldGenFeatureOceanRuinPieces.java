package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.monster.EntityDrowned;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.World;
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
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureStructurePieceType;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureOceanRuinConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessorBlockIgnore;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessorRotation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.LootTables;

public class WorldGenFeatureOceanRuinPieces {

    private static final MinecraftKey[] WARM_RUINS = new MinecraftKey[]{new MinecraftKey("underwater_ruin/warm_1"), new MinecraftKey("underwater_ruin/warm_2"), new MinecraftKey("underwater_ruin/warm_3"), new MinecraftKey("underwater_ruin/warm_4"), new MinecraftKey("underwater_ruin/warm_5"), new MinecraftKey("underwater_ruin/warm_6"), new MinecraftKey("underwater_ruin/warm_7"), new MinecraftKey("underwater_ruin/warm_8")};
    private static final MinecraftKey[] RUINS_BRICK = new MinecraftKey[]{new MinecraftKey("underwater_ruin/brick_1"), new MinecraftKey("underwater_ruin/brick_2"), new MinecraftKey("underwater_ruin/brick_3"), new MinecraftKey("underwater_ruin/brick_4"), new MinecraftKey("underwater_ruin/brick_5"), new MinecraftKey("underwater_ruin/brick_6"), new MinecraftKey("underwater_ruin/brick_7"), new MinecraftKey("underwater_ruin/brick_8")};
    private static final MinecraftKey[] RUINS_CRACKED = new MinecraftKey[]{new MinecraftKey("underwater_ruin/cracked_1"), new MinecraftKey("underwater_ruin/cracked_2"), new MinecraftKey("underwater_ruin/cracked_3"), new MinecraftKey("underwater_ruin/cracked_4"), new MinecraftKey("underwater_ruin/cracked_5"), new MinecraftKey("underwater_ruin/cracked_6"), new MinecraftKey("underwater_ruin/cracked_7"), new MinecraftKey("underwater_ruin/cracked_8")};
    private static final MinecraftKey[] RUINS_MOSSY = new MinecraftKey[]{new MinecraftKey("underwater_ruin/mossy_1"), new MinecraftKey("underwater_ruin/mossy_2"), new MinecraftKey("underwater_ruin/mossy_3"), new MinecraftKey("underwater_ruin/mossy_4"), new MinecraftKey("underwater_ruin/mossy_5"), new MinecraftKey("underwater_ruin/mossy_6"), new MinecraftKey("underwater_ruin/mossy_7"), new MinecraftKey("underwater_ruin/mossy_8")};
    private static final MinecraftKey[] BIG_RUINS_BRICK = new MinecraftKey[]{new MinecraftKey("underwater_ruin/big_brick_1"), new MinecraftKey("underwater_ruin/big_brick_2"), new MinecraftKey("underwater_ruin/big_brick_3"), new MinecraftKey("underwater_ruin/big_brick_8")};
    private static final MinecraftKey[] BIG_RUINS_MOSSY = new MinecraftKey[]{new MinecraftKey("underwater_ruin/big_mossy_1"), new MinecraftKey("underwater_ruin/big_mossy_2"), new MinecraftKey("underwater_ruin/big_mossy_3"), new MinecraftKey("underwater_ruin/big_mossy_8")};
    private static final MinecraftKey[] BIG_RUINS_CRACKED = new MinecraftKey[]{new MinecraftKey("underwater_ruin/big_cracked_1"), new MinecraftKey("underwater_ruin/big_cracked_2"), new MinecraftKey("underwater_ruin/big_cracked_3"), new MinecraftKey("underwater_ruin/big_cracked_8")};
    private static final MinecraftKey[] BIG_WARM_RUINS = new MinecraftKey[]{new MinecraftKey("underwater_ruin/big_warm_4"), new MinecraftKey("underwater_ruin/big_warm_5"), new MinecraftKey("underwater_ruin/big_warm_6"), new MinecraftKey("underwater_ruin/big_warm_7")};

    public WorldGenFeatureOceanRuinPieces() {}

    private static MinecraftKey a(Random random) {
        return (MinecraftKey) SystemUtils.a((Object[]) WorldGenFeatureOceanRuinPieces.WARM_RUINS, random);
    }

    private static MinecraftKey b(Random random) {
        return (MinecraftKey) SystemUtils.a((Object[]) WorldGenFeatureOceanRuinPieces.BIG_WARM_RUINS, random);
    }

    public static void a(DefinedStructureManager definedstructuremanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation, StructurePieceAccessor structurepieceaccessor, Random random, WorldGenFeatureOceanRuinConfiguration worldgenfeatureoceanruinconfiguration) {
        boolean flag = random.nextFloat() <= worldgenfeatureoceanruinconfiguration.largeProbability;
        float f = flag ? 0.9F : 0.8F;

        a(definedstructuremanager, blockposition, enumblockrotation, structurepieceaccessor, random, worldgenfeatureoceanruinconfiguration, flag, f);
        if (flag && random.nextFloat() <= worldgenfeatureoceanruinconfiguration.clusterProbability) {
            a(definedstructuremanager, random, enumblockrotation, blockposition, worldgenfeatureoceanruinconfiguration, structurepieceaccessor);
        }

    }

    private static void a(DefinedStructureManager definedstructuremanager, Random random, EnumBlockRotation enumblockrotation, BlockPosition blockposition, WorldGenFeatureOceanRuinConfiguration worldgenfeatureoceanruinconfiguration, StructurePieceAccessor structurepieceaccessor) {
        BlockPosition blockposition1 = new BlockPosition(blockposition.getX(), 90, blockposition.getZ());
        BlockPosition blockposition2 = DefinedStructure.a(new BlockPosition(15, 0, 15), EnumBlockMirror.NONE, enumblockrotation, BlockPosition.ZERO).f(blockposition1);
        StructureBoundingBox structureboundingbox = StructureBoundingBox.a(blockposition1, blockposition2);
        BlockPosition blockposition3 = new BlockPosition(Math.min(blockposition1.getX(), blockposition2.getX()), blockposition1.getY(), Math.min(blockposition1.getZ(), blockposition2.getZ()));
        List<BlockPosition> list = a(random, blockposition3);
        int i = MathHelper.nextInt(random, 4, 8);

        for (int j = 0; j < i; ++j) {
            if (!list.isEmpty()) {
                int k = random.nextInt(list.size());
                BlockPosition blockposition4 = (BlockPosition) list.remove(k);
                EnumBlockRotation enumblockrotation1 = EnumBlockRotation.a(random);
                BlockPosition blockposition5 = DefinedStructure.a(new BlockPosition(5, 0, 6), EnumBlockMirror.NONE, enumblockrotation1, BlockPosition.ZERO).f(blockposition4);
                StructureBoundingBox structureboundingbox1 = StructureBoundingBox.a(blockposition4, blockposition5);

                if (!structureboundingbox1.a(structureboundingbox)) {
                    a(definedstructuremanager, blockposition4, enumblockrotation1, structurepieceaccessor, random, worldgenfeatureoceanruinconfiguration, false, 0.8F);
                }
            }
        }

    }

    private static List<BlockPosition> a(Random random, BlockPosition blockposition) {
        List<BlockPosition> list = Lists.newArrayList();

        list.add(blockposition.c(-16 + MathHelper.nextInt(random, 1, 8), 0, 16 + MathHelper.nextInt(random, 1, 7)));
        list.add(blockposition.c(-16 + MathHelper.nextInt(random, 1, 8), 0, MathHelper.nextInt(random, 1, 7)));
        list.add(blockposition.c(-16 + MathHelper.nextInt(random, 1, 8), 0, -16 + MathHelper.nextInt(random, 4, 8)));
        list.add(blockposition.c(MathHelper.nextInt(random, 1, 7), 0, 16 + MathHelper.nextInt(random, 1, 7)));
        list.add(blockposition.c(MathHelper.nextInt(random, 1, 7), 0, -16 + MathHelper.nextInt(random, 4, 6)));
        list.add(blockposition.c(16 + MathHelper.nextInt(random, 1, 7), 0, 16 + MathHelper.nextInt(random, 3, 8)));
        list.add(blockposition.c(16 + MathHelper.nextInt(random, 1, 7), 0, MathHelper.nextInt(random, 1, 7)));
        list.add(blockposition.c(16 + MathHelper.nextInt(random, 1, 7), 0, -16 + MathHelper.nextInt(random, 4, 8)));
        return list;
    }

    private static void a(DefinedStructureManager definedstructuremanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation, StructurePieceAccessor structurepieceaccessor, Random random, WorldGenFeatureOceanRuinConfiguration worldgenfeatureoceanruinconfiguration, boolean flag, float f) {
        switch (worldgenfeatureoceanruinconfiguration.biomeTemp) {
            case WARM:
            default:
                MinecraftKey minecraftkey = flag ? b(random) : a(random);

                structurepieceaccessor.a((StructurePiece) (new WorldGenFeatureOceanRuinPieces.a(definedstructuremanager, minecraftkey, blockposition, enumblockrotation, f, worldgenfeatureoceanruinconfiguration.biomeTemp, flag)));
                break;
            case COLD:
                MinecraftKey[] aminecraftkey = flag ? WorldGenFeatureOceanRuinPieces.BIG_RUINS_BRICK : WorldGenFeatureOceanRuinPieces.RUINS_BRICK;
                MinecraftKey[] aminecraftkey1 = flag ? WorldGenFeatureOceanRuinPieces.BIG_RUINS_CRACKED : WorldGenFeatureOceanRuinPieces.RUINS_CRACKED;
                MinecraftKey[] aminecraftkey2 = flag ? WorldGenFeatureOceanRuinPieces.BIG_RUINS_MOSSY : WorldGenFeatureOceanRuinPieces.RUINS_MOSSY;
                int i = random.nextInt(aminecraftkey.length);

                structurepieceaccessor.a((StructurePiece) (new WorldGenFeatureOceanRuinPieces.a(definedstructuremanager, aminecraftkey[i], blockposition, enumblockrotation, f, worldgenfeatureoceanruinconfiguration.biomeTemp, flag)));
                structurepieceaccessor.a((StructurePiece) (new WorldGenFeatureOceanRuinPieces.a(definedstructuremanager, aminecraftkey1[i], blockposition, enumblockrotation, 0.7F, worldgenfeatureoceanruinconfiguration.biomeTemp, flag)));
                structurepieceaccessor.a((StructurePiece) (new WorldGenFeatureOceanRuinPieces.a(definedstructuremanager, aminecraftkey2[i], blockposition, enumblockrotation, 0.5F, worldgenfeatureoceanruinconfiguration.biomeTemp, flag)));
        }

    }

    public static class a extends DefinedStructurePiece {

        private final WorldGenFeatureOceanRuin.Temperature biomeType;
        private final float integrity;
        private final boolean isLarge;

        public a(DefinedStructureManager definedstructuremanager, MinecraftKey minecraftkey, BlockPosition blockposition, EnumBlockRotation enumblockrotation, float f, WorldGenFeatureOceanRuin.Temperature worldgenfeatureoceanruin_temperature, boolean flag) {
            super(WorldGenFeatureStructurePieceType.OCEAN_RUIN, 0, definedstructuremanager, minecraftkey, minecraftkey.toString(), a(enumblockrotation), blockposition);
            this.integrity = f;
            this.biomeType = worldgenfeatureoceanruin_temperature;
            this.isLarge = flag;
        }

        public a(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super(WorldGenFeatureStructurePieceType.OCEAN_RUIN, nbttagcompound, worldserver, (minecraftkey) -> {
                return a(EnumBlockRotation.valueOf(nbttagcompound.getString("Rot")));
            });
            this.integrity = nbttagcompound.getFloat("Integrity");
            this.biomeType = WorldGenFeatureOceanRuin.Temperature.valueOf(nbttagcompound.getString("BiomeType"));
            this.isLarge = nbttagcompound.getBoolean("IsLarge");
        }

        private static DefinedStructureInfo a(EnumBlockRotation enumblockrotation) {
            return (new DefinedStructureInfo()).a(enumblockrotation).a(EnumBlockMirror.NONE).a((DefinedStructureProcessor) DefinedStructureProcessorBlockIgnore.STRUCTURE_AND_AIR);
        }

        @Override
        protected void a(WorldServer worldserver, NBTTagCompound nbttagcompound) {
            super.a(worldserver, nbttagcompound);
            nbttagcompound.setString("Rot", this.placeSettings.d().name());
            nbttagcompound.setFloat("Integrity", this.integrity);
            nbttagcompound.setString("BiomeType", this.biomeType.toString());
            nbttagcompound.setBoolean("IsLarge", this.isLarge);
        }

        @Override
        protected void a(String s, BlockPosition blockposition, WorldAccess worldaccess, Random random, StructureBoundingBox structureboundingbox) {
            if ("chest".equals(s)) {
                worldaccess.setTypeAndData(blockposition, (IBlockData) Blocks.CHEST.getBlockData().set(BlockChest.WATERLOGGED, worldaccess.getFluid(blockposition).a((Tag) TagsFluid.WATER)), 2);
                TileEntity tileentity = worldaccess.getTileEntity(blockposition);

                if (tileentity instanceof TileEntityChest) {
                    ((TileEntityChest) tileentity).setLootTable(this.isLarge ? LootTables.UNDERWATER_RUIN_BIG : LootTables.UNDERWATER_RUIN_SMALL, random.nextLong());
                }
            } else if ("drowned".equals(s)) {
                EntityDrowned entitydrowned = (EntityDrowned) EntityTypes.DROWNED.a((World) worldaccess.getLevel());

                entitydrowned.setPersistent();
                entitydrowned.setPositionRotation(blockposition, 0.0F, 0.0F);
                entitydrowned.prepare(worldaccess, worldaccess.getDamageScaler(blockposition), EnumMobSpawn.STRUCTURE, (GroupDataEntity) null, (NBTTagCompound) null);
                worldaccess.addAllEntities(entitydrowned);
                if (blockposition.getY() > worldaccess.getSeaLevel()) {
                    worldaccess.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 2);
                } else {
                    worldaccess.setTypeAndData(blockposition, Blocks.WATER.getBlockData(), 2);
                }
            }

        }

        @Override
        public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair, BlockPosition blockposition) {
            this.placeSettings.b().a((DefinedStructureProcessor) (new DefinedStructureProcessorRotation(this.integrity))).a((DefinedStructureProcessor) DefinedStructureProcessorBlockIgnore.STRUCTURE_AND_AIR);
            int i = generatoraccessseed.a(HeightMap.Type.OCEAN_FLOOR_WG, this.templatePosition.getX(), this.templatePosition.getZ());

            this.templatePosition = new BlockPosition(this.templatePosition.getX(), i, this.templatePosition.getZ());
            BlockPosition blockposition1 = DefinedStructure.a(new BlockPosition(this.template.a().getX() - 1, 0, this.template.a().getZ() - 1), EnumBlockMirror.NONE, this.placeSettings.d(), BlockPosition.ZERO).f(this.templatePosition);

            this.templatePosition = new BlockPosition(this.templatePosition.getX(), this.a(this.templatePosition, (IBlockAccess) generatoraccessseed, blockposition1), this.templatePosition.getZ());
            return super.a(generatoraccessseed, structuremanager, chunkgenerator, random, structureboundingbox, chunkcoordintpair, blockposition);
        }

        private int a(BlockPosition blockposition, IBlockAccess iblockaccess, BlockPosition blockposition1) {
            int i = blockposition.getY();
            int j = 512;
            int k = i - 1;
            int l = 0;
            Iterator iterator = BlockPosition.a(blockposition, blockposition1).iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition2 = (BlockPosition) iterator.next();
                int i1 = blockposition2.getX();
                int j1 = blockposition2.getZ();
                int k1 = blockposition.getY() - 1;
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(i1, k1, j1);
                IBlockData iblockdata = iblockaccess.getType(blockposition_mutableblockposition);

                for (Fluid fluid = iblockaccess.getFluid(blockposition_mutableblockposition); (iblockdata.isAir() || fluid.a((Tag) TagsFluid.WATER) || iblockdata.a((Tag) TagsBlock.ICE)) && k1 > iblockaccess.getMinBuildHeight() + 1; fluid = iblockaccess.getFluid(blockposition_mutableblockposition)) {
                    --k1;
                    blockposition_mutableblockposition.d(i1, k1, j1);
                    iblockdata = iblockaccess.getType(blockposition_mutableblockposition);
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
