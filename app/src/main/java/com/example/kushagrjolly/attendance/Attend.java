package com.example.kushagrjolly.attendance;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

/**
 * Created by Kushagr Jolly on 01-Jun-15.
 */
public class Attend extends Activity implements AdapterView.OnItemSelectedListener {
    ProgressDialog prgDialog;
    private static String SOAP_ACTION1 = "http://pack1/names";
    private static String NAMESPACE = "http://pack1/";
    private static String METHOD_NAME1 = "names";
    private static String URL = "http://192.168.1.109:8080/pgs/test?wsdl";
    ArrayList<String> resp=new ArrayList<>();
    ArrayList<String> sname=new ArrayList<>();
    private Spinner spinner;
    private Button b;
    String courseName;
    ListView listView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attend);
        prgDialog = new ProgressDialog(this);
        // Set Cancelable as False
        prgDialog.setCancelable(false);
        Intent i=getIntent();
        resp=i.getStringArrayListExtra("resp");
        for(int j=0;j<resp.size();j++) {
                Log.d("resp", resp.get(j));
        }
        spinner = (Spinner) findViewById(R.id.spinner);
        listView = (ListView) findViewById(R.id.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,resp);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        b=(Button)findViewById(R.id.button2);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment newFragment = new DatePickerFragment();

                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        courseName=spinner.getSelectedItem().toString();
        //Log.d("couse",courseName);
        names();

    }
    public void names() {
        new AsyncTask<Void, Void, String>() {

            protected void onPreExecute() {
                //prgDialog.setMessage("Uploading data, please wait...");
                prgDialog.show();
            };

            @Override
            protected String doInBackground(Void... params) {
                SoapObject request1 = new SoapObject(NAMESPACE, METHOD_NAME1);
                request1.addProperty("courseName",courseName);

                //Declare the version of the SOAP request
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

                envelope.setOutputSoapObject(request1);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                //this is the actual part that will call the webservice
                try {
                    androidHttpTransport.call(SOAP_ACTION1, envelope);
                } catch (Exception e)  {
                    e.printStackTrace();
                }
                try {


                    // Get the SoapResult from the envelope body.
                    SoapObject result1 = (SoapObject)envelope.bodyIn;

                    if(result1 != null)
                    {
                        sname.clear();

                        Log.d("count", String.valueOf(result1.getPropertyCount()));
                        //Get the first property and change the label text
                        for(int i=0;i<result1.getPropertyCount();i++) {

                            sname.add(result1.getProperty(i).toString());

                            Log.d("student names", sname.get(i));
                        }
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
                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1, android.R.id.text1, sname);


                // Assign adapter to ListView
                listView.setAdapter(adapter1);

                // ListView Item Click Listener
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

                        // ListView Clicked item index
                        int itemPosition     = position;

                        // ListView Clicked item value
                        String  itemValue    = (String) listView.getItemAtPosition(position);

                        // Show Alert
                        Toast.makeText(getApplicationContext(),
                                "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
                                .show();

                    }

                });
            }
        }.execute(null, null, null);
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
