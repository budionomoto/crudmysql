package id.xyzsystem.budiono.crudmysql;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import java.util.ArrayList;
import java.util.List;

import id.xyzsystem.budiono.crudmysql.util.server;
import id.xyzsystem.budiono.crudmysql.data.data;
import id.xyzsystem.budiono.crudmysql.adapter.Adapter;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    //declare
    List<data> itemList = new ArrayList<data>();
    ListView list;
    Adapter adapter;
    int success;

    String id, nama, alamat;
    EditText txt_nama, txt_alamat;
    TextView txt_id;

    public static final String TAG_ID = "id";
    public static final String TAG_NAMA = "nama";
    public static final String TAG_ALAMAT = "alamat";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    //ini url
    private static String url_select = server.URL + "select.php";
    private static String url_insert = server.URL + "insert.php";
    private static String url_edit = server.URL + "edit.php";
    private static String url_update = server.URL + "update.php";
    private static String url_delete = server.URL + "delete.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView) findViewById(R.id.list);
        adapter = new Adapter(MainActivity.this, itemList);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(final AdapterView<?> parent, View view, final int position, long l) {
                    //final String idx = itemList.get(position).getId();


                    final String idx = itemList.get(position).getId();
                    final String get_nama = itemList.get(position).getNama();
                    final String get_alamat = itemList.get(position).getAlamat();

                    TextView nomer = (TextView) findViewById(R.id.txt_id);
                    EditText nama = (EditText) findViewById(R.id.txt_nama);
                    EditText alamat = (EditText) findViewById(R.id.txt_alamat);

                    nomer.setText(idx);
                    nama.setText(get_nama);
                    alamat.setText(get_alamat);


                    Toast.makeText(MainActivity.this, "posisi: " + position + ", idx: " + idx, Toast.LENGTH_LONG).show();

                }
            }
        );

    }

    public void panggil(View view) {
        ambil_data();
    }

    private void ambil_data() {
        //start
        //menghubungkan variabel dan layout
        //list = (ListView) findViewById(R.id.list);

        // untuk mengisi data dari JSON ke dalam adapter
        adapter = new Adapter(MainActivity.this, itemList);
        list.setAdapter(adapter);
        //end
        itemList.clear();
        RequestQueue rq;

        //TextView mTxtDisplay;
        ImageView mImageView;

        TextView mTxtDisplay = (TextView) findViewById(R.id.txtDisplay);
        mTxtDisplay.setText("Tunggu...");

        //ListView daftar = (ListView) findViewById(R.id.list);

        String url = "http://budijava.com/biodata/select.php";

        //
        rq = Volley.newRequestQueue(this);
        //mulai panggil request
        /*JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET
                , url
                , (String)null
                ,new Response.Listener<JSONArray>() {
                */
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(
                url
                , new Response.Listener<JSONArray>() {


            @Override
            public void onResponse(JSONArray response) {
                TextView mTxtDisplay = (TextView) findViewById(R.id.txtDisplay);
                mTxtDisplay.setText("Response: " + response.toString());

                // Parsing json
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);

                        data item = new data();

                        item.setId(obj.getString(TAG_ID));
                        item.setNama(obj.getString(TAG_NAMA));
                        item.setAlamat(obj.getString(TAG_ALAMAT));

                        // menambah item ke array
                        itemList.add(item);

                        mTxtDisplay.setText(item.getNama());


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //TO DO auto-generated method stub
                TextView mTxtDisplay = (TextView) findViewById(R.id.txtDisplay);
                mTxtDisplay.setText("Error Bro: " + error.getMessage());
            }
        });

        rq.add(jsObjRequest);

//Access the RequestQueue through

        //MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }
    public void hapus(View view) {

        RequestQueue rq;
        TextView nomer = (TextView) findViewById(R.id.txt_id);
        final String idx = nomer.getText().toString();

        rq = Volley.newRequestQueue(this);
        StringRequest strReq = new StringRequest(Request.Method.POST, url_delete, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Log.d(TAG, "Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Cek error node pada json
                    if (success == 1) {
                        //Log.d("delete", jObj.toString());

                        //callVolley();
                        ambil_data();
                        kosong();



                        Toast.makeText(MainActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(MainActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters ke post url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", idx);

                return params;
            }

        };
        rq.add(strReq);
        //AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }


    //kosongkan form
    public void kosong(){

        TextView txt_id1 = (TextView) findViewById(R.id.txt_id);
        EditText txt_nama1= (EditText) findViewById(R.id.txt_nama);
        EditText txt_alamat1 = (EditText) findViewById(R.id.txt_alamat);

        txt_id1.setText(null);
        txt_nama1.setText(null);
        txt_alamat1.setText(null);
    }

    // fungsi untuk menyimpan atau update
    public void simpan(View view) {
        RequestQueue rq;
        String url;

        TextView txt_id1 = (TextView) findViewById(R.id.txt_id);
        EditText txt_nama1= (EditText) findViewById(R.id.txt_nama);
        EditText txt_alamat1 = (EditText) findViewById(R.id.txt_alamat);

        id      = txt_id1.getText().toString();
        nama    = txt_nama1.getText().toString();
        alamat  = txt_alamat1.getText().toString();

        // jika id kosong maka simpan, jika id ada nilainya maka update
        if (id.isEmpty()){
            url = url_insert;
        } else {
            url = url_update;
        }


        rq = Volley.newRequestQueue(this);

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Log.d(TAG, "Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Cek error node pada json
                    if (success == 1) {
                        //Log.d("Add/update", jObj.toString());

                        //callVolley();
                        ambil_data();
                        kosong();

                        Toast.makeText(MainActivity.this, "berhasil: " + jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                        adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(MainActivity.this, "gagal simpan: " + jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(MainActivity.this, "error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters ke post url
                Map<String, String> params = new HashMap<String, String>();
                // jika id kosong maka simpan, jika id ada nilainya maka update
                if (id.isEmpty()){
                    params.put("nama", nama);
                    params.put("alamat", alamat);
                } else {
                    params.put("id", id);
                    params.put("nama", nama);
                    params.put("alamat", alamat);
                }

                return params;
            }

        };

        rq.add(strReq);
        //AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

}
