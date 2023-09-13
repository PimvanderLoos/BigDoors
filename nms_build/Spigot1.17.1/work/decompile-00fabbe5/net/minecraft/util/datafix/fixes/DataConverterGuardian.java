package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;

public class DataConverterGuardian extends DataConverterEntityNameAbstract {

    public DataConverterGuardian(Schema schema, boolean flag) {
        super("EntityElderGuardianSplitFix", schema, flag);
    }

    @Override
    protected Pair<String, Dynamic<?>> a(String s, Dynamic<?> dynamic) {
        return Pair.of(Objects.equals(s, "Guardian") && dynamic.get("Elder").asBoolean(false) ? "ElderGuardian" : s, dynamic);
    }
}
