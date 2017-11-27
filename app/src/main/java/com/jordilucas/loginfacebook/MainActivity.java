package com.jordilucas.loginfacebook;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    LoginButton loginButton;
    CallbackManager callbackManager;
    ProfileTracker profileTracker;
    ImageView imageView;
    TextView textView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.login_button);
        imageView = findViewById(R.id.profile_image);
        textView = findViewById(R.id.name);
        loginButton.setReadPermissions("public_profile");
        callbackManager = CallbackManager.Factory.create();
        progressBar = findViewById(R.id.progress_bar);

        obterDadosFacebook();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume(){
        super.onResume();



    }

    private void obterDadosFacebook(){

        List permissionsNeeds = Arrays.asList("email", "public_profile");
        LoginManager.getInstance().logInWithReadPermissions(this, permissionsNeeds);

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("TAG", "JSON: " + object);
                        try {
                            if(AccessToken.getCurrentAccessToken() == null){
                                textView.setText("");
                                imageView.invalidate();
                            }

                            textView.setText(object.getString("name"));
                            recuperaFotoPerfilFacebook(object.getString("id"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,picture.width(120).height(120)");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                Log.e("TAG", "facebook login canceled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("TAG", "facebook login failed error");
            }
        });


    }

    private void recuperaFotoPerfilFacebook(String userID) throws MalformedURLException {
        Uri.Builder builder = Uri.parse("https://graph.facebook.com").buildUpon();
        builder.appendPath(userID).appendPath("picture").appendQueryParameter("type", "large");
        Picasso.with(MainActivity.this).load(builder.toString()).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {

            }
        });




    }

}
