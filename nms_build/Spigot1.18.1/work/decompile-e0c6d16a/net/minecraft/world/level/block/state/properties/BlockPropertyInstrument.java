package net.minecraft.world.level.block.state.properties;

import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.INamable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.Material;

public enum BlockPropertyInstrument implements INamable {

    HARP("harp", SoundEffects.NOTE_BLOCK_HARP), BASEDRUM("basedrum", SoundEffects.NOTE_BLOCK_BASEDRUM), SNARE("snare", SoundEffects.NOTE_BLOCK_SNARE), HAT("hat", SoundEffects.NOTE_BLOCK_HAT), BASS("bass", SoundEffects.NOTE_BLOCK_BASS), FLUTE("flute", SoundEffects.NOTE_BLOCK_FLUTE), BELL("bell", SoundEffects.NOTE_BLOCK_BELL), GUITAR("guitar", SoundEffects.NOTE_BLOCK_GUITAR), CHIME("chime", SoundEffects.NOTE_BLOCK_CHIME), XYLOPHONE("xylophone", SoundEffects.NOTE_BLOCK_XYLOPHONE), IRON_XYLOPHONE("iron_xylophone", SoundEffects.NOTE_BLOCK_IRON_XYLOPHONE), COW_BELL("cow_bell", SoundEffects.NOTE_BLOCK_COW_BELL), DIDGERIDOO("didgeridoo", SoundEffects.NOTE_BLOCK_DIDGERIDOO), BIT("bit", SoundEffects.NOTE_BLOCK_BIT), BANJO("banjo", SoundEffects.NOTE_BLOCK_BANJO), PLING("pling", SoundEffects.NOTE_BLOCK_PLING);

    private final String name;
    private final SoundEffect soundEvent;

    private BlockPropertyInstrument(String s, SoundEffect soundeffect) {
        this.name = s;
        this.soundEvent = soundeffect;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public SoundEffect getSoundEvent() {
        return this.soundEvent;
    }

    public static BlockPropertyInstrument byState(IBlockData iblockdata) {
        if (iblockdata.is(Blocks.CLAY)) {
            return BlockPropertyInstrument.FLUTE;
        } else if (iblockdata.is(Blocks.GOLD_BLOCK)) {
            return BlockPropertyInstrument.BELL;
        } else if (iblockdata.is((Tag) TagsBlock.WOOL)) {
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
}
