package austeretony.oxygen_groups.client.command;

import java.util.concurrent.TimeUnit;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.common.command.ArgumentExecutor;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import austeretony.oxygen_groups.client.gui.group.GroupMenuScreen;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class GroupsArgumentClient implements ArgumentExecutor {

    @Override
    public String getName() {
        return "groups";
    }

    @Override
    public void process(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 1)
            OxygenHelperClient.scheduleTask(()->this.openMenu(), 100L, TimeUnit.MILLISECONDS);
        else if (args.length == 2) {
            if (args[1].equals("-reset-data")) {
                GroupsManagerClient.instance().init();
                ClientReference.showChatMessage("oxygen_groups.command.dataReset");
            }
        }
    }

    private void openMenu() {
        ClientReference.delegateToClientThread(()->ClientReference.displayGuiScreen(new GroupMenuScreen()));
    }
}
