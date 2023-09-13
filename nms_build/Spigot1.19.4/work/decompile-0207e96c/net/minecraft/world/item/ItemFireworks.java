package net.minecraft.world.item;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.stats.StatisticList;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityFireworks;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;

public class ItemFireworks extends Item {

    public static final byte[] CRAFTABLE_DURATIONS = new byte[]{1, 2, 3};
    public static final String TAG_FIREWORKS = "Fireworks";
    public static final String TAG_EXPLOSION = "Explosion";
    public static final String TAG_EXPLOSIONS = "Explosions";
    public static final String TAG_FLIGHT = "Flight";
    public static final String TAG_EXPLOSION_TYPE = "Type";
    public static final String TAG_EXPLOSION_TRAIL = "Trail";
    public static final String TAG_EXPLOSION_FLICKER = "Flicker";
    public static final String TAG_EXPLOSION_COLORS = "Colors";
    public static final String TAG_EXPLOSION_FADECOLORS = "FadeColors";
    public static final double ROCKET_PLACEMENT_OFFSET = 0.15D;

    public ItemFireworks(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public EnumInteractionResult useOn(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getLevel();

        if (!world.isClientSide) {
            ItemStack itemstack = itemactioncontext.getItemInHand();
            Vec3D vec3d = itemactioncontext.getClickLocation();
            EnumDirection enumdirection = itemactioncontext.getClickedFace();
            EntityFireworks entityfireworks = new EntityFireworks(world, itemactioncontext.getPlayer(), vec3d.x + (double) enumdirection.getStepX() * 0.15D, vec3d.y + (double) enumdirection.getStepY() * 0.15D, vec3d.z + (double) enumdirection.getStepZ() * 0.15D, itemstack);

            world.addFreshEntity(entityfireworks);
            itemstack.shrink(1);
        }

        return EnumInteractionResult.sidedSuccess(world.isClientSide);
    }

    @Override
    public InteractionResultWrapper<ItemStack> use(World world, EntityHuman entityhuman, EnumHand enumhand) {
        if (entityhuman.isFallFlying()) {
            ItemStack itemstack = entityhuman.getItemInHand(enumhand);

            if (!world.isClientSide) {
                EntityFireworks entityfireworks = new EntityFireworks(world, itemstack, entityhuman);

                world.addFreshEntity(entityfireworks);
                if (!entityhuman.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                entityhuman.awardStat(StatisticList.ITEM_USED.get(this));
            }

            return InteractionResultWrapper.sidedSuccess(entityhuman.getItemInHand(enumhand), world.isClientSide());
        } else {
            return InteractionResultWrapper.pass(entityhuman.getItemInHand(enumhand));
        }
    }

    @Override
    public void appendHoverText(ItemStack itemstack, @Nullable World world, List<IChatBaseComponent> list, TooltipFlag tooltipflag) {
        NBTTagCompound nbttagcompound = itemstack.getTagElement("Fireworks");

        if (nbttagcompound != null) {
            if (nbttagcompound.contains("Flight", 99)) {
                list.add(IChatBaseComponent.translatable("item.minecraft.firework_rocket.flight").append(CommonComponents.SPACE).append(String.valueOf(nbttagcompound.getByte("Flight"))).withStyle(EnumChatFormat.GRAY));
            }

            NBTTagList nbttaglist = nbttagcompound.getList("Explosions", 10);

            if (!nbttaglist.isEmpty()) {
                for (int i = 0; i < nbttaglist.size(); ++i) {
                    NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);
                    List<IChatBaseComponent> list1 = Lists.newArrayList();

                    ItemFireworksCharge.appendHoverText(nbttagcompound1, list1);
                    if (!list1.isEmpty()) {
                        for (int j = 1; j < list1.size(); ++j) {
                            list1.set(j, IChatBaseComponent.literal("  ").append((IChatBaseComponent) list1.get(j)).withStyle(EnumChatFormat.GRAY));
                        }

                        list.addAll(list1);
                    }
                }
            }

        }
    }

    public static void setDuration(ItemStack itemstack, byte b0) {
        itemstack.getOrCreateTagElement("Fireworks").putByte("Flight", b0);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemstack = new ItemStack(this);

        setDuration(itemstack, (byte) 1);
        return itemstack;
    }

    public static enum EffectType {

        SMALL_BALL(0, "small_ball"), LARGE_BALL(1, "large_ball"), STAR(2, "star"), CREEPER(3, "creeper"), BURST(4, "burst");

        private static final IntFunction<ItemFireworks.EffectType> BY_ID = ByIdMap.continuous(ItemFireworks.EffectType::getId, values(), ByIdMap.a.ZERO);
        private final int id;
        private final String name;

        private EffectType(int i, String s) {
            this.id = i;
            this.name = s;
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public static ItemFireworks.EffectType byId(int i) {
            return (ItemFireworks.EffectType) ItemFireworks.EffectType.BY_ID.apply(i);
        }
    }
}
