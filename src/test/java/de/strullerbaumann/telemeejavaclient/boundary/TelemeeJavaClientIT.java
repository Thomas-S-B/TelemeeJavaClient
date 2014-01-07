package de.strullerbaumann.telemeejavaclient.boundary;

/*
 * #%L
 * TelemeeJavaClient
 * %%
 * Copyright (C) 2013 - 2014 Thomas Struller-Baumann
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import de.strullerbaumann.telemeejavaclient.entity.Channel;
import de.strullerbaumann.telemeejavaclient.entity.ChannelAttribute;
import de.strullerbaumann.telemeejavaclient.entity.TelemeeApp;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class TelemeeJavaClientIT {

   private static TelemeeJavaClient tj;

   private final static TelemeeApp TEST_APP_01 = new TelemeeApp("TelemeeJavaTest -- Testapp 01");
   private final static TelemeeApp TEST_APP_02 = new TelemeeApp("TelemeeJavaTest -- Testapp 02");
   private final static Channel TEST_CHANNEL_01 = new Channel("TelemeeJavaTest -- Testchannel 01");
   private final static Channel TEST_CHANNEL_02 = new Channel("TelemeeJavaTest -- Testchannel 02");
   private final static ChannelAttribute TEST_CHANNELATTRIBUTE_X = new ChannelAttribute("TelemeeJavaTest -- X");
   private final static ChannelAttribute TEST_CHANNELATTRIBUTE_Y = new ChannelAttribute("TelemeeJavaTest -- Y");
   private final static ChannelAttribute TEST_CHANNELATTRIBUTE_Z = new ChannelAttribute("TelemeeJavaTest -- Z");
   private final static ChannelAttribute TEST_CHANNELATTRIBUTE_GROUP = new ChannelAttribute("TelemeeJavaTest -- Group");

   public TelemeeJavaClientIT() {
   }

   @BeforeClass
   public static void setUpClass() {
      tj = new TelemeeJavaClient();
      tj.init();
      tj.setLogLevel(TelemeeJavaClient.INFO);
   }

   @AfterClass
   public static void tearDownClass() {
      clearData();
      tj.destroy();
   }

   @Before
   public void setUp() {
      clearData();
      tj.clearCache();
   }

   @Test
   public void testNoData() {
      int expected;
      int actual;

      expected = 0;
      actual = tj.getTelemeeApps().size();
      Assert.assertEquals(expected, actual);

      actual = tj.getChannels().size();
      Assert.assertEquals(expected, actual);

      actual = tj.getChannelAttributes().size();
      Assert.assertEquals(expected, actual);
   }

   @Test
   public void testCreateTelemeeApp() {
      long expected;
      long actual;

      expected = tj.getTelemeeApps().size() + 1;
      tj.forTelemeeApp(TEST_APP_01).send();
      actual = tj.getTelemeeApps().size();
      Assert.assertEquals(expected, actual);

      actual = TEST_APP_01.getId();
      Assert.assertNotEquals(Long.MIN_VALUE, actual);
   }

   @Test
   public void testCreateChannel() {
      long expected;
      long actual;

      expected = tj.getChannels().size() + 1;
      tj.forTelemeeApp(TEST_APP_01)
              .forChannel(TEST_CHANNEL_01)
              .send();
      actual = tj.getChannels().size();
      Assert.assertEquals(expected, actual);

      actual = TEST_CHANNEL_01.getId();
      Assert.assertNotEquals(Long.MIN_VALUE, actual);
   }

   @Test(expected = IllegalStateException.class)
   public void testCreateChannelFails() {
      tj.forChannel(TEST_CHANNEL_01).send();
   }

   @Test
   public void testCreateChannelAttribute() {
      long expected;
      long actual;

      expected = tj.getChannelAttributes().size() + 1;
      tj.forTelemeeApp(TEST_APP_01)
              .forChannel(TEST_CHANNEL_01)
              .forChannelAttribute(TEST_CHANNELATTRIBUTE_X)
              .send();
      actual = tj.getChannelAttributes().size();
      Assert.assertEquals(expected, actual);

      actual = TEST_CHANNELATTRIBUTE_X.getId();
      Assert.assertNotEquals(Long.MIN_VALUE, actual);
   }

   @Test(expected = IllegalStateException.class)
   public void testCreateChannelAttributeFails() {
      tj.forChannelAttribute(TEST_CHANNELATTRIBUTE_X).send();
   }

   @Test
   public void testCreateLogEntry() {
      long expected;
      long actual;
      int countCreate = 10;

      tj.forTelemeeApp(TEST_APP_01)
              .forChannel(TEST_CHANNEL_01)
              .send();
      expected = tj.getLogEntriesCount(TEST_CHANNEL_01) + countCreate;

      for (int i = 0; i < countCreate; i++) {
         tj.startLogEntry("Testlogentry", TelemeeJavaClient.INFO)
                 .forChannelAttribute(TEST_CHANNELATTRIBUTE_X)
                 .log(123)
                 .forChannelAttribute(TEST_CHANNELATTRIBUTE_Y)
                 .log(456)
                 .endLogEntry();
      }
      tj.send();

      actual = tj.getLogEntriesCount(TEST_CHANNEL_01);
      Assert.assertEquals(expected, actual);
   }

   @Test(expected = IllegalStateException.class)
   public void testCreateLogEntryFails() {
      tj.startLogEntry("Testlogentry", TelemeeJavaClient.INFO)
              .forChannelAttribute(TEST_CHANNELATTRIBUTE_X)
              .log(123)
              .forChannelAttribute(TEST_CHANNELATTRIBUTE_Y)
              .log(456)
              .endLogEntry();
      tj.send();
   }

   @Test
   public void testDeleteChannelAttribute() {
      int expected;
      int actual;

      tj.forTelemeeApp(TEST_APP_01)
              .forChannel(TEST_CHANNEL_01)
              .forChannelAttribute(TEST_CHANNELATTRIBUTE_X)
              .forChannelAttribute(TEST_CHANNELATTRIBUTE_Y)
              .send();

      expected = tj.getChannelAttributes().size() - 1;
      tj.deleteChannelAttribute(TEST_CHANNELATTRIBUTE_X);
      actual = tj.getChannelAttributes().size();
      Assert.assertEquals(expected, actual);

      expected = tj.getChannelAttributes().size() - 1;
      tj.deleteChannelAttribute(TEST_CHANNELATTRIBUTE_Y);
      actual = tj.getChannelAttributes().size();
      Assert.assertEquals(expected, actual);
   }

   @Test
   public void testDeleteChannel() {
      int expected;
      int actual;

      tj.forTelemeeApp(TEST_APP_02)
              .forChannel(TEST_CHANNEL_02)
              .send();

      expected = tj.getChannels().size() - 1;
      tj.deleteChannel(TEST_CHANNEL_02);
      actual = tj.getChannels().size();
      Assert.assertEquals(expected, actual);
   }

   @Test
   public void testDeleteApp() {
      int expected;
      int actual;

      tj.forTelemeeApp(TEST_APP_02)
              .forChannel(TEST_CHANNEL_02)
              .send();

      expected = tj.getTelemeeApps().size() - 1;
      tj.deleteTelemeeApp(TEST_APP_02);
      actual = tj.getTelemeeApps().size();

      Assert.assertEquals(expected, actual);
   }

   private static void clearData() {
      tj.deleteChannel(TEST_CHANNEL_01);
      tj.deleteChannel(TEST_CHANNEL_02);

      tj.deleteLogEntries(TEST_CHANNEL_01);
      tj.deleteLogEntries(TEST_CHANNEL_02);

      tj.deleteTelemeeApp(TEST_APP_01);
      tj.deleteTelemeeApp(TEST_APP_02);

      tj.deleteChannelAttribute(TEST_CHANNELATTRIBUTE_X);
      tj.deleteChannelAttribute(TEST_CHANNELATTRIBUTE_Y);
      tj.deleteChannelAttribute(TEST_CHANNELATTRIBUTE_Z);
      tj.deleteChannelAttribute(TEST_CHANNELATTRIBUTE_GROUP);
   }

}
