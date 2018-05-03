package com.example.bvarg.comercio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class listacomercios extends AppCompatActivity implements ListView.OnItemClickListener{

    ListView lista;
    Button cerrarsesion;
    Button agregarcomercio;
    ArrayAdapter<String> adapter;
    ArrayList<String> listaid = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listacomercios);
        cerrarsesion = (Button) findViewById(R.id.buttonSalir);
        cerrarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                MainActivity.sharedPreferences.edit().putString("token", "").apply();
                MainActivity.sharedPreferences.edit().putString("id", "").apply();
            }
        });

        agregarcomercio = (Button) findViewById(R.id.buttonAgregarComercio);
        agregarcomercio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AgregarComercio.class);
                startActivity(intent);
            }
        });

        String id = MainActivity.sharedPreferences.getString("id", "");
        llenar(id);

        lista = (ListView) findViewById(R.id.listacomercios);
        lista.setOnItemClickListener(this);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        lista.setAdapter(adapter);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MainActivity.sharedPreferences.edit().putString("idcomercio", listaid.get(position)).apply();
        Intent intent = new Intent(getApplicationContext(), Home.class);
        startActivity(intent);
    }

    public void llenar(String id){
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, "https://food-manager.herokuapp.com/markets/"+id, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        try{
                            Iterator<String> keys = response.keys();
                            keys.next();
                            while (keys.hasNext())
                            {
                                // obtiene el nombre del objeto.
                                String key = keys.next();
                                Log.i("Parser", "objeto : " + key);
                                JSONArray jsonArray = response.getJSONArray(key);
                                for(int i= 0; i<response.getJSONArray(key).length(); i++){
                                    JSONObject mainObject = new JSONObject(jsonArray.getString(i));

                                    //obtiene valores dentro del objeto.
                                    String valorName = mainObject.getString("name");
                                    String valorId = mainObject.getString("_id");
                                    adapter.add(valorName);
                                    listaid.add(valorId);
                                }

                                //Imprimimos los valores.
                                //Log.i("Nombre", valorName);
                                //Log.i("ID", valorId);
                            }
                        }
                        catch (Exception e){

                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", "No hay locales");
                    }
                }
        );
        // add it to the RequestQueue
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        MyRequestQueue.add(getRequest);
    }
}
