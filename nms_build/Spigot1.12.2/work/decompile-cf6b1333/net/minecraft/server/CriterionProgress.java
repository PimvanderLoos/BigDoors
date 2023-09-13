package net.minecraft.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CriterionProgress {

    private static final SimpleDateFormat a = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    private final AdvancementProgress b;
    private Date c;

    public CriterionProgress(AdvancementProgress advancementprogress) {
        this.b = advancementprogress;
    }

    public boolean a() {
        return this.c != null;
    }

    public void b() {
        this.c = new Date();
    }

    public void c() {
        this.c = null;
    }

    public Date getDate() {
        return this.c;
    }

    public String toString() {
        return "CriterionProgress{obtained=" + (this.c == null ? "false" : this.c) + '}';
    }

    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeBoolean(this.c != null);
        if (this.c != null) {
            packetdataserializer.a(this.c);
        }

    }

    public JsonElement e() {
        return (JsonElement) (this.c != null ? new JsonPrimitive(CriterionProgress.a.format(this.c)) : JsonNull.INSTANCE);
    }

    public static CriterionProgress a(PacketDataSerializer packetdataserializer, AdvancementProgress advancementprogress) {
        CriterionProgress criterionprogress = new CriterionProgress(advancementprogress);

        if (packetdataserializer.readBoolean()) {
            criterionprogress.c = packetdataserializer.m();
        }

        return criterionprogress;
    }

    public static CriterionProgress a(AdvancementProgress advancementprogress, String s) {
        CriterionProgress criterionprogress = new CriterionProgress(advancementprogress);

        try {
            criterionprogress.c = CriterionProgress.a.parse(s);
            return criterionprogress;
        } catch (ParseException parseexception) {
            throw new JsonSyntaxException("Invalid datetime: " + s, parseexception);
        }
    }
}
