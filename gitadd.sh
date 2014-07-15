git commit | grep modified | awk '{t=t" "$NF}END{print "git add "t}'
