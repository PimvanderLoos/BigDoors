package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IPosition;
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
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockCrops;
import net.minecraft.world.level.block.state.IBlockData;

public class BehaviorBonemeal extends Behavior<EntityVillager> {

    private long b;
    private long c;
    private int d;
    private Optional<BlockPosition> e = Optional.empty();

    public BehaviorBonemeal() {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
    }

    protected boolean a(WorldServer worldserver, EntityVillager entityvillager) {
        if (entityvillager.ticksLived % 10 == 0 && (this.c == 0L || this.c + 160L <= (long) entityvillager.ticksLived)) {
            if (entityvillager.getInventory().a(Items.BONE_MEAL) <= 0) {
                return false;
            } else {
                this.e = this.b(worldserver, entityvillager);
                return this.e.isPresent();
            }
        } else {
            return false;
        }
    }

    protected boolean b(WorldServer worldserver, EntityVillager entityvillager, long i) {
        return this.d < 80 && this.e.isPresent();
    }

    private Optional<BlockPosition> b(WorldServer worldserver, EntityVillager entityvillager) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        Optional<BlockPosition> optional = Optional.empty();
        int i = 0;

        for (int j = -1; j <= 1; ++j) {
            for (int k = -1; k <= 1; ++k) {
                for (int l = -1; l <= 1; ++l) {
                    blockposition_mutableblockposition.a((BaseBlockPosition) entityvillager.getChunkCoordinates(), j, k, l);
                    if (this.a((BlockPosition) blockposition_mutableblockposition, worldserver)) {
                        ++i;
                        if (worldserver.random.nextInt(i) == 0) {
                            optional = Optional.of(blockposition_mutableblockposition.immutableCopy());
                        }
                    }
                }
            }
        }

        return optional;
    }

    private boolean a(BlockPosition blockposition, WorldServer worldserver) {
        IBlockData iblockdata = worldserver.getType(blockposition);
        Block block = iblockdata.getBlock();

        return block instanceof BlockCrops && !((BlockCrops) block).isRipe(iblockdata);
    }

    protected void a(WorldServer worldserver, EntityVillager entityvillager, long i) {
        this.a(entityvillager);
        entityvillager.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.BONE_MEAL));
        this.b = i;
        this.d = 0;
    }

    private void a(EntityVillager entityvillager) {
        this.e.ifPresent((blockposition) -> {
            BehaviorTarget behaviortarget = new BehaviorTarget(blockposition);

            entityvillager.getBehaviorController().setMemory(MemoryModuleType.LOOK_TARGET, (Object) behaviortarget);
            entityvillager.getBehaviorController().setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(behaviortarget, 0.5F, 1)));
        });
    }

    protected void c(WorldServer worldserver, EntityVillager entityvillager, long i) {
        entityvillager.setSlot(EnumItemSlot.MAINHAND, ItemStack.b);
        this.c = (long) entityvillager.ticksLived;
    }

    protected void d(WorldServer worldserver, EntityVillager entityvillager, long i) {
        BlockPosition blockposition = (BlockPosition) this.e.get();

        if (i >= this.b && blockposition.a((IPosition) entityvillager.getPositionVector(), 1.0D)) {
            ItemStack itemstack = ItemStack.b;
            InventorySubcontainer inventorysubcontainer = entityvillager.getInventory();
            int j = inventorysubcontainer.getSize();

            for (int k = 0; k < j; ++k) {
                ItemStack itemstack1 = inventorysubcontainer.getItem(k);

                if (itemstack1.getItem() == Items.BONE_MEAL) {
                    itemstack = itemstack1;
                    break;
                }
            }

            if (!itemstack.isEmpty() && ItemBoneMeal.a(itemstack, (World) worldserver, blockposition)) {
                worldserver.triggerEffect(2005, blockposition, 0);
                this.e = this.b(worldserver, entityvillager);
                this.a(entityvillager);
                this.b = i + 40L;
            }

            ++this.d;
        }
    }
}
