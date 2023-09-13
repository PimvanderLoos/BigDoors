package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class TileEntityShulkerBox extends TileEntityLootable implements ITickable, IWorldInventory {

    private static final int[] a = new int[27];
    private NonNullList<ItemStack> f;
    private boolean g;
    private int h;
    private TileEntityShulkerBox.AnimationPhase i;
    private float j;
    private float k;
    private EnumColor l;
    private boolean p;

    public TileEntityShulkerBox() {
        this((EnumColor) null);
    }

    public TileEntityShulkerBox(@Nullable EnumColor enumcolor) {
        this.f = NonNullList.a(27, ItemStack.a);
        this.i = TileEntityShulkerBox.AnimationPhase.CLOSED;
        this.l = enumcolor;
    }

    public void e() {
        this.o();
        if (this.i == TileEntityShulkerBox.AnimationPhase.OPENING || this.i == TileEntityShulkerBox.AnimationPhase.CLOSING) {
            this.G();
        }

    }

    protected void o() {
        this.k = this.j;
        switch (this.i) {
        case CLOSED:
            this.j = 0.0F;
            break;

        case OPENING:
            this.j += 0.1F;
            if (this.j >= 1.0F) {
                this.G();
                this.i = TileEntityShulkerBox.AnimationPhase.OPENED;
                this.j = 1.0F;
            }
            break;

        case CLOSING:
            this.j -= 0.1F;
            if (this.j <= 0.0F) {
                this.i = TileEntityShulkerBox.AnimationPhase.CLOSED;
                this.j = 0.0F;
            }
            break;

        case OPENED:
            this.j = 1.0F;
        }

    }

    public TileEntityShulkerBox.AnimationPhase p() {
        return this.i;
    }

    public AxisAlignedBB a(IBlockData iblockdata) {
        return this.b((EnumDirection) iblockdata.get(BlockShulkerBox.a));
    }

    public AxisAlignedBB b(EnumDirection enumdirection) {
        return Block.j.b((double) (0.5F * this.a(1.0F) * (float) enumdirection.getAdjacentX()), (double) (0.5F * this.a(1.0F) * (float) enumdirection.getAdjacentY()), (double) (0.5F * this.a(1.0F) * (float) enumdirection.getAdjacentZ()));
    }

    private AxisAlignedBB c(EnumDirection enumdirection) {
        EnumDirection enumdirection1 = enumdirection.opposite();

        return this.b(enumdirection).a((double) enumdirection1.getAdjacentX(), (double) enumdirection1.getAdjacentY(), (double) enumdirection1.getAdjacentZ());
    }

    private void G() {
        IBlockData iblockdata = this.world.getType(this.getPosition());

        if (iblockdata.getBlock() instanceof BlockShulkerBox) {
            EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockShulkerBox.a);
            AxisAlignedBB axisalignedbb = this.c(enumdirection).a(this.position);
            List list = this.world.getEntities((Entity) null, axisalignedbb);

            if (!list.isEmpty()) {
                for (int i = 0; i < list.size(); ++i) {
                    Entity entity = (Entity) list.get(i);

                    if (entity.getPushReaction() != EnumPistonReaction.IGNORE) {
                        double d0 = 0.0D;
                        double d1 = 0.0D;
                        double d2 = 0.0D;
                        AxisAlignedBB axisalignedbb1 = entity.getBoundingBox();

                        switch (enumdirection.k()) {
                        case X:
                            if (enumdirection.c() == EnumDirection.EnumAxisDirection.POSITIVE) {
                                d0 = axisalignedbb.d - axisalignedbb1.a;
                            } else {
                                d0 = axisalignedbb1.d - axisalignedbb.a;
                            }

                            d0 += 0.01D;
                            break;

                        case Y:
                            if (enumdirection.c() == EnumDirection.EnumAxisDirection.POSITIVE) {
                                d1 = axisalignedbb.e - axisalignedbb1.b;
                            } else {
                                d1 = axisalignedbb1.e - axisalignedbb.b;
                            }

                            d1 += 0.01D;
                            break;

                        case Z:
                            if (enumdirection.c() == EnumDirection.EnumAxisDirection.POSITIVE) {
                                d2 = axisalignedbb.f - axisalignedbb1.c;
                            } else {
                                d2 = axisalignedbb1.f - axisalignedbb.c;
                            }

                            d2 += 0.01D;
                        }

                        entity.move(EnumMoveType.SHULKER_BOX, d0 * (double) enumdirection.getAdjacentX(), d1 * (double) enumdirection.getAdjacentY(), d2 * (double) enumdirection.getAdjacentZ());
                    }
                }

            }
        }
    }

    public int getSize() {
        return this.f.size();
    }

    public int getMaxStackSize() {
        return 64;
    }

    public boolean c(int i, int j) {
        if (i == 1) {
            this.h = j;
            if (j == 0) {
                this.i = TileEntityShulkerBox.AnimationPhase.CLOSING;
            }

            if (j == 1) {
                this.i = TileEntityShulkerBox.AnimationPhase.OPENING;
            }

            return true;
        } else {
            return super.c(i, j);
        }
    }

    public void startOpen(EntityHuman entityhuman) {
        if (!entityhuman.isSpectator()) {
            if (this.h < 0) {
                this.h = 0;
            }

            ++this.h;
            this.world.playBlockAction(this.position, this.getBlock(), 1, this.h);
            if (this.h == 1) {
                this.world.a((EntityHuman) null, this.position, SoundEffects.gC, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
            }
        }

    }

    public void closeContainer(EntityHuman entityhuman) {
        if (!entityhuman.isSpectator()) {
            --this.h;
            this.world.playBlockAction(this.position, this.getBlock(), 1, this.h);
            if (this.h <= 0) {
                this.world.a((EntityHuman) null, this.position, SoundEffects.gB, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
            }
        }

    }

    public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
        return new ContainerShulkerBox(playerinventory, this, entityhuman);
    }

    public String getContainerName() {
        return "minecraft:shulker_box";
    }

    public String getName() {
        return this.hasCustomName() ? this.o : "container.shulkerBox";
    }

    public static void a(DataConverterManager dataconvertermanager) {
        dataconvertermanager.a(DataConverterTypes.BLOCK_ENTITY, (DataInspector) (new DataInspectorItemList(TileEntityShulkerBox.class, new String[] { "Items"})));
    }

    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.e(nbttagcompound);
    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        return this.f(nbttagcompound);
    }

    public void e(NBTTagCompound nbttagcompound) {
        this.f = NonNullList.a(this.getSize(), ItemStack.a);
        if (!this.c(nbttagcompound) && nbttagcompound.hasKeyOfType("Items", 9)) {
            ContainerUtil.b(nbttagcompound, this.f);
        }

        if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
            this.o = nbttagcompound.getString("CustomName");
        }

    }

    public NBTTagCompound f(NBTTagCompound nbttagcompound) {
        if (!this.d(nbttagcompound)) {
            ContainerUtil.a(nbttagcompound, this.f, false);
        }

        if (this.hasCustomName()) {
            nbttagcompound.setString("CustomName", this.o);
        }

        if (!nbttagcompound.hasKey("Lock") && this.isLocked()) {
            this.getLock().a(nbttagcompound);
        }

        return nbttagcompound;
    }

    protected NonNullList<ItemStack> q() {
        return this.f;
    }

    public boolean x_() {
        Iterator iterator = this.f.iterator();

        ItemStack itemstack;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            itemstack = (ItemStack) iterator.next();
        } while (itemstack.isEmpty());

        return false;
    }

    public int[] getSlotsForFace(EnumDirection enumdirection) {
        return TileEntityShulkerBox.a;
    }

    public boolean canPlaceItemThroughFace(int i, ItemStack itemstack, EnumDirection enumdirection) {
        return !(Block.asBlock(itemstack.getItem()) instanceof BlockShulkerBox);
    }

    public boolean canTakeItemThroughFace(int i, ItemStack itemstack, EnumDirection enumdirection) {
        return true;
    }

    public void clear() {
        this.g = true;
        super.clear();
    }

    public boolean r() {
        return this.g;
    }

    public float a(float f) {
        return this.k + (this.j - this.k) * f;
    }

    @Nullable
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return new PacketPlayOutTileEntityData(this.position, 10, this.d());
    }

    public boolean E() {
        return this.p;
    }

    public void a(boolean flag) {
        this.p = flag;
    }

    public boolean F() {
        return !this.E() || !this.x_() || this.hasCustomName() || this.m != null;
    }

    static {
        for (int i = 0; i < TileEntityShulkerBox.a.length; TileEntityShulkerBox.a[i] = i++) {
            ;
        }

    }

    public static enum AnimationPhase {

        CLOSED, OPENING, OPENED, CLOSING;

        private AnimationPhase() {}
    }
}
