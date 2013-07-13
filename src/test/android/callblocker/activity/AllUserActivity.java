package test.android.callblocker.activity;

import java.util.ArrayList;

import test.android.callblocker.R;
import test.android.callblocker.background.ListAdapter;
import test.android.callblocker.background.ListPopulateTask;
import test.android.callblocker.common.Page;
import test.android.callblocker.common.enumeration.Enum_ListPopulationMode;
import test.android.callblocker.common.enumeration.Enum_UserCreationMode;
import test.android.callblocker.database.DatabaseAdapter;
import test.android.callblocker.domain.Person;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AllUserActivity extends Activity {
	public static final String POPULATION_MODE = "test.android.callblocker.activity.AllUserActivity.POPULATION_MODE";
	private Page page;
	private ListView listView;
	private static Enum_ListPopulationMode LIST_POPULATION_MODE;
	private static final Enum_UserCreationMode USER_CREATION_MODE = Enum_UserCreationMode.EXISTING_USER;
	private ArrayList<Person> listContent;
	private ArrayAdapter<Person> listAdapter;
	private DatabaseAdapter databaseAdapter;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		this.setContentView( R.layout.activity_view_all );
		Bundle activityIntent = this.getIntent().getExtras();
		final String INCOMING_POPULATION_MODE = activityIntent
				.getString( POPULATION_MODE );
		if ( INCOMING_POPULATION_MODE
				.equals( Enum_ListPopulationMode.FROM_VIEW_ALL.toString() ) ) {
			AllUserActivity.LIST_POPULATION_MODE = Enum_ListPopulationMode.FROM_VIEW_ALL;
			this.setTitle( getResources().getString( R.string.button_viewall ) );
		} else if ( INCOMING_POPULATION_MODE
				.equals( Enum_ListPopulationMode.FROM_RECENT_CALL.toString() ) ) {
			AllUserActivity.LIST_POPULATION_MODE = Enum_ListPopulationMode.FROM_RECENT_CALL;
			this.setTitle( getResources().getString(
					R.string.button_recentcalls ) );
		} else if ( INCOMING_POPULATION_MODE
				.equals( Enum_ListPopulationMode.FROM_UNBLOCK.toString() ) ) {
			AllUserActivity.LIST_POPULATION_MODE = Enum_ListPopulationMode.FROM_UNBLOCK;
			this.setTitle( getResources().getString( R.string.button_unblock ) );
		}
		this.databaseAdapter = new DatabaseAdapter( this );
		this.listView = (ListView) this.findViewById( R.id.personListView );
		this.listView
				.setOnItemClickListener( new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick( AdapterView<?> parent, View view,
							int position, long _id ) {
						Intent userIntent = new Intent( AllUserActivity.this,
								SingleUserActivity.class );
						userIntent.putExtra( SingleUserActivity.CREATION_MODE,
								AllUserActivity.USER_CREATION_MODE.getName() );
						Person block = AllUserActivity.this.databaseAdapter
								.getPersonById( AllUserActivity.this.listContent
										.get( position ).getId() );
						userIntent.putExtra( SingleUserActivity._ID,
								block.getId() );
						userIntent.putExtra( SingleUserActivity.CONTACT_NAME,
								block.getContactName() );
						userIntent.putStringArrayListExtra(
								SingleUserActivity.CONTACT_NUMBER,
								block.getContactNumber() );
						userIntent.putExtra( SingleUserActivity.LAST_CALL_TIME,
								block.getLastCallTimeText() );
						AllUserActivity.this.startActivity( userIntent );
					}
				} );
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.listContent = new ArrayList<Person>();
		this.listAdapter = new ListAdapter( this, R.layout.list_unit,
				this.listContent, AllUserActivity.LIST_POPULATION_MODE );
		this.listView.setAdapter( this.listAdapter );
		this.page = new Page( Integer.parseInt( this.getResources().getString(
				R.string.page_size ) ) );
		this.loadMore();
	}

	private void loadMore() {
		new ListPopulateTask( this, AllUserActivity.LIST_POPULATION_MODE )
				.execute( this.page );
	}

	public void updateListContent( ArrayList<Person> addedContent ) {
		if ( addedContent != null ) {
			this.listContent.addAll( addedContent );
			this.listAdapter.notifyDataSetChanged();
			this.page.forward();
		}
	}

	public void loadMore( View view ) {
		this.loadMore();
	}
}
