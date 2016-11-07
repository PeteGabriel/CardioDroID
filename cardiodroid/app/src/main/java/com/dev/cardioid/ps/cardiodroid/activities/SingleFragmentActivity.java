package com.dev.cardioid.ps.cardiodroid.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * This class contains the common behavior between
 * classes that only host one fragment.
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {

  /**
   * Override this method in order to instantiate a specific
   * implementation of a fragment.
   *
   * @return  The instance to host
   */
  protected abstract Fragment createFragment();

  /**
   * Override this method to specify the
   * layout of the activity.
   */
  protected abstract void setupContentView();

  /**
   * Child classes must implement this method in order to return
   * the id of their frame layout.
   * This frame will be where the fragment gets added.
   *
   * @return The id of the frame layout
   */
  protected abstract int getFrameContainer();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setupContentView(); //setup your custom layout

    FragmentManager fm = getFragmentManager();
    Fragment fragmentRef = fm.findFragmentById(getFrameContainer());

    if(fragmentRef == null){
      fragmentRef = createFragment();

      //Fragment transactions are used to
      //add, remove, attach, detach, or replace fragments in the fragment list.
      if (fragmentRef != null) {
        fm.beginTransaction()
                .add(getFrameContainer(), fragmentRef)
                .commit();
      }
    }
  }


}