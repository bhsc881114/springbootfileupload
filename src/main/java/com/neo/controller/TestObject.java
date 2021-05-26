package com.neo.controller;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class TestObject {
    private String id;
    private String name;
    private String desc;

    private List<String> arrars;
    private Map<String,String> map;

    public static void main(String[] args) {
        File fold = new File("/Users/bishan/Downloads/peicaipan");
        Map<String,Integer> map = new HashMap();
        map.put("1",9);
//        map.put("2",2);
//        map.put("3",2);
//        map.put("4",3);
//        map.put("5",3);
//        map.put("6",3);
//        map.put("7",2);

        List<List<String>> list = new ArrayList<List<String>>();
        for(int i=0;i<map.size();i++) {
            int count = map.get(String.valueOf(i+1));
            List<String> sublist = new ArrayList<String>();
            for(int j=1;j<count+1;j++) {
                sublist.add(String.valueOf(i)+"."+String.valueOf(j)+".ts");
            }
            list.add(sublist);
        }

        List<Cmd> doneList = new ArrayList<>();
        List<List<String>> result = new ArrayList<List<String>>();
        descartes(list, result, 0, new ArrayList<String>());
        System.out.println(result.size());
        int i =0;

        for (List<String> aa : result) {
            String val = String.join("|",aa);
            List<String> newAa = aa.stream().map(e->{
                return e.replace(".ts", "");
            }).collect(Collectors.toList());;
            String name = String.join("-",newAa);
            String cmd ="~/Downloads/ffmpeg -i \"concat:"+val+"\" -c copy -bsf:a aac_adtstoasc -movflags +faststart output/"+name+".mp4";
            System.out.println(cmd);

            if (i%30==0) {
                Cmd cmdList = new Cmd();
                cmdList.mergedName1 = name;
                cmdList.mergeVideo = cmd;
                doneList.add(cmdList);
            }
            i++;
        }
        System.out.println("------------------------------------------------------------");


        for (Cmd cmd : doneList) {
            String voice = i%2==0?"yinping2" : "yinping3";
            i++;
            String newName = "n-" + cmd.mergedName1;
            String voiceCmd
                    = "\n" +
                    "~/Downloads/ffmpeg -i output/"+cmd.mergedName1+".mp4 -i "+voice+".aac  -map 0:v:0 -map 1:a:0 -c copy -y output/"+newName+".mp4";
            cmd.setMergedName2(newName);
            cmd.setMergeVoice(voiceCmd);


            String newName2 = "srt-" + newName;
            String srtCmd = "~/Downloads/ffmpeg  -i output/"+newName+".mp4 -vf subtitles=CHS.srt  output/"+newName2+".mp4\n";
            cmd.setMergedName3(newName2);
            cmd.setMergeStr(srtCmd);

            System.out.println(cmd.mergeVideo);
            System.out.println(cmd.mergeVoice);
            System.out.println(cmd.mergeStr);


            System.out.println("#------------------------------------------------------------");


        }
//        System.out.println(JSON.toJSONString(result));
    }

    private static void descartes(List<List<String>> dimvalue, List<List<String>> result, int layer, List<String> curList) {
        if (layer < dimvalue.size() - 1) {
            if (dimvalue.get(layer).size() == 0) {
                descartes(dimvalue, result, layer + 1, curList);
            } else {
                for (int i = 0; i < dimvalue.get(layer).size(); i++) {
                    List<String> list = new ArrayList<String>(curList);
                    list.add(dimvalue.get(layer).get(i));
                    descartes(dimvalue, result, layer + 1, list);
                }
            }
        } else if (layer == dimvalue.size() - 1) {
            if (dimvalue.get(layer).size() == 0) {
                result.add(curList);
            } else {
                for (int i = 0; i < dimvalue.get(layer).size(); i++) {
                    List<String> list = new ArrayList<String>(curList);
                    list.add(dimvalue.get(layer).get(i));
                    result.add(list);
                }
            }
        }
    }
}
