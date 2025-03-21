/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package UIs;

import EDD.List;
import EDD.Node;
import Objects.Archivo;

/**
 *
 * @author Dell
 */
public class UpdateArchiveUI extends javax.swing.JFrame {

    private Simulacion sim;

    /**
     * Creates new form UpdateArchiveUI
     */
    public UpdateArchiveUI(Simulacion s) {
        initComponents();
        this.sim=s;
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.actualizarArchivos();
        this.sim.disable();
        
        NameTF.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            NameTFMouseClicked(evt);
        }
    });
    }

    
    private UpdateArchiveUI() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    private void actualizarArchivos(){
        List direc = this.sim.getArchivos();
        Node cabeza = direc.getPfirst();
        while(cabeza!=null){
            Archivo ac = (Archivo) cabeza.getDato();
            this.UpdateCB.addItem(ac.getNombre()+" ("+ac.getDirectorio()+")");
            cabeza=cabeza.getPnext();
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

        jPanel1 = new javax.swing.JPanel();
        TitleLab = new javax.swing.JLabel();
        Lab1 = new javax.swing.JLabel();
        UpdateCB = new javax.swing.JComboBox<>();
        Lab2 = new javax.swing.JLabel();
        NameTF = new javax.swing.JTextField();
        BackButt = new javax.swing.JButton();
        UpdateButt = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(153, 255, 153));

        TitleLab.setFont(new java.awt.Font("Castellar", 1, 18)); // NOI18N
        TitleLab.setText("Actualizar Archivo");

        Lab1.setText("Actualizar archivo:");

        Lab2.setText("Nuevo nombre:");

        BackButt.setBackground(new java.awt.Color(255, 51, 51));
        BackButt.setText("Volver");
        BackButt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BackButtActionPerformed(evt);
            }
        });

        UpdateButt.setBackground(new java.awt.Color(102, 255, 102));
        UpdateButt.setText("Actualizar archivo");
        UpdateButt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UpdateButtActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(TitleLab)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(Lab2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(NameTF))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(Lab1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(UpdateCB, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(145, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(BackButt)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(UpdateButt)
                .addGap(90, 90, 90))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(TitleLab)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Lab1)
                    .addComponent(UpdateCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Lab2)
                    .addComponent(NameTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BackButt)
                    .addComponent(UpdateButt))
                .addGap(32, 32, 32))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BackButtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BackButtActionPerformed
        this.sim.enable();
        this.setVisible(false);
        this.NameTF.setText("");
    }//GEN-LAST:event_BackButtActionPerformed

    private void UpdateButtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpdateButtActionPerformed
        String[] partes = this.UpdateCB.getSelectedItem().toString().split("\\(");
        String nombreArchivo = partes[0].trim();
        String nombreDirectorio = partes[1].substring(0, partes[1].length() - 1).trim(); 
        Archivo archivo = this.sim.getArchivos().encontrarArchivo(nombreArchivo, nombreDirectorio);
        this.sim.editarArchivo(archivo,this.NameTF.getText());
        this.UpdateCB.removeAllItems();
        this.actualizarArchivos();
        this.NameTF.setText("");
    }//GEN-LAST:event_UpdateButtActionPerformed

    
    private void NameTFMouseClicked(java.awt.event.MouseEvent evt) {                                          
    NameTF.setText("");
    NameTF.setForeground(new java.awt.Color(0, 0, 0));
    }
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
            java.util.logging.Logger.getLogger(UpdateArchiveUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UpdateArchiveUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UpdateArchiveUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UpdateArchiveUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UpdateArchiveUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BackButt;
    private javax.swing.JLabel Lab1;
    private javax.swing.JLabel Lab2;
    private javax.swing.JTextField NameTF;
    private javax.swing.JLabel TitleLab;
    private javax.swing.JButton UpdateButt;
    private javax.swing.JComboBox<String> UpdateCB;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
