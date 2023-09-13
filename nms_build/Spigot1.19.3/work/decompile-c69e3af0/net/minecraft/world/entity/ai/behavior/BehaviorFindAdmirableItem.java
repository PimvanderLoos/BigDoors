package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.kinds.K1;
import java.util.function.Predicate;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryTarget;
import net.minecraft.world.entity.item.EntityItem;

public class BehaviorFindAdmirableItem {

    public BehaviorFindAdmirableItem() {}

    public static BehaviorControl<EntityLiving> create(float f, boolean flag, int i) {
        return create((entityliving) -> {
            return true;
        }, f, flag, i);
    }

    public static <E extends EntityLiving> BehaviorControl<E> create(Predicate<E> predicate, float f, boolean flag, int i) {
        return BehaviorBuilder.create((behaviorbuilder_b) -> {
            BehaviorBuilder<E, ? extends MemoryAccessor<? extends K1, MemoryTarget>> behaviorbuilder = flag ? behaviorbuilder_b.registered(MemoryModuleType.WALK_TARGET) : behaviorbuilder_b.absent(MemoryModuleType.WALK_TARGET);

            return behaviorbuilder_b.group(behaviorbuilder_b.registered(MemoryModuleType.LOOK_TARGET), behaviorbuilder, behaviorbuilder_b.present(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM), behaviorbuilder_b.registered(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS)).apply(behaviorbuilder_b, (memoryaccessor, memoryaccessor1, memoryaccessor2, memoryaccessor3) -> {
                return (worldserver, entityliving, j) -> {
                    EntityItem entityitem = (EntityItem) behaviorbuilder_b.get(memoryaccessor2);

                    if (behaviorbuilder_b.tryGet(memoryaccessor3).isEmpty() && predicate.test(entityliving) && entityitem.closerThan(entityliving, (double) i) && entityliving.level.getWorldBorder().isWithinBounds(entityitem.blockPosition())) {
                        MemoryTarget memorytarget = new MemoryTarget(new BehaviorPositionEntity(entityitem, false), f, 0);

                        memoryaccessor.set(new BehaviorPositionEntity(entityitem, true));
                        memoryaccessor1.set(memorytarget);
                        return true;
                    } else {
                        return false;
                    }
                };
            });
        });
    }
}
