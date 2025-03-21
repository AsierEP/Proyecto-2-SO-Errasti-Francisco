/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Objects;

import EDD.List;

/**
 *
 * @author Dell
 */
public class Directorio {
    private String padre;
    private String nombre;
    private List<Archivo> archivos;
    private List<Directorio> subdirectorios;

    public Directorio(String padre, String nombre) {
        this.padre = padre;
        this.nombre = nombre;
        this.archivos = new List<>();
        this.subdirectorios = new List<>();
    }
    
    public Directorio(String nombre) {
        this.padre = "FileSystem";
        this.nombre = nombre;
        this.archivos = new List<>();
        this.subdirectorios = new List<>();
    }

    public String getPadre() {
        return padre;
    }

    public void setPadre(String padre) {
        this.padre = padre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Archivo> getArchivos() {
        return archivos;
    }

    public void setArchivos(List<Archivo> archivos) {
        this.archivos = archivos;
    }

    public List<Directorio> getSubdirectorios() {
        return subdirectorios;
    }

    public void setSubdirectorios(List<Directorio> subdirectorios) {
        this.subdirectorios = subdirectorios;
    }
}
