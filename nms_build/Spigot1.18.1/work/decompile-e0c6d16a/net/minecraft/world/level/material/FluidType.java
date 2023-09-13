package net.minecraft.world.level.material;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.RegistryBlockID;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class FluidType {

    public static final RegistryBlockID<Fluid> FLUID_STATE_REGISTRY = new RegistryBlockID<>();
    protected final BlockStateList<FluidType, Fluid> stateDefinition;
    private Fluid defaultFluidState;

    protected FluidType() {
        BlockStateList.a<FluidType, Fluid> blockstatelist_a = new BlockStateList.a<>(this);

        this.createFluidStateDefinition(blockstatelist_a);
        this.stateDefinition = blockstatelist_a.create(FluidType::defaultFluidState, Fluid::new);
        this.registerDefaultState((Fluid) this.stateDefinition.any());
    }

    protected void createFluidStateDefinition(BlockStateList.a<FluidType, Fluid> blockstatelist_a) {}

    public BlockStateList<FluidType, Fluid> getStateDefinition() {
        return this.stateDefinition;
    }

    protected final void registerDefaultState(Fluid fluid) {
        this.defaultFluidState = fluid;
    }

    public final Fluid defaultFluidState() {
        return this.defaultFluidState;
    }

    public abstract Item getBucket();

    protected void animateTick(World world, BlockPosition blockposition, Fluid fluid, Random random) {}

    protected void tick(World world, BlockPosition blockposition, Fluid fluid) {}

    protected void randomTick(World world, BlockPosition blockposition, Fluid fluid, Random random) {}

    @Nullable
    protected ParticleParam getDripParticle() {
        return null;
    }

    protected abstract boolean canBeReplacedWith(Fluid fluid, IBlockAccess iblockaccess, BlockPosition blockposition, FluidType fluidtype, EnumDirection enumdirection);

    protected abstract Vec3D getFlow(IBlockAccess iblockaccess, BlockPosition blockposition, Fluid fluid);

    public abstract int getTickDelay(IWorldReader iworldreader);

    protected boolean isRandomlyTicking() {
        return false;
    }

    protected boolean isEmpty() {
        return false;
    }

    protected abstract float getExplosionResistance();

    public abstract float getHeight(Fluid fluid, IBlockAccess iblockaccess, BlockPosition blockposition);

    public abstract float getOwnHeight(Fluid fluid);

    protected abstract IBlockData createLegacyBlock(Fluid fluid);

    public abstract boolean isSource(Fluid fluid);

    public abstract int getAmount(Fluid fluid);

    public boolean isSame(FluidType fluidtype) {
        return fluidtype == this;
    }

    public boolean is(Tag<FluidType> tag) {
        return tag.contains(this);
    }

    public abstract VoxelShape getShape(Fluid fluid, IBlockAccess iblockaccess, BlockPosition blockposition);

    public Optional<SoundEffect> getPickupSound() {
        return Optional.empty();
    }
}
