package net.minecraft.server;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public interface IEntityAccess {

    List<Entity> getEntities(@Nullable Entity entity, AxisAlignedBB axisalignedbb, @Nullable Predicate<? super Entity> predicate);

    default List<Entity> getEntities(@Nullable Entity entity, AxisAlignedBB axisalignedbb) {
        return this.getEntities(entity, axisalignedbb, IEntitySelector.f);
    }

    default Stream<VoxelShape> a(@Nullable Entity entity, VoxelShape voxelshape, Set<Entity> set) {
        if (voxelshape.b()) {
            return Stream.empty();
        } else {
            AxisAlignedBB axisalignedbb = voxelshape.a();

            return this.getEntities(entity, axisalignedbb.g(0.25D)).stream().filter((entity) -> {
                return !set.contains(entity) && (entity1 == null || !entity1.x(entity));
            }).flatMap((entity) -> {
                return Stream.of(new AxisAlignedBB[] { entity.al(), entity1 == null ? null : entity1.j(entity)}).filter(Objects::nonNull).filter((axisalignedbb) -> {
                    return axisalignedbb.c(axisalignedbb1);
                }).map(VoxelShapes::a);
            });
        }
    }
}
