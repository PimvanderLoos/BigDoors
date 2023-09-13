package net.minecraft.world.entity.ai.behavior.declarative;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.IdF;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.OptionalBox;
import com.mojang.datafixers.kinds.OptionalBox.Mu;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Unit;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class BehaviorBuilder<E extends EntityLiving, M> implements App<BehaviorBuilder.c<E>, M> {

    private final BehaviorBuilder.e<E, M> trigger;

    public static <E extends EntityLiving, M> BehaviorBuilder<E, M> unbox(App<BehaviorBuilder.c<E>, M> app) {
        return (BehaviorBuilder) app;
    }

    public static <E extends EntityLiving> BehaviorBuilder.b<E> instance() {
        return new BehaviorBuilder.b<>();
    }

    public static <E extends EntityLiving> OneShot<E> create(Function<BehaviorBuilder.b<E>, ? extends App<BehaviorBuilder.c<E>, Trigger<E>>> function) {
        final BehaviorBuilder.e<E, Trigger<E>> behaviorbuilder_e = get((App) function.apply(instance()));

        return new OneShot<E>() {
            @Override
            public boolean trigger(WorldServer worldserver, E e0, long i) {
                Trigger<E> trigger = (Trigger) behaviorbuilder_e.tryTrigger(worldserver, e0, i);

                return trigger == null ? false : trigger.trigger(worldserver, e0, i);
            }

            @Override
            public String debugString() {
                return "OneShot[" + behaviorbuilder_e.debugString() + "]";
            }

            public String toString() {
                return this.debugString();
            }
        };
    }

    public static <E extends EntityLiving> OneShot<E> sequence(Trigger<? super E> trigger, Trigger<? super E> trigger1) {
        return create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.group(behaviorbuilder_b.ifTriggered(trigger)).apply(behaviorbuilder_b, (unit) -> {
                Objects.requireNonNull(trigger1);
                return trigger1::trigger;
            });
        });
    }

    public static <E extends EntityLiving> OneShot<E> triggerIf(Predicate<E> predicate, OneShot<? super E> oneshot) {
        return sequence(triggerIf(predicate), oneshot);
    }

    public static <E extends EntityLiving> OneShot<E> triggerIf(Predicate<E> predicate) {
        return create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.point((worldserver, entityliving, i) -> {
                return predicate.test(entityliving);
            });
        });
    }

    public static <E extends EntityLiving> OneShot<E> triggerIf(BiPredicate<WorldServer, E> bipredicate) {
        return create((behaviorbuilder_b) -> {
            return behaviorbuilder_b.point((worldserver, entityliving, i) -> {
                return bipredicate.test(worldserver, entityliving);
            });
        });
    }

    static <E extends EntityLiving, M> BehaviorBuilder.e<E, M> get(App<BehaviorBuilder.c<E>, M> app) {
        return unbox(app).trigger;
    }

    BehaviorBuilder(BehaviorBuilder.e<E, M> behaviorbuilder_e) {
        this.trigger = behaviorbuilder_e;
    }

    static <E extends EntityLiving, M> BehaviorBuilder<E, M> create(BehaviorBuilder.e<E, M> behaviorbuilder_e) {
        return new BehaviorBuilder<>(behaviorbuilder_e);
    }

    public static final class b<E extends EntityLiving> implements Applicative<BehaviorBuilder.c<E>, BehaviorBuilder.b.a<E>> {

        public b() {}

        public <Value> Optional<Value> tryGet(MemoryAccessor<Mu, Value> memoryaccessor) {
            return OptionalBox.unbox(memoryaccessor.value());
        }

        public <Value> Value get(MemoryAccessor<com.mojang.datafixers.kinds.IdF.Mu, Value> memoryaccessor) {
            return IdF.get(memoryaccessor.value());
        }

        public <Value> BehaviorBuilder<E, MemoryAccessor<Mu, Value>> registered(MemoryModuleType<Value> memorymoduletype) {
            return new BehaviorBuilder.d<>(new MemoryCondition.c<>(memorymoduletype));
        }

        public <Value> BehaviorBuilder<E, MemoryAccessor<com.mojang.datafixers.kinds.IdF.Mu, Value>> present(MemoryModuleType<Value> memorymoduletype) {
            return new BehaviorBuilder.d<>(new MemoryCondition.b<>(memorymoduletype));
        }

        public <Value> BehaviorBuilder<E, MemoryAccessor<com.mojang.datafixers.kinds.Const.Mu<Unit>, Value>> absent(MemoryModuleType<Value> memorymoduletype) {
            return new BehaviorBuilder.d<>(new MemoryCondition.a<>(memorymoduletype));
        }

        public BehaviorBuilder<E, Unit> ifTriggered(Trigger<? super E> trigger) {
            return new BehaviorBuilder.f<>(trigger);
        }

        public <A> BehaviorBuilder<E, A> point(A a0) {
            return new BehaviorBuilder.a<>(a0);
        }

        public <A> BehaviorBuilder<E, A> point(Supplier<String> supplier, A a0) {
            return new BehaviorBuilder.a<>(a0, supplier);
        }

        public <A, R> Function<App<BehaviorBuilder.c<E>, A>, App<BehaviorBuilder.c<E>, R>> lift1(App<BehaviorBuilder.c<E>, Function<A, R>> app) {
            return (app1) -> {
                final BehaviorBuilder.e<E, A> behaviorbuilder_e = BehaviorBuilder.get(app1);
                final BehaviorBuilder.e<E, Function<A, R>> behaviorbuilder_e1 = BehaviorBuilder.get(app);

                return BehaviorBuilder.create(new BehaviorBuilder.e<E, R>() {
                    @Override
                    public R tryTrigger(WorldServer worldserver, E e0, long i) {
                        A a0 = behaviorbuilder_e.tryTrigger(worldserver, e0, i);

                        if (a0 == null) {
                            return null;
                        } else {
                            Function<A, R> function = (Function) behaviorbuilder_e1.tryTrigger(worldserver, e0, i);

                            return function == null ? null : function.apply(a0);
                        }
                    }

                    @Override
                    public String debugString() {
                        String s = behaviorbuilder_e1.debugString();

                        return s + " * " + behaviorbuilder_e.debugString();
                    }

                    public String toString() {
                        return this.debugString();
                    }
                });
            };
        }

        public <T, R> BehaviorBuilder<E, R> map(final Function<? super T, ? extends R> function, App<BehaviorBuilder.c<E>, T> app) {
            final BehaviorBuilder.e<E, T> behaviorbuilder_e = BehaviorBuilder.get(app);

            return BehaviorBuilder.create(new BehaviorBuilder.e<E, R>() {
                @Override
                public R tryTrigger(WorldServer worldserver, E e0, long i) {
                    T t0 = behaviorbuilder_e.tryTrigger(worldserver, e0, i);

                    return t0 == null ? null : function.apply(t0);
                }

                @Override
                public String debugString() {
                    String s = behaviorbuilder_e.debugString();

                    return s + ".map[" + function + "]";
                }

                public String toString() {
                    return this.debugString();
                }
            });
        }

        public <A, B, R> BehaviorBuilder<E, R> ap2(App<BehaviorBuilder.c<E>, BiFunction<A, B, R>> app, App<BehaviorBuilder.c<E>, A> app1, App<BehaviorBuilder.c<E>, B> app2) {
            final BehaviorBuilder.e<E, A> behaviorbuilder_e = BehaviorBuilder.get(app1);
            final BehaviorBuilder.e<E, B> behaviorbuilder_e1 = BehaviorBuilder.get(app2);
            final BehaviorBuilder.e<E, BiFunction<A, B, R>> behaviorbuilder_e2 = BehaviorBuilder.get(app);

            return BehaviorBuilder.create(new BehaviorBuilder.e<E, R>() {
                @Override
                public R tryTrigger(WorldServer worldserver, E e0, long i) {
                    A a0 = behaviorbuilder_e.tryTrigger(worldserver, e0, i);

                    if (a0 == null) {
                        return null;
                    } else {
                        B b0 = behaviorbuilder_e1.tryTrigger(worldserver, e0, i);

                        if (b0 == null) {
                            return null;
                        } else {
                            BiFunction<A, B, R> bifunction = (BiFunction) behaviorbuilder_e2.tryTrigger(worldserver, e0, i);

                            return bifunction == null ? null : bifunction.apply(a0, b0);
                        }
                    }
                }

                @Override
                public String debugString() {
                    String s = behaviorbuilder_e2.debugString();

                    return s + " * " + behaviorbuilder_e.debugString() + " * " + behaviorbuilder_e1.debugString();
                }

                public String toString() {
                    return this.debugString();
                }
            });
        }

        public <T1, T2, T3, R> BehaviorBuilder<E, R> ap3(App<BehaviorBuilder.c<E>, Function3<T1, T2, T3, R>> app, App<BehaviorBuilder.c<E>, T1> app1, App<BehaviorBuilder.c<E>, T2> app2, App<BehaviorBuilder.c<E>, T3> app3) {
            final BehaviorBuilder.e<E, T1> behaviorbuilder_e = BehaviorBuilder.get(app1);
            final BehaviorBuilder.e<E, T2> behaviorbuilder_e1 = BehaviorBuilder.get(app2);
            final BehaviorBuilder.e<E, T3> behaviorbuilder_e2 = BehaviorBuilder.get(app3);
            final BehaviorBuilder.e<E, Function3<T1, T2, T3, R>> behaviorbuilder_e3 = BehaviorBuilder.get(app);

            return BehaviorBuilder.create(new BehaviorBuilder.e<E, R>() {
                @Override
                public R tryTrigger(WorldServer worldserver, E e0, long i) {
                    T1 t1 = behaviorbuilder_e.tryTrigger(worldserver, e0, i);

                    if (t1 == null) {
                        return null;
                    } else {
                        T2 t2 = behaviorbuilder_e1.tryTrigger(worldserver, e0, i);

                        if (t2 == null) {
                            return null;
                        } else {
                            T3 t3 = behaviorbuilder_e2.tryTrigger(worldserver, e0, i);

                            if (t3 == null) {
                                return null;
                            } else {
                                Function3<T1, T2, T3, R> function3 = (Function3) behaviorbuilder_e3.tryTrigger(worldserver, e0, i);

                                return function3 == null ? null : function3.apply(t1, t2, t3);
                            }
                        }
                    }
                }

                @Override
                public String debugString() {
                    String s = behaviorbuilder_e3.debugString();

                    return s + " * " + behaviorbuilder_e.debugString() + " * " + behaviorbuilder_e1.debugString() + " * " + behaviorbuilder_e2.debugString();
                }

                public String toString() {
                    return this.debugString();
                }
            });
        }

        public <T1, T2, T3, T4, R> BehaviorBuilder<E, R> ap4(App<BehaviorBuilder.c<E>, Function4<T1, T2, T3, T4, R>> app, App<BehaviorBuilder.c<E>, T1> app1, App<BehaviorBuilder.c<E>, T2> app2, App<BehaviorBuilder.c<E>, T3> app3, App<BehaviorBuilder.c<E>, T4> app4) {
            final BehaviorBuilder.e<E, T1> behaviorbuilder_e = BehaviorBuilder.get(app1);
            final BehaviorBuilder.e<E, T2> behaviorbuilder_e1 = BehaviorBuilder.get(app2);
            final BehaviorBuilder.e<E, T3> behaviorbuilder_e2 = BehaviorBuilder.get(app3);
            final BehaviorBuilder.e<E, T4> behaviorbuilder_e3 = BehaviorBuilder.get(app4);
            final BehaviorBuilder.e<E, Function4<T1, T2, T3, T4, R>> behaviorbuilder_e4 = BehaviorBuilder.get(app);

            return BehaviorBuilder.create(new BehaviorBuilder.e<E, R>() {
                @Override
                public R tryTrigger(WorldServer worldserver, E e0, long i) {
                    T1 t1 = behaviorbuilder_e.tryTrigger(worldserver, e0, i);

                    if (t1 == null) {
                        return null;
                    } else {
                        T2 t2 = behaviorbuilder_e1.tryTrigger(worldserver, e0, i);

                        if (t2 == null) {
                            return null;
                        } else {
                            T3 t3 = behaviorbuilder_e2.tryTrigger(worldserver, e0, i);

                            if (t3 == null) {
                                return null;
                            } else {
                                T4 t4 = behaviorbuilder_e3.tryTrigger(worldserver, e0, i);

                                if (t4 == null) {
                                    return null;
                                } else {
                                    Function4<T1, T2, T3, T4, R> function4 = (Function4) behaviorbuilder_e4.tryTrigger(worldserver, e0, i);

                                    return function4 == null ? null : function4.apply(t1, t2, t3, t4);
                                }
                            }
                        }
                    }
                }

                @Override
                public String debugString() {
                    String s = behaviorbuilder_e4.debugString();

                    return s + " * " + behaviorbuilder_e.debugString() + " * " + behaviorbuilder_e1.debugString() + " * " + behaviorbuilder_e2.debugString() + " * " + behaviorbuilder_e3.debugString();
                }

                public String toString() {
                    return this.debugString();
                }
            });
        }

        private static final class a<E extends EntityLiving> implements com.mojang.datafixers.kinds.Applicative.Mu {

            private a() {}
        }
    }

    private interface e<E extends EntityLiving, R> {

        @Nullable
        R tryTrigger(WorldServer worldserver, E e0, long i);

        String debugString();
    }

    private static final class f<E extends EntityLiving> extends BehaviorBuilder<E, Unit> {

        f(final Trigger<? super E> trigger) {
            super(new BehaviorBuilder.e<E, Unit>() {
                @Nullable
                @Override
                public Unit tryTrigger(WorldServer worldserver, E e0, long i) {
                    return trigger.trigger(worldserver, e0, i) ? Unit.INSTANCE : null;
                }

                @Override
                public String debugString() {
                    return "T[" + trigger + "]";
                }
            });
        }
    }

    private static final class a<E extends EntityLiving, A> extends BehaviorBuilder<E, A> {

        a(A a0) {
            this(a0, () -> {
                return "C[" + a0 + "]";
            });
        }

        a(final A a0, final Supplier<String> supplier) {
            super(new BehaviorBuilder.e<E, A>() {
                @Override
                public A tryTrigger(WorldServer worldserver, E e0, long i) {
                    return a0;
                }

                @Override
                public String debugString() {
                    return (String) supplier.get();
                }

                public String toString() {
                    return this.debugString();
                }
            });
        }
    }

    private static final class d<E extends EntityLiving, F extends K1, Value> extends BehaviorBuilder<E, MemoryAccessor<F, Value>> {

        d(final MemoryCondition<F, Value> memorycondition) {
            super(new BehaviorBuilder.e<E, MemoryAccessor<F, Value>>() {
                @Override
                public MemoryAccessor<F, Value> tryTrigger(WorldServer worldserver, E e0, long i) {
                    BehaviorController<?> behaviorcontroller = e0.getBrain();
                    Optional<Value> optional = behaviorcontroller.getMemoryInternal(memorycondition.memory());

                    return optional == null ? null : memorycondition.createAccessor(behaviorcontroller, optional);
                }

                @Override
                public String debugString() {
                    return "M[" + memorycondition + "]";
                }

                public String toString() {
                    return this.debugString();
                }
            });
        }
    }

    public static final class c<E extends EntityLiving> implements K1 {

        public c() {}
    }
}
