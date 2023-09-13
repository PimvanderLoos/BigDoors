package net.minecraft.world.entity.animal;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.pathfinder.PathType;

public abstract class EntityWaterAnimal extends EntityCreature {

    protected EntityWaterAnimal(EntityTypes<? extends EntityWaterAnimal> entitytypes, World world) {
        super(entitytypes, world);
        this.a(PathType.WATER, 0.0F);
    }

    @Override
    public boolean cM() {
        return true;
    }

    @Override
    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.WATER_MOB;
    }

    @Override
    public boolean a(IWorldReader iworldreader) {
        return iworldreader.j((Entity) this);
    }

    @Override
    public int D() {
        return 120;
    }

    @Override
    protected int getExpValue(EntityHuman entityhuman) {
        return 1 + this.world.random.nextInt(3);
    }

    protected void a(int i) {
        if (this.isAlive() && !this.aH()) {
            this.setAirTicks(i - 1);
            if (this.getAirTicks() == -20) {
                this.setAirTicks(0);
                this.damageEntity(DamageSource.DROWN, 2.0F);
            }
        } else {
            this.setAirTicks(300);
        }

    }

    @Override
    public void entityBaseTick() {
        int i = this.getAirTicks();

        super.entityBaseTick();
        this.a(i);
    }

    @Override
    public boolean bV() {
        return false;
    }

    @Override
    public boolean a(EntityHuman entityhuman) {
        return false;
    }
}
