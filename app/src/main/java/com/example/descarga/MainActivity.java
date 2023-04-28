package com.example.descarga;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageView = (ImageView) findViewById(R.id.image_descarga);
        imageView.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        Log.e("paso por aqui", "Onclick");
        switch (v.getId()){
            case R.id.image_descarga:
                descargarPDF();

                break;
        }
    }

    void  descargarPDF (){

        String urlADescargar = "https://gfgrobotics.com/wp-content/uploads/2023/04/lanzador-coches.pdf";

        ProgressDialog barradeprogreso = new ProgressDialog(this);
        barradeprogreso.setIndeterminate(true);
        barradeprogreso.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        barradeprogreso.setMessage("Descargando ...");

        new DescararPDFAsynctask(barradeprogreso).execute(urlADescargar);

    }

    class DescararPDFAsynctask extends AsyncTask<String,Integer,String>{
        ProgressDialog barradeprogreso;
        DescararPDFAsynctask(ProgressDialog barradeprogreso){
            this.barradeprogreso = barradeprogreso;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            barradeprogreso.show();
        }

        @Override
        protected String doInBackground(String... urlPDF) {
            String urlADescargar =  urlPDF[0];

            HttpURLConnection conexion = null;
            InputStream input = null;
            OutputStream output = null;

            try {
                URL url = new URL(urlADescargar);
                conexion =(HttpURLConnection) url.openConnection();
                Log.e("paso por aqui", "antes de conexion");
                conexion.connect();
                Log.e("paso por aqui", "conexion");
                String rutaFicheroGuardado = getFilesDir() + "/arana.pdf";
                int tamanoFichero = conexion.getContentLength();

                if (conexion.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Coneción Fallida";
                }

                input = conexion.getInputStream();
                output = new FileOutputStream(rutaFicheroGuardado);
                //PDFView pdfView = findViewById(R.id.pdfView);


                byte[] data = new byte[1024];
                int total = 0;

                int count = 0;
                while ((count = input.read(data)) != -1){
                    output.write(data,0,count);
                    total +=count;
                    publishProgress((int) (total*100 /tamanoFichero));
                }

                 } catch (MalformedURLException e) {
                e.printStackTrace();
                    return "Error: " + e.getMessage();
                    } catch (IOException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
                     }
            finally {
                try {
                if (input != null) input.close();
                if (output != null) output.close();
                if (conexion != null) conexion.disconnect();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }

                        return "Se realizo la descarga";

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            barradeprogreso.setIndeterminate(false);
            barradeprogreso.setMax(100);
            barradeprogreso.setProgress(values[0]);

        }


        @Override
        protected void onPostExecute(String  mensaje) {
            super.onPostExecute(mensaje);
            barradeprogreso.dismiss();
            Toast.makeText(MainActivity.this, mensaje, Toast.LENGTH_SHORT).show();
        }


    }

}