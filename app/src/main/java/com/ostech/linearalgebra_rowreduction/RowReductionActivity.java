package com.ostech.linearalgebra_rowreduction;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.*;
import com.ostech.linearalgebra_rowreduction.model.Matrix;
import org.apache.commons.math3.exception.MathParseException;
import org.apache.commons.math3.fraction.*;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class RowReductionActivity extends AppCompatActivity {
    private static final String TAG = "RowReductionActivity";

    private Spinner reductionFormSelector;
    private Spinner reductionRowNumberSelector;
    private Spinner reductionColumnNumberSelector;
    
    private TableLayout rowReductionMatrixLayout;

    private Button rowReductionSolveButton;

    private LinearLayout resultLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_row_reduction);

        reductionFormSelector = findViewById(R.id.reduction_form_selector);
        reductionRowNumberSelector = findViewById(R.id.row_reduction_row_number_selector);
        reductionColumnNumberSelector = findViewById(R.id.row_reduction_column_number_selector);

        rowReductionMatrixLayout = findViewById(R.id.row_reduction_matrix_layout);

        rowReductionSolveButton = findViewById(R.id.row_reduction_solve_button);

        resultLayout = findViewById(R.id.result_layout);

        reductionRowNumberSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateMatrixLayout();
                resultLayout.removeAllViews();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        reductionColumnNumberSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateMatrixLayout();
                resultLayout.removeAllViews();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        rowReductionSolveButton.setOnClickListener(v -> performReduction());
    }   //  end of onCreate()

    private void updateMatrixLayout() {
        int numberOfRows = Integer.parseInt(reductionRowNumberSelector.getSelectedItem().toString());
        int numberOfColumns = Integer.parseInt(reductionColumnNumberSelector.getSelectedItem().toString());

        rowReductionMatrixLayout.removeAllViews();

        GradientDrawable inputBackground = getInputBackground();

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams();
        layoutParams.setMargins(15, 15, 15, 15);
        layoutParams.width = WRAP_CONTENT;
        layoutParams.height = WRAP_CONTENT;

        for (int i = 1; i <= numberOfRows; i++) {
            TableRow currentRow = new TableRow(this);

            for (int j = 1; j <= numberOfColumns; j++) {
                currentRow.addView(getMatrixElementInput(inputBackground), layoutParams);
            }   //  end of for j

            rowReductionMatrixLayout.addView(currentRow);
        }   //  end of for i
    }   //  end of updateMatrixLayout()

    private GradientDrawable getInputBackground() {
        GradientDrawable inputBackground = new GradientDrawable();
        inputBackground.setStroke(2, Color.BLACK);
        inputBackground.setShape(GradientDrawable.RECTANGLE);
        return inputBackground;
    }   //  end of getInputBackground()

    private EditText getMatrixElementInput(GradientDrawable inputBackground) {
        EditText matrixElementInput = new EditText(this);

        matrixElementInput.setPadding(10, 10, 10, 10);
        matrixElementInput.setHint("0");
        matrixElementInput.setTextColor(Color.BLACK);
        matrixElementInput.setHintTextColor(Color.BLACK);
        matrixElementInput.setBackground(inputBackground);
        matrixElementInput.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL |
                InputType.TYPE_NUMBER_FLAG_SIGNED);

        return matrixElementInput;
    }   //  end of getMatrixElementInput()

    private void performReduction() {
        resultLayout.removeAllViews();
        TextView solutionHeader = new TextView(this);
        solutionHeader.setText(R.string.solution);
        solutionHeader.setTextSize(34);
        resultLayout.addView(solutionHeader);

        int numberOfRows = rowReductionMatrixLayout.getChildCount();
        int numberOfColumns = ((TableRow) rowReductionMatrixLayout.getChildAt(0)).getChildCount();

        Matrix matrix = new Matrix(numberOfRows, numberOfColumns);

        setMatrixElements(matrix);

        int reductionForm = reductionFormSelector.getSelectedItemPosition();
        final int REDUCED_ECHELON_FORM = 0;
        final int ROW_REDUCED_ECHELON_FORM = 1;

        switch (reductionForm) {
            case REDUCED_ECHELON_FORM:
                matrix.getReducedEchelonForm(resultLayout, this);
                break;

            case ROW_REDUCED_ECHELON_FORM:
                matrix.getRowReducedEchelonForm(resultLayout, this);
                break;
        }   //  end of switch for reduction form
    }   //  end of performReduction()

    private void setMatrixElements(Matrix matrix) {
        for (int i = 0; i < rowReductionMatrixLayout.getChildCount(); i++) {
            TableRow currentRow = (TableRow) rowReductionMatrixLayout.getChildAt(i);

            for (int j = 0; j < currentRow.getChildCount(); j++) {
                String matrixElementInputText = ((EditText) currentRow.getChildAt(j)).getText().toString();
                BigFractionFormat format = new BigFractionFormat();

                if (matrixElementInputText.length() == 0) {
                    matrix.elements[i][j] = BigFraction.ZERO;
                } else {
                    try {
                        BigFraction fraction = format.parse(matrixElementInputText);

                        matrix.elements[i][j] = fraction;
                    } catch (MathParseException e) {
                        e.printStackTrace();

                        try {
                            Fraction temporaryFraction = new Fraction(Double.parseDouble(matrixElementInputText));
                            BigFraction fraction = new BigFraction(temporaryFraction.getNumerator(),
                                    temporaryFraction.getDenominator());

                            matrix.elements[i][j] = fraction;
                        } catch (FractionConversionException e1) {
                            BigFraction temporaryFraction = new BigFraction(Double.parseDouble(matrixElementInputText));
                            BigFraction fraction = new BigFraction(temporaryFraction.getNumerator(),
                                    temporaryFraction.getDenominator());

                            matrix.elements[i][j] = fraction;
                        } catch (NumberFormatException e1) {
                            e1.printStackTrace();
                        }   //  end of NumberFormatException catch
                    }   //  end of MathParseException catch
                }   //  end of else
            }   //  end of for i
        }   //  end of for i
    }   //  end of setMatrixElements()
}   //  end of class