package contacts;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    static ArrayList<Contact> contactsArrayList = new ArrayList<>();
    static File file;
    static String fileName;
    static Scanner scanner = new Scanner(System.in);

    static void saveToFile() {
        if (file == null) {
            return;
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(contactsArrayList);
            objectOutputStream.close();
        } catch (Exception e) {
            System.out.println("can't save to file phonebook.db: " + e.getMessage());
        }
    }

    static void readFromFile() {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            contactsArrayList = (ArrayList) objectInputStream.readObject();
        } catch (FileNotFoundException e) {
//            System.out.println("can't read from file phonebook.db: "+e.getMessage());
            file = new File(fileName);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void printInfo(ArrayList<Contact> contactsArrayList) {
        for (int i = 0; i < contactsArrayList.size(); i++) {
            System.out.println(i + 1 + ". " + contactsArrayList.get(i).getFieldValue("name") +
                    " " + contactsArrayList.get(i).getFieldValue("surname"));
        }
        System.out.println();
    }

    public static void main(String[] args) {

        String answer;
        int contactsIndex;

//        contactsArrayList.add(new ContactHuman("Alex", "Smith", "667", "M", "1983-12-01"));
//        file = new File("phonebook.db");
//        saveToFile();
//        fileName = "phonebook.db";
//        file = new File(fileName);//"phonebook.db");
//        readFromFile();

        if (args.length > 0) {
            System.out.println("open " + args[0].trim());//phonebook.db");
            fileName = args[0].trim();
            file = new File(fileName);//"phonebook.db");
            readFromFile();
        }


        while (true) {
            System.out.print("[menu] Enter action (add, list, search, count, exit): ");
            answer = scanner.nextLine();
            switch (answer) {
                case "count"://////////////////////////////////

                    System.out.println("The Phone Book has " + contactsArrayList.size() + " records.");
                    break;
                case "exit"://////////////////////////////////

                    saveToFile();
                    return;
                case "list"://////////////////////////////////

                    list();
                    break;

                case "add"://////////////////////////////////
                    //                abstract class ContactStaticFactory {
                    //
                    //                    public static Contact newInstance(String type) {
                    //                        if (type.trim().toLowerCase().equals("human")) {
                    //                            return new ContactHuman();
                    //                        } else if (type.equals("organization")) {
                    //                            return new ContactOrganization();
                    //                        }
                    //                        return null; // if not a suitable type
                    //                    }
                    //                }
                    add();
                    break;

                case "search"://////////////////////////////////

                    do {
                        ArrayList<Contact> contacts = search();
                        System.out.println("Found " + contacts.size() + " results:");
                        printInfo(contacts);
                        System.out.println();

                        System.out.print("[search] Enter action ([number], back, again): ");
                        answer = scanner.nextLine();
                    } while (answer.trim().toLowerCase().equals("again"));

                    if (answer.trim().toLowerCase().matches("[1-9]\\d*")) {
                        Contact contact = contactsArrayList.get(Integer.parseInt(answer.trim().toLowerCase()) - 1);
                        System.out.println(contact.toPrint());
                        record(contact);
                    }
                    break;


                case "edit"://////////////////////////////////

                    if (contactsArrayList.size() == 0) {
                        System.out.println("No records to edit!");
                        System.out.println();
                        continue;
                    }
                    printInfo(contactsArrayList);

                    System.out.print("Select a record: ");
                    contactsIndex = scanner.nextInt();
                    contactsIndex--;
                    scanner.nextLine();

                    ArrayList<String> fieldsArray = contactsArrayList.get(contactsIndex).getFieldsChangeable();

                    editRecord(contactsArrayList.get(contactsIndex));
                    break;

            }
            System.out.println();
        }
    }

    private static void add() {
        Contact contact = null;
        String answer;
        System.out.print("Enter the type (person, organization):");
        answer = scanner.nextLine().trim().toLowerCase();
        if (answer.equals("person")) {
            System.out.print("Enter the name: ");
            String name = scanner.nextLine();
            System.out.print("Enter the surname: ");
            String surname = scanner.nextLine();
            System.out.print("Enter the birth date: ");
            String birth = scanner.nextLine();
            System.out.print("Enter the gender (M, F): ");
            String gender = scanner.nextLine();
            System.out.print("Enter the number: ");
            String phone = scanner.nextLine();
            contact = new ContactHuman(name, surname, phone, gender, birth);
            contactsArrayList.add(contact);
        } else if (answer.equals("organization")) {
            System.out.print("Enter the organization name: ");
            String name = scanner.nextLine();
            System.out.print("Enter the address: ");
            String address = scanner.nextLine();
            System.out.print("Enter the number: ");
            String phone = scanner.nextLine();
            contact = new ContactOrganization(phone, name, address);
            contactsArrayList.add(new ContactOrganization(phone, name, address));
        }
        if (contact != null) {
            System.out.println("The record added.");
            saveToFile();
        }
    }

    private static ArrayList<Contact> search() {

        System.out.print("Enter search query: ");
        String answer = scanner.nextLine();
        Pattern pattern = Pattern.compile(answer, Pattern.CASE_INSENSITIVE);
        Matcher matcher;
        ArrayList<Contact> resultSearch = new ArrayList<>();
        for (Contact contact : contactsArrayList) {
            matcher = pattern.matcher(contact.toString());
            if (matcher.find()) {
                resultSearch.add(contact);
            }
        }
        return resultSearch;
    }

    private static void remove(Contact contact) {

        contactsArrayList.remove(contactsArrayList.lastIndexOf(contact));//contactsIndex-1);
        System.out.println("The record removed!");
        saveToFile();
    }

    private static void editRecord(Contact contact) {

        String answer;
        if (contact instanceof ContactHuman) {
            System.out.print("Select a field (name, surname, birth, gender, number): ");
        } else if (contact instanceof ContactOrganization) {
            System.out.print("Select a field (name, address, number): ");
        }
        answer = scanner.nextLine().toLowerCase().trim();
        if ("number".equals(answer)) {
            answer = "phone";
        }
        if (contact.getFieldsChangeable().contains(answer)) {
            System.out.print("Enter " + answer + ": ");
            contact.setValueToField(answer, scanner.nextLine().trim());
        }

        System.out.println("Saved");
        System.out.println(contact.toPrint());
        saveToFile();
    }

    private static void list() {

        int answerInt;
        String answer;
        printInfo(contactsArrayList);
        Pattern pattern = Pattern.compile("back", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher("");
        do {
            System.out.print("[list] Enter action ([number], back): ");//Select a record: ");
            answer = scanner.nextLine();
            if (answer.toLowerCase().trim().matches("[1-9]\\d*")) {
                answerInt = Integer.parseInt(answer.toLowerCase().trim());
                if (answerInt > contactsArrayList.size()) {
                    continue;
                } else {
                    System.out.println(contactsArrayList.get(answerInt - 1).toPrint());
                    System.out.println();
                    record(contactsArrayList.get(answerInt - 1));
                    return;
                }

            }

            matcher = pattern.matcher(answer);
        } while (!matcher.matches());

    }

    private static void record(Contact contact) {
        System.out.print("[record] Enter action (edit, delete, menu): ");
        switch (scanner.nextLine()) {
            case "edit":
                editRecord(contact);
                break;
            case "delete":
                remove(contact);
                break;
            default:
            case "menu":
                break;
        }
    }

}

abstract class Contact extends GetterSetter implements Serializable {
    private String name;
    protected LocalDateTime createDateTime;
    protected LocalDateTime editDateTime;

    private String phone;

    Contact(String name, String phone) {
        this.name = name;
        this.createDateTime = LocalDateTime.now().withNano(0);
        this.editDateTime = LocalDateTime.now().withNano(0);
        if (checkValidityNumber(phone)) {
            this.phone = phone;
        } else {
            System.out.println("Wrong number format!");
            this.phone = "";
        }
    }

    public void setName(String name) {
        this.name = name;
        this.editDateTime = LocalDateTime.now().withNano(0);
    }

    public String getName() {
        return name;
    }

    public String getDateTime() {
//        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("HH:mm:ss");
        return createDateTime.toString();//.format(formatter1)+"T"+createDateTime.format(formatter2);
    }

    public String getEditDateTime() {
//        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("HH:mm:ss");
        return editDateTime.toString();//.format(formatter1)+"T"+editDateTime.format(formatter2);
    }

    public void setPhone(String phone) {

        if (checkValidityNumber(phone)) {
            this.phone = phone;
        } else {
            System.out.println("Wrong number format!");
            this.phone = "";
        }
        this.editDateTime = LocalDateTime.now().withNano(0);
    }

    public String getPhone() {
        if (hasNumberPhone()) {
            return phone;
        } else {
            return "[no number]";
        }
    }

    public boolean hasNumberPhone() {

        return !this.phone.isEmpty();
    }


    private boolean checkValidityNumber(String number) {

        String numberPattern = "((?i)([+]|)[(][0-9a-z]{1,}[)]((\\s+|[-]|)([0-9a-z]{2,}){1}|)((\\s+|[-]|)[0-9a-z]{2,})*" +  //+(0) 123-456 789 ABcd
                "|(?i)([+]|\\b)[0-9a-z]{1,}((\\s|[-]|)([(][0-9a-z]{2,}[)]){1}|)((\\s|[-]|)[0-9a-z]{2,})*" + //+0 (123) 456 789-ABcd
                "|(?i)([+]|\\b)[0-9a-z]{1,}((\\s|[-]|)([0-9a-z]{2,}){1}|)((\\s|[-]|)[0-9a-z]{2,})*)";      //+0 123-456 789 ABcd or +0123456789ABcd

        Matcher matcher = Pattern.compile(numberPattern).matcher(number);

        return matcher.matches();
    }

    public String toPrint() {

        return String.format("Number: %s\nTime created: %s\nTime last edit: %s", this.getPhone(), this.getDateTime(), this.getEditDateTime());
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", this.getPhone(), this.getDateTime(), this.getEditDateTime());
    }
}

class ContactHuman extends Contact {

    private String surname;
    private LocalDate birth;
    private String gender;

    public ContactHuman(String name, String surname, String phone, String gender, String birth) {
        super(name, phone);
        this.surname = surname;

        if (checkGenderFormat(gender)) {
            this.gender = gender;
        } else {
            System.out.println("Bad gender!");
            this.gender = "";
        }

        if (birth.equals("")) {///////???????
            System.out.println("Bad birth date!");
        } else {
            this.birth = convertStringToLocalDate(birth);////??????
        }
    }

    public void setSurname(String surname) {
        this.surname = surname;
        this.editDateTime = LocalDateTime.now().withNano(0);
    }

    public String getSurname() {
        return surname;
    }

    public void setGender(String gender) {
        if (checkGenderFormat(gender)) {
            this.gender = gender;
        } else {
            System.out.println("Bad gender!");
            this.gender = "";
        }
        this.editDateTime = LocalDateTime.now().withNano(0);
    }

    public String getGender() {
        if (hasGender()) {
            return gender;
        } else {
            return "[no data]";
        }
    }

    public void setBirth(String birth) {
        if (birth.equals("")) {///////???????
            System.out.println("Bad birth date!");
        } else {
            this.birth = convertStringToLocalDate(birth);//////?????????
        }
        this.editDateTime = LocalDateTime.now().withNano(0);
    }

    public String getBirth() {
        if (hasBirth()) {
            return birth.toString();
        } else {
            return "[no data]";
        }
    }

    private boolean checkGenderFormat(String gender) {
        return gender.matches("[M,m,F,f]");
    }

    public boolean hasGender() {
        return !this.gender.isEmpty();
    }

    public boolean hasBirth() {
        return this.birth != null;
    }

    private LocalDate convertStringToLocalDate(String birth) {
//        if(birth.matches("(\\d{4}[-, ,\.]\\d{1,2}[-, ,\\.]\\d{1,2}|\\d{1,2}[-, ,\.]\\d{1,2}[-, ,\.]\\d{4})"){
//            ///...............need add parsing: parse(CharSequence birth, DateTimeFormatter formatter)
//        }
        return LocalDate.parse(birth);
    }

    @Override
    public String toPrint() {

        return String.format("Name: %s\nSurname: %s\nBirth date: %s\nGender: %s\n%s",
                this.getName(), this.getSurname(), this.getBirth(), this.getGender(), super.toPrint());
    }

    @Override
    public String toString() {
        return String.format(" %s %s %s %s %s", this.getName(), this.getSurname(), this.getBirth(), this.getGender(), super.toString());
    }
}

class ContactOrganization extends Contact {
    private String address;

    ContactOrganization(String phone, String name, String address) {
        super(name, phone);
        this.address = address;
    }

    public void setAddress(String address) {
        this.address = address;
        this.editDateTime = LocalDateTime.now().withNano(0);
    }

    public String getAddress() {
        return address.equals("") ? "[no data]" : address;
    }

    @Override
    public String toPrint() {

        return String.format("Organization name: %s\nAddress: %s\n%s",
                this.getName(), this.getAddress(), super.toPrint());
    }

    @Override
    public String toString() {
        return String.format(" %s %s %s", this.getName(), this.getAddress(), super.toString());
    }
}

abstract class GetterSetter {

    public ArrayList<String> getFieldsChangeable() {

        //GET Super Class fields
        ArrayList<String> fieldsArray = getStringsFieldsArrayList(this.getClass().getSuperclass());
        //GET Class fields
        fieldsArray.addAll(getStringsFieldsArrayList(this.getClass()));//, new Class[] {Object.class}));

        return fieldsArray;//or comment all and return new String [] {"name", "surname", ......};
    }

    private ArrayList<String> getStringsFieldsArrayList(Class classIn) {
        ArrayList<String> fieldsArray = new ArrayList<>();
        Field[] fields = classIn.getDeclaredFields();
        Method[] methods = classIn.getMethods();
        String searchingMethodName;
        for (Field field : fields) {

            searchingMethodName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
            for (Method method : methods) {
                if (method.getName().equals(searchingMethodName)) {
                    fieldsArray.add(field.getName());
                }
            }
        }
        //OR just return String[]{"","",....};
        return fieldsArray;
    }

    public boolean setValueToField(String field, Object value) {

        Method method;

        try {
            method = this.getClass().getMethod("set" + field.substring(0, 1).toUpperCase() + field.substring(1), value.getClass());
            method.invoke(this, value);
        } catch (Exception e) {
            System.out.println("setValueToField()=" + e.getMessage());
            return false;
        }
        return true;
    }

    public Object getFieldValue(String field) {

        Method method;
        try {
            method = this.getClass().getMethod("get" + field.substring(0, 1).toUpperCase() + field.substring(1));
            return method.invoke(this);
        } catch (Exception e) {
//            System.out.println("catch="+e.getMessage());
            return "";
        }
    }

}