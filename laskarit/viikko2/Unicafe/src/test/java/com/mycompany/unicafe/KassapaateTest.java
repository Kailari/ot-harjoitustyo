package com.mycompany.unicafe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * KassapaateTest
 */
public class KassapaateTest {
    Kassapaate kassapaate;
    Maksukortti porvarikortti;
    Maksukortti opiskelijakortti;

    @Before
    public void setUp() {
        kassapaate = new Kassapaate();
        porvarikortti = new Maksukortti(100000);
        opiskelijakortti = new Maksukortti(120);
    }

    @Test
    public void luodussaKassassaOikeaMaaraRahaa() {
        assertEquals(100000, kassapaate.kassassaRahaa());
    }

    @Test
    public void luodussaKassassaEiMyytyjaLounaita() {
        assertEquals(0, kassapaate.edullisiaLounaitaMyyty());
        assertEquals(0, kassapaate.maukkaitaLounaitaMyyty());
    }

    @Test
    public void ladatessaKortinSaldoKasvaa() {
        kassapaate.lataaRahaaKortille(opiskelijakortti, 120);
        assertEquals(240, opiskelijakortti.saldo());
    }

    @Test
    public void ladatessaKassanRahamaaraKasvaa() {
        kassapaate.lataaRahaaKortille(opiskelijakortti, 120);
        assertEquals(100120, kassapaate.kassassaRahaa());
    }

    @Test
    public void ladatessaSaldoEiMuutuJosSummaNegatiivinen() {
        kassapaate.lataaRahaaKortille(opiskelijakortti, -120);
        assertEquals(120, opiskelijakortti.saldo());
    }

    @Test
    public void ladatessaKassanRahamaaraEiMuutuJosSummaNegatiivinen() {
        kassapaate.lataaRahaaKortille(opiskelijakortti, -120);
        assertEquals(100000, kassapaate.kassassaRahaa());
    }

    /////////////////////////////////////////////////////////////////////////
    // Käteisostot

    @Test
    public void edullinenKateisostoVaihtorahaOnOikeinKunRahatRiittaa() {
        assertEquals(260, kassapaate.syoEdullisesti(500));
    }

    @Test
    public void edullinenKateisostoVaihtorahaOnOikeinKunRahatEiRiita() {
        assertEquals(100, kassapaate.syoEdullisesti(100));
    }

    @Test
    public void edullinenKateisostoMyytyjenMaaraKasvaaKunRahatRiittaa() {
        for (int i = 1; i < 10; i++) {
            kassapaate.syoEdullisesti(500);
            assertEquals(i, kassapaate.edullisiaLounaitaMyyty());
        }
    }

    @Test
    public void edullinenKateisostoMyytyjenMaaraEiKasvaKunRahatEiRiita() {
        kassapaate.syoEdullisesti(100);
        assertEquals(0, kassapaate.edullisiaLounaitaMyyty());
    }

    @Test
    public void edullinenKateisostoMyytyjenMaukkaidenMaaraEiKasvaKunRahatRiittaa() {
        kassapaate.syoEdullisesti(500);
        assertEquals(0, kassapaate.maukkaitaLounaitaMyyty());
    }

    @Test
    public void edullinenKateisostoMyytyjenMaukkaidenMaaraEiKasvaKunRahatEiRiita() {
        kassapaate.syoEdullisesti(100);
        assertEquals(0, kassapaate.maukkaitaLounaitaMyyty());
    }

    @Test
    public void edullinenKateisostoRahamaaraKassassaKasvaaKunRahatRiittaa() {
        for (int i = 1; i < 10; i++) {
            kassapaate.syoEdullisesti(500);
            assertEquals(100000 + i * 240, kassapaate.kassassaRahaa());
        }
    }

    @Test
    public void edullinenKateisostoRahamaaraKassassaEiKasvaaKunRahatEiRiita() {
        kassapaate.syoEdullisesti(100);
        assertEquals(100000, kassapaate.kassassaRahaa());
    }

    @Test
    public void maukasKateisostoVaihtorahaOnOikeinKunRahatRiittaa() {
        assertEquals(100, kassapaate.syoMaukkaasti(500));
    }

    @Test
    public void maukasKateisostoVaihtorahaOnOikeinKunRahatEiRiita() {
        assertEquals(100, kassapaate.syoMaukkaasti(100));
    }

    @Test
    public void maukasKateisostoMyytyjenMaaraKasvaaKunRahatRiittaa() {
        for (int i = 1; i < 10; i++) {
            kassapaate.syoMaukkaasti(500);
            assertEquals(i, kassapaate.maukkaitaLounaitaMyyty());
        }
    }

    @Test
    public void maukasKateisostoMyytyjenMaaraEiKasvaKunRahatEiRiita() {
        kassapaate.syoMaukkaasti(100);
        assertEquals(0, kassapaate.maukkaitaLounaitaMyyty());
    }

    @Test
    public void maukasKateisostoMyytyjenEdullistenMaaraEiKasvaKunRahatRiittaa() {
        kassapaate.syoMaukkaasti(500);
        assertEquals(0, kassapaate.edullisiaLounaitaMyyty());
    }

    @Test
    public void maukasKateisostoMyytyjenEdullistenMaaraEiKasvaKunRahatEiRiita() {
        kassapaate.syoMaukkaasti(100);
        assertEquals(0, kassapaate.edullisiaLounaitaMyyty());
    }

    @Test
    public void maukasKateisostoRahamaaraKassassaKasvaaKunRahatRiittaa() {
        for (int i = 1; i < 10; i++) {
            kassapaate.syoMaukkaasti(500);
            assertEquals(100000 + i * 400, kassapaate.kassassaRahaa());
        }
    }

    @Test
    public void maukasKateisostoRahamaaraKassassaEiKasvaaKunRahatEiRiita() {
        kassapaate.syoMaukkaasti(100);
        assertEquals(100000, kassapaate.kassassaRahaa());
    }

    /////////////////////////////////////////////////////////////////////////
    // Korttiostot

    @Test
    public void edullinenKorttiostoPalauttaaTrueKunRahatRiittaa() {
        assertTrue(kassapaate.syoEdullisesti(porvarikortti));
    }

    @Test
    public void edullinenKorttiostoPalauttaaFalseKunRahatEiRiita() {
        assertFalse(kassapaate.syoEdullisesti(opiskelijakortti));
    }

    @Test
    public void edullinenKorttiostoMyytyjenEdullistenMaaraKasvaaKunRahatRiittaa() {
        for (int i = 1; i < 10; i++) {
            kassapaate.syoEdullisesti(porvarikortti);
            assertEquals(i, kassapaate.edullisiaLounaitaMyyty());
        }
    }

    @Test
    public void edullinenKorttiostoMyytyjenEdullistenMaaraEiKasvaKunRahatEiRiita() {
        kassapaate.syoEdullisesti(opiskelijakortti);
        assertEquals(0, kassapaate.maukkaitaLounaitaMyyty());
    }

    @Test
    public void edullinenKorttiostoMyytyjenMaukkaidenMaaraEiKasvaKunRahatRiittaa() {
        kassapaate.syoEdullisesti(porvarikortti);
        assertEquals(0, kassapaate.maukkaitaLounaitaMyyty());
    }

    @Test
    public void edullinenKorttiostoEiMuutaRahamaaraaKunRahatRiittaa() {
        kassapaate.syoEdullisesti(porvarikortti);
        assertEquals(100000, kassapaate.kassassaRahaa());
    }

    @Test
    public void edullinenKorttiostoEiMuutaRahamaaraaKunRahatEiRiita() {
        kassapaate.syoEdullisesti(opiskelijakortti);
        assertEquals(100000, kassapaate.kassassaRahaa());
    }

    @Test
    public void edullinenKorttiostoVeloittaaOikeinKunRahatRiittaa() {
        for (int i = 1; i < 10; i++) {
            kassapaate.syoEdullisesti(porvarikortti);
            assertEquals(100000 - i * 240, porvarikortti.saldo());
        }
    }

    @Test
    public void edullinenKorttiostoEiVeloitaJosRahatEiRiita() {
        kassapaate.syoEdullisesti(opiskelijakortti);
        assertEquals(120, opiskelijakortti.saldo());
    }

    @Test
    public void maukasKorttiostoPalauttaaTrueKunRahatRiittaa() {
        assertTrue(kassapaate.syoMaukkaasti(porvarikortti));
    }

    @Test
    public void maukasKorttiostoPalauttaaFalseKunRahatEiRiita() {
        assertFalse(kassapaate.syoMaukkaasti(opiskelijakortti));
    }

    @Test
    public void maukasKorttiostoMyytyjenMaukkaidenMaaraKasvaaKunRahatRiittaa() {
        for (int i = 1; i < 10; i++) {
            kassapaate.syoMaukkaasti(porvarikortti);
            assertEquals(i, kassapaate.maukkaitaLounaitaMyyty());
        }
    }

    @Test
    public void maukasKorttiostoMyytyjenMaukkaidenMaaraEiKasvaKunRahatEiRiita() {
        kassapaate.syoMaukkaasti(opiskelijakortti);
        assertEquals(0, kassapaate.maukkaitaLounaitaMyyty());
    }

    @Test
    public void maukasKorttiostoMyytyjenEdullistenMaaraEiKasvaKunRahatRiittaa() {
        kassapaate.syoMaukkaasti(porvarikortti);
        assertEquals(0, kassapaate.edullisiaLounaitaMyyty());
    }

    @Test
    public void maukasKorttiostoEiMuutaRahamaaraaKunRahatRiittaa() {
        kassapaate.syoMaukkaasti(porvarikortti);
        assertEquals(100000, kassapaate.kassassaRahaa());
    }

    @Test
    public void maukasKorttiostoEiMuutaRahamaaraaKunRahatEiRiita() {
        kassapaate.syoMaukkaasti(opiskelijakortti);
        assertEquals(100000, kassapaate.kassassaRahaa());
    }

    @Test
    public void maukasKorttiostoVeloittaaOikeinKunRahatRiittaa() {
        for (int i = 1; i < 10; i++) {
            kassapaate.syoMaukkaasti(porvarikortti);
            assertEquals(100000 - i * 400, porvarikortti.saldo());
        }
    }

    @Test
    public void maukasKorttiostoEiVeloitaJosRahatEiRiita() {
        kassapaate.syoMaukkaasti(opiskelijakortti);
        assertEquals(120, opiskelijakortti.saldo());
    }
}