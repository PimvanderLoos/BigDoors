package net.minecraft.server.network;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.server.level.EntityPlayer;

public record FilteredText<T> (T raw, @Nullable T filtered) {

    public static final FilteredText<String> EMPTY_STRING = passThrough("");

    public static <T> FilteredText<T> passThrough(T t0) {
        return new FilteredText<>(t0, t0);
    }

    public static <T> FilteredText<T> fullyFiltered(T t0) {
        return new FilteredText<>(t0, (Object) null);
    }

    public <U> FilteredText<U> map(Function<T, U> function) {
        return new FilteredText<>(function.apply(this.raw), SystemUtils.mapNullable(this.filtered, function));
    }

    public boolean isFiltered() {
        return !this.raw.equals(this.filtered);
    }

    public boolean isFullyFiltered() {
        return this.filtered == null;
    }

    public T filteredOrElse(T t0) {
        return this.filtered != null ? this.filtered : t0;
    }

    @Nullable
    public T filter(EntityPlayer entityplayer, EntityPlayer entityplayer1) {
        return entityplayer.shouldFilterMessageTo(entityplayer1) ? this.filtered : this.raw;
    }

    @Nullable
    public T filter(CommandListenerWrapper commandlistenerwrapper, EntityPlayer entityplayer) {
        EntityPlayer entityplayer1 = commandlistenerwrapper.getPlayer();

        return entityplayer1 != null ? this.filter(entityplayer1, entityplayer) : this.raw;
    }
}
