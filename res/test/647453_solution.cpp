#include <bits/stdc++.h>

using namespace std;

#define int long double

signed main()
{
    int n, sum = 0, ch = 0, t, temp;
    long double ans = 0;
    cin >> n;
    vector<int>a;
    for(int i = 0; i < n; i++) {
        cin >> t;
        for(int j = 0; j < t; j++) {
            cin >> temp;
            a.push_back(temp);
        }
    }
    sort(a.begin(), a.end());
    if(a.size() % 2 == 0) {
        for(int i = a.size() - 1; i > 2; i -= 2)
            ans += (a[i] + a[i - 1]) / 2;
    } else {
        for(int i = a.size() - 1; i > 3; i -= 2)
            ans += (a[i] + a[i - 1]) / 2;
        ans += (a[0] + a[1] + a[2]) / 3;
    }
    cout << setprecision(10) << fixed << ans;
    return 0;
}