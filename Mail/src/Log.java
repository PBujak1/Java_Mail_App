import javax.mail.MessagingException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Log extends JFrame {
    private JPanel panel1;
    public JTextField textField1;
    private JButton button1; // Zaloguj
    private JButton stwórzKontoButton;
    private JButton resetHasłaButton;
    public JPasswordField passwordField1;
    private JTabbedPane tabbedPane1;
    private String currentUserEmail;

    public Log() {
        super("Logowanie");
        tabbedPane1 = new JTabbedPane();
        setContentPane(panel1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);


        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentUserEmail = textField1.getText();
                String password = new String(passwordField1.getPassword());

                if (DatabaseConnector.validateUser(currentUserEmail, password)) {
                    MailReceiver mailReceiver = new MailReceiver(currentUserEmail, password);

                    try {
                        mailReceiver.connect();
                    } catch (MessagingException ex) {
                        throw new RuntimeException(ex);
                    }

                    mailReceiver.setRecipientEmail(currentUserEmail); // << DODAJ TO

                    Thread mailThread = new Thread(mailReceiver);
                    mailThread.start();

                    OknoGlowne oknoGlowne = new OknoGlowne(currentUserEmail);

                    mailReceiver.fetchAndSaveToDatabase(currentUserEmail);

                    oknoGlowne.loadReceivedEmails();
                    oknoGlowne.loadSentEmails();
                    oknoGlowne.loadBinEmails();

                    setTitle("MAIL");
                    setContentPane(oknoGlowne.getContentPane());
                    revalidate();
                    repaint();

                    // Wylogowanie
                    oknoGlowne.getWylogujButton().addActionListener(evt -> {
                        JOptionPane.showMessageDialog(null, "Wylogowano z konta!");
                        setContentPane(panel1);
                        setTitle("Logowanie");
                        textField1.setText("");
                        passwordField1.setText("");
                        revalidate();
                        repaint();
                        mailReceiver.disconnect();
                    });

                    oknoGlowne.getOdswiezButton().addActionListener(evt -> {
                        // Połącz z serwerem mailowym i odśwież bazę

                        try {
                            oknoGlowne.odswiez(currentUserEmail, password);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        // Załaduj wiadomości z bazy do listy
                        oknoGlowne.loadReceivedEmails();
                    });

                    // Przejście do okna wysyłania wiadomości
                    oknoGlowne.getWyslijButton().addActionListener(evt -> {
                        Wyslij wyslij = new Wyslij();
                        setContentPane(wyslij.getContentPane());
                        setTitle("Wyślij wiadomość");
                        revalidate();
                        repaint();

                        // Wysyłanie wiadomości
                        wyslij.getWyslijButton().addActionListener(sendEvt -> {
                            String recipient = wyslij.textField1.getText();
                            String tytul = wyslij.TytulTekst.getText();
                            String content = wyslij.textArea1.getText();
                            String zalacznik = wyslij.zalacznik.getText();

                            try {
                                SendgridMailer.sendEmail(
                                        currentUserEmail,
                                        recipient,
                                        tytul,
                                        content,
                                        zalacznik
                                );

                                DatabaseConnector.saveSentEmail(currentUserEmail, recipient, content, tytul);

                                oknoGlowne.loadSentEmails();

                                JOptionPane.showMessageDialog(null, "Wysłano wiadomość!");
                                setContentPane(oknoGlowne.getContentPane());
                                setTitle("MAIL");
                                revalidate();
                                repaint();
                            } catch (IOException ex) {
                                JOptionPane.showMessageDialog(null, "Błąd podczas wysyłania wiadomości: " + ex.getMessage());
                            }
                        });

                        // Cofnij z wysyłania
                        wyslij.getWylogujButton().addActionListener(backEvt -> {
                            setContentPane(oknoGlowne.getContentPane());
                            setTitle("MAIL");
                            revalidate();
                            repaint();
                        });
                    });

                    // Ustawienia
                    oknoGlowne.getUstawieniaButton().addActionListener(evt -> {
                        Ustawienia ustawienia = new Ustawienia();
                        ustawienia.ustawAkcjeWyjdz((JFrame) Log.this, (JPanel) oknoGlowne.getContentPane(), "MAIL");
                        setContentPane(ustawienia.getContentPane());
                        setTitle("Ustawienia");
                        revalidate();
                        repaint();

                        ustawienia.getAkceptujButton().addActionListener(sendEvt -> {

                            String nazwisko = ustawienia.textField1.getText();
                            String dataUrodzeniaTxt = ustawienia.textField2.getText();

                            if (!nazwisko.isEmpty() || !dataUrodzeniaTxt.isEmpty()) {
                                Date dataUrodzenia = Date.valueOf(ustawienia.textField2.getText());
                                if (DatabaseConnector.resetUser(currentUserEmail, nazwisko, dataUrodzenia)) {
                                    JOptionPane.showMessageDialog(null, "Dane zmienione!");
                                } else {
                                    JOptionPane.showMessageDialog(null, "Błąd zmieniania danych!");
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Proszę uzupełnić każde pole!");
                            }

                            setContentPane(oknoGlowne.getContentPane());
                            setTitle("MAIL");
                            revalidate();
                            repaint();
                        });
                        ustawienia.getUsunButton().addActionListener(sendEvt -> {
                            if (DatabaseConnector.DeleteUser(currentUserEmail, password)) {
                                JOptionPane.showMessageDialog(null, "Usunięto konto!");
                            } else {
                                JOptionPane.showMessageDialog(null, "Błąd przy usuwaniu!");
                            }
                            setContentPane(oknoGlowne.getContentPane());
                            setTitle("MAIL");
                            revalidate();
                            repaint();
                        });
                    });

                } else {
                    JOptionPane.showMessageDialog(null, "Nieprawidłowy email lub hasło!");
                }
            }
        });

        // ------------------------------
        // TWORZENIE KONTA
        // ------------------------------
        //anonimowa klasa wewnętrzna
            stwórzKontoButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    StworzKonto stworzKonto = new StworzKonto();
                    setContentPane(stworzKonto.getContentPane());
                    setTitle("Konto");
                    revalidate();
                    repaint();

                    // Cofnij
                    stworzKonto.getWyjdzButton().addActionListener(evt -> {
                        setContentPane(panel1);
                        setTitle("Logowanie");
                        textField1.setText("");
                        passwordField1.setText("");
                        revalidate();
                        repaint();
                    });

                    // Tworzenie konta
                    stworzKonto.getStworzKontoButton().addActionListener(evt -> {
                        String email = stworzKonto.textField3.getText().trim();
                        String password = stworzKonto.textField5.getText();
                        String nazwisko = stworzKonto.textField2.getText();
                        String imie = stworzKonto.textField1.getText();
                        String dataText = stworzKonto.textField4.getText();

                        try {
                            // Walidacja daty
                            Date dataUrodzenia = Date.valueOf(dataText);

                            // Walidacja adresu e-mail
                            if (!email.endsWith("@fastdove.pl")) {
                                JOptionPane.showMessageDialog(null, "Adres email musi kończyć się na @fastdove.pl");
                                return;
                            }

                            // Sprawdzenie czy e-mail już istnieje
                            if (DatabaseConnector.emailExists(email)) {
                                JOptionPane.showMessageDialog(null, "Ten email jest już zajęty!");
                                return;
                            }

                            // Rejestracja użytkownika
                            if (DatabaseConnector.registerUser(email, password, nazwisko, imie, dataUrodzenia)) {
                                JOptionPane.showMessageDialog(null, "Konto utworzone!");

                                // Wysyłka maila do administratora
                                //Obsługa wyjątków
                                try {
                                    SendgridMailer.sendEmail(
                                            "admin@fastdove.pl",
                                            "admin@fastdove.pl",
                                            "Nowe konto do autoryzacji",
                                            "Do autoryzacji::\nEmail: " + email +
                                                    "\nImię: " + imie +
                                                    "\nNazwisko: " + nazwisko,
                                            null
                                    );
                                } catch (IOException et) {
                                    JOptionPane.showMessageDialog(null, "Nie udało się wysłać maila do administratora.");
                                    et.printStackTrace();
                                }

                                // Komunikat i powrót do panelu logowania
                                JOptionPane.showMessageDialog(null, "Proszę poczekać na autoryzację. Potrwa to do 24h.");
                                setContentPane(panel1);
                                setTitle("Logowanie");
                                textField1.setText("");
                                passwordField1.setText("");
                                revalidate();
                                repaint();

                            } else {
                                JOptionPane.showMessageDialog(null, "Błąd rejestracji!");
                            }

                        } catch (IllegalArgumentException ex) {
                            JOptionPane.showMessageDialog(null, "Nieprawidłowy format daty. Użyj formatu RRRR-MM-DD.");
                        }
                    });

                }
            });
    }
}