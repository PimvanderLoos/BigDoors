package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.schemas.DataConverterSchemaNamed;

public class DataConverterFlattenSpawnEgg extends DataFix {

    private final String itemType;
    private static final Map<String, String> MAP = (Map) DataFixUtils.make(Maps.newHashMap(), (hashmap) -> {
        hashmap.put("minecraft:bat", "minecraft:bat_spawn_egg");
        hashmap.put("minecraft:blaze", "minecraft:blaze_spawn_egg");
        hashmap.put("minecraft:cave_spider", "minecraft:cave_spider_spawn_egg");
        hashmap.put("minecraft:chicken", "minecraft:chicken_spawn_egg");
        hashmap.put("minecraft:cow", "minecraft:cow_spawn_egg");
        hashmap.put("minecraft:creeper", "minecraft:creeper_spawn_egg");
        hashmap.put("minecraft:donkey", "minecraft:donkey_spawn_egg");
        hashmap.put("minecraft:elder_guardian", "minecraft:elder_guardian_spawn_egg");
        hashmap.put("minecraft:ender_dragon", "minecraft:ender_dragon_spawn_egg");
        hashmap.put("minecraft:enderman", "minecraft:enderman_spawn_egg");
        hashmap.put("minecraft:endermite", "minecraft:endermite_spawn_egg");
        hashmap.put("minecraft:evocation_illager", "minecraft:evocation_illager_spawn_egg");
        hashmap.put("minecraft:ghast", "minecraft:ghast_spawn_egg");
        hashmap.put("minecraft:guardian", "minecraft:guardian_spawn_egg");
        hashmap.put("minecraft:horse", "minecraft:horse_spawn_egg");
        hashmap.put("minecraft:husk", "minecraft:husk_spawn_egg");
        hashmap.put("minecraft:iron_golem", "minecraft:iron_golem_spawn_egg");
        hashmap.put("minecraft:llama", "minecraft:llama_spawn_egg");
        hashmap.put("minecraft:magma_cube", "minecraft:magma_cube_spawn_egg");
        hashmap.put("minecraft:mooshroom", "minecraft:mooshroom_spawn_egg");
        hashmap.put("minecraft:mule", "minecraft:mule_spawn_egg");
        hashmap.put("minecraft:ocelot", "minecraft:ocelot_spawn_egg");
        hashmap.put("minecraft:pufferfish", "minecraft:pufferfish_spawn_egg");
        hashmap.put("minecraft:parrot", "minecraft:parrot_spawn_egg");
        hashmap.put("minecraft:pig", "minecraft:pig_spawn_egg");
        hashmap.put("minecraft:polar_bear", "minecraft:polar_bear_spawn_egg");
        hashmap.put("minecraft:rabbit", "minecraft:rabbit_spawn_egg");
        hashmap.put("minecraft:sheep", "minecraft:sheep_spawn_egg");
        hashmap.put("minecraft:shulker", "minecraft:shulker_spawn_egg");
        hashmap.put("minecraft:silverfish", "minecraft:silverfish_spawn_egg");
        hashmap.put("minecraft:skeleton", "minecraft:skeleton_spawn_egg");
        hashmap.put("minecraft:skeleton_horse", "minecraft:skeleton_horse_spawn_egg");
        hashmap.put("minecraft:slime", "minecraft:slime_spawn_egg");
        hashmap.put("minecraft:snow_golem", "minecraft:snow_golem_spawn_egg");
        hashmap.put("minecraft:spider", "minecraft:spider_spawn_egg");
        hashmap.put("minecraft:squid", "minecraft:squid_spawn_egg");
        hashmap.put("minecraft:stray", "minecraft:stray_spawn_egg");
        hashmap.put("minecraft:turtle", "minecraft:turtle_spawn_egg");
        hashmap.put("minecraft:vex", "minecraft:vex_spawn_egg");
        hashmap.put("minecraft:villager", "minecraft:villager_spawn_egg");
        hashmap.put("minecraft:vindication_illager", "minecraft:vindication_illager_spawn_egg");
        hashmap.put("minecraft:witch", "minecraft:witch_spawn_egg");
        hashmap.put("minecraft:wither", "minecraft:wither_spawn_egg");
        hashmap.put("minecraft:wither_skeleton", "minecraft:wither_skeleton_spawn_egg");
        hashmap.put("minecraft:wolf", "minecraft:wolf_spawn_egg");
        hashmap.put("minecraft:zombie", "minecraft:zombie_spawn_egg");
        hashmap.put("minecraft:zombie_horse", "minecraft:zombie_horse_spawn_egg");
        hashmap.put("minecraft:zombie_pigman", "minecraft:zombie_pigman_spawn_egg");
        hashmap.put("minecraft:zombie_villager", "minecraft:zombie_villager_spawn_egg");
    });

    public DataConverterFlattenSpawnEgg(Schema schema, boolean flag, String s) {
        super(schema, flag);
        this.itemType = s;
    }

    public TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(DataConverterTypes.ITEM_STACK);
        OpticFinder<Pair<String, String>> opticfinder = DSL.fieldFinder("id", DSL.named(DataConverterTypes.ITEM_NAME.typeName(), DataConverterSchemaNamed.namespacedString()));
        OpticFinder<String> opticfinder1 = DSL.fieldFinder("id", DataConverterSchemaNamed.namespacedString());
        OpticFinder<?> opticfinder2 = type.findField("tag");
        OpticFinder<?> opticfinder3 = opticfinder2.type().findField("EntityTag");

        return this.fixTypeEverywhereTyped("ItemInstanceSpawnEggFix" + this.getOutputSchema().getVersionKey(), type, (typed) -> {
            Optional<Pair<String, String>> optional = typed.getOptional(opticfinder);

            if (optional.isPresent() && Objects.equals(((Pair) optional.get()).getSecond(), this.itemType)) {
                Typed<?> typed1 = typed.getOrCreateTyped(opticfinder2);
                Typed<?> typed2 = typed1.getOrCreateTyped(opticfinder3);
                Optional<String> optional1 = typed2.getOptional(opticfinder1);

                if (optional1.isPresent()) {
                    return typed.set(opticfinder, Pair.of(DataConverterTypes.ITEM_NAME.typeName(), (String) DataConverterFlattenSpawnEgg.MAP.getOrDefault(optional1.get(), "minecraft:pig_spawn_egg")));
                }
            }

            return typed;
        });
    }
}
