package net.minecraft.server;

import com.google.common.base.Predicate;
import javax.annotation.Nullable;

public class MaterialPredicate implements Predicate<IBlockData> {

    private final Material a;

    private MaterialPredicate(Material material) {
        this.a = material;
    }

    public static MaterialPredicate a(Material material) {
        return new MaterialPredicate(material);
    }

    public boolean a(@Nullable IBlockData iblockdata) {
        return iblockdata != null && iblockdata.getMaterial() == this.a;
    }

    public boolean apply(@Nullable Object object) {
        return this.a((IBlockData) object);
    }
}
