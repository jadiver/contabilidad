package com.example;

import java.util.ArrayList;
import java.util.List;

public class PGC {
    List<Cuenta>PGC = new ArrayList<>();

    public void Listar(String grupo){
        String SQL = "select * from pgc";

        if (!(grupo.equals(" ") || grupo ==null)) {
            SQL += "where codigo like ' " + grupo +" ' ";
        }
    }

    public static void main(String[] args) {
        
    }
}
