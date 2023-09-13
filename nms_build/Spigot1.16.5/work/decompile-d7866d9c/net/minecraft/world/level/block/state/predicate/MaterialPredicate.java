package net.minecraft.world.level.block.state.predicate;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.Material;

public class MaterialPredicate implements Predicate<IBlockData> {

    private static final MaterialPredicate a = new MaterialPredicate(Material.AIR) {
        @Override
        public boolean test(@Nullable IBlockData iblockdata) {
            return iblockdata != null && iblockdata.isAir();
        }
    };
    private final Material b;

    private MaterialPredicate(Material material) {
        this.b = material;
    }

    public static MaterialPredicate a(Material material) {
        return material == Material.AIR ? MaterialPredicate.a : new MaterialPredicate(material);
    }

    public boolean test(@Nullable IBlockData iblockdata) {
        return iblockdata != null && iblockdata.getMaterial() == this.b;
    }
}
