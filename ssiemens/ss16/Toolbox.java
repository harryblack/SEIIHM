package ssiemens.ss16;

import java.util.Arrays;

class Toolbox {
    // ############################
    // ### Methods to implement ###
    // ############################
    private static int evenSum(int n) {
        int sum = 0;
        boolean isEven = checkIfEven(0, n, true);

        if (n >= 0) {
            if (n != 0) {
                if (isEven) {
                    sum += n;
                    n = n - 2;
                } else {
                    n--;
                }
                sum += evenSum(n);
            }
        } else {
            if (isEven) {
                sum += n;
                n = n + 2;
            } else {
                n++;
            }
            sum += evenSum(n);
        }
        return sum;
    }

    private static int multiplication(int x, int y) {
        final boolean yIsPositive = y >= 0;

        int result = 0;
        if (y != 0) {
            if (yIsPositive) {
                result += x;
            } else {
                result -= x;
            }

            if (y >= 0) {
                result += multiplication(x, y - 1);
            } else {
                result += multiplication(x, y + 1);
            }
        }
        return result;
    }

    private static void reverse(int[] m) {
        if (m.length == 0) {
            throw new IllegalArgumentException("ERROR: Array is empty!");
        }

        int startIndex = 0;
        int endIndex = m.length - 1;
        reverse(m, startIndex, endIndex);       // Call helper method (see below)
    }

    private static int numberOfOddIntegers(int[] m) {
        if (m.length == 0) {
            throw new IllegalArgumentException("ERROR: Array is empty!");
        }

        int index = m.length - 1;
        int counter = 0;
        return numberOfOddIntegers(m, index, counter);  // Call helper method (see below)
    }

    private static int[] filterOdd(int[] m) {
        final int lengthOfResultArray = numberOfOddIntegers(m);
        int[] result = new int[lengthOfResultArray];
        filterOdd(m, result, 0, 0);     // Call helper method (see below)

        return result;
    }

    // #######################
    // ### Main            ###
    // #######################
    public static void main(String[] args) {
        System.out.println("evenSum: " + evenSum(5));
        System.out.println("multiplication: " + multiplication(4, -3));

        // Reverse
        int[] m = new int[]{0, 1, 2, 3, 4, 5};
        reverse(m);
        System.out.println("reverse: " + Arrays.toString(m));

        //numberOfOddIntegers
        int[] n = new int[]{4, 7, 42, 5, 1, -5, 0, -4, -3};
        System.out.println("numberOfOddIntegers: " + numberOfOddIntegers(n));

        //filterOdd
        int[] o = new int[]{4, 7, 42, 5, 1, -5, 0, -4, -3};
        int[] arrayWithFilteredOdd = filterOdd(o);
        System.out.println("filterOdd: " + Arrays.toString(arrayWithFilteredOdd));
    }

    // #######################
    // ### Helping methods ###
    // #######################
    private static boolean checkIfEven(int counter, int n, boolean isEven) {
        if (counter != n) {
            if (n > 0) {
                counter++;
            } else {
                counter--;
            }
            isEven = checkIfEven(counter, n, !isEven);
        }
        return isEven;
    }

    private static void reverse(int[] m, int startIndex, int endIndex) {
        if (startIndex < endIndex) {
            int temp = m[startIndex];
            m[startIndex] = m[endIndex];
            m[endIndex] = temp;

            startIndex++;
            endIndex--;
            reverse(m, startIndex, endIndex);
        }
    }

    private static int numberOfOddIntegers(int[] m, int index, int counter) {
        if (index >= 0) {
            if (!checkIfEven(0, m[index], true)) {
                counter++;
            }
            index--;
            counter = numberOfOddIntegers(m, index, counter);
        }
        return counter;
    }

    private static void filterOdd(int[] m, int[] result, int indexM, int indexResult) {
        if (indexM < m.length) {
            if (!checkIfEven(0, m[indexM], true)) {
                result[indexResult] = m[indexM];
                indexResult++;
            }
            indexM++;
            filterOdd(m, result, indexM, indexResult);
        }
    }
}
