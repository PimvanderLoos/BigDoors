package net.minecraft.world.entity.ai.behavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.level.block.Block;

public class LongJumpToPreferredBlock<E extends EntityInsentient> extends LongJumpToRandomPos<E> {

    private final TagKey<Block> preferredBlockTag;
    private final float preferredBlocksChance;
    private final List<LongJumpToRandomPos.a> notPrefferedJumpCandidates = new ArrayList();
    private boolean currentlyWantingPreferredOnes;

    public LongJumpToPreferredBlock(UniformInt uniformint, int i, int j, float f, Function<E, SoundEffect> function, TagKey<Block> tagkey, float f1, BiPredicate<E, BlockPosition> bipredicate) {
        super(uniformint, i, j, f, function, bipredicate);
        this.preferredBlockTag = tagkey;
        this.preferredBlocksChance = f1;
    }

    @Override
    protected void start(WorldServer worldserver, E e0, long i) {
        super.start(worldserver, e0, i);
        this.notPrefferedJumpCandidates.clear();
        this.currentlyWantingPreferredOnes = e0.getRandom().nextFloat() < this.preferredBlocksChance;
    }

    @Override
    protected Optional<LongJumpToRandomPos.a> getJumpCandidate(WorldServer worldserver) {
        if (!this.currentlyWantingPreferredOnes) {
            return super.getJumpCandidate(worldserver);
        } else {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            while (!this.jumpCandidates.isEmpty()) {
                Optional<LongJumpToRandomPos.a> optional = super.getJumpCandidate(worldserver);

                if (optional.isPresent()) {
                    LongJumpToRandomPos.a longjumptorandompos_a = (LongJumpToRandomPos.a) optional.get();

                    if (worldserver.getBlockState(blockposition_mutableblockposition.setWithOffset(longjumptorandompos_a.getJumpTarget(), EnumDirection.DOWN)).is(this.preferredBlockTag)) {
                        return optional;
                    }

                    this.notPrefferedJumpCandidates.add(longjumptorandompos_a);
                }
            }

            if (!this.notPrefferedJumpCandidates.isEmpty()) {
                return Optional.of((LongJumpToRandomPos.a) this.notPrefferedJumpCandidates.remove(0));
            } else {
                return Optional.empty();
            }
        }
    }
}
