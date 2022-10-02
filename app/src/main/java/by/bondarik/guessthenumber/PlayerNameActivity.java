package by.bondarik.guessthenumber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PlayerNameActivity extends AppCompatActivity {

    private EditText editPlayerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_name);

        editPlayerName = findViewById(R.id.et_player_name);
        Button btnOk = findViewById(R.id.btn_ok);

        btnOk.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("player_name", editPlayerName.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        });
    }
}