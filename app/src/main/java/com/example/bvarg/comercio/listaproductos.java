package com.example.bvarg.comercio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class listaproductos extends AppCompatActivity {

    static String[] listaproductos;

    ListView lista;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listaproductos);
        lista = findViewById(R.id.lista);

        Log.i("estos son: ", listaproductos.toString());
        ListAdapter adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaproductos);
        lista.setAdapter(adaptador);

    }
}
