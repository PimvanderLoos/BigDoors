package net.minecraft.world.level.block.entity;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
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
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureProcessorRotation;

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
        this.mode = (BlockPropertyStructureMode) iblockdata.get(BlockStructure.MODE);
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        nbttagcompound.setString("name", this.getStructureName());
        nbttagcompound.setString("author", this.author);
        nbttagcompound.setString("metadata", this.metaData);
        nbttagcompound.setInt("posX", this.structurePos.getX());
        nbttagcompound.setInt("posY", this.structurePos.getY());
        nbttagcompound.setInt("posZ", this.structurePos.getZ());
        nbttagcompound.setInt("sizeX", this.structureSize.getX());
        nbttagcompound.setInt("sizeY", this.structureSize.getY());
        nbttagcompound.setInt("sizeZ", this.structureSize.getZ());
        nbttagcompound.setString("rotation", this.rotation.toString());
        nbttagcompound.setString("mirror", this.mirror.toString());
        nbttagcompound.setString("mode", this.mode.toString());
        nbttagcompound.setBoolean("ignoreEntities", this.ignoreEntities);
        nbttagcompound.setBoolean("powered", this.powered);
        nbttagcompound.setBoolean("showair", this.showAir);
        nbttagcompound.setBoolean("showboundingbox", this.showBoundingBox);
        nbttagcompound.setFloat("integrity", this.integrity);
        nbttagcompound.setLong("seed", this.seed);
        return nbttagcompound;
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
        if (nbttagcompound.hasKey("integrity")) {
            this.integrity = nbttagcompound.getFloat("integrity");
        } else {
            this.integrity = 1.0F;
        }

        this.seed = nbttagcompound.getLong("seed");
        this.F();
    }

    private void F() {
        if (this.level != null) {
            BlockPosition blockposition = this.getPosition();
            IBlockData iblockdata = this.level.getType(blockposition);

            if (iblockdata.a(Blocks.STRUCTURE_BLOCK)) {
                this.level.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockStructure.MODE, this.mode), 2);
            }

        }
    }

    @Nullable
    @Override
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return new PacketPlayOutTileEntityData(this.worldPosition, 7, this.Z_());
    }

    @Override
    public NBTTagCompound Z_() {
        return this.save(new NBTTagCompound());
    }

    public boolean a(EntityHuman entityhuman) {
        if (!entityhuman.isCreativeAndOp()) {
            return false;
        } else {
            if (entityhuman.getWorld().isClientSide) {
                entityhuman.a(this);
            }

            return true;
        }
    }

    public String getStructureName() {
        return this.structureName == null ? "" : this.structureName.toString();
    }

    public String f() {
        return this.structureName == null ? "" : this.structureName.getKey();
    }

    public boolean g() {
        return this.structureName != null;
    }

    public void setStructureName(@Nullable String s) {
        this.a(UtilColor.b(s) ? null : MinecraftKey.a(s));
    }

    public void a(@Nullable MinecraftKey minecraftkey) {
        this.structureName = minecraftkey;
    }

    public void setAuthor(EntityLiving entityliving) {
        this.author = entityliving.getDisplayName().getString();
    }

    public BlockPosition h() {
        return this.structurePos;
    }

    public void a(BlockPosition blockposition) {
        this.structurePos = blockposition;
    }

    public BaseBlockPosition i() {
        return this.structureSize;
    }

    public void a(BaseBlockPosition baseblockposition) {
        this.structureSize = baseblockposition;
    }

    public EnumBlockMirror j() {
        return this.mirror;
    }

    public void a(EnumBlockMirror enumblockmirror) {
        this.mirror = enumblockmirror;
    }

    public EnumBlockRotation s() {
        return this.rotation;
    }

    public void a(EnumBlockRotation enumblockrotation) {
        this.rotation = enumblockrotation;
    }

    public String t() {
        return this.metaData;
    }

    public void b(String s) {
        this.metaData = s;
    }

    public BlockPropertyStructureMode getUsageMode() {
        return this.mode;
    }

    public void setUsageMode(BlockPropertyStructureMode blockpropertystructuremode) {
        this.mode = blockpropertystructuremode;
        IBlockData iblockdata = this.level.getType(this.getPosition());

        if (iblockdata.a(Blocks.STRUCTURE_BLOCK)) {
            this.level.setTypeAndData(this.getPosition(), (IBlockData) iblockdata.set(BlockStructure.MODE, blockpropertystructuremode), 2);
        }

    }

    public boolean v() {
        return this.ignoreEntities;
    }

    public void a(boolean flag) {
        this.ignoreEntities = flag;
    }

    public float w() {
        return this.integrity;
    }

    public void a(float f) {
        this.integrity = f;
    }

    public long x() {
        return this.seed;
    }

    public void a(long i) {
        this.seed = i;
    }

    public boolean y() {
        if (this.mode != BlockPropertyStructureMode.SAVE) {
            return false;
        } else {
            BlockPosition blockposition = this.getPosition();
            boolean flag = true;
            BlockPosition blockposition1 = new BlockPosition(blockposition.getX() - 80, this.level.getMinBuildHeight(), blockposition.getZ() - 80);
            BlockPosition blockposition2 = new BlockPosition(blockposition.getX() + 80, this.level.getMaxBuildHeight() - 1, blockposition.getZ() + 80);
            Stream<BlockPosition> stream = this.a(blockposition1, blockposition2);

            return a(blockposition, stream).filter((structureboundingbox) -> {
                int i = structureboundingbox.j() - structureboundingbox.g();
                int j = structureboundingbox.k() - structureboundingbox.h();
                int k = structureboundingbox.l() - structureboundingbox.i();

                if (i > 1 && j > 1 && k > 1) {
                    this.structurePos = new BlockPosition(structureboundingbox.g() - blockposition.getX() + 1, structureboundingbox.h() - blockposition.getY() + 1, structureboundingbox.i() - blockposition.getZ() + 1);
                    this.structureSize = new BaseBlockPosition(i - 1, j - 1, k - 1);
                    this.update();
                    IBlockData iblockdata = this.level.getType(blockposition);

                    this.level.notify(blockposition, iblockdata, iblockdata, 3);
                    return true;
                } else {
                    return false;
                }
            }).isPresent();
        }
    }

    private Stream<BlockPosition> a(BlockPosition blockposition, BlockPosition blockposition1) {
        Stream stream = BlockPosition.b(blockposition, blockposition1).filter((blockposition2) -> {
            return this.level.getType(blockposition2).a(Blocks.STRUCTURE_BLOCK);
        });
        World world = this.level;

        Objects.requireNonNull(this.level);
        return stream.map(world::getTileEntity).filter((tileentity) -> {
            return tileentity instanceof TileEntityStructure;
        }).map((tileentity) -> {
            return (TileEntityStructure) tileentity;
        }).filter((tileentitystructure) -> {
            return tileentitystructure.mode == BlockPropertyStructureMode.CORNER && Objects.equals(this.structureName, tileentitystructure.structureName);
        }).map(TileEntity::getPosition);
    }

    private static Optional<StructureBoundingBox> a(BlockPosition blockposition, Stream<BlockPosition> stream) {
        Iterator<BlockPosition> iterator = stream.iterator();

        if (!iterator.hasNext()) {
            return Optional.empty();
        } else {
            BlockPosition blockposition1 = (BlockPosition) iterator.next();
            StructureBoundingBox structureboundingbox = new StructureBoundingBox(blockposition1);

            if (iterator.hasNext()) {
                Objects.requireNonNull(structureboundingbox);
                iterator.forEachRemaining(structureboundingbox::a);
            } else {
                structureboundingbox.a(blockposition);
            }

            return Optional.of(structureboundingbox);
        }
    }

    public boolean z() {
        return this.b(true);
    }

    public boolean b(boolean flag) {
        if (this.mode == BlockPropertyStructureMode.SAVE && !this.level.isClientSide && this.structureName != null) {
            BlockPosition blockposition = this.getPosition().f(this.structurePos);
            WorldServer worldserver = (WorldServer) this.level;
            DefinedStructureManager definedstructuremanager = worldserver.p();

            DefinedStructure definedstructure;

            try {
                definedstructure = definedstructuremanager.a(this.structureName);
            } catch (ResourceKeyInvalidException resourcekeyinvalidexception) {
                return false;
            }

            definedstructure.a(this.level, blockposition, this.structureSize, !this.ignoreEntities, Blocks.STRUCTURE_VOID);
            definedstructure.a(this.author);
            if (flag) {
                try {
                    return definedstructuremanager.c(this.structureName);
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

    public boolean a(WorldServer worldserver) {
        return this.a(worldserver, true);
    }

    private static Random b(long i) {
        return i == 0L ? new Random(SystemUtils.getMonotonicMillis()) : new Random(i);
    }

    public boolean a(WorldServer worldserver, boolean flag) {
        if (this.mode == BlockPropertyStructureMode.LOAD && this.structureName != null) {
            DefinedStructureManager definedstructuremanager = worldserver.p();

            Optional optional;

            try {
                optional = definedstructuremanager.b(this.structureName);
            } catch (ResourceKeyInvalidException resourcekeyinvalidexception) {
                return false;
            }

            return !optional.isPresent() ? false : this.a(worldserver, flag, (DefinedStructure) optional.get());
        } else {
            return false;
        }
    }

    public boolean a(WorldServer worldserver, boolean flag, DefinedStructure definedstructure) {
        BlockPosition blockposition = this.getPosition();

        if (!UtilColor.b(definedstructure.b())) {
            this.author = definedstructure.b();
        }

        BaseBlockPosition baseblockposition = definedstructure.a();
        boolean flag1 = this.structureSize.equals(baseblockposition);

        if (!flag1) {
            this.structureSize = baseblockposition;
            this.update();
            IBlockData iblockdata = worldserver.getType(blockposition);

            worldserver.notify(blockposition, iblockdata, iblockdata, 3);
        }

        if (flag && !flag1) {
            return false;
        } else {
            DefinedStructureInfo definedstructureinfo = (new DefinedStructureInfo()).a(this.mirror).a(this.rotation).a(this.ignoreEntities);

            if (this.integrity < 1.0F) {
                definedstructureinfo.b().a((DefinedStructureProcessor) (new DefinedStructureProcessorRotation(MathHelper.a(this.integrity, 0.0F, 1.0F)))).a(b(this.seed));
            }

            BlockPosition blockposition1 = blockposition.f(this.structurePos);

            definedstructure.a(worldserver, blockposition1, blockposition1, definedstructureinfo, b(this.seed), 2);
            return true;
        }
    }

    public void A() {
        if (this.structureName != null) {
            WorldServer worldserver = (WorldServer) this.level;
            DefinedStructureManager definedstructuremanager = worldserver.p();

            definedstructuremanager.d(this.structureName);
        }
    }

    public boolean B() {
        if (this.mode == BlockPropertyStructureMode.LOAD && !this.level.isClientSide && this.structureName != null) {
            WorldServer worldserver = (WorldServer) this.level;
            DefinedStructureManager definedstructuremanager = worldserver.p();

            try {
                return definedstructuremanager.b(this.structureName).isPresent();
            } catch (ResourceKeyInvalidException resourcekeyinvalidexception) {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean C() {
        return this.powered;
    }

    public void c(boolean flag) {
        this.powered = flag;
    }

    public boolean D() {
        return this.showAir;
    }

    public void d(boolean flag) {
        this.showAir = flag;
    }

    public boolean E() {
        return this.showBoundingBox;
    }

    public void e(boolean flag) {
        this.showBoundingBox = flag;
    }

    public static enum UpdateType {

        UPDATE_DATA, SAVE_AREA, LOAD_AREA, SCAN_AREA;

        private UpdateType() {}
    }
}
