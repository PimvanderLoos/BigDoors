package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface IRegistry<T> extends Registry<T> {

    Logger e = LogManager.getLogger();
    IRegistry<IRegistry<?>> f = new RegistryMaterials<>();
    IRegistry<Block> BLOCK = a("block", (IRegistry) (new RegistryBlocks<>(new MinecraftKey("air"))));
    IRegistry<FluidType> FLUID = a("fluid", (IRegistry) (new RegistryBlocks<>(new MinecraftKey("empty"))));
    IRegistry<Paintings> MOTIVE = a("motive", (IRegistry) (new RegistryBlocks<>(new MinecraftKey("kebab"))));
    IRegistry<PotionRegistry> POTION = a("potion", (IRegistry) (new RegistryBlocks<>(new MinecraftKey("empty"))));
    IRegistry<DimensionManager> DIMENSION_TYPE = a("dimension_type", (IRegistry) (new RegistryMaterials<>()));
    IRegistry<MinecraftKey> CUSTOM_STAT = a("custom_stat", (IRegistry) (new RegistryMaterials<>()));
    IRegistry<BiomeBase> BIOME = a("biome", (IRegistry) (new RegistryMaterials<>()));
    IRegistry<BiomeLayout<?, ?>> BIOME_SOURCE_TYPE = a("biome_source_type", (IRegistry) (new RegistryMaterials<>()));
    IRegistry<TileEntityTypes<?>> BLOCK_ENTITY_TYPE = a("block_entity_type", (IRegistry) (new RegistryMaterials<>()));
    IRegistry<ChunkGeneratorType<?, ?>> CHUNK_GENERATOR_TYPE = a("chunk_generator_type", (IRegistry) (new RegistryMaterials<>()));
    IRegistry<Enchantment> ENCHANTMENT = a("enchantment", (IRegistry) (new RegistryMaterials<>()));
    IRegistry<EntityTypes<?>> ENTITY_TYPE = a("entity_type", (IRegistry) (new RegistryMaterials<>()));
    IRegistry<Item> ITEM = a("item", (IRegistry) (new RegistryMaterials<>()));
    IRegistry<MobEffectList> MOB_EFFECT = a("mob_effect", (IRegistry) (new RegistryMaterials<>()));
    IRegistry<Particle<? extends ParticleParam>> PARTICLE_TYPE = a("particle_type", (IRegistry) (new RegistryMaterials<>()));
    IRegistry<SoundEffect> SOUND_EVENT = a("sound_event", (IRegistry) (new RegistryMaterials<>()));
    IRegistry<StatisticWrapper<?>> STATS = a("stats", (IRegistry) (new RegistryMaterials<>()));

    static <T> IRegistry<T> a(String s, IRegistry<T> iregistry) {
        IRegistry.f.a(new MinecraftKey(s), (Object) iregistry);
        return iregistry;
    }

    static void e() {
        IRegistry.f.forEach((iregistry) -> {
            if (iregistry.d()) {
                IRegistry.e.error("Registry '{}' was empty after loading", IRegistry.f.getKey(iregistry));
                if (SharedConstants.b) {
                    throw new IllegalStateException("Registry: '" + IRegistry.f.getKey(iregistry) + "' is empty, not allowed, fix me!");
                }
            }

            if (iregistry instanceof RegistryBlocks) {
                MinecraftKey minecraftkey = iregistry.b();

                Validate.notNull(iregistry.get(minecraftkey), "Missing default of DefaultedMappedRegistry: " + minecraftkey, new Object[0]);
            }

        });
    }

    @Nullable
    MinecraftKey getKey(T t0);

    T getOrDefault(@Nullable MinecraftKey minecraftkey);

    MinecraftKey b();

    int a(@Nullable T t0);

    @Nullable
    T fromId(int i);

    Iterator<T> iterator();

    @Nullable
    T get(@Nullable MinecraftKey minecraftkey);

    void a(int i, MinecraftKey minecraftkey, T t0);

    void a(MinecraftKey minecraftkey, T t0);

    Set<MinecraftKey> keySet();

    boolean d();

    @Nullable
    T a(Random random);

    default Stream<T> f() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    boolean c(MinecraftKey minecraftkey);
}
