package net.minecraft.world.level.levelgen.structure.placement;

import com.mojang.serialization.Codec;
import net.minecraft.util.INamable;
import net.minecraft.world.level.levelgen.RandomSource;

public enum RandomSpreadType implements INamable {

    LINEAR("linear"), TRIANGULAR("triangular");

    private static final RandomSpreadType[] VALUES = values();
    public static final Codec<RandomSpreadType> CODEC = INamable.fromEnum(() -> {
        return RandomSpreadType.VALUES;
    }, RandomSpreadType::byName);
    private final String id;

    private RandomSpreadType(String s) {
        this.id = s;
    }

    public static RandomSpreadType byName(String s) {
        RandomSpreadType[] arandomspreadtype = RandomSpreadType.VALUES;
        int i = arandomspreadtype.length;

        for (int j = 0; j < i; ++j) {
            RandomSpreadType randomspreadtype = arandomspreadtype[j];

            if (randomspreadtype.getSerializedName().equals(s)) {
                return randomspreadtype;
            }
        }

        throw new IllegalArgumentException("Unknown Random Spread type: " + s);
    }

    @Override
    public String getSerializedName() {
        return this.id;
    }

    public int evaluate(RandomSource randomsource, int i) {
        int j;

        switch (this) {
            case LINEAR:
                j = randomsource.nextInt(i);
                break;
            case TRIANGULAR:
                j = (randomsource.nextInt(i) + randomsource.nextInt(i)) / 2;
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        return j;
    }
}
