package com.example.mysqliteapp.entities;

import java.util.Objects;

public class User {
    // 1. Atributos
    private int document;
    private String firstName;
    private String lastName;
    private String username;
    private String passwordHash; // Contraseña almacenada en hash

    // 2. Constructores
    public User() {}

    public User(int document, String firstName, String lastName, String username, String password) {
        this.document = document;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.passwordHash = hashPassword(password); // Guardamos la contraseña cifrada
    }

    // 3. Métodos de acceso (Getters y Setters)
    public int getDocument() {
        return document;
    }

    public void setDocument(int document) {
        this.document = document;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPassword(String password) {
        this.passwordHash = hashPassword(password); // Hash al modificar la contraseña
    }

    // 4. Método para cifrar la contraseña (simulación)
    private String hashPassword(String password) {
        return Integer.toHexString(password.hashCode()); // No es seguro en producción, solo demostrativo
    }

    // 5. toString mejorado sin exponer la contraseña
    @Override
    public String toString() {
        return "User{" +
                "document=" + document +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    // 6. Métodos equals y hashCode para comparaciones seguras
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return document == user.document && username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(document, username);
    }
}
