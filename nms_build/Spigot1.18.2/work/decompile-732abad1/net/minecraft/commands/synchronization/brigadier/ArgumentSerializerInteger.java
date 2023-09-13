package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.PacketDataSerializer;

public class ArgumentSerializerInteger implements ArgumentSerializer<IntegerArgumentType> {

    public ArgumentSerializerInteger() {}

    public void serializeToNetwork(IntegerArgumentType integerargumenttype, PacketDataSerializer packetdataserializer) {
        boolean flag = integerargumenttype.getMinimum() != Integer.MIN_VALUE;
        boolean flag1 = integerargumenttype.getMaximum() != Integer.MAX_VALUE;

        packetdataserializer.writeByte(ArgumentSerializers.createNumberFlags(flag, flag1));
        if (flag) {
            packetdataserializer.writeInt(integerargumenttype.getMinimum());
        }

        if (flag1) {
            packetdataserializer.writeInt(integerargumenttype.getMaximum());
        }

    }

    @Override
    public IntegerArgumentType deserializeFromNetwork(PacketDataSerializer packetdataserializer) {
        byte b0 = packetdataserializer.readByte();
        int i = ArgumentSerializers.numberHasMin(b0) ? packetdataserializer.readInt() : Integer.MIN_VALUE;
        int j = ArgumentSerializers.numberHasMax(b0) ? packetdataserializer.readInt() : Integer.MAX_VALUE;

        return IntegerArgumentType.integer(i, j);
    }

    public void serializeToJson(IntegerArgumentType integerargumenttype, JsonObject jsonobject) {
        if (integerargumenttype.getMinimum() != Integer.MIN_VALUE) {
            jsonobject.addProperty("min", integerargumenttype.getMinimum());
        }

        if (integerargumenttype.getMaximum() != Integer.MAX_VALUE) {
            jsonobject.addProperty("max", integerargumenttype.getMaximum());
        }

    }
}
