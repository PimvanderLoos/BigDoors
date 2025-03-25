package nl.pim16aap2.bigDoors.reflection;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a {@link ParameterizedType} implementation.
 */
public class ParameterizedTypeImpl implements ParameterizedType
{
    private final @Nullable Type ownerType;
    private final Type rawType;
    private final Type[] typeArguments;

    public ParameterizedTypeImpl(@Nullable Type ownerType, Class<?> rawType, Type[] typeArguments)
    {
        this.ownerType = ownerType;
        this.rawType = Objects.requireNonNull(rawType);
        this.typeArguments = typeArguments;

        if (typeArguments.length != rawType.getTypeParameters().length)
            throw new IllegalArgumentException(
                "Type arguments length does not match the number of type parameters of the raw type.");
    }

    public ParameterizedTypeImpl(Class<?> rawType, Type... typeArguments)
    {
        this(null, rawType, typeArguments);
    }

    @Override
    public Type @NotNull [] getActualTypeArguments()
    {
        return this.typeArguments;
    }

    @Override
    public @NotNull Type getRawType()
    {
        return this.rawType;
    }

    @Override
    public @Nullable Type getOwnerType()
    {
        return this.ownerType;
    }

    @Override
    public String toString()
    {
        return "ParameterizedTypeImpl{" +
            "ownerType=" + ownerType +
            ", rawType=" + rawType +
            ", typeArguments=" +
            Stream
                .of(typeArguments)
                .map(Type::getTypeName)
                .collect(Collectors.joining(", ", "[", "]")) +
            '}';
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(ownerType, rawType, Arrays.hashCode(typeArguments));
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        final ParameterizedTypeImpl other = (ParameterizedTypeImpl) obj;
        return Objects.equals(ownerType, other.ownerType) &&
            Objects.equals(rawType, other.rawType) &&
            Arrays.equals(typeArguments, other.typeArguments);
    }
}
