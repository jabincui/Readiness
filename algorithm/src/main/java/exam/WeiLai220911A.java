package exam;

public class WeiLai220911A {
  static void solution(int[] arr) {
    int n = arr.length;
    int[] left = new int[n];
    left[0] = arr[0];
    for (int i = 1; i < n; i++) {
      left[i] = left[i-1] + arr[i];
    }
    int right = 0;
    int indOfLeftThatRightNotBiggerThan = 0;
    int smallestGap = Integer.MAX_VALUE;
    int smallestGapL = 0, smallestGapR = 0;
    for (int i = n-1; i > indOfLeftThatRightNotBiggerThan; i--) {
      right += arr[i];
      while (indOfLeftThatRightNotBiggerThan < i
          && right > left[indOfLeftThatRightNotBiggerThan]) {
        indOfLeftThatRightNotBiggerThan++;
      }
      int leftGap = Integer.MAX_VALUE, rightGap = Integer.MAX_VALUE;
      if (indOfLeftThatRightNotBiggerThan < n) {
        leftGap = left[indOfLeftThatRightNotBiggerThan] - right;
      }
      if (indOfLeftThatRightNotBiggerThan > 0) {
         rightGap = right - left[indOfLeftThatRightNotBiggerThan-1];
      }
      if (leftGap < smallestGap) {
        smallestGap = leftGap;
        smallestGapL = indOfLeftThatRightNotBiggerThan;
        smallestGapR = i;
      }
      if (rightGap < smallestGap) {
        smallestGap = rightGap;
        smallestGapL = indOfLeftThatRightNotBiggerThan-1;
        smallestGapR = i;
      }
    }
    System.out.println(smallestGap + " " + (smallestGapL+1) + " " + (smallestGapR+1));
  }

  public static void main(String[] args) {
    WeiLai220911A.solution(new int[]{1, 2, 3, 4, 5});
  }
}
