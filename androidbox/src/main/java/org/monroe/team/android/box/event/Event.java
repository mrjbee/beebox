package org.monroe.team.android.box.event;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import org.monroe.team.android.box.Closure;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Event<DataType>{

    private static final Map<Object, List<BroadcastReceiver>> registerMap = new HashMap<Object, List<BroadcastReceiver>>();

    public static <DataT> void  send(Context context, Event<DataT> event, DataT data){
        event.send(context,data);
    }

    public static void subscribeOnEvent(Context context, Object owner, final Closure<Void, Void> onEvent, final Event<?>... events){
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onEvent.execute(null);
            }
        };

        for (Event<?> event : events) {
            context.registerReceiver(receiver,new IntentFilter(event.getAction()));
        }
        register(owner, receiver);
    }


    public static <DataT> void subscribeOnEvent(Context context, Object owner, final Event<DataT> event, final Closure<DataT, Void> onEvent){
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onEvent.execute(event.extractValue(intent));
            }
        };

        context.registerReceiver(receiver,new IntentFilter(event.getAction()));
        register(owner, receiver);
    }

    public static <DataT> void unSubscribeFromEvents(Context context, Object owner){
        if (registerMap.get(owner) != null){
            for (BroadcastReceiver receiver : registerMap.get(owner)) {
                context.unregisterReceiver(receiver);
            }
            registerMap.get(owner).clear();
        }
    }

    private static void register(Object owner, BroadcastReceiver receiver) {
        if (registerMap.get(owner) == null){
            registerMap.put(owner, new ArrayList<BroadcastReceiver>(2));
        }
        registerMap.get(owner).add(receiver);
    }


    public void send(Context context, DataType data){
        Intent intent = new Intent(getAction());
        putValue(intent, data);
        context.sendBroadcast(intent);
    }

    protected abstract DataType extractValue(Intent intent);
    protected abstract void putValue(Intent intent, DataType data);
    public abstract String getAction();

}
