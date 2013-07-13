package test.android.callblocker.receiver;

import java.lang.reflect.Method;

import test.android.callblocker.R;
import test.android.callblocker.activity.AllUserActivity;
import test.android.callblocker.common.enumeration.Enum_ListPopulationMode;
import test.android.callblocker.database.DatabaseAdapter;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

public class IncomingCallReceiver extends BroadcastReceiver {
	private static final String TAG = "IncomingCallReceiver";
	private static DatabaseAdapter databaseAdapter;

	public IncomingCallReceiver() {

	}

	@Override
	public void onReceive( Context context, Intent intent ) {
		if ( intent.getStringExtra( TelephonyManager.EXTRA_STATE ).equals(
				TelephonyManager.EXTRA_STATE_RINGING ) ) {
			String incomingNumber = intent
					.getStringExtra( TelephonyManager.EXTRA_INCOMING_NUMBER );
			Log.d( TAG, "Incoming Number = " + incomingNumber );
			// TODO : see
			// http://prasanta-paul.blogspot.com/2010/09/call-control-in-android.html
			// TODO : for sms , also see http://androidsourcecode.blogspot.com/
			if ( IncomingCallReceiver.databaseAdapter == null ) {
				IncomingCallReceiver.databaseAdapter = new DatabaseAdapter(
						context );
			}
			TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService( Context.TELEPHONY_SERVICE );

			if ( incomingNumber.startsWith( "+" ) ) {
				incomingNumber = incomingNumber.substring( 1 );
			}
			try {
				Class<?> c = Class.forName( telephonyManager.getClass()
						.getName() );
				Method method = c.getDeclaredMethod( "getITelephony" );
				method.setAccessible( true );
				ITelephony iTelephony = (ITelephony) method
						.invoke( telephonyManager );
				String name = IncomingCallReceiver.databaseAdapter
						.getPersonForBlock( incomingNumber );
				if ( !name.equals( null ) ) {
					iTelephony.endCall(); // works for Android emulator
					Log.d( TAG, "Call ended from = " + incomingNumber );
					// Now push notification
					// TODO : view
					// http://stackoverflow.com/questions/12924835/android-notification-from-broadcastreceiver
					if ( PreferenceManager
							.getDefaultSharedPreferences( context )
							.getBoolean(
									context.getResources().getString(
											R.string.notify_call_arrive ), true ) ) {
						Intent recentCallIntent = new Intent( context,
								AllUserActivity.class );
						recentCallIntent.putExtra(
								AllUserActivity.POPULATION_MODE,
								Enum_ListPopulationMode.FROM_RECENT_CALL
										.getName() );
						PendingIntent notificationIntent = PendingIntent
								.getActivity( context, 0, recentCallIntent, 0 );
						Notification notification = new NotificationCompat.Builder(
								context )
								.setSmallIcon( R.drawable.ic_launcher )
								.setContentTitle(
										context.getResources().getString(
												R.string.app_name ) )
								.setContentText(
										"Call blocked from " + name + " ( "
												+ incomingNumber + " )" )
								.setContentIntent( notificationIntent )
								.setAutoCancel( true ).build();
						NotificationManager notificationManager = (NotificationManager) context
								.getSystemService( Context.NOTIFICATION_SERVICE );
						notification.flags |= Notification.FLAG_AUTO_CANCEL;
						notificationManager.notify( 1, notification );
					}
				}
			} catch ( Exception e ) {
				Log.d( TAG, "Exception !" );
			}
		}
	}
}
