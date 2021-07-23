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

public class RankFragment extends Fragment {
    private static final String TAG = "RowReductionActivity";

    private Spinner rankRowNumberSelector;
    private Spinner rankColumnNumberSelector;

    private TableLayout rankMatrixLayout;

    private Button rankSolveButton;

    private LinearLayout rankResultLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rank, container, false);

        rankRowNumberSelector = view.findViewById(R.id.rank_row_number_selector);
        rankColumnNumberSelector = view.findViewById(R.id.rank_column_number_selector);

        rankMatrixLayout = view.findViewById(R.id.rank_matrix_layout);

        rankSolveButton = view.findViewById(R.id.rank_solve_button);

        rankResultLayout = view.findViewById(R.id.rank_result_layout);

        rankRowNumberSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateMatrixLayout();
                rankResultLayout.removeAllViews();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        rankColumnNumberSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateMatrixLayout();
                rankResultLayout.removeAllViews();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        rankSolveButton.setOnClickListener(v -> getRank());

        return view;
    }   //  end of onCreateView()

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(R.string.rank_menu_item_title);
    }

    private void updateMatrixLayout() {
        int numberOfRows = Integer.parseInt(rankRowNumberSelector.getSelectedItem().toString());
        int numberOfColumns = Integer.parseInt(rankColumnNumberSelector.getSelectedItem().toString());

        rankMatrixLayout.removeAllViews();

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

            rankMatrixLayout.addView(currentRow);
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

    private void getRank() {
        rankResultLayout.removeAllViews();
        TextView solutionHeader = new TextView(getActivity());
        solutionHeader.setText(R.string.solution);
        solutionHeader.setTextSize(34);
        rankResultLayout.addView(solutionHeader);

        int numberOfRows = rankMatrixLayout.getChildCount();
        int numberOfColumns = ((TableRow) rankMatrixLayout.getChildAt(0)).getChildCount();

        Matrix matrix = new Matrix(numberOfRows, numberOfColumns);

        setMatrixElements(matrix);

        matrix.getRank(rankResultLayout, getActivity());
    }   //  end of getRank()

    private void setMatrixElements(Matrix matrix) {
        for (int i = 0; i < rankMatrixLayout.getChildCount(); i++) {
            TableRow currentRow = (TableRow) rankMatrixLayout.getChildAt(i);

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
