package com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options.simple_value;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import com.dev.cardioid.ps.cardiodroid.R;
import com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options.ISimpleValueListener;
import com.dev.cardioid.ps.cardiodroid.rules.parser.models.contexts.JsonTimeIntervalModel;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;

import org.json.JSONObject;

public class TimeIntervalValueDialog extends DialogFragment {

  public static final String TAG = Utils.makeLogTag(TimeIntervalValueDialog.class);

  private ISimpleValueListener mGeneralSelector;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try{
      mGeneralSelector = (ISimpleValueListener) activity;
    }catch(ClassCastException e){
      Log.d(TAG, TAG + " must implement ISimpleValueListener");
    }
  }

  @Override public Dialog onCreateDialog(Bundle savedInstanceState) {

    // TODO - accept a time interval passed via arguments

    // Use the Builder class for convenient dialog construction
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    View view =
        getActivity().getLayoutInflater().inflate(R.layout.time_interval_select_layout, null);

    final TimePicker startPicker = (TimePicker) view.findViewById(R.id.start_time_picker);
    final TimePicker endPicker = (TimePicker) view.findViewById(R.id.end_time_picker);

    startPicker.setIs24HourView(true);
    endPicker.setIs24HourView(true);

    builder.setTitle(R.string.select_timeinterval_title)
        .setView(view)
        .setPositiveButton(R.string.create_text, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {

            String interval_start =
                String.format("%s:%s:%s", Integer.toString(startPicker.getCurrentHour()),
                    Integer.toString(startPicker.getCurrentMinute()), "00");
            String interval_end =
                String.format("%s:%s:%s", Integer.toString(endPicker.getCurrentHour()),
                    Integer.toString(endPicker.getCurrentMinute()), "00");

            JSONObject parsedValue = JsonTimeIntervalModel.create(interval_start, interval_end);
            mGeneralSelector.onSimpleValueSelected(parsedValue);
          }
        });

    // Create the AlertDialog object and return it
    return builder.create();
  }
}