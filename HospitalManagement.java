package org.project;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

class Patient {
    private int patient_id;
    private String name;
    private int age;
    private String gender;

    // Constructor
    public Patient(int patient_id, String name, int age, String gender) {
        this.patient_id=patient_id;
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    // Getters
    public int getPatient_id() { return patient_id;}
    public String getName() {
        return name;
    }
    public int getAge() {
        return age;
    }
    public String getGender() {
        return gender;
    }

    // toString method
    public String toString() {
        return "Patient Id: "+patient_id+ ", Name: " + name + ", Age: " + age + ", Gender: " + gender;
    }
}

class Hospital {
    private Patient[] patients;
    private int numPatients;

    // Constructor
    public Hospital(int maxNumPatients) {
        patients = new Patient[maxNumPatients];
        numPatients = 0;
    }

    // Add a patient to the hospital
    public void addPatient(Patient patient, Connection conn) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO patients (patient_id, name, age, gender) VALUES (?, ?, ?,?)");
        pstmt.setInt(1,patient.getPatient_id());
        pstmt.setString(2, patient.getName());
        pstmt.setInt(3, patient.getAge());
        pstmt.setString(4, patient.getGender());

        pstmt.executeUpdate();

        patients[numPatients++] = patient;

        System.out.println("Patient added successfully!");
    }

    // Display all patients in the hospital
    public void displayPatients(Connection conn) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM patients");
        ResultSet rs = pstmt.executeQuery();

        System.out.println("List of Patients:");
        while (rs.next()) {
            int patient_id=rs.getInt("patient_id");
            String name = rs.getString("name");
            int age = rs.getInt("age");
            String gender = rs.getString("gender");

            Patient patient = new Patient(patient_id,name, age, gender);
            patients[numPatients++] = patient;

            System.out.println(patient);
        }
    }
}

public class HospitalManagement {
    public static void main(String[] args) throws ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        Hospital hospital = new Hospital(10); // Maximum 10 patients in the hospital

        String url = "jdbc:mysql://localhost:3306/hospital";
        String user = "root";
        String password = "1234";

        try {

            Connection conn = DriverManager.getConnection(url, user, password);

            while (true) {
                System.out.println("Hospital Management System");
                System.out.println("1. Add Patient");
                System.out.println("2. Display Patients");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline character after reading integer input

                switch (choice) {
                    case 1:
                        System.out.println("Enter patient id: ");
                        int patient_id= scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Enter patient name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter patient age: ");
                        int age = scanner.nextInt();
                        scanner.nextLine(); // Consume newline character after reading integer input
                        System.out.print("Enter patient gender: ");
                        String gender = scanner.nextLine();
                        Patient patient = new Patient(patient_id,name, age, gender);
                        hospital.addPatient(patient, conn);
                        break;
                    case 2:
                        hospital.displayPatients(conn);
                        break;
                    case 3:
                        System.out.println("Thank you for using the Hospital Management System!");
                        scanner.close();
                        conn.close();
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
