#!/bin/bash

# Check if a bearer token is provided as an argument
if [ -z "$1" ]; then
  echo "Usage: $0 <BEARER_TOKEN>"
  exit 1
fi

BEARER_TOKEN="$1"

curl 'http://localhost:8080/api/v1/admin/bars/check' \
  -H 'Accept: application/json, text/plain, */*' \
  -H 'Accept-Language: en-US,en;q=0.9,es;q=0.8' \
  -H "Authorization: Bearer ${BEARER_TOKEN}" \
  -H 'Connection: keep-alive' \
  -H 'Origin: http://localhost:3000' \
  -H 'Referer: http://localhost:3000/' \
  -H 'Sec-Fetch-Dest: empty' \
  -H 'Sec-Fetch-Mode: cors' \
  -H 'Sec-Fetch-Site: same-site' \
  -H 'User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36' \
  -H 'sec-ch-ua: "Google Chrome";v="137", "Chromium";v="137", "Not/A)Brand";v="24"' \
  -H 'sec-ch-ua-mobile: ?0' \
  -H 'sec-ch-ua-platform: "Linux"'
