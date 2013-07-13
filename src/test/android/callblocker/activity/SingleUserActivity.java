package test.android.callblocker.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import test.android.callblocker.R;
import test.android.callblocker.common.enumeration.Enum_UserCreationMode;
import test.android.callblocker.database.DatabaseAdapter;
import test.android.callblocker.domain.Person;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SingleUserActivity extends Activity {
	public static final String TAG = "SingleUserActivity";

	public static final String CREATION_MODE = "test.android.callblocker.activity.SingleUserActivity.CREATION_MODE";
	public static final String _ID = "test.android.callblocker.activity.SingleUserActivity._ID";
	public static final String CONTACT_NAME = "test.android.callblocker.activity.SingleUserActivity.CONTACT_NAME";
	public static final String CONTACT_NUMBER = "test.android.callblocker.activity.SingleUserActivity.CONTACT_NUMBER";
	public static final String LAST_CALL_TIME = "test.android.callblocker.activity.SingleUserActivity.LAST_CALL_TIME";

	private long _id;
	private String contact_name;
	private String last_call_time;
	private ArrayList<String> contact_number = new ArrayList<String>();
	private EditText addContactNumberEditText, contactNameEditText,
			contactNumberEditText;
	private TextView lastCallTimeTextView;
	private View dialogView;
	private Button editContactNumberButton;
	private DatabaseAdapter databaseAdapter;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		this.setContentView( R.layout.activity_single_user );
		this.dialogView = this.getLayoutInflater().inflate(
				R.layout.number_input_dialog, null );
		this.addContactNumberEditText = (EditText) this.dialogView
				.findViewById( R.id.newNumberEditText );
		this.contactNameEditText = (EditText) this
				.findViewById( R.id.contact_name );
		this.contactNumberEditText = (EditText) this
				.findViewById( R.id.contact_number );
		this.editContactNumberButton = (Button) this
				.findViewById( R.id.editContactNumber );
		this.lastCallTimeTextView = (TextView) this
				.findViewById( R.id.lastCallTimeTextView );
		Bundle activityBundle = this.getIntent().getExtras();
		Log.d( TAG, "In single user screen" );
		if ( activityBundle.getString( CREATION_MODE ).equals(
				Enum_UserCreationMode.EXISTING_USER.getName() ) ) {
			this._id = activityBundle.getLong( SingleUserActivity._ID );
			Log.d( TAG, "id = " + _id );
			this.setTitle( getResources().getString(
					R.string.title_activity_existing_single_user ) );
			this.contact_name = activityBundle.getString( CONTACT_NAME );
			this.contactNameEditText.setText( this.contact_name );
			this.last_call_time = activityBundle.getString( LAST_CALL_TIME );
			this.setContactNumber( activityBundle
					.getStringArrayList( CONTACT_NUMBER ) );
			if ( !this.last_call_time.equals( "" ) ) {
				this.lastCallTimeTextView.setText( "Last Call Time : "
						+ this.last_call_time );
				this.lastCallTimeTextView.setVisibility( View.VISIBLE );
			}
		} else if ( activityBundle.getString( CREATION_MODE ).equals(
				Enum_UserCreationMode.NEW_USER.getName() ) ) {
			this._id = activityBundle.getLong( SingleUserActivity._ID );
			Log.d( TAG, "id = " + _id );
			this.contact_name = activityBundle.getString( CONTACT_NAME ) != null ? activityBundle
					.getString( CONTACT_NAME ) : "";
			this.contactNameEditText.setText( this.contact_name );
			this.setContactNumber( activityBundle
					.getStringArrayList( CONTACT_NUMBER ) );
			this.setTitle( getResources().getString(
					R.string.title_activity_single_user ) );
		}
		this.databaseAdapter = new DatabaseAdapter( this );
	}

	public void addNewNumber( View view ) {
		new AlertDialog.Builder( this )
				.setTitle( "Add Contact Number" )
				.setView( SingleUserActivity.this.dialogView )
				.setIcon( R.drawable.ic_launcher )
				.setPositiveButton( "Add",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick( DialogInterface dialog,
									int which ) {
								String number = SingleUserActivity.this.addContactNumberEditText
										.getText().toString();
								if ( !number.equals( "" ) ) { // TODO :
																// validation
																// goes here
									SingleUserActivity.this.addNumber( number );
									( (ViewGroup) SingleUserActivity.this.dialogView
											.getParent() )
											.removeView( SingleUserActivity.this.dialogView );
									// for showing subsequest dialog
								}
							}
						} )
				.setNegativeButton( "Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick( DialogInterface dialog,
									int which ) {
								( (ViewGroup) SingleUserActivity.this.dialogView
										.getParent() )
										.removeView( SingleUserActivity.this.dialogView );
							}
						} ).create().show();

	}

	private void addNumber( String newNumber ) {
		this.contact_number.add( newNumber );
		String content = "";
		final int size = this.contact_number.size();
		for ( int i = 0; i < size; i++ ) {
			content += this.contact_number.get( i );
			if ( i < size - 1 ) {
				content += "; ";
			}
		}
		this.contactNumberEditText.setVisibility( View.VISIBLE );
		this.contactNumberEditText.setText( content );
		this.editContactNumberButton.setVisibility( View.VISIBLE );
	}

	private void setContactNumber( ArrayList<String> newSetOfNumber ) {
		if ( newSetOfNumber == null ) {
			return;
		}
		this.contact_number = newSetOfNumber;
		if ( this.contact_number.isEmpty() ) {
			this.contactNumberEditText.setVisibility( View.GONE );
			this.editContactNumberButton.setVisibility( View.INVISIBLE );
		} else {
			String content = "";
			final int size = this.contact_number.size();
			for ( int i = 0; i < size; i++ ) {
				content += this.contact_number.get( i );
				if ( i < size - 1 ) {
					content += "; ";
				}
			}
			this.contactNumberEditText.setText( content );
			this.contactNumberEditText.setVisibility( View.VISIBLE );
			this.editContactNumberButton.setVisibility( View.VISIBLE );
		}
	}

	public void editContactNumber( View view ) {
		final ArrayList<String> selectedNumbers = new ArrayList<String>(
				this.contact_number );
		String numberArray[] = new String[this.contact_number.size()];
		boolean checked[] = new boolean[this.contact_number.size()];
		Arrays.fill( checked, true );
		new AlertDialog.Builder( this )
				.setTitle( "Edit Number" )
				.setIcon( R.drawable.ic_launcher )
				.setMultiChoiceItems(
						SingleUserActivity.this.contact_number
								.toArray( numberArray ),
						checked,
						new DialogInterface.OnMultiChoiceClickListener() {
							@Override
							public void onClick( DialogInterface dialog,
									int which, boolean isChecked ) {
								if ( isChecked ) {
									selectedNumbers
											.add( SingleUserActivity.this.contact_number
													.get( which ) );
								} else if ( selectedNumbers
										.contains( SingleUserActivity.this.contact_number
												.get( which ) ) ) {
									selectedNumbers
											.remove( SingleUserActivity.this.contact_number
													.get( which ) );
								}
							}
						} )
				.setPositiveButton( "Ok",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick( DialogInterface dialog,
									int which ) {
								SingleUserActivity.this
										.setContactNumber( selectedNumbers );
								dialog.dismiss();
							}
						} )
				.setNegativeButton( "Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick( DialogInterface dialog,
									int which ) {
								dialog.dismiss();
							}
						} ).create().show();
	}

	public void save( View view ) {
		Person block = new Person();
		if ( this._id == -1 ) { // create new entity
			String contactName = this.contactNameEditText.getText().toString();
			block.setContactName( contactName );
			// TODO : Do validation if needed
			String allNumber = this.contactNumberEditText.getText().toString();
			String numberArray[] = allNumber.split( ";" );
			ArrayList<String> new_contact_number = new ArrayList<String>();
			for ( String singleNumber : numberArray ) {
				singleNumber = singleNumber.trim();
				if ( singleNumber.startsWith( "+" ) ) {
					singleNumber = singleNumber.substring( 1 );
				}
				new_contact_number.add( singleNumber );
			}
			this.contact_number = new_contact_number;
			block.setContactNumber( this.contact_number );
			block.setBlockTime( ( new Date() ).getTime() / 1000 );
			this.databaseAdapter.insertPerson( block );
			Toast.makeText( this, "New Block Created.", Toast.LENGTH_LONG )
					.show();
		} else { // update exiting
			block.setId( this._id );
			String contactName = this.contactNameEditText.getText().toString();
			block.setContactName( contactName );
			// TODO : Do validation if needed
			Log.d( TAG, "Number before update : " + block.getNumberText() );
			String allNumber = this.contactNumberEditText.getText().toString();
			String numberArray[] = allNumber.split( ";" );
			ArrayList<String> new_contact_number = new ArrayList<String>();
			for ( String singleNumber : numberArray ) {
				singleNumber = singleNumber.trim();
				if ( singleNumber.startsWith( "+" ) ) {
					singleNumber = singleNumber.substring( 1 );
				}
				new_contact_number.add( singleNumber );
			}
			this.contact_number = new_contact_number;
			block.setContactNumber( this.contact_number );
			Log.d( TAG, "Number after update : " + block.getNumberText() );
			this.databaseAdapter.updatePerson( block );
			Toast.makeText( this, "Block Information Updated.",
					Toast.LENGTH_LONG ).show();
		}
		this.finish();
	}

	public void cancel( View view ) {
		new AlertDialog.Builder( this )
				.setTitle( "Cancel" )
				.setMessage(
						"Your changes are NOT saved. Move to previous screen ?" )
				.setPositiveButton( "Yes, Discard",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick( DialogInterface dialog,
									int which ) {
								dialog.dismiss();
								SingleUserActivity.this.finish();
							}
						} )
				.setNegativeButton( "No",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick( DialogInterface dialog,
									int which ) {
								dialog.dismiss();
							}
						} ).create().show();
	}

	public void delete( View view ) {
		new AlertDialog.Builder( this )
				.setTitle( "Unblock" )
				.setMessage( "Do you want to unblock that person ?" )
				.setPositiveButton( "Yes, Unblock",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick( DialogInterface dialog,
									int which ) {
								Person block = new Person();
								block.setId( SingleUserActivity.this._id );
								block.setContactName( SingleUserActivity.this.contact_name );
								block.setContactNumber( SingleUserActivity.this.contact_number );
								if ( SingleUserActivity.this.databaseAdapter
										.deletePerson( block ) ) {
									Toast.makeText( SingleUserActivity.this,
											"Entry unblocked.",
											Toast.LENGTH_LONG ).show();
								}
								dialog.dismiss();
								SingleUserActivity.this.finish();
							}
						} )
				.setNegativeButton( "No",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick( DialogInterface dialog,
									int which ) {
								dialog.dismiss();
							}
						} ).create().show();
	}
}
