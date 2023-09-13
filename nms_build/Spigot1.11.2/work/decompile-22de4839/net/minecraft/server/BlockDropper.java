package net.minecraft.server;

public class BlockDropper extends BlockDispenser {

    private final IDispenseBehavior e = new DispenseBehaviorItem();

    public BlockDropper() {}

    protected IDispenseBehavior a(ItemStack itemstack) {
        return this.e;
    }

    public TileEntity a(World world, int i) {
        return new TileEntityDropper();
    }

    public void dispense(World world, BlockPosition blockposition) {
        SourceBlock sourceblock = new SourceBlock(world, blockposition);
        TileEntityDispenser tileentitydispenser = (TileEntityDispenser) sourceblock.getTileEntity();

        if (tileentitydispenser != null) {
            int i = tileentitydispenser.o();

            if (i < 0) {
                world.triggerEffect(1001, blockposition, 0);
            } else {
                ItemStack itemstack = tileentitydispenser.getItem(i);

                if (!itemstack.isEmpty()) {
                    EnumDirection enumdirection = (EnumDirection) world.getType(blockposition).get(BlockDropper.FACING);
                    BlockPosition blockposition1 = blockposition.shift(enumdirection);
                    IInventory iinventory = TileEntityHopper.b(world, (double) blockposition1.getX(), (double) blockposition1.getY(), (double) blockposition1.getZ());
                    ItemStack itemstack1;

                    if (iinventory == null) {
                        itemstack1 = this.e.a(sourceblock, itemstack);
                    } else {
                        itemstack1 = TileEntityHopper.addItem(tileentitydispenser, iinventory, itemstack.cloneItemStack().cloneAndSubtract(1), enumdirection.opposite());
                        if (itemstack1.isEmpty()) {
                            itemstack1 = itemstack.cloneItemStack();
                            itemstack1.subtract(1);
                        } else {
                            itemstack1 = itemstack.cloneItemStack();
                        }
                    }

                    tileentitydispenser.setItem(i, itemstack1);
                }
            }
        }
    }
}
