package net.minecraft.server;

import com.google.common.collect.ImmutableMap;

public class BehaviorSwim extends Behavior<EntityInsentient> {

    private final float b;

    public BehaviorSwim(float f) {
        super(ImmutableMap.of());
        this.b = f;
    }

    protected boolean a(WorldServer worldserver, EntityInsentient entityinsentient) {
        return entityinsentient.isInWater() && entityinsentient.b((Tag) TagsFluid.WATER) > entityinsentient.cw() || entityinsentient.aN();
    }

    protected boolean b(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        return this.a(worldserver, entityinsentient);
    }

    protected void d(WorldServer worldserver, EntityInsentient entityinsentient, long i) {
        if (entityinsentient.getRandom().nextFloat() < this.b) {
            entityinsentient.getControllerJump().jump();
        }

    }
}
