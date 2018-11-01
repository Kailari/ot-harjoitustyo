package com.mycompany.unicafe;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class MaksukorttiTest {

    Maksukortti kortti;

    @Before
    public void setUp() {
        kortti = new Maksukortti(10);
    }

    @Test
    public void luotuKorttiOlemassa() {
        assertTrue(kortti != null);
    }

    @Test
    public void saldoOikein() {
        assertEquals(10, kortti.saldo());
    }

    @Test
    public void saldoKasvaa() {
        kortti.lataaRahaa(10);
        assertEquals(20, kortti.saldo());
    }

    @Test
    public void palauttaaTrueKunRahaaOnTarpeeksi() {
        assertTrue(kortti.otaRahaa(10));
    }

    @Test
    public void saldoPieneneeKunRahaaOnTarpeeksi() {
        kortti.otaRahaa(5);
        assertEquals(5, kortti.saldo());
    }

    @Test
    public void palauttaaFalseKunRahaaOnLiianVahan() {
        assertFalse(kortti.otaRahaa(15));
    }

    @Test
    public void saldoEiMuutuKunRahaaOnLiianVahan() {
        kortti.otaRahaa(15);
        assertEquals(10, kortti.saldo());
    }

    @Test
    public void toStringPalauttaaOikein() {
        assertEquals("saldo: 0.10", kortti.toString());
    }
}
