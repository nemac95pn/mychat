package com.example.nemanja.mychat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;

public class MainActivity extends AppCompatActivity {

    String[] arrayName={"Facebook", "Twitter", "YouTube", "Windows", "Drive"};

    private Toolbar mToolbar;
    private FirebaseAuth mAuth;

    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsPagerAdapter myTabsPagerAdapter;

    FirebaseUser currentUser;
    private DatabaseReference UsersReference;


    private static int TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();

        if (currentUser != null)
        {
            String online_user_id = mAuth.getCurrentUser().getUid();

            UsersReference = FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);

        }

        //Tabs for MainActivity

        myViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        myTabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsPagerAdapter);
        myTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);


        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Barely");

        //For CircleMenu//



        CircleMenu circleMenu = (CircleMenu) findViewById(R.id.circle_menu);
        circleMenu.setMainMenu(Color.parseColor("#CDCDCD"),R.drawable.add, R.drawable.exit)
                .addSubMenu(Color.parseColor("#258CFF"),R.drawable.facebook)
                .addSubMenu(Color.parseColor("#6d4cff"),R.drawable.twitter)
                .addSubMenu(Color.parseColor("#ff0000"),R.drawable.youtube)
                .addSubMenu(Color.parseColor("#03a9f4"),R.drawable.windows)
                .addSubMenu(Color.parseColor("#1a237e"),R.drawable.drive)
                .setOnMenuSelectedListener(new OnMenuSelectedListener()
                {
                    @Override
                    public void onMenuSelected(int index)
                    {
                        final int i = index;
                        Toast.makeText(MainActivity.this, "You selected" + arrayName[index], Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Intent intentOne = null;

                                switch (i)
                                {
                                    case 0:
                                        intentOne = new Intent(MainActivity.this, SettingsActivity.class);

                                        break;

                                    case 1:
                                        intentOne = new Intent(MainActivity.this, StatusActivity.class);
                                        intentOne.putExtra("user_status", SettingsActivity.settingsDisplayStatus.getText().toString());
                                        break;

                                    case 2:
                                        intentOne = new Intent(MainActivity.this, AllUsersActivity.class);
                                        break;

                                    case 3:
                                        intentOne = new Intent(MainActivity.this, SettingsActivity.class);
                                        break;
                                    default:
                                        break;
                                }
                                startActivity(intentOne);

                              //  Intent userProfileIntent = new Intent(MainActivity.this, SettingsActivity.class);
                               // startActivity(userProfileIntent);

                            }
                        }, TIME_OUT);

                    }
                });



    }
/*
    private void setSupportActionBar(Toolbar mToolbar) {
    } //ovo je za: setSupportActionBar(mToolbar) nije hteo da radi
*/



    @Override
    protected void onStart()
    {
        super.onStart();

        currentUser = mAuth.getCurrentUser();

        if (currentUser == null)
        {
            LogOutUser();
        }
        else if (currentUser != null)
        {
            UsersReference.child("online").setValue("true");
        }
    }


    @Override
    protected void onStop()
    {
        super.onStop();

        currentUser = mAuth.getCurrentUser();// dodato

        if (currentUser != null)
        {
            UsersReference.child("online").setValue(ServerValue.TIMESTAMP); //false
        }
    }

    private void LogOutUser()
    {
        Intent startPageIntent = new Intent(MainActivity.this, StartPageActivity.class);
        startPageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startPageIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_button)
        {
            if (currentUser != null)
            {
                UsersReference.child("online").setValue(ServerValue.TIMESTAMP);
            }

            mAuth.signOut();

            LogOutUser();
        }

        if (item.getItemId() == R.id.main_account_settings_button)
        {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        }

        if (item.getItemId() == R.id.main_all_users_button)
        {
            Intent allUsersIntent = new Intent(MainActivity.this, AllUsersActivity.class);
            startActivity(allUsersIntent);
        }

        return true;
    }
}
