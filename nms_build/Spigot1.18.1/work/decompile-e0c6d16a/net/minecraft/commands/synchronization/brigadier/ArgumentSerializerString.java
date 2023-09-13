package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType.StringType;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.PacketDataSerializer;

public class ArgumentSerializerString implements ArgumentSerializer<StringArgumentType> {

    public ArgumentSerializerString() {}

    public void serializeToNetwork(StringArgumentType stringargumenttype, PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeEnum(stringargumenttype.getType());
    }

    @Override
    public StringArgumentType deserializeFromNetwork(PacketDataSerializer packetdataserializer) {
        StringType stringtype = (StringType) packetdataserializer.readEnum(StringType.class);

        switch (stringtype) {
            case SINGLE_WORD:
                return StringArgumentType.word();
            case QUOTABLE_PHRASE:
                return StringArgumentType.string();
            case GREEDY_PHRASE:
            default:
                return StringArgumentType.greedyString();
        }
    }

    public void serializeToJson(StringArgumentType stringargumenttype, JsonObject jsonobject) {
        switch (stringargumenttype.getType()) {
            case SINGLE_WORD:
                jsonobject.addProperty("type", "word");
                break;
            case QUOTABLE_PHRASE:
                jsonobject.addProperty("type", "phrase");
                break;
            case GREEDY_PHRASE:
            default:
                jsonobject.addProperty("type", "greedy");
        }

    }
}
