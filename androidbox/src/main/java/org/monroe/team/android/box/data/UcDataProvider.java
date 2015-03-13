package org.monroe.team.android.box.data;

import android.content.Context;

import org.monroe.team.android.box.event.Event;
import org.monroe.team.corebox.app.Model;
import org.monroe.team.corebox.uc.UserCase;

import java.io.Serializable;

public class UcDataProvider <UCResponse extends Serializable> extends DataProvider<UCResponse>{

    private final Class<? extends UserCase<Void,UCResponse>> userCaseClass;

    public UcDataProvider(Model model, Context context, Class<UCResponse> dataClass, Event<UCResponse> event, Class<? extends UserCase<Void, UCResponse>> userCaseClass) {
        super(dataClass, model, context, event);
        this.userCaseClass = userCaseClass;
    }

    public UcDataProvider(Model model, Context context, Class<UCResponse> dataClass, Class<? extends UserCase<Void, UCResponse>> userCaseClass) {
        super(dataClass, model, context);
        this.userCaseClass = userCaseClass;
    }

    @Override
    protected UCResponse provideData() {
        return model.execute(userCaseClass,null);
    }

}
