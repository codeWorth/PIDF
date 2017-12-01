package ai;

public class GeneralPath extends Path {

	private int slopeIndex;
	private double slope;
	private double offset;
	private int offsetIndex;
	
	public GeneralPath(double delta, int deltaIndex, double slope, int slopeIndex) {
		
		int indexDiff = slopeIndex - deltaIndex;
		
		this.slopeIndex = slopeIndex;
		this.slope = slope;
		
		if (indexDiff <= 0 || slope == 0) {
			throw new IllegalArgumentException();
		}
		
		int factorial = factorial(indexDiff);
		duration = Math.pow(factorial * delta / slope, 1 / (double)indexDiff);
		
	}
	
	public GeneralPath(double delta, int deltaIndex, double slope, int slopeIndex, double prevSpeed) {
		int indexDiff = slopeIndex - deltaIndex;
		
		this.slopeIndex = slopeIndex;
		this.slope = slope;
		
		if (indexDiff <= 0) {
			throw new IllegalArgumentException();
		}
		
		if (slope == 0) {
			if (deltaIndex == PathIndex.DISTANCE) {
				duration = delta/prevSpeed;
			} else {
				throw new IllegalArgumentException();
			}
		}
		
		double factorial = factorial(indexDiff);
		double coefficient = slope / factorial;
		
		if (deltaIndex > 0) {
			duration = Math.pow(factorial * delta / slope, 1 / (double)indexDiff);
		} else {
			duration = solve(coefficient, slopeIndex, prevSpeed, delta);
		}
		
	}
	
	public GeneralPath(double initial, double delta, int deltaIndex, double slope, int slopeIndex, double prevSpeed) {
		if (deltaIndex < 2) {
			throw new IllegalArgumentException();
		}
		
		int indexDiff = slopeIndex - deltaIndex;
		
		this.slopeIndex = slopeIndex;
		this.slope = slope;
		this.offset = initial;
		this.offsetIndex = deltaIndex;
		
		if (indexDiff <= 0) {
			throw new IllegalArgumentException();
		}
		
		if (slope == 0) {
			if (deltaIndex == PathIndex.DISTANCE) {
				duration = delta/prevSpeed;
			} else {
				throw new IllegalArgumentException();
			}
		}
		
		double factorial = factorial(indexDiff);		
		duration = Math.pow(factorial * delta / slope, 1 / (double)indexDiff);
		
	}
	
	/*
	 * Solves an equation in the form ax^n + bx = d
	 */
	private static double solve(double a, int n, double b, double d) {
	
		double x = 0;
		double p = 0.05;
		double error = a * Math.pow(x, n) + b * x - d;
		boolean wantedSign = (error < 0);
		
		double delta = -1;
		if (wantedSign) {
			delta = 1;
		}
		
		int count = 0;
		while (count < 50 && Math.abs(error) > 0.001) {
			
			x += delta;
			count++;
			
			error = a * Math.pow(x, n) + b * x - d;
			boolean sign = error > 0;
			
			if (wantedSign == sign) {
				delta *= -1;
				wantedSign = !wantedSign;
				delta *= Math.sqrt(Math.abs(error)*p);
			}
			
		}
		
		return x;
		
	}
	
	@Override
	public double deltaSpeed(double time) {
		
		if (time > duration) {
			return 0;
		}
		
		double factorial = factorial(slopeIndex - 1);
		double factorialOffset = factorial(this.offsetIndex - 1);
		return Math.pow(time, slopeIndex - 1) * slope / factorial + Math.pow(time, offsetIndex - 1) * offset / factorialOffset;
		
	};
	
	@Override
	public double displacement(double time) {
		
		if (time > duration) {
			return displacement(duration);
		} else {
			
			double factorial = factorial(slopeIndex);
			double factorialOffset = factorial(this.offsetIndex);
			return Math.pow(time, slopeIndex) * slope / factorial + Math.pow(time, offsetIndex) * offset / factorialOffset;
			
		}
		
	};
	
	public static int factorial(int i) {
		int result = 1;
		
		while (i > 1) {
			result *= i;
			i--;
		}
		
		return result;
	}
	
	public static void main(String[] args) {
		
		PathGroup pathGroup = PathGroup.trapezoidSmooth(1, 2, 5, 30);
		
		for (double i = 0; i < 12; i+=0.1) {
			System.out.println(pathGroup.deltaSpeed(i));
			System.out.println(pathGroup.displacement(i));
			System.out.println();
		}
		
	}
		
}