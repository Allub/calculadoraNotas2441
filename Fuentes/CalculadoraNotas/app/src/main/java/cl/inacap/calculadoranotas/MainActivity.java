package cl.inacap.calculadoranotas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cl.inacap.calculadoranotas.dto.Nota;

public class MainActivity extends AppCompatActivity {

    private int porcentajeActual=0;
    private EditText notaTxt;
    private EditText porcentajeTxt;
    private Button agregarBtn;
    private Button limpiarBtn;
    private ListView notasLv;
    private List<Nota> notas = new ArrayList<>();
    private ArrayAdapter<Nota> adapter;
    private TextView promedioTxt;
    private LinearLayout promedioLl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.promedioTxt = findViewById(R.id.promedioTxt);
        this.promedioLl = findViewById(R.id.promedioLl);
        this.notaTxt = findViewById(R.id.notaTxt);
        this.porcentajeTxt = findViewById(R.id.porcentajeTxt);
        this.agregarBtn = findViewById(R.id.agregarBtn);
        this.limpiarBtn = findViewById(R.id.limpiarBtn);
        this.notasLv = findViewById(R.id.notasLv);
        this.adapter = new ArrayAdapter<>(this,android.R.layout. simple_list_item_1,notas);
        this.notasLv.setAdapter(adapter);
        this.limpiarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //limpiar los EditText
                notaTxt.setText("");
                porcentajeTxt.setText("");
                //Ocultar el LinearLayout de resultado
                promedioLl.setVisibility(View.INVISIBLE);
                //volver el porcentaje acumulado a 0
                porcentajeActual = 0;
                //limpiar la lista
                notas.clear();
                adapter.notifyDataSetChanged();
            }
        });


        this.agregarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> errores = new ArrayList<>();
                String notaStr = notaTxt.getText().toString().trim();
                String porcStr = porcentajeTxt.getText().toString().trim();
                int porcentaje = 0;
                double nota = 0;
                try {
                    nota = Double.parseDouble(notaStr);
                    if (nota < 1.0 || nota > 7.0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    errores.add("-La nota debe ser un numero entre 1.0 y 7.0");
                }

                try {
                    porcentaje = Integer.parseInt(porcStr);
                    if (porcentaje < 1 || porcentaje > 100) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    errores.add("-El procentaje debe ser un numero entre 1 y 100");
                }
                if (errores.isEmpty()) {
                    if(porcentaje + porcentajeActual > 100){
                        //se excede el valor permitido
                        Toast.makeText(MainActivity.this
                                , "El porcentaje no puede ser mayor que 100"
                                , Toast.LENGTH_SHORT).show();
                    }else{
                        Nota n = new Nota();
                        n.setValor(nota);
                        n.setPorcentaje(porcentaje);
                        porcentajeActual += porcentaje;
                        notas.add(n);
                        //cada vez que modifique el recurso
                        //asociado al adaptador debo llamar a este metodo
                        adapter.notifyDataSetChanged();
                        mostrarPromedio();
                    }

                } else {
                    mostrarErrores(errores);
                }
            }
        });
    }

    private void mostrarPromedio(){
        double promedio=0;
        for(Nota n:notas){
            promedio+= (n.getValor()*n.getPorcentaje()/100);
        }
        this.promedioTxt.setText(String.format("%.1f",promedio));
        if(promedio < 4.0){
            //el texto salga en rojo
            this.promedioTxt.setTextColor(ContextCompat.getColor(this, R.color.colorError));
        }else{
            //el texto salga en azul
            this.promedioTxt.setTextColor(ContextCompat.getColor(this,R.color.colorExitoso));
        }
        this.promedioLl.setVisibility(View.VISIBLE);

    }

    private void mostrarErrores(List<String> errores) {
        //generar cadena de texto con los errores
        String mensaje = "";
        for (String e : errores) {
            mensaje += "-" + e + "\n";
        }
        AlertDialog.Builder aleBuilder = new AlertDialog.Builder(MainActivity.this);
        //Chaining (encadenamiento)
        aleBuilder.setTitle("Error de validación")//define el titulo
                .setMessage(mensaje)//define el mensaje del dialogo
                .setPositiveButton("Aceptar", null)//agrega el boton aceptar
                .create()//crea el alert
                .show();//lo muestra

    }


}