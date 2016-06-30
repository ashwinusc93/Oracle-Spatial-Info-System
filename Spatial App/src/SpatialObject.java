import oracle.spatial.geometry.JGeometry;

public class SpatialObject {
	public JGeometry geometry;
	public String id;
	public int radius;
	
	public SpatialObject(JGeometry geometry, String id) {
		this.geometry = geometry;
		this.id = id;
	}
	
	public SpatialObject(JGeometry geometry, String id, int radius) {
		this.geometry = geometry;
		this.id = id;
		this.radius = radius;
	}
}
