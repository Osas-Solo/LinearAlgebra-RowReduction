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

public class SimultaneousEquationsFragment extends Fragment {
    private static final String TAG = "RowReductionActivity";

    private Spinner unknownVariablesNumberSelector;

    private TableLayout simultaneousEquationsMatrixLayout;

    private Button simultaneousEquationsSolveButton;

    private LinearLayout simultaneousEquationsResultLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_simultaneous_equations, container, false);

        unknownVariablesNumberSelector = view.findViewById(R.id.unknown_variables_number_selector);

        simultaneousEquationsMatrixLayout = view.findViewById(R.id.simultaneous_equations_matrix_layout);

        simultaneousEquationsSolveButton = view.findViewById(R.id.simultaneous_equations_solve_button);

        simultaneousEquationsResultLayout = view.findViewById(R.id.simultaneous_equations_result_layout);

        unknownVariablesNumberSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateMatrixLayout();
                simultaneousEquationsResultLayout.removeAllViews();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        simultaneousEquationsSolveButton.setOnClickListener(v -> solveSimultaneousEquations());

        return view;
    }   //  end of onCreateView()

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(R.string.simultaneous_menu_item_long_title);
    }

    private void updateMatrixLayout() {
        int numberOfUnknownVariables = Integer.parseInt(unknownVariablesNumberSelector.getSelectedItem().toString());

        simultaneousEquationsMatrixLayout.removeAllViews();

        GradientDrawable inputBackground = getInputBackground();

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams();
        layoutParams.setMargins(15, 15, 15, 15);
        layoutParams.width = WRAP_CONTENT;
        layoutParams.height = WRAP_CONTENT;

        updateUnknownVariableNames(numberOfUnknownVariables, layoutParams);

        for (int i = 1; i <= numberOfUnknownVariables; i++) {
            TableRow currentRow = new TableRow(getActivity());

            for (int j = 1; j <= numberOfUnknownVariables + 1; j++) {
                currentRow.addView(getMatrixElementInput(inputBackground), layoutParams);

                if (j == numberOfUnknownVariables) {
                    currentRow.addView(getAugmentedMatrixSeparator(), layoutParams);
                }   //  end of if i == numberOfUnknownVariables
            }   //  end of for j

            simultaneousEquationsMatrixLayout.addView(currentRow);
        }   //  end of for i
    }   //  end of updateMatrixLayout()

    private void updateUnknownVariableNames(int numberOfUnknownVariables, TableRow.LayoutParams layoutParams) {
        TableRow unknownVariableNameRow = new TableRow(getActivity());

        for (int i = 1; i <= numberOfUnknownVariables; i++) {
            TextView unknownVariableName = new TextView(getActivity());
            unknownVariableName.setText("x" + i);
            unknownVariableName.setTextSize(20);

            unknownVariableNameRow.addView(unknownVariableName, layoutParams);
        }   //  end of for j

        simultaneousEquationsMatrixLayout.addView(unknownVariableNameRow);
    }   //  end of updateUnknownVariableNames()

    private TextView getAugmentedMatrixSeparator() {
        TextView augmentedMatrixSeparator = new TextView(getActivity());
        augmentedMatrixSeparator.setText("| ");
        augmentedMatrixSeparator.setTextSize(20);

        return augmentedMatrixSeparator;
    }   //  end of getAugmentedMatrixSeparator()

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

    private void solveSimultaneousEquations() {
        simultaneousEquationsResultLayout.removeAllViews();
        TextView solutionHeader = new TextView(getActivity());
        solutionHeader.setText(R.string.solution);
        solutionHeader.setTextSize(34);
        simultaneousEquationsResultLayout.addView(solutionHeader);

        int numberOfRows = simultaneousEquationsMatrixLayout.getChildCount() - 1;
        int numberOfColumns = ((TableRow) simultaneousEquationsMatrixLayout.getChildAt(1)).
                                                                            getChildCount() - 1;

        Matrix matrix = new Matrix(numberOfRows, numberOfColumns);

        setMatrixElements(matrix);

        matrix.setAugmentedWithSolution(true);

        matrix.solveSimultaneousEquations(simultaneousEquationsResultLayout, getActivity());
    }   //  end of performReduction()

    private void setMatrixElements(Matrix matrix) {
        for (int i = 1; i < simultaneousEquationsMatrixLayout.getChildCount(); i++) {
            TableRow currentRow = (TableRow) simultaneousEquationsMatrixLayout.getChildAt(i);

            int matrixColumnCount = 0;

            for (int j = 0; j < currentRow.getChildCount(); j++) {
                if (j == currentRow.getChildCount() - 2) {
                    continue;
                }

                String matrixElementInputText = ((EditText) currentRow.getChildAt(j)).getText().toString();
                BigFractionFormat format = new BigFractionFormat();

                if (matrixElementInputText.length() == 0) {
                    matrix.elements[i - 1][matrixColumnCount] = BigFraction.ZERO;
                } else {
                    try {
                        BigFraction fraction = format.parse(matrixElementInputText);

                        matrix.elements[i - 1][matrixColumnCount] = fraction;
                    } catch (MathParseException e) {
                        e.printStackTrace();

                        try {
                            Fraction temporaryFraction = new Fraction(Double.parseDouble(matrixElementInputText));
                            BigFraction fraction = new BigFraction(temporaryFraction.getNumerator(),
                                    temporaryFraction.getDenominator());

                            matrix.elements[i - 1][matrixColumnCount] = fraction;
                        } catch (FractionConversionException e1) {
                            BigFraction temporaryFraction = new BigFraction(Double.parseDouble(matrixElementInputText));
                            BigFraction fraction = new BigFraction(temporaryFraction.getNumerator(),
                                    temporaryFraction.getDenominator());

                            matrix.elements[i - 1][matrixColumnCount] = fraction;
                        } catch (NumberFormatException e1) {
                            e1.printStackTrace();
                        }   //  end of NumberFormatException catch
                    }   //  end of MathParseException catch
                }   //  end of else

                matrixColumnCount++;
            }   //  end of for j
        }   //  end of for i
    }   //  end of setMatrixElements()
}   //  end of class
