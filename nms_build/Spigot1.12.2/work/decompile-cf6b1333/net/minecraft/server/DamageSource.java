package net.minecraft.server;

import javax.annotation.Nullable;

public class DamageSource {

    public static final DamageSource FIRE = (new DamageSource("inFire")).setExplosion();
    public static final DamageSource LIGHTNING = new DamageSource("lightningBolt");
    public static final DamageSource BURN = (new DamageSource("onFire")).setIgnoreArmor().setExplosion();
    public static final DamageSource LAVA = (new DamageSource("lava")).setExplosion();
    public static final DamageSource HOT_FLOOR = (new DamageSource("hotFloor")).setExplosion();
    public static final DamageSource STUCK = (new DamageSource("inWall")).setIgnoreArmor();
    public static final DamageSource CRAMMING = (new DamageSource("cramming")).setIgnoreArmor();
    public static final DamageSource DROWN = (new DamageSource("drown")).setIgnoreArmor();
    public static final DamageSource STARVE = (new DamageSource("starve")).setIgnoreArmor().m();
    public static final DamageSource CACTUS = new DamageSource("cactus");
    public static final DamageSource FALL = (new DamageSource("fall")).setIgnoreArmor();
    public static final DamageSource FLY_INTO_WALL = (new DamageSource("flyIntoWall")).setIgnoreArmor();
    public static final DamageSource OUT_OF_WORLD = (new DamageSource("outOfWorld")).setIgnoreArmor().l();
    public static final DamageSource GENERIC = (new DamageSource("generic")).setIgnoreArmor();
    public static final DamageSource MAGIC = (new DamageSource("magic")).setIgnoreArmor().setMagic();
    public static final DamageSource WITHER = (new DamageSource("wither")).setIgnoreArmor();
    public static final DamageSource ANVIL = new DamageSource("anvil");
    public static final DamageSource FALLING_BLOCK = new DamageSource("fallingBlock");
    public static final DamageSource DRAGON_BREATH = (new DamageSource("dragonBreath")).setIgnoreArmor();
    public static final DamageSource t = (new DamageSource("fireworks")).d();
    private boolean v;
    private boolean w;
    private boolean x;
    private float y = 0.1F;
    private boolean z;
    private boolean A;
    private boolean B;
    private boolean C;
    private boolean D;
    public String translationIndex;

    public static DamageSource mobAttack(EntityLiving entityliving) {
        return new EntityDamageSource("mob", entityliving);
    }

    public static DamageSource a(Entity entity, EntityLiving entityliving) {
        return new EntityDamageSourceIndirect("mob", entity, entityliving);
    }

    public static DamageSource playerAttack(EntityHuman entityhuman) {
        return new EntityDamageSource("player", entityhuman);
    }

    public static DamageSource arrow(EntityArrow entityarrow, @Nullable Entity entity) {
        return (new EntityDamageSourceIndirect("arrow", entityarrow, entity)).b();
    }

    public static DamageSource fireball(EntityFireball entityfireball, @Nullable Entity entity) {
        return entity == null ? (new EntityDamageSourceIndirect("onFire", entityfireball, entityfireball)).setExplosion().b() : (new EntityDamageSourceIndirect("fireball", entityfireball, entity)).setExplosion().b();
    }

    public static DamageSource projectile(Entity entity, @Nullable Entity entity1) {
        return (new EntityDamageSourceIndirect("thrown", entity, entity1)).b();
    }

    public static DamageSource b(Entity entity, @Nullable Entity entity1) {
        return (new EntityDamageSourceIndirect("indirectMagic", entity, entity1)).setIgnoreArmor().setMagic();
    }

    public static DamageSource a(Entity entity) {
        return (new EntityDamageSource("thorns", entity)).w().setMagic();
    }

    public static DamageSource explosion(@Nullable Explosion explosion) {
        return explosion != null && explosion.getSource() != null ? (new EntityDamageSource("explosion.player", explosion.getSource())).q().d() : (new DamageSource("explosion")).q().d();
    }

    public static DamageSource b(@Nullable EntityLiving entityliving) {
        return entityliving != null ? (new EntityDamageSource("explosion.player", entityliving)).q().d() : (new DamageSource("explosion")).q().d();
    }

    public boolean a() {
        return this.A;
    }

    public DamageSource b() {
        this.A = true;
        return this;
    }

    public boolean isExplosion() {
        return this.D;
    }

    public DamageSource d() {
        this.D = true;
        return this;
    }

    public boolean ignoresArmor() {
        return this.v;
    }

    public float getExhaustionCost() {
        return this.y;
    }

    public boolean ignoresInvulnerability() {
        return this.w;
    }

    public boolean isStarvation() {
        return this.x;
    }

    protected DamageSource(String s) {
        this.translationIndex = s;
    }

    @Nullable
    public Entity i() {
        return this.getEntity();
    }

    @Nullable
    public Entity getEntity() {
        return null;
    }

    protected DamageSource setIgnoreArmor() {
        this.v = true;
        this.y = 0.0F;
        return this;
    }

    protected DamageSource l() {
        this.w = true;
        return this;
    }

    protected DamageSource m() {
        this.x = true;
        this.y = 0.0F;
        return this;
    }

    protected DamageSource setExplosion() {
        this.z = true;
        return this;
    }

    public IChatBaseComponent getLocalizedDeathMessage(EntityLiving entityliving) {
        EntityLiving entityliving1 = entityliving.ci();
        String s = "death.attack." + this.translationIndex;
        String s1 = s + ".player";

        return entityliving1 != null && LocaleI18n.c(s1) ? new ChatMessage(s1, new Object[] { entityliving.getScoreboardDisplayName(), entityliving1.getScoreboardDisplayName()}) : new ChatMessage(s, new Object[] { entityliving.getScoreboardDisplayName()});
    }

    public boolean o() {
        return this.z;
    }

    public String p() {
        return this.translationIndex;
    }

    public DamageSource q() {
        this.B = true;
        return this;
    }

    public boolean r() {
        return this.B;
    }

    public boolean isMagic() {
        return this.C;
    }

    public DamageSource setMagic() {
        this.C = true;
        return this;
    }

    public boolean u() {
        Entity entity = this.getEntity();

        return entity instanceof EntityHuman && ((EntityHuman) entity).abilities.canInstantlyBuild;
    }

    @Nullable
    public Vec3D v() {
        return null;
    }
}
