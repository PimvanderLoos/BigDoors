package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.Arrays;
import java.util.function.Function;

public class DataConverterEntityProjectileOwner extends DataFix {

    public DataConverterEntityProjectileOwner(Schema schema) {
        super(schema, false);
    }

    protected TypeRewriteRule makeRule() {
        Schema schema = this.getInputSchema();

        return this.fixTypeEverywhereTyped("EntityProjectileOwner", schema.getType(DataConverterTypes.ENTITY), this::a);
    }

    private Typed<?> a(Typed<?> typed) {
        typed = this.a(typed, "minecraft:egg", this::d);
        typed = this.a(typed, "minecraft:ender_pearl", this::d);
        typed = this.a(typed, "minecraft:experience_bottle", this::d);
        typed = this.a(typed, "minecraft:snowball", this::d);
        typed = this.a(typed, "minecraft:potion", this::d);
        typed = this.a(typed, "minecraft:potion", this::c);
        typed = this.a(typed, "minecraft:llama_spit", this::b);
        typed = this.a(typed, "minecraft:arrow", this::a);
        typed = this.a(typed, "minecraft:spectral_arrow", this::a);
        typed = this.a(typed, "minecraft:trident", this::a);
        return typed;
    }

    private Dynamic<?> a(Dynamic<?> dynamic) {
        long i = dynamic.get("OwnerUUIDMost").asLong(0L);
        long j = dynamic.get("OwnerUUIDLeast").asLong(0L);

        return this.a(dynamic, i, j).remove("OwnerUUIDMost").remove("OwnerUUIDLeast");
    }

    private Dynamic<?> b(Dynamic<?> dynamic) {
        OptionalDynamic<?> optionaldynamic = dynamic.get("Owner");
        long i = optionaldynamic.get("OwnerUUIDMost").asLong(0L);
        long j = optionaldynamic.get("OwnerUUIDLeast").asLong(0L);

        return this.a(dynamic, i, j).remove("Owner");
    }

    private Dynamic<?> c(Dynamic<?> dynamic) {
        OptionalDynamic<?> optionaldynamic = dynamic.get("Potion");

        return dynamic.set("Item", optionaldynamic.orElseEmptyMap()).remove("Potion");
    }

    private Dynamic<?> d(Dynamic<?> dynamic) {
        String s = "owner";
        OptionalDynamic<?> optionaldynamic = dynamic.get("owner");
        long i = optionaldynamic.get("M").asLong(0L);
        long j = optionaldynamic.get("L").asLong(0L);

        return this.a(dynamic, i, j).remove("owner");
    }

    private Dynamic<?> a(Dynamic<?> dynamic, long i, long j) {
        String s = "OwnerUUID";

        return i != 0L && j != 0L ? dynamic.set("OwnerUUID", dynamic.createIntList(Arrays.stream(a(i, j)))) : dynamic;
    }

    private static int[] a(long i, long j) {
        return new int[]{(int) (i >> 32), (int) i, (int) (j >> 32), (int) j};
    }

    private Typed<?> a(Typed<?> typed, String s, Function<Dynamic<?>, Dynamic<?>> function) {
        Type<?> type = this.getInputSchema().getChoiceType(DataConverterTypes.ENTITY, s);
        Type<?> type1 = this.getOutputSchema().getChoiceType(DataConverterTypes.ENTITY, s);

        return typed.updateTyped(DSL.namedChoice(s, type), type1, (typed1) -> {
            return typed1.update(DSL.remainderFinder(), function);
        });
    }
}
