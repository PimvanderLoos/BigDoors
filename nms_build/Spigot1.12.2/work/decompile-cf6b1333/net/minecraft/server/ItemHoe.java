package net.minecraft.server;

import com.google.common.collect.Multimap;

public class ItemHoe extends Item {

    private final float b;
    protected Item.EnumToolMaterial a;

    public ItemHoe(Item.EnumToolMaterial item_enumtoolmaterial) {
        this.a = item_enumtoolmaterial;
        this.maxStackSize = 1;
        this.setMaxDurability(item_enumtoolmaterial.a());
        this.b(CreativeModeTab.i);
        this.b = item_enumtoolmaterial.c() + 1.0F;
    }

    public EnumInteractionResult a(EntityHuman entityhuman, World world, BlockPosition blockposition, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (!entityhuman.a(blockposition.shift(enumdirection), enumdirection, itemstack)) {
            return EnumInteractionResult.FAIL;
        } else {
            IBlockData iblockdata = world.getType(blockposition);
            Block block = iblockdata.getBlock();

            if (enumdirection != EnumDirection.DOWN && world.getType(blockposition.up()).getMaterial() == Material.AIR) {
                if (block == Blocks.GRASS || block == Blocks.GRASS_PATH) {
                    this.a(itemstack, entityhuman, world, blockposition, Blocks.FARMLAND.getBlockData());
                    return EnumInteractionResult.SUCCESS;
                }

                if (block == Blocks.DIRT) {
                    switch ((BlockDirt.EnumDirtVariant) iblockdata.get(BlockDirt.VARIANT)) {
                    case DIRT:
                        this.a(itemstack, entityhuman, world, blockposition, Blocks.FARMLAND.getBlockData());
                        return EnumInteractionResult.SUCCESS;

                    case COARSE_DIRT:
                        this.a(itemstack, entityhuman, world, blockposition, Blocks.DIRT.getBlockData().set(BlockDirt.VARIANT, BlockDirt.EnumDirtVariant.DIRT));
                        return EnumInteractionResult.SUCCESS;
                    }
                }
            }

            return EnumInteractionResult.PASS;
        }
    }

    public boolean a(ItemStack itemstack, EntityLiving entityliving, EntityLiving entityliving1) {
        itemstack.damage(1, entityliving1);
        return true;
    }

    protected void a(ItemStack itemstack, EntityHuman entityhuman, World world, BlockPosition blockposition, IBlockData iblockdata) {
        world.a(entityhuman, blockposition, SoundEffects.cE, SoundCategory.BLOCKS, 1.0F, 1.0F);
        if (!world.isClientSide) {
            world.setTypeAndData(blockposition, iblockdata, 11);
            itemstack.damage(1, entityhuman);
        }

    }

    public String g() {
        return this.a.toString();
    }

    public Multimap<String, AttributeModifier> a(EnumItemSlot enumitemslot) {
        Multimap multimap = super.a(enumitemslot);

        if (enumitemslot == EnumItemSlot.MAINHAND) {
            multimap.put(GenericAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ItemHoe.h, "Weapon modifier", 0.0D, 0));
            multimap.put(GenericAttributes.g.getName(), new AttributeModifier(ItemHoe.i, "Weapon modifier", (double) (this.b - 4.0F), 0));
        }

        return multimap;
    }
}
