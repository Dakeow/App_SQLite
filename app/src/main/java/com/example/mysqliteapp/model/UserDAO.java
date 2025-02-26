package com.example.mysqliteapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;

import com.example.mysqliteapp.entities.User;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

/**
 * Clase UserDAO que maneja las operaciones de base de datos relacionadas con la entidad User.
 * Se comunica con la base de datos mediante ManagerDBUser.
 */
public class UserDAO {
    private ManagerDBUser dbUser; // Instancia del gestor de base de datos
    private Context context; // Contexto de la aplicación
    private View view; // Vista para mostrar mensajes mediante Snackbar

    /**
     * Constructor de la clase UserDAO.
     *
     * @param context Contexto de la aplicación
     * @param view Vista de la interfaz para mostrar mensajes
     */
    public UserDAO(Context context, View view) {
        this.context = context;
        this.view = view;
        this.dbUser = new ManagerDBUser(context);
    }

    /**
     * Método privado para insertar un usuario en la base de datos.
     *
     * @param user Objeto User con los datos del usuario a registrar
     */
    private void insertUser(User user) {
        SQLiteDatabase sqLiteDatabase = null;
        try {
            sqLiteDatabase = dbUser.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("use_document", user.getDocument());
            values.put("use_names", user.getFirstName());
            values.put("use_last_names", user.getLastName());
            values.put("use_user", user.getUsername());
            values.put("use_password", user.getPasswordHash()); // Contraseña ya en hash
            values.put("use_status", 1); // Estado activo por defecto

            long response = sqLiteDatabase.insert("users", null, values);

            if (response != -1) {
                Snackbar.make(this.view, "Usuario registrado con éxito", Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(this.view, "No se pudo registrar el usuario.", Snackbar.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("Error BD", "msg: " + e.getMessage());
        } finally {
            if (sqLiteDatabase != null) sqLiteDatabase.close();
        }
    }

    /**
     * Método público que permite insertar un usuario en la base de datos.
     *
     * @param user Objeto User a insertar
     */
    public void getInsertUser(User user) {
        insertUser(user);
    }

    /**
     * Método para obtener la lista de todos los usuarios activos.
     *
     * @return Lista de usuarios activos en la base de datos
     */
    public ArrayList<User> getUserList() {
        ArrayList<User> listUser = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = dbUser.getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT use_document, use_names, use_last_names, use_user FROM users WHERE use_status = 1;";
            cursor = sqLiteDatabase.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    User user = new User();
                    user.setDocument(cursor.getInt(0));
                    user.setFirstName(cursor.getString(1));
                    user.setLastName(cursor.getString(2));
                    user.setUsername(cursor.getString(3));
                    listUser.add(user);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("Error BD", e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            sqLiteDatabase.close();
        }
        return listUser;
    }
}
