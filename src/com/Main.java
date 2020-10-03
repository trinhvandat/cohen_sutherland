package com;

import java.util.Scanner;

public class Main {

    private static int INSIDE = 0; // 0000
    private static int LEFT = 1; // 0001
    private static int RIGHT = 2; // 0010
    private static int BOTTOM = 4; // 0100
    private static int TOP = 8; // 1000

    // Defining x_max, y_max and x_min, y_min for
// clipping rectangle. Since diagonal points are
// enough to define a rectangle
    private static double z_max;
    private static double x_max;
    private static double z_min;
    private static double x_min;

    private static int computeCode(double z, double x){
        int code = INSIDE;
        if (z < z_min) // to the left of rectangle
            code |= LEFT;
        else if (z > z_max) // to the right of rectangle
            code |= RIGHT;
        else if (x < x_min) // below the rectangle
            code |= BOTTOM;
        else if (x > x_max) // above the rectangle
            code |= TOP;
        return code;
    }

    private static double getDiagonal(double wight, double height){

        double diagonal;
        diagonal = (Math.sqrt((Math.pow(wight, 2) + Math.pow(height, 2))) /2);
        return diagonal;
    }


    // Implementing Cohen-Sutherland algorithm
// Clipping a line from P1 = (z2, z2) to P2 = (z2, z2)
    private static void cohenSutherlandClip(double z1, double x1,
                             double z2, double x2)
    {
        // Compute region codes for P1, P2
        int code1 = computeCode(z1, x1);
        int code2 = computeCode(z2, x2);

        // Initialize line as outside the rectangular window
        boolean accept = false;

        while (true) {
            if ((code1 == 0) && (code2 == 0)) {
                // If both endpoints lie within rectangle
                accept = true;
                break;
            }
            else if ((code1 & code2) != 0) {
                // If both endpoints are outside rectangle,
                // in same region
                break;
            }
            else {
                // Some segment of line lies within the
                // rectangle
                int code_out;
                double z = 0, x = 0;

                // At least one endpoint is outside the
                // rectangle, pick it.
                if (code1 != 0)
                    code_out = code1;
                else
                    code_out = code2;

                // Find intersection point;
                // using formulas x = x1 + slope * (z - z1),
                // z = z1 + (1 / slope) * (x - x1)
                if ((code_out & TOP) == 8) {
                    // point is above the clip rectangle
                    z = z1 + (z2 - z1) * (x_max - x1) / (x2 - x1);
                    x = x_max;


                }
                else if ((code_out & BOTTOM) == 4) {
                    // point is below the rectangle
                    z = z1 + (z2 - z1) * (x_min - x1) / (x2 - x1);
                    x = x_min;
                }
                else if ((code_out & RIGHT) == 2) {
                    // point is to the right of rectangle
                    x = x1 + (x2 - x1) * (z_max - z1) / (z2 - z1);
                    z = z_max;
                }
                else if ((code_out & LEFT) == 1) {
                    // point is to the left of rectangle
                    x = x1 + (x2 - x1) * (z_min - z1) / (z2 - z1);
                    z = z_min;
                }

                // Now intersection point z, x is found
                // We replace point outside rectangle
                // by intersection point

                if (code_out == code1) {
                    z1 = z;
                    x1 = x;
                    code1 = computeCode(z1, x1);
                }
                else {
                    z2 = z;
                    x2 = x;
                    code2 = computeCode(z2, x2);
                }
            }
        }
        if (accept) {
            System.out.println("Line accepted from " + z1  + ", " + x1 + " to " + z2 + ", " + x2);
            // Here the user can add code to display the rectangle
            // along with the accepted (portion of) lines
        }
        else
            System.out.println("Line rejected");
    }

    private static double[][] getPointMatrix(double diagonal){
        double[][] pointMatrix = new double[4][3];

        pointMatrix[0][0] = diagonal;
        pointMatrix[0][1] = 0;
        pointMatrix[0][2] = 1;

        pointMatrix[1][0] = 0;
        pointMatrix[1][1] = diagonal;
        pointMatrix[1][2] = 1;

        pointMatrix[2][0] = 0 - diagonal;
        pointMatrix[2][1] = 0;
        pointMatrix[2][2] = 1;

        pointMatrix[3][0] = 0;
        pointMatrix[3][1] = 0 - diagonal;
        pointMatrix[3][2] = 1;

        return pointMatrix;
    }

    private static void showMatrix(double [][] matrix){
        System.out.println("Ma trận : ");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(matrix[i][j] + "\t");
            }
            System.out.println("\n");
        }
    }

    private static double[][] getRotationMatrix(double alpha){
        double alphaPi =  ((alpha / 180) * Math.PI);
        double[][] rotationMatrix = new double[3][3];

        rotationMatrix[0][0] = Math.cos(alphaPi);
        rotationMatrix[0][1] = Math.sin(alphaPi);
        rotationMatrix[0][2] = 0;

        rotationMatrix[1][0] = Math.sin(0 - alphaPi);
        rotationMatrix[1][1] = Math.cos(alphaPi);
        rotationMatrix[1][2] = 0;

        rotationMatrix[2][0] = 0;
        rotationMatrix[2][1] = 0;
        rotationMatrix[2][2] = 1;

        return rotationMatrix;
    }


    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        double wight;
        double height;
        double alpha;

        System.out.print("wight = "); wight = input.nextDouble();
        System.out.print("height = "); height = input.nextDouble();
        System.out.print("alpha = "); alpha = input.nextDouble();

        z_max = wight / 2;
        z_min = - wight / 2;
        x_min = - height / 2;
        x_max = height / 2;


        double diagonal = getDiagonal(wight, height);

        double[][] pointMatrix = getPointMatrix(diagonal);
        double[][] rotationMatrix = getRotationMatrix(alpha);

        /*
        nhan ma tran diem voi ma tran quay
         */
        double[][] result = new double[4][3];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                result[i][j] = 0;
                for (int k = 0; k < 3; k++) {
                    result[i][j] = result[i][j] + pointMatrix[i][k] * rotationMatrix[k][j];
                }
            }
        }


        cohenSutherlandClip(result[0][1], result[0][0], result[2][1], result[2][0]);

        cohenSutherlandClip(result[1][1], result[1][0], result[3][1], result[3][0]);


    }
}
