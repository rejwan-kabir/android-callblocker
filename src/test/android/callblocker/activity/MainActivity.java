package test.android.callblocker.activity;

import java.util.ArrayList;
import java.util.Arrays;

import test.android.callblocker.R;
import test.android.callblocker.common.enumeration.Enum_ListPopulationMode;
import test.android.callblocker.common.enumeration.Enum_UserCreationMode;
import test.android.callblocker.domain.Person;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final int PICK_CONTACT_FROM_CONTACT_BOOK = 100,
			RESULT_SETTINGS = 200;
	private static final String[] QUERY_CONTACT_CONTACT_BOOK = new String[] {
			ContactsContract.Contacts.DISPLAY_NAME,
			ContactsContract.Contacts._ID,
			ContactsContract.Contacts.HAS_PHONE_NUMBER },
			QUERY_PHONE = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER };
	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences( this );
		sharedPreferences
				.edit()
				.putBoolean(
						this.getResources().getString(
								R.string.notify_call_arrive ), true ).commit();
	}

	public void recentCalls( View view ) {
		Intent viewAllIntent = new Intent( this, AllUserActivity.class );
		viewAllIntent.putExtra( AllUserActivity.POPULATION_MODE,
				Enum_ListPopulationMode.FROM_RECENT_CALL.getName() );
		this.startActivity( viewAllIntent );
	}

	public void viewAll( View view ) {
		Intent viewAllIntent = new Intent( this, AllUserActivity.class );
		viewAllIntent.putExtra( AllUserActivity.POPULATION_MODE,
				Enum_ListPopulationMode.FROM_VIEW_ALL.getName() );
		this.startActivity( viewAllIntent );
	}

	public void addNew( View view ) {
		new AlertDialog.Builder( this )
				.setTitle( "Add New Block" )
				.setIcon( R.drawable.home_button_addnew )
				.setItems(
						new String[] { "Contact Book", "Create New" },
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick( DialogInterface dialog,
									int which ) {
								switch ( which ) {
								case 0 :
									Intent contactPickFromContactBook = new Intent(
											Intent.ACTION_PICK,
											ContactsContract.Contacts.CONTENT_URI );
									MainActivity.this.startActivityForResult(
											contactPickFromContactBook,
											PICK_CONTACT_FROM_CONTACT_BOOK );
									break;
								case 1 :
									Intent createUserIntent = new Intent(
											MainActivity.this,
											SingleUserActivity.class );
									createUserIntent.putExtra(
											SingleUserActivity.CREATION_MODE,
											Enum_UserCreationMode.NEW_USER
													.getName() );
									createUserIntent.putExtra(
											SingleUserActivity._ID, -1l );
									MainActivity.this
											.startActivity( createUserIntent );
									break;
								default :

								}
							}
						} ).create().show();

		/*
		 * Intent contactPickFromCallLog = new Intent( Intent.ACTION_PICK,
		 * CallLog.Calls.CONTENT_URI ); this.startActivityForResult(
		 * contactPickFromCallLog, PICK_CONTACT_FROM_CALL_LOG );
		 */
	}

	public void unblock( View view ) {
		Intent viewAllIntent = new Intent( this, AllUserActivity.class );
		viewAllIntent.putExtra( AllUserActivity.POPULATION_MODE,
				Enum_ListPopulationMode.FROM_UNBLOCK.getName() );
		this.startActivity( viewAllIntent );
	}

	public void settings( View view ) {
		Intent settingsIntent = new Intent( this, SettingsActivity.class );
		this.startActivityForResult( settingsIntent,
				MainActivity.RESULT_SETTINGS );
	}

	public void about( View view ) {
		new AlertDialog.Builder( this )
				.setTitle( "About Software" )
				.setIcon( R.drawable.home_button_about )
				.setMessage(
						Html.fromHtml( this.getResources().getString(
								R.string.about_text ) ) )
				.setPositiveButton( "Gotcha !",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick( DialogInterface dialog,
									int which ) {
								dialog.dismiss();
							}
						} ).create().show();
	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode,
			Intent data ) {
		if ( requestCode == PICK_CONTACT_FROM_CONTACT_BOOK ) {
			if ( data == null ) {
				return;
			}
			Uri contactUri = data.getData();
			if ( resultCode == Activity.RESULT_OK ) {
				final Person block = this
						.getContactfromContactBook( contactUri );
				if ( block.getContactNumber().size() == 0 ) {
					Toast.makeText( MainActivity.this,
							"No Numbers of selected contact recorder",
							Toast.LENGTH_LONG ).show();
				} else if ( block.getContactNumber().size() == 1 ) {
					MainActivity.this.createEntryFromContactBook( block );
				} else {
					final ArrayList<String> selectedNumber = new ArrayList<String>(
							block.getContactNumber() );
					String[] phoneNumbers = new String[block.getContactNumber()
							.size()];
					boolean[] checked = new boolean[block.getContactNumber()
							.size()];
					Arrays.fill( checked, true );
					new AlertDialog.Builder( this )
							.setTitle( "Import Number" )
							.setIcon( R.drawable.ic_launcher )
							.setMultiChoiceItems(
									block.getContactNumber().toArray(
											phoneNumbers ),
									checked,
									new DialogInterface.OnMultiChoiceClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which, boolean isChecked ) {
											if ( isChecked ) {
												selectedNumber.add( block
														.getContactNumber()
														.get( which ) );
											} else if ( selectedNumber
													.contains( block
															.getContactNumber()
															.get( which ) ) ) {
												selectedNumber.remove( which );
											}
										}
									} )
							.setPositiveButton( "Ok",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which ) {
											if ( selectedNumber.size() == 0 ) {
												Toast.makeText(
														MainActivity.this,
														"No Numbers were chosen",
														Toast.LENGTH_LONG )
														.show();
											} else {
												block.setContactNumber( selectedNumber );
												Log.d( TAG,
														"Selected Number : "
																+ block.getContactNumber()
																		.toString() );
												MainActivity.this
														.createEntryFromContactBook( block );
											}
										}
									} )
							.setNegativeButton( "Cancel",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which ) {
										}
									} ).create().show();
				}
			}
		} else if ( requestCode == RESULT_SETTINGS ) {
			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences( this );
			Log.d( TAG,
					"save call log ? = "
							+ sharedPreferences.getBoolean( this.getResources()
									.getString( R.string.notify_call_arrive ),
									true ) );
			sharedPreferences
					.edit()
					.putBoolean(
							this.getResources().getString(
									R.string.notify_call_arrive ),
							sharedPreferences.getBoolean( this.getResources()
									.getString( R.string.notify_call_arrive ),
									true ) ).commit();
		}
	}

	private void createEntryFromContactBook( Person block ) {
		Intent singleUserScreenIntent = new Intent( this,
				SingleUserActivity.class );
		singleUserScreenIntent.putExtra( SingleUserActivity.CREATION_MODE,
				Enum_UserCreationMode.NEW_USER.getName() );
		singleUserScreenIntent.putExtra( SingleUserActivity._ID, -1l );
		Log.d( TAG, "Passing -1" );
		singleUserScreenIntent.putExtra( SingleUserActivity.CONTACT_NAME,
				block.getContactName() );
		singleUserScreenIntent.putStringArrayListExtra(
				SingleUserActivity.CONTACT_NUMBER, block.getContactNumber() );
		this.startActivity( singleUserScreenIntent );
	}

	private Person getContactfromContactBook( Uri data ) {
		Person person = new Person();
		Cursor cursor = this.getContentResolver().query( data,
				MainActivity.QUERY_CONTACT_CONTACT_BOOK, null, null, null );
		if ( cursor.moveToFirst() ) {
			String name = cursor.getString( cursor
					.getColumnIndex( ContactsContract.Contacts.DISPLAY_NAME ) );
			Log.d( MainActivity.TAG, "name : " + name );
			person.setContactName( name );
			int contactbook_id = Integer.parseInt( cursor.getString( cursor
					.getColumnIndex( ContactsContract.Contacts._ID ) ) );
			Log.d( MainActivity.TAG, "_id : " + contactbook_id );
			boolean hasNumber = cursor
					.getString(
							cursor.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER ) )
					.trim().equalsIgnoreCase( "1" );
			if ( hasNumber ) {
				Cursor cursorPhone = this.getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						MainActivity.QUERY_PHONE,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = ?", new String[] { "" + contactbook_id },
						null );
				String number = "";
				while ( cursorPhone.moveToNext() ) {
					String currentNumber = cursorPhone
							.getString( cursorPhone
									.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER ) );
					number += "\"" + currentNumber + "\",";
					person.addContactNumber( currentNumber );
				}
				Log.d( MainActivity.TAG, "number : " + number );
			}
		}
		return person;
	}
}
