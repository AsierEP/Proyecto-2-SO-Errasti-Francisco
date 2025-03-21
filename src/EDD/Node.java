/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EDD;

/**
 *
 * @author Dell
 */
public class Node<T> {
    private T dato;
    private Node<T> pnext;

    public Node(T dato) {
        this.dato = dato;
        this.pnext = null;
    }

    public T getDato() {
        return dato;
    }

    public void setDato(T dato) {
        this.dato = dato;
    }

    public Node<T> getPnext() {
        return pnext;
    }

    public void setPnext(Node<T> pnext) {
        this.pnext = pnext;
    }
    
    
}
