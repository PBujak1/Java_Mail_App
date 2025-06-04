import javax.swing.*;

public class Ustawienia extends OknoBazowe {
    private JPanel panel1;
    public JTextField textField1;
    public JTextField textField2;
    private JButton button1;
    private JButton wyjdźButton;
    private JButton akceptujButton;

    public Ustawienia() {
        super("Ustawienia");
        setContentPane(panel1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
    }

    @Override
    public void ustawAkcjeWyjdz(JFrame rodzicFrame, JPanel panel1, String tytulOkna) {
        wyjdźButton.addActionListener(e -> {
            rodzicFrame.setContentPane(panel1);
            rodzicFrame.setTitle(tytulOkna);
            rodzicFrame.revalidate();
            rodzicFrame.repaint();
        });
    }

    @Override
    public AbstractButton getUsunButton() {
        return button1;
    }

    @Override
    public AbstractButton getAkceptujButton() {
        return akceptujButton;
    }
}