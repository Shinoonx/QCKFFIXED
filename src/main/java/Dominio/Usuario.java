package Dominio;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;

public class Usuario {
    private String correoElectronico;
    private String contraseña;
    private String almuerzoComprado;
    JsonNode root;
    private ObjectMapper mapper;
    private String rut; // Agregar atributo rut

    // Constructor para inicializar el objeto Usuario
    public Usuario(String correoElectronico, String contraseña, String almuerzoComprado) {
        this.correoElectronico = correoElectronico;
        this.contraseña = contraseña;
        this.almuerzoComprado = almuerzoComprado;
    }

    // Constructor para inicializar el JSON y el ObjectMapper
    public Usuario() throws IOException {
        mapper = new ObjectMapper();
        File jsonFile = new File("src/main/java/Datos/usuarios.json");
        if (!jsonFile.exists()) {
            throw new IOException("Archivo JSON no encontrado en la ruta especificada: " + jsonFile.getAbsolutePath());
        }
        root = mapper.readTree(jsonFile);
    }

    // Getters y setters
    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getAlmuerzoComprado() {
        return almuerzoComprado;
    }

    public void setAlmuerzoComprado(String almuerzoComprado) {
        this.almuerzoComprado = almuerzoComprado;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }


}