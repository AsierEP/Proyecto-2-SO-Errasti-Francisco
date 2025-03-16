/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyecto.pkg2.so.errasti.francisco;

import UIs.Simulacion;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dell
 */
public class Proyecto2SOErrastiFrancisco {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // TODO code application logic here
            Simulacion proyecto = new Simulacion();
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(Proyecto2SOErrastiFrancisco.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Proyecto2SOErrastiFrancisco.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Proyecto2SOErrastiFrancisco.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
