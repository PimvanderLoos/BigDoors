package net.minecraft.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.item.Item;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;

public class ChatDeserializer {

    private static final Gson GSON = (new GsonBuilder()).create();

    public ChatDeserializer() {}

    public static boolean isStringValue(JsonObject jsonobject, String s) {
        return !isValidPrimitive(jsonobject, s) ? false : jsonobject.getAsJsonPrimitive(s).isString();
    }

    public static boolean isStringValue(JsonElement jsonelement) {
        return !jsonelement.isJsonPrimitive() ? false : jsonelement.getAsJsonPrimitive().isString();
    }

    public static boolean isNumberValue(JsonObject jsonobject, String s) {
        return !isValidPrimitive(jsonobject, s) ? false : jsonobject.getAsJsonPrimitive(s).isNumber();
    }

    public static boolean isNumberValue(JsonElement jsonelement) {
        return !jsonelement.isJsonPrimitive() ? false : jsonelement.getAsJsonPrimitive().isNumber();
    }

    public static boolean isBooleanValue(JsonObject jsonobject, String s) {
        return !isValidPrimitive(jsonobject, s) ? false : jsonobject.getAsJsonPrimitive(s).isBoolean();
    }

    public static boolean isBooleanValue(JsonElement jsonelement) {
        return !jsonelement.isJsonPrimitive() ? false : jsonelement.getAsJsonPrimitive().isBoolean();
    }

    public static boolean isArrayNode(JsonObject jsonobject, String s) {
        return !isValidNode(jsonobject, s) ? false : jsonobject.get(s).isJsonArray();
    }

    public static boolean isObjectNode(JsonObject jsonobject, String s) {
        return !isValidNode(jsonobject, s) ? false : jsonobject.get(s).isJsonObject();
    }

    public static boolean isValidPrimitive(JsonObject jsonobject, String s) {
        return !isValidNode(jsonobject, s) ? false : jsonobject.get(s).isJsonPrimitive();
    }

    public static boolean isValidNode(JsonObject jsonobject, String s) {
        return jsonobject == null ? false : jsonobject.get(s) != null;
    }

    public static String convertToString(JsonElement jsonelement, String s) {
        if (jsonelement.isJsonPrimitive()) {
            return jsonelement.getAsString();
        } else {
            throw new JsonSyntaxException("Expected " + s + " to be a string, was " + getType(jsonelement));
        }
    }

    public static String getAsString(JsonObject jsonobject, String s) {
        if (jsonobject.has(s)) {
            return convertToString(jsonobject.get(s), s);
        } else {
            throw new JsonSyntaxException("Missing " + s + ", expected to find a string");
        }
    }

    @Nullable
    @Contract("_,_,!null->!null;_,_,null->_")
    public static String getAsString(JsonObject jsonobject, String s, @Nullable String s1) {
        return jsonobject.has(s) ? convertToString(jsonobject.get(s), s) : s1;
    }

    public static Item convertToItem(JsonElement jsonelement, String s) {
        if (jsonelement.isJsonPrimitive()) {
            String s1 = jsonelement.getAsString();

            return (Item) IRegistry.ITEM.getOptional(new MinecraftKey(s1)).orElseThrow(() -> {
                return new JsonSyntaxException("Expected " + s + " to be an item, was unknown string '" + s1 + "'");
            });
        } else {
            throw new JsonSyntaxException("Expected " + s + " to be an item, was " + getType(jsonelement));
        }
    }

    public static Item getAsItem(JsonObject jsonobject, String s) {
        if (jsonobject.has(s)) {
            return convertToItem(jsonobject.get(s), s);
        } else {
            throw new JsonSyntaxException("Missing " + s + ", expected to find an item");
        }
    }

    @Nullable
    @Contract("_,_,!null->!null;_,_,null->_")
    public static Item getAsItem(JsonObject jsonobject, String s, @Nullable Item item) {
        return jsonobject.has(s) ? convertToItem(jsonobject.get(s), s) : item;
    }

    public static boolean convertToBoolean(JsonElement jsonelement, String s) {
        if (jsonelement.isJsonPrimitive()) {
            return jsonelement.getAsBoolean();
        } else {
            throw new JsonSyntaxException("Expected " + s + " to be a Boolean, was " + getType(jsonelement));
        }
    }

    public static boolean getAsBoolean(JsonObject jsonobject, String s) {
        if (jsonobject.has(s)) {
            return convertToBoolean(jsonobject.get(s), s);
        } else {
            throw new JsonSyntaxException("Missing " + s + ", expected to find a Boolean");
        }
    }

    public static boolean getAsBoolean(JsonObject jsonobject, String s, boolean flag) {
        return jsonobject.has(s) ? convertToBoolean(jsonobject.get(s), s) : flag;
    }

    public static double convertToDouble(JsonElement jsonelement, String s) {
        if (jsonelement.isJsonPrimitive() && jsonelement.getAsJsonPrimitive().isNumber()) {
            return jsonelement.getAsDouble();
        } else {
            throw new JsonSyntaxException("Expected " + s + " to be a Double, was " + getType(jsonelement));
        }
    }

    public static double getAsDouble(JsonObject jsonobject, String s) {
        if (jsonobject.has(s)) {
            return convertToDouble(jsonobject.get(s), s);
        } else {
            throw new JsonSyntaxException("Missing " + s + ", expected to find a Double");
        }
    }

    public static double getAsDouble(JsonObject jsonobject, String s, double d0) {
        return jsonobject.has(s) ? convertToDouble(jsonobject.get(s), s) : d0;
    }

    public static float convertToFloat(JsonElement jsonelement, String s) {
        if (jsonelement.isJsonPrimitive() && jsonelement.getAsJsonPrimitive().isNumber()) {
            return jsonelement.getAsFloat();
        } else {
            throw new JsonSyntaxException("Expected " + s + " to be a Float, was " + getType(jsonelement));
        }
    }

    public static float getAsFloat(JsonObject jsonobject, String s) {
        if (jsonobject.has(s)) {
            return convertToFloat(jsonobject.get(s), s);
        } else {
            throw new JsonSyntaxException("Missing " + s + ", expected to find a Float");
        }
    }

    public static float getAsFloat(JsonObject jsonobject, String s, float f) {
        return jsonobject.has(s) ? convertToFloat(jsonobject.get(s), s) : f;
    }

    public static long convertToLong(JsonElement jsonelement, String s) {
        if (jsonelement.isJsonPrimitive() && jsonelement.getAsJsonPrimitive().isNumber()) {
            return jsonelement.getAsLong();
        } else {
            throw new JsonSyntaxException("Expected " + s + " to be a Long, was " + getType(jsonelement));
        }
    }

    public static long getAsLong(JsonObject jsonobject, String s) {
        if (jsonobject.has(s)) {
            return convertToLong(jsonobject.get(s), s);
        } else {
            throw new JsonSyntaxException("Missing " + s + ", expected to find a Long");
        }
    }

    public static long getAsLong(JsonObject jsonobject, String s, long i) {
        return jsonobject.has(s) ? convertToLong(jsonobject.get(s), s) : i;
    }

    public static int convertToInt(JsonElement jsonelement, String s) {
        if (jsonelement.isJsonPrimitive() && jsonelement.getAsJsonPrimitive().isNumber()) {
            return jsonelement.getAsInt();
        } else {
            throw new JsonSyntaxException("Expected " + s + " to be a Int, was " + getType(jsonelement));
        }
    }

    public static int getAsInt(JsonObject jsonobject, String s) {
        if (jsonobject.has(s)) {
            return convertToInt(jsonobject.get(s), s);
        } else {
            throw new JsonSyntaxException("Missing " + s + ", expected to find a Int");
        }
    }

    public static int getAsInt(JsonObject jsonobject, String s, int i) {
        return jsonobject.has(s) ? convertToInt(jsonobject.get(s), s) : i;
    }

    public static byte convertToByte(JsonElement jsonelement, String s) {
        if (jsonelement.isJsonPrimitive() && jsonelement.getAsJsonPrimitive().isNumber()) {
            return jsonelement.getAsByte();
        } else {
            throw new JsonSyntaxException("Expected " + s + " to be a Byte, was " + getType(jsonelement));
        }
    }

    public static byte getAsByte(JsonObject jsonobject, String s) {
        if (jsonobject.has(s)) {
            return convertToByte(jsonobject.get(s), s);
        } else {
            throw new JsonSyntaxException("Missing " + s + ", expected to find a Byte");
        }
    }

    public static byte getAsByte(JsonObject jsonobject, String s, byte b0) {
        return jsonobject.has(s) ? convertToByte(jsonobject.get(s), s) : b0;
    }

    public static char convertToCharacter(JsonElement jsonelement, String s) {
        if (jsonelement.isJsonPrimitive() && jsonelement.getAsJsonPrimitive().isNumber()) {
            return jsonelement.getAsCharacter();
        } else {
            throw new JsonSyntaxException("Expected " + s + " to be a Character, was " + getType(jsonelement));
        }
    }

    public static char getAsCharacter(JsonObject jsonobject, String s) {
        if (jsonobject.has(s)) {
            return convertToCharacter(jsonobject.get(s), s);
        } else {
            throw new JsonSyntaxException("Missing " + s + ", expected to find a Character");
        }
    }

    public static char getAsCharacter(JsonObject jsonobject, String s, char c0) {
        return jsonobject.has(s) ? convertToCharacter(jsonobject.get(s), s) : c0;
    }

    public static BigDecimal convertToBigDecimal(JsonElement jsonelement, String s) {
        if (jsonelement.isJsonPrimitive() && jsonelement.getAsJsonPrimitive().isNumber()) {
            return jsonelement.getAsBigDecimal();
        } else {
            throw new JsonSyntaxException("Expected " + s + " to be a BigDecimal, was " + getType(jsonelement));
        }
    }

    public static BigDecimal getAsBigDecimal(JsonObject jsonobject, String s) {
        if (jsonobject.has(s)) {
            return convertToBigDecimal(jsonobject.get(s), s);
        } else {
            throw new JsonSyntaxException("Missing " + s + ", expected to find a BigDecimal");
        }
    }

    public static BigDecimal getAsBigDecimal(JsonObject jsonobject, String s, BigDecimal bigdecimal) {
        return jsonobject.has(s) ? convertToBigDecimal(jsonobject.get(s), s) : bigdecimal;
    }

    public static BigInteger convertToBigInteger(JsonElement jsonelement, String s) {
        if (jsonelement.isJsonPrimitive() && jsonelement.getAsJsonPrimitive().isNumber()) {
            return jsonelement.getAsBigInteger();
        } else {
            throw new JsonSyntaxException("Expected " + s + " to be a BigInteger, was " + getType(jsonelement));
        }
    }

    public static BigInteger getAsBigInteger(JsonObject jsonobject, String s) {
        if (jsonobject.has(s)) {
            return convertToBigInteger(jsonobject.get(s), s);
        } else {
            throw new JsonSyntaxException("Missing " + s + ", expected to find a BigInteger");
        }
    }

    public static BigInteger getAsBigInteger(JsonObject jsonobject, String s, BigInteger biginteger) {
        return jsonobject.has(s) ? convertToBigInteger(jsonobject.get(s), s) : biginteger;
    }

    public static short convertToShort(JsonElement jsonelement, String s) {
        if (jsonelement.isJsonPrimitive() && jsonelement.getAsJsonPrimitive().isNumber()) {
            return jsonelement.getAsShort();
        } else {
            throw new JsonSyntaxException("Expected " + s + " to be a Short, was " + getType(jsonelement));
        }
    }

    public static short getAsShort(JsonObject jsonobject, String s) {
        if (jsonobject.has(s)) {
            return convertToShort(jsonobject.get(s), s);
        } else {
            throw new JsonSyntaxException("Missing " + s + ", expected to find a Short");
        }
    }

    public static short getAsShort(JsonObject jsonobject, String s, short short0) {
        return jsonobject.has(s) ? convertToShort(jsonobject.get(s), s) : short0;
    }

    public static JsonObject convertToJsonObject(JsonElement jsonelement, String s) {
        if (jsonelement.isJsonObject()) {
            return jsonelement.getAsJsonObject();
        } else {
            throw new JsonSyntaxException("Expected " + s + " to be a JsonObject, was " + getType(jsonelement));
        }
    }

    public static JsonObject getAsJsonObject(JsonObject jsonobject, String s) {
        if (jsonobject.has(s)) {
            return convertToJsonObject(jsonobject.get(s), s);
        } else {
            throw new JsonSyntaxException("Missing " + s + ", expected to find a JsonObject");
        }
    }

    @Nullable
    @Contract("_,_,!null->!null;_,_,null->_")
    public static JsonObject getAsJsonObject(JsonObject jsonobject, String s, @Nullable JsonObject jsonobject1) {
        return jsonobject.has(s) ? convertToJsonObject(jsonobject.get(s), s) : jsonobject1;
    }

    public static JsonArray convertToJsonArray(JsonElement jsonelement, String s) {
        if (jsonelement.isJsonArray()) {
            return jsonelement.getAsJsonArray();
        } else {
            throw new JsonSyntaxException("Expected " + s + " to be a JsonArray, was " + getType(jsonelement));
        }
    }

    public static JsonArray getAsJsonArray(JsonObject jsonobject, String s) {
        if (jsonobject.has(s)) {
            return convertToJsonArray(jsonobject.get(s), s);
        } else {
            throw new JsonSyntaxException("Missing " + s + ", expected to find a JsonArray");
        }
    }

    @Nullable
    @Contract("_,_,!null->!null;_,_,null->_")
    public static JsonArray getAsJsonArray(JsonObject jsonobject, String s, @Nullable JsonArray jsonarray) {
        return jsonobject.has(s) ? convertToJsonArray(jsonobject.get(s), s) : jsonarray;
    }

    public static <T> T convertToObject(@Nullable JsonElement jsonelement, String s, JsonDeserializationContext jsondeserializationcontext, Class<? extends T> oclass) {
        if (jsonelement != null) {
            return jsondeserializationcontext.deserialize(jsonelement, oclass);
        } else {
            throw new JsonSyntaxException("Missing " + s);
        }
    }

    public static <T> T getAsObject(JsonObject jsonobject, String s, JsonDeserializationContext jsondeserializationcontext, Class<? extends T> oclass) {
        if (jsonobject.has(s)) {
            return convertToObject(jsonobject.get(s), s, jsondeserializationcontext, oclass);
        } else {
            throw new JsonSyntaxException("Missing " + s);
        }
    }

    @Nullable
    @Contract("_,_,!null,_,_->!null;_,_,null,_,_->_")
    public static <T> T getAsObject(JsonObject jsonobject, String s, @Nullable T t0, JsonDeserializationContext jsondeserializationcontext, Class<? extends T> oclass) {
        return jsonobject.has(s) ? convertToObject(jsonobject.get(s), s, jsondeserializationcontext, oclass) : t0;
    }

    public static String getType(@Nullable JsonElement jsonelement) {
        String s = StringUtils.abbreviateMiddle(String.valueOf(jsonelement), "...", 10);

        if (jsonelement == null) {
            return "null (missing)";
        } else if (jsonelement.isJsonNull()) {
            return "null (json)";
        } else if (jsonelement.isJsonArray()) {
            return "an array (" + s + ")";
        } else if (jsonelement.isJsonObject()) {
            return "an object (" + s + ")";
        } else {
            if (jsonelement.isJsonPrimitive()) {
                JsonPrimitive jsonprimitive = jsonelement.getAsJsonPrimitive();

                if (jsonprimitive.isNumber()) {
                    return "a number (" + s + ")";
                }

                if (jsonprimitive.isBoolean()) {
                    return "a boolean (" + s + ")";
                }
            }

            return s;
        }
    }

    @Nullable
    public static <T> T fromJson(Gson gson, Reader reader, Class<T> oclass, boolean flag) {
        try {
            JsonReader jsonreader = new JsonReader(reader);

            jsonreader.setLenient(flag);
            return gson.getAdapter(oclass).read(jsonreader);
        } catch (IOException ioexception) {
            throw new JsonParseException(ioexception);
        }
    }

    @Nullable
    public static <T> T fromJson(Gson gson, Reader reader, TypeToken<T> typetoken, boolean flag) {
        try {
            JsonReader jsonreader = new JsonReader(reader);

            jsonreader.setLenient(flag);
            return gson.getAdapter(typetoken).read(jsonreader);
        } catch (IOException ioexception) {
            throw new JsonParseException(ioexception);
        }
    }

    @Nullable
    public static <T> T fromJson(Gson gson, String s, TypeToken<T> typetoken, boolean flag) {
        return fromJson(gson, (Reader) (new StringReader(s)), typetoken, flag);
    }

    @Nullable
    public static <T> T fromJson(Gson gson, String s, Class<T> oclass, boolean flag) {
        return fromJson(gson, (Reader) (new StringReader(s)), oclass, flag);
    }

    @Nullable
    public static <T> T fromJson(Gson gson, Reader reader, TypeToken<T> typetoken) {
        return fromJson(gson, reader, typetoken, false);
    }

    @Nullable
    public static <T> T fromJson(Gson gson, String s, TypeToken<T> typetoken) {
        return fromJson(gson, s, typetoken, false);
    }

    @Nullable
    public static <T> T fromJson(Gson gson, Reader reader, Class<T> oclass) {
        return fromJson(gson, reader, oclass, false);
    }

    @Nullable
    public static <T> T fromJson(Gson gson, String s, Class<T> oclass) {
        return fromJson(gson, s, oclass, false);
    }

    public static JsonObject parse(String s, boolean flag) {
        return parse((Reader) (new StringReader(s)), flag);
    }

    public static JsonObject parse(Reader reader, boolean flag) {
        return (JsonObject) fromJson(ChatDeserializer.GSON, reader, JsonObject.class, flag);
    }

    public static JsonObject parse(String s) {
        return parse(s, false);
    }

    public static JsonObject parse(Reader reader) {
        return parse(reader, false);
    }

    public static JsonArray parseArray(Reader reader) {
        return (JsonArray) fromJson(ChatDeserializer.GSON, reader, JsonArray.class, false);
    }
}
