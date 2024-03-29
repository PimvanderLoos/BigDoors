package nl.pim16aap2.bigDoors.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Represents a lazily initialized object.
 * <p>
 * Initialization is thread-safe.
 *
 * @param <T>
 *     The type of the object to initialize lazily.
 */
public final class LazyInit<T>
{
    private final @NotNull Supplier<T> supplier;
    private volatile @Nullable T obj;

    /**
     * @param supplier
     *     The supplier to use to create the instance of the object when needed.
     *     <p>
     *     Note that the return value may not be null!
     */
    public LazyInit(@NotNull Supplier<T> supplier)
    {
        this.supplier = supplier;
    }

    /**
     * Gets the lazily initialized object. If it does not exist yet, a new instance will be created using
     * {@link #supplier}.
     *
     * @return The lazily initialized object.
     */
    public T get()
    {
        @Nullable T tmp = obj;
        if (tmp != null)
            return tmp;

        synchronized (this)
        {
            tmp = obj;
            if (tmp == null)
                tmp = obj = supplier.get();
            return Objects.requireNonNull(tmp, "Instance obtained from supplier must not be null!");
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Note that calling this method will result in {@link #get()} being called for both objects if the other object is
     * also a {@link LazyInit}.
     */
    @Override
    public boolean equals(final Object o)
    {
        if (o == this)
            return true;
        if (!(o instanceof LazyInit<?>))
            return false;
        final @NotNull LazyInit<?> other = (LazyInit<?>) o;
        return Objects.equals(this.supplier, other.supplier) && Objects.equals(this.get(), other.get());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Note that calling this method will result in {@link #get()} being called.
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(get(), this.supplier);
    }

    @Override
    public @NotNull String toString()
    {
        return "LazyInit(obj=" + obj + ")";
    }
}
