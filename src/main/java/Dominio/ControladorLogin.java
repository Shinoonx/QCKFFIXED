package Dominio;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ControladorLogin {
    private Usuario usuario;
    private ObjectMapper mapper;
    private File jsonFile;

    public ControladorLogin() {
        try {
            usuario = new Usuario();
            mapper = new ObjectMapper();
            jsonFile = new File("src/main/java/Datos/usuarios.json");
            if (!jsonFile.exists()) {
                throw new IOException("Archivo JSON no encontrado en la ruta especificada: " + jsonFile.getAbsolutePath());
            }
        } catch (IOException e) {
            System.out.println("Error al inicializar el Gestor de Sesión: " + e.getMessage());
        }
    }

    public Usuario iniciarSesion() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingrese su correo electrónico: ");
        String correo = scanner.nextLine();
        System.out.println("Ingrese su contraseña: ");
        String contraseña = scanner.nextLine();

        try {
            Iterator<JsonNode> usuarios = usuario.root.get("usuarios").elements();
            while (usuarios.hasNext()) {
                JsonNode usuarioNode = usuarios.next();

                if (usuarioNode.has("correoElectronico") && usuarioNode.has("contraseña")) {
                    String jsonCorreo = usuarioNode.get("correoElectronico").asText();
                    String jsonContraseña = usuarioNode.get("contraseña").asText();

                    if (jsonCorreo.equals(correo) && jsonContraseña.equals(contraseña)) {
                        Usuario usuario = new Usuario(correo, contraseña, "");
                        if (usuarioNode.has("rut")) {
                            usuario.setRut(usuarioNode.get("rut").asText()); // Obtener el RUT del JSON
                        }
                        System.out.println("Inicio de sesión exitoso.");
                        return usuario;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error al procesar el inicio de sesión: " + e.getMessage());
        }

        System.out.println("Correo electrónico o contraseña incorrectos.");
        return null;
    }

    public boolean registrarse() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingrese su correo electrónico: ");
        String correo = scanner.nextLine();
        System.out.println("Ingrese su contraseña: ");
        String contraseña = scanner.nextLine();

        try {

            Iterator<JsonNode> usuarios = usuario.root.get("usuarios").elements();
            while (usuarios.hasNext()) {
                JsonNode usuarioNode = usuarios.next();
                if (usuarioNode.has("correoElectronico") && usuarioNode.get("correoElectronico").asText().equals(correo)) {
                    System.out.println("El correo electrónico ya está registrado.");
                    return false;
                }
            }


            JsonNodeFactory factory = JsonNodeFactory.instance;
            ObjectNode newNode = factory.objectNode();
            newNode.put("correoElectronico", correo);
            newNode.put("contraseña", contraseña);


            ArrayNode usuariosArray = (ArrayNode) usuario.root.get("usuarios");
            usuariosArray.add(newNode);


            FileWriter fileWriter = new FileWriter(jsonFile);
            mapper.writeValue(fileWriter, usuario.root);
            fileWriter.close();

            System.out.println("Registro exitoso.");
            return true;
        } catch (Exception e) {
            System.out.println("Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }
}
