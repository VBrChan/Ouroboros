package com.luorrak.ouroboros.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Ouroboros - An 8chan browser
 * Copyright (C) 2015  Luorrak
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

public class SettingsHelper {
    public static int getTheme(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String themeValue = sharedPreferences.getString("theme_preference", "0");
        return Integer.valueOf(themeValue);
    }

    public static int getThreadView(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String threadView = sharedPreferences.getString("thread_view", "0");
        return Integer.valueOf(threadView);
    }

    public static void setThreadView(Context context, int layoutValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("thread_view", String.valueOf(layoutValue));
        editor.apply();
    }

    public static int getCatalogView(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String catalogView = sharedPreferences.getString("catalog_view", "0");
        return Integer.valueOf(catalogView);
    }

    public static void setCatalogView(Context context, int layoutValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("catalog_view", String.valueOf(layoutValue));
        editor.apply();
    }

    public static int getCatalogColumns(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String catalogGridColumns = sharedPreferences.getString("catalog_grid_columns", "3");
        return Integer.valueOf(catalogGridColumns);
    }

    public static String getDefaultName(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String defaultName = sharedPreferences.getString("default_name", "");
        return defaultName;
    }

    public static String getDefaultEmail(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String defaultEmail = sharedPreferences.getString("default_email", "");
        return defaultEmail;
    }

    public static String getPostPassword(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String postPassword = sharedPreferences.getString("post_password", "");
        return postPassword;
    }

    public static void setPostPassword(Context context, String postPassword){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("post_password", postPassword);
        editor.apply();
    }
}
