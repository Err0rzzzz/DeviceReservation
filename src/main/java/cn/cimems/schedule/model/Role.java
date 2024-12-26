// package cn.cimems.schedule.model;

// public enum Role {
//     SUPER_ADMIN, ADMIN, USER
// }
package cn.cimems.schedule.model;

public enum Role {
    SUPER_ADMIN(2),
    ADMIN(1),
    USER(0);

    private final int level;

    Role(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public boolean canApprove(Role targetRole) {
        return this.level > targetRole.level;
    }
}
