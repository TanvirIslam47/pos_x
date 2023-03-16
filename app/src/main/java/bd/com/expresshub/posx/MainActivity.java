package bd.com.expresshub.posx;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import bd.com.expresshub.posx.print.BluetoothCheck;

public class MainActivity extends AppCompatActivity {

    Button completeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        completeBtn = findViewById(R.id.btn_payment);

        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BluetoothCheck.class);
                startActivity(intent);
            }
        });
    }
}