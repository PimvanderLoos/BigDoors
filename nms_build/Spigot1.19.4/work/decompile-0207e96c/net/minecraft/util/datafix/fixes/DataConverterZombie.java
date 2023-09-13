package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.RandomSource;

public class DataConverterZombie extends DataConverterNamedEntity {

    private static final int PROFESSION_MAX = 6;
    private static final RandomSource RANDOM = RandomSource.create();

    public DataConverterZombie(Schema schema, boolean flag) {
        super(schema, flag, "EntityZombieVillagerTypeFix", DataConverterTypes.ENTITY, "Zombie");
    }

    public Dynamic<?> fixTag(Dynamic<?> dynamic) {
        if (dynamic.get("IsVillager").asBoolean(false)) {
            if (!dynamic.get("ZombieType").result().isPresent()) {
                int i = this.getVillagerProfession(dynamic.get("VillagerProfession").asInt(-1));

                if (i == -1) {
                    i = this.getVillagerProfession(DataConverterZombie.RANDOM.nextInt(6));
                }

                dynamic = dynamic.set("ZombieType", dynamic.createInt(i));
            }

            dynamic = dynamic.remove("IsVillager");
        }

        return dynamic;
    }

    private int getVillagerProfession(int i) {
        return i >= 0 && i < 6 ? i : -1;
    }

    @Override
    protected Typed<?> fix(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), this::fixTag);
    }
}
