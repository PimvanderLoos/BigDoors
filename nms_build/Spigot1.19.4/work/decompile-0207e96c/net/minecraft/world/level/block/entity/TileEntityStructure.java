package net.minecraft.world.level.block.entity;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ResourceKeyInvalidException;
import net.minecraft.SystemUtils;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketPlayOutTileEntityData;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.util.UtilColor;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockStructure;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnumBlockMirror;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockPropertyStructureMode;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessorRotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class TileEntityStructure extends TileEntity {

    private static final int SCAN_CORNER_BLOCKS_RANGE = 5;
    public static final int MAX_OFFSET_PER_AXIS = 48;
    public static final int MAX_SIZE_PER_AXIS = 48;
    public static final String AUTHOR_TAG = "author";
    private MinecraftKey structureName;
    public String author = "";
    public String metaData = "";
    public BlockPosition structurePos = new BlockPosition(0, 1, 0);
    public BaseBlockPosition structureSize;
    public EnumBlockMirror mirror;
    public EnumBlockRotation rotation;
    public BlockPropertyStructureMode mode;
    public boolean ignoreEntities;
    private boolean powered;
    public boolean showAir;
    public boolean showBoundingBox;
    public float integrity;
    public long seed;

    public TileEntityStructure(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.STRUCTURE_BLOCK, blockposition, iblockdata);
        this.structureSize = BaseBlockPosition.ZERO;
        this.mirror = EnumBlockMirror.NONE;
        this.rotation = EnumBlockRotation.NONE;
        this.ignoreEntities = true;
        this.showBoundingBox = true;
        this.integrity = 1.0F;
        this.mode = (BlockPropertyStructureMode) iblockdata.getValue(BlockStructure.MODE);
    }

    @Override
    protected void saveAdditional(NBTTagCompound nbttagcompound) {
        super.saveAdditional(nbttagcompound);
        nbttagcompound.putString("name", this.getStructureName());
        nbttagcompound.putString("author", this.author);
        nbttagcompound.putString("metadata", this.metaData);
        nbttagcompound.putInt("posX", this.structurePos.getX());
        nbttagcompound.putInt("posY", this.structurePos.getY());
        nbttagcompound.putInt("posZ", this.structurePos.getZ());
        nbttagcompound.putInt("sizeX", this.structureSize.getX());
        nbttagcompound.putInt("sizeY", this.structureSize.getY());
        nbttagcompound.putInt("sizeZ", this.structureSize.getZ());
        nbttagcompound.putString("rotation", this.rotation.toString());
        nbttagcompound.putString("mirror", this.mirror.toString());
        nbttagcompound.putString("mode", this.mode.toString());
        nbttagcompound.putBoolean("ignoreEntities", this.ignoreEntities);
        nbttagcompound.putBoolean("powered", this.powered);
        nbttagcompound.putBoolean("showair", this.showAir);
        nbttagcompound.putBoolean("showboundingbox", this.showBoundingBox);
        nbttagcompound.putFloat("integrity", this.integrity);
        nbttagcompound.putLong("seed", this.seed);
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.setStructureName(nbttagcompound.getString("name"));
        this.author = nbttagcompound.getString("author");
        this.metaData = nbttagcompound.getString("metadata");
        int i = MathHelper.clamp(nbttagcompound.getInt("posX"), -48, 48);
        int j = MathHelper.clamp(nbttagcompound.getInt("posY"), -48, 48);
        int k = MathHelper.clamp(nbttagcompound.getInt("posZ"), -48, 48);

        this.structurePos = new BlockPosition(i, j, k);
        int l = MathHelper.clamp(nbttagcompound.getInt("sizeX"), 0, 48);
        int i1 = MathHelper.clamp(nbttagcompound.getInt("sizeY"), 0, 48);
        int j1 = MathHelper.clamp(nbttagcompound.getInt("sizeZ"), 0, 48);

        this.structureSize = new BaseBlockPosition(l, i1, j1);

        try {
            this.rotation = EnumBlockRotation.valueOf(nbttagcompound.getString("rotation"));
        } catch (IllegalArgumentException illegalargumentexception) {
            this.rotation = EnumBlockRotation.NONE;
        }

        try {
            this.mirror = EnumBlockMirror.valueOf(nbttagcompound.getString("mirror"));
        } catch (IllegalArgumentException illegalargumentexception1) {
            this.mirror = EnumBlockMirror.NONE;
        }

        try {
            this.mode = BlockPropertyStructureMode.valueOf(nbttagcompound.getString("mode"));
        } catch (IllegalArgumentException illegalargumentexception2) {
            this.mode = BlockPropertyStructureMode.DATA;
        }

        this.ignoreEntities = nbttagcompound.getBoolean("ignoreEntities");
        this.powered = nbttagcompound.getBoolean("powered");
        this.showAir = nbttagcompound.getBoolean("showair");
        this.showBoundingBox = nbttagcompound.getBoolean("showboundingbox");
        if (nbttagcompound.contains("integrity")) {
            this.integrity = nbttagcompound.getFloat("integrity");
        } else {
            this.integrity = 1.0F;
        }

        this.seed = nbttagcompound.getLong("seed");
        this.updateBlockState();
    }

    private void updateBlockState() {
        if (this.level != null) {
            BlockPosition blockposition = this.getBlockPos();
            IBlockData iblockdata = this.level.getBlockState(blockposition);

            if (iblockdata.is(Blocks.STRUCTURE_BLOCK)) {
                this.level.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockStructure.MODE, this.mode), 2);
            }

        }
    }

    @Override
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return PacketPlayOutTileEntityData.create(this);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public boolean usedBy(EntityHuman entityhuman) {
        if (!entityhuman.canUseGameMasterBlocks()) {
            return false;
        } else {
            if (entityhuman.getCommandSenderWorld().isClientSide) {
                entityhuman.openStructureBlock(this);
            }

            return true;
        }
    }

    public String getStructureName() {
        return this.structureName == null ? "" : this.structureName.toString();
    }

    public String getStructurePath() {
        return this.structureName == null ? "" : this.structureName.getPath();
    }

    public boolean hasStructureName() {
        return this.structureName != null;
    }

    public void setStructureName(@Nullable String s) {
        this.setStructureName(UtilColor.isNullOrEmpty(s) ? null : MinecraftKey.tryParse(s));
    }

    public void setStructureName(@Nullable MinecraftKey minecraftkey) {
        this.structureName = minecraftkey;
    }

    public void createdBy(EntityLiving entityliving) {
        this.author = entityliving.getName().getString();
    }

    public BlockPosition getStructurePos() {
        return this.structurePos;
    }

    public void setStructurePos(BlockPosition blockposition) {
        this.structurePos = blockposition;
    }

    public BaseBlockPosition getStructureSize() {
        return this.structureSize;
    }

    public void setStructureSize(BaseBlockPosition baseblockposition) {
        this.structureSize = baseblockposition;
    }

    public EnumBlockMirror getMirror() {
        return this.mirror;
    }

    public void setMirror(EnumBlockMirror enumblockmirror) {
        this.mirror = enumblockmirror;
    }

    public EnumBlockRotation getRotation() {
        return this.rotation;
    }

    public void setRotation(EnumBlockRotation enumblockrotation) {
        this.rotation = enumblockrotation;
    }

    public String getMetaData() {
        return this.metaData;
    }

    public void setMetaData(String s) {
        this.metaData = s;
    }

    public BlockPropertyStructureMode getMode() {
        return this.mode;
    }

    public void setMode(BlockPropertyStructureMode blockpropertystructuremode) {
        this.mode = blockpropertystructuremode;
        IBlockData iblockdata = this.level.getBlockState(this.getBlockPos());

        if (iblockdata.is(Blocks.STRUCTURE_BLOCK)) {
            this.level.setBlock(this.getBlockPos(), (IBlockData) iblockdata.setValue(BlockStructure.MODE, blockpropertystructuremode), 2);
        }

    }

    public boolean isIgnoreEntities() {
        return this.ignoreEntities;
    }

    public void setIgnoreEntities(boolean flag) {
        this.ignoreEntities = flag;
    }

    public float getIntegrity() {
        return this.integrity;
    }

    public void setIntegrity(float f) {
        this.integrity = f;
    }

    public long getSeed() {
        return this.seed;
    }

    public void setSeed(long i) {
        this.seed = i;
    }

    public boolean detectSize() {
        if (this.mode != BlockPropertyStructureMode.SAVE) {
            return false;
        } else {
            BlockPosition blockposition = this.getBlockPos();
            boolean flag = true;
            BlockPosition blockposition1 = new BlockPosition(blockposition.getX() - 80, this.level.getMinBuildHeight(), blockposition.getZ() - 80);
            BlockPosition blockposition2 = new BlockPosition(blockposition.getX() + 80, this.level.getMaxBuildHeight() - 1, blockposition.getZ() + 80);
            Stream<BlockPosition> stream = this.getRelatedCorners(blockposition1, blockposition2);

            return calculateEnclosingBoundingBox(blockposition, stream).filter((structureboundingbox) -> {
                int i = structureboundingbox.maxX() - structureboundingbox.minX();
                int j = structureboundingbox.maxY() - structureboundingbox.minY();
                int k = structureboundingbox.maxZ() - structureboundingbox.minZ();

                if (i > 1 && j > 1 && k > 1) {
                    this.structurePos = new BlockPosition(structureboundingbox.minX() - blockposition.getX() + 1, structureboundingbox.minY() - blockposition.getY() + 1, structureboundingbox.minZ() - blockposition.getZ() + 1);
                    this.structureSize = new BaseBlockPosition(i - 1, j - 1, k - 1);
                    this.setChanged();
                    IBlockData iblockdata = this.level.getBlockState(blockposition);

                    this.level.sendBlockUpdated(blockposition, iblockdata, iblockdata, 3);
                    return true;
                } else {
                    return false;
                }
            }).isPresent();
        }
    }

    private Stream<BlockPosition> getRelatedCorners(BlockPosition blockposition, BlockPosition blockposition1) {
        Stream stream = BlockPosition.betweenClosedStream(blockposition, blockposition1).filter((blockposition2) -> {
            return this.level.getBlockState(blockposition2).is(Blocks.STRUCTURE_BLOCK);
        });
        World world = this.level;

        Objects.requireNonNull(this.level);
        return stream.map(world::getBlockEntity).filter((tileentity) -> {
            return tileentity instanceof TileEntityStructure;
        }).map((tileentity) -> {
            return (TileEntityStructure) tileentity;
        }).filter((tileentitystructure) -> {
            return tileentitystructure.mode == BlockPropertyStructureMode.CORNER && Objects.equals(this.structureName, tileentitystructure.structureName);
        }).map(TileEntity::getBlockPos);
    }

    private static Optional<StructureBoundingBox> calculateEnclosingBoundingBox(BlockPosition blockposition, Stream<BlockPosition> stream) {
        Iterator<BlockPosition> iterator = stream.iterator();

        if (!iterator.hasNext()) {
            return Optional.empty();
        } else {
            BlockPosition blockposition1 = (BlockPosition) iterator.next();
            StructureBoundingBox structureboundingbox = new StructureBoundingBox(blockposition1);

            if (iterator.hasNext()) {
                Objects.requireNonNull(structureboundingbox);
                iterator.forEachRemaining(structureboundingbox::encapsulate);
            } else {
                structureboundingbox.encapsulate(blockposition);
            }

            return Optional.of(structureboundingbox);
        }
    }

    public boolean saveStructure() {
        return this.saveStructure(true);
    }

    public boolean saveStructure(boolean flag) {
        if (this.mode == BlockPropertyStructureMode.SAVE && !this.level.isClientSide && this.structureName != null) {
            BlockPosition blockposition = this.getBlockPos().offset(this.structurePos);
            WorldServer worldserver = (WorldServer) this.level;
            StructureTemplateManager structuretemplatemanager = worldserver.getStructureManager();

            DefinedStructure definedstructure;

            try {
                definedstructure = structuretemplatemanager.getOrCreate(this.structureName);
            } catch (ResourceKeyInvalidException resourcekeyinvalidexception) {
                return false;
            }

            definedstructure.fillFromWorld(this.level, blockposition, this.structureSize, !this.ignoreEntities, Blocks.STRUCTURE_VOID);
            definedstructure.setAuthor(this.author);
            if (flag) {
                try {
                    return structuretemplatemanager.save(this.structureName);
                } catch (ResourceKeyInvalidException resourcekeyinvalidexception1) {
                    return false;
                }
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean loadStructure(WorldServer worldserver) {
        return this.loadStructure(worldserver, true);
    }

    public static RandomSource createRandom(long i) {
        return i == 0L ? RandomSource.create(SystemUtils.getMillis()) : RandomSource.create(i);
    }

    public boolean loadStructure(WorldServer worldserver, boolean flag) {
        if (this.mode == BlockPropertyStructureMode.LOAD && this.structureName != null) {
            StructureTemplateManager structuretemplatemanager = worldserver.getStructureManager();

            Optional optional;

            try {
                optional = structuretemplatemanager.get(this.structureName);
            } catch (ResourceKeyInvalidException resourcekeyinvalidexception) {
                return false;
            }

            return !optional.isPresent() ? false : this.loadStructure(worldserver, flag, (DefinedStructure) optional.get());
        } else {
            return false;
        }
    }

    public boolean loadStructure(WorldServer worldserver, boolean flag, DefinedStructure definedstructure) {
        BlockPosition blockposition = this.getBlockPos();

        if (!UtilColor.isNullOrEmpty(definedstructure.getAuthor())) {
            this.author = definedstructure.getAuthor();
        }

        BaseBlockPosition baseblockposition = definedstructure.getSize();
        boolean flag1 = this.structureSize.equals(baseblockposition);

        if (!flag1) {
            this.structureSize = baseblockposition;
            this.setChanged();
            IBlockData iblockdata = worldserver.getBlockState(blockposition);

            worldserver.sendBlockUpdated(blockposition, iblockdata, iblockdata, 3);
        }

        if (flag && !flag1) {
            return false;
        } else {
            DefinedStructureInfo definedstructureinfo = (new DefinedStructureInfo()).setMirror(this.mirror).setRotation(this.rotation).setIgnoreEntities(this.ignoreEntities);

            if (this.integrity < 1.0F) {
                definedstructureinfo.clearProcessors().addProcessor(new DefinedStructureProcessorRotation(MathHelper.clamp(this.integrity, 0.0F, 1.0F))).setRandom(createRandom(this.seed));
            }

            BlockPosition blockposition1 = blockposition.offset(this.structurePos);

            definedstructure.placeInWorld(worldserver, blockposition1, blockposition1, definedstructureinfo, createRandom(this.seed), 2);
            return true;
        }
    }

    public void unloadStructure() {
        if (this.structureName != null) {
            WorldServer worldserver = (WorldServer) this.level;
            StructureTemplateManager structuretemplatemanager = worldserver.getStructureManager();

            structuretemplatemanager.remove(this.structureName);
        }
    }

    public boolean isStructureLoadable() {
        if (this.mode == BlockPropertyStructureMode.LOAD && !this.level.isClientSide && this.structureName != null) {
            WorldServer worldserver = (WorldServer) this.level;
            StructureTemplateManager structuretemplatemanager = worldserver.getStructureManager();

            try {
                return structuretemplatemanager.get(this.structureName).isPresent();
            } catch (ResourceKeyInvalidException resourcekeyinvalidexception) {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isPowered() {
        return this.powered;
    }

    public void setPowered(boolean flag) {
        this.powered = flag;
    }

    public boolean getShowAir() {
        return this.showAir;
    }

    public void setShowAir(boolean flag) {
        this.showAir = flag;
    }

    public boolean getShowBoundingBox() {
        return this.showBoundingBox;
    }

    public void setShowBoundingBox(boolean flag) {
        this.showBoundingBox = flag;
    }

    public static enum UpdateType {

        UPDATE_DATA, SAVE_AREA, LOAD_AREA, SCAN_AREA;

        private UpdateType() {}
    }
}
