package net.minecraft.commands.synchronization.brigadier;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.synchronization.ArgumentRegistry;
import net.minecraft.commands.synchronization.ArgumentSerializerVoid;

public class ArgumentSerializers {

    private static final byte NUMBER_FLAG_MIN = 1;
    private static final byte NUMBER_FLAG_MAX = 2;

    public ArgumentSerializers() {}

    public static void bootstrap() {
        ArgumentRegistry.register("brigadier:bool", BoolArgumentType.class, new ArgumentSerializerVoid<>(BoolArgumentType::bool));
        ArgumentRegistry.register("brigadier:float", FloatArgumentType.class, new ArgumentSerializerFloat());
        ArgumentRegistry.register("brigadier:double", DoubleArgumentType.class, new ArgumentSerializerDouble());
        ArgumentRegistry.register("brigadier:integer", IntegerArgumentType.class, new ArgumentSerializerInteger());
        ArgumentRegistry.register("brigadier:long", LongArgumentType.class, new ArgumentSerializerLong());
        ArgumentRegistry.register("brigadier:string", StringArgumentType.class, new ArgumentSerializerString());
    }

    public static byte createNumberFlags(boolean flag, boolean flag1) {
        byte b0 = 0;

        if (flag) {
            b0 = (byte) (b0 | 1);
        }

        if (flag1) {
            b0 = (byte) (b0 | 2);
        }

        return b0;
    }

    public static boolean numberHasMin(byte b0) {
        return (b0 & 1) != 0;
    }

    public static boolean numberHasMax(byte b0) {
        return (b0 & 2) != 0;
    }
}
