package com.xzero.refreshreply.helpers;

import android.app.Activity;
import android.content.Intent;

import com.xzero.refreshreply.R;

public class Utils {
    private static int sTheme;

    public final static int MALE_THEME = 0;
    public final static int FEMALE_THEME = 1;

    public static void changeToTheme(Activity activity, int theme) {
        sTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
        activity.overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
    }

    public static void onActivityCreateSetTheme(Activity activity) {
        switch (sTheme) {
        default:
        case MALE_THEME:
            activity.setTheme(R.style.Theme_Male);
            break;
        case FEMALE_THEME:
            activity.setTheme(R.style.Theme_Female);
            break;
        }
    }
}