import urllib.request, http.cookiejar

cj = http.cookiejar.CookieJar()
opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(cj))

# login
data = b"username=admin@entrepot.com&password=admin123"
req = urllib.request.Request("http://localhost:8080/login", data=data, method="POST")
try:
    opener.open(req)
except Exception as e:
    pass

routes = [l.strip() for l in open("routes_to_test.txt") if l.strip()]
for route in routes:
    try:
        resp = opener.open("http://localhost:8080" + route, timeout=10)
        body = resp.read().decode("utf-8", errors="replace")
        code = resp.status
        bs = body.count("bootstrap.bundle")
        html = body.count("</html>")
        print(f"{route:35s} HTTP:{code:<4} bootstrap:{bs} closingHtml:{html} size:{len(body)}")
    except Exception as e:
        print(f"{route:35s} ERROR: {e}")
