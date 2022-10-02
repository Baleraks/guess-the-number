package by.bondarik.guessthenumber;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
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

    private EditText editNum;

    private Button btnGuess;
    private Button btnRestart;

    private int hiddenNumber;

    private int maxAttempts = 5;
    private int minNumber = 10;
    private int maxNumber = 99;

    private int currentAttempts;

    private String currentPlayerName = "Super player";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showHint = findViewById(R.id.show_hint);
        showAttemptsLeft = findViewById(R.id.show_attempts_left);
        showAttemptsLeft.append(" ");
        showAttemptsLeft.append(Integer.toString(maxAttempts));

        showPlayerName = findViewById(R.id.show_player_name);
        showPlayerName.append(" ");
        showPlayerName.append(currentPlayerName);

        editNum = findViewById(R.id.edit_num);
        btnGuess = findViewById(R.id.btn_guess);
        btnRestart = findViewById(R.id.btn_restart);
        Button btnSettings = findViewById(R.id.btn_settings);

        currentAttempts = maxAttempts;

        hiddenNumber = NumberGenerator.generate(10, 99);

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

        View.OnClickListener clickPlayerName = view -> {
            Intent playerNameActivityIntent = new Intent(this, PlayerNameActivity.class);
            playerNameActivityResultLauncher.launch(playerNameActivityIntent);
        };

        View.OnClickListener editNumFocus = view -> editNum.selectAll();

        View.OnClickListener clickGuess = view -> {
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
                    showHint.setText(inputNumber > hiddenNumber ? R.string.show_hint_less_label : R.string.show_hint_greater_label);
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
        };

        View.OnClickListener clickRestart = view -> {
            showHint.setText(R.string.show_hint_label);

            showAttemptsLeft.setText((R.string.show_attempts_left_label));
            showAttemptsLeft.append(" ");
            showAttemptsLeft.append(Integer.toString(maxAttempts));
            currentAttempts = maxAttempts;

            btnGuess.setText(R.string.btn_guess_label);

            hiddenNumber = NumberGenerator.generate(minNumber, maxNumber);

            btnGuess.setEnabled(true);
        };

        View.OnClickListener clickSettings = view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(R.string.ab_settings);

            builder.setNegativeButton(R.string.ab_btn_cancel, (dialog, id) -> { });

            builder.setItems(R.array.diaps_array, (dialog, which) -> {
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
            });

            builder.create();
            builder.show();
        };

        showPlayerName.setOnClickListener(clickPlayerName);
        editNum.setOnClickListener(editNumFocus);
        btnGuess.setOnClickListener(clickGuess);
        btnRestart.setOnClickListener(clickRestart);
        btnSettings.setOnClickListener(clickSettings);
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

        /*sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, endGameResultBuilder.toString());
        sendIntent.setType("text/plain");

        shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);*/
    }
}