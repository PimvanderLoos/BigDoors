package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import org.apache.commons.lang3.ArrayUtils;

public class LootSelector {

    private final LotoSelectorEntry[] a;
    private final LootItemCondition[] b;
    private final LootValueBounds c;
    private final LootValueBounds d;

    public LootSelector(LotoSelectorEntry[] alotoselectorentry, LootItemCondition[] alootitemcondition, LootValueBounds lootvaluebounds, LootValueBounds lootvaluebounds1) {
        this.a = alotoselectorentry;
        this.b = alootitemcondition;
        this.c = lootvaluebounds;
        this.d = lootvaluebounds1;
    }

    protected void a(Collection<ItemStack> collection, Random random, LootTableInfo loottableinfo) {
        ArrayList arraylist = Lists.newArrayList();
        int i = 0;
        LotoSelectorEntry[] alotoselectorentry = this.a;
        int j = alotoselectorentry.length;

        for (int k = 0; k < j; ++k) {
            LotoSelectorEntry lotoselectorentry = alotoselectorentry[k];

            if (LootItemConditions.a(lotoselectorentry.e, random, loottableinfo)) {
                int l = lotoselectorentry.a(loottableinfo.f());

                if (l > 0) {
                    arraylist.add(lotoselectorentry);
                    i += l;
                }
            }
        }

        if (i != 0 && !arraylist.isEmpty()) {
            int i1 = random.nextInt(i);
            Iterator iterator = arraylist.iterator();

            LotoSelectorEntry lotoselectorentry1;

            do {
                if (!iterator.hasNext()) {
                    return;
                }

                lotoselectorentry1 = (LotoSelectorEntry) iterator.next();
                i1 -= lotoselectorentry1.a(loottableinfo.f());
            } while (i1 >= 0);

            lotoselectorentry1.a(collection, random, loottableinfo);
        }
    }

    public void b(Collection<ItemStack> collection, Random random, LootTableInfo loottableinfo) {
        if (LootItemConditions.a(this.b, random, loottableinfo)) {
            int i = this.c.a(random) + MathHelper.d(this.d.b(random) * loottableinfo.f());

            for (int j = 0; j < i; ++j) {
                this.a(collection, random, loottableinfo);
            }

        }
    }

    public static class a implements JsonDeserializer<LootSelector>, JsonSerializer<LootSelector> {

        public a() {}

        public LootSelector a(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "loot pool");
            LotoSelectorEntry[] alotoselectorentry = (LotoSelectorEntry[]) ChatDeserializer.a(jsonobject, "entries", jsondeserializationcontext, LotoSelectorEntry[].class);
            LootItemCondition[] alootitemcondition = (LootItemCondition[]) ChatDeserializer.a(jsonobject, "conditions", new LootItemCondition[0], jsondeserializationcontext, LootItemCondition[].class);
            LootValueBounds lootvaluebounds = (LootValueBounds) ChatDeserializer.a(jsonobject, "rolls", jsondeserializationcontext, LootValueBounds.class);
            LootValueBounds lootvaluebounds1 = (LootValueBounds) ChatDeserializer.a(jsonobject, "bonus_rolls", new LootValueBounds(0.0F, 0.0F), jsondeserializationcontext, LootValueBounds.class);

            return new LootSelector(alotoselectorentry, alootitemcondition, lootvaluebounds, lootvaluebounds1);
        }

        public JsonElement a(LootSelector lootselector, Type type, JsonSerializationContext jsonserializationcontext) {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("entries", jsonserializationcontext.serialize(lootselector.a));
            jsonobject.add("rolls", jsonserializationcontext.serialize(lootselector.c));
            if (lootselector.d.a() != 0.0F && lootselector.d.b() != 0.0F) {
                jsonobject.add("bonus_rolls", jsonserializationcontext.serialize(lootselector.d));
            }

            if (!ArrayUtils.isEmpty(lootselector.b)) {
                jsonobject.add("conditions", jsonserializationcontext.serialize(lootselector.b));
            }

            return jsonobject;
        }

        public JsonElement serialize(Object object, Type type, JsonSerializationContext jsonserializationcontext) {
            return this.a((LootSelector) object, type, jsonserializationcontext);
        }

        public Object deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            return this.a(jsonelement, type, jsondeserializationcontext);
        }
    }
}
