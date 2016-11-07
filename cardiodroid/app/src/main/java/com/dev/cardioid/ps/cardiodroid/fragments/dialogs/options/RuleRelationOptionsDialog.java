package com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dev.cardioid.ps.cardiodroid.R;
import com.dev.cardioid.ps.cardiodroid.rules.conditions.ComposedConditionAbstract;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;

/**
 * It presents a dialog for the user to choose one relation
 * between conditions of a composed rule. Possible options are
 * "AND", "OR", etc...
 *
 */
public final class RuleRelationOptionsDialog extends SimpleChoiceDialogAbstract {

    public static final String TAG = Utils.makeLogTag(RuleRelationOptionsDialog.class);

    public interface IRelationOptionListener{
        public void onRelationOptionSelected(String option);
    }

    private IRelationOptionListener  mGeneralSelector;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mGeneralSelector = (IRelationOptionListener) activity;
        }catch(ClassCastException e){
            Log.d(TAG, TAG + " must implement IRelationOptionListener");
        }
    }


    @Override
    protected void onPositiveButtonHandler(String valuePicked) {
        mGeneralSelector.onRelationOptionSelected(valuePicked);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        final String title = getResources().getString(R.string.select_composed_condition_relation_text);

        final String[] options = new String[]{
                ComposedConditionAbstract.Type.AND_CONDITION,
                ComposedConditionAbstract.Type.OR_CONDITION
        };

        return buildDialog(title, options, options);
    }
}
