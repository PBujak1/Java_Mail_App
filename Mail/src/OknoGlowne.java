import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//dziedziczenie
public class OknoGlowne extends JFrame implements Odswiezalny {

    //Prywatne dane których inne klasy nie mogą zmienić typu nazwy guzików

    private JPanel panel1;
    private JButton odebraneButton;
    private JTabbedPane tabbedPane1;
    private JButton wyslijWiadomośćButton;
    private JButton ustawieniaKontaButton;
    private JButton wylogujButton;
    private JPanel Odebrane;
    private JPanel Wyslane;
    private JPanel Usuniete;
    private JButton xButton;
    private JButton nuttonButton;
    private JLabel NazwaMaila;
    private JButton zamknijButton;
    private JLabel NazwaMaila2;
    private JLabel NazwaMaila3;
    private JButton wysłaneButton;
    private JButton koszButton;
    private JButton usunButton;


    //Publiczne dane aby inne klasy mogły np dodawać maile do listy

    public JList list1;
    public JList list2;
    public JList list3;
    public List<String> lista1 = new ArrayList<>();
    public List<String> lista2 = new ArrayList<>();
    public List<String> lista3 = new ArrayList<>();
    public List<String> lista4 = new ArrayList<>();
    public List<String> lista5 = new ArrayList<>();
    public List<String> lista6 = new ArrayList<>(); // <--- Nowa lista na adresy e-mail

    public JPanel wyslanePanel;
    public JTextArea WyslanaWiadomosc;
    public JTextArea OdebranaWiadomosc;
    private JTextArea UsunietaWiadomosc;
    private JLabel temat;
    private JLabel tutuł2;
    private JLabel tytul3;
    public JButton odświeżButton;
    public Log log;
    private String currentUserEmail;

    public OknoGlowne(String email) {
        this.currentUserEmail = email;

        //Zaplanowanie jak ma wyglądać panel okna głównego

        super("Mail");
        setContentPane(panel1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        //Wyłączenie podglądania paneli tak aby się włączały dopiero jak zostaną wciśnięte myszką

        Odebrane.setVisible(false);
        Wyslane.setVisible(false);
        Usuniete.setVisible(false);


        //Funkcje ktore wlaczaja panel wiadomosci po wybraniu pozycji z listy

        list1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1 && list1.getSelectedIndex() != -1) {
                    String mail = getMailOdebrany(list1.getSelectedIndex());
                    String tytul = lista4.get(list1.getSelectedIndex());
                    String wiadomosc = getWiadomoscOdebrana(list1.getSelectedIndex());

                    if (mail != null && Odebrane != null) {

                        //Ustawienie odpowiednich maili oraz tekstu

                        NazwaMaila.setText(mail);
                        temat.setText(tytul);

                        OdebranaWiadomosc.setLineWrap(true);
                        OdebranaWiadomosc.setWrapStyleWord(true);

                        if (OdebranaWiadomosc.getText().length() > 10) {
                            OdebranaWiadomosc.append("\n");
                        }

                        OdebranaWiadomosc.setText(wiadomosc);

                        Odebrane.setVisible(true);
                        nuttonButton.setVisible(true);

                        //Dodanie przycisku zamykania

                        nuttonButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                Odebrane.setVisible(false);
                                nuttonButton.setVisible(false);
                            }
                        });

                        //Dodanie przycisku usuwania wiadomości (przenoszenie do kosza)

                        usunButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (list1.getSelectedIndex() >= 0) {
                                    JOptionPane.showMessageDialog(null, "Usunięto Wiadomosc");

                                    //pobranie modeli list aby z jednej usunąć wiadomość a druga to kosz do której dodajemy wiadomość

                                    DefaultListModel model1 = (DefaultListModel) list1.getModel();
                                    DefaultListModel model2 = (DefaultListModel) list3.getModel();

                                    //przekazanie wiadomości do kosza

                                    String wiadomosc = getWiadomoscOdebrana(list1.getSelectedIndex());
                                    String sender = getMailOdebrany(list1.getSelectedIndex());
                                    String title = lista4.get(list1.getSelectedIndex());


                                    // Zapisz wiadomość do bazy w tabeli bin
                                    DatabaseConnector.moveToBin(sender, currentUserEmail, title, wiadomosc);
                                    // Usuń z received_emails
                                    DatabaseConnector.deleteFromReceived(sender, currentUserEmail, title, wiadomosc);

                                    loadBinEmails();  // <-- ponowne załadowanie danych z kosza

                                    model2.addElement(mail);
                                    lista3.add(wiadomosc);

                                    UsunietaWiadomosc.setLineWrap(true);
                                    UsunietaWiadomosc.setWrapStyleWord(true);

                                    if (UsunietaWiadomosc.getText().length() > 10) {
                                        UsunietaWiadomosc.append("\n");
                                    }

                                    OdebranaWiadomosc.setText(wiadomosc);

                                    //usunięcie wiadomości z odebranych

                                    model1.remove(list1.getSelectedIndex());

                                    //wyłączenie panelu

                                    Odebrane.setVisible(false);
                                    nuttonButton.setVisible(false);
                                }
                            }
                        });
                    }
                }
            }
        });

        list2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1 && list2.getSelectedIndex() != -1) {
                    int index = list2.getSelectedIndex();
                    String mail = getMailWyslany(index);
                    String tytul = lista5.get(index);
                    String wiadomosc = getWiadomoscWyslana(index);

                    if (mail != null) {
                        NazwaMaila2.setText(mail);
                        tutuł2.setText(tytul);

                        //Kiedy tekst osiągnie szerokość text Area(WyslanaWiadomosc) to tekst przechodzi do kolejnej linijki

                        WyslanaWiadomosc.setLineWrap(true);
                        WyslanaWiadomosc.setWrapStyleWord(true);

                        if (WyslanaWiadomosc.getText().length() > 10) {
                            WyslanaWiadomosc.append("\n");
                        }

                        WyslanaWiadomosc.setText(wiadomosc);
                        Wyslane.setVisible(true);
                        xButton.setVisible(true);

                        xButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                Wyslane.setVisible(false);
                                xButton.setVisible(false);
                            }
                        });
                    }
                }
            }
        });

        list3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1 && list3.getSelectedIndex() != -1) {
                    String mail = getMailUsuniety(list3.getSelectedIndex());
                    String tytul = lista6.get(list3.getSelectedIndex());
                    if (mail != null) {
                        NazwaMaila3.setText(mail);
                        tytul3.setText(tytul);
                        Usuniete.setVisible(true);
                        zamknijButton.setVisible(true);

                        zamknijButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                Usuniete.setVisible(false);
                                zamknijButton.setVisible(false);
                            }
                        });
                    }
                }
            }
        });
    }

    public void loadReceivedEmails() {
        String query = "SELECT sender_email,title, message FROM received_emails WHERE recipient_email = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, currentUserEmail); // <-- email zalogowanego użytkownika
            ResultSet rs = stmt.executeQuery();

            DefaultListModel<String> model = new DefaultListModel<>();
            lista1.clear(); // np. ArrayList przechowująca treści wiadomości
            lista4.clear(); //przechowije tytuły

            while (rs.next()) {
                String sender = rs.getString("sender_email");
                String title = rs.getString("title");
                String message = rs.getString("message");

                model.addElement(sender); // pokazuje nadawcę na liście
                lista1.add(message);      // zapisuje treść do listy
                lista4.add(title);
            }

            list1.setModel(model); // przypisanie listy nadawców do komponentu JList
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void loadBinEmails() {
        String query = "SELECT sender_email, title, message FROM bin WHERE recipient_email = ?";

        lista3.clear(); // treści
        lista6.clear(); // e-maile
        DefaultListModel<String> model = new DefaultListModel<>();

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, currentUserEmail);


            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String sender = rs.getString("sender_email");
                String title = rs.getString("title");
                String message = rs.getString("message");

                model.addElement(title);        // pokazuj tytuł w JList
                lista3.add(message);            // zapisz treść
                lista6.add(sender);             // zapisz nadawcę
            }

            list3.setModel(model); // przypisz model do JList z kosza
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    //Funckja ktora nam zwraca guziki zebysmy mogli w prosty sposob po jego uzyciu wrocic do panelu logowania

    public AbstractButton getWylogujButton() {
        return wylogujButton;
    }

    public AbstractButton getWyslijButton() {
        return wyslijWiadomośćButton;
    }

    public AbstractButton getUstawieniaButton() { return ustawieniaKontaButton; }

    public AbstractButton getOdswiezButton() { return odświeżButton; }
    //Funkcje do sprawdzania i zwracania kto lub do kogo jest widamosc i jej przechwytywanie, ustalenie na jakiej pozycji jest dany mail

    public String getMailOdebrany(int index) {
        if (index >= 0 && index < list1.getModel().getSize()) {
            return list1.getModel().getElementAt(index).toString();
        }
        return null;
    }

    public String getMailWyslany(int index) {
        if (index >= 0 && index < list2.getModel().getSize()) {
            return list2.getModel().getElementAt(index).toString();
        }
        return null;
    }

    public String getMailUsuniety(int index) {
        if (index >= 0 && index < list3.getModel().getSize()) {
            return list3.getModel().getElementAt(index).toString();
        }
        return null;
    }

    public String getWiadomoscOdebrana(int index) {
        if (index >= 0 && index < lista1.size()) {
            return lista1.get(index); // już nie trzeba .toString()
        }
        return null;
    }


    public String getWiadomoscWyslana(int index) {
        if (index >= 0 && index < lista2.size()) {
            return lista2.get(index).toString();
        }
        return null;
    }

    public String getWiadomoscUsunieta(int index) {
        if (index >= 0 && index < lista3.size()) {
            return lista3.get(index).toString();
        }
        return null;
    }
    public void loadSentEmails() {
        String query = "SELECT recipient_email, title, message FROM sent_emails WHERE sender_email = ?";

        lista2.clear();
        lista5.clear();

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, currentUserEmail);
            ResultSet rs = stmt.executeQuery();

            DefaultListModel<String> model = new DefaultListModel<>();

            while (rs.next()) {
                String recipient = rs.getString("recipient_email");
                String title = rs.getString("title");
                String message = rs.getString("message");

                model.addElement(recipient);
                lista2.add(message);
                lista5.add(title);
            }

            list2.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //polimorfizm
    @Override
    public void odswiez(String cuurent, String password) {
        // Połącz z serwerem mailowym i odśwież bazę
        MailReceiver mailReceiver = new MailReceiver(cuurent, password);

        try {
            mailReceiver.connect();
            mailReceiver.fetchAndSaveToDatabase(cuurent); // <-- zapis do bazy
            mailReceiver.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Załaduj wiadomości z bazy do listy
        loadReceivedEmails();
    }
}
