package soft3d.v1_0;

public final class FastMath {
	public static float pow(final double a, final double b) {
		final int x = (int) (Double.doubleToRawLongBits(a) >> 32);
		final int y = (int) (b * (x - 1072632447) + 1072632447);
		return (float) Double.longBitsToDouble(((long) y) << 32);
	}

	public static float exp(double val) {
		final long tmp = (long) (1512775 * val + 1072632447);
		return (float) Double.longBitsToDouble(tmp << 32);
	}

	public static float log(float x) {
		return 0.69314718f * log2 (x);
	}
	public static float fastLog2(float val) {
	  //union { float f; uint32_t i; } vx = { x };
	  float y = Float.floatToRawIntBits(val);  //vx.i;
	  y *= 1.0 / (1 << 23);
	  return y - 126.94269504f;
} 
	public static float log2(float val) {
	    //union { float val; int32_t x; } u = { val };
		int x = Float.floatToRawIntBits(val);
	    float log_2 = (float)(((x >> 23) & 255) - 128);              
	    x   &= ~(255 << 23);
	    x   += 127 << 23;
	    val = Float.intBitsToFloat(x);
	    log_2 += ((-0.34484843f) * val + 2.02466578f) * val - 0.67487759f;
	    return (log_2);
	} 

//	public static double powd(double a, double b) {
//		final long tmp = (long) (9076650 * (a - 1) / (a + 1 + 4 * (sqrtd(a)))
//				* b + 1072632447);
//		return Double.longBitsToDouble(tmp << 32);
//	}
//	public static double expd(double val) {
//		final long tmp = (long) (1512775 * val + 1072632447);
//		return Double.longBitsToDouble(tmp << 32);
//	}
//	public static double logd(double x) {
//		return 6 * (x - 1) / (x + 1 + 4 * (Math.sqrt(x)));
//	}
	/**
	 * fast inverse square root : 1.0/sqrt(value)
	 * 
	 * @param x
	 * @return
	 */
//	public static final float invSqrt(float x) {
//		float xhalf = 0.5f * x;
//		int i = floatToRawIntBits(x); // get bits for floating VALUE
//		i = 0x5f375a86 - (i >> 1); // gives initial guess y0
//		x = intBitsToFloat(i); // convert bits BACK to float
//		x = x * (1.5f - xhalf * x * x); // Newton step, repeating increases
//										// accuracy
//										// x = x * (1.5f - (xhalf * x * x)); //
//										// 2nd iteration, this can be removed
//		return x;
//	}

	/**
	 * The second pass gives you almost the exact value of the square root. <br>
	 * 
	 * sqrt 17022.533813476562 <br>
	 * better 17010.557763511835 <br>
	 * evenbetter 17010.553547724947 <br>
	 * Math.sqrt() 17010.553547724423 <br>
	 **/
//	public static double sqrtd(double d) {
//		// double d = 289358932.0;
//		double sqrt = Double
//				.longBitsToDouble(((Double.doubleToRawLongBits(d) - (1l << 52)) >> 1)
//						+ (1l << 61));
//		// double better = (sqrt + d/sqrt)/2.0;
//		// double evenbetter = (better + d/better)/2.0;
//
//		return sqrt;
//	}

	public static float sqrt(final float x) {
		int ui = Float.floatToRawIntBits(x);
		ui = (1 << 29) + (ui >> 1) - (1 << 22);
		float ux = Float.intBitsToFloat(ui);

		// Two Babylonian Steps (simplified from:)
		// u.x = 0.5f * (u.x + x/u.x);
		// u.x = 0.5f * (u.x + x/u.x);
		ux = ux + x / ux;
		ux = 0.25f * ux + x / ux;

		return ux;
	}
	public static final float PI_FLOAT =   3.14159265f;
	public static final float PIBY2_FLOAT = 1.5707963f;
	// |error| < 0.005
	public static float atan2( float y, float x )
	{
	  if ( x == 0.0f )
	  {
	    if ( y > 0.0f ) return PIBY2_FLOAT;
	    if ( y == 0.0f ) return 0.0f;
	    return -PIBY2_FLOAT;
	  }
	  float atan;
	  float z = y/x;
	  if ( Math.abs( z ) < 1.0f )
	  {
	    atan = z/(1.0f + 0.28f*z*z);
	    if ( x < 0.0f )
	    {
	      if ( y < 0.0f ) return atan - PI_FLOAT;
	      return atan + PI_FLOAT;
	    }
	  }
	  else
	  {
	    atan = PIBY2_FLOAT - z/(z*z + 0.28f);
	    if ( y < 0.0f ) return atan - PI_FLOAT;
	  }
	  return atan;
	}
	public static float sin(double x){
		return (float) sind(x);
	}
	public static float cos(double x){
		return (float) cosd(x);
	}
	private static double sind(double x){
		double sin;
		//always wrap input angle to -PI..PI
		if (x< -3.14159265)
		    x+= 6.28318531;
		else
		if (x>  3.14159265)
		    x-= 6.28318531;

		//compute sine
		if (x< 0)
		    sin= 1.27323954 * x+ .405284735 * x* x;
		else
		    sin= 1.27323954 * x- 0.405284735 * x* x;

		return sin;
	}
	private static double cosd(double x){
		double  cos;
		//always wrap input angle to -PI..PI
		if (x< -3.14159265)
		    x+= 6.28318531;
		else
		if (x>  3.14159265)
		    x-= 6.28318531;

		//compute cosine: sin(x + PI/2) = cos(x)
		x+= 1.57079632;
		if (x>  3.14159265)
		    x-= 6.28318531;

		if (x< 0)
		    cos= 1.27323954 * x+ 0.405284735 * x* x;
		else
		    cos= 1.27323954 * x- 0.405284735 * x* x;
		return cos;
	}
	
//	public final static double w_sin(double x){
//		double sin,cos;
//	//always wrap input angle to -PI..PI
//	if (x< -3.14159265)
//	    x+= 6.28318531;
//	else
//	if (x>  3.14159265)
//	    x-= 6.28318531;
//
//	//compute sine
//	if (x< 0)
//	{
//	    sin= 1.27323954 * x+ .405284735 * x* x;
//
//	   if (sin< 0)
//	        sin= .225 * (sin*-sin- sin) + sin;
//	   else
//	        sin= .225 * (sin* sin- sin) + sin;
//	}
//	else
//	{
//	    sin= 1.27323954 * x- 0.405284735 * x* x;
//
//	   if (sin< 0)
//	        sin= .225 * (sin*-sin- sin) + sin;
//	   else
//	        sin= .225 * (sin* sin- sin) + sin;
//	}
//
//	//compute cosine: sin(x + PI/2) = cos(x)
//	x+= 1.57079632;
//	if (x>  3.14159265)
//	    x-= 6.28318531;
//
//	if (x< 0)
//	{
//	    cos= 1.27323954 * x+ 0.405284735 * x* x;
//
//	   if (cos< 0)
//	        cos= .225 * (cos*-cos- cos) + cos;
//	   else
//	        cos= .225 * (cos* cos- cos) + cos;
//	}
//	else
//	{
//	    cos= 1.27323954 * x- 0.405284735 * x* x;
//
//	   if (cos< 0)
//	        cos= .225 * (cos*-cos- cos) + cos;
//	   else
//	        cos= .225 * (cos* cos- cos) + cos;
//	}
//	return sin;
//	}
//	private static final double atan2_p1 = 0.9997878412794807f*(180/Math.PI);
//	private static final double atan2_p3 = -0.3258083974640975f*(180/Math.PI);
//	private static final double atan2_p5 = 0.1555786518463281f*(180/Math.PI);
//	private static final double atan2_p7 = -0.04432655554792128f*(180/Math.PI);
//	private static final double DBL_EPSILON = Float.MIN_VALUE;
//	private static double atan2d_deg( double y, double x )
//	{
//		double ax = Math.abs(x), ay = Math.abs(y);//首先不分象限，求得一个锐角角度
//		double a, c, c2;
//	    if( ax >= ay )
//	    {
//	        c = ay/(ax + (double)DBL_EPSILON);
//	        c2 = c*c;
//	        a = (((atan2_p7*c2 + atan2_p5)*c2 + atan2_p3)*c2 + atan2_p1)*c;
//	    }
//	    else
//	    {
//	        c = ax/(ay + (double) DBL_EPSILON );//
//	        c2 = c*c;
//	        a = 90.f - (((atan2_p7*c2 + atan2_p5)*c2 + atan2_p3)*c2 + atan2_p1)*c;
//	    }
//	    if( x < 0 )//锐角求出后，根据x和y的正负性确定向量的方向，即角度。
//	        a = 180.f - a;
//	    if( y < 0 )
//	        a = 360.f - a;
//	    return a;
//	}
}
