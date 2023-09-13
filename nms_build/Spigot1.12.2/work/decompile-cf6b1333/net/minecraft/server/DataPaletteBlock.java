package net.minecraft.server;

import javax.annotation.Nullable;

public class DataPaletteBlock implements DataPaletteExpandable {

    private static final DataPalette d = new DataPaletteGlobal();
    protected static final IBlockData a = Blocks.AIR.getBlockData();
    protected DataBits b;
    protected DataPalette c;
    private int e;

    public DataPaletteBlock() {
        this.b(4);
    }

    private static int b(int i, int j, int k) {
        return j << 8 | k << 4 | i;
    }

    private void b(int i) {
        if (i != this.e) {
            this.e = i;
            if (this.e <= 4) {
                this.e = 4;
                this.c = new DataPaletteLinear(this.e, this);
            } else if (this.e <= 8) {
                this.c = new DataPaletteHash(this.e, this);
            } else {
                this.c = DataPaletteBlock.d;
                this.e = MathHelper.d(Block.REGISTRY_ID.a());
            }

            this.c.a(DataPaletteBlock.a);
            this.b = new DataBits(this.e, 4096);
        }
    }

    public int a(int i, IBlockData iblockdata) {
        DataBits databits = this.b;
        DataPalette datapalette = this.c;

        this.b(i);

        for (int j = 0; j < databits.b(); ++j) {
            IBlockData iblockdata1 = datapalette.a(databits.a(j));

            if (iblockdata1 != null) {
                this.setBlockIndex(j, iblockdata1);
            }
        }

        return this.c.a(iblockdata);
    }

    public void setBlock(int i, int j, int k, IBlockData iblockdata) {
        this.setBlockIndex(b(i, j, k), iblockdata);
    }

    protected void setBlockIndex(int i, IBlockData iblockdata) {
        int j = this.c.a(iblockdata);

        this.b.a(i, j);
    }

    public IBlockData a(int i, int j, int k) {
        return this.a(b(i, j, k));
    }

    protected IBlockData a(int i) {
        IBlockData iblockdata = this.c.a(this.b.a(i));

        return iblockdata == null ? DataPaletteBlock.a : iblockdata;
    }

    public void b(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeByte(this.e);
        this.c.b(packetdataserializer);
        packetdataserializer.a(this.b.a());
    }

    @Nullable
    public NibbleArray exportData(byte[] abyte, NibbleArray nibblearray) {
        NibbleArray nibblearray1 = null;

        for (int i = 0; i < 4096; ++i) {
            int j = Block.REGISTRY_ID.getId(this.a(i));
            int k = i & 15;
            int l = i >> 8 & 15;
            int i1 = i >> 4 & 15;

            if ((j >> 12 & 15) != 0) {
                if (nibblearray1 == null) {
                    nibblearray1 = new NibbleArray();
                }

                nibblearray1.a(k, l, i1, j >> 12 & 15);
            }

            abyte[i] = (byte) (j >> 4 & 255);
            nibblearray.a(k, l, i1, j & 15);
        }

        return nibblearray1;
    }

    public void a(byte[] abyte, NibbleArray nibblearray, @Nullable NibbleArray nibblearray1) {
        for (int i = 0; i < 4096; ++i) {
            int j = i & 15;
            int k = i >> 8 & 15;
            int l = i >> 4 & 15;
            int i1 = nibblearray1 == null ? 0 : nibblearray1.a(j, k, l);
            int j1 = i1 << 12 | (abyte[i] & 255) << 4 | nibblearray.a(j, k, l);

            this.setBlockIndex(i, (IBlockData) Block.REGISTRY_ID.fromId(j1));
        }

    }

    public int a() {
        return 1 + this.c.a() + PacketDataSerializer.a(this.b.b()) + this.b.a().length * 8;
    }
}
