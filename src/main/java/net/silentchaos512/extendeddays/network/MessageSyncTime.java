package net.silentchaos512.extendeddays.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.extendeddays.event.TimeEvents;
import net.silentchaos512.lib.network.MessageSL;


public class MessageSyncTime extends MessageSL {

  public long worldTime;
  public int extendedTime;

  public MessageSyncTime() {

  }

  public MessageSyncTime(long worldTime, int extendedTime) {

    this.worldTime = worldTime;
    this.extendedTime = extendedTime;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IMessage handleMessage(MessageContext context) {

    TimeEvents.INSTANCE.syncTimeFromPacket(this);

    return null;
  }
}
