package net.minecraft.world.entity.ai.village;

public interface ReputationEvent {

    ReputationEvent ZOMBIE_VILLAGER_CURED = register("zombie_villager_cured");
    ReputationEvent GOLEM_KILLED = register("golem_killed");
    ReputationEvent VILLAGER_HURT = register("villager_hurt");
    ReputationEvent VILLAGER_KILLED = register("villager_killed");
    ReputationEvent TRADE = register("trade");

    static ReputationEvent register(final String s) {
        return new ReputationEvent() {
            public String toString() {
                return s;
            }
        };
    }
}
