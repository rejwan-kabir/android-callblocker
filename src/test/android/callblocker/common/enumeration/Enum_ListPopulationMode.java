package test.android.callblocker.common.enumeration;

public enum Enum_ListPopulationMode {
	FROM_VIEW_ALL("FROM_VIEW_ALL"), FROM_UNBLOCK("FROM_UNBLOCK"), FROM_RECENT_CALL(
			"FROM_RECENT_CALL");

	private String name;

	private Enum_ListPopulationMode( String name ) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}
}
