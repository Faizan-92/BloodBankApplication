package com.android.iunoob.bloodbank.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.iunoob.bloodbank.R;
import com.android.iunoob.bloodbank.fragments.AboutUs;
import com.android.iunoob.bloodbank.fragments.AchievmentsView;
import com.android.iunoob.bloodbank.fragments.HomeView;
import com.android.iunoob.bloodbank.fragments.NearByHospitalActivity;
import com.android.iunoob.bloodbank.fragments.SearchDonorFragment;
import com.android.iunoob.bloodbank.viewmodels.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.android.iunoob.bloodbank.R.id.home;

public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private TextView getUserName;
    private FirebaseDatabase user_db;
    private FirebaseUser cur_user;
    private DatabaseReference userdb_ref;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Log.d("MyFirebase","Showing pd from Dashboard.java");
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();
        user_db = FirebaseDatabase.getInstance();
        cur_user = mAuth.getCurrentUser();
        userdb_ref = user_db.getReference("users");
        Log.d("MyFirebase","Got usersdb_ref");
        getUserName = findViewById(R.id.UserNameView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MyFirebase","starting postactivity from Dashboard");
                startActivity(new Intent(Dashboard.this, PostActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        getUserName = (TextView) header.findViewById(R.id.UserNameView);
        Log.d("MyFirebase","Here in Dashboard.java");
        Query singleuser = userdb_ref.child(cur_user.getUid());
        pd.show();
        Log.d("MyFirebase","Showing progress dialog");
        singleuser.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //pd.show();
                String name = dataSnapshot.getValue(UserData.class).getName();

                getUserName.setText(name);

                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());

            }
        });

        //If no fragment is loaded
        if(savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new HomeView()).commit();
            navigationView.getMenu().getItem(0).setChecked(true);

        }
        Log.d("MyFirebase","Finishing onCreate() of Dashboard.java");
    }


    @Override
    public void onBackPressed() {
        Log.d("MyFirebase","onBackPressed() of Dashboard.java");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("MyFirebase","Finishing onCreateOptionsMenu() of Dashboard.java");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("MyFirebase","onOptionsItemSelected of Dashboard.java");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.devinfo) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new AboutUs()).commit();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Log.d("MyFirebase","onNavigationItemSelected of Dashboard.java");
        int id = item.getItemId();

        if (id == home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new HomeView()).commit();

        } else if (id == R.id.userprofile) {
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));

        }
        else if (id == R.id.user_achiev) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new AchievmentsView()).commit();

        }
        else if (id == R.id.logout) {
            mAuth.signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.blood_storage){

            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new SearchDonorFragment()).commit();

        } else if (id == R.id.nearby_hospital) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new NearByHospitalActivity()).commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d("MyFirebase","onStart() of Dashboard.java");
        if(currentUser == null)
        {
            Log.d("MyFirebase","CurrentUser==null");
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            Log.d("MyFirebase","current user is not null !");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MyFirebase","Starting onResume() of Dashboard.java");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null)
        {
            Log.d("MyFirebase","current user null at onResume() of Dashboard.java");
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        Log.d("MyFirebase","Finishing onResume() of Dashboard.java");
    }
    @Override
    protected void onStop(){
        super.onStop();
        Log.d("MyFirebase","Finishing onStop() of Dashboard.java");
    }

}