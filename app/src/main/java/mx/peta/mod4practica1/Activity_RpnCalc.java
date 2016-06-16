package mx.peta.mod4practica1;

import android.content.Context;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import mx.peta.mod4practica1.Utileria.SystemMsg;

/*
    Se implementa una calculadora en notación polaca porque esmas simple de implementar
    solo se manejan numeros enteros y se limitan a 10 posiciones de entrada para que no ocurra
    overflow en la multiplicaciones
    Como todas las operaciones son con numeros enteros se implementa la división entera y el modulo
 */
public class Activity_RpnCalc extends AppCompatActivity implements View.OnClickListener {

    Vibrator mVibrator;

    private long input = 0;
    /* HP stack */
    private long lastX = 0;
    private long X = 0;
    private long Y = 0;
    private long Z = 0;
    private long T = 0;

    private TextView txtPantalla;
    private TextView pantallaT;
    private TextView pantallaZ;
    private TextView pantallaY;
    private TextView pantallaX;
    private TextView pantallaLastX;

    final private int maximoNumeroDeDigitos = 10; // limitamos el numero de digitos para no manejar overflow
    private int cuantosDigitos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rpn_calc);
        txtPantalla = (TextView) findViewById(R.id.pantalla);
        txtPantalla.setText(String.valueOf(lastX));
        pantallaT = (TextView) findViewById(R.id.T);
        pantallaZ = (TextView) findViewById(R.id.Z);
        pantallaY = (TextView) findViewById(R.id.Y);
        pantallaX = (TextView) findViewById(R.id.X);
        pantallaLastX = (TextView) findViewById(R.id.lastX);

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Asignamos los event handlers a los botones
        findViewById(R.id.CHS).setOnClickListener(this);
        findViewById(R.id.CLS).setOnClickListener(this);
        findViewById(R.id.CLX).setOnClickListener(this);
        findViewById(R.id.x_intercambia_y).setOnClickListener(this);
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

    /*
        En este manejador de eventos se implementa toda la funcionalidad de la calculadora
        Todos los eventos estan relacionados a una tecla.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.CHS:
                if (cuantosDigitos > 0) {
                    input = - input;
                    txtPantalla.setText(String.valueOf(input));
                } else {
                    X = -X;
                    txtPantalla.setText(String.valueOf(X));
                }
                break;
            case R.id.CLS:
                X = 0;
                Y = 0;
                Z = 0;
                T = 0;
                input = 0;
                cuantosDigitos = 0;
                txtPantalla.setText(String.valueOf(X));
                break;
            case R.id.CLX:
                if (cuantosDigitos > 0) {
                    input = 0;
                    cuantosDigitos = 0;
                    txtPantalla.setText(String.valueOf(input));
                } else {
                    X = 0;
                    txtPantalla.setText(String.valueOf(X));
                }
                break;
            case R.id.x_intercambia_y:
                long temp;
                temp = X;
                X = Y;
                Y = temp;
                txtPantalla.setText(String.valueOf(X));
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
                verificaInput();
                if (X == 0)
                    SystemMsg.msg(getApplicationContext(), "división por cero invalida");
                else
                    X = Y / X;
                Y = Z;
                Z = T;
                txtPantalla.setText(String.valueOf(X));
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
                verificaInput();
                X = X * Y;
                Y = Z;
                Z = T;
                txtPantalla.setText(String.valueOf(X));
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
                verificaInput();
                X = Y - X;
                Y = Z;
                Z = T;
                txtPantalla.setText(String.valueOf(X));
                break;
            case R.id.cero:
                capturaDigito(0);
                break;
            case R.id.modulo:
                verificaInput();
                if (X == 0)
                    SystemMsg.msg(getApplicationContext(),"División por cero invalida");
                else
                    X = Y % X;
                Y = Z;
                Z = T;
                txtPantalla.setText(String.valueOf(X));
                break;
            case R.id.enter:
                if (cuantosDigitos > 0) {
                    T = Z;
                    Z = Y;
                    Y = X;
                    X = input;
                    input = 0;
                    cuantosDigitos = 0;
                } else {
                    T = Z;
                    Z = Y;
                    Y = X;
                }
                txtPantalla.setText(String.valueOf(X));
                break;
            case R.id.adicion:
                verificaInput();
                X = X + Y;
                Y = Z;
                Z = T;
                txtPantalla.setText(String.valueOf(X));
                break;
        }
        /*
            Mostramos abajo del teclado el comportamiento del stack HP para verificar su funcionamiento
            algunas de las operaciones que ocurren en el stack no se pueden mostrar ya que ocurren
            internamente en la máquina y esta visualización no las ve
         */
        pantallaLastX.setText(String.valueOf(lastX));
        pantallaT.setText(String.valueOf(T));
        pantallaZ.setText(String.valueOf(Z));
        pantallaY.setText(String.valueOf(Y));
        pantallaX.setText(String.valueOf(X));
    }

    /*
        Esta función implementa la captura de digitos, utiliza un regisro dedicado y verifica
        si el numero es mayor a diez posiciones, cuando esto ocurre vibra para avizar que
        se alcanzó el límite
     */
    private void capturaDigito(int d) {
        if (cuantosDigitos++ < maximoNumeroDeDigitos) {
            if (input < 0)
                input = input * 10 - d;
            else
                input = input * 10 + d;
        } else {
            // Vibra por 100 milisegundos
            mVibrator.vibrate(100);
        }
        txtPantalla.setText(String.valueOf(input));
    }

    /*
        se usa esta rutina verificar si se esta usando el registro input
     */
    private void verificaInput() {
        if (cuantosDigitos > 0) { // el usuario ha tecleado un numero
            T = Z;
            Z = Y;
            Y = X;
            X = input;
            lastX = X;
            input = 0;
            cuantosDigitos = 0;
        } else
            lastX = X;
    }
}
