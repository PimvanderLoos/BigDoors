package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockCrops;
import net.minecraft.world.level.block.BlockSoil;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class BehaviorFarm extends Behavior<EntityVillager> {

    private static final int HARVEST_DURATION = 200;
    public static final float SPEED_MODIFIER = 0.5F;
    @Nullable
    private BlockPosition aboveFarmlandPos;
    private long nextOkStartTime;
    private int timeWorkedSoFar;
    private final List<BlockPosition> validFarmlandAroundVillager = Lists.newArrayList();

    public BehaviorFarm() {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.SECONDARY_JOB_SITE, MemoryStatus.VALUE_PRESENT));
    }

    protected boolean a(WorldServer worldserver, EntityVillager entityvillager) {
        if (!worldserver.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return false;
        } else if (entityvillager.getVillagerData().getProfession() != VillagerProfession.FARMER) {
            return false;
        } else {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = entityvillager.getChunkCoordinates().i();

            this.validFarmlandAroundVillager.clear();

            for (int i = -1; i <= 1; ++i) {
                for (int j = -1; j <= 1; ++j) {
                    for (int k = -1; k <= 1; ++k) {
                        blockposition_mutableblockposition.c(entityvillager.locX() + (double) i, entityvillager.locY() + (double) j, entityvillager.locZ() + (double) k);
                        if (this.a((BlockPosition) blockposition_mutableblockposition, worldserver)) {
                            this.validFarmlandAroundVillager.add(new BlockPosition(blockposition_mutableblockposition));
                        }
                    }
                }
            }

            this.aboveFarmlandPos = this.a(worldserver);
            return this.aboveFarmlandPos != null;
        }
    }

    @Nullable
    private BlockPosition a(WorldServer worldserver) {
        return this.validFarmlandAroundVillager.isEmpty() ? null : (BlockPosition) this.validFarmlandAroundVillager.get(worldserver.getRandom().nextInt(this.validFarmlandAroundVillager.size()));
    }

    private boolean a(BlockPosition blockposition, WorldServer worldserver) {
        IBlockData iblockdata = worldserver.getType(blockposition);
        Block block = iblockdata.getBlock();
        Block block1 = worldserver.getType(blockposition.down()).getBlock();

        return block instanceof BlockCrops && ((BlockCrops) block).isRipe(iblockdata) || iblockdata.isAir() && block1 instanceof BlockSoil;
    }

    protected void a(WorldServer worldserver, EntityVillager entityvillager, long i) {
        if (i > this.nextOkStartTime && this.aboveFarmlandPos != null) {
            entityvillager.getBehaviorController().setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorTarget(this.aboveFarmlandPos)));
            entityvillager.getBehaviorController().setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(new BehaviorTarget(this.aboveFarmlandPos), 0.5F, 1)));
        }

    }

    protected void c(WorldServer worldserver, EntityVillager entityvillager, long i) {
        entityvillager.getBehaviorController().removeMemory(MemoryModuleType.LOOK_TARGET);
        entityvillager.getBehaviorController().removeMemory(MemoryModuleType.WALK_TARGET);
        this.timeWorkedSoFar = 0;
        this.nextOkStartTime = i + 40L;
    }

    protected void d(WorldServer worldserver, EntityVillager entityvillager, long i) {
        if (this.aboveFarmlandPos == null || this.aboveFarmlandPos.a((IPosition) entityvillager.getPositionVector(), 1.0D)) {
            if (this.aboveFarmlandPos != null && i > this.nextOkStartTime) {
                IBlockData iblockdata = worldserver.getType(this.aboveFarmlandPos);
                Block block = iblockdata.getBlock();
                Block block1 = worldserver.getType(this.aboveFarmlandPos.down()).getBlock();

                if (block instanceof BlockCrops && ((BlockCrops) block).isRipe(iblockdata)) {
                    worldserver.a(this.aboveFarmlandPos, true, entityvillager);
                }

                if (iblockdata.isAir() && block1 instanceof BlockSoil && entityvillager.canPlant()) {
                    InventorySubcontainer inventorysubcontainer = entityvillager.getInventory();

                    for (int j = 0; j < inventorysubcontainer.getSize(); ++j) {
                        ItemStack itemstack = inventorysubcontainer.getItem(j);
                        boolean flag = false;

                        if (!itemstack.isEmpty()) {
                            if (itemstack.a(Items.WHEAT_SEEDS)) {
                                worldserver.setTypeAndData(this.aboveFarmlandPos, Blocks.WHEAT.getBlockData(), 3);
                                flag = true;
                            } else if (itemstack.a(Items.POTATO)) {
                                worldserver.setTypeAndData(this.aboveFarmlandPos, Blocks.POTATOES.getBlockData(), 3);
                                flag = true;
                            } else if (itemstack.a(Items.CARROT)) {
                                worldserver.setTypeAndData(this.aboveFarmlandPos, Blocks.CARROTS.getBlockData(), 3);
                                flag = true;
                            } else if (itemstack.a(Items.BEETROOT_SEEDS)) {
                                worldserver.setTypeAndData(this.aboveFarmlandPos, Blocks.BEETROOTS.getBlockData(), 3);
                                flag = true;
                            }
                        }

                        if (flag) {
                            worldserver.playSound((EntityHuman) null, (double) this.aboveFarmlandPos.getX(), (double) this.aboveFarmlandPos.getY(), (double) this.aboveFarmlandPos.getZ(), SoundEffects.CROP_PLANTED, SoundCategory.BLOCKS, 1.0F, 1.0F);
                            itemstack.subtract(1);
                            if (itemstack.isEmpty()) {
                                inventorysubcontainer.setItem(j, ItemStack.EMPTY);
                            }
                            break;
                        }
                    }
                }

                if (block instanceof BlockCrops && !((BlockCrops) block).isRipe(iblockdata)) {
                    this.validFarmlandAroundVillager.remove(this.aboveFarmlandPos);
                    this.aboveFarmlandPos = this.a(worldserver);
                    if (this.aboveFarmlandPos != null) {
                        this.nextOkStartTime = i + 20L;
                        entityvillager.getBehaviorController().setMemory(MemoryModuleType.WALK_TARGET, (Object) (new MemoryTarget(new BehaviorTarget(this.aboveFarmlandPos), 0.5F, 1)));
                        entityvillager.getBehaviorController().setMemory(MemoryModuleType.LOOK_TARGET, (Object) (new BehaviorTarget(this.aboveFarmlandPos)));
                    }
                }
            }

            ++this.timeWorkedSoFar;
        }
    }

    protected boolean b(WorldServer worldserver, EntityVillager entityvillager, long i) {
        return this.timeWorkedSoFar < 200;
    }
}
