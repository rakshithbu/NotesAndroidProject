package com.example.mynotes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mynotes.adapters.CustomAdapter;
import com.example.mynotes.adapters.NotesListAdapter;
import com.example.mynotes.comparator.NoteTitleSorting;
import com.example.mynotes.comparator.NotesDateSorting;
import com.example.mynotes.helper.LoginHelper;
import com.example.mynotes.helper.HTTPRequestHelper;
import com.example.mynotes.model.Categories;
import com.example.mynotes.model.DrawerDataModel;
import com.example.mynotes.model.Notes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity implements Serializable {

    ProgressDialog pDialog;
    Button manageCats;
    ArrayList <Categories> categories = new ArrayList<Categories>();
    ArrayList<Notes> notesBackup;

    //https://www.journaldev.com/9958/android-navigation-drawer-example-tutorial
    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    Toolbar toolbar;
    ActionBarDrawerToggle mDrawerToggle;

    SearchView searchView;
    int catpos;

    RadioButton rbTitle,rbDate,rbAsc,rbDsc;
    RadioGroup rgsortType,rgsortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        searchView = findViewById(R.id.searchView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitleTextColor(Color.WHITE);

        rbTitle = findViewById(R.id.radio_title);
        rbDate = findViewById(R.id.radio_date);
        //rbAsc = findViewById(R.id.radio_asc);
      //  rbDsc = findViewById(R.id.radio_dsc);

        rgsortType = findViewById(R.id.sortSelection);
        //rgsortOrder = findViewById(R.id.orderSelection);

        rgsortType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                updateListContents();
            }
        });

        /*rgsortOrder.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                updateListContents();
            }
        });*/


        pDialog = new ProgressDialog(this);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NotesEdit.class);
                intent.putExtra("note",new Notes(-1,-1 ,"","","","","","",""));
                intent.putExtra("categories",categories);
                startActivity(intent);
            }
        });
        findViewById(R.id.manageCats).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Catogories.class));
            }
        });
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginHelper.setSharedPreferenceString(getApplicationContext(),"username","");
                startActivity(new Intent(MainActivity.this, ActivityLogin.class));
                finish();
            }
        });
        ((TextView)findViewById(R.id.username)).setText("Welcome " + LoginHelper.getSharedPreferenceString(getApplicationContext(),"username",""));
        updateDrawer();
        setupDrawerToggle();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                updateListContents();
                return false;
            }
        });
    }

    private void updateListContents() {
        String s = searchView.getQuery().toString();
        if(notesBackup == null)
        {return ;}
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.notesRecycleView);
        ArrayList<Notes> tempNotes = new ArrayList<Notes>();
        if(catpos == categories.size())
        {
            for(Notes note : notesBackup)
            {
                if(note.title.toLowerCase().contains(s.toLowerCase()) || note.content.toLowerCase().contains(s.toLowerCase()))
                {
                    tempNotes.add(note);
                }
            }
            tempNotes = getSorterdList(tempNotes);
            NotesListAdapter dataAdapter = new NotesListAdapter(getApplicationContext(), tempNotes,categories);
            recyclerView.setAdapter(dataAdapter);
            return ;
        }
        Categories selectedCat = categories.get(catpos);
        for(Notes note : notesBackup)
        {
            if(note.category_id == selectedCat.id && (note.title.toLowerCase().contains(s.toLowerCase()) || note.content.toLowerCase().contains(s.toLowerCase())))
            {
                tempNotes.add(note);
            }
        }
        tempNotes = getSorterdList(tempNotes);
        NotesListAdapter dataAdapter = new NotesListAdapter(getApplicationContext(), tempNotes,categories);
        recyclerView.setAdapter(dataAdapter);
    }

    ArrayList<Notes> getSorterdList(ArrayList<Notes> notesArrayList)
    {
        if(rbTitle.isChecked())
        {
            Collections.sort(notesArrayList,new NoteTitleSorting());
            return notesArrayList;
        }
        /*if(rbTitle.isChecked())
        {
            Collections.sort(notesArrayList,new NoteTitleSorting());
            Collections.reverse(notesArrayList);
            return notesArrayList;
        }
        if(rbDate.isChecked())
        {
            Collections.sort(notesArrayList,new NotesDateSorting());
            return notesArrayList;
        }*/
        if(rbDate.isChecked())
        {
            Collections.sort(notesArrayList,new NotesDateSorting());
            Collections.reverse(notesArrayList);
            return notesArrayList;
        }
        return notesArrayList;
    }


    void updateDrawer()
    {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        DrawerDataModel[] drawerItem = new DrawerDataModel[categories.size()+1];

        for(int i=0;i<categories.size();i++)
        {
            drawerItem[i] = new DrawerDataModel(R.drawable.plus, categories.get(i).name);
        }
        drawerItem[categories.size()] = new DrawerDataModel(R.drawable.plus, "All categories");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        CustomAdapter adapter = new CustomAdapter(this, R.layout.drawer_list_view_item, drawerItem);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerList.setItemChecked(drawerItem.length-1,true);
        mDrawerList.setSelection(drawerItem.length-1);
    }
    void setupDrawerToggle(){
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.app_name, R.string.app_name);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        mDrawerToggle.syncState();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //Toast.makeText(getApplicationContext(),String.valueOf(position),Toast.LENGTH_SHORT).show();
            catpos = position;
            updateListContents();
        }

    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        getCategories();
    }
    private void getCategories() {
        String url = "http://alllinks.online/andproject/getCategoriesOfUser.php?email="+ LoginHelper.getSharedPreferenceString(getApplicationContext(),"username","");

        pDialog.setMessage("Fetching your Notes...");
        pDialog.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("status").equals("success"))
                            {

                               // Toast.makeText(getApplicationContext(), response.toString(),Toast.LENGTH_LONG).show();
                                updateCategories(response.getJSONArray("categories"));
                                updateDrawer();
                                getNotes();

                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Some Error occurred",Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"Sorry, Some error occured",Toast.LENGTH_LONG).show();
                            pDialog.hide();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Sorry, Some error occured",Toast.LENGTH_LONG).show();
                // hide the progress dialog

            }
        });
        // Adding request to request queue
        HTTPRequestHelper.getInstance(this).addToRequestQueue(jsonObjReq);
    }

    private void getNotes() {
        String url = "http://alllinks.online/andproject/getNotesForUser.php?email="+ LoginHelper.getSharedPreferenceString(getApplicationContext(),"username","");

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.hide();
                        try {
                            if(response.getString("status").equals("success"))
                            {
                               // Toast.makeText(getApplicationContext(), response.toString(),Toast.LENGTH_LONG).show();

                                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.notesRecycleView);
                                GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
                                recyclerView.setLayoutManager(gridLayoutManager);

                                ArrayList<Notes> notesList = prepareNotesList(response.getJSONArray("notes"));
                                notesBackup = notesList;
                                NotesListAdapter dataAdapter = new NotesListAdapter(getApplicationContext(), notesList,categories);
                                recyclerView.setAdapter(dataAdapter);

                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Some Error occurred", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"Sorry, Some error occured",Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Sorry, Some error occured",Toast.LENGTH_LONG).show();
                // hide the progress dialog
                pDialog.hide();
            }
        });

        // Adding request to request queue
        jsonObjReq.setShouldCache(false);
        HTTPRequestHelper.getInstance(this).addToRequestQueue(jsonObjReq);
    }


    private void updateCategories(JSONArray catArr) throws JSONException {
       ArrayList<Categories> al = new ArrayList<>();
        for (int i = 0; i < catArr.length(); i++) {
            JSONObject row = catArr.getJSONObject(i);
            al.add(new Categories(row.getString("name"),row.getInt("id")));
        }
        categories = al;
        catpos = categories.size();
    }

    ArrayList<Notes> prepareNotesList(JSONArray jsonArray) throws JSONException {
        ArrayList<Notes> al = new ArrayList<>();
        for(int i=0;i<jsonArray.length();i++)
        {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            al.add(new Notes(jsonObject.getInt("id"),jsonObject.getInt("category_id"),jsonObject.getString("content"),jsonObject.getString("created_date"),jsonObject.getString("updated_date"),jsonObject.getString("title"),jsonObject.getString("image"),jsonObject.getString("audio"),jsonObject.getString("video")));
        }
        return al;
    }
}
