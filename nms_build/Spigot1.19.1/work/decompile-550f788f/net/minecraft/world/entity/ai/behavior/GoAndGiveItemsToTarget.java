package net.minecraft.world.entity.ai.behavior;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.SystemUtils;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.allay.AllayAi;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;

public class GoAndGiveItemsToTarget<E extends EntityLiving & InventoryCarrier> extends Behavior<E> {

    private static final int CLOSE_ENOUGH_DISTANCE_TO_TARGET = 3;
    private static final int ITEM_PICKUP_COOLDOWN_AFTER_THROWING = 60;
    private final Function<EntityLiving, Optional<BehaviorPosition>> targetPositionGetter;
    private final float speedModifier;

    public GoAndGiveItemsToTarget(Function<EntityLiving, Optional<BehaviorPosition>> function, float f) {
        super(Map.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryStatus.REGISTERED));
        this.targetPositionGetter = function;
        this.speedModifier = f;
    }

    @Override
    protected boolean checkExtraStartConditions(WorldServer worldserver, E e0) {
        return this.canThrowItemToTarget(e0);
    }

    @Override
    protected boolean canStillUse(WorldServer worldserver, E e0, long i) {
        return this.canThrowItemToTarget(e0);
    }

    @Override
    protected void start(WorldServer worldserver, E e0, long i) {
        ((Optional) this.targetPositionGetter.apply(e0)).ifPresent((behaviorposition) -> {
            BehaviorUtil.setWalkAndLookTargetMemories(e0, behaviorposition, this.speedModifier, 3);
        });
    }

    @Override
    protected void tick(WorldServer worldserver, E e0, long i) {
        Optional<BehaviorPosition> optional = (Optional) this.targetPositionGetter.apply(e0);

        if (!optional.isEmpty()) {
            BehaviorPosition behaviorposition = (BehaviorPosition) optional.get();
            double d0 = behaviorposition.currentPosition().distanceTo(e0.getEyePosition());

            if (d0 < 3.0D) {
                ItemStack itemstack = ((InventoryCarrier) e0).getInventory().removeItem(0, 1);

                if (!itemstack.isEmpty()) {
                    throwItem(e0, itemstack, getThrowPosition(behaviorposition));
                    if (e0 instanceof Allay) {
                        Allay allay = (Allay) e0;

                        AllayAi.getLikedPlayer(allay).ifPresent((entityplayer) -> {
                            this.triggerDropItemOnBlock(behaviorposition, itemstack, entityplayer);
                        });
                    }

                    e0.getBrain().setMemory(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, (int) 60);
                }
            }

        }
    }

    private void triggerDropItemOnBlock(BehaviorPosition behaviorposition, ItemStack itemstack, EntityPlayer entityplayer) {
        BlockPosition blockposition = behaviorposition.currentBlockPosition().below();

        CriterionTriggers.ALLAY_DROP_ITEM_ON_BLOCK.trigger(entityplayer, blockposition, itemstack);
    }

    private boolean canThrowItemToTarget(E e0) {
        if (((InventoryCarrier) e0).getInventory().isEmpty()) {
            return false;
        } else {
            Optional<BehaviorPosition> optional = (Optional) this.targetPositionGetter.apply(e0);

            return optional.isPresent();
        }
    }

    private static Vec3D getThrowPosition(BehaviorPosition behaviorposition) {
        return behaviorposition.currentPosition().add(0.0D, 1.0D, 0.0D);
    }

    public static void throwItem(EntityLiving entityliving, ItemStack itemstack, Vec3D vec3d) {
        Vec3D vec3d1 = new Vec3D(0.20000000298023224D, 0.30000001192092896D, 0.20000000298023224D);

        BehaviorUtil.throwItem(entityliving, itemstack, vec3d, vec3d1, 0.2F);
        World world = entityliving.level;

        if (world.getGameTime() % 7L == 0L && world.random.nextDouble() < 0.9D) {
            float f = (Float) SystemUtils.getRandom((List) Allay.THROW_SOUND_PITCHES, world.getRandom());

            world.playSound((EntityHuman) null, (Entity) entityliving, SoundEffects.ALLAY_THROW, SoundCategory.NEUTRAL, 1.0F, f);
        }

    }
}
