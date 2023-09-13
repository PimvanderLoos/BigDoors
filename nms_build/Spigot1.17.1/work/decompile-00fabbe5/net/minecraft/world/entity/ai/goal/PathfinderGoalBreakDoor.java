package net.minecraft.world.entity.ai.goal;

import java.util.function.Predicate;
import net.minecraft.core.IPosition;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;

public class PathfinderGoalBreakDoor extends PathfinderGoalDoorInteract {

    private static final int DEFAULT_DOOR_BREAK_TIME = 240;
    private final Predicate<EnumDifficulty> validDifficulties;
    protected int breakTime;
    protected int lastBreakProgress;
    protected int doorBreakTime;

    public PathfinderGoalBreakDoor(EntityInsentient entityinsentient, Predicate<EnumDifficulty> predicate) {
        super(entityinsentient);
        this.lastBreakProgress = -1;
        this.doorBreakTime = -1;
        this.validDifficulties = predicate;
    }

    public PathfinderGoalBreakDoor(EntityInsentient entityinsentient, int i, Predicate<EnumDifficulty> predicate) {
        this(entityinsentient, predicate);
        this.doorBreakTime = i;
    }

    protected int f() {
        return Math.max(240, this.doorBreakTime);
    }

    @Override
    public boolean a() {
        return !super.a() ? false : (!this.mob.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ? false : this.a(this.mob.level.getDifficulty()) && !this.g());
    }

    @Override
    public void c() {
        super.c();
        this.breakTime = 0;
    }

    @Override
    public boolean b() {
        return this.breakTime <= this.f() && !this.g() && this.doorPos.a((IPosition) this.mob.getPositionVector(), 2.0D) && this.a(this.mob.level.getDifficulty());
    }

    @Override
    public void d() {
        super.d();
        this.mob.level.a(this.mob.getId(), this.doorPos, -1);
    }

    @Override
    public void e() {
        super.e();
        if (this.mob.getRandom().nextInt(20) == 0) {
            this.mob.level.triggerEffect(1019, this.doorPos, 0);
            if (!this.mob.swinging) {
                this.mob.swingHand(this.mob.getRaisedHand());
            }
        }

        ++this.breakTime;
        int i = (int) ((float) this.breakTime / (float) this.f() * 10.0F);

        if (i != this.lastBreakProgress) {
            this.mob.level.a(this.mob.getId(), this.doorPos, i);
            this.lastBreakProgress = i;
        }

        if (this.breakTime == this.f() && this.a(this.mob.level.getDifficulty())) {
            this.mob.level.a(this.doorPos, false);
            this.mob.level.triggerEffect(1021, this.doorPos, 0);
            this.mob.level.triggerEffect(2001, this.doorPos, Block.getCombinedId(this.mob.level.getType(this.doorPos)));
        }

    }

    private boolean a(EnumDifficulty enumdifficulty) {
        return this.validDifficulties.test(enumdifficulty);
    }
}
