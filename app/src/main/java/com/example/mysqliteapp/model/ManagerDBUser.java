package com.example.mysqliteapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ManagerDBUser extends SQLiteOpenHelper {
    // 1. Declaración de constantes
    private static final String DATABASE_NAME = "dbUsers";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_USERS = "users";

    private static final String QUERY_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    "use_document INTEGER PRIMARY KEY, " +
                    "use_names VARCHAR(150) NOT NULL, " +
                    "use_last_names VARCHAR(150) NOT NULL, " +
                    "use_user VARCHAR(100) NOT NULL UNIQUE, " +
                    "use_password VARCHAR(25) NOT NULL, " +
                    "use_status INTEGER(1) DEFAULT 1 );";  // Estado activo por defecto

    // 2. Constructor
    public ManagerDBUser(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase dataBase) {
        dataBase.execSQL(QUERY_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase dataBase, int oldVersion, int newVersion) {
        dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(dataBase);
    }

    // 3. Método para verificar si un usuario ya existe (por documento o correo)
    public boolean usuarioExiste(String documento, String correo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean existe = false;

        try {
            String query = "SELECT use_document FROM " + TABLE_USERS + " WHERE use_document = ? OR use_user = ?";
            cursor = db.rawQuery(query, new String[]{documento, correo});
            existe = cursor.getCount() > 0;
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return existe;
    }

    // 4. Método para insertar un nuevo usuario
    public boolean insertarUsuario(String documento, String nombres, String apellidos, String usuario, String password) {
        if (usuarioExiste(documento, usuario)) return false;  // Evita duplicados

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("use_document", documento);
        valores.put("use_names", nombres);
        valores.put("use_last_names", apellidos);
        valores.put("use_user", usuario);
        valores.put("use_password", password);
        valores.put("use_status", 1);  // Activo por defecto

        long resultado = db.insert(TABLE_USERS, null, valores);
        db.close();
        return resultado != -1;
    }

    // 5. Método para buscar un usuario por documento o correo
    public Cursor buscarUsuario(String criterio) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE use_document = ? OR use_user = ?";
        return db.rawQuery(query, new String[]{criterio, criterio});
    }

    // 6. Método para actualizar un usuario
    public boolean actualizarUsuario(String documento, String nombres, String apellidos, String usuario, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("use_names", nombres);
        valores.put("use_last_names", apellidos);
        valores.put("use_user", usuario);
        valores.put("use_password", password);

        int filasAfectadas = db.update(TABLE_USERS, valores, "use_document = ?", new String[]{documento});
        db.close();
        return filasAfectadas > 0;
    }

    // 7. Método para eliminar un usuario
    public boolean eliminarUsuario(String documento) {
        SQLiteDatabase db = this.getWritableDatabase();
        int filasEliminadas = db.delete(TABLE_USERS, "use_document = ?", new String[]{documento});
        db.close();
        return filasEliminadas > 0;
    }
}
