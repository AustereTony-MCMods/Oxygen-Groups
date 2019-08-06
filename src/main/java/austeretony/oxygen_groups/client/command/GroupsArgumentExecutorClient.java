package austeretony.oxygen_groups.client.command;

import java.util.Set;

import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.common.api.command.AbstractArgumentExecutor;
import austeretony.oxygen.common.api.command.ArgumentParameter;
import austeretony.oxygen.common.command.IArgumentParameter;
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
    public void getParams(Set<IArgumentParameter> params) {        
        params.add(new ArgumentParameter(ACTION_RESET_DATA));
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, Set<IArgumentParameter> params) throws CommandException {
        for (IArgumentParameter param : params) {
            if (param.getBaseName().equals(ACTION_RESET_DATA)) {
                GroupsManagerClient.instance().reset();
                GroupsManagerClient.instance().downloadGroupDataSynced();
                ClientReference.showMessage("oxygen_groups.command.dataReset");
            }
        }
    }
}