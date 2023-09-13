package net.minecraft.advancements;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.network.PacketDataSerializer;

public class CriterionProgress {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.ROOT);
    @Nullable
    private Date obtained;

    public CriterionProgress() {}

    public boolean isDone() {
        return this.obtained != null;
    }

    public void grant() {
        this.obtained = new Date();
    }

    public void revoke() {
        this.obtained = null;
    }

    @Nullable
    public Date getObtained() {
        return this.obtained;
    }

    public String toString() {
        return "CriterionProgress{obtained=" + (this.obtained == null ? "false" : this.obtained) + "}";
    }

    public void serializeToNetwork(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeNullable(this.obtained, PacketDataSerializer::writeDate);
    }

    public JsonElement serializeToJson() {
        return (JsonElement) (this.obtained != null ? new JsonPrimitive(CriterionProgress.DATE_FORMAT.format(this.obtained)) : JsonNull.INSTANCE);
    }

    public static CriterionProgress fromNetwork(PacketDataSerializer packetdataserializer) {
        CriterionProgress criterionprogress = new CriterionProgress();

        criterionprogress.obtained = (Date) packetdataserializer.readNullable(PacketDataSerializer::readDate);
        return criterionprogress;
    }

    public static CriterionProgress fromJson(String s) {
        CriterionProgress criterionprogress = new CriterionProgress();

        try {
            criterionprogress.obtained = CriterionProgress.DATE_FORMAT.parse(s);
            return criterionprogress;
        } catch (ParseException parseexception) {
            throw new JsonSyntaxException("Invalid datetime: " + s, parseexception);
        }
    }
}
