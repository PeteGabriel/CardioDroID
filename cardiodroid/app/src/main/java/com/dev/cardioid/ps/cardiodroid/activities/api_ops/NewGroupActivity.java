package com.dev.cardioid.ps.cardiodroid.activities.api_ops;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dev.cardioid.ps.cardiodroid.CardioDroidApplication;
import com.dev.cardioid.ps.cardiodroid.R;
import com.dev.cardioid.ps.cardiodroid.activities.RuleManagerActivity;
import com.dev.cardioid.ps.cardiodroid.network.async.process.CallResult;
import com.dev.cardioid.ps.cardiodroid.network.async.process.Completion;
import com.dev.cardioid.ps.cardiodroid.network.dtos.cardio_api.GroupDto;
import com.dev.cardioid.ps.cardiodroid.utils.NetworkUtils;
import com.dev.cardioid.ps.cardiodroid.utils.PreferencesUtils;
import com.dev.cardioid.ps.cardiodroid.utils.ToastUtils;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;

/**
 * Activity used to create a new group in the cloud storage.
 */
public class NewGroupActivity extends AppCompatActivity{

    public static final String GROUP_NAME_KEY = "group_name_text_saved_key";

    /**
     * Debug purposes.
     */
    public static final String TAG = Utils.makeLogTag(NewGroupActivity.class);

    /**
     * Application instance ref
     */
    private CardioDroidApplication mApp;

    /*
     * UI elements
     */
    private Button mBtnCreateGroup;
    private TextView mGroupName;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_new_group);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.new_cloud_group));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mApp = (CardioDroidApplication) getApplicationContext();
        setupUiElements();

        //restore state if needed.
        restoreElementsState(savedInstanceState);
    }

    /**
     * Restore state of UI widgets
     */
    private void restoreElementsState(Bundle savedInstanceState) {
        if (savedInstanceState != null){
            mGroupName.setText(savedInstanceState.getString(GROUP_NAME_KEY));
        }
    }

    /**
     * Get a reference to the UI widgets
     */
    private void setupUiElements(){
        mBtnCreateGroup = (Button) findViewById(R.id.create_new_group_button);
        mBtnCreateGroup.setOnClickListener(createNewGroupListener);
        mGroupName = (TextView) findViewById(R.id.new_group_name);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(GROUP_NAME_KEY, mGroupName.getText().toString());
        //save view hierarchy state
        super.onSaveInstanceState(outState);
    }

    /**
     * The listener used when the user clicks the button to create a new group.
     */
    private View.OnClickListener createNewGroupListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d(TAG, "On Create Group button click.");

            //cannot create a group without a name
            if (mGroupName.getText().toString().isEmpty()){
                String msg = getResources().getString(R.string.new_group_creation_name_error);
                Log.d(TAG, msg);
                ToastUtils.showError(msg, mApp);
                return;
            }

            //don't call API without internet
            long connType = PreferencesUtils.getConnectionSpecifiedByUser(mApp);
            if (!NetworkUtils.isConnected(mApp, connType)){
                String msg = getResources().getString(R.string.new_group_creation_network_error);
                Log.d(TAG, msg);
                ToastUtils.showError(msg, mApp);
                return;
            }

            //all good call the API
            final GroupDto dto = new GroupDto();
            dto.setName(mGroupName.getText().toString());
            mApp.getCardioApiProvider().createGroupAsync(dto, new Completion<Void>() {
                @Override
                public void onResult(CallResult<Void> result) {
                    Intent info;
                    try{
                        result.getResult();
                        info = new Intent().putExtra(RuleManagerActivity.GROUP_CREATED_KEY, dto);
                        setResult(RESULT_OK, info);
                        finish();
                    }catch(Exception ex){
                        String msg = getResources().getString(R.string.group_not_created);
                        ToastUtils.showError(msg, mApp);
                        /*info = new Intent().putExtra(RuleManagerActivity.GROUP_CREATED_KEY, msg);
                        setResult(RESULT_CANCELED, info);
                        finish();
                        */
                    }
                }
            });
        }
    };
}
