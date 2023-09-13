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

    public static final DamageSource IN_FIRE = (new DamageSource("inFire")).bypassArmor().setIsFire();
    public static final DamageSource LIGHTNING_BOLT = new DamageSource("lightningBolt");
    public static final DamageSource ON_FIRE = (new DamageSource("onFire")).bypassArmor().setIsFire();
    public static final DamageSource LAVA = (new DamageSource("lava")).setIsFire();
    public static final DamageSource HOT_FLOOR = (new DamageSource("hotFloor")).setIsFire();
    public static final DamageSource IN_WALL = (new DamageSource("inWall")).bypassArmor();
    public static final DamageSource CRAMMING = (new DamageSource("cramming")).bypassArmor();
    public static final DamageSource DROWN = (new DamageSource("drown")).bypassArmor();
    public static final DamageSource STARVE = (new DamageSource("starve")).bypassArmor().bypassMagic();
    public static final DamageSource CACTUS = new DamageSource("cactus");
    public static final DamageSource FALL = (new DamageSource("fall")).bypassArmor().setIsFall();
    public static final DamageSource FLY_INTO_WALL = (new DamageSource("flyIntoWall")).bypassArmor();
    public static final DamageSource OUT_OF_WORLD = (new DamageSource("outOfWorld")).bypassArmor().bypassInvul();
    public static final DamageSource GENERIC = (new DamageSource("generic")).bypassArmor();
    public static final DamageSource MAGIC = (new DamageSource("magic")).bypassArmor().setMagic();
    public static final DamageSource WITHER = (new DamageSource("wither")).bypassArmor();
    public static final DamageSource ANVIL = (new DamageSource("anvil")).damageHelmet();
    public static final DamageSource FALLING_BLOCK = (new DamageSource("fallingBlock")).damageHelmet();
    public static final DamageSource DRAGON_BREATH = (new DamageSource("dragonBreath")).bypassArmor();
    public static final DamageSource DRY_OUT = new DamageSource("dryout");
    public static final DamageSource SWEET_BERRY_BUSH = new DamageSource("sweetBerryBush");
    public static final DamageSource FREEZE = (new DamageSource("freeze")).bypassArmor();
    public static final DamageSource FALLING_STALACTITE = (new DamageSource("fallingStalactite")).damageHelmet();
    public static final DamageSource STALAGMITE = (new DamageSource("stalagmite")).bypassArmor().setIsFall();
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

    public static DamageSource sting(EntityLiving entityliving) {
        return new EntityDamageSource("sting", entityliving);
    }

    public static DamageSource mobAttack(EntityLiving entityliving) {
        return new EntityDamageSource("mob", entityliving);
    }

    public static DamageSource indirectMobAttack(Entity entity, @Nullable EntityLiving entityliving) {
        return new EntityDamageSourceIndirect("mob", entity, entityliving);
    }

    public static DamageSource playerAttack(EntityHuman entityhuman) {
        return new EntityDamageSource("player", entityhuman);
    }

    public static DamageSource arrow(EntityArrow entityarrow, @Nullable Entity entity) {
        return (new EntityDamageSourceIndirect("arrow", entityarrow, entity)).setProjectile();
    }

    public static DamageSource trident(Entity entity, @Nullable Entity entity1) {
        return (new EntityDamageSourceIndirect("trident", entity, entity1)).setProjectile();
    }

    public static DamageSource fireworks(EntityFireworks entityfireworks, @Nullable Entity entity) {
        return (new EntityDamageSourceIndirect("fireworks", entityfireworks, entity)).setExplosion();
    }

    public static DamageSource fireball(EntityFireballFireball entityfireballfireball, @Nullable Entity entity) {
        return entity == null ? (new EntityDamageSourceIndirect("onFire", entityfireballfireball, entityfireballfireball)).setIsFire().setProjectile() : (new EntityDamageSourceIndirect("fireball", entityfireballfireball, entity)).setIsFire().setProjectile();
    }

    public static DamageSource witherSkull(EntityWitherSkull entitywitherskull, Entity entity) {
        return (new EntityDamageSourceIndirect("witherSkull", entitywitherskull, entity)).setProjectile();
    }

    public static DamageSource thrown(Entity entity, @Nullable Entity entity1) {
        return (new EntityDamageSourceIndirect("thrown", entity, entity1)).setProjectile();
    }

    public static DamageSource indirectMagic(Entity entity, @Nullable Entity entity1) {
        return (new EntityDamageSourceIndirect("indirectMagic", entity, entity1)).bypassArmor().setMagic();
    }

    public static DamageSource thorns(Entity entity) {
        return (new EntityDamageSource("thorns", entity)).setThorns().setMagic();
    }

    public static DamageSource explosion(@Nullable Explosion explosion) {
        return explosion(explosion != null ? explosion.getSourceMob() : null);
    }

    public static DamageSource explosion(@Nullable EntityLiving entityliving) {
        return entityliving != null ? (new EntityDamageSource("explosion.player", entityliving)).setScalesWithDifficulty().setExplosion() : (new DamageSource("explosion")).setScalesWithDifficulty().setExplosion();
    }

    public static DamageSource badRespawnPointExplosion() {
        return new DamageSourceNetherBed();
    }

    public String toString() {
        return "DamageSource (" + this.msgId + ")";
    }

    public boolean isProjectile() {
        return this.isProjectile;
    }

    public DamageSource setProjectile() {
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

    public boolean isBypassArmor() {
        return this.bypassArmor;
    }

    public boolean isDamageHelmet() {
        return this.damageHelmet;
    }

    public float getFoodExhaustion() {
        return this.exhaustion;
    }

    public boolean isBypassInvul() {
        return this.bypassInvul;
    }

    public boolean isBypassMagic() {
        return this.bypassMagic;
    }

    protected DamageSource(String s) {
        this.msgId = s;
    }

    @Nullable
    public Entity getDirectEntity() {
        return this.getEntity();
    }

    @Nullable
    public Entity getEntity() {
        return null;
    }

    protected DamageSource bypassArmor() {
        this.bypassArmor = true;
        this.exhaustion = 0.0F;
        return this;
    }

    protected DamageSource damageHelmet() {
        this.damageHelmet = true;
        return this;
    }

    protected DamageSource bypassInvul() {
        this.bypassInvul = true;
        return this;
    }

    protected DamageSource bypassMagic() {
        this.bypassMagic = true;
        this.exhaustion = 0.0F;
        return this;
    }

    protected DamageSource setIsFire() {
        this.isFireSource = true;
        return this;
    }

    public DamageSource setNoAggro() {
        this.noAggro = true;
        return this;
    }

    public IChatBaseComponent getLocalizedDeathMessage(EntityLiving entityliving) {
        EntityLiving entityliving1 = entityliving.getKillCredit();
        String s = "death.attack." + this.msgId;
        String s1 = s + ".player";

        return entityliving1 != null ? new ChatMessage(s1, new Object[]{entityliving.getDisplayName(), entityliving1.getDisplayName()}) : new ChatMessage(s, new Object[]{entityliving.getDisplayName()});
    }

    public boolean isFire() {
        return this.isFireSource;
    }

    public boolean isNoAggro() {
        return this.noAggro;
    }

    public String getMsgId() {
        return this.msgId;
    }

    public DamageSource setScalesWithDifficulty() {
        this.scalesWithDifficulty = true;
        return this;
    }

    public boolean scalesWithDifficulty() {
        return this.scalesWithDifficulty;
    }

    public boolean isMagic() {
        return this.isMagic;
    }

    public DamageSource setMagic() {
        this.isMagic = true;
        return this;
    }

    public boolean isFall() {
        return this.isFall;
    }

    public DamageSource setIsFall() {
        this.isFall = true;
        return this;
    }

    public boolean isCreativePlayer() {
        Entity entity = this.getEntity();

        return entity instanceof EntityHuman && ((EntityHuman) entity).getAbilities().instabuild;
    }

    @Nullable
    public Vec3D getSourcePosition() {
        return null;
    }
}
