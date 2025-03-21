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
public class Contenedor {
    private List<Archivo> archivos;
    private List<Directorio> directorios;

    public Contenedor() {
        this.archivos = new List<>();
        this.directorios = new List<>();
    }

    public void agregarArchivo(Archivo archivo) {
        archivos.AddElement(archivo);
    }

    public void agregarDirectorio(Directorio directorio) {
        directorios.AddElement(directorio);
    }

    public List<Archivo> getArchivos() { return archivos; }
    public List<Directorio> getDirectorios() { return directorios; }

    public void setArchivos(List<Archivo> archivos) { this.archivos = archivos; }
    public void setDirectorios(List<Directorio> directorios) { this.directorios = directorios; }
}
