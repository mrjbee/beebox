package org.monroe.team.android.box.manager;

import android.content.Context;

import org.monroe.team.android.box.event.Event;

public class EventMessenger {

    private final Context context;

    public EventMessenger(Context context) {
        this.context = context;
    }

    public <Data> void send(Event<Data> event, Data data){
        Event.send(context, event, data);
    }
}
