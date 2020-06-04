package it.polimi.tiw.estimates.beans;

public enum OptionalType {
	NORMAL("NORMAL"), SALE("SALE");

	private final String text;

	OptionalType(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
}
