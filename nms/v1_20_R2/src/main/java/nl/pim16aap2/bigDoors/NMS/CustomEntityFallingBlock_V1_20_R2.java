package nl.pim16aap2.bigDoors.NMS;

import net.minecraft.CrashReportSystemDetails;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.item.EntityFallingBlock;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CustomEntityFallingBlock_V1_20_R2 extends EntityFallingBlock implements CustomEntityFallingBlock
{
    private IBlockData block;
    private final CraftWorld world;

    public CustomEntityFallingBlock_V1_20_R2(
        final org.bukkit.World world, final double d0, final double d1, final double d2, final IBlockData iblockdata)
    {
        super(EntityTypes.L, ((CraftWorld) world).getHandle());
        this.world = (CraftWorld) world;
        block = iblockdata;

        this.e(d0, d1, d2);
        super.b = 0;
        super.i = false;
        super.af = true;
        this.e(true);
        this.f(new Vec3D(0.0, 0.0, 0.0));
        this.a(BlockPosition.a(this.dq(), this.ds(), this.dw()));
        spawn();
    }

    public void spawn()
    {
        this.world.addEntityToWorld(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    @Override
    public void l()
    {
        if (block.i())
            ai();
        else
        {
            this.a(EnumMoveType.a, this.getDeltaMovement0());
            if (++b > 12000)
                ai();

            f(dl().d(0.9800000190734863D, 1.0D, 0.9800000190734863D));
        }
    }

    /**
     * Placeholder method.
     * </p>
     * The actual method is called 'do' in 1.20.2, which is a reserved keyword in Java, so we can't call the method that
     * way.
     * </p>
     * This method should be changed by bytecode manipulation to call the actual method.
     *
     * @return A new {@link Vec3D} with all values set to 0 unless overridden.
     */
    public Vec3D getDeltaMovement0()
    {
        throw new UnsupportedOperationException("This method should be overridden!");
    }

    @Override
    public boolean a(float f, float f1, DamageSource damagesource)
    {
        return false;
    }

    @Override
    protected void b(final NBTTagCompound nbttagcompound)
    {
        nbttagcompound.a("BlockState", GameProfileSerializer.a(block));
        nbttagcompound.a("Time", b);
        nbttagcompound.a("DropItem", false);
        nbttagcompound.a("HurtEntities", i);
        nbttagcompound.a("FallHurtAmount", 0.0f);
        nbttagcompound.a("FallHurtMax", 0);
        if (d != null)
            nbttagcompound.a("TileEntityData", d);
    }

    @Override
    protected void a(final NBTTagCompound nbttagcompound)
    {
        block = GameProfileSerializer.a(super.cJ().a(Registries.e), nbttagcompound.p("BlockState"));
        b = nbttagcompound.h("Time");

        if (nbttagcompound.b("TileEntityData", 10))
            super.d = nbttagcompound.p("TileEntityData");
    }

    @Override
    public void a(final CrashReportSystemDetails crashreportsystemdetails)
    {
        super.a(crashreportsystemdetails);
        crashreportsystemdetails.a("Animated BigDoors block with state: ", block.toString());
    }

    @Override
    public IBlockData t()
    {
        return block;
    }
}
