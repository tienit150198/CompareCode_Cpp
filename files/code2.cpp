
// C++ implementation of counting pairs 
// such that gcd (a, b) = b 
#include <bits/stdc++.h> 
using namespace std; 
#define ttt 333
// returns number of valid pairs 
int CountPairs(int n) 
{ 
    // initialize k 
    int k = n; 
  
    // loop till imin <= n 
    int imin = 1; 
  
    // Initialize result 
    int ans = 0; 
  
    while (imin <= n) { 
  
        // max i with given k floor(n/k) 
        int imax = n / k; 
  
        // adding k*(number of i with 
        // floor(n/i) = k to ans 
        ans += k * (imax - imin + 1); 
  
        // set imin = imax + 1 and k = n/imin 
        imin = imax + 1; 
        k = n / imin; 
    } 
  
    return ans; 
} 
  
// Driver function 
int main() 
{ 
	int x = ttt;
	
    cout << CountPairs(1) << endl; 
    cout << CountPairs(2) << endl; 
    cout << CountPairs(3) << endl; 
    return 0; 
} 
