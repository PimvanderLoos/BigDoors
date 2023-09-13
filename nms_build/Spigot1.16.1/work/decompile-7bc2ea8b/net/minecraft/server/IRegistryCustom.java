package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import java.util.Objects;
import java.util.Optional;

public interface IRegistryCustom {

    <E> Optional<IRegistryWritable<E>> a(ResourceKey<IRegistry<E>> resourcekey);

    static IRegistryCustom.Dimension b() {
        return DimensionManager.a(new IRegistryCustom.Dimension());
    }

    public static final class Dimension implements IRegistryCustom {

        public static final Codec<IRegistryCustom.Dimension> a = RegistryMaterials.a(IRegistry.ad, Lifecycle.experimental(), DimensionManager.a).xmap(IRegistryCustom.Dimension::new, (iregistrycustom_dimension) -> {
            return iregistrycustom_dimension.b;
        }).fieldOf("dimension").codec();
        private final RegistryMaterials<DimensionManager> b;

        public Dimension() {
            this(new RegistryMaterials<>(IRegistry.ad, Lifecycle.experimental()));
        }

        private Dimension(RegistryMaterials<DimensionManager> registrymaterials) {
            this.b = registrymaterials;
        }

        public void a(ResourceKey<DimensionManager> resourcekey, DimensionManager dimensionmanager) {
            this.b.a(resourcekey, (Object) dimensionmanager);
        }

        @Override
        public <E> Optional<IRegistryWritable<E>> a(ResourceKey<IRegistry<E>> resourcekey) {
            return Objects.equals(resourcekey, IRegistry.ad) ? Optional.of(this.b) : Optional.empty();
        }

        public IRegistry<DimensionManager> a() {
            return this.b;
        }
    }
}
