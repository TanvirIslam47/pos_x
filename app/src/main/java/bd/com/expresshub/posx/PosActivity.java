package bd.com.expresshub.posx;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import bd.com.expresshub.posx.print.BluetoothCheck;

public class PosActivity extends AppCompatActivity {

    Button paymentBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pos);

        paymentBtn = findViewById(R.id.btn_bill_payment);

        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PosActivity.this, BluetoothCheck.class);
                startActivity(intent);
            }
        });
    }
}