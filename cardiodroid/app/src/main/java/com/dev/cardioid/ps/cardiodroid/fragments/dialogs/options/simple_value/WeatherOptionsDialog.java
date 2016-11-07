package com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options.simple_value;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.dev.cardioid.ps.cardiodroid.R;
import com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options.ISimpleValueListener;
import com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options.SimpleChoiceDialogAbstract;
import com.dev.cardioid.ps.cardiodroid.network.http.api.WeatherApiService;
import com.dev.cardioid.ps.cardiodroid.rules.parser.models.contexts.JsonWeatherModel;

import org.json.JSONObject;

/**
 * DialogFragment that displays an alert dialog
 * in order for the user to choose one weather condition.
 *
 * The type selected is is thrown as an event by the interface {@link ISimpleValueListener}.
 */
public final class WeatherOptionsDialog extends SimpleChoiceDialogAbstract {

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
        JSONObject tmp = JsonWeatherModel.create(valuePicked);
        mListener.onSimpleValueSelected(tmp);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final String title = getResources().getString(R.string.select_one_option);

        //FIXME Isto nao pode ser HARDCODED !!!!!!
        final String[] options = WeatherApiService.WeatherApiValues.POSSIBLE_WEATHER_VALUES;

        //FIXME FIXME options are keys. BAD BAD BAD
        return buildDialog(title, options, options);
    }
}
