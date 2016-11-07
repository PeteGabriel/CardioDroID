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

import com.dev.cardioid.ps.cardiodroid.CardioDroidApplication;
import com.dev.cardioid.ps.cardiodroid.R;
import com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options.ConditionTypeChoiceDialog;
import com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options.RuleRelationOptionsDialog;
import com.dev.cardioid.ps.cardiodroid.rules.conditions.ComposedConditionAbstract;
import com.dev.cardioid.ps.cardiodroid.rules.parser.JsonRuleContext;
import com.dev.cardioid.ps.cardiodroid.rules.parser.models.JsonComposedConditionValueModel;
import com.dev.cardioid.ps.cardiodroid.rules.parser.models.JsonConditionModel;
import com.dev.cardioid.ps.cardiodroid.rules.parser.models.JsonSimpleConditionValueModel;
import com.dev.cardioid.ps.cardiodroid.utils.ToastUtils;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import static com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options.RuleRelationOptionsDialog.IRelationOptionListener;

/**
 * This class comprises the creation process of a composed condition.
 * A composed condition is a condition that holds two others with a relation
 * associated between them.
 *
 */
public class DefineComposedRuleActivity extends DefineRuleAbstract
       implements IRelationOptionListener{

  private static final String TAG = Utils.makeLogTag(DefineComposedRuleActivity.class);

  public static final int CREATE_COMPOSED_REQUEST = 123;

  public static final String CREATE_COMPOSED_RESULT = "composed_condition_result";

  private final String MODIFICATION_SELECTOR_VALUE_KEY = "composed.condition.creation.modification.selector.value";

  private final String RELATION_VALUE_KEY = "composed.condition.creation.relation.value";

  private final String CONDITION1_VALUE_KEY = "composed.condition.creation.condition1.value";
  private final String CONDITION2_VALUE_KEY = "composed.condition.creation.condition2.value";


  private String composedConditionRelation;


  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_create_composed_condition);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle(getResources().getString(R.string.add_composed_condition_activity_label));


    if (savedInstanceState != null) {
      conditionModificationSelector = savedInstanceState.getInt(MODIFICATION_SELECTOR_VALUE_KEY);

      // Restore the state of the relation which may have been defined for the composed condition.
      if (savedInstanceState.containsKey(RELATION_VALUE_KEY)) {
        composedConditionRelation = savedInstanceState.getString(RELATION_VALUE_KEY);
      }

      // Attempt ot restore the state of possible saved conditions.
      try {
        if (savedInstanceState.containsKey(CONDITION1_VALUE_KEY)) {
          jsonCondition1 = new JSONObject(savedInstanceState.getString(CONDITION1_VALUE_KEY));
        }

        if (savedInstanceState.containsKey(CONDITION2_VALUE_KEY)) {
          jsonCondition2 = new JSONObject(savedInstanceState.getString(CONDITION2_VALUE_KEY));
        }
      } catch (JSONException e) {
        Log.e(TAG, e.getMessage());
        return;
      }
    }

    setupContent();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putInt(MODIFICATION_SELECTOR_VALUE_KEY, conditionModificationSelector);
    if (composedConditionRelation != null) {
      outState.putString(RELATION_VALUE_KEY, composedConditionRelation);
    }
    if (jsonCondition1 != null) outState.putString(CONDITION1_VALUE_KEY, jsonCondition1.toString());
    if (jsonCondition2 != null) outState.putString(CONDITION2_VALUE_KEY, jsonCondition2.toString());
  }


  //FIXME Precisa de ser limpo
  public void setupContent() {

    /** Condition1 Button */
    Button condition1Button = (Button) findViewById(R.id.create_condition1_button);
    condition1Button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        conditionModificationSelector = CONDITION1_MODIFICATION_ID;
        Log.d(TAG, "Building Condition#" + conditionModificationSelector);
        new ConditionTypeChoiceDialog().show(getSupportFragmentManager(), "add condition");
      }
    });

    /** Condition2 Button */
    Button condition2Button = (Button) findViewById(R.id.create_condition2_button);
    condition2Button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        conditionModificationSelector = CONDITION2_MODIFICATION_ID;
        Log.d(TAG, "Building Condition#" + conditionModificationSelector);
        new ConditionTypeChoiceDialog().show(getSupportFragmentManager(), "add condition");
      }
    });

    /** Relation Button */
    Button selectRelationButton = (Button) findViewById(R.id.select_relation_button);
    selectRelationButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        new RuleRelationOptionsDialog().show(getSupportFragmentManager(), "condition relation");
      }
    });

    /** Create Composed Condition Button */
    Button createComposedConditionButton =
        (Button) findViewById(R.id.create_composed_condition_button);

    assert createComposedConditionButton != null;
    createComposedConditionButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        String msg;
        if (jsonCondition1 == null) {
          msg = getResources().getString(R.string.create_condition_error_set_condition_1);
          ToastUtils.showMessage(msg, getApplicationContext());
          return;
        }
        if (jsonCondition2 == null) {
          msg = getResources().getString(R.string.create_condition_error_set_condition_2);
          ToastUtils.showMessage(msg, getApplicationContext());
          return;
        }
        if (composedConditionRelation == null) {
          msg = getResources().getString(R.string.create_condition_error_set_rlation);
          ToastUtils.showMessage(msg, getApplicationContext());
          return;
        }

        try {
          JSONObject tmpCondition =
              JsonConditionModel.create(ComposedConditionAbstract.IDENTIFIER,
                  JsonComposedConditionValueModel.create(jsonCondition1, jsonCondition2,
                      composedConditionRelation));

          JSONObject composedCondition = new JSONObject();
          composedCondition.put(JsonRuleContext.CONDITION, tmpCondition);
          Intent resultIntent = new Intent()
                  .putExtra(CREATE_COMPOSED_RESULT, composedCondition.toString());
          setResult(Activity.RESULT_OK, resultIntent);
          finish();
        } catch (JSONException e) {
          Log.e(TAG, e.getMessage());
        }

      }
    });
  }



  /**
   * In the case of creating a sub condition which is a composed condition, the resulting
   * condition will be available through this callback.
   */
  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    if (resultCode == RESULT_OK){
      Log.d(TAG, "OnActivityResult RESULT_OK");

      if (requestCode == DefineComposedRuleActivity.CREATE_COMPOSED_REQUEST) {
        try {
          assignConditionCreated(conditionModificationSelector,
                  new JSONObject(data.getExtras().getString(CREATE_COMPOSED_RESULT)));
        } catch (JSONException e) {
          Log.e(TAG, e.getMessage());
        }
      }

      if (requestCode == CREATE_LOCATION_CONDITION_REQUEST ){
        JSONObject obj;
        try {
          obj = new JSONObject(data.getStringExtra(LocationMapsActivity.LOCATION_CONDITION_KEY));
          obj = JsonSimpleConditionValueModel.create(simpleConditionType, "EQUALS", obj);
          obj = JsonConditionModel.create(JsonConditionModel.CONDITION_TYPE_SIMPLE, obj);
          assignConditionCreated(conditionModificationSelector, obj);
        } catch (JSONException e) {
          Log.e(TAG, e.getMessage());
          return;
        }

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

      }
    }else{
      Log.d(TAG, "OnActivityResult RESULT_NOT_OK");
    }
  }



  @Override
  public void onRelationOptionSelected(String option) {
    Log.d(TAG, "Option Received: " + option);
    composedConditionRelation = option;
  }



  /*
    This class does not use a fragment.
    Yet.
     */
  @Override
  protected Fragment createFragment() {
    return null;
  }

  @Override
  protected void setupContentView() {

  }

  @Override
  protected int getFrameContainer() {
    return 0;
  }



}
