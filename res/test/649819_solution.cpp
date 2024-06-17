#include <bits/stdc++.h>

using namespace std;

#define int long double

signed main()
{
    int n, sum = 0, sumb = 0, t, temp;
    long double ans = 0;
    cin >> n;
    vector<int>a, b;
    for(int i = 0; i < n; i++) {
        cin >> t;
        b.push_back(t);
        for(int j = 0; j < t; j++) {
            cin >> temp;
            a.push_back(temp);
        }
    }
    sort(a.begin(), a.end());
    sort(b.rbegin(), b.rend());
    for(int i = 0; i < n; i++) {
        sum = 0;
        for(int j = sumb; j < sumb + b[i]; j++)
            sum += a[j];
        sumb += b[i];
        ans += sum / b[i];
    }
    cout << setprecision(10) << fixed << ans;
    return 0;
}