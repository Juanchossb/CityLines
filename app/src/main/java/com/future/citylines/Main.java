package com.future.citylines;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Handler;

public class Main extends AppCompatActivity {

    EditText username,password;
    Button loginbutton;
    TextView errorlogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

final Database db =new Database();
        final Utilidades util = new Utilidades();
       // LinearLayout mainlinea = (LinearLayout) findViewById(R.id.mainlinea);
        username = (EditText) findViewById(R.id.user);
       // username.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        password = (EditText) findViewById(R.id.pass);
        //password.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        loginbutton = (Button) findViewById(R.id.botonlogin);
    errorlogin = (TextView) findViewById(R.id.errorlogin);
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final String user= username.getText().toString();
                final String pass = password.getText().toString();

                if (user.length()<1 || password.length()<1){
                    //Si algun campo fue dejado sin llenar
                    errorlogin.setText("Por Favor llene ambos campos");
                    errorlogin.setVisibility(View.VISIBLE);

                }else{
                    //Obtenemos la Base de datos y comparamos
                  final Dialog dialog = util.loadingDialog(Main.this,"Validando");
                    dialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ResultSet rs = db.executeQuery("select * from usuario where usuario = '"+user+"' and password = '"+pass+"'");

                            try {
                                if (rs != null) {
                                    while (rs.next()) {
                                        //Obtemos y guardamos el nombre de usuario registrado y la contrasena
                                        String cloudpass = rs.getString("password");
                                        String clouduser = rs.getString("usuario");
                                        String cloudid = rs.getString("id");

                                        if (user.equals(clouduser) && pass.equals(cloudpass)) {
                                            //Login fue exitoso, lanzar Intent y abrir ofertas
                                            Intent intent = new Intent(Main.this,Navigation.class);
                                            intent.putExtra("id",cloudid);
                                            startActivity(intent);

                                        }


                                    }
                                }else{
                                    errorlogin.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            errorlogin.setText("Su nombre de usuario y contrasena no concuerdan");
                                            errorlogin.setVisibility(View.VISIBLE);
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();




                }
            }
        });
/*
        new Thread(new Runnable() {
            @Override
            public void run() {

                ResultSet res = db.executeQuery("Select * from NEGOCIOS");
                try {
                    while(res.next()){
                        System.out.println("id: "+res.getInt("id")+" // name: "+res.getString("nombre"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
*/
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
