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

        this.a(blockstatelist_a);
        this.stateDefinition = blockstatelist_a.a(FluidType::h, Fluid::new);
        this.f((Fluid) this.stateDefinition.getBlockData());
    }

    protected void a(BlockStateList.a<FluidType, Fluid> blockstatelist_a) {}

    public BlockStateList<FluidType, Fluid> g() {
        return this.stateDefinition;
    }

    protected final void f(Fluid fluid) {
        this.defaultFluidState = fluid;
    }

    public final Fluid h() {
        return this.defaultFluidState;
    }

    public abstract Item a();

    protected void a(World world, BlockPosition blockposition, Fluid fluid, Random random) {}

    protected void a(World world, BlockPosition blockposition, Fluid fluid) {}

    protected void b(World world, BlockPosition blockposition, Fluid fluid, Random random) {}

    @Nullable
    protected ParticleParam i() {
        return null;
    }

    protected abstract boolean a(Fluid fluid, IBlockAccess iblockaccess, BlockPosition blockposition, FluidType fluidtype, EnumDirection enumdirection);

    protected abstract Vec3D a(IBlockAccess iblockaccess, BlockPosition blockposition, Fluid fluid);

    public abstract int a(IWorldReader iworldreader);

    protected boolean j() {
        return false;
    }

    protected boolean b() {
        return false;
    }

    protected abstract float c();

    public abstract float a(Fluid fluid, IBlockAccess iblockaccess, BlockPosition blockposition);

    public abstract float a(Fluid fluid);

    protected abstract IBlockData b(Fluid fluid);

    public abstract boolean c(Fluid fluid);

    public abstract int d(Fluid fluid);

    public boolean a(FluidType fluidtype) {
        return fluidtype == this;
    }

    public boolean a(Tag<FluidType> tag) {
        return tag.isTagged(this);
    }

    public abstract VoxelShape b(Fluid fluid, IBlockAccess iblockaccess, BlockPosition blockposition);

    public Optional<SoundEffect> k() {
        return Optional.empty();
    }
}
