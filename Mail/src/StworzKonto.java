import javax.swing.*;
import java.io.IOException;
import java.sql.Date;

public class StworzKonto extends JFrame {
    private JPanel panelS;
    private JLabel stwórzKontoLabel;
    JTextField textField1;
    JTextField textField2;
    JTextField textField3;
    JTextField textField4;
    JTextField textField5;
    private JButton stwórzKontoButton;
    private JButton wyjdźButton;
    private JLabel DataU;


    //dodanie innych klas tak aby można było brać z nich konkretne dane typu nazwa jakiegos maila

    private OknoGlowne oknoGlowne;
    private Log log;

    public StworzKonto() {
        //Ustawienie jak ma wyglądać panel
        super("StworzKonto");
        setContentPane(panelS);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);


    }
    public void ustawAkcjeWyjdz(JFrame rodzicFrame, JPanel panelLogowania, String tytulOkna) {
        wyjdźButton.addActionListener(e -> {
            rodzicFrame.setContentPane(panelLogowania);
            rodzicFrame.setTitle(tytulOkna);
            rodzicFrame.revalidate();
            rodzicFrame.repaint();
        });
    }

    public AbstractButton getStworzKontoButton() {
        return stwórzKontoButton;
    }
    public AbstractButton getWyjdzButton() {
        return wyjdźButton;
    }

}