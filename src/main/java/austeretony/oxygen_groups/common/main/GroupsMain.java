package austeretony.oxygen_groups.common.main;

import austeretony.oxygen_core.client.api.OxygenGUIHelper;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.api.PlayerInteractionMenuHelper;
import austeretony.oxygen_core.client.command.CommandOxygenClient;
import austeretony.oxygen_core.client.gui.settings.SettingsScreen;
import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.api.OxygenHelperCommon;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_core.server.command.CommandOxygenServer;
import austeretony.oxygen_core.server.network.NetworkRequestsRegistryServer;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import austeretony.oxygen_groups.client.GroupsStatusMessagesHandler;
import austeretony.oxygen_groups.client.chat.GroupChatChannelProperties;
import austeretony.oxygen_groups.client.command.GroupsArgumentClient;
import austeretony.oxygen_groups.client.event.GroupsEventsClient;
import austeretony.oxygen_groups.client.gui.context.InviteToGroupContextAction;
import austeretony.oxygen_groups.client.gui.group.GroupMenuScreen;
import austeretony.oxygen_groups.client.gui.interaction.InviteToGroupInteractionExecutor;
import austeretony.oxygen_groups.client.gui.overlay.GroupMembersMarkOverlay;
import austeretony.oxygen_groups.client.gui.overlay.GroupOverlay;
import austeretony.oxygen_groups.client.gui.settings.GroupsSettingsContainer;
import austeretony.oxygen_groups.client.settings.EnumGroupsClientSetting;
import austeretony.oxygen_groups.client.settings.gui.EnumGroupsGUISetting;
import austeretony.oxygen_groups.common.Group;
import austeretony.oxygen_groups.common.config.GroupsConfig;
import austeretony.oxygen_groups.common.network.client.CPAddNewGroupMember;
import austeretony.oxygen_groups.common.network.client.CPLeaveGroup;
import austeretony.oxygen_groups.common.network.client.CPRemovePlayerFromGroup;
import austeretony.oxygen_groups.common.network.client.CPSyncGroup;
import austeretony.oxygen_groups.common.network.client.CPUpdateGroupLeader;
import austeretony.oxygen_groups.common.network.server.SPGroupMemberOperation;
import austeretony.oxygen_groups.common.network.server.SPInviteToGroup;
import austeretony.oxygen_groups.common.network.server.SPLeaveGroup;
import austeretony.oxygen_groups.server.GroupsManagerServer;
import austeretony.oxygen_groups.server.chat.GroupChatChannel;
import austeretony.oxygen_groups.server.command.GroupsArgumentServer;
import austeretony.oxygen_groups.server.event.GroupsEventsServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(
        modid = GroupsMain.MODID, 
        name = GroupsMain.NAME, 
        version = GroupsMain.VERSION,
        dependencies = "required-after:oxygen_core@[0.11.3,);",
        certificateFingerprint = "@FINGERPRINT@",
        updateJSON = GroupsMain.VERSIONS_FORGE_URL)
public class GroupsMain {

    public static final String 
    MODID = "oxygen_groups", 
    NAME = "Oxygen: Groups", 
    VERSION = "0.11.2", 
    VERSION_CUSTOM = VERSION + ":beta:0",
    GAME_VERSION = "1.12.2",
    VERSIONS_FORGE_URL = "https://raw.githubusercontent.com/AustereTony-MCMods/Oxygen-Groups/info/mod_versions_forge.json";

    public static final int 
    GROUPS_MOD_INDEX = 2,

    GROUP_INVITATION_REQUEST_ID = 20,
    READINESS_CHECK_REQUEST_ID = 21,
    VOTE_KICK_REQUEST_ID = 22,

    GROUP_MENU_SCREEN_ID = 20,

    GROUP_MANAGEMENT_REQUEST_ID = 20;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        OxygenHelperCommon.registerConfig(new GroupsConfig());
        if (event.getSide() == Side.CLIENT)
            CommandOxygenClient.registerArgument(new GroupsArgumentClient());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        this.initNetwork();
        GroupsManagerServer.create();
        CommonReference.registerEvent(new GroupsEventsServer());
        NetworkRequestsRegistryServer.registerRequest(GROUP_MANAGEMENT_REQUEST_ID, 1000);
        OxygenHelperServer.registerChatChannel(new GroupChatChannel());
        CommandOxygenServer.registerArgument(new GroupsArgumentServer());
        if (GroupsConfig.DISABLE_PVP_FOR_GROUP_MEMBERS.asBoolean())
            OxygenHelperServer.registerRestrictedAttacksValidator((attackerUUID, attackedUUID)->{
                Group group = GroupsManagerServer.instance().getGroupsDataContainer().getGroup(attackerUUID);
                if (group != null)
                    return !group.isMember(attackedUUID);
                return false;
            });
        EnumGroupsPrivilege.register();
        if (event.getSide() == Side.CLIENT) {
            GroupsManagerClient.create();       
            CommonReference.registerEvent(new GroupsEventsClient());
            CommonReference.registerEvent(new GroupMembersMarkOverlay());
            OxygenGUIHelper.registerScreenId(GROUP_MENU_SCREEN_ID);
            PlayerInteractionMenuHelper.registerInteractionMenuEntry(new InviteToGroupInteractionExecutor());
            OxygenGUIHelper.registerContextAction(50, new InviteToGroupContextAction());//50 - players list menu id
            OxygenGUIHelper.registerContextAction(60, new InviteToGroupContextAction());//60 - friends list menu id
            OxygenGUIHelper.registerContextAction(110, new InviteToGroupContextAction());//110 - guild menu id
            OxygenGUIHelper.registerOverlay(new GroupOverlay());
            OxygenGUIHelper.registerOxygenMenuEntry(GroupMenuScreen.GROUP_MENU_ENTRY);
            OxygenHelperClient.registerStatusMessagesHandler(new GroupsStatusMessagesHandler());
            OxygenHelperClient.registerSharedDataSyncListener(GROUP_MENU_SCREEN_ID, GroupsManagerClient.instance().getGroupMenuManager()::sharedDataSynchronized);
            OxygenHelperClient.registerChatChannelProperties(new GroupChatChannelProperties());
            EnumGroupsClientSetting.register();
            EnumGroupsGUISetting.register();
            SettingsScreen.registerSettingsContainer(new GroupsSettingsContainer());
        }
    }

    private void initNetwork() {
        OxygenMain.network().registerPacket(CPLeaveGroup.class);
        OxygenMain.network().registerPacket(CPSyncGroup.class);
        OxygenMain.network().registerPacket(CPAddNewGroupMember.class);
        OxygenMain.network().registerPacket(CPRemovePlayerFromGroup.class);
        OxygenMain.network().registerPacket(CPUpdateGroupLeader.class);

        OxygenMain.network().registerPacket(SPInviteToGroup.class);
        OxygenMain.network().registerPacket(SPLeaveGroup.class);
        OxygenMain.network().registerPacket(SPGroupMemberOperation.class);
    }
}
