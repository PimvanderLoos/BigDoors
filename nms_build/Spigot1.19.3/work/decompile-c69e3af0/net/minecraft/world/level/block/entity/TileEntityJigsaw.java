package net.minecraft.world.level.block.entity;

import java.util.Arrays;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.BlockPropertyJigsawOrientation;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutTileEntityData;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.INamable;
import net.minecraft.world.level.block.BlockJigsaw;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.structure.pools.WorldGenFeatureDefinedStructureJigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.WorldGenFeatureDefinedStructurePoolTemplate;

public class TileEntityJigsaw extends TileEntity {

    public static final String TARGET = "target";
    public static final String POOL = "pool";
    public static final String JOINT = "joint";
    public static final String NAME = "name";
    public static final String FINAL_STATE = "final_state";
    private MinecraftKey name = new MinecraftKey("empty");
    private MinecraftKey target = new MinecraftKey("empty");
    private ResourceKey<WorldGenFeatureDefinedStructurePoolTemplate> pool;
    private TileEntityJigsaw.JointType joint;
    private String finalState;

    public TileEntityJigsaw(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.JIGSAW, blockposition, iblockdata);
        this.pool = ResourceKey.create(Registries.TEMPLATE_POOL, new MinecraftKey("empty"));
        this.joint = TileEntityJigsaw.JointType.ROLLABLE;
        this.finalState = "minecraft:air";
    }

    public MinecraftKey getName() {
        return this.name;
    }

    public MinecraftKey getTarget() {
        return this.target;
    }

    public ResourceKey<WorldGenFeatureDefinedStructurePoolTemplate> getPool() {
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

    public void setPool(ResourceKey<WorldGenFeatureDefinedStructurePoolTemplate> resourcekey) {
        this.pool = resourcekey;
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
        nbttagcompound.putString("pool", this.pool.location().toString());
        nbttagcompound.putString("final_state", this.finalState);
        nbttagcompound.putString("joint", this.joint.getSerializedName());
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.name = new MinecraftKey(nbttagcompound.getString("name"));
        this.target = new MinecraftKey(nbttagcompound.getString("target"));
        this.pool = ResourceKey.create(Registries.TEMPLATE_POOL, new MinecraftKey(nbttagcompound.getString("pool")));
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
        BlockPosition blockposition = this.getBlockPos().relative(((BlockPropertyJigsawOrientation) this.getBlockState().getValue(BlockJigsaw.ORIENTATION)).front());
        IRegistry<WorldGenFeatureDefinedStructurePoolTemplate> iregistry = worldserver.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
        Holder<WorldGenFeatureDefinedStructurePoolTemplate> holder = iregistry.getHolderOrThrow(this.pool);

        WorldGenFeatureDefinedStructureJigsawPlacement.generateJigsaw(worldserver, holder, this.target, i, blockposition, flag);
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
            return IChatBaseComponent.translatable("jigsaw_block.joint." + this.name);
        }
    }
}
