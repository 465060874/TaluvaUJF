package map;

import java.util.Arrays;

public class Partition {

    /*
     * tab contient pour chaque element i soit :
     *   - si le nombre est positif, le parent tab[i]
     *   - si le nombre est négatif, le nombre d'éléments -tab[i]
     *     de la composante dont i est le representant.
     *
     */
    private int[] tab;

    /**
     * Initialise une structure Partition avec {@code n} elements
     * La partition est composée de singletons
     *
     * @param n nombre d'elements dans la partition
     */
    public Partition(int n) {
        if (n < 0) {
            throw new IllegalArgumentException();
        }

        tab = new int [n];
        Arrays.fill(tab, -1);
    }

    /**
     * @return la reference de la classe de l'element {@code i}
     *
     */

    public int trouver(int i){
        validateIndex(i);

        if (tab[i] == i) {
            throw new IllegalStateException();
        }

        if (tab[i] >= 0) {
            // When we call the trouver(i) we update its tab[i] value to point directly to the group representative.
            tab[i] = trouver(tab[i]);
            return tab[i];
        }

        return  i;
    }


    /**
     * Reunit les classes d'equivalence de {@code i} et {@code j}
     *
     * @param i
     * @param j
     */
    public void unir(int i , int j){
        validateIndex(i);
        validateIndex(j);

        int repI = trouver(i);
        int repJ = trouver(j);
        if (repI == repJ) {
            throw new IllegalArgumentException("Déjà dans la même classe");
        }

        if (tab[repI] <= tab[repJ]){
            tab[repI] += tab[repJ];
            tab[repJ] = repI;
        }
        else {
            tab[repJ] += tab[repI];
            tab[repI] = repJ;
        }
    }

    /**
     * indique si {@code i} et {@code j} sont dans la meme composante connexe
     *
     * @return true si {@code i} et {@code j} sont dans la meme composante connexe
     */
    public boolean sontConnectes(int i, int j){
        validateIndex(i);
        validateIndex(j);
        return (trouver(i) == trouver(j));

    }

    /**
     * verifie que i est un parametre valide
     * renvoie un IllegalArgumentException  sauf si 0 <= {@code i} < n
     */
    private void validateIndex(int i) {
        if (i < 0 || i >= tab.length) {
            throw new IllegalArgumentException();
        }
    }
}

