package com.future.citylines;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.TranslateAnimation;
import android.widget.ActionMenuView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ocpsoft.pretty.time.PrettyTime;

import org.w3c.dom.Text;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.transform.Result;

/**
 * Created by future on 3/09/15.
 */
public class Ofertas extends Fragment implements View.OnTouchListener {
    private static final String ARG_SECTION_NUMBER = "section_number";
    Context ctx;
    String USER_ID;
    Database db;
    LinearLayout lineaofertas,linearevistas,lineapreciorevistas;
    List<LinearLayout> revistas;
    LinearLayout.LayoutParams oferparams;
    RelativeLayout.LayoutParams relativeparams;
    Utilidades util;
    PrettyTime prettytime;
    List <RelativeLayout> listaofertas;
    List<View> listarevistas;
    List <TextView> listatiempos,listatiempos2;
    List<RelativeLayout> listarelativa = new ArrayList<RelativeLayout>();
    List<TextView> listatext= new ArrayList<TextView>();
    LinearLayout lineaimagen,lineatime;
    List<String> listaidrevista,listaidoferta;
    static float sX2,sY2,fX2,fy2;
    EditText ofertabuscar;
    ImageView logo;

    public static Ofertas newInstance(int sectionNumber) {
        Ofertas fragment = new Ofertas();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    public Ofertas(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.ofertas, container, false);

        ctx = getActivity().getApplicationContext();
        Intent intent = getActivity().getIntent();
        USER_ID = intent.getStringExtra("id");
        db= new Database();
        lineaofertas = (LinearLayout) rootView.findViewById(R.id.lineaofertas);
        linearevistas = (LinearLayout) rootView.findViewById(R.id.linearevistas);
     //   linearevistas.setOnTouchListener(this);
        lineapreciorevistas = (LinearLayout) rootView.findViewById(R.id.lineatiemporevista);
        ofertabuscar = (EditText) rootView.findViewById(R.id.ofertabuscar);
        logo = (ImageView) rootView.findViewById(R.id.logo);

        //Instanciamos clase Utilidades
        util=new Utilidades();
        prettytime = new PrettyTime();
        //Las dimensiones de las ofertas
        oferparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,util.screenSizeY(ctx)/3);
        relativeparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, util.screenSizeY(ctx)/12);
        relativeparams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        ofertabuscar.setLayoutParams(new LinearLayout.LayoutParams(util.screenSizeX(ctx) - 140, 35));


        listarevistas = new ArrayList<View>();
        listatiempos = new ArrayList<TextView>();
        listaofertas = new ArrayList<RelativeLayout>();
        listatiempos2 = new ArrayList<TextView>();
        listaidrevista = new ArrayList<String>();
        listaidoferta = new ArrayList<String>();
        //Iniciamos la lista donde van a estar guardadas las revistas


      //  ResultSet resultset = db.executeQuery("SELECT * FROM oferta where fecha_inicio < NOW() and fecha_finalizacion > NOW();");

        //Nuevo Thread para obtener las revistas
        new Thread(new Runnable() {
    @Override
    public void run() {
        listarRevistas();
    }
}).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
           //     listarOfertas(1);
            }
        }).start();


        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent perfilintent = new Intent(Ofertas.this,Perfil.class);
               // perfilintent.putExtra("userid",USER_ID);
               // startActivity(perfilintent);
            }
        });

        return rootView;
    }


    //Metodo para obtener y listar la informacion de las Revistas
    public void listarRevistas(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        final String revista_query="Select * from revista where fecha_final > '"+currentDateandTime+"'";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                ResultSet revista_result = db.executeQuery(revista_query);
               // revista_result.beforeFirst();
                    OfertasYRevistas(1,revista_result);
                    revista_result.beforeFirst();

                    while(revista_result.next()) {
                        listarOfertas(Integer.parseInt(revista_result.getString("id")));
                        break;
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }).start();





    }

    //Metodo para obtener Ofertas
    public void listarOfertas(int id){

        final String oferta_query="Select * from oferta where id in (select oferta_id from revista_oferta where revista_id = "+id+")";
        new Thread(new Runnable() {
            @Override
            public void run() {
                ResultSet oferta_result = db.executeQuery(oferta_query);
                OfertasYRevistas(2,oferta_result);
            }
        }).start();

    }


    public void OfertasYRevistas(final int tipo, final ResultSet resultset){
        //tipo 1 = revistas
        //tipo 2 = ofertas

        String urlimage = null;
lineaofertas.post(new Runnable() {
    @Override
    public void run() {
        lineaofertas.removeAllViews();
    }
});

        final List<String> columnas = new ArrayList<String>();
        listatext.clear();
        listarelativa.clear();


        switch(tipo){
            case 1:

                urlimage = "http://23.251.149.115/images/revistas/";
                columnas.add("id");
                columnas.add("nombre_revista");
                columnas.add("fecha_inicio");
                columnas.add("fecha_final");
                columnas.add("img_revista");
               break;
            case 2:

                urlimage = "http://23.251.149.115/images/ofertas/";
                columnas.add("id");
                columnas.add("nombre_oferta");
                columnas.add("fecha_inicio");
                columnas.add("fecha_finalizacion");
                columnas.add("imagen_oferta");

                //nuevos
                columnas.add("subtotal_oferta");
                columnas.add("total_oferta");

        }




        try {
            int cuenta=0;
            final List<View> listaviews = new ArrayList<View>();
            while(resultset.next()){
              //  String id = resultset.getString("id");
              //  String nombre = resultset.getString("nombre_revista");
              //  String inicio = resultset.getString("fecha_inicio");
              //  final String fin = resultset.getString("fecha_final");
              //  String imagen = resultset.getString("img_revista");

                final List<String> listatmp = new ArrayList<String>();
                for (int i=0; i<columnas.size();i++){
                    listatmp.add(columnas.get(i));
                }



                LinearLayout oferta_line = new LinearLayout(ctx);
                oferta_line.setLayoutParams(oferparams);
                oferta_line.setOrientation(LinearLayout.VERTICAL);
                oferta_line.setGravity(Gravity.RIGHT);


                final RelativeLayout cadaoferta = new RelativeLayout(ctx);
                cadaoferta.setLayoutParams(oferparams);

                Bitmap imagen_revista = util.getBitmap(urlimage+resultset.getString(listatmp.get(4))); ///4 = imagen
                Drawable drawable_revista =new BitmapDrawable(ctx.getResources(),imagen_revista);
                cadaoferta.setBackground(drawable_revista);

                LinearLayout lineafondo = new LinearLayout(ctx);
                lineafondo.setLayoutParams(relativeparams);
                lineafondo.setBackground(ctx.getResources().getDrawable(R.drawable.gradient_negro));
                lineafondo.setOrientation(LinearLayout.HORIZONTAL);


                TextView titulo_revista = new TextView(ctx);
                titulo_revista.setLayoutParams(new LinearLayout.LayoutParams((2 * util.screenSizeX(ctx)) / 3, ViewGroup.LayoutParams.WRAP_CONTENT));
                titulo_revista.setTextColor(Color.WHITE);
                titulo_revista.setTextSize(24);
                titulo_revista.setText(resultset.getString(listatmp.get(1))); //1 = nombre

                TextView boton_revista = new TextView(ctx);
                boton_revista.setLayoutParams(new LinearLayout.LayoutParams(util.screenSizeX(ctx) / 3, 100));
                if (tipo == 1) {
                    boton_revista.setText("DESCUBRE");
                }else if (tipo == 2){
                    DecimalFormat dec = new DecimalFormat("#.000");
                    boton_revista.setText("$ "+dec.format(resultset.getDouble(columnas.get(6))/1000));
                }
                boton_revista.setBackground(ctx.getResources().getDrawable(R.drawable.rounded_button_azul));
                boton_revista.setPadding(5, 10, 5, 10);
                boton_revista.setGravity(Gravity.CENTER);

                lineafondo.addView(titulo_revista);
                lineafondo.addView(boton_revista);

                cadaoferta.addView(lineafondo);



                final TextView tiempo = new TextView(ctx);
                LinearLayout.LayoutParams tiempoparams = new LinearLayout.LayoutParams(util.screenSizeX(ctx) / 4, ViewGroup.LayoutParams.WRAP_CONTENT);

                tiempoparams.setMargins((3*util.screenSizeX(ctx))/4,0,0,0);
                tiempo.setLayoutParams(tiempoparams);
                tiempo.setTextColor(Color.DKGRAY);
                tiempo.setBackground(ctx.getResources().getDrawable(R.drawable.rounded_button_verde));
                tiempo.setPadding(15, 10, 0, 20);
                tiempo.setTextSize(10);

                //Usamos pretty time para calcular el tiempo restante en letras
                final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                final Date date = format.parse(resultset.getString(listatmp.get(3)));
                final String pretty_dated = prettytime.format(date);
                // cadaoferta.addView(tiempo);
                final int finalcuenta = cuenta;

                    String rs = resultset.getString(listatmp.get(0));

                    if (tipo == 2) {
                        listaidoferta.add(rs);
                    }else if (tipo==1){
                        listaidrevista.add(rs);
                    }


                final String id = resultset.getString(listatmp.get(0));
                linearevistas.post(new Runnable() {
                    @Override
                    public void run() {
                        tiempo.setText(pretty_dated);

                        if (tipo == 1) {
                            //Agregamos el tiempo restante a la lista de tiempos
                            listatiempos.add(tiempo);
                            lineapreciorevistas.addView(listatiempos.get(finalcuenta));

                            //Agregamos la oferta completa a la lista de ofertas
                            listarevistas.add(cadaoferta);
                            //linearevistas.addView(listarevistas.get(finalcuenta));




                        }else if(tipo == 2 ){

                            try {
                            //Agregamos la oferta completa a la lista de ofertas
                                cadaoferta.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String id = null;
                                        id = listaidoferta.get(Integer.parseInt(v.getTag().toString()));
                                       /*
                                        Intent intentdetalle = new Intent(Ofertas.this, DetalleOferta.class);
                                       intentdetalle.putExtra("id", id);
                                       startActivity(intentdetalle);
*/
                                        DetalleOferta detalleoferta = new DetalleOferta();

                                                ((Navigation) getActivity()).getFragmentManager().beginTransaction()
                                                .replace(R.id.container, detalleoferta,null)
                                                .addToBackStack(null)
                                                .commit();
                                    }
                                });
                                cadaoferta.setTag(finalcuenta);
                                listaofertas.add(cadaoferta);
                                lineaofertas.addView(listaofertas.get(finalcuenta));
                            //Agregamos el tiempo restante a la lista de tiempos
                                listatiempos2.add(tiempo);

                             ///  lineaofertas.addView();
                               listaviews.add(listatiempos2.get(finalcuenta));




                            } catch (NullPointerException e){

                            }catch (IllegalStateException e){
                                e.printStackTrace();
                            }
                        }
                    }
                });

                cuenta++;

            }
            linearevistas.post(new Runnable() {
                @Override
                public void run() {
                    ViewPager viewpager = new ViewPager(ctx);
                    viewpager.setLayoutParams(new LinearLayout.LayoutParams(util.screenSizeX(ctx),util.screenSizeY(ctx)/3));
                    viewpager.setAdapter(new PagerAdapter(ctx,listarevistas));
                    linearevistas.addView(viewpager);
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }






    }


    public void setCronometro(TextView textView,String texto_pre,String texto_pos){



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

                if (difX > 100 || difX < -100) {
                    //El usuario hizo swipe
                    String direccion = null;
                    if (difX > 100) {
                        //>El swipe fue de derecha a izquierda
                        direccion = "left";
                        int nLayouts = listarevistas.size();
                        int tmpcuenta=0;
                        for (int n = 0; n < nLayouts; n++) {

                            if (listarevistas.get(n).getVisibility() == View.VISIBLE){
                                tmpcuenta = n;
                                listarevistas.get(n).setVisibility(View.GONE);
                                listatiempos.get(n).setVisibility(View.GONE);
                                break;
                            }
                            listarevistas.get(n).setVisibility(View.GONE);
                            listatiempos.get(n).setVisibility(View.GONE);


                        }
                        TranslateAnimation animate = new TranslateAnimation(view.getWidth(), 0, 0, 0);
                        animate.setDuration(200);
                        //  animate.setFillAfter(true);


                        if (tmpcuenta - 1 >= 0) {
                            listarevistas.get(tmpcuenta - 1).startAnimation(animate);
                            listarevistas.get(tmpcuenta - 1).setVisibility(View.VISIBLE);
                            listatiempos.get(tmpcuenta - 1).startAnimation(animate);
                            listatiempos.get(tmpcuenta - 1).setVisibility(View.VISIBLE);

                            listarOfertas(Integer.parseInt(listaidrevista.get(tmpcuenta - 1)));
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

                            listarevistas.get(nLayouts - 1).startAnimation(animate);
                            listarevistas.get(nLayouts - 1).setVisibility(View.VISIBLE);
                            listatiempos.get(nLayouts - 1).startAnimation(animate);
                            listatiempos.get(nLayouts - 1).setVisibility(View.VISIBLE);
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
                        int nLayouts = listarevistas.size();
                        int tmpcuenta = 0;
                        for (int n = 0; n < nLayouts; n++) {
                            if (listarevistas.get(n).getVisibility() == View.VISIBLE)
                                tmpcuenta = n;
                            // linearlayout[n].startAnimation(animate);
                            listarevistas.get(n).setVisibility(View.GONE);
                            listatiempos.get(n).setVisibility(View.GONE);
                        }
                        TranslateAnimation animate = new TranslateAnimation(-view.getWidth(), 0, 0, 0);
                        animate.setDuration(200);
                        //animate.setFillAfter(true);

                        if (tmpcuenta < nLayouts-1) {
                            listarevistas.get(tmpcuenta + 1).startAnimation(animate);
                            listarevistas.get(tmpcuenta + 1).setVisibility(View.VISIBLE);
                            listatiempos.get(tmpcuenta + 1).startAnimation(animate);
                            listatiempos.get(tmpcuenta + 1).setVisibility(View.VISIBLE);
                            listarOfertas(Integer.parseInt(listaidrevista.get(tmpcuenta + 1)));
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
                            listarevistas.get(0).startAnimation(animate);
                            listarevistas.get(0).setVisibility(View.VISIBLE);
                            listatiempos.get(0).startAnimation(animate);
                            listatiempos.get(0).setVisibility(View.VISIBLE);
                            listarOfertas(Integer.parseInt(listaidrevista.get(0)));
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
}
