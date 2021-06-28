package com.ostech.linearalgebra_rowreduction.model;

import org.apache.commons.math3.fraction.*;

import android.content.Context;
import android.os.Build;

import android.view.View;
import android.widget.*;

public class Matrix {
    private int numberOfRows;
    private int numberOfColumns;

    private boolean isAugmentedWithSolution;
    private boolean isAugmentedWithInverse;

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    public boolean isAugmentedWithSolution() {
        return isAugmentedWithSolution;
    }

    public void setAugmentedWithSolution(boolean isAugmentedWithSolution) {
        this.isAugmentedWithSolution = (numberOfColumns == numberOfRows + 1) && isAugmentedWithSolution;
    }

    public boolean isAugmentedWithInverse() {
        return isAugmentedWithInverse;
    }

    public void setAugmentedWithInverse(boolean isAugmentedWithInverse) {
        this.isAugmentedWithInverse = numberOfColumns == 2 * numberOfRows;
    }

    public BigFraction[][] elements;

    private final BigFraction ZERO = BigFraction.ZERO;
    private final BigFraction ONE = BigFraction.ONE;
    private final BigFraction MINUS_ONE = new BigFraction(-1, 1);

    public Matrix(int numberOfRows, int numberOfColumns) {
        this.numberOfRows = numberOfRows;
        this.numberOfColumns = numberOfColumns;

        this.elements = new BigFraction[numberOfRows][numberOfColumns];
    }  //  end of constructor


    public void getRowReducedEchelonForm(LinearLayout resultLayout, Context context) {
        getReducedEchelonForm(resultLayout, context);

        String operations;

        //  go through rows in reverse
        for (int i = elements.length - 1; i > 0; i--) {
            operations = "";
            int pivotColumn = getPivotColumn(elements[i]);

            if (pivotColumn != -1) {
                //  add rows
                for (int j = i - 1; j >= 0; j--) {
                    if (!elements[j][pivotColumn].equals(ZERO)) {
                        operations += "R" + (j + 1) + " = R" + (j + 1) + " + ("
                                + toMoreReadableFraction(elements[j][pivotColumn].multiply(MINUS_ONE))
                                + ") R" + (i + 1) + "\n";

                        addRows(elements[i], elements[j]);
                    }  //  end of if
                }  //  end of for loop to add rows

                if (operations != "") {
                    resultLayout.addView(getCurrentSolution("\n\n\n" + operations,
                            false, context));
                    resultLayout.addView(displayMatrix(context));
                }   //  end of if operations is not empty
            }   //  end of if pivotColumn is not -1
        }  //  end of for loop to go through rows
    }  //  end of getRowReducedEchelonForm

    public void getReducedEchelonForm(LinearLayout resultLayout, Context context) {
        resultLayout.addView(getCurrentSolution("Original Matrix:",
                false, context));
        resultLayout.addView(displayMatrix(context));

        String operations = "";

        //  go through each row
        for (int i = 0; i < elements.length - 1; i++) {
            operations = "";

            BigFraction pivot = getPivot(elements[i]);
            int pivotColumn = getPivotColumn(elements[i]);

            //  interchange rows where possible
            if (!pivot.equals(ONE)) {
                for (int j = i + 1; j < elements.length; j++) {
                    if (pivotColumn != -1) {
                        if (elements[j][pivotColumn].equals(ONE) ||
                                (getPivotColumn(elements[j]) < pivotColumn &&
                                        !getPivot(elements[j]).equals(ZERO))) {
                            operations += "R" + (i + 1) + " <--> " + "R" + (j + 1) + "\n";
                            interchangeRows(elements[i], elements[j]);

                            resultLayout.addView(getCurrentSolution("\n\n\n" + operations,
                                    false, context));
                            resultLayout.addView(displayMatrix(context));
                            break;
                        }  //  end of if
                    } else if (pivotColumn == -1) {
                        if (!getPivot(elements[j]).equals(ZERO)) {
                            operations += "R" + (i + 1) + " <--> " + "R" + (j + 1) + "\n";
                            interchangeRows(elements[i], elements[j]);

                            resultLayout.addView(getCurrentSolution("\n\n\n" + operations,
                                    false, context));
                            resultLayout.addView(displayMatrix(context));
                            break;
                        }   //  end of if pivot of element at index j != 0
                    }   //  end of else if pivotColumn == -1
                }  //   end of j for loop
            }  //  end of if to interchange rows

            pivot = getPivot(elements[i]);
            pivotColumn = getPivotColumn(elements[i]);

            if (pivotColumn != -1) {
                operations = "";
                //  divide row by pivot
                if (!pivot.equals(ONE)) {
                    operations += "(R" + (i + 1) + ") / (" + toMoreReadableFraction(pivot) + ")\n";
                    divideRowByPivot(elements[i]);

                    resultLayout.addView(getCurrentSolution("\n\n\n" + operations,
                            false, context));
                    resultLayout.addView(displayMatrix(context));
                }  //  end of if to divide rows

                operations = "";
                //  add rows
                for (int j = i + 1; j < elements.length; j++) {
                    if (!elements[j][pivotColumn].equals(ZERO)) {
                        operations += "R" + (j + 1) + " = R" + (j + 1) + " + ("
                                + toMoreReadableFraction(elements[j][pivotColumn].multiply(MINUS_ONE))
                                + ") R" + (i + 1) + "\n";

                        addRows(elements[i], elements[j]);
                    }  //  end of if element at pivot column is not 0
                }  //  end of for loop to add rows
            }   //  end of if pivotColumn != -1

            if (operations != "") {
                resultLayout.addView(getCurrentSolution("\n\n\n" + operations,
                        false, context));
                resultLayout.addView(displayMatrix(context));
            }   //  end of if operations is not empty
        }  //  end of for loop to go through rows

        //  reduce last row
        BigFraction[] lastRow = elements[elements.length - 1];
        BigFraction pivot = getPivot(lastRow);

        if (!pivot.equals(ONE) && !pivot.equals(ZERO)) {
            operations = "";
            operations += "(R" + elements.length + ") / (" + toMoreReadableFraction(pivot) + ")\n";
            divideRowByPivot(lastRow);
            resultLayout.addView(getCurrentSolution("\n\n\n" + operations,
                    false, context));
            resultLayout.addView(displayMatrix(context));
        }  //  end of if
    }  //  end of getReducedEchelonForm()

    private void interchangeRows(BigFraction[] row1, BigFraction[] row2) {
        BigFraction[] temporaryRow = new BigFraction[row1.length];
        for (int i = 0; i < row1.length; i++) {
            temporaryRow[i] = row1[i];
            row1[i] = row2[i];
            row2[i] = temporaryRow[i];
        }  //  end of for
    }  //  end of interchangeRows()

    private void divideRowByPivot(BigFraction[] row) {
        BigFraction pivot = getPivot(row);

        for (int i = 0; i < row.length; i++) {
            if (row[i].compareTo(ZERO) != 0) {
                row[i] = row[i].divide(pivot);
            }  //  end of if
        }  //  end of for
    }  //  end of divideRowByPivot()

    private void addRows(BigFraction[] currentRow, BigFraction[] otherRow) {
        BigFraction pivot = otherRow[getPivotColumn(currentRow)];
        pivot = pivot.negate();

        for (int i = 0; i < otherRow.length; i++) {
            if (!currentRow[i].equals(ZERO) || !otherRow[i].equals(ZERO)) {
                otherRow[i] = otherRow[i].add(pivot.multiply(currentRow[i]));
            }  //  end of if
        }  //  end of for
    }  //  end of addRows()

    private BigFraction getPivot(BigFraction[] row) {
        BigFraction pivot = ZERO;

        //  find pivot
        for (BigFraction currentElement: row) {
            if (!currentElement.equals(ZERO)) {
                pivot = currentElement;
                break;
            }  //  end of if
        }  //  end of for to find pivot

        return pivot;
    }  //  end of getPivot()

    private int getPivotColumn(BigFraction[] row) {
        int pivotColumn = -1;

        //  find pivot
        for (int j = 0; j < row.length; j++) {
            if (!row[j].equals(ZERO)) {
                pivotColumn = j;
                break;
            }   //  end of if
        }  //  end of for loop to find pivot

        return pivotColumn;
    }   //   end of getPivotColumn()

    public void solveSimultaneousEquations(LinearLayout resultLayout, Context context) {
        if (isAugmentedWithSolution()) {
            getRowReducedEchelonForm(resultLayout, context);

            if (hasUniqueSolution()) {
                BigFraction[] solutions = getSolutions();

                resultLayout.addView(getCurrentSolution("The solution to this system of " +
                                                                "equations is:", false,
                                                                context));

                for (int i = 0; i < solutions.length; i++) {
                    resultLayout.addView(getCurrentSolution("x" + (i + 1) + " = " + ((solutions[i].getDenominatorAsInt() == 1) ?
                                                                solutions[i].getNumerator() :
                                                                solutions[i]),
                                                    false, context));

                    if (i < solutions.length - 1) {
                        if (i == solutions.length - 2) {
                            resultLayout.addView(getCurrentSolution(" and ",
                                    false, context));
                        } else {
                            resultLayout.addView(getCurrentSolution(", ",
                                    false, context));
                        }   //  end of else
                    }   //  end of if
                }   //  end of for
            } else {
                resultLayout.addView(getCurrentSolution("This system of equations has no " +
                                                                "unique solution",
                                            false, context));
            }
        } else {
            resultLayout.addView(getCurrentSolution("Matrix is not an augmented matrix",
                                                            false, context));
        }
    }   //  end of solveSimultaneousEquations

    private BigFraction[] getSolutions() {
        int numberOfEquations = numberOfRows;

        BigFraction[] solutions = new BigFraction[numberOfEquations];

        if (isAugmentedWithSolution()) {
            for (int i = 0; i < numberOfEquations; i++) {
                solutions[i] = elements[i][numberOfColumns - 1];
            }   //  end of for
        }   //  end of if

        return solutions;
    }   //  end of getSolutions()

    public boolean hasUniqueSolution() {
        int numberOfEquations = numberOfRows;
        int numberOfVariables = numberOfColumns - 1;

        for (int i = 0; i < numberOfEquations; i++) {
            if (!elements[i][i].equals(ONE)) {
                return false;
            }   //  end of if

            for (int j = 0; j < numberOfVariables; j++) {
                if (j == i) {
                    continue;
                }   //  end of if j == 1

                if (!elements[i][j].equals(ZERO)) {
                    return false;
                }   //  end of if element is not zero
            }   //  end of for j
        }   //  end of for i

        return true;
    }   //  end of haveUniqueSolution

    public void getInverse(LinearLayout resultLayout, Context context) {
        if (isAugmentedWithInverse()) {
            getRowReducedEchelonForm(resultLayout, context);

            Matrix inverseMatrix = new Matrix(numberOfRows, numberOfColumns / 2);

            for (int i = 0; i < inverseMatrix.numberOfRows; i++) {
                for (int j = 0; j < inverseMatrix.numberOfColumns; j++) {
                    inverseMatrix.elements[i][j] = this.elements[i][j + inverseMatrix.numberOfColumns];
                }   //  end of for j
            }   //  end of for i

            resultLayout.addView(getCurrentSolution("Inverse of the matrix is:",
                                        false, context));
            resultLayout.addView(inverseMatrix.displayMatrix(context));
        } else {
            resultLayout.addView(getCurrentSolution("This matrix is not a square matrix",
                                                    false, context));
        }   //  end of else
    }   //  end of getInverse()

    public void getRank(LinearLayout resultLayout, Context context) {
        getRowReducedEchelonForm(resultLayout, context);

        int numberOfNonZeroRows = getNumberOfNonZeroRows();

        resultLayout.addView(getCurrentSolution("The rank of this matrix is " + numberOfNonZeroRows
                            + " since it has " + numberOfNonZeroRows + " non-zero rows",
                            false, context));
    }   //  end of getRank()

    private int getNumberOfNonZeroRows() {
        int numberOfZeroRows = 0;

        for (int i = numberOfRows - 1; i >= 0; i--) {
            for (int j = 0; j < numberOfColumns; j++) {
                if (!elements[i][j].equals(ZERO)) {
                    numberOfZeroRows++;
                    break;
                }   //  end of if element is not 0
            }   //  end of for j
        }   //  end of for i

        return numberOfZeroRows;
    }   //  end of getNumberOfNonZeroRows()

    public void checkLinearDependency(LinearLayout resultLayout, Context context) {
        getRowReducedEchelonForm(resultLayout, context);

        boolean hasUniqueSolution = hasUniqueSolution();

        if (hasUniqueSolution) {
            resultLayout.addView(getCurrentSolution("This system is linearly independent since ",
                                                    false, context));
        } else {
            resultLayout.addView(getCurrentSolution("This system is linearly dependent since ",
                                                    false, context));
        }

        for (int i = 1; i < numberOfColumns; i++) {
            resultLayout.addView(getCurrentSolution("x" + i + ((hasUniqueSolution) ? " = "
                                                    : " is not equals "), false, context));
        }

        resultLayout.addView(getCurrentSolution(" 0", false, context));
    }   //  end of checkLinearDependency()

    public TableLayout displayMatrix(Context context) {
        TableLayout matrix = new TableLayout(context);

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams();
        layoutParams.setMargins(15, 0, 15, 0);

        if (isAugmentedWithSolution()) {
            TableRow headerRow = new TableRow(context);
            int numberOfVariables = numberOfColumns - 1;

            for (int i = 0; i < numberOfVariables; i++) {
                headerRow.addView(getCurrentSolution("x" + (i + 1), false, context),
                                    layoutParams);
            }   //  end of for

            matrix.addView(headerRow);
        }   //  end of if isAugmentedWithSolution

        for (int i = 0; i < elements.length; i++) {
            TableRow currentRow = new TableRow(context);

            for (int j = 0; j < elements[i].length; j++) {
                currentRow.addView(getCurrentSolution(toMoreReadableFraction(elements[i][j]),
                        true, context), layoutParams);

                if (isAugmentedWithSolution()) {
                    int numberOfVariables = numberOfColumns - 1;

                    if (j == numberOfVariables - 1) {
                        currentRow.addView(getCurrentSolution("| ", true, context),
                                            layoutParams);
                    }   //  end of if j == numberOfVariables - 1
                } else if (isAugmentedWithInverse()) {
                    int inverseMatrixStartIndex = (numberOfColumns / 2) - 1;

                    if (j == inverseMatrixStartIndex) {
                        currentRow.addView(getCurrentSolution("| ", true, context),
                                            layoutParams);
                    }
                }   //  end of else if isAugmentedWithInverse()
            }   //  end of for j

            matrix.addView(currentRow);
        }   //  end of for i

        return matrix;
    }  //  end of toString()

    private String toMoreReadableFraction(BigFraction fraction) {
        return "" + ((fraction.getDenominatorAsInt()== 1) ? fraction.getNumerator() :
                fraction.getNumerator() + "/" + fraction.getDenominator());
    }   //  end of toMoreReadableFraction()

    private TextView getCurrentSolution(String solution, boolean isAlignedCentre, Context context) {
        TextView textView = new TextView(context);
        textView.setPadding(10, 10, 10, 10);
        textView.setTextSize(17);

        if (isAlignedCentre) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        }

        textView.setText(solution);

        return textView;
    }   //  end of getCurrentSolution()
}   //  end of class