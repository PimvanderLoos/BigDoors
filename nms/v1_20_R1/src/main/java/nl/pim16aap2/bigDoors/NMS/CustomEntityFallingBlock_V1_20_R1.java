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
import nl.pim16aap2.bigDoors.ILogger;
import nl.pim16aap2.bigDoors.util.ILoggableDoor;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CustomEntityFallingBlock_V1_20_R1 extends EntityFallingBlock implements CustomEntityFallingBlock
{
    private final ILogger logger;
    private final ILoggableDoor door;
    private final CraftWorld world;

    private IBlockData block;

    public CustomEntityFallingBlock_V1_20_R1(
        final ILogger logger,
        final ILoggableDoor door,
        final org.bukkit.World world,
        final double d0,
        final double d1,
        final double d2,
        final IBlockData iblockdata)
    {
        super(EntityTypes.L, ((CraftWorld) world).getHandle());
        this.world = (CraftWorld) world;
        block = iblockdata;
        this.logger = logger;
        this.door = door;

        this.e(d0, d1, d2);
        super.b = 0;
        super.i = false;
        super.ae = true;
        this.e(true);
        this.f(new Vec3D(0.0, 0.0, 0.0));
        this.a(BlockPosition.a(this.dn(), this.dp(), this.dt()));
        spawn();
    }

    public int getUid()
    {
        return super.af();
    }

    void logIllegalRemoval()
    {
        logger.logMessageToLogFileForDoor(
            door,
            new Exception("Stack trace"),
            String.format(
                "Animated block [%3d] illegally removed by external source!",
                getUid()
            ));
    }

    @Override
    public void a(RemovalReason entity_removalreason) {
        logIllegalRemoval();
        this.b(entity_removalreason);
    }

    void privateRemoval()
    {
//        logger.logMessageToLogFileForDoor(
//            door,
//            String.format(
//                "Animated block [%3d] removed properly.",
//                getUid()
//        ));
        super.b(RemovalReason.b);
    }

    public void spawn()
    {
//        logger.logMessageToLogFileForDoor(
//            door,
//            String.format(
//                "Animated block [%3d] spawned.",
//                getUid()
//            ));
        this.world.addEntityToWorld(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    @Override
    public void l()
    {
        if (block.i())
            ai();
        else
        {
            this.a(EnumMoveType.a, this.dl());
            if (++b > 12000)
                ai();

            f(dl().d(0.9800000190734863D, 1.0D, 0.9800000190734863D));
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
        nbttagcompound.a("HurtEntities", i);
        nbttagcompound.a("FallHurtAmount", 0.0f);
        nbttagcompound.a("FallHurtMax", 0);
        if (d != null)
            nbttagcompound.a("TileEntityData", d);
    }

    @Override
    protected void a(final NBTTagCompound nbttagcompound)
    {
        block = GameProfileSerializer.a(super.dI().a(Registries.e), nbttagcompound.p("BlockState"));
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
    public IBlockData o()
    {
        return block;
    }
}
