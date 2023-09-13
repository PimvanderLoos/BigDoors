package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutTileEntityData;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.INamable;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockJigsaw;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.structures.WorldGenFeatureDefinedStructureJigsawPlacement;
import net.minecraft.world.level.levelgen.feature.structures.WorldGenFeatureDefinedStructurePoolSingle;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.WorldGenFeaturePillagerOutpostPoolPiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;

public class TileEntityJigsaw extends TileEntity {

    public static final String TARGET = "target";
    public static final String POOL = "pool";
    public static final String JOINT = "joint";
    public static final String NAME = "name";
    public static final String FINAL_STATE = "final_state";
    private MinecraftKey name = new MinecraftKey("empty");
    private MinecraftKey target = new MinecraftKey("empty");
    private MinecraftKey pool = new MinecraftKey("empty");
    private TileEntityJigsaw.JointType joint;
    private String finalState;

    public TileEntityJigsaw(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.JIGSAW, blockposition, iblockdata);
        this.joint = TileEntityJigsaw.JointType.ROLLABLE;
        this.finalState = "minecraft:air";
    }

    public MinecraftKey getName() {
        return this.name;
    }

    public MinecraftKey getTarget() {
        return this.target;
    }

    public MinecraftKey getPool() {
        return this.pool;
    }

    public String getFinalState() {
        return this.finalState;
    }

    public TileEntityJigsaw.JointType getJoint() {
        return this.joint;
    }

    public void setName(MinecraftKey minecraftkey) {
        this.name = minecraftkey;
    }

    public void setTarget(MinecraftKey minecraftkey) {
        this.target = minecraftkey;
    }

    public void setPool(MinecraftKey minecraftkey) {
        this.pool = minecraftkey;
    }

    public void setFinalState(String s) {
        this.finalState = s;
    }

    public void setJoint(TileEntityJigsaw.JointType tileentityjigsaw_jointtype) {
        this.joint = tileentityjigsaw_jointtype;
    }

    @Override
    protected void saveAdditional(NBTTagCompound nbttagcompound) {
        super.saveAdditional(nbttagcompound);
        nbttagcompound.putString("name", this.name.toString());
        nbttagcompound.putString("target", this.target.toString());
        nbttagcompound.putString("pool", this.pool.toString());
        nbttagcompound.putString("final_state", this.finalState);
        nbttagcompound.putString("joint", this.joint.getSerializedName());
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.name = new MinecraftKey(nbttagcompound.getString("name"));
        this.target = new MinecraftKey(nbttagcompound.getString("target"));
        this.pool = new MinecraftKey(nbttagcompound.getString("pool"));
        this.finalState = nbttagcompound.getString("final_state");
        this.joint = (TileEntityJigsaw.JointType) TileEntityJigsaw.JointType.byName(nbttagcompound.getString("joint")).orElseGet(() -> {
            return BlockJigsaw.getFrontFacing(this.getBlockState()).getAxis().isHorizontal() ? TileEntityJigsaw.JointType.ALIGNED : TileEntityJigsaw.JointType.ROLLABLE;
        });
    }

    @Override
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return PacketPlayOutTileEntityData.create(this);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public void generate(WorldServer worldserver, int i, boolean flag) {
        ChunkGenerator chunkgenerator = worldserver.getChunkSource().getGenerator();
        DefinedStructureManager definedstructuremanager = worldserver.getStructureManager();
        StructureManager structuremanager = worldserver.structureFeatureManager();
        Random random = worldserver.getRandom();
        BlockPosition blockposition = this.getBlockPos();
        List<WorldGenFeaturePillagerOutpostPoolPiece> list = Lists.newArrayList();
        DefinedStructure definedstructure = new DefinedStructure();

        definedstructure.fillFromWorld(worldserver, blockposition, new BaseBlockPosition(1, 1, 1), false, (Block) null);
        WorldGenFeatureDefinedStructurePoolSingle worldgenfeaturedefinedstructurepoolsingle = new WorldGenFeatureDefinedStructurePoolSingle(definedstructure);
        WorldGenFeaturePillagerOutpostPoolPiece worldgenfeaturepillageroutpostpoolpiece = new WorldGenFeaturePillagerOutpostPoolPiece(definedstructuremanager, worldgenfeaturedefinedstructurepoolsingle, blockposition, 1, EnumBlockRotation.NONE, new StructureBoundingBox(blockposition));

        WorldGenFeatureDefinedStructureJigsawPlacement.addPieces(worldserver.registryAccess(), worldgenfeaturepillageroutpostpoolpiece, i, WorldGenFeaturePillagerOutpostPoolPiece::new, chunkgenerator, definedstructuremanager, list, random, worldserver);
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            WorldGenFeaturePillagerOutpostPoolPiece worldgenfeaturepillageroutpostpoolpiece1 = (WorldGenFeaturePillagerOutpostPoolPiece) iterator.next();

            worldgenfeaturepillageroutpostpoolpiece1.place(worldserver, structuremanager, chunkgenerator, random, StructureBoundingBox.infinite(), blockposition, flag);
        }

    }

    public static enum JointType implements INamable {

        ROLLABLE("rollable"), ALIGNED("aligned");

        private final String name;

        private JointType(String s) {
            this.name = s;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public static Optional<TileEntityJigsaw.JointType> byName(String s) {
            return Arrays.stream(values()).filter((tileentityjigsaw_jointtype) -> {
                return tileentityjigsaw_jointtype.getSerializedName().equals(s);
            }).findFirst();
        }

        public IChatBaseComponent getTranslatedName() {
            return new ChatMessage("jigsaw_block.joint." + this.name);
        }
    }
}
