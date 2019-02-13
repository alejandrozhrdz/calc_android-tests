package com.bitdevelopertech.ez_dummie_calculator;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.etInput)
    EditText etInput;
    @BindView(R.id.rlMain)
    RelativeLayout rlMain;

    private boolean isEditInProgress = false;
    private int minLength;
    private int textSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        minLength = getResources().getInteger(R.integer.main_min_length);
        textSize = getResources().getInteger(R.integer.main_input_textSize);
        configEditText();
    }

    private void configEditText() {
//        etInput.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
////                input.hideSoftInputFromWindow(view.getWindowToken(), 0);
////            }
////        });

        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                if(!isEditInProgress &&
                Metodos.canReplaceOperator(charSequence)){
                    isEditInProgress = true;
                    etInput.getText().delete(etInput.getText().length()-2, etInput.getText().length()-1);
                }

                if (charSequence.length() > minLength){
                    etInput.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize -
                            (((charSequence.length() - minLength)*2)+ (charSequence.length() - minLength)) );
                } else {
                    etInput.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                isEditInProgress = false;
            }
        });



        etInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    if(motionEvent.getRawX() >= (etInput.getRight() -
                            etInput.getCompoundDrawables()[Constantes.DRAWABLE_RIGHT].getBounds().width())){
                        if(etInput.length() > 0){

                            final int lenght = etInput.getText().length();
                            etInput.getText().delete(lenght-1, lenght);
                        }
                    }
                    return true;
                }
                return false;
            }
        });
    }


    @OnClick({R.id.btnSeven, R.id.btnFour, R.id.btnOne, R.id.btnPoint, R.id.btnEight, R.id.btnFive,
            R.id.btnTwo, R.id.btnZero, R.id.btnNine, R.id.btnSix, R.id.btnTree})
    public void onClickNumbers(View view) {
        final String valStr = ((Button) view).getText().toString();
        switch (view.getId()) {
            case R.id.btnZero:
            case R.id.btnOne:
            case R.id.btnTwo:
            case R.id.btnTree:
            case R.id.btnFour:
            case R.id.btnFive:
            case R.id.btnSix:
            case R.id.btnSeven:
            case R.id.btnEight:
            case R.id.btnNine:
                etInput.getText().append(valStr);
                break;
            case R.id.btnPoint:
                final String operacion = etInput.getText().toString();

                final String operador = Metodos.getOperator(operacion);

                final int count = operacion.length() - operacion.replace(".", "").length();

                if (!operacion.contains(Constantes.POINT) ||
                        (count < 2 && (!operador.equals(Constantes.OPERATOR_NULL)))) {
                    etInput.getText().append(valStr);
                }
                break;
        }
    }

    @OnClick({R.id.btnClear, R.id.btnDiv, R.id.btnMult, R.id.btnMinus, R.id.btnSum, R.id.btnResult})
    public void onClickControls(View view) {
        switch (view.getId()) {
            case R.id.btnClear:
                etInput.setText("");
                break;
            case R.id.btnDiv:
            case R.id.btnMult:
            case R.id.btnMinus:
            case R.id.btnSum:
                resolve(false);


                final String operador = ((Button)view).getText().toString();
                final String operacion = etInput.getText().toString();
                final String ultimoCaracter = operacion.isEmpty() ? "" :
                        operacion.substring(operacion.length() - 1);

                if(operador.equals(Constantes.OPERATOR_SUB)){
                    if (operacion.isEmpty() ||
                        (!(ultimoCaracter.equals(Constantes.OPERATOR_SUB)) &&
                                !(ultimoCaracter.equals(Constantes.POINT)))){
                            etInput.getText().append(operador);
                    }

                } else {
                    if(!operacion.isEmpty() &&
                            !(ultimoCaracter.equals(Constantes.OPERATOR_SUB)) &&
                            !(ultimoCaracter.equals(Constantes.POINT))){

                                etInput.getText().append(operador);
                    }
                }
                break;

            case R.id.btnResult:
                resolve(true);
                break;
        }
    }

    private void resolve(boolean fromResult) {

        Metodos.tryResolve(fromResult, etInput, new OnResolveCallback() {
            @Override
            public void onShowMessage(int errorRes) {
                showMessage(errorRes);
            }

            @Override
            public void onIsEditing() {
                    isEditInProgress = true;
            }
        });

    }

    private void showMessage(int errorRes) {
        Snackbar.make(rlMain, errorRes, Snackbar.LENGTH_SHORT).show();
    }
}
