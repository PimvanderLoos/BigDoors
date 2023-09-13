package net.minecraft.server;

import java.util.function.Predicate;

public interface IMonster extends IAnimal {

    Predicate<Entity> d = (entity) -> {
        return entity instanceof IMonster;
    };
    Predicate<Entity> e = (entity) -> {
        return entity instanceof IMonster && !entity.isInvisible();
    };
}
