package main.konfy.lib.core.utils;

public class SearchUtils {
   public static int levenshteinDistance(String s1, String s2) {
      int[][] dp = new int[s1.length() + 1][s2.length() + 1];
      int i = 0;

      while (i <= s1.length()) {
         dp[i][0] = i++;
      }

      i = 0;

      while (i <= s2.length()) {
         dp[0][i] = i++;
      }

      for (int ix = 1; ix <= s1.length(); ix++) {
         for (int j = 1; j <= s2.length(); j++) {
            int cost = s1.charAt(ix - 1) == s2.charAt(j - 1) ? 0 : 1;
            dp[ix][j] = Math.min(Math.min(dp[ix - 1][j] + 1, dp[ix][j - 1] + 1), dp[ix - 1][j - 1] + cost);
         }
      }

      return dp[s1.length()][s2.length()];
   }
}
