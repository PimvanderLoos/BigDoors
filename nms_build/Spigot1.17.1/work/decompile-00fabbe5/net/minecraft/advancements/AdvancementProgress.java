package net.minecraft.advancements;

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
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.util.ChatDeserializer;

public class AdvancementProgress implements Comparable<AdvancementProgress> {

    final Map<String, CriterionProgress> criteria;
    private String[][] requirements = new String[0][];

    private AdvancementProgress(Map<String, CriterionProgress> map) {
        this.criteria = map;
    }

    public AdvancementProgress() {
        this.criteria = Maps.newHashMap();
    }

    public void a(Map<String, Criterion> map, String[][] astring) {
        Set<String> set = map.keySet();

        this.criteria.entrySet().removeIf((entry) -> {
            return !set.contains(entry.getKey());
        });
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            if (!this.criteria.containsKey(s)) {
                this.criteria.put(s, new CriterionProgress());
            }
        }

        this.requirements = astring;
    }

    public boolean isDone() {
        if (this.requirements.length == 0) {
            return false;
        } else {
            String[][] astring = this.requirements;
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
        Iterator iterator = this.criteria.values().iterator();

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
        CriterionProgress criterionprogress = (CriterionProgress) this.criteria.get(s);

        if (criterionprogress != null && !criterionprogress.a()) {
            criterionprogress.b();
            return true;
        } else {
            return false;
        }
    }

    public boolean b(String s) {
        CriterionProgress criterionprogress = (CriterionProgress) this.criteria.get(s);

        if (criterionprogress != null && criterionprogress.a()) {
            criterionprogress.c();
            return true;
        } else {
            return false;
        }
    }

    public String toString() {
        return "AdvancementProgress{criteria=" + this.criteria + ", requirements=" + Arrays.deepToString(this.requirements) + "}";
    }

    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.criteria, PacketDataSerializer::a, (packetdataserializer1, criterionprogress) -> {
            criterionprogress.a(packetdataserializer1);
        });
    }

    public static AdvancementProgress b(PacketDataSerializer packetdataserializer) {
        Map<String, CriterionProgress> map = packetdataserializer.a(PacketDataSerializer::p, CriterionProgress::b);

        return new AdvancementProgress(map);
    }

    @Nullable
    public CriterionProgress getCriterionProgress(String s) {
        return (CriterionProgress) this.criteria.get(s);
    }

    public float c() {
        if (this.criteria.isEmpty()) {
            return 0.0F;
        } else {
            float f = (float) this.requirements.length;
            float f1 = (float) this.h();

            return f1 / f;
        }
    }

    @Nullable
    public String d() {
        if (this.criteria.isEmpty()) {
            return null;
        } else {
            int i = this.requirements.length;

            if (i <= 1) {
                return null;
            } else {
                int j = this.h();

                return j + "/" + i;
            }
        }
    }

    private int h() {
        int i = 0;
        String[][] astring = this.requirements;
        int j = astring.length;
        int k = 0;

        while (k < j) {
            String[] astring1 = astring[k];
            boolean flag = false;
            String[] astring2 = astring1;
            int l = astring1.length;
            int i1 = 0;

            while (true) {
                if (i1 < l) {
                    String s = astring2[i1];
                    CriterionProgress criterionprogress = this.getCriterionProgress(s);

                    if (criterionprogress == null || !criterionprogress.a()) {
                        ++i1;
                        continue;
                    }

                    flag = true;
                }

                if (flag) {
                    ++i;
                }

                ++k;
                break;
            }
        }

        return i;
    }

    public Iterable<String> getRemainingCriteria() {
        List<String> list = Lists.newArrayList();
        Iterator iterator = this.criteria.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<String, CriterionProgress> entry = (Entry) iterator.next();

            if (!((CriterionProgress) entry.getValue()).a()) {
                list.add((String) entry.getKey());
            }
        }

        return list;
    }

    public Iterable<String> getAwardedCriteria() {
        List<String> list = Lists.newArrayList();
        Iterator iterator = this.criteria.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<String, CriterionProgress> entry = (Entry) iterator.next();

            if (((CriterionProgress) entry.getValue()).a()) {
                list.add((String) entry.getKey());
            }
        }

        return list;
    }

    @Nullable
    public Date g() {
        Date date = null;
        Iterator iterator = this.criteria.values().iterator();

        while (iterator.hasNext()) {
            CriterionProgress criterionprogress = (CriterionProgress) iterator.next();

            if (criterionprogress.a() && (date == null || criterionprogress.getDate().before(date))) {
                date = criterionprogress.getDate();
            }
        }

        return date;
    }

    public int compareTo(AdvancementProgress advancementprogress) {
        Date date = this.g();
        Date date1 = advancementprogress.g();

        return date == null && date1 != null ? 1 : (date != null && date1 == null ? -1 : (date == null && date1 == null ? 0 : date.compareTo(date1)));
    }

    public static class a implements JsonDeserializer<AdvancementProgress>, JsonSerializer<AdvancementProgress> {

        public a() {}

        public JsonElement serialize(AdvancementProgress advancementprogress, Type type, JsonSerializationContext jsonserializationcontext) {
            JsonObject jsonobject = new JsonObject();
            JsonObject jsonobject1 = new JsonObject();
            Iterator iterator = advancementprogress.criteria.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<String, CriterionProgress> entry = (Entry) iterator.next();
                CriterionProgress criterionprogress = (CriterionProgress) entry.getValue();

                if (criterionprogress.a()) {
                    jsonobject1.add((String) entry.getKey(), criterionprogress.e());
                }
            }

            if (!jsonobject1.entrySet().isEmpty()) {
                jsonobject.add("criteria", jsonobject1);
            }

            jsonobject.addProperty("done", advancementprogress.isDone());
            return jsonobject;
        }

        public AdvancementProgress deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "advancement");
            JsonObject jsonobject1 = ChatDeserializer.a(jsonobject, "criteria", new JsonObject());
            AdvancementProgress advancementprogress = new AdvancementProgress();
            Iterator iterator = jsonobject1.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<String, JsonElement> entry = (Entry) iterator.next();
                String s = (String) entry.getKey();

                advancementprogress.criteria.put(s, CriterionProgress.a(ChatDeserializer.a((JsonElement) entry.getValue(), s)));
            }

            return advancementprogress;
        }
    }
}
