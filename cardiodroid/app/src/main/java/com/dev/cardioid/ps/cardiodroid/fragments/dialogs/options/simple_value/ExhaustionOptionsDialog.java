package com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options.simple_value;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.dev.cardioid.ps.cardiodroid.R;
import com.dev.cardioid.ps.cardiodroid.contexts.ContextEnvironment;
import com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options.ISimpleValueListener;
import com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options.SimpleChoiceDialogAbstract;
import com.dev.cardioid.ps.cardiodroid.rules.parser.models.contexts.JsonExhaustionModel;

/**
 * DialogFragment that displays an alert dialog
 * in order for the user to choose one state of exhaustion.
 *
 * The type selected is is thrown as an event by the interface {@link ISimpleValueListener}.
 */
public final class ExhaustionOptionsDialog extends SimpleChoiceDialogAbstract {

    private ISimpleValueListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mListener = (ISimpleValueListener) activity;
        }catch(ClassCastException _ex){
            throw new ClassCastException("Hosting component must implement ISimpleValueListener");
        }
    }

    @Override
    protected void onPositiveButtonHandler(String valuePicked) {
        mListener.onSimpleValueSelected(JsonExhaustionModel.create(valuePicked));
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final String title = getResources().getString(R.string.select_one_option);
        final String[] options = getResources().getStringArray(R.array.exhaustion_states_values);

        //FIXME Isto nao pode ser HARDCODED !!!!!!
        final String[] keys = new String[]{
                ContextEnvironment.ExhaustionStates.LOW,
                ContextEnvironment.ExhaustionStates.MEDIUM,
                ContextEnvironment.ExhaustionStates.HIGH
        };

        return buildDialog(title, options, keys);
    }
}
