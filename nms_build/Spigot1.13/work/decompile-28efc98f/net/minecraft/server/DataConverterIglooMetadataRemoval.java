package net.minecraft.server;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DataConverterIglooMetadataRemoval extends DataFix {

    public DataConverterIglooMetadataRemoval(Schema schema, boolean flag) {
        super(schema, flag);
    }

    protected TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(DataConverterTypes.s);
        Type type1 = this.getOutputSchema().getType(DataConverterTypes.s);

        return this.writeFixAndRead("IglooMetadataRemovalFix", type, type1, DataConverterIglooMetadataRemoval::a);
    }

    private static <T> Dynamic<T> a(Dynamic<T> dynamic) {
        boolean flag = ((Boolean) dynamic.get("Children").flatMap(Dynamic::getStream).map((stream) -> {
            return Boolean.valueOf(stream.allMatch(DataConverterIglooMetadataRemoval::c));
        }).orElse(Boolean.valueOf(false))).booleanValue();

        return flag ? dynamic.set("id", dynamic.createString("Igloo")).remove("Children") : dynamic.update("Children", DataConverterIglooMetadataRemoval::b);
    }

    private static <T> Dynamic<T> b(Dynamic<T> dynamic) {
        Optional optional = dynamic.getStream().map((stream) -> {
            return stream.filter((dynamic) -> {
                return !c(dynamic);
            });
        });

        dynamic.getClass();
        return (Dynamic) optional.map(dynamic::createList).orElse(dynamic);
    }

    private static boolean c(Dynamic<?> dynamic) {
        return dynamic.getString("id").equals("Iglu");
    }
}
