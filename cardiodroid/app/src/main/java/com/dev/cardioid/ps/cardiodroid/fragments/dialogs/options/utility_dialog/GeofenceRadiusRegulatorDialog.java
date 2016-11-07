package com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options.utility_dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import com.dev.cardioid.ps.cardiodroid.R;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;

/**
 * TODO
 */
public class GeofenceRadiusRegulatorDialog extends DialogFragment {

  public static final String TAG = Utils.makeLogTag(GeofenceRadiusRegulatorDialog.class);
  private int mProgress;

  /**
   * Event called every time the user specifies a value
   * for the geofence being created.
   */
  public interface OnRadiusSetupListener{
    void onRadiusValueRegulated(int progress);
  }

  /**
   * The listener
   */
  private OnRadiusSetupListener mListener;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try{
      mListener = (OnRadiusSetupListener) activity;
    }catch(ClassCastException e){
      Log.d(TAG, TAG + " must implement OnRadiusSetupListener");
    }
  }

  //TODO add documentation of google advising the minimum distance for a radius
  public static final int MAX_RADIUS = 5000; //look at this like "5000 meters";
  public static final int MIN_RADIUS = 100;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mProgress = 0;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = getActivity().getLayoutInflater();
    View view = inflater.inflate(R.layout.dialog_regulate_radius_geofence, null);

    SeekBar bar = (SeekBar) view.findViewById(R.id.seekbar_geofence_radius);
    //FIXME max e min devem ser constantes
    bar.setMax(MAX_RADIUS);
    bar.setProgress(MIN_RADIUS);
    bar.setOnSeekBarChangeListener(onSeekbarChangeHandler(view));
    builder.setView(view)
        .setTitle("Geofence radius")
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            //Notification that the user has finished a touch gesture.
            if (mListener != null){
              mListener.onRadiusValueRegulated(mProgress);
            }
          }
        })
        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {

          }
        });
    return builder.create();
  }

  /**
   * The handler for the event thrown every time the user modifies
   * the seekbar widget.
   * @return
   *  an anonymous instance of {@link SeekBar.OnSeekBarChangeListener}
   */
  private SeekBar.OnSeekBarChangeListener onSeekbarChangeHandler(final View layoutHolder) {
    final TextView radiusIndicator = (TextView) layoutHolder.findViewById(R.id.textview_geofence_radius);
    radiusIndicator.setText("0 meters");
    return new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        radiusIndicator.setText("" + progress + " meters");
      }
      @Override public void onStartTrackingTouch(SeekBar seekBar) { }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {
        mProgress = seekBar.getProgress();
      }
    };
  }
}
