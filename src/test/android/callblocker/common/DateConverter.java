package test.android.callblocker.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"hh:mm aa, EEE, dd MMM yyyy" );

	public static String convert( long unixTimeStamp ) {
		Date date = new Date( unixTimeStamp * 1000 );
		return DATE_FORMAT.format( date );
	}
}
