package com.xzero.refreshreply.helpers;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by kemo on 6/7/15.
 */
public class KeyBoardUtil
{
    public static void hideKeyboard(Activity activity)
    {
        try
        {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        catch (Exception e)
        {
            // Ignore exceptions if any
            //Log.e("KeyBoardUtil", e.toString(), e);
        }
    }
}