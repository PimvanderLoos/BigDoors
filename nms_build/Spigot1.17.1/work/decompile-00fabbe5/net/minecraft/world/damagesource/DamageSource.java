package net.minecraft.world.damagesource;

import javax.annotation.Nullable;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.entity.projectile.EntityFireballFireball;
import net.minecraft.world.entity.projectile.EntityFireworks;
import net.minecraft.world.entity.projectile.EntityWitherSkull;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3D;

public class DamageSource {

    public static final DamageSource IN_FIRE = (new DamageSource("inFire")).setIgnoreArmor().setFire();
    public static final DamageSource LIGHTNING_BOLT = new DamageSource("lightningBolt");
    public static final DamageSource ON_FIRE = (new DamageSource("onFire")).setIgnoreArmor().setFire();
    public static final DamageSource LAVA = (new DamageSource("lava")).setFire();
    public static final DamageSource HOT_FLOOR = (new DamageSource("hotFloor")).setFire();
    public static final DamageSource IN_WALL = (new DamageSource("inWall")).setIgnoreArmor();
    public static final DamageSource CRAMMING = (new DamageSource("cramming")).setIgnoreArmor();
    public static final DamageSource DROWN = (new DamageSource("drown")).setIgnoreArmor();
    public static final DamageSource STARVE = (new DamageSource("starve")).setIgnoreArmor().setStarvation();
    public static final DamageSource CACTUS = new DamageSource("cactus");
    public static final DamageSource FALL = (new DamageSource("fall")).setIgnoreArmor().A();
    public static final DamageSource FLY_INTO_WALL = (new DamageSource("flyIntoWall")).setIgnoreArmor();
    public static final DamageSource OUT_OF_WORLD = (new DamageSource("outOfWorld")).setIgnoreArmor().setIgnoresInvulnerability();
    public static final DamageSource GENERIC = (new DamageSource("generic")).setIgnoreArmor();
    public static final DamageSource MAGIC = (new DamageSource("magic")).setIgnoreArmor().setMagic();
    public static final DamageSource WITHER = (new DamageSource("wither")).setIgnoreArmor();
    public static final DamageSource ANVIL = (new DamageSource("anvil")).n();
    public static final DamageSource FALLING_BLOCK = (new DamageSource("fallingBlock")).n();
    public static final DamageSource DRAGON_BREATH = (new DamageSource("dragonBreath")).setIgnoreArmor();
    public static final DamageSource DRY_OUT = new DamageSource("dryout");
    public static final DamageSource SWEET_BERRY_BUSH = new DamageSource("sweetBerryBush");
    public static final DamageSource FREEZE = (new DamageSource("freeze")).setIgnoreArmor();
    public static final DamageSource FALLING_STALACTITE = (new DamageSource("fallingStalactite")).n();
    public static final DamageSource STALAGMITE = (new DamageSource("stalagmite")).setIgnoreArmor().A();
    private boolean damageHelmet;
    private boolean bypassArmor;
    private boolean bypassInvul;
    private boolean bypassMagic;
    private float exhaustion = 0.1F;
    private boolean isFireSource;
    private boolean isProjectile;
    private boolean scalesWithDifficulty;
    private boolean isMagic;
    private boolean isExplosion;
    private boolean isFall;
    private boolean noAggro;
    public final String msgId;

    public static DamageSource b(EntityLiving entityliving) {
        return new EntityDamageSource("sting", entityliving);
    }

    public static DamageSource mobAttack(EntityLiving entityliving) {
        return new EntityDamageSource("mob", entityliving);
    }

    public static DamageSource a(Entity entity, @Nullable EntityLiving entityliving) {
        return new EntityDamageSourceIndirect("mob", entity, entityliving);
    }

    public static DamageSource playerAttack(EntityHuman entityhuman) {
        return new EntityDamageSource("player", entityhuman);
    }

    public static DamageSource arrow(EntityArrow entityarrow, @Nullable Entity entity) {
        return (new EntityDamageSourceIndirect("arrow", entityarrow, entity)).c();
    }

    public static DamageSource a(Entity entity, @Nullable Entity entity1) {
        return (new EntityDamageSourceIndirect("trident", entity, entity1)).c();
    }

    public static DamageSource a(EntityFireworks entityfireworks, @Nullable Entity entity) {
        return (new EntityDamageSourceIndirect("fireworks", entityfireworks, entity)).setExplosion();
    }

    public static DamageSource fireball(EntityFireballFireball entityfireballfireball, @Nullable Entity entity) {
        return entity == null ? (new EntityDamageSourceIndirect("onFire", entityfireballfireball, entityfireballfireball)).setFire().c() : (new EntityDamageSourceIndirect("fireball", entityfireballfireball, entity)).setFire().c();
    }

    public static DamageSource a(EntityWitherSkull entitywitherskull, Entity entity) {
        return (new EntityDamageSourceIndirect("witherSkull", entitywitherskull, entity)).c();
    }

    public static DamageSource projectile(Entity entity, @Nullable Entity entity1) {
        return (new EntityDamageSourceIndirect("thrown", entity, entity1)).c();
    }

    public static DamageSource c(Entity entity, @Nullable Entity entity1) {
        return (new EntityDamageSourceIndirect("indirectMagic", entity, entity1)).setIgnoreArmor().setMagic();
    }

    public static DamageSource a(Entity entity) {
        return (new EntityDamageSource("thorns", entity)).D().setMagic();
    }

    public static DamageSource explosion(@Nullable Explosion explosion) {
        return d(explosion != null ? explosion.getSource() : null);
    }

    public static DamageSource d(@Nullable EntityLiving entityliving) {
        return entityliving != null ? (new EntityDamageSource("explosion.player", entityliving)).v().setExplosion() : (new DamageSource("explosion")).v().setExplosion();
    }

    public static DamageSource a() {
        return new DamageSourceNetherBed();
    }

    public String toString() {
        return "DamageSource (" + this.msgId + ")";
    }

    public boolean b() {
        return this.isProjectile;
    }

    public DamageSource c() {
        this.isProjectile = true;
        return this;
    }

    public boolean isExplosion() {
        return this.isExplosion;
    }

    public DamageSource setExplosion() {
        this.isExplosion = true;
        return this;
    }

    public boolean ignoresArmor() {
        return this.bypassArmor;
    }

    public boolean g() {
        return this.damageHelmet;
    }

    public float getExhaustionCost() {
        return this.exhaustion;
    }

    public boolean ignoresInvulnerability() {
        return this.bypassInvul;
    }

    public boolean isStarvation() {
        return this.bypassMagic;
    }

    protected DamageSource(String s) {
        this.msgId = s;
    }

    @Nullable
    public Entity k() {
        return this.getEntity();
    }

    @Nullable
    public Entity getEntity() {
        return null;
    }

    protected DamageSource setIgnoreArmor() {
        this.bypassArmor = true;
        this.exhaustion = 0.0F;
        return this;
    }

    protected DamageSource n() {
        this.damageHelmet = true;
        return this;
    }

    protected DamageSource setIgnoresInvulnerability() {
        this.bypassInvul = true;
        return this;
    }

    protected DamageSource setStarvation() {
        this.bypassMagic = true;
        this.exhaustion = 0.0F;
        return this;
    }

    protected DamageSource setFire() {
        this.isFireSource = true;
        return this;
    }

    public DamageSource r() {
        this.noAggro = true;
        return this;
    }

    public IChatBaseComponent getLocalizedDeathMessage(EntityLiving entityliving) {
        EntityLiving entityliving1 = entityliving.getKillingEntity();
        String s = "death.attack." + this.msgId;
        String s1 = s + ".player";

        return entityliving1 != null ? new ChatMessage(s1, new Object[]{entityliving.getScoreboardDisplayName(), entityliving1.getScoreboardDisplayName()}) : new ChatMessage(s, new Object[]{entityliving.getScoreboardDisplayName()});
    }

    public boolean isFire() {
        return this.isFireSource;
    }

    public boolean t() {
        return this.noAggro;
    }

    public String u() {
        return this.msgId;
    }

    public DamageSource v() {
        this.scalesWithDifficulty = true;
        return this;
    }

    public boolean w() {
        return this.scalesWithDifficulty;
    }

    public boolean isMagic() {
        return this.isMagic;
    }

    public DamageSource setMagic() {
        this.isMagic = true;
        return this;
    }

    public boolean z() {
        return this.isFall;
    }

    public DamageSource A() {
        this.isFall = true;
        return this;
    }

    public boolean B() {
        Entity entity = this.getEntity();

        return entity instanceof EntityHuman && ((EntityHuman) entity).getAbilities().instabuild;
    }

    @Nullable
    public Vec3D C() {
        return null;
    }
}
