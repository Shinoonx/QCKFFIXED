package Dominio;

import java.util.Scanner;

public class MenuLogin {
    private ControladorLogin controladorLogin;
    private Scanner scanner;

    public MenuLogin() {
        this.controladorLogin = new ControladorLogin();
        this.scanner = new Scanner(System.in);
    }

    public void mostrarMenu() {
        boolean continuar = true;
        while (continuar) {
            System.out.println("Bienvenido!");
            System.out.println("1. Registrarse");
            System.out.println("2. Iniciar sesión");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opción: ");

            int opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    registrarUsuario();
                    break;
                case 2:
                    iniciarSesion();
                    break;
                case 3:
                    continuar = false;
                    System.out.println("¡Hasta luego!");
                    break;
                default:
                    System.out.println("Opción no válida. Por favor, seleccione una opción válida.");
            }
        }
    }

    private void registrarUsuario() {
        controladorLogin.registrarse();
    }

    private void iniciarSesion() {
        Usuario usuarioLogueado = controladorLogin.iniciarSesion();
        if (usuarioLogueado != null) {
            new ServicioPedidos().iniciar(usuarioLogueado);
        }
    }
}