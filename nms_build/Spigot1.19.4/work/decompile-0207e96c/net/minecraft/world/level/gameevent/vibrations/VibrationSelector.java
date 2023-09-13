package net.minecraft.world.level.gameevent.vibrations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;

public class VibrationSelector {

    public static final Codec<VibrationSelector> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(VibrationInfo.CODEC.optionalFieldOf("event").forGetter((vibrationselector) -> {
            return vibrationselector.currentVibrationData.map(Pair::getLeft);
        }), Codec.LONG.fieldOf("tick").forGetter((vibrationselector) -> {
            return (Long) vibrationselector.currentVibrationData.map(Pair::getRight).orElse(-1L);
        })).apply(instance, VibrationSelector::new);
    });
    private Optional<Pair<VibrationInfo, Long>> currentVibrationData;

    public VibrationSelector(Optional<VibrationInfo> optional, long i) {
        this.currentVibrationData = optional.map((vibrationinfo) -> {
            return Pair.of(vibrationinfo, i);
        });
    }

    public VibrationSelector() {
        this.currentVibrationData = Optional.empty();
    }

    public void addCandidate(VibrationInfo vibrationinfo, long i) {
        if (this.shouldReplaceVibration(vibrationinfo, i)) {
            this.currentVibrationData = Optional.of(Pair.of(vibrationinfo, i));
        }

    }

    private boolean shouldReplaceVibration(VibrationInfo vibrationinfo, long i) {
        if (this.currentVibrationData.isEmpty()) {
            return true;
        } else {
            Pair<VibrationInfo, Long> pair = (Pair) this.currentVibrationData.get();
            long j = (Long) pair.getRight();

            if (i != j) {
                return false;
            } else {
                VibrationInfo vibrationinfo1 = (VibrationInfo) pair.getLeft();

                return vibrationinfo.distance() < vibrationinfo1.distance() ? true : (vibrationinfo.distance() > vibrationinfo1.distance() ? false : VibrationListener.getGameEventFrequency(vibrationinfo.gameEvent()) > VibrationListener.getGameEventFrequency(vibrationinfo1.gameEvent()));
            }
        }
    }

    public Optional<VibrationInfo> chosenCandidate(long i) {
        return this.currentVibrationData.isEmpty() ? Optional.empty() : ((Long) ((Pair) this.currentVibrationData.get()).getRight() < i ? Optional.of((VibrationInfo) ((Pair) this.currentVibrationData.get()).getLeft()) : Optional.empty());
    }

    public void startOver() {
        this.currentVibrationData = Optional.empty();
    }
}
