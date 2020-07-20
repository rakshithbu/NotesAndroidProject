package com.example.mynotes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mynotes.helper.HTTPRequestHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUp extends AppCompatActivity {

    EditText emailET, passwordET;
    Button signupBtn;
    TextView oldUserLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        oldUserLabel = findViewById(R.id.existingUser);
        signupBtn = findViewById(R.id.signupButton);
        emailET = findViewById(R.id.emailEditText);
        passwordET = findViewById(R.id.passwordEditText);

        oldUserLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ActivityLogin.class));
                finish();
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(emailET.getText().length()>0 && passwordET.getText().length()>0)
                {
                    attemptSignUP();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Both fields Required.",Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private void attemptSignUP() {
        String url = "http://alllinks.online/andproject/addUser.php?email="+emailET.getText().toString()+"&password="+passwordET.getText().toString();
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Signing up...");
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
//

                                Toast.makeText(getApplicationContext(),"Please Login to continue",Toast.LENGTH_LONG).show();

                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Invalid Attempt",Toast.LENGTH_LONG).show();
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
        HTTPRequestHelper.getInstance(this).addToRequestQueue(jsonObjReq);
    }
}
