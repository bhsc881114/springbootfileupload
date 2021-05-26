package com.neo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TOTS {

    public static void main(String[] args) {
        Map<String,Integer> map = new HashMap();
        map.put("1",2);
//        map.put("2",2);
//        map.put("3",2);
//        map.put("4",3);
//        map.put("5",3);
//        map.put("6",3);
//        map.put("7",2);

        List<List<String>> list = new ArrayList<List<String>>();
        for(int i=1;i<8;i++) {
            int count = map.get(String.valueOf(i));
            for(int j=1;j<count+1;j++) {
                String cmd = "~/Downloads/ffmpeg -i "+  String.valueOf(i)+"."+String.valueOf(j)+".mp4"+
                        " -vcodec copy -acodec copy -vbsf h264_mp4toannexb "+  String.valueOf(i)+"."+String.valueOf(j)+".ts";
                System.out.println(cmd);
            }
        }
        //
    }
}
