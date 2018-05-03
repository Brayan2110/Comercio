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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HistorialFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HistorialFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistorialFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ArrayList fecha = new ArrayList();
    ArrayList monto = new ArrayList();
    ArrayList cliente = new ArrayList();
    ArrayList productos = new ArrayList();
    ArrayList total = new ArrayList();
    ArrayList estado = new ArrayList();
    ListAdapter adaptador;
    ListView lista;
    View vista;
    private OnFragmentInteractionListener mListener;

    public HistorialFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistorialFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistorialFragment newInstance(String param1, String param2) {
        HistorialFragment fragment = new HistorialFragment();
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
        vista = inflater.inflate(R.layout.fragment_pedidos, container, false);
        lista = vista.findViewById(R.id.lista);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] partes = productos.get(position).toString().replace("[","").replace("]","").split(",");
                listaproductos.listaproductos = partes;
                Intent intent = new Intent(getContext(), listaproductos.class);
                startActivity(intent);

            }
        });

        adaptador = new ArrayAdapter<String>(vista.getContext(), android.R.layout.simple_list_item_1, total);
        llenar(MainActivity.sharedPreferences.getString("idcomercio", ""));
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

    public void llenar(String id){
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, "https://food-manager.herokuapp.com/orders/market/"+id, null,
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

                                    Log.i("precio",mainObject.getString("price"));
                                    monto.add(mainObject.getString("price"));

                                    String anho = mainObject.getString("date").substring(0,4);
                                    String mes = mainObject.getString("date").substring(5,7);
                                    String dia = mainObject.getString("date").substring(8,10);
                                    Log.i("fecha",dia+"/"+mes+"/"+anho);
                                    fecha.add(dia+"/"+mes+"/"+anho);
                                    Log.i("productos",mainObject.getString("products"));
                                    productos.add(mainObject.getString("products"));
                                    estado.add(mainObject.getString("status"));
                                    JSONObject mainObject2 = new JSONObject(mainObject.getString("user"));
                                    //obtiene valores dentro del objeto
                                    String nombre = mainObject2.getString("name");
                                    Log.i("nombre",nombre);
                                    cliente.add(nombre);
                                    total.add("Monto: "+ monto.get(i)+"\n"+"Fecha: "+fecha.get(i)+"\n"+"Cliente: "+cliente.get(i)+"\n"+"Estado: "+estado.get(i));
                                }
                            }
                            lista.setAdapter(adaptador);
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
