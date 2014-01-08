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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class TelemeeJavaClientPerformanceTest {

   private static TelemeeJavaClient tj;

   private final static TelemeeApp TEST_APP_01 = new TelemeeApp("TelemeeJavaClientPerformanceTest -- Testapp 01");
   private final static TelemeeApp TEST_APP_02 = new TelemeeApp("TelemeeJavaClientPerformanceTest -- Testapp 02");
   private final static Channel TEST_CHANNEL_01 = new Channel("TelemeeJavaClientPerformanceTest -- Testchannel 01");
   private final static Channel TEST_CHANNEL_02 = new Channel("TelemeeJavaClientPerformanceTest -- Testchannel 02");
   private final static ChannelAttribute TEST_CHANNELATTRIBUTE_X = new ChannelAttribute("TelemeeJavaClientPerformanceTest -- X");
   private final static ChannelAttribute TEST_CHANNELATTRIBUTE_Y = new ChannelAttribute("TelemeeJavaClientPerformanceTest -- Y");
   private final static ChannelAttribute TEST_CHANNELATTRIBUTE_Z = new ChannelAttribute("TelemeeJavaClientPerformanceTest -- Z");
   private final static ChannelAttribute TEST_CHANNELATTRIBUTE_GROUP = new ChannelAttribute("TelemeeJavaClientPerformanceTest -- Group");

   private final static int COUNT_CREATE = 500;
   private final static Logger LOGGER = Logger.getLogger(TelemeeJavaClientPerformanceTest.class.getName());

   public TelemeeJavaClientPerformanceTest() {
   }

   public static void main(String args[]) {
      tj = new TelemeeJavaClient();
      tj.init();
      tj.setLogLevel(TelemeeJavaClient.INFO);
      Logger.getLogger(TelemeeJavaClientPerformanceTest.class.getName()).log(Level.INFO, "Running tests - each creating {0} logentries", COUNT_CREATE);
      clearData();
      testCreateLogEntriesSync1();
      testCreateLogEntriesSync2();
      testCreateLogEntriesAsync1();
      testCreateLogEntriesAsync2();
      tj.waitTillAllIsDone();
      clearData();

      tj.destroy();
   }

   public static void testCreateLogEntriesAsync1() {
      long start = System.currentTimeMillis();
      tj.forTelemeeApp(TEST_APP_01)
              .forChannel(TEST_CHANNEL_01);
      for (int i = 0; i < COUNT_CREATE; i++) {
         tj.startLogEntry("Testlogentry", TelemeeJavaClient.INFO)
                 .forChannelAttribute(TEST_CHANNELATTRIBUTE_X)
                 .log(i)
                 .forChannelAttribute(TEST_CHANNELATTRIBUTE_Y)
                 .log(COUNT_CREATE - i)
                 .endLogEntry();
      }
      tj.sendAsync();
      long duration = System.currentTimeMillis() - start;
      log("testCreateLogEntriesAsync1", duration);
   }

   public static void testCreateLogEntriesAsync2() {
      long start = System.currentTimeMillis();
      tj.forTelemeeApp(TEST_APP_02)
              .forChannel(TEST_CHANNEL_02);
      for (int i = 0; i < COUNT_CREATE; i++) {
         tj.startLogEntry("Testlogentry", TelemeeJavaClient.INFO)
                 .forChannelAttribute(TEST_CHANNELATTRIBUTE_X)
                 .log(i)
                 .forChannelAttribute(TEST_CHANNELATTRIBUTE_Y)
                 .log(COUNT_CREATE - i)
                 .endLogEntry();
      }
      tj.sendAsync();
      long duration = System.currentTimeMillis() - start;
      log("testCreateLogEntriesAsync2", duration);
   }

   public static void testCreateLogEntriesSync1() {
      long start = System.currentTimeMillis();
      tj.forTelemeeApp(TEST_APP_01)
              .forChannel(TEST_CHANNEL_01);
      for (int i = 0; i < COUNT_CREATE; i++) {
         tj.startLogEntry("Testlogentry", TelemeeJavaClient.INFO)
                 .forChannelAttribute(TEST_CHANNELATTRIBUTE_X)
                 .log(i)
                 .forChannelAttribute(TEST_CHANNELATTRIBUTE_Y)
                 .log(COUNT_CREATE - i)
                 .endLogEntry();
      }
      tj.send();
      long duration = System.currentTimeMillis() - start;
      log("testCreateLogEntriesSync1", duration);
   }

   public static void testCreateLogEntriesSync2() {
      long start = System.currentTimeMillis();
      tj.forTelemeeApp(TEST_APP_02)
              .forChannel(TEST_CHANNEL_02);
      for (int i = 0; i < COUNT_CREATE; i++) {
         tj.startLogEntry("Testlogentry", TelemeeJavaClient.INFO)
                 .forChannelAttribute(TEST_CHANNELATTRIBUTE_X)
                 .log(i)
                 .forChannelAttribute(TEST_CHANNELATTRIBUTE_Y)
                 .log(COUNT_CREATE - i)
                 .endLogEntry();
      }
      tj.send();
      long duration = System.currentTimeMillis() - start;
      log("testCreateLogEntriesSync2", duration);
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

   private static void log(String name, long duration) {
      LOGGER.log(Level.INFO, "{0} took {1} ms ({2} ms average time per logEntry)", new Object[]{name, duration, duration / COUNT_CREATE});
   }

}
