package net.minecraft.world.entity.animal;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketPlayOutGameStateChange;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;

public class EntityPufferFish extends EntityFish {

    private static final DataWatcherObject<Integer> b = DataWatcher.a(EntityPufferFish.class, DataWatcherRegistry.b);
    private int c;
    private int d;
    private static final Predicate<EntityLiving> bo = (entityliving) -> {
        return entityliving == null ? false : (entityliving instanceof EntityHuman && (entityliving.isSpectator() || ((EntityHuman) entityliving).isCreative()) ? false : entityliving.getMonsterType() != EnumMonsterType.WATER_MOB);
    };

    public EntityPufferFish(EntityTypes<? extends EntityPufferFish> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityPufferFish.b, 0);
    }

    public int getPuffState() {
        return (Integer) this.datawatcher.get(EntityPufferFish.b);
    }

    public void setPuffState(int i) {
        this.datawatcher.set(EntityPufferFish.b, i);
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityPufferFish.b.equals(datawatcherobject)) {
            this.updateSize();
        }

        super.a(datawatcherobject);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("PuffState", this.getPuffState());
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.setPuffState(nbttagcompound.getInt("PuffState"));
    }

    @Override
    protected ItemStack eK() {
        return new ItemStack(Items.PUFFERFISH_BUCKET);
    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();
        this.goalSelector.a(1, new EntityPufferFish.a(this));
    }

    @Override
    public void tick() {
        if (!this.world.isClientSide && this.isAlive() && this.doAITick()) {
            if (this.c > 0) {
                if (this.getPuffState() == 0) {
                    this.playSound(SoundEffects.ENTITY_PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.dH());
                    this.setPuffState(1);
                } else if (this.c > 40 && this.getPuffState() == 1) {
                    this.playSound(SoundEffects.ENTITY_PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.dH());
                    this.setPuffState(2);
                }

                ++this.c;
            } else if (this.getPuffState() != 0) {
                if (this.d > 60 && this.getPuffState() == 2) {
                    this.playSound(SoundEffects.ENTITY_PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.dH());
                    this.setPuffState(1);
                } else if (this.d > 100 && this.getPuffState() == 1) {
                    this.playSound(SoundEffects.ENTITY_PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.dH());
                    this.setPuffState(0);
                }

                ++this.d;
            }
        }

        super.tick();
    }

    @Override
    public void movementTick() {
        super.movementTick();
        if (this.isAlive() && this.getPuffState() > 0) {
            List<EntityInsentient> list = this.world.a(EntityInsentient.class, this.getBoundingBox().g(0.3D), EntityPufferFish.bo);
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityInsentient entityinsentient = (EntityInsentient) iterator.next();

                if (entityinsentient.isAlive()) {
                    this.a(entityinsentient);
                }
            }
        }

    }

    private void a(EntityInsentient entityinsentient) {
        int i = this.getPuffState();

        if (entityinsentient.damageEntity(DamageSource.mobAttack(this), (float) (1 + i))) {
            entityinsentient.addEffect(new MobEffect(MobEffects.POISON, 60 * i, 0));
            this.playSound(SoundEffects.ENTITY_PUFFER_FISH_STING, 1.0F, 1.0F);
        }

    }

    @Override
    public void pickup(EntityHuman entityhuman) {
        int i = this.getPuffState();

        if (entityhuman instanceof EntityPlayer && i > 0 && entityhuman.damageEntity(DamageSource.mobAttack(this), (float) (1 + i))) {
            if (!this.isSilent()) {
                ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.j, 0.0F));
            }

            entityhuman.addEffect(new MobEffect(MobEffects.POISON, 60 * i, 0));
        }

    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_PUFFER_FISH_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_PUFFER_FISH_DEATH;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_PUFFER_FISH_HURT;
    }

    @Override
    protected SoundEffect getSoundFlop() {
        return SoundEffects.ENTITY_PUFFER_FISH_FLOP;
    }

    @Override
    public EntitySize a(EntityPose entitypose) {
        return super.a(entitypose).a(s(this.getPuffState()));
    }

    private static float s(int i) {
        switch (i) {
            case 0:
                return 0.5F;
            case 1:
                return 0.7F;
            default:
                return 1.0F;
        }
    }

    static class a extends PathfinderGoal {

        private final EntityPufferFish a;

        public a(EntityPufferFish entitypufferfish) {
            this.a = entitypufferfish;
        }

        @Override
        public boolean a() {
            List<EntityLiving> list = this.a.world.a(EntityLiving.class, this.a.getBoundingBox().g(2.0D), EntityPufferFish.bo);

            return !list.isEmpty();
        }

        @Override
        public void c() {
            this.a.c = 1;
            this.a.d = 0;
        }

        @Override
        public void d() {
            this.a.c = 0;
        }

        @Override
        public boolean b() {
            List<EntityLiving> list = this.a.world.a(EntityLiving.class, this.a.getBoundingBox().g(2.0D), EntityPufferFish.bo);

            return !list.isEmpty();
        }
    }
}
