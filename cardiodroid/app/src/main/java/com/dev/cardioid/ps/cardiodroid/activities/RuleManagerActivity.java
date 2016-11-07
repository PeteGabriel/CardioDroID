package com.dev.cardioid.ps.cardiodroid.activities;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.dev.cardioid.ps.cardiodroid.CardioDroidApplication;
import com.dev.cardioid.ps.cardiodroid.R;
import com.dev.cardioid.ps.cardiodroid.activities.adapters.RulesListAdapter;
import com.dev.cardioid.ps.cardiodroid.activities.api_ops.JoinGroupActivity;
import com.dev.cardioid.ps.cardiodroid.activities.api_ops.NewGroupActivity;
import com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options.utility_dialog.FilePickerDialogFragment;
import com.dev.cardioid.ps.cardiodroid.network.async.process.CallResult;
import com.dev.cardioid.ps.cardiodroid.network.async.process.Completion;
import com.dev.cardioid.ps.cardiodroid.network.dtos.ApiError;
import com.dev.cardioid.ps.cardiodroid.network.dtos.cardio_api.GroupConnect;
import com.dev.cardioid.ps.cardiodroid.network.dtos.cardio_api.GroupDto;
import com.dev.cardioid.ps.cardiodroid.network.dtos.cardio_api.RuleDto;
import com.dev.cardioid.ps.cardiodroid.network.dtos.cardio_api.RulesDto;
import com.dev.cardioid.ps.cardiodroid.repo.RepoUtils;
import com.dev.cardioid.ps.cardiodroid.rules.Rule;
import com.dev.cardioid.ps.cardiodroid.utils.NetworkUtils;
import com.dev.cardioid.ps.cardiodroid.utils.PermissionUtils;
import com.dev.cardioid.ps.cardiodroid.utils.PreferencesUtils;
import com.dev.cardioid.ps.cardiodroid.utils.ToastUtils;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This activity displays a list of rules stored inside the device.
 * It gives the possibility to manage the stored set of rules by deleting,
 * creating or modifying any selected rule.
 */
public class RuleManagerActivity extends AppCompatActivity
    implements RulesListAdapter.OnItemClickListener,
        RulesListAdapter.OnNewItemSelectedListener,
    FilePickerDialogFragment.OnFileSelectedListener {

  public static final String RULES_PROP = "rules";
  /**
   * A reference to the {@link RecyclerView} widget
   */
  private RecyclerView mRecyclerView;

  /**
   * A reference to the {@link RecyclerView.Adapter} component
   */
  private RecyclerView.Adapter mAdapter;

  /**
   * debug purposes
   */
  private static final String TAG = Utils.makeLogTag(RuleManagerActivity.class);

  /**
   * Name given to the exported file.
   */
  private static final String FILE_NAME = "/exported_rules.json";


  /* UI elements */
  private MenuItem mImportButton;
  private MenuItem mExportButton;
  private MenuItem mDeleteButton;

  /**
   * List that reference each selected item from the list of rules.
   */
  private List<Rule> mSelectedRules;

  /**
   * A  reference to the singleton instance of {@link android.app.Application}
   */
  private CardioDroidApplication mApp;

  private static final int REQUEST_CREATE_GROUP = 1990;
  public static final int REQUEST_JOIN_GROUP = 1880;

  private boolean mExportNotImport;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_manage_rules_layout);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    mApp = ((CardioDroidApplication) getApplicationContext());
    mSelectedRules = new ArrayList<>();

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    if (fab != null) {
      fab.setOnClickListener(createNewRuleFabAction());
    }

    List<Rule> dataset = new ArrayList<>();
    mRecyclerView = (RecyclerView) findViewById(R.id.rules_recycler_view_list);

    assert mRecyclerView != null;
    mRecyclerView.setHasFixedSize(true); // use this setting to improve performance

    //for the first time, get records and setup adapter source
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
    mRecyclerView.setLayoutManager(mLayoutManager);
    mAdapter = new RulesListAdapter(dataset, RuleManagerActivity.this, RuleManagerActivity.this);
    mRecyclerView.setAdapter(mAdapter);

    populateViewWithRules();
  }

  public static final String GROUP_CREATED_KEY = "group_created_request_key";
  public static final String USER_CONNECT_GROUP_KEY = "user_connect_to_group_request_key";


  /**
   * It is supposed to receive a rule from the other activity. If so, insert it into the DB
   * and update adapter.
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    if (resultCode != Activity.RESULT_OK) return;

    switch (requestCode) {

      case DefineRuleActivity.REQUEST_CREATE_RULE:
        Rule ruleReceived;
        final String createdRule;
        if (data.getExtras() != null && data.getExtras().containsKey(DefineRuleActivity.DEFINE_RULE_RESULT)) {
          ruleReceived = ((Rule) data.getExtras().get(DefineRuleActivity.DEFINE_RULE_RESULT));
          createdRule = ruleReceived.getNativeRule().toString();
        }else{
          Log.i(TAG, "Intent is empty. Created rule must come from the intent.");
          return;
        }
        Log.d(TAG, "Rule Creation Result: " + createdRule);

        //insert received rule and add it to the adapter
        mApp.getRepository().insertRule(createdRule, new Completion<Uri>() {
          @Override public void onResult(CallResult<Uri> result) {
            try {
              long id = ContentUris.parseId(result.getResult());
              getRuleByID(id);
            } catch (Exception e) {
              Log.d(TAG, e.getMessage());
            }
          }
        });
        break;

      case DefineRuleActivity.REQUEST_MODIFY_RULE:
        final Rule modifiedRule = (Rule) data.getExtras().get(DefineRuleActivity.DEFINE_RULE_RESULT);
        Log.d(TAG, "Rule Modified: " + modifiedRule);
        mApp.getRepository().updateRule(modifiedRule, new Completion<Void>() {
          @Override
          public void onResult(CallResult<Void> result) {
            ((RulesListAdapter)mAdapter).removeItem(modifiedRule);
            getRuleByID(modifiedRule.getID());
          }
        });
        break;

      case REQUEST_CREATE_GROUP:
        final GroupDto dto = (GroupDto) data.getExtras().get(GROUP_CREATED_KEY);
        String msg = String.format("%s %s",
                getResources().getString(R.string.group_created),
                dto.getName());
        ToastUtils.showMessage(msg, mApp);
        break;

      case REQUEST_JOIN_GROUP:
        final GroupConnect connection = (GroupConnect) data.getExtras().get(USER_CONNECT_GROUP_KEY);
        if (connection != null) {
          String joinMsg = String.format("%s %s %s",
                  connection.getUserName(),
                  getResources().getString(R.string.user_joined_group),
                  connection.getGroupName());
          ToastUtils.showMessage(joinMsg, mApp);
        }
        break;
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_manage_rules, menu);

    //icons get shown as items get selected and vice versa
    mImportButton = menu.findItem(R.id.import_cloud_managed_rule_icon);
    mImportButton.setVisible(true);
    mImportButton.setOnMenuItemClickListener(handleImportRulesActionListener());


    mExportButton = menu.findItem(R.id.export_cloud_managed_rule_icon);
    mExportButton.setVisible(false);
    mExportButton.setOnMenuItemClickListener(handleExportRulesActionListener());

    mDeleteButton = menu.findItem(R.id.delete_managed_rule_icon);
    mDeleteButton.setVisible(false);

    return true;
  }



  /**
   * Leads the path to the two options available
   * through the action bar - Delete and Export.
   *
   * @param item the menu view
   * @return true if event must be handled only inside this method
   * false otherwise.
   */
  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      //export set of rules
      case R.id.export_managed_rule_icon:
        importExportRuleDecisionHandler(true);
        return true;

      //import set of rules
      case R.id.import_managed_rule_icon:
        importExportRuleDecisionHandler(false);
        return true;

      //delete set of rules
      case R.id.delete_managed_rule_icon:
        deleteRulesDecisionHandler();
        resetViewState();
        return true;

      case R.id.join_group_icon:
        startActivityForResult(new Intent(mApp, JoinGroupActivity.class), REQUEST_JOIN_GROUP);
        return true;


      case R.id.new_cloud_group_icon:
        startActivityForResult(new Intent(mApp, NewGroupActivity.class), REQUEST_CREATE_GROUP);
        return true;


      default:
        return super.onOptionsItemSelected(item);
    }
  }

  /**
   * Belongs to {@link RulesListAdapter.OnNewItemSelectedListener} and
   * is invoked every time a item is selected or deselected.
   *
   * @param amount the amount of selected items.
   */
  @Override public void onNewItemSelected(int amount) {
    boolean hasItemSelected = amount > 0;
    if (!hasItemSelected) {
      setTitle(getResources().getString(R.string.label_manage_rules_activity));
    } else {
      setTitle("" + amount);
    }
    mImportButton.setVisible(!hasItemSelected);
    mExportButton.setVisible(hasItemSelected);
    mDeleteButton.setVisible(hasItemSelected);
  }

  /**
   * Invoked everytime an item is clicked.
   *
   * @param selectedRule element clicked
   */
  @Override public void onItemClick(final Rule selectedRule) {
    Intent intent = new Intent(getApplicationContext(), DefineRuleActivity.class)
        .putExtra(DefineRuleActivity.BASE_RULE_KEY, selectedRule);
    startActivityForResult(intent, DefineRuleActivity.REQUEST_MODIFY_RULE);
  }

  /**
   * This method will be invoked after a click event performed
   * on the FAB widget of the activity's layout.
   *
   * @return an anonymous implementation of {@link View.OnClickListener}
   */
  private View.OnClickListener createNewRuleFabAction() {
    return new View.OnClickListener() {
      @Override public void onClick(View view) {
        startActivityForResult(new Intent(getApplicationContext(), DefineRuleActivity.class),
           DefineRuleActivity.REQUEST_CREATE_RULE);
      }
    };
  }

  /**
   * Reset layout of the action bar.
   */
  private void resetViewState() {
    //reset label of activity
    setTitle(getResources().getString(R.string.manage_rules_action_menu));
    //reset action bar
    mExportButton.setVisible(false);
    mDeleteButton.setVisible(false);
    mImportButton.setVisible(true);
    mSelectedRules.clear();
  }


  /**
   * Invoked when the user chooses a file from the FilePicker fragment
   */
  @Override public void onFileSelected(File selectedFile) {
    File tmp = selectedFile.isDirectory() ? 
          new File(selectedFile.getAbsolutePath() + FILE_NAME) :
          new File(selectedFile.getAbsolutePath());

    if (mExportNotImport) { //export
      final String toExport = formatJson(mSelectedRules);
      writeJson(tmp, toExport);
    } else { //import
      String parsedContent = parseFile(tmp);
      importNewSetOfRules(mApp, parsedContent);
    }
    //restore view state
    resetViewState();
    //reset set of selected items
    ((RulesListAdapter) mAdapter).deselectItems();
  }


  private void importNewSetOfRules(CardioDroidApplication app, String parsedContent) {
    JSONObject toImport;
    try {
      toImport = new JSONObject(parsedContent);
      if (toImport.has(RULES_PROP) && toImport.get(RULES_PROP) != null) {
        JSONArray rootArray = toImport.optJSONArray(RULES_PROP);
        for (int i = 0; i < rootArray.length(); ++i) {
          String obj = rootArray.getString(i);

          app.getRepository().insertRule(obj, new Completion<Uri>() {
            @Override
            public void onResult(CallResult<Uri> result) {
              try {
                long id = ContentUris.parseId(result.getResult());
                getRuleByID(id);
              } catch (Exception e) {
                Log.d(TAG, e.getMessage());
              }
            }
          });
        }

      }
    } catch (JSONException e) {
      String toastMsg = getResources().getString(R.string.import_of_file_not_possible);
      ToastUtils.showError(toastMsg, mApp);
      Log.d(TAG, e.getMessage());
    }
  }

  /**
   * Request a certain rule by a given ID.
   *
   */
  private void getRuleByID(long id) {
    String activeEmail = mApp.getPersonalUserAccountInfo().getEmail();
    Uri rulesForActiveUser = Uri.withAppendedPath(RepoUtils.makeUriForTableRules(), activeEmail);

    //The uri must point to the entire set of records
    mApp.getRepository().getRuleByID(rulesForActiveUser, id, new Completion<List<Rule>>() {
      @Override public void onResult(CallResult<List<Rule>> result) {
        try {
          //((RulesListAdapter) mAdapter).removeItem(result.getResult().get(0));
          Rule tmp = result.getResult().get(0);
          ((RulesListAdapter) mAdapter).addItem(tmp);
          mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
          Log.e(TAG, e.getMessage());
        }
      }
    });
  }

  /**
   * Read contents from a given file and return it.
   *
   * @param file file to read
   * @return file's content
   */
  private String parseFile(File file) {
    StringBuilder stb = new StringBuilder();
    try (BufferedReader bfr = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = bfr.readLine()) != null) {
        stb.append(line);
      }
      //TODO devolver as excepçoes para cima
    } catch (FileNotFoundException e) {
      Log.d(TAG, "File Not Found: " + file.getName());
    } catch (IOException e) {
      Log.d(TAG, e.getMessage());
    }
    return stb.toString();
  }

  /**
   * Transforms a list of objects into an array of JSON objects which
   * each represents a rule to export.
   *
   * @param rules list of rules to export
   * @return a string in JSON format
   */
  private String formatJson(List<Rule> rules) {
    JSONObject obj = new JSONObject();
    JSONArray arr = new JSONArray();

    Iterator<Rule> it = rules.iterator();
    if (it.hasNext()) {
      arr.put(it.next().getNativeRule().toString());
    }
    while (it.hasNext()) {
      arr.put(it.next().getNativeRule().toString());
    }
    try {
      obj.put(RULES_PROP, arr);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return obj.toString();
  }

  /**
   * Export rules to a certain file.
   *
   * @param file file instance of where to export
   * @param contentToExport the content to write into the file
   */
  private void writeJson(File file, String contentToExport) {
    File root = Environment.getExternalStorageDirectory();
    if (root.canWrite()) {
      final boolean DO_NOT_APPEND = false;
      try (BufferedWriter fw = new BufferedWriter(new FileWriter(file, DO_NOT_APPEND))) {
        fw.write(contentToExport);
      } catch (Exception e) {
        Log.e(TAG, e.getMessage());
      }
    }
  }

  /**
   * Request all the rules for the active user present in the repository.
   */
  private void populateViewWithRules() {
    String activeEmail = mApp.getPersonalUserAccountInfo().getEmail();
    Uri rulesForActiveUser = Uri.withAppendedPath(RepoUtils.makeUriForTableRules(), activeEmail);

    mApp.getRepository().getRules(rulesForActiveUser, new Completion<List<Rule>>() {
      @Override public void onResult(CallResult<List<Rule>> result) {
        try {
          ((RulesListAdapter) mAdapter).swapDataSet(result.getResult());
        } catch (Exception e) {
          Log.e(TAG, e.getMessage());
        }
      }
    });
  }

  /**
   * Handles the decision of exporting or importing one or more rules
   * from a file present inside the device.
   * A pop-up windows is shown in order to let the user choose the file.
   */
  private void importExportRuleDecisionHandler(boolean exportNotImport){
    String msg = exportNotImport ?
            getResources().getString(R.string.you_dont_have_permission_to_write) :
            getResources().getString(R.string.you_dont_have_permission_to_read);

    if (!PermissionUtils.isReadToExternalStoragePermissionGranted(mApp)
            || !PermissionUtils.isWriteToExternalStoragePermissionGranted(mApp)){
      ToastUtils.showMessage(msg, mApp);
      return;
    }

    mExportNotImport = exportNotImport;

    //to export we need a set of rules.
    if (mExportNotImport) {
      mSelectedRules = ((RulesListAdapter) mAdapter).getSelectedItems();
    }

    new FilePickerDialogFragment().show(getFragmentManager(), "");
  }

  /**
   * Handle the decision of delete a set of rules.
   */
  private void deleteRulesDecisionHandler() {
    mSelectedRules = ((RulesListAdapter) mAdapter).getSelectedItems();

    for (final Rule r : mSelectedRules) {
      Uri uri = RepoUtils.makeUriForTableRules();
      uri = Uri.withAppendedPath(uri, mApp.getPersonalUserAccountInfo().getEmail());
      uri = ContentUris.withAppendedId(uri, r.getID());

      Log.d(TAG, "Delete Resource: " + uri.toString());

      mApp.getRepository().deleteRule(uri, r, new Completion<Void>() {
        @Override public void onResult(CallResult<Void> result) {
          ((RulesListAdapter) mAdapter).removeItem(r);
        }
      });
    }
  }

  /**
   * Check if internet connection is up
   * @param ctx Application's context
   * @return
   */
  protected final boolean checkInternetConn(Context ctx){
    long typeOfConn = PreferencesUtils.getConnectionSpecifiedByUser(ctx);
    return NetworkUtils.isConnected(ctx, typeOfConn);
  }

  @NonNull
  private MenuItem.OnMenuItemClickListener handleExportRulesActionListener() {
    return new MenuItem.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem menuItem) {
        //if connection is not active, act as if nothing happened
        if (!checkInternetConn(mApp)){
          Utils.showInternetErrorToast(mApp);
          return true;
        }

        mSelectedRules = ((RulesListAdapter) mAdapter).getSelectedItems();

        RuleDto dto;
        final String email = mApp.getPersonalUserAccountInfo().getEmail();
        for (Rule r : mSelectedRules){
          dto = new RuleDto(r, false, email);
          mApp.getCardioApiProvider().uploadRules(email, dto, new Completion<Void>() {
            @Override
            public void onResult(CallResult<Void> result) {
              try{

                result.getResult();
                Log.d(TAG, "Rule uploaded.");
                RuleManagerActivity.this.resetViewState();

              }catch(Exception e){
                Log.e(TAG, "Rule not uploaded !");
                ApiError error;
                try{
                  error = (ApiError)e;
                } catch(ClassCastException castExc){
                  //reset state
                  RuleManagerActivity.this.resetViewState();
                  return;
                }
                //handle the error
                if (error.status() == NetworkUtils.HttpCodes.CONFLICT){
                  ToastUtils.showError("Rule already uploaded.", mApp);
                }else{
                  Log.e(TAG, e.getMessage());
                }
                //reset state
                RuleManagerActivity.this.resetViewState();

              }
            }
          });
        }
        return true;
      }
    };
  }

  @NonNull
  private MenuItem.OnMenuItemClickListener handleImportRulesActionListener(){
    return new MenuItem.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem menuItem) {
        //if connection is not active, act as if nothing happened
        if (!checkInternetConn(mApp)){
          Utils.showInternetErrorToast(mApp);
          return true;
        }

        final String email = mApp.getPersonalUserAccountInfo().getEmail();
        mApp.getCardioApiProvider().getRulesByUserAsync(email, new Completion<RulesDto>() {
          @Override
          public void onResult(CallResult<RulesDto> result) {
            try{
              //create the array of objects in order to
              //reuse the "importNewSetOfRules" method that is used
              //when we are importing rules locally
              RulesDto rules = result.getResult();
              JSONObject rulesJsonObj = new JSONObject();
              rulesJsonObj.put("rules", new JSONArray());
              for (RuleDto r : rules.getRules()){
                rulesJsonObj.getJSONArray("rules").put(r.getJsonRule());
              }
              Log.e(TAG, "Rules found: " + rules.getRules().length);
              importNewSetOfRules(mApp, rulesJsonObj.toString());
            }catch(Exception e){
              Log.e(TAG, "No rules were found for the user " + email );
            }
          }
        });

        return true;
      }
    };
  }
}
