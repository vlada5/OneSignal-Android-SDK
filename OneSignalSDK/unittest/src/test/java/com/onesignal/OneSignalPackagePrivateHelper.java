package com.onesignal;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;

import org.json.JSONObject;
import org.robolectric.util.Scheduler;

import java.util.Map;

import static org.robolectric.Shadows.shadowOf;

public class OneSignalPackagePrivateHelper {
   public static boolean runAllNetworkRunnables() {
      boolean startedRunnable = false;
      for (Map.Entry<Integer, OneSignalStateSynchronizer.NetworkHandlerThread> handlerThread : OneSignalStateSynchronizer.networkHandlerThreads.entrySet()) {
         Scheduler scheduler = shadowOf(handlerThread.getValue().getLooper()).getScheduler();
         if (scheduler.advanceToLastPostedRunnable())
            startedRunnable = true;
      }

      return startedRunnable;
   }

   public static boolean runFocusRunnables() {
      Looper looper = ActivityLifecycleHandler.focusHandlerThread.getHandlerLooper();
      if (looper == null) return false;
      
      Scheduler scheduler = shadowOf(looper).getScheduler();
      return scheduler.advanceToLastPostedRunnable();
   }

   public static void resetRunnables() {
      for (Map.Entry<Integer, OneSignalStateSynchronizer.NetworkHandlerThread> handlerThread : OneSignalStateSynchronizer.networkHandlerThreads.entrySet())
         handlerThread.getValue().stopScheduledRunnable();

      Looper looper = ActivityLifecycleHandler.focusHandlerThread.getHandlerLooper();
      if (looper == null) return;

      shadowOf(looper).reset();
   }

   public static void SyncService_onTaskRemoved() {
      SyncService.onTaskRemoved();
   }

   public static JSONObject bundleAsJSONObject(Bundle bundle) {
      return NotificationBundleProcessor.bundleAsJSONObject(bundle);
   }

   public static Bundle createInternalPayloadBundle(Bundle bundle) {
      Bundle retBundle = new Bundle();
      retBundle.putString("json_payload", OneSignalPackagePrivateHelper.bundleAsJSONObject(bundle).toString());
      return retBundle;
   }

   public static void NotificationBundleProcessor_ProcessFromGCMIntentService(Context context, Bundle bundle, NotificationExtenderService.OverrideSettings overrideSettings) {
      NotificationBundleProcessor.ProcessFromGCMIntentService(context, createInternalPayloadBundle(bundle), overrideSettings);
   }

   public static void NotificationBundleProcessor_ProcessFromGCMIntentService_NoWrap(Context context, Bundle bundle, NotificationExtenderService.OverrideSettings overrideSettings) {
      NotificationBundleProcessor.ProcessFromGCMIntentService(context, bundle, overrideSettings);
   }

   public static boolean GcmBroadcastReceiver_processBundle(Context context, Bundle bundle) {
      return NotificationBundleProcessor.processBundle(context, bundle);
   }

   public static int NotificationBundleProcessor_Process(Context context, boolean restoring, JSONObject jsonPayload, NotificationExtenderService.OverrideSettings overrideSettings) {
      return NotificationBundleProcessor.Process(context, restoring, jsonPayload, overrideSettings);
   }

   public class NotificationTable extends OneSignalDbContract.NotificationTable { }
   public class NotificationRestorer extends com.onesignal.NotificationRestorer { }
}
