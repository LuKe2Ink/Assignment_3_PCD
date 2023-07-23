package it.unibo.ppc.MOM;

import java.io.IOException;

public interface MessageBus {
    public void initBroker () throws IOException;
    public String getBrokerName ();
    public void addChannel (String name) throws IOException;
    public void publishChannelMessage (String name, Object message) throws IOException;
}
