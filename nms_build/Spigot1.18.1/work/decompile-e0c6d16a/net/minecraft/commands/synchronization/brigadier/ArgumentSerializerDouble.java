package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.PacketDataSerializer;

public class ArgumentSerializerDouble implements ArgumentSerializer<DoubleArgumentType> {

    public ArgumentSerializerDouble() {}

    public void serializeToNetwork(DoubleArgumentType doubleargumenttype, PacketDataSerializer packetdataserializer) {
        boolean flag = doubleargumenttype.getMinimum() != -1.7976931348623157E308D;
        boolean flag1 = doubleargumenttype.getMaximum() != Double.MAX_VALUE;

        packetdataserializer.writeByte(ArgumentSerializers.createNumberFlags(flag, flag1));
        if (flag) {
            packetdataserializer.writeDouble(doubleargumenttype.getMinimum());
        }

        if (flag1) {
            packetdataserializer.writeDouble(doubleargumenttype.getMaximum());
        }

    }

    @Override
    public DoubleArgumentType deserializeFromNetwork(PacketDataSerializer packetdataserializer) {
        byte b0 = packetdataserializer.readByte();
        double d0 = ArgumentSerializers.numberHasMin(b0) ? packetdataserializer.readDouble() : -1.7976931348623157E308D;
        double d1 = ArgumentSerializers.numberHasMax(b0) ? packetdataserializer.readDouble() : Double.MAX_VALUE;

        return DoubleArgumentType.doubleArg(d0, d1);
    }

    public void serializeToJson(DoubleArgumentType doubleargumenttype, JsonObject jsonobject) {
        if (doubleargumenttype.getMinimum() != -1.7976931348623157E308D) {
            jsonobject.addProperty("min", doubleargumenttype.getMinimum());
        }

        if (doubleargumenttype.getMaximum() != Double.MAX_VALUE) {
            jsonobject.addProperty("max", doubleargumenttype.getMaximum());
        }

    }
}
