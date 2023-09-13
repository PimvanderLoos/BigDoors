package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.stream.Stream;

public class DataConverterVillage extends DataFix {

    public DataConverterVillage(Schema schema, boolean flag) {
        super(schema, flag);
    }

    public TypeRewriteRule makeRule() {
        return this.writeFixAndRead("SavedDataVillageCropFix", this.getInputSchema().getType(DataConverterTypes.STRUCTURE_FEATURE), this.getOutputSchema().getType(DataConverterTypes.STRUCTURE_FEATURE), this::a);
    }

    private <T> Dynamic<T> a(Dynamic<T> dynamic) {
        return dynamic.update("Children", DataConverterVillage::b);
    }

    private static <T> Dynamic<T> b(Dynamic<T> dynamic) {
        DataResult dataresult = dynamic.asStreamOpt().map(DataConverterVillage::a);

        dynamic.getClass();
        return (Dynamic) dataresult.map(dynamic::createList).result().orElse(dynamic);
    }

    private static Stream<? extends Dynamic<?>> a(Stream<? extends Dynamic<?>> stream) {
        return stream.map((dynamic) -> {
            String s = dynamic.get("id").asString("");

            return "ViF".equals(s) ? c(dynamic) : ("ViDF".equals(s) ? d(dynamic) : dynamic);
        });
    }

    private static <T> Dynamic<T> c(Dynamic<T> dynamic) {
        dynamic = a(dynamic, "CA");
        return a(dynamic, "CB");
    }

    private static <T> Dynamic<T> d(Dynamic<T> dynamic) {
        dynamic = a(dynamic, "CA");
        dynamic = a(dynamic, "CB");
        dynamic = a(dynamic, "CC");
        return a(dynamic, "CD");
    }

    private static <T> Dynamic<T> a(Dynamic<T> dynamic, String s) {
        return dynamic.get(s).asNumber().result().isPresent() ? dynamic.set(s, DataConverterFlattenData.b(dynamic.get(s).asInt(0) << 4)) : dynamic;
    }
}
