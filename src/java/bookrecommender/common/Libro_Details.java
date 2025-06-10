package bookrecommender.common;

import java.io.Serial;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;

public class Libro_Details implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Hashtable<String, List<Libro>> consigli;
    private List<Valutazione> valutazioni;
    private float mStile, mContenuto, mGradevolezza, mOriginalita, mEdizione, mFinale;


    public Libro_Details(Hashtable<String, List<Libro>> consigli, List<Valutazione> valutazioni) {
        this.consigli = consigli;
        this.valutazioni = valutazioni;
        this.mStile = 0;
        this.mContenuto = 0;
        this.mGradevolezza = 0;
        this.mOriginalita = 0;
        this.mEdizione = 0;
        this.mFinale = 0;
        for(Valutazione v : valutazioni) {
            List<Float> vList = v.getValutazioni();
            mStile += vList.get(0);
            mContenuto += vList.get(1);
            mGradevolezza += vList.get(2);
            mOriginalita += vList.get(3);
            mEdizione += vList.get(4);
            mFinale += vList.get(5);
        }

    }

    public Hashtable<String, List<Libro>> getConsigli() {
        return consigli;
    }

    public List<Valutazione> getValutazioni() {
        return valutazioni;
    }

    public float getmStile() {
        return mStile / valutazioni.size();
    }

    public float getmContenuto() {
        return mContenuto / valutazioni.size();
    }

    public float getmGradevolezza() {
        return mGradevolezza / valutazioni.size();
    }

    public float getmOriginalita() {
        return mOriginalita / valutazioni.size();
    }

    public float getmEdizione() {
        return mEdizione / valutazioni.size();
    }

    public float getmFinale() {
        return mFinale / valutazioni.size();
    }

}
