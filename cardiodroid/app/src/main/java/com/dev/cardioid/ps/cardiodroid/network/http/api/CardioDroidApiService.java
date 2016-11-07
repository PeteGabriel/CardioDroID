package com.dev.cardioid.ps.cardiodroid.network.http.api;

import com.dev.cardioid.ps.cardiodroid.models.LogInfo;
import com.dev.cardioid.ps.cardiodroid.models.User;
import com.dev.cardioid.ps.cardiodroid.network.dtos.cardio_api.GroupDto;
import com.dev.cardioid.ps.cardiodroid.network.dtos.cardio_api.GroupsDto;
import com.dev.cardioid.ps.cardiodroid.network.dtos.cardio_api.RuleDto;
import com.dev.cardioid.ps.cardiodroid.network.dtos.cardio_api.RulesDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * This interface represents the communication available
 * for the app to interact with the Web Service built
 * to support the application in terms of sharing rules
 * between users.
 */
public interface CardioDroidApiService {

    //TODO add @Body String token to every method

    /**
     * Create a new group.
     */
    @POST("/api/groups")
    Call<Void> createGroup(@Body GroupDto groupDto);

    /**
     * Retrieve a list of all groups.
     */
    @GET("/api/groups")
    Call<GroupsDto> getGroups();

    /**
     * Join a user to a certain group.
     */
    @POST("/api/groups/{groupName}/users/{email}")
    Call<Void> joinUserToGroup(@Path("groupName") String groupName, @Path("email") String email);

    /**
     * Create a new user.
     */
    @POST("/api/users")
    Call<Void> createUser(@Body User user);

    /**
     * Retrieve a list of rules for a certain User.
     */
    @Headers("Content-Type: application/json")
    @GET("/api/rules/{user_email}")
    Call<RulesDto> getRulesByUser(@Path("user_email") String userEmail);//, @Query("shared") String sharedStatus);

    /**
     * obtencao do grupo de um utilizador
     */
    @GET("/api/users/{email}/group")
    Call<GroupDto> getGroupByUser(@Path("email") String userEmail);


    /**
     * Create a new user.
     */
    @POST("/api/rules/{email}")
    Call<Void> uploadRules(@Path("email") String email, @Body RuleDto rule);

    /**
     * Send information about a certain broken rule.
     */
    @POST("/api/logs")
    Call<Void> logInfo(@Body LogInfo logInfoInstance);
}
