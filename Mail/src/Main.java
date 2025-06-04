import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Log login = new Log();
        System.out.println("Hello, World!");
        SendgridMailer.sendEmail(
                "admin@fastdove.pl",
                "admin@fastdove.pl",
                "Temat testowy",
                "Treść testowej wiadomości",
                "C:\\Users\\bober\\OneDrive\\Pulpit\\Java-zadanie\\Mail1\\Mail\\raport.ser" );// lub null
        login.setVisible(true);
    }
} 