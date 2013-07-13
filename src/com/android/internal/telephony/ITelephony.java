package com.android.internal.telephony;

// Look at : http://www.androidjavadoc.com/0.9_beta/com/android/internal/telephony/ITelephony.html
public interface ITelephony {
	void answerRingingCall();
	void silenceRinger();
	boolean endCall();
}
