/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MusicSynthesizer;

/**
 *
 * @author Monkey D Alok
 */
public class Transformer
{
	// compute the Transformer of x[], assuming its length is a power of 2
	/**
	 *
	 * @param x
	 * @return a complex array of frequency domain
	 */
	public static Complex[] fastFourierTransform(Complex[] x) {
		int N = x.length;

		// base case
		if (N == 1){
			return new Complex[] { x[0] };
		}
		
		// radix 2 Cooley-Tukey Transformer
		if (N % 2 != 0) {
			throw new RuntimeException("N is not a power of 2"); 
		}

		// fastFourierTransform of even terms
		Complex[] even = new Complex[N/2];
		for (int k = 0; k < N/2; k++) {
			even[k] = x[2*k];
		}
		Complex[] q = fastFourierTransform(even);

		// fastFourierTransform of odd terms
		Complex[] odd  = even;  // reuse the array
		for (int k = 0; k < N/2; k++) {
			odd[k] = x[2*k + 1];
		}
		Complex[] r = fastFourierTransform(odd);

		// combine
		Complex[] y = new Complex[N];
		for (int k = 0; k < N/2; k++) {
			double kth = -2 * k * Math.PI / N;
			Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
			y[k]       = q[k].plus(wk.times(r[k]));
			y[k + N/2] = q[k].minus(wk.times(r[k]));
		}
		return y;
	}

}
