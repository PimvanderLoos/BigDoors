package net.minecraft.world.entity.npc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.IRegistry;

public class VillagerData {

    public static final int MIN_VILLAGER_LEVEL = 1;
    public static final int MAX_VILLAGER_LEVEL = 5;
    private static final int[] NEXT_LEVEL_XP_THRESHOLDS = new int[]{0, 10, 70, 150, 250};
    public static final Codec<VillagerData> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(IRegistry.VILLAGER_TYPE.fieldOf("type").orElseGet(() -> {
            return VillagerType.PLAINS;
        }).forGetter((villagerdata) -> {
            return villagerdata.type;
        }), IRegistry.VILLAGER_PROFESSION.fieldOf("profession").orElseGet(() -> {
            return VillagerProfession.NONE;
        }).forGetter((villagerdata) -> {
            return villagerdata.profession;
        }), Codec.INT.fieldOf("level").orElse(1).forGetter((villagerdata) -> {
            return villagerdata.level;
        })).apply(instance, VillagerData::new);
    });
    private final VillagerType type;
    private final VillagerProfession profession;
    private final int level;

    public VillagerData(VillagerType villagertype, VillagerProfession villagerprofession, int i) {
        this.type = villagertype;
        this.profession = villagerprofession;
        this.level = Math.max(1, i);
    }

    public VillagerType getType() {
        return this.type;
    }

    public VillagerProfession getProfession() {
        return this.profession;
    }

    public int getLevel() {
        return this.level;
    }

    public VillagerData withType(VillagerType villagertype) {
        return new VillagerData(villagertype, this.profession, this.level);
    }

    public VillagerData withProfession(VillagerProfession villagerprofession) {
        return new VillagerData(this.type, villagerprofession, this.level);
    }

    public VillagerData withLevel(int i) {
        return new VillagerData(this.type, this.profession, i);
    }

    public static int b(int i) {
        return d(i) ? VillagerData.NEXT_LEVEL_XP_THRESHOLDS[i - 1] : 0;
    }

    public static int c(int i) {
        return d(i) ? VillagerData.NEXT_LEVEL_XP_THRESHOLDS[i] : 0;
    }

    public static boolean d(int i) {
        return i >= 1 && i < 5;
    }
}
