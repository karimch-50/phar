package com.pharma.fs.ui.components.css.lumo;

public enum BadgeShape {

	NORMAL("normal"), PILL("pill");

	private final String style;

	BadgeShape(String style) {
		this.style = style;
	}

	public String getThemeName() {
		return style;
	}

}
