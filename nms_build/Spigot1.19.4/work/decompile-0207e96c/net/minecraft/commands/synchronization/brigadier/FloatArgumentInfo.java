package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentUtils;
import net.minecraft.network.PacketDataSerializer;

public class FloatArgumentInfo implements ArgumentTypeInfo<FloatArgumentType, FloatArgumentInfo.a> {

    public FloatArgumentInfo() {}

    public void serializeToNetwork(FloatArgumentInfo.a floatargumentinfo_a, PacketDataSerializer packetdataserializer) {
        boolean flag = floatargumentinfo_a.min != -3.4028235E38F;
        boolean flag1 = floatargumentinfo_a.max != Float.MAX_VALUE;

        packetdataserializer.writeByte(ArgumentUtils.createNumberFlags(flag, flag1));
        if (flag) {
            packetdataserializer.writeFloat(floatargumentinfo_a.min);
        }

        if (flag1) {
            packetdataserializer.writeFloat(floatargumentinfo_a.max);
        }

    }

    @Override
    public FloatArgumentInfo.a deserializeFromNetwork(PacketDataSerializer packetdataserializer) {
        byte b0 = packetdataserializer.readByte();
        float f = ArgumentUtils.numberHasMin(b0) ? packetdataserializer.readFloat() : -3.4028235E38F;
        float f1 = ArgumentUtils.numberHasMax(b0) ? packetdataserializer.readFloat() : Float.MAX_VALUE;

        return new FloatArgumentInfo.a(f, f1);
    }

    public void serializeToJson(FloatArgumentInfo.a floatargumentinfo_a, JsonObject jsonobject) {
        if (floatargumentinfo_a.min != -3.4028235E38F) {
            jsonobject.addProperty("min", floatargumentinfo_a.min);
        }

        if (floatargumentinfo_a.max != Float.MAX_VALUE) {
            jsonobject.addProperty("max", floatargumentinfo_a.max);
        }

    }

    public FloatArgumentInfo.a unpack(FloatArgumentType floatargumenttype) {
        return new FloatArgumentInfo.a(floatargumenttype.getMinimum(), floatargumenttype.getMaximum());
    }

    public final class a implements ArgumentTypeInfo.a<FloatArgumentType> {

        final float min;
        final float max;

        a(float f, float f1) {
            this.min = f;
            this.max = f1;
        }

        @Override
        public FloatArgumentType instantiate(CommandBuildContext commandbuildcontext) {
            return FloatArgumentType.floatArg(this.min, this.max);
        }

        @Override
        public ArgumentTypeInfo<FloatArgumentType, ?> type() {
            return FloatArgumentInfo.this;
        }
    }
}
