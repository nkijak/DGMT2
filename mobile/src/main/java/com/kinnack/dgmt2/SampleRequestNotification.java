package com.kinnack.dgmt2;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.kinnack.dgmt2.service.RecordHandlerService;

/**
 * Helper class for showing and canceling sample request
 * notifications.
 * <p/>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class SampleRequestNotification {
    public static final String EXTRA_VOICE_REPLY="voice_tag";
    public static final String EXTRA_SELECTED_TAG = "selected_tag";
    public static final int CHOICE_COUNT = 8;

    /**
     * The unique identifier for this type of notification.
     */
    private static final String NOTIFICATION_TAG = "SampleRequest";

    /**
     * Shows the notification, or updates a previously shown notification of
     * this type, with the given parameters.
     * <p/>
     * TODO: Customize this method's arguments to present relevant content in
     * the notification.
     * <p/>
     * TODO: Customize the contents of this method to tweak the behavior and
     * presentation of sample request notifications. Make
     * sure to follow the
     * <a href="https://developer.android.com/design/patterns/notifications.html">
     * Notification design guidelines</a> when doing so.
     *
     */
    public static void notify(final Context context,
                              final long when,
                              final int number) {
        final Resources res = context.getResources();

//
//        final String title = res.getString(
//                R.string.sample_request_notification_title_template);
//        final String text = "Placeholder note text"; //res.getString( R.string.sample_request_notification_placeholder_text_template);
//
//        RecordHandlerService.Request requestForIntent = submissionIntent.forTime(when);
//
//
//        final NotificationCompat.WearableExtender wear = new NotificationCompat.WearableExtender()
////                .addAction(tagAction(context, requestForIntent, "fishing"))
////                .addAction(tagAction(context, requestForIntent, "programming"))
//                //.addAction(voiceAction(context, requestForIntent, tagGen))
//                ;
//
//        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
//
//                // Set appropriate defaults for the notification light, sound,
//                // and vibration.
//                .setDefaults(Notification.DEFAULT_ALL)
//
//                        // Set required fields, including the small icon, the
//                        // notification title, and text.
//                //.setSmallIcon(R.drawable.ic_sample_request)
//                .setContentTitle(title)
//                .setContentText(text)
//
//                        // All fields below this line are optional.
//
//                        // Use a default priority (recognized on devices running Android
//                        // 4.1 or later)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//
//                        // Set ticker text (preview) information for this notification.
//                .setTicker(text)
//
//                        // Show a number. This is useful when stacking notifications of
//                        // a single type.
//                .setNumber(number)
//
//                        // If this notification relates to a past or upcoming event, you
//                        // should set the relevant time information using the setWhen
//                        // method below. If this call is omitted, the notification's
//                        // timestamp will by set to the time at which it was shown.
//                        // TODO: Call setWhen if this notification relates to a past or
//                        // upcoming event. The sole argument to this method should be
//                        // the notification timestamp in milliseconds.
//                //.setWhen(...)
//
//                        // Set the pending intent to be initiated when the user touches
//                        // the notification.
//                //.setContentIntent(getSampleActivityIntent(context, SamplingActivity.gatherSample(context, when)))
//                        // Example additional actions for this notification. These will
//                        // only show on devices running Android 4.1 or later, so you
//                        // should ensure that the activity in this notification's
//                        // content intent provides access to the same actions in
//                        // another way.
////                .addAction(
////                        R.drawable.ic_action_stat_share,
////                        res.getString(R.string.action_share),
////                        PendingIntent.getActivity(
////                                context,
////                                0,
////                                Intent.createChooser(new Intent(Intent.ACTION_SEND)
////                                        .setType("text/plain")
////                                        .putExtra(Intent.EXTRA_TEXT, "Dummy text"), "Dummy title"),
////                                PendingIntent.FLAG_UPDATE_CURRENT))
//                .extend(wear)
//                        // Automatically dismiss the notification when it is touched.
//                .setAutoCancel(true);
//
//        notify(context, builder.build());
//    }
//
//    @TargetApi(Build.VERSION_CODES.ECLAIR)
//    private static void notify(final Context context, final Notification notification) {
//        final NotificationManager nm = (NotificationManager) context
//                .getSystemService(Context.NOTIFICATION_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
//            nm.notify(NOTIFICATION_TAG, 0, notification);
//        } else {
//            nm.notify(NOTIFICATION_TAG.hashCode(), notification);
//        }
//    }
//
//    /**
//     * Cancels any notifications of this type previously shown using
//     * {@link #notify(Context, String, int)}.
//     */
//    @TargetApi(Build.VERSION_CODES.ECLAIR)
//    public static void cancel(final Context context) {
//        final NotificationManager nm = (NotificationManager) context
//                .getSystemService(Context.NOTIFICATION_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
//            nm.cancel(NOTIFICATION_TAG, 0);
//        } else {
//            nm.cancel(NOTIFICATION_TAG.hashCode());
//        }
//    }
//
//
//    protected static PendingIntent getSampleActivityIntent(Context context, Intent intent) {
//       return PendingIntent.getActivity(
//               context,
//               0,
//               intent,
//               PendingIntent.FLAG_UPDATE_CURRENT);
//    }
//
//    protected static PendingIntent getServiceIntent(Context context, Intent intent) {
//        return PendingIntent.getService(
//                context,
//                0,
//                intent,
//                PendingIntent.FLAG_UPDATE_CURRENT
//        );
    }
//
//    protected static final RecordHandlerService.Request submissionIntent = RecordHandlerService.Request.forSubmission();
//
//    protected static NotificationCompat.Action tagAction(final Context context, RecordHandlerService.Request request, final String tag) {
//        Intent intent = request.addTags(tag).build(context);
//
//        return new NotificationCompat.Action.Builder(
//                R.drawable.ic_plusone_medium_off_client,
//                tag,
//                getServiceIntent(context, intent))
//                .build();
//    }

//    protected static NotificationCompat.Action voiceAction(final Context context, RecordHandlerService.Request request, Gen<Option<TagRepo.TagCount>> tagByCount) {
//        String replyLabel = String.format(context.getResources().getString(R.string.input_reply), new SimpleDateFormat(context.getString(R.string.time_format)).format(new Date()));
//
//        List<String> tags = new ArrayList<String>(3);
//        for (int i = 0; i < CHOICE_COUNT; i++) {
//            for (TagRepo.TagCount tagCount : tagByCount.generate()) {
//                tags.add(tagCount.getTag());}}
//
//
//        RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_VOICE_REPLY)
//                .setLabel(replyLabel)
//                .setChoices(tags.toArray(new String[]{}))
//                .build();
//        Intent intent = request.build(context);
//        return new NotificationCompat.Action.Builder(
//                R.drawable.ic_action_stat_reply,
//                context.getResources().getString(R.string.action_reply),
//                getServiceIntent(context, intent))
//            .addRemoteInput(remoteInput)
//            .build();
//   }
}