package com.dev.cardioid.ps.cardiodroid.services.ble;

import java.util.UUID;

/**
 * TODO
 */
public class BleDefinedUuid {

  public static class Service {
    final static String CARDIO_SERVICE_NAME = "CardioPsService";

    final static UUID CARDIO_SERVICE = UUID.fromString("0000ec00-0000-1000-8000-00805F9B34FB");
  }

  public static class Characteristic {
    public final static UUID ACCESS_RIGHTS_PROCESS = UUID.fromString("0000ec02-0000-1000-8000-00805F9B34FB");
    public final static UUID EXHAUSTION_MEASUREMENT_RATE = UUID.fromString("0000ec01-0000-1000-8000-00805F9B34FB");
    public final static UUID AUTH_ID_MESSENGER_CHAR = UUID.fromString("0000ec03-0000-1000-8000-00805F9B34FB");
  }

  public static class Descriptor {
    final static public UUID CHAR_CLIENT_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
  }

  public static class Protocol{
    final static String R_PROCESS = "R";
    final static String I_PROCESS = "I";
    final static String A_PROCESS = "A";

    public final static String OK_ANSWER = "OK";
    public final static String NOT_OK_ANSWER = "NOT_OK";
  }

  public static class STATES{
    public final static String HEALTHY = "LOW";
    public final static String TIRED = "MEDIUM";
    public final static String EXHAUSTED = "HIGH";
    public final static String UNKNOWN = "unknown";
  }
}
