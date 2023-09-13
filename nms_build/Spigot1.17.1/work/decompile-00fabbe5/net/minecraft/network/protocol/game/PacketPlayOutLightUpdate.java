package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.SectionPosition;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.chunk.NibbleArray;
import net.minecraft.world.level.lighting.LightEngine;

public class PacketPlayOutLightUpdate implements Packet<PacketListenerPlayOut> {

    private final int x;
    private final int z;
    private final BitSet skyYMask;
    private final BitSet blockYMask;
    private final BitSet emptySkyYMask;
    private final BitSet emptyBlockYMask;
    private final List<byte[]> skyUpdates;
    private final List<byte[]> blockUpdates;
    private final boolean trustEdges;

    public PacketPlayOutLightUpdate(ChunkCoordIntPair chunkcoordintpair, LightEngine lightengine, @Nullable BitSet bitset, @Nullable BitSet bitset1, boolean flag) {
        this.x = chunkcoordintpair.x;
        this.z = chunkcoordintpair.z;
        this.trustEdges = flag;
        this.skyYMask = new BitSet();
        this.blockYMask = new BitSet();
        this.emptySkyYMask = new BitSet();
        this.emptyBlockYMask = new BitSet();
        this.skyUpdates = Lists.newArrayList();
        this.blockUpdates = Lists.newArrayList();

        for (int i = 0; i < lightengine.b(); ++i) {
            if (bitset == null || bitset.get(i)) {
                a(chunkcoordintpair, lightengine, EnumSkyBlock.SKY, i, this.skyYMask, this.emptySkyYMask, this.skyUpdates);
            }

            if (bitset1 == null || bitset1.get(i)) {
                a(chunkcoordintpair, lightengine, EnumSkyBlock.BLOCK, i, this.blockYMask, this.emptyBlockYMask, this.blockUpdates);
            }
        }

    }

    private static void a(ChunkCoordIntPair chunkcoordintpair, LightEngine lightengine, EnumSkyBlock enumskyblock, int i, BitSet bitset, BitSet bitset1, List<byte[]> list) {
        NibbleArray nibblearray = lightengine.a(enumskyblock).a(SectionPosition.a(chunkcoordintpair, lightengine.c() + i));

        if (nibblearray != null) {
            if (nibblearray.c()) {
                bitset1.set(i);
            } else {
                bitset.set(i);
                list.add((byte[]) nibblearray.asBytes().clone());
            }
        }

    }

    public PacketPlayOutLightUpdate(PacketDataSerializer packetdataserializer) {
        this.x = packetdataserializer.j();
        this.z = packetdataserializer.j();
        this.trustEdges = packetdataserializer.readBoolean();
        this.skyYMask = packetdataserializer.t();
        this.blockYMask = packetdataserializer.t();
        this.emptySkyYMask = packetdataserializer.t();
        this.emptyBlockYMask = packetdataserializer.t();
        this.skyUpdates = packetdataserializer.a((packetdataserializer1) -> {
            return packetdataserializer1.b(2048);
        });
        this.blockUpdates = packetdataserializer.a((packetdataserializer1) -> {
            return packetdataserializer1.b(2048);
        });
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.x);
        packetdataserializer.d(this.z);
        packetdataserializer.writeBoolean(this.trustEdges);
        packetdataserializer.a(this.skyYMask);
        packetdataserializer.a(this.blockYMask);
        packetdataserializer.a(this.emptySkyYMask);
        packetdataserializer.a(this.emptyBlockYMask);
        packetdataserializer.a((Collection) this.skyUpdates, PacketDataSerializer::a);
        packetdataserializer.a((Collection) this.blockUpdates, PacketDataSerializer::a);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public int b() {
        return this.x;
    }

    public int c() {
        return this.z;
    }

    public BitSet d() {
        return this.skyYMask;
    }

    public BitSet e() {
        return this.emptySkyYMask;
    }

    public List<byte[]> f() {
        return this.skyUpdates;
    }

    public BitSet g() {
        return this.blockYMask;
    }

    public BitSet h() {
        return this.emptyBlockYMask;
    }

    public List<byte[]> i() {
        return this.blockUpdates;
    }

    public boolean j() {
        return this.trustEdges;
    }
}
