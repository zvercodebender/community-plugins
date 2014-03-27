package com.xebialabs.deployit.plugins.byoc.steps;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class FindIpAddressTest {

    String TEST_1 = 
        "eth0      Link encap:Ethernet  HWaddr 08:00:27:12:96:98\n" +
        "  inet addr:10.0.2.15  Bcast:10.0.2.255  Mask:255.255.255.0\n" +
        "  inet6 addr: fe80::a00:27ff:fe12:9698/64 Scope:Link\n" +
        "  UP BROADCAST RUNNING MULTICAST  MTU:1500  Metric:1\n" +
        "  RX packets:4015 errors:0 dropped:0 overruns:0 frame:0\n" +
        "  TX packets:2355 errors:0 dropped:0 overruns:0 carrier:0\n" +
        "  collisions:0 txqueuelen:1000\n" +
        "  RX bytes:4733719 (4.7 MB)  TX bytes:189563 (189.5 KB)\n\n" +
        "eth1      Link encap:Ethernet  HWaddr 08:00:27:12:96:98\n" +
        "  inet addr:10.0.2.16  Bcast:10.0.2.255  Mask:255.255.255.0\n" +
        "  inet6 addr: fe80::a00:27ff:fe12:9698/64 Scope:Link\n" +
        "  UP BROADCAST RUNNING MULTICAST  MTU:1500  Metric:1\n" +
        "  RX packets:4015 errors:0 dropped:0 overruns:0 frame:0\n" +
        "  TX packets:2355 errors:0 dropped:0 overruns:0 carrier:0\n" +
        "  collisions:0 txqueuelen:1000\n" +
        "  RX bytes:4733719 (4.7 MB)  TX bytes:189563 (189.5 KB)\n";
    
    private Pattern eth0Pattern;
    private Pattern eth1Pattern;

    @Before
    public void setupPattern() {
        eth0Pattern = Pattern.compile("eth0.*?\\s*inet addr:(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})\\s+", Pattern.MULTILINE);
        eth1Pattern = Pattern.compile("eth1.*?\\s*inet addr:(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})\\s+", Pattern.MULTILINE);
    }
    
    @Test
    public void testRegexpOnString() {
        Matcher matcher = eth0Pattern.matcher(TEST_1);
        Assert.assertTrue(matcher.find());
        Assert.assertEquals(1, matcher.groupCount());
        Assert.assertEquals("10.0.2.15", matcher.group(1));
        
        matcher = eth1Pattern.matcher(TEST_1);
        Assert.assertTrue(matcher.find());
        Assert.assertEquals(1, matcher.groupCount());
        Assert.assertEquals("10.0.2.16", matcher.group(1));
    }
}
