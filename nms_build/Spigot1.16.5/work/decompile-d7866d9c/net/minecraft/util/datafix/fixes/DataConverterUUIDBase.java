package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class DataConverterUUIDBase extends DataFix {

    protected static final Logger LOGGER = LogManager.getLogger();
    protected TypeReference b;

    public DataConverterUUIDBase(Schema schema, TypeReference typereference) {
        super(schema, false);
        this.b = typereference;
    }

    protected Typed<?> a(Typed<?> typed, String s, Function<Dynamic<?>, Dynamic<?>> function) {
        Type<?> type = this.getInputSchema().getChoiceType(this.b, s);
        Type<?> type1 = this.getOutputSchema().getChoiceType(this.b, s);

        return typed.updateTyped(DSL.namedChoice(s, type), type1, (typed1) -> {
            return typed1.update(DSL.remainderFinder(), function);
        });
    }

    protected static Optional<Dynamic<?>> a(Dynamic<?> dynamic, String s, String s1) {
        return a(dynamic, s).map((dynamic1) -> {
            return dynamic.remove(s).set(s1, dynamic1);
        });
    }

    protected static Optional<Dynamic<?>> b(Dynamic<?> dynamic, String s, String s1) {
        return dynamic.get(s).result().flatMap(DataConverterUUIDBase::a).map((dynamic1) -> {
            return dynamic.remove(s).set(s1, dynamic1);
        });
    }

    protected static Optional<Dynamic<?>> c(Dynamic<?> dynamic, String s, String s1) {
        String s2 = s + "Most";
        String s3 = s + "Least";

        return d(dynamic, s2, s3).map((dynamic1) -> {
            return dynamic.remove(s2).remove(s3).set(s1, dynamic1);
        });
    }

    protected static Optional<Dynamic<?>> a(Dynamic<?> dynamic, String s) {
        return dynamic.get(s).result().flatMap((dynamic1) -> {
            String s1 = dynamic1.asString((String) null);

            if (s1 != null) {
                try {
                    UUID uuid = UUID.fromString(s1);

                    return a(dynamic, uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
                } catch (IllegalArgumentException illegalargumentexception) {
                    ;
                }
            }

            return Optional.empty();
        });
    }

    protected static Optional<Dynamic<?>> a(Dynamic<?> dynamic) {
        return d(dynamic, "M", "L");
    }

    protected static Optional<Dynamic<?>> d(Dynamic<?> dynamic, String s, String s1) {
        long i = dynamic.get(s).asLong(0L);
        long j = dynamic.get(s1).asLong(0L);

        return i != 0L && j != 0L ? a(dynamic, i, j) : Optional.empty();
    }

    protected static Optional<Dynamic<?>> a(Dynamic<?> dynamic, long i, long j) {
        return Optional.of(dynamic.createIntList(Arrays.stream(new int[]{(int) (i >> 32), (int) i, (int) (j >> 32), (int) j})));
    }
}
