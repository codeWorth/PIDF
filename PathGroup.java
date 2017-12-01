package ai;

import java.util.ArrayList;

public class PathGroup extends Path {

	private ArrayList<Path> paths = new ArrayList<>();
	
	public void addPath(double delta, int deltaIndex, double slope, int slopeIndex) {
		
		double lastSpeed = deltaSpeed(this.duration);
		GeneralPath newPath = new GeneralPath(delta, deltaIndex, slope, slopeIndex, lastSpeed);
		this.duration += newPath.duration;
		this.paths.add(newPath);
		
	}
	
	public void addPath(double offset, double delta, int deltaIndex, double slope, int slopeIndex) {
		
		double lastSpeed = deltaSpeed(this.duration);
		GeneralPath newPath = new GeneralPath(offset, delta, deltaIndex, slope, slopeIndex, lastSpeed);
		this.duration += newPath.duration;
		this.paths.add(newPath);
		
	}

	@Override
	public double deltaSpeed(double time) {
		
		double speed = 0;
		double timeSum = 0;
		
		for (Path path : this.paths) {
			
			if (time <= timeSum + path.duration) {
				double realTime = time - timeSum;
				return path.deltaSpeed(realTime) + speed;
			}
			
			timeSum += path.duration;
			speed += path.deltaSpeed(path.duration);
			
		}
		
		return speed;
		
	}

	@Override
	public double displacement(double time) {
		
		double position = 0;
		double lastSpeed = 0;
		double timeSum = 0;
		
		for (Path path : this.paths) {
			
			if (time < timeSum + path.duration) {
				double realTime = time - timeSum;
				return path.displacement(realTime) + realTime * lastSpeed + position;
			}
			
			timeSum += path.duration;
			position += path.displacement(path.duration) + path.duration * lastSpeed;
			lastSpeed = deltaSpeed(timeSum);
			
		}
		
		return position;
	}
	
	
	public static PathGroup trapezoid(double acceleration, double maxSpeed, double distance) {
		
		PathGroup group = new PathGroup();
		
		group.addPath(maxSpeed, PathIndex.SPEED, acceleration, PathIndex.ACCELERATION);
		double accelDist = group.displacement(group.duration);
		double remainingDist = distance - accelDist * 2;
		
		group.addPath(remainingDist, PathIndex.DISTANCE, 0, PathIndex.SPEED);
		group.addPath(-maxSpeed, PathIndex.SPEED, -acceleration, PathIndex.ACCELERATION);
		
		return group;
		
	}
	
	public static PathGroup trapezoidSmooth(double jerk, double acceleration, double maxSpeed, double distance) {
		
		PathGroup group = new PathGroup();
		
		group.addPath(acceleration, PathIndex.ACCELERATION, jerk, PathIndex.JERK);
		double neededSpeed = maxSpeed - group.deltaSpeed(group.duration)*2;
		
		group.addPath(neededSpeed, PathIndex.SPEED, acceleration, PathIndex.ACCELERATION);
		group.addPath(acceleration, -acceleration, PathIndex.ACCELERATION, -jerk, PathIndex.JERK);
		
		double neededDist = distance - 2 * group.displacement(group.duration);
		group.addPath(neededDist, PathIndex.DISTANCE, 0, PathIndex.SPEED);
		
		group.addPath(-acceleration, PathIndex.ACCELERATION, -jerk, PathIndex.JERK);
		group.addPath(-neededSpeed, PathIndex.SPEED, -acceleration, PathIndex.ACCELERATION);
		group.addPath(-acceleration, acceleration, PathIndex.ACCELERATION, jerk, PathIndex.JERK);
		
		return group;
		
	}
	
}
