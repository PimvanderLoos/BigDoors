package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class TileEntityChest extends TileEntityLootable implements ITickable {

    private NonNullList<ItemStack> items;
    public boolean a;
    public TileEntityChest f;
    public TileEntityChest g;
    public TileEntityChest h;
    public TileEntityChest i;
    public float j;
    public float k;
    public int l;
    private int q;
    private BlockChest.Type r;

    public TileEntityChest() {
        this.items = NonNullList.a(27, ItemStack.a);
    }

    public TileEntityChest(BlockChest.Type blockchest_type) {
        this.items = NonNullList.a(27, ItemStack.a);
        this.r = blockchest_type;
    }

    public int getSize() {
        return 27;
    }

    public boolean x_() {
        Iterator iterator = this.items.iterator();

        ItemStack itemstack;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            itemstack = (ItemStack) iterator.next();
        } while (itemstack.isEmpty());

        return false;
    }

    public String getName() {
        return this.hasCustomName() ? this.o : "container.chest";
    }

    public static void a(DataConverterManager dataconvertermanager) {
        dataconvertermanager.a(DataConverterTypes.BLOCK_ENTITY, (DataInspector) (new DataInspectorItemList(TileEntityChest.class, new String[] { "Items"})));
    }

    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.items = NonNullList.a(this.getSize(), ItemStack.a);
        if (!this.c(nbttagcompound)) {
            ContainerUtil.b(nbttagcompound, this.items);
        }

        if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
            this.o = nbttagcompound.getString("CustomName");
        }

    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        if (!this.d(nbttagcompound)) {
            ContainerUtil.a(nbttagcompound, this.items);
        }

        if (this.hasCustomName()) {
            nbttagcompound.setString("CustomName", this.o);
        }

        return nbttagcompound;
    }

    public int getMaxStackSize() {
        return 64;
    }

    public void invalidateBlockCache() {
        super.invalidateBlockCache();
        this.a = false;
    }

    private void a(TileEntityChest tileentitychest, EnumDirection enumdirection) {
        if (tileentitychest.y()) {
            this.a = false;
        } else if (this.a) {
            switch (enumdirection) {
            case NORTH:
                if (this.f != tileentitychest) {
                    this.a = false;
                }
                break;

            case SOUTH:
                if (this.i != tileentitychest) {
                    this.a = false;
                }
                break;

            case EAST:
                if (this.g != tileentitychest) {
                    this.a = false;
                }
                break;

            case WEST:
                if (this.h != tileentitychest) {
                    this.a = false;
                }
            }
        }

    }

    public void o() {
        if (!this.a) {
            this.a = true;
            this.h = this.a(EnumDirection.WEST);
            this.g = this.a(EnumDirection.EAST);
            this.f = this.a(EnumDirection.NORTH);
            this.i = this.a(EnumDirection.SOUTH);
        }
    }

    @Nullable
    protected TileEntityChest a(EnumDirection enumdirection) {
        BlockPosition blockposition = this.position.shift(enumdirection);

        if (this.b(blockposition)) {
            TileEntity tileentity = this.world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityChest) {
                TileEntityChest tileentitychest = (TileEntityChest) tileentity;

                tileentitychest.a(this, enumdirection.opposite());
                return tileentitychest;
            }
        }

        return null;
    }

    private boolean b(BlockPosition blockposition) {
        if (this.world == null) {
            return false;
        } else {
            Block block = this.world.getType(blockposition).getBlock();

            return block instanceof BlockChest && ((BlockChest) block).g == this.p();
        }
    }

    public void e() {
        this.o();
        int i = this.position.getX();
        int j = this.position.getY();
        int k = this.position.getZ();

        ++this.q;
        float f;

        if (!this.world.isClientSide && this.l != 0 && (this.q + i + j + k) % 200 == 0) {
            this.l = 0;
            f = 5.0F;
            List list = this.world.a(EntityHuman.class, new AxisAlignedBB((double) ((float) i - 5.0F), (double) ((float) j - 5.0F), (double) ((float) k - 5.0F), (double) ((float) (i + 1) + 5.0F), (double) ((float) (j + 1) + 5.0F), (double) ((float) (k + 1) + 5.0F)));
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityHuman entityhuman = (EntityHuman) iterator.next();

                if (entityhuman.activeContainer instanceof ContainerChest) {
                    IInventory iinventory = ((ContainerChest) entityhuman.activeContainer).e();

                    if (iinventory == this || iinventory instanceof InventoryLargeChest && ((InventoryLargeChest) iinventory).a((IInventory) this)) {
                        ++this.l;
                    }
                }
            }
        }

        this.k = this.j;
        f = 0.1F;
        double d0;

        if (this.l > 0 && this.j == 0.0F && this.f == null && this.h == null) {
            double d1 = (double) i + 0.5D;

            d0 = (double) k + 0.5D;
            if (this.i != null) {
                d0 += 0.5D;
            }

            if (this.g != null) {
                d1 += 0.5D;
            }

            this.world.a((EntityHuman) null, d1, (double) j + 0.5D, d0, SoundEffects.ac, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
        }

        if (this.l == 0 && this.j > 0.0F || this.l > 0 && this.j < 1.0F) {
            float f1 = this.j;

            if (this.l > 0) {
                this.j += 0.1F;
            } else {
                this.j -= 0.1F;
            }

            if (this.j > 1.0F) {
                this.j = 1.0F;
            }

            float f2 = 0.5F;

            if (this.j < 0.5F && f1 >= 0.5F && this.f == null && this.h == null) {
                d0 = (double) i + 0.5D;
                double d2 = (double) k + 0.5D;

                if (this.i != null) {
                    d2 += 0.5D;
                }

                if (this.g != null) {
                    d0 += 0.5D;
                }

                this.world.a((EntityHuman) null, d0, (double) j + 0.5D, d2, SoundEffects.aa, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
            }

            if (this.j < 0.0F) {
                this.j = 0.0F;
            }
        }

    }

    public boolean c(int i, int j) {
        if (i == 1) {
            this.l = j;
            return true;
        } else {
            return super.c(i, j);
        }
    }

    public void startOpen(EntityHuman entityhuman) {
        if (!entityhuman.isSpectator()) {
            if (this.l < 0) {
                this.l = 0;
            }

            ++this.l;
            this.world.playBlockAction(this.position, this.getBlock(), 1, this.l);
            this.world.applyPhysics(this.position, this.getBlock(), false);
            if (this.p() == BlockChest.Type.TRAP) {
                this.world.applyPhysics(this.position.down(), this.getBlock(), false);
            }
        }

    }

    public void closeContainer(EntityHuman entityhuman) {
        if (!entityhuman.isSpectator() && this.getBlock() instanceof BlockChest) {
            --this.l;
            this.world.playBlockAction(this.position, this.getBlock(), 1, this.l);
            this.world.applyPhysics(this.position, this.getBlock(), false);
            if (this.p() == BlockChest.Type.TRAP) {
                this.world.applyPhysics(this.position.down(), this.getBlock(), false);
            }
        }

    }

    public void z() {
        super.z();
        this.invalidateBlockCache();
        this.o();
    }

    public BlockChest.Type p() {
        if (this.r == null) {
            if (this.world == null || !(this.getBlock() instanceof BlockChest)) {
                return BlockChest.Type.BASIC;
            }

            this.r = ((BlockChest) this.getBlock()).g;
        }

        return this.r;
    }

    public String getContainerName() {
        return "minecraft:chest";
    }

    public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
        this.d(entityhuman);
        return new ContainerChest(playerinventory, this, entityhuman);
    }

    protected NonNullList<ItemStack> q() {
        return this.items;
    }
}
