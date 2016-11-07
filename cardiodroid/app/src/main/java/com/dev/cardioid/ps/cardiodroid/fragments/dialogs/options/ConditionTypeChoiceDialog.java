package com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.dev.cardioid.ps.cardiodroid.R;
import com.dev.cardioid.ps.cardiodroid.rules.conditions.ComposedConditionAbstract;
import com.dev.cardioid.ps.cardiodroid.rules.conditions.SimpleConditionAbstract;

/**
 * DialogFragment that displays an alert dialog
 * in order for the user to choose one type of condition
 * to create.
 *
 * The type selected is is thrown as an event by the interface {@link ITypeSelectorListener}.
 */
public final class ConditionTypeChoiceDialog extends SimpleChoiceDialogAbstract {

    /**
     * The event handlers needed to update the creator.
     * */
    public interface ITypeSelectorListener {
        void onConditionTypeSelectedClick(String conditionType);
    }

    private ITypeSelectorListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mListener = (ITypeSelectorListener) activity;
        }catch(ClassCastException _ex){
            throw new ClassCastException("Hosting component must implement ITypeSelectorListener");
        }
    }

    @Override
    protected void onPositiveButtonHandler(String valuePicked) {
        mListener.onConditionTypeSelectedClick(valuePicked);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String dialogTitle = getResources().getString(R.string.add_condition_type_dialog_text);

        //FIXME isto n\ao pode ser "estatico". Tem de ser obtido de forma mais dinamica
        final String[] keys = new String[] {
                SimpleConditionAbstract.IDENTIFIER,
                ComposedConditionAbstract.IDENTIFIER
        };

        final String[] options = getResources().getStringArray(R.array.condition_types);

        return buildDialog(dialogTitle, options, keys);
    }
}
