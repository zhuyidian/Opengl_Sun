package io.weichao.obj_loader.matcher.implementation;

import java.util.List;
import java.util.regex.Pattern;

import io.weichao.obj_loader.matcher.MatchHandler;
import io.weichao.obj_loader.matcher.primitive.FloatMatcher;
import io.weichao.obj_loader.model.TexturePoint;

public class TextureMatcher extends MatchHandler<TexturePoint> {
	
	private Pattern textureLinePattern = Pattern.compile("^vt.*$");

	@Override
	protected Pattern getPattern() {
		return this.textureLinePattern;
	}
	
	@Override
	protected void handleMatch(String group) {
		FloatMatcher floatMatcher = new FloatMatcher();
		floatMatcher.matchString(group);
		
		List<Float> coordinates = floatMatcher.getMatches();
		if(coordinates.size() < 3) {
			coordinates.add(null);
		}
		
		this.addMatch(new TexturePoint(coordinates));
	}
	
}
