package com.example.kushagrjolly.attendance;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by Kushagr Jolly on 30-May-15.
 */
public class MainActivity extends Activity{
    EditText user,pass;
    ProgressDialog prgDialog;
    private static String SOAP_ACTION1 = "http://pack1/hello";
    private static String NAMESPACE = "http://pack1/";
    private static String METHOD_NAME1 = "hello";
    private static String URL = "http://192.168.1.109:8080/pgs/test?wsdl";
    private String resp,username,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user=(EditText)findViewById(R.id.username);
        pass=(EditText)findViewById(R.id.password);
        prgDialog = new ProgressDialog(this);
        // Set Cancelable as False
        prgDialog.setCancelable(false);

    }

    public void login(View view){
        username=user.getText().toString();
        password=pass.getText().toString();
        Log.d("user,pass",username+password);
        if(user.getText().toString()!=null && pass.getText().toString()!=null){
            prgDialog.setMessage("Uploading data, please wait...");
            prgDialog.show();
            checklogin();
        }
        else if(user.getText().toString()==""){
            Toast.makeText(getApplicationContext(),"Please enter Username",Toast.LENGTH_SHORT).show();
        }
        else if(pass.getText().toString()==null){
            Toast.makeText(getApplicationContext(),"Please enter Password",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(),"Wrong Input!!!",Toast.LENGTH_LONG).show();
        }
    }
    public void checklogin() {
        new AsyncTask<Void, Void, String>() {

            protected void onPreExecute() {

            };

            @Override
            protected String doInBackground(Void... params) {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
                request.addProperty("user", username);
                request.addProperty("pass",password);

                //Declare the version of the SOAP request
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                //this is the actual part that will call the webservice
                try {
                    androidHttpTransport.call(SOAP_ACTION1, envelope);
                } catch (Exception e)  {
                    e.printStackTrace();
                }
                try {


                    // Get the SoapResult from the envelope body.
                    SoapObject result = (SoapObject)envelope.bodyIn;

                    if(result != null)
                    {
                        //Get the first property and change the label text
                        resp=result.getProperty(0).toString();
                        Log.d("rsult", resp);
                    }
                    else
                    {
//                        Toast.makeText(getApplicationContext(), "No Response",Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {

                prgDialog.hide();
                //launchActivity(true);
            }
        }.execute(null, null, null);
    }
}
