package net.minecraft.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.SystemUtils;
import net.minecraft.resources.ResourceKey;

public class LayeredRegistryAccess<T> {

    private final List<T> keys;
    private final List<IRegistryCustom.Dimension> values;
    private final IRegistryCustom.Dimension composite;

    public LayeredRegistryAccess(List<T> list) {
        this(list, (List) SystemUtils.make(() -> {
            IRegistryCustom.Dimension[] airegistrycustom_dimension = new IRegistryCustom.Dimension[list.size()];

            Arrays.fill(airegistrycustom_dimension, IRegistryCustom.EMPTY);
            return Arrays.asList(airegistrycustom_dimension);
        }));
    }

    private LayeredRegistryAccess(List<T> list, List<IRegistryCustom.Dimension> list1) {
        this.keys = List.copyOf(list);
        this.values = List.copyOf(list1);
        this.composite = (new IRegistryCustom.c(collectRegistries(list1.stream()))).freeze();
    }

    private int getLayerIndexOrThrow(T t0) {
        int i = this.keys.indexOf(t0);

        if (i == -1) {
            throw new IllegalStateException("Can't find " + t0 + " inside " + this.keys);
        } else {
            return i;
        }
    }

    public IRegistryCustom.Dimension getLayer(T t0) {
        int i = this.getLayerIndexOrThrow(t0);

        return (IRegistryCustom.Dimension) this.values.get(i);
    }

    public IRegistryCustom.Dimension getAccessForLoading(T t0) {
        int i = this.getLayerIndexOrThrow(t0);

        return this.getCompositeAccessForLayers(0, i);
    }

    public IRegistryCustom.Dimension getAccessFrom(T t0) {
        int i = this.getLayerIndexOrThrow(t0);

        return this.getCompositeAccessForLayers(i, this.values.size());
    }

    private IRegistryCustom.Dimension getCompositeAccessForLayers(int i, int j) {
        return (new IRegistryCustom.c(collectRegistries(this.values.subList(i, j).stream()))).freeze();
    }

    public LayeredRegistryAccess<T> replaceFrom(T t0, IRegistryCustom.Dimension... airegistrycustom_dimension) {
        return this.replaceFrom(t0, Arrays.asList(airegistrycustom_dimension));
    }

    public LayeredRegistryAccess<T> replaceFrom(T t0, List<IRegistryCustom.Dimension> list) {
        int i = this.getLayerIndexOrThrow(t0);

        if (list.size() > this.values.size() - i) {
            throw new IllegalStateException("Too many values to replace");
        } else {
            List<IRegistryCustom.Dimension> list1 = new ArrayList();

            for (int j = 0; j < i; ++j) {
                list1.add((IRegistryCustom.Dimension) this.values.get(j));
            }

            list1.addAll(list);

            while (list1.size() < this.values.size()) {
                list1.add(IRegistryCustom.EMPTY);
            }

            return new LayeredRegistryAccess<>(this.keys, list1);
        }
    }

    public IRegistryCustom.Dimension compositeAccess() {
        return this.composite;
    }

    private static Map<ResourceKey<? extends IRegistry<?>>, IRegistry<?>> collectRegistries(Stream<? extends IRegistryCustom> stream) {
        Map<ResourceKey<? extends IRegistry<?>>, IRegistry<?>> map = new HashMap();

        stream.forEach((iregistrycustom) -> {
            iregistrycustom.registries().forEach((iregistrycustom_d) -> {
                if (map.put(iregistrycustom_d.key(), iregistrycustom_d.value()) != null) {
                    throw new IllegalStateException("Duplicated registry " + iregistrycustom_d.key());
                }
            });
        });
        return map;
    }
}
