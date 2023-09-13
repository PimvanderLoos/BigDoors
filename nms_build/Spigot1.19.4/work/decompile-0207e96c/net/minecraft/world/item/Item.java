package net.minecraft.world.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.DamageTypeTags;
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
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
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
import org.slf4j.Logger;

public class Item implements FeatureElement, IMaterial {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Map<Block, Item> BY_BLOCK = Maps.newHashMap();
    protected static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    protected static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    public static final int MAX_STACK_SIZE = 64;
    public static final int EAT_DURATION = 32;
    public static final int MAX_BAR_WIDTH = 13;
    private final Holder.c<Item> builtInRegistryHolder;
    private final EnumItemRarity rarity;
    private final int maxStackSize;
    private final int maxDamage;
    private final boolean isFireResistant;
    @Nullable
    private final Item craftingRemainingItem;
    @Nullable
    private String descriptionId;
    @Nullable
    private final FoodInfo foodProperties;
    private final FeatureFlagSet requiredFeatures;

    public static int getId(Item item) {
        return item == null ? 0 : BuiltInRegistries.ITEM.getId(item);
    }

    public static Item byId(int i) {
        return (Item) BuiltInRegistries.ITEM.byId(i);
    }

    /** @deprecated */
    @Deprecated
    public static Item byBlock(Block block) {
        return (Item) Item.BY_BLOCK.getOrDefault(block, Items.AIR);
    }

    public Item(Item.Info item_info) {
        this.builtInRegistryHolder = BuiltInRegistries.ITEM.createIntrusiveHolder(this);
        this.rarity = item_info.rarity;
        this.craftingRemainingItem = item_info.craftingRemainingItem;
        this.maxDamage = item_info.maxDamage;
        this.maxStackSize = item_info.maxStackSize;
        this.foodProperties = item_info.foodProperties;
        this.isFireResistant = item_info.isFireResistant;
        this.requiredFeatures = item_info.requiredFeatures;
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            String s = this.getClass().getSimpleName();

            if (!s.endsWith("Item")) {
                Item.LOGGER.error("Item classes should end with Item and {} doesn't.", s);
            }
        }

    }

    /** @deprecated */
    @Deprecated
    public Holder.c<Item> builtInRegistryHolder() {
        return this.builtInRegistryHolder;
    }

    public void onUseTick(World world, EntityLiving entityliving, ItemStack itemstack, int i) {}

    public void onDestroyed(EntityItem entityitem) {}

    public void verifyTagAfterLoad(NBTTagCompound nbttagcompound) {}

    public boolean canAttackBlock(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman) {
        return true;
    }

    @Override
    public Item asItem() {
        return this;
    }

    public EnumInteractionResult useOn(ItemActionContext itemactioncontext) {
        return EnumInteractionResult.PASS;
    }

    public float getDestroySpeed(ItemStack itemstack, IBlockData iblockdata) {
        return 1.0F;
    }

    public InteractionResultWrapper<ItemStack> use(World world, EntityHuman entityhuman, EnumHand enumhand) {
        if (this.isEdible()) {
            ItemStack itemstack = entityhuman.getItemInHand(enumhand);

            if (entityhuman.canEat(this.getFoodProperties().canAlwaysEat())) {
                entityhuman.startUsingItem(enumhand);
                return InteractionResultWrapper.consume(itemstack);
            } else {
                return InteractionResultWrapper.fail(itemstack);
            }
        } else {
            return InteractionResultWrapper.pass(entityhuman.getItemInHand(enumhand));
        }
    }

    public ItemStack finishUsingItem(ItemStack itemstack, World world, EntityLiving entityliving) {
        return this.isEdible() ? entityliving.eat(world, itemstack) : itemstack;
    }

    public final int getMaxStackSize() {
        return this.maxStackSize;
    }

    public final int getMaxDamage() {
        return this.maxDamage;
    }

    public boolean canBeDepleted() {
        return this.maxDamage > 0;
    }

    public boolean isBarVisible(ItemStack itemstack) {
        return itemstack.isDamaged();
    }

    public int getBarWidth(ItemStack itemstack) {
        return Math.round(13.0F - (float) itemstack.getDamageValue() * 13.0F / (float) this.maxDamage);
    }

    public int getBarColor(ItemStack itemstack) {
        float f = Math.max(0.0F, ((float) this.maxDamage - (float) itemstack.getDamageValue()) / (float) this.maxDamage);

        return MathHelper.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    public boolean overrideStackedOnOther(ItemStack itemstack, Slot slot, ClickAction clickaction, EntityHuman entityhuman) {
        return false;
    }

    public boolean overrideOtherStackedOnMe(ItemStack itemstack, ItemStack itemstack1, Slot slot, ClickAction clickaction, EntityHuman entityhuman, SlotAccess slotaccess) {
        return false;
    }

    public boolean hurtEnemy(ItemStack itemstack, EntityLiving entityliving, EntityLiving entityliving1) {
        return false;
    }

    public boolean mineBlock(ItemStack itemstack, World world, IBlockData iblockdata, BlockPosition blockposition, EntityLiving entityliving) {
        return false;
    }

    public boolean isCorrectToolForDrops(IBlockData iblockdata) {
        return false;
    }

    public EnumInteractionResult interactLivingEntity(ItemStack itemstack, EntityHuman entityhuman, EntityLiving entityliving, EnumHand enumhand) {
        return EnumInteractionResult.PASS;
    }

    public IChatBaseComponent getDescription() {
        return IChatBaseComponent.translatable(this.getDescriptionId());
    }

    public String toString() {
        return BuiltInRegistries.ITEM.getKey(this).getPath();
    }

    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = SystemUtils.makeDescriptionId("item", BuiltInRegistries.ITEM.getKey(this));
        }

        return this.descriptionId;
    }

    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    public String getDescriptionId(ItemStack itemstack) {
        return this.getDescriptionId();
    }

    public boolean shouldOverrideMultiplayerNbt() {
        return true;
    }

    @Nullable
    public final Item getCraftingRemainingItem() {
        return this.craftingRemainingItem;
    }

    public boolean hasCraftingRemainingItem() {
        return this.craftingRemainingItem != null;
    }

    public void inventoryTick(ItemStack itemstack, World world, Entity entity, int i, boolean flag) {}

    public void onCraftedBy(ItemStack itemstack, World world, EntityHuman entityhuman) {}

    public boolean isComplex() {
        return false;
    }

    public EnumAnimation getUseAnimation(ItemStack itemstack) {
        return itemstack.getItem().isEdible() ? EnumAnimation.EAT : EnumAnimation.NONE;
    }

    public int getUseDuration(ItemStack itemstack) {
        return itemstack.getItem().isEdible() ? (this.getFoodProperties().isFastFood() ? 16 : 32) : 0;
    }

    public void releaseUsing(ItemStack itemstack, World world, EntityLiving entityliving, int i) {}

    public void appendHoverText(ItemStack itemstack, @Nullable World world, List<IChatBaseComponent> list, TooltipFlag tooltipflag) {}

    public Optional<TooltipComponent> getTooltipImage(ItemStack itemstack) {
        return Optional.empty();
    }

    public IChatBaseComponent getName(ItemStack itemstack) {
        return IChatBaseComponent.translatable(this.getDescriptionId(itemstack));
    }

    public boolean isFoil(ItemStack itemstack) {
        return itemstack.isEnchanted();
    }

    public EnumItemRarity getRarity(ItemStack itemstack) {
        if (!itemstack.isEnchanted()) {
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

    public boolean isEnchantable(ItemStack itemstack) {
        return this.getMaxStackSize() == 1 && this.canBeDepleted();
    }

    protected static MovingObjectPositionBlock getPlayerPOVHitResult(World world, EntityHuman entityhuman, RayTrace.FluidCollisionOption raytrace_fluidcollisionoption) {
        float f = entityhuman.getXRot();
        float f1 = entityhuman.getYRot();
        Vec3D vec3d = entityhuman.getEyePosition();
        float f2 = MathHelper.cos(-f1 * 0.017453292F - 3.1415927F);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - 3.1415927F);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = 5.0D;
        Vec3D vec3d1 = vec3d.add((double) f6 * 5.0D, (double) f5 * 5.0D, (double) f7 * 5.0D);

        return world.clip(new RayTrace(vec3d, vec3d1, RayTrace.BlockCollisionOption.OUTLINE, raytrace_fluidcollisionoption, entityhuman));
    }

    public int getEnchantmentValue() {
        return 0;
    }

    public boolean isValidRepairItem(ItemStack itemstack, ItemStack itemstack1) {
        return false;
    }

    public Multimap<AttributeBase, AttributeModifier> getDefaultAttributeModifiers(EnumItemSlot enumitemslot) {
        return ImmutableMultimap.of();
    }

    public boolean useOnRelease(ItemStack itemstack) {
        return false;
    }

    public ItemStack getDefaultInstance() {
        return new ItemStack(this);
    }

    public boolean isEdible() {
        return this.foodProperties != null;
    }

    @Nullable
    public FoodInfo getFoodProperties() {
        return this.foodProperties;
    }

    public SoundEffect getDrinkingSound() {
        return SoundEffects.GENERIC_DRINK;
    }

    public SoundEffect getEatingSound() {
        return SoundEffects.GENERIC_EAT;
    }

    public boolean isFireResistant() {
        return this.isFireResistant;
    }

    public boolean canBeHurtBy(DamageSource damagesource) {
        return !this.isFireResistant || !damagesource.is(DamageTypeTags.IS_FIRE);
    }

    public boolean canFitInsideContainerItems() {
        return true;
    }

    @Override
    public FeatureFlagSet requiredFeatures() {
        return this.requiredFeatures;
    }

    public static class Info {

        int maxStackSize = 64;
        int maxDamage;
        @Nullable
        Item craftingRemainingItem;
        EnumItemRarity rarity;
        @Nullable
        FoodInfo foodProperties;
        boolean isFireResistant;
        FeatureFlagSet requiredFeatures;

        public Info() {
            this.rarity = EnumItemRarity.COMMON;
            this.requiredFeatures = FeatureFlags.VANILLA_SET;
        }

        public Item.Info food(FoodInfo foodinfo) {
            this.foodProperties = foodinfo;
            return this;
        }

        public Item.Info stacksTo(int i) {
            if (this.maxDamage > 0) {
                throw new RuntimeException("Unable to have damage AND stack.");
            } else {
                this.maxStackSize = i;
                return this;
            }
        }

        public Item.Info defaultDurability(int i) {
            return this.maxDamage == 0 ? this.durability(i) : this;
        }

        public Item.Info durability(int i) {
            this.maxDamage = i;
            this.maxStackSize = 1;
            return this;
        }

        public Item.Info craftRemainder(Item item) {
            this.craftingRemainingItem = item;
            return this;
        }

        public Item.Info rarity(EnumItemRarity enumitemrarity) {
            this.rarity = enumitemrarity;
            return this;
        }

        public Item.Info fireResistant() {
            this.isFireResistant = true;
            return this;
        }

        public Item.Info requiredFeatures(FeatureFlag... afeatureflag) {
            this.requiredFeatures = FeatureFlags.REGISTRY.subset(afeatureflag);
            return this;
        }
    }
}
