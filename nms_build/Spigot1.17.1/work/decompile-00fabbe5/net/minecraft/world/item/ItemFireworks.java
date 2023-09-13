package net.minecraft.world.item;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityFireworks;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;

public class ItemFireworks extends Item {

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
    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getWorld();

        if (!world.isClientSide) {
            ItemStack itemstack = itemactioncontext.getItemStack();
            Vec3D vec3d = itemactioncontext.getPos();
            EnumDirection enumdirection = itemactioncontext.getClickedFace();
            EntityFireworks entityfireworks = new EntityFireworks(world, itemactioncontext.getEntity(), vec3d.x + (double) enumdirection.getAdjacentX() * 0.15D, vec3d.y + (double) enumdirection.getAdjacentY() * 0.15D, vec3d.z + (double) enumdirection.getAdjacentZ() * 0.15D, itemstack);

            world.addEntity(entityfireworks);
            itemstack.subtract(1);
        }

        return EnumInteractionResult.a(world.isClientSide);
    }

    @Override
    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        if (entityhuman.isGliding()) {
            ItemStack itemstack = entityhuman.b(enumhand);

            if (!world.isClientSide) {
                EntityFireworks entityfireworks = new EntityFireworks(world, itemstack, entityhuman);

                world.addEntity(entityfireworks);
                if (!entityhuman.getAbilities().instabuild) {
                    itemstack.subtract(1);
                }

                entityhuman.b(StatisticList.ITEM_USED.b(this));
            }

            return InteractionResultWrapper.a(entityhuman.b(enumhand), world.isClientSide());
        } else {
            return InteractionResultWrapper.pass(entityhuman.b(enumhand));
        }
    }

    @Override
    public void a(ItemStack itemstack, @Nullable World world, List<IChatBaseComponent> list, TooltipFlag tooltipflag) {
        NBTTagCompound nbttagcompound = itemstack.b("Fireworks");

        if (nbttagcompound != null) {
            if (nbttagcompound.hasKeyOfType("Flight", 99)) {
                list.add((new ChatMessage("item.minecraft.firework_rocket.flight")).c(" ").c(String.valueOf(nbttagcompound.getByte("Flight"))).a(EnumChatFormat.GRAY));
            }

            NBTTagList nbttaglist = nbttagcompound.getList("Explosions", 10);

            if (!nbttaglist.isEmpty()) {
                for (int i = 0; i < nbttaglist.size(); ++i) {
                    NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);
                    List<IChatBaseComponent> list1 = Lists.newArrayList();

                    ItemFireworksCharge.a(nbttagcompound1, (List) list1);
                    if (!list1.isEmpty()) {
                        for (int j = 1; j < list1.size(); ++j) {
                            list1.set(j, (new ChatComponentText("  ")).addSibling((IChatBaseComponent) list1.get(j)).a(EnumChatFormat.GRAY));
                        }

                        list.addAll(list1);
                    }
                }
            }

        }
    }

    @Override
    public ItemStack createItemStack() {
        ItemStack itemstack = new ItemStack(this);

        itemstack.getOrCreateTag().setByte("Flight", (byte) 1);
        return itemstack;
    }

    public static enum EffectType {

        SMALL_BALL(0, "small_ball"), LARGE_BALL(1, "large_ball"), STAR(2, "star"), CREEPER(3, "creeper"), BURST(4, "burst");

        private static final ItemFireworks.EffectType[] BY_ID = (ItemFireworks.EffectType[]) Arrays.stream(values()).sorted(Comparator.comparingInt((itemfireworks_effecttype) -> {
            return itemfireworks_effecttype.id;
        })).toArray((i) -> {
            return new ItemFireworks.EffectType[i];
        });
        private final int id;
        private final String name;

        private EffectType(int i, String s) {
            this.id = i;
            this.name = s;
        }

        public int a() {
            return this.id;
        }

        public String b() {
            return this.name;
        }

        public static ItemFireworks.EffectType a(int i) {
            return i >= 0 && i < ItemFireworks.EffectType.BY_ID.length ? ItemFireworks.EffectType.BY_ID[i] : ItemFireworks.EffectType.SMALL_BALL;
        }
    }
}
