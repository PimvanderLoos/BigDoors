package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;

public class DataConverterZombieVillagerLevelXp extends DataConverterNamedEntity {

    public DataConverterZombieVillagerLevelXp(Schema schema, boolean flag) {
        super(schema, flag, "Zombie Villager XP rebuild", DataConverterTypes.ENTITY, "minecraft:zombie_villager");
    }

    @Override
    protected Typed<?> fix(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), (dynamic) -> {
            Optional<Number> optional = dynamic.get("Xp").asNumber().result();

            if (!optional.isPresent()) {
                int i = dynamic.get("VillagerData").get("level").asInt(1);

                return dynamic.set("Xp", dynamic.createInt(DataConverterVillagerLevelXp.getMinXpPerLevel(i)));
            } else {
                return dynamic;
            }
        });
    }
}
