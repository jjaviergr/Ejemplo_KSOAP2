package com.example.pc.pruebaws;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class MainActivity extends AppCompatActivity {
    private final String NAMESPACE = "http://iesaguadulce.16mb.com";
    private final String URL = "http://iesaguadulce.16mb.com/recetas/servicio.php?wsdl";
    private final String SOAP_ACTION_PREFIX = "/";
    private final String METHOD_NAME_1 = "obtener_nombre_receta";
    private final String METHOD_NAME_2 = "obtener_tipo_receta";
    private final String METHOD_NAME_3 = "obtener_preparacion_receta";
    private final String METHOD_NAME_4 = "obtener_presentacion_receta";
    private String TAG = "Depuración:";
    private static int numero_receta;
    private static String nombre_receta, tipo_receta, preparacion_receta, presentacion_receta;

    Button b;
    TextView tv, tv1,tv2,tv3;
    EditText et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Numero de receta Edit Control
        et = (EditText) findViewById(R.id.editText1);
        //Nombre de la receta
        tv = (TextView) findViewById(R.id.tv_result);
        //tipo de la receta
        tv1 = (TextView) findViewById(R.id.tv_tipo);
        //Nombre de la receta
        tv2 = (TextView) findViewById(R.id.tv_preparacion);
        //Nombre de la receta
        tv3 = (TextView) findViewById(R.id.tv_presentacion);
        //Button to trigger web service invocation
        b = (Button) findViewById(R.id.button1);
        //Button Click Listener
        b.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //Check if Celcius text control is not empty
                if (et.getText().length() != 0 && et.getText().toString() != "") {
                    //Get the text control value
                    numero_receta = Integer.parseInt(et.getText().toString());
                    //Create instance for AsyncCallWS
                    AsyncCallWS task = new AsyncCallWS();
                    //Call execute
                    task.execute();
                    //If text control is empty
                } else {
                    tv.setText("Por favor introduce número de receta");
                }
            }
        });
    }

    public String peticiones ( SoapSerializationEnvelope envelope, String metodo, String propiedad, int num_receta){
        String cadena;
        //Create request
        SoapObject request = new SoapObject(NAMESPACE, metodo);
        //bodyOut is the body object to be sent out with this envelope
        //Add the property to request object
        request.addProperty(propiedad, num_receta);
        envelope.bodyOut = request;
        HttpTransportSE transport = new HttpTransportSE(URL);
        try {
            transport.call(NAMESPACE + SOAP_ACTION_PREFIX + metodo, envelope);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //bodyIn is the body object received with this envelope
        if (envelope.bodyIn != null) {
            //getProperty() Returns a specific property at a certain index.
            cadena=((SoapObject) envelope.bodyIn).getProperty(0).toString();
            // nombre_receta= envelope.bodyIn.toString();
        }
        else cadena="Error";
        return cadena;
    }

    public void getReceta(int numero_receta) {
        try {
            // SoapEnvelop.VER11 is SOAP Version 1.1 constant
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);


            nombre_receta= peticiones(envelope,METHOD_NAME_1,"cod_receta",numero_receta);
            tipo_receta= peticiones(envelope,METHOD_NAME_2,"cod_receta",numero_receta);
            preparacion_receta= peticiones(envelope,METHOD_NAME_3,"cod_receta",numero_receta);
            presentacion_receta= peticiones(envelope,METHOD_NAME_4,"cod_receta",numero_receta);

        } catch (Exception e) {
            e.printStackTrace();
            nombre_receta = e.getMessage();
            tipo_receta="";
            preparacion_receta="";
            presentacion_receta="";
        }
    }

    private class AsyncCallWS extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            Log.i(TAG, "doInBackground");
            getReceta(numero_receta);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");
            tv.setText(nombre_receta);
            tv1.setText(tipo_receta);
            tv2.setText(preparacion_receta);
            tv3.setText(presentacion_receta);
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            tv.setText(String.format("Obteniendo...%d", numero_receta));
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            Log.i(TAG, "onProgressUpdate");
        }

    }

}
