package test.android.callblocker.domain;

import java.util.ArrayList;

import test.android.callblocker.common.DateConverter;

public class Person {
	private long _id;
	private String contactName;
	private ArrayList<String> contactNumber;
	private long blockTime, lastCallTime;

	public Person() {
		this._id = -1;
		this.contactNumber = new ArrayList<String>();
		this.blockTime = 0;
		this.lastCallTime = 0;
	}

	public String getNumberText() {
		String content = "";
		final int size = this.contactNumber.size();
		for ( int i = 0; i < size; i++ ) {
			content += this.contactNumber.get( i );
			if ( i < size - 1 ) {
				content += "; ";
			}
		}
		return content;
	}

	public String getBlockTimeText() {
		return DateConverter.convert( this.blockTime );
	}

	public String getLastCallTimeText() {
		if ( this.lastCallTime == 0 )
			return "";
		return DateConverter.convert( this.lastCallTime );
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName( String contactName ) {
		this.contactName = contactName;
	}

	public ArrayList<String> getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber( ArrayList<String> contactNumber ) {
		this.contactNumber = contactNumber;
	}

	public long getId() {
		return _id;
	}

	public void setId( long _id ) {
		this._id = _id;
	}

	public void addContactNumber( String number ) {
		this.contactNumber.add( number );
	}

	public long getBlockTime() {
		return blockTime;
	}

	public void setBlockTime( long blockTime ) {
		this.blockTime = blockTime;
	}

	public long getLastCallTime() {
		return lastCallTime;
	}

	public void setLastCallTime( long lastCallTime ) {
		this.lastCallTime = lastCallTime;
	}
}
