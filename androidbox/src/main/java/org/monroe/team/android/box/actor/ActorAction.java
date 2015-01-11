package org.monroe.team.android.box.actor;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class ActorAction implements Serializable {

    private final String name;
    private final int pendingId;
    private final Class<? extends BroadcastReceiver> owner;

    public ActorAction(String name, int pendingId, Class<? extends BroadcastReceiver> owner) {
        this.name = name;
        this.pendingId = pendingId;
        this.owner = owner;
    }

    public PendingIntent createPendingIntent(Context context) {
        return createPendingIntent(context, Collections.EMPTY_LIST);
    }

    public PendingIntent createPendingIntent(Context context, List<Pair<String, ? extends Serializable>> parameterList) {
        Intent intent = createIntent(context, parameterList);
        return PendingIntent.getBroadcast(context, pendingId, intent, 0);
    }

    public Intent createIntent(Context context, List<Pair<String, ? extends Serializable>> parameterList) {
        Intent intent = new Intent(context, owner);
        intent.putExtra("NAME", name);
        for (Pair<String,? extends Serializable> stringSerializablePair : parameterList) {
            intent.putExtra(stringSerializablePair.first,stringSerializablePair.second);
        }
        return intent;
    }

    public boolean isMe(Intent intent){
        return name.equals(intent.getStringExtra("NAME"));
    }


}
