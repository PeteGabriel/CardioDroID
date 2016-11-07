package com.dev.cardioid.ps.cardiodroid.activities;

import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dev.cardioid.ps.cardiodroid.CardioDroidApplication;
import com.dev.cardioid.ps.cardiodroid.contexts.ContextEnvironment;
import com.dev.cardioid.ps.cardiodroid.fragments.ComposedViewFragment;
import com.dev.cardioid.ps.cardiodroid.fragments.SimpleViewFragment;
import com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options.ContextTypeChoiceDialog;
import com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options.ISimpleValueListener;
import com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options.simple_value.ExhaustionOptionsDialog;
import com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options.simple_value.TimeIntervalValueDialog;
import com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options.simple_value.WeatherOptionsDialog;
import com.dev.cardioid.ps.cardiodroid.rules.Rule;
import com.dev.cardioid.ps.cardiodroid.rules.conditions.ComposedConditionAbstract;
import com.dev.cardioid.ps.cardiodroid.rules.conditions.SimpleConditionAbstract;
import com.dev.cardioid.ps.cardiodroid.rules.parser.JsonRuleContext;
import com.dev.cardioid.ps.cardiodroid.rules.parser.JsonRuleException;
import com.dev.cardioid.ps.cardiodroid.rules.parser.models.JsonNamedRuleModel;
import com.dev.cardioid.ps.cardiodroid.services.location.GeofenceTransitionsIntentService;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;
import com.google.android.gms.location.GeofencingRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options.ConditionTypeChoiceDialog.ITypeSelectorListener;
import static com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options.ContextTypeChoiceDialog.IContextSelectorListener;
import static com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options.SelectActionsDialogFragment.ActionsSelectedListener;

/**
 * The common behavior necessary to create a new rule.
 */
public abstract class DefineRuleAbstract extends SingleFragmentActivity
        implements ActionsSelectedListener, //event for actions selected
        ITypeSelectorListener, //event for condition type selected
        IContextSelectorListener, //event for each context of a simple condition
        ISimpleValueListener { //event that tells which option was selected from the dialog

    /**
     * For debug purposes.
     */
    public static final String TAG = Utils.makeLogTag(DefineRuleAbstract.class);
    /**
     * Used when creating some condition that is related to
     * collect info about the location context. Used when
     * performing the request "startForResult" to the
     * activity {@link LocationMapsActivity};
     */
    public static final int CREATE_LOCATION_CONDITION_REQUEST = 124;
    protected static final int CONDITION1_MODIFICATION_ID = 1;
    protected static final int CONDITION2_MODIFICATION_ID = 2;
    /**
     * This object keeps record of the different
     * parts of a rule as it gets created.
     * This relates only to simple conditions.
     * Composed conditions are maintained
     * by the activity {@link DefineComposedRuleActivity}.
     */
    protected JSONObject mRuleCreated;
    /**
     * Contains the selected actions chosen
     * from the dialog by the user himself.
     */
    protected ArrayList<String> selectedActions;
    protected List<String> mContextIdentifier;
    protected JSONObject jsonCondition1;
    protected JSONObject jsonCondition2;
    /**
     * This property serves as a switch to indicate which condition is being created.
     */
    protected int conditionModificationSelector;
    protected String simpleConditionType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRuleCreated = new JSONObject();
        conditionModificationSelector = -1;
        mContextIdentifier = new ArrayList<>();
    }

    /**
     * This callback receives the type of condition selected by the user
     * through the dialog.
     *
     * @param conditionType Type of condition being created
     */
    @Override
    public void onConditionTypeSelectedClick(String conditionType) {
        Log.d(TAG, "OnConditionTypeSelected event");

        switch (conditionType) {

            case SimpleConditionAbstract.IDENTIFIER:
                Log.d(TAG, "Condition Type: SIMPLE");
                addTypeToRule(conditionType);
                new ContextTypeChoiceDialog().show(getSupportFragmentManager(), null);
                break;

            case ComposedConditionAbstract.IDENTIFIER:
                Log.d(TAG, "Condition Type: COMPOSED");
                addTypeToRule(conditionType);
                startActivityForResult(
                        new Intent(this, DefineComposedRuleActivity.class),
                        DefineComposedRuleActivity.CREATE_COMPOSED_REQUEST);
        }
    }

    /**
     * Injects the selected actions by the user through the dialog.
     * @param actions The selected actions
     */
    @Override
    public void onActionsSelected(List<String> actions) {
        Log.d(TAG, "Activity has received actions selected from dialog fragment");
        selectedActions.clear();
        selectedActions.addAll(actions);

        //update actions
        try {
            JSONArray arrayOfActions = new JSONArray(selectedActions);
            mRuleCreated.put(JsonRuleContext.ACTIONS, arrayOfActions);

            try {
                //JSONObject temporaryRule = JsonNamedRuleModel.create("", mRuleCreated, mContextIdentifier);
                //temporaryRule.getJSONObject(JsonRuleModel.RULE).put(JsonRuleModel.RULE_CONDITION, JsonRuleModel.emptyObject());

                String typeofRule = mRuleCreated.getJSONObject("condition").getString("type");
                if (typeofRule.equals(ComposedConditionAbstract.IDENTIFIER)) {
                    String condition2Context = mRuleCreated.getJSONObject("condition")
                            .getJSONObject("value")
                            .getJSONObject("condition2")
                            .getJSONObject("value")
                            .getString("context");
                    String condition1Context = mRuleCreated.getJSONObject("condition")
                            .getJSONObject("value")
                            .getJSONObject("condition1")
                            .getJSONObject("value")
                            .getString("context");

                    mContextIdentifier.add(condition1Context);
                    mContextIdentifier.add(condition2Context);
                }

                final String NO_NAME = "";
                Rule rule =  Rule.buildFromJson(getApplicationContext(),
                        JsonNamedRuleModel.create(NO_NAME, mRuleCreated, mContextIdentifier));
                updateConditionDetailsSection(rule);
            } catch (JsonRuleException e) {
                Log.e(TAG, e.getMessage());
            }

        } catch (JSONException e) {
           Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onContextTypeSelected(String typeOfContext) {
        Log.d(TAG, "Context Type Received: " + typeOfContext);

        simpleConditionType = typeOfContext;

        mContextIdentifier.add(typeOfContext);

        //modify the condition known so far
        try {

            JSONObject tmp = new JSONObject();
            tmp.put(JsonRuleContext.CONTEXT, typeOfContext);
            tmp.put(JsonRuleContext.EVALUATOR, "EQUALS"); //TODO magic string
            mRuleCreated.getJSONObject(JsonRuleContext.CONDITION)
                    .put(JsonRuleContext.VALUE, tmp);

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        getContextInfo(typeOfContext);
    }

    /**
     * This callback method receives the option selected from a list
     * of possible options presented by a dialog.
     *
     * @param value Option selected in JSON notation
     */
    @Override
    public void onSimpleValueSelected(JSONObject value) {
        Log.d(TAG, "OnSimpleValueSelected event");
        Log.d(TAG, "Value received: " + value);

        //modify the condition known so far
        try {

            mRuleCreated.getJSONObject(JsonRuleContext.CONDITION)
                    .getJSONObject(JsonRuleContext.VALUE)
                    .put(JsonRuleContext.FIXED_VALUE, value);

            assignConditionCreated(conditionModificationSelector,
                    mRuleCreated.getJSONObject(JsonRuleContext.CONDITION)
            );

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

    }

    protected void updateConditionDetailsSection(Rule rule) {
        if (rule.getTypeOfRule().equals(ComposedConditionAbstract.IDENTIFIER)){
            Fragment fragment = ComposedViewFragment.newInstance(rule);
            getFragmentManager().beginTransaction()
                    .replace(getFrameContainer(), fragment)
                    .commit();
        }else{
            Fragment fragment = SimpleViewFragment.newInstance(rule);
            getFragmentManager().beginTransaction()
                    .replace(getFrameContainer(), fragment)
                    .commit();
        }
    }

    protected final void getContextInfo(String conditionContextType){
        switch(conditionContextType) {
            case ContextEnvironment.Types.WEATHER:
                Log.d(TAG, "Weather Condition Context Type");
                new WeatherOptionsDialog().show(getSupportFragmentManager(), null);
                break;

            case ContextEnvironment.Types.TIME:
                Log.d(TAG, "Time Condition Context Type");
                new TimeIntervalValueDialog().show(getFragmentManager(), null);
                break;

            case ContextEnvironment.Types.EXHAUSTION:
                Log.d(TAG, "Exhaustion Condition Context Type");
                new ExhaustionOptionsDialog().show(getSupportFragmentManager(), null);
                break;

            case ContextEnvironment.Types.LOCATION:
                Log.d(TAG, "Location Condition Context Type");
                startActivityForResult(new Intent(this, LocationMapsActivity.class),
                        CREATE_LOCATION_CONDITION_REQUEST);
                break;
        }
    }


    private void addTypeToRule(String type){
        try {
            JSONObject obj = new JSONObject();
            obj.put(JsonRuleContext.TYPE, type);
            obj.put(JsonRuleContext.VALUE, "");

            mRuleCreated.put(JsonRuleContext.CONDITION, obj);

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * The creation of a sub condition of this composed condition results in this method being
     * called.
     *
     * This method knows which condition is being currently being defined, by referencing the
     * conditionModificationSelector variable.
     *
     * @param condition the created condition.
     */
    protected void assignConditionCreated(int conditionNumber, JSONObject condition) throws JSONException {
        switch (conditionNumber) {
            case CONDITION1_MODIFICATION_ID:
                jsonCondition1 = new JSONObject(condition.toString());
                break;
            case CONDITION2_MODIFICATION_ID:
                jsonCondition2 = new JSONObject(condition.toString());
                break;
        }
    }


    /**
     * Uses the GeofencingRequest class and its nested GeofencingRequestBuilder class to
     * specify the geofences to monitor and to set how related geofence events are triggered.
     *
     * @param instance An instance of {@link CardioDroidApplication}
     */
    protected GeofencingRequest getGeofencingRequest(CardioDroidApplication instance) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(instance.getListOfGeofences());
        return builder.build();
    }

    protected PendingIntent mGeofencePendingIntent;

    protected PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

}
