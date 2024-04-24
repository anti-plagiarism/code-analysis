### Примеры для тестов api:

#### PUT - http://localhost:8085/v0/solution

```
{
"taskId": "123213123",
"solutionId": "1",
"lang": "cpp",
"program": "#include <bits\/stdc++.h>\r\n#define int long long\r\nusing namespace std;\r\n\r\nint def (int n) {\r\n\tint cnt = 0;\r\n\tfor (int i = 1; i * i <= n; i++) {\r\n\t    if (n & i) continue;\r\n\t    cnt++;\r\n\t    if (i * i != n) cnt++;\r\n\t}\r\n\treturn cnt;\r\n}\r\n\r\nsigned main () {\r\n  ios::sync_with_stdio();\r\n  cin.tie(0);\r\n  cout.tie(0);\r\n  int t;\r\n  cin >> t;\r\n  for (int j = 0; j < t; j++){\r\n  \tint n;\r\n  \tcin >> n;\r\n  \tcout << def(n) << '\\n';\r\n  }\r\n  return 0;\r\n\r\n}"
}
```
```
{
"taskId": "123213123",
"solutionId": "2",
"lang": "cpp",
"program": "#include <bits\/stdc++.h>\r\nusing namespace std;\r\n#define ll long long\r\n#define ld long double\r\n#define fi first\r\n#define se second\r\n#define pb push_back\r\n#define cok cout << (ok ? \"YES\\n\" : \"NO\\n\");\r\n#define dbg(x) cout << (#x) << \": \" << x << endl;\r\n#define dbga(x,l,r) cout << (#x) << \": \"; for (int ii=l;ii<r;ii++) cout << x[ii] << \" \"; cout << endl;\r\n\/\/ #define int long long\r\n#define pi pair<int, int>\r\nconst int N = 1e6+9, INF = 2e18;\r\nint a[N];\r\nint pos[10000009];\r\nconst int C = 1000;\r\npi go[C];\r\nvoid solve() {\r\n    int n;\r\n    cin >> n;\r\n    for (int i = 0; i < n; i++) cin >> a[i], pos[a[i]] = i + 1;\r\n    pair<int, pi> ans = {0, {0, 0}};\r\n    for (int i = 0; i < n; i++) {\r\n        for (int div = 1; div < C; div++) {\r\n            if (a[i] % div == 0) {\r\n                go[div] = max(go[div], (pi){a[i], i + 1});\r\n            }\r\n        }\r\n    }\r\n    for (int i = 0; i < n; i++) {\r\n        if (a[i] < C) {\r\n            ans = max(ans, (pair<int, pi>) {go[a[i]].fi - a[i], {go[a[i]].se, i + 1}});\r\n            continue;\r\n        }\r\n        for (int j = a[i]; j < 1e7+1; j += a[i]) {\r\n            if (pos[j]) ans = max(ans, (pair<int, pi>){j - a[i], {i + 1, pos[j]}});\r\n        }\r\n    }\r\n    memset(go, 0, sizeof(go));\r\n    for (int i = 0; i < n; i++) pos[a[i]] = 0;\r\n    cout << ans.se.fi << \" \" << ans.se.se << \"\\n\";\r\n}\r\nsigned main()\r\n{\r\n    cin.tie(0);ios_base::sync_with_stdio(0);\r\n    int t;\r\n    cin >> t;\r\n    while (t--) solve();\r\n}"
}
```
```
{
"taskId": "123213123",
"solutionId": "3",
"lang": "cpp",
"program": "#include <iostream>\r\nusing ll = long long;\r\nusing namespace std;\r\n\r\nint main(){\r\n    int t;\r\n    cin >> t;\r\n    while (t--){\r\n        int n;\r\n        cin >> n;\r\n        ll a[n];\r\n        for (int i=0;i<n;i++){\r\n            cin >> a[i];\r\n        }\r\n        int bl=-1, br=-1;\r\n        for (int l=0;l<n;l++){\r\n            for (int r=n-1;r>l;r--){\r\n                if (max(a[r], a[l])%min(a[r], a[l]) == 0){\r\n                    if (r!=l){\r\n                        if (bl == -1){\r\n                            bl = l;\r\n                            br = r;\r\n                        }\r\n                        else{\r\n                            if (abs(a[r]-a[l]) > abs(a[bl]-a[br])){\r\n                                bl = l;\r\n                                br = r;\r\n                            }\r\n                        }\r\n                    }\r\n                }\r\n            }\r\n        }\r\n        if (bl == -1){\r\n            cout << 1 << \" \" << 1;\r\n        }\r\n        else{\r\n            cout << bl+1 << \" \" << br+1 << \"\\n\";\r\n        }\r\n    }\r\n    return 0;\r\n}"
}
```
```
{
"taskId": "123213123",
"solutionId": "4",
"lang": "cpp",
"program": "\/\/#pragma GCC optimize(\"Ofast,unroll-loops\")\r\n\/\/#pragma GCC target(\"avx,avx2,fma\")\r\n\r\n#include <bits\/stdc++.h>\r\n\/\/#include <ext\/pb_ds\/assoc_container.hpp>\r\n\/\/#include <ext\/pb_ds\/tree_policy.hpp>\r\n\/\/#include <utility>\r\n\r\n\/\/#define int long long\r\n#define pb push_back\r\n#define vi vector<int>\r\n#define vvi vector<vector<int>>\r\n#define double long double\r\n\r\n\r\nusing namespace std;\r\n\/\/using namespace __gnu_pbds;\r\n\r\n\r\nconst int m=998244353;\r\n\r\nstruct seq_tree{\r\n    map<int,int>tree;\r\n    int lb, rb;\r\n    seq_tree *l=0, *r=0;\r\n\r\n    seq_tree (int _lb, int _rb){\r\n        lb = _lb, rb = _rb;\r\n        if (lb + 1 < rb){\r\n            int t = (lb + rb) \/ 2;\r\n            l = new seq_tree(lb, t);\r\n            r = new seq_tree(t, rb);\r\n        }\r\n    }\r\n\r\n    void add(int k, int x){\r\n        ++tree[x];\r\n        if (l){\r\n            if (k < l->rb){\r\n                l->add(k, x);\r\n            }\r\n            else{\r\n                r->add(k, x);\r\n            }\r\n        }\r\n    }\r\n\r\n    map<int, int> get(int lq, int rq){\r\n        if (lb >= lq && rb <= rq){\r\n            return tree;\r\n        }\r\n        if (max(lb, lq) >= min(rb, rq)){\r\n            return {{0, 0}};\r\n        }\r\n        map <int, int> a=get(lq, rq), b = get(lq, rq);\r\n        for (auto &i:b){\r\n            a[i.first] += i.second;\r\n        }\r\n        return a;\r\n    }\r\n};\r\n\r\n\r\n\r\nsigned main() {\r\n    ios_base::sync_with_stdio(false);\r\n    cin.tie(nullptr);\r\n    int t, n, mx, l, r;\r\n    cin >> t;\r\n    while(t--){\r\n        cin >> n;\r\n        mx = -1;\r\n        int sp[n];\r\n        for (int i = 0; i < n; ++i){\r\n            cin >> sp[i];\r\n        }\r\n        for (int i = 0; i < n; ++i){\r\n            for (int j = i; j < n; ++j){\r\n                int a = max(sp[i], sp[j]), b = min(sp[i], sp[j]);\r\n\r\n                if (!(a % b) && mx < a - b){\r\n                    mx = a - b;\r\n                    l = i, r = j;\r\n                }\r\n            }\r\n        }\r\n        cout << l + 1 << \" \" << r + 1 << '\\n';\r\n    }\r\n\r\n    return 0;\r\n}"
}
```
```
{
"taskId": "123213123",
"solutionId": "5",
"lang": "cpp",
"program": "#include <iostream>\r\n#include <algorithm>\r\n#include <vector>\r\n\/\/#define int long long\r\nusing namespace std;\r\n\r\nsigned main()\r\n{\r\n    \/\/ios_base::sync_with_stdio(false);\r\n    \/\/cin.tie(0);\r\n    vector<vector<int>>a(1e5+1);\r\n    for(int j=2;j<1e5;j++){\r\n        int n=j;\r\n        for(int i=1;i*i<=n;i++){\r\n            if(n%i==0){\r\n                if(i*i==n){\r\n                    a[n].push_back(i);\r\n                }\r\n                else{\r\n                    a[n].push_back(i);\r\n                    a[n].push_back(n\/i);\r\n                }\r\n            }\r\n        }\r\n    }\r\n    int t;\r\n    cin>>t;\r\n    while(t--){\r\n        int n;\r\n        cin>>n;\r\n        vector<pair<int,int>>b(n);\r\n        for(int i=0;i<n;i++){\r\n            cin>>b[i].first;\r\n            b[i].second=i;\r\n        }\r\n        sort(b.begin(),b.end(),greater<>());\r\n        int ansmin=1,ansmax=1;\r\n        int ans1=1;\r\n        int ans2=1;\r\n        for(int i=0;i<n;i++){\r\n            int k=b[i].first;\r\n            for(int j=0;j<a[k].size();j++){\r\n                int o=a[k][j];\r\n                int l=0,r=b.size()-1;\r\n                int m=0;\r\n                int f=0;\r\n                while(r-l>1){\r\n                    int m=(r+l)\/2;\r\n                    if(b[m].first==o){\r\n                        f=1;\r\n                        break;\r\n                    }\r\n                    if(b[m].first>=o){\r\n                        r=m;\r\n                    }\r\n                    else{\r\n                        l=m;\r\n                    }\r\n                }\r\n if(f==1){\r\n                    if(b[i].first-b[m].first>ansmax-ansmin){\r\n                        ansmax=b[i].first;\r\n                        ansmin=b[m].first;\r\n                        ans1=b[i].second+1;\r\n                        ans2=b[m].second+1;\r\n                    }\r\n                }\r\n                if(o==b[0].first){\r\n                    if(b[i].first-b[0].first>ansmax-ansmin){\r\n                        ansmax=b[i].first;\r\n                        ansmin=b[0].first;\r\n                        ans1=b[i].second+1;\r\n                        ans2=b[0].second+1;\r\n                    }\r\n                }\r\n                if(o==b[n-1].first){\r\n                    if(b[i].first-b[n-1].first>ansmax-ansmin){\r\n                        ansmax=b[i].first;\r\n                        ansmin=b[n-1].first;\r\n                        ans1=b[i].second+1;\r\n                        ans2=b[n-1].second+1;\r\n                    }\r\n                }\r\n            }\r\n        }\r\n        cout<<ans1<<\" \"<<ans2<<'\\n';\r\n    }\r\n    return 0;\r\n}"
}
```


#### GET - http://localhost:8085/v0/solution
```
{
"similarityThreshold": "3"
}
```