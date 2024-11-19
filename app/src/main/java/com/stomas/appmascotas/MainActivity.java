package com.stomas.appmascotas;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //DECLARACION DE VARIABLES
    private EditText txtCodigo, txtNombre, txtDueño, txtDireccion;
    private ListView lista;
    private Spinner spMascota;

    //VARIABLE CONEXION FIRESTORE
    private Firebasestore db;
    //DATOS SPINNER
    String[] TiposMascotas = {"Perro", "Gato", "Pájaro"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //Llamamos el metodo que carga la Lista
        CargarListaFirestore();
        //INICIO FireStore
        db= FirebaseFirestore.getInstance();

        txtCodigo = findViewById(R.id.txtCodigo);
        txtNombre = findViewById(R.id.txtNombre);
        txtDueño = findViewById(R.id.txtDueño);
        txtDireccion = findViewById(R.id.txtDireccion);
        spMascota = findViewById(R.id.spMascota);
        lista = findViewById(R.id.lista);

        //Poblar Spinner Tipos de Mascota
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, TiposMascotas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMascota.setAdapter(adapter);

        //MÉTODO ENVIAR DATOS
        public void enviarDatosFirestore(View view){
            String codigo = txtCodigo.getText().toString();
            String nombre = txtNombre.getText().toString();
            String dueño = txtDueño.getText().toString();
            String direccion = txtDireccion.getText().toString();
            String tipoMascota = spMascota.getSelectedItem().toString();

            //MAPA con los datos a enviar
            Map<String, Object> mascota = new HashMap<>();
            mascota.put("codigo", codigo);
            mascota.put("nombre", nombre);
            mascota.put("dueño", dueño);
            mascota.put("direccion", direccion);
            mascota.put("tipoMascota", tipoMascota);

            //Envio de datos a Firestore
            db.collection("mascotas")
                    .document(codigo)
                    .set(mascota)
                    .addOnSuccessListener(aVoid ->{
                        Toast.makeText(MainActivity.this, "Datos enviados a Firestore correctamente", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MainActivity.this, "Error al enviar datos a Firestore" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

        public void CargarListaFirestore(){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("mascotas")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        List<String> listaMascotas = new ArrayList<>();
                        for(QueryDocumentSnapshot document : task.getResult()){
                            String linea = "||" + document.getString("codigo") + "||" +
                                    document.getString("nombre") + "||" +
                                    document.getString("dueño") + "||" +
                                    document.getString("direccion");
                            listaMascotas.add(linea);
                        }

                        ArrayAdapter<String> adaptador = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, listaMascotas);
                        lista.setAdapter(adaptador);
                    } else{
                        Log.e("TAG", "Error al obtener datos de Firestore", task.getException());
                    }
                }
            });
        }
    }
}