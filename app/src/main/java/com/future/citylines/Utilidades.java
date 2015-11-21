package com.future.citylines;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by future on 1/09/15.
 */
public class Utilidades {

    public Toast toast(Context ctx, String texto){
        Toast result =  Toast.makeText(ctx, texto,
                Toast.LENGTH_LONG);

        return result;
    }

    public Dialog loadingDialog (Context ctx, String mensaje){
        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LinearLayout linea = new LinearLayout(ctx);
        LinearLayout.LayoutParams paramslinea = new LinearLayout.LayoutParams(400, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramslinea.setMargins(15,15,15,15);
        linea.setOrientation(LinearLayout.VERTICAL);
        linea.setLayoutParams(paramslinea);
        linea.setPadding(20, 20, 20, 20);
        linea.setGravity(Gravity.CENTER);

        TextView texto = new TextView(ctx);
        texto.setLayoutParams(paramslinea);
        texto.setTextColor(Color.BLUE);
        texto.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        texto.setText(mensaje);
        texto.setPadding(5, 20, 5, 20);
        texto.setTextSize(24);

        linea.setBackgroundResource(R.drawable.rounded);

        linea.addView(spinner(ctx));
        linea.addView(texto);

        dialog.setContentView(linea);

        return dialog;
    }

    public ProgressBar spinner (Context ctx){
        ProgressBar mSpinner = new ProgressBar(ctx);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 3, 10, 3);
        mSpinner.setLayoutParams(params);
        mSpinner.setIndeterminate(true);
        return  mSpinner;
    }

    public int screenSizeY(Context ctx) {
        int sizeY = 0;
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        sizeY = display.getHeight();


        return sizeY;
    }

    public int screenSizeX(Context ctx) {
        int sizeX = 0;
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        sizeX = display.getWidth();


        return sizeX;
    }
    public Bitmap getBitmap(String imageUrl) {


        Bitmap bitmap=null;

        do {
            try {
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                bitmap = BitmapFactory.decodeStream((InputStream) new java.net.URL(imageUrl).getContent(), null, o);
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize = 2;
                try {
                    bitmap = BitmapFactory.decodeStream((InputStream) new java.net.URL(imageUrl).getContent(), null, o2);
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        } while (bitmap == null);

        return bitmap;


    }
    public String getSHA512(String texto) throws IOException {

        String PATHTOFILE = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        File mydir = new File(PATHTOFILE);
        mydir.mkdir();
        File myfile = new File(PATHTOFILE+"/citylines.log");
        myfile.createNewFile();
        FileInputStream fis = new FileInputStream(myfile);

        byte[] dataBytes = texto.getBytes();

        int nread = 0;
        while ((nread = fis.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nread);
        };
        byte[] mdbytes = md.digest();

        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mdbytes.length; i++) {
            sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }


        return sb.toString();
    }


}
