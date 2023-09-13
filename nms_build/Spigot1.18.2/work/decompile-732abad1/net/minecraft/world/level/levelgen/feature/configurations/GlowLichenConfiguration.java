package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.world.level.block.Block;

public class GlowLichenConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<GlowLichenConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.intRange(1, 64).fieldOf("search_range").orElse(10).forGetter((glowlichenconfiguration) -> {
            return glowlichenconfiguration.searchRange;
        }), Codec.BOOL.fieldOf("can_place_on_floor").orElse(false).forGetter((glowlichenconfiguration) -> {
            return glowlichenconfiguration.canPlaceOnFloor;
        }), Codec.BOOL.fieldOf("can_place_on_ceiling").orElse(false).forGetter((glowlichenconfiguration) -> {
            return glowlichenconfiguration.canPlaceOnCeiling;
        }), Codec.BOOL.fieldOf("can_place_on_wall").orElse(false).forGetter((glowlichenconfiguration) -> {
            return glowlichenconfiguration.canPlaceOnWall;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_spreading").orElse(0.5F).forGetter((glowlichenconfiguration) -> {
            return glowlichenconfiguration.chanceOfSpreading;
        }), RegistryCodecs.homogeneousList(IRegistry.BLOCK_REGISTRY).fieldOf("can_be_placed_on").forGetter((glowlichenconfiguration) -> {
            return glowlichenconfiguration.canBePlacedOn;
        })).apply(instance, GlowLichenConfiguration::new);
    });
    public final int searchRange;
    public final boolean canPlaceOnFloor;
    public final boolean canPlaceOnCeiling;
    public final boolean canPlaceOnWall;
    public final float chanceOfSpreading;
    public final HolderSet<Block> canBePlacedOn;
    public final List<EnumDirection> validDirections;

    public GlowLichenConfiguration(int i, boolean flag, boolean flag1, boolean flag2, float f, HolderSet<Block> holderset) {
        this.searchRange = i;
        this.canPlaceOnFloor = flag;
        this.canPlaceOnCeiling = flag1;
        this.canPlaceOnWall = flag2;
        this.chanceOfSpreading = f;
        this.canBePlacedOn = holderset;
        List<EnumDirection> list = Lists.newArrayList();

        if (flag1) {
            list.add(EnumDirection.UP);
        }

        if (flag) {
            list.add(EnumDirection.DOWN);
        }

        if (flag2) {
            EnumDirection.EnumDirectionLimit enumdirection_enumdirectionlimit = EnumDirection.EnumDirectionLimit.HORIZONTAL;

            Objects.requireNonNull(list);
            enumdirection_enumdirectionlimit.forEach(list::add);
        }

        this.validDirections = Collections.unmodifiableList(list);
    }
}
