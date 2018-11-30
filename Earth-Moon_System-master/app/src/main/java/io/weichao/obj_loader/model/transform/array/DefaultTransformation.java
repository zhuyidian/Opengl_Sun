package io.weichao.obj_loader.model.transform.array;

import java.util.List;

import io.weichao.obj_loader.model.Normal;
import io.weichao.obj_loader.model.TexturePoint;
import io.weichao.obj_loader.model.Vertex;

public class DefaultTransformation extends ArrayTransformation {
	
	public DefaultTransformation() {
		super(1);
	}

	@Override
	public List<Vertex> transformVertices(List<Vertex> vertices) {
		return vertices;
	}

	@Override
	public List<Normal> transformNormals(List<Normal> normals) {
		return normals;
	}
	
	@Override
	public List<Short> transformIndices(List<Short> indices, int maxIndex) {
		return indices;
	}

	@Override
	public List<TexturePoint> transformTextureCoordinates(List<TexturePoint> textureCoordinates) {
		return textureCoordinates;
	}

}
