package com.example.nemanja.mychat;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class StartPageActivity extends AppCompatActivity {

   // private RelativeLayout mListLayout;
    private Button NeedNewAccountButton;
    private Button AlreadyHaveAccountButton;
    private ImageView startLogo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

      //  mListLayout = (RelativeLayout) findViewById(R.id.ListLayout);
        NeedNewAccountButton = (Button) findViewById(R.id.need_account_button);
        AlreadyHaveAccountButton = (Button) findViewById(R.id.already_have_account_button);
        startLogo = (ImageView) findViewById(R.id.start_page_logo);


        NeedNewAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent registerIntent = new Intent(StartPageActivity.this, RegisterActivity.class);

                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(startLogo, "imageTransition");
               // pairs[1] = new Pair<View, String>(AlreadyHaveAccountButton, "loginTransition");
                pairs[1] = new Pair<View, String>(NeedNewAccountButton, "registerTransition");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(StartPageActivity.this, pairs);

                startActivity(registerIntent, options.toBundle());

            }
        });


        AlreadyHaveAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent loginIntent = new Intent(StartPageActivity.this, LoginActivity.class);

                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(startLogo, "imageTransition");
                pairs[1] = new Pair<View, String>(AlreadyHaveAccountButton, "loginTransition");
               // pairs[2] = new Pair<View, String>(NeedNewAccountButton, "registerTransition");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(StartPageActivity.this, pairs);

                startActivity(loginIntent, options.toBundle());
            }
        });
    }
}
