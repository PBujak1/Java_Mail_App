import javax.swing.*;

public abstract class OknoBazowe extends JFrame {
    protected JPanel panel1;

    public OknoBazowe(String tytul) {
        super(tytul);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
    }

    public abstract void ustawAkcjeWyjdz(JFrame rodzicFrame, JPanel panel1, String tytulOkna);

    public abstract AbstractButton getUsunButton();

    public abstract AbstractButton getAkceptujButton();
}
