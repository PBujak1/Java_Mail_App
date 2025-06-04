import java.io.Serializable;
import java.util.Date;

public class Raport implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nadawca;
    private String odbiorca;
    private String temat;
    private String tresc;
    private String zalacznik;
    private Date data;

    public Raport(String nadawca, String odbiorca, String temat, String tresc, String zalacznik) {
        this.nadawca = nadawca;
        this.odbiorca = odbiorca;
        this.temat = temat;
        this.tresc = tresc;
        this.zalacznik = (zalacznik == null || zalacznik.isEmpty()) ? "Brak" : zalacznik;
        this.data = new Date();
    }

    @Override
    public String toString() {
        return "Raport wysłania maila:\n" +
                "Nadawca: " + nadawca + "\n" +
                "Odbiorca: " + odbiorca + "\n" +
                "Temat: " + temat + "\n" +
                "Treść: " + tresc + "\n" +
                "Załącznik: " + zalacznik + "\n" +
                "Data: " + data + "\n";
    }
}
