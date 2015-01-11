package org.monroe.team.android.box.utils;

import android.content.res.Resources;
import android.util.TypedValue;

import org.monroe.team.android.box.Closure;

final public class DisplayUtils {

    private DisplayUtils() {}

    public static float dpToPx(float dp, Resources resources){
       return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }

    public static float screenHeight(Resources resources){
       return resources.getDisplayMetrics().heightPixels;
    }

    public static float screenWidth(Resources resources){
        return resources.getDisplayMetrics().widthPixels;
    }


    public static boolean isLandscape(Resources resources, Class resourceClass){
        return resources.getBoolean(ResourceUtils.resourceID(resourceClass,"is_landscape"));
    }


    public static void landscape_portrait (Resources resources, Class boolResourceClass,
                                           Closure<Void,Void> landscape,
                                           Closure<Void,Void> portrait){
        if (isLandscape(resources, boolResourceClass)){
            landscape.execute(null);
        } else {
            portrait.execute(null);
        }
    }

}
