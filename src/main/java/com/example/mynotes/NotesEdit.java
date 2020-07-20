package com.example.mynotes;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.mynotes.adapters.NotesImageAdapter;
import com.example.mynotes.helper.LoginHelper;
import com.example.mynotes.helper.HTTPRequestHelper;
import com.example.mynotes.model.Categories;
import com.example.mynotes.model.ImageUrl;
import com.example.mynotes.model.Notes;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class NotesEdit extends FragmentActivity implements Serializable, OnMapReadyCallback {
    EditText titleET, noteET;
    Spinner categorySpinner;
    LinearLayout linearLayout;
    MediaRecorder recorder;
    Button recordBtn, deleteAudioBtn;
    boolean recording = false;
    String newRecordingAvailable;
    boolean audioPlaying;
    boolean isNewNote = false, isNoteUpdated = false;
    static Notes note;

    ArrayList<Categories> categories;

    public static final int RequestPermissionCode = 1;

    public static final int GALLERY_REQUEST_CODE = 101;

    LocationManager locationManager;

    ArrayList<LatLng> latLngs = new ArrayList<LatLng>();

    boolean mapsLoaded = false;
    boolean addedFirstMap = false;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_notes);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_cats);
        toolbar.setNavigationIcon(R.drawable.back_arrow); // your drawable
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.inflateMenu(R.menu.edit_notes_menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Implemented by activity
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.save:
                        savenote();
                        return (true);

                }
                return false;
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        titleET = findViewById(R.id.editTextTitle);
        noteET = findViewById(R.id.editTextNote);


        note = (Notes) getIntent().getSerializableExtra("note");
        if (note.id == -1) {
            isNewNote = true;
            toolbar.setTitle("Create Note");
        }
        if (!isNewNote) {
            toolbar.setTitle(note.title);
        }
        titleET.setText(note.title);
        noteET.setText(note.content);
        categories = (ArrayList<Categories>) getIntent().getSerializableExtra("categories");


        categorySpinner = findViewById(R.id.categorySpinner);
        ArrayAdapter<Categories> adapter = new ArrayAdapter<Categories>(this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        if (!isNewNote) {
            categorySpinner.setSelection(getCategoryPositionForOldNote());
        }
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Categories selected = ((Categories) parent.getSelectedItem());
                note.category_id = selected.id;
                //Toast.makeText(getApplicationContext(),"Selected"+ selected.name +" - " + selected.id , Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        linearLayout = findViewById(R.id.cl);
        recorder = new MediaRecorder();


        loadAudioView();


        noteET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        titleET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        loadImagesView();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private int getCategoryPositionForOldNote() {
        for (int i = 0; i < categories.size(); i++) {
            Categories cat = categories.get(i);
            if (cat.id == note.category_id) {
                return i;
            }
        }
        return 0;
    }

    void loadAudioView() {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        findViewById(R.id.recordingIcon).setVisibility(View.GONE);
        deleteAudioBtn = (Button) findViewById(R.id.deleteAudio);
        recordBtn = (Button) findViewById(R.id.recordBtn);
        deleteAudioBtn.setVisibility(View.GONE);
        deleteAudioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                note.audio = "";
                loadAudioView();
            }
        });
        if (existingRecordingAvailable()) {
            recordBtn.setText("Record New Audio");


            SimpleExoPlayer player = new SimpleExoPlayer.Builder(getApplicationContext()).build();
            // Produces DataSource instances through which media data is loaded.
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(),
                    Util.getUserAgent(getApplicationContext(), "My Notes"));
// This is the MediaSource representing the media to be played.
            MediaSource videoSource =
                    null;

            videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(note.audio));

// Prepare the player with the source.
            player.prepare(videoSource);

            player.addListener(new Player.EventListener() {
                @Override
                public void onIsPlayingChanged(boolean isPlaying) {
                    if (isPlaying) {
                        Toast.makeText(getApplicationContext(), "Playing", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Completed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            PlayerView playerView = findViewById(R.id.playerView);
            playerView.setVisibility(View.VISIBLE);
            playerView.setPlayer(player);
            findViewById(R.id.deleteAudio).setVisibility(View.VISIBLE);

        }


        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkAudioPermission()) {
                    requestAudioPermission();
                    return;
                }
                if (recordBtn.getText().equals("Save") || recordBtn.getText().toString().equals("Update")) {
                    Toast.makeText(getApplicationContext(), newRecordingAvailable, Toast.LENGTH_LONG).show();
                    uploadAudioFile(newRecordingAvailable);
                    return;
                }
                if (recording) {
                    findViewById(R.id.recordingIcon).setVisibility(View.GONE);
                    stopRecording();
                    recording = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recordBtn.setText("Update");
                        }
                    });
                } else {
                    findViewById(R.id.recordingIcon).setVisibility(View.VISIBLE);
                    Glide.with(getApplicationContext())
                            .load(R.drawable.recording)
                            .into((ImageView) findViewById(R.id.recordingIcon));
                    startRecording();
                    recording = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recordBtn.setText("Stop");
                        }
                    });
                }
            }
        });


    }

    private void startRecording() {
        String uuid = UUID.randomUUID().toString();
        String fileName = getExternalCacheDir().getAbsolutePath() + "/" + uuid + ".3gp";
        newRecordingAvailable = fileName;
        Log.i(MainActivity.class.getSimpleName(), fileName);

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(MainActivity.class.getSimpleName() + ":startRecording()", "prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }

    boolean existingRecordingAvailable() {
        return !note.audio.equals("");
    }

    private void requestAudioPermission() {
        ActivityCompat.requestPermissions(NotesEdit.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(NotesEdit.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(NotesEdit.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkAudioPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    byte[] getByteArr(String path) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(path));

            int read;
            byte[] buff = new byte[1024];
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
            out.flush();
            return out.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }


    public boolean uploadAudioFile(String audioPath) {

        byte[] audioBytes = getByteArr(audioPath);
        final String audioString = Base64.encodeToString(audioBytes, Base64.DEFAULT);
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Uploading the Audio...");
        pDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, "http://alllinks.online/andproject/fileUpload.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    pDialog.hide();
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getString("status").equals("success")) {
                        Toast.makeText(NotesEdit.this, "Uploaded Successful", Toast.LENGTH_LONG).show();
                        note.audio = jsonObject.getString("path");
                        //audioPopupWindow.dismiss();
                        loadAudioView();
                        isNoteUpdated = true;
                    } else {
                        Toast.makeText(NotesEdit.this, "Some error occurred!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(NotesEdit.this, "Some error occurred -> " + volleyError, Toast.LENGTH_LONG).show();
            }
        }) {
            //adding parameters to send
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("fileToUpload", audioString);
                parameters.put("type", "3gp");
                return parameters;
            }
        };

        request.setShouldCache(false);
        HTTPRequestHelper.getInstance(this).addToRequestQueue(request);
        return true;
    }


    void loadImagesView() {
        //https://javapapers.com/android/android-image-gallery-example-app-using-glide-library/

        findViewById(R.id.addImgBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFromGallery();
            }
        });


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewImgs);
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 30);
        LinearLayoutManager linearLayoutManager
                = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false);
        recyclerView.setLayoutManager(linearLayoutManager);

        ArrayList<ImageUrl> imageUrlList = prepareImageList();
        NotesImageAdapter dataAdapter = new NotesImageAdapter(NotesEdit.this, imageUrlList);
        recyclerView.setAdapter(dataAdapter);

    }

    ArrayList<ImageUrl> prepareImageList() {
        ArrayList<ImageUrl> al = new ArrayList<ImageUrl>();
        String[] items = note.image.split("#");
        for (String i : items) {
            if (!i.equals("")) {
                al.add(new ImageUrl(i));
            }
        }
        return al;
    }

    public void removeImageFromList(String removeKey) {
        String[] items = note.image.split("#");
        String newList = "";
        for (String i : items) {
            if ((!i.equals("")) && (!removeKey.equals(i))) {
                if (newList.equals("")) {
                    newList = i;
                } else {
                    newList = newList + "#" + i;
                }
            }
        }
        note.image = newList;
        loadImagesView();
    }

    private void pickFromGallery() {
        //Create an Intent with action as ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        // Launching the Intent
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    //data.getData returns the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    uploadImageFile(getPath(selectedImage));
                    break;
            }

    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public boolean uploadImageFile(String imagePath) {

        byte[] audioBytes = getByteArr(imagePath);
        final String imageString = Base64.encodeToString(audioBytes, Base64.DEFAULT);
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Uploading the Image...");
        pDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, "http://alllinks.online/andproject/fileUpload.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    pDialog.hide();
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getString("status").equals("success")) {
                        Toast.makeText(NotesEdit.this, "Uploaded Successful", Toast.LENGTH_LONG).show();

                        if (note.image.equals("")) {
                            note.image = jsonObject.getString("path");
                        } else {
                            note.image = note.image + "#" + jsonObject.getString("path");
                        }
                        loadImagesView();
                        isNoteUpdated = true;
                    } else {
                        Toast.makeText(NotesEdit.this, "Some error occurred!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(NotesEdit.this, "Some error occurred -> " + volleyError, Toast.LENGTH_LONG).show();

            }
        }) {
            //adding parameters to send
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("fileToUpload", imageString);
                parameters.put("type", "jpg");
                return parameters;
            }
        };

        request.setShouldCache(false);
        HTTPRequestHelper.getInstance(this).addToRequestQueue(request);
        return true;
    }


    void savenote() {
        if (isNewNote) {
            //run create function and change the new note flag to false
            saveNewNote();
            addNewLocation();
        } else {
            updateNote();
            addNewLocation();
            //no changes and return
        }
    }

    private void updateNote() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Updating Note...");
        pDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, "http://alllinks.online/andproject/updateNote.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    pDialog.hide();
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getString("status").equals("success")) {
                        Toast.makeText(NotesEdit.this, "Uploaded Successful", Toast.LENGTH_LONG).show();
                        isNewNote = false;
                        note.id = jsonObject.getInt("id");
                    } else {
                        Toast.makeText(NotesEdit.this, "Some error occurred!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(NotesEdit.this, "Some error occurred -> " + volleyError, Toast.LENGTH_LONG).show();

            }
        }) {
            //adding parameters to send
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                note.title = titleET.getText().toString();
                note.content = noteET.getText().toString();
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("title", note.title);
                parameters.put("content", note.content);
                parameters.put("category_id", String.valueOf(note.category_id));
                parameters.put("created_date", note.created_on);
                parameters.put("updated_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                parameters.put("location", "");
                parameters.put("secured", "");
                parameters.put("image", note.image);
                parameters.put("audio", note.audio);
                parameters.put("video", note.video);
                parameters.put("id", String.valueOf(note.id));
                return parameters;
            }
        };

        request.setShouldCache(false);
        HTTPRequestHelper.getInstance(this).addToRequestQueue(request);
    }

    private void saveNewNote() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Adding Note...");
        pDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, "http://alllinks.online/andproject/addNote.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    pDialog.hide();
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getString("status").equals("success")) {
                        Toast.makeText(NotesEdit.this, "Uploaded Successful", Toast.LENGTH_LONG).show();
                        isNewNote = false;
                        note.id = jsonObject.getInt("id");
                    } else {
                        Toast.makeText(NotesEdit.this, "Some error occurred!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(NotesEdit.this, "Some error occurred -> " + volleyError, Toast.LENGTH_LONG).show();

            }
        }) {
            //adding parameters to send
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                note.title = titleET.getText().toString();
                note.content = noteET.getText().toString();
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("title", note.title);
                parameters.put("content", note.content);
                parameters.put("category_id", String.valueOf(note.category_id));
                parameters.put("created_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                parameters.put("updated_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                parameters.put("location", "");
                parameters.put("secured", "");
                parameters.put("image", note.image);
                parameters.put("audio", note.audio);
                parameters.put("video", note.video);
                parameters.put("email", LoginHelper.getSharedPreferenceString(getApplicationContext(), "username", ""));
                return parameters;
            }
        };

        request.setShouldCache(false);
        HTTPRequestHelper.getInstance(this).addToRequestQueue(request);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mapsLoaded = true;
        mMap.clear();
        if(!addedFirstMap)
        {
            updateMapLocationsVariable(false);
        }
    }


    void getLocationsFromDatabase() {

    }

    void addNewLocation() {
        if(latLngs.size()>0)
        {
         Location oldLast= new Location("old");
         oldLast.setLatitude(latLngs.get(0).latitude);
         oldLast.setLatitude(latLngs.get(0).longitude);
         Location newLocation = getLastBestLocation();
         if(newLocation != null)
         {
             float distance = oldLast.distanceTo(newLocation);
             if(distance>200)
             {
                 latLngs.add(new LatLng(newLocation.getLatitude(),newLocation.getLongitude()));
                 updateLocationInDatabase(newLocation);
                 updateMapLocationsVariable(true);
             }
         }

        }
        else
        {
            Location newLocation = getLastBestLocation();
            if(newLocation != null)
            {
                    latLngs.add(new LatLng(newLocation.getLatitude(),newLocation.getLongitude()));
                    updateLocationInDatabase(newLocation);
                    updateMapLocationsVariable(true);
            }
        }
        }


    private void updateMapLocationsVariable(final boolean showdialog) {
        if(note.id == -1){return;}
        String url = "http://alllinks.online/andproject/getLocations.php?noteid="+note.id;
        final ProgressDialog pDialog = new ProgressDialog(NotesEdit.this);
        if(showdialog) {
            pDialog.setMessage("Saving Notes...");
            pDialog.show();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if(showdialog) {pDialog.hide();}
                        try {
                            if(response.getString("status").equals("success"))
                            {
                                // Toast.makeText(mContext, response.toString(),Toast.LENGTH_LONG).show();
                                JSONArray locationJSONArray = response.getJSONArray("locations");
                                latLngs.clear();
                                for(int i=0;i<locationJSONArray.length() && i<5;i++)
                                {
                                    float lat = (float) ((JSONObject)locationJSONArray.getJSONObject(i)).getDouble("lat");
                                    float lng = (float) ((JSONObject)locationJSONArray.getJSONObject(i)).getDouble("lng");
                                    latLngs.add(new LatLng(lat,lng));
                                    if(mapsLoaded)
                                    {
                                        addedFirstMap = true;
                                        updateMapPins();
                                    }
                                }
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Some Error occurred in updating Locations", Toast.LENGTH_LONG).show();
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"Sorry, Some error occurred in updating Locations",Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Sorry, Some error occurred",Toast.LENGTH_LONG).show();
                // hide the progress dialog
                if(showdialog) {pDialog.hide();}
            }
        });

        // Adding request to request queue
        jsonObjReq.setShouldCache(false);
        HTTPRequestHelper.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
    }

    private void updateMapPins() {
        mMap.clear();
        boolean first = true;
        for(LatLng latLng : latLngs)
        {
            if(first)
            {
                first = false;
                Marker marker =  mMap.addMarker(new MarkerOptions().position(latLng).title(String.valueOf(latLngs.size()))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                marker.showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                continue;
            }
            Marker marker =  mMap.addMarker(new MarkerOptions().position(latLng).title(String.valueOf(latLngs.size())));
            marker.showInfoWindow();
            //markers.add(marker);
        }
        if(latLngs.size() != 0)
        {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(latLngs.size()-1),5));
        }
    }

    void updateLocationInDatabase(Location newLocaiton)
    {
        String url = "http://alllinks.online/andproject/addLocation.php?noteid="+note.id+"&lat="+newLocaiton.getLatitude()+"&long="+newLocaiton.getLongitude()+"&place=";
        final ProgressDialog pDialog = new ProgressDialog(NotesEdit.this);
        pDialog.setMessage("Saving Notes...");
        pDialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.hide();
                        try {
                            if(response.getString("status").equals("success"))
                            {
                                // Toast.makeText(mContext, response.toString(),Toast.LENGTH_LONG).show();


                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Some Error occurred in updating Locations", Toast.LENGTH_LONG).show();
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"Sorry, Some error occurred in updating Locations",Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Sorry, Some error occurred",Toast.LENGTH_LONG).show();
                // hide the progress dialog
                pDialog.hide();
            }
        });

        // Adding request to request queue
        jsonObjReq.setShouldCache(false);
        HTTPRequestHelper.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
    }
    private boolean checkLocationPermission()
    {
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        int res = getApplicationContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private Location getLastBestLocation() {
        if (checkLocationPermission()){
            locationManager = (LocationManager)getSystemService
                    (Context.LOCATION_SERVICE);
            Location getLastLocation = locationManager.getLastKnownLocation
                    (LocationManager.PASSIVE_PROVIDER);
            return getLastLocation;
        }
        Toast.makeText(getApplicationContext(), "location permission not granted",Toast.LENGTH_SHORT).show();
        return null;
    }

}
