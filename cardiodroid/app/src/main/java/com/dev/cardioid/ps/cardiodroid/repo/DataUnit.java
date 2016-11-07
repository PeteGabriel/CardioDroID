package com.dev.cardioid.ps.cardiodroid.repo;

/**
 * This utility class specifies the scripts to create each table of the application's database.
 * The column names can be accessed through this class as well.
 */
public class DataUnit {

  /*
   * Name of each table
   */
  public static class Tables {
    public static final String RULES_TABLE = "RULES_TABLE";
  }

  public static final String RULE_BODY = "BODY";
  public static final String RULE_ID = "ID";
  public static final String RULE_USER_EMAIL = "USER_EMAIL";


  /**
   * Statement to create the Upcoming table.
   */
  public static final String CREATE_RULES_TABLE = "CREATE TABLE "
      + Tables.RULES_TABLE
      + " ("
      + " " + RULE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
      + " " + RULE_BODY + " TEXT,"
      + " " + RULE_USER_EMAIL + " TEXT"
      + ")";


  private DataUnit(){
    /*no instances of this*/
  }


}
