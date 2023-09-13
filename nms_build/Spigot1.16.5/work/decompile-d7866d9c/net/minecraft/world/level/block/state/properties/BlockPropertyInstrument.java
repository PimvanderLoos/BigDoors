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

    HARP("harp", SoundEffects.BLOCK_NOTE_BLOCK_HARP), BASEDRUM("basedrum", SoundEffects.BLOCK_NOTE_BLOCK_BASEDRUM), SNARE("snare", SoundEffects.BLOCK_NOTE_BLOCK_SNARE), HAT("hat", SoundEffects.BLOCK_NOTE_BLOCK_HAT), BASS("bass", SoundEffects.BLOCK_NOTE_BLOCK_BASS), FLUTE("flute", SoundEffects.BLOCK_NOTE_BLOCK_FLUTE), BELL("bell", SoundEffects.BLOCK_NOTE_BLOCK_BELL), GUITAR("guitar", SoundEffects.BLOCK_NOTE_BLOCK_GUITAR), CHIME("chime", SoundEffects.BLOCK_NOTE_BLOCK_CHIME), XYLOPHONE("xylophone", SoundEffects.BLOCK_NOTE_BLOCK_XYLOPHONE), IRON_XYLOPHONE("iron_xylophone", SoundEffects.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE), COW_BELL("cow_bell", SoundEffects.BLOCK_NOTE_BLOCK_COW_BELL), DIDGERIDOO("didgeridoo", SoundEffects.BLOCK_NOTE_BLOCK_DIDGERIDOO), BIT("bit", SoundEffects.BLOCK_NOTE_BLOCK_BIT), BANJO("banjo", SoundEffects.BLOCK_NOTE_BLOCK_BANJO), PLING("pling", SoundEffects.BLOCK_NOTE_BLOCK_PLING);

    private final String q;
    private final SoundEffect r;

    private BlockPropertyInstrument(String s, SoundEffect soundeffect) {
        this.q = s;
        this.r = soundeffect;
    }

    @Override
    public String getName() {
        return this.q;
    }

    public SoundEffect b() {
        return this.r;
    }

    public static BlockPropertyInstrument a(IBlockData iblockdata) {
        if (iblockdata.a(Blocks.CLAY)) {
            return BlockPropertyInstrument.FLUTE;
        } else if (iblockdata.a(Blocks.GOLD_BLOCK)) {
            return BlockPropertyInstrument.BELL;
        } else if (iblockdata.a((Tag) TagsBlock.WOOL)) {
            return BlockPropertyInstrument.GUITAR;
        } else if (iblockdata.a(Blocks.PACKED_ICE)) {
            return BlockPropertyInstrument.CHIME;
        } else if (iblockdata.a(Blocks.BONE_BLOCK)) {
            return BlockPropertyInstrument.XYLOPHONE;
        } else if (iblockdata.a(Blocks.IRON_BLOCK)) {
            return BlockPropertyInstrument.IRON_XYLOPHONE;
        } else if (iblockdata.a(Blocks.SOUL_SAND)) {
            return BlockPropertyInstrument.COW_BELL;
        } else if (iblockdata.a(Blocks.PUMPKIN)) {
            return BlockPropertyInstrument.DIDGERIDOO;
        } else if (iblockdata.a(Blocks.EMERALD_BLOCK)) {
            return BlockPropertyInstrument.BIT;
        } else if (iblockdata.a(Blocks.HAY_BLOCK)) {
            return BlockPropertyInstrument.BANJO;
        } else if (iblockdata.a(Blocks.GLOWSTONE)) {
            return BlockPropertyInstrument.PLING;
        } else {
            Material material = iblockdata.getMaterial();

            return material == Material.STONE ? BlockPropertyInstrument.BASEDRUM : (material == Material.SAND ? BlockPropertyInstrument.SNARE : (material == Material.SHATTERABLE ? BlockPropertyInstrument.HAT : (material != Material.WOOD && material != Material.NETHER_WOOD ? BlockPropertyInstrument.HARP : BlockPropertyInstrument.BASS)));
        }
    }
}
