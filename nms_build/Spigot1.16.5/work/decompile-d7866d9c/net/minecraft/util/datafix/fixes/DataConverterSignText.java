package net.minecraft.util.datafix.fixes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.lang.reflect.Type;
import java.util.Iterator;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.util.ChatDeserializer;
import org.apache.commons.lang3.StringUtils;

public class DataConverterSignText extends DataConverterNamedEntity {

    public static final Gson a = (new GsonBuilder()).registerTypeAdapter(IChatBaseComponent.class, new JsonDeserializer<IChatBaseComponent>() {
        public IChatMutableComponent deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            if (jsonelement.isJsonPrimitive()) {
                return new ChatComponentText(jsonelement.getAsString());
            } else if (jsonelement.isJsonArray()) {
                JsonArray jsonarray = jsonelement.getAsJsonArray();
                IChatMutableComponent ichatmutablecomponent = null;
                Iterator iterator = jsonarray.iterator();

                while (iterator.hasNext()) {
                    JsonElement jsonelement1 = (JsonElement) iterator.next();
                    IChatMutableComponent ichatmutablecomponent1 = this.deserialize(jsonelement1, jsonelement1.getClass(), jsondeserializationcontext);

                    if (ichatmutablecomponent == null) {
                        ichatmutablecomponent = ichatmutablecomponent1;
                    } else {
                        ichatmutablecomponent.addSibling(ichatmutablecomponent1);
                    }
                }

                return ichatmutablecomponent;
            } else {
                throw new JsonParseException("Don't know how to turn " + jsonelement + " into a Component");
            }
        }
    }).create();

    public DataConverterSignText(Schema schema, boolean flag) {
        super(schema, flag, "BlockEntitySignTextStrictJsonFix", DataConverterTypes.BLOCK_ENTITY, "Sign");
    }

    private Dynamic<?> a(Dynamic<?> dynamic, String s) {
        String s1 = dynamic.get(s).asString("");
        Object object = null;

        if (!"null".equals(s1) && !StringUtils.isEmpty(s1)) {
            if ((s1.charAt(0) != '"' || s1.charAt(s1.length() - 1) != '"') && (s1.charAt(0) != '{' || s1.charAt(s1.length() - 1) != '}')) {
                object = new ChatComponentText(s1);
            } else {
                try {
                    object = (IChatBaseComponent) ChatDeserializer.a(DataConverterSignText.a, s1, IChatBaseComponent.class, true);
                    if (object == null) {
                        object = ChatComponentText.d;
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
            object = ChatComponentText.d;
        }

        return dynamic.set(s, dynamic.createString(IChatBaseComponent.ChatSerializer.a((IChatBaseComponent) object)));
    }

    @Override
    protected Typed<?> a(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), (dynamic) -> {
            dynamic = this.a(dynamic, "Text1");
            dynamic = this.a(dynamic, "Text2");
            dynamic = this.a(dynamic, "Text3");
            dynamic = this.a(dynamic, "Text4");
            return dynamic;
        });
    }
}
