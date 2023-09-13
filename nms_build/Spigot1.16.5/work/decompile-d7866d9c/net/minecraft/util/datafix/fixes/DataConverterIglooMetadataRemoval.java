package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;

public class DataConverterIglooMetadataRemoval extends DataFix {

    public DataConverterIglooMetadataRemoval(Schema schema, boolean flag) {
        super(schema, flag);
    }

    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(DataConverterTypes.STRUCTURE_FEATURE);
        Type<?> type1 = this.getOutputSchema().getType(DataConverterTypes.STRUCTURE_FEATURE);

        return this.writeFixAndRead("IglooMetadataRemovalFix", type, type1, DataConverterIglooMetadataRemoval::a);
    }

    private static <T> Dynamic<T> a(Dynamic<T> dynamic) {
        boolean flag = (Boolean) dynamic.get("Children").asStreamOpt().map((stream) -> {
            return stream.allMatch(DataConverterIglooMetadataRemoval::c);
        }).result().orElse(false);

        return flag ? dynamic.set("id", dynamic.createString("Igloo")).remove("Children") : dynamic.update("Children", DataConverterIglooMetadataRemoval::b);
    }

    private static <T> Dynamic<T> b(Dynamic<T> dynamic) {
        DataResult dataresult = dynamic.asStreamOpt().map((stream) -> {
            return stream.filter((dynamic1) -> {
                return !c(dynamic1);
            });
        });

        dynamic.getClass();
        return (Dynamic) dataresult.map(dynamic::createList).result().orElse(dynamic);
    }

    private static boolean c(Dynamic<?> dynamic) {
        return dynamic.get("id").asString("").equals("Iglu");
    }
}
