package com.example.bvarg.comercio;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AgregarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AgregarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AgregarFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ArrayAdapter<String> adapter;
    String[] categorias = {"Canasta básica","Carnes", "Pastas","Salsas","Condimentos","Granos","Lácteos","Frutas y Verduras","Pan","Enlatados","Snacks","Golosinas","Refrescos","Higiene Personal","Productos para hogar","Licores"};
    static String codigodebarra;
    static String idproducto;
    Button agregar;
    EditText nombre;
    EditText marca;
    Spinner categoria;
    EditText descripcion;
    EditText precio;
    View vista;

    private OnFragmentInteractionListener mListener;

    public AgregarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AgregarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AgregarFragment newInstance(String param1, String param2) {
        AgregarFragment fragment = new AgregarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Intent intent = new Intent(getContext(), SimpleScannerActivity.class);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_agregar, container, false);

        nombre = (EditText) vista.findViewById(R.id.editTextNombre);
        marca = (EditText) vista.findViewById(R.id.editTextMarca);
        descripcion = (EditText) vista.findViewById(R.id.editTextDescripcion);
        precio = (EditText) vista.findViewById(R.id.editTextPrecio);
        categoria = (Spinner) vista.findViewById(R.id.spinnerCategoria);

        adapter = new ArrayAdapter<String>(vista.getContext(), android.R.layout.simple_spinner_item,categorias);
        categoria.setAdapter(adapter);


        agregar = (Button) vista.findViewById(R.id.buttonAgregar);
        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregar(nombre.getText().toString(), marca.getText().toString(), descripcion.getText().toString(), categoria.getSelectedItem().toString(), codigodebarra, MainActivity.sharedPreferences.getString("token", ""));
            }
        });




        return vista;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void agregar(final String name, final String brand, final String description,final String category, final String code, final String token){
        StringRequest postRequest = null;
        String url = "https://food-manager.herokuapp.com/products/";
        postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.i("token",response);
                        validarcodigo(code);

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
                params.put("name", name);
                params.put("brand", brand);
                params.put("description", description);
                params.put("category", category);
                params.put("code", code);
                params.put("token", token);

                return params;
            }
        };
        RequestQueue MyRequestQueue = Volley.newRequestQueue(vista.getContext());
        MyRequestQueue.add(postRequest);
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
                                Log.i("Codigo", key);
                                JSONArray jsonArray = response.getJSONArray(key);
                                if(jsonArray.length() == 0){
                                    //no esta en el sistema hay que agregar a las dos partes quedese en la misma interfaz
                                    Log.i("Caso 1","Agregar a las dos partes");
                                }
                                else{
                                    //si esta en el sistema
                                    JSONObject mainObject = new JSONObject(jsonArray.getString(0));
                                    Log.i("idproducto",mainObject.getString("_id"));
                                    Log.i("largo",String.valueOf(jsonArray.length()));
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
                        Log.d("Error", "no se pudo revisar el codigo");
                    }
                }
        );
        // add it to the RequestQueue
        RequestQueue MyRequestQueue = Volley.newRequestQueue(vista.getContext());
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
                                Log.i("Codigo", key);
                                JSONArray jsonArray = response.getJSONArray(key);
                                Log.i("largo", String.valueOf(jsonArray.length()));
                                boolean paso = false;
                                for(int i= 0; i<jsonArray.length(); i++){
                                    JSONObject mainObject = new JSONObject(jsonArray.getString(i));
                                    Log.i("idproducto",mainObject.getString("_id"));
                                    Iterator<String> keys2 = mainObject.keys();
                                    keys2.next();
                                    while (keys2.hasNext()){
                                        String key2 = keys2.next();
                                        JSONObject jsonObject = mainObject.getJSONObject(key2);
                                        Log.i("idproducto", jsonObject.getString("_id"));
                                        if(jsonObject.getString("_id").equals(id)){
                                            paso = true;
                                        }
                                        keys2.next();
                                        keys2.next();
                                    }
                                }
                                if(paso){
                                    //Producto ya se encuentra en las dos partes
                                    Log.i("Caso 2","Ya se encuentra agregado en las dos partes");
                                    Toast.makeText(vista.getContext(), "El producto ya se encuentra registrado", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(vista.getContext(), Home.class);
                                    startActivity(intent);
                                }
                                else{
                                    //Agregar producto al mercado, existe en la base
                                    Log.i("Caso 3","Agregar producto al mercado");

                                    agregarproducto(id, idmarket, precio.getText().toString(), MainActivity.sharedPreferences.getString("token", ""));
                                    Intent intent = new Intent(vista.getContext(), Precio.class);
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
                        Log.d("juuuum", "No sirvio esta vara");
                    }
                }
        );
        // add it to the RequestQueue
        RequestQueue MyRequestQueue = Volley.newRequestQueue(vista.getContext());
        MyRequestQueue.add(getRequest);
    }

    public void agregarproducto(final String id, final String idmarket, final String precio, final String token){
        StringRequest postRequest = null;
        String url = "https://food-manager.herokuapp.com/productsbymarkets";
        postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        //sharedPreferences.edit().putString("token", response.substring(96,response.length()-2)).apply();
                        //sharedPreferences.edit().putString("id", response.substring(61,response.length()-277)).apply();
                        Log.i("token",response.toString());
                        Intent intent = new Intent(vista.getContext(), Home.class);
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
                params.put("productId", id);
                params.put("marketId", idmarket);
                params.put("price", precio);
                params.put("token", token);

                return params;
            }
        };
        RequestQueue MyRequestQueue = Volley.newRequestQueue(vista.getContext());
        MyRequestQueue.add(postRequest);
    }
}
