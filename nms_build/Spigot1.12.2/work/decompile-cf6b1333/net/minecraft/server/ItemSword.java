package net.minecraft.server;

import com.google.common.collect.Multimap;

public class ItemSword extends Item {

    private final float a;
    private final Item.EnumToolMaterial b;

    public ItemSword(Item.EnumToolMaterial item_enumtoolmaterial) {
        this.b = item_enumtoolmaterial;
        this.maxStackSize = 1;
        this.setMaxDurability(item_enumtoolmaterial.a());
        this.b(CreativeModeTab.j);
        this.a = 3.0F + item_enumtoolmaterial.c();
    }

    public float g() {
        return this.b.c();
    }

    public float getDestroySpeed(ItemStack itemstack, IBlockData iblockdata) {
        Block block = iblockdata.getBlock();

        if (block == Blocks.WEB) {
            return 15.0F;
        } else {
            Material material = iblockdata.getMaterial();

            return material != Material.PLANT && material != Material.REPLACEABLE_PLANT && material != Material.CORAL && material != Material.LEAVES && material != Material.PUMPKIN ? 1.0F : 1.5F;
        }
    }

    public boolean a(ItemStack itemstack, EntityLiving entityliving, EntityLiving entityliving1) {
        itemstack.damage(1, entityliving1);
        return true;
    }

    public boolean a(ItemStack itemstack, World world, IBlockData iblockdata, BlockPosition blockposition, EntityLiving entityliving) {
        if ((double) iblockdata.b(world, blockposition) != 0.0D) {
            itemstack.damage(2, entityliving);
        }

        return true;
    }

    public boolean canDestroySpecialBlock(IBlockData iblockdata) {
        return iblockdata.getBlock() == Blocks.WEB;
    }

    public int c() {
        return this.b.e();
    }

    public String h() {
        return this.b.toString();
    }

    public boolean a(ItemStack itemstack, ItemStack itemstack1) {
        return this.b.f() == itemstack1.getItem() ? true : super.a(itemstack, itemstack1);
    }

    public Multimap<String, AttributeModifier> a(EnumItemSlot enumitemslot) {
        Multimap multimap = super.a(enumitemslot);

        if (enumitemslot == EnumItemSlot.MAINHAND) {
            multimap.put(GenericAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ItemSword.h, "Weapon modifier", (double) this.a, 0));
            multimap.put(GenericAttributes.g.getName(), new AttributeModifier(ItemSword.i, "Weapon modifier", -2.4000000953674316D, 0));
        }

        return multimap;
    }
}
