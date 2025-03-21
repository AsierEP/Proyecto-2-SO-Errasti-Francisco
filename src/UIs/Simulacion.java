/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package UIs;

import EDD.Contenedor;
import EDD.List;
import EDD.Node;
import Objects.Archivo;
import Objects.ColorCell;
import Objects.Directorio;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author Dell
 */
public final class Simulacion extends javax.swing.JFrame {
    
    
    private String mode="Administrador";
    private DefaultTreeModel model;
    private List directorios = new List();
    private List archivos = new List();


    public String getMode() {
        return mode;
    }
    
    public List getDirectorios() {
        return directorios;
    }
    
    public List getArchivos() {
        return archivos;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }


    public Simulacion() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        initComponents();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        LoadRoot("FileSystem");
        this.Tree.setEditable(false);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                guardarEstado();

                dispose();
                System.exit(0);
            }
        });
    }
    
    public void LoadRoot(String n){
        Directorio rwy = new Directorio(n);
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode (n);
        DefaultMutableTreeNode aux = new DefaultMutableTreeNode ("");
        raiz.add(aux);
        model = (DefaultTreeModel)Tree.getModel();
        model.setRoot(raiz);
        Tree.setModel(model);
        directorios.AddElement(rwy);
    }
    
    public void nuevoArchivo(String nombreArchivo, String nombrePadre,int tamano){
        boolean seAgrego = this.anadirArchivoJTree(nombrePadre, nombreArchivo,tamano);
        Archivo nuvo = new Archivo(nombreArchivo,tamano,nombrePadre);
        this.anadirArchivoJTable(seAgrego,nuvo );
    }
    
    public void anadirArchivoJTable(boolean re, Archivo nue){
        Random rand = new Random();
        int r = rand.nextInt(256);
        int g = rand.nextInt(256);
        int b = rand.nextInt(256);
        while(r==255 && g==255 && b ==255){
            r = rand.nextInt(256);
            g = rand.nextInt(256);
            b = rand.nextInt(256);
        }
        Color colorArchivo = new Color(r, g, b);
        int se=100;
        if(re){
            se = this.addFile(nue.getTamaño(), r, g, b);
        }
        if (re&&(se!=100)) {
            nue.setDireccionPrimerBloque(se);
            DefaultTableModel modeloTabla = (DefaultTableModel) Table.getModel();
            Table.getColumnModel().getColumn(3).setCellRenderer(new ColorCell());

            Object[] nuevaFila = {nue.getNombre(),nue.getDireccionPrimerBloque(), nue.getTamaño(), colorArchivo};

            modeloTabla.addRow(nuevaFila);
            this.archivos.AddElement(nue);
            nue.setR(r);
            nue.setG(g);
            nue.setB(b);
            nue.setColor(colorArchivo);
        }
    }
    
    
    public void anadirArchivoJTableConColor(boolean re, Archivo nue,int r,int g,int b){
        Color colorArchivo = new Color(r,g,b);
        int se=100;
        if(re){
            se = this.addFile(nue.getTamaño(), r, g, b);
        }
        if (re&&(se!=100)) {
            nue.setDireccionPrimerBloque(se);
            DefaultTableModel modeloTabla = (DefaultTableModel) Table.getModel();
            Table.getColumnModel().getColumn(3).setCellRenderer(new ColorCell());

            Object[] nuevaFila = {nue.getNombre(),nue.getDireccionPrimerBloque(), nue.getTamaño(), colorArchivo};

            modeloTabla.addRow(nuevaFila);
            this.archivos.AddElement(nue);
            nue.setColor(colorArchivo);
        }
    }
    
    public void eliminarArchivoJTable(Archivo archivo) {
        DefaultTableModel modeloTabla = (DefaultTableModel) Table.getModel();

        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            String nombre = (String) modeloTabla.getValueAt(i, 0);
            int tamaño = (int) modeloTabla.getValueAt(i, 2);
            Color color = (Color) modeloTabla.getValueAt(i, 3);

            if (nombre.equals(archivo.getNombre()) && 
                tamaño == archivo.getTamaño() && 
                color.equals(archivo.getColor())) {

                modeloTabla.removeRow(i);

                break;
            }
        }
    }
    
    public void eliminarDirectorio(String nombre,String padre){
        
         eliminarNodoDelArbol(padre, nombre);
        if(!archivos.IsEmpty()){
            Node actual = archivos.getPfirst();
            while(actual!=null){
                Archivo este = (Archivo)actual.getDato();
                String directorio = este.getDirectorio();
                Directorio esperado = this.directorios.encontrarDirectorio(nombre,padre);
                if(nombre.equals(este.getDirectorio())&&padre.equals(esperado.getPadre())){
                    this.eliminarArchivo(este.getNombre(), directorio);
                }
                actual=actual.getPnext();
            }
        }
        this.directorios.eliminarDirectorioDeLista(nombre, padre);
    }
    
   public boolean anadirArchivoJTree(String nombrePadre, String nombreArchivo, int fileSize) {
        
       
       DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
       DefaultMutableTreeNode parentNode = findNodeJTree(root, nombrePadre);

       if (parentNode != null) {
           for (int i = 0; i < parentNode.getChildCount(); i++) {
               DefaultMutableTreeNode child = (DefaultMutableTreeNode) parentNode.getChildAt(i);
               if (child.getUserObject().equals(nombreArchivo)) {
                   System.out.println("Error: Ya existe un archivo con el nombre '" + nombreArchivo + "' en el nodo padre '" + nombrePadre + "'.");
                   return false;
               }
           }
           if (this.findFreePanels(fileSize)==-1) {
                JOptionPane.showMessageDialog(this, "No hay suficiente espacio para el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
           }
           
           
           DefaultMutableTreeNode newFileNode = new DefaultMutableTreeNode(nombreArchivo);
           parentNode.add(newFileNode);
           model.reload(parentNode);
           this.eliminarArchivoJTree(nombrePadre,"");
           System.out.println("Archivo '" + nombreArchivo + "' añadido correctamente al nodo padre '" + nombrePadre + "'.");
           return true;
       } else {
           System.out.println("Error: Nodo padre no encontrado: " + nombrePadre);
           return false;
       }
   }
   
   public boolean anadirDirectorio(String nombrePadre, String nombreDirectorio) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        DefaultMutableTreeNode parentNode = findNodeJTree(root, nombrePadre);
        Directorio nuevv = new Directorio(nombrePadre, nombreDirectorio);

        if (parentNode != null) {
            boolean nombreRepetido = false;
            for (int i = 0; i < parentNode.getChildCount(); i++) {
                DefaultMutableTreeNode hijo = (DefaultMutableTreeNode) parentNode.getChildAt(i);
                if (hijo.getUserObject().equals(nombreDirectorio)) {
                    nombreRepetido = true;
                    return false;
                }
            }

            if (!nombreRepetido) {
                DefaultMutableTreeNode nuevoDirectorio = new DefaultMutableTreeNode(nombreDirectorio);
                DefaultMutableTreeNode hijoPredeterminado = new DefaultMutableTreeNode("");
                nuevoDirectorio.add(hijoPredeterminado);

                parentNode.add(nuevoDirectorio);
                model.reload(parentNode);
                directorios.AddElement(nuevv);
                System.out.println("Directorio '" + nombreDirectorio + "' añadido correctamente al nodo padre '" + nombrePadre + "'.");
                this.eliminarArchivoJTree(nombrePadre,"");
                return true;
            } else {
                System.out.println("Error: Ya existe un directorio con el nombre '" + nombreDirectorio + "' en el nodo padre '" + nombrePadre + "'.");
                return false;
            }
        } else {
            System.out.println("Error: No se encontró el nodo padre '" + nombrePadre + "'.");
            return false;
        }
    }
   
    private void eliminarNodoDelArbol(String nombrePadre, String nombreDirectorio) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        DefaultMutableTreeNode parentNode = findNodeJTree(root, nombrePadre);

        if (parentNode != null) {
            for (int i = 0; i < parentNode.getChildCount(); i++) {
                DefaultMutableTreeNode hijo = (DefaultMutableTreeNode) parentNode.getChildAt(i);
                if (hijo.getUserObject().equals(nombreDirectorio)) {
                    parentNode.remove(hijo);
                    model.reload(parentNode);
                    System.out.println("Directorio '" + nombreDirectorio + "' eliminado del árbol.");

                    if (parentNode.getChildCount() == 0) {
                        DefaultMutableTreeNode hojaPredeterminada = new DefaultMutableTreeNode("");
                        parentNode.add(hojaPredeterminada);
                        model.reload(parentNode);
                        System.out.println("Se agregó una hoja predeterminada al nodo padre '" + nombrePadre + "'.");
                    }

                    return;
                }
            }
            System.out.println("Error: No se encontró el directorio '" + nombreDirectorio + "' en el nodo padre '" + nombrePadre + "'.");
        } else {
            System.out.println("Error: No se encontró el nodo padre '" + nombrePadre + "'.");
        }
    }
    
    public void editarArchivo(Archivo archivo, String nuevoNombre) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        DefaultMutableTreeNode parentNode = findNodeJTree(root, archivo.getDirectorio());

        if (parentNode != null) {
            boolean nombreRepetido = false;
            for (int i = 0; i < parentNode.getChildCount(); i++) {
                DefaultMutableTreeNode hijo = (DefaultMutableTreeNode) parentNode.getChildAt(i);
                if (hijo.getUserObject().equals(nuevoNombre)) {
                    nombreRepetido = true;
                    break;
                }
            }

            if (nombreRepetido) {
                System.out.println("Error: Ya existe un archivo con el nombre '" + nuevoNombre + "' en el nodo padre '" + archivo.getDirectorio() + "'.");
                return;
            }

            for (int i = 0; i < parentNode.getChildCount(); i++) {
                DefaultMutableTreeNode hijo = (DefaultMutableTreeNode) parentNode.getChildAt(i);
                if (hijo.getUserObject().equals(archivo.getNombre())) {
                    hijo.setUserObject(nuevoNombre);
                    model.reload(parentNode);
                    System.out.println("Nombre del archivo cambiado a '" + nuevoNombre + "' en el árbol.");
                    break;
                }
            }
        } else {
            System.out.println("Error: No se encontró el nodo padre '" + archivo.getDirectorio() + "'.");
        }

        DefaultTableModel modeloTabla = (DefaultTableModel) Table.getModel();

        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            String nombre = (String) modeloTabla.getValueAt(i, 0);
            int tamaño = (int) modeloTabla.getValueAt(i, 2);
            Color color = (Color) modeloTabla.getValueAt(i, 3);

            if (nombre.equals(archivo.getNombre()) && 
                tamaño == archivo.getTamaño() && 
                color.equals(archivo.getColor())) {

                modeloTabla.setValueAt(nuevoNombre, i, 0);

                break;
            }
        }
        this.archivos.encontrarArchivoYCambiar(nuevoNombre, archivo.getNombre(), archivo.getDirectorio());
    }
    
    public void editarDirectorio(String nombrePadre, String nombreViejo, String nuevoNombre) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        DefaultMutableTreeNode parentNode = findNodeJTree(root, nombrePadre);

        if (parentNode != null) {
            boolean nombreRepetido = false;
            for (int i = 0; i < parentNode.getChildCount(); i++) {
                DefaultMutableTreeNode hijo = (DefaultMutableTreeNode) parentNode.getChildAt(i);
                if (hijo.getUserObject().equals(nuevoNombre)) {
                    nombreRepetido = true;
                    break;
                }
            }

            if (nombreRepetido) {
                System.out.println("Error: Ya existe un directorio con el nombre '" + nuevoNombre + "' en el nodo padre '" + nombrePadre + "'.");
                return;
            }

            for (int i = 0; i < parentNode.getChildCount(); i++) {
                DefaultMutableTreeNode hijo = (DefaultMutableTreeNode) parentNode.getChildAt(i);
                if (hijo.getUserObject().equals(nombreViejo)) {
                    hijo.setUserObject(nuevoNombre);
                    this.directorios.encontrarDirectorioYCambiar(nuevoNombre, nombreViejo, nombrePadre);
                    model.reload(parentNode);
                    System.out.println("Nombre del directorio cambiado de '" + nombreViejo + "' a '" + nuevoNombre + "'.");
                    return;
                }
            }
            System.out.println("Error: No se encontró el directorio '" + nombreViejo + "' en el nodo padre '" + nombrePadre + "'.");
        } else {
            System.out.println("Error: No se encontró el nodo padre '" + nombrePadre + "'.");
        }
    }

   public void eliminarArchivoJTree(String nombrePadre, String nombreArchivo) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        DefaultMutableTreeNode parentNode = findNodeJTree(root, nombrePadre);

        if (parentNode != null) {
            DefaultMutableTreeNode nodeToDelete = null;
            for (int i = 0; i < parentNode.getChildCount(); i++) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) parentNode.getChildAt(i);
                if (child.getUserObject().equals(nombreArchivo) && child.isLeaf()) {
                    nodeToDelete = child;
                    break;
                }
            }

            if (nodeToDelete != null) {
                if (parentNode.getChildCount() == 1) {
                    DefaultMutableTreeNode emptyNode = new DefaultMutableTreeNode("");
                    parentNode.add(emptyNode);
                }

                parentNode.remove(nodeToDelete);
                model.reload(parentNode);
                System.out.println("Archivo '" + nombreArchivo + "' eliminado correctamente del nodo padre '" + nombrePadre + "'.");
            } else {
                System.out.println("Error: No se encontró el archivo '" + nombreArchivo + "' como hoja del nodo padre '" + nombrePadre + "'.");
            }
        } else {
            System.out.println("Error: No se encontró el nodo padre '" + nombrePadre + "'.");
        }
    }
   
    private DefaultMutableTreeNode findNodeJTree(DefaultMutableTreeNode root, String nombre) {
        if (root.getUserObject().equals(nombre)) {
            return root;
        }

        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
            DefaultMutableTreeNode foundNode = findNodeJTree(child, nombre);
            if (foundNode != null) {
                return foundNode;
            }
        }

        return null;
    }
    
    public int addFile(int fileSize, int r, int g, int b) {
        if (fileSize > 60) {
            JOptionPane.showMessageDialog(this, "El tamaño del archivo excede el límite de 60 paneles.", "Error", JOptionPane.ERROR_MESSAGE);
            return 100;
        }

        Color color = new Color(r, g, b);

        int startPanel = findFreePanels(fileSize);
        if (startPanel != -1) {
            for (int i = startPanel; i < startPanel + fileSize; i++) {
                setPanelColor(i, color);
            }
            return startPanel;
        } else {
            JOptionPane.showMessageDialog(this, "No hay suficiente espacio para el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
            return 100;
        }
    }
    
    private int findFreePanels(int fileSize) {
        int consecutiveFreePanels = 0;
        for (int i = 1; i < 81; i++) {
            if (isPanelFree(i)) {
                consecutiveFreePanels++;
                if (consecutiveFreePanels == fileSize) {
                    System.out.println(i - fileSize + 1);
                    return i - fileSize + 1;
                }
            } else {
                consecutiveFreePanels = 0;
            }
        }
        return -1;
    }
    
    private boolean isPanelFree(int panelIndex) {
        try {
            Field field = this.getClass().getDeclaredField("Panel" + panelIndex);
            field.setAccessible(true);
            JPanel panel = (JPanel) field.get(this);
            return panel.getBackground().equals(new Color(255, 255, 255));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public void eliminarArchivo(String nombreArchivo,String nombreDirectorio) {
        Archivo archivo = this.archivos.encontrarArchivo(nombreArchivo, nombreDirectorio);
        this.archivos.eliminarArchivo(nombreArchivo,nombreDirectorio);
        int direccionPrimerBloque = archivo.getDireccionPrimerBloque();
        int tamano = archivo.getTamaño();

        for (int i = direccionPrimerBloque; i < direccionPrimerBloque + tamano; i++) {
            setPanelColor(i, new Color(255, 255, 255));
        }
        this.eliminarArchivoJTree(nombreDirectorio, nombreArchivo);
        this.eliminarArchivoJTable(archivo);
        System.out.println("Archivo eliminado y paneles liberados: " + archivo.getNombre());
    }
    
    private void setPanelColor(int panelIndex, Color color) {
        try {
            Field field = this.getClass().getDeclaredField("Panel" + panelIndex);
            field.setAccessible(true);
            JPanel panel = (JPanel) field.get(this);
            panel.setBackground(color);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetFileSystem() {
        for (int i = 1; i <= 60; i++) {
            try {
                Field field = this.getClass().getDeclaredField("jPanel" + i);
                field.setAccessible(true);
                JPanel panel = (JPanel) field.get(this);
                panel.setBackground(Color.WHITE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.revalidate();
        this.repaint();
    }
    
    public void guardarEstado() {
        Contenedor contenedor = new Contenedor();
        contenedor.setArchivos(this.archivos);
        contenedor.setDirectorios(this.directorios);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("test/simulacion.json")) {
            gson.toJson(contenedor, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void cargarEstado() {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader("test/simulacion.json")) {
            Contenedor contenedor = gson.fromJson(reader, Contenedor.class);

            DefaultTableModel modeloTabla = (DefaultTableModel) Table.getModel();
            modeloTabla.setRowCount(0);
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
            root.removeAllChildren();
            model.reload();

            Node<Directorio> actualDirectorio = contenedor.getDirectorios().getPfirst();
            while (actualDirectorio != null) {
                Directorio directorio = actualDirectorio.getDato();
                if(!directorio.getNombre().equals("FileSystem")){
                                    this.anadirDirectorio(directorio.getPadre(), directorio.getNombre());
                }
                actualDirectorio = actualDirectorio.getPnext();
            }

           Node<Archivo> actualArchivo = contenedor.getArchivos().getPfirst();
            while (actualArchivo != null) {
                Archivo archivo = actualArchivo.getDato();

                boolean seAgrego = this.anadirArchivoJTree(archivo.getDirectorio(), archivo.getNombre(), archivo.getTamaño());

                if (seAgrego) {
                    this.anadirArchivoJTableConColor(true, archivo, archivo.getR(),archivo.getG(),archivo.getB());
                }

                actualArchivo = actualArchivo.getPnext();
            }

            System.out.println("Estado cargado correctamente.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        BGPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Tree = new javax.swing.JTree();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table = new javax.swing.JTable();
        CrearArchivoButt = new javax.swing.JButton();
        CrearDirButt = new javax.swing.JButton();
        ModoLab = new javax.swing.JLabel();
        ChangeModeButt = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        Panel10 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        Panel20 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        Panel30 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        Panel40 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        Panel50 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        Panel60 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        Panel59 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        Panel58 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        Panel57 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        Panel56 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        Panel55 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        Panel54 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        Panel53 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        Panel52 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        Panel51 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        Panel9 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        Panel19 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        Panel39 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        Panel49 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        Panel29 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        Panel48 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        Panel38 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        Panel28 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        Panel18 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        Panel8 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        Panel47 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        Panel37 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        Panel27 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        Panel17 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        Panel7 = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        Panel46 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        Panel36 = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        Panel26 = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        Panel45 = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        Panel35 = new javax.swing.JPanel();
        jLabel38 = new javax.swing.JLabel();
        Panel25 = new javax.swing.JPanel();
        jLabel39 = new javax.swing.JLabel();
        Panel24 = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        Panel34 = new javax.swing.JPanel();
        jLabel41 = new javax.swing.JLabel();
        Panel44 = new javax.swing.JPanel();
        jLabel42 = new javax.swing.JLabel();
        Panel43 = new javax.swing.JPanel();
        jLabel43 = new javax.swing.JLabel();
        Panel42 = new javax.swing.JPanel();
        jLabel44 = new javax.swing.JLabel();
        Panel41 = new javax.swing.JPanel();
        jLabel45 = new javax.swing.JLabel();
        Panel33 = new javax.swing.JPanel();
        jLabel46 = new javax.swing.JLabel();
        Panel32 = new javax.swing.JPanel();
        jLabel47 = new javax.swing.JLabel();
        Panel31 = new javax.swing.JPanel();
        jLabel48 = new javax.swing.JLabel();
        Panel23 = new javax.swing.JPanel();
        jLabel49 = new javax.swing.JLabel();
        Panel22 = new javax.swing.JPanel();
        jLabel50 = new javax.swing.JLabel();
        Panel21 = new javax.swing.JPanel();
        jLabel51 = new javax.swing.JLabel();
        Panel16 = new javax.swing.JPanel();
        jLabel52 = new javax.swing.JLabel();
        Panel15 = new javax.swing.JPanel();
        jLabel53 = new javax.swing.JLabel();
        Panel14 = new javax.swing.JPanel();
        jLabel54 = new javax.swing.JLabel();
        Panel13 = new javax.swing.JPanel();
        jLabel55 = new javax.swing.JLabel();
        Panel12 = new javax.swing.JPanel();
        jLabel56 = new javax.swing.JLabel();
        Panel11 = new javax.swing.JPanel();
        jLabel57 = new javax.swing.JLabel();
        Panel6 = new javax.swing.JPanel();
        jLabel58 = new javax.swing.JLabel();
        Panel5 = new javax.swing.JPanel();
        jLabel59 = new javax.swing.JLabel();
        Panel4 = new javax.swing.JPanel();
        jLabel60 = new javax.swing.JLabel();
        Panel3 = new javax.swing.JPanel();
        jLabel61 = new javax.swing.JLabel();
        Panel2 = new javax.swing.JPanel();
        jLabel62 = new javax.swing.JLabel();
        Panel1 = new javax.swing.JPanel();
        jLabel63 = new javax.swing.JLabel();
        ActArchivoButt = new javax.swing.JButton();
        ActDirButt = new javax.swing.JButton();
        BorrarArchivoButt = new javax.swing.JButton();
        BorrarDirButt = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        BGPanel.setBackground(new java.awt.Color(2, 2, 130));

        jScrollPane1.setViewportView(Tree);

        Table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Nombre", "Bloque inicial", "Longitud", "Color"
            }
        ));
        jScrollPane2.setViewportView(Table);

        CrearArchivoButt.setBackground(new java.awt.Color(51, 255, 51));
        CrearArchivoButt.setText("Crear Archivo");
        CrearArchivoButt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CrearArchivoButtActionPerformed(evt);
            }
        });

        CrearDirButt.setBackground(new java.awt.Color(51, 153, 255));
        CrearDirButt.setText("Crear Directorio");
        CrearDirButt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CrearDirButtActionPerformed(evt);
            }
        });

        ModoLab.setBackground(new java.awt.Color(255, 255, 204));
        ModoLab.setForeground(new java.awt.Color(255, 255, 255));
        ModoLab.setText("Modo Actual: Administrador");

        ChangeModeButt.setBackground(new java.awt.Color(255, 255, 153));
        ChangeModeButt.setText("Cambiar de modo");
        ChangeModeButt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChangeModeButtActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Castellar", 3, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("SIMULACIÓN");

        Panel10.setBackground(new java.awt.Color(255, 255, 255));
        Panel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel10.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel3.setText("10");

        javax.swing.GroupLayout Panel10Layout = new javax.swing.GroupLayout(Panel10);
        Panel10.setLayout(Panel10Layout);
        Panel10Layout.setHorizontalGroup(
            Panel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel10Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel10Layout.setVerticalGroup(
            Panel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel10Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel20.setBackground(new java.awt.Color(255, 255, 255));
        Panel20.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel20.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel4.setText("20");

        javax.swing.GroupLayout Panel20Layout = new javax.swing.GroupLayout(Panel20);
        Panel20.setLayout(Panel20Layout);
        Panel20Layout.setHorizontalGroup(
            Panel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel20Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel20Layout.setVerticalGroup(
            Panel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel20Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel30.setBackground(new java.awt.Color(255, 255, 255));
        Panel30.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel30.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel5.setText("30");

        javax.swing.GroupLayout Panel30Layout = new javax.swing.GroupLayout(Panel30);
        Panel30.setLayout(Panel30Layout);
        Panel30Layout.setHorizontalGroup(
            Panel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel30Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel30Layout.setVerticalGroup(
            Panel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel30Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel40.setBackground(new java.awt.Color(255, 255, 255));
        Panel40.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel40.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel6.setText("40");

        javax.swing.GroupLayout Panel40Layout = new javax.swing.GroupLayout(Panel40);
        Panel40.setLayout(Panel40Layout);
        Panel40Layout.setHorizontalGroup(
            Panel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel40Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel40Layout.setVerticalGroup(
            Panel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel40Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel50.setBackground(new java.awt.Color(255, 255, 255));
        Panel50.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel50.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel7.setText("50");

        javax.swing.GroupLayout Panel50Layout = new javax.swing.GroupLayout(Panel50);
        Panel50.setLayout(Panel50Layout);
        Panel50Layout.setHorizontalGroup(
            Panel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel50Layout.createSequentialGroup()
                .addComponent(jLabel7)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel50Layout.setVerticalGroup(
            Panel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel50Layout.createSequentialGroup()
                .addComponent(jLabel7)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel60.setBackground(new java.awt.Color(255, 255, 255));
        Panel60.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel60.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel8.setText("60");

        javax.swing.GroupLayout Panel60Layout = new javax.swing.GroupLayout(Panel60);
        Panel60.setLayout(Panel60Layout);
        Panel60Layout.setHorizontalGroup(
            Panel60Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel60Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel60Layout.setVerticalGroup(
            Panel60Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel60Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel59.setBackground(new java.awt.Color(255, 255, 255));
        Panel59.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel59.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel10.setText("59");

        javax.swing.GroupLayout Panel59Layout = new javax.swing.GroupLayout(Panel59);
        Panel59.setLayout(Panel59Layout);
        Panel59Layout.setHorizontalGroup(
            Panel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel59Layout.createSequentialGroup()
                .addComponent(jLabel10)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel59Layout.setVerticalGroup(
            Panel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel59Layout.createSequentialGroup()
                .addComponent(jLabel10)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel58.setBackground(new java.awt.Color(255, 255, 255));
        Panel58.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel58.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel11.setText("58");

        javax.swing.GroupLayout Panel58Layout = new javax.swing.GroupLayout(Panel58);
        Panel58.setLayout(Panel58Layout);
        Panel58Layout.setHorizontalGroup(
            Panel58Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel58Layout.createSequentialGroup()
                .addComponent(jLabel11)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel58Layout.setVerticalGroup(
            Panel58Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel58Layout.createSequentialGroup()
                .addComponent(jLabel11)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel57.setBackground(new java.awt.Color(255, 255, 255));
        Panel57.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel57.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel12.setText("57");

        javax.swing.GroupLayout Panel57Layout = new javax.swing.GroupLayout(Panel57);
        Panel57.setLayout(Panel57Layout);
        Panel57Layout.setHorizontalGroup(
            Panel57Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel57Layout.createSequentialGroup()
                .addComponent(jLabel12)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel57Layout.setVerticalGroup(
            Panel57Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel57Layout.createSequentialGroup()
                .addComponent(jLabel12)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel56.setBackground(new java.awt.Color(255, 255, 255));
        Panel56.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel56.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel13.setText("56");

        javax.swing.GroupLayout Panel56Layout = new javax.swing.GroupLayout(Panel56);
        Panel56.setLayout(Panel56Layout);
        Panel56Layout.setHorizontalGroup(
            Panel56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel56Layout.createSequentialGroup()
                .addComponent(jLabel13)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel56Layout.setVerticalGroup(
            Panel56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel56Layout.createSequentialGroup()
                .addComponent(jLabel13)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel55.setBackground(new java.awt.Color(255, 255, 255));
        Panel55.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel55.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel14.setText("55");

        javax.swing.GroupLayout Panel55Layout = new javax.swing.GroupLayout(Panel55);
        Panel55.setLayout(Panel55Layout);
        Panel55Layout.setHorizontalGroup(
            Panel55Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel55Layout.createSequentialGroup()
                .addComponent(jLabel14)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel55Layout.setVerticalGroup(
            Panel55Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel55Layout.createSequentialGroup()
                .addComponent(jLabel14)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel54.setBackground(new java.awt.Color(255, 255, 255));
        Panel54.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel54.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel15.setText("54");

        javax.swing.GroupLayout Panel54Layout = new javax.swing.GroupLayout(Panel54);
        Panel54.setLayout(Panel54Layout);
        Panel54Layout.setHorizontalGroup(
            Panel54Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel54Layout.createSequentialGroup()
                .addComponent(jLabel15)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel54Layout.setVerticalGroup(
            Panel54Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel54Layout.createSequentialGroup()
                .addComponent(jLabel15)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel53.setBackground(new java.awt.Color(255, 255, 255));
        Panel53.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel53.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel16.setText("53");

        javax.swing.GroupLayout Panel53Layout = new javax.swing.GroupLayout(Panel53);
        Panel53.setLayout(Panel53Layout);
        Panel53Layout.setHorizontalGroup(
            Panel53Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel53Layout.createSequentialGroup()
                .addComponent(jLabel16)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel53Layout.setVerticalGroup(
            Panel53Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel53Layout.createSequentialGroup()
                .addComponent(jLabel16)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel52.setBackground(new java.awt.Color(255, 255, 255));
        Panel52.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel52.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel17.setText("52");

        javax.swing.GroupLayout Panel52Layout = new javax.swing.GroupLayout(Panel52);
        Panel52.setLayout(Panel52Layout);
        Panel52Layout.setHorizontalGroup(
            Panel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel52Layout.createSequentialGroup()
                .addComponent(jLabel17)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel52Layout.setVerticalGroup(
            Panel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel52Layout.createSequentialGroup()
                .addComponent(jLabel17)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel51.setBackground(new java.awt.Color(255, 255, 255));
        Panel51.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel51.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel18.setText("51");

        javax.swing.GroupLayout Panel51Layout = new javax.swing.GroupLayout(Panel51);
        Panel51.setLayout(Panel51Layout);
        Panel51Layout.setHorizontalGroup(
            Panel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel51Layout.createSequentialGroup()
                .addComponent(jLabel18)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel51Layout.setVerticalGroup(
            Panel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel51Layout.createSequentialGroup()
                .addComponent(jLabel18)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel9.setBackground(new java.awt.Color(255, 255, 255));
        Panel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel9.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel19.setText("9");

        javax.swing.GroupLayout Panel9Layout = new javax.swing.GroupLayout(Panel9);
        Panel9.setLayout(Panel9Layout);
        Panel9Layout.setHorizontalGroup(
            Panel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel9Layout.createSequentialGroup()
                .addComponent(jLabel19)
                .addGap(0, 72, Short.MAX_VALUE))
        );
        Panel9Layout.setVerticalGroup(
            Panel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel9Layout.createSequentialGroup()
                .addComponent(jLabel19)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel19.setBackground(new java.awt.Color(255, 255, 255));
        Panel19.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel19.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel20.setText("19");

        javax.swing.GroupLayout Panel19Layout = new javax.swing.GroupLayout(Panel19);
        Panel19.setLayout(Panel19Layout);
        Panel19Layout.setHorizontalGroup(
            Panel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel19Layout.createSequentialGroup()
                .addComponent(jLabel20)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel19Layout.setVerticalGroup(
            Panel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel19Layout.createSequentialGroup()
                .addComponent(jLabel20)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel39.setBackground(new java.awt.Color(255, 255, 255));
        Panel39.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel39.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel21.setText("39");

        javax.swing.GroupLayout Panel39Layout = new javax.swing.GroupLayout(Panel39);
        Panel39.setLayout(Panel39Layout);
        Panel39Layout.setHorizontalGroup(
            Panel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel39Layout.createSequentialGroup()
                .addComponent(jLabel21)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel39Layout.setVerticalGroup(
            Panel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel39Layout.createSequentialGroup()
                .addComponent(jLabel21)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel49.setBackground(new java.awt.Color(255, 255, 255));
        Panel49.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel49.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel22.setText("49");

        javax.swing.GroupLayout Panel49Layout = new javax.swing.GroupLayout(Panel49);
        Panel49.setLayout(Panel49Layout);
        Panel49Layout.setHorizontalGroup(
            Panel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel49Layout.createSequentialGroup()
                .addComponent(jLabel22)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel49Layout.setVerticalGroup(
            Panel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel49Layout.createSequentialGroup()
                .addComponent(jLabel22)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel29.setBackground(new java.awt.Color(255, 255, 255));
        Panel29.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel29.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel23.setText("29");

        javax.swing.GroupLayout Panel29Layout = new javax.swing.GroupLayout(Panel29);
        Panel29.setLayout(Panel29Layout);
        Panel29Layout.setHorizontalGroup(
            Panel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel29Layout.createSequentialGroup()
                .addComponent(jLabel23)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel29Layout.setVerticalGroup(
            Panel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel29Layout.createSequentialGroup()
                .addComponent(jLabel23)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel48.setBackground(new java.awt.Color(255, 255, 255));
        Panel48.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel48.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel24.setText("48");

        javax.swing.GroupLayout Panel48Layout = new javax.swing.GroupLayout(Panel48);
        Panel48.setLayout(Panel48Layout);
        Panel48Layout.setHorizontalGroup(
            Panel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel48Layout.createSequentialGroup()
                .addComponent(jLabel24)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel48Layout.setVerticalGroup(
            Panel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel48Layout.createSequentialGroup()
                .addComponent(jLabel24)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel38.setBackground(new java.awt.Color(255, 255, 255));
        Panel38.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel38.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel25.setText("38");

        javax.swing.GroupLayout Panel38Layout = new javax.swing.GroupLayout(Panel38);
        Panel38.setLayout(Panel38Layout);
        Panel38Layout.setHorizontalGroup(
            Panel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel38Layout.createSequentialGroup()
                .addComponent(jLabel25)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel38Layout.setVerticalGroup(
            Panel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel38Layout.createSequentialGroup()
                .addComponent(jLabel25)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel28.setBackground(new java.awt.Color(255, 255, 255));
        Panel28.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel28.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel26.setText("28");

        javax.swing.GroupLayout Panel28Layout = new javax.swing.GroupLayout(Panel28);
        Panel28.setLayout(Panel28Layout);
        Panel28Layout.setHorizontalGroup(
            Panel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel28Layout.createSequentialGroup()
                .addComponent(jLabel26)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel28Layout.setVerticalGroup(
            Panel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel28Layout.createSequentialGroup()
                .addComponent(jLabel26)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel18.setBackground(new java.awt.Color(255, 255, 255));
        Panel18.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel18.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel27.setText("18");

        javax.swing.GroupLayout Panel18Layout = new javax.swing.GroupLayout(Panel18);
        Panel18.setLayout(Panel18Layout);
        Panel18Layout.setHorizontalGroup(
            Panel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel18Layout.createSequentialGroup()
                .addComponent(jLabel27)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel18Layout.setVerticalGroup(
            Panel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel18Layout.createSequentialGroup()
                .addComponent(jLabel27)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel8.setBackground(new java.awt.Color(255, 255, 255));
        Panel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel8.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel28.setText("8");

        javax.swing.GroupLayout Panel8Layout = new javax.swing.GroupLayout(Panel8);
        Panel8.setLayout(Panel8Layout);
        Panel8Layout.setHorizontalGroup(
            Panel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel8Layout.createSequentialGroup()
                .addComponent(jLabel28)
                .addGap(0, 72, Short.MAX_VALUE))
        );
        Panel8Layout.setVerticalGroup(
            Panel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel8Layout.createSequentialGroup()
                .addComponent(jLabel28)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel47.setBackground(new java.awt.Color(255, 255, 255));
        Panel47.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel47.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel29.setText("47");

        javax.swing.GroupLayout Panel47Layout = new javax.swing.GroupLayout(Panel47);
        Panel47.setLayout(Panel47Layout);
        Panel47Layout.setHorizontalGroup(
            Panel47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel47Layout.createSequentialGroup()
                .addComponent(jLabel29)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel47Layout.setVerticalGroup(
            Panel47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel47Layout.createSequentialGroup()
                .addComponent(jLabel29)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel37.setBackground(new java.awt.Color(255, 255, 255));
        Panel37.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel37.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel30.setText("37");

        javax.swing.GroupLayout Panel37Layout = new javax.swing.GroupLayout(Panel37);
        Panel37.setLayout(Panel37Layout);
        Panel37Layout.setHorizontalGroup(
            Panel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel37Layout.createSequentialGroup()
                .addComponent(jLabel30)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel37Layout.setVerticalGroup(
            Panel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel37Layout.createSequentialGroup()
                .addComponent(jLabel30)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel27.setBackground(new java.awt.Color(255, 255, 255));
        Panel27.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel27.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel31.setText("27");

        javax.swing.GroupLayout Panel27Layout = new javax.swing.GroupLayout(Panel27);
        Panel27.setLayout(Panel27Layout);
        Panel27Layout.setHorizontalGroup(
            Panel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel27Layout.createSequentialGroup()
                .addComponent(jLabel31)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel27Layout.setVerticalGroup(
            Panel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel27Layout.createSequentialGroup()
                .addComponent(jLabel31)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel17.setBackground(new java.awt.Color(255, 255, 255));
        Panel17.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel17.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel32.setText("17");

        javax.swing.GroupLayout Panel17Layout = new javax.swing.GroupLayout(Panel17);
        Panel17.setLayout(Panel17Layout);
        Panel17Layout.setHorizontalGroup(
            Panel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel17Layout.createSequentialGroup()
                .addComponent(jLabel32)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel17Layout.setVerticalGroup(
            Panel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel17Layout.createSequentialGroup()
                .addComponent(jLabel32)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel7.setBackground(new java.awt.Color(255, 255, 255));
        Panel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel7.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel33.setText("7");

        javax.swing.GroupLayout Panel7Layout = new javax.swing.GroupLayout(Panel7);
        Panel7.setLayout(Panel7Layout);
        Panel7Layout.setHorizontalGroup(
            Panel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel7Layout.createSequentialGroup()
                .addComponent(jLabel33)
                .addGap(0, 72, Short.MAX_VALUE))
        );
        Panel7Layout.setVerticalGroup(
            Panel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel7Layout.createSequentialGroup()
                .addComponent(jLabel33)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel46.setBackground(new java.awt.Color(255, 255, 255));
        Panel46.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel46.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel34.setText("46");

        javax.swing.GroupLayout Panel46Layout = new javax.swing.GroupLayout(Panel46);
        Panel46.setLayout(Panel46Layout);
        Panel46Layout.setHorizontalGroup(
            Panel46Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel46Layout.createSequentialGroup()
                .addComponent(jLabel34)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel46Layout.setVerticalGroup(
            Panel46Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel46Layout.createSequentialGroup()
                .addComponent(jLabel34)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel36.setBackground(new java.awt.Color(255, 255, 255));
        Panel36.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel36.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel35.setText("36");

        javax.swing.GroupLayout Panel36Layout = new javax.swing.GroupLayout(Panel36);
        Panel36.setLayout(Panel36Layout);
        Panel36Layout.setHorizontalGroup(
            Panel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel36Layout.createSequentialGroup()
                .addComponent(jLabel35)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel36Layout.setVerticalGroup(
            Panel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel36Layout.createSequentialGroup()
                .addComponent(jLabel35)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel26.setBackground(new java.awt.Color(255, 255, 255));
        Panel26.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel26.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel36.setText("26");

        javax.swing.GroupLayout Panel26Layout = new javax.swing.GroupLayout(Panel26);
        Panel26.setLayout(Panel26Layout);
        Panel26Layout.setHorizontalGroup(
            Panel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel26Layout.createSequentialGroup()
                .addComponent(jLabel36)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel26Layout.setVerticalGroup(
            Panel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel26Layout.createSequentialGroup()
                .addComponent(jLabel36)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel45.setBackground(new java.awt.Color(255, 255, 255));
        Panel45.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel45.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel37.setText("45");

        javax.swing.GroupLayout Panel45Layout = new javax.swing.GroupLayout(Panel45);
        Panel45.setLayout(Panel45Layout);
        Panel45Layout.setHorizontalGroup(
            Panel45Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel45Layout.createSequentialGroup()
                .addComponent(jLabel37)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel45Layout.setVerticalGroup(
            Panel45Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel45Layout.createSequentialGroup()
                .addComponent(jLabel37)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel35.setBackground(new java.awt.Color(255, 255, 255));
        Panel35.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel35.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel38.setText("35");

        javax.swing.GroupLayout Panel35Layout = new javax.swing.GroupLayout(Panel35);
        Panel35.setLayout(Panel35Layout);
        Panel35Layout.setHorizontalGroup(
            Panel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel35Layout.createSequentialGroup()
                .addComponent(jLabel38)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel35Layout.setVerticalGroup(
            Panel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel35Layout.createSequentialGroup()
                .addComponent(jLabel38)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel25.setBackground(new java.awt.Color(255, 255, 255));
        Panel25.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel25.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel39.setText("25");

        javax.swing.GroupLayout Panel25Layout = new javax.swing.GroupLayout(Panel25);
        Panel25.setLayout(Panel25Layout);
        Panel25Layout.setHorizontalGroup(
            Panel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel25Layout.createSequentialGroup()
                .addComponent(jLabel39)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel25Layout.setVerticalGroup(
            Panel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel25Layout.createSequentialGroup()
                .addComponent(jLabel39)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel24.setBackground(new java.awt.Color(255, 255, 255));
        Panel24.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel24.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel40.setText("24");

        javax.swing.GroupLayout Panel24Layout = new javax.swing.GroupLayout(Panel24);
        Panel24.setLayout(Panel24Layout);
        Panel24Layout.setHorizontalGroup(
            Panel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel24Layout.createSequentialGroup()
                .addComponent(jLabel40)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel24Layout.setVerticalGroup(
            Panel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel24Layout.createSequentialGroup()
                .addComponent(jLabel40)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel34.setBackground(new java.awt.Color(255, 255, 255));
        Panel34.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel34.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel41.setText("34");

        javax.swing.GroupLayout Panel34Layout = new javax.swing.GroupLayout(Panel34);
        Panel34.setLayout(Panel34Layout);
        Panel34Layout.setHorizontalGroup(
            Panel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel34Layout.createSequentialGroup()
                .addComponent(jLabel41)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel34Layout.setVerticalGroup(
            Panel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel34Layout.createSequentialGroup()
                .addComponent(jLabel41)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel44.setBackground(new java.awt.Color(255, 255, 255));
        Panel44.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel44.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel42.setText("44");

        javax.swing.GroupLayout Panel44Layout = new javax.swing.GroupLayout(Panel44);
        Panel44.setLayout(Panel44Layout);
        Panel44Layout.setHorizontalGroup(
            Panel44Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel44Layout.createSequentialGroup()
                .addComponent(jLabel42)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel44Layout.setVerticalGroup(
            Panel44Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel44Layout.createSequentialGroup()
                .addComponent(jLabel42)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel43.setBackground(new java.awt.Color(255, 255, 255));
        Panel43.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel43.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel43.setText("43");

        javax.swing.GroupLayout Panel43Layout = new javax.swing.GroupLayout(Panel43);
        Panel43.setLayout(Panel43Layout);
        Panel43Layout.setHorizontalGroup(
            Panel43Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel43Layout.createSequentialGroup()
                .addComponent(jLabel43)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel43Layout.setVerticalGroup(
            Panel43Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel43Layout.createSequentialGroup()
                .addComponent(jLabel43)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel42.setBackground(new java.awt.Color(255, 255, 255));
        Panel42.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel42.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel44.setText("42");

        javax.swing.GroupLayout Panel42Layout = new javax.swing.GroupLayout(Panel42);
        Panel42.setLayout(Panel42Layout);
        Panel42Layout.setHorizontalGroup(
            Panel42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel42Layout.createSequentialGroup()
                .addComponent(jLabel44)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel42Layout.setVerticalGroup(
            Panel42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel42Layout.createSequentialGroup()
                .addComponent(jLabel44)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel41.setBackground(new java.awt.Color(255, 255, 255));
        Panel41.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel41.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel45.setText("41");

        javax.swing.GroupLayout Panel41Layout = new javax.swing.GroupLayout(Panel41);
        Panel41.setLayout(Panel41Layout);
        Panel41Layout.setHorizontalGroup(
            Panel41Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel41Layout.createSequentialGroup()
                .addComponent(jLabel45)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel41Layout.setVerticalGroup(
            Panel41Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel41Layout.createSequentialGroup()
                .addComponent(jLabel45)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel33.setBackground(new java.awt.Color(255, 255, 255));
        Panel33.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel33.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel46.setText("33");

        javax.swing.GroupLayout Panel33Layout = new javax.swing.GroupLayout(Panel33);
        Panel33.setLayout(Panel33Layout);
        Panel33Layout.setHorizontalGroup(
            Panel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel33Layout.createSequentialGroup()
                .addComponent(jLabel46)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel33Layout.setVerticalGroup(
            Panel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel33Layout.createSequentialGroup()
                .addComponent(jLabel46)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel32.setBackground(new java.awt.Color(255, 255, 255));
        Panel32.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel32.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel47.setText("32");

        javax.swing.GroupLayout Panel32Layout = new javax.swing.GroupLayout(Panel32);
        Panel32.setLayout(Panel32Layout);
        Panel32Layout.setHorizontalGroup(
            Panel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel32Layout.createSequentialGroup()
                .addComponent(jLabel47)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel32Layout.setVerticalGroup(
            Panel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel32Layout.createSequentialGroup()
                .addComponent(jLabel47)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel31.setBackground(new java.awt.Color(255, 255, 255));
        Panel31.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel31.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel48.setText("31");

        javax.swing.GroupLayout Panel31Layout = new javax.swing.GroupLayout(Panel31);
        Panel31.setLayout(Panel31Layout);
        Panel31Layout.setHorizontalGroup(
            Panel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel31Layout.createSequentialGroup()
                .addComponent(jLabel48)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel31Layout.setVerticalGroup(
            Panel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel31Layout.createSequentialGroup()
                .addComponent(jLabel48)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel23.setBackground(new java.awt.Color(255, 255, 255));
        Panel23.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel23.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel49.setText("23");

        javax.swing.GroupLayout Panel23Layout = new javax.swing.GroupLayout(Panel23);
        Panel23.setLayout(Panel23Layout);
        Panel23Layout.setHorizontalGroup(
            Panel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel23Layout.createSequentialGroup()
                .addComponent(jLabel49)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel23Layout.setVerticalGroup(
            Panel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel23Layout.createSequentialGroup()
                .addComponent(jLabel49)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel22.setBackground(new java.awt.Color(255, 255, 255));
        Panel22.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel22.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel50.setText("22");

        javax.swing.GroupLayout Panel22Layout = new javax.swing.GroupLayout(Panel22);
        Panel22.setLayout(Panel22Layout);
        Panel22Layout.setHorizontalGroup(
            Panel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel22Layout.createSequentialGroup()
                .addComponent(jLabel50)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel22Layout.setVerticalGroup(
            Panel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel22Layout.createSequentialGroup()
                .addComponent(jLabel50)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel21.setBackground(new java.awt.Color(255, 255, 255));
        Panel21.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel21.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel51.setText("21");

        javax.swing.GroupLayout Panel21Layout = new javax.swing.GroupLayout(Panel21);
        Panel21.setLayout(Panel21Layout);
        Panel21Layout.setHorizontalGroup(
            Panel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel21Layout.createSequentialGroup()
                .addComponent(jLabel51)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel21Layout.setVerticalGroup(
            Panel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel21Layout.createSequentialGroup()
                .addComponent(jLabel51)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel16.setBackground(new java.awt.Color(255, 255, 255));
        Panel16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel16.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel52.setText("16");

        javax.swing.GroupLayout Panel16Layout = new javax.swing.GroupLayout(Panel16);
        Panel16.setLayout(Panel16Layout);
        Panel16Layout.setHorizontalGroup(
            Panel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel16Layout.createSequentialGroup()
                .addComponent(jLabel52)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel16Layout.setVerticalGroup(
            Panel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel16Layout.createSequentialGroup()
                .addComponent(jLabel52)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel15.setBackground(new java.awt.Color(255, 255, 255));
        Panel15.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel15.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel53.setText("15");

        javax.swing.GroupLayout Panel15Layout = new javax.swing.GroupLayout(Panel15);
        Panel15.setLayout(Panel15Layout);
        Panel15Layout.setHorizontalGroup(
            Panel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel15Layout.createSequentialGroup()
                .addComponent(jLabel53)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel15Layout.setVerticalGroup(
            Panel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel15Layout.createSequentialGroup()
                .addComponent(jLabel53)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel14.setBackground(new java.awt.Color(255, 255, 255));
        Panel14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel14.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel54.setText("14");

        javax.swing.GroupLayout Panel14Layout = new javax.swing.GroupLayout(Panel14);
        Panel14.setLayout(Panel14Layout);
        Panel14Layout.setHorizontalGroup(
            Panel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel14Layout.createSequentialGroup()
                .addComponent(jLabel54)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel14Layout.setVerticalGroup(
            Panel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel14Layout.createSequentialGroup()
                .addComponent(jLabel54)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel13.setBackground(new java.awt.Color(255, 255, 255));
        Panel13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel13.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel55.setText("13");

        javax.swing.GroupLayout Panel13Layout = new javax.swing.GroupLayout(Panel13);
        Panel13.setLayout(Panel13Layout);
        Panel13Layout.setHorizontalGroup(
            Panel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel13Layout.createSequentialGroup()
                .addComponent(jLabel55)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel13Layout.setVerticalGroup(
            Panel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel13Layout.createSequentialGroup()
                .addComponent(jLabel55)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel12.setBackground(new java.awt.Color(255, 255, 255));
        Panel12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel12.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel56.setText("12");

        javax.swing.GroupLayout Panel12Layout = new javax.swing.GroupLayout(Panel12);
        Panel12.setLayout(Panel12Layout);
        Panel12Layout.setHorizontalGroup(
            Panel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel12Layout.createSequentialGroup()
                .addComponent(jLabel56)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel12Layout.setVerticalGroup(
            Panel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel12Layout.createSequentialGroup()
                .addComponent(jLabel56)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel11.setBackground(new java.awt.Color(255, 255, 255));
        Panel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel11.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel57.setText("11");

        javax.swing.GroupLayout Panel11Layout = new javax.swing.GroupLayout(Panel11);
        Panel11.setLayout(Panel11Layout);
        Panel11Layout.setHorizontalGroup(
            Panel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel11Layout.createSequentialGroup()
                .addComponent(jLabel57)
                .addGap(0, 65, Short.MAX_VALUE))
        );
        Panel11Layout.setVerticalGroup(
            Panel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel11Layout.createSequentialGroup()
                .addComponent(jLabel57)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel6.setBackground(new java.awt.Color(255, 255, 255));
        Panel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel6.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel58.setText("6");

        javax.swing.GroupLayout Panel6Layout = new javax.swing.GroupLayout(Panel6);
        Panel6.setLayout(Panel6Layout);
        Panel6Layout.setHorizontalGroup(
            Panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel6Layout.createSequentialGroup()
                .addComponent(jLabel58)
                .addGap(0, 72, Short.MAX_VALUE))
        );
        Panel6Layout.setVerticalGroup(
            Panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel6Layout.createSequentialGroup()
                .addComponent(jLabel58)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel5.setBackground(new java.awt.Color(255, 255, 255));
        Panel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel5.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel59.setText("5");

        javax.swing.GroupLayout Panel5Layout = new javax.swing.GroupLayout(Panel5);
        Panel5.setLayout(Panel5Layout);
        Panel5Layout.setHorizontalGroup(
            Panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel5Layout.createSequentialGroup()
                .addComponent(jLabel59)
                .addGap(0, 72, Short.MAX_VALUE))
        );
        Panel5Layout.setVerticalGroup(
            Panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel5Layout.createSequentialGroup()
                .addComponent(jLabel59)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel4.setBackground(new java.awt.Color(255, 255, 255));
        Panel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel4.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel60.setText("4");

        javax.swing.GroupLayout Panel4Layout = new javax.swing.GroupLayout(Panel4);
        Panel4.setLayout(Panel4Layout);
        Panel4Layout.setHorizontalGroup(
            Panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel4Layout.createSequentialGroup()
                .addComponent(jLabel60)
                .addGap(0, 72, Short.MAX_VALUE))
        );
        Panel4Layout.setVerticalGroup(
            Panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel4Layout.createSequentialGroup()
                .addComponent(jLabel60)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel3.setBackground(new java.awt.Color(255, 255, 255));
        Panel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel3.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel61.setText("3");

        javax.swing.GroupLayout Panel3Layout = new javax.swing.GroupLayout(Panel3);
        Panel3.setLayout(Panel3Layout);
        Panel3Layout.setHorizontalGroup(
            Panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel3Layout.createSequentialGroup()
                .addComponent(jLabel61)
                .addGap(0, 72, Short.MAX_VALUE))
        );
        Panel3Layout.setVerticalGroup(
            Panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel3Layout.createSequentialGroup()
                .addComponent(jLabel61)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel2.setBackground(new java.awt.Color(255, 255, 255));
        Panel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel2.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel62.setText("2");

        javax.swing.GroupLayout Panel2Layout = new javax.swing.GroupLayout(Panel2);
        Panel2.setLayout(Panel2Layout);
        Panel2Layout.setHorizontalGroup(
            Panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel2Layout.createSequentialGroup()
                .addComponent(jLabel62)
                .addGap(0, 72, Short.MAX_VALUE))
        );
        Panel2Layout.setVerticalGroup(
            Panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel2Layout.createSequentialGroup()
                .addComponent(jLabel62)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        Panel1.setBackground(new java.awt.Color(255, 255, 255));
        Panel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Panel1.setPreferredSize(new java.awt.Dimension(80, 80));

        jLabel63.setText("1");

        javax.swing.GroupLayout Panel1Layout = new javax.swing.GroupLayout(Panel1);
        Panel1.setLayout(Panel1Layout);
        Panel1Layout.setHorizontalGroup(
            Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel1Layout.createSequentialGroup()
                .addComponent(jLabel63)
                .addGap(0, 72, Short.MAX_VALUE))
        );
        Panel1Layout.setVerticalGroup(
            Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel1Layout.createSequentialGroup()
                .addComponent(jLabel63)
                .addGap(0, 62, Short.MAX_VALUE))
        );

        ActArchivoButt.setBackground(new java.awt.Color(0, 255, 153));
        ActArchivoButt.setText("Actualizar Archivo");
        ActArchivoButt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ActArchivoButtActionPerformed(evt);
            }
        });

        ActDirButt.setBackground(new java.awt.Color(153, 153, 255));
        ActDirButt.setText("Actualizar Directorio");
        ActDirButt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ActDirButtActionPerformed(evt);
            }
        });

        BorrarArchivoButt.setBackground(new java.awt.Color(204, 255, 204));
        BorrarArchivoButt.setText("Borrar Archivo");
        BorrarArchivoButt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BorrarArchivoButtActionPerformed(evt);
            }
        });

        BorrarDirButt.setBackground(new java.awt.Color(204, 153, 255));
        BorrarDirButt.setText("Borrar Directorio");
        BorrarDirButt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BorrarDirButtActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout BGPanelLayout = new javax.swing.GroupLayout(BGPanel);
        BGPanel.setLayout(BGPanelLayout);
        BGPanelLayout.setHorizontalGroup(
            BGPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BGPanelLayout.createSequentialGroup()
                .addGroup(BGPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(BGPanelLayout.createSequentialGroup()
                        .addGroup(BGPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE))
                        .addGap(81, 81, 81))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BGPanelLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(BGPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(CrearDirButt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(CrearArchivoButt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(BGPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ActArchivoButt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ActDirButt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(BGPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(BGPanelLayout.createSequentialGroup()
                                .addComponent(BorrarArchivoButt)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ModoLab)
                                .addGap(12, 12, 12))
                            .addGroup(BGPanelLayout.createSequentialGroup()
                                .addComponent(BorrarDirButt)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BGPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(ChangeModeButt)
                        .addGap(27, 27, 27)))
                .addGroup(BGPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BGPanelLayout.createSequentialGroup()
                        .addComponent(Panel51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel53, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel55, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel56, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel57, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel58, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel59, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BGPanelLayout.createSequentialGroup()
                        .addComponent(Panel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BGPanelLayout.createSequentialGroup()
                        .addComponent(Panel31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BGPanelLayout.createSequentialGroup()
                        .addComponent(Panel41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel49, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BGPanelLayout.createSequentialGroup()
                        .addComponent(Panel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Panel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BGPanelLayout.createSequentialGroup()
                        .addComponent(Panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(BGPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(BGPanelLayout.createSequentialGroup()
                                .addComponent(Panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(Panel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(Panel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(Panel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(Panel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(Panel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(Panel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(Panel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(BGPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Panel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Panel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Panel30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Panel40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Panel50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Panel60, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 23, Short.MAX_VALUE))
        );
        BGPanelLayout.setVerticalGroup(
            BGPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BGPanelLayout.createSequentialGroup()
                .addGroup(BGPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(BGPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(BGPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(CrearArchivoButt)
                            .addComponent(ModoLab)
                            .addComponent(ActArchivoButt)
                            .addComponent(BorrarArchivoButt))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ChangeModeButt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(BGPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(CrearDirButt)
                            .addComponent(ActDirButt)
                            .addComponent(BorrarDirButt)))
                    .addGroup(BGPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(BGPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(BGPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(BGPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(BGPanelLayout.createSequentialGroup()
                                        .addGroup(BGPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(Panel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Panel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Panel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Panel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Panel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Panel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Panel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Panel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(Panel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(Panel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(Panel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(Panel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(Panel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Panel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Panel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Panel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Panel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Panel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(BGPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(BGPanelLayout.createSequentialGroup()
                                .addGroup(BGPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(Panel30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Panel28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Panel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Panel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Panel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Panel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Panel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Panel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Panel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(BGPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(BGPanelLayout.createSequentialGroup()
                                        .addGroup(BGPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(Panel40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Panel39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Panel38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Panel36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Panel35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Panel34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Panel33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Panel32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Panel31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(BGPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(BGPanelLayout.createSequentialGroup()
                                                .addGroup(BGPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(Panel50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(Panel48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(Panel47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(Panel46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(Panel45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(Panel44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(Panel43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(Panel42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(Panel41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(BGPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(Panel60, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(Panel59, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(Panel58, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(Panel57, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(Panel56, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(Panel55, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(Panel54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(Panel53, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(Panel52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(Panel51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addComponent(Panel49, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(Panel37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(Panel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(BGPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(BGPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void CrearArchivoButtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CrearArchivoButtActionPerformed
        CreateArchiveUI ca = new CreateArchiveUI(this);
    }//GEN-LAST:event_CrearArchivoButtActionPerformed

    private void CrearDirButtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CrearDirButtActionPerformed
        CreateDirUI cd = new CreateDirUI(this);
    }//GEN-LAST:event_CrearDirButtActionPerformed

    private void ChangeModeButtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChangeModeButtActionPerformed
        if(this.mode=="Administrador"){
            this.setMode("Usuario");
            this.CrearDirButt.setVisible(false);
            this.CrearArchivoButt.setVisible(false);
            this.ModoLab.setText("Actual: Usuario");
        }else{
            this.setMode("Administrador");
            this.CrearDirButt.setVisible(true);
            this.CrearArchivoButt.setVisible(true);
            this.ModoLab.setText("Actual: Administrador");
        }
    }//GEN-LAST:event_ChangeModeButtActionPerformed

    private void ActArchivoButtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ActArchivoButtActionPerformed
        UpdateArchiveUI ua = new UpdateArchiveUI(this);
    }//GEN-LAST:event_ActArchivoButtActionPerformed

    private void ActDirButtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ActDirButtActionPerformed
        UpdateDirUI ud = new UpdateDirUI(this);
    }//GEN-LAST:event_ActDirButtActionPerformed

    private void BorrarArchivoButtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BorrarArchivoButtActionPerformed
        RemoveArchiveUI ra = new RemoveArchiveUI(this);
    }//GEN-LAST:event_BorrarArchivoButtActionPerformed

    private void BorrarDirButtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BorrarDirButtActionPerformed
        RemoveDirUI ed = new RemoveDirUI(this);
    }//GEN-LAST:event_BorrarDirButtActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Simulacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Simulacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Simulacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Simulacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new Simulacion().setVisible(true);
                } catch (NoSuchFieldException ex) {
                    Logger.getLogger(Simulacion.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(Simulacion.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(Simulacion.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ActArchivoButt;
    private javax.swing.JButton ActDirButt;
    private javax.swing.JPanel BGPanel;
    private javax.swing.JButton BorrarArchivoButt;
    private javax.swing.JButton BorrarDirButt;
    private javax.swing.JButton ChangeModeButt;
    private javax.swing.JButton CrearArchivoButt;
    private javax.swing.JButton CrearDirButt;
    private javax.swing.JLabel ModoLab;
    private javax.swing.JPanel Panel1;
    private javax.swing.JPanel Panel10;
    private javax.swing.JPanel Panel11;
    private javax.swing.JPanel Panel12;
    private javax.swing.JPanel Panel13;
    private javax.swing.JPanel Panel14;
    private javax.swing.JPanel Panel15;
    private javax.swing.JPanel Panel16;
    private javax.swing.JPanel Panel17;
    private javax.swing.JPanel Panel18;
    private javax.swing.JPanel Panel19;
    private javax.swing.JPanel Panel2;
    private javax.swing.JPanel Panel20;
    private javax.swing.JPanel Panel21;
    private javax.swing.JPanel Panel22;
    private javax.swing.JPanel Panel23;
    private javax.swing.JPanel Panel24;
    private javax.swing.JPanel Panel25;
    private javax.swing.JPanel Panel26;
    private javax.swing.JPanel Panel27;
    private javax.swing.JPanel Panel28;
    private javax.swing.JPanel Panel29;
    private javax.swing.JPanel Panel3;
    private javax.swing.JPanel Panel30;
    private javax.swing.JPanel Panel31;
    private javax.swing.JPanel Panel32;
    private javax.swing.JPanel Panel33;
    private javax.swing.JPanel Panel34;
    private javax.swing.JPanel Panel35;
    private javax.swing.JPanel Panel36;
    private javax.swing.JPanel Panel37;
    private javax.swing.JPanel Panel38;
    private javax.swing.JPanel Panel39;
    private javax.swing.JPanel Panel4;
    private javax.swing.JPanel Panel40;
    private javax.swing.JPanel Panel41;
    private javax.swing.JPanel Panel42;
    private javax.swing.JPanel Panel43;
    private javax.swing.JPanel Panel44;
    private javax.swing.JPanel Panel45;
    private javax.swing.JPanel Panel46;
    private javax.swing.JPanel Panel47;
    private javax.swing.JPanel Panel48;
    private javax.swing.JPanel Panel49;
    private javax.swing.JPanel Panel5;
    private javax.swing.JPanel Panel50;
    private javax.swing.JPanel Panel51;
    private javax.swing.JPanel Panel52;
    private javax.swing.JPanel Panel53;
    private javax.swing.JPanel Panel54;
    private javax.swing.JPanel Panel55;
    private javax.swing.JPanel Panel56;
    private javax.swing.JPanel Panel57;
    private javax.swing.JPanel Panel58;
    private javax.swing.JPanel Panel59;
    private javax.swing.JPanel Panel6;
    private javax.swing.JPanel Panel60;
    private javax.swing.JPanel Panel7;
    private javax.swing.JPanel Panel8;
    private javax.swing.JPanel Panel9;
    private javax.swing.JTable Table;
    private javax.swing.JTree Tree;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
