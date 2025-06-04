import javax.swing.*;

public class Wyslij extends JFrame {

        //dodanie innych klas tak aby można było brać z nich konkretne dane typu nazwa jakiegos maila

    private OknoGlowne oknoGlowne;
    private Log log;

        //Prywatne dane używane tylko i wyłącznie w klasie Wyślij

    private JPanel panel1;
    private JButton button1;
    private JButton wyslijButton;
    private JLabel tytulLabel;

        //Publiczne dane takie aby inne klasy mogły wykorzystywać lub przechwytywać tekst który wprowadzimy np aby go dodać do listy wysłanych

    JTextField textField1;
    JTextArea textArea1;
    JTextField TytulTekst;
    JTextField zalacznik;


    public Wyslij() {

            //Ustawienie jak ma wyglądać panel wysyłania

        super("Wyślij wiadomość");
        setContentPane(panel1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        textArea1.setLineWrap(true);
        textArea1.setWrapStyleWord(true);

        if (textArea1.getText().length() > 10) {
            textArea1.append("\n");
        }

    }


    public AbstractButton getWylogujButton() {
        return button1;
    }

    public AbstractButton getWyslijButton() {
        return wyslijButton;
    }

}
