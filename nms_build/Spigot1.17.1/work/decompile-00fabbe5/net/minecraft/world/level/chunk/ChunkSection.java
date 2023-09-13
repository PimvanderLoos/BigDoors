package net.minecraft.world.level.chunk;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.Fluid;

public class ChunkSection {

    public static final int SECTION_WIDTH = 16;
    public static final int SECTION_HEIGHT = 16;
    public static final int SECTION_SIZE = 4096;
    public static final DataPalette<IBlockData> GLOBAL_BLOCKSTATE_PALETTE = new DataPaletteGlobal<>(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.getBlockData());
    private final int bottomBlockY;
    private short nonEmptyBlockCount;
    private short tickingBlockCount;
    private short tickingFluidCount;
    private final DataPaletteBlock<IBlockData> states;

    public ChunkSection(int i) {
        this(i, (short) 0, (short) 0, (short) 0);
    }

    public ChunkSection(int i, short short0, short short1, short short2) {
        this.bottomBlockY = a(i);
        this.nonEmptyBlockCount = short0;
        this.tickingBlockCount = short1;
        this.tickingFluidCount = short2;
        this.states = new DataPaletteBlock<>(ChunkSection.GLOBAL_BLOCKSTATE_PALETTE, Block.BLOCK_STATE_REGISTRY, GameProfileSerializer::c, GameProfileSerializer::a, Blocks.AIR.getBlockData());
    }

    public static int a(int i) {
        return i << 4;
    }

    public IBlockData getType(int i, int j, int k) {
        return (IBlockData) this.states.a(i, j, k);
    }

    public Fluid b(int i, int j, int k) {
        return ((IBlockData) this.states.a(i, j, k)).getFluid();
    }

    public void a() {
        this.states.a();
    }

    public void b() {
        this.states.b();
    }

    public IBlockData setType(int i, int j, int k, IBlockData iblockdata) {
        return this.setType(i, j, k, iblockdata, true);
    }

    public IBlockData setType(int i, int j, int k, IBlockData iblockdata, boolean flag) {
        IBlockData iblockdata1;

        if (flag) {
            iblockdata1 = (IBlockData) this.states.setBlock(i, j, k, iblockdata);
        } else {
            iblockdata1 = (IBlockData) this.states.b(i, j, k, iblockdata);
        }

        Fluid fluid = iblockdata1.getFluid();
        Fluid fluid1 = iblockdata.getFluid();

        if (!iblockdata1.isAir()) {
            --this.nonEmptyBlockCount;
            if (iblockdata1.isTicking()) {
                --this.tickingBlockCount;
            }
        }

        if (!fluid.isEmpty()) {
            --this.tickingFluidCount;
        }

        if (!iblockdata.isAir()) {
            ++this.nonEmptyBlockCount;
            if (iblockdata.isTicking()) {
                ++this.tickingBlockCount;
            }
        }

        if (!fluid1.isEmpty()) {
            ++this.tickingFluidCount;
        }

        return iblockdata1;
    }

    public boolean c() {
        return this.nonEmptyBlockCount == 0;
    }

    public static boolean a(@Nullable ChunkSection chunksection) {
        return chunksection == Chunk.EMPTY_SECTION || chunksection.c();
    }

    public boolean d() {
        return this.shouldTick() || this.f();
    }

    public boolean shouldTick() {
        return this.tickingBlockCount > 0;
    }

    public boolean f() {
        return this.tickingFluidCount > 0;
    }

    public int getYPosition() {
        return this.bottomBlockY;
    }

    public void recalcBlockCounts() {
        this.nonEmptyBlockCount = 0;
        this.tickingBlockCount = 0;
        this.tickingFluidCount = 0;
        this.states.a((iblockdata, i) -> {
            Fluid fluid = iblockdata.getFluid();

            if (!iblockdata.isAir()) {
                this.nonEmptyBlockCount = (short) (this.nonEmptyBlockCount + i);
                if (iblockdata.isTicking()) {
                    this.tickingBlockCount = (short) (this.tickingBlockCount + i);
                }
            }

            if (!fluid.isEmpty()) {
                this.nonEmptyBlockCount = (short) (this.nonEmptyBlockCount + i);
                if (fluid.f()) {
                    this.tickingFluidCount = (short) (this.tickingFluidCount + i);
                }
            }

        });
    }

    public DataPaletteBlock<IBlockData> getBlocks() {
        return this.states;
    }

    public void a(PacketDataSerializer packetdataserializer) {
        this.nonEmptyBlockCount = packetdataserializer.readShort();
        this.states.a(packetdataserializer);
    }

    public void b(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeShort(this.nonEmptyBlockCount);
        this.states.b(packetdataserializer);
    }

    public int j() {
        return 2 + this.states.c();
    }

    public boolean a(Predicate<IBlockData> predicate) {
        return this.states.contains(predicate);
    }
}
