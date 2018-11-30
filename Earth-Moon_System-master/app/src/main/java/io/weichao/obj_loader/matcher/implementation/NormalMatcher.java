package io.weichao.obj_loader.matcher.implementation;

import java.util.List;
import java.util.regex.Pattern;

import io.weichao.obj_loader.matcher.MatchHandler;
import io.weichao.obj_loader.matcher.primitive.FloatMatcher;
import io.weichao.obj_loader.model.Normal;

public class NormalMatcher extends MatchHandler<Normal> {
	
	
	private Pattern normalLinePattern = Pattern.compile("^vn.*$");

	@Override
	protected Pattern getPattern() {
		return this.normalLinePattern;
	}
	
	@Override
	protected void handleMatch(String group) {
		FloatMatcher floatMatcher = new FloatMatcher();
		floatMatcher.matchString(group);
		
		List<Float> coordinates = floatMatcher.getMatches();
		
		this.addMatch(new Normal(coordinates));
	}
	
}
