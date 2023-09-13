package net.minecraft.world.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.food.FoodInfo;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.RayTrace;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Item implements IMaterial {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final Map<Block, Item> BY_BLOCK = Maps.newHashMap();
    protected static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    protected static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    public static final int MAX_STACK_SIZE = 64;
    public static final int EAT_DURATION = 32;
    public static final int MAX_BAR_WIDTH = 13;
    protected final CreativeModeTab category;
    private final EnumItemRarity rarity;
    private final int maxStackSize;
    private final int maxDamage;
    private final boolean isFireResistant;
    private final Item craftingRemainingItem;
    @Nullable
    private String descriptionId;
    @Nullable
    private final FoodInfo foodProperties;

    public static int getId(Item item) {
        return item == null ? 0 : IRegistry.ITEM.getId(item);
    }

    public static Item getById(int i) {
        return (Item) IRegistry.ITEM.fromId(i);
    }

    @Deprecated
    public static Item getItemOf(Block block) {
        return (Item) Item.BY_BLOCK.getOrDefault(block, Items.AIR);
    }

    public Item(Item.Info item_info) {
        this.category = item_info.category;
        this.rarity = item_info.rarity;
        this.craftingRemainingItem = item_info.craftingRemainingItem;
        this.maxDamage = item_info.maxDamage;
        this.maxStackSize = item_info.maxStackSize;
        this.foodProperties = item_info.foodProperties;
        this.isFireResistant = item_info.isFireResistant;
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            String s = this.getClass().getSimpleName();

            if (!s.endsWith("Item")) {
                Item.LOGGER.error("Item classes should end with Item and {} doesn't.", s);
            }
        }

    }

    public void a(World world, EntityLiving entityliving, ItemStack itemstack, int i) {}

    public void a(EntityItem entityitem) {}

    public void b(NBTTagCompound nbttagcompound) {}

    public boolean a(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman) {
        return true;
    }

    @Override
    public Item getItem() {
        return this;
    }

    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        return EnumInteractionResult.PASS;
    }

    public float getDestroySpeed(ItemStack itemstack, IBlockData iblockdata) {
        return 1.0F;
    }

    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        if (this.isFood()) {
            ItemStack itemstack = entityhuman.b(enumhand);

            if (entityhuman.s(this.getFoodInfo().d())) {
                entityhuman.c(enumhand);
                return InteractionResultWrapper.consume(itemstack);
            } else {
                return InteractionResultWrapper.fail(itemstack);
            }
        } else {
            return InteractionResultWrapper.pass(entityhuman.b(enumhand));
        }
    }

    public ItemStack a(ItemStack itemstack, World world, EntityLiving entityliving) {
        return this.isFood() ? entityliving.a(world, itemstack) : itemstack;
    }

    public final int getMaxStackSize() {
        return this.maxStackSize;
    }

    public final int getMaxDurability() {
        return this.maxDamage;
    }

    public boolean usesDurability() {
        return this.maxDamage > 0;
    }

    public boolean e(ItemStack itemstack) {
        return itemstack.g();
    }

    public int f(ItemStack itemstack) {
        return Math.round(13.0F - (float) itemstack.getDamage() * 13.0F / (float) this.maxDamage);
    }

    public int g(ItemStack itemstack) {
        float f = Math.max(0.0F, ((float) this.maxDamage - (float) itemstack.getDamage()) / (float) this.maxDamage);

        return MathHelper.g(f / 3.0F, 1.0F, 1.0F);
    }

    public boolean a(ItemStack itemstack, Slot slot, ClickAction clickaction, EntityHuman entityhuman) {
        return false;
    }

    public boolean a(ItemStack itemstack, ItemStack itemstack1, Slot slot, ClickAction clickaction, EntityHuman entityhuman, SlotAccess slotaccess) {
        return false;
    }

    public boolean a(ItemStack itemstack, EntityLiving entityliving, EntityLiving entityliving1) {
        return false;
    }

    public boolean a(ItemStack itemstack, World world, IBlockData iblockdata, BlockPosition blockposition, EntityLiving entityliving) {
        return false;
    }

    public boolean canDestroySpecialBlock(IBlockData iblockdata) {
        return false;
    }

    public EnumInteractionResult a(ItemStack itemstack, EntityHuman entityhuman, EntityLiving entityliving, EnumHand enumhand) {
        return EnumInteractionResult.PASS;
    }

    public IChatBaseComponent o() {
        return new ChatMessage(this.getName());
    }

    public String toString() {
        return IRegistry.ITEM.getKey(this).getKey();
    }

    protected String p() {
        if (this.descriptionId == null) {
            this.descriptionId = SystemUtils.a("item", IRegistry.ITEM.getKey(this));
        }

        return this.descriptionId;
    }

    public String getName() {
        return this.p();
    }

    public String j(ItemStack itemstack) {
        return this.getName();
    }

    public boolean q() {
        return true;
    }

    @Nullable
    public final Item getCraftingRemainingItem() {
        return this.craftingRemainingItem;
    }

    public boolean s() {
        return this.craftingRemainingItem != null;
    }

    public void a(ItemStack itemstack, World world, Entity entity, int i, boolean flag) {}

    public void b(ItemStack itemstack, World world, EntityHuman entityhuman) {}

    public boolean M_() {
        return false;
    }

    public EnumAnimation c(ItemStack itemstack) {
        return itemstack.getItem().isFood() ? EnumAnimation.EAT : EnumAnimation.NONE;
    }

    public int b(ItemStack itemstack) {
        return itemstack.getItem().isFood() ? (this.getFoodInfo().e() ? 16 : 32) : 0;
    }

    public void a(ItemStack itemstack, World world, EntityLiving entityliving, int i) {}

    public void a(ItemStack itemstack, @Nullable World world, List<IChatBaseComponent> list, TooltipFlag tooltipflag) {}

    public Optional<TooltipComponent> h(ItemStack itemstack) {
        return Optional.empty();
    }

    public IChatBaseComponent m(ItemStack itemstack) {
        return new ChatMessage(this.j(itemstack));
    }

    public boolean i(ItemStack itemstack) {
        return itemstack.hasEnchantments();
    }

    public EnumItemRarity n(ItemStack itemstack) {
        if (!itemstack.hasEnchantments()) {
            return this.rarity;
        } else {
            switch (this.rarity) {
                case COMMON:
                case UNCOMMON:
                    return EnumItemRarity.RARE;
                case RARE:
                    return EnumItemRarity.EPIC;
                case EPIC:
                default:
                    return this.rarity;
            }
        }
    }

    public boolean a(ItemStack itemstack) {
        return this.getMaxStackSize() == 1 && this.usesDurability();
    }

    protected static MovingObjectPositionBlock a(World world, EntityHuman entityhuman, RayTrace.FluidCollisionOption raytrace_fluidcollisionoption) {
        float f = entityhuman.getXRot();
        float f1 = entityhuman.getYRot();
        Vec3D vec3d = entityhuman.bb();
        float f2 = MathHelper.cos(-f1 * 0.017453292F - 3.1415927F);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - 3.1415927F);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = 5.0D;
        Vec3D vec3d1 = vec3d.add((double) f6 * 5.0D, (double) f5 * 5.0D, (double) f7 * 5.0D);

        return world.rayTrace(new RayTrace(vec3d, vec3d1, RayTrace.BlockCollisionOption.OUTLINE, raytrace_fluidcollisionoption, entityhuman));
    }

    public int c() {
        return 0;
    }

    public void a(CreativeModeTab creativemodetab, NonNullList<ItemStack> nonnulllist) {
        if (this.a(creativemodetab)) {
            nonnulllist.add(new ItemStack(this));
        }

    }

    protected boolean a(CreativeModeTab creativemodetab) {
        CreativeModeTab creativemodetab1 = this.t();

        return creativemodetab1 != null && (creativemodetab == CreativeModeTab.TAB_SEARCH || creativemodetab == creativemodetab1);
    }

    @Nullable
    public final CreativeModeTab t() {
        return this.category;
    }

    public boolean a(ItemStack itemstack, ItemStack itemstack1) {
        return false;
    }

    public Multimap<AttributeBase, AttributeModifier> a(EnumItemSlot enumitemslot) {
        return ImmutableMultimap.of();
    }

    public boolean l(ItemStack itemstack) {
        return false;
    }

    public ItemStack createItemStack() {
        return new ItemStack(this);
    }

    public boolean isFood() {
        return this.foodProperties != null;
    }

    @Nullable
    public FoodInfo getFoodInfo() {
        return this.foodProperties;
    }

    public SoundEffect O_() {
        return SoundEffects.GENERIC_DRINK;
    }

    public SoundEffect h() {
        return SoundEffects.GENERIC_EAT;
    }

    public boolean w() {
        return this.isFireResistant;
    }

    public boolean a(DamageSource damagesource) {
        return !this.isFireResistant || !damagesource.isFire();
    }

    @Nullable
    public SoundEffect g() {
        return null;
    }

    public boolean P_() {
        return true;
    }

    public static class Info {

        int maxStackSize = 64;
        int maxDamage;
        Item craftingRemainingItem;
        CreativeModeTab category;
        EnumItemRarity rarity;
        FoodInfo foodProperties;
        boolean isFireResistant;

        public Info() {
            this.rarity = EnumItemRarity.COMMON;
        }

        public Item.Info a(FoodInfo foodinfo) {
            this.foodProperties = foodinfo;
            return this;
        }

        public Item.Info a(int i) {
            if (this.maxDamage > 0) {
                throw new RuntimeException("Unable to have damage AND stack.");
            } else {
                this.maxStackSize = i;
                return this;
            }
        }

        public Item.Info b(int i) {
            return this.maxDamage == 0 ? this.c(i) : this;
        }

        public Item.Info c(int i) {
            this.maxDamage = i;
            this.maxStackSize = 1;
            return this;
        }

        public Item.Info a(Item item) {
            this.craftingRemainingItem = item;
            return this;
        }

        public Item.Info a(CreativeModeTab creativemodetab) {
            this.category = creativemodetab;
            return this;
        }

        public Item.Info a(EnumItemRarity enumitemrarity) {
            this.rarity = enumitemrarity;
            return this;
        }

        public Item.Info a() {
            this.isFireResistant = true;
            return this;
        }
    }
}
