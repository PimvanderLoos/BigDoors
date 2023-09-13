package net.minecraft.world.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.ISourceBlock;
import net.minecraft.core.dispenser.DispenseBehaviorItem;
import net.minecraft.core.dispenser.IDispenseBehavior;
import net.minecraft.sounds.SoundEffect;
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

public class ItemArmor extends Item implements Equipable {

    private static final EnumMap<ItemArmor.a, UUID> ARMOR_MODIFIER_UUID_PER_TYPE = (EnumMap) SystemUtils.make(new EnumMap(ItemArmor.a.class), (enummap) -> {
        enummap.put(ItemArmor.a.BOOTS, UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"));
        enummap.put(ItemArmor.a.LEGGINGS, UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"));
        enummap.put(ItemArmor.a.CHESTPLATE, UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"));
        enummap.put(ItemArmor.a.HELMET, UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150"));
    });
    public static final IDispenseBehavior DISPENSE_ITEM_BEHAVIOR = new DispenseBehaviorItem() {
        @Override
        protected ItemStack execute(ISourceBlock isourceblock, ItemStack itemstack) {
            return ItemArmor.dispenseArmor(isourceblock, itemstack) ? itemstack : super.execute(isourceblock, itemstack);
        }
    };
    protected final ItemArmor.a type;
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

    public ItemArmor(ArmorMaterial armormaterial, ItemArmor.a itemarmor_a, Item.Info item_info) {
        super(item_info.defaultDurability(armormaterial.getDurabilityForType(itemarmor_a)));
        this.material = armormaterial;
        this.type = itemarmor_a;
        this.defense = armormaterial.getDefenseForType(itemarmor_a);
        this.toughness = armormaterial.getToughness();
        this.knockbackResistance = armormaterial.getKnockbackResistance();
        BlockDispenser.registerBehavior(this, ItemArmor.DISPENSE_ITEM_BEHAVIOR);
        Builder<AttributeBase, AttributeModifier> builder = ImmutableMultimap.builder();
        UUID uuid = (UUID) ItemArmor.ARMOR_MODIFIER_UUID_PER_TYPE.get(itemarmor_a);

        builder.put(GenericAttributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", (double) this.defense, AttributeModifier.Operation.ADDITION));
        builder.put(GenericAttributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", (double) this.toughness, AttributeModifier.Operation.ADDITION));
        if (armormaterial == EnumArmorMaterial.NETHERITE) {
            builder.put(GenericAttributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "Armor knockback resistance", (double) this.knockbackResistance, AttributeModifier.Operation.ADDITION));
        }

        this.defaultModifiers = builder.build();
    }

    public ItemArmor.a getType() {
        return this.type;
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
        return this.swapWithEquipmentSlot(this, world, entityhuman, enumhand);
    }

    @Override
    public Multimap<AttributeBase, AttributeModifier> getDefaultAttributeModifiers(EnumItemSlot enumitemslot) {
        return enumitemslot == this.type.getSlot() ? this.defaultModifiers : super.getDefaultAttributeModifiers(enumitemslot);
    }

    public int getDefense() {
        return this.defense;
    }

    public float getToughness() {
        return this.toughness;
    }

    @Override
    public EnumItemSlot getEquipmentSlot() {
        return this.type.getSlot();
    }

    @Override
    public SoundEffect getEquipSound() {
        return this.getMaterial().getEquipSound();
    }

    public static enum a {

        HELMET(EnumItemSlot.HEAD, "helmet"), CHESTPLATE(EnumItemSlot.CHEST, "chestplate"), LEGGINGS(EnumItemSlot.LEGS, "leggings"), BOOTS(EnumItemSlot.FEET, "boots");

        private final EnumItemSlot slot;
        private final String name;

        private a(EnumItemSlot enumitemslot, String s) {
            this.slot = enumitemslot;
            this.name = s;
        }

        public EnumItemSlot getSlot() {
            return this.slot;
        }

        public String getName() {
            return this.name;
        }
    }
}
