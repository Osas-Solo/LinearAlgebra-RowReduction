package com.ostech.linearalgebra_rowreduction;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.*;
import android.widget.*;

import androidx.fragment.app.Fragment;

import com.ostech.linearalgebra_rowreduction.model.Matrix;

import org.apache.commons.math3.exception.MathParseException;
import org.apache.commons.math3.fraction.*;

import static android.view.ViewGroup.LayoutParams.*;

public class InverseFragment extends Fragment {
    private static final String TAG = "RowReductionActivity";

    private Spinner inverseDimensionSelector;

    private TableLayout inverseMatrixLayout;

    private Button inverseSolveButton;

    private LinearLayout inverseResultLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inverse, container, false);

        inverseDimensionSelector = view.findViewById(R.id.inverse_dimension_selector);

        inverseMatrixLayout = view.findViewById(R.id.inverse_matrix_layout);

        inverseSolveButton = view.findViewById(R.id.inverse_solve_button);

        inverseResultLayout = view.findViewById(R.id.inverse_result_layout);

        inverseDimensionSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateMatrixLayout();
                inverseResultLayout.removeAllViews();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        inverseSolveButton.setOnClickListener(v -> getInverse());

        return view;
    }   //  end of onCreateView()

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(R.string.inverse_menu_item_title);
    }

    private void updateMatrixLayout() {
        int numberOfRowAndColumns = inverseDimensionSelector.getSelectedItemPosition() + 1;

        inverseMatrixLayout.removeAllViews();

        GradientDrawable inputBackground = getInputBackground();

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams();
        layoutParams.setMargins(15, 15, 15, 15);
        layoutParams.width = WRAP_CONTENT;
        layoutParams.height = WRAP_CONTENT;

        for (int i = 1; i <= numberOfRowAndColumns; i++) {
            TableRow currentRow = new TableRow(getActivity());

            for (int j = 1; j <= numberOfRowAndColumns; j++) {
                currentRow.addView(getMatrixElementInput(inputBackground), layoutParams);
            }   //  end of for j

            inverseMatrixLayout.addView(currentRow);
        }   //  end of for i
    }   //  end of updateMatrixLayout()

    private GradientDrawable getInputBackground() {
        GradientDrawable inputBackground = new GradientDrawable();
        inputBackground.setStroke(2, Color.BLACK);
        inputBackground.setShape(GradientDrawable.RECTANGLE);
        return inputBackground;
    }   //  end of getInputBackground()

    private EditText getMatrixElementInput(GradientDrawable inputBackground) {
        EditText matrixElementInput = new EditText(getActivity());

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

    private void getInverse() {
        inverseResultLayout.removeAllViews();
        TextView solutionHeader = new TextView(getActivity());
        solutionHeader.setText(R.string.solution);
        solutionHeader.setTextSize(34);
        inverseResultLayout.addView(solutionHeader);

        int numberOfRows = inverseMatrixLayout.getChildCount();
        int numberOfColumns = ((TableRow) inverseMatrixLayout.getChildAt(0)).getChildCount() * 2;

        Matrix matrix = new Matrix(numberOfRows, numberOfColumns);

        setMatrixElements(matrix);

        matrix.setAugmentedWithInverse(true);

        matrix.getInverse(inverseResultLayout, getActivity());
    }   //  end of getInverse()

    private void setMatrixElements(Matrix matrix) {
        for (int i = 0; i < inverseMatrixLayout.getChildCount(); i++) {
            TableRow currentRow = (TableRow) inverseMatrixLayout.getChildAt(i);

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
            }   //  end of for j

            int squareDimensionNumber = matrix.getNumberOfRows();
            for (int j = matrix.getNumberOfRows(); j < matrix.getNumberOfColumns(); j++) {
                if (i == (j - squareDimensionNumber)) {
                    matrix.elements[i][j] = BigFraction.ONE;
                } else {
                    matrix.elements[i][j] = BigFraction.ZERO;
                }
            }
        }   //  end of for i
    }   //  end of setMatrixElements()
}   //  end of class
