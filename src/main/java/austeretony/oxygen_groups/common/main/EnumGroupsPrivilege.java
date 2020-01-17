package austeretony.oxygen_groups.common.main;

import austeretony.oxygen_core.common.EnumValueType;
import austeretony.oxygen_core.common.privilege.PrivilegeRegistry;

public enum EnumGroupsPrivilege {

    ALLOW_GROUP_CREATION("group:allowGroupCreation", 200, EnumValueType.BOOLEAN);

    private final String name;

    private final int id;

    private final EnumValueType type;

    EnumGroupsPrivilege(String name, int id, EnumValueType type) {
        this.name = name;
        this.id = id;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public int id() {
        return id;
    }

    public static void register() {
        for (EnumGroupsPrivilege privilege : EnumGroupsPrivilege.values())
            PrivilegeRegistry.registerPrivilege(privilege.name, privilege.id, privilege.type);
    }
}
