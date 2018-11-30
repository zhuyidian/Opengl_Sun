package io.weichao.obj_loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import io.weichao.obj_loader.io.LineReader;
import io.weichao.obj_loader.matcher.MatchHandler;
import io.weichao.obj_loader.matcher.MatchHandlerPool;
import io.weichao.obj_loader.matcher.implementation.FaceMatcher;
import io.weichao.obj_loader.matcher.implementation.NormalMatcher;
import io.weichao.obj_loader.matcher.implementation.TextureMatcher;
import io.weichao.obj_loader.matcher.implementation.VertexMatcher;
import io.weichao.obj_loader.model.FaceProperty;
import io.weichao.obj_loader.model.Normal;
import io.weichao.obj_loader.model.RawOpenGLModel;
import io.weichao.obj_loader.model.TexturePoint;
import io.weichao.obj_loader.model.Vertex;
import io.weichao.obj_loader.model.builder.OpenGLModelBuilder;

/**
 * Usage:
 * 
 * <pre>
 * public static void main(String [] args) {
 *		OpenGLModelData openGLModelData;
 *		try {
 *			RawOpenGLModel openGLModel = new Obj2OpenJL().convert("src/org/obj2openjl/box.obj");
 *			openGLModelData = openGLModel.getDataForGLDrawElements();
 *			
 *			// Access to data via openGLModelData.getVertices() etc.
 *
 *		} catch (FileNotFoundException e) {
 *			e.printStackTrace();
 *		}
 *	}
 * </pre>
 * 
 * 
 * @author Miffels
 *
 */
public class Obj2OpenJL {
	
	private MatchHandlerPool matchHandlerPool;
	private MatchHandler<Vertex> vertexMatcher;
	private MatchHandler<Normal> normalMatcher;
	private MatchHandler<TexturePoint> textureMatcher;
	private MatchHandler<List<FaceProperty>> faceMatcher;
	
	public Obj2OpenJL() {
		this.resetMatchers();
	}
	
	private void resetMatchers() {
		this.vertexMatcher = new VertexMatcher();
		this.normalMatcher = new NormalMatcher();
		this.textureMatcher = new TextureMatcher();
		this.faceMatcher = new FaceMatcher();
		
		this.matchHandlerPool = new MatchHandlerPool(
				this.vertexMatcher,
				this.normalMatcher,
				this.textureMatcher,
				this.faceMatcher);
	}
	
	public RawOpenGLModel convert(String fileName) throws FileNotFoundException {
		File file = new File(fileName);
		if(!file.exists() && !fileName.endsWith(".obj") && !fileName.endsWith(".wvo")) {
			file = new File(fileName + ".obj");
		}
		if(!file.exists()) {
			file = new File(fileName + ".wvo");
		}
		return this.convert(new FileInputStream(file));
	}
	
	public RawOpenGLModel convert(InputStream inputStream) {
		this.resetMatchers();
		
		matchHandlerPool.handleAll(new LineReader(inputStream));
		
		return this.buildOpenGLModel();
	}
	
	private RawOpenGLModel buildOpenGLModel() {
		return new OpenGLModelBuilder().buildOpenGLModel(
				this.vertexMatcher.getMatches(),
				this.normalMatcher.getMatches(),
				this.textureMatcher.getMatches(),
				this.faceMatcher.getMatches());
		
	}
}