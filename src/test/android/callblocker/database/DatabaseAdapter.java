package test.android.callblocker.database;

import java.util.ArrayList;
import java.util.Date;

import test.android.callblocker.common.Page;
import test.android.callblocker.common.enumeration.Enum_NumberTableIndex;
import test.android.callblocker.common.enumeration.Enum_PersonTableIndex;
import test.android.callblocker.domain.Person;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAdapter {
	private Context context;
	private SQLiteDatabase db;
	private DatabaseOpenHelper databaseOpenHelper;
	private static final String TAG = "DatabaseAdapter";

	public DatabaseAdapter( Context context ) {
		this.context = context;
		this.databaseOpenHelper = new DatabaseOpenHelper( this.context );
	}

	private DatabaseAdapter open() {
		this.db = this.databaseOpenHelper.getWritableDatabase();
		return this;
	}

	private void close() {
		this.databaseOpenHelper.close();
	}

	public void insertPerson( Person person ) {
		try {
			this.open();
			ContentValues values = new ContentValues();
			values.put( PERSON_FIELD_CONTACT_NAME, person.getContactName() );
			values.put( PERSON_FIELD_BLOCK_TIME, person.getBlockTime() );
			person.setId( this.db.insert( TABLE_PERSON, null, values ) );
			this.insertNumber( person );
		} catch ( Exception exception ) {

		} finally {
			this.close();
		}
	}

	public Person getPersonById( long _id ) {
		Person block = null;
		try {
			this.open();
			Cursor cursor = this.db.query( TABLE_PERSON, QUERY_PERSON,
					"_id = ?", new String[] { "" + _id }, null, null, null );
			cursor.moveToNext();
			block = this.convertCursorToPerson( cursor );
		} catch ( Exception exception ) {

		} finally {
			this.close();
		}
		return block;
	}

	public boolean updatePerson( Person person ) {
		try {
			this.open();
			ContentValues values = new ContentValues();
			Log.d( TAG, "Person before update : " );
			Log.d( TAG, "Id = " + person.getId() );
			Log.d( TAG, "Name = " + person.getContactName() );
			Log.d( TAG, "Number = " + person.getNumberText() );
			values.put( PERSON_FIELD_CONTACT_NAME, person.getContactName() );
			if ( this.db.update( TABLE_PERSON, values, "_id = ?",
					new String[] { "" + person.getId() } ) > 0 ) {
				this.updateNumber( person );
				this.close();
				return true;
			}
		} catch ( Exception exception ) {

		} finally {
			this.close();
		}
		return false;
	}

	public boolean deletePerson( Person person ) {
		boolean success = false;
		try {
			this.open();
			success = this.db.delete( TABLE_PERSON, "_id = ?",
					new String[] { "" + person.getId() } ) > 0;
		} catch ( Exception exception ) {

		} finally {
			this.close();
		}
		return success;
	}

	public String getPersonForBlock( String number ) { 
		// TODO : check if number starts with country code
		// remove country code
		String name = null;
		try {
			this.open();
			Cursor cursor = this.db.rawQuery( DatabaseAdapter.QUERY_FOR_NUMBER,
					new String[] { number } );
			if ( cursor != null ) {
				if ( cursor.moveToFirst() ) {
					String _id = cursor.getString( 0 );
					name = cursor.getString( 1 ); // hard coded
					this.db.execSQL( ENTRY_LAST_CALL_TIME, new String[] {
							( new Date().getTime() / 1000 ) + "", _id } );
				}
			}
		} catch ( Exception exception ) {

		} finally {
			this.close();
		}
		return name;
	}

	public ArrayList<Person> getPersonByName( Page page ) {
		return this.getPerson( PERSON_FIELD_CONTACT_NAME, "asc", page );
	}

	public ArrayList<Person> getPersonByBlockTime( Page page ) {
		return this.getPerson( PERSON_FIELD_BLOCK_TIME, "desc", page );
	}

	public ArrayList<Person> getPersonByLastCallTime( Page page ) {
		return this.getPerson( PERSON_FIELD_LAST_CALL_TIME, "desc", page );
	}

	private ArrayList<Person> getPerson( String criteria, String sort, Page page ) {
		ArrayList<Person> newPersonList = null;
		try {
			this.open();
			Cursor cursor = this.db
					.query( TABLE_PERSON,
							QUERY_PERSON,
							criteria.equals( PERSON_FIELD_LAST_CALL_TIME ) ? PERSON_FIELD_LAST_CALL_TIME
									+ " is not null"
									: null, null, null, null, criteria + " "
									+ sort,
							page.getOffset() + "," + page.getSize() );
			if ( cursor == null || cursor.getCount() == 0 ) {
				this.close();
				return null;
			}
			newPersonList = new ArrayList<Person>();
			while ( cursor.moveToNext() ) {
				newPersonList.add( this.convertCursorToPerson( cursor ) );
			}

		} catch ( Exception exception ) {

		} finally {
			this.close();
		}
		return newPersonList;
	}

	private Person convertCursorToPerson( Cursor cursor ) {
		Person person = new Person();
		person.setId( cursor.getLong( Enum_PersonTableIndex._ID.ordinal() ) );
		person.setContactName( cursor
				.getString( Enum_PersonTableIndex.PERSON_FIELD_CONTACT_NAME
						.ordinal() ) );
		person.setBlockTime( cursor
				.getLong( Enum_PersonTableIndex.PERSON_FIELD_BLOCK_TIME
						.ordinal() ) );
		person.setLastCallTime( cursor
				.getLong( Enum_PersonTableIndex.PERSON_FIELD_LAST_CALL_TIME
						.ordinal() ) );
		person.setContactNumber( this.getNumber( person.getId() ) );
		return person;
	}

	private void insertNumber( Person person ) {
		ContentValues values = new ContentValues();
		for ( String number : person.getContactNumber() ) {
			values.put( PERSON_NUMBER_FIELD_PERSON_ID, person.getId() );
			values.put( PERSON_NUMBER_FIELD_PERSON_NUMBER, number );
			this.db.insert( TABLE_PERSON_NUMBER, null, values );
		}
	}

	private ArrayList<String> getNumber( long person_id ) {
		Cursor cursor = this.db.query( TABLE_PERSON_NUMBER, QUERY_NUMBER,
				"person_id = ?", new String[] { "" + person_id }, null, null,
				null, null );
		if ( cursor == null || cursor.getCount() == 0 ) {
			return null;
		}
		ArrayList<String> numberList = new ArrayList<String>();
		while ( cursor.moveToNext() ) {
			String number = cursor
					.getString( Enum_NumberTableIndex.PERSON_NUMBER_FIELD_PERSON_NUMBER
							.ordinal() );
			numberList.add( number );
		}
		return numberList;
	}

	private void updateNumber( Person person ) {
		this.deleteNumber( person );
		this.insertNumber( person );
		Log.d( TAG, "Number after update = " + person.getNumberText() );
	}

	private void deleteNumber( Person person ) {
		this.db.delete( TABLE_PERSON_NUMBER, "person_id = ?", new String[] { ""
				+ person.getId() } );
	}

	public static final String DATABASE_NAME = "blockdb";
	public static final int DATABASE_VERSION = 1;

	public static final String TABLE_PERSON = "person";
	public static final String TABLE_PERSON_NUMBER = "person_number";

	public static final String _ID = "_id";
	public static final String PERSON_FIELD_CONTACT_NAME = "contact_name";
	public static final String PERSON_FIELD_LAST_CALL_TIME = "last_call_time";
	public static final String PERSON_FIELD_BLOCK_TIME = "block_time";

	public static final String PERSON_NUMBER_FIELD_PERSON_ID = "person_id";
	public static final String PERSON_NUMBER_FIELD_PERSON_NUMBER = "person_number";

	private static final String CREATE_TABLE_PERSON = "CREATE TABLE if not exists person ( "
			+ "_id integer primary key autoincrement, "
			+ "contact_name text, "
			+ "last_call_time integer, block_time integer);";

	private static final String CREATE_TABLE_PERSON_NUMBER = "CREATE TABLE if not exists person_number ( "
			+ "_id integer primary key autoincrement, "
			+ "person_id integer, person_number text, "
			+ "FOREIGN KEY(person_id) references person(_id) "
			+ "on update cascade on delete cascade );";

	private static final String[] QUERY_PERSON = new String[] { _ID,
			PERSON_FIELD_CONTACT_NAME, PERSON_FIELD_LAST_CALL_TIME,
			PERSON_FIELD_BLOCK_TIME };

	private static final String[] QUERY_NUMBER = new String[] { _ID,
			PERSON_NUMBER_FIELD_PERSON_ID, PERSON_NUMBER_FIELD_PERSON_NUMBER };

	private static final String QUERY_FOR_NUMBER = "select " + TABLE_PERSON
			+ "." + _ID + ", " + PERSON_FIELD_CONTACT_NAME + " from "
			+ TABLE_PERSON + ", " + TABLE_PERSON_NUMBER + " where "
			+ PERSON_NUMBER_FIELD_PERSON_NUMBER + " = ? and " + TABLE_PERSON
			+ "." + _ID + " = " + TABLE_PERSON_NUMBER + "."
			+ PERSON_NUMBER_FIELD_PERSON_ID;

	private final String ENTRY_LAST_CALL_TIME = "update " + TABLE_PERSON
			+ " set " + PERSON_FIELD_LAST_CALL_TIME + " = ? where " + _ID
			+ " = ?";

	class DatabaseOpenHelper extends SQLiteOpenHelper {
		public DatabaseOpenHelper( Context context ) {
			super( context, DATABASE_NAME, null, DATABASE_VERSION );
		}

		@Override
		public void onCreate( SQLiteDatabase db ) {
			try {
				db.execSQL( CREATE_TABLE_PERSON );
				db.execSQL( CREATE_TABLE_PERSON_NUMBER );
			} catch ( SQLException exception ) {

			}
		}

		@Override
		public void onOpen( SQLiteDatabase db ) {
			super.onOpen( db );
			if ( !db.isReadOnly() ) {
				db.execSQL( "PRAGMA foreign_keys = ON;" );
				// for enabling foreign key support
				// that feature will get support from Android 2.2
			}
		}

		@Override
		public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
			Log.w( TAG, "Database is upgraded from version : " + oldVersion
					+ " to version : " + newVersion
					+ ". So all the existing tables will be erased." );
			db.execSQL( "drop table if exists person_number" );
			db.execSQL( "drop table if exists person" );
			this.onCreate( db );
		}
	}
}
