package org.monroe.team.android.box.actor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class Actor extends BroadcastReceiver {

    public <Type> Type reactOn(ActorAction actorAction, Intent intent,Type defValue, Reaction<Type> reaction){
        if (actorAction.isMe(intent)){
            return reaction.react(intent);
        }
        return defValue;
    }

    public void reactOn(ActorAction actorAction, Intent intent, Reaction<?> reaction){
        if (actorAction.isMe(intent)){
            reaction.react(intent);
        }
    }

    public static interface Reaction <ResultType> {
        public ResultType react(Intent intent);
    }

    public static abstract class SilentReaction implements Reaction<Void> {
        @Override
        final public Void react(Intent intent) {
            reactSilent(intent);
            return null;
        }

        protected abstract void reactSilent(Intent intent);
    }
}
