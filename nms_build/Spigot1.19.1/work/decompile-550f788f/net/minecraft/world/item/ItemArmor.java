package net.minecraft.world.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.ISourceBlock;
import net.minecraft.core.dispenser.DispenseBehaviorItem;
import net.minecraft.core.dispenser.IDispenseBehavior;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockDispenser;
import net.minecraft.world.phys.AxisAlignedBB;

public class ItemArmor extends Item implements ItemWearable {

    private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
    public static final IDispenseBehavior DISPENSE_ITEM_BEHAVIOR = new DispenseBehaviorItem() {
        @Override
        protected ItemStack execute(ISourceBlock isourceblock, ItemStack itemstack) {
            return ItemArmor.dispenseArmor(isourceblock, itemstack) ? itemstack : super.execute(isourceblock, itemstack);
        }
    };
    protected final EnumItemSlot slot;
    private final int defense;
    private final float toughness;
    protected final float knockbackResistance;
    protected final ArmorMaterial material;
    private final Multimap<AttributeBase, AttributeModifier> defaultModifiers;

    public static boolean dispenseArmor(ISourceBlock isourceblock, ItemStack itemstack) {
        BlockPosition blockposition = isourceblock.getPos().relative((EnumDirection) isourceblock.getBlockState().getValue(BlockDispenser.FACING));
        List<EntityLiving> list = isourceblock.getLevel().getEntitiesOfClass(EntityLiving.class, new AxisAlignedBB(blockposition), IEntitySelector.NO_SPECTATORS.and(new IEntitySelector.EntitySelectorEquipable(itemstack)));

        if (list.isEmpty()) {
            return false;
        } else {
            EntityLiving entityliving = (EntityLiving) list.get(0);
            EnumItemSlot enumitemslot = EntityInsentient.getEquipmentSlotForItem(itemstack);
            ItemStack itemstack1 = itemstack.split(1);

            entityliving.setItemSlot(enumitemslot, itemstack1);
            if (entityliving instanceof EntityInsentient) {
                ((EntityInsentient) entityliving).setDropChance(enumitemslot, 2.0F);
                ((EntityInsentient) entityliving).setPersistenceRequired();
            }

            return true;
        }
    }

    public ItemArmor(ArmorMaterial armormaterial, EnumItemSlot enumitemslot, Item.Info item_info) {
        super(item_info.defaultDurability(armormaterial.getDurabilityForSlot(enumitemslot)));
        this.material = armormaterial;
        this.slot = enumitemslot;
        this.defense = armormaterial.getDefenseForSlot(enumitemslot);
        this.toughness = armormaterial.getToughness();
        this.knockbackResistance = armormaterial.getKnockbackResistance();
        BlockDispenser.registerBehavior(this, ItemArmor.DISPENSE_ITEM_BEHAVIOR);
        Builder<AttributeBase, AttributeModifier> builder = ImmutableMultimap.builder();
        UUID uuid = ItemArmor.ARMOR_MODIFIER_UUID_PER_SLOT[enumitemslot.getIndex()];

        builder.put(GenericAttributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", (double) this.defense, AttributeModifier.Operation.ADDITION));
        builder.put(GenericAttributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", (double) this.toughness, AttributeModifier.Operation.ADDITION));
        if (armormaterial == EnumArmorMaterial.NETHERITE) {
            builder.put(GenericAttributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "Armor knockback resistance", (double) this.knockbackResistance, AttributeModifier.Operation.ADDITION));
        }

        this.defaultModifiers = builder.build();
    }

    public EnumItemSlot getSlot() {
        return this.slot;
    }

    @Override
    public int getEnchantmentValue() {
        return this.material.getEnchantmentValue();
    }

    public ArmorMaterial getMaterial() {
        return this.material;
    }

    @Override
    public boolean isValidRepairItem(ItemStack itemstack, ItemStack itemstack1) {
        return this.material.getRepairIngredient().test(itemstack1) || super.isValidRepairItem(itemstack, itemstack1);
    }

    @Override
    public InteractionResultWrapper<ItemStack> use(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);
        EnumItemSlot enumitemslot = EntityInsentient.getEquipmentSlotForItem(itemstack);
        ItemStack itemstack1 = entityhuman.getItemBySlot(enumitemslot);

        if (itemstack1.isEmpty()) {
            entityhuman.setItemSlot(enumitemslot, itemstack.copy());
            if (!world.isClientSide()) {
                entityhuman.awardStat(StatisticList.ITEM_USED.get(this));
            }

            itemstack.setCount(0);
            return InteractionResultWrapper.sidedSuccess(itemstack, world.isClientSide());
        } else {
            return InteractionResultWrapper.fail(itemstack);
        }
    }

    @Override
    public Multimap<AttributeBase, AttributeModifier> getDefaultAttributeModifiers(EnumItemSlot enumitemslot) {
        return enumitemslot == this.slot ? this.defaultModifiers : super.getDefaultAttributeModifiers(enumitemslot);
    }

    public int getDefense() {
        return this.defense;
    }

    public float getToughness() {
        return this.toughness;
    }

    @Nullable
    @Override
    public SoundEffect getEquipSound() {
        return this.getMaterial().getEquipSound();
    }
}
