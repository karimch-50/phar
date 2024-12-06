package com.labo.fs.ui.components;

public enum Position {

	ABSOLUTE("absolute"), FIXED("fixed"), RELATIVE("relative");

	private final String value;

	Position(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
