package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.item.ItemStack;

public class PacketPlayOutEntityEquipment implements Packet<PacketListenerPlayOut> {

    private static final byte CONTINUE_MASK = -128;
    private final int entity;
    private final List<Pair<EnumItemSlot, ItemStack>> slots;

    public PacketPlayOutEntityEquipment(int i, List<Pair<EnumItemSlot, ItemStack>> list) {
        this.entity = i;
        this.slots = list;
    }

    public PacketPlayOutEntityEquipment(PacketDataSerializer packetdataserializer) {
        this.entity = packetdataserializer.j();
        EnumItemSlot[] aenumitemslot = EnumItemSlot.values();

        this.slots = Lists.newArrayList();

        byte b0;

        do {
            b0 = packetdataserializer.readByte();
            EnumItemSlot enumitemslot = aenumitemslot[b0 & 127];
            ItemStack itemstack = packetdataserializer.o();

            this.slots.add(Pair.of(enumitemslot, itemstack));
        } while ((b0 & -128) != 0);

    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.entity);
        int i = this.slots.size();

        for (int j = 0; j < i; ++j) {
            Pair<EnumItemSlot, ItemStack> pair = (Pair) this.slots.get(j);
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

    public int b() {
        return this.entity;
    }

    public List<Pair<EnumItemSlot, ItemStack>> c() {
        return this.slots;
    }
}
