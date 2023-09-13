package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public class PoiTypeRenameFix extends AbstractPoiSectionFix {

    private final Function<String, String> renamer;

    public PoiTypeRenameFix(Schema schema, String s, Function<String, String> function) {
        super(schema, s);
        this.renamer = function;
    }

    @Override
    protected <T> Stream<Dynamic<T>> processRecords(Stream<Dynamic<T>> stream) {
        return stream.map((dynamic) -> {
            return dynamic.update("type", (dynamic1) -> {
                DataResult dataresult = dynamic1.asString().map(this.renamer);

                Objects.requireNonNull(dynamic1);
                return (Dynamic) DataFixUtils.orElse(dataresult.map(dynamic1::createString).result(), dynamic1);
            });
        });
    }
}
