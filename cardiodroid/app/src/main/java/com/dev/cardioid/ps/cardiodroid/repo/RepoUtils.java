package com.dev.cardioid.ps.cardiodroid.repo;

import android.content.ContentValues;
import android.net.Uri;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;

/**
 * THis class provides various methods that help converting from one model class into
 * {@link ContentValues} and vice versa.
 */
public class RepoUtils {

  public static final String TAG = Utils.makeLogTag(RepoUtils.class);

  private RepoUtils(){}


  /*
  public static ContentValues getContentValuesOfRule(final JSONObject rule) {
    ContentValues values = new ContentValues();
    //values.put(DataUnit.RULE_ID, );
    values.put(DataUnit.RULE_BODY, rule.toString());
    return values;
  }*/

  public static Uri makeUriForTableRules() {
    return Uri.parse("content://" + DataProvider.AUTHORITY + DataProvider.RULES_TABLE_NAME);
  }
}
