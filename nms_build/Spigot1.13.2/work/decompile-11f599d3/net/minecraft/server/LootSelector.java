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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.ArrayUtils;

public class LootSelector {

    private final LootSelectorEntry[] a;
    private final LootItemCondition[] b;
    private final LootValueBounds c;
    private final LootValueBounds d;

    public LootSelector(LootSelectorEntry[] alootselectorentry, LootItemCondition[] alootitemcondition, LootValueBounds lootvaluebounds, LootValueBounds lootvaluebounds1) {
        this.a = alootselectorentry;
        this.b = alootitemcondition;
        this.c = lootvaluebounds;
        this.d = lootvaluebounds1;
    }

    protected void a(Collection<ItemStack> collection, Random random, LootTableInfo loottableinfo) {
        List<LootSelectorEntry> list = Lists.newArrayList();
        int i = 0;
        LootSelectorEntry[] alootselectorentry = this.a;
        int j = alootselectorentry.length;

        for (int k = 0; k < j; ++k) {
            LootSelectorEntry lootselectorentry = alootselectorentry[k];

            if (LootItemConditions.a(lootselectorentry.e, random, loottableinfo)) {
                int l = lootselectorentry.a(loottableinfo.g());

                if (l > 0) {
                    list.add(lootselectorentry);
                    i += l;
                }
            }
        }

        if (i != 0 && !list.isEmpty()) {
            int i1 = random.nextInt(i);
            Iterator iterator = list.iterator();

            LootSelectorEntry lootselectorentry1;

            do {
                if (!iterator.hasNext()) {
                    return;
                }

                lootselectorentry1 = (LootSelectorEntry) iterator.next();
                i1 -= lootselectorentry1.a(loottableinfo.g());
            } while (i1 >= 0);

            lootselectorentry1.a(collection, random, loottableinfo);
        }
    }

    public void b(Collection<ItemStack> collection, Random random, LootTableInfo loottableinfo) {
        if (LootItemConditions.a(this.b, random, loottableinfo)) {
            int i = this.c.a(random) + MathHelper.d(this.d.b(random) * loottableinfo.g());

            for (int j = 0; j < i; ++j) {
                this.a(collection, random, loottableinfo);
            }

        }
    }

    public static class a implements JsonDeserializer<LootSelector>, JsonSerializer<LootSelector> {

        public a() {}

        public LootSelector deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "loot pool");
            LootSelectorEntry[] alootselectorentry = (LootSelectorEntry[]) ChatDeserializer.a(jsonobject, "entries", jsondeserializationcontext, LootSelectorEntry[].class);
            LootItemCondition[] alootitemcondition = (LootItemCondition[]) ChatDeserializer.a(jsonobject, "conditions", new LootItemCondition[0], jsondeserializationcontext, LootItemCondition[].class);
            LootValueBounds lootvaluebounds = (LootValueBounds) ChatDeserializer.a(jsonobject, "rolls", jsondeserializationcontext, LootValueBounds.class);
            LootValueBounds lootvaluebounds1 = (LootValueBounds) ChatDeserializer.a(jsonobject, "bonus_rolls", new LootValueBounds(0.0F, 0.0F), jsondeserializationcontext, LootValueBounds.class);

            return new LootSelector(alootselectorentry, alootitemcondition, lootvaluebounds, lootvaluebounds1);
        }

        public JsonElement serialize(LootSelector lootselector, Type type, JsonSerializationContext jsonserializationcontext) {
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
    }
}
