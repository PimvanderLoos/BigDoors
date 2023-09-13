package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentUtils;
import net.minecraft.network.PacketDataSerializer;

public class IntegerArgumentInfo implements ArgumentTypeInfo<IntegerArgumentType, IntegerArgumentInfo.a> {

    public IntegerArgumentInfo() {}

    public void serializeToNetwork(IntegerArgumentInfo.a integerargumentinfo_a, PacketDataSerializer packetdataserializer) {
        boolean flag = integerargumentinfo_a.min != Integer.MIN_VALUE;
        boolean flag1 = integerargumentinfo_a.max != Integer.MAX_VALUE;

        packetdataserializer.writeByte(ArgumentUtils.createNumberFlags(flag, flag1));
        if (flag) {
            packetdataserializer.writeInt(integerargumentinfo_a.min);
        }

        if (flag1) {
            packetdataserializer.writeInt(integerargumentinfo_a.max);
        }

    }

    @Override
    public IntegerArgumentInfo.a deserializeFromNetwork(PacketDataSerializer packetdataserializer) {
        byte b0 = packetdataserializer.readByte();
        int i = ArgumentUtils.numberHasMin(b0) ? packetdataserializer.readInt() : Integer.MIN_VALUE;
        int j = ArgumentUtils.numberHasMax(b0) ? packetdataserializer.readInt() : Integer.MAX_VALUE;

        return new IntegerArgumentInfo.a(i, j);
    }

    public void serializeToJson(IntegerArgumentInfo.a integerargumentinfo_a, JsonObject jsonobject) {
        if (integerargumentinfo_a.min != Integer.MIN_VALUE) {
            jsonobject.addProperty("min", integerargumentinfo_a.min);
        }

        if (integerargumentinfo_a.max != Integer.MAX_VALUE) {
            jsonobject.addProperty("max", integerargumentinfo_a.max);
        }

    }

    public IntegerArgumentInfo.a unpack(IntegerArgumentType integerargumenttype) {
        return new IntegerArgumentInfo.a(integerargumenttype.getMinimum(), integerargumenttype.getMaximum());
    }

    public final class a implements ArgumentTypeInfo.a<IntegerArgumentType> {

        final int min;
        final int max;

        a(int i, int j) {
            this.min = i;
            this.max = j;
        }

        @Override
        public IntegerArgumentType instantiate(CommandBuildContext commandbuildcontext) {
            return IntegerArgumentType.integer(this.min, this.max);
        }

        @Override
        public ArgumentTypeInfo<IntegerArgumentType, ?> type() {
            return IntegerArgumentInfo.this;
        }
    }
}
