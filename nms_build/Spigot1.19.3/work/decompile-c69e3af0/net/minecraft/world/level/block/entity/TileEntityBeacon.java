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
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutTileEntityData;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.ChestLock;
import net.minecraft.world.INamableTileEntity;
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
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IBeaconBeam;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.phys.AxisAlignedBB;

public class TileEntityBeacon extends TileEntity implements ITileInventory, INamableTileEntity {

    private static final int MAX_LEVELS = 4;
    public static final MobEffectList[][] BEACON_EFFECTS = new MobEffectList[][]{{MobEffects.MOVEMENT_SPEED, MobEffects.DIG_SPEED}, {MobEffects.DAMAGE_RESISTANCE, MobEffects.JUMP}, {MobEffects.DAMAGE_BOOST}, {MobEffects.REGENERATION}};
    private static final Set<MobEffectList> VALID_EFFECTS = (Set) Arrays.stream(TileEntityBeacon.BEACON_EFFECTS).flatMap(Arrays::stream).collect(Collectors.toSet());
    public static final int DATA_LEVELS = 0;
    public static final int DATA_PRIMARY = 1;
    public static final int DATA_SECONDARY = 2;
    public static final int NUM_DATA_VALUES = 3;
    private static final int BLOCKS_CHECK_PER_TICK = 10;
    private static final IChatBaseComponent DEFAULT_NAME = IChatBaseComponent.translatable("container.beacon");
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
            public int get(int i) {
                int j;

                switch (i) {
                    case 0:
                        j = TileEntityBeacon.this.levels;
                        break;
                    case 1:
                        j = MobEffectList.getIdFromNullable(TileEntityBeacon.this.primaryPower);
                        break;
                    case 2:
                        j = MobEffectList.getIdFromNullable(TileEntityBeacon.this.secondaryPower);
                        break;
                    default:
                        j = 0;
                }

                return j;
            }

            @Override
            public void set(int i, int j) {
                switch (i) {
                    case 0:
                        TileEntityBeacon.this.levels = j;
                        break;
                    case 1:
                        if (!TileEntityBeacon.this.level.isClientSide && !TileEntityBeacon.this.beamSections.isEmpty()) {
                            TileEntityBeacon.playSound(TileEntityBeacon.this.level, TileEntityBeacon.this.worldPosition, SoundEffects.BEACON_POWER_SELECT);
                        }

                        TileEntityBeacon.this.primaryPower = TileEntityBeacon.getValidEffectById(j);
                        break;
                    case 2:
                        TileEntityBeacon.this.secondaryPower = TileEntityBeacon.getValidEffectById(j);
                }

            }

            @Override
            public int getCount() {
                return 3;
            }
        };
    }

    public static void tick(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityBeacon tileentitybeacon) {
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
        int l = world.getHeight(HeightMap.Type.WORLD_SURFACE, i, k);

        int i1;

        for (i1 = 0; i1 < 10 && blockposition1.getY() <= l; ++i1) {
            IBlockData iblockdata1 = world.getBlockState(blockposition1);
            Block block = iblockdata1.getBlock();

            if (block instanceof IBeaconBeam) {
                float[] afloat = ((IBeaconBeam) block).getColor().getTextureDiffuseColors();

                if (tileentitybeacon.checkingBeamSections.size() <= 1) {
                    tileentitybeacon_beaconcolortracker = new TileEntityBeacon.BeaconColorTracker(afloat);
                    tileentitybeacon.checkingBeamSections.add(tileentitybeacon_beaconcolortracker);
                } else if (tileentitybeacon_beaconcolortracker != null) {
                    if (Arrays.equals(afloat, tileentitybeacon_beaconcolortracker.color)) {
                        tileentitybeacon_beaconcolortracker.increaseHeight();
                    } else {
                        tileentitybeacon_beaconcolortracker = new TileEntityBeacon.BeaconColorTracker(new float[]{(tileentitybeacon_beaconcolortracker.color[0] + afloat[0]) / 2.0F, (tileentitybeacon_beaconcolortracker.color[1] + afloat[1]) / 2.0F, (tileentitybeacon_beaconcolortracker.color[2] + afloat[2]) / 2.0F});
                        tileentitybeacon.checkingBeamSections.add(tileentitybeacon_beaconcolortracker);
                    }
                }
            } else {
                if (tileentitybeacon_beaconcolortracker == null || iblockdata1.getLightBlock(world, blockposition1) >= 15 && !iblockdata1.is(Blocks.BEDROCK)) {
                    tileentitybeacon.checkingBeamSections.clear();
                    tileentitybeacon.lastCheckY = l;
                    break;
                }

                tileentitybeacon_beaconcolortracker.increaseHeight();
            }

            blockposition1 = blockposition1.above();
            ++tileentitybeacon.lastCheckY;
        }

        i1 = tileentitybeacon.levels;
        if (world.getGameTime() % 80L == 0L) {
            if (!tileentitybeacon.beamSections.isEmpty()) {
                tileentitybeacon.levels = updateBase(world, i, j, k);
            }

            if (tileentitybeacon.levels > 0 && !tileentitybeacon.beamSections.isEmpty()) {
                applyEffects(world, blockposition, tileentitybeacon.levels, tileentitybeacon.primaryPower, tileentitybeacon.secondaryPower);
                playSound(world, blockposition, SoundEffects.BEACON_AMBIENT);
            }
        }

        if (tileentitybeacon.lastCheckY >= l) {
            tileentitybeacon.lastCheckY = world.getMinBuildHeight() - 1;
            boolean flag = i1 > 0;

            tileentitybeacon.beamSections = tileentitybeacon.checkingBeamSections;
            if (!world.isClientSide) {
                boolean flag1 = tileentitybeacon.levels > 0;

                if (!flag && flag1) {
                    playSound(world, blockposition, SoundEffects.BEACON_ACTIVATE);
                    Iterator iterator = world.getEntitiesOfClass(EntityPlayer.class, (new AxisAlignedBB((double) i, (double) j, (double) k, (double) i, (double) (j - 4), (double) k)).inflate(10.0D, 5.0D, 10.0D)).iterator();

                    while (iterator.hasNext()) {
                        EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                        CriterionTriggers.CONSTRUCT_BEACON.trigger(entityplayer, tileentitybeacon.levels);
                    }
                } else if (flag && !flag1) {
                    playSound(world, blockposition, SoundEffects.BEACON_DEACTIVATE);
                }
            }
        }

    }

    private static int updateBase(World world, int i, int j, int k) {
        int l = 0;

        for (int i1 = 1; i1 <= 4; l = i1++) {
            int j1 = j - i1;

            if (j1 < world.getMinBuildHeight()) {
                break;
            }

            boolean flag = true;

            for (int k1 = i - i1; k1 <= i + i1 && flag; ++k1) {
                for (int l1 = k - i1; l1 <= k + i1; ++l1) {
                    if (!world.getBlockState(new BlockPosition(k1, j1, l1)).is(TagsBlock.BEACON_BASE_BLOCKS)) {
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
    public void setRemoved() {
        playSound(this.level, this.worldPosition, SoundEffects.BEACON_DEACTIVATE);
        super.setRemoved();
    }

    private static void applyEffects(World world, BlockPosition blockposition, int i, @Nullable MobEffectList mobeffectlist, @Nullable MobEffectList mobeffectlist1) {
        if (!world.isClientSide && mobeffectlist != null) {
            double d0 = (double) (i * 10 + 10);
            byte b0 = 0;

            if (i >= 4 && mobeffectlist == mobeffectlist1) {
                b0 = 1;
            }

            int j = (9 + i * 2) * 20;
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB(blockposition)).inflate(d0).expandTowards(0.0D, (double) world.getHeight(), 0.0D);
            List<EntityHuman> list = world.getEntitiesOfClass(EntityHuman.class, axisalignedbb);
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

    public static void playSound(World world, BlockPosition blockposition, SoundEffect soundeffect) {
        world.playSound((EntityHuman) null, blockposition, soundeffect, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    public List<TileEntityBeacon.BeaconColorTracker> getBeamSections() {
        return (List) (this.levels == 0 ? ImmutableList.of() : this.beamSections);
    }

    @Override
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return PacketPlayOutTileEntityData.create(this);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Nullable
    static MobEffectList getValidEffectById(int i) {
        MobEffectList mobeffectlist = MobEffectList.byId(i);

        return TileEntityBeacon.VALID_EFFECTS.contains(mobeffectlist) ? mobeffectlist : null;
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.primaryPower = getValidEffectById(nbttagcompound.getInt("Primary"));
        this.secondaryPower = getValidEffectById(nbttagcompound.getInt("Secondary"));
        if (nbttagcompound.contains("CustomName", 8)) {
            this.name = IChatBaseComponent.ChatSerializer.fromJson(nbttagcompound.getString("CustomName"));
        }

        this.lockKey = ChestLock.fromTag(nbttagcompound);
    }

    @Override
    protected void saveAdditional(NBTTagCompound nbttagcompound) {
        super.saveAdditional(nbttagcompound);
        nbttagcompound.putInt("Primary", MobEffectList.getIdFromNullable(this.primaryPower));
        nbttagcompound.putInt("Secondary", MobEffectList.getIdFromNullable(this.secondaryPower));
        nbttagcompound.putInt("Levels", this.levels);
        if (this.name != null) {
            nbttagcompound.putString("CustomName", IChatBaseComponent.ChatSerializer.toJson(this.name));
        }

        this.lockKey.addToTag(nbttagcompound);
    }

    public void setCustomName(@Nullable IChatBaseComponent ichatbasecomponent) {
        this.name = ichatbasecomponent;
    }

    @Nullable
    @Override
    public IChatBaseComponent getCustomName() {
        return this.name;
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerinventory, EntityHuman entityhuman) {
        return TileEntityContainer.canUnlock(entityhuman, this.lockKey, this.getDisplayName()) ? new ContainerBeacon(i, playerinventory, this.dataAccess, ContainerAccess.create(this.level, this.getBlockPos())) : null;
    }

    @Override
    public IChatBaseComponent getDisplayName() {
        return this.getName();
    }

    @Override
    public IChatBaseComponent getName() {
        return this.name != null ? this.name : TileEntityBeacon.DEFAULT_NAME;
    }

    @Override
    public void setLevel(World world) {
        super.setLevel(world);
        this.lastCheckY = world.getMinBuildHeight() - 1;
    }

    public static class BeaconColorTracker {

        final float[] color;
        private int height;

        public BeaconColorTracker(float[] afloat) {
            this.color = afloat;
            this.height = 1;
        }

        protected void increaseHeight() {
            ++this.height;
        }

        public float[] getColor() {
            return this.color;
        }

        public int getHeight() {
            return this.height;
        }
    }
}
