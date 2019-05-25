package austeretony.oxygen_groups.common.main;

import java.util.UUID;

import austeretony.oxygen.common.api.process.AbstractTemporaryProcess;
import austeretony.oxygen_groups.common.GroupsManagerServer;
import austeretony.oxygen_groups.common.config.GroupsConfig;

public class KickPlayerVotingProcess extends AbstractTemporaryProcess {

    public final long groupId;

    public final UUID playerUUID;

    public KickPlayerVotingProcess(long groupId, UUID playerUUID) {
        this.groupId = groupId;
        this.playerUUID = playerUUID;
    }

    @Override
    public int getExpireTime() {
        return GroupsConfig.VOTE_KICK_REQUEST_EXPIRE_TIME.getIntValue();
    }

    @Override
    public void expired() {
        GroupsManagerServer.instance().stopKickPlayerVoting(this.groupId, this.playerUUID);
    }
}