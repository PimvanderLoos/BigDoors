package net.minecraft.world.entity.animal;

import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;

public record CatVariant(MinecraftKey texture) {

    public static final ResourceKey<CatVariant> TABBY = createKey("tabby");
    public static final ResourceKey<CatVariant> BLACK = createKey("black");
    public static final ResourceKey<CatVariant> RED = createKey("red");
    public static final ResourceKey<CatVariant> SIAMESE = createKey("siamese");
    public static final ResourceKey<CatVariant> BRITISH_SHORTHAIR = createKey("british_shorthair");
    public static final ResourceKey<CatVariant> CALICO = createKey("calico");
    public static final ResourceKey<CatVariant> PERSIAN = createKey("persian");
    public static final ResourceKey<CatVariant> RAGDOLL = createKey("ragdoll");
    public static final ResourceKey<CatVariant> WHITE = createKey("white");
    public static final ResourceKey<CatVariant> JELLIE = createKey("jellie");
    public static final ResourceKey<CatVariant> ALL_BLACK = createKey("all_black");

    private static ResourceKey<CatVariant> createKey(String s) {
        return ResourceKey.create(Registries.CAT_VARIANT, new MinecraftKey(s));
    }

    public static CatVariant bootstrap(IRegistry<CatVariant> iregistry) {
        register(iregistry, CatVariant.TABBY, "textures/entity/cat/tabby.png");
        register(iregistry, CatVariant.BLACK, "textures/entity/cat/black.png");
        register(iregistry, CatVariant.RED, "textures/entity/cat/red.png");
        register(iregistry, CatVariant.SIAMESE, "textures/entity/cat/siamese.png");
        register(iregistry, CatVariant.BRITISH_SHORTHAIR, "textures/entity/cat/british_shorthair.png");
        register(iregistry, CatVariant.CALICO, "textures/entity/cat/calico.png");
        register(iregistry, CatVariant.PERSIAN, "textures/entity/cat/persian.png");
        register(iregistry, CatVariant.RAGDOLL, "textures/entity/cat/ragdoll.png");
        register(iregistry, CatVariant.WHITE, "textures/entity/cat/white.png");
        register(iregistry, CatVariant.JELLIE, "textures/entity/cat/jellie.png");
        return register(iregistry, CatVariant.ALL_BLACK, "textures/entity/cat/all_black.png");
    }

    private static CatVariant register(IRegistry<CatVariant> iregistry, ResourceKey<CatVariant> resourcekey, String s) {
        return (CatVariant) IRegistry.register(iregistry, resourcekey, new CatVariant(new MinecraftKey(s)));
    }
}
