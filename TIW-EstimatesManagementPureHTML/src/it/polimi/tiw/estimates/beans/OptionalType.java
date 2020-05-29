package it.polimi.tiw.estimates.beans;

public enum OptionalType {
	normal("normal"), sale("sale");

	private final String value;

	OptionalType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
