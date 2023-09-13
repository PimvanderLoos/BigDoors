package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.blocks.ArgumentBlock;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class DefinedStructureProcessorJigsawReplacement extends DefinedStructureProcessor {

    public static final Codec<DefinedStructureProcessorJigsawReplacement> a = Codec.unit(() -> {
        return DefinedStructureProcessorJigsawReplacement.b;
    });
    public static final DefinedStructureProcessorJigsawReplacement b = new DefinedStructureProcessorJigsawReplacement();

    private DefinedStructureProcessorJigsawReplacement() {}

    @Nullable
    @Override
    public DefinedStructure.BlockInfo a(IWorldReader iworldreader, BlockPosition blockposition, BlockPosition blockposition1, DefinedStructure.BlockInfo definedstructure_blockinfo, DefinedStructure.BlockInfo definedstructure_blockinfo1, DefinedStructureInfo definedstructureinfo) {
        IBlockData iblockdata = definedstructure_blockinfo1.b;

        if (iblockdata.a(Blocks.JIGSAW)) {
            String s = definedstructure_blockinfo1.c.getString("final_state");
            ArgumentBlock argumentblock = new ArgumentBlock(new StringReader(s), false);

            try {
                argumentblock.a(true);
            } catch (CommandSyntaxException commandsyntaxexception) {
                throw new RuntimeException(commandsyntaxexception);
            }

            return argumentblock.getBlockData().a(Blocks.STRUCTURE_VOID) ? null : new DefinedStructure.BlockInfo(definedstructure_blockinfo1.a, argumentblock.getBlockData(), (NBTTagCompound) null);
        } else {
            return definedstructure_blockinfo1;
        }
    }

    @Override
    protected DefinedStructureStructureProcessorType<?> a() {
        return DefinedStructureStructureProcessorType.d;
    }
}
