package net.minecraft.world.level.block.state.predicate;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.Material;

public class MaterialPredicate implements Predicate<IBlockData> {

    private static final MaterialPredicate AIR = new MaterialPredicate(Material.AIR) {
        @Override
        public boolean test(@Nullable IBlockData iblockdata) {
            return iblockdata != null && iblockdata.isAir();
        }
    };
    private final Material material;

    MaterialPredicate(Material material) {
        this.material = material;
    }

    public static MaterialPredicate a(Material material) {
        return material == Material.AIR ? MaterialPredicate.AIR : new MaterialPredicate(material);
    }

    public boolean test(@Nullable IBlockData iblockdata) {
        return iblockdata != null && iblockdata.getMaterial() == this.material;
    }
}
