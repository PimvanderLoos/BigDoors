package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentUtils;
import net.minecraft.network.PacketDataSerializer;

public class DoubleArgumentInfo implements ArgumentTypeInfo<DoubleArgumentType, DoubleArgumentInfo.a> {

    public DoubleArgumentInfo() {}

    public void serializeToNetwork(DoubleArgumentInfo.a doubleargumentinfo_a, PacketDataSerializer packetdataserializer) {
        boolean flag = doubleargumentinfo_a.min != -1.7976931348623157E308D;
        boolean flag1 = doubleargumentinfo_a.max != Double.MAX_VALUE;

        packetdataserializer.writeByte(ArgumentUtils.createNumberFlags(flag, flag1));
        if (flag) {
            packetdataserializer.writeDouble(doubleargumentinfo_a.min);
        }

        if (flag1) {
            packetdataserializer.writeDouble(doubleargumentinfo_a.max);
        }

    }

    @Override
    public DoubleArgumentInfo.a deserializeFromNetwork(PacketDataSerializer packetdataserializer) {
        byte b0 = packetdataserializer.readByte();
        double d0 = ArgumentUtils.numberHasMin(b0) ? packetdataserializer.readDouble() : -1.7976931348623157E308D;
        double d1 = ArgumentUtils.numberHasMax(b0) ? packetdataserializer.readDouble() : Double.MAX_VALUE;

        return new DoubleArgumentInfo.a(d0, d1);
    }

    public void serializeToJson(DoubleArgumentInfo.a doubleargumentinfo_a, JsonObject jsonobject) {
        if (doubleargumentinfo_a.min != -1.7976931348623157E308D) {
            jsonobject.addProperty("min", doubleargumentinfo_a.min);
        }

        if (doubleargumentinfo_a.max != Double.MAX_VALUE) {
            jsonobject.addProperty("max", doubleargumentinfo_a.max);
        }

    }

    public DoubleArgumentInfo.a unpack(DoubleArgumentType doubleargumenttype) {
        return new DoubleArgumentInfo.a(doubleargumenttype.getMinimum(), doubleargumenttype.getMaximum());
    }

    public final class a implements ArgumentTypeInfo.a<DoubleArgumentType> {

        final double min;
        final double max;

        a(double d0, double d1) {
            this.min = d0;
            this.max = d1;
        }

        @Override
        public DoubleArgumentType instantiate(CommandBuildContext commandbuildcontext) {
            return DoubleArgumentType.doubleArg(this.min, this.max);
        }

        @Override
        public ArgumentTypeInfo<DoubleArgumentType, ?> type() {
            return DoubleArgumentInfo.this;
        }
    }
}
