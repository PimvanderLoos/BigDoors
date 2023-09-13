package net.minecraft.world.level.block.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutTileEntityData;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.ChestLock;
import net.minecraft.world.ITileInventory;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerAccess;
import net.minecraft.world.inventory.ContainerBeacon;
import net.minecraft.world.inventory.IContainerProperties;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IBeaconBeam;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.phys.AxisAlignedBB;

public class TileEntityBeacon extends TileEntity implements ITileInventory {

    private static final int MAX_LEVELS = 4;
    public static final MobEffectList[][] BEACON_EFFECTS = new MobEffectList[][]{{MobEffects.MOVEMENT_SPEED, MobEffects.DIG_SPEED}, {MobEffects.DAMAGE_RESISTANCE, MobEffects.JUMP}, {MobEffects.DAMAGE_BOOST}, {MobEffects.REGENERATION}};
    private static final Set<MobEffectList> VALID_EFFECTS = (Set) Arrays.stream(TileEntityBeacon.BEACON_EFFECTS).flatMap(Arrays::stream).collect(Collectors.toSet());
    public static final int DATA_LEVELS = 0;
    public static final int DATA_PRIMARY = 1;
    public static final int DATA_SECONDARY = 2;
    public static final int NUM_DATA_VALUES = 3;
    private static final int BLOCKS_CHECK_PER_TICK = 10;
    List<TileEntityBeacon.BeaconColorTracker> beamSections = Lists.newArrayList();
    private List<TileEntityBeacon.BeaconColorTracker> checkingBeamSections = Lists.newArrayList();
    public int levels;
    private int lastCheckY;
    @Nullable
    public MobEffectList primaryPower;
    @Nullable
    public MobEffectList secondaryPower;
    @Nullable
    public IChatBaseComponent name;
    public ChestLock lockKey;
    private final IContainerProperties dataAccess;

    public TileEntityBeacon(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.BEACON, blockposition, iblockdata);
        this.lockKey = ChestLock.NO_LOCK;
        this.dataAccess = new IContainerProperties() {
            @Override
            public int getProperty(int i) {
                switch (i) {
                    case 0:
                        return TileEntityBeacon.this.levels;
                    case 1:
                        return MobEffectList.getId(TileEntityBeacon.this.primaryPower);
                    case 2:
                        return MobEffectList.getId(TileEntityBeacon.this.secondaryPower);
                    default:
                        return 0;
                }
            }

            @Override
            public void setProperty(int i, int j) {
                switch (i) {
                    case 0:
                        TileEntityBeacon.this.levels = j;
                        break;
                    case 1:
                        if (!TileEntityBeacon.this.level.isClientSide && !TileEntityBeacon.this.beamSections.isEmpty()) {
                            TileEntityBeacon.a(TileEntityBeacon.this.level, TileEntityBeacon.this.worldPosition, SoundEffects.BEACON_POWER_SELECT);
                        }

                        TileEntityBeacon.this.primaryPower = TileEntityBeacon.a(j);
                        break;
                    case 2:
                        TileEntityBeacon.this.secondaryPower = TileEntityBeacon.a(j);
                }

            }

            @Override
            public int a() {
                return 3;
            }
        };
    }

    public static void a(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityBeacon tileentitybeacon) {
        int i = blockposition.getX();
        int j = blockposition.getY();
        int k = blockposition.getZ();
        BlockPosition blockposition1;

        if (tileentitybeacon.lastCheckY < j) {
            blockposition1 = blockposition;
            tileentitybeacon.checkingBeamSections = Lists.newArrayList();
            tileentitybeacon.lastCheckY = blockposition.getY() - 1;
        } else {
            blockposition1 = new BlockPosition(i, tileentitybeacon.lastCheckY + 1, k);
        }

        TileEntityBeacon.BeaconColorTracker tileentitybeacon_beaconcolortracker = tileentitybeacon.checkingBeamSections.isEmpty() ? null : (TileEntityBeacon.BeaconColorTracker) tileentitybeacon.checkingBeamSections.get(tileentitybeacon.checkingBeamSections.size() - 1);
        int l = world.a(HeightMap.Type.WORLD_SURFACE, i, k);

        int i1;

        for (i1 = 0; i1 < 10 && blockposition1.getY() <= l; ++i1) {
            IBlockData iblockdata1 = world.getType(blockposition1);
            Block block = iblockdata1.getBlock();

            if (block instanceof IBeaconBeam) {
                float[] afloat = ((IBeaconBeam) block).a().getColor();

                if (tileentitybeacon.checkingBeamSections.size() <= 1) {
                    tileentitybeacon_beaconcolortracker = new TileEntityBeacon.BeaconColorTracker(afloat);
                    tileentitybeacon.checkingBeamSections.add(tileentitybeacon_beaconcolortracker);
                } else if (tileentitybeacon_beaconcolortracker != null) {
                    if (Arrays.equals(afloat, tileentitybeacon_beaconcolortracker.color)) {
                        tileentitybeacon_beaconcolortracker.a();
                    } else {
                        tileentitybeacon_beaconcolortracker = new TileEntityBeacon.BeaconColorTracker(new float[]{(tileentitybeacon_beaconcolortracker.color[0] + afloat[0]) / 2.0F, (tileentitybeacon_beaconcolortracker.color[1] + afloat[1]) / 2.0F, (tileentitybeacon_beaconcolortracker.color[2] + afloat[2]) / 2.0F});
                        tileentitybeacon.checkingBeamSections.add(tileentitybeacon_beaconcolortracker);
                    }
                }
            } else {
                if (tileentitybeacon_beaconcolortracker == null || iblockdata1.b((IBlockAccess) world, blockposition1) >= 15 && !iblockdata1.a(Blocks.BEDROCK)) {
                    tileentitybeacon.checkingBeamSections.clear();
                    tileentitybeacon.lastCheckY = l;
                    break;
                }

                tileentitybeacon_beaconcolortracker.a();
            }

            blockposition1 = blockposition1.up();
            ++tileentitybeacon.lastCheckY;
        }

        i1 = tileentitybeacon.levels;
        if (world.getTime() % 80L == 0L) {
            if (!tileentitybeacon.beamSections.isEmpty()) {
                tileentitybeacon.levels = a(world, i, j, k);
            }

            if (tileentitybeacon.levels > 0 && !tileentitybeacon.beamSections.isEmpty()) {
                applyEffects(world, blockposition, tileentitybeacon.levels, tileentitybeacon.primaryPower, tileentitybeacon.secondaryPower);
                a(world, blockposition, SoundEffects.BEACON_AMBIENT);
            }
        }

        if (tileentitybeacon.lastCheckY >= l) {
            tileentitybeacon.lastCheckY = world.getMinBuildHeight() - 1;
            boolean flag = i1 > 0;

            tileentitybeacon.beamSections = tileentitybeacon.checkingBeamSections;
            if (!world.isClientSide) {
                boolean flag1 = tileentitybeacon.levels > 0;

                if (!flag && flag1) {
                    a(world, blockposition, SoundEffects.BEACON_ACTIVATE);
                    Iterator iterator = world.a(EntityPlayer.class, (new AxisAlignedBB((double) i, (double) j, (double) k, (double) i, (double) (j - 4), (double) k)).grow(10.0D, 5.0D, 10.0D)).iterator();

                    while (iterator.hasNext()) {
                        EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                        CriterionTriggers.CONSTRUCT_BEACON.a(entityplayer, tileentitybeacon.levels);
                    }
                } else if (flag && !flag1) {
                    a(world, blockposition, SoundEffects.BEACON_DEACTIVATE);
                }
            }
        }

    }

    private static int a(World world, int i, int j, int k) {
        int l = 0;

        for (int i1 = 1; i1 <= 4; l = i1++) {
            int j1 = j - i1;

            if (j1 < world.getMinBuildHeight()) {
                break;
            }

            boolean flag = true;

            for (int k1 = i - i1; k1 <= i + i1 && flag; ++k1) {
                for (int l1 = k - i1; l1 <= k + i1; ++l1) {
                    if (!world.getType(new BlockPosition(k1, j1, l1)).a((Tag) TagsBlock.BEACON_BASE_BLOCKS)) {
                        flag = false;
                        break;
                    }
                }
            }

            if (!flag) {
                break;
            }
        }

        return l;
    }

    @Override
    public void aa_() {
        a(this.level, this.worldPosition, SoundEffects.BEACON_DEACTIVATE);
        super.aa_();
    }

    private static void applyEffects(World world, BlockPosition blockposition, int i, @Nullable MobEffectList mobeffectlist, @Nullable MobEffectList mobeffectlist1) {
        if (!world.isClientSide && mobeffectlist != null) {
            double d0 = (double) (i * 10 + 10);
            byte b0 = 0;

            if (i >= 4 && mobeffectlist == mobeffectlist1) {
                b0 = 1;
            }

            int j = (9 + i * 2) * 20;
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB(blockposition)).g(d0).b(0.0D, (double) world.getHeight(), 0.0D);
            List<EntityHuman> list = world.a(EntityHuman.class, axisalignedbb);
            Iterator iterator = list.iterator();

            EntityHuman entityhuman;

            while (iterator.hasNext()) {
                entityhuman = (EntityHuman) iterator.next();
                entityhuman.addEffect(new MobEffect(mobeffectlist, j, b0, true, true));
            }

            if (i >= 4 && mobeffectlist != mobeffectlist1 && mobeffectlist1 != null) {
                iterator = list.iterator();

                while (iterator.hasNext()) {
                    entityhuman = (EntityHuman) iterator.next();
                    entityhuman.addEffect(new MobEffect(mobeffectlist1, j, 0, true, true));
                }
            }

        }
    }

    public static void a(World world, BlockPosition blockposition, SoundEffect soundeffect) {
        world.playSound((EntityHuman) null, blockposition, soundeffect, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    public List<TileEntityBeacon.BeaconColorTracker> f() {
        return (List) (this.levels == 0 ? ImmutableList.of() : this.beamSections);
    }

    @Nullable
    @Override
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return new PacketPlayOutTileEntityData(this.worldPosition, 3, this.Z_());
    }

    @Override
    public NBTTagCompound Z_() {
        return this.save(new NBTTagCompound());
    }

    @Nullable
    static MobEffectList a(int i) {
        MobEffectList mobeffectlist = MobEffectList.fromId(i);

        return TileEntityBeacon.VALID_EFFECTS.contains(mobeffectlist) ? mobeffectlist : null;
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.primaryPower = a(nbttagcompound.getInt("Primary"));
        this.secondaryPower = a(nbttagcompound.getInt("Secondary"));
        if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
            this.name = IChatBaseComponent.ChatSerializer.a(nbttagcompound.getString("CustomName"));
        }

        this.lockKey = ChestLock.b(nbttagcompound);
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        nbttagcompound.setInt("Primary", MobEffectList.getId(this.primaryPower));
        nbttagcompound.setInt("Secondary", MobEffectList.getId(this.secondaryPower));
        nbttagcompound.setInt("Levels", this.levels);
        if (this.name != null) {
            nbttagcompound.setString("CustomName", IChatBaseComponent.ChatSerializer.a(this.name));
        }

        this.lockKey.a(nbttagcompound);
        return nbttagcompound;
    }

    public void setCustomName(@Nullable IChatBaseComponent ichatbasecomponent) {
        this.name = ichatbasecomponent;
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerinventory, EntityHuman entityhuman) {
        return TileEntityContainer.a(entityhuman, this.lockKey, this.getScoreboardDisplayName()) ? new ContainerBeacon(i, playerinventory, this.dataAccess, ContainerAccess.at(this.level, this.getPosition())) : null;
    }

    @Override
    public IChatBaseComponent getScoreboardDisplayName() {
        return (IChatBaseComponent) (this.name != null ? this.name : new ChatMessage("container.beacon"));
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        this.lastCheckY = world.getMinBuildHeight() - 1;
    }

    public static class BeaconColorTracker {

        final float[] color;
        private int height;

        public BeaconColorTracker(float[] afloat) {
            this.color = afloat;
            this.height = 1;
        }

        protected void a() {
            ++this.height;
        }

        public float[] b() {
            return this.color;
        }

        public int c() {
            return this.height;
        }
    }
}
