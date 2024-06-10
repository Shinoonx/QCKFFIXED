package Dominio;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pagos {
    private Map<String, String> pagos;

    public Pagos() {
        pagos = new HashMap<>();
        cargarPagos();
    }

    private void cargarPagos() {
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/java/Datos/pago.json"))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();
            Gson gson = new Gson();
            Type pagosListType = new TypeToken<Map<String, List<Map<String, String>>>>(){}.getType();
            Map<String, List<Map<String, String>>> pagosMap = gson.fromJson(json, pagosListType);
            for (Map<String, String> pago : pagosMap.get("pago")) {
                pagos.put(pago.get("rut"), pago.get("codigo"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean verificarPago(String rut, String codigo) {
        return pagos.containsKey(rut) && pagos.get(rut).equals(codigo);
    }
}
