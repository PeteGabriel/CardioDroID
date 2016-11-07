package com.dev.cardioid.ps.cardiodroid.services.location;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.dev.cardioid.ps.cardiodroid.contexts.ContextEnvironment;
import com.dev.cardioid.ps.cardiodroid.contexts.validation.RulesValidator;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;


public class GeofenceTransitionsIntentService extends IntentService {

    public static final String TAG = Utils.makeLogTag(GeofenceTransitionsIntentService.class);


    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
    }

    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = "Error Code: " + geofencingEvent.getErrorCode();
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            String requestId = ((Geofence)triggeringGeofences.get(0)).getRequestId();

            RulesValidator.startService(getApplicationContext(), ContextEnvironment.Types.LOCATION);

        } else {
            // Log the error.
            Log.e(TAG, "Invalid Type of Geofence Transition");
        }
    }


}
