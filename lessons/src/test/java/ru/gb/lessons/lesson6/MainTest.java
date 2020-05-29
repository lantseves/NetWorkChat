package ru.gb.lessons.lesson6;

import com.sun.javafx.sg.prism.web.NGWebView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MainTest {
    private Main main ;

    @BeforeEach
    public void init() {
        main = new Main() ;
    }

    @Test
    public void getSubArrayAfterFourExceptionTest() {
        Assertions.assertThrows(RuntimeException.class, () -> main.getSubArrayAfterFour(new int[] {2, 2, 1, 7 ,5}));
    }

    @ParameterizedTest
    @MethodSource("dataGenerateSubArrayAfterFourTest")
    public void getSubArrayAfterFourTest(int[] expected, int[] actual) {
        Assertions.assertArrayEquals(new int[]{7 ,5} , main.getSubArrayAfterFour(new int[] {4, 2, 4, 7 ,5}));
    }

    public static Stream<Arguments> dataGenerateSubArrayAfterFourTest() {
        List<Arguments> out = new ArrayList<>() ;
        out.add(Arguments.arguments(new int[] {1, 2, 4, 4, 2, 3, 4, 1, 7} , new int[] {1, 7})) ;
        out.add(Arguments.arguments(new int[] {1, 2, 4, 4, 2, 3, 1, 2, 7} , new int[] {1, 7})) ;
        out.add(Arguments.arguments(new int[] {1, 2, 3, 3, 2, 3, 3, 1, 7} , new int[] {})) ;
        return out.stream() ;
    }

    @ParameterizedTest
    @MethodSource("dataGenerateContainsOneAndFourTest")
    public void containsOneAndFourTest(boolean expected, int[] actual) {
        Assertions.assertEquals(expected , main.containsOneAndFour(actual));
    }

    public static Stream<Arguments> dataGenerateContainsOneAndFourTest() {
        List<Arguments> out = new ArrayList<>() ;
        out.add(Arguments.arguments(true , new int[] {1, 1, 1, 4, 4, 1, 4, 4})) ;
        out.add(Arguments.arguments(false, new int[] {1, 1, 1, 1, 1, 1})) ;
        out.add(Arguments.arguments(false ,new int[] {4, 4, 4, 4})) ;
        out.add(Arguments.arguments(false ,new int[] {1, 4, 4, 1, 1, 4, 3})) ;
        return out.stream() ;
    }
}
