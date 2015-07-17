package org.monroe.team.android.box.utils;


import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;

import org.monroe.team.corebox.utils.Closure;

public class Views {

    public static View findChild(ViewGroup group, Closure<Pair<Integer,View>, Boolean> closure){
        for (int i=0; i<group.getChildCount(); i++){
            if(closure.execute(new Pair<Integer, View>(i, group.getChildAt(i)))){
             return group.getChildAt(i);
            }
        }
        return null;
    }

    public static <ViewType> ViewType viewAs(View view, Class<ViewType> type){
        return (ViewType) view;
    }

    public static int color(View view, int colorRes, int fallbackColor) {
        if (view.isInEditMode()){
            return fallbackColor;
        }
        return view.getResources().getColor(colorRes);
    }
}
