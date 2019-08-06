package austeretony.oxygen_groups.client.gui.group;

import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.client.sync.gui.api.IGUIHandlerClient;

public class GroupMenuGUIHandler implements IGUIHandlerClient {

    @Override
    public void open() {
        ClientReference.displayGuiScreen(new GroupMenuGUIScreen());
    }
}
