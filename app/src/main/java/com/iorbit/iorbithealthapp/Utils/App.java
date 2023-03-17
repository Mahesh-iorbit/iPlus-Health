package com.iorbit.iorbithealthapp.Utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class App extends Application implements Application.ActivityLifecycleCallbacks {
    public static Activity context;
    public static Activity getContext() {
        return context;
    }

    @Override
    public void onActivityPreCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        ActivityLifecycleCallbacks.super.onActivityPreCreated(activity, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

    }

    @Override
    public void onActivityPostCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        ActivityLifecycleCallbacks.super.onActivityPostCreated(activity, savedInstanceState);
    }

    @Override
    public void onActivityPreStarted(@NonNull Activity activity) {
        ActivityLifecycleCallbacks.super.onActivityPreStarted(activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPostStarted(@NonNull Activity activity) {
        ActivityLifecycleCallbacks.super.onActivityPostStarted(activity);
    }

    @Override
    public void onActivityPreResumed(@NonNull Activity activity) {
        ActivityLifecycleCallbacks.super.onActivityPreResumed(activity);
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPostResumed(@NonNull Activity activity) {
        ActivityLifecycleCallbacks.super.onActivityPostResumed(activity);
    }

    @Override
    public void onActivityPrePaused(@NonNull Activity activity) {
        ActivityLifecycleCallbacks.super.onActivityPrePaused(activity);
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPostPaused(@NonNull Activity activity) {
        ActivityLifecycleCallbacks.super.onActivityPostPaused(activity);
    }

    @Override
    public void onActivityPreStopped(@NonNull Activity activity) {
        ActivityLifecycleCallbacks.super.onActivityPreStopped(activity);
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPostStopped(@NonNull Activity activity) {
        ActivityLifecycleCallbacks.super.onActivityPostStopped(activity);
    }

    @Override
    public void onActivityPreSaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        ActivityLifecycleCallbacks.super.onActivityPreSaveInstanceState(activity, outState);
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityPostSaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        ActivityLifecycleCallbacks.super.onActivityPostSaveInstanceState(activity, outState);
    }

    @Override
    public void onActivityPreDestroyed(@NonNull Activity activity) {
        ActivityLifecycleCallbacks.super.onActivityPreDestroyed(activity);
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPostDestroyed(@NonNull Activity activity) {
        ActivityLifecycleCallbacks.super.onActivityPostDestroyed(activity);
    }
}
