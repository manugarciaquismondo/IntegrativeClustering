package org.feffermanlab.epidemics.symmetrychecker;
import java.util.*;
/**
 * A class to check if a matrix is symmetric
 * @author manu_
 *
 */
public class SymmetryMatrix{
    /**
     * Display a matrix
     * @param A The matrix to display
     */
    public static void display( int A[][] ){
        for( int row=0; row < A.length; row++ ){
            System.out.print( "\t\t\t" );
            for( int col=0; col < A.length; col++ ){
                System.out.print( A[row][col]+"\t" );
            }
            System.out.println();
        }
    }
 
    /**
     * Check if an integer matrix is symmetric
     * @param A the matrix to check
     * @return True if {@code A} is symmetric, false otherwise.
     */
    public static boolean isSymmetric( int A[][] ){
        for( int row=0; row < A.length; row++ ){
            for( int col=0; col < row; col++ ){
                if( A[row][col] != A[col][row] ){
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Check if a float matrix is symmetric
     * @param A the matrix to check
     * @return True if {@code A} is symmetric, false otherwise.
     */
    public static boolean isSymmetric( float A[][] ){
        for( int row=0; row < A.length; row++ ){
            for( int col=0; col < row; col++ ){
                if( A[row][col] != A[col][row] ){
                    return false;
                }
            }
        }
        return true;
    }
 
    /**
     * Compute and display the sum of the left and right diagonal
     * @param A the input matrix
     */
    public static void computeAndDisplaySumOfDiagonals( int A[][] ){
        int sumOfLeftDiagonal=0, sumOfRightDiagonal=0;
        for( int i=0; i < A.length; i++ ){
            sumOfLeftDiagonal += A[i][i];
            sumOfRightDiagonal += A[i][A.length -i -1];
        }
        System.out.println( "The sum of the left diagonal = " + sumOfLeftDiagonal );
        System.out.println( "The sum of the right diagonal = " + sumOfRightDiagonal );
    }
 
    public static void main( String args[] ){
        int M;
        Scanner sc = new Scanner( System.in );
        System.out.print( "M = " );
        M = sc.nextInt();
        if(M <= 2 || M >= 10){
            System.out.println( "OUTPUT:\t\tTHE MATRIX SIZE IS OUT OF RANGE" );
        }else{
            int A[][] = new int[M][M];
            for( int row=0; row < M; row++ ){
                for( int col=0; col < M; col++ ){
                    A[row][col] = sc.nextInt();
                }
            }
            System.out.println( "ORIGINAL MATRIX" );
            display(A);
            if( isSymmetric(A) ) System.out.println( "THE GIVEN MATRIX IS SYMMETRIC" );
            else System.out.println( "THE GIVEN MATRIX IS NOT SYMMETRIC" );
            computeAndDisplaySumOfDiagonals(A);
        }
    }
}