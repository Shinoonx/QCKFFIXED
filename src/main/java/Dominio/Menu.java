package Dominio;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Iterator;

public class Menu {
	private int precio;
	private String[] bebestibles;
	private String[] platoDeFondo;
	private String[] ensalada;
	private String[] postre;
	private String[] sopa;
	private String[] acompañamiento;

	// Getters y setters
	public int getPrecio() {
		return precio;
	}

	public void setPrecio(int precio) {
		this.precio = precio;
	}

	public String[] getBebestibles() {
		return bebestibles;
	}

	public void setBebestibles(String[] bebestibles) {
		this.bebestibles = bebestibles;
	}

	public String[] getPlatoDeFondo() {
		return platoDeFondo;
	}

	public void setPlatoDeFondo(String[] platoDeFondo) {
		this.platoDeFondo = platoDeFondo;
	}

	public String[] getEnsalada() {
		return ensalada;
	}

	public void setEnsalada(String[] ensalada) {
		this.ensalada = ensalada;
	}

	public String[] getPostre() {
		return postre;
	}

	public void setPostre(String[] postre) {
		this.postre = postre;
	}

	public String[] getSopa() {
		return sopa;
	}

	public void setSopa(String[] sopa) {
		this.sopa = sopa;
	}

	public String[] getAcompañamiento() {
		return acompañamiento;
	}

	public void setAcompañamiento(String[] acompañamiento) {
		this.acompañamiento = acompañamiento;
	}

	public static Menu fromJsonNode(JsonNode node) {
		Menu menu = new Menu();
		if (node != null) {
			menu.setPrecio(node.get("precio").asInt());
			menu.setBebestibles(loadArray(node.get("bebestibles")));
			menu.setPlatoDeFondo(loadArray(node.get("platoDeFondo")));
			menu.setEnsalada(loadArray(node.get("ensalada")));
			menu.setPostre(loadArray(node.get("postre")));
			menu.setSopa(loadArray(node.get("sopa")));
			menu.setAcompañamiento(loadArray(node.get("acompañamiento")));
		}
		return menu;
	}

	private static String[] loadArray(JsonNode arrayNode) {
		if (arrayNode != null) {
			Iterator<JsonNode> elements = arrayNode.elements();
			String[] array = new String[arrayNode.size()];
			int i = 0;
			while (elements.hasNext()) {
				array[i++] = elements.next().asText();
			}
			return array;
		}
		return null;
	}
}