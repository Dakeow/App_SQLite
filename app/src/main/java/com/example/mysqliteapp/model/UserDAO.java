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

public class UserDAO {
    private ManagerDBUser dbUser;
    private Context context;
    private View view;

    public UserDAO(Context context, View view) {
        this.context = context;
        this.view = view;
        this.dbUser = new ManagerDBUser(context);
    }

    // Método privado para insertar un usuario
    private void insertUser(User user) {
        SQLiteDatabase sqLiteDatabase = null;
        try {
            sqLiteDatabase = dbUser.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("use_document", user.getDocument());
            values.put("use_names", user.getFirstName());
            values.put("use_last_names", user.getLastName());
            values.put("use_user", user.getUsername());
            values.put("use_password", user.getPasswordHash()); // Guardamos la contraseña ya en hash
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

    // Método público para insertar un usuario
    public void getInsertUser(User user) {
        insertUser(user);
    }

    // Método para obtener todos los usuarios activos
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
