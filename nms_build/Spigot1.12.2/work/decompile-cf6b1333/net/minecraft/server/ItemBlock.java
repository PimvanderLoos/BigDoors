package net.minecraft.server;

import javax.annotation.Nullable;

public class ItemBlock extends Item {

    protected final Block a;

    public ItemBlock(Block block) {
        this.a = block;
    }

    public EnumInteractionResult a(EntityHuman entityhuman, World world, BlockPosition blockposition, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        IBlockData iblockdata = world.getType(blockposition);
        Block block = iblockdata.getBlock();

        if (!block.a((IBlockAccess) world, blockposition)) {
            blockposition = blockposition.shift(enumdirection);
        }

        ItemStack itemstack = entityhuman.b(enumhand);

        if (!itemstack.isEmpty() && entityhuman.a(blockposition, enumdirection, itemstack) && world.a(this.a, blockposition, false, enumdirection, (Entity) null)) {
            int i = this.filterData(itemstack.getData());
            IBlockData iblockdata1 = this.a.getPlacedState(world, blockposition, enumdirection, f, f1, f2, i, entityhuman);

            if (world.setTypeAndData(blockposition, iblockdata1, 11)) {
                iblockdata1 = world.getType(blockposition);
                if (iblockdata1.getBlock() == this.a) {
                    a(world, entityhuman, blockposition, itemstack);
                    this.a.postPlace(world, blockposition, iblockdata1, entityhuman, itemstack);
                    if (entityhuman instanceof EntityPlayer) {
                        CriterionTriggers.x.a((EntityPlayer) entityhuman, blockposition, itemstack);
                    }
                }

                SoundEffectType soundeffecttype = this.a.getStepSound();

                world.a(entityhuman, blockposition, soundeffecttype.e(), SoundCategory.BLOCKS, (soundeffecttype.a() + 1.0F) / 2.0F, soundeffecttype.b() * 0.8F);
                itemstack.subtract(1);
            }

            return EnumInteractionResult.SUCCESS;
        } else {
            return EnumInteractionResult.FAIL;
        }
    }

    public static boolean a(World world, @Nullable EntityHuman entityhuman, BlockPosition blockposition, ItemStack itemstack) {
        MinecraftServer minecraftserver = world.getMinecraftServer();

        if (minecraftserver == null) {
            return false;
        } else {
            NBTTagCompound nbttagcompound = itemstack.d("BlockEntityTag");

            if (nbttagcompound != null) {
                TileEntity tileentity = world.getTileEntity(blockposition);

                if (tileentity != null) {
                    if (!world.isClientSide && tileentity.isFilteredNBT() && (entityhuman == null || !entityhuman.isCreativeAndOp())) {
                        return false;
                    }

                    NBTTagCompound nbttagcompound1 = tileentity.save(new NBTTagCompound());
                    NBTTagCompound nbttagcompound2 = nbttagcompound1.g();

                    nbttagcompound1.a(nbttagcompound);
                    nbttagcompound1.setInt("x", blockposition.getX());
                    nbttagcompound1.setInt("y", blockposition.getY());
                    nbttagcompound1.setInt("z", blockposition.getZ());
                    if (!nbttagcompound1.equals(nbttagcompound2)) {
                        tileentity.load(nbttagcompound1);
                        tileentity.update();
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public String a(ItemStack itemstack) {
        return this.a.a();
    }

    public String getName() {
        return this.a.a();
    }

    public CreativeModeTab b() {
        return this.a.q();
    }

    public void a(CreativeModeTab creativemodetab, NonNullList<ItemStack> nonnulllist) {
        if (this.a(creativemodetab)) {
            this.a.a(creativemodetab, nonnulllist);
        }

    }

    public Block getBlock() {
        return this.a;
    }
}
