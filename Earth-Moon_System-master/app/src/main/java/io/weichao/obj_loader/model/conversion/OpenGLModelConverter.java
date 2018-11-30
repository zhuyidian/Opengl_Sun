package io.weichao.obj_loader.model.conversion;

import java.util.List;

import io.weichao.obj_loader.model.Face;
import io.weichao.obj_loader.model.OpenGLModelData;

public interface OpenGLModelConverter {
	
	public OpenGLModelData convert(List<Face> faces);

}