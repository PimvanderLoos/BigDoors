package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType.StringType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.PacketDataSerializer;

public class ArgumentSerializerString implements ArgumentTypeInfo<StringArgumentType, ArgumentSerializerString.a> {

    public ArgumentSerializerString() {}

    public void serializeToNetwork(ArgumentSerializerString.a argumentserializerstring_a, PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeEnum(argumentserializerstring_a.type);
    }

    @Override
    public ArgumentSerializerString.a deserializeFromNetwork(PacketDataSerializer packetdataserializer) {
        StringType stringtype = (StringType) packetdataserializer.readEnum(StringType.class);

        return new ArgumentSerializerString.a(stringtype);
    }

    public void serializeToJson(ArgumentSerializerString.a argumentserializerstring_a, JsonObject jsonobject) {
        String s;

        switch (argumentserializerstring_a.type) {
            case SINGLE_WORD:
                s = "word";
                break;
            case QUOTABLE_PHRASE:
                s = "phrase";
                break;
            case GREEDY_PHRASE:
                s = "greedy";
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        jsonobject.addProperty("type", s);
    }

    public ArgumentSerializerString.a unpack(StringArgumentType stringargumenttype) {
        return new ArgumentSerializerString.a(stringargumenttype.getType());
    }

    public final class a implements ArgumentTypeInfo.a<StringArgumentType> {

        final StringType type;

        public a(StringType stringtype) {
            this.type = stringtype;
        }

        @Override
        public StringArgumentType instantiate(CommandBuildContext commandbuildcontext) {
            StringArgumentType stringargumenttype;

            switch (this.type) {
                case SINGLE_WORD:
                    stringargumenttype = StringArgumentType.word();
                    break;
                case QUOTABLE_PHRASE:
                    stringargumenttype = StringArgumentType.string();
                    break;
                case GREEDY_PHRASE:
                    stringargumenttype = StringArgumentType.greedyString();
                    break;
                default:
                    throw new IncompatibleClassChangeError();
            }

            return stringargumenttype;
        }

        @Override
        public ArgumentTypeInfo<StringArgumentType, ?> type() {
            return ArgumentSerializerString.this;
        }
    }
}
