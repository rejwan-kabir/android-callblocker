package test.android.callblocker.common.enumeration;

public enum Enum_UserCreationMode {
	NEW_USER("NEW_USER"),
	EXISTING_USER("EXISTING_USER");
	
	private String name;
	private Enum_UserCreationMode(String name) {
		this.name=name;
	}
	
	public String getName() {
		return name;
	}
	public void setName( String name ) {
		this.name = name;
	}
}
