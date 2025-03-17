/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EDD;

/**
 *
 * @author Dell hola nelly
 */
public class List<T> {
    private Node<T> pfirst;
    private Node<T> plast;

    public List() {
        this.pfirst = null;
        this.plast = null;
    }

    public Node<T> getPfirst() {
        return pfirst;
    }

    public void setPfirst(Node<T> pfirst) {
        this.pfirst = pfirst;
    }

    public Node<T> getPlast() {
        return plast;
    }

    public void setPlast(Node<T> plast) {
        this.plast = plast;
    }
    
    public boolean IsEmpty() {
        return pfirst == null;
    }
    
    public void AddElement(T dato) {
        Node<T> nuevoNodo = new Node<>(dato);
        if (IsEmpty()) {
            pfirst = nuevoNodo;
            plast = nuevoNodo;
        } else {
            plast.setPnext(nuevoNodo);
            plast = nuevoNodo;
        }
    }
    
    public void RemoveFirst() {
        if (IsEmpty()) {
            System.out.println("La lista está vacía");
            return;
        }
        if (pfirst == plast) {
            pfirst = null;
            plast = null;
        } else {
            pfirst = pfirst.getPnext();
        }
    }
    
    public void mostrar() {
        if (IsEmpty()) {
            System.out.println("La lista está vacía");
            return;
        }
        Node<T> actual = pfirst;
        while (actual != null) {
            System.out.print(actual.getDato() + " -> ");
            actual = actual.getPnext();
        }
        System.out.println("null");
    }
    
    public boolean buscar(T dato) {
        Node<T> actual = pfirst;
        while (actual != null) {
            if (actual.getDato().equals(dato)) {
                return true;
            }
            actual = actual.getPnext();
        }
        return false;
    }
    
    public int Size() {
        int contador = 0;
        Node<T> actual = pfirst;
        while (actual != null) {
            contador++;
            actual = actual.getPnext();
        }
        return contador;
    }
}
