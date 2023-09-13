package net.minecraft.world.entity.projectile;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.core.IRegistry;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionRegistry;
import net.minecraft.world.item.alchemy.PotionUtil;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.World;

public class EntityTippedArrow extends EntityArrow {

    private static final int EXPOSED_POTION_DECAY_TIME = 600;
    private static final int NO_EFFECT_COLOR = -1;
    private static final DataWatcherObject<Integer> ID_EFFECT_COLOR = DataWatcher.a(EntityTippedArrow.class, DataWatcherRegistry.INT);
    private static final byte EVENT_POTION_PUFF = 0;
    private PotionRegistry potion;
    public final Set<MobEffect> effects;
    private boolean fixedColor;

    public EntityTippedArrow(EntityTypes<? extends EntityTippedArrow> entitytypes, World world) {
        super(entitytypes, world);
        this.potion = Potions.EMPTY;
        this.effects = Sets.newHashSet();
    }

    public EntityTippedArrow(World world, double d0, double d1, double d2) {
        super(EntityTypes.ARROW, d0, d1, d2, world);
        this.potion = Potions.EMPTY;
        this.effects = Sets.newHashSet();
    }

    public EntityTippedArrow(World world, EntityLiving entityliving) {
        super(EntityTypes.ARROW, entityliving, world);
        this.potion = Potions.EMPTY;
        this.effects = Sets.newHashSet();
    }

    public void a(ItemStack itemstack) {
        if (itemstack.a(Items.TIPPED_ARROW)) {
            this.potion = PotionUtil.d(itemstack);
            Collection<MobEffect> collection = PotionUtil.b(itemstack);

            if (!collection.isEmpty()) {
                Iterator iterator = collection.iterator();

                while (iterator.hasNext()) {
                    MobEffect mobeffect = (MobEffect) iterator.next();

                    this.effects.add(new MobEffect(mobeffect));
                }
            }

            int i = c(itemstack);

            if (i == -1) {
                this.A();
            } else {
                this.setColor(i);
            }
        } else if (itemstack.a(Items.ARROW)) {
            this.potion = Potions.EMPTY;
            this.effects.clear();
            this.entityData.set(EntityTippedArrow.ID_EFFECT_COLOR, -1);
        }

    }

    public static int c(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.getTag();

        return nbttagcompound != null && nbttagcompound.hasKeyOfType("CustomPotionColor", 99) ? nbttagcompound.getInt("CustomPotionColor") : -1;
    }

    private void A() {
        this.fixedColor = false;
        if (this.potion == Potions.EMPTY && this.effects.isEmpty()) {
            this.entityData.set(EntityTippedArrow.ID_EFFECT_COLOR, -1);
        } else {
            this.entityData.set(EntityTippedArrow.ID_EFFECT_COLOR, PotionUtil.a((Collection) PotionUtil.a(this.potion, (Collection) this.effects)));
        }

    }

    public void addEffect(MobEffect mobeffect) {
        this.effects.add(mobeffect);
        this.getDataWatcher().set(EntityTippedArrow.ID_EFFECT_COLOR, PotionUtil.a((Collection) PotionUtil.a(this.potion, (Collection) this.effects)));
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityTippedArrow.ID_EFFECT_COLOR, -1);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            if (this.inGround) {
                if (this.inGroundTime % 5 == 0) {
                    this.b(1);
                }
            } else {
                this.b(2);
            }
        } else if (this.inGround && this.inGroundTime != 0 && !this.effects.isEmpty() && this.inGroundTime >= 600) {
            this.level.broadcastEntityEffect(this, (byte) 0);
            this.potion = Potions.EMPTY;
            this.effects.clear();
            this.entityData.set(EntityTippedArrow.ID_EFFECT_COLOR, -1);
        }

    }

    private void b(int i) {
        int j = this.getColor();

        if (j != -1 && i > 0) {
            double d0 = (double) (j >> 16 & 255) / 255.0D;
            double d1 = (double) (j >> 8 & 255) / 255.0D;
            double d2 = (double) (j >> 0 & 255) / 255.0D;

            for (int k = 0; k < i; ++k) {
                this.level.addParticle(Particles.ENTITY_EFFECT, this.d(0.5D), this.da(), this.g(0.5D), d0, d1, d2);
            }

        }
    }

    public int getColor() {
        return (Integer) this.entityData.get(EntityTippedArrow.ID_EFFECT_COLOR);
    }

    public void setColor(int i) {
        this.fixedColor = true;
        this.entityData.set(EntityTippedArrow.ID_EFFECT_COLOR, i);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        if (this.potion != Potions.EMPTY) {
            nbttagcompound.setString("Potion", IRegistry.POTION.getKey(this.potion).toString());
        }

        if (this.fixedColor) {
            nbttagcompound.setInt("Color", this.getColor());
        }

        if (!this.effects.isEmpty()) {
            NBTTagList nbttaglist = new NBTTagList();
            Iterator iterator = this.effects.iterator();

            while (iterator.hasNext()) {
                MobEffect mobeffect = (MobEffect) iterator.next();

                nbttaglist.add(mobeffect.a(new NBTTagCompound()));
            }

            nbttagcompound.set("CustomPotionEffects", nbttaglist);
        }

    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("Potion", 8)) {
            this.potion = PotionUtil.c(nbttagcompound);
        }

        Iterator iterator = PotionUtil.b(nbttagcompound).iterator();

        while (iterator.hasNext()) {
            MobEffect mobeffect = (MobEffect) iterator.next();

            this.addEffect(mobeffect);
        }

        if (nbttagcompound.hasKeyOfType("Color", 99)) {
            this.setColor(nbttagcompound.getInt("Color"));
        } else {
            this.A();
        }

    }

    @Override
    protected void a(EntityLiving entityliving) {
        super.a(entityliving);
        Entity entity = this.x();
        Iterator iterator = this.potion.a().iterator();

        MobEffect mobeffect;

        while (iterator.hasNext()) {
            mobeffect = (MobEffect) iterator.next();
            entityliving.addEffect(new MobEffect(mobeffect.getMobEffect(), Math.max(mobeffect.getDuration() / 8, 1), mobeffect.getAmplifier(), mobeffect.isAmbient(), mobeffect.isShowParticles()), entity);
        }

        if (!this.effects.isEmpty()) {
            iterator = this.effects.iterator();

            while (iterator.hasNext()) {
                mobeffect = (MobEffect) iterator.next();
                entityliving.addEffect(mobeffect, entity);
            }
        }

    }

    @Override
    protected ItemStack getItemStack() {
        if (this.effects.isEmpty() && this.potion == Potions.EMPTY) {
            return new ItemStack(Items.ARROW);
        } else {
            ItemStack itemstack = new ItemStack(Items.TIPPED_ARROW);

            PotionUtil.a(itemstack, this.potion);
            PotionUtil.a(itemstack, (Collection) this.effects);
            if (this.fixedColor) {
                itemstack.getOrCreateTag().setInt("CustomPotionColor", this.getColor());
            }

            return itemstack;
        }
    }

    @Override
    public void a(byte b0) {
        if (b0 == 0) {
            int i = this.getColor();

            if (i != -1) {
                double d0 = (double) (i >> 16 & 255) / 255.0D;
                double d1 = (double) (i >> 8 & 255) / 255.0D;
                double d2 = (double) (i >> 0 & 255) / 255.0D;

                for (int j = 0; j < 20; ++j) {
                    this.level.addParticle(Particles.ENTITY_EFFECT, this.d(0.5D), this.da(), this.g(0.5D), d0, d1, d2);
                }
            }
        } else {
            super.a(b0);
        }

    }
}
