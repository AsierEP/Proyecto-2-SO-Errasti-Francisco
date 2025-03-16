/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Objects;

/**
 *
 * @author Dell
 */
public class Archivo {
    private String nombre;
    private int tamaño;
    private int direccionPrimerBloque;
    private String color="";
    private String directorio;
    
    public Archivo(String nombre, int tamaño,String directori) {
        this.nombre = nombre;
        this.tamaño = tamaño;
        this.directorio=directori;
    }
    
    public Archivo(String nombre, int tamaño, int direccionPrimerBloque) {
        this.nombre = nombre;
        this.tamaño = tamaño;
        this.direccionPrimerBloque = direccionPrimerBloque;
    }

    public Archivo(String nombre, int tamaño, int direccionPrimerBloque, String color) {
        this.nombre = nombre;
        this.tamaño = tamaño;
        this.direccionPrimerBloque = direccionPrimerBloque;
        this.color = color;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getTamaño() {
        return tamaño;
    }

    public String getDirectorio() {
        return directorio;
    }

    public void setDirectorio(String directorio) {
        this.directorio = directorio;
    }

    public void setTamaño(int tamaño) {
        this.tamaño = tamaño;
    }

    public int getDireccionPrimerBloque() {
        return direccionPrimerBloque;
    }

    public void setDireccionPrimerBloque(int direccionPrimerBloque) {
        this.direccionPrimerBloque = direccionPrimerBloque;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void actualizarAsignacion(int nuevaDireccionPrimerBloque) {
        this.direccionPrimerBloque = nuevaDireccionPrimerBloque;
    }

    @Override
    public String toString() {
        return "Archivo{" +
                "nombre='" + nombre + '\'' +
                ", tamaño=" + tamaño +
                ", direccionPrimerBloque=" + direccionPrimerBloque +
                ", color='" + color + '\'' +
                '}';
    }
}
