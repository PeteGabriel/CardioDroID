package com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.dev.cardioid.ps.cardiodroid.utils.Utils;

/**
 * This is a "generic" alert dialog fragment that lets the user
 * choose an option from a set of possible options. It lets
 * the hosting component know about the selected value through
 * the event {@link ISimpleValueListener#onSimpleValueSelected(String)}
 */

public abstract class SimpleChoiceDialogAbstract extends DialogFragment {

    /**
     * Debug purposes.
     */
    public static final String TAG = Utils.makeLogTag(SimpleChoiceDialogAbstract.class);

    /**
     * Hopefully, a reference to the hosting component.
     */
    //protected ISimpleValueListener mValueChosenListener;

    String chosenIdentifier;

    /**
     * Default ctor
     */
    public SimpleChoiceDialogAbstract() {
        super();
    }

/*
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mValueChosenListener = (ISimpleValueListener) activity;
        }catch(ClassCastException _exc){
            String msg = "Hosting component must implement ISimpleValueListener interface.";
            Log.e(TAG, msg);
            throw new ClassCastException(msg);
        }
    }
*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "OnCreate Call");
    }

    protected Dialog buildDialog(String title, String[] options, final String[] keys){
        //mark default option
        chosenIdentifier = keys[0];

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setSingleChoiceItems(options, 0, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        chosenIdentifier = keys[which];
                        Log.d(TAG,"Option Selected: " + chosenIdentifier);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        onPositiveButtonHandler(chosenIdentifier);
                    }
                });
        return builder.create();
    }


    protected abstract void onPositiveButtonHandler(String valuePicked);

}
