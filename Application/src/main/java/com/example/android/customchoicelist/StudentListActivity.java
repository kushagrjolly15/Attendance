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
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
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
    ProgressDialog prgDialog;
    private static String SOAP_ACTION1 = "http://pack1/names";
    private static String NAMESPACE = "http://pack1/";
    private static String METHOD_NAME1 = "names";
    private static String URL = "http://192.168.1.105:8080/pgs/test?wsdl";
    ArrayList<String> resp=new ArrayList<>();
    ArrayList<String> sname;
    ArrayList<String> atten;
    String userType;
    boolean[] isPresent;
    MyCustomAdapter dataAdapter = null;
    ArrayList<String> studentsPresent = new ArrayList<>();
    ArrayList<String> studentsAbsent = new ArrayList<>();


    private String courseName;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xyz);

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

    public void  displayListView(){
        ArrayList<Student> studentList = new ArrayList<Student>();
        for (int i = 0; i <sname.size(); i++) {

            Student student = new Student(sname.get(i),false);
            studentList.add(student);
        }
        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(this,
                R.layout.student_info,studentList);
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
    }
    private class MyCustomAdapter extends ArrayAdapter<Student> {

        private ArrayList<Student> studentList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<Student> studentList) {
            super(context, textViewResourceId, studentList);
            this.studentList = new ArrayList<Student>();
            this.studentList.addAll(studentList);
        }

        private class ViewHolder {
            TextView studentName;
            CheckBox name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;


            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.student_info, null);

                holder = new ViewHolder();
                holder.studentName = (TextView) convertView.findViewById(R.id.code);
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        Student student  = (Student) cb.getTag();
                        student.setSelected(cb.isChecked());
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            Student student = studentList.get(position);
            Log.d("Name",student.toString());
            holder.name.setText(student.getName());
            holder.name.setChecked(student.isSelected());
            holder.name.setTag(student);

            return convertView;

        }

    }

    private void checkButtonClick() {


        Button myButton = (Button) findViewById(R.id.findSelected);
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");

                ArrayList<Student> studentList = dataAdapter.studentList;
                for(int i=0;i<studentList.size();i++){
                    Student student = studentList.get(i);
                    if(student.isSelected()){
                        studentsPresent.add(student.getName());
                        responseText.append("\n" + student.getName() + " Present");
                    }
                    else{
                        studentsAbsent.add(student.getName());
                        responseText.append("\n" + student.getName()+" Absent");}
                }
                upload();
                Toast.makeText(getApplicationContext(),
                        responseText, Toast.LENGTH_LONG).show();

            }
        });

    }
    public void upload() {
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
                            Log.d("cxz",sname.get(i));
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

                //Student List
                displayListView();

                checkButtonClick();

            }

        }.execute(null, null, null);
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
                            Log.d("cxz",sname.get(i));
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

                //Student List
                displayListView();

                checkButtonClick();

            }

        }.execute(null, null, null);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
