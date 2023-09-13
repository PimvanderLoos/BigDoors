package net.minecraft.server;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class TileEntityStructure extends TileEntity {

    private String a = "";
    private String f = "";
    private String g = "";
    private BlockPosition h = new BlockPosition(0, 1, 0);
    private BlockPosition i;
    private EnumBlockMirror j;
    private EnumBlockRotation k;
    private TileEntityStructure.UsageMode l;
    private boolean m;
    private boolean n;
    private boolean o;
    private boolean p;
    private float q;
    private long r;

    public TileEntityStructure() {
        this.i = BlockPosition.ZERO;
        this.j = EnumBlockMirror.NONE;
        this.k = EnumBlockRotation.NONE;
        this.l = TileEntityStructure.UsageMode.DATA;
        this.m = true;
        this.p = true;
        this.q = 1.0F;
    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        nbttagcompound.setString("name", this.a);
        nbttagcompound.setString("author", this.f);
        nbttagcompound.setString("metadata", this.g);
        nbttagcompound.setInt("posX", this.h.getX());
        nbttagcompound.setInt("posY", this.h.getY());
        nbttagcompound.setInt("posZ", this.h.getZ());
        nbttagcompound.setInt("sizeX", this.i.getX());
        nbttagcompound.setInt("sizeY", this.i.getY());
        nbttagcompound.setInt("sizeZ", this.i.getZ());
        nbttagcompound.setString("rotation", this.k.toString());
        nbttagcompound.setString("mirror", this.j.toString());
        nbttagcompound.setString("mode", this.l.toString());
        nbttagcompound.setBoolean("ignoreEntities", this.m);
        nbttagcompound.setBoolean("powered", this.n);
        nbttagcompound.setBoolean("showair", this.o);
        nbttagcompound.setBoolean("showboundingbox", this.p);
        nbttagcompound.setFloat("integrity", this.q);
        nbttagcompound.setLong("seed", this.r);
        return nbttagcompound;
    }

    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.a(nbttagcompound.getString("name"));
        this.f = nbttagcompound.getString("author");
        this.g = nbttagcompound.getString("metadata");
        int i = MathHelper.clamp(nbttagcompound.getInt("posX"), -32, 32);
        int j = MathHelper.clamp(nbttagcompound.getInt("posY"), -32, 32);
        int k = MathHelper.clamp(nbttagcompound.getInt("posZ"), -32, 32);

        this.h = new BlockPosition(i, j, k);
        int l = MathHelper.clamp(nbttagcompound.getInt("sizeX"), 0, 32);
        int i1 = MathHelper.clamp(nbttagcompound.getInt("sizeY"), 0, 32);
        int j1 = MathHelper.clamp(nbttagcompound.getInt("sizeZ"), 0, 32);

        this.i = new BlockPosition(l, i1, j1);

        try {
            this.k = EnumBlockRotation.valueOf(nbttagcompound.getString("rotation"));
        } catch (IllegalArgumentException illegalargumentexception) {
            this.k = EnumBlockRotation.NONE;
        }

        try {
            this.j = EnumBlockMirror.valueOf(nbttagcompound.getString("mirror"));
        } catch (IllegalArgumentException illegalargumentexception1) {
            this.j = EnumBlockMirror.NONE;
        }

        try {
            this.l = TileEntityStructure.UsageMode.valueOf(nbttagcompound.getString("mode"));
        } catch (IllegalArgumentException illegalargumentexception2) {
            this.l = TileEntityStructure.UsageMode.DATA;
        }

        this.m = nbttagcompound.getBoolean("ignoreEntities");
        this.n = nbttagcompound.getBoolean("powered");
        this.o = nbttagcompound.getBoolean("showair");
        this.p = nbttagcompound.getBoolean("showboundingbox");
        if (nbttagcompound.hasKey("integrity")) {
            this.q = nbttagcompound.getFloat("integrity");
        } else {
            this.q = 1.0F;
        }

        this.r = nbttagcompound.getLong("seed");
        this.J();
    }

    private void J() {
        if (this.world != null) {
            BlockPosition blockposition = this.getPosition();
            IBlockData iblockdata = this.world.getType(blockposition);

            if (iblockdata.getBlock() == Blocks.STRUCTURE_BLOCK) {
                this.world.setTypeAndData(blockposition, iblockdata.set(BlockStructure.a, this.l), 2);
            }

        }
    }

    @Nullable
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return new PacketPlayOutTileEntityData(this.position, 7, this.d());
    }

    public NBTTagCompound d() {
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

    public String a() {
        return this.a;
    }

    public void a(String s) {
        String s1 = s;
        char[] achar = SharedConstants.b;
        int i = achar.length;

        for (int j = 0; j < i; ++j) {
            char c0 = achar[j];

            s1 = s1.replace(c0, '_');
        }

        this.a = s1;
    }

    public void a(EntityLiving entityliving) {
        if (!UtilColor.b(entityliving.getName())) {
            this.f = entityliving.getName();
        }

    }

    public void b(BlockPosition blockposition) {
        this.h = blockposition;
    }

    public void c(BlockPosition blockposition) {
        this.i = blockposition;
    }

    public void b(EnumBlockMirror enumblockmirror) {
        this.j = enumblockmirror;
    }

    public void b(EnumBlockRotation enumblockrotation) {
        this.k = enumblockrotation;
    }

    public void b(String s) {
        this.g = s;
    }

    public TileEntityStructure.UsageMode k() {
        return this.l;
    }

    public void a(TileEntityStructure.UsageMode tileentitystructure_usagemode) {
        this.l = tileentitystructure_usagemode;
        IBlockData iblockdata = this.world.getType(this.getPosition());

        if (iblockdata.getBlock() == Blocks.STRUCTURE_BLOCK) {
            this.world.setTypeAndData(this.getPosition(), iblockdata.set(BlockStructure.a, tileentitystructure_usagemode), 2);
        }

    }

    public void a(boolean flag) {
        this.m = flag;
    }

    public void a(float f) {
        this.q = f;
    }

    public void a(long i) {
        this.r = i;
    }

    public boolean p() {
        if (this.l != TileEntityStructure.UsageMode.SAVE) {
            return false;
        } else {
            BlockPosition blockposition = this.getPosition();
            boolean flag = true;
            BlockPosition blockposition1 = new BlockPosition(blockposition.getX() - 80, 0, blockposition.getZ() - 80);
            BlockPosition blockposition2 = new BlockPosition(blockposition.getX() + 80, 255, blockposition.getZ() + 80);
            List list = this.a(blockposition1, blockposition2);
            List list1 = this.a(list);

            if (list1.size() < 1) {
                return false;
            } else {
                StructureBoundingBox structureboundingbox = this.a(blockposition, list1);

                if (structureboundingbox.d - structureboundingbox.a > 1 && structureboundingbox.e - structureboundingbox.b > 1 && structureboundingbox.f - structureboundingbox.c > 1) {
                    this.h = new BlockPosition(structureboundingbox.a - blockposition.getX() + 1, structureboundingbox.b - blockposition.getY() + 1, structureboundingbox.c - blockposition.getZ() + 1);
                    this.i = new BlockPosition(structureboundingbox.d - structureboundingbox.a - 1, structureboundingbox.e - structureboundingbox.b - 1, structureboundingbox.f - structureboundingbox.c - 1);
                    this.update();
                    IBlockData iblockdata = this.world.getType(blockposition);

                    this.world.notify(blockposition, iblockdata, iblockdata, 3);
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    private List<TileEntityStructure> a(List<TileEntityStructure> list) {
        Iterable iterable = Iterables.filter(list, new Predicate() {
            public boolean a(@Nullable TileEntityStructure tileentitystructure) {
                return tileentitystructure.l == TileEntityStructure.UsageMode.CORNER && TileEntityStructure.this.a.equals(tileentitystructure.a);
            }

            public boolean apply(@Nullable Object object) {
                return this.a((TileEntityStructure) object);
            }
        });

        return Lists.newArrayList(iterable);
    }

    private List<TileEntityStructure> a(BlockPosition blockposition, BlockPosition blockposition1) {
        ArrayList arraylist = Lists.newArrayList();
        Iterator iterator = BlockPosition.b(blockposition, blockposition1).iterator();

        while (iterator.hasNext()) {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = (BlockPosition.MutableBlockPosition) iterator.next();
            IBlockData iblockdata = this.world.getType(blockposition_mutableblockposition);

            if (iblockdata.getBlock() == Blocks.STRUCTURE_BLOCK) {
                TileEntity tileentity = this.world.getTileEntity(blockposition_mutableblockposition);

                if (tileentity != null && tileentity instanceof TileEntityStructure) {
                    arraylist.add((TileEntityStructure) tileentity);
                }
            }
        }

        return arraylist;
    }

    private StructureBoundingBox a(BlockPosition blockposition, List<TileEntityStructure> list) {
        StructureBoundingBox structureboundingbox;

        if (list.size() > 1) {
            BlockPosition blockposition1 = ((TileEntityStructure) list.get(0)).getPosition();

            structureboundingbox = new StructureBoundingBox(blockposition1, blockposition1);
        } else {
            structureboundingbox = new StructureBoundingBox(blockposition, blockposition);
        }

        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            TileEntityStructure tileentitystructure = (TileEntityStructure) iterator.next();
            BlockPosition blockposition2 = tileentitystructure.getPosition();

            if (blockposition2.getX() < structureboundingbox.a) {
                structureboundingbox.a = blockposition2.getX();
            } else if (blockposition2.getX() > structureboundingbox.d) {
                structureboundingbox.d = blockposition2.getX();
            }

            if (blockposition2.getY() < structureboundingbox.b) {
                structureboundingbox.b = blockposition2.getY();
            } else if (blockposition2.getY() > structureboundingbox.e) {
                structureboundingbox.e = blockposition2.getY();
            }

            if (blockposition2.getZ() < structureboundingbox.c) {
                structureboundingbox.c = blockposition2.getZ();
            } else if (blockposition2.getZ() > structureboundingbox.f) {
                structureboundingbox.f = blockposition2.getZ();
            }
        }

        return structureboundingbox;
    }

    public boolean q() {
        return this.b(true);
    }

    public boolean b(boolean flag) {
        if (this.l == TileEntityStructure.UsageMode.SAVE && !this.world.isClientSide && !UtilColor.b(this.a)) {
            BlockPosition blockposition = this.getPosition().a((BaseBlockPosition) this.h);
            WorldServer worldserver = (WorldServer) this.world;
            MinecraftServer minecraftserver = this.world.getMinecraftServer();
            DefinedStructureManager definedstructuremanager = worldserver.y();
            DefinedStructure definedstructure = definedstructuremanager.a(minecraftserver, new MinecraftKey(this.a));

            definedstructure.a(this.world, blockposition, this.i, !this.m, Blocks.dj);
            definedstructure.a(this.f);
            return !flag || definedstructuremanager.c(minecraftserver, new MinecraftKey(this.a));
        } else {
            return false;
        }
    }

    public boolean r() {
        return this.c(true);
    }

    public boolean c(boolean flag) {
        if (this.l == TileEntityStructure.UsageMode.LOAD && !this.world.isClientSide && !UtilColor.b(this.a)) {
            BlockPosition blockposition = this.getPosition();
            BlockPosition blockposition1 = blockposition.a((BaseBlockPosition) this.h);
            WorldServer worldserver = (WorldServer) this.world;
            MinecraftServer minecraftserver = this.world.getMinecraftServer();
            DefinedStructureManager definedstructuremanager = worldserver.y();
            DefinedStructure definedstructure = definedstructuremanager.b(minecraftserver, new MinecraftKey(this.a));

            if (definedstructure == null) {
                return false;
            } else {
                if (!UtilColor.b(definedstructure.b())) {
                    this.f = definedstructure.b();
                }

                BlockPosition blockposition2 = definedstructure.a();
                boolean flag1 = this.i.equals(blockposition2);

                if (!flag1) {
                    this.i = blockposition2;
                    this.update();
                    IBlockData iblockdata = this.world.getType(blockposition);

                    this.world.notify(blockposition, iblockdata, iblockdata, 3);
                }

                if (flag && !flag1) {
                    return false;
                } else {
                    DefinedStructureInfo definedstructureinfo = (new DefinedStructureInfo()).a(this.j).a(this.k).a(this.m).a((ChunkCoordIntPair) null).a((Block) null).b(false);

                    if (this.q < 1.0F) {
                        definedstructureinfo.a(MathHelper.a(this.q, 0.0F, 1.0F)).a(Long.valueOf(this.r));
                    }

                    definedstructure.a(this.world, blockposition1, definedstructureinfo);
                    return true;
                }
            }
        } else {
            return false;
        }
    }

    public void s() {
        WorldServer worldserver = (WorldServer) this.world;
        DefinedStructureManager definedstructuremanager = worldserver.y();

        definedstructuremanager.b(new MinecraftKey(this.a));
    }

    public boolean E() {
        if (this.l == TileEntityStructure.UsageMode.LOAD && !this.world.isClientSide) {
            WorldServer worldserver = (WorldServer) this.world;
            MinecraftServer minecraftserver = this.world.getMinecraftServer();
            DefinedStructureManager definedstructuremanager = worldserver.y();

            return definedstructuremanager.b(minecraftserver, new MinecraftKey(this.a)) != null;
        } else {
            return false;
        }
    }

    public boolean F() {
        return this.n;
    }

    public void d(boolean flag) {
        this.n = flag;
    }

    public void e(boolean flag) {
        this.o = flag;
    }

    public void f(boolean flag) {
        this.p = flag;
    }

    @Nullable
    public IChatBaseComponent i_() {
        return new ChatMessage("structure_block.hover." + this.l.f, new Object[] { this.l == TileEntityStructure.UsageMode.DATA ? this.g : this.a});
    }

    public static enum UsageMode implements INamable {

        SAVE("save", 0), LOAD("load", 1), CORNER("corner", 2), DATA("data", 3);

        private static final TileEntityStructure.UsageMode[] e = new TileEntityStructure.UsageMode[values().length];
        private final String f;
        private final int g;

        private UsageMode(String s, int i) {
            this.f = s;
            this.g = i;
        }

        public String getName() {
            return this.f;
        }

        public int a() {
            return this.g;
        }

        public static TileEntityStructure.UsageMode a(int i) {
            return i >= 0 && i < TileEntityStructure.UsageMode.e.length ? TileEntityStructure.UsageMode.e[i] : TileEntityStructure.UsageMode.e[0];
        }

        static {
            TileEntityStructure.UsageMode[] atileentitystructure_usagemode = values();
            int i = atileentitystructure_usagemode.length;

            for (int j = 0; j < i; ++j) {
                TileEntityStructure.UsageMode tileentitystructure_usagemode = atileentitystructure_usagemode[j];

                TileEntityStructure.UsageMode.e[tileentitystructure_usagemode.a()] = tileentitystructure_usagemode;
            }

        }
    }
}
