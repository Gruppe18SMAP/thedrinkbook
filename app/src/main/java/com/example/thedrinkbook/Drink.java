package com.example.thedrinkbook;

import java.io.Serializable;

public class Drink implements Serializable, Cloneable{

    // properties i objektet drinks
        public String Key;
        public int Antal;
        public String Ikon;
        public String Navn;
        public int Pris;

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
