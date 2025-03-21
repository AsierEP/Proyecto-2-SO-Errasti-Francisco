/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EDD;

import Objects.Archivo;
import Objects.Directorio;

/**
 *
 * @author Dell
 */
public class List<T> {
    private Node<T> pfirst;
    private Node<T> plast;

    public List() {
        this.pfirst = null;
        this.plast = null;
    }
    
    public Node getPfirst(){
        return pfirst;
    }
    
    public Node getPlast(){
        return plast;
    }
    
    public void setPlast(Node l){
        this.plast=l;
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

    public void DeleteFirst() {
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
            System.out.println("La lista está vacía.");
            return;
        }
        Node<T> actual = pfirst;
        while (actual != null) {
            Directorio actus = (Directorio) actual.getDato();
            System.out.print( actus.getNombre()+ " -> ");
            actual = actual.getPnext();
        }
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
    public int tamaño() {
        int contador = 0;
        Node<T> actual = pfirst;
        while (actual != null) {
            contador++;
            actual = actual.getPnext();
        }
        return contador;
    }
        public void eliminarArchivo(String nombre,String direct) {
        if (IsEmpty()) {
            System.out.println("La lista está vacía");
            return;
        }

        Node<T> actual = pfirst;
        Node<T> anterior = null;

        while (actual != null) {
            Archivo archivo = (Archivo) actual.getDato();
            if (archivo.getNombre().equals(nombre)&&archivo.getDirectorio().equals(direct)) {
                if (anterior == null) {
                    pfirst = actual.getPnext();
                    if (pfirst == null) {
                        plast = null;
                    }
                } else {
                    anterior.setPnext(actual.getPnext());
                    if (actual.getPnext() == null) {
                        plast = anterior;
                    }
                }
                System.out.println("Archivo eliminado: " + archivo.getNombre());
                return;
            }
            anterior = actual;
            actual = actual.getPnext();
        }
        System.out.println("Archivo no encontrado: " + nombre);
    }
    
   public void eliminarDirectorioDeLista(String nombre, String direct) {
        if (IsEmpty()) {
            System.out.println("La lista está vacía");
            return;
        }

        Node<T> actual = pfirst;
        Node<T> anterior = null;

        while (actual != null) {
            Directorio directorio = (Directorio) actual.getDato();
            if (directorio.getNombre().equals(nombre) && directorio.getPadre().equals(direct)) {
                eliminarSubdirectoriosRecursivo(directorio.getNombre());

                if (anterior == null) {
                    pfirst = actual.getPnext();
                    if (pfirst == null) {
                        plast = null;
                    }
                } else {
                    anterior.setPnext(actual.getPnext());
                    if (actual.getPnext() == null) {
                        plast = anterior;
                    }
                }
                System.out.println("Directorio eliminado: " + directorio.getNombre());
                return;
            }
            anterior = actual;
            actual = actual.getPnext();
        }

        System.out.println("Directorio no encontrado: " + nombre);
    }

    private void eliminarSubdirectoriosRecursivo(String nombrePadre) {
        if (IsEmpty()) {
            return;
        }

        Node<T> actual = pfirst;
        Node<T> anterior = null;

        while (actual != null) {
            Directorio directorio = (Directorio) actual.getDato();
            if (directorio.getPadre().equals(nombrePadre)) {
                eliminarSubdirectoriosRecursivo(directorio.getNombre());

                if (anterior == null) {
                    pfirst = actual.getPnext();
                    if (pfirst == null) {
                        plast = null;
                    }
                } else {
                    anterior.setPnext(actual.getPnext());
                    if (actual.getPnext() == null) {
                        plast = anterior;
                    }
                }
                System.out.println("Subdirectorio eliminado: " + directorio.getNombre());
            } else {
                anterior = actual;
            }
            actual = actual.getPnext();
        }
    }
    
    public Archivo encontrarArchivo(String nombreArchivo, String nombreDirectorio) {
        if (IsEmpty()) {
            System.out.println("La lista está vacía.");
            return null;
        }

        Node<T> actual = pfirst;

        while (actual != null) {
            Archivo archivo = (Archivo) actual.getDato();

            if (archivo.getNombre().equals(nombreArchivo) && archivo.getDirectorio().equals(nombreDirectorio)) {
                System.out.println("Archivo encontrado: " + archivo.getNombre() + " en el directorio: " + archivo.getDirectorio());
                return archivo;
            }
            actual = actual.getPnext();
        }

        System.out.println("Archivo no encontrado: " + nombreArchivo + " en el directorio: " + nombreDirectorio);
        return null;
    }
    public Directorio encontrarDirectorio(String nombreDirectorio,String nombrePadre) {
        if (IsEmpty()) {
            System.out.println("La lista está vacía.");
            return null;
        }
        Node<T> actual = pfirst;

        while (actual != null) {
            Directorio archivo = (Directorio) actual.getDato();
            if (archivo.getNombre().equals(nombreDirectorio)&&archivo.getPadre().equals(nombrePadre)) {
                return archivo;
            }
            actual = actual.getPnext();
        }
        return null;
    }
    
    public void encontrarArchivoYCambiar(String nuevoNombre,String nombreArchivo, String nombreDirectorio) {
        if (IsEmpty()) {
            System.out.println("La lista está vacía.");
            return;
        }

        Node<T> actual = pfirst;
        
        while (actual != null) {
            Archivo archivo = (Archivo) actual.getDato();

            if (archivo.getNombre().equals(nombreArchivo) && archivo.getDirectorio().equals(nombreDirectorio)) {
                System.out.println("Archivo encontrado: " + archivo.getNombre() + " en el directorio: " + archivo.getDirectorio());
                archivo.setNombre(nuevoNombre);
                return;
            }

            actual = actual.getPnext();
        }

        System.out.println("Archivo no encontrado: " + nombreArchivo + " en el directorio: " + nombreDirectorio);
        return;
    }
    
    public void encontrarDirectorioYCambiar(String nuevoNombre,String nombreDirectorio, String nombrePadre) {
        if (IsEmpty()) {
            System.out.println("La lista está vacía.");
            return;
        }

        Node<T> actual = pfirst;

        while (actual != null) {
            Directorio archivo = (Directorio) actual.getDato();

            if (archivo.getNombre().equals(nombreDirectorio) && archivo.getPadre().equals(nombrePadre)) {
                System.out.println("Directorio encontrado: " + archivo.getNombre() + " en el directorio: " + archivo.getPadre());
                archivo.setNombre(nuevoNombre);
                return;
            }

            actual = actual.getPnext();
        }

        System.out.println("Directorio no encontrado: " + nombreDirectorio + " en el directorio: " + nombrePadre);
        return;
    }
}
