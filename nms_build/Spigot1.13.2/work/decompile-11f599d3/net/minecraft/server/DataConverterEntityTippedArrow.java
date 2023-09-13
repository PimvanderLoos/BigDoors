package net.minecraft.server;

import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;

public class DataConverterEntityTippedArrow extends DataConverterEntityRenameAbstract {

    public DataConverterEntityTippedArrow(Schema schema, boolean flag) {
        super("EntityTippedArrowFix", schema, flag);
    }

    protected String a(String s) {
        return Objects.equals(s, "TippedArrow") ? "Arrow" : s;
    }
}
