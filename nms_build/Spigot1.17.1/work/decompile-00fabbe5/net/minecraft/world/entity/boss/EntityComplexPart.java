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
        super(entityenderdragon.getEntityType(), entityenderdragon.level);
        this.size = EntitySize.b(f, f1);
        this.updateSize();
        this.parentMob = entityenderdragon;
        this.name = s;
    }

    @Override
    protected void initDatawatcher() {}

    @Override
    protected void loadData(NBTTagCompound nbttagcompound) {}

    @Override
    protected void saveData(NBTTagCompound nbttagcompound) {}

    @Override
    public boolean isInteractable() {
        return true;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        return this.isInvulnerable(damagesource) ? false : this.parentMob.a(this, damagesource, f);
    }

    @Override
    public boolean q(Entity entity) {
        return this == entity || this.parentMob == entity;
    }

    @Override
    public Packet<?> getPacket() {
        throw new UnsupportedOperationException();
    }

    @Override
    public EntitySize a(EntityPose entitypose) {
        return this.size;
    }

    @Override
    public boolean dm() {
        return false;
    }
}
