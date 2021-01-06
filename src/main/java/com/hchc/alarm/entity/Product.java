package com.hchc.alarm.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author wangrong
 * @date 2020-11-19
 */
@Setter
@Getter
public class Product {

    private int id;
    private String name;
    private String code;

    public Product() {

    }

    public Product(String name, String code) {
        this.name = name;
        this.code = code;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Product) {
            return ((Product) obj).getName().equals(name);
        }
        return false;
    }
}
