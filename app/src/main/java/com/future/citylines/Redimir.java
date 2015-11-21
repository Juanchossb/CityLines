package com.future.citylines;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.encoder.QRCode;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by future on 29/10/15.
 */
public class Redimir extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    Context ctx;
    String USER_ID;
    Database db;
    Handler handler;
    Utilidades util;
    LinearLayout lineapendiente;
    Button escanbuton;
    public static Redimir newInstance(int sectionNumber) {
        Redimir fragment = new Redimir();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;

    }
    public Redimir(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.redimir, container, false);

        ctx = getActivity().getApplicationContext();
        Intent intent = getActivity().getIntent();
        handler = new Handler();
        lineapendiente= (LinearLayout) rootView.findViewById(R.id.lineapendientes);
        db=new Database();
        util = new Utilidades();
        USER_ID = getArguments().getString("userid");
        escanbuton =(Button) rootView.findViewById(R.id.escanear);

        escanbuton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
    startActivityForResult(intent,0);
/*
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");

              startActivityForResult(intent, 0); */

                IntentIntegrator integrator = new IntentIntegrator(getActivity());
                integrator.initiateScan();
            }
        });


new Thread(new Runnable() {
    @Override
    public void run() {
        updatePendientes();
    }
}).start();

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            // handle scan result
        }
        // else continue with any other code you need in the method

    }

//retrieve scan result
     //   super.onActivityResult(requestCode, resultCode, intent);
/*
        if (requestCode == 0) {
            if (resultCode == getActivity().RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                // Handle successful scan
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                // Handle cancel
            }
        }

        /*
        IntentIntegrator integrator = new IntentIntegrator(getActivity());
        integrator.initiateScan();
       */ //IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        /*
        if (scanningResult != null) {


        }else{
            Toast toast = Toast.makeText(ctx,
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
        */
    //}

    public void updatePendientes(){
        String pendientes_query = "select c.nombre_negocio,b.nombre_oferta,b.descripcion_oferta,b.subtotal_oferta,b.total_oferta,b.descuento,a.fecha_orden,a.id,a.id_oferta from orden as a, oferta as b, negocio as c where c.id = b.id_negocio and b.id=a.id_oferta and a.estado_orden = 'activa' and a.id_negocio = 1";

        ResultSet resultset = db.executeQuery(pendientes_query);

        final TextView tituloseccion = new TextView(ctx);
        tituloseccion.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tituloseccion.setTextSize(20);
        tituloseccion.setText("ORDENES PENDIENTES");
        tituloseccion.setGravity(Gravity.CENTER_HORIZONTAL);

        lineapendiente.post(new Runnable() {
            @Override
            public void run() {
                lineapendiente.addView(tituloseccion);
            }
        });

        try {
            while (resultset.next()){

                String nombre_negocio = resultset.getString("nombre_negocio");
                String nombre_oferta = resultset.getString("nombre_oferta");
                String fecha_orden = resultset.getString("fecha_orden");
                final String oferta_id = resultset.getString("id_oferta");
                final String orden_id= resultset.getString("id");

                final LinearLayout cadapendiente = new LinearLayout(ctx);
                LinearLayout.LayoutParams cadaparam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                cadaparam.setMargins(0, 5, 0, 5);
                cadapendiente.setLayoutParams(cadaparam);

                cadapendiente.setOrientation(LinearLayout.VERTICAL);
                cadapendiente.setBackgroundResource(R.drawable.rounded_gris);

                TextView textofecha = new TextView(ctx);
                textofecha.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                textofecha.setText(fecha_orden);
                textofecha.setTextSize(16);
                textofecha.setTextColor(Color.BLACK);

                TextView texto_negocio = new TextView(ctx);
                texto_negocio.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                texto_negocio.setText(nombre_negocio);
                texto_negocio.setTextSize(18);
                texto_negocio.setTextColor(Color.BLACK);

                TextView texto_oferta = new TextView(ctx);
                texto_oferta.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                texto_oferta.setText(nombre_oferta);
                texto_oferta.setTextSize(16);
                texto_oferta.setTextColor(Color.BLACK);
                cadapendiente.addView(textofecha);
                cadapendiente.addView(texto_negocio);
                cadapendiente.addView(texto_oferta);

                cadapendiente.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //     String id = null;

                        // id = listaidoferta.get(Integer.parseInt(v.getTag().toString()));
                                       /*
                                        Intent intentdetalle = new Intent(Ofertas.this, DetalleOferta.class);
                                       intentdetalle.putExtra("id", id);
                                       startActivity(intentdetalle);
*/
                        final DetalleOferta detalleoferta = new DetalleOferta();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ArrayList<String> listaproductos = new ArrayList<String>();
                                ArrayList<String> listaingredientes = new ArrayList<String>();
                                Bundle bundle = new Bundle();
                                bundle.putString("id", oferta_id);
                                bundle.putString("TIPO_NAVEGACION", "redimir");



                                String producto_ingrediente_query = "select id_producto,id_ingrediente from producto_orden where id_orden = " + orden_id;
                                ResultSet resultpo = db.executeQuery(producto_ingrediente_query);
                                try {
                                    while (resultpo.next()) {
                                        listaproductos.add(resultpo.getString("id_producto"));
                                        listaingredientes.add(resultpo.getString("id_ingrediente"));
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                bundle.putStringArrayList("listaproductos",listaproductos);
                                bundle.putStringArrayList("listaingredientes",listaingredientes);
                                detalleoferta.setArguments(bundle);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((Navigation) getActivity()).getFragmentManager().beginTransaction()
                                                .replace(R.id.container, detalleoferta, null)
                                                .addToBackStack(null)
                                                .commit();
                                    }
                                });


                            }
                        }).start();


                    }
                });



                lineapendiente.post(new Runnable() {
                    @Override
                    public void run() {
                        lineapendiente.addView(cadapendiente);
                    }
                });



            }
        } catch (SQLException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            e.printStackTrace();
        }


    }

}
