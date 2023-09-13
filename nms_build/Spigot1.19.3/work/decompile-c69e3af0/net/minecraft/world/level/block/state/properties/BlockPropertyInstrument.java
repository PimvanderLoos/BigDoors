package net.minecraft.world.level.block.state.properties;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.INamable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.Material;

public enum BlockPropertyInstrument implements INamable {

    HARP("harp", SoundEffects.NOTE_BLOCK_HARP, BlockPropertyInstrument.a.BASE_BLOCK), BASEDRUM("basedrum", SoundEffects.NOTE_BLOCK_BASEDRUM, BlockPropertyInstrument.a.BASE_BLOCK), SNARE("snare", SoundEffects.NOTE_BLOCK_SNARE, BlockPropertyInstrument.a.BASE_BLOCK), HAT("hat", SoundEffects.NOTE_BLOCK_HAT, BlockPropertyInstrument.a.BASE_BLOCK), BASS("bass", SoundEffects.NOTE_BLOCK_BASS, BlockPropertyInstrument.a.BASE_BLOCK), FLUTE("flute", SoundEffects.NOTE_BLOCK_FLUTE, BlockPropertyInstrument.a.BASE_BLOCK), BELL("bell", SoundEffects.NOTE_BLOCK_BELL, BlockPropertyInstrument.a.BASE_BLOCK), GUITAR("guitar", SoundEffects.NOTE_BLOCK_GUITAR, BlockPropertyInstrument.a.BASE_BLOCK), CHIME("chime", SoundEffects.NOTE_BLOCK_CHIME, BlockPropertyInstrument.a.BASE_BLOCK), XYLOPHONE("xylophone", SoundEffects.NOTE_BLOCK_XYLOPHONE, BlockPropertyInstrument.a.BASE_BLOCK), IRON_XYLOPHONE("iron_xylophone", SoundEffects.NOTE_BLOCK_IRON_XYLOPHONE, BlockPropertyInstrument.a.BASE_BLOCK), COW_BELL("cow_bell", SoundEffects.NOTE_BLOCK_COW_BELL, BlockPropertyInstrument.a.BASE_BLOCK), DIDGERIDOO("didgeridoo", SoundEffects.NOTE_BLOCK_DIDGERIDOO, BlockPropertyInstrument.a.BASE_BLOCK), BIT("bit", SoundEffects.NOTE_BLOCK_BIT, BlockPropertyInstrument.a.BASE_BLOCK), BANJO("banjo", SoundEffects.NOTE_BLOCK_BANJO, BlockPropertyInstrument.a.BASE_BLOCK), PLING("pling", SoundEffects.NOTE_BLOCK_PLING, BlockPropertyInstrument.a.BASE_BLOCK), ZOMBIE("zombie", SoundEffects.NOTE_BLOCK_IMITATE_ZOMBIE, BlockPropertyInstrument.a.MOB_HEAD), SKELETON("skeleton", SoundEffects.NOTE_BLOCK_IMITATE_SKELETON, BlockPropertyInstrument.a.MOB_HEAD), CREEPER("creeper", SoundEffects.NOTE_BLOCK_IMITATE_CREEPER, BlockPropertyInstrument.a.MOB_HEAD), DRAGON("dragon", SoundEffects.NOTE_BLOCK_IMITATE_ENDER_DRAGON, BlockPropertyInstrument.a.MOB_HEAD), WITHER_SKELETON("wither_skeleton", SoundEffects.NOTE_BLOCK_IMITATE_WITHER_SKELETON, BlockPropertyInstrument.a.MOB_HEAD), PIGLIN("piglin", SoundEffects.NOTE_BLOCK_IMITATE_PIGLIN, BlockPropertyInstrument.a.MOB_HEAD), CUSTOM_HEAD("custom_head", SoundEffects.UI_BUTTON_CLICK, BlockPropertyInstrument.a.CUSTOM);

    private final String name;
    private final Holder<SoundEffect> soundEvent;
    private final BlockPropertyInstrument.a type;

    private BlockPropertyInstrument(String s, Holder holder, BlockPropertyInstrument.a blockpropertyinstrument_a) {
        this.name = s;
        this.soundEvent = holder;
        this.type = blockpropertyinstrument_a;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public Holder<SoundEffect> getSoundEvent() {
        return this.soundEvent;
    }

    public boolean isTunable() {
        return this.type == BlockPropertyInstrument.a.BASE_BLOCK;
    }

    public boolean hasCustomSound() {
        return this.type == BlockPropertyInstrument.a.CUSTOM;
    }

    public boolean requiresAirAbove() {
        return this.type == BlockPropertyInstrument.a.BASE_BLOCK;
    }

    public static Optional<BlockPropertyInstrument> byStateAbove(IBlockData iblockdata) {
        return iblockdata.is(Blocks.ZOMBIE_HEAD) ? Optional.of(BlockPropertyInstrument.ZOMBIE) : (iblockdata.is(Blocks.SKELETON_SKULL) ? Optional.of(BlockPropertyInstrument.SKELETON) : (iblockdata.is(Blocks.CREEPER_HEAD) ? Optional.of(BlockPropertyInstrument.CREEPER) : (iblockdata.is(Blocks.DRAGON_HEAD) ? Optional.of(BlockPropertyInstrument.DRAGON) : (iblockdata.is(Blocks.WITHER_SKELETON_SKULL) ? Optional.of(BlockPropertyInstrument.WITHER_SKELETON) : (iblockdata.is(Blocks.PIGLIN_HEAD) ? Optional.of(BlockPropertyInstrument.PIGLIN) : (iblockdata.is(Blocks.PLAYER_HEAD) ? Optional.of(BlockPropertyInstrument.CUSTOM_HEAD) : Optional.empty()))))));
    }

    public static BlockPropertyInstrument byStateBelow(IBlockData iblockdata) {
        if (iblockdata.is(Blocks.CLAY)) {
            return BlockPropertyInstrument.FLUTE;
        } else if (iblockdata.is(Blocks.GOLD_BLOCK)) {
            return BlockPropertyInstrument.BELL;
        } else if (iblockdata.is(TagsBlock.WOOL)) {
            return BlockPropertyInstrument.GUITAR;
        } else if (iblockdata.is(Blocks.PACKED_ICE)) {
            return BlockPropertyInstrument.CHIME;
        } else if (iblockdata.is(Blocks.BONE_BLOCK)) {
            return BlockPropertyInstrument.XYLOPHONE;
        } else if (iblockdata.is(Blocks.IRON_BLOCK)) {
            return BlockPropertyInstrument.IRON_XYLOPHONE;
        } else if (iblockdata.is(Blocks.SOUL_SAND)) {
            return BlockPropertyInstrument.COW_BELL;
        } else if (iblockdata.is(Blocks.PUMPKIN)) {
            return BlockPropertyInstrument.DIDGERIDOO;
        } else if (iblockdata.is(Blocks.EMERALD_BLOCK)) {
            return BlockPropertyInstrument.BIT;
        } else if (iblockdata.is(Blocks.HAY_BLOCK)) {
            return BlockPropertyInstrument.BANJO;
        } else if (iblockdata.is(Blocks.GLOWSTONE)) {
            return BlockPropertyInstrument.PLING;
        } else {
            Material material = iblockdata.getMaterial();

            return material == Material.STONE ? BlockPropertyInstrument.BASEDRUM : (material == Material.SAND ? BlockPropertyInstrument.SNARE : (material == Material.GLASS ? BlockPropertyInstrument.HAT : (material != Material.WOOD && material != Material.NETHER_WOOD ? BlockPropertyInstrument.HARP : BlockPropertyInstrument.BASS)));
        }
    }

    private static enum a {

        BASE_BLOCK, MOB_HEAD, CUSTOM;

        private a() {}
    }
}
