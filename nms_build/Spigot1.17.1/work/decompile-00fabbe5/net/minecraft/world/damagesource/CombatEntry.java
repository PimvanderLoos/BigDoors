package net.minecraft.world.damagesource;

import javax.annotation.Nullable;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;

public class CombatEntry {

    private final DamageSource source;
    private final int time;
    private final float damage;
    private final float health;
    @Nullable
    private final String location;
    private final float fallDistance;

    public CombatEntry(DamageSource damagesource, int i, float f, float f1, @Nullable String s, float f2) {
        this.source = damagesource;
        this.time = i;
        this.damage = f1;
        this.health = f;
        this.location = s;
        this.fallDistance = f2;
    }

    public DamageSource a() {
        return this.source;
    }

    public int b() {
        return this.time;
    }

    public float c() {
        return this.damage;
    }

    public float d() {
        return this.health;
    }

    public float e() {
        return this.health - this.damage;
    }

    public boolean f() {
        return this.source.getEntity() instanceof EntityLiving;
    }

    @Nullable
    public String g() {
        return this.location;
    }

    @Nullable
    public IChatBaseComponent h() {
        return this.a().getEntity() == null ? null : this.a().getEntity().getScoreboardDisplayName();
    }

    @Nullable
    public Entity i() {
        return this.a().getEntity();
    }

    public float j() {
        return this.source == DamageSource.OUT_OF_WORLD ? Float.MAX_VALUE : this.fallDistance;
    }
}
