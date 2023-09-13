package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.SystemUtils;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;

public class MultifaceGrowthConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<MultifaceGrowthConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").flatXmap(MultifaceGrowthConfiguration::apply, DataResult::success).orElse((MultifaceBlock) Blocks.GLOW_LICHEN).forGetter((multifacegrowthconfiguration) -> {
            return multifacegrowthconfiguration.placeBlock;
        }), Codec.intRange(1, 64).fieldOf("search_range").orElse(10).forGetter((multifacegrowthconfiguration) -> {
            return multifacegrowthconfiguration.searchRange;
        }), Codec.BOOL.fieldOf("can_place_on_floor").orElse(false).forGetter((multifacegrowthconfiguration) -> {
            return multifacegrowthconfiguration.canPlaceOnFloor;
        }), Codec.BOOL.fieldOf("can_place_on_ceiling").orElse(false).forGetter((multifacegrowthconfiguration) -> {
            return multifacegrowthconfiguration.canPlaceOnCeiling;
        }), Codec.BOOL.fieldOf("can_place_on_wall").orElse(false).forGetter((multifacegrowthconfiguration) -> {
            return multifacegrowthconfiguration.canPlaceOnWall;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_spreading").orElse(0.5F).forGetter((multifacegrowthconfiguration) -> {
            return multifacegrowthconfiguration.chanceOfSpreading;
        }), RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("can_be_placed_on").forGetter((multifacegrowthconfiguration) -> {
            return multifacegrowthconfiguration.canBePlacedOn;
        })).apply(instance, MultifaceGrowthConfiguration::new);
    });
    public final MultifaceBlock placeBlock;
    public final int searchRange;
    public final boolean canPlaceOnFloor;
    public final boolean canPlaceOnCeiling;
    public final boolean canPlaceOnWall;
    public final float chanceOfSpreading;
    public final HolderSet<Block> canBePlacedOn;
    private final ObjectArrayList<EnumDirection> validDirections;

    private static DataResult<MultifaceBlock> apply(Block block) {
        DataResult dataresult;

        if (block instanceof MultifaceBlock) {
            MultifaceBlock multifaceblock = (MultifaceBlock) block;

            dataresult = DataResult.success(multifaceblock);
        } else {
            dataresult = DataResult.error(() -> {
                return "Growth block should be a multiface block";
            });
        }

        return dataresult;
    }

    public MultifaceGrowthConfiguration(MultifaceBlock multifaceblock, int i, boolean flag, boolean flag1, boolean flag2, float f, HolderSet<Block> holderset) {
        this.placeBlock = multifaceblock;
        this.searchRange = i;
        this.canPlaceOnFloor = flag;
        this.canPlaceOnCeiling = flag1;
        this.canPlaceOnWall = flag2;
        this.chanceOfSpreading = f;
        this.canBePlacedOn = holderset;
        this.validDirections = new ObjectArrayList(6);
        if (flag1) {
            this.validDirections.add(EnumDirection.UP);
        }

        if (flag) {
            this.validDirections.add(EnumDirection.DOWN);
        }

        if (flag2) {
            EnumDirection.EnumDirectionLimit enumdirection_enumdirectionlimit = EnumDirection.EnumDirectionLimit.HORIZONTAL;
            ObjectArrayList objectarraylist = this.validDirections;

            Objects.requireNonNull(this.validDirections);
            enumdirection_enumdirectionlimit.forEach(objectarraylist::add);
        }

    }

    public List<EnumDirection> getShuffledDirectionsExcept(RandomSource randomsource, EnumDirection enumdirection) {
        return SystemUtils.toShuffledList(this.validDirections.stream().filter((enumdirection1) -> {
            return enumdirection1 != enumdirection;
        }), randomsource);
    }

    public List<EnumDirection> getShuffledDirections(RandomSource randomsource) {
        return SystemUtils.shuffledCopy(this.validDirections, randomsource);
    }
}
