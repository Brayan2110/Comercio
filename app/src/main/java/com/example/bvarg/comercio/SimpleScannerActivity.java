package com.example.bvarg.comercio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * Created by Bvarg on 04/04/2018.
 */

public class SimpleScannerActivity extends Activity implements ZBarScannerView.ResultHandler {
    private ZBarScannerView mScannerView;
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZBarScannerView(this);    // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        AgregarFragment.codigodebarra = rawResult.getContents();
        Log.v("primero", rawResult.getContents()); // Prints scan results
        Log.v("segundo", rawResult.getBarcodeFormat().getName()); // Prints the scan format (qrcode, pdf417 etc.)

        // If you would like to resume scanning, call this method below:
        //mScannerView.resumeCameraPreview(this);


        //MainActivity.texto.setText(rawResult.getContents());
        //MainActivity.texto2.setText(rawResult.getBarcodeFormat().getName());
        validarcodigo(rawResult.getContents());
        onBackPressed();
    }

    public void validarcodigo(String id){
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, "https://food-manager.herokuapp.com/products/code/"+id, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        try{
                            Iterator<String> keys = response.keys();
                            keys.next();
                            while (keys.hasNext())
                            {
                                // obtiene el nombre del objeto.
                                String key = keys.next();
                                JSONArray jsonArray = response.getJSONArray(key);
                                if(jsonArray.length() == 0){
                                    //no esta en el sistema hay que agregar a las dos partes quedese en la misma interfaz
                                    Log.i("Caso 1","No hay nada agregado");
                                }
                                else{
                                    //si esta en el sistema
                                    JSONObject mainObject = new JSONObject(jsonArray.getString(0));
                                    Log.i("idproducto",mainObject.getString("_id"));
                                    Log.i("largo",String.valueOf(jsonArray.length()));
                                    Log.i("idcomercio", MainActivity.sharedPreferences.getString("idcomercio", ""));
                                    //validarcodigo2(mainObject.getString("_id"),"5ad41eba8212a036c044e18b");
                                    validarcodigo2(mainObject.getString("_id"), MainActivity.sharedPreferences.getString("idcomercio", ""));
                                }
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
                        Log.d("Error", "Error al comparar codigo");
                    }
                }
        );
        // add it to the RequestQueue
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        MyRequestQueue.add(getRequest);
    }
    public void validarcodigo2(final String id, final String idmarket){
        // prepare the Request
        final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, "https://food-manager.herokuapp.com/productsbymarkets/market/"+idmarket, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        try{
                            Iterator<String> keys = response.keys();
                            while (keys.hasNext())
                            {
                                // obtiene el nombre del objeto.
                                String key = keys.next();
                                Log.i("identificador", key);
                                JSONArray jsonArray = response.getJSONArray(key);
                                Log.i("largo2", String.valueOf(jsonArray.length()));
                                boolean paso = false;
                                for(int i= 0; i<jsonArray.length(); i++){
                                    JSONObject mainObject = new JSONObject(jsonArray.getString(i));
                                    Log.i("codigo2",mainObject.getString("_id"));
                                    Iterator<String> keys2 = mainObject.keys();
                                    keys2.next();
                                    while (keys2.hasNext()){
                                        String key2 = keys2.next();
                                        JSONObject jsonObject = mainObject.getJSONObject(key2);
                                        Log.i("codigo22", jsonObject.getString("_id"));
                                        if(jsonObject.getString("_id").equals(id)){
                                            paso = true;
                                        }
                                        keys2.next();
                                        keys2.next();
                                    }
                                }
                                if(paso){
                                    //Producto ya se encuentra en las dos partes
                                    Log.i("Caso 2","Agregado em las dos partes");
                                    Toast.makeText(getApplicationContext(), "El producto ya se encuentra registrado", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), Home.class);
                                    startActivity(intent);
                                }
                                else{
                                    //Agregar producto al mercado, existe en la base
                                    Log.i("Caso 3","Agregar a mercado");
                                    Precio.idlugar = idmarket;
                                    Precio.idproducto = id;
                                    Intent intent = new Intent(getApplicationContext(), Precio.class);
                                    startActivity(intent);
                                }
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
                        Log.d("Error", "Error con el Id de producto");
                    }
                }
        );
        // add it to the RequestQueue
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        MyRequestQueue.add(getRequest);
    }
}