package net.minecraft.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootItemFunctionSetAttribute extends LootItemFunction {

    private static final Logger a = LogManager.getLogger();
    private final LootItemFunctionSetAttribute.a[] b;

    public LootItemFunctionSetAttribute(LootItemCondition[] alootitemcondition, LootItemFunctionSetAttribute.a[] alootitemfunctionsetattribute_a) {
        super(alootitemcondition);
        this.b = alootitemfunctionsetattribute_a;
    }

    public ItemStack a(ItemStack itemstack, Random random, LootTableInfo loottableinfo) {
        LootItemFunctionSetAttribute.a[] alootitemfunctionsetattribute_a = this.b;
        int i = alootitemfunctionsetattribute_a.length;

        for (int j = 0; j < i; ++j) {
            LootItemFunctionSetAttribute.a lootitemfunctionsetattribute_a = alootitemfunctionsetattribute_a[j];
            UUID uuid = lootitemfunctionsetattribute_a.e;

            if (uuid == null) {
                uuid = UUID.randomUUID();
            }

            EnumItemSlot enumitemslot = lootitemfunctionsetattribute_a.f[random.nextInt(lootitemfunctionsetattribute_a.f.length)];

            itemstack.a(lootitemfunctionsetattribute_a.b, new AttributeModifier(uuid, lootitemfunctionsetattribute_a.a, (double) lootitemfunctionsetattribute_a.d.b(random), lootitemfunctionsetattribute_a.c), enumitemslot);
        }

        return itemstack;
    }

    static class a {

        private final String a;
        private final String b;
        private final int c;
        private final LootValueBounds d;
        @Nullable
        private final UUID e;
        private final EnumItemSlot[] f;

        private a(String s, String s1, int i, LootValueBounds lootvaluebounds, EnumItemSlot[] aenumitemslot, @Nullable UUID uuid) {
            this.a = s;
            this.b = s1;
            this.c = i;
            this.d = lootvaluebounds;
            this.e = uuid;
            this.f = aenumitemslot;
        }

        public JsonObject a(JsonSerializationContext jsonserializationcontext) {
            JsonObject jsonobject = new JsonObject();

            jsonobject.addProperty("name", this.a);
            jsonobject.addProperty("attribute", this.b);
            jsonobject.addProperty("operation", a(this.c));
            jsonobject.add("amount", jsonserializationcontext.serialize(this.d));
            if (this.e != null) {
                jsonobject.addProperty("id", this.e.toString());
            }

            if (this.f.length == 1) {
                jsonobject.addProperty("slot", this.f[0].d());
            } else {
                JsonArray jsonarray = new JsonArray();
                EnumItemSlot[] aenumitemslot = this.f;
                int i = aenumitemslot.length;

                for (int j = 0; j < i; ++j) {
                    EnumItemSlot enumitemslot = aenumitemslot[j];

                    jsonarray.add(new JsonPrimitive(enumitemslot.d()));
                }

                jsonobject.add("slot", jsonarray);
            }

            return jsonobject;
        }

        public static LootItemFunctionSetAttribute.a a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            String s = ChatDeserializer.h(jsonobject, "name");
            String s1 = ChatDeserializer.h(jsonobject, "attribute");
            int i = a(ChatDeserializer.h(jsonobject, "operation"));
            LootValueBounds lootvaluebounds = (LootValueBounds) ChatDeserializer.a(jsonobject, "amount", jsondeserializationcontext, LootValueBounds.class);
            UUID uuid = null;
            EnumItemSlot[] aenumitemslot;

            if (ChatDeserializer.a(jsonobject, "slot")) {
                aenumitemslot = new EnumItemSlot[] { EnumItemSlot.a(ChatDeserializer.h(jsonobject, "slot"))};
            } else {
                if (!ChatDeserializer.d(jsonobject, "slot")) {
                    throw new JsonSyntaxException("Invalid or missing attribute modifier slot; must be either string or array of strings.");
                }

                JsonArray jsonarray = ChatDeserializer.u(jsonobject, "slot");

                aenumitemslot = new EnumItemSlot[jsonarray.size()];
                int j = 0;

                JsonElement jsonelement;

                for (Iterator iterator = jsonarray.iterator(); iterator.hasNext(); aenumitemslot[j++] = EnumItemSlot.a(ChatDeserializer.a(jsonelement, "slot"))) {
                    jsonelement = (JsonElement) iterator.next();
                }

                if (aenumitemslot.length == 0) {
                    throw new JsonSyntaxException("Invalid attribute modifier slot; must contain at least one entry.");
                }
            }

            if (jsonobject.has("id")) {
                String s2 = ChatDeserializer.h(jsonobject, "id");

                try {
                    uuid = UUID.fromString(s2);
                } catch (IllegalArgumentException illegalargumentexception) {
                    throw new JsonSyntaxException("Invalid attribute modifier id \'" + s2 + "\' (must be UUID format, with dashes)");
                }
            }

            return new LootItemFunctionSetAttribute.a(s, s1, i, lootvaluebounds, aenumitemslot, uuid);
        }

        private static String a(int i) {
            switch (i) {
            case 0:
                return "addition";

            case 1:
                return "multiply_base";

            case 2:
                return "multiply_total";

            default:
                throw new IllegalArgumentException("Unknown operation " + i);
            }
        }

        private static int a(String s) {
            if ("addition".equals(s)) {
                return 0;
            } else if ("multiply_base".equals(s)) {
                return 1;
            } else if ("multiply_total".equals(s)) {
                return 2;
            } else {
                throw new JsonSyntaxException("Unknown attribute modifier operation " + s);
            }
        }
    }

    public static class b extends LootItemFunction.a<LootItemFunctionSetAttribute> {

        public b() {
            super(new MinecraftKey("set_attributes"), LootItemFunctionSetAttribute.class);
        }

        public void a(JsonObject jsonobject, LootItemFunctionSetAttribute lootitemfunctionsetattribute, JsonSerializationContext jsonserializationcontext) {
            JsonArray jsonarray = new JsonArray();
            LootItemFunctionSetAttribute.a[] alootitemfunctionsetattribute_a = lootitemfunctionsetattribute.b;
            int i = alootitemfunctionsetattribute_a.length;

            for (int j = 0; j < i; ++j) {
                LootItemFunctionSetAttribute.a lootitemfunctionsetattribute_a = alootitemfunctionsetattribute_a[j];

                jsonarray.add(lootitemfunctionsetattribute_a.a(jsonserializationcontext));
            }

            jsonobject.add("modifiers", jsonarray);
        }

        public LootItemFunctionSetAttribute a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            JsonArray jsonarray = ChatDeserializer.u(jsonobject, "modifiers");
            LootItemFunctionSetAttribute.a[] alootitemfunctionsetattribute_a = new LootItemFunctionSetAttribute.a[jsonarray.size()];
            int i = 0;

            JsonElement jsonelement;

            for (Iterator iterator = jsonarray.iterator(); iterator.hasNext(); alootitemfunctionsetattribute_a[i++] = LootItemFunctionSetAttribute.a.a(ChatDeserializer.m(jsonelement, "modifier"), jsondeserializationcontext)) {
                jsonelement = (JsonElement) iterator.next();
            }

            if (alootitemfunctionsetattribute_a.length == 0) {
                throw new JsonSyntaxException("Invalid attribute modifiers array; cannot be empty");
            } else {
                return new LootItemFunctionSetAttribute(alootitemcondition, alootitemfunctionsetattribute_a);
            }
        }

        public LootItemFunction b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            return this.a(jsonobject, jsondeserializationcontext, alootitemcondition);
        }
    }
}
