import java.io.*;
import java.util.Scanner;
import java.io.File;

public class Main {

    public static void main(String[] args) {
        deleteFilesInDirectory("csv");
        deleteFilesInDirectory("binary");

        do_part1();
        do_part2();
        do_part3();
    }


    private static void deleteFilesInDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    file.delete();
                }
            }
        }
    }

    /*--------------------------------Part1--------------------------------- */
    public static void do_part1() {
        try (BufferedReader inputFileNamesReader = new BufferedReader(new FileReader("Part1_input_file_names.txt"))) {

            int numFiles = Integer.parseInt(inputFileNamesReader.readLine());


            for (int i = 0; i < numFiles; i++) {
                String fileName = inputFileNamesReader.readLine();
                processFile(fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processFile(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = reader.readLine()) != null) {
                try {

                    validateRecord(line);


                    String genre = getGenre(line);



                    writeToGenreFile(genre, line);

                } catch (SyntaxErrorException e) {

                    writeSyntaxError(fileName, e.getMessage(), line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void validateRecord(String record) throws SyntaxErrorException {
        String[] fields = parseCSVRecord(record);

        if (fields.length != 6) {
            throw new TooFewFieldsException("Too few fields");
        } else if (fields.length > 6) {
            throw new TooManyFieldsException("Too many fields");
        }


        for (String field : fields) {
            if (field.isEmpty()) {
                throw new MissingFieldException("Missing field");
            }
        }

        String genre = fields[4].trim().replaceAll("^\"|\"$", "");
        if (!isValidGenre(genre)) {
            throw new UnknownGenreException("Unknown genre");
        }
    }

    private static String[] parseCSVRecord(String record) {
        return record.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
    }


    private static boolean isValidGenre(String genre) {
        String[] validGenres = {"CCB", "HCB", "MTV", "MRB", "NEB", "OTR", "SSM", "TPA"};
        for (String validGenre : validGenres) {
            if (validGenre.equals(genre)) {
                return true;
            }
        }
        return false;
    }

    private static String getGenre(String line) {
        String genre = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)")[4].trim().replaceAll("^\"|\"$", "");
        String file = switch (genre) {
            case "CCB" -> "Cartoons_Comics_Books.csv";
            case "HCB" -> "Hobbies_Collectibles_Books.csv";
            case "MTV" -> "Movies_TV.csv";
            case "MRB" -> "Music_Radio_Books.csv";
            case "NEB" -> "Nostalgia_Eclectic_Books.csv";
            case "OTR" -> "Old_Time_Radio.csv";
            case "SSM" -> "Sports_Sports_Memorabilia.csv";
            case "TPA" -> "Trains_Planes_Automobiles.csv";
            default -> "";
        };
        return file;
    }

    private static void writeToGenreFile(String genre, String line) throws IOException {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./csv/" + genre, true))) {
            writer.write(line);
            writer.newLine();
        }
    }

    private static void writeSyntaxError(String fileName, String errorMessage, String line) {
        try (BufferedWriter errorWriter = new BufferedWriter(new FileWriter("syntax_error_file.txt", true))) {
            errorWriter.write("syntax error in file: " + fileName);
            errorWriter.newLine();
            errorWriter.write("====================");
            errorWriter.newLine();
            errorWriter.write("Error: " + errorMessage);
            errorWriter.newLine();
            errorWriter.write("Record: " + line);
            errorWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class TooManyFieldsException extends SyntaxErrorException {
        TooManyFieldsException(String message) {
            super(message);
        }
    }

    static class TooFewFieldsException extends SyntaxErrorException {
        TooFewFieldsException(String message) {
            super(message);
        }
    }

    static class MissingFieldException extends SyntaxErrorException {
        MissingFieldException(String message) {
            super(message);
        }
    }

    static class UnknownGenreException extends SyntaxErrorException {
        UnknownGenreException(String message) {
            super(message);
        }
    }

    static class SyntaxErrorException extends Exception {
        SyntaxErrorException(String message) {
            super(message);
        }
    }

    /*--------------------------------Part2--------------------------------- */

    public static void do_part2() {
        File csvDirectory = new File("./csv");
        if (csvDirectory.exists()) {
            File[] csvFiles = csvDirectory.listFiles();
            if (csvFiles != null) {
                for (File file : csvFiles) {
                    processFilePart2(file.getName());
                }
            }
        }
    }

    private static void processFilePart2(String fileName) {
        File file = new File("./csv/" + fileName);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;


                String[][] validRecords = new String[1000][6];
                int recordCount = 0;


                while ((line = reader.readLine()) != null) {
                    String[] fields = parseCSVRecord(line);


                    try {
                        validateRecord(fields);
                        validRecords[recordCount++] = fields;
                    } catch (SemanticErrorException e) {
                        writeSemanticError(fileName, e.getMessage(), line);
                    }
                }


                Book[] books = createBookArrayFromValidRecords(validRecords, recordCount);


                serializeBooks(books, fileName);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("File not found: " + fileName);
        }
    }

    private static Book[] createBookArrayFromValidRecords(String[][] validRecords, int recordCount) {
        Book[] books = new Book[recordCount];
        int bookIndex = 0;

        for (int i = 0; i < recordCount; i++) {
            String[] fields = validRecords[i];
            String title = fields[0].trim().replaceAll("^\"|\"$", "");
            String authors = fields[1].trim().replaceAll("^\"|\"$", "");
            double price = Double.parseDouble(fields[2].trim().replaceAll("^\"|\"$", ""));
            String isbn = fields[3].trim().replaceAll("^\"|\"$", "");
            String genre = fields[4].trim().replaceAll("^\"|\"$", "");
            int year = Integer.parseInt(fields[5].trim().replaceAll("^\"|\"$", ""));

            books[bookIndex++] = new Book(title, authors, price, isbn, genre, year);
        }
        return books;
    }

    private static void validateRecord(String[] fields) throws SemanticErrorException {
        double price;
        try {
            price = Double.parseDouble(fields[2].trim().replaceAll("^\"|\"$", ""));
            if (!isValidPrice(price)) {
                throw new BadPriceException("Invalid price");
            }
        } catch (NumberFormatException e) {
            throw new BadPriceException("Invalid price");
        }
        String isbn = fields[3].trim().replaceAll("^\"|\"$", "");
        int year;
        try {
            year = Integer.parseInt(fields[5].trim().replaceAll("^\"|\"$", ""));
            if (!isValidYear(year)) {
                throw new BadYearException("Invalid year");
            }
        } catch (NumberFormatException e) {
            throw new BadYearException("Invalid year");
        }

        if (!isValidIsbn(isbn)) {
            if (isbn.length() == 10) {
                throw new BadIsbn10Exception("Invalid ISBN-10 format");
            } else if (isbn.length() == 13) {
                throw new BadIsbn13Exception("Invalid ISBN-13 format");
            } else {
                throw new SemanticErrorException("Invalid ISBN format");
            }
        }
    }

    private static boolean isValidPrice(double price) {
        return price >= 0;
    }


    private static boolean isValidYear(int year) {
        return year >= 1995 && year <= 2024;
    }


    private static boolean isValidIsbn(String isbn) {
        if (isbn.length() == 10) {
            int sum = 0;
            for (int i = 0; i < 10; i++) {
                int digit = Character.getNumericValue(isbn.charAt(i));
                sum += (10 - i) * digit;
            }
            return sum % 11 == 0;
        } else if (isbn.length() == 13) {
            int sum = 0;
            for (int i = 0; i < 13; i++) {
                int digit = Character.getNumericValue(isbn.charAt(i));
                sum += (i % 2 == 0) ? digit : 3 * digit;
            }
            return sum % 10 == 0;
        }
        return false;
    }

    private static void serializeBooks(Book[] books, String fileName) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("./binary/" + fileName + ".ser"))) {

            outputStream.writeObject(books);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeSemanticError(String fileName, String errorMessage, String line) {
        try (BufferedWriter errorWriter = new BufferedWriter(new FileWriter("semantic_error_file.txt", true))) {
            errorWriter.write("semantic error in file: " + fileName);
            errorWriter.newLine();
            errorWriter.write("====================");
            errorWriter.newLine();
            errorWriter.write("Error: " + errorMessage);
            errorWriter.newLine();
            errorWriter.write("Record: " + line);
            errorWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class BadIsbn10Exception extends SemanticErrorException {
        BadIsbn10Exception(String message) {
            super(message);
        }
    }

    static class BadIsbn13Exception extends SemanticErrorException {
        BadIsbn13Exception(String message) {
            super(message);
        }
    }

    static class BadPriceException extends SemanticErrorException {
        BadPriceException(String message) {
            super(message);
        }
    }

    static class BadYearException extends SemanticErrorException {
        BadYearException(String message) {
            super(message);
        }
    }

    static class SemanticErrorException extends Exception {
        SemanticErrorException(String message) {
            super(message);
        }
    }


    /*--------------------------------Part3--------------------------------- */
    public static void do_part3() {
        File binaryDirectory = new File("./binary");
        if (binaryDirectory.exists()) {
            File[] binaryFiles = binaryDirectory.listFiles((dir, name) -> name.endsWith(".ser"));
            if (binaryFiles != null && binaryFiles.length > 0) {
                Scanner scanner = new Scanner(System.in);
                int choice;
                int selectedFile= 0;
                do {
                    System.out.println("Main Menu:");
                    System.out.println("1. View File: " + binaryFiles[selectedFile].getName());
                    System.out.println("2. List Files and Records");
                    System.out.println("3. Exit");
                    System.out.print("Enter your choice: ");
                    choice = scanner.nextInt();

                    switch (choice) {
                        case 1:
                        viewFile(binaryFiles[selectedFile]);
                            break;
                        case 2:
                            System.out.println("Select a file to view:");
                            listFilesAndRecords(binaryFiles);
                            System.out.print("Enter file number: ");
                            int fileNumber = scanner.nextInt();
                            if (fileNumber > 0 && fileNumber <= binaryFiles.length) {
                                selectedFile=fileNumber - 1;
                            } else {
                                System.out.println("Invalid file number.");
                            }
                            break;
                        case 3:
                            System.out.println("Exiting...");
                            break;
                        default:
                            System.out.println("Invalid choice. Please enter again.");
                            break;
                    }
                } while (choice != 3);
            } else {
                System.out.println("No binary files found.");
            }
        } else {
            System.out.println("Binary directory does not exist.");
        }
    }

    private static void viewFile(File file) {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
            Book[] books = (Book[]) inputStream.readObject();
            Scanner scanner = new Scanner(System.in);
            int currentRecordIndex = 0;
            System.out.println("Viewing file: " + file.getName());

            while (true) {
                System.out.println("Current Record:");
                if (books.length != 0 && currentRecordIndex >= 0 && currentRecordIndex < books.length) {
                    System.out.println(books[currentRecordIndex].toString());
                }
                System.out.println("Enter a number to navigate records (+n or -n), or 0 to exit viewing: ");
                int navigate = scanner.nextInt();
                if (navigate == 0) {
                    break;
                } else if (navigate > 0) {
                    int endIndex = currentRecordIndex + navigate;
                    if (endIndex >= books.length) {
                        endIndex = books.length - 1;
                        System.out.println("EOF has been reached.");
                    }
                    for (int i = currentRecordIndex + 1; i <= endIndex; i++) {
                        System.out.println(books[i].toString());
                    }
                    currentRecordIndex = endIndex;
                } else if (navigate < 0) {
                    int startIndex = currentRecordIndex + navigate;
                    if (startIndex < 0) {
                        startIndex = 0;
                        System.out.println("BOF has been reached.");
                    }
                    for (int i = currentRecordIndex - 1; i >= startIndex; i--) {
                        System.out.println(books[i].toString());
                    }
                    currentRecordIndex = startIndex;
                } else {
                    System.out.println("Invalid navigation.");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    private static void listFilesAndRecords(File[] binaryFiles) {
        for (int i = 0; i < binaryFiles.length; i++) {
            File file = binaryFiles[i];
            try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
                Book[] books = (Book[]) inputStream.readObject();
                System.out.println("File: " + file.getName() + ", Records: " + books.length);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}

