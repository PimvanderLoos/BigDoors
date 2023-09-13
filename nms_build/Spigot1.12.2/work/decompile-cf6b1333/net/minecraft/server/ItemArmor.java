package net.minecraft.server;

import com.google.common.base.Predicates;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;

public class ItemArmor extends Item {

    private static final int[] n = new int[] { 13, 15, 16, 11};
    private static final UUID[] o = new UUID[] { UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
    public static final String[] a = new String[] { "minecraft:items/empty_armor_slot_boots", "minecraft:items/empty_armor_slot_leggings", "minecraft:items/empty_armor_slot_chestplate", "minecraft:items/empty_armor_slot_helmet"};
    public static final IDispenseBehavior b = new DispenseBehaviorItem() {
        protected ItemStack b(ISourceBlock isourceblock, ItemStack itemstack) {
            ItemStack itemstack1 = ItemArmor.a(isourceblock, itemstack);

            return itemstack1.isEmpty() ? super.b(isourceblock, itemstack) : itemstack1;
        }
    };
    public final EnumItemSlot c;
    public final int d;
    public final float e;
    public final int f;
    private final ItemArmor.EnumArmorMaterial p;

    public static ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
        BlockPosition blockposition = isourceblock.getBlockPosition().shift((EnumDirection) isourceblock.e().get(BlockDispenser.FACING));
        List list = isourceblock.getWorld().a(EntityLiving.class, new AxisAlignedBB(blockposition), Predicates.and(IEntitySelector.e, new IEntitySelector.EntitySelectorEquipable(itemstack)));

        if (list.isEmpty()) {
            return ItemStack.a;
        } else {
            EntityLiving entityliving = (EntityLiving) list.get(0);
            EnumItemSlot enumitemslot = EntityInsentient.d(itemstack);
            ItemStack itemstack1 = itemstack.cloneAndSubtract(1);

            entityliving.setSlot(enumitemslot, itemstack1);
            if (entityliving instanceof EntityInsentient) {
                ((EntityInsentient) entityliving).a(enumitemslot, 2.0F);
            }

            return itemstack;
        }
    }

    public ItemArmor(ItemArmor.EnumArmorMaterial itemarmor_enumarmormaterial, int i, EnumItemSlot enumitemslot) {
        this.p = itemarmor_enumarmormaterial;
        this.c = enumitemslot;
        this.f = i;
        this.d = itemarmor_enumarmormaterial.b(enumitemslot);
        this.setMaxDurability(itemarmor_enumarmormaterial.a(enumitemslot));
        this.e = itemarmor_enumarmormaterial.e();
        this.maxStackSize = 1;
        this.b(CreativeModeTab.j);
        BlockDispenser.REGISTRY.a(this, ItemArmor.b);
    }

    public int c() {
        return this.p.a();
    }

    public ItemArmor.EnumArmorMaterial d() {
        return this.p;
    }

    public boolean e_(ItemStack itemstack) {
        if (this.p != ItemArmor.EnumArmorMaterial.LEATHER) {
            return false;
        } else {
            NBTTagCompound nbttagcompound = itemstack.getTag();

            return nbttagcompound != null && nbttagcompound.hasKeyOfType("display", 10) ? nbttagcompound.getCompound("display").hasKeyOfType("color", 3) : false;
        }
    }

    public int c(ItemStack itemstack) {
        if (this.p != ItemArmor.EnumArmorMaterial.LEATHER) {
            return 16777215;
        } else {
            NBTTagCompound nbttagcompound = itemstack.getTag();

            if (nbttagcompound != null) {
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("display");

                if (nbttagcompound1 != null && nbttagcompound1.hasKeyOfType("color", 3)) {
                    return nbttagcompound1.getInt("color");
                }
            }

            return 10511680;
        }
    }

    public void d(ItemStack itemstack) {
        if (this.p == ItemArmor.EnumArmorMaterial.LEATHER) {
            NBTTagCompound nbttagcompound = itemstack.getTag();

            if (nbttagcompound != null) {
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("display");

                if (nbttagcompound1.hasKey("color")) {
                    nbttagcompound1.remove("color");
                }

            }
        }
    }

    public void a(ItemStack itemstack, int i) {
        if (this.p != ItemArmor.EnumArmorMaterial.LEATHER) {
            throw new UnsupportedOperationException("Can\'t dye non-leather!");
        } else {
            NBTTagCompound nbttagcompound = itemstack.getTag();

            if (nbttagcompound == null) {
                nbttagcompound = new NBTTagCompound();
                itemstack.setTag(nbttagcompound);
            }

            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("display");

            if (!nbttagcompound.hasKeyOfType("display", 10)) {
                nbttagcompound.set("display", nbttagcompound1);
            }

            nbttagcompound1.setInt("color", i);
        }
    }

    public boolean a(ItemStack itemstack, ItemStack itemstack1) {
        return this.p.c() == itemstack1.getItem() ? true : super.a(itemstack, itemstack1);
    }

    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        EnumItemSlot enumitemslot = EntityInsentient.d(itemstack);
        ItemStack itemstack1 = entityhuman.getEquipment(enumitemslot);

        if (itemstack1.isEmpty()) {
            entityhuman.setSlot(enumitemslot, itemstack.cloneItemStack());
            itemstack.setCount(0);
            return new InteractionResultWrapper(EnumInteractionResult.SUCCESS, itemstack);
        } else {
            return new InteractionResultWrapper(EnumInteractionResult.FAIL, itemstack);
        }
    }

    public Multimap<String, AttributeModifier> a(EnumItemSlot enumitemslot) {
        Multimap multimap = super.a(enumitemslot);

        if (enumitemslot == this.c) {
            multimap.put(GenericAttributes.h.getName(), new AttributeModifier(ItemArmor.o[enumitemslot.b()], "Armor modifier", (double) this.d, 0));
            multimap.put(GenericAttributes.i.getName(), new AttributeModifier(ItemArmor.o[enumitemslot.b()], "Armor toughness", (double) this.e, 0));
        }

        return multimap;
    }

    public static enum EnumArmorMaterial {

        LEATHER("leather", 5, new int[] { 1, 2, 3, 1}, 15, SoundEffects.t, 0.0F), CHAIN("chainmail", 15, new int[] { 1, 4, 5, 2}, 12, SoundEffects.n, 0.0F), IRON("iron", 15, new int[] { 2, 5, 6, 2}, 9, SoundEffects.s, 0.0F), GOLD("gold", 7, new int[] { 1, 3, 5, 2}, 25, SoundEffects.r, 0.0F), DIAMOND("diamond", 33, new int[] { 3, 6, 8, 3}, 10, SoundEffects.o, 2.0F);

        private final String f;
        private final int g;
        private final int[] h;
        private final int i;
        private final SoundEffect j;
        private final float k;

        private EnumArmorMaterial(String s, int i, int[] aint, int j, SoundEffect soundeffect, float f) {
            this.f = s;
            this.g = i;
            this.h = aint;
            this.i = j;
            this.j = soundeffect;
            this.k = f;
        }

        public int a(EnumItemSlot enumitemslot) {
            return ItemArmor.n[enumitemslot.b()] * this.g;
        }

        public int b(EnumItemSlot enumitemslot) {
            return this.h[enumitemslot.b()];
        }

        public int a() {
            return this.i;
        }

        public SoundEffect b() {
            return this.j;
        }

        public Item c() {
            return this == ItemArmor.EnumArmorMaterial.LEATHER ? Items.LEATHER : (this == ItemArmor.EnumArmorMaterial.CHAIN ? Items.IRON_INGOT : (this == ItemArmor.EnumArmorMaterial.GOLD ? Items.GOLD_INGOT : (this == ItemArmor.EnumArmorMaterial.IRON ? Items.IRON_INGOT : (this == ItemArmor.EnumArmorMaterial.DIAMOND ? Items.DIAMOND : null))));
        }

        public float e() {
            return this.k;
        }
    }
}
