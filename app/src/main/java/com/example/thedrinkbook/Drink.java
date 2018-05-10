package com.example.thedrinkbook;

import java.io.Serializable;

public class Drink implements Serializable, Cloneable{

        String Key;
        int Antal;
        String Ikon;
        String Navn;
        int Pris;

        public Drink(){

        }

        public Drink(Drink copy){
            this.Key = copy.Key;
            this.Antal = copy.Antal;
            this.Ikon = copy.Ikon;
            this.Navn = copy.Navn;
            this.Pris = copy.Pris;
        }
}
