package net.minecraft.commands.synchronization.brigadier;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.synchronization.ArgumentRegistry;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.commands.synchronization.ArgumentSerializerVoid;

public class ArgumentSerializers {

    public static void a() {
        ArgumentRegistry.a("brigadier:bool", BoolArgumentType.class, (ArgumentSerializer) (new ArgumentSerializerVoid<>(BoolArgumentType::bool)));
        ArgumentRegistry.a("brigadier:float", FloatArgumentType.class, (ArgumentSerializer) (new ArgumentSerializerFloat()));
        ArgumentRegistry.a("brigadier:double", DoubleArgumentType.class, (ArgumentSerializer) (new ArgumentSerializerDouble()));
        ArgumentRegistry.a("brigadier:integer", IntegerArgumentType.class, (ArgumentSerializer) (new ArgumentSerializerInteger()));
        ArgumentRegistry.a("brigadier:long", LongArgumentType.class, (ArgumentSerializer) (new ArgumentSerializerLong()));
        ArgumentRegistry.a("brigadier:string", StringArgumentType.class, (ArgumentSerializer) (new ArgumentSerializerString()));
    }

    public static byte a(boolean flag, boolean flag1) {
        byte b0 = 0;

        if (flag) {
            b0 = (byte) (b0 | 1);
        }

        if (flag1) {
            b0 = (byte) (b0 | 2);
        }

        return b0;
    }

    public static boolean a(byte b0) {
        return (b0 & 1) != 0;
    }

    public static boolean b(byte b0) {
        return (b0 & 2) != 0;
    }
}
