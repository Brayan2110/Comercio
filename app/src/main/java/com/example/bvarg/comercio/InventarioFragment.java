package com.example.bvarg.comercio;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InventarioFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InventarioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InventarioFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View vista;
    TableLayout tableLayout;

    private OnFragmentInteractionListener mListener;

    public InventarioFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InventarioFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InventarioFragment newInstance(String param1, String param2) {
        InventarioFragment fragment = new InventarioFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_inventario, container, false);
        tableLayout = (TableLayout) vista.findViewById(R.id.tabla);

        llenar(MainActivity.sharedPreferences.getString("idcomercio",""));
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

    public void llenartabla(){

    }

    public void llenar(String id){
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, "https://food-manager.herokuapp.com/productsbymarkets/market/"+id, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        try{
                            Iterator<String> keys = response.keys();
                            while (keys.hasNext())
                            {
                                // obtiene el nombre del objeto.
                                String key = keys.next();
                                Log.i("Parser", "objeto : " + key);
                                JSONArray jsonArray = response.getJSONArray(key);
                                Log.i("largo",String.valueOf(jsonArray.length()));
                                for(int i= 0; i<response.getJSONArray(key).length(); i++){
                                    JSONObject mainObject = new JSONObject(jsonArray.getString(i));
                                    JSONObject mainObject2 = new JSONObject(mainObject.getString("product"));
                                    //obtiene valores dentro del objeto.
                                    String nombre = mainObject2.getString("name");
                                    String marca = mainObject2.getString("brand");
                                    String descripcion = mainObject2.getString("description");
                                    Log.i("nombre",nombre);
                                    Log.i("marca",marca);
                                    Log.i("descripcion",descripcion);

                                    TableRow fila = new TableRow(vista.getContext());
                                    fila.setBackgroundColor(Color.parseColor("#80CBC4"));
                                    TextView tv1 = new TextView(vista.getContext());
                                    TextView tv2 = new TextView(vista.getContext());
                                    TextView tv3 = new TextView(vista.getContext());
                                    tv1.setText(nombre);
                                    tv2.setText(marca);
                                    tv3.setText(descripcion);
                                    fila.addView(tv1);
                                    fila.addView(tv2);
                                    fila.addView(tv3);
                                    tableLayout.addView(fila);






                                    //String cadena = nombre + " " + marca + " " + descripcion;
                                    //Log.i("cadena",cadena);
                                    //TableRow row = new TableRow(vista.getContext());
                                    //TextView textView;
                                    //textView = new TextView(vista.getContext());
                                    //textView.setGravity(Gravity.CENTER_VERTICAL);
                                    //textView.setPadding(15,15,15,15);
                                    //textView.setBackgroundResource(R.color.colorPrimary);
                                    //textView.setText(cadena);
                                    //textView.setTextColor(Color.WHITE);
                                    //row.addView(textView);
                                    //Log.i("qwerty","qwerty");
                                    //tableLayout.addView(row);
                                    //Log.i("qwerty2","qwerty2");
                                    //adapter.add(valorName);
                                    //listaid.add(valorId);
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
        RequestQueue MyRequestQueue = Volley.newRequestQueue(vista.getContext());
        MyRequestQueue.add(getRequest);
    }
}
