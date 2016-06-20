package mx.peta.mod4practica1;

import android.content.Context;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import mx.peta.mod4practica1.Utileria.SystemMsg;

/*
    Se implementa una calculadora en notación polaca porque es mas simple de implementar
    solo se manejan numeros enteros y se limitan a 10 posiciones de entrada para que no ocurra
    overflow en la multiplicaciones
    Como todas las operaciones son con numeros enteros se implementa la división entera y el modulo
    Para la calculadora binaria se va a usar el teclado restringido solo se implementa la operación de
    suma y se usa la misma estructura de la calculadora decimal.
    En la calculadora binaria se manejan 15 digitos en la pantalla.
    Internamente todo se maneja en decimal, solamente la entrada y la salida se manejan en binario
    de acuerdo con el estado de la calculadora

    La caclculadora es una calculadora RPN (Reverse Polish Notation) lo que significa que la siguiente
    operacion
                (3 + 5) * (7 + 9)
    se hace de la forma siguiente
                3 ENTER 5 + 7 ENTER 9 + *
    lo cual deve dar 128.
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

    final static private int maximoNumeroDeDigitosBinario    = 14;
    final static private int getMaximoNumeroDeDigitosDecimal = 10;
    final static private float tamañoTextoPantalla = 40.0f;
    final static private float tamañoTextoPantallaChico = 20.0f;
    private int maximoNumeroDeDigitos; // limitamos el numero de digitos para no manejar overflow
    private int cuantosDigitos = 0;

    private boolean binaria = false; // señala cuando se esta en modo binario, solo hay dos modos binario o decimal

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rpn_calc);
        txtPantalla = (TextView) findViewById(R.id.pantalla);
        display(lastX);
        // txtPantalla.setTextSize(getResources().getDimension(R.dimen.textsize));
        //getResources().getDimension(R.dimen.pantalla_text_size);

        // espacio abajo del teclado para mostrar el stack HP
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
        findViewById(R.id.binario).setOnClickListener(this);
        findViewById(R.id.decimal).setOnClickListener(this);
        findViewById(R.id.lastx).setOnClickListener(this);
        findViewById(R.id.R).setOnClickListener(this);

        /*
            la calculadora nace en modo decinal por lo tanto el boton binario está
            habilitado y el decimal deshabilitado
         */
        findViewById(R.id.decimal).setEnabled(false);
        txtPantalla.setTextSize(tamañoTextoPantalla);
        maximoNumeroDeDigitos = getMaximoNumeroDeDigitosDecimal;
    }

    /*
        En este manejador de eventos se implementa toda la funcionalidad de la calculadora
        Todos los eventos estan relacionados a una tecla.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.CHS: // change sign
                if (cuantosDigitos > 0) {
                    input = - input;
                    display(input);
                } else {
                    X = -X;
                    display(X);
                }
                break;
            case R.id.CLS:  // clear stack
                X = 0;
                Y = 0;
                Z = 0;
                T = 0;
                input = 0;
                cuantosDigitos = 0;
                display(X);
                break;
            case R.id.CLX:  // clear X
                if (cuantosDigitos > 0) {
                    input = 0;
                    cuantosDigitos = 0;
                    display(input);
                } else {
                    X = 0;
                    display(X);
                }
                break;
            case R.id.x_intercambia_y:  // intercambia x y y
                long temp;
                temp = X;
                X = Y;
                Y = temp;
                display(X);
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
                if (X == 0) // division por cero invalida
                    SystemMsg.msg(getApplicationContext(), getResources().getString(R.string.DivInv));
                else {
                    X = Y / X;
                    Y = Z;
                    Z = T;
                }
                display(X);
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
                X = X * Y;          // Desafortunadamente no podemos detectar overflow !!!!!!
                Y = Z;
                Z = T;
                display(X);
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
                display(X);
                break;
            case R.id.cero:
                    capturaDigito(0);
                break;
            case R.id.modulo:
                verificaInput();
                if (X == 0) // division por cero invalida
                    SystemMsg.msg(getApplicationContext(),getResources().getString(R.string.DivInv));
                else {
                    X = Y % X;
                    Y = Z;
                    Z = T;
                }
                display(X);
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
                display(X);
                break;
            case R.id.adicion:
                verificaInput();
                X = X + Y;
                Y = Z;
                Z = T;
                display(X);
                break;
            case R.id.binario:
                binaria = true;
                maximoNumeroDeDigitos = maximoNumeroDeDigitosBinario;
                input = 0;
                cuantosDigitos = 0;
                findViewById(R.id.decimal).setEnabled(true);
                findViewById(R.id.binario).setEnabled(false);
                findViewById(R.id.CHS).setEnabled(false);
                findViewById(R.id.dos).setEnabled(false);
                findViewById(R.id.tres).setEnabled(false);
                findViewById(R.id.cuatro).setEnabled(false);
                findViewById(R.id.cinco).setEnabled(false);
                findViewById(R.id.seis).setEnabled(false);
                findViewById(R.id.siete).setEnabled(false);
                findViewById(R.id.ocho).setEnabled(false);
                findViewById(R.id.nueve).setEnabled(false);
                findViewById(R.id.division).setEnabled(false);
                findViewById(R.id.multiplicasion).setEnabled(false);
                findViewById(R.id.sustraccion).setEnabled(false);
                findViewById(R.id.modulo).setEnabled(false);
                X = Y = Z = T = lastX = 0;
                display(X);
                break;
            case R.id.decimal:
                binaria = false;
                maximoNumeroDeDigitos = getMaximoNumeroDeDigitosDecimal;
                X = Y = Z = T = 0;
                input = 0;
                cuantosDigitos = 0;
                findViewById(R.id.binario).setEnabled(true);
                findViewById(R.id.decimal).setEnabled(false);
                findViewById(R.id.CHS).setEnabled(true);
                findViewById(R.id.dos).setEnabled(true);
                findViewById(R.id.tres).setEnabled(true);
                findViewById(R.id.cuatro).setEnabled(true);
                findViewById(R.id.cinco).setEnabled(true);
                findViewById(R.id.seis).setEnabled(true);
                findViewById(R.id.siete).setEnabled(true);
                findViewById(R.id.ocho).setEnabled(true);
                findViewById(R.id.nueve).setEnabled(true);
                findViewById(R.id.division).setEnabled(true);
                findViewById(R.id.multiplicasion).setEnabled(true);
                findViewById(R.id.sustraccion).setEnabled(true);
                findViewById(R.id.modulo).setEnabled(true);
                display(X);
                break;
            case R.id.lastx:
                T = Z;
                Z = Y;
                Y = X;
                X = lastX;
                input = 0;
                cuantosDigitos = 0;
                display(X);
                break;
            case R.id.R:
                X = Y;
                Y = Z;
                Z = T;
                input = 0;
                cuantosDigitos = 0;
                display(X);
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
        if (binaria) {
            if (cuantosDigitos++ < maximoNumeroDeDigitosBinario) {
                input = input * 2 + d;
            } else
                // Vibra por 100 milisegundos
                mVibrator.vibrate(100);
        } else {
            if (cuantosDigitos++ < maximoNumeroDeDigitos) {
                if (input < 0)
                    input = input * 10 - d;
                else
                    input = input * 10 + d;
            } else {
                // Vibra por 100 milisegundos
                mVibrator.vibrate(100);
            }
        }
        display(input);
    }

    /*
        se usa esta rutina verificar si se esta usando el registro input
        Esta rutina trabaja con side effects unicamente
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

    private void display(long X) {
        String buffer  = new String();
        long exp       = 0;
        double binario = 0;
        long digito    = 0;
        if (binaria) {
            exp     = 0;
            binario = 0;
            while(X != 0){
                digito = X % 2;
                binario = binario + digito * Math.pow(10, exp);
                exp++;
                X = X / 2;
            }
            buffer = String.valueOf((long) binario);
        } else {
            buffer = String.valueOf(X);
        }
        if (buffer.length() > 10) {
            txtPantalla.setTextSize(tamañoTextoPantallaChico);
        } else {
            txtPantalla.setTextSize(tamañoTextoPantalla);
        }
        txtPantalla.setText(buffer);
    }
}
