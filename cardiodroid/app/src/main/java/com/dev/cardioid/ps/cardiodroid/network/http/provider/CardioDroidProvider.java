package com.dev.cardioid.ps.cardiodroid.network.http.provider;

import android.util.Log;

import com.dev.cardioid.ps.cardiodroid.models.LogInfo;
import com.dev.cardioid.ps.cardiodroid.models.User;
import com.dev.cardioid.ps.cardiodroid.network.async.process.CallResult;
import com.dev.cardioid.ps.cardiodroid.network.async.process.Completion;
import com.dev.cardioid.ps.cardiodroid.network.dtos.ApiError;
import com.dev.cardioid.ps.cardiodroid.network.dtos.cardio_api.GroupDto;
import com.dev.cardioid.ps.cardiodroid.network.dtos.cardio_api.GroupsDto;
import com.dev.cardioid.ps.cardiodroid.network.dtos.cardio_api.RuleDto;
import com.dev.cardioid.ps.cardiodroid.network.dtos.cardio_api.RulesDto;
import com.dev.cardioid.ps.cardiodroid.network.http.api.CardioDroidApiService;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Path;

/**
 * This class represents the API provider that supplies operations
 * that let the app interact with the web API made to support
 * users sharing rules between them.
 */
public final class CardioDroidProvider{

    private final String TAG = Utils.makeLogTag(CardioDroidProvider.class);

    /**
     * The base url for the given API.
     */
    private static final String BASE_URL = "https://cardiowebservice.herokuapp.com";
    //"http://46.105.254.184:3000/api/";

    private CardioDroidApiService mService;

    public CardioDroidProvider(Class apiInterface){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mService = (CardioDroidApiService) retrofit.create(apiInterface);
    }


    /**
     * Create a new group.
     */
    public Call<Void> createGroupAsync(GroupDto groupName,
                                                final Completion<Void> task){
        Log.d(TAG, "Create group with name: " + groupName);
        Call<Void> future = mService.createGroup(groupName);
        future.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                final CallResult<Void> result = response.isSuccessful() ?
                        new CallResult(response.body()) :
                        new CallResult<Void>(new Exception(response.errorBody().toString()));

                task.onResult(result);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                task.onResult(new CallResult<Void>((Exception) t));
            }
        });
        return future;
    }

    /**
     * Retrieve a list of all groups.
     */
    public Call<GroupsDto> getGroupsAsync(final Completion<GroupsDto> task){
        Log.d(TAG, "Get groups from Cardio API");
        Call<GroupsDto> future = mService.getGroups();
        future.enqueue(new Callback<GroupsDto>() {
            @Override
            public void onResponse(Call<GroupsDto> call, Response<GroupsDto> response) {
                final CallResult<GroupsDto> result = response.isSuccessful() ?
                        new CallResult(response.body()) :
                        new CallResult<GroupsDto>(new Exception(response.errorBody().toString()));

                task.onResult(result);
            }

            @Override
            public void onFailure(Call<GroupsDto> call, Throwable t) {
                task.onResult(new CallResult<GroupsDto>((Exception) t));
            }
        });
        return future;
    }

    /**
     * Join a user to a certain group.
     */
    public Call<Void> joinUserToGroupAsync(String groupName, String email,
                                                    final Completion<Void> task){
        Log.d(TAG, "Join user " + email + " to group " + groupName);
        Call<Void> future = mService.joinUserToGroup(groupName, email);
        future.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                final CallResult<Void> result = response.isSuccessful() ?
                        new CallResult(response.body()) :
                        new CallResult<Void>(new Exception(response.errorBody().toString()));

                task.onResult(result);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                task.onResult(new CallResult<Void>((Exception) t));
            }
        });
        return future;
    }

    /**
     * Create a new user.
     */
    public Call<Void> createUserAsync(User user, final Completion<Void> task){
        Log.d(TAG, "Create User: " + user.getUserName() + " - " + user.getEmail());
        Call<Void> future = mService.createUser(user);
        future.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                final CallResult<Void> result = response.isSuccessful() ?
                        new CallResult(response.body()) :
                        new CallResult<Void>(new ApiError(response.code(), response.message()));

                task.onResult(result);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                task.onResult(new CallResult<Void>((Exception) t));
            }
        });
        return future;
    }

    /**
     * Retrieve a list of rules for a certain User.
     */
    public Call<RulesDto> getRulesByUserAsync(String userEmail, final Completion<RulesDto> task){
        Log.d(TAG, "Get rules for User " + userEmail);
        Call<RulesDto> future = mService.getRulesByUser(userEmail);
        future.enqueue(new Callback<RulesDto>() {
            @Override
            public void onResponse(Call<RulesDto> call, Response<RulesDto> response) {
                final CallResult<RulesDto> result = response.isSuccessful() ?
                        new CallResult(response.body()) :
                        new CallResult<RulesDto>(new Exception(response.errorBody().toString()));

                task.onResult(result);
            }

            @Override
            public void onFailure(Call<RulesDto> call, Throwable t) {
                task.onResult(new CallResult<RulesDto>((Exception) t));
            }
        });
        return future;
    }

    /**
     * Get the group associated with the given user's email.
     *
     * @param userEmail Email of user
     * @param task Async task
     * @return An instance of {@link GroupDto}
     */
    public Call<GroupDto> getGroupByUserAsync( String userEmail,
                                                    final Completion<GroupDto> task){
        Log.d(TAG, "Get group for User " + userEmail);
        Call<GroupDto> future = mService.getGroupByUser(userEmail);
        future.enqueue(new Callback<GroupDto>() {
            @Override
            public void onResponse(Call<GroupDto> call, Response<GroupDto> response) {
                final CallResult<GroupDto> result = response.isSuccessful() ?
                        new CallResult(response.body()) :
                        new CallResult<GroupDto>(new Exception(response.errorBody().toString()));

                task.onResult(result);
            }

            @Override
            public void onFailure(Call<GroupDto> call, Throwable t) {
                task.onResult(new CallResult<GroupDto>((Exception) t));
            }
        });
        return future;
    }


    /**
     * Upload a rule into the web service.
     * @param email Email of user
     * @param rule Rule to upload
     * @param task Async task
     */
    public Call<Void> uploadRules(@Path("email") String email, @Body final RuleDto rule,
                                  final Completion<Void> task){
        Log.d(TAG, "Upload set of rules to the user " + email);
        Call<Void> future = mService.uploadRules(email, rule);
        future.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                final CallResult<Void> result = response.isSuccessful() ?
                        new CallResult(response.body()) :
                        new CallResult<Void>(new Exception(response.errorBody().toString()));

                task.onResult(result);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                task.onResult(new CallResult<Void>((Exception) t));
            }
        });
        return future;
    }

    public Call<Void> logInfo(@Body final LogInfo log, final Completion<Void> task){
        Log.d(TAG, "Logging info about a certain rule");
        Call<Void> future = mService.logInfo(log);
        future.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                final CallResult<Void> result = response.isSuccessful() ?
                        new CallResult(response.body()) :
                        new CallResult<Void>(new Exception(response.errorBody().toString()));
                task.onResult(result);
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                task.onResult(new CallResult<Void>((Exception) t));
            }
        });
        return future;
    }

}
