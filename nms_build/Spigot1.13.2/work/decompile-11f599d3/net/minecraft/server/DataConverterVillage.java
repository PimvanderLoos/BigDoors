package net.minecraft.server;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;
import java.util.stream.Stream;

public class DataConverterVillage extends DataFix {

    public DataConverterVillage(Schema schema, boolean flag) {
        super(schema, flag);
    }

    public TypeRewriteRule makeRule() {
        return this.writeFixAndRead("SavedDataVillageCropFix", this.getInputSchema().getType(DataConverterTypes.s), this.getOutputSchema().getType(DataConverterTypes.s), this::a);
    }

    private <T> Dynamic<T> a(Dynamic<T> dynamic) {
        return dynamic.update("Children", DataConverterVillage::b);
    }

    private static <T> Dynamic<T> b(Dynamic<T> dynamic) {
        Optional optional = dynamic.getStream().map(DataConverterVillage::a);

        dynamic.getClass();
        return (Dynamic) optional.map(dynamic::createList).orElse(dynamic);
    }

    private static Stream<? extends Dynamic<?>> a(Stream<? extends Dynamic<?>> stream) {
        return stream.map((dynamic) -> {
            String s = dynamic.getString("id");

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
        return dynamic.get(s).flatMap(Dynamic::getNumberValue).isPresent() ? dynamic.set(s, DataConverterFlattenData.b(dynamic.getInt(s) << 4)) : dynamic;
    }
}
