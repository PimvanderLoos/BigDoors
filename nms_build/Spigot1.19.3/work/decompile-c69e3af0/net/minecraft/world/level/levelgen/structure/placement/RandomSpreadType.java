package net.minecraft.world.level.levelgen.structure.placement;

import com.mojang.serialization.Codec;
import net.minecraft.util.INamable;
import net.minecraft.util.RandomSource;

public enum RandomSpreadType implements INamable {

    LINEAR("linear"), TRIANGULAR("triangular");

    public static final Codec<RandomSpreadType> CODEC = INamable.fromEnum(RandomSpreadType::values);
    private final String id;

    private RandomSpreadType(String s) {
        this.id = s;
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
