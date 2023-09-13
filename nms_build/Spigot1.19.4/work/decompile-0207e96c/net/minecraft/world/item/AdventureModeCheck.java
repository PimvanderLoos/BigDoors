package net.minecraft.world.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.blocks.ArgumentBlockPredicate;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.pattern.ShapeDetectorBlock;

public class AdventureModeCheck {

    private final String tagName;
    @Nullable
    private ShapeDetectorBlock lastCheckedBlock;
    private boolean lastResult;
    private boolean checksBlockEntity;

    public AdventureModeCheck(String s) {
        this.tagName = s;
    }

    private static boolean areSameBlocks(ShapeDetectorBlock shapedetectorblock, @Nullable ShapeDetectorBlock shapedetectorblock1, boolean flag) {
        return shapedetectorblock1 != null && shapedetectorblock.getState() == shapedetectorblock1.getState() ? (!flag ? true : (shapedetectorblock.getEntity() == null && shapedetectorblock1.getEntity() == null ? true : (shapedetectorblock.getEntity() != null && shapedetectorblock1.getEntity() != null ? Objects.equals(shapedetectorblock.getEntity().saveWithId(), shapedetectorblock1.getEntity().saveWithId()) : false))) : false;
    }

    public boolean test(ItemStack itemstack, IRegistry<Block> iregistry, ShapeDetectorBlock shapedetectorblock) {
        if (areSameBlocks(shapedetectorblock, this.lastCheckedBlock, this.checksBlockEntity)) {
            return this.lastResult;
        } else {
            this.lastCheckedBlock = shapedetectorblock;
            this.checksBlockEntity = false;
            NBTTagCompound nbttagcompound = itemstack.getTag();

            if (nbttagcompound != null && nbttagcompound.contains(this.tagName, 9)) {
                NBTTagList nbttaglist = nbttagcompound.getList(this.tagName, 8);

                for (int i = 0; i < nbttaglist.size(); ++i) {
                    String s = nbttaglist.getString(i);

                    try {
                        ArgumentBlockPredicate.b argumentblockpredicate_b = ArgumentBlockPredicate.parse(iregistry.asLookup(), new StringReader(s));

                        this.checksBlockEntity |= argumentblockpredicate_b.requiresNbt();
                        if (argumentblockpredicate_b.test(shapedetectorblock)) {
                            this.lastResult = true;
                            return true;
                        }
                    } catch (CommandSyntaxException commandsyntaxexception) {
                        ;
                    }
                }
            }

            this.lastResult = false;
            return false;
        }
    }
}
