package com.example.notatnik;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.widget.TextView;


@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    public static Boolean isFingerprint = false;
    private Context context;

    public FingerprintHandler(Context context){
        this.context = context;

    }

    public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject){
        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        this.update("Auth Error. " + errString);
    }

    @Override
    public void onAuthenticationFailed() {
        this.update("Auth Failed. ");
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        this.update("Error: " + helpString);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        this.update("");
        isFingerprint = true;
        context.startActivity(new Intent(context.getApplicationContext(), NoteActivity.class));
    }

    private void update(String s) {
        TextView paraLabel = (TextView)((Activity)context).findViewById(R.id.textView);
        paraLabel.setText(s);

    }
}
