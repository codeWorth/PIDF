package ai;

public abstract class Path {

	public double duration;
	
	public abstract double deltaSpeed(double time);
	public abstract double displacement(double time);
	
}
