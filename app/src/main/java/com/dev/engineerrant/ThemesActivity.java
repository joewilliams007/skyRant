package com.dev.engineerrant;

import static com.dev.engineerrant.app.hideKeyboard;
import static com.dev.engineerrant.app.toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;

public class ThemesActivity extends AppCompatActivity {

    EditText editTextSearchText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(ThemesActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_themes);
        initialize();
    }

    private void initialize() {
        editTextSearchText = findViewById(R.id.editTextSearchText);
        SwitchCompat switchUsername = findViewById(R.id.switchUsername);

        switchUsername.setChecked(Account.isFeedUsername());
    }

    public void switchUsername(View view) {
        Account.setFeedUsername(!Account.isFeedUsername());
        initialize();
    }

    public void saveSearch(View view) {
        String t = editTextSearchText.getText().toString();
        if (t.length()>1) {
            Account.setSearch(t);
            hideKeyboard(ThemesActivity.this);
        } else {
            toast("enter a term");
        }
    }

    public void themeLight(View view) {
        Account.setTheme("light");
        Tools.setTheme(ThemesActivity.this);
        Intent intent = new Intent(ThemesActivity.this, ThemesActivity.class);
        startActivity(intent);
        finish();
    }

    public void themeAmoled(View view) {
        Account.setTheme("amoled");
        Tools.setTheme(ThemesActivity.this);
        Intent intent = new Intent(ThemesActivity.this, ThemesActivity.class);
        startActivity(intent);
        finish();

    }
    public void themeAmoledPart(View view) {
        Account.setTheme("amoledPart");
        Tools.setTheme(ThemesActivity.this);
        Intent intent = new Intent(ThemesActivity.this, ThemesActivity.class);
        startActivity(intent);
        finish();
    }
    public void themeCoffee(View view) {
        Account.setTheme("coffee");
        Tools.setTheme(ThemesActivity.this);
        Intent intent = new Intent(ThemesActivity.this, ThemesActivity.class);
        startActivity(intent);
        finish();
    }

    public void themeDark(View view) {
        Account.setTheme("dark");
        Tools.setTheme(ThemesActivity.this);
        Intent intent = new Intent(ThemesActivity.this, ThemesActivity.class);
        startActivity(intent);
        finish();
    }

    public void themeGreen(View view) {
        Account.setTheme("green");
        Tools.setTheme(ThemesActivity.this);
        Intent intent = new Intent(ThemesActivity.this, ThemesActivity.class);
        startActivity(intent);
        finish();
    }

    public void themeDiscord(View view) {
        Account.setTheme("discord");
        Tools.setTheme(ThemesActivity.this);
        Intent intent = new Intent(ThemesActivity.this, ThemesActivity.class);
        startActivity(intent);
        finish();
    }

    public void openHome(View view) {
        Intent intent = new Intent(ThemesActivity.this, MainActivity.class);
        startActivity(intent);
    }
}