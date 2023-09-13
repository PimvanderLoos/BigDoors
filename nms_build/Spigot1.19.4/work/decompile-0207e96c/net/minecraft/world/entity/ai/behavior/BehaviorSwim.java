package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.entity.EntityInsentient;

public class BehaviorSwim extends Behavior<EntityInsentient> {

    private final float chance;

    public BehaviorSwim(float f) {
        super(ImmutableMap.of());
        this.chance = f;
    }

    protected boolean checkExtraStartConditions(WorldServer worldserver, EntityInsentient entityinsentient) {
        return entityinsentient.isInWater() && entityinsentient.getFluidHeight(TagsFluid.WATER) > entityinsentient.getFluidJumpThreshold() || entityinsentient.isInLava();
    }

    protected boolean canStillUse(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        return this.checkExtraStartConditions(worldserver, entityinsentient);
    }

    protected void tick(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        if (entityinsentient.getRandom().nextFloat() < this.chance) {
            entityinsentient.getJumpControl().jump();
        }

    }
}
