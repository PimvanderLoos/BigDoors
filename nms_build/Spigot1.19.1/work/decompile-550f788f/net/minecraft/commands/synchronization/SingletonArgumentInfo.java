package net.minecraft.commands.synchronization;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.PacketDataSerializer;

public class SingletonArgumentInfo<A extends ArgumentType<?>> implements ArgumentTypeInfo<A, SingletonArgumentInfo<A>.a> {

    private final SingletonArgumentInfo<A>.a template;

    private SingletonArgumentInfo(Function<CommandBuildContext, A> function) {
        this.template = new SingletonArgumentInfo.a(function);
    }

    public static <T extends ArgumentType<?>> SingletonArgumentInfo<T> contextFree(Supplier<T> supplier) {
        return new SingletonArgumentInfo<>((commandbuildcontext) -> {
            return (ArgumentType) supplier.get();
        });
    }

    public static <T extends ArgumentType<?>> SingletonArgumentInfo<T> contextAware(Function<CommandBuildContext, T> function) {
        return new SingletonArgumentInfo<>(function);
    }

    public void serializeToNetwork(SingletonArgumentInfo<A>.a singletonargumentinfo_a, PacketDataSerializer packetdataserializer) {}

    public void serializeToJson(SingletonArgumentInfo<A>.a singletonargumentinfo_a, JsonObject jsonobject) {}

    @Override
    public SingletonArgumentInfo<A>.a deserializeFromNetwork(PacketDataSerializer packetdataserializer) {
        return this.template;
    }

    @Override
    public SingletonArgumentInfo<A>.a unpack(A a0) {
        return this.template;
    }

    public final class a implements ArgumentTypeInfo.a<A> {

        private final Function<CommandBuildContext, A> constructor;

        public a(Function function) {
            this.constructor = function;
        }

        @Override
        public A instantiate(CommandBuildContext commandbuildcontext) {
            return (ArgumentType) this.constructor.apply(commandbuildcontext);
        }

        @Override
        public ArgumentTypeInfo<A, ?> type() {
            return SingletonArgumentInfo.this;
        }
    }
}
