package com.example.mysqliteapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * Clase para gestionar la base de datos de usuarios.
 * Extiende SQLiteOpenHelper para manejar la creación y actualización de la base de datos.
 */
public class ManagerDBUser extends SQLiteOpenHelper {
    // 1. Declaración de constantes para el nombre y versión de la base de datos
    private static final String DATABASE_NAME = "dbUsers";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_USERS = "users";

    // 2. Consulta SQL para la creación de la tabla de usuarios
    private static final String QUERY_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    "use_document INTEGER PRIMARY KEY, " +
                    "use_names VARCHAR(150) NOT NULL, " +
                    "use_last_names VARCHAR(150) NOT NULL, " +
                    "use_user VARCHAR(100) NOT NULL UNIQUE, " +
                    "use_password VARCHAR(25) NOT NULL, " +
                    "use_status INTEGER(1) DEFAULT 1 );";  // Estado activo por defecto

    /**
     * Constructor de la clase.
     * @param context Contexto de la aplicación.
     */
    public ManagerDBUser(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase dataBase) {
        // Creación de la tabla de usuarios al inicializar la base de datos
        dataBase.execSQL(QUERY_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase dataBase, int oldVersion, int newVersion) {
        // Eliminación y recreación de la tabla si se actualiza la versión de la base de datos
        dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(dataBase);
    }

    /**
     * Verifica si un usuario ya existe en la base de datos.
     * @param documento Documento de identidad del usuario.
     * @param correo Correo electrónico del usuario.
     * @return true si el usuario existe, false en caso contrario.
     */
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

    /**
     * Inserta un nuevo usuario en la base de datos.
     * @param documento Documento del usuario.
     * @param nombres Nombres del usuario.
     * @param apellidos Apellidos del usuario.
     * @param usuario Correo del usuario.
     * @param password Contraseña del usuario.
     * @return true si el usuario fue insertado correctamente, false en caso contrario.
     */
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

    /**
     * Busca un usuario en la base de datos por documento o correo.
     * @param criterio Documento o correo a buscar.
     * @return Cursor con los datos del usuario encontrado.
     */
    public Cursor buscarUsuario(String criterio) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE use_document = ? OR use_user = ?";
        return db.rawQuery(query, new String[]{criterio, criterio});
    }

    /**
     * Actualiza los datos de un usuario en la base de datos.
     * @param documento Documento del usuario.
     * @param nombres Nuevos nombres.
     * @param apellidos Nuevos apellidos.
     * @param usuario Nuevo correo.
     * @param password Nueva contraseña.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
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

    /**
     * Elimina un usuario de la base de datos.
     * @param documento Documento del usuario a eliminar.
     * @return true si el usuario fue eliminado correctamente, false en caso contrario.
     */
    public boolean eliminarUsuario(String documento) {
        SQLiteDatabase db = this.getWritableDatabase();
        int filasEliminadas = db.delete(TABLE_USERS, "use_document = ?", new String[]{documento});
        db.close();
        return filasEliminadas > 0;
    }
}
