package com.future.citylines;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by future on 7/10/15.
 */
public class Perfil extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    Context ctx;

    TextView titulonombre,titulotelefono,tituloemail,contenidonombre,contenidotelefono,contenidoemail;
    LinearLayout lineapendiente,lineapasado;
    String USER_ID;
    Utilidades util;
    Database db;
    Handler handler;

    public static Perfil newInstance(int sectionNumber) {
        Perfil fragment = new Perfil();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    public Perfil(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.detalleoferta);
        View rootView = inflater.inflate(R.layout.perfil, container, false);
        ctx = getActivity().getApplicationContext();
        Intent intent = getActivity().getIntent();

     Bundle args = getArguments();
       //int index = args.getInt("index", 0);
        USER_ID = ((Navigation) getActivity()).getUserId();
        util = new Utilidades();
        db = new Database();
        handler = new Handler();

        titulonombre = (TextView) rootView.findViewById(R.id.titulonombre);
        titulotelefono = (TextView) rootView.findViewById(R.id.titultelefono);
        tituloemail = (TextView) rootView.findViewById(R.id.tituloemail);
        contenidonombre = (TextView) rootView.findViewById(R.id.contenidonombre);
        contenidotelefono = (TextView) rootView.findViewById(R.id.contenidotelefono);
        contenidoemail = (TextView) rootView.findViewById(R.id.contenidoemail);

        lineapendiente = (LinearLayout) rootView.findViewById(R.id.lineapendientes);
        lineapasado = (LinearLayout) rootView.findViewById(R.id.lineapasados);



        new Thread(new Runnable() {
            @Override
            public void run() {
                updatePersonalInfo();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                updatePendientes();
            }
        }).start();

        return rootView;
    }

    public void updatePendientes(){
        String pendientes_query = "select c.nombre_negocio,b.nombre_oferta,b.descripcion_oferta,b.subtotal_oferta,b.total_oferta,b.descuento,a.fecha_orden,a.id,a.id_oferta from orden as a, oferta as b, negocio as c where c.id = b.id_negocio and b.id=a.id_oferta and a.estado_orden = 'activa' and a.id_usuario = "+USER_ID;
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
                                bundle.putString("ordenid",orden_id);
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

    public void updatePersonalInfo(){

        String personal_query = "select a.nombres_persona,a.apellidos_persona,a.telefono,b.email from datos_personal as a, usuario as b where a.id_usuario=b.id and b.id = "+USER_ID;
        final ResultSet resultset = db.executeQuery(personal_query);

        try {
            while(resultset.next()){
                final String nombres = resultset.getString("nombres_persona");
                final String apellidos = resultset.getString("apellidos_persona");
                final String email = resultset.getString("email");
                final String telefono = resultset.getString("telefono");
            lineapendiente.post(new Runnable() {
                @Override
                public void run() {
                        contenidonombre.setText(nombres+" "+apellidos);
                        contenidotelefono.setText(telefono);
                        contenidoemail.setText(email);
                }
            });

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

}
