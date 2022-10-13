package by.bondarik.guessthenumber;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView showHint;
    private TextView showAttemptsLeft;
    private TextView showPlayerName;
    private TextView showGuess;

    private EditText editNum;

    private Button btnGuess;
    private Button btnRestart;

    private int hiddenNumber;

    private int currentDifficulty;

    private int maxAttempts = 5;
    private int minNumber = 10;
    private int maxNumber = 99;

    private int currentAttempts;
    private String currentHint;

    private String currentPlayerName = "Super player";

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Угадай число. onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentDifficulty = savedInstanceState != null ? savedInstanceState.getInt("currentDifficulty") : 2;
        currentHint = savedInstanceState != null ? savedInstanceState.getString("currentHint") : getString(R.string.show_hint_label) + " " + currentDifficulty;

        showHint = findViewById(R.id.show_hint);
        showHint.setText(currentHint);

        currentAttempts = savedInstanceState != null ? savedInstanceState.getInt("currentAttempts") : maxAttempts;

        showAttemptsLeft = findViewById(R.id.show_attempts_left);
        showAttemptsLeft.append(" ");
        showAttemptsLeft.append(Integer.toString(currentAttempts));

        showPlayerName = findViewById(R.id.show_player_name);
        showPlayerName.append(" ");
        showPlayerName.append(savedInstanceState != null ? savedInstanceState.getString("currentPlayerName") : currentPlayerName);

        showGuess = findViewById(R.id.show_msg);
        registerForContextMenu(showGuess);

        editNum = findViewById(R.id.edit_num);
        btnGuess = findViewById(R.id.btn_guess);
        btnRestart = findViewById(R.id.btn_restart);

        hiddenNumber = savedInstanceState != null ? savedInstanceState.getInt("hiddenNumber") : NumberGenerator.generate(10, 99);

        ActivityResultLauncher<Intent> playerNameActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (result.getData() != null) {
                            currentPlayerName = result.getData().getStringExtra("player_name");
                            showPlayerName.setText(R.string.show_player_name_label);
                            showPlayerName.append(" ");
                            showPlayerName.append(currentPlayerName);
                        }
                    }
                });

        showPlayerName.setOnClickListener(v -> {
            Intent playerNameActivityIntent = new Intent(this, PlayerNameActivity.class);
            playerNameActivityResultLauncher.launch(playerNameActivityIntent);
        });

        editNum.setOnClickListener(v -> editNum.selectAll());

        btnGuess.setOnClickListener(v -> {
            editNum.selectAll();
            try {
                int inputNumber = Integer.parseInt(editNum.getText().toString());

                if (inputNumber == hiddenNumber) {
                    btnGuess.setText(R.string.btn_guessed_label);
                    btnGuess.setEnabled(false);

                    Toast tCongratulation = Toast.makeText(this, R.string.t_congratulation_label, Toast.LENGTH_LONG);
                    tCongratulation.show();

                    sendEndGameResult(true);
                }
                else {
                    currentHint = inputNumber > hiddenNumber ? getString(R.string.show_hint_less_label) : getString(R.string.show_hint_greater_label);
                    showHint.setText(currentHint);

                    currentAttempts--;

                    showAttemptsLeft.setText(R.string.show_attempts_left_label);
                    showAttemptsLeft.append(" ");
                    showAttemptsLeft.append(Integer.toString(currentAttempts));

                    if (currentAttempts == 0) {
                        btnGuess.setText(R.string.btn_not_guessed_label);
                        btnGuess.setEnabled(false);

                        sendEndGameResult(false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnRestart.setOnClickListener(v -> {
            currentHint = getString(R.string.show_hint_label) + " " + currentDifficulty;
            showHint.setText(currentHint);

            showAttemptsLeft.setText((R.string.show_attempts_left_label));
            showAttemptsLeft.append(" ");
            showAttemptsLeft.append(Integer.toString(maxAttempts));
            currentAttempts = maxAttempts;

            btnGuess.setText(R.string.btn_guess_label);

            hiddenNumber = NumberGenerator.generate(minNumber, maxNumber);

            btnGuess.setEnabled(true);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(this);
            builder.setContentTitle("Walter White")
                    .setContentText(getString(R.string.notification_content))
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_walter_white_notification)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

            NotificationChannel channel = new NotificationChannel("1","waltuh_channel", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(true);
            manager.createNotificationChannel(channel);
            builder.setChannelId("1");

            Notification notification = builder.build();
            manager.notify(1, notification);
        });
    }

    private void sendEndGameResult(boolean isGuessed) {
        StringBuilder endGameResultBuilder = new StringBuilder();

        endGameResultBuilder.append("Date: ");
        endGameResultBuilder.append(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(new Date()));

        if (isGuessed) {
            endGameResultBuilder.append("\nAttempt number: ");
            endGameResultBuilder.append(maxAttempts - currentAttempts + 1);
        }

        endGameResultBuilder.append("\nNumber: ");
        endGameResultBuilder.append(hiddenNumber);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setPackage("com.miui.notes");

        sendIntent.putExtra(Intent.EXTRA_TEXT, endGameResultBuilder.toString());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);

        sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, endGameResultBuilder.toString());
        sendIntent.setType("text/plain");

        shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Log.i(LOG_TAG, "Угадай число. onSaveInstanceState()");

        super.onSaveInstanceState(outState);

        outState.putInt("hiddenNumber", hiddenNumber);
        outState.putInt("currentDifficulty", currentDifficulty);
        outState.putInt("maxAttempts", maxAttempts);
        outState.putInt("minNumber", minNumber);
        outState.putInt("maxNumber", maxNumber);
        outState.putInt("currentAttempts", currentAttempts);
        outState.putString("currentHint", currentHint);
        outState.putString("currentPlayerName", currentPlayerName);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Угадай число. onRestoreInstanceState()");

        super.onRestoreInstanceState(savedInstanceState);

        minNumber = savedInstanceState.getInt("minNumber");
        maxNumber = savedInstanceState.getInt("maxNumber");
    }

    @Override
    protected void onStart() {
        Log.i(LOG_TAG, "Угадай число. onStart()");

        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.i(LOG_TAG, "Угадай число. onResume()");

        super.onResume();
    }

    @Override
    protected void onRestart() {
        Log.i(LOG_TAG, "Угадай число. onRestart()");

        super.onRestart();
    }

    @Override
    protected void onStop() {
        Log.i(LOG_TAG, "Угадай число. onStop()");

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(LOG_TAG, "Угадай число. onDestroy()");

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_settings) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(R.string.ab_settings);

            builder.setNegativeButton(R.string.ab_btn_cancel, (dialog, id) -> { });

            builder.setItems(R.array.diaps_array, (dialog, which) -> {
                currentDifficulty = which + 2;

                switch (which) {
                    case 0:
                        maxAttempts = 5;
                        minNumber = 10;
                        maxNumber = 99;
                        break;
                    case 1:
                        maxAttempts = 7;
                        minNumber = 100;
                        maxNumber = 999;
                        break;
                    case 2:
                        maxAttempts = 10;
                        minNumber = 1000;
                        maxNumber = 9999;
                        break;
                }

                btnRestart.performClick();

                showHint.setText(R.string.show_hint_label);
                showHint.append(" ");
                showHint.append(Integer.toString(which + 2));
            });

            builder.create();
            builder.show();
            editNum.setText("");
        }
        else if (itemId == R.id.menu_about) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setComponent(new ComponentName("by.bondarik.helloworld","by.bondarik.helloworld.MainActivity"));
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_color_green) {
            showGuess.setTextColor(Color.GREEN);
        }
        else if (itemId == R.id.menu_color_red) {
            showGuess.setTextColor(Color.RED);
        }

        return super.onContextItemSelected(item);
    }
}