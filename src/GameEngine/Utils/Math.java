package GameEngine.Utils;

public class Math {
   private Math(){} // No Constructor Just A Container Class

   /* ***** Math Functions ***** */
   // Hyperbolics Taken From https://introcs.cs.princeton.edu/java/22library/Hyperbolic.java.html
   public static double cosh(double x) {
      return (java.lang.Math.exp(x) + java.lang.Math.exp(-x)) / 2.0;
   }


   public static double sinh(double x) {
      return (java.lang.Math.exp(x) - java.lang.Math.exp(-x)) / 2.0;
   }


   public static double tanh(double x) {
      return sinh(x) / cosh(x);
   }


   public static double atanh(double a) {
      //www.java2s.com/example/java-utility-method/atanh/atanh-double-a-fb896.html
      final double mult;
      // check the sign bit of the raw representation to handle -0
      if (Double.doubleToRawLongBits(a) < 0) {
         a = java.lang.Math.abs(a);
         mult = -0.5d;
      } else {
         mult = 0.5d;
      }
      return mult * java.lang.Math.log((1.0d + a) / (1.0d - a));
   }


   public static double acosh(double x) {
      // http://www.java2s.com/example/java-utility-method/acosh/acosh-double-x-02b94.html
      double ans;

      if (Double.isNaN(x) || (x < 1)) {
         ans = Double.NaN;
      }
      // 94906265.62 = 1.0/Math.sqrt(EPSILON_SMALL)

      else if (x < 94906265.62) {
         ans = safeLog(x + java.lang.Math.sqrt(x * x - 1.0D));
      } else {
         ans = 0.69314718055994530941723212145818D + safeLog(x);
      }

      return ans;
   }


   public static double safeLog(double x) {
      if (x == 0.0D) {
         return 0.0D;
      } else {
         return java.lang.Math.log(x);
      }
   }
}
