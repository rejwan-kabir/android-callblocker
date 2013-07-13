package test.android.callblocker.common;

public class Page {
	private int size;
	private int offset;

	public Page( int size ) {
		this.size = size;
		this.offset = 0;
	}

	public void forward() {
		this.offset += this.size;
	}

	public int getSize() {
		return this.size;
	}

	public int getOffset() {
		return this.offset;
	}
}
