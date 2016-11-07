package com.dev.cardioid.ps.cardiodroid.activities.api_ops;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.dev.cardioid.ps.cardiodroid.CardioDroidApplication;
import com.dev.cardioid.ps.cardiodroid.R;
import com.dev.cardioid.ps.cardiodroid.activities.RuleManagerActivity;
import com.dev.cardioid.ps.cardiodroid.network.async.process.CallResult;
import com.dev.cardioid.ps.cardiodroid.network.async.process.Completion;
import com.dev.cardioid.ps.cardiodroid.network.dtos.cardio_api.GroupConnect;
import com.dev.cardioid.ps.cardiodroid.network.dtos.cardio_api.GroupDto;
import com.dev.cardioid.ps.cardiodroid.network.dtos.cardio_api.GroupsDto;
import com.dev.cardioid.ps.cardiodroid.utils.NetworkUtils;
import com.dev.cardioid.ps.cardiodroid.utils.PreferencesUtils;
import com.dev.cardioid.ps.cardiodroid.utils.ToastUtils;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;

import java.util.ArrayList;

public class JoinGroupActivity extends AppCompatActivity {

    /**
     * Debug purposes.
     */
    public static final String TAG = Utils.makeLogTag(JoinGroupActivity.class);

    /**
     * Application instance ref
     */
    private CardioDroidApplication mApp;

    private ListView mListView;

    private boolean mHasExtraContent;

    private TextView mJoinGroupLabel;
    private TextView mJoinedGroupName;

    private TextView mMessageInfo;
    private Button mBtnJoinNewGroup;
    private TextView mTxtSelectedGroupName;

    private String mChosenGroupName;

    /**
     * When sending information to this component, use this key.
     * It can handle a group or nothing at all.
     */
    public static final String GROUP_FOUND_EXTRA_DATA = "intent_data_group_found_extra_key";

    public static final String CHOSEN_GROUP_NAME_KEY = "chosen_group_name_saved_instance_key";

    private static final String JOIN_GROUP_NAME_KEY = "join_group_name_saved_instance_key";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.join_group));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mApp = (CardioDroidApplication) getApplicationContext();
        //if has extra like a group
        mHasExtraContent = getIntent().hasExtra(GROUP_FOUND_EXTRA_DATA);
        setupUiElements();

        //restore state
        if (savedInstanceState != null){
            mJoinedGroupName.setText(savedInstanceState.getString(JOIN_GROUP_NAME_KEY));
            mChosenGroupName = savedInstanceState.getString(CHOSEN_GROUP_NAME_KEY);
        }
    }

    /**
     * Get a reference to the UI widgets
     */
    private void setupUiElements(){
        mListView = (ListView) findViewById(R.id.join_group_list);
        mJoinGroupLabel = (TextView)findViewById(R.id.join_name_label);
        mJoinedGroupName = (TextView)findViewById(R.id.join_group_name);
        mMessageInfo = (TextView) findViewById(R.id.group_explanation_text_view);
        mBtnJoinNewGroup = (Button)findViewById(R.id.join_new_group_button);
        mTxtSelectedGroupName = (TextView) findViewById(R.id.selected_group_label);

        //message or group's name
        if (mHasExtraContent){
            mJoinGroupLabel.setVisibility(View.VISIBLE);
            mJoinedGroupName.setVisibility(View.VISIBLE);
            mMessageInfo.setVisibility(View.GONE);
        }else{

            String email = mApp.getPersonalUserAccountInfo().getEmail();
            mApp.getCardioApiProvider().getGroupByUserAsync(email,
                    new Completion<GroupDto>() {
                        @Override
                        public void onResult(CallResult<GroupDto> result) {
                            try{
                                GroupDto group = result.getResult();
                                Log.d(TAG, "Group found: " + group.getName());
                                //set existent group visible
                                mJoinGroupLabel.setVisibility(View.VISIBLE);
                                mJoinedGroupName.setVisibility(View.VISIBLE);
                                mJoinedGroupName.setText(group.getName());
                                mMessageInfo.setVisibility(View.GONE);
                            }catch(Exception e){
                                Log.e(TAG, e.getMessage());
                                //set message visible
                                mJoinGroupLabel.setVisibility(View.GONE);
                                mJoinedGroupName.setVisibility(View.GONE);
                                mMessageInfo.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }


        mBtnJoinNewGroup.setOnClickListener(joinButtonHandlerClick);
        mListView.setOnItemClickListener(listViewHandlerClick);

        populateListOfGroups();

    }

    private void populateListOfGroups() {
        mApp.getCardioApiProvider().getGroupsAsync(new Completion<GroupsDto>() {
            @Override
            public void onResult(CallResult<GroupsDto> result) {
                try{
                    GroupsDto groups = result.getResult();
                    Log.d(TAG, "Groups Found: " + groups.getGroups().length);
                    final ArrayList<String> list = new ArrayList<>();
                    GroupDto[] groupDtos = groups.getGroups();
                    for (int i = 0; i < groupDtos.length; ++i) {
                        list.add(groupDtos[i].getName());
                    }
                    final ArrayAdapter adapter =
                            new ArrayAdapter(JoinGroupActivity.this
                                    , android.R.layout.simple_list_item_1, list);
                    mListView.setAdapter(adapter);
                }catch(Exception e){
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(JOIN_GROUP_NAME_KEY,  mJoinedGroupName.getText().toString());
        outState.putString(CHOSEN_GROUP_NAME_KEY,  mChosenGroupName);
        super.onSaveInstanceState(outState);
    }

    /**
     * What to do when an element of the list is clicked
     */
    private AdapterView.OnItemClickListener listViewHandlerClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mChosenGroupName = parent.getItemAtPosition(position).toString();
            Log.d(TAG, "Click ListItem: " + mChosenGroupName);
            mTxtSelectedGroupName.setText(mChosenGroupName);
        }
    };

    private View.OnClickListener joinButtonHandlerClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //cannot send request without data
            if (mChosenGroupName.isEmpty()){
                ToastUtils.showError("You must select a group to join", mApp);
                return;
            }

            //send only if there is a valid connection
            long connType = PreferencesUtils.getConnectionSpecifiedByUser(mApp);
            if (NetworkUtils.isConnected(mApp, connType)){
                //gather data to send later
                String email = mApp.getPersonalUserAccountInfo().getEmail();
                final String UserName = mApp.getPersonalUserAccountInfo().getUserName();
                final Intent resultData = new Intent();

                mApp.getCardioApiProvider().joinUserToGroupAsync(mChosenGroupName, email,
                        new Completion<Void>() {
                            @Override
                            public void onResult(CallResult<Void> result) {
                                try{
                                    result.getResult();
                                    GroupConnect connection = new GroupConnect(UserName, mChosenGroupName);
                                    resultData.putExtra(RuleManagerActivity.USER_CONNECT_GROUP_KEY, connection);
                                    setResult(RESULT_OK, resultData);
                                    finish();
                                }catch(Exception e){
                                    String msg = getResources().getString(R.string.connection_to_group_not_created);
                                    ToastUtils.showError(msg, mApp);
                                    Log.e(TAG, e.getMessage());
                                }
                            }
                        });
            }else{
                String msg = getResources().getString(R.string.network_down_warning);
                ToastUtils.showError(msg, mApp);
            }
        }
    };

}
