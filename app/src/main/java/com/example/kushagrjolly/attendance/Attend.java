package com.example.kushagrjolly.attendance;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

/**
 * Created by Kushagr Jolly on 01-Jun-15.
 */
public class Attend extends ListActivity implements AdapterView.OnItemSelectedListener {
    ProgressDialog prgDialog;
    private static String SOAP_ACTION1 = "http://pack1/names";
    private static String NAMESPACE = "http://pack1/";
    private static String METHOD_NAME1 = "names";
    private static String URL = "http://172.16.6.87:8080/pgs/test?wsdl";
    ArrayList<String> resp=new ArrayList<>();
    ArrayList<String> sname=new ArrayList<>();
    private Spinner spinner;
    private static Button b,b1;
    String courseName;
    ListView listView ;
    String[] values ={"One","Two","Three","Four","Five"};
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
        listView = (ListView) findViewById(R.id.list2);
        ArrayAdapter<String> ladapter = new ArrayAdapter<String>(this,
                android.R.layout.activity_list_item,values);
        listView.setAdapter(ladapter);
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

    public static void getDate(String d)
    {
        b.setText(d);

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

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {

                prgDialog.hide();

            }
        }.execute(null, null, null);
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class MyAdapter implements ListAdapter {


        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getCount() {
            return values.length;
        }

        @Override
        public String getItem(int position) {
            return values[position];
        }

        @Override
        public long getItemId(int position) {
            return values[position].hashCode();
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.activity_item_list, container, false);
            }

            ((TextView) convertView.findViewById(android.R.id.text1))
                    .setText(getItem(position));
            return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }
    }
}
