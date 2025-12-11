package nl.pim16aap2.bigDoors.NMS;

import net.minecraft.CrashReportSystemDetails;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.item.EntityFallingBlock;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.craftbukkit.v1_21_R7.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityRemoveEvent;

public class CustomEntityFallingBlock_V1_21_R7 extends EntityFallingBlock implements CustomEntityFallingBlock
{
    private IBlockData block;
    private final CraftWorld world;

    public CustomEntityFallingBlock_V1_21_R7(
        final org.bukkit.World world, final double d0, final double d1, final double d2, final IBlockData iblockdata)
    {
        super(EntityTypes.ab, ((CraftWorld) world).getHandle());
        this.world = (CraftWorld) world;
        block = iblockdata;

        this.a_(d0, d1, d2);
        super.a = 0;
        super.o = false;
        super.ar = true;
        this.g(true);
        this.k(new Vec3D(0.0F, 0.0F, 0.0F));
        this.a(BlockPosition.a(this.dP(), this.dR(), this.dV()));
        spawn();
    }

    public void spawn()
    {
        this.world.addEntityToWorld(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

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
    protected void a(ValueOutput valueOutput)
    {
        scSetTicksLived(0);

        super.a(valueOutput);

        valueOutput.a("BlockState", IBlockData.a, block);
        valueOutput.a("DropItem", false);
        valueOutput.a("FallHurtAmount", 0.0f);
        valueOutput.a("FallHurtMax", 0);
    }

    @Override
    protected void a(ValueInput valueInput)
    {
        super.a(valueInput);
        this.block = super.i();
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
        return scGetIBlockData();
    }

    /* scs for variables/methods that change regularly
     *
     * Each method is prefixed with "sc" to avoid conflicts with the original methods depending on the mapping
     * being used.
     *
     * The names are purposefully similar to those used by the code generation system to make it easier to use the
     * new values for future MC versions.
     */

    public IBlockData scGetIBlockData()
    {
        return block;
    }

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
        k(vec);
    }

    private Vec3D scGetMotVec()
    {
        return dN();
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
