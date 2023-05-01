import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import password.generator.PasswordGenerator;
import property.validation.PropertyValidator;
import property.validation.PropertyValidator.Configuration;

public class App {

    private final Configuration configuration;

    public App() {
        PropertyValidator propertyValidator = new PropertyValidator(new File("config.properties"));
        configuration = propertyValidator.validate();

        String characters = getCharacters();
        PrintStream printStream = getPrintStream();

        for (int i = 0; i < Integer.parseInt(configuration.getNUMBER_OF_PASSWORDS()); i++) {
            printStream.println(
                    PasswordGenerator.generatePassword(Integer.parseInt(configuration.getPASSWORD_LENGTH()),
                            characters));
        }
    }

    public static void main(String[] args) {
        new App();
    }

    public void out(final PrintStream printStream, final String output) {
        printStream.println(output);
    }

    private PrintStream getPrintStream() {
        PrintStream printStream;
        if (!configuration.getOUT_FILE().isEmpty()) {
            try {
                printStream = new PrintStream(new File(configuration.getOUT_FILE()));
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Failed to find output file.");
            }
        } else {
            printStream = new PrintStream(System.out);
        }
        return printStream;
    }

    public String getCharacters() {
        String characters = new String();
        if (configuration.getLOWER_CASE().equalsIgnoreCase("true")) {
            characters += PasswordGenerator.LOWER_CASE;
        }
        if (configuration.getUPPER_CASE().equalsIgnoreCase("true")) {
            characters += PasswordGenerator.UPPER_CASE;
        }
        if (configuration.getDIGITS().equalsIgnoreCase("true")) {
            characters += PasswordGenerator.DIGITS;
        }
        if (configuration.getSYMBOLS().equalsIgnoreCase("true")) {
            characters += PasswordGenerator.SYMBOLS;
        }
        if (characters.isEmpty()) {
            characters += PasswordGenerator.ALL_CHARACTERS;
        }
        return characters;
    }

}
