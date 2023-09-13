package net.minecraft.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Iterator;

public class DataConverterSignText implements IDataConverter {

    public static final Gson a = (new GsonBuilder()).registerTypeAdapter(IChatBaseComponent.class, new JsonDeserializer() {
        public IChatBaseComponent a(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            if (jsonelement.isJsonPrimitive()) {
                return new ChatComponentText(jsonelement.getAsString());
            } else if (jsonelement.isJsonArray()) {
                JsonArray jsonarray = jsonelement.getAsJsonArray();
                IChatBaseComponent ichatbasecomponent = null;
                Iterator iterator = jsonarray.iterator();

                while (iterator.hasNext()) {
                    JsonElement jsonelement1 = (JsonElement) iterator.next();
                    IChatBaseComponent ichatbasecomponent1 = this.a(jsonelement1, jsonelement1.getClass(), jsondeserializationcontext);

                    if (ichatbasecomponent == null) {
                        ichatbasecomponent = ichatbasecomponent1;
                    } else {
                        ichatbasecomponent.addSibling(ichatbasecomponent1);
                    }
                }

                return ichatbasecomponent;
            } else {
                throw new JsonParseException("Don\'t know how to turn " + jsonelement + " into a Component");
            }
        }

        public Object deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            return this.a(jsonelement, type, jsondeserializationcontext);
        }
    }).create();

    public DataConverterSignText() {}

    public int a() {
        return 101;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        if ("Sign".equals(nbttagcompound.getString("id"))) {
            this.a(nbttagcompound, "Text1");
            this.a(nbttagcompound, "Text2");
            this.a(nbttagcompound, "Text3");
            this.a(nbttagcompound, "Text4");
        }

        return nbttagcompound;
    }

    private void a(NBTTagCompound nbttagcompound, String s) {
        String s1 = nbttagcompound.getString(s);
        Object object = null;

        if (!"null".equals(s1) && !UtilColor.b(s1)) {
            if ((s1.charAt(0) != 34 || s1.charAt(s1.length() - 1) != 34) && (s1.charAt(0) != 123 || s1.charAt(s1.length() - 1) != 125)) {
                object = new ChatComponentText(s1);
            } else {
                try {
                    object = (IChatBaseComponent) ChatDeserializer.a(DataConverterSignText.a, s1, IChatBaseComponent.class, true);
                    if (object == null) {
                        object = new ChatComponentText("");
                    }
                } catch (JsonParseException jsonparseexception) {
                    ;
                }

                if (object == null) {
                    try {
                        object = IChatBaseComponent.ChatSerializer.a(s1);
                    } catch (JsonParseException jsonparseexception1) {
                        ;
                    }
                }

                if (object == null) {
                    try {
                        object = IChatBaseComponent.ChatSerializer.b(s1);
                    } catch (JsonParseException jsonparseexception2) {
                        ;
                    }
                }

                if (object == null) {
                    object = new ChatComponentText(s1);
                }
            }
        } else {
            object = new ChatComponentText("");
        }

        nbttagcompound.setString(s, IChatBaseComponent.ChatSerializer.a((IChatBaseComponent) object));
    }
}
