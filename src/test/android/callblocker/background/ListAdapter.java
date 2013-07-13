package test.android.callblocker.background;

import java.util.ArrayList;

import test.android.callblocker.R;
import test.android.callblocker.common.enumeration.Enum_ListPopulationMode;
import test.android.callblocker.domain.Person;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListAdapter extends ArrayAdapter<Person> {
	private ArrayList<Person> personList;
	private Context context;
	private Enum_ListPopulationMode calle;

	public ListAdapter( Context context, int viewResourceId,
			ArrayList<Person> personList, Enum_ListPopulationMode calle ) {
		super( context, viewResourceId, personList );
		this.context = context;
		this.personList = personList;
		this.calle = calle;
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {
		Person person = this.personList.get( position );
		PersonHolder personHolder;
		if ( convertView == null ) {
			convertView = ( (LayoutInflater) this.context
					.getSystemService( Context.LAYOUT_INFLATER_SERVICE ) )
					.inflate( R.layout.list_unit, parent, false );
			personHolder = new PersonHolder();
			personHolder.name = (TextView) convertView
					.findViewById( R.id.nameOfUnit );
			personHolder.number = (TextView) convertView
					.findViewById( R.id.numberOfUnit );
			personHolder.time = (TextView) convertView
					.findViewById( R.id.timeOfUnit );
			convertView.setTag( personHolder );
		} else {
			personHolder = (PersonHolder) convertView.getTag();
		}
		personHolder.name.setText( person.getContactName() );
		personHolder.number.setText( person.getNumberText() );
		if ( this.calle.equals( Enum_ListPopulationMode.FROM_VIEW_ALL )
				|| this.calle.equals( Enum_ListPopulationMode.FROM_UNBLOCK ) ) {
			personHolder.time.setText( "Blocked since "
					+ person.getBlockTimeText() );
		} else if ( this.calle
				.equals( Enum_ListPopulationMode.FROM_RECENT_CALL ) ) {
			personHolder.time.setText( "Last call time : "
					+ person.getLastCallTimeText() );
		}
		return convertView;
	}

	private static class PersonHolder {
		TextView name, number, time;
	}
}
