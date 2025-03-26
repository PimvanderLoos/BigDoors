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
import org.bukkit.craftbukkit.v1_21_R4.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityRemoveEvent;

public class CustomEntityFallingBlock_V1_21_R4 extends EntityFallingBlock implements CustomEntityFallingBlock
{
    private IBlockData block;
    private final CraftWorld world;

    public CustomEntityFallingBlock_V1_21_R4(
        final org.bukkit.World world, final double d0, final double d1, final double d2, final IBlockData iblockdata)
    {
        super(EntityTypes.Y, ((CraftWorld) world).getHandle());
        this.world = (CraftWorld) world;
        block = iblockdata;

        this.a_(d0, d1, d2);
        super.a = 0;
        super.o = false;
        super.ad = true;
        this.f(true);
        this.i(new Vec3D(0.0, 0.0, 0.0));
        this.a(BlockPosition.a(this.dA(), this.dC(), this.dG()));
        spawn();
    }

    public void spawn()
    {
        this.world.addEntityToWorld(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    @SuppressWarnings("UnstableApiUsage")
    private void die()
    {
        this.discard(EntityRemoveEvent.Cause.PLUGIN);
    }

    @Override
    public void g()
    {
        if (scBlockIsAir())
            die();
        else
        {
            scMove(EnumMoveType.a, scGetMotVec());
            if (scIncrementAndGetTicksLived() > 12000)
                die();

            scSetMotVec(scGetMotVec().d(0.9800000190734863D, 1.0D, 0.9800000190734863D));
        }
    }

    @Override
    public boolean a(double d0, float f, DamageSource damagesource)
    {
        return false;
    }

    @Override
    protected void b(final NBTTagCompound nbttagcompound)
    {
        scSetTicksLived(0);

        super.b(nbttagcompound);

        nbttagcompound.a("BlockState", GameProfileSerializer.a(block));
        nbttagcompound.a("DropItem", false);
        nbttagcompound.a("FallHurtAmount", 0.0f);
        nbttagcompound.a("FallHurtMax", 0);
    }

    @Override
    protected void a(final NBTTagCompound nbttagcompound)
    {
        super.a(nbttagcompound);
        block = GameProfileSerializer.a(dV().a(Registries.i), nbttagcompound.n("BlockState"));
    }

    @Override
    public void a(final CrashReportSystemDetails crashreportsystemdetails)
    {
        super.a(crashreportsystemdetails);
        crashreportsystemdetails.a("Animated BigDoors block with state: ", block.toString());
    }

    @Override
    public IBlockData i()
    {
        return block;
    }

    /* scs for variables/methods that change regularly
     *
     * Each method is prefixed with "sc" to avoid conflicts with the original methods depending on the mapping
     * being used.
     *
     * The names are purposefully similar to those used by the code generation system to make it easier to use the
     * new values for future MC versions.
     */

    void scSetTicksLived(int ticks)
    {
        a = ticks;
    }

    public int scGetTicksLived()
    {
        return a;
    }

    private int scIncrementAndGetTicksLived()
    {
        return ++a;
    }

    private void scSetMotVec(Vec3D vec)
    {
        i(vec);
    }

    private Vec3D scGetMotVec()
    {
        return dy();
    }

    private void scMove(EnumMoveType moveType, Vec3D vec)
    {
        a(moveType, vec);
    }

    private boolean scBlockIsAir()
    {
        return block.l();
    }
}
