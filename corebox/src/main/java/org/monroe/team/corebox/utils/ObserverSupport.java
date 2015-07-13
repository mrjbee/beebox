package org.monroe.team.corebox.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ObserverSupport <ObserverType> {

    private final List<ObserverType> observerList = new ArrayList<>();

    public synchronized void add(ObserverType observer){
        observerList.add(observer);
    }

    public synchronized void remove(ObserverType observer){
        observerList.remove(observer);
    }
    public synchronized void notify(final Closure<ObserverType, Void> notificationFunction){
        Lists.iterateAndRemove(observerList, new Closure<Iterator<ObserverType>, Boolean>() {
            @Override
            public Boolean execute(Iterator<ObserverType> arg) {
                ObserverType observer = arg.next();
                notificationFunction.execute(observer);
                return false;
            }
        });
    }
}
