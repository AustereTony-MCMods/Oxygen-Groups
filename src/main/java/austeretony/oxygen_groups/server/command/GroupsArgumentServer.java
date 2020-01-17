package austeretony.oxygen_groups.server.command;

import java.util.UUID;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.command.ArgumentExecutor;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.server.OxygenManagerServer;
import austeretony.oxygen_core.server.OxygenPlayerData;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_groups.common.Group;
import austeretony.oxygen_groups.common.main.EnumGroupsStatusMessage;
import austeretony.oxygen_groups.common.main.GroupsMain;
import austeretony.oxygen_groups.common.network.client.CPSyncGroup;
import austeretony.oxygen_groups.server.GroupsManagerServer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class GroupsArgumentServer implements ArgumentExecutor {

    @Override
    public String getName() {
        return "groups";
    }

    @Override
    public void process(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 2) {
            if (args[1].equals("-sync-group")) {
                EntityPlayerMP senderPlayerMP = CommandBase.getCommandSenderAsPlayer(sender);
                UUID senderUUID = CommonReference.getPersistentUUID(senderPlayerMP);
                if (OxygenHelperServer.isNetworkRequestAvailable(senderUUID, GroupsMain.GROUP_MANAGEMENT_REQUEST_ID)) {
                    if (GroupsManagerServer.instance().getGroupsDataContainer().haveGroup(senderUUID)) {
                        Group group = GroupsManagerServer.instance().getGroupsDataContainer().getGroup(senderUUID); 
                        OxygenPlayerData senderData = OxygenHelperServer.getOxygenPlayerData(senderUUID);
                        if (group == null || senderData == null) return;
                        OxygenManagerServer.instance().getSharedDataManager().syncObservedPlayersData(senderPlayerMP);
                        for (UUID memberUUID : group.getMembers()) {
                            if (!memberUUID.equals(senderUUID) && OxygenHelperServer.isPlayerOnline(memberUUID)) {
                                senderData.addTrackedEntity(memberUUID, true);
                                OxygenHelperServer.sendPlayerSharedData(OxygenHelperServer.getPlayerSharedData(memberUUID), senderPlayerMP);
                            }
                        }
                        OxygenMain.network().sendTo(new CPSyncGroup(group), senderPlayerMP);

                        OxygenHelperServer.sendStatusMessage(senderPlayerMP, GroupsMain.GROUPS_MOD_INDEX, EnumGroupsStatusMessage.GROUP_SYNCHRONIZED.ordinal());
                    }
                }
            }
        }
    }
}
