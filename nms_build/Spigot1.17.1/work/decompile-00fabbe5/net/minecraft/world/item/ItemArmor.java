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
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockDispenser;
import net.minecraft.world.phys.AxisAlignedBB;

public class ItemArmor extends Item implements ItemWearable {

    private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
    public static final IDispenseBehavior DISPENSE_ITEM_BEHAVIOR = new DispenseBehaviorItem() {
        @Override
        protected ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
            return ItemArmor.a(isourceblock, itemstack) ? itemstack : super.a(isourceblock, itemstack);
        }
    };
    protected final EnumItemSlot slot;
    private final int defense;
    private final float toughness;
    protected final float knockbackResistance;
    protected final ArmorMaterial material;
    private final Multimap<AttributeBase, AttributeModifier> defaultModifiers;

    public static boolean a(ISourceBlock isourceblock, ItemStack itemstack) {
        BlockPosition blockposition = isourceblock.getBlockPosition().shift((EnumDirection) isourceblock.getBlockData().get(BlockDispenser.FACING));
        List<EntityLiving> list = isourceblock.getWorld().a(EntityLiving.class, new AxisAlignedBB(blockposition), IEntitySelector.NO_SPECTATORS.and(new IEntitySelector.EntitySelectorEquipable(itemstack)));

        if (list.isEmpty()) {
            return false;
        } else {
            EntityLiving entityliving = (EntityLiving) list.get(0);
            EnumItemSlot enumitemslot = EntityInsentient.getEquipmentSlotForItem(itemstack);
            ItemStack itemstack1 = itemstack.cloneAndSubtract(1);

            entityliving.setSlot(enumitemslot, itemstack1);
            if (entityliving instanceof EntityInsentient) {
                ((EntityInsentient) entityliving).a(enumitemslot, 2.0F);
                ((EntityInsentient) entityliving).setPersistent();
            }

            return true;
        }
    }

    public ItemArmor(ArmorMaterial armormaterial, EnumItemSlot enumitemslot, Item.Info item_info) {
        super(item_info.b(armormaterial.a(enumitemslot)));
        this.material = armormaterial;
        this.slot = enumitemslot;
        this.defense = armormaterial.b(enumitemslot);
        this.toughness = armormaterial.e();
        this.knockbackResistance = armormaterial.f();
        BlockDispenser.a((IMaterial) this, ItemArmor.DISPENSE_ITEM_BEHAVIOR);
        Builder<AttributeBase, AttributeModifier> builder = ImmutableMultimap.builder();
        UUID uuid = ItemArmor.ARMOR_MODIFIER_UUID_PER_SLOT[enumitemslot.b()];

        builder.put(GenericAttributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", (double) this.defense, AttributeModifier.Operation.ADDITION));
        builder.put(GenericAttributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", (double) this.toughness, AttributeModifier.Operation.ADDITION));
        if (armormaterial == EnumArmorMaterial.NETHERITE) {
            builder.put(GenericAttributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "Armor knockback resistance", (double) this.knockbackResistance, AttributeModifier.Operation.ADDITION));
        }

        this.defaultModifiers = builder.build();
    }

    public EnumItemSlot b() {
        return this.slot;
    }

    @Override
    public int c() {
        return this.material.a();
    }

    public ArmorMaterial d() {
        return this.material;
    }

    @Override
    public boolean a(ItemStack itemstack, ItemStack itemstack1) {
        return this.material.c().test(itemstack1) || super.a(itemstack, itemstack1);
    }

    @Override
    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        EnumItemSlot enumitemslot = EntityInsentient.getEquipmentSlotForItem(itemstack);
        ItemStack itemstack1 = entityhuman.getEquipment(enumitemslot);

        if (itemstack1.isEmpty()) {
            entityhuman.setSlot(enumitemslot, itemstack.cloneItemStack());
            if (!world.isClientSide()) {
                entityhuman.b(StatisticList.ITEM_USED.b(this));
            }

            itemstack.setCount(0);
            return InteractionResultWrapper.a(itemstack, world.isClientSide());
        } else {
            return InteractionResultWrapper.fail(itemstack);
        }
    }

    @Override
    public Multimap<AttributeBase, AttributeModifier> a(EnumItemSlot enumitemslot) {
        return enumitemslot == this.slot ? this.defaultModifiers : super.a(enumitemslot);
    }

    public int e() {
        return this.defense;
    }

    public float f() {
        return this.toughness;
    }

    @Nullable
    @Override
    public SoundEffect g() {
        return this.d().b();
    }
}
