package net.minecraft.resources;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;

public class HolderSetCodec<E> implements Codec<HolderSet<E>> {

    private final ResourceKey<? extends IRegistry<E>> registryKey;
    private final Codec<Holder<E>> elementCodec;
    private final Codec<List<Holder<E>>> homogenousListCodec;
    private final Codec<Either<TagKey<E>, List<Holder<E>>>> registryAwareCodec;

    private static <E> Codec<List<Holder<E>>> homogenousList(Codec<Holder<E>> codec, boolean flag) {
        Codec<List<Holder<E>>> codec1 = ExtraCodecs.validate(codec.listOf(), ExtraCodecs.ensureHomogenous(Holder::kind));

        return flag ? codec1 : Codec.either(codec1, codec).xmap((either) -> {
            return (List) either.map((list) -> {
                return list;
            }, List::of);
        }, (list) -> {
            return list.size() == 1 ? Either.right((Holder) list.get(0)) : Either.left(list);
        });
    }

    public static <E> Codec<HolderSet<E>> create(ResourceKey<? extends IRegistry<E>> resourcekey, Codec<Holder<E>> codec, boolean flag) {
        return new HolderSetCodec<>(resourcekey, codec, flag);
    }

    private HolderSetCodec(ResourceKey<? extends IRegistry<E>> resourcekey, Codec<Holder<E>> codec, boolean flag) {
        this.registryKey = resourcekey;
        this.elementCodec = codec;
        this.homogenousListCodec = homogenousList(codec, flag);
        this.registryAwareCodec = Codec.either(TagKey.hashedCodec(resourcekey), this.homogenousListCodec);
    }

    public <T> DataResult<Pair<HolderSet<E>, T>> decode(DynamicOps<T> dynamicops, T t0) {
        if (dynamicops instanceof RegistryOps) {
            RegistryOps<T> registryops = (RegistryOps) dynamicops;
            Optional<HolderGetter<E>> optional = registryops.getter(this.registryKey);

            if (optional.isPresent()) {
                HolderGetter<E> holdergetter = (HolderGetter) optional.get();

                return this.registryAwareCodec.decode(dynamicops, t0).map((pair) -> {
                    return pair.mapFirst((either) -> {
                        Objects.requireNonNull(holdergetter);
                        return (HolderSet) either.map(holdergetter::getOrThrow, HolderSet::direct);
                    });
                });
            }
        }

        return this.decodeWithoutRegistry(dynamicops, t0);
    }

    public <T> DataResult<T> encode(HolderSet<E> holderset, DynamicOps<T> dynamicops, T t0) {
        if (dynamicops instanceof RegistryOps) {
            RegistryOps<T> registryops = (RegistryOps) dynamicops;
            Optional<HolderOwner<E>> optional = registryops.owner(this.registryKey);

            if (optional.isPresent()) {
                if (!holderset.canSerializeIn((HolderOwner) optional.get())) {
                    return DataResult.error(() -> {
                        return "HolderSet " + holderset + " is not valid in current registry set";
                    });
                }

                return this.registryAwareCodec.encode(holderset.unwrap().mapRight(List::copyOf), dynamicops, t0);
            }
        }

        return this.encodeWithoutRegistry(holderset, dynamicops, t0);
    }

    private <T> DataResult<Pair<HolderSet<E>, T>> decodeWithoutRegistry(DynamicOps<T> dynamicops, T t0) {
        return this.elementCodec.listOf().decode(dynamicops, t0).flatMap((pair) -> {
            List<Holder.a<E>> list = new ArrayList();
            Iterator iterator = ((List) pair.getFirst()).iterator();

            while (iterator.hasNext()) {
                Holder<E> holder = (Holder) iterator.next();

                if (!(holder instanceof Holder.a)) {
                    return DataResult.error(() -> {
                        return "Can't decode element " + holder + " without registry";
                    });
                }

                Holder.a<E> holder_a = (Holder.a) holder;

                list.add(holder_a);
            }

            return DataResult.success(new Pair(HolderSet.direct((List) list), pair.getSecond()));
        });
    }

    private <T> DataResult<T> encodeWithoutRegistry(HolderSet<E> holderset, DynamicOps<T> dynamicops, T t0) {
        return this.homogenousListCodec.encode(holderset.stream().toList(), dynamicops, t0);
    }
}
