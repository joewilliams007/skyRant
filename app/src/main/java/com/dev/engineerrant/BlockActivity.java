package com.dev.engineerrant;

import static com.dev.engineerrant.app.toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;

public class BlockActivity extends AppCompatActivity {

    EditText editTextWords, editTextUsers;
    SwitchCompat switchWords, switchUsers, switchGreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);
        initialize();
        setSwitches();
    }

    private void initialize() {
        editTextWords = findViewById(R.id.editTextWords);
        editTextUsers = findViewById(R.id.editTextUsers);
        editTextWords.setText(Account.blockedWords());
        editTextUsers.setText(Account.blockedUsers());
        switchUsers = findViewById(R.id.switchUsers);
        switchWords = findViewById(R.id.switchWords);
        switchGreen = findViewById(R.id.switchGreen);
    }

    private void setSwitches() {
        switchWords.setChecked(Account.blockWordsComments());
        switchUsers.setChecked(Account.blockUsersComments());
        switchGreen.setChecked(Account.blockGreenDot());
    }

    public void saveWords(View view) {
        String words = editTextWords.getText().toString().replaceAll(" ","");
        Account.setBlockedWords(words);
        editTextWords.setText(words);
        toast("saved");
        app.hideKeyboard(BlockActivity.this);
    }

    public void switchComments(View view) {
        Account.setBlockWordsComments(!Account.blockWordsComments());
        setSwitches();
    }

    public void switchUsers(View view) {
        Account.setBlockUsersComments(!Account.blockUsersComments());
        setSwitches();
    }

    public void saveUsers(View view) {
        String users = editTextUsers.getText().toString().replaceAll(" ","");
        Account.setBlockedUsers(users);
        editTextUsers.setText(users);
        toast("saved");
        app.hideKeyboard(BlockActivity.this);
    }

    public void switchGreen(View view) {
        Account.setBlockGreenDot(!Account.blockGreenDot());
        setSwitches();
    }
}