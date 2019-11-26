package com.konfuse.bean;

import java.io.Serializable;

/**
 * @Author: Konfuse
 * @Date: 2019/11/26 11:01
 */
public class Record extends Entry implements Serializable {
    private String describe = "";
    public Record(MBR mbr) {
        super(mbr);
    }

    public Record(String describe, double x1, double y1, double x2, double y2) {
        super(new MBR(x1, y1, x2, y2));
        this.describe = describe;
    }

    @Override
    public String toString() {
        return "Record{" +
                "describe='" + describe + '\'' +
                '}';
    }
}