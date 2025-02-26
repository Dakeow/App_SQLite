package com.example.mysqliteapp;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mysqliteapp.model.ManagerDBUser;

public class MainActivity extends AppCompatActivity {

    // Declaración de los elementos de la interfaz
    private EditText etDocumento, etUsuario, etNombres, etApellidos, etContraseña;
    private ListView listUsers;
    private Button btnGuardar, btnListUsers, btnBuscar, btnActualizar, btnLimpiar;

    // Instancia del gestor de base de datos
    private ManagerDBUser dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Vinculación de los elementos con su ID en el layout
        etDocumento = findViewById(R.id.etDocumento);
        etUsuario = findViewById(R.id.etUsuario);
        etNombres = findViewById(R.id.etNombres);
        etApellidos = findViewById(R.id.etApellidos);
        etContraseña = findViewById(R.id.etContraseña);
        listUsers = findViewById(R.id.lvLista);
        btnGuardar = findViewById(R.id.btnRegister);
        btnListUsers = findViewById(R.id.btnListar);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnLimpiar = findViewById(R.id.btnLimpiar);

        dbHelper = new ManagerDBUser(this);

        // Configuración de los eventos de los botones
        btnGuardar.setOnClickListener(v -> registrarUsuario());
        btnBuscar.setOnClickListener(v -> buscarUsuario());
        btnActualizar.setOnClickListener(v -> actualizarUsuario());
        btnLimpiar.setOnClickListener(v -> limpiarCampos());
    }

    /**
     * Valida los campos de entrada según expresiones regulares
     * para asegurar datos correctos antes de enviarlos a la base de datos.
     * @return true si los datos son válidos, false en caso contrario.
     */
    private boolean validarCampos() {
        String documento = etDocumento.getText().toString().trim();
        String usuario = etUsuario.getText().toString().trim();
        String nombres = etNombres.getText().toString().trim();
        String apellidos = etApellidos.getText().toString().trim();
        String password = etContraseña.getText().toString().trim();

        // Expresiones regulares para validar los campos
        String regexDocumento = "^[0-9]{8,10}$"; // Documento de 8 a 10 dígitos numéricos
        String regexNombres = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$"; // Solo letras y espacios
        String regexCorreo = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"; // Formato de email
        String regexPassword = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$"; // Mínimo 6 caracteres, al menos 1 letra y 1 número

        if (!documento.matches(regexDocumento)) {
            etDocumento.setError("Documento inválido (8-10 dígitos numéricos)");
            return false;
        }
        if (!nombres.matches(regexNombres)) {
            etNombres.setError("Solo se permiten letras y espacios");
            return false;
        }
        if (!apellidos.matches(regexNombres)) {
            etApellidos.setError("Solo se permiten letras y espacios");
            return false;
        }
        if (!usuario.matches(regexCorreo)) {
            etUsuario.setError("Correo electrónico inválido");
            return false;
        }
        if (!password.matches(regexPassword)) {
            etContraseña.setError("Contraseña débil (Mín. 6 caracteres, al menos 1 letra y 1 número)");
            return false;
        }
        return true;
    }

    /**
     * Registra un usuario en la base de datos si los datos son válidos.
     */
    private void registrarUsuario() {
        if (!validarCampos()) return;

        String documento = etDocumento.getText().toString();
        String usuario = etUsuario.getText().toString();

        // Verifica si el usuario ya existe en la base de datos
        if (dbHelper.usuarioExiste(documento, usuario)) {
            Toast.makeText(this, "El usuario o documento ya están registrados", Toast.LENGTH_SHORT).show();
            return;
        }

        // Inserta el usuario en la base de datos
        boolean insertado = dbHelper.insertarUsuario(
                documento,
                etNombres.getText().toString(),
                etApellidos.getText().toString(),
                usuario,
                etContraseña.getText().toString()
        );

        if (insertado) {
            Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
            limpiarCampos();
        } else {
            Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Busca un usuario en la base de datos y muestra sus datos en los campos de texto.
     */
    private void buscarUsuario() {
        String criterio = etDocumento.getText().toString().trim();
        if (criterio.isEmpty()) {
            etDocumento.setError("Ingrese un documento o usuario");
            return;
        }

        Cursor cursor = dbHelper.buscarUsuario(criterio);

        if (cursor != null && cursor.moveToFirst()) {
            etDocumento.setText(cursor.getString(0));
            etNombres.setText(cursor.getString(1));
            etApellidos.setText(cursor.getString(2));
            etUsuario.setText(cursor.getString(3));
            etContraseña.setText(cursor.getString(4));
            cursor.close();
        } else {
            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Actualiza los datos de un usuario en la base de datos.
     */
    private void actualizarUsuario() {
        if (!validarCampos()) return;

        boolean actualizado = dbHelper.actualizarUsuario(
                etDocumento.getText().toString(),
                etNombres.getText().toString(),
                etApellidos.getText().toString(),
                etUsuario.getText().toString(),
                etContraseña.getText().toString()
        );

        if (actualizado) {
            Toast.makeText(this, "Usuario actualizado con éxito", Toast.LENGTH_SHORT).show();
            limpiarCampos();
        } else {
            Toast.makeText(this, "Error al actualizar usuario", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Limpia los campos de entrada en la interfaz.
     */
    private void limpiarCampos() {
        etDocumento.setText("");
        etUsuario.setText("");
        etNombres.setText("");
        etApellidos.setText("");
        etContraseña.setText("");
    }
}