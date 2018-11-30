package io.weichao.obj_loader.matcher.implementation;

import java.util.List;
import java.util.regex.Pattern;

import io.weichao.obj_loader.matcher.MatchHandler;
import io.weichao.obj_loader.matcher.primitive.FloatMatcher;
import io.weichao.obj_loader.model.Vertex;

public class VertexMatcher extends MatchHandler<Vertex> {
	
	private Pattern vertexLinePattern = Pattern.compile("^v .*$");

	@Override
	protected Pattern getPattern() {
		return this.vertexLinePattern;
	}
	
	
	@Override
	protected void handleMatch(String group) {
		FloatMatcher floatMatcher = new FloatMatcher();
		floatMatcher.matchString(group);
		List<Float> xyzwCoordinates = floatMatcher.getMatches();
		// w is missing in xyzw
		if(xyzwCoordinates.size() < 4) {
			xyzwCoordinates.add(null);
		}
		
		this.addMatch(new Vertex(
				xyzwCoordinates.get(0),
				xyzwCoordinates.get(1),
				xyzwCoordinates.get(2),
				xyzwCoordinates.get(3)));
	}
	
}
