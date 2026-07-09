package invalid.myask.feelingpeaky.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;

public class MultiBlockChangeHandler_S22b implements IMessageHandler<MultiBlockChangeMessage_S22b, IMessage> {
    @Override
    public IMessage onMessage(MultiBlockChangeMessage_S22b message, MessageContext ctx) {
        message.deploy(Minecraft.getMinecraft().theWorld);
        return null;
    }
}
