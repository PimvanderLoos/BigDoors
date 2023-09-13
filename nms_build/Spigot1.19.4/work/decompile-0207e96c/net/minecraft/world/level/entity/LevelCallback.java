package net.minecraft.world.level.entity;

public interface LevelCallback<T> {

    void onCreated(T t0);

    void onDestroyed(T t0);

    void onTickingStart(T t0);

    void onTickingEnd(T t0);

    void onTrackingStart(T t0);

    void onTrackingEnd(T t0);

    void onSectionChange(T t0);
}
