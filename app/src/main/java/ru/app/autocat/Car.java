package ru.app.autocat;

import java.io.Serializable;


//Todo Сохранение\загрузка кол-ва коробок,

/**
 * Created by Yakovlev on 19.06.2015.
 */
public class Car implements Serializable {
    private String id;
    private String model;
    private String title;
    private String mark;
    private String imageName;
    private String created;
    private String kpp;
    private String country;
    private String KppAT = null;
    private String KppMT = null;
    private int AmountKppAt = 0;
    private int AmountKppMt = 0;

    public Car(String mark) {this.mark = mark;}

    public Car() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getKpp() {
        return kpp;
    }

    public String getKppAT() {
        return kpp.split(" ")[2];
    }

    public String getKppMT() {
        return kpp.split(" ")[0];
    }


    public void setKpp(String kpp) {
        if (KppMT != null && KppAT != null) {
           this.kpp = KppMT + " и " + KppAT;
        } else {
            this.kpp = kpp;
        }
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setKppAT(String kppAT) {
        this.KppAT = kppAT;
    }

    public void setKppMT(String kppMT) {
        this.KppMT = kppMT;
    }

    public int getAmountKppAt() {
        return AmountKppAt;
    }

    public void setAmountKppAt(int amountKppAt) {
        AmountKppAt = amountKppAt;
    }

    public int getAmountKppMt() {
        return AmountKppMt;
    }

    public void setAmountKppMt(int amountKppMt) {
        AmountKppMt = amountKppMt;
    }
}
