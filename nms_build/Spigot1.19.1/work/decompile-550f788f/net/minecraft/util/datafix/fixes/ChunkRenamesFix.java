package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.function.Function;

public class ChunkRenamesFix extends DataFix {

    public ChunkRenamesFix(Schema schema) {
        super(schema, true);
    }

    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(DataConverterTypes.CHUNK);
        OpticFinder<?> opticfinder = type.findField("Level");
        OpticFinder<?> opticfinder1 = opticfinder.type().findField("Structures");
        Type<?> type1 = this.getOutputSchema().getType(DataConverterTypes.CHUNK);
        Type<?> type2 = type1.findFieldType("structures");

        return this.fixTypeEverywhereTyped("Chunk Renames; purge Level-tag", type, type1, (typed) -> {
            Typed<?> typed1 = typed.getTyped(opticfinder);
            Typed<?> typed2 = appendChunkName(typed1);

            typed2 = typed2.set(DSL.remainderFinder(), mergeRemainders(typed, (Dynamic) typed1.get(DSL.remainderFinder())));
            typed2 = renameField(typed2, "TileEntities", "block_entities");
            typed2 = renameField(typed2, "TileTicks", "block_ticks");
            typed2 = renameField(typed2, "Entities", "entities");
            typed2 = renameField(typed2, "Sections", "sections");
            typed2 = typed2.updateTyped(opticfinder1, type2, (typed3) -> {
                return renameField(typed3, "Starts", "starts");
            });
            typed2 = renameField(typed2, "Structures", "structures");
            return typed2.update(DSL.remainderFinder(), (dynamic) -> {
                return dynamic.remove("Level");
            });
        });
    }

    private static Typed<?> renameField(Typed<?> typed, String s, String s1) {
        return renameFieldHelper(typed, s, s1, typed.getType().findFieldType(s)).update(DSL.remainderFinder(), (dynamic) -> {
            return dynamic.remove(s);
        });
    }

    private static <A> Typed<?> renameFieldHelper(Typed<?> typed, String s, String s1, Type<A> type) {
        Type<Either<A, Unit>> type1 = DSL.optional(DSL.field(s, type));
        Type<Either<A, Unit>> type2 = DSL.optional(DSL.field(s1, type));

        return typed.update(type1.finder(), type2, Function.identity());
    }

    private static <A> Typed<Pair<String, A>> appendChunkName(Typed<A> typed) {
        return new Typed(DSL.named("chunk", typed.getType()), typed.getOps(), Pair.of("chunk", typed.getValue()));
    }

    private static <T> Dynamic<T> mergeRemainders(Typed<?> typed, Dynamic<T> dynamic) {
        DynamicOps<T> dynamicops = dynamic.getOps();
        Dynamic<T> dynamic1 = ((Dynamic) typed.get(DSL.remainderFinder())).convert(dynamicops);
        DataResult<T> dataresult = dynamicops.getMap(dynamic.getValue()).flatMap((maplike) -> {
            return dynamicops.mergeToMap(dynamic1.getValue(), maplike);
        });

        return (Dynamic) dataresult.result().map((object) -> {
            return new Dynamic(dynamicops, object);
        }).orElse(dynamic);
    }
}
