package GrayWhale.Compare;

public class C_Affine {
	// *used by:Compare.compareTwo(), .exhaustiveSearch()
	// comapre.find()
	boolean calcAffine(double from1x, double from1y, double from2x,
			double from2y, double from3x, double from3y, double to1x,
			double to1y, double to2x, double to2y, double to3x, double to3y,
			double[] matrix) {
		// Affine.gaussj()
		// 'from' is the known image
		// 'to' is the unknown image
		double[][] a1 = new double[3][3];
		double[][] b1 = new double[3][1];
		double[][] a2 = new double[3][3];
		double[][] b2 = new double[3][1];

		a1[0][0] = from1x;
		a2[0][0] = from1x;
		a1[0][1] = from1y;
		a2[0][1] = from1y;
		a1[0][2] = 1;
		a2[0][2] = 1;
		b1[0][0] = to1x;
		b2[0][0] = to1y;

		a1[1][0] = from2x;
		a2[1][0] = from2x;
		a1[1][1] = from2y;
		a2[1][1] = from2y;
		a1[1][2] = 1;
		a2[1][2] = 1;
		b1[1][0] = to2x;
		b2[1][0] = to2y;

		a1[2][0] = from3x;
		a2[2][0] = from3x;
		a1[2][1] = from3y;
		a2[2][1] = from3y;
		a1[2][2] = 1;
		a2[2][2] = 1;
		b1[2][0] = to3x;
		b2[2][0] = to3y;

		if (gaussj(a1, 3, b1, 1) == false)
			return false;
		if (gaussj(a2, 3, b2, 1) == false)
			return false;

		matrix[0] = b1[0][0];
		matrix[1] = b1[1][0];
		matrix[2] = b1[2][0];
		matrix[3] = b2[0][0];
		matrix[4] = b2[1][0];
		matrix[5] = b2[2][0];

		return true;
	}

	// *used by:Affine.CalcAffine()
	boolean gaussj(double a[][], int n, double b[][], int m) {
		int[] indxc= new int[n];/* The integer arrays ipiv, indxr, and indxc are */
		int[] indxr= new int[n];/* used for bookkeeping on the pivoting. */ 
		int[] ipiv= new int[n];
		int i, icol = 0, irow = 0, j, k, l, ll;
		double big, dum, pivinv, temp;
		
		for(j=0; j<n; j++)
			ipiv[j]=0;
		
		
		 for (i=0;i<n;i++) { /* This is the main loop over the columns to be reduced. */ 
		     big=0.0;
		     for (j=0;j<n;j++) /* This is the outer loop of the search for a pivot element. */
		       if (ipiv[j] != 1)
		         for (k=0;k<n;k++) {
		           if (ipiv[k] == 0) {
		             if (Math.abs(a[j][k]) >= big) {
		               big=Math.abs(a[j][k]);
		               irow=j;
		               icol=k;
		             }
		           } else if (ipiv[k] > 1) return false; // fprintf(stderr, "gaussj: Singular Matrix-1");
		         }
		     ++(ipiv[icol]);
		/*************************************************************************************************\
		We now have the pivot element, so we interchange rows, if needed, to put the pivot
		element on the diagonal. The columns are not physically interchanged, only relabeled:
		indxc[i], the column of the ith pivot element, is the ith column that is reduced, while
		indxr[i] is the row in which that pivot element was originally located. If indxr[i]
		!= indxc[i] there is an implied column interchange. With this form of bookkeeping, the
		solution b's will end up in the correct order, and the inverse matrix will be scrambled
		by columns.
		\*************************************************************************************************/
		     if (irow != icol) {
		     	for (l=0;l<n;l++) SWAP(a, irow, icol, l);// SWAP(a[irow][l],a[icol][l]);
		     	for (l=0;l<m;l++) SWAP(b, irow, icol, l);// SWAP(b[irow][l],b[icol][l]);
		     }
		     indxr[i]=irow;	   /* We are now ready to divide the pivot row by the */
		     indxc[i]=icol;	   /* pivot element, located at irow and icol. */
		     if (a[icol][icol] == 0.0) return false; // fprintf(stderr, "gaussj: Singular Matrix-2");
		     pivinv=1.0/a[icol][icol];
		     a[icol][icol]=1.0;
		     for (l=0;l<n;l++) a[icol][l] *= pivinv;
		     for (l=0;l<m;l++) b[icol][l] *= pivinv;
		     for (ll=0;ll<n;ll++) /* Next, we reduce the rows... */
		       if (ll != icol) { /* ...except for the pivot one, of course. */
		     	 dum=a[ll][icol];
		     	 a[ll][icol]=0.0;
		     	 for (l=0;l<n;l++) a[ll][l] -= a[icol][l]*dum;
		     	 for (l=0;l<m;l++) b[ll][l] -= b[icol][l]*dum;
		       }
		  }
		/*************************************************************************************************\
		This is the end of the main loop over columns of the reduction. It only remains to unscram-
		ble the solution in view of the column interchanges. We do this by interchanging pairs of
		columns in the reverse order that the permutation was built up.
		\*************************************************************************************************/
		  for (l=n-1;l>=0;l--) {
		    if (indxr[l] != indxc[l])
		       for (k=0;k<n;k++)
		          SWAP2(a, k, indxr, l, indxc);//SWAP2(a[k][indxr[l]],a[k][indxc[l]]);
		  } /* And we are done. */
		
		  for(int p=0;p<ipiv.length;p++){
			  ipiv[p]=0;//delete
			  indxr[p]=0;//delete
			  indxc[p]=0;//delete
		  }
		  
		
		return true;
	}


	private void SWAP2(double[][] a, int k, int[] indxr, int l, int[] indxc) {
		//temp=(a);(a)=(b);(b)=temp;
		double temp=a[k][indxr[l]];
		a[k][indxr[l]]=a[k][indxc[l]];
		a[k][indxc[l]]=temp;
	}

	void SWAP(double[][] x, int row, int col, int l) {
		//temp=(a);(a)=(b);(b)=temp;
		double temp=x[row][l];
		x[row][l]=x[col][l];
		x[col][l]=temp;
	}
}// end of class

