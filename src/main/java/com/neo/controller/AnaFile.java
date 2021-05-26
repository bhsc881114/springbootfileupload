package com.neo.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnaFile {


    static class MeasureEntity {
        private String insId;
        private long edasId;
        private long cpu;
        private long mem;
    }

    public static void main(String[] args) throws Exception {
        // instaid user cpu men
        Map<String, MeasureEntity> oldMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("/Users/bishan/Downloads/old-mea.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                MeasureEntity entity = parseLine(line);
                if (entity!=null) {
                    oldMap.put(entity.insId, entity);
                }
            }
        }

        Map<String, MeasureEntity> newMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("/Users/bishan/Downloads/new-mea2.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                MeasureEntity entity = parseLine(line);
                if (entity!=null) {
                    newMap.put(entity.insId, entity);
                }
            }
        }
        List<MeasureEntity> outputs = new ArrayList<>();
        for (Map.Entry<String, MeasureEntity> entry : newMap.entrySet()) {
            MeasureEntity old = oldMap.get(entry.getKey());
            if (old == null) {
                outputs.add(entry.getValue());
            } else {
                MeasureEntity newEn = entry.getValue();
                newEn.cpu = newEn.cpu - old.cpu;
                newEn.mem = newEn.mem - old.mem;
                outputs.add(newEn);
            }
        }
        PrintWriter writer = new PrintWriter("/Users/bishan/Downloads/diff.csv", "UTF-8");
        for (MeasureEntity en : outputs) {
            writer.println("cn-hangzhou," + en.edasId + "," + en.insId + "," + en.cpu + "," + en.mem);
        }
        writer.close();
    }

    private static MeasureEntity parseLine(String line) {
        if (line.contains("sum")) {
            return null;
        }
        line = line.replace("\"", "");
        MeasureEntity entiry = new MeasureEntity();
        String[] es = line.split(",");
        entiry.insId = es[0];
        entiry.edasId = Long.parseLong(es[1].trim());
        entiry.cpu = Long.parseLong(es[2].trim());
        entiry.mem = Long.parseLong(es[3].trim());
        return entiry;
    }
}
