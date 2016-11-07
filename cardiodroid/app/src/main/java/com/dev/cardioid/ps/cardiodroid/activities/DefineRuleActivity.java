package com.dev.cardioid.ps.cardiodroid.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dev.cardioid.ps.cardiodroid.CardioDroidApplication;
import com.dev.cardioid.ps.cardiodroid.R;
import com.dev.cardioid.ps.cardiodroid.fragments.SimpleViewFragment;
import com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options.ConditionTypeChoiceDialog;
import com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options.ISimpleValueListener;
import com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options.SelectActionsDialogFragment;
import com.dev.cardioid.ps.cardiodroid.rules.Rule;
import com.dev.cardioid.ps.cardiodroid.rules.parser.JsonRuleException;
import com.dev.cardioid.ps.cardiodroid.rules.parser.JsonRulesParser;
import com.dev.cardioid.ps.cardiodroid.rules.parser.models.JsonConditionModel;
import com.dev.cardioid.ps.cardiodroid.rules.parser.models.JsonNamedRuleModel;
import com.dev.cardioid.ps.cardiodroid.rules.parser.models.JsonRuleModel;
import com.dev.cardioid.ps.cardiodroid.rules.parser.models.JsonSimpleConditionValueModel;
import com.dev.cardioid.ps.cardiodroid.utils.ToastUtils;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This activity lets the user create a new rule, or presents the means to
 * modify an existing one.
 */
public class DefineRuleActivity extends DefineRuleAbstract {

  public static final String TAG = Utils.makeLogTag(DefineRuleActivity.class);

  public static final String BASE_RULE_KEY = "base_condition_key";

  public static final String DEFINE_RULE_RESULT = "rule_result";

  public static final int REQUEST_CREATE_RULE = 456;
  public static final int REQUEST_MODIFY_RULE = 567;

  public static final int NOT_A_VALID_RULE_ID = -1;

  /**
   * Keeps record of rule's name.
   */
  private EditText mRuleNameEditText;

  private Fragment mConditionViewer;

  private int modifyingRuleId;

  @Override
  protected Fragment createFragment() {
    mConditionViewer = new Fragment();
    return mConditionViewer;
  }

  @Override
  protected void setupContentView() {
    setContentView(R.layout.activity_define_rule_layout);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }

  @Override
  protected int getFrameContainer() {
    return R.id.base_condition_container;
  }

  private Button mCreateButton;
  private Button mAddConditionButton;
  private Button mAddActionButton;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    modifyingRuleId = NOT_A_VALID_RULE_ID; //id's são sempre acima de 0
    mRuleCreated = new JSONObject();

    //widgets do layout
    mAddActionButton = ((Button) findViewById(R.id.add_action_button));
    mAddConditionButton = ((Button) findViewById(R.id.add_condition_button));
    mCreateButton = ((Button) findViewById(R.id.create_rule_button));
    mRuleNameEditText = ((EditText) findViewById(R.id.rule_name_text));

    // Setup the button listeners.
    mAddActionButton.setOnClickListener(onAddActionButtonListener());
    mAddConditionButton.setOnClickListener(onAddConditionButtonListener());
    mCreateButton.setOnClickListener(onCreateRuleButtonListener());

    /** Setup the state of this fragment. */

    selectedActions = new ArrayList<>();

    Bundle extras = getIntent().getExtras();
    if (extras != null && extras.containsKey(BASE_RULE_KEY)) {
      Rule rule = extras.getParcelable(BASE_RULE_KEY);
      String updateLabel = String.format("%s \"%s\"", getResources().getString(R.string.update_rule_label), rule.getName());
      remakeView(rule, updateLabel);
    }
  }


  private void remakeView(Rule rule, String activityLabel) {
    mRuleNameEditText.setText(rule.getName());
    modifyingRuleId = rule.getID();
    this.setTitle(activityLabel);
    try {
      mRuleCreated = rule.getNativeRule().getJSONObject(JsonRuleModel.RULE);

      updateConditionDetailsSection(rule);

      JSONArray actionsArray = mRuleCreated.getJSONArray(JsonRuleModel.RULE_ACTIONS);
      selectedActions.addAll(getCollectionOfActions(actionsArray));
      mCreateButton.setText(getResources().getString(android.R.string.ok));
    } catch (JSONException e) {
      Log.e(TAG, e.getMessage());
    }
  }


  /**
   * In the case of creating a composed condition, the resulting
   * condition will be available through this callback
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.d(TAG, "OnActivityResult Call");

    if (resultCode == RESULT_OK) {
      Log.i(TAG, "onActivityResult - RESULT OK");

      //result received from the activity DefineComposedRuleActivity
      if (requestCode == DefineComposedRuleActivity.CREATE_COMPOSED_REQUEST) {
        handleComposedRuleCreation(data);
      }
      //result received from the activity LocationMapsActivity
      if (requestCode == CREATE_LOCATION_CONDITION_REQUEST) {

        CardioDroidApplication app = (CardioDroidApplication) getApplicationContext();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
          //will not run, just to keep the IDE happy as a bird.
          return;
        }
        LocationServices.GeofencingApi.addGeofences(
                app.getLocationApiClient(),
                getGeofencingRequest(app),
                getGeofencePendingIntent()
        ).setResultCallback(new ResultCallback<Status>() {
          @Override
          public void onResult(@NonNull Status status) {
            Log.d(TAG, "PedingResult Status: " + status.getStatus().toString() + " " +status.getStatusMessage());
          }
        });

        /*LatLng ruleLocation = data.getParcelableExtra(LocationMapsActivity.LOCATION_CONDITION_KEY);
        String ruleGeoRequestKey = data.getStringExtra(LocationMapsActivity.LOCATION_GEO_REQUEST_KEY);
        double ruleGeoRadius = data.getDoubleExtra(LocationMapsActivity.LOCATION_GEO_RADIUS_KEY, 0);

        app.getCircularAreasList().add(new CircularArea(ruleGeoRequestKey, ruleLocation, ruleGeoRadius));
*/

        handleLocationRuleCreation(data); //changes the view
        }
      }
    }

  /**
   * Display the correct interface in order for the
   * user to create a location-based rule.
   *
   * @param data Intent instance with extra data
   */
  private void handleLocationRuleCreation(Intent data) {
    JSONObject obj;
    try {
      obj = new JSONObject(data.getStringExtra(LocationMapsActivity.LOCATION_CONDITION_KEY));
      obj = JsonSimpleConditionValueModel.create(simpleConditionType, "EQUALS", obj);
      obj = JsonConditionModel.create(JsonConditionModel.CONDITION_TYPE_SIMPLE, obj);

      if (obj == null)
        return;

      if (mRuleCreated != null){
        mRuleCreated.put(JsonRuleModel.RULE_CONDITION, obj);
      }


      Rule toDisplayRule = Rule.buildFromJson(getApplicationContext(), mRuleCreated);
      Fragment frag = SimpleViewFragment.newInstance(toDisplayRule);
      getFragmentManager().beginTransaction()
              .replace(getFrameContainer(), frag)
              .commit();

    } catch (JSONException | JsonRuleException e) {
      Log.e(TAG, e.getMessage());
    }
  }


  /**
   * What to do when the result sent from another activity
   * tries to create a composed rule.
   *
   * @param data Intent instance with extra data
   */
  private void handleComposedRuleCreation(Intent data) {
    try {

      String ruleRepresentation = data.getStringExtra(DefineComposedRuleActivity.CREATE_COMPOSED_RESULT);
      mRuleCreated = new JSONObject(ruleRepresentation);

      Log.d(TAG, "Composed Rule Received: " + mRuleCreated.toString());

      //show details of a composed rule in the frame layout
      try {
        //update view with details
        JSONObject tmp = JsonNamedRuleModel.create("", mRuleCreated, mContextIdentifier);
        Rule tmpRule;
        if (selectedActions == null || selectedActions.size() == 0) {
          tmpRule = JsonRulesParser.parseRuleNoActions(getApplicationContext(), tmp.toString());
          updateConditionDetailsSection(tmpRule);
        } else {
          tmpRule = Rule.buildFromJson(getApplicationContext(), tmp);
          updateConditionDetailsSection(tmpRule);
        }

      } catch (JsonRuleException e) {
        Log.e(TAG, "Trying to create JsonObject with invalid data.");
      }
    } catch (JSONException e) {
      Log.e(TAG, e.getMessage());
    }
  }

  /********************************************************************************
   *****************   Event handlers for the UI widgets   ************************
   ********************************************************************************/

  /**
   * Callback method to be executed every time the button "add condition"
   * is clicked.
   * @return An instance of {@link View.OnClickListener}
   */
  private View.OnClickListener onAddConditionButtonListener() {
    final String descriptionLabel = getResources().getString(R.string.label_add_condition_button);
    return new View.OnClickListener() {
      @Override public void onClick(View v) {
        Log.d(TAG, "OnAddConditionButtonListener event");
        new ConditionTypeChoiceDialog().show(getSupportFragmentManager(), descriptionLabel);
      }
    };
  }

  /**
   * Callback method to be executed every time the button "create rule"
   * is clicked.
   * @return An instance of {@link View.OnClickListener}
   */
  private View.OnClickListener onCreateRuleButtonListener() {
    return new View.OnClickListener() {
      @Override public void onClick(View v) {
        //Create the JSON rule object.
        //This implies querying the displayed ConditionFragment for the jsonCondition.
        Log.d(TAG, "OnCreateRuleButtonListener event");

        String ruleName = mRuleNameEditText.getText().toString();
        //cant create rules without a name
        if (ruleName.isEmpty()) {
          ToastUtils.showError(getResources().getString(R.string.error_toast_rule_name_not_specified), getApplicationContext());
          return;
        }

        //cant create rules without a group of actions
        if (selectedActions == null || selectedActions.size() == 0) {
          ToastUtils.showError(getResources().getString(R.string.error_toast_actions_not_specified), getApplicationContext());
          return;
        }

        if (mRuleCreated == null){
          ToastUtils.showError(getResources().getString(R.string.error_toast_condition_not_specified), getApplicationContext());
          return;
        }

        try {

          Rule newRule = Rule.buildFromJson(getApplicationContext(),
              JsonNamedRuleModel.create(ruleName, mRuleCreated, mContextIdentifier));
          if(modifyingRuleId  != NOT_A_VALID_RULE_ID)
            newRule.setID(modifyingRuleId );

          Log.d(TAG, "Rule Created: " + newRule.getNativeRule().toString());
          setResult(Activity.RESULT_OK, new Intent().putExtra(DEFINE_RULE_RESULT, newRule));
          finish();
        } catch (JsonRuleException e) {
          Log.e(TAG, e.getMessage());
        }
      }
    };
  }

  /**
   * Callback method to be executed every time the button "add action"
   * is clicked.
   * @return An instance of {@link View.OnClickListener}
   */
  private View.OnClickListener onAddActionButtonListener() {
    final String descriptionLabel = getResources().getString(R.string.label_add_action_button);
    return new View.OnClickListener() {
      @Override public void onClick(View v) {
        Log.d(TAG, "OnAddActionButtonListener event");
        new SelectActionsDialogFragment().show(getFragmentManager(), descriptionLabel);
      }
    };
  }
  /********************************************************************************
   ********************************************************************************
   ********************************************************************************/

  /**
   * Transforms the JSONArray of actions into a Collection of the elements inside the JSONArray.
   *
   * @param jsonActions the JSONArray which contains the selected actions of a Rule.
   * @return The Collection which contains the selected actions of a Rule.
   */
  private Collection<String> getCollectionOfActions(JSONArray jsonActions) throws JSONException {
    ArrayList<String> actions = new ArrayList<>();

    for (int i = 0; i < jsonActions.length(); ++i)
      actions.add(jsonActions.getString(i));

    return actions;
  }

  /**
   * Callback from {@link ISimpleValueListener} interface.
   *
   * @param value Option selected in JSON notation
   */
  @Override
  public void onSimpleValueSelected(JSONObject value) {
    super.onSimpleValueSelected(value);

    try {
      //update view with details
      Rule tmpRule;
      boolean hasNoActions = selectedActions.size() == 0;
      if (selectedActions == null || hasNoActions) {
        String modelJsonNotation = JsonNamedRuleModel.create("", mRuleCreated, mContextIdentifier).toString();
        tmpRule = JsonRulesParser.parseRuleNoActions(getApplicationContext(), modelJsonNotation);
      } else {
        tmpRule = Rule.buildFromJson(getApplicationContext(), JsonNamedRuleModel.create("", mRuleCreated, mContextIdentifier));
      }
      updateConditionDetailsSection(tmpRule);
    }catch(JsonRuleException e){
      Log.d(TAG, e.getMessage());
    }

  }
}
