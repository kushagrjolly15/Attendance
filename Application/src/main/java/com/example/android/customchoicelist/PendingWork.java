package com.example.android.customchoicelist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;


public class PendingWork extends Activity {

    private static String SOAP_ACTION = "http://pack1/pending";
    private static String NAMESPACE = "http://pack1/";
    private static String METHOD_NAME = "pending";
    private static String URL = "http://172.16.6.123:8080/pgs/test?wsdl";
    Intent intent;
    String userType;
    MyCustomAdapter dataAdapter = null;
    String[] fields ={
            "Faculty Approval",
            "Guide Approval",
            "Member Approval",
            "Grades Sem I",
            "Grades Sem II",
            "Grades Sem III",
            "Guide Approval of PPW",
            "Advisory Committee Approval",
            "Guide Approval of ORW",
            "Guide Approval of Thesis",
    };
    private ProgressDialog prgDialog;
    ArrayList<String> pendinglist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pending_work);
        intent=getIntent();
        userType=intent.getStringExtra("userType");

        prgDialog = new ProgressDialog(this);
        // Set Cancelable as False
        prgDialog.setCancelable(false);
        getList();

    }

    private void displayListView() {

        ArrayList<PendingItem> pendingItems = new ArrayList<>();

        for (int i = 0; i < fields.length; i++) {
            PendingItem item = new PendingItem(fields[i],false);
            pendingItems.add(item);
        }



        dataAdapter = new MyCustomAdapter(this,R.layout.pending_list_item,pendingItems);
        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(dataAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pending_work, menu);
        return true;
    }
    public void getList(){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                prgDialog.setMessage("Loading..........");
                prgDialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);


                //Declare the version of the SOAP request
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                //this is the actual part that will call the webservice
                try {
                    androidHttpTransport.call(SOAP_ACTION, envelope);
                } catch (Exception e)  {
                    e.printStackTrace();
                }
                try {


                    // Get the SoapResult from the envelope body.
                    SoapObject result2 = (SoapObject)envelope.bodyIn;

                    if(result2 != null)
                    {
                        pendinglist=new ArrayList<String>();
                        Log.d("count", String.valueOf(result2.getPropertyCount()));
                        //Get the first property and change the label text
                        for(int i=0;i<result2.getPropertyCount();i++) {
                            pendinglist.add(result2.getProperty(i).toString());
                            Log.d("pendingList",pendinglist.get(i));
                        }
                    }
                    else
                    {
//                        Toast.makeText(getApplicationContext(), "No Response",Toast.LENGTH_LONG).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
            @Override
            protected void onPostExecute(String msg) {
                displayListView();
                prgDialog.hide();


            }

        }.execute(null,null,null);

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

    private class MyCustomAdapter extends ArrayAdapter<PendingItem> {

        ArrayList<PendingItem> pendingItems;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<PendingItem> pendingItems) {
            super(context, textViewResourceId, pendingItems);
            this.pendingItems = new ArrayList<PendingItem>();
            this.pendingItems.addAll(pendingItems);
        }


        private class ViewHolder {
            TextView itemName;
            TextView isPending;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            // Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.pending_list_item, null);

                holder = new ViewHolder();
                holder.itemName = (TextView) convertView.findViewById(R.id.textView);
                holder.isPending = (TextView) convertView.findViewById(R.id.textView2);
                convertView.setTag(holder);

                final ViewHolder finalHolder = holder;
                holder.isPending.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        //Insert Intent
                        Toast.makeText(PendingWork.this,
                                finalHolder.itemName.getText() + " " + finalHolder.isPending.getText()
                                , Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            PendingItem item = pendingItems.get(position);
            // Log.d("Pending Item", item.getName() + " " + item.isPending);
            holder.itemName.setText(item.getName());
            holder.isPending.setText(pendinglist.get(position));

            return convertView;

        }
    }
/*
    private void checkClick() {
        TextView
    }*/

}
