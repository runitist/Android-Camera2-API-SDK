package com.prowdloner.camera2libraryproject.prowdlib;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class Prowd_Utils {

    // String[] 타입의 배열을 받아들여 모두 결합하여 반환하는 함수
    // 단순 합치기에 중복 값이 존재 가능
    public final String[] merge_both_StringArray(final String[] STR1, final String[] STR2) {
        final String[] rst = new String[STR1.length + STR2.length];
        System.arraycopy(STR1, 0, rst, 0, STR1.length);
        System.arraycopy(STR2, 0, rst, STR1.length, STR2.length);

        return rst;
    }

    public final String[] merge_all_StringArray(final String[][] STR_ARRAY){
        String[] rst = new String[]{};
        for(String[] str : STR_ARRAY){
            rst = merge_both_StringArray(rst, str);
        }
        return rst;
    }

    // 리스트를 문자열 배열로 만들기
    public final String[] strList2strArray(final ArrayList<String > STRING_ARRAY_LIST){
        String[] stockArr = new String[STRING_ARRAY_LIST.size()];
        stockArr = STRING_ARRAY_LIST.toArray(stockArr);

        return stockArr;
    }

    // 문자열 배열 중복 제거
    public final String[] get_unique_StringArray(final String[] STRINGS){
        final ArrayList<String> arrayList = new ArrayList<>();

        for(String item : STRINGS){
            if(!arrayList.contains(item))
                arrayList.add(item);
        }
        return strList2strArray(arrayList);
    }

    // 정수 세트를 배열로 변환하는 함수
    public final Integer[] integerSet2integerArray(Set<Integer> set){
        Integer[] array = new Integer[set.size()];
        set.toArray(array);
        return array;
    }

}
