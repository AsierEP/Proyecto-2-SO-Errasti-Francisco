/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Objects;

import java.awt.Color;

/**
 *
 * @author Dell
 */
public class Archivo {
    private String nombre;
    private int tamaño;
    private int direccionPrimerBloque;
    private transient Color color;
    private int colorRGB;
    private String directorio;
     private int r;
    private int g;
    private int b;
    
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

    public Archivo(String nombre, int tamaño, int direccionPrimerBloque, Color color) {
        this.nombre = nombre;
        this.tamaño = tamaño;
        this.direccionPrimerBloque = direccionPrimerBloque;
        this.colorRGB = color.getRGB();
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

    public Color getColor() {
        return new Color(r, g, b);
    }
    
    public int getR() { return r; }
    public int getG() { return g; }
    public int getB() { return b; }
    
    public void setR(int rr) { this.r=rr; }
    public void setG(int gg) { this.g=gg; }
    public void setB(int bb) { this.b=bb; }

    public void setColor(Color color) {
        this.color = color;
        this.colorRGB = color.getRGB();
    }
    
    public int getColorRGB() {
        return colorRGB;
    }

    public void setColorRGB(int colorRGB) {
        this.colorRGB = colorRGB;
        this.color = new Color(colorRGB);
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