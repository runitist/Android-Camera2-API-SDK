package com.prowdloner.camera2libraryproject;

import com.prowdloner.camera2libraryproject.prowdlib.Prowd_Utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

// Prowd_Utils 에 대한 유닛 테스트
public class Prowd_Utils_Test {
    private final Prowd_Utils MY_UTILS = new Prowd_Utils();

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void merge_both_StringArray_isCorrect() {
        final String[] CORRECT_STRINGS = new String[]{"hi", "hello", "hello"};
        final String[] STR1 = {"hi"};
        final String[] STR2 = {"hello", "hello"};
        final String[] RESULT_ARRAY = MY_UTILS.merge_both_StringArray(STR1, STR2);

        assertArrayEquals(CORRECT_STRINGS, RESULT_ARRAY);
    }

    @Test
    public void merge_all_StringArray_isCorrect() {
        final String[] CORRECT_STRINGS = {"hi", "hello", "hello", "sup"};
        final String[] STR1 = {"hi"};
        final String[] STR2 = {"hello", "hello"};
        final String[] STR3 = {"sup"};
        final String[][] STR_ARRAY_ARRAY = {STR1, STR2, STR3};
        final String[] RESULT_ARRAY =  MY_UTILS.merge_all_StringArray(STR_ARRAY_ARRAY);

        assertArrayEquals(CORRECT_STRINGS, RESULT_ARRAY);
    }

    @Test
    public void strList2strArray_isCorrect(){
        final String[] CORRECT_STRINGS = {"hi", "hello"};
        final ArrayList<String> STRING_ARRAY_LIST = new ArrayList<>();
        STRING_ARRAY_LIST.add("hi");
        STRING_ARRAY_LIST.add("hello");
        final String[] RESULT_ARRAY =  MY_UTILS.strList2strArray(STRING_ARRAY_LIST);
        assertArrayEquals(CORRECT_STRINGS, RESULT_ARRAY);
    }

    @Test
    public void get_unique_StringArray_isCorrect(){
        final String[] CORRECT_STRINGS = {"hi", "hello", "sup"};
        final String[] STRINGS = {"hi", "hello", "hello", "sup"};
        final String[] RESULT_STRINGS = MY_UTILS.get_unique_StringArray(STRINGS);

        assertArrayEquals(CORRECT_STRINGS, RESULT_STRINGS);
    }

    @Test
    public void intSet2intArray_isCorrect(){
        final Set<Integer> SET = new HashSet<>();
        SET.add(10);
        SET.add(11);

        final Integer[] CORRECT_ARRAY = {10,11};
        final Integer[] RESULT_ARRAY = MY_UTILS.integerSet2integerArray(SET);

        assertArrayEquals(CORRECT_ARRAY, RESULT_ARRAY);

    }
}