package austeretony.groups.common.main;

import java.util.UUID;

import austeretony.groups.common.GroupsManagerServer;
import austeretony.groups.common.config.GroupsConfig;
import austeretony.oxygen.common.api.process.AbstractTemporaryProcess;

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