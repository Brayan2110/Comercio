package com.example.bvarg.comercio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class AgregarComercio extends AppCompatActivity {

    EditText nombre;
    EditText telefono;
    Button agregar;
    Spinner localizacion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_comercio);

        String[] datos = new String[] {"San Jose", "Cartago", "Heredia", "Alajuela", "Guanacaste", "Puntarenas", "Limon"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, datos);

        localizacion = (Spinner) findViewById(R.id.spinnerLocalizacion);
        localizacion.setAdapter(adapter);

        nombre = (EditText) findViewById(R.id.editTextNombre);
        telefono = (EditText) findViewById(R.id.editTextTelefono);

        agregar = (Button) findViewById(R.id.buttonAgregar);
        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!nombre.getText().toString().equals("") && !telefono.getText().toString().equals("")){
                    nuevocomercio(MainActivity.sharedPreferences.getString("id", ""));
                }
                else{
                    Toast.makeText(getApplicationContext(), "Se debe llenar toda la informacion", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    public void nuevocomercio(final String id){
        StringRequest postRequest = null;
        String url = "https://food-manager.herokuapp.com/markets";
        postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Toast.makeText(getApplicationContext(), "Agregada con exito", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), listacomercios.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", "fallo");
                        Toast.makeText(getApplicationContext(), "Fallo al agregar", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", id);
                params.put("name", nombre.getText().toString());
                params.put("location", localizacion.getSelectedItem().toString());
                params.put("phone", telefono.getText().toString());

                return params;
            }
        };
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        MyRequestQueue.add(postRequest);
    }
}
