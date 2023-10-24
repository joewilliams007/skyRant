package com.dev.engineerrant;

import static com.dev.engineerrant.app.toast;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.auth.MatrixAccount;

public class AccountsActivity extends AppCompatActivity {
    TextView dRlogin, dRlogout, skyLogin, skyLogout, matrixLogout, matrixLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);
        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialize();
    }

    private void initialize() {
        dRlogin = findViewById(R.id.dRlogin);
        dRlogout = findViewById(R.id.dRlogout);

        if (Account.isLoggedIn()) {
            dRlogout.setVisibility(View.VISIBLE);
            dRlogin.setVisibility(View.GONE);
        } else {
            dRlogout.setVisibility(View.GONE);
            dRlogin.setVisibility(View.VISIBLE);
        }

        skyLogin = findViewById(R.id.skylogin);
        skyLogout = findViewById(R.id.skylogout);

        if (Account.isSessionSkyVerified()) {
            skyLogout.setVisibility(View.VISIBLE);
            skyLogin.setVisibility(View.GONE);
        } else {
            skyLogout.setVisibility(View.GONE);
            skyLogin.setVisibility(View.VISIBLE);
        }

        matrixLogin = findViewById(R.id.matrixlogin);
        matrixLogout = findViewById(R.id.matrixlogout);

        if (MatrixAccount.isLoggedIn()) {
            matrixLogout.setVisibility(View.VISIBLE);
            matrixLogin.setVisibility(View.GONE);
        } else {
            matrixLogout.setVisibility(View.GONE);
            matrixLogin.setVisibility(View.VISIBLE);
        }
    }

    public void toggleDrLogin(View view) {
        if (Account.isLoggedIn()) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this)
                    .setTitle("logout")
                    .setMessage("U sure u want to logout :(")
                    .setCancelable(true)

                    .setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    Account.setKey(null);
                                    Account.setExpire_time(0);
                                    Account.setUser_id(0);
                                    Account.setId(0);
                                    Account.setUsername(null);
                                    Account.setSessionSkyVerified(false);
                                    initialize();
                                }
                            })

                    .setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

            AlertDialog alert = builder1.create();
            alert.show();
        } else {
            Intent intent = new Intent(AccountsActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void avatarBuilder(View view) {
        if (Account.isLoggedIn()) {
            Intent intent = new Intent(AccountsActivity.this, BuilderActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(AccountsActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void blockingActivity(View view) {
        Intent intent = new Intent(AccountsActivity.this, BlockActivity.class);
        startActivity(intent);
    }

    public void editProfile(View view) {
        if (Account.isLoggedIn()) {
            Intent intent = new Intent(AccountsActivity.this, EditProfileActivity.class);
            intent.putExtra("user_id",String.valueOf(Account.user_id()));
            startActivity(intent);
        } else {
            Intent intent = new Intent(AccountsActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void toggleSkyLogin(View view) {
        if (Account.isSessionSkyVerified()) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this)
                    .setTitle("logout")
                    .setMessage("U sure u want to logout from sky :(")
                    .setCancelable(true)

                    .setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    Account.setSessionSkyVerified(false);
                                    initialize();
                                }
                            })

                    .setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

            AlertDialog alert = builder1.create();
            alert.show();

        } else {
            if (Account.isLoggedIn()) {
                Intent intent = new Intent(AccountsActivity.this, SkyLoginActivity.class);
                startActivity(intent);
            } else {
                toast("you must login to devRant first");
                Intent intent = new Intent(AccountsActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }

    public void backupRestore(View view) {
        if (Account.isLoggedIn()) {
            if (Account.isSessionSkyVerified()) {
                Intent intent = new Intent(AccountsActivity.this, BackupActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(AccountsActivity.this, SkyLoginActivity.class);
                startActivity(intent);
            }
        } else {
            toast("you must login to devRant first");
            Intent intent = new Intent(AccountsActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }
    public void customize(View view) {
        if (Account.isLoggedIn()) {
            if (Account.isSessionSkyVerified()) {
                Intent intent = new Intent(AccountsActivity.this, SkyCustomActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(AccountsActivity.this, SkyLoginActivity.class);
                startActivity(intent);
            }
        } else {
            toast("you must login to devRant first");
            Intent intent = new Intent(AccountsActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void skyProfile(View view) {
        if (Account.isLoggedIn()) {
            if (Account.isSessionSkyVerified()) {
                Intent intent = new Intent(AccountsActivity.this, SkyActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(AccountsActivity.this, SkyLoginActivity.class);
                startActivity(intent);
            }
        } else {
            toast("you must login to devRant first");
            Intent intent = new Intent(AccountsActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void toggleMatrixLogin(View view) {
        if (MatrixAccount.isLoggedIn()) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this)
                    .setTitle("logout")
                    .setMessage("U sure u want to logout from matrix :(")
                    .setCancelable(true)

                    .setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    MatrixAccount.setAccessToken(null);
                                    MatrixAccount.setExpire_time(0);
                                    MatrixAccount.setUser_id(null);
                                    MatrixAccount.setUsername(null);
                                    initialize();
                                }
                            })

                    .setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

            AlertDialog alert = builder1.create();
            alert.show();
        } else {
            Intent intent = new Intent(AccountsActivity.this, MatrixLoginActivity.class);
            startActivity(intent);
        }
    }

    public void matrixChat(View view) {
        if (!MatrixAccount.isLoggedIn()) {
            Intent intent = new Intent(AccountsActivity.this, MatrixLoginActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(AccountsActivity.this, MatrixChatActivity.class);
            startActivity(intent);
        }
    }
}