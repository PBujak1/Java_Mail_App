import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class RaportZapis {

    public static void zapiszRaport(Raport raport, String nazwaPliku) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nazwaPliku))) {
            oos.writeObject(raport);
            System.out.println("Raport zapisano do pliku: " + nazwaPliku);
        } catch (IOException e) {
            System.err.println("Błąd zapisu raportu: " + e.getMessage());
        }
    }
}
