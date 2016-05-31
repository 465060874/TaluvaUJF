package IA;

/**
 * Created by milanovb on 31/05/16.
 */
public enum AlphaBetaIntervalles {
    INF,SUP;

    public AlphaBetaIntervalles inverse(){
        if( this == SUP )
            return INF;
        else
            return SUP;
    }
}
