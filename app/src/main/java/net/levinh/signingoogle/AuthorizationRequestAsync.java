package net.levinh.signingoogle;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by virus on 06/11/2016.
 */

public class AuthorizationRequestAsync extends AsyncTask<String, Void, GoogleCredential> {

    private static final String TAG = AuthorizationRequestAsync.class.getSimpleName();
    Context context;
    AsyncCallBack callBack;

    public AuthorizationRequestAsync(Context context, AsyncCallBack callBack) {
        this.context = context;
        this.callBack = callBack;
    }

    @Override
    protected GoogleCredential doInBackground(String... params) {
        try {
            GoogleClientSecrets clientSecrets =
                    GoogleClientSecrets.load(
                            JacksonFactory.getDefaultInstance(),
                            new InputStreamReader(context.getAssets().open("client_secret.json")));
            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(),
                    JacksonFactory.getDefaultInstance(),
                    "https://www.googleapis.com/oauth2/v4/token",
                    clientSecrets.getDetails().getClientId(),
                    clientSecrets.getDetails().getClientSecret(),
                    params[0], "")
                    .execute();
            String accessToken = tokenResponse.getAccessToken();
            String refreshToken = tokenResponse.getRefreshToken();
            Long expiresInSeconds = tokenResponse.getExpiresInSeconds();

            GoogleCredential credential = new GoogleCredential.Builder()
                    .setTransport(new NetHttpTransport())
                    .setJsonFactory(JacksonFactory.getDefaultInstance())
                    .setClientSecrets(clientSecrets)
                    .build();

            credential.setAccessToken(accessToken);
            credential.setExpiresInSeconds(expiresInSeconds);
            credential.setRefreshToken(refreshToken);
            return credential;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "authorizationCodeRequest: " + e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(GoogleCredential googleCredential) {
        super.onPostExecute(googleCredential);
        callBack.onFinish(googleCredential);
    }

    interface AsyncCallBack {
        void onFinish(GoogleCredential cre);
    }
}
