package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;

public class AdvancementProgress implements Comparable<AdvancementProgress> {

    private final Map<String, CriterionProgress> a = Maps.newHashMap();
    private String[][] b = new String[0][];

    public AdvancementProgress() {}

    public void a(Map<String, Criterion> map, String[][] astring) {
        Set set = map.keySet();
        Iterator iterator = this.a.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            if (!set.contains(entry.getKey())) {
                iterator.remove();
            }
        }

        iterator = set.iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            if (!this.a.containsKey(s)) {
                this.a.put(s, new CriterionProgress(this));
            }
        }

        this.b = astring;
    }

    public boolean isDone() {
        if (this.b.length == 0) {
            return false;
        } else {
            String[][] astring = this.b;
            int i = astring.length;
            int j = 0;

            while (j < i) {
                String[] astring1 = astring[j];
                boolean flag = false;
                String[] astring2 = astring1;
                int k = astring1.length;
                int l = 0;

                while (true) {
                    if (l < k) {
                        String s = astring2[l];
                        CriterionProgress criterionprogress = this.getCriterionProgress(s);

                        if (criterionprogress == null || !criterionprogress.a()) {
                            ++l;
                            continue;
                        }

                        flag = true;
                    }

                    if (!flag) {
                        return false;
                    }

                    ++j;
                    break;
                }
            }

            return true;
        }
    }

    public boolean b() {
        Iterator iterator = this.a.values().iterator();

        CriterionProgress criterionprogress;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            criterionprogress = (CriterionProgress) iterator.next();
        } while (!criterionprogress.a());

        return true;
    }

    public boolean a(String s) {
        CriterionProgress criterionprogress = (CriterionProgress) this.a.get(s);

        if (criterionprogress != null && !criterionprogress.a()) {
            criterionprogress.b();
            return true;
        } else {
            return false;
        }
    }

    public boolean b(String s) {
        CriterionProgress criterionprogress = (CriterionProgress) this.a.get(s);

        if (criterionprogress != null && criterionprogress.a()) {
            criterionprogress.c();
            return true;
        } else {
            return false;
        }
    }

    public String toString() {
        return "AdvancementProgress{criteria=" + this.a + ", requirements=" + Arrays.deepToString(this.b) + '}';
    }

    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.a.size());
        Iterator iterator = this.a.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            packetdataserializer.a((String) entry.getKey());
            ((CriterionProgress) entry.getValue()).a(packetdataserializer);
        }

    }

    public static AdvancementProgress b(PacketDataSerializer packetdataserializer) {
        AdvancementProgress advancementprogress = new AdvancementProgress();
        int i = packetdataserializer.g();

        for (int j = 0; j < i; ++j) {
            advancementprogress.a.put(packetdataserializer.e(32767), CriterionProgress.a(packetdataserializer, advancementprogress));
        }

        return advancementprogress;
    }

    @Nullable
    public CriterionProgress getCriterionProgress(String s) {
        return (CriterionProgress) this.a.get(s);
    }

    public Iterable<String> getRemainingCriteria() {
        ArrayList arraylist = Lists.newArrayList();
        Iterator iterator = this.a.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            if (!((CriterionProgress) entry.getValue()).a()) {
                arraylist.add(entry.getKey());
            }
        }

        return arraylist;
    }

    public Iterable<String> getAwardedCriteria() {
        ArrayList arraylist = Lists.newArrayList();
        Iterator iterator = this.a.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            if (((CriterionProgress) entry.getValue()).a()) {
                arraylist.add(entry.getKey());
            }
        }

        return arraylist;
    }

    @Nullable
    public Date g() {
        Date date = null;
        Iterator iterator = this.a.values().iterator();

        while (iterator.hasNext()) {
            CriterionProgress criterionprogress = (CriterionProgress) iterator.next();

            if (criterionprogress.a() && (date == null || criterionprogress.getDate().before(date))) {
                date = criterionprogress.getDate();
            }
        }

        return date;
    }

    public int a(AdvancementProgress advancementprogress) {
        Date date = this.g();
        Date date1 = advancementprogress.g();

        return date == null && date1 != null ? 1 : (date != null && date1 == null ? -1 : (date == null && date1 == null ? 0 : date.compareTo(date1)));
    }

    public int compareTo(Object object) {
        return this.a((AdvancementProgress) object);
    }

    public static class a implements JsonDeserializer<AdvancementProgress>, JsonSerializer<AdvancementProgress> {

        public a() {}

        public JsonElement a(AdvancementProgress advancementprogress, Type type, JsonSerializationContext jsonserializationcontext) {
            JsonObject jsonobject = new JsonObject();
            JsonObject jsonobject1 = new JsonObject();
            Iterator iterator = advancementprogress.a.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                CriterionProgress criterionprogress = (CriterionProgress) entry.getValue();

                if (criterionprogress.a()) {
                    jsonobject1.add((String) entry.getKey(), criterionprogress.e());
                }
            }

            if (!jsonobject1.entrySet().isEmpty()) {
                jsonobject.add("criteria", jsonobject1);
            }

            jsonobject.addProperty("done", Boolean.valueOf(advancementprogress.isDone()));
            return jsonobject;
        }

        public AdvancementProgress a(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "advancement");
            JsonObject jsonobject1 = ChatDeserializer.a(jsonobject, "criteria", new JsonObject());
            AdvancementProgress advancementprogress = new AdvancementProgress();
            Iterator iterator = jsonobject1.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                String s = (String) entry.getKey();

                advancementprogress.a.put(s, CriterionProgress.a(advancementprogress, ChatDeserializer.a((JsonElement) entry.getValue(), s)));
            }

            return advancementprogress;
        }

        public Object deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            return this.a(jsonelement, type, jsondeserializationcontext);
        }

        public JsonElement serialize(Object object, Type type, JsonSerializationContext jsonserializationcontext) {
            return this.a((AdvancementProgress) object, type, jsonserializationcontext);
        }
    }
}
