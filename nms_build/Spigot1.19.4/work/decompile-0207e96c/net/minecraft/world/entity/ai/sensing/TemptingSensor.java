package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeItemStack;

public class TemptingSensor extends Sensor<EntityCreature> {

    public static final int TEMPTATION_RANGE = 10;
    private static final PathfinderTargetCondition TEMPT_TARGETING = PathfinderTargetCondition.forNonCombat().range(10.0D).ignoreLineOfSight();
    private final RecipeItemStack temptations;

    public TemptingSensor(RecipeItemStack recipeitemstack) {
        this.temptations = recipeitemstack;
    }

    protected void doTick(WorldServer worldserver, EntityCreature entitycreature) {
        BehaviorController<?> behaviorcontroller = entitycreature.getBrain();
        Stream stream = worldserver.players().stream().filter(IEntitySelector.NO_SPECTATORS).filter((entityplayer) -> {
            return TemptingSensor.TEMPT_TARGETING.test(entitycreature, entityplayer);
        }).filter((entityplayer) -> {
            return entitycreature.closerThan(entityplayer, 10.0D);
        }).filter(this::playerHoldingTemptation).filter((entityplayer) -> {
            return !entitycreature.hasPassenger((Entity) entityplayer);
        });

        Objects.requireNonNull(entitycreature);
        List<EntityHuman> list = (List) stream.sorted(Comparator.comparingDouble(entitycreature::distanceToSqr)).collect(Collectors.toList());

        if (!list.isEmpty()) {
            EntityHuman entityhuman = (EntityHuman) list.get(0);

            behaviorcontroller.setMemory(MemoryModuleType.TEMPTING_PLAYER, (Object) entityhuman);
        } else {
            behaviorcontroller.eraseMemory(MemoryModuleType.TEMPTING_PLAYER);
        }

    }

    private boolean playerHoldingTemptation(EntityHuman entityhuman) {
        return this.isTemptation(entityhuman.getMainHandItem()) || this.isTemptation(entityhuman.getOffhandItem());
    }

    private boolean isTemptation(ItemStack itemstack) {
        return this.temptations.test(itemstack);
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.TEMPTING_PLAYER);
    }
}
