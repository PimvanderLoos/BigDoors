package net.minecraft.server;

import com.google.common.base.Predicate;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityPotion extends EntityProjectile {

    private static final DataWatcherObject<ItemStack> e = DataWatcher.a(EntityPotion.class, DataWatcherRegistry.f);
    private static final Logger f = LogManager.getLogger();
    public static final Predicate<EntityLiving> d = new Predicate() {
        public boolean a(@Nullable EntityLiving entityliving) {
            return EntityPotion.c(entityliving);
        }

        public boolean apply(@Nullable Object object) {
            return this.a((EntityLiving) object);
        }
    };

    public EntityPotion(World world) {
        super(world);
    }

    public EntityPotion(World world, EntityLiving entityliving, ItemStack itemstack) {
        super(world, entityliving);
        this.setItem(itemstack);
    }

    public EntityPotion(World world, double d0, double d1, double d2, ItemStack itemstack) {
        super(world, d0, d1, d2);
        if (!itemstack.isEmpty()) {
            this.setItem(itemstack);
        }

    }

    protected void i() {
        this.getDataWatcher().register(EntityPotion.e, ItemStack.a);
    }

    public ItemStack getItem() {
        ItemStack itemstack = (ItemStack) this.getDataWatcher().get(EntityPotion.e);

        if (itemstack.getItem() != Items.SPLASH_POTION && itemstack.getItem() != Items.LINGERING_POTION) {
            if (this.world != null) {
                EntityPotion.f.error("ThrownPotion entity {} has no item?!", new Object[] { Integer.valueOf(this.getId())});
            }

            return new ItemStack(Items.SPLASH_POTION);
        } else {
            return itemstack;
        }
    }

    public void setItem(ItemStack itemstack) {
        this.getDataWatcher().set(EntityPotion.e, itemstack);
        this.getDataWatcher().markDirty(EntityPotion.e);
    }

    protected float j() {
        return 0.05F;
    }

    protected void a(MovingObjectPosition movingobjectposition) {
        if (!this.world.isClientSide) {
            ItemStack itemstack = this.getItem();
            PotionRegistry potionregistry = PotionUtil.d(itemstack);
            List list = PotionUtil.getEffects(itemstack);
            boolean flag = potionregistry == Potions.b && list.isEmpty();

            if (movingobjectposition.type == MovingObjectPosition.EnumMovingObjectType.BLOCK && flag) {
                BlockPosition blockposition = movingobjectposition.a().shift(movingobjectposition.direction);

                this.a(blockposition);
                Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                while (iterator.hasNext()) {
                    EnumDirection enumdirection = (EnumDirection) iterator.next();

                    this.a(blockposition.shift(enumdirection));
                }
            }

            if (flag) {
                this.n();
            } else if (!list.isEmpty()) {
                if (this.isLingering()) {
                    this.a(itemstack, potionregistry);
                } else {
                    this.a(movingobjectposition, list);
                }
            }

            int i = potionregistry.c() ? 2007 : 2002;

            this.world.triggerEffect(i, new BlockPosition(this), PotionUtil.c(itemstack));
            this.die();
        }
    }

    private void n() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox().grow(4.0D, 2.0D, 4.0D);
        List list = this.world.a(EntityLiving.class, axisalignedbb, EntityPotion.d);

        if (!list.isEmpty()) {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityLiving entityliving = (EntityLiving) iterator.next();
                double d0 = this.h(entityliving);

                if (d0 < 16.0D && c(entityliving)) {
                    entityliving.damageEntity(DamageSource.DROWN, 1.0F);
                }
            }
        }

    }

    private void a(MovingObjectPosition movingobjectposition, List<MobEffect> list) {
        AxisAlignedBB axisalignedbb = this.getBoundingBox().grow(4.0D, 2.0D, 4.0D);
        List list1 = this.world.a(EntityLiving.class, axisalignedbb);

        if (!list1.isEmpty()) {
            Iterator iterator = list1.iterator();

            while (iterator.hasNext()) {
                EntityLiving entityliving = (EntityLiving) iterator.next();

                if (entityliving.cJ()) {
                    double d0 = this.h(entityliving);

                    if (d0 < 16.0D) {
                        double d1 = 1.0D - Math.sqrt(d0) / 4.0D;

                        if (entityliving == movingobjectposition.entity) {
                            d1 = 1.0D;
                        }

                        Iterator iterator1 = list.iterator();

                        while (iterator1.hasNext()) {
                            MobEffect mobeffect = (MobEffect) iterator1.next();
                            MobEffectList mobeffectlist = mobeffect.getMobEffect();

                            if (mobeffectlist.isInstant()) {
                                mobeffectlist.applyInstantEffect(this, this.getShooter(), entityliving, mobeffect.getAmplifier(), d1);
                            } else {
                                int i = (int) (d1 * (double) mobeffect.getDuration() + 0.5D);

                                if (i > 20) {
                                    entityliving.addEffect(new MobEffect(mobeffectlist, i, mobeffect.getAmplifier(), mobeffect.isAmbient(), mobeffect.isShowParticles()));
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    private void a(ItemStack itemstack, PotionRegistry potionregistry) {
        EntityAreaEffectCloud entityareaeffectcloud = new EntityAreaEffectCloud(this.world, this.locX, this.locY, this.locZ);

        entityareaeffectcloud.setSource(this.getShooter());
        entityareaeffectcloud.setRadius(3.0F);
        entityareaeffectcloud.setRadiusOnUse(-0.5F);
        entityareaeffectcloud.setWaitTime(10);
        entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float) entityareaeffectcloud.getDuration());
        entityareaeffectcloud.a(potionregistry);
        Iterator iterator = PotionUtil.b(itemstack).iterator();

        while (iterator.hasNext()) {
            MobEffect mobeffect = (MobEffect) iterator.next();

            entityareaeffectcloud.a(new MobEffect(mobeffect));
        }

        NBTTagCompound nbttagcompound = itemstack.getTag();

        if (nbttagcompound != null && nbttagcompound.hasKeyOfType("CustomPotionColor", 99)) {
            entityareaeffectcloud.setColor(nbttagcompound.getInt("CustomPotionColor"));
        }

        this.world.addEntity(entityareaeffectcloud);
    }

    public boolean isLingering() {
        return this.getItem().getItem() == Items.LINGERING_POTION;
    }

    private void a(BlockPosition blockposition) {
        if (this.world.getType(blockposition).getBlock() == Blocks.FIRE) {
            this.world.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 2);
        }

    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityProjectile.a(dataconvertermanager, "ThrownPotion");
        dataconvertermanager.a(DataConverterTypes.ENTITY, (DataInspector) (new DataInspectorItem(EntityPotion.class, new String[] { "Potion"})));
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        ItemStack itemstack = new ItemStack(nbttagcompound.getCompound("Potion"));

        if (itemstack.isEmpty()) {
            this.die();
        } else {
            this.setItem(itemstack);
        }

    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        ItemStack itemstack = this.getItem();

        if (!itemstack.isEmpty()) {
            nbttagcompound.set("Potion", itemstack.save(new NBTTagCompound()));
        }

    }

    private static boolean c(EntityLiving entityliving) {
        return entityliving instanceof EntityEnderman || entityliving instanceof EntityBlaze;
    }
}
