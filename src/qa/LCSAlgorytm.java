package qa;
	/*
	 * LCSAlgorithm.java
	 */

	/**
	 * <p>This is implementation of the famous "Longest Common Subsequence". It uses
	 * dynamic programming approach to calculate the matrix of two strings based
	 * on how many characters match. This is a core implementation which first
	 * computes the matrix based on the matching characters & assign weights in
	 * the incremental approach. After assigning weights it traverses through the
	 * matrix to find all the matched characters. This longest common subsequence
	 * can be used in different ways based on the underlying implementation. This
	 * approach uses dynamic programming method and works in bottom up way to
	 * generate the suboptimal solution. </p>
	 *
	 * @author rgandhi
	 *
	 */
	public class LCSAlgorytm {

		/**
		 * <p>Calculates the length of longest common sequence.  The matrix is
		 * calculated based on the matching characters between two strings &
		 * by assigning weights in each subsequent steps. </p>
		 *
		 * @param source - Input data
		 * @param target - Entity to be matched
		 * @return - Matrix of weights.
		 */
		public int[][] calculateLength(final char[] source,
				final char[] target) {
			int[][] matrix = new int[source.length+1][target.length+1];

			for (int i = 0; i < source.length; i++) {
				matrix[i][0] = 0;
			}
			for (int j = 0; j < target.length; j++) {
				matrix[0][j] = 0;
			}

			for(int i = 1; i < source.length; i++) {
				for(int j = 1; j < target.length; j++) {
	                if (source[i-1] == target[j-1]) {
	                	matrix[i][j] = matrix[i-1][j-1] + 1;
	                } else if (matrix[i-1][j] >= matrix[i][j-1 ]) {
	                	matrix[i][j] = matrix[i-1][j];
	                } else {
	            		matrix[i][j] = matrix[i][j-1];
	            	}
				}
			}
			return matrix;
		}

		/**
		 * <p>This function traverses through the matrix obtained in the earlier
		 * step in recursive manner & appends the matched elements together and
		 * hence returns the longest subsequence.</p>
		 *
		 * @param builder - StringBuilder
		 * @param matrix - Weights between 2 strings
		 * @param source - Input data
		 * @param target - Entity to be matched
		 * @param i - Source counter
		 * @param j - Target counter
		 * @return - StringBuilder
		 */
		public StringBuilder constructLCS(StringBuilder builder, int[][] matrix,
				final char[] source, final char[] target,int i, int j ) {

			if (i == 0 || j == 0) {
				return new StringBuilder();
			} else 	if (source[i-1] == target[j-1]) {
				constructLCS(builder, matrix, source, target, i-1, j-1);
				builder.append(target[j-1]);
			} else {
				if (matrix[i -1][j] > matrix[i][j -1]) {
					constructLCS(builder, matrix, source, target, i-1, j);
				} else {
					constructLCS(builder, matrix, source, target, i, j-1);
				}
			}

			return builder;
		}
}
