package by.bondarik.guessthenumber;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView showHint;
    private TextView showAttemptsLeft;

    private EditText editNum;

    private Button btnGuess;
    private Button btnRestart;

    private int hiddenNumber;

    private static final int MAX_ATTEMPTS = 5;
    private int currentAttempts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showHint = findViewById(R.id.show_hint);
        showAttemptsLeft = findViewById(R.id.show_attempts_left);
        showAttemptsLeft.append(" ");
        showAttemptsLeft.append(Integer.toString(MAX_ATTEMPTS));
        editNum = findViewById(R.id.edit_num);
        btnGuess = findViewById(R.id.btn_guess);
        btnRestart = findViewById(R.id.btn_restart);

        currentAttempts = MAX_ATTEMPTS;

        hiddenNumber = NumberGenerator.generate(10, 99);

        View.OnClickListener editNumFocus = view -> editNum.selectAll();

        View.OnClickListener clickGuess = view -> {
            try {
                int inputNumber = Integer.parseInt(editNum.getText().toString());

                if (inputNumber == hiddenNumber) {
                    btnGuess.setText(R.string.btn_guessed_label);
                    btnGuess.setEnabled(false);

                    Toast tCongratulation = Toast.makeText(this, R.string.t_congratulation_label, Toast.LENGTH_LONG);
                    tCongratulation.show();
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
            showAttemptsLeft.append(Integer.toString(MAX_ATTEMPTS));
            currentAttempts = MAX_ATTEMPTS;

            btnGuess.setText(R.string.btn_guess_label);

            hiddenNumber = NumberGenerator.generate(10, 99);

            btnGuess.setEnabled(true);
        };

        editNum.setOnClickListener(editNumFocus);
        btnGuess.setOnClickListener(clickGuess);
        btnRestart.setOnClickListener(clickRestart);
    }

    public void editNumClick(View view) {
        editNum.selectAll();
    }
}