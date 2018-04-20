package com.example.bvarg.comercio;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button registro;
    Button login;
    EditText email;
    EditText password;
    static  SharedPreferences sharedPreferences;

    @Override
    public boolean isLocalVoiceInteractionSupported() {
        return super.isLocalVoiceInteractionSupported();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("com.example.bvarg.comercio", Context.MODE_PRIVATE);
        email = (EditText) findViewById(R.id.editTextCorreo);
        password = (EditText) findViewById(R.id.editTextContrasena);
        String token = sharedPreferences.getString("token", "");
        validartoken(token);
        registro = (Button) findViewById(R.id.buttonRegistrarse);
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), registro.class);
                startActivity(intent);
            }
        });

        login = (Button) findViewById(R.id.buttonLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarusuario(email.getText().toString(), password.getText().toString());
            }
        });
    }
    public void validartoken(final String token) {
        StringRequest postRequest = null;
        if (token == "") {

        }
        else {
            String url = "https://food-manager.herokuapp.com/users/check";
            postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.d("Response", response);
                            Intent intent = new Intent(getApplicationContext(), listacomercios.class);
                            startActivity(intent);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("Error.Response", "fallo");
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", token);
                    return params;
                }
            };
            RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
            MyRequestQueue.add(postRequest);
        }
    }
    public void validarusuario(final String email, final String password){
        StringRequest postRequest = null;
        String url = "https://food-manager.herokuapp.com/users/login";
        postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        sharedPreferences.edit().putString("token", response.substring(96,response.length()-2)).apply();
                        sharedPreferences.edit().putString("id", response.substring(61,response.length()-277)).apply();
                        Log.i("token",response.substring(96,response.length()-2));
                        Log.i("id",response.substring(61,response.length()-277));
                        Intent intent = new Intent(getApplicationContext(), listacomercios.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", "fallo");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }
        };
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        MyRequestQueue.add(postRequest);
    }
}
