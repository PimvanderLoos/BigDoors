package net.minecraft.server;

import com.google.common.collect.Multimap;
import java.util.Set;

public class ItemTool extends Item {

    private final Set<Block> e;
    protected float a;
    protected float b;
    protected float c;
    protected Item.EnumToolMaterial d;

    protected ItemTool(float f, float f1, Item.EnumToolMaterial item_enumtoolmaterial, Set<Block> set) {
        this.a = 4.0F;
        this.d = item_enumtoolmaterial;
        this.e = set;
        this.maxStackSize = 1;
        this.setMaxDurability(item_enumtoolmaterial.a());
        this.a = item_enumtoolmaterial.b();
        this.b = f + item_enumtoolmaterial.c();
        this.c = f1;
        this.b(CreativeModeTab.i);
    }

    protected ItemTool(Item.EnumToolMaterial item_enumtoolmaterial, Set<Block> set) {
        this(0.0F, 0.0F, item_enumtoolmaterial, set);
    }

    public float getDestroySpeed(ItemStack itemstack, IBlockData iblockdata) {
        return this.e.contains(iblockdata.getBlock()) ? this.a : 1.0F;
    }

    public boolean a(ItemStack itemstack, EntityLiving entityliving, EntityLiving entityliving1) {
        itemstack.damage(2, entityliving1);
        return true;
    }

    public boolean a(ItemStack itemstack, World world, IBlockData iblockdata, BlockPosition blockposition, EntityLiving entityliving) {
        if (!world.isClientSide && (double) iblockdata.b(world, blockposition) != 0.0D) {
            itemstack.damage(1, entityliving);
        }

        return true;
    }

    public int c() {
        return this.d.e();
    }

    public String h() {
        return this.d.toString();
    }

    public boolean a(ItemStack itemstack, ItemStack itemstack1) {
        return this.d.f() == itemstack1.getItem() ? true : super.a(itemstack, itemstack1);
    }

    public Multimap<String, AttributeModifier> a(EnumItemSlot enumitemslot) {
        Multimap multimap = super.a(enumitemslot);

        if (enumitemslot == EnumItemSlot.MAINHAND) {
            multimap.put(GenericAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ItemTool.h, "Tool modifier", (double) this.b, 0));
            multimap.put(GenericAttributes.g.getName(), new AttributeModifier(ItemTool.i, "Tool modifier", (double) this.c, 0));
        }

        return multimap;
    }
}
