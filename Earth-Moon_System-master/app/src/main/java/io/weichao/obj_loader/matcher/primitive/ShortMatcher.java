package io.weichao.obj_loader.matcher.primitive;

import java.util.regex.Pattern;

import io.weichao.obj_loader.matcher.MatchHandler;

public class ShortMatcher extends MatchHandler<Short> {

	private Pattern shortNumberPattern = Pattern.compile("-?[1-9]++[0-9]*");
	
	@Override
	protected Pattern getPattern() {
		return this.shortNumberPattern;
	}

	@Override
	protected void handleMatch(String group) {
		this.addMatch(Short.parseShort(group));
	}

}
