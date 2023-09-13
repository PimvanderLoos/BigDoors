package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentUtils;
import net.minecraft.network.PacketDataSerializer;

public class LongArgumentInfo implements ArgumentTypeInfo<LongArgumentType, LongArgumentInfo.a> {

    public LongArgumentInfo() {}

    public void serializeToNetwork(LongArgumentInfo.a longargumentinfo_a, PacketDataSerializer packetdataserializer) {
        boolean flag = longargumentinfo_a.min != Long.MIN_VALUE;
        boolean flag1 = longargumentinfo_a.max != Long.MAX_VALUE;

        packetdataserializer.writeByte(ArgumentUtils.createNumberFlags(flag, flag1));
        if (flag) {
            packetdataserializer.writeLong(longargumentinfo_a.min);
        }

        if (flag1) {
            packetdataserializer.writeLong(longargumentinfo_a.max);
        }

    }

    @Override
    public LongArgumentInfo.a deserializeFromNetwork(PacketDataSerializer packetdataserializer) {
        byte b0 = packetdataserializer.readByte();
        long i = ArgumentUtils.numberHasMin(b0) ? packetdataserializer.readLong() : Long.MIN_VALUE;
        long j = ArgumentUtils.numberHasMax(b0) ? packetdataserializer.readLong() : Long.MAX_VALUE;

        return new LongArgumentInfo.a(i, j);
    }

    public void serializeToJson(LongArgumentInfo.a longargumentinfo_a, JsonObject jsonobject) {
        if (longargumentinfo_a.min != Long.MIN_VALUE) {
            jsonobject.addProperty("min", longargumentinfo_a.min);
        }

        if (longargumentinfo_a.max != Long.MAX_VALUE) {
            jsonobject.addProperty("max", longargumentinfo_a.max);
        }

    }

    public LongArgumentInfo.a unpack(LongArgumentType longargumenttype) {
        return new LongArgumentInfo.a(longargumenttype.getMinimum(), longargumenttype.getMaximum());
    }

    public final class a implements ArgumentTypeInfo.a<LongArgumentType> {

        final long min;
        final long max;

        a(long i, long j) {
            this.min = i;
            this.max = j;
        }

        @Override
        public LongArgumentType instantiate(CommandBuildContext commandbuildcontext) {
            return LongArgumentType.longArg(this.min, this.max);
        }

        @Override
        public ArgumentTypeInfo<LongArgumentType, ?> type() {
            return LongArgumentInfo.this;
        }
    }
}
