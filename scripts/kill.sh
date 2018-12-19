GAMEID=80b1d698-0d68-4474-be1c-a7cc6bea516b

curl -XPOST "localhost:8080/games/${GAMEID}" -H 'Content-Type: application/json' -H 'Accept: application/json' \
-d '{
	"event_type": "KILL",
	"targets": [1]
}'
