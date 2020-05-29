package ru.gb.lessons.lesson6;

import java.util.Arrays;

public class Main {

    /*
    Написать метод, которому в качестве аргумента передается не пустой одномерный целочисленный массив.
    Метод должен вернуть новый массив, который получен путем вытаскивания из исходного массива элементов,
    идущих после последней четверки. Входной массив должен содержать хотя бы одну четверку,
    иначе в методе необходимо выбросить RuntimeException.

    Написать набор тестов для этого метода (по 3-4 варианта входных данных).
    Вх: [ 1 2 4 4 2 3 4 1 7 ] -> вых: [ 1 7 ].
     */
    public int[] getSubArrayAfterFour(int[] arr) {
        int indexFour = - 1 ;
        for(int i = arr.length - 1 ; i >= 0 ; i--) {
            if (arr[i] == 4) {
                indexFour = i ;
                break;
            }
        }

        if (indexFour == -1) throw new RuntimeException("Not contains four") ;

        return Arrays.copyOfRange(arr , indexFour + 1 , arr.length);
    }

    /*
    Написать метод, который проверяет состав массива из чисел 1 и 4.
    Если в нем нет хоть одной четверки или единицы, то метод вернет false;

    Написать набор тестов для этого метода (по 3-4 варианта входных данных).
    [ 1 1 1 4 4 1 4 4 ] -> true
    [ 1 1 1 1 1 1 ] -> false
    [ 4 4 4 4 ] -> false
    [ 1 4 4 1 1 4 3 ] -> false
    */
    public boolean containsOneAndFour(int[] arr) {
        boolean isContainsOne = false ;
        boolean isContainsFour = false ;
        boolean isContainsOther = false ;

        for (int i : arr) {
           if (i == 1) isContainsOne = true ;
           else if (i == 4) isContainsFour = true ;
           else isContainsOther = true ;
        }
        return isContainsOne && isContainsFour && !isContainsOther ;
    }

}
