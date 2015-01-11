package org.monroe.team.android.box.actor;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ActorActionBuilder<EventType extends ActorAction> {

    private final Context context;
    private final EventType event;

    private List<Pair<String,? extends Serializable>> parameterList = new ArrayList<Pair<String,? extends Serializable>>(3);

    public ActorActionBuilder(Context context, EventType event) {
        this.context = context;
        this.event = event;
    }

    final protected  <BuilderType extends ActorActionBuilder> BuilderType with(String name, Serializable value){
        parameterList.add(new Pair<String, Serializable>(name, value));
        return (BuilderType) this;
    }

    public final PendingIntent  build(){
        PendingIntent intent = event.createPendingIntent(context, parameterList);
        return intent;
    }

    public final Intent  buildIntent(){
        Intent intent = event.createIntent(context, parameterList);
        return intent;
    }

    public static boolean requested(String feature, Intent intent) {
        return intent.getBooleanExtra(feature, false);
    }
}
