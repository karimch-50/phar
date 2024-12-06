package com.pharma.fs.ui.components;

import com.pharma.fs.ui.components.css.lumo.BadgeColor;
import com.pharma.fs.ui.components.css.lumo.BadgeShape;
import com.pharma.fs.ui.components.css.lumo.BadgeSize;
import com.vaadin.flow.component.html.Span;

import static com.pharma.fs.ui.components.css.lumo.BadgeShape.PILL;

import java.util.StringJoiner;


public class Badge extends Span {

	public Badge(String text) {
		this(text, BadgeColor.NORMAL);
	}

	public Badge(String text, BadgeColor color) {
		super(text);
		//UIUtils.setTheme(color.getThemeName(), this);
	}

	public Badge(String text, BadgeColor color, BadgeSize size, BadgeShape shape) {
		super(text);
		StringJoiner joiner = new StringJoiner(" ");
		joiner.add(color.getThemeName());
		if (shape.equals(PILL)) {
			joiner.add(shape.getThemeName());
		}
		if (size.equals(BadgeSize.S)) {
			joiner.add(size.getThemeName());
		}
	//	UIUtils.setTheme(joiner.toString(), this);
	}

}
