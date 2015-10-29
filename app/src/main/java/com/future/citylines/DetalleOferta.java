package com.future.citylines;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;

import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


/**
 * Created by future on 9/09/15.
 */
public class DetalleOferta extends Fragment implements View.OnTouchListener{
    private static final String ARG_SECTION_NUMBER = "section_number";
    Context ctx;
    String ID_OFERTA;
    String TIPO_OFERTA;
    String ID_NEGOCIO;
    List<String> lista_query_ingredientes;
    //Tipos de Oferta
    String TIPO_OFERTA_PORCENTAJE_OFERTA = "oferta porcentaje";
    String TIPO_OFERTA_PRECIO_FIJO = "precio fijo";
    String TIPO_OFERTA_PORCENTAJE_ITEMS = "porcentaje items";

    LinearLayout detallebanner,lineaoferta,lineaproductos,lineaconstante;
    ImageView logooferta;
    TextView nombrenegocio,nombreoferta,textodescripcion,texttotal,textsubtotal;
    Utilidades util;
Handler handler;
    Database db ;
    DecimalFormat dec;
    static float sX2,sY2,fX2,fy2;
    List<List<LinearLayout>> listaproductos;
    List<List<View>> linea;
    List<List<Double>> preciosfinales;
    List<List<Double>> preciosanteriores;

    List<List<List<String[]>>> precios_productos;
    String ORDER_QUERY= null;

    List<String[]> ingrediente_tipo;

    Button botoncomprar;

    Dialog loading;

    public static DetalleOferta newInstance(int sectionNumber) {
        DetalleOferta fragment = new DetalleOferta();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    public DetalleOferta(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.detalleoferta);
        View rootView = inflater.inflate(R.layout.detalleoferta, container, false);

        ctx = getActivity().getApplicationContext();
        util =new Utilidades();
        db = new Database();
        handler = new Handler();
        precios_productos = new ArrayList<List<List<String[]>>>() ;
        lista_query_ingredientes = new ArrayList<String>();
        ingrediente_tipo = new ArrayList<String[]>();
        Intent intent = getActivity().getIntent();
       ID_OFERTA = intent.getStringExtra("id");
        listaproductos = new ArrayList<List<LinearLayout>>();
        //Inicializar las vistas del layout
        detallebanner = (LinearLayout) rootView.findViewById(R.id.detalleimg);
        lineaoferta = (LinearLayout) rootView.findViewById(R.id.lineaofertas);
        lineaproductos = (LinearLayout) rootView.findViewById(R.id.lineaproductos);
        logooferta = (ImageView) rootView.findViewById(R.id.logooferta);
        nombrenegocio = (TextView) rootView.findViewById(R.id.nombrenegocio);
        nombreoferta = (TextView) rootView.findViewById(R.id.nombreoferta);
        textodescripcion = (TextView) rootView.findViewById(R.id.textodescripcion);
        lineaconstante = (LinearLayout) rootView.findViewById(R.id.lineaconstante);
        texttotal = (TextView) rootView.findViewById(R.id.texttotal);
        texttotal.setTextColor(Color.GREEN);
        texttotal.setTextSize(16);
        textsubtotal = (TextView) rootView.findViewById(R.id.textsubtotal);
        textsubtotal.setPaintFlags(textsubtotal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        textsubtotal.setTextColor(Color.RED);

        dec =  new DecimalFormat("#.000");
        detallebanner.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, util.screenSizeY(ctx) / 3));
        detallebanner.setPadding(0, util.screenSizeY(ctx) / 6, 0, 0);
        logooferta.setLayoutParams(new LinearLayout.LayoutParams(util.screenSizeX(ctx) / 5, util.screenSizeX(ctx) / 5));
        nombrenegocio.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, util.screenSizeX(ctx) / 10));
        nombreoferta.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, util.screenSizeX(ctx) / 10));

        botoncomprar = (Button) rootView.findViewById(R.id.btncomparar);

       // lineaconstante.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,util.screenSizeY(this)/7));
        lineaconstante.setBackgroundColor(Color.LTGRAY);
        loading = util.loadingDialog(ctx,"Por faqvor Espere...");
        final String query_oferta = "select a.nombre_oferta, a.imagen_oferta, a.id_negocio, a.descripcion_oferta,a.subtotal_oferta,a.total_oferta, a.descuento, b.logo, b.nombre_negocio from oferta as a, negocio as b where b.id = a.id_negocio and a.id = "+ID_OFERTA;

        new Thread(new Runnable() {
            @Override
            public void run() {


                ResultSet resultset = db.executeQuery(query_oferta);
                try {
                    while (resultset.next()) {
                        final String noferta = resultset.getString("nombre_oferta");
                        final String imgoferta = resultset.getString("imagen_oferta");
                        ID_NEGOCIO = resultset.getString("id_negocio");
                        final String descoferta = resultset.getString("descripcion_oferta");
                        String subtotal_orden = resultset.getString("subtotal_oferta");
                        Double mst = Double.valueOf(subtotal_orden)/1000;
                        final String subtotal = dec.format(mst);

                        String total_orden = resultset.getString("total_oferta");
                        Double mt = Double.valueOf(total_orden)/1000;
                        final String total = dec.format(mt);

                        String descuento = resultset.getString("descuento");


                        final String logonegocio = resultset.getString("logo");
                        final String nnegocio = resultset.getString("nombre_negocio");
                        final Drawable bannerdrawable = new BitmapDrawable(util.getBitmap("http://104.197.16.226/images/ofertas/" + imgoferta));
                        final Bitmap logobitmap = util.getBitmap("http://23.251.149.115/images/logos/" + logonegocio);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                nombreoferta.setText(noferta);
                                nombrenegocio.setText(nnegocio);
                                textodescripcion.setText(descoferta);
                                texttotal.setText("$"+total);
                                textsubtotal.setText("$"+subtotal);

                                detallebanner.setBackground(bannerdrawable);
                                logooferta.setImageBitmap(logobitmap);
                            }
                        });

                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }


            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    linea = CadaProducto(ID_OFERTA);
                    calcularTotales();

                    lineaproductos.post(new Runnable() {
                        @Override
                        public void run() {
                            for (List<View> vista : linea) {
                                int t=0;
                             //   List<View> cadaseccion = new ArrayList<View>();
                               // LinearLayout cadaseccion = new LinearLayout(ctx);
                               // cadaseccion.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                              //  cadaseccion.setOrientation(LinearLayout.HORIZONTAL);
                                for (View view : vista){
                                    if (view instanceof TextView) {

                                  //      lineaproductos.addView(view);
                                    } else {
                                      //  view.setVisibility(View.GONE);
                                           // cadaseccion.addView(view);
                                        if (t == 0){
                                     //       view.setVisibility(View.VISIBLE);
                                            t++;
                                        }
                                    }
                                }
                              //  lineaproductos.addView(cadaseccion);

                            }

                        }
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();


updateOrder();


        botoncomprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                    /*
                        String query_enviar_orden ="";
                        //db.insertData(ORDER_QUERY);
                        query_enviar_orden+=ORDER_QUERY+";";
                        for(String qry:lista_query_ingredientes){
                            query_enviar_orden+=qry+";";
                           // db.insertData(qry);
                        }
*/
                        try {
                            db.insertMultipleRows(lista_query_ingredientes);

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    loading.dismiss();
                                    util.toast(ctx, "La orden ha sido enviada con exito");
                                   // finish();
                                }
                            });

                        } catch (SQLException e) {
                          //  e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    loading.dismiss();
                                    util.toast(ctx, "Error enviando la Orden");
                                   // finish();
                                }
                            });
                        } catch (ClassNotFoundException e) {
                            //e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    loading.dismiss();
                                    util.toast(ctx, "Error enviando la orden");
                                  //  finish();
                                }
                            });
                        }


                    }
                }).start();
            }
        });
        return  rootView;
    }
    public List<List<View>> CadaProducto(String id_oferta) throws SQLException {

        List<List<String[]>> cadalista = new ArrayList<List<String[]>>();
        //Linea que va a ser devuelta conteniendo todos los elemententos de la oferta
        LinearLayout linea = new LinearLayout(ctx);
        linea.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        linea.setOrientation(LinearLayout.VERTICAL);

        String secciones_query = "SELECT id,nombre_seccion from seccion_oferta where id_oferta = "+id_oferta;
        ResultSet result_secciones = db.executeQuery(secciones_query);
        List<List<View>> ls = new ArrayList<List<View>>();
    while (result_secciones.next()) {
    String producto_query = "SELECT a.nombre_seccion, b.nombre_producto,b.descripcion_producto,b.precio,b.img_producto, c.porcentaje_descuento, b.id from seccion_oferta as a, producto as b, producto_oferta as c where a.id=" + result_secciones.getString("id") + " and a.id_oferta = " + id_oferta + " and c.id_seccion_oferta = a.id and b.id = c.id_producto";
    ResultSet resultSet = db.executeQuery(producto_query);


    //Linea para cada seccion

    //Listado de views
    List<View> vistas = new ArrayList<View>();


    preciosanteriores = new ArrayList<List<Double>>();
    preciosfinales = new ArrayList<List<Double>>();


    //Listado de secciones

    final List<View> lineahorizontal = new ArrayList<View>();
   // final LinearLayout lineahorizontal = new LinearLayout(ctx);
   // lineahorizontal.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
   // lineahorizontal.setOrientation(LinearLayout.HORIZONTAL);


    try {
        String seccion = null;
        int counttag = 0;
        while (resultSet.next()) {

            //Cada seccion de la oferta
            final LinearLayout cadaseccion = new LinearLayout(ctx);
            cadaseccion.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            cadaseccion.setOrientation(LinearLayout.HORIZONTAL);
            final LinearLayout lineapersonalizacion = new LinearLayout(ctx);
            lineapersonalizacion.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, util.screenSizeY(ctx) / 4));
            lineapersonalizacion.setOrientation(LinearLayout.HORIZONTAL);
            lineapersonalizacion.setVisibility(View.GONE);

            final LinearLayout lineaimgingre = new LinearLayout(ctx);
            lineaimgingre.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            lineaimgingre.setOrientation(LinearLayout.VERTICAL);

            //Datos de cada plato de la oferta
            String nombreseccion = resultSet.getString("nombre_seccion");
            String nombreproducto = resultSet.getString("nombre_producto");
            String descripcionproducto = resultSet.getString("descripcion_producto");
            String precio_prod = resultSet.getString("precio");
            Double precio_double = Double.valueOf(precio_prod) / 1000;
            String precio = "$" + dec.format(precio_double);
            String imagen = resultSet.getString("img_producto");
            String idproducto = resultSet.getString("id");

            String descuento = null;

lineaimgingre.setTag(idproducto);

            if (resultSet.getString("porcentaje_descuento") != null) {
                descuento = resultSet.getString("porcentaje_descuento");
            }

            List<String[]> lista_precio_producto = new ArrayList<String[]>();
            String tmp2[] = new String[4];
            tmp2[0] = idproducto;
            tmp2[1] =precio;
            tmp2[2] = descuento;
            tmp2[3] = "producto";
            lista_precio_producto.add(tmp2);
            cadalista.add(lista_precio_producto);

            //Funcion para saber cuando porner la Linea de los titulos de las secciones
            //Esta funcion tambien nos sirve para saber cuando hay cambio de una seccion a otra
            if (!nombreseccion.equals(seccion)) {
                final TextView lineaseccion = new TextView(ctx);
                lineaseccion.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                lineaseccion.setBackground(getResources().getDrawable(R.drawable.cuadro_borde_gris));
                lineaseccion.setText(nombreseccion);
                lineaseccion.length();
                linea.setTag(100);

                ls.add(new ArrayList<View>());
                ls.get(ls.size() - 1).add(lineaseccion);

                preciosanteriores.add(new ArrayList<Double>());
                preciosfinales.add(new ArrayList<Double>());

              //  vistas.add(lineaseccion);
                lineaproductos.post(new Runnable() {
                    @Override
                    public void run() {
                        lineaproductos.addView(lineaseccion);
                    }
                });

                seccion = nombreseccion;
            }


            //Linea que va a tener la imagen del producto, el nombre y el precio
            final RelativeLayout imagenproducto = new RelativeLayout(ctx);

            if (imagen != null) {
                imagenproducto.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, util.screenSizeY(ctx) / 3));
                String imgurl = "http://23.251.149.115/images/productos/" + imagen;
                Drawable prod = new BitmapDrawable(util.getBitmap(imgurl));
                imagenproducto.setBackground(prod);
            }
            //Linea horizontal que contiene los texviews del nombre del producto y el precio
            LinearLayout lineainfoprod = new LinearLayout(ctx);
            RelativeLayout.LayoutParams paramsproducto = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            paramsproducto.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lineainfoprod.setLayoutParams(paramsproducto);
            lineainfoprod.setOrientation(LinearLayout.HORIZONTAL);
            lineainfoprod.setBackgroundColor(Color.BLACK);
            lineainfoprod.setBackground(getResources().getDrawable(R.drawable.gradient_negro));
            lineainfoprod.setAlpha(new Float(50));
            lineainfoprod.setPadding(0, 5, 0, 5);
            lineainfoprod.setTag(104);


            TextView productoname = new TextView(ctx);
            productoname.setLayoutParams(new LinearLayout.LayoutParams(util.screenSizeX(ctx) / 2, ViewGroup.LayoutParams.MATCH_PARENT));
            productoname.setText(nombreproducto);
            productoname.setTextColor(Color.WHITE);
            productoname.setTextSize(20);

            TextView productopreciodesc = new TextView(ctx);
            productopreciodesc.setLayoutParams(new LinearLayout.LayoutParams(util.screenSizeX(ctx) / 4, ViewGroup.LayoutParams.MATCH_PARENT));
            productopreciodesc.setText(precio);
            productopreciodesc.setTextColor(Color.WHITE);
            productopreciodesc.setTextSize(20);
            productopreciodesc.setVisibility(View.GONE);
            TextView productoprecio = new TextView(ctx);
            productoprecio.setLayoutParams(new LinearLayout.LayoutParams(util.screenSizeX(ctx) / 4, ViewGroup.LayoutParams.MATCH_PARENT));
            productoprecio.setText(precio);
            productoprecio.setTextColor(Color.WHITE);
            productoprecio.setTextSize(20);
            double precio_descuento = 0;
            if (descuento != null) {
                precio_descuento = (precio_double * 1000) - ((precio_double * 1000) * (Double.valueOf(descuento) / 100));
                String precio2 = dec.format(precio_descuento / 1000);
                productopreciodesc.setText("$" + precio2);
                productoprecio.setPaintFlags(productoprecio.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                productopreciodesc.setVisibility(View.VISIBLE);
                productopreciodesc.setTag(105);

            } else {
                productoprecio.setTag(105);
            }
//                imagenproducto.setTag(1,productoprecio);
            //              imagenproducto.setTag(2,productopreciodesc);


            TextView productodescuento = new TextView(ctx);
            RelativeLayout.LayoutParams decparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            decparams.addRule(RelativeLayout.ABOVE, lineainfoprod.getId());
            decparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            productodescuento.setLayoutParams(decparams);
            productodescuento.setTextColor(Color.YELLOW);
            productodescuento.setTextSize(30);

            //Agregamos boton de personalizacion
            RelativeLayout.LayoutParams perparams = new RelativeLayout.LayoutParams(util.screenSizeX(ctx)/10,util.screenSizeX(ctx)/10);
            perparams.addRule(RelativeLayout.BELOW, productodescuento.getId());
            perparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            perparams.setMargins(5, util.screenSizeY(ctx) / 6, 0, 0);

            ImageView botonpersonalizacion = new ImageView(ctx);
            botonpersonalizacion.setLayoutParams(perparams);
            botonpersonalizacion.setImageResource(R.drawable.icon2);
            botonpersonalizacion.setVisibility(View.GONE);

            ImageView botonextra = new ImageView(ctx);

            RelativeLayout.LayoutParams perparams2 = new RelativeLayout.LayoutParams(util.screenSizeX(ctx)/10,util.screenSizeX(ctx)/10);
           // perparams2.addRule(RelativeLayout.BELOW, productodescuento.getId());
            perparams2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            perparams2.setMargins(5, (util.screenSizeY(ctx) / 6)-(util.screenSizeX(ctx)/10), 0, 0);
            botonextra.setLayoutParams(perparams2);
        //    botonextra.setPadding(0,50,0,0);
            botonextra.setImageResource(R.drawable.icon1);
            botonextra.setVisibility(View.GONE);


            //Agregamos el nombre del producto y el precio a la linea de informacion
            lineainfoprod.addView(productoname);
            lineainfoprod.addView(productoprecio);
            lineainfoprod.addView(productopreciodesc);

            //Agregamos la linea de la informaciona a la linea de la foto
            imagenproducto.addView(lineainfoprod);
            imagenproducto.addView(botonpersonalizacion);
            imagenproducto.addView(botonextra);

            if (descuento != null) {
                TIPO_OFERTA = TIPO_OFERTA_PORCENTAJE_OFERTA;
                productodescuento.setText(descuento + "%");
                imagenproducto.addView(productodescuento);
            }

            //Agregamos la linea de las imagenes a cada seccion
            imagenproducto.setTag(counttag);
            counttag++;
            imagenproducto.setTag(505);
            //imagenproducto.setOnTouchListener(this);
          //  vistas.add(imagenproducto);

         // cadaseccion.addView(imagenproducto);
            ls.get(ls.size() - 1).add(imagenproducto);
            if (preciosanteriores.size() == 0) {
                preciosanteriores.get(0).add(precio_double * 1000);
                preciosfinales.get(0).add(precio_descuento);
            } else {
                preciosanteriores.get(preciosanteriores.size() - 1).add(precio_double * 1000);
                preciosfinales.get(preciosfinales.size() - 1).add(precio_descuento);
            }

            //Agregramos los ingredientesd de los items

            String query_extras= "select a.id,a.nombre_ingrediente,b.incluido,b.extra,c.precio_ingrediente from ingrediente as a, ingrediente_producto as b, precio_ingrediente as c where b.id_ingrediente = a.id and c.id_ingrediente = a.id and a.id in (select id_ingrediente from ingrediente_producto where id_producto = "+idproducto+" and (incluido = 'si' or extra= 'si')) ";
            ResultSet result_extras = db.executeQuery(query_extras);

            final LinearLayout lineaextras = new LinearLayout(ctx);
            lineaextras.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            lineaextras.setOrientation(LinearLayout.VERTICAL);

            TextView extratitulo = new TextView(ctx);
            extratitulo.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            extratitulo.setText("Ingredientes:");
            lineaextras.addView(extratitulo);
            lineaextras.setVisibility(View.GONE);

            List<String[]> lista_extras = new ArrayList<String[]>();
            while(result_extras.next()){

               botonextra.setVisibility(View.VISIBLE);
                CheckBox checkingre = new CheckBox(ctx);
                checkingre.setLayoutParams(new LinearLayout.LayoutParams(util.screenSizeX(ctx) / 2, ViewGroup.LayoutParams.WRAP_CONTENT));
                checkingre.setText(result_extras.getString("nombre_ingrediente") + " $" + result_extras.getString("precio_ingrediente"));
                checkingre.setTag(result_extras.getString("id"));


                if (result_extras.getString("incluido").equals("si")){
                    checkingre.setChecked(true);
                    checkingre.setText(result_extras.getString("nombre_ingrediente"));

                }
                if (result_extras.getString("extra").equals("si") && result_extras.getString("incluido").equals("no")){
                    String tmp[] = new String[4];
                    tmp[0] = result_extras.getString("id");
                    tmp[1] = result_extras.getString("precio_ingrediente");
                    tmp[3] = "ingrediente";
                    lista_extras.add(tmp);
                    checkingre.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            updateOrder();
                        }
                    });
                }
                lineaextras.addView(checkingre);

            }
            cadalista.add(lista_extras);
            precios_productos.add(cadalista);
            String seccion_oferta_query = "select c.nombre_tipo_ingrediente from ingrediente_producto as a, ingrediente as b, tipo_ingrediente as c where a.id_ingrediente = b.id and c.id= b.id_tipo_ingrediente and a.id_producto = "+idproducto+" group by (c.nombre_tipo_ingrediente)";
            ResultSet resultidtipoingrediente = db.executeQuery(seccion_oferta_query);

            while(resultidtipoingrediente.next()){


                String ingrediente_query = "select a.id, a.nombre_ingrediente from ingrediente as a, tipo_ingrediente as b where b.nombre_tipo_ingrediente = '"+resultidtipoingrediente.getString("nombre_tipo_ingrediente")+"' and a.id_tipo_ingrediente = b.id";
                ResultSet resultingre = db.executeQuery(ingrediente_query);

                final LinearLayout cadaingre = new LinearLayout(ctx);
                cadaingre.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                cadaingre.setOrientation(LinearLayout.VERTICAL);
                TextView ingretitulo = new TextView(ctx);
                ingretitulo.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                ingretitulo.setTextSize(20);

                cadaingre.addView(ingretitulo);

                final List<View> listacheck = new ArrayList<View>();


                while (resultingre.next()) {
                    botonpersonalizacion.setVisibility(View.VISIBLE);
                    String [] tmparray = new String[2];
                    tmparray[0] = resultingre.getString("id");
                    tmparray[1] = resultidtipoingrediente.getString("nombre_tipo_ingrediente");
                    ingrediente_tipo.add(tmparray);
                    ingretitulo.setText("Que clase de " + resultidtipoingrediente.getString("nombre_tipo_ingrediente"));

                    final CheckBox checkingre = new CheckBox(ctx);
                    checkingre.setLayoutParams(new LinearLayout.LayoutParams(util.screenSizeX(ctx) / 2, ViewGroup.LayoutParams.WRAP_CONTENT));
                    checkingre.setText(resultingre.getString("nombre_ingrediente"));
                    checkingre.setTag(resultingre.getString("id"));
                    listacheck.add(checkingre);

                    checkingre.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked){
                                for (int i=1; i<cadaingre.getChildCount();i++){
                                    ((CheckBox) cadaingre.getChildAt(i)).setChecked(false);
                                }
                                checkingre.setChecked(true);
                                updateOrder();
                            }
                        }
                    });
                    //vistas.add(checkingre);
                    cadaingre.addView(checkingre);
                    cadaingre.setOnTouchListener(this);


                }
                ((CheckBox) cadaingre.getChildAt(1)).setChecked(true);
                // cadaingre.setVisibility(View.GONE);
                lineapersonalizacion.addView(cadaingre);
                lineapersonalizacion.setTag(606);

                ls.add(listacheck);
            }

            if (botonpersonalizacion.getVisibility() == View.VISIBLE){
                botonpersonalizacion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //noinspection ResourceType
                        lineapersonalizacion.setVisibility(toggleVisibility(lineapersonalizacion.getVisibility()));
                        lineaextras.setVisibility(View.GONE);
                    }
                });
            }

                botonextra.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //noinspection ResourceType
                        lineaextras.setVisibility(toggleVisibility(lineaextras.getVisibility()));
                        lineapersonalizacion.setVisibility(View.GONE);
                    }
                });


           // lineapersonalizacion.getChildAt(0).setVisibility(View.VISIBLE);

            lineaimgingre.addView(imagenproducto);
            lineaimgingre.addView(lineapersonalizacion);
            lineaimgingre.addView(lineaextras);
        //    lineaimgingre.setVisibility(View.GONE);
           // lineaimgingre.setVisibility(View.GONE);
            //lineaimgingre.setOnTouchListener(this);
            lineahorizontal.add(lineaimgingre);


        }
        lineahorizontal.get(0).setVisibility(View.VISIBLE);
       // lineaimgingre.setVisibility(View.GONE);
        lineaproductos.post(new Runnable() {
            @Override
            public void run() {
                ViewPager viewpager = new ViewPager(ctx);
                viewpager.setLayoutParams(new LinearLayout.LayoutParams(util.screenSizeX(ctx),util.screenSizeY(ctx)/3));
                viewpager.setAdapter(new PagerAdapter(ctx,lineahorizontal));

                lineaproductos.addView(viewpager);

            }
        });


    } catch (SQLException e) {
        e.printStackTrace();
    }

}
        return ls;
    }
    //Implementamos un ontouch listener para decidir que hacer cuando el usuario hace gestos en la pantalla
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            //Cuando el usuario toca
            case MotionEvent.ACTION_DOWN:
                sX2 = event.getX();
                sY2 = event.getY();

                break;
            //Cuando el usuario levanta el dedo despues de tocar
            case MotionEvent.ACTION_UP:
                fX2 = event.getX();
                fy2 = event.getY();
                double difX;
                double difY;
                difX = sX2 - fX2;
                difY = sY2 - fy2;

               // RelativeLayout linear = (RelativeLayout) view;
                LinearLayout parent,uncle = null;
                String tag = "";
                try {
                    tag = view.getTag().toString();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
                if (tag.equals("505")) {
                    parent = (LinearLayout) view.getParent().getParent();
                    uncle = (LinearLayout) ((LinearLayout) view.getParent()).getChildAt(1);
                }
                else
                parent = (LinearLayout) view.getParent();
                if (difX > 100 || difX < -100) {
                    //El usuario hizo swipe
                    String direccion = null;
                    if (difX > 100) {
                        //>El swipe fue de derecha a izquierda
                        direccion = "left";

                        int nLayouts = parent.getChildCount();
                        int tmpcuenta=0;
                        for (int n = 0; n < nLayouts; n++) {

                            if (parent.getChildAt(n).getVisibility() == View.VISIBLE){
                                tmpcuenta = n;
                                parent.getChildAt(n).setVisibility(View.GONE);
                                if (uncle!=null) {
                                    if (uncle.getChildCount() > n)
                                    uncle.getChildAt(n).setVisibility(View.GONE);
                                }
                              //  parent.getChildAt(n).setVisibility(View.GONE);
                               // break;
                            }
                            parent.getChildAt(n).setVisibility(View.GONE);
                            if (uncle!=null) {
                                if (uncle.getChildCount() > n)
                                uncle.getChildAt(n).setVisibility(View.GONE);
                            }
                          //  parent.getChildAt(n).setVisibility(View.GONE);


                        }
                        TranslateAnimation animate = new TranslateAnimation(view.getWidth(), 0, 0, 0);
                        animate.setDuration(200);
                        //  animate.setFillAfter(true);


                        if (tmpcuenta - 1 >= 0) {
                            parent.getChildAt(tmpcuenta - 1).startAnimation(animate);
                            parent.getChildAt(tmpcuenta - 1).setVisibility(View.VISIBLE);
                            if (uncle!=null) {
                                if (uncle.getChildCount() > 0)
                                uncle.getChildAt(tmpcuenta - 1).setVisibility(View.VISIBLE);
                            }
                          //  parent.getChildAt(tmpcuenta - 1).startAnimation(animate);
                          //  parent.getChildAt(tmpcuenta - 1).setVisibility(View.VISIBLE);

                          //  listarOfertas(Integer.parseInt(listaidrevista.get(tmpcuenta - 1)));
                            final int tmpcuenta2 = tmpcuenta;
                            /*
                            linearevistas.post(new Runnable() {
                                @Override
                                public void run() {
                                    videocuenta.setText(tmpcuenta2 + "/" + videos.size());
                                }
                            });

*/
                        } else {

                            parent.getChildAt(nLayouts - 1).startAnimation(animate);
                            parent.getChildAt(nLayouts - 1).setVisibility(View.VISIBLE);
                            if (uncle!=null) {
                                if (uncle.getChildCount() > 0)
                                    try {
                                        uncle.getChildAt(nLayouts - 1).setVisibility(View.VISIBLE);
                                    }catch (NullPointerException e){

                                    }
                            }
                          //  parent.getChildAt(nLayouts - 1).startAnimation(animate);
                          //  parent.getChildAt(nLayouts - 1).setVisibility(View.VISIBLE);
                           /*
                            try {
                                listarOfertas(Integer.parseInt(listaidrevista.get(nLayouts - 1)));
                            }catch (IndexOutOfBoundsException e){

                            }
                            /*
                            lineavideos.post(new Runnable() {
                                @Override
                                public void run() {
                                    videocuenta.setText(videos.size() + "/" + videos.size());
                                }
                            });
*/
                        }



                    } else {
                        //El swipe fue de izquierda a derecha
                        direccion = "right";
                        int nLayouts = parent.getChildCount();
                        int tmpcuenta = 0;
                        for (int n = 0; n < nLayouts; n++) {
                            if (parent.getChildAt(n).getVisibility() == View.VISIBLE)
                                tmpcuenta = n;
                            // linearlayout[n].startAnimation(animate);
                            parent.getChildAt(n).setVisibility(View.GONE);
                            if (uncle!=null) {

                                if (uncle.getChildCount() > n)
                                uncle.getChildAt(n).setVisibility(View.GONE);
                            }
                           // parent.getChildAt(n).setVisibility(View.GONE);
                        }
                        TranslateAnimation animate = new TranslateAnimation(-view.getWidth(), 0, 0, 0);
                        animate.setDuration(200);
                        //animate.setFillAfter(true);

                        if (tmpcuenta < nLayouts-1) {
                            parent.getChildAt(tmpcuenta + 1).startAnimation(animate);
                            parent.getChildAt(tmpcuenta + 1).setVisibility(View.VISIBLE);
                            if (uncle!=null) {
                                try {
                                    if (uncle.getChildCount() > tmpcuenta + 1)
                                        uncle.getChildAt(tmpcuenta + 1).setVisibility(View.VISIBLE);
                                }catch (NullPointerException e){

                                }
                            }
                          //  parent.getChildAt(tmpcuenta + 1).startAnimation(animate);
                           // parent.getChildAt(tmpcuenta + 1).setVisibility(View.VISIBLE);
                          //  listarOfertas(Integer.parseInt(listaidrevista.get(tmpcuenta + 1)));
                            final int tmpcuenta2 = tmpcuenta;
                            /*
                            lineavideos.post(new Runnable() {
                                @Override
                                public void run() {
                                    videocuenta.setText((tmpcuenta2 + 2) + "/" + videos.size());
                                }
                            });
*/

                        } else {
                            parent.getChildAt(0).startAnimation(animate);
                            parent.getChildAt(0).setVisibility(View.VISIBLE);
                            if (uncle!=null) {
                                if (uncle.getChildCount() > 0)
                                uncle.getChildAt(0).setVisibility(View.VISIBLE);
                            }
                      //      parent.getChildAt(0).startAnimation(animate);
                      //      parent.getChildAt(0).setVisibility(View.VISIBLE);
                           // listarOfertas(Integer.parseInt(listaidrevista.get(0)));
                            /*
                            lineavideos.post(new Runnable() {
                                @Override
                                public void run() {
                                    videocuenta.setText("1/" + videos.size());
                                }
                            });
*/

                        }

                    }

//calcularTotales();
updateOrder();

                } else if (difX < 50 || difX > -50) {
                    //todo
                    /*
                    int tmpcount = 0;
                    for (int x = 0 ;x<urls.length;x++){
                        if (cadavideo[x].getVisibility() == View.VISIBLE) {
                            tmpcount = x;
                            break;
                        }
                    }
                    if (urls[tmpcount].contains("youtube")) {




                        //   cadawebview[tmpcount].loadUrl("https://www.youtube.com/embed/"+urls[tmpcount].substring(urls[tmpcount].indexOf("=")+1));
                    }else {
                        videointent.putExtra("url", urls[tmpcount]);
                        startActivity(videointent);

                    }
                */
                }
        }
        return true;
    }
    public void updateLowerBar(final double precio, final double preciodesc){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Double newprice = precio / 1000;
                textsubtotal.setText("$" + dec.format(newprice));
                Double newpricedesc = preciodesc / 1000;
                texttotal.setText("$" + dec.format(newpricedesc));
            }
        });

    }
    public void calcularTotales(){
        int precio=0, preciodesc=0;
        int cuenta1=0;
        for (List<View> ls :linea){
            int  cuenta2=0;
            for (View view1: ls){

                if (view1.getVisibility() == View.VISIBLE){
                    try {
                        precio += preciosanteriores.get(cuenta1).get(cuenta2);
                        if (preciosfinales.get(cuenta1).get(cuenta2)!= 0.0)
                            preciodesc += preciosfinales.get(cuenta1).get(cuenta2);
                        else
                            preciodesc+= preciosanteriores.get(cuenta1).get(cuenta2);

                        cuenta2++;
                    }catch (IndexOutOfBoundsException e){
                  //      e.printStackTrace();
                    }
                }

            }
            cuenta1++;

        }
        updateLowerBar(precio, preciodesc);
    }
    public int toggleVisibility(int visibility){
        if (visibility == View.GONE)
            return View.VISIBLE;
        else
            return View.GONE;
    }
    public double getPrice(String id, String tipo, Boolean descuento){
        double respuesta=0;
        int count=0;

        for (int c=0;c<precios_productos.size();c++){
            //oferta


            for (int v=0; v<precios_productos.get(c).size();v++){
                //Items

                for (int b=0; b<precios_productos.get(c).get(v).size();b++){
                    //Ingredientes
                    if (precios_productos.get(c).get(v).get(b)[3].equals(tipo)) {
                        String ID = precios_productos.get(c).get(v).get(b)[0];
                        String PRICE = precios_productos.get(c).get(v).get(b)[1].replace("$", "").replace(".", "").replace(",","");
                        String DESCUENTO = precios_productos.get(c).get(v).get(b)[2];



                        if (id.equals(ID)) {
                            respuesta = Double.parseDouble(PRICE);
                            if (descuento && DESCUENTO!=null)
                                respuesta = respuesta - (respuesta*(Double.parseDouble(DESCUENTO)/100));
                            break;
                        }
                    }

                }


            }

        }


        return respuesta;
    }

    public void updateOrder(){

        double subtotal=0,total=0;
        lista_query_ingredientes.clear();

        for (int i=0; i< lineaproductos.getChildCount(); i++){

            if (lineaproductos.getChildAt(i) instanceof  LinearLayout){
                //Verificamos que el elemento sea un Linar Layout
               LinearLayout linea1 = (LinearLayout) lineaproductos.getChildAt(i);

                for (int x=0; x<linea1.getChildCount(); x++){
                    //Linea que horizontal que contiene otra linea vertical
                    if ( linea1.getChildAt(x).getVisibility() == View.VISIBLE){
                        //Verificamos que el elemento sea visible
                        //Este elemento deberia contener la imagen, la linea con los elementos intercambiables y la lista con los elementos que van a ser incluidos
                   LinearLayout linea2 =(LinearLayout) linea1.getChildAt(x);
                        String id_articulo = linea2.getTag().toString();

                        if (linea2.getVisibility() == View.VISIBLE) {
                            Double precio_articulo = getPrice(id_articulo,"producto",true);
                            subtotal += getPrice(id_articulo,"producto",false);
                            total += precio_articulo;
                        }
                        String product_query = "insert into producto_orden values (null,(select @lastid),"+id_articulo+",null,null,null)";
                        lista_query_ingredientes.add(product_query);
                        //Objetos intercambiables
                        LinearLayout linea3 = (LinearLayout) linea2.getChildAt(1); // Linea que tiene otras lineas los checkboxes de los elementos intercambiables

                        for (int t=0; t<linea3.getChildCount(); t++){
                            LinearLayout linea4 = (LinearLayout) linea3.getChildAt(t);

                            //Buscamos que los checkboxes dentro de la linea, cuales estan checkeados y devolvemos el id de ingrediente
                            for (int u=0;u<linea4.getChildCount();u++){

                                //Buscamos cada checkbox hasta encontrar el checkeado
                                if (linea4.getChildAt(u) instanceof CheckBox){
                                    CheckBox check = (CheckBox) linea4.getChildAt(u);

                                    if (check.isChecked()){
                                        //El checkbox esta checkeado
                                        String id_ingrediente = check.getTag().toString();
                                        //obtener el id_tipo_ingrediente
                                        String id_tipo_ingre=null;
                                        for (String[] tmpstring:ingrediente_tipo){
                                            if (tmpstring[0].equals(id_ingrediente)){
                                                id_tipo_ingre = tmpstring[0];
                                                break;
                                            }
                                        }

                                        String ingre_query = "insert into producto_orden values (null,(select @lastid),"+id_articulo+","+id_ingrediente+","+id_tipo_ingre+",null)";
                                        lista_query_ingredientes.add(ingre_query);
                                       // lista_ingredientes.add(id_ingrediente);
                                    }
                                }

                            }
                        }

                        //Objetos incluidos y extras

                        LinearLayout linea5 = (LinearLayout) linea2.getChildAt(2);

                        for(int o=0; o<linea5.getChildCount();o++ ){

                            if (linea5.getChildAt(o) instanceof CheckBox){
                                CheckBox check = (CheckBox) linea5.getChildAt(o);

                                if (check.isChecked() ){
                                    //El checkbox esta checkeado
                                    String id_ingrediente = check.getTag().toString();
                                    //obtener el id_tipo_ingrediente
                                    String id_tipo_ingre=null;
                                    for (String[] tmpstring:ingrediente_tipo){
                                        if (tmpstring[0].equals(id_ingrediente)){
                                            id_tipo_ingre = tmpstring[0];
                                            break;
                                        }
                                    }

                                    String ingre_query = "insert into producto_orden values (null,(select @lastid),"+id_articulo+","+id_ingrediente+","+id_tipo_ingre+",null)";
                                    lista_query_ingredientes.add(ingre_query);

                                    Double precio_ingre = getPrice(id_ingrediente,"ingrediente",false);
                                    subtotal+=precio_ingre;
                                    total+=precio_ingre;
                                }
                            }
                        }


                    }


                }
            }
        }
        String set_variable ="set @lastid=(select max(id) from orden);";
        lista_query_ingredientes.add(0,set_variable);
        ORDER_QUERY = "insert into orden values(null,"+ID_OFERTA+","+ID_NEGOCIO+",null,"+subtotal+","+total+",'activa')";
        lista_query_ingredientes.add(0,ORDER_QUERY);
        updateLowerBar(subtotal, total);

    }
}
