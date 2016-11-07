package com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.dev.cardioid.ps.cardiodroid.R;
import com.dev.cardioid.ps.cardiodroid.contexts.ContextEnvironment;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;

/**
 * DialogFragment that displays an alert dialog
 * in order for the user to choose one type of context
 * in which the condition will be based upon.
 *
 * The type selected is is thrown as an event by the interface {@link IContextSelectorListener}.
 */
public final class ContextTypeChoiceDialog extends SimpleChoiceDialogAbstract {

    /**
     * Debug purposes.
     */
    public static final String TAG = Utils.makeLogTag(ContextTypeChoiceDialog.class);

    public interface IContextSelectorListener {
        void onContextTypeSelected(String type);
    }

    private IContextSelectorListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mListener = (IContextSelectorListener) activity;
        }catch(ClassCastException _ex){
            throw new ClassCastException("Hosting component must implement IContextSelectorListener");
        }
    }


    @Override
    protected void onPositiveButtonHandler(String valuePicked) {
        mListener.onContextTypeSelected(valuePicked);
    }

    /**
     * Override to build your own custom Dialog container.  This is typically
     * used to show an AlertDialog instead of a generic Dialog; when doing so,
     * onCreateView does not need to be implemented since the AlertDialog takes
     * care of its own content.
     * @param savedInstanceState The last saved instance state of the Fragment,
     *                           or null if this is a freshly created Fragment.
     * @return Return a new Dialog instance to be displayed by the Fragment.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        final String title = getResources().getString(R.string.select_simple_condition_type_text);

        //FIXME make me dynamic and accept translations in a better way
        final String[] readableOptions = getResources().getStringArray(R.array.simple_condition_types);
        final String[] keys = new String[]{
                ContextEnvironment.Types.EXHAUSTION,
                ContextEnvironment.Types.LOCATION,
                ContextEnvironment.Types.TIME,
                ContextEnvironment.Types.WEATHER
        };


        //default option selected
        chosenIdentifier = keys[0];

        return buildDialog(title, readableOptions, keys);
    }
}
