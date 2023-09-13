package net.minecraft.advancements;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.minecraft.network.PacketDataSerializer;

public class CriterionProgress {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    private Date obtained;

    public CriterionProgress() {}

    public boolean a() {
        return this.obtained != null;
    }

    public void b() {
        this.obtained = new Date();
    }

    public void c() {
        this.obtained = null;
    }

    public Date getDate() {
        return this.obtained;
    }

    public String toString() {
        return "CriterionProgress{obtained=" + (this.obtained == null ? "false" : this.obtained) + "}";
    }

    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeBoolean(this.obtained != null);
        if (this.obtained != null) {
            packetdataserializer.a(this.obtained);
        }

    }

    public JsonElement e() {
        return (JsonElement) (this.obtained != null ? new JsonPrimitive(CriterionProgress.DATE_FORMAT.format(this.obtained)) : JsonNull.INSTANCE);
    }

    public static CriterionProgress b(PacketDataSerializer packetdataserializer) {
        CriterionProgress criterionprogress = new CriterionProgress();

        if (packetdataserializer.readBoolean()) {
            criterionprogress.obtained = packetdataserializer.r();
        }

        return criterionprogress;
    }

    public static CriterionProgress a(String s) {
        CriterionProgress criterionprogress = new CriterionProgress();

        try {
            criterionprogress.obtained = CriterionProgress.DATE_FORMAT.parse(s);
            return criterionprogress;
        } catch (ParseException parseexception) {
            throw new JsonSyntaxException("Invalid datetime: " + s, parseexception);
        }
    }
}
