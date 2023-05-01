package property.validation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import annotations.Condition;
import annotations.Property;
import property.validation.enums.ConditionType;

public class PropertyValidator {

    private final Properties properties;

    public PropertyValidator(File configurationFile) {
        properties = new Properties();
        try (InputStream input = new FileInputStream(configurationFile)) {
            properties.load(input);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(MessageFormat.format("Failed to find the configuariotn file at {0}",
                    configurationFile.getAbsolutePath(), e));
        } catch (IOException e) {
            throw new RuntimeException(MessageFormat.format("Failed to load the configuariotn file at {0}",
                    configurationFile.getAbsolutePath(), e));
        }
    }

    public Configuration validate() {
        Configuration configuration = new Configuration();
        for (Field field : configuration.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Property.class)) {
                Property property = field.getAnnotation(Property.class);

                if (!properties.containsKey(field.getName())) {
                    if (property.required()) {
                        throw new RuntimeException(MessageFormat.format("Expected property {0}", field.getName()));
                    } else {
                        continue;
                    }
                }
                String value = checkField(field);
                field.setAccessible(true);
                try {
                    field.set(configuration, value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return configuration;
    }

    private String checkField(final Field field) {
        Property property = field.getAnnotation(Property.class);
        String value = properties.getProperty(field.getName());
        List<String> errors = new ArrayList<>();
        for (Condition condition : property.conditions()) {
            errors.addAll(checkCondition(condition, field, value));
        }

        if (!errors.isEmpty()) {
            throw new RuntimeException(MessageFormat.format("Property {0} is invalid.", field.getName()));
        }
        return value;
    }

    private List<String> checkCondition(final Condition condition, final Field field, final String value) {
        List<String> errors = new ArrayList<>();
        switch (condition.type()) {
            case NOT_NULL:
                if (value.isEmpty()) {
                    errors.add(
                            MessageFormat.format("Expected property {0} not to be null.", field.getName()));
                }
                break;
            case NOT_BLANK:
                if (value.isBlank()) {
                    errors.add(MessageFormat
                            .format("Expected property {0} not to be blank.", field.getName()));
                }
                break;
            case INT:
                try {
                    Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    errors.add(MessageFormat
                            .format("Expected property {0} to be an integer.", field.getName(), e));
                }
                break;
            case BOOLEAN:
                if (!("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value))) {
                    errors.add(MessageFormat
                            .format("Expected property {0} to be a boolean.", field.getName()));
                }
                break;
            case DIRECTORY:
                Path directoryPath = Paths.get(value);
                if (!(Files.exists(directoryPath) && Files.isDirectory(directoryPath))) {
                    errors.add(MessageFormat
                            .format("Expected property {0} to be a directory.", field.getName()));
                }
                break;
            case FILE:
                Path filePath = Paths.get(value);
                if (!(Files.exists(filePath) && Files.isRegularFile(filePath))) {
                    errors.add(MessageFormat
                            .format("Expected property {0} to be a file.", field.getName()));
                }
                break;
        }
        return errors;
    }

    public static class Configuration {
        @Property(required = true, conditions = {
                @Condition(type = ConditionType.INT)
        })
        private String PASSWORD_LENGTH;

        @Property(required = true, conditions = {
                @Condition(type = ConditionType.INT)
        })
        private String NUMBER_OF_PASSWORDS;

        @Property(required = true, conditions = {
                @Condition(type = ConditionType.DIRECTORY),
                @Condition(type = ConditionType.NOT_BLANK)
        })
        private String OUT_DIRECTORY;

        @Property(required = false, conditions = {
                @Condition(type = ConditionType.NOT_BLANK)
        })
        private String OUT_FILE;

        @Property(required = false, conditions = {
                @Condition(type = ConditionType.BOOLEAN)
        })
        private String doExport;

        @Property(required = false, conditions = {
                @Condition(type = ConditionType.BOOLEAN)
        })
        private String LOWER_CASE;

        @Property(required = false, conditions = {
                @Condition(type = ConditionType.BOOLEAN)
        })
        private String UPPER_CASE;

        @Property(required = false, conditions = {
                @Condition(type = ConditionType.BOOLEAN)
        })
        private String DIGITS;

        @Property(required = false, conditions = {
                @Condition(type = ConditionType.BOOLEAN)
        })
        private String SYMBOLS;

        /**
         * @return String return the PASSWORD_LENGTH
         */
        public String getPASSWORD_LENGTH() {
            return PASSWORD_LENGTH;
        }

        /**
         * @return String return the NUMBER_OF_PASSWORDS
         */
        public String getNUMBER_OF_PASSWORDS() {
            return NUMBER_OF_PASSWORDS;
        }

        /**
         * @return String return the OUT_DIRECTORY
         */
        public String getOUT_DIRECTORY() {
            return OUT_DIRECTORY;
        }

        /**
         * @return String return the OUT_FILE
         */
        public String getOUT_FILE() {
            return OUT_FILE;
        }

        /**
         * @return String return the doExport
         */
        public String getDoExport() {
            return doExport;
        }

        /**
         * @return String return the LOWER_CASE
         */
        public String getLOWER_CASE() {
            return LOWER_CASE;
        }

        /**
         * @return String return the UPPER_CASE
         */
        public String getUPPER_CASE() {
            return UPPER_CASE;
        }

        /**
         * @return String return the DIGITS
         */
        public String getDIGITS() {
            return DIGITS;
        }

        /**
         * @return String return the SYMBOLS
         */
        public String getSYMBOLS() {
            return SYMBOLS;
        }

        @Override
        public String toString() {
            return "MyConfig{" +
                    "PASSWORD_LENGTH='" + PASSWORD_LENGTH + '\'' +
                    ", NUMBER_OF_PASSWORDS='" + NUMBER_OF_PASSWORDS + '\'' +
                    ", OUT_DIRECTORY='" + OUT_DIRECTORY + '\'' +
                    ", OUT_FILE='" + OUT_FILE + '\'' +
                    ", doExport='" + doExport + '\'' +
                    ", LOWER_CASE='" + LOWER_CASE + '\'' +
                    ", UPPER_CASE='" + UPPER_CASE + '\'' +
                    ", DIGITS='" + DIGITS + '\'' +
                    ", SYMBOLS='" + SYMBOLS + '\'' +
                    '}';
        }

    }

}