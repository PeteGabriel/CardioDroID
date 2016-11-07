package com.dev.cardioid.ps.cardiodroid.activities;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.dev.cardioid.ps.cardiodroid.R;

public class UserPreferencesActivity extends AppCompatActivity {

  //Keys used in preference.xml
  public static final String DEVICE_ADDR_KEY = "device_address_preference";
  public static final String USER_ID_KEY = "user_id_preference";
  public static final String USER_IDENTIFICATION_PROCESS_KEY =
      "identification_processes_list_preference";

  /**
   * The key used to get the value of connection type option located in settings.
   */
  public static final String TYPE_OF_CONNECTION_KEY = "type_connection_list_preference";


  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTheme(R.style.AppTheme);
    setContentView(R.layout.activity_settings);

    Toolbar toolBar = (Toolbar) findViewById(R.id.settings_toolbar);
    setSupportActionBar(toolBar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    getFragmentManager().beginTransaction()
        .replace(R.id.settings_frame_layout_container, new UserPreferenceFragment())
        .commit();
  }

  /**
   * A simple {@link Fragment} subclass.
   *
   * If this Fragment innerclass was non-static you'd always hold a reference to the
   * parent Activity.
   * The GarbageCollector cannot collect the Activity that way.
   * It can 'leak' the Activity if for example the orientation changes because the Fragment
   * might still live and gets inserted in a new Activity and continues to hold the reference.
   */
  public static class UserPreferenceFragment extends PreferenceFragment {

    public UserPreferenceFragment() {
      // Required empty public constructor
    }

    @Override public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      getActivity().setTheme(R.style.AppTheme);
      addPreferencesFromResource(R.xml.preference);

      // show the current value in the settings screen
      for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
        initSummary(getPreferenceScreen().getPreference(i));
      }
    }


    private void initSummary(Preference p) {
      if (p instanceof PreferenceCategory) {
        PreferenceCategory cat = (PreferenceCategory) p;
        for (int i = 0; i < cat.getPreferenceCount(); i++) {
          initSummary(cat.getPreference(i));
        }
      } else {
        updatePreferences(p);
      }
    }

    private void updatePreferences(Preference p) {
      if (p instanceof EditTextPreference) {
        EditTextPreference editTextPref = (EditTextPreference) p;
        p.setEnabled(false); //Isto faz disable a TODOS ! cuidado.
        p.setSummary(editTextPref.getText());
      }
    }


  }
}
