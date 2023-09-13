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
    private static final PathfinderTargetCondition TEMPT_TARGETING = PathfinderTargetCondition.b().a(10.0D).d();
    private final RecipeItemStack temptations;

    public TemptingSensor(RecipeItemStack recipeitemstack) {
        this.temptations = recipeitemstack;
    }

    protected void a(WorldServer worldserver, EntityCreature entitycreature) {
        BehaviorController<?> behaviorcontroller = entitycreature.getBehaviorController();
        Stream stream = worldserver.getPlayers().stream().filter(IEntitySelector.NO_SPECTATORS).filter((entityplayer) -> {
            return TemptingSensor.TEMPT_TARGETING.a(entitycreature, entityplayer);
        }).filter((entityplayer) -> {
            return entitycreature.a((Entity) entityplayer, 10.0D);
        }).filter(this::a);

        Objects.requireNonNull(entitycreature);
        List<EntityHuman> list = (List) stream.sorted(Comparator.comparingDouble(entitycreature::f)).collect(Collectors.toList());

        if (!list.isEmpty()) {
            EntityHuman entityhuman = (EntityHuman) list.get(0);

            behaviorcontroller.setMemory(MemoryModuleType.TEMPTING_PLAYER, (Object) entityhuman);
        } else {
            behaviorcontroller.removeMemory(MemoryModuleType.TEMPTING_PLAYER);
        }

    }

    private boolean a(EntityHuman entityhuman) {
        return this.a(entityhuman.getItemInMainHand()) || this.a(entityhuman.getItemInOffHand());
    }

    private boolean a(ItemStack itemstack) {
        return this.temptations.test(itemstack);
    }

    @Override
    public Set<MemoryModuleType<?>> a() {
        return ImmutableSet.of(MemoryModuleType.TEMPTING_PLAYER);
    }
}
