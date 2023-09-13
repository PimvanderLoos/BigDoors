package net.minecraft.world.entity.monster.piglin;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.Navigation;
import net.minecraft.world.entity.ai.util.PathfinderGoalUtil;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.entity.monster.EntityPigZombie;
import net.minecraft.world.item.ItemToolMaterial;
import net.minecraft.world.level.World;
import net.minecraft.world.level.pathfinder.PathType;

public abstract class EntityPiglinAbstract extends EntityMonster {

    protected static final DataWatcherObject<Boolean> b = DataWatcher.a(EntityPiglinAbstract.class, DataWatcherRegistry.i);
    public int conversionTicks = 0;

    public EntityPiglinAbstract(EntityTypes<? extends EntityPiglinAbstract> entitytypes, World world) {
        super(entitytypes, world);
        this.setCanPickupLoot(true);
        this.eS();
        this.a(PathType.DANGER_FIRE, 16.0F);
        this.a(PathType.DAMAGE_FIRE, -1.0F);
    }

    private void eS() {
        if (PathfinderGoalUtil.a(this)) {
            ((Navigation) this.getNavigation()).a(true);
        }

    }

    protected abstract boolean m();

    public void setImmuneToZombification(boolean flag) {
        this.getDataWatcher().set(EntityPiglinAbstract.b, flag);
    }

    public boolean isImmuneToZombification() {
        return (Boolean) this.getDataWatcher().get(EntityPiglinAbstract.b);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityPiglinAbstract.b, false);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        if (this.isImmuneToZombification()) {
            nbttagcompound.setBoolean("IsImmuneToZombification", true);
        }

        nbttagcompound.setInt("TimeInOverworld", this.conversionTicks);
    }

    @Override
    public double bb() {
        return this.isBaby() ? -0.05D : -0.45D;
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.setImmuneToZombification(nbttagcompound.getBoolean("IsImmuneToZombification"));
        this.conversionTicks = nbttagcompound.getInt("TimeInOverworld");
    }

    @Override
    protected void mobTick() {
        super.mobTick();
        if (this.isConverting()) {
            ++this.conversionTicks;
        } else {
            this.conversionTicks = 0;
        }

        if (this.conversionTicks > 300) {
            this.eP();
            this.c((WorldServer) this.world);
        }

    }

    public boolean isConverting() {
        return !this.world.getDimensionManager().isPiglinSafe() && !this.isImmuneToZombification() && !this.isNoAI();
    }

    protected void c(WorldServer worldserver) {
        EntityPigZombie entitypigzombie = (EntityPigZombie) this.a(EntityTypes.ZOMBIFIED_PIGLIN, true);

        if (entitypigzombie != null) {
            entitypigzombie.addEffect(new MobEffect(MobEffects.CONFUSION, 200, 0));
        }

    }

    public boolean eM() {
        return !this.isBaby();
    }

    @Nullable
    @Override
    public EntityLiving getGoalTarget() {
        return (EntityLiving) this.bg.getMemory(MemoryModuleType.ATTACK_TARGET).orElse((Object) null);
    }

    protected boolean eO() {
        return this.getItemInMainHand().getItem() instanceof ItemToolMaterial;
    }

    @Override
    public void F() {
        if (PiglinAI.d(this)) {
            super.F();
        }

    }

    @Override
    protected void M() {
        super.M();
        PacketDebug.a((EntityLiving) this);
    }

    protected abstract void eP();
}
