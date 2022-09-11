package exam;

import java.util.Scanner;

public class WeiLai220911ASimple {

  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    int n = sc.nextInt();
    int[] arr = new int[n];
    int[] left = new int[n];
    arr[0] = sc.nextInt();
    left[0] = arr[0];
    for (int i = 1; i < n; i++) {
      arr[i] = sc.nextInt();
      left[i] = left[i-1] + arr[i];
    }
    int right = 0;
    int ind = 0;
    int g = Integer.MAX_VALUE;
    int l = 0, r = 0;
    for (int i = n-1; i > ind; i--) {
      right += arr[i];
      while (ind < i && right > left[ind]) ind++;
      int leftGap = Integer.MAX_VALUE, rightGap = Integer.MAX_VALUE;
      if (ind < n) leftGap = left[ind] - right;
      if (ind > 0) rightGap = right - left[ind-1];
      if (leftGap < g) {
        g = leftGap;
        l = ind;
        r = i;
      }
      if (rightGap < g) {
        g = rightGap;
        l = ind-1;
        r = i;
      }
    }
    System.out.println(g + " " + (l+1) + " " + (r+1));
  }
}
