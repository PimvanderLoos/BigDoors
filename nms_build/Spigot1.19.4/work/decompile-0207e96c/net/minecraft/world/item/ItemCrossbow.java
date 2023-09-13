package net.minecraft.world.item;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.EnumHand;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.monster.ICrossbow;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.entity.projectile.EntityFireworks;
import net.minecraft.world.entity.projectile.IProjectile;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ItemCrossbow extends ItemProjectileWeapon implements ItemVanishable {

    private static final String TAG_CHARGED = "Charged";
    private static final String TAG_CHARGED_PROJECTILES = "ChargedProjectiles";
    private static final int MAX_CHARGE_DURATION = 25;
    public static final int DEFAULT_RANGE = 8;
    private boolean startSoundPlayed = false;
    private boolean midLoadSoundPlayed = false;
    private static final float START_SOUND_PERCENT = 0.2F;
    private static final float MID_SOUND_PERCENT = 0.5F;
    private static final float ARROW_POWER = 3.15F;
    private static final float FIREWORK_POWER = 1.6F;

    public ItemCrossbow(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public Predicate<ItemStack> getSupportedHeldProjectiles() {
        return ItemCrossbow.ARROW_OR_FIREWORK;
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return ItemCrossbow.ARROW_ONLY;
    }

    @Override
    public InteractionResultWrapper<ItemStack> use(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);

        if (isCharged(itemstack)) {
            performShooting(world, entityhuman, enumhand, itemstack, getShootingPower(itemstack), 1.0F);
            setCharged(itemstack, false);
            return InteractionResultWrapper.consume(itemstack);
        } else if (!entityhuman.getProjectile(itemstack).isEmpty()) {
            if (!isCharged(itemstack)) {
                this.startSoundPlayed = false;
                this.midLoadSoundPlayed = false;
                entityhuman.startUsingItem(enumhand);
            }

            return InteractionResultWrapper.consume(itemstack);
        } else {
            return InteractionResultWrapper.fail(itemstack);
        }
    }

    private static float getShootingPower(ItemStack itemstack) {
        return containsChargedProjectile(itemstack, Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
    }

    @Override
    public void releaseUsing(ItemStack itemstack, World world, EntityLiving entityliving, int i) {
        int j = this.getUseDuration(itemstack) - i;
        float f = getPowerForTime(j, itemstack);

        if (f >= 1.0F && !isCharged(itemstack) && tryLoadProjectiles(entityliving, itemstack)) {
            setCharged(itemstack, true);
            SoundCategory soundcategory = entityliving instanceof EntityHuman ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;

            world.playSound((EntityHuman) null, entityliving.getX(), entityliving.getY(), entityliving.getZ(), SoundEffects.CROSSBOW_LOADING_END, soundcategory, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F);
        }

    }

    private static boolean tryLoadProjectiles(EntityLiving entityliving, ItemStack itemstack) {
        int i = EnchantmentManager.getItemEnchantmentLevel(Enchantments.MULTISHOT, itemstack);
        int j = i == 0 ? 1 : 3;
        boolean flag = entityliving instanceof EntityHuman && ((EntityHuman) entityliving).getAbilities().instabuild;
        ItemStack itemstack1 = entityliving.getProjectile(itemstack);
        ItemStack itemstack2 = itemstack1.copy();

        for (int k = 0; k < j; ++k) {
            if (k > 0) {
                itemstack1 = itemstack2.copy();
            }

            if (itemstack1.isEmpty() && flag) {
                itemstack1 = new ItemStack(Items.ARROW);
                itemstack2 = itemstack1.copy();
            }

            if (!loadProjectile(entityliving, itemstack, itemstack1, k > 0, flag)) {
                return false;
            }
        }

        return true;
    }

    private static boolean loadProjectile(EntityLiving entityliving, ItemStack itemstack, ItemStack itemstack1, boolean flag, boolean flag1) {
        if (itemstack1.isEmpty()) {
            return false;
        } else {
            boolean flag2 = flag1 && itemstack1.getItem() instanceof ItemArrow;
            ItemStack itemstack2;

            if (!flag2 && !flag1 && !flag) {
                itemstack2 = itemstack1.split(1);
                if (itemstack1.isEmpty() && entityliving instanceof EntityHuman) {
                    ((EntityHuman) entityliving).getInventory().removeItem(itemstack1);
                }
            } else {
                itemstack2 = itemstack1.copy();
            }

            addChargedProjectile(itemstack, itemstack2);
            return true;
        }
    }

    public static boolean isCharged(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.getTag();

        return nbttagcompound != null && nbttagcompound.getBoolean("Charged");
    }

    public static void setCharged(ItemStack itemstack, boolean flag) {
        NBTTagCompound nbttagcompound = itemstack.getOrCreateTag();

        nbttagcompound.putBoolean("Charged", flag);
    }

    private static void addChargedProjectile(ItemStack itemstack, ItemStack itemstack1) {
        NBTTagCompound nbttagcompound = itemstack.getOrCreateTag();
        NBTTagList nbttaglist;

        if (nbttagcompound.contains("ChargedProjectiles", 9)) {
            nbttaglist = nbttagcompound.getList("ChargedProjectiles", 10);
        } else {
            nbttaglist = new NBTTagList();
        }

        NBTTagCompound nbttagcompound1 = new NBTTagCompound();

        itemstack1.save(nbttagcompound1);
        nbttaglist.add(nbttagcompound1);
        nbttagcompound.put("ChargedProjectiles", nbttaglist);
    }

    private static List<ItemStack> getChargedProjectiles(ItemStack itemstack) {
        List<ItemStack> list = Lists.newArrayList();
        NBTTagCompound nbttagcompound = itemstack.getTag();

        if (nbttagcompound != null && nbttagcompound.contains("ChargedProjectiles", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("ChargedProjectiles", 10);

            if (nbttaglist != null) {
                for (int i = 0; i < nbttaglist.size(); ++i) {
                    NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);

                    list.add(ItemStack.of(nbttagcompound1));
                }
            }
        }

        return list;
    }

    private static void clearChargedProjectiles(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.getTag();

        if (nbttagcompound != null) {
            NBTTagList nbttaglist = nbttagcompound.getList("ChargedProjectiles", 9);

            nbttaglist.clear();
            nbttagcompound.put("ChargedProjectiles", nbttaglist);
        }

    }

    public static boolean containsChargedProjectile(ItemStack itemstack, Item item) {
        return getChargedProjectiles(itemstack).stream().anyMatch((itemstack1) -> {
            return itemstack1.is(item);
        });
    }

    private static void shootProjectile(World world, EntityLiving entityliving, EnumHand enumhand, ItemStack itemstack, ItemStack itemstack1, float f, boolean flag, float f1, float f2, float f3) {
        if (!world.isClientSide) {
            boolean flag1 = itemstack1.is(Items.FIREWORK_ROCKET);
            Object object;

            if (flag1) {
                object = new EntityFireworks(world, itemstack1, entityliving, entityliving.getX(), entityliving.getEyeY() - 0.15000000596046448D, entityliving.getZ(), true);
            } else {
                object = getArrow(world, entityliving, itemstack, itemstack1);
                if (flag || f3 != 0.0F) {
                    ((EntityArrow) object).pickup = EntityArrow.PickupStatus.CREATIVE_ONLY;
                }
            }

            if (entityliving instanceof ICrossbow) {
                ICrossbow icrossbow = (ICrossbow) entityliving;

                icrossbow.shootCrossbowProjectile(icrossbow.getTarget(), itemstack, (IProjectile) object, f3);
            } else {
                Vec3D vec3d = entityliving.getUpVector(1.0F);
                Quaternionf quaternionf = (new Quaternionf()).setAngleAxis((double) (f3 * 0.017453292F), vec3d.x, vec3d.y, vec3d.z);
                Vec3D vec3d1 = entityliving.getViewVector(1.0F);
                Vector3f vector3f = vec3d1.toVector3f().rotate(quaternionf);

                ((IProjectile) object).shoot((double) vector3f.x(), (double) vector3f.y(), (double) vector3f.z(), f1, f2);
            }

            itemstack.hurtAndBreak(flag1 ? 3 : 1, entityliving, (entityliving1) -> {
                entityliving1.broadcastBreakEvent(enumhand);
            });
            world.addFreshEntity((Entity) object);
            world.playSound((EntityHuman) null, entityliving.getX(), entityliving.getY(), entityliving.getZ(), SoundEffects.CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0F, f);
        }
    }

    private static EntityArrow getArrow(World world, EntityLiving entityliving, ItemStack itemstack, ItemStack itemstack1) {
        ItemArrow itemarrow = (ItemArrow) (itemstack1.getItem() instanceof ItemArrow ? itemstack1.getItem() : Items.ARROW);
        EntityArrow entityarrow = itemarrow.createArrow(world, itemstack1, entityliving);

        if (entityliving instanceof EntityHuman) {
            entityarrow.setCritArrow(true);
        }

        entityarrow.setSoundEvent(SoundEffects.CROSSBOW_HIT);
        entityarrow.setShotFromCrossbow(true);
        int i = EnchantmentManager.getItemEnchantmentLevel(Enchantments.PIERCING, itemstack);

        if (i > 0) {
            entityarrow.setPierceLevel((byte) i);
        }

        return entityarrow;
    }

    public static void performShooting(World world, EntityLiving entityliving, EnumHand enumhand, ItemStack itemstack, float f, float f1) {
        List<ItemStack> list = getChargedProjectiles(itemstack);
        float[] afloat = getShotPitches(entityliving.getRandom());

        for (int i = 0; i < list.size(); ++i) {
            ItemStack itemstack1 = (ItemStack) list.get(i);
            boolean flag = entityliving instanceof EntityHuman && ((EntityHuman) entityliving).getAbilities().instabuild;

            if (!itemstack1.isEmpty()) {
                if (i == 0) {
                    shootProjectile(world, entityliving, enumhand, itemstack, itemstack1, afloat[i], flag, f, f1, 0.0F);
                } else if (i == 1) {
                    shootProjectile(world, entityliving, enumhand, itemstack, itemstack1, afloat[i], flag, f, f1, -10.0F);
                } else if (i == 2) {
                    shootProjectile(world, entityliving, enumhand, itemstack, itemstack1, afloat[i], flag, f, f1, 10.0F);
                }
            }
        }

        onCrossbowShot(world, entityliving, itemstack);
    }

    private static float[] getShotPitches(RandomSource randomsource) {
        boolean flag = randomsource.nextBoolean();

        return new float[]{1.0F, getRandomShotPitch(flag, randomsource), getRandomShotPitch(!flag, randomsource)};
    }

    private static float getRandomShotPitch(boolean flag, RandomSource randomsource) {
        float f = flag ? 0.63F : 0.43F;

        return 1.0F / (randomsource.nextFloat() * 0.5F + 1.8F) + f;
    }

    private static void onCrossbowShot(World world, EntityLiving entityliving, ItemStack itemstack) {
        if (entityliving instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) entityliving;

            if (!world.isClientSide) {
                CriterionTriggers.SHOT_CROSSBOW.trigger(entityplayer, itemstack);
            }

            entityplayer.awardStat(StatisticList.ITEM_USED.get(itemstack.getItem()));
        }

        clearChargedProjectiles(itemstack);
    }

    @Override
    public void onUseTick(World world, EntityLiving entityliving, ItemStack itemstack, int i) {
        if (!world.isClientSide) {
            int j = EnchantmentManager.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, itemstack);
            SoundEffect soundeffect = this.getStartSound(j);
            SoundEffect soundeffect1 = j == 0 ? SoundEffects.CROSSBOW_LOADING_MIDDLE : null;
            float f = (float) (itemstack.getUseDuration() - i) / (float) getChargeDuration(itemstack);

            if (f < 0.2F) {
                this.startSoundPlayed = false;
                this.midLoadSoundPlayed = false;
            }

            if (f >= 0.2F && !this.startSoundPlayed) {
                this.startSoundPlayed = true;
                world.playSound((EntityHuman) null, entityliving.getX(), entityliving.getY(), entityliving.getZ(), soundeffect, SoundCategory.PLAYERS, 0.5F, 1.0F);
            }

            if (f >= 0.5F && soundeffect1 != null && !this.midLoadSoundPlayed) {
                this.midLoadSoundPlayed = true;
                world.playSound((EntityHuman) null, entityliving.getX(), entityliving.getY(), entityliving.getZ(), soundeffect1, SoundCategory.PLAYERS, 0.5F, 1.0F);
            }
        }

    }

    @Override
    public int getUseDuration(ItemStack itemstack) {
        return getChargeDuration(itemstack) + 3;
    }

    public static int getChargeDuration(ItemStack itemstack) {
        int i = EnchantmentManager.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, itemstack);

        return i == 0 ? 25 : 25 - 5 * i;
    }

    @Override
    public EnumAnimation getUseAnimation(ItemStack itemstack) {
        return EnumAnimation.CROSSBOW;
    }

    private SoundEffect getStartSound(int i) {
        switch (i) {
            case 1:
                return SoundEffects.CROSSBOW_QUICK_CHARGE_1;
            case 2:
                return SoundEffects.CROSSBOW_QUICK_CHARGE_2;
            case 3:
                return SoundEffects.CROSSBOW_QUICK_CHARGE_3;
            default:
                return SoundEffects.CROSSBOW_LOADING_START;
        }
    }

    private static float getPowerForTime(int i, ItemStack itemstack) {
        float f = (float) i / (float) getChargeDuration(itemstack);

        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    @Override
    public void appendHoverText(ItemStack itemstack, @Nullable World world, List<IChatBaseComponent> list, TooltipFlag tooltipflag) {
        List<ItemStack> list1 = getChargedProjectiles(itemstack);

        if (isCharged(itemstack) && !list1.isEmpty()) {
            ItemStack itemstack1 = (ItemStack) list1.get(0);

            list.add(IChatBaseComponent.translatable("item.minecraft.crossbow.projectile").append(CommonComponents.SPACE).append(itemstack1.getDisplayName()));
            if (tooltipflag.isAdvanced() && itemstack1.is(Items.FIREWORK_ROCKET)) {
                List<IChatBaseComponent> list2 = Lists.newArrayList();

                Items.FIREWORK_ROCKET.appendHoverText(itemstack1, world, list2, tooltipflag);
                if (!list2.isEmpty()) {
                    for (int i = 0; i < list2.size(); ++i) {
                        list2.set(i, IChatBaseComponent.literal("  ").append((IChatBaseComponent) list2.get(i)).withStyle(EnumChatFormat.GRAY));
                    }

                    list.addAll(list2);
                }
            }

        }
    }

    @Override
    public boolean useOnRelease(ItemStack itemstack) {
        return itemstack.is((Item) this);
    }

    @Override
    public int getDefaultProjectileRange() {
        return 8;
    }
}
