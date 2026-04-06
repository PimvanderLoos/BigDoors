package nl.pim16aap2.bigDoors.NMS;

import net.minecraft.CrashReportCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.jspecify.annotations.NonNull;

public class CustomEntityFallingBlock_V26_R1 extends FallingBlockEntity implements CustomEntityFallingBlock
{
    // We need to keep our own blockstate as the parent is private without setter.
    private BlockState blockState;
    private final CraftWorld world;

    public CustomEntityFallingBlock_V26_R1(
        final org.bukkit.World world,
        final double spawnX,
        final double spawnY,
        final double spawnZ,
        final BlockState blockState
    )
    {
        super(
            EntityType.FALLING_BLOCK,
            ((CraftWorld) world).getHandle()
        );

        this.world = (CraftWorld) world;
        this.blockState = blockState;

        applyDefaultSettings();

        super.setPos(spawnX, spawnY, spawnZ);
        super.setStartPos(BlockPos.containing(spawnX, spawnY, spawnZ));
        super.setDeltaMovement(new Vec3(0.0F, 0.0F, 0.0F));

        spawn();
    }

    private void applyDefaultSettings()
    {
        super.time = 0;
        super.hurtEntities = false;
        super.noPhysics = true;
        super.setNoGravity(true);
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
    public void tick()
    {
        if (this.blockState.isAir())
        {
            die();
            return;
        }

        move(MoverType.SELF, getDeltaMovement());
        if (++super.time > 12000)
            die();

        setDeltaMovement(getDeltaMovement().multiply(0.9800000190734863D, 1.0D, 0.9800000190734863D));
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput)
    {
        super.addAdditionalSaveData(valueOutput);
        applyDefaultSettings();
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput)
    {
        super.readAdditionalSaveData(valueInput);
        this.blockState = super.getBlockState();
        setTicksLived(0);
    }

    @Override
    public void fillCrashReportCategory(@NonNull CrashReportCategory crashReportCategory)
    {
        super.fillCrashReportCategory(crashReportCategory);
        crashReportCategory.setDetail("Animated BigDoors block with state: ", blockState.toString());
    }

    @Override
    public @NonNull BlockState getBlockState()
    {
        return blockState;
    }

    void setTicksLived(int ticks)
    {
        this.time = ticks;
    }
}
