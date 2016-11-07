package com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.dev.cardioid.ps.cardiodroid.R;
import com.dev.cardioid.ps.cardiodroid.rules.IAction;

import java.util.ArrayList;
import java.util.List;

/**
 * This fragment presents the different options for the user
 * to select in order to choose which actions should be
 * performed one the rule is validated.
 */
public class SelectActionsDialogFragment extends DialogFragment {

  private String[] keysOptions;
  private ArrayList<String> mSelectedItems;


  public SelectActionsDialogFragment(){
    mSelectedItems = new ArrayList<>();
  }


  public interface ActionsSelectedListener {
    void onActionsSelected(List<String> selectedActions);
  }

  private ActionsSelectedListener mListener;

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mListener = (ActionsSelectedListener) activity;

   } catch (ClassCastException e) {
      // The activity doesn't implement the interface, throw exception
      throw new ClassCastException(activity.toString() + " must implement ActionsSelectedListener");
    }
  }

  @Override public Dialog onCreateDialog(Bundle savedInstanceState) {


    final String[] keys = IAction.getActionRelations;
    final String title = getResources().getString(R.string.add_actions_dialog_text);
    final String[] options = getResources().getStringArray(R.array.actions_options);

    // Use the Builder class for convenient dialog construction
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    builder.setTitle(title)
        .setSingleChoiceItems(options, 0, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            mSelectedItems.clear();
            mSelectedItems.add(keys[i]);
          }
        })
        .setPositiveButton(R.string.add_text, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            mListener.onActionsSelected(mSelectedItems);
          }
        })
        .setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            dismiss();
          }
        });

    // Create the AlertDialog object and return it
    return builder.create();
  }

}
