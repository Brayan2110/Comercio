package com.example.bvarg.comercio;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PedidosFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PedidosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PedidosFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ArrayList ordenid = new ArrayList();
    ArrayList fecha = new ArrayList();
    ArrayList monto = new ArrayList();
    ArrayList cliente = new ArrayList();
    ArrayList productos = new ArrayList();
    ArrayList total = new ArrayList();
    String correo;
    String contraseña;
    Session  session;
    ListAdapter adaptador;
    ListView lista;
    View vista;

    private OnFragmentInteractionListener mListener;

    public PedidosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PedidosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PedidosFragment newInstance(String param1, String param2) {
        PedidosFragment fragment = new PedidosFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v, final int index, long arg3) {
                // TODO Auto-generated method stub
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                builder1.setMessage("Aceptar el pedido");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Si",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                update("Aceptada",String.valueOf(ordenid.get(index)));
                                //Obtener el correo del cliente que hizo el pedido y pasarlo como parametro.
                                // sendRegisterMail(email)
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                update("Rechazada",String.valueOf(ordenid.get(index)));
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
                return true;
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
                                    Log.i("estado2", mainObject.getString("status"));
                                    if(mainObject.getString("status").equals("Pendiente")){
                                        Log.i("precio",mainObject.getString("price"));
                                        monto.add(mainObject.getString("price"));
                                        ordenid.add(mainObject.getString("_id"));
                                        String anho = mainObject.getString("date").substring(0,4);
                                        String mes = mainObject.getString("date").substring(5,7);
                                        String dia = mainObject.getString("date").substring(8,10);
                                        Log.i("fecha",dia+"/"+mes+"/"+anho);
                                        fecha.add(dia+"/"+mes+"/"+anho);
                                        Log.i("productos",mainObject.getString("products"));
                                        productos.add(mainObject.getString("products"));

                                        JSONObject mainObject2 = new JSONObject(mainObject.getString("user"));
                                        //obtiene valores dentro del objeto
                                        String nombre = mainObject2.getString("name");
                                        Log.i("nombre",nombre);
                                        cliente.add(nombre);
                                        total.add("Monto: "+ mainObject.getString("price")+"\n"+"Fecha: "+dia+"/"+mes+"/"+anho+"\n"+"Cliente: "+mainObject2.getString("name"));
                                    }
                                }
                                lista.setAdapter(adaptador);
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


    public void update(final String estado, final String OrderID){
        String url ="https://food-manager.herokuapp.com/orders/"+OrderID;
        StringRequest putRequest = new StringRequest(Request.Method.PATCH, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        if(estado.equals("Aceptada")){
                            Toast.makeText(getContext(), "Se acepto el pedido", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getContext(), "Se rechazo el pedido", Toast.LENGTH_SHORT).show();
                        }
                        ordenid.clear();
                        fecha.clear();
                        monto.clear();
                        cliente.clear();
                        productos.clear();
                        total.clear();
                        ordenid = new ArrayList();
                        fecha = new ArrayList();
                        monto = new ArrayList();
                        cliente = new ArrayList();
                        productos = new ArrayList();
                        total = new ArrayList();
                        adaptador = new ArrayAdapter<String>(vista.getContext(), android.R.layout.simple_list_item_1, total);
                        llenar(MainActivity.sharedPreferences.getString("idcomercio", ""));
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        //Log.d("Error.Response", response);
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("value",estado);


                return params;
            }

        };
        RequestQueue MyRequestQueue = Volley.newRequestQueue(getContext());
        MyRequestQueue.add(putRequest);
    }
    private void sendRegisterMail(String email){
        correo = "Foodmanagercr@gmail.com";
        contraseña = "FoodManager#admin";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Properties properties = new Properties();
        properties.put("mail.smtp.host","smtp.gmail.com");
        properties.put("mail.smtp.socketFactory.port","465");
        properties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.port","465");

        try{
            session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return  new PasswordAuthentication(correo,contraseña);
                }
            });

            if(session!= null){
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(correo));
                message.setSubject("<b>Bienvenido</b>");
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
                message.setContent("Tu pedido ha esta listo, gracias por utilizar food Manager","text/html; charset=utf-8");
                Transport.send(message);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
