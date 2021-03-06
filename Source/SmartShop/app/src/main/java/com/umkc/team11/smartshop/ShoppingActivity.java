package com.umkc.team11.smartshop;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ShoppingActivity extends AppCompatActivity {

    //private SearchData sql;
    private ArrayList<ItemData> clothData;
    private Context appContext;
    private Spinner spinSort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ArrayList<String> spinnerArray =  new ArrayList<>();
        spinnerArray.add("No Sorting");
        spinnerArray.add("Name");
        spinnerArray.add("Price");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinSort = findViewById(R.id.spin_sort);
        spinSort.setAdapter(adapter);

        appContext = this;

        //sql = SearchData.getInstance(this);
    } // end onCreate

    public void searchCall(View v)
    {
        EditText searchText = findViewById(R.id.txt_search);
        String searchPhrase = String.valueOf(searchText.getText());
        searchAPIs(searchPhrase);
    } // end searchCall

    public void sortCall(View v)
    {
        if(clothData != null && !clothData.isEmpty())
        {
            // find the list view
            ListView lv = findViewById(R.id.lst_items);

            if(spinSort.getSelectedItem() == "Name")
            {
                Collections.sort(clothData, new Comparator<ItemData>() {
                    public int compare(ItemData item1, ItemData item2) {
                        return item1.getName().compareTo(item2.getName());
                    }
                });

                // place the data into the list
                lv.setAdapter(new ItemAdapter(appContext, clothData));
            }
            else if (spinSort.getSelectedItem() == "Price")
            {
                Collections.sort(clothData, new Comparator<ItemData>() {
                    public int compare(ItemData item1, ItemData item2) {
                        return Double.compare(item1.getPrice(),item2.getPrice());
                    }
                });

                // place the data into the list
                lv.setAdapter(new ItemAdapter(appContext, clothData));
            } // end if
        } // end if
    } // end searchCall

    public void searchAPIs(String searchText)
    {
        String url ="https://api.indix.com/v2/summary/products?countryCode=US&q=" + searchText + "&app_key=w2xqtl4uBXLJnCk0zscGrt86TEh80bmx";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try {
                            JSONObject res = response.getJSONObject("result");
                            JSONArray jarray = res.getJSONArray("products");
                            clothData = new ArrayList<>();

                            for(int i = 0; i < jarray.length(); i++)
                            {
                                JSONObject item = jarray.getJSONObject(i);
                                String name = item.getString("title");
                                String brand = item.getString("brandName");
                                double price = Double.parseDouble(item.getString("maxSalePrice"));
                                String image = item.getString("imageUrl");

                                clothData.add(new ItemData(name, brand, price, image));
                            } // end loop

                            // find the list view
                            ListView lv = findViewById(R.id.lst_items);

                            // place the data into the list
                            lv.setAdapter(new ItemAdapter(appContext, clothData));
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        } // end try/catch
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        System.out.println("Error");
                    }
                });

        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    } // end searchAPIs
} // end ShoppingActivity
