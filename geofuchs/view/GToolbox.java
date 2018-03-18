

package geofuchs.view;

/** This class contains some helper methods. */
public class GToolbox
{

	/**
	 * Decreases a value, but only if the result isn't lower than zero.
	 * @param val The value.
	 * @return The decreased value val-1, or val, if val-1 is lower than zero.
	 */
	public static double decreaseValue(double val)
	{
		if ((val-1)>0)
			return --val;
		return val;
	}

	/**
	 * Decreases a value, but only if the result isn't lower than zero.
	 * @param val The value.
	 * @param step Another value.
	 * @return The decreased value val-step, or val, if val-step is lower than zero.
	 */
	public static double decreaseValue(double val,double step){
		if ((val-step)>0)
			return (val-step);
		return val;
	}

}
