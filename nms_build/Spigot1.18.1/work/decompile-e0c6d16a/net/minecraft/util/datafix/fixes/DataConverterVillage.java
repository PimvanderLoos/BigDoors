package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.stream.Stream;

public class DataConverterVillage extends DataFix {

    public DataConverterVillage(Schema schema, boolean flag) {
        super(schema, flag);
    }

    public TypeRewriteRule makeRule() {
        return this.writeFixAndRead("SavedDataVillageCropFix", this.getInputSchema().getType(DataConverterTypes.STRUCTURE_FEATURE), this.getOutputSchema().getType(DataConverterTypes.STRUCTURE_FEATURE), this::fixTag);
    }

    private <T> Dynamic<T> fixTag(Dynamic<T> dynamic) {
        return dynamic.update("Children", DataConverterVillage::updateChildren);
    }

    private static <T> Dynamic<T> updateChildren(Dynamic<T> dynamic) {
        DataResult dataresult = dynamic.asStreamOpt().map(DataConverterVillage::updateChildren);

        Objects.requireNonNull(dynamic);
        return (Dynamic) dataresult.map(dynamic::createList).result().orElse(dynamic);
    }

    private static Stream<? extends Dynamic<?>> updateChildren(Stream<? extends Dynamic<?>> stream) {
        return stream.map((dynamic) -> {
            String s = dynamic.get("id").asString("");

            return "ViF".equals(s) ? updateSingleField(dynamic) : ("ViDF".equals(s) ? updateDoubleField(dynamic) : dynamic);
        });
    }

    private static <T> Dynamic<T> updateSingleField(Dynamic<T> dynamic) {
        dynamic = updateCrop(dynamic, "CA");
        return updateCrop(dynamic, "CB");
    }

    private static <T> Dynamic<T> updateDoubleField(Dynamic<T> dynamic) {
        dynamic = updateCrop(dynamic, "CA");
        dynamic = updateCrop(dynamic, "CB");
        dynamic = updateCrop(dynamic, "CC");
        return updateCrop(dynamic, "CD");
    }

    private static <T> Dynamic<T> updateCrop(Dynamic<T> dynamic, String s) {
        return dynamic.get(s).asNumber().result().isPresent() ? dynamic.set(s, DataConverterFlattenData.getTag(dynamic.get(s).asInt(0) << 4)) : dynamic;
    }
}
