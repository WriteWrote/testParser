package org.example.model;

public class Teacher {
//    private Integer index;
    private String fio;

    public Teacher(String fio) {
        this.fio = fio;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }
}
