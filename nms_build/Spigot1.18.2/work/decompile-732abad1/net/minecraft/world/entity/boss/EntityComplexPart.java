package net.minecraft.world.entity.boss;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;

public class EntityComplexPart extends Entity {

    public final EntityEnderDragon parentMob;
    public final String name;
    private final EntitySize size;

    public EntityComplexPart(EntityEnderDragon entityenderdragon, String s, float f, float f1) {
        super(entityenderdragon.getType(), entityenderdragon.level);
        this.size = EntitySize.scalable(f, f1);
        this.refreshDimensions();
        this.parentMob = entityenderdragon;
        this.name = s;
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(NBTTagCompound nbttagcompound) {}

    @Override
    protected void addAdditionalSaveData(NBTTagCompound nbttagcompound) {}

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        return this.isInvulnerableTo(damagesource) ? false : this.parentMob.hurt(this, damagesource, f);
    }

    @Override
    public boolean is(Entity entity) {
        return this == entity || this.parentMob == entity;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        throw new UnsupportedOperationException();
    }

    @Override
    public EntitySize getDimensions(EntityPose entitypose) {
        return this.size;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }
}
