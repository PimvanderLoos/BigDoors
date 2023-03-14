package nl.pim16aap2.bigDoors.NMS;

import net.minecraft.CrashReportSystemDetails;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.item.EntityFallingBlock;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * v1_19_R1 implementation of {@link CustomEntityFallingBlock}.
 *
 * @author Pim
 * @see CustomEntityFallingBlock
 */
public class CustomEntityFallingBlock_V1_19_R3 extends EntityFallingBlock implements CustomEntityFallingBlock
{
    private IBlockData block;

    public CustomEntityFallingBlock_V1_19_R3(
        final org.bukkit.World world, final double d0, final double d1, final double d2, final IBlockData iblockdata)
    {
        super(EntityTypes.L, ((CraftWorld) world).getHandle());
        block = iblockdata;

        this.e(d0, d1, d2);
        super.b = 0;
        super.i = false;
        super.ae = true;
        this.e(true);
        this.f(new Vec3D(0.0, 0.0, 0.0));
        this.a(BlockPosition.a(this.dl(), this.dn(), this.dr()));
        spawn();
    }

    public void spawn()
    {
        ((WorldServer)super.H).addWithUUID(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    @Override
    public void l()
    {
        if (block.h())
            ai();
        else
        {
            a(EnumMoveType.a, dj());
            if (++b > 12000)
                ai();

            f(dj().d(0.9800000190734863D, 1.0D, 0.9800000190734863D));
        }
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
        nbttagcompound.a("HurtEntities", aq);
        nbttagcompound.a("FallHurtAmount", 0.0f);
        nbttagcompound.a("FallHurtMax", 0);
        if (d != null)
            nbttagcompound.a("TileEntityData", d);
    }

    @Override
    protected void a(final NBTTagCompound nbttagcompound)
    {
        block = GameProfileSerializer.a(super.H.a(Registries.e), nbttagcompound.p("BlockState"));
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
    public IBlockData k()
    {
        return block;
    }
}
