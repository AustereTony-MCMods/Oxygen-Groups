package austeretony.oxygen_groups.client.command;

import java.util.Set;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.common.api.command.AbstractArgumentExecutor;
import austeretony.oxygen_core.common.api.command.ArgumentParameterImpl;
import austeretony.oxygen_core.common.command.ArgumentParameter;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class GroupsArgumentExecutorClient extends AbstractArgumentExecutor {

    public static final String ACTION_RESET_DATA = "reset-data";

    public GroupsArgumentExecutorClient(String argument, boolean hasParams) {
        super(argument, hasParams);
    }

    @Override
    public void getParams(Set<ArgumentParameter> params) {        
        params.add(new ArgumentParameterImpl(ACTION_RESET_DATA));
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, Set<ArgumentParameter> params) throws CommandException {
        for (ArgumentParameter param : params) {
            if (param.getBaseName().equals(ACTION_RESET_DATA)) {
                GroupsManagerClient.instance().init();
                ClientReference.showChatMessage("oxygen_groups.command.dataReset");
            }
        }
    }
}
