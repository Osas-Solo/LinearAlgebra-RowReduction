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

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class RowReductionFragment extends Fragment {
    private static final String TAG = "RowReductionActivity";

    private Spinner reductionFormSelector;
    private Spinner reductionRowNumberSelector;
    private Spinner reductionColumnNumberSelector;

    private TableLayout rowReductionMatrixLayout;

    private Button rowReductionSolveButton;

    private LinearLayout rowReductionResultLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_row_reduction, container, false);

        reductionFormSelector = view.findViewById(R.id.reduction_form_selector);
        reductionRowNumberSelector = view.findViewById(R.id.row_reduction_row_number_selector);
        reductionColumnNumberSelector = view.findViewById(R.id.row_reduction_column_number_selector);

        rowReductionMatrixLayout = view.findViewById(R.id.row_reduction_matrix_layout);

        rowReductionSolveButton = view.findViewById(R.id.row_reduction_solve_button);

        rowReductionResultLayout = view.findViewById(R.id.row_reduction_result_layout);

        reductionRowNumberSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateMatrixLayout();
                rowReductionResultLayout.removeAllViews();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        reductionColumnNumberSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateMatrixLayout();
                rowReductionResultLayout.removeAllViews();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        rowReductionSolveButton.setOnClickListener(v -> performReduction());

        return view;
    }   //  end of onCreateView()

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(R.string.row_reduction_menu_item_title);
    }

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
            TableRow currentRow = new TableRow(getActivity());

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

    private void performReduction() {
        rowReductionResultLayout.removeAllViews();
        TextView solutionHeader = new TextView(getActivity());
        solutionHeader.setText(R.string.solution);
        solutionHeader.setTextSize(34);
        rowReductionResultLayout.addView(solutionHeader);

        int numberOfRows = rowReductionMatrixLayout.getChildCount();
        int numberOfColumns = ((TableRow) rowReductionMatrixLayout.getChildAt(0)).getChildCount();

        Matrix matrix = new Matrix(numberOfRows, numberOfColumns);

        setMatrixElements(matrix);

        int reductionForm = reductionFormSelector.getSelectedItemPosition();
        final int REDUCED_ECHELON_FORM = 0;
        final int ROW_REDUCED_ECHELON_FORM = 1;

        switch (reductionForm) {
            case REDUCED_ECHELON_FORM:
                matrix.getReducedEchelonForm(rowReductionResultLayout, getActivity());
                break;

            case ROW_REDUCED_ECHELON_FORM:
                matrix.getRowReducedEchelonForm(rowReductionResultLayout, getActivity());
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
            }   //  end of for j
        }   //  end of for i
    }   //  end of setMatrixElements()
}   //  end of class
