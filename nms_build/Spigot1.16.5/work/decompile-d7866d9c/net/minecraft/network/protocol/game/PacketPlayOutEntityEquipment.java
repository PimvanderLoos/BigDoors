package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.item.ItemStack;

public class PacketPlayOutEntityEquipment implements Packet<PacketListenerPlayOut> {

    private int a;
    private final List<Pair<EnumItemSlot, ItemStack>> b;

    public PacketPlayOutEntityEquipment() {
        this.b = Lists.newArrayList();
    }

    public PacketPlayOutEntityEquipment(int i, List<Pair<EnumItemSlot, ItemStack>> list) {
        this.a = i;
        this.b = list;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.i();
        EnumItemSlot[] aenumitemslot = EnumItemSlot.values();

        byte b0;

        do {
            b0 = packetdataserializer.readByte();
            EnumItemSlot enumitemslot = aenumitemslot[b0 & 127];
            ItemStack itemstack = packetdataserializer.n();

            this.b.add(Pair.of(enumitemslot, itemstack));
        } while ((b0 & -128) != 0);

    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.d(this.a);
        int i = this.b.size();

        for (int j = 0; j < i; ++j) {
            Pair<EnumItemSlot, ItemStack> pair = (Pair) this.b.get(j);
            EnumItemSlot enumitemslot = (EnumItemSlot) pair.getFirst();
            boolean flag = j != i - 1;
            int k = enumitemslot.ordinal();

            packetdataserializer.writeByte(flag ? k | -128 : k);
            packetdataserializer.a((ItemStack) pair.getSecond());
        }

    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
