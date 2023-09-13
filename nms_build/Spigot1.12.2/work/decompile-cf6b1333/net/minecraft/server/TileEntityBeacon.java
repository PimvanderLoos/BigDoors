package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

public class TileEntityBeacon extends TileEntityContainer implements ITickable, IWorldInventory {

    public static final MobEffectList[][] a = new MobEffectList[][] { { MobEffects.FASTER_MOVEMENT, MobEffects.FASTER_DIG}, { MobEffects.RESISTANCE, MobEffects.JUMP}, { MobEffects.INCREASE_DAMAGE}, { MobEffects.REGENERATION}};
    private static final Set<MobEffectList> f = Sets.newHashSet();
    private final List<TileEntityBeacon.BeaconColorTracker> g = Lists.newArrayList();
    private boolean j;
    public int levels = -1;
    @Nullable
    public MobEffectList primaryEffect;
    @Nullable
    public MobEffectList secondaryEffect;
    private ItemStack inventorySlot;
    private String o;

    public TileEntityBeacon() {
        this.inventorySlot = ItemStack.a;
    }

    public void e() {
        if (this.world.getTime() % 80L == 0L) {
            this.n();
        }

    }

    public void n() {
        if (this.world != null) {
            this.F();
            this.E();
        }

    }

    private void E() {
        if (this.j && this.levels > 0 && !this.world.isClientSide && this.primaryEffect != null) {
            double d0 = (double) (this.levels * 10 + 10);
            byte b0 = 0;

            if (this.levels >= 4 && this.primaryEffect == this.secondaryEffect) {
                b0 = 1;
            }

            int i = (9 + this.levels * 2) * 20;
            int j = this.position.getX();
            int k = this.position.getY();
            int l = this.position.getZ();
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB((double) j, (double) k, (double) l, (double) (j + 1), (double) (k + 1), (double) (l + 1))).g(d0).b(0.0D, (double) this.world.getHeight(), 0.0D);
            List list = this.world.a(EntityHuman.class, axisalignedbb);
            Iterator iterator = list.iterator();

            EntityHuman entityhuman;

            while (iterator.hasNext()) {
                entityhuman = (EntityHuman) iterator.next();
                entityhuman.addEffect(new MobEffect(this.primaryEffect, i, b0, true, true));
            }

            if (this.levels >= 4 && this.primaryEffect != this.secondaryEffect && this.secondaryEffect != null) {
                iterator = list.iterator();

                while (iterator.hasNext()) {
                    entityhuman = (EntityHuman) iterator.next();
                    entityhuman.addEffect(new MobEffect(this.secondaryEffect, i, 0, true, true));
                }
            }
        }

    }

    private void F() {
        int i = this.position.getX();
        int j = this.position.getY();
        int k = this.position.getZ();
        int l = this.levels;

        this.levels = 0;
        this.g.clear();
        this.j = true;
        TileEntityBeacon.BeaconColorTracker tileentitybeacon_beaconcolortracker = new TileEntityBeacon.BeaconColorTracker(EnumColor.WHITE.f());

        this.g.add(tileentitybeacon_beaconcolortracker);
        boolean flag = true;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        int i1;

        for (i1 = j + 1; i1 < 256; ++i1) {
            IBlockData iblockdata = this.world.getType(blockposition_mutableblockposition.c(i, i1, k));
            float[] afloat;

            if (iblockdata.getBlock() == Blocks.STAINED_GLASS) {
                afloat = ((EnumColor) iblockdata.get(BlockStainedGlass.COLOR)).f();
            } else {
                if (iblockdata.getBlock() != Blocks.STAINED_GLASS_PANE) {
                    if (iblockdata.c() >= 15 && iblockdata.getBlock() != Blocks.BEDROCK) {
                        this.j = false;
                        this.g.clear();
                        break;
                    }

                    tileentitybeacon_beaconcolortracker.a();
                    continue;
                }

                afloat = ((EnumColor) iblockdata.get(BlockStainedGlassPane.COLOR)).f();
            }

            if (!flag) {
                afloat = new float[] { (tileentitybeacon_beaconcolortracker.b()[0] + afloat[0]) / 2.0F, (tileentitybeacon_beaconcolortracker.b()[1] + afloat[1]) / 2.0F, (tileentitybeacon_beaconcolortracker.b()[2] + afloat[2]) / 2.0F};
            }

            if (Arrays.equals(afloat, tileentitybeacon_beaconcolortracker.b())) {
                tileentitybeacon_beaconcolortracker.a();
            } else {
                tileentitybeacon_beaconcolortracker = new TileEntityBeacon.BeaconColorTracker(afloat);
                this.g.add(tileentitybeacon_beaconcolortracker);
            }

            flag = false;
        }

        if (this.j) {
            for (i1 = 1; i1 <= 4; this.levels = i1++) {
                int j1 = j - i1;

                if (j1 < 0) {
                    break;
                }

                boolean flag1 = true;

                for (int k1 = i - i1; k1 <= i + i1 && flag1; ++k1) {
                    for (int l1 = k - i1; l1 <= k + i1; ++l1) {
                        Block block = this.world.getType(new BlockPosition(k1, j1, l1)).getBlock();

                        if (block != Blocks.EMERALD_BLOCK && block != Blocks.GOLD_BLOCK && block != Blocks.DIAMOND_BLOCK && block != Blocks.IRON_BLOCK) {
                            flag1 = false;
                            break;
                        }
                    }
                }

                if (!flag1) {
                    break;
                }
            }

            if (this.levels == 0) {
                this.j = false;
            }
        }

        if (!this.world.isClientSide && l < this.levels) {
            Iterator iterator = this.world.a(EntityPlayer.class, (new AxisAlignedBB((double) i, (double) j, (double) k, (double) i, (double) (j - 4), (double) k)).grow(10.0D, 5.0D, 10.0D)).iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                CriterionTriggers.k.a(entityplayer, this);
            }
        }

    }

    public int s() {
        return this.levels;
    }

    @Nullable
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return new PacketPlayOutTileEntityData(this.position, 3, this.d());
    }

    public NBTTagCompound d() {
        return this.save(new NBTTagCompound());
    }

    @Nullable
    private static MobEffectList f(int i) {
        MobEffectList mobeffectlist = MobEffectList.fromId(i);

        return TileEntityBeacon.f.contains(mobeffectlist) ? mobeffectlist : null;
    }

    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.primaryEffect = f(nbttagcompound.getInt("Primary"));
        this.secondaryEffect = f(nbttagcompound.getInt("Secondary"));
        this.levels = nbttagcompound.getInt("Levels");
    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        nbttagcompound.setInt("Primary", MobEffectList.getId(this.primaryEffect));
        nbttagcompound.setInt("Secondary", MobEffectList.getId(this.secondaryEffect));
        nbttagcompound.setInt("Levels", this.levels);
        return nbttagcompound;
    }

    public int getSize() {
        return 1;
    }

    public boolean x_() {
        return this.inventorySlot.isEmpty();
    }

    public ItemStack getItem(int i) {
        return i == 0 ? this.inventorySlot : ItemStack.a;
    }

    public ItemStack splitStack(int i, int j) {
        if (i == 0 && !this.inventorySlot.isEmpty()) {
            if (j >= this.inventorySlot.getCount()) {
                ItemStack itemstack = this.inventorySlot;

                this.inventorySlot = ItemStack.a;
                return itemstack;
            } else {
                return this.inventorySlot.cloneAndSubtract(j);
            }
        } else {
            return ItemStack.a;
        }
    }

    public ItemStack splitWithoutUpdate(int i) {
        if (i == 0) {
            ItemStack itemstack = this.inventorySlot;

            this.inventorySlot = ItemStack.a;
            return itemstack;
        } else {
            return ItemStack.a;
        }
    }

    public void setItem(int i, ItemStack itemstack) {
        if (i == 0) {
            this.inventorySlot = itemstack;
        }

    }

    public String getName() {
        return this.hasCustomName() ? this.o : "container.beacon";
    }

    public boolean hasCustomName() {
        return this.o != null && !this.o.isEmpty();
    }

    public void setCustomName(String s) {
        this.o = s;
    }

    public int getMaxStackSize() {
        return 1;
    }

    public boolean a(EntityHuman entityhuman) {
        return this.world.getTileEntity(this.position) != this ? false : entityhuman.d((double) this.position.getX() + 0.5D, (double) this.position.getY() + 0.5D, (double) this.position.getZ() + 0.5D) <= 64.0D;
    }

    public void startOpen(EntityHuman entityhuman) {}

    public void closeContainer(EntityHuman entityhuman) {}

    public boolean b(int i, ItemStack itemstack) {
        return itemstack.getItem() == Items.EMERALD || itemstack.getItem() == Items.DIAMOND || itemstack.getItem() == Items.GOLD_INGOT || itemstack.getItem() == Items.IRON_INGOT;
    }

    public String getContainerName() {
        return "minecraft:beacon";
    }

    public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
        return new ContainerBeacon(playerinventory, this);
    }

    public int getProperty(int i) {
        switch (i) {
        case 0:
            return this.levels;

        case 1:
            return MobEffectList.getId(this.primaryEffect);

        case 2:
            return MobEffectList.getId(this.secondaryEffect);

        default:
            return 0;
        }
    }

    public void setProperty(int i, int j) {
        switch (i) {
        case 0:
            this.levels = j;
            break;

        case 1:
            this.primaryEffect = f(j);
            break;

        case 2:
            this.secondaryEffect = f(j);
        }

    }

    public int h() {
        return 3;
    }

    public void clear() {
        this.inventorySlot = ItemStack.a;
    }

    public boolean c(int i, int j) {
        if (i == 1) {
            this.n();
            return true;
        } else {
            return super.c(i, j);
        }
    }

    public int[] getSlotsForFace(EnumDirection enumdirection) {
        return new int[0];
    }

    public boolean canPlaceItemThroughFace(int i, ItemStack itemstack, EnumDirection enumdirection) {
        return false;
    }

    public boolean canTakeItemThroughFace(int i, ItemStack itemstack, EnumDirection enumdirection) {
        return false;
    }

    static {
        MobEffectList[][] amobeffectlist = TileEntityBeacon.a;
        int i = amobeffectlist.length;

        for (int j = 0; j < i; ++j) {
            MobEffectList[] amobeffectlist1 = amobeffectlist[j];

            Collections.addAll(TileEntityBeacon.f, amobeffectlist1);
        }

    }

    public static class BeaconColorTracker {

        private final float[] a;
        private int b;

        public BeaconColorTracker(float[] afloat) {
            this.a = afloat;
            this.b = 1;
        }

        protected void a() {
            ++this.b;
        }

        public float[] b() {
            return this.a;
        }
    }
}
