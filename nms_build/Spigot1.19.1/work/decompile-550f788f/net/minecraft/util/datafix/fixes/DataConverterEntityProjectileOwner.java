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

        return this.fixTypeEverywhereTyped("EntityProjectileOwner", schema.getType(DataConverterTypes.ENTITY), this::updateProjectiles);
    }

    private Typed<?> updateProjectiles(Typed<?> typed) {
        typed = this.updateEntity(typed, "minecraft:egg", this::updateOwnerThrowable);
        typed = this.updateEntity(typed, "minecraft:ender_pearl", this::updateOwnerThrowable);
        typed = this.updateEntity(typed, "minecraft:experience_bottle", this::updateOwnerThrowable);
        typed = this.updateEntity(typed, "minecraft:snowball", this::updateOwnerThrowable);
        typed = this.updateEntity(typed, "minecraft:potion", this::updateOwnerThrowable);
        typed = this.updateEntity(typed, "minecraft:potion", this::updateItemPotion);
        typed = this.updateEntity(typed, "minecraft:llama_spit", this::updateOwnerLlamaSpit);
        typed = this.updateEntity(typed, "minecraft:arrow", this::updateOwnerArrow);
        typed = this.updateEntity(typed, "minecraft:spectral_arrow", this::updateOwnerArrow);
        typed = this.updateEntity(typed, "minecraft:trident", this::updateOwnerArrow);
        return typed;
    }

    private Dynamic<?> updateOwnerArrow(Dynamic<?> dynamic) {
        long i = dynamic.get("OwnerUUIDMost").asLong(0L);
        long j = dynamic.get("OwnerUUIDLeast").asLong(0L);

        return this.setUUID(dynamic, i, j).remove("OwnerUUIDMost").remove("OwnerUUIDLeast");
    }

    private Dynamic<?> updateOwnerLlamaSpit(Dynamic<?> dynamic) {
        OptionalDynamic<?> optionaldynamic = dynamic.get("Owner");
        long i = optionaldynamic.get("OwnerUUIDMost").asLong(0L);
        long j = optionaldynamic.get("OwnerUUIDLeast").asLong(0L);

        return this.setUUID(dynamic, i, j).remove("Owner");
    }

    private Dynamic<?> updateItemPotion(Dynamic<?> dynamic) {
        OptionalDynamic<?> optionaldynamic = dynamic.get("Potion");

        return dynamic.set("Item", optionaldynamic.orElseEmptyMap()).remove("Potion");
    }

    private Dynamic<?> updateOwnerThrowable(Dynamic<?> dynamic) {
        String s = "owner";
        OptionalDynamic<?> optionaldynamic = dynamic.get("owner");
        long i = optionaldynamic.get("M").asLong(0L);
        long j = optionaldynamic.get("L").asLong(0L);

        return this.setUUID(dynamic, i, j).remove("owner");
    }

    private Dynamic<?> setUUID(Dynamic<?> dynamic, long i, long j) {
        String s = "OwnerUUID";

        return i != 0L && j != 0L ? dynamic.set("OwnerUUID", dynamic.createIntList(Arrays.stream(createUUIDArray(i, j)))) : dynamic;
    }

    private static int[] createUUIDArray(long i, long j) {
        return new int[]{(int) (i >> 32), (int) i, (int) (j >> 32), (int) j};
    }

    private Typed<?> updateEntity(Typed<?> typed, String s, Function<Dynamic<?>, Dynamic<?>> function) {
        Type<?> type = this.getInputSchema().getChoiceType(DataConverterTypes.ENTITY, s);
        Type<?> type1 = this.getOutputSchema().getChoiceType(DataConverterTypes.ENTITY, s);

        return typed.updateTyped(DSL.namedChoice(s, type), type1, (typed1) -> {
            return typed1.update(DSL.remainderFinder(), function);
        });
    }
}
