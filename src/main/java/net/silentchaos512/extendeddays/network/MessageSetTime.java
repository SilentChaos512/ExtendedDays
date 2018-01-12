package net.silentchaos512.extendeddays.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.extendeddays.ExtendedDays;
import net.silentchaos512.extendeddays.event.TimeEvents;
import net.silentchaos512.lib.network.MessageSL;

public class MessageSetTime extends MessageSL {

  public long worldTime;
  public int extendedTime;

  public MessageSetTime() {

  }

  public MessageSetTime(long worldTime, int extendedTime) {

  }

  @Override
  public IMessage handleMessage(MessageContext context) {

    ExtendedDays.logHelper.debug("MessageSetTime#handleMessage");

    TimeEvents.INSTANCE.setTimeFromPacket(this);

    return null;
  }
}
