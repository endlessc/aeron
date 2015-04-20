package uk.co.real_logic.aeron.tools;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class StatsNetstatOutput implements StatsOutput
{
  private HashMap<String, PublisherStats> pubs = null;
  private HashMap<String, SubscriberStats> subs = null;

  public StatsNetstatOutput()
  {
    pubs = new HashMap<String, PublisherStats>();
    subs = new HashMap<String, SubscriberStats>();
  }

  @Override
public void format(final String[] keys, final long[] vals) throws Exception
  {
    for (int i = 0; i < keys.length; i++)
    {
      if (keys[i].startsWith("receiver") || keys[i].startsWith("subscriber"))
      {
        processSubscriberInfo(keys[i], vals[i]);
      }
      else if (keys[i].startsWith("sender") || keys[i].startsWith("publisher"))
      {
        processPublisherInfo(keys[i], vals[i]);
      }
    }

    System.out.println("Aeron Channel Statistics");
    Iterator itr = pubs.entrySet().iterator();
    System.out.println("Publishers");
    System.out.format("%1$5s %2$8s %3$8s %4$16s %5$10s\n", "proto", "pos", "limit", "location", "session");
    while (itr.hasNext())
    {
      final Map.Entry pair = (Map.Entry)itr.next();
      System.out.print((pair.getValue()));
    }

    System.out.println();

    itr = subs.entrySet().iterator();
    System.out.println("Subscribers");

    System.out.format("%1$5s %2$8s %3$8s %4$16s %5$10s\n", "proto", "pos", "hwm", "location", "session");
    while (itr.hasNext())
    {
      final Map.Entry pair = (Map.Entry)itr.next();
      System.out.print((pair.getValue()));
    }
    System.out.println("-----------------------------------------------------------\n");
  }

  @Override
public void close() throws Exception
  {

  }

  private void processSubscriberInfo(final String key, final long val)
  {
    SubscriberStats sub = null;
    final String senderInfoType = key.substring(key.indexOf(' ') + 1, key.indexOf(':'));
    final String current = key.substring(key.indexOf(':') + 2);
    final String channelInfo = current;

    if (subs.containsKey(channelInfo))
    {
      sub = subs.get(channelInfo);
    }
    else
    {
      sub = new SubscriberStats(channelInfo);
    }

    updateField(sub, senderInfoType, val);
    subs.put(channelInfo, sub);
  }

  private void processPublisherInfo(final String key, final long val)
  {
    PublisherStats pub = null;
    final String senderInfoType = key.substring(key.indexOf(' ') + 1, key.indexOf(':'));
    final String current = key.substring(key.indexOf(':') + 2);
    final String channelInfo = current;

    if (pubs.containsKey(channelInfo))
    {
      pub = pubs.get(channelInfo);
    }
    else
    {
      pub = new PublisherStats(channelInfo);
    }

    updateField(pub, senderInfoType, val);
    pubs.put(channelInfo, pub);
  }

  private void updateField(final PublisherStats pub, final String key, final long val)
  {
    if (key.equalsIgnoreCase("limit"))
    {
      pub.setLimit(val);
    }
    else if (key.equalsIgnoreCase("pos"))
    {
      pub.setPos(val);
    }
  }

  private void updateField(final SubscriberStats sub, final String key, final long val)
  {
    if (key.equalsIgnoreCase("hwm"))
    {
      sub.setHWM(val);
    }
    else if (key.equalsIgnoreCase("pos"))
    {
      sub.setPos(val);
    }
  }
}
