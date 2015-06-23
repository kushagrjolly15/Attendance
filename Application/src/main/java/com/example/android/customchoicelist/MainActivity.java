package com.example.android.customchoicelist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
public class MainActivity extends Activity{
    EditText user,pass;
    ProgressDialog prgDialog;
    private static String SOAP_ACTION1 = "http://pack1/hello";
    private static String NAMESPACE = "http://pack1/";
    private static String METHOD_NAME1 = "hello";
    private static String URL = "http://192.168.3.12:8080/pgs/test?wsdl";
    private String username,password;
    private ArrayList<String> resp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        resp= new ArrayList<>();
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
        if(!user.getText().toString().isEmpty() && !pass.getText().toString().isEmpty()){
            prgDialog.setMessage("Uploading data, please wait...");
            prgDialog.show();
            checklogin();
        }
        else if(user.getText().toString().equalsIgnoreCase("")){
            Toast.makeText(getApplicationContext(),"Please enter Username",Toast.LENGTH_SHORT).show();
        }
        else if(pass.getText().toString().equalsIgnoreCase("")){
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
                        Log.d("count", String.valueOf(result.getPropertyCount()));
                        //Get the first property and change the label text
                        for(int i=0;i<result.getPropertyCount();i++) {

                            resp.add(result.getProperty(i).toString());
                            Log.d("rsult", resp.get(i));
                        }
                        launchActivity(true);
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

    private void launchActivity(boolean b) {
        Intent i= new Intent(this,IntermediateActivity.class);
        i.putExtra("resp",resp);
        startActivity(i);
    }
}
