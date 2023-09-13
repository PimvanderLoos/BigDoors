package net.minecraft.server;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public interface IEntityAccess {

    List<Entity> getEntities(@Nullable Entity entity, AxisAlignedBB axisalignedbb, @Nullable Predicate<? super Entity> predicate);

    default List<Entity> getEntities(@Nullable Entity entity, AxisAlignedBB axisalignedbb) {
        return this.getEntities(entity, axisalignedbb, IEntitySelector.f);
    }

    default Stream<VoxelShape> a(@Nullable Entity entity, VoxelShape voxelshape, Set<Entity> set) {
        if (voxelshape.isEmpty()) {
            return Stream.empty();
        } else {
            AxisAlignedBB axisalignedbb = voxelshape.getBoundingBox();

            return this.getEntities(entity, axisalignedbb.g(0.25D)).stream().filter((entity1) -> {
                return !set.contains(entity1) && (entity == null || !entity.x(entity1));
            }).flatMap((entity1) -> {
                return Stream.of(entity1.al(), entity == null ? null : entity.j(entity1)).filter(Objects::nonNull).filter((axisalignedbb1) -> {
                    return axisalignedbb1.c(axisalignedbb);
                }).map(VoxelShapes::a);
            });
        }
    }
}
