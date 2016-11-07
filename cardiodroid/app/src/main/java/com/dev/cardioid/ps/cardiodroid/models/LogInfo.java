package com.dev.cardioid.ps.cardiodroid.models;

import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Represents the info sent to the remote service.
 * {
 *  contexts : [
 *    {
 *     type : <tipo_do_contexto>,
 *     value: <valor_do_contexto>
 *    }
 *  ]
 * }
 */
public final class LogInfo{

    public static final String VALUE = "value";
    public static final String TYPE = "type";

    private List<ContextLogInfo> contexts;
    private String date;
    private String user;

    public LogInfo(String userEmail){
        contexts = new ArrayList<>();
        date = DateTime.now().toString(DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss"));
        user = userEmail;
    }

    public String getDate() {
        return date;
    }

    public String getUser() {
        return user;
    }

    public List<ContextLogInfo> getContexts() {
        return contexts;
    }

    public void setContexts(List<ContextLogInfo> contexts) {
        this.contexts = contexts;
    }
}
