package test.android.callblocker.background;

import java.util.ArrayList;

import test.android.callblocker.activity.AllUserActivity;
import test.android.callblocker.common.Page;
import test.android.callblocker.common.enumeration.Enum_ListPopulationMode;
import test.android.callblocker.database.DatabaseAdapter;
import test.android.callblocker.domain.Person;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class ListPopulateTask extends
		AsyncTask<Page, Integer, ArrayList<Person>> {
	private ProgressDialog progressDialog;
	private Context context;
	private DatabaseAdapter databaseAdapter;
	private Enum_ListPopulationMode calle;

	public ListPopulateTask( Context context, Enum_ListPopulationMode calle ) {
		this.context = context;
		this.progressDialog = new ProgressDialog( this.context );
		this.progressDialog.setCancelable( true );
		this.progressDialog.setMessage( "Loading ..." );
		this.progressDialog.setIndeterminate( true );
		this.databaseAdapter = new DatabaseAdapter( this.context );
		this.calle = calle;
	}

	@Override
	protected void onPreExecute() {
		this.progressDialog.show();
	}

	@Override
	protected ArrayList<Person> doInBackground( Page... page ) {
		ArrayList<Person> newPersonList = null;
		if ( this.calle.equals( Enum_ListPopulationMode.FROM_VIEW_ALL ) ) {
			newPersonList = this.databaseAdapter.getPersonByName( page[0] );
		} else if ( this.calle
				.equals( Enum_ListPopulationMode.FROM_RECENT_CALL ) ) {
			newPersonList = this.databaseAdapter
					.getPersonByLastCallTime( page[0] );
		} else if ( this.calle.equals( Enum_ListPopulationMode.FROM_UNBLOCK ) ) {
			newPersonList = this.databaseAdapter.getPersonByBlockTime( page[0] );
		}
		return newPersonList;
	}

	@Override
	protected void onPostExecute( ArrayList<Person> result ) {
		super.onPostExecute( result );
		( (AllUserActivity) context ).updateListContent( result );
		if ( this.progressDialog != null && this.progressDialog.isShowing() ) {
			this.progressDialog.dismiss();
		}
	}
}
