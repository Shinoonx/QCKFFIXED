package Dominio;

import Launcher.Main;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public class ServicioPedidos {
	private JsonNode rootUsuarios;
	private JsonNode rootDias;
	private ObjectMapper mapper;
	private Scanner scanner;
	private Pagos pagos;

	public ServicioPedidos() {
		mapper = new ObjectMapper();
		pagos = new Pagos(); // Inicializar la clase de pagos

		try {
			// Cargar JSON de usuarios
			File jsonFileUsuarios = new File("src/main/java/Datos/usuarios.json");
			if (!jsonFileUsuarios.exists()) {
				throw new IOException("Archivo JSON de usuarios no encontrado en la ruta especificada: " + jsonFileUsuarios.getAbsolutePath());
			}
			rootUsuarios = mapper.readTree(jsonFileUsuarios);

			// Cargar JSON de días
			File jsonFileDias = new File("src/main/java/Datos/dia.json");
			if (!jsonFileDias.exists()) {
				throw new IOException("Archivo JSON de días no encontrado en la ruta especificada: " + jsonFileDias.getAbsolutePath());
			}
			rootDias = mapper.readTree(jsonFileDias);
		} catch (IOException e) {
			System.out.println("Error al cargar los archivos JSON: " + e.getMessage());
		}

		scanner = new Scanner(System.in);
	}

	public void iniciar(Usuario usuario) {
		System.out.println("Bienvenido, " + usuario.getCorreoElectronico());

		// Validar día
		String dia;
		JsonNode diasNode = rootDias.get("dia");
		if (diasNode == null) {
			System.out.println("El nodo 'dia' no existe en el JSON.");
			return;
		}

		while (true) {
			System.out.println("Ingrese el día (lunes, martes, miércoles, jueves, viernes): ");
			dia = scanner.nextLine().toLowerCase();
			if (diasNode.has(dia)) {
				break;
			} else {
				System.out.println("El día no es válido. Por favor, inténtelo de nuevo.");
			}
		}

		JsonNode diaNode = diasNode.get(dia);
		if (diaNode == null) {
			System.out.println("El nodo para el día " + dia + " no existe.");
			return;
		}

		// Cargar JSON de menús
		JsonNode menusNode = null;
		try {
			File jsonFileMenus = new File("src/main/java/Datos/menu.json");
			if (!jsonFileMenus.exists()) {
				throw new IOException("Archivo JSON de menús no encontrado en la ruta especificada: " + jsonFileMenus.getAbsolutePath());
			}
			JsonNode rootMenus = mapper.readTree(jsonFileMenus);
			menusNode = rootMenus.get("dias").get(dia).get("menus");
			if (menusNode == null) {
				System.out.println("El nodo 'menus' no existe para el día " + dia + ".");
				return;
			}
		} catch (IOException e) {
			System.out.println("Error al cargar el archivo de menús: " + e.getMessage());
			return;
		}

		// Seleccionar tipo de menú
		String tipoMenu;
		Menu menu = null;
		while (true) {
			System.out.println("Ingrese el tipo de menú (vegetariano, economico, ejecutivo, baes): ");
			tipoMenu = scanner.nextLine().toLowerCase();
			if (menusNode.has(tipoMenu)) {
				menu = Menu.fromJsonNode(menusNode.get(tipoMenu));
				break;
			} else {
				System.out.println("El tipo de menú no es válido. Por favor, inténtelo de nuevo.");
			}
		}

		// Seleccionar opciones de menú
		String almuerzoComprado = selectOption("bebestibles", menu.getBebestibles())
				+ ", " + selectOption("plato de fondo", menu.getPlatoDeFondo())
				+ ", " + selectOption("ensalada", menu.getEnsalada())
				+ ", " + selectOption("postre", menu.getPostre())
				+ ", " + selectOption("sopa", menu.getSopa())
				+ ", " + selectOption("acompañamiento", menu.getAcompañamiento());

		usuario.setAlmuerzoComprado(almuerzoComprado);
		System.out.println("Cliente: " + usuario.getCorreoElectronico());
		System.out.println("Almuerzo comprado: " + usuario.getAlmuerzoComprado());
		System.out.println("Precio total: $" + menu.getPrecio());

		// Solicitar el RUT y el código de pago
		System.out.println("Ingrese el RUT: ");
		String rut = scanner.nextLine();
		System.out.println("Ingrese el código de pago: ");
		String codigoPago = scanner.nextLine();

		// Verificar pago antes de actualizar los JSON
		if (pagos.verificarPago(rut, codigoPago)) {
			System.out.println("Pago verificado.");
			// Actualizar JSON de usuarios y días
			try {
				int numeroAsignado = actualizarJsonDia(dia, usuario);
				actualizarJsonUsuario(usuario);
				System.out.println("El numero de retiro de su almuerzo es: " + numeroAsignado);
			} catch (IOException e) {
				System.out.println("Error al actualizar los archivos JSON: " + e.getMessage());
			}
		} else {
			System.out.println("Pago no verificado o no disponible, no se puede agregar el pedido.");
		}
	}

	private String selectOption(String category, String[] options) {
		if (options != null && options.length > 0) {
			while (true) {
				System.out.println("Seleccione " + category + ": ");
				for (int i = 0; i < options.length; i++) {
					System.out.println((i + 1) + ". " + options[i]);
				}
				try {
					int choice = scanner.nextInt();
					scanner.nextLine();  // Consumir nueva línea
					if (choice > 0 && choice <= options.length) {
						System.out.println("Usted ha seleccionado: " + options[choice - 1] + " para " + category);
						return options[choice - 1];
					} else {
						System.out.println("Selección no válida para " + category + ". Por favor, inténtelo de nuevo.");
					}
				} catch (InputMismatchException e) {
					System.out.println("Entrada no válida. Por favor, ingrese un número.");
					scanner.nextLine();  // Consumir la entrada inválida
				}
			}
		}
		return "";
	}

	private void actualizarJsonUsuario(Usuario usuario) throws IOException {
		File jsonFileUsuarios = new File("src/main/java/Datos/usuarios.json");
		JsonNode usuariosNode = rootUsuarios.get("usuarios");
		for (JsonNode usuarioNode : usuariosNode) {
			if (usuarioNode.get("correoElectronico").asText().equals(usuario.getCorreoElectronico())) {
				ObjectNode almuerzosCompradosNode = (ObjectNode) usuarioNode.get("almuerzosComprados");
				if (almuerzosCompradosNode == null) {
					almuerzosCompradosNode = mapper.createObjectNode();
					((ObjectNode) usuarioNode).set("almuerzosComprados", almuerzosCompradosNode);
				}

				// Contar el número de almuerzos existentes
				int numAlmuerzos = 0;
				Iterator<Map.Entry<String, JsonNode>> fields = almuerzosCompradosNode.fields();
				while (fields.hasNext()) {
					Map.Entry<String, JsonNode> field = fields.next();
					int currentNum = Integer.parseInt(field.getKey());
					if (currentNum > numAlmuerzos) {
						numAlmuerzos = currentNum;
					}
				}

				// Crear un nuevo objeto para el almuerzo con el número de almuerzo
				ObjectNode nuevoAlmuerzo = mapper.createObjectNode();
				String[] detallesAlmuerzo = usuario.getAlmuerzoComprado().split(", ");
				ArrayNode detallesArray = nuevoAlmuerzo.putArray("detalles");
				for (String detalle : detallesAlmuerzo) {
					detallesArray.add(detalle);
				}
				nuevoAlmuerzo.put("correoElectronico", usuario.getCorreoElectronico());

				// Añadir el nuevo almuerzo al nodo de almuerzos comprados
				almuerzosCompradosNode.set(String.valueOf(numAlmuerzos + 1), nuevoAlmuerzo);
				break;
			}
		}
		mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFileUsuarios, rootUsuarios);
	}

	private int actualizarJsonDia(String dia, Usuario usuario) throws IOException {
		File jsonFileDias = new File("src/main/java/Datos/dia.json");
		JsonNode diaNode = rootDias.get("dia").get(dia);
		if (diaNode == null) {
			System.out.println("El nodo para el día " + dia + " no existe.");
			return -1;
		}
		ObjectNode almuerzosCompradosNode = (ObjectNode) diaNode.get("almuerzosComprados");
		if (almuerzosCompradosNode == null) {
			almuerzosCompradosNode = mapper.createObjectNode();
			((ObjectNode) diaNode).set("almuerzosComprados", almuerzosCompradosNode);
		}

		// Contar el número de almuerzos existentes
		int numAlmuerzos = 0;
		Iterator<Map.Entry<String, JsonNode>> fields = almuerzosCompradosNode.fields();
		while (fields.hasNext()) {
			Map.Entry<String, JsonNode> field = fields.next();
			int currentNum = Integer.parseInt(field.getKey());
			if (currentNum > numAlmuerzos) {
				numAlmuerzos = currentNum;
			}
		}

		// Crear un nuevo objeto para el almuerzo con el número de almuerzo
		ObjectNode nuevoAlmuerzo = mapper.createObjectNode();
		String[] detallesAlmuerzo = usuario.getAlmuerzoComprado().split(", ");
		ArrayNode detallesArray = nuevoAlmuerzo.putArray("detalles");
		for (String detalle : detallesAlmuerzo) {
			detallesArray.add(detalle);
		}
		nuevoAlmuerzo.put("correoElectronico", usuario.getCorreoElectronico());

		// Añadir el nuevo almuerzo al nodo de almuerzos comprados
		int numeroAsignado = numAlmuerzos + 1;
		almuerzosCompradosNode.set(String.valueOf(numeroAsignado), nuevoAlmuerzo);

		mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFileDias, rootDias);

		return numeroAsignado;
	}
}
