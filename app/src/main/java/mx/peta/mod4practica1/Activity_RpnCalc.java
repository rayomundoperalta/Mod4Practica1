package mx.peta.mod4practica1;

import android.content.Context;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/*
    Se implementa una calculadora en notación polaca porque esmas simple de implementar
    solo se manejan numeros enteros y se limitan a 10 posiciones de entrada para que no ocurra
    overflow en la multiplicaciones
    Todas las operaciones son con numeros enteros por lo cual se implementa la división y el modulo
 */
public class Activity_RpnCalc extends AppCompatActivity implements View.OnClickListener {

    Vibrator mVibrator;

    private long registroX = 0;
    private TextView txtRegistroX;
    final private int maximoNumeroDeDigitos = 10; // limitamos el numero de digitos para no manejar overflow
    private int cuantosDigitos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rpn_calc);
        txtRegistroX = (TextView) findViewById(R.id.registroX);
        txtRegistroX.setText(String.valueOf(registroX));
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Asignamos los event handlers a los botones
        findViewById(R.id.CHS).setOnClickListener(this);
        findViewById(R.id.CLS).setOnClickListener(this);
        findViewById(R.id.CLX).setOnClickListener(this);
        findViewById(R.id.C).setOnClickListener(this);
        findViewById(R.id.siete).setOnClickListener(this);
        findViewById(R.id.ocho).setOnClickListener(this);
        findViewById(R.id.nueve).setOnClickListener(this);
        findViewById(R.id.division).setOnClickListener(this);
        findViewById(R.id.cuatro).setOnClickListener(this);
        findViewById(R.id.cinco).setOnClickListener(this);
        findViewById(R.id.seis).setOnClickListener(this);
        findViewById(R.id.multiplicasion).setOnClickListener(this);
        findViewById(R.id.uno).setOnClickListener(this);
        findViewById(R.id.dos).setOnClickListener(this);
        findViewById(R.id.tres).setOnClickListener(this);
        findViewById(R.id.sustraccion).setOnClickListener(this);
        findViewById(R.id.cero).setOnClickListener(this);
        findViewById(R.id.modulo).setOnClickListener(this);
        findViewById(R.id.enter).setOnClickListener(this);
        findViewById(R.id.adicion).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.CHS:
                registroX = - registroX;
                break;
            case R.id.CLS:
                registroX      = 0;
                cuantosDigitos = 0;
                break;
            case R.id.CLX:
                registroX      = 0;
                cuantosDigitos = 0;
                break;
            case R.id.C:
                break;
            case R.id.siete:
                capturaDigito(7);
                break;
            case R.id.ocho:
                capturaDigito(8);
                break;
            case R.id.nueve:
                capturaDigito(9);
                break;
            case R.id.division:
                break;
            case R.id.cuatro:
                capturaDigito(4);
                break;
            case R.id.cinco:
                capturaDigito(5);
                break;
            case R.id.seis:
                capturaDigito(6);
                break;
            case R.id.multiplicasion:
                break;
            case R.id.uno:
                capturaDigito(1);
                break;
            case R.id.dos:
                capturaDigito(2);
                break;
            case R.id.tres:
                capturaDigito(3);
                break;
            case R.id.sustraccion:
                break;
            case R.id.cero:
                capturaDigito(0);
                break;
            case R.id.modulo:
                break;
            case R.id.enter:
                break;
            case R.id.adicion:
                break;
        }
        txtRegistroX.setText(String.valueOf(registroX));
    }

    private void capturaDigito(int d) {
        if (cuantosDigitos++ < maximoNumeroDeDigitos) {
            if (registroX < 0)
                registroX = registroX * 10 - d;
            else
                registroX = registroX * 10 + d;
        } else {
            // Vibra por 100 milisegundos
            mVibrator.vibrate(100);
        }
    }
}
