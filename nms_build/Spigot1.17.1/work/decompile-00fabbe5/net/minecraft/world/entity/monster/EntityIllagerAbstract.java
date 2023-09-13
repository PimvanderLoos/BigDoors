package net.minecraft.world.entity.monster;

import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.ai.goal.PathfinderGoalDoorOpen;
import net.minecraft.world.entity.raid.EntityRaider;
import net.minecraft.world.level.World;

public abstract class EntityIllagerAbstract extends EntityRaider {

    protected EntityIllagerAbstract(EntityTypes<? extends EntityIllagerAbstract> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();
    }

    @Override
    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.ILLAGER;
    }

    public EntityIllagerAbstract.a n() {
        return EntityIllagerAbstract.a.CROSSED;
    }

    public static enum a {

        CROSSED, ATTACKING, SPELLCASTING, BOW_AND_ARROW, CROSSBOW_HOLD, CROSSBOW_CHARGE, CELEBRATING, NEUTRAL;

        private a() {}
    }

    protected class b extends PathfinderGoalDoorOpen {

        public b(EntityRaider entityraider) {
            super(entityraider, false);
        }

        @Override
        public boolean a() {
            return super.a() && EntityIllagerAbstract.this.fL();
        }
    }
}
