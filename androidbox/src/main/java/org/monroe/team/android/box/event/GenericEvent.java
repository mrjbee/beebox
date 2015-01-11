package org.monroe.team.android.box.event;

import android.content.Intent;
import android.os.Bundle;

import java.io.Serializable;

public class GenericEvent <Data extends Serializable> extends Event<Data>{

    private final String actionId;

    public GenericEvent(String actionId) {
        this.actionId = actionId;
    }

    @Override
    public Data extractValue(Intent intent) {
        return (Data) intent.getExtras().getSerializable("DATA");
    }

    @Override
    protected void putValue(Intent intent, Data data) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("DATA",data);
        intent.putExtras(bundle);
    }

    @Override
    public String getAction() {
        return this.getClass().getPackage().getName()+"."+actionId;
    }
}
