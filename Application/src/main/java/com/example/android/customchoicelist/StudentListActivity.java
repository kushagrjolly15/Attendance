/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.customchoicelist;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * This sample demonstrates how to create custom single- or multi-choice
 * {@link android.widget.ListView} UIs. The most interesting bits are in
 * the <code>res/layout/</code> directory of this sample.
 */
public class StudentListActivity extends ListActivity implements AdapterView.OnItemSelectedListener{

    static Button b;
    Spinner spinner;
    String[] values;
    ProgressDialog prgDialog;
    private static String SOAP_ACTION1 = "http://pack1/names";
    private static String NAMESPACE = "http://pack1/";
    private static String METHOD_NAME1 = "names";
    private static String URL = "http://172.16.6.55:8080/pgs/test?wsdl";
    ArrayList<String> resp=new ArrayList<>();
    ArrayList<String> sname;
    ArrayList<String> atten;
    String userType;
    static int num =0;
    boolean mchecked=false;
    boolean[] isPresent;

   private String courseName;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_main);

        prgDialog = new ProgressDialog(this);
        // Set Cancelable as False
        prgDialog.setCancelable(false);
        Intent i=getIntent();
        resp=i.getStringArrayListExtra("resp");
        for(int j=0;j<resp.size();j++) {
            Log.d("resp", resp.get(j));
        }
        userType=resp.get(resp.size()-1);
        resp.remove(resp.size()-1);
        Log.d("UserType =", userType);

        b=(Button)findViewById(R.id.button);
        if(userType.contentEquals("gu") || userType.contentEquals("ft")){
            Calendar c = Calendar.getInstance();
            String d = c.get(Calendar.DATE)+"/"+c.get(Calendar.MONTH)+"/"+c.get(Calendar.YEAR);
            b.setText(d);
        }else if(userType.contentEquals("pr") || userType.contentEquals("prhd")){


            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogFragment newFragment = new DatePickerFragment();

                    newFragment.show(getFragmentManager(), "datePicker");
                }
            });
        }
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(StudentListActivity.this,
                android.R.layout.simple_spinner_item,resp);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);



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
                        sname=new ArrayList<>();
                        for (int i=0;i<sname.size();i++)
                            sname.remove(i);
                        atten=new ArrayList<String>();
                        Log.d("count", String.valueOf(result1.getPropertyCount()));
                        //Get the first property and change the label text
                        for(int i=0;i<result1.getPropertyCount();i++) {

                            sname.add(result1.getProperty(i).toString());
                            atten.add("absent");
                            Log.d("student names", sname.get(i));
                        }
                        isPresent= new boolean[sname.size()];
                        for (int i=0;i<isPresent.length;i++)
                            isPresent[i]=false;
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
                values= new String[sname.size()];
                for(int i=0;i<values.length;i++) {
                    values[i] = sname.get(i);
                    Log.d("VAL",values[i]);
                }


                setListAdapter(new MyAdapter());
                Log.d("mchecked", String.valueOf(mchecked));
            }

        }.execute(null, null, null);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * A simple array adapter that creates a list of cheeses.
     */
    private class MyAdapter extends BaseAdapter {

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
        public View getView(final int position, View convertView, ViewGroup container) {
          //  atten.set(position,"absent");
            //num++;

            ImageView imageView = (ImageView)findViewById(R.id.img);
            /*imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isPresent[position])
                        isPresent[position]=false;
                    else
                        isPresent[position]=true;
                }
            });*/
            if (convertView == null) {
               // Log.d("Attendance",sname.get(position)+" "+atten.get(position));

                convertView = getLayoutInflater().inflate(R.layout.list_item, container, mchecked);
               // convertView.is
            }/*
            else if(convertView!=null){
                atten.remove(position);
                atten.add("present");
                Log.d("present", atten.get(position));

            }*/

                    ((TextView) convertView.findViewById(android.R.id.text1))
                    .setText(getItem(position));

           // Log.d("isPresent",sname.get(position)+isPresent[position]);
            return convertView;
        }
    }
}
