package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.item.ItemBoneMeal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockCrops;
import net.minecraft.world.level.block.state.IBlockData;

public class BehaviorBonemeal extends Behavior<EntityVillager> {

    private static final int BONEMEALING_DURATION = 80;
    private long nextWorkCycleTime;
    private long lastBonemealingSession;
    private int timeWorkedSoFar;
    private Optional<BlockPosition> cropPos = Optional.empty();

    public BehaviorBonemeal() {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityVillager entityvillager) {
        if (entityvillager.tickCount % 10 == 0 && (this.lastBonemealingSession == 0L || this.lastBonemealingSession + 160L <= (long) entityvillager.tickCount)) {
            if (entityvillager.getInventory().countItem(Items.BONE_MEAL) <= 0) {
                return false;
            } else {
                this.cropPos = this.pickNextTarget(worldserver, entityvillager);
                return this.cropPos.isPresent();
            }
        } else {
            return false;
        }
    }

    protected boolean canStillUse(WorldServer worldserver, EntityVillager entityvillager, long i) {
        return this.timeWorkedSoFar < 80 && this.cropPos.isPresent();
    }

    private Optional<BlockPosition> pickNextTarget(WorldServer worldserver, EntityVillager entityvillager) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        Optional<BlockPosition> optional = Optional.empty();
        int i = 0;

        for (int j = -1; j <= 1; ++j) {
            for (int k = -1; k <= 1; ++k) {
                for (int l = -1; l <= 1; ++l) {
                    blockposition_mutableblockposition.setWithOffset(entityvillager.blockPosition(), j, k, l);
                    if (this.validPos(blockposition_mutableblockposition, worldserver)) {
                        ++i;
                        if (worldserver.random.nextInt(i) == 0) {
                            optional = Optional.of(blockposition_mutableblockposition.immutable());
                        }
                    }
                }
            }
        }

        return optional;
    }

    private boolean validPos(BlockPosition blockposition, WorldServer worldserver) {
        IBlockData iblockdata = worldserver.getBlockState(blockposition);
        Block block = iblockdata.getBlock();

        return block instanceof BlockCrops && !((BlockCrops) block).isMaxAge(iblockdata);
    }

    protected void start(WorldServer worldserver, EntityVillager entityvillager, long i) {
        this.setCurrentCropAsTarget(entityvillager);
        entityvillager.setItemSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.BONE_MEAL));
        this.nextWorkCycleTime = i;
        this.timeWorkedSoFar = 0;
    }

    private void setCurrentCropAsTarget(EntityVillager entityvillager) {
        this.cropPos.ifPresent((blockposition) -> {
            BehaviorTarget behaviortarget = new BehaviorTarget(blockposition);

            entityvillager.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, (Object) behaviortarget);
            entityvillager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(behaviortarget, 0.5F, 1)));
        });
    }

    protected void stop(WorldServer worldserver, EntityVillager entityvillager, long i) {
        entityvillager.setItemSlot(EnumItemSlot.MAINHAND, ItemStack.EMPTY);
        this.lastBonemealingSession = (long) entityvillager.tickCount;
    }

    protected void tick(WorldServer worldserver, EntityVillager entityvillager, long i) {
        BlockPosition blockposition = (BlockPosition) this.cropPos.get();

        if (i >= this.nextWorkCycleTime && blockposition.closerToCenterThan(entityvillager.position(), 1.0D)) {
            ItemStack itemstack = ItemStack.EMPTY;
            InventorySubcontainer inventorysubcontainer = entityvillager.getInventory();
            int j = inventorysubcontainer.getContainerSize();

            for (int k = 0; k < j; ++k) {
                ItemStack itemstack1 = inventorysubcontainer.getItem(k);

                if (itemstack1.is(Items.BONE_MEAL)) {
                    itemstack = itemstack1;
                    break;
                }
            }

            if (!itemstack.isEmpty() && ItemBoneMeal.growCrop(itemstack, worldserver, blockposition)) {
                worldserver.levelEvent(1505, blockposition, 0);
                this.cropPos = this.pickNextTarget(worldserver, entityvillager);
                this.setCurrentCropAsTarget(entityvillager);
                this.nextWorkCycleTime = i + 40L;
            }

            ++this.timeWorkedSoFar;
        }
    }
}
