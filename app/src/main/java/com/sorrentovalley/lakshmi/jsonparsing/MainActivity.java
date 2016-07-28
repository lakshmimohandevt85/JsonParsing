package com.sorrentovalley.lakshmi.jsonparsing;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends ListActivity
{
    // URL to get posts JSON
    private static String url = "http://www.washingtonpost.com/wp-srv/simulation/simulation_test.json";
    private ProgressDialog progressDialog;
    // JSON Node names
    private static final String TAG_POSTS = "posts";
    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_CONTENT = "content";
    private static final String TAG_DATE = "date";

    // contacts JSONArray
    JSONArray posts = null;

    // Hashmap for ListView
     ArrayList<HashMap<String, String>> postsList;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        postsList = new ArrayList<HashMap<String, String>>();
        ListView listView = getListView();
        new GetPosts().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetPosts extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            // Showing progress dialog
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            // Creating service handler class instance
            HttpCalls httpCalls = new HttpCalls();

            // Making a request to url and getting response
            String jsonString = httpCalls.makeServiceCall(url, HttpCalls.GET);

            if (jsonString != null)
            {
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);

                    // Getting JSON Array node
                    posts = jsonObject.getJSONArray(TAG_POSTS);

                    // looping through All Contacts
                    for (int i = 0; i < posts.length(); i++)
                    {
                        JSONObject postsJSONObject = posts.getJSONObject(i);

                        String id = postsJSONObject.getString(TAG_ID);
                        String title = postsJSONObject.getString(TAG_TITLE);
                        String content = postsJSONObject.getString(TAG_CONTENT);
                        String date = postsJSONObject.getString(TAG_DATE);
                        HashMap<String, String> post = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        post.put(TAG_ID, id);
                        post.put(TAG_TITLE, title);
                        post.put(TAG_DATE, date);
                        post.put(TAG_CONTENT, content);

                        // adding contact to contact list
                        postsList.add(post);
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */

            ListAdapter adapter = new SimpleAdapter(MainActivity.this, postsList, R.layout.list_item, new String[]
                    { TAG_DATE, TAG_ID, TAG_DATE }, new int[] { R.id.name, R.id.email, R.id.mobile });

            setListAdapter(adapter);
        }

    }

}

