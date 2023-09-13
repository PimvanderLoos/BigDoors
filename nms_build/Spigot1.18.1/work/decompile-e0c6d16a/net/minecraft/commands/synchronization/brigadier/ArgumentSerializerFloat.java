package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.PacketDataSerializer;

public class ArgumentSerializerFloat implements ArgumentSerializer<FloatArgumentType> {

    public ArgumentSerializerFloat() {}

    public void serializeToNetwork(FloatArgumentType floatargumenttype, PacketDataSerializer packetdataserializer) {
        boolean flag = floatargumenttype.getMinimum() != -3.4028235E38F;
        boolean flag1 = floatargumenttype.getMaximum() != Float.MAX_VALUE;

        packetdataserializer.writeByte(ArgumentSerializers.createNumberFlags(flag, flag1));
        if (flag) {
            packetdataserializer.writeFloat(floatargumenttype.getMinimum());
        }

        if (flag1) {
            packetdataserializer.writeFloat(floatargumenttype.getMaximum());
        }

    }

    @Override
    public FloatArgumentType deserializeFromNetwork(PacketDataSerializer packetdataserializer) {
        byte b0 = packetdataserializer.readByte();
        float f = ArgumentSerializers.numberHasMin(b0) ? packetdataserializer.readFloat() : -3.4028235E38F;
        float f1 = ArgumentSerializers.numberHasMax(b0) ? packetdataserializer.readFloat() : Float.MAX_VALUE;

        return FloatArgumentType.floatArg(f, f1);
    }

    public void serializeToJson(FloatArgumentType floatargumenttype, JsonObject jsonobject) {
        if (floatargumenttype.getMinimum() != -3.4028235E38F) {
            jsonobject.addProperty("min", floatargumenttype.getMinimum());
        }

        if (floatargumenttype.getMaximum() != Float.MAX_VALUE) {
            jsonobject.addProperty("max", floatargumenttype.getMaximum());
        }

    }
}
