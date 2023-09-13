package net.minecraft.server;

import com.google.common.base.Predicate;
import javax.annotation.Nullable;

public interface IMonster extends IAnimal {

    Predicate<Entity> d = new Predicate() {
        public boolean a(@Nullable Entity entity) {
            return entity instanceof IMonster;
        }

        public boolean apply(@Nullable Object object) {
            return this.a((Entity) object);
        }
    };
    Predicate<Entity> e = new Predicate() {
        public boolean a(@Nullable Entity entity) {
            return entity instanceof IMonster && !entity.isInvisible();
        }

        public boolean apply(@Nullable Object object) {
            return this.a((Entity) object);
        }
    };
}
